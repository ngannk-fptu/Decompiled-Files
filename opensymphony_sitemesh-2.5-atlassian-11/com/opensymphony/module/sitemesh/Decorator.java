/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import java.util.Iterator;

public interface Decorator {
    public String getPage();

    public String getName();

    public String getURIPath();

    public String getRole();

    public String getInitParameter(String var1);

    public Iterator getInitParameterNames();
}

