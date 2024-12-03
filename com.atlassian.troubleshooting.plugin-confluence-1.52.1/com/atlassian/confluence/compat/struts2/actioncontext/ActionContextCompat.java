/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.compat.struts2.actioncontext;

import java.util.Locale;
import java.util.Map;

interface ActionContextCompat {
    public void setApplication(Map var1);

    public Map getApplication();

    public void setContextMap(Map var1);

    public Map getContextMap();

    public void setConversionErrors(Map var1);

    public Map getConversionErrors();

    public void setLocale(Locale var1);

    public Locale getLocale();

    public void setName(String var1);

    public String getName();

    public void setParameters(Map var1);

    public Map getParameters();

    public void setSession(Map var1);

    public Map getSession();

    public Object get(Object var1);

    public void put(Object var1, Object var2);
}

