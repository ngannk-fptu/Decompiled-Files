/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

final class Element {
    protected String mLocalName;
    protected String mPrefix;
    protected String mNamespaceURI;
    protected String mDefaultNsURI;
    protected int mNsOffset;
    protected Element mParent;
    protected int mChildCount;

    public Element(Element parent, int nsOffset, String prefix, String ln) {
        this.mParent = parent;
        this.mNsOffset = nsOffset;
        this.mPrefix = prefix;
        this.mLocalName = ln;
    }

    public void reset(Element parent, int nsOffset, String prefix, String ln) {
        this.mParent = parent;
        this.mNsOffset = nsOffset;
        this.mPrefix = prefix;
        this.mLocalName = ln;
        this.mChildCount = 0;
    }

    public void relink(Element next) {
        this.mParent = next;
    }
}

