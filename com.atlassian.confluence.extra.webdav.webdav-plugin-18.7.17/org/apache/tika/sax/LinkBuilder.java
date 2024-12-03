/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.apache.tika.sax.Link;

class LinkBuilder {
    private final String type;
    private final StringBuilder text = new StringBuilder();
    private String uri = "";
    private String title = "";
    private String rel = "";

    public LinkBuilder(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setURI(String uri) {
        this.uri = uri != null ? uri : "";
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public void setRel(String rel) {
        this.rel = rel != null ? rel : "";
    }

    public void characters(char[] ch, int offset, int length) {
        this.text.append(ch, offset, length);
    }

    public Link getLink() {
        return this.getLink(false);
    }

    public Link getLink(boolean collapseWhitespace) {
        String anchor = this.text.toString();
        if (collapseWhitespace) {
            anchor = anchor.replaceAll("\\s+", " ").trim();
        }
        return new Link(this.type, this.uri, this.title, anchor, this.rel);
    }
}

