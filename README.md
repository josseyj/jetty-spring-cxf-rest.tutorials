## Embedded Jetty with no web.xml
Since Servlet 3.0, there is no need for web.xml. When deploying the app on a container is works well. But getting in working on an embbeded Jetty server and not very easy. 
Jetty (9.2.3) supports Servelt 3.0 web-fragments and Servlet Context Initializers.

### Embedded Jetty
To get started with an embedded Jetty is pretty straight fwd.

#### Required Dependencies
* jetty-server
* jetty-webapp


```
Server server = new Server();

WebAppContext webAppContext = new WebAppContext();
webAppContext.setContextPath("/");

server.setHandler(webAppContext);

server.start();
server.join();

```
### No web.xml

#### Required Dependencies
* jetty-annotations

In order to enable that we need to enable <code>o.e.j.annotations.AnnotationConfiguration</code>.

```
...
webAppContext.setConfigurations(new Configuration[] {
	new AnnotationConfiguration()
});

```

But it does not search the current jar/dir for those implementations. So we need a single executable jar the code below includes the current jar in the searchable locations.

```
Resource resource = Resource.newResource(getClass().getProtectionDomain().getCodeSource().getLocation());
...
webAppContext.getMetaData().addContainerResource(resource);

```
**Limitation**: If there is a security manager installed, it might not allow access to the code-source and this might not work.

## Spring Context with no web.xml

Spring allows us to take advantage on the Servelt 3.0 no web.xml configurations and do everything in code. 

#### Required Dependencies
* spring-web
* javax.servlet-api (3.0) - *may not be required with embedded Jetty.*

For this we will have to implement <code>WebApplicationInitializer</code> interface and configure the <code>ServletContext</code> during start-up. We could use the abstract subclass <code>AbstractContextLoaderInitializer</code> to make things easier.

```
public class AppInitializer extends AbstractContextLoaderInitializer {
    
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(XXXXXXXXXXXXXXXXXXXXXXXXX.class);
        return applicationContext;
    }

}

```

## REST App with JAX-RS CXF

First we need a <code>CXFServlet</code> to handle the requests. CXFServlet is spring aware and looks for required configurations in the context.

Since we have <code>WebApplicationInitializer</code> we can quickly add a CXFServlet configuration in there.

```
public class AppInitializer extends AbstractContextLoaderInitializer {
    
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        Dynamic servletConfig = servletContext.addServlet("CXFServlet", new CXFServlet());
	        servletConfig.addMapping("/*");
    	    servletConfig.setLoadOnStartup(1);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
		...
        return applicationContext;
    }

}
```

### JAX-RS (CXF) Spring Configurations
We need a JAXRSServer bean and a CXFBus bean defined. The name of the CXFBus bean should be **'cxf'**. (This name can be configured with the CXFServlet 'bus' param.)
The resource beans should be added as service bean objects.

#### Required dependencies
* cxf-rt-frontend-jaxrs

```
@Configuration
@ComponentScan
public class ApplicationConfiguration {

    @Bean(destroyMethod = "destroy")
    public Server server() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(cxf());
        factory.setServiceBeanObjects(...);
        factory.setProvider(...);
        return factory.create();
    }
    
    @Bean
    public SpringBus cxf() {
        return new SpringBus();
    }
    
    ...    
}

```

### Jackson Binding

#### Required dependencies
* jackson-jaxrs-json-provider

```
	...
	
    @Bean
    public Server server() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        ...
        factory.setProvider(jsonProvider());
        return factory.create();
    }
    
    @Bean
    public JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider();
    }
    
    ...    
```

## Single executable jar.
Because of the limitations of assembly plugin, **shade** plugin works the best for creating a single executable jar.

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
              <createDependencyReducedPom>false</createDependencyReducedPom>
              <filters>
                <filter>
                  <artifact>*:*</artifact>
                  <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                  </excludes>
                </filter>
              </filters>
                <transformers>
                    <transformer
                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <mainClass>xxx.xxxxxx.xxxxxxx.Launcher</mainClass>
                    </transformer>
                    <transformer
                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>META-INF/spring.handlers</resource>
                    </transformer>
                    <transformer
                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>META-INF/spring.schemas</resource>
                    </transformer>
                    <transformer
                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                        <resource>META-INF/cxf/bus-extensions.txt</resource>
                    </transformer>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

