/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface Page {
    public void writePage(Writer var1) throws IOException;

    public String getPage();

    public void writeBody(Writer var1) throws IOException;

    public String getBody();

    public String getTitle();

    public String getProperty(String var1);

    public int getIntProperty(String var1);

    public long getLongProperty(String var1);

    public boolean getBooleanProperty(String var1);

    public boolean isPropertySet(String var1);

    public String[] getPropertyKeys();

    public Map getProperties();

    public HttpServletRequest getRequest();

    public void setRequest(HttpServletRequest var1);

    public void addProperty(String var1, String var2);
}

