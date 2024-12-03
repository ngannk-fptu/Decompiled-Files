/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BodyType;
import org.apache.poi.xwpf.usermodel.IBody;

public interface IBodyElement {
    public IBody getBody();

    public POIXMLDocumentPart getPart();

    public BodyType getPartType();

    public BodyElementType getElementType();
}

