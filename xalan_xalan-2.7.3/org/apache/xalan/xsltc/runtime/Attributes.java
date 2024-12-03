/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import org.apache.xalan.xsltc.DOM;
import org.xml.sax.AttributeList;

public final class Attributes
implements AttributeList {
    private int _element;
    private DOM _document;

    public Attributes(DOM document, int element) {
        this._element = element;
        this._document = document;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String getName(int i) {
        return null;
    }

    @Override
    public String getType(int i) {
        return null;
    }

    @Override
    public String getType(String name) {
        return null;
    }

    @Override
    public String getValue(int i) {
        return null;
    }

    @Override
    public String getValue(String name) {
        return null;
    }
}

