/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public interface ResourceCache {
    public PDFont getFont(COSObject var1) throws IOException;

    public PDColorSpace getColorSpace(COSObject var1) throws IOException;

    public PDExtendedGraphicsState getExtGState(COSObject var1);

    public PDShading getShading(COSObject var1) throws IOException;

    public PDAbstractPattern getPattern(COSObject var1) throws IOException;

    public PDPropertyList getProperties(COSObject var1);

    public PDXObject getXObject(COSObject var1) throws IOException;

    public void put(COSObject var1, PDFont var2) throws IOException;

    public void put(COSObject var1, PDColorSpace var2) throws IOException;

    public void put(COSObject var1, PDExtendedGraphicsState var2);

    public void put(COSObject var1, PDShading var2) throws IOException;

    public void put(COSObject var1, PDAbstractPattern var2) throws IOException;

    public void put(COSObject var1, PDPropertyList var2);

    public void put(COSObject var1, PDXObject var2) throws IOException;
}

