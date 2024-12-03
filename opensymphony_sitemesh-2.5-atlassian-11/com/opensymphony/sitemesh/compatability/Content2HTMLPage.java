/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.sitemesh.compatability;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.sitemesh.Content;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class Content2HTMLPage
implements HTMLPage {
    private final Content content;
    private HttpServletRequest request;

    public Content2HTMLPage(Content content, HttpServletRequest request) {
        this.content = content;
        this.request = request;
    }

    public void writePage(Writer out) throws IOException {
        this.content.writeOriginal(out);
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

    public void writeBody(Writer out) throws IOException {
        this.content.writeBody(out);
    }

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

    public void writeHead(Writer out) throws IOException {
        this.content.writeHead(out);
    }

    public String getHead() {
        try {
            StringWriter writer = new StringWriter();
            this.writeHead(writer);
            return writer.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not get head " + e.getMessage(), e);
        }
    }

    public String getTitle() {
        return this.content.getTitle();
    }

    public String getProperty(String name) {
        return this.content.getProperty(name);
    }

    public int getIntProperty(String name) {
        try {
            return Integer.parseInt(this.noNull(this.getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public long getLongProperty(String name) {
        try {
            return Long.parseLong(this.noNull(this.getProperty(name)));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String noNull(String property) {
        return property == null ? "" : property;
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
        return this.getProperty(name) != null;
    }

    public String[] getPropertyKeys() {
        return this.content.getPropertyKeys();
    }

    public Map getProperties() {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] keys = this.content.getPropertyKeys();
        for (int i = 0; i < keys.length; ++i) {
            result.put(keys[i], this.content.getProperty(keys[i]));
        }
        return result;
    }

    public boolean isFrameSet() {
        return this.isPropertySet("frameset") && this.getProperty("frameset").equalsIgnoreCase("true");
    }

    public void setFrameSet(boolean frameset) {
        this.addProperty("frameset", frameset ? "true" : "false");
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void addProperty(String name, String value) {
        this.content.addProperty(name, value);
    }
}

