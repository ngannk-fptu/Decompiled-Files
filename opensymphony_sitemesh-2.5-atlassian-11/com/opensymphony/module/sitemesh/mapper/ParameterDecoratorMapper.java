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

public class ParameterDecoratorMapper
extends AbstractDecoratorMapper {
    private String decoratorParameter = null;
    private String paramName = null;
    private String paramValue = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.decoratorParameter = properties.getProperty("decorator.parameter", "decorator");
        this.paramName = properties.getProperty("parameter.name", null);
        this.paramValue = properties.getProperty("parameter.value", null);
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        String decoratorParamValue = request.getParameter(this.decoratorParameter);
        if ((this.paramName == null || this.paramValue.equals(request.getParameter(this.paramName))) && decoratorParamValue != null && !decoratorParamValue.trim().equals("")) {
            result = this.getNamedDecorator(request, decoratorParamValue);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}

