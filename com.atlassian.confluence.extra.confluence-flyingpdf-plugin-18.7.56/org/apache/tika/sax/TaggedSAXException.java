/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.xml.sax.SAXException;

public class TaggedSAXException
extends SAXException {
    private final Object tag;

    public TaggedSAXException(SAXException original, Object tag) {
        super(original.getMessage(), original);
        this.tag = tag;
    }

    public Object getTag() {
        return this.tag;
    }

    @Override
    public SAXException getCause() {
        return (SAXException)super.getCause();
    }
}

