/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.xwpf.usermodel.XWPFRelation;

public final class XWPFFactory
extends POIXMLFactory {
    private static final XWPFFactory inst = new XWPFFactory();

    public static XWPFFactory getInstance() {
        return inst;
    }

    private XWPFFactory() {
    }

    @Override
    protected POIXMLRelation getDescriptor(String relationshipType) {
        return XWPFRelation.getInstance(relationshipType);
    }
}

