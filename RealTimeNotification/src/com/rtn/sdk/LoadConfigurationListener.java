package com.rtn.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class LoadConfigurationListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        // read file or rdbms
        
        ServletContext context = sce.getServletContext();
        Config config = new Config();
		ArrayList<String> shopperProfile = new ArrayList<String>();
		Map<String, Integer> shopperEmails = new HashMap<String, Integer>();

        context.setAttribute("Config", config);
        context.setAttribute("ShopperProfile", shopperProfile);
        context.setAttribute("ShopperEmails", shopperEmails);
        
        
        // set attributes
        
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        // remove attributes
        context.removeAttribute("Config");
        context.removeAttribute("ShopperProfile");
        context.removeAttribute("ShopperEmails");
        
        
    }

}
