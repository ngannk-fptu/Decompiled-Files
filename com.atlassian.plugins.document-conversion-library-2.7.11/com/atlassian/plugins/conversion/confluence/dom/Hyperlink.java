/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

public class Hyperlink {
    public static final int PAGE_REF = 0;
    public static final int ID_REF = 1;
    public static final int ATTACHMENT_REF = 2;
    public static final int ANCHOR_REF = 3;
    public static final int EXTERNAL_REF = 4;
    public static final int BLOG_REF = 5;
    public static final int USER_REF = 6;
    public static final int SHORTCUT_REF = 7;
    private String _space;
    private String _reference;
    private String _anchor;
    private String _attachment;
    private String _display;
    private String _linkTip;
    private int _type;

    public String getAnchor() {
        return this._anchor;
    }

    public void setAnchor(String _anchor) {
        this._anchor = _anchor;
    }

    public String getAttachment() {
        return this._attachment;
    }

    public void setAttachment(String _attachment) {
        this._attachment = _attachment;
    }

    public String getDisplay() {
        return this._display;
    }

    public void setDisplay(String _display) {
        this._display = _display;
    }

    public String getReference() {
        return this._reference;
    }

    public void setReference(String _reference) {
        this._reference = _reference;
    }

    public String getSpace() {
        return this._space;
    }

    public void setSpace(String _space) {
        this._space = _space;
    }

    public int getType() {
        return this._type;
    }

    public void setType(int _type) {
        this._type = _type;
    }

    public String getLinkTip() {
        return this._linkTip;
    }

    public void setLinkTip(String tip) {
        this._linkTip = tip;
    }
}

