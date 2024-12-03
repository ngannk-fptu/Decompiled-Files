/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper;
import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

public class EnvEntryDecoratorMapper
extends ConfigDecoratorMapper {
    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        String resourceValue = EnvEntryDecoratorMapper.getStringResource(name);
        if (resourceValue == null) {
            return super.getNamedDecorator(request, name);
        }
        return new DefaultDecorator(name, resourceValue, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getStringResource(String name) {
        String value = null;
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            Object o = ctx.lookup("java:comp/env/" + name);
            if (o != null) {
                value = o.toString();
            }
        }
        catch (NamingException ne) {
        }
        finally {
            try {
                if (ctx != null) {
                    ctx.close();
                }
            }
            catch (NamingException ne) {}
        }
        return value;
    }
}

