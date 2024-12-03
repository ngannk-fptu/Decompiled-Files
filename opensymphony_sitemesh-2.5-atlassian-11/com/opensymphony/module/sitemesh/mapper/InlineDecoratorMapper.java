/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.factory.FactoryException;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import javax.servlet.http.HttpServletRequest;

public class InlineDecoratorMapper
extends AbstractDecoratorMapper
implements RequestConstants {
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String decoratorName;
        Decorator result = null;
        if (request.getAttribute(DECORATOR) != null && (result = this.getNamedDecorator(request, decoratorName = (String)request.getAttribute(DECORATOR))) == null) {
            throw new FactoryException("Cannot locate inline Decorator: " + decoratorName);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}

