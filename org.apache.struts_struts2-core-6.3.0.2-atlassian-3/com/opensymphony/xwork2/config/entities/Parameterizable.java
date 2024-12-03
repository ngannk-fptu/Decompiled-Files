/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.entities;

import java.util.Map;

public interface Parameterizable {
    public void addParam(String var1, String var2);

    public void setParams(Map<String, String> var1);

    public Map<String, String> getParams();
}

