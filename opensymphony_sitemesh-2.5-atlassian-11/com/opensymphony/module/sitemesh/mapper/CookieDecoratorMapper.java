/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import java.util.Properties;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieDecoratorMapper
extends AbstractDecoratorMapper {
    private String cookieName;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.cookieName = properties.getProperty("cookie.name", null);
        if (this.cookieName == null) {
            throw new InstantiationException("'cookie.name' name parameter not set for this decorator mapper");
        }
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];
                if (!cookie.getName().equals(this.cookieName)) continue;
                result = this.getNamedDecorator(request, cookie.getValue());
            }
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}

