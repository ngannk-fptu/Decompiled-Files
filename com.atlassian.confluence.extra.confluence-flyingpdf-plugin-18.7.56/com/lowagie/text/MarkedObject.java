/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.util.ArrayList;
import java.util.Properties;

public class MarkedObject
implements Element {
    protected Element element;
    protected Properties markupAttributes = new Properties();

    protected MarkedObject() {
        this.element = null;
    }

    public MarkedObject(Element element) {
        this.element = element;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return this.element.getChunks();
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this.element);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 50;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    public Properties getMarkupAttributes() {
        return this.markupAttributes;
    }

    public void setMarkupAttribute(String key, String value) {
        this.markupAttributes.setProperty(key, value);
    }
}

