/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.params;

public interface HttpParams {
    public HttpParams getDefaults();

    public void setDefaults(HttpParams var1);

    public Object getParameter(String var1);

    public void setParameter(String var1, Object var2);

    public long getLongParameter(String var1, long var2);

    public void setLongParameter(String var1, long var2);

    public int getIntParameter(String var1, int var2);

    public void setIntParameter(String var1, int var2);

    public double getDoubleParameter(String var1, double var2);

    public void setDoubleParameter(String var1, double var2);

    public boolean getBooleanParameter(String var1, boolean var2);

    public void setBooleanParameter(String var1, boolean var2);

    public boolean isParameterSet(String var1);

    public boolean isParameterSetLocally(String var1);

    public boolean isParameterTrue(String var1);

    public boolean isParameterFalse(String var1);
}

