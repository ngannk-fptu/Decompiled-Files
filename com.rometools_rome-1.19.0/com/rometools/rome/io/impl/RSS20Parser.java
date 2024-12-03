/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Document
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.io.impl.RSS094Parser;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

public class RSS20Parser
extends RSS094Parser {
    public RSS20Parser() {
        this("rss_2.0");
    }

    protected RSS20Parser(String type) {
        super(type);
    }

    @Override
    protected String getRSSVersion() {
        return "2.0";
    }

    @Override
    protected boolean isHourFormat24(Element rssRoot) {
        return false;
    }

    @Override
    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = super.parseItemDescription(rssRoot, eDesc);
        return desc;
    }

    @Override
    public boolean isMyType(Document document) {
        return this.rootElementMatches(document) && (this.versionMatches(document) || this.versionAbsent(document));
    }

    private boolean rootElementMatches(Document document) {
        return document.getRootElement().getName().equals("rss");
    }

    private boolean versionMatches(Document document) {
        Attribute version = document.getRootElement().getAttribute("version");
        return version != null && version.getValue().trim().startsWith(this.getRSSVersion());
    }

    private boolean versionAbsent(Document document) {
        return document.getRootElement().getAttribute("version") == null;
    }
}

