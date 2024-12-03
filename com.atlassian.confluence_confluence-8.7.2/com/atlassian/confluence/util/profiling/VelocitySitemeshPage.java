/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.opensymphony.module.sitemesh.HTMLPage
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.opensymphony.module.sitemesh.HTMLPage;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class VelocitySitemeshPage
implements HTMLPage {
    private final HTMLPage delegatePage;

    public VelocitySitemeshPage(HTMLPage delegatePage) {
        this.delegatePage = delegatePage;
    }

    public void writePage(Writer writer) throws IOException {
        this.delegatePage.writePage(writer);
    }

    public String getPage() {
        return this.delegatePage.getPage();
    }

    public void writeBody(Writer writer) throws IOException {
        this.delegatePage.writeBody(writer);
    }

    public String getBody() {
        return this.delegatePage.getBody();
    }

    public String getTitle() {
        return this.delegatePage.getTitle();
    }

    @HtmlSafe
    public String getProperty(String s) {
        return this.delegatePage.getProperty(s);
    }

    public int getIntProperty(String s) {
        return this.delegatePage.getIntProperty(s);
    }

    public long getLongProperty(String s) {
        return this.delegatePage.getLongProperty(s);
    }

    public boolean getBooleanProperty(String s) {
        return this.delegatePage.getBooleanProperty(s);
    }

    public boolean isPropertySet(String s) {
        return this.delegatePage.isPropertySet(s);
    }

    public String[] getPropertyKeys() {
        return this.delegatePage.getPropertyKeys();
    }

    public Map getProperties() {
        return this.delegatePage.getProperties();
    }

    public HttpServletRequest getRequest() {
        return this.delegatePage.getRequest();
    }

    public void setRequest(HttpServletRequest httpServletRequest) {
        this.delegatePage.setRequest(httpServletRequest);
    }

    public void addProperty(String s, String s1) {
        this.delegatePage.addProperty(s, s1);
    }

    public void writeHead(Writer writer) throws IOException {
        this.delegatePage.writeHead(writer);
    }

    public String getHead() {
        return this.delegatePage.getHead();
    }

    public boolean isFrameSet() {
        return this.delegatePage.isFrameSet();
    }

    public void setFrameSet(boolean b) {
        this.delegatePage.setFrameSet(b);
    }
}

