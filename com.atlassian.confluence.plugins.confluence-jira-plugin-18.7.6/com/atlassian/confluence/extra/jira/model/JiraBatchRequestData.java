/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package com.atlassian.confluence.extra.jira.model;

import java.util.Map;
import org.jdom.Element;

public class JiraBatchRequestData {
    private Map<String, Element> elementMap;
    private String displayUrl;
    private Exception exception;

    public Map<String, Element> getElementMap() {
        return this.elementMap;
    }

    public void setElementMap(Map<String, Element> elementMap) {
        this.elementMap = elementMap;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

