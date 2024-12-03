/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.compatability.Content2HTMLPage;
import com.opensymphony.sitemesh.compatability.OldDecorator2NewDecorator;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.NoDecorator;
import javax.servlet.http.HttpServletRequest;

public class DecoratorMapper2DecoratorSelector
implements DecoratorSelector {
    private final DecoratorMapper decoratorMapper;

    public DecoratorMapper2DecoratorSelector(DecoratorMapper decoratorMapper) {
        this.decoratorMapper = decoratorMapper;
    }

    public Decorator selectDecorator(Content content, SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext)context;
        HttpServletRequest request = webAppContext.getRequest();
        com.opensymphony.module.sitemesh.Decorator decorator = this.decoratorMapper.getDecorator(request, new Content2HTMLPage(content, request));
        if (decorator == null || decorator.getPage() == null) {
            return new NoDecorator();
        }
        return new OldDecorator2NewDecorator(decorator);
    }
}

