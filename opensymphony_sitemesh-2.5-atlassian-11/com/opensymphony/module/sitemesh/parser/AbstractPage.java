/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.parser.PageRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractPage
implements Page {
    private final Map properties = new HashMap();
    private final SitemeshBuffer sitemeshBuffer;
    private HttpServletRequest request;

    protected AbstractPage(SitemeshBuffer sitemeshBuffer) {
        this.sitemeshBuffer = sitemeshBuffer;
    }

    public void writePage(Writer out) throws IOException {
        this.sitemeshBuffer.writeTo(out, 0, this.sitemeshBuffer.getBufferLength());
    }

    public String getPage() {
        try {
            StringWriter writer = new StringWriter();
            this.writePage(writer);
            return writer.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not get page " + e.getMessage(), e);
        }
    }

    public abstract void writeBody(Writer var1) throws IOException;

    public String getBody() {
        try {
            StringWriter writer = new StringWriter();
            this.writeBody(writer);
            return writer.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not get body " + e.getMessage(), e);
        }
    }

    public String getTitle() {
        return AbstractPage.noNull(this.getProperty("title"));
    }

    public String getProperty(String name) {
        if (!this.isPropertySet(name)) {
            return null;
        }
        return (String)this.properties.get(name);
    }

    public int getIntProperty(String name) {
        try {
            return Integer.parseInt(AbstractPage.noNull(this.getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public long getLongProperty(String name) {
        try {
            return Long.parseLong(AbstractPage.noNull(this.getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    public boolean getBooleanProperty(String name) {
        String property = this.getProperty(name);
        if (property == null || property.trim().length() == 0) {
            return false;
        }
        switch (property.charAt(0)) {
            case '1': 
            case 'T': 
            case 'Y': 
            case 't': 
            case 'y': {
                return true;
            }
        }
        return false;
    }

    public boolean isPropertySet(String name) {
        return this.properties.containsKey(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getPropertyKeys() {
        Map map = this.properties;
        synchronized (map) {
            Set keys = this.properties.keySet();
            return keys.toArray(new String[keys.size()]);
        }
    }

    public Map getProperties() {
        return this.properties;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = new PageRequest(request);
    }

    public void addProperty(String name, String value) {
        this.properties.put(name, value);
    }

    protected static String noNull(String in) {
        return in == null ? "" : in;
    }
}

