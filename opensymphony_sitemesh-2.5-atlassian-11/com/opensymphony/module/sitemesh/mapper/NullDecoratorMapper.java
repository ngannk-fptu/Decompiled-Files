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

public class NullDecoratorMapper
implements DecoratorMapper {
    public void init(Config config, Properties properties, DecoratorMapper parent) {
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        return null;
    }

    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        return null;
    }
}

