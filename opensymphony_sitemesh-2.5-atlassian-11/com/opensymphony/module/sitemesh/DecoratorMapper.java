/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

public interface DecoratorMapper {
    public void init(Config var1, Properties var2, DecoratorMapper var3) throws InstantiationException;

    public Decorator getDecorator(HttpServletRequest var1, Page var2);

    public Decorator getNamedDecorator(HttpServletRequest var1, String var2);
}

