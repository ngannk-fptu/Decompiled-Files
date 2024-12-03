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

public class PrintableDecoratorMapper
extends AbstractDecoratorMapper {
    private String decorator;
    private String paramName;
    private String paramValue;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.decorator = properties.getProperty("decorator");
        this.paramName = properties.getProperty("parameter.name", "printable");
        this.paramValue = properties.getProperty("parameter.value", "true");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        if (this.decorator != null && this.paramValue.equalsIgnoreCase(request.getParameter(this.paramName))) {
            result = this.getNamedDecorator(request, this.decorator);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}

