/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.ConfigLoader;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class ConfigDecoratorMapper
extends AbstractDecoratorMapper {
    private ConfigLoader configLoader = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        try {
            String fileName = properties.getProperty("config", "/WEB-INF/decorators.xml");
            this.configLoader = new ConfigLoader(fileName, config);
        }
        catch (Exception e) {
            throw new InstantiationException(e.toString());
        }
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String thisPath = request.getServletPath();
        if (thisPath == null) {
            String requestURI = request.getRequestURI();
            thisPath = request.getPathInfo() != null ? requestURI.substring(0, requestURI.indexOf(request.getPathInfo())) : requestURI;
        } else if ("".equals(thisPath)) {
            thisPath = request.getPathInfo();
        }
        String name = null;
        try {
            name = this.configLoader.getMappedName(thisPath);
        }
        catch (ServletException e) {
            e.printStackTrace();
        }
        Decorator result = this.getNamedDecorator(request, name);
        return result == null ? super.getDecorator(request, page) : result;
    }

    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        Decorator result = null;
        try {
            result = this.configLoader.getDecoratorByName(name);
        }
        catch (ServletException e) {
            e.printStackTrace();
        }
        if (result == null || result.getRole() != null && !request.isUserInRole(result.getRole())) {
            return super.getNamedDecorator(request, name);
        }
        return result;
    }
}

