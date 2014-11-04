package com.paypal.credit.tutorials.rest.launcher;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

/**
 * This class defines a Launcher.
 */
public class Launcher {

    
    public static void main(String[] args) throws Exception {
        
        Resource resource = Resource.newResource(Launcher.class.getProtectionDomain().getCodeSource().getLocation());
        
        Server server = new Server(8181);
        
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.getMetaData().addContainerResource(resource);
        webAppContext.setContextPath("/");
        
        webAppContext.setConfigurations(new Configuration[] {
                new AnnotationConfiguration()
        });
        
        server.setHandler(webAppContext);
        
        server.start();
        server.join();
    }
    
}
