/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.NameValuePair;

public interface HeaderElement {
    public String getName();

    public String getValue();

    public NameValuePair[] getParameters();

    public NameValuePair getParameterByName(String var1);

    public int getParameterCount();

    public NameValuePair getParameter(int var1);
}

