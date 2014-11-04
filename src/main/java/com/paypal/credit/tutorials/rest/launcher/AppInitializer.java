package com.paypal.credit.tutorials.rest.launcher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.paypal.credit.tutorials.rest.config.ApplicationConfiguration;

/**
 * This class defines a AppInitializer.
 */
public class AppInitializer extends AbstractContextLoaderInitializer implements WebApplicationInitializer {
    
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        Dynamic servletConfig = servletContext.addServlet("CXFServlet", new CXFServlet());
        servletConfig.addMapping("/*");
        servletConfig.setLoadOnStartup(1);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ApplicationConfiguration.class);
        return applicationContext;
    }

}
