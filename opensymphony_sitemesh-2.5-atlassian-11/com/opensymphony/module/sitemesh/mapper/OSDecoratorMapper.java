/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public class OSDecoratorMapper
extends AbstractDecoratorMapper {
    protected Properties properties;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        this.properties = properties;
        this.parent = parent;
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String osHeader = request.getHeader("UA-OS");
        if (osHeader == null) {
            return this.parent.getDecorator(request, page);
        }
        Enumeration<?> e = this.properties.propertyNames();
        while (e.hasMoreElements()) {
            String os = (String)e.nextElement();
            if (osHeader.toLowerCase().indexOf(os.toLowerCase()) == -1) continue;
            String decoratorName = this.parent.getDecorator(request, page).getName();
            if (decoratorName != null) {
                decoratorName = decoratorName + '-' + this.properties.getProperty(os);
            }
            return this.getNamedDecorator(request, decoratorName);
        }
        return this.parent.getDecorator(request, page);
    }
}

