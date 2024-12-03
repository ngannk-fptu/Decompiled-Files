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
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public class FrameSetDecoratorMapper
extends AbstractDecoratorMapper {
    private String decorator = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.decorator = properties.getProperty("decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        if (page instanceof HTMLPage && ((HTMLPage)page).isFrameSet()) {
            return this.getNamedDecorator(request, this.decorator);
        }
        return super.getDecorator(request, page);
    }
}

