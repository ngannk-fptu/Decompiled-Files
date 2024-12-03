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
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public class SessionDecoratorMapper
extends AbstractDecoratorMapper {
    private String decoratorParameter = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.decoratorParameter = properties.getProperty("decorator.parameter", "decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        String decorator = (String)request.getSession().getAttribute(this.decoratorParameter);
        if (decorator != null) {
            result = this.getNamedDecorator(request, decorator);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}

