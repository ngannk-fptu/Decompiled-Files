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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public class PageDecoratorMapper
extends AbstractDecoratorMapper {
    private List pageProps = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.pageProps = new ArrayList();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String)entry.getKey();
            if (!key.startsWith("property")) continue;
            this.pageProps.add(entry.getValue());
        }
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String propName;
        Decorator result = null;
        Iterator i = this.pageProps.iterator();
        while (i.hasNext() && (result = this.getByProperty(request, page, propName = (String)i.next())) == null) {
        }
        return result == null ? super.getDecorator(request, page) : result;
    }

    private Decorator getByProperty(HttpServletRequest request, Page p, String name) {
        if (p.isPropertySet(name)) {
            return this.getNamedDecorator(request, p.getProperty(name));
        }
        return null;
    }
}

