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
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractDecoratorMapper
implements DecoratorMapper {
    protected DecoratorMapper parent = null;
    protected Config config = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        this.parent = parent;
        this.config = config;
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        return this.parent.getDecorator(request, page);
    }

    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        return this.parent.getNamedDecorator(request, name);
    }
}

