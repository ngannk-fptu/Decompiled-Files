/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.jira.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MacroTableParam {
    private String wikiMarkup;
    private String columnName;
    private String order;
    private Boolean clearCache;

    public String getWikiMarkup() {
        return this.wikiMarkup;
    }

    public void setWikiMarkup(String wikiMarkup) {
        this.wikiMarkup = wikiMarkup;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Boolean getClearCache() {
        return this.clearCache;
    }

    public void setClearCache(Boolean clearCache) {
        this.clearCache = clearCache;
    }
}

