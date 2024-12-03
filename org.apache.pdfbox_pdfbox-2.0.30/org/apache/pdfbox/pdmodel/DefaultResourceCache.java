/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public class DefaultResourceCache
implements ResourceCache {
    private final Map<COSObject, SoftReference<PDFont>> fonts = new HashMap<COSObject, SoftReference<PDFont>>();
    private final Map<COSObject, SoftReference<PDColorSpace>> colorSpaces = new HashMap<COSObject, SoftReference<PDColorSpace>>();
    private final Map<COSObject, SoftReference<PDXObject>> xobjects = new HashMap<COSObject, SoftReference<PDXObject>>();
    private final Map<COSObject, SoftReference<PDExtendedGraphicsState>> extGStates = new HashMap<COSObject, SoftReference<PDExtendedGraphicsState>>();
    private final Map<COSObject, SoftReference<PDShading>> shadings = new HashMap<COSObject, SoftReference<PDShading>>();
    private final Map<COSObject, SoftReference<PDAbstractPattern>> patterns = new HashMap<COSObject, SoftReference<PDAbstractPattern>>();
    private final Map<COSObject, SoftReference<PDPropertyList>> properties = new HashMap<COSObject, SoftReference<PDPropertyList>>();

    @Override
    public PDFont getFont(COSObject indirect) throws IOException {
        SoftReference<PDFont> font = this.fonts.get(indirect);
        if (font != null) {
            return font.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDFont font) throws IOException {
        this.fonts.put(indirect, new SoftReference<PDFont>(font));
    }

    @Override
    public PDColorSpace getColorSpace(COSObject indirect) throws IOException {
        SoftReference<PDColorSpace> colorSpace = this.colorSpaces.get(indirect);
        if (colorSpace != null) {
            return colorSpace.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDColorSpace colorSpace) throws IOException {
        this.colorSpaces.put(indirect, new SoftReference<PDColorSpace>(colorSpace));
    }

    @Override
    public PDExtendedGraphicsState getExtGState(COSObject indirect) {
        SoftReference<PDExtendedGraphicsState> extGState = this.extGStates.get(indirect);
        if (extGState != null) {
            return extGState.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDExtendedGraphicsState extGState) {
        this.extGStates.put(indirect, new SoftReference<PDExtendedGraphicsState>(extGState));
    }

    @Override
    public PDShading getShading(COSObject indirect) throws IOException {
        SoftReference<PDShading> shading = this.shadings.get(indirect);
        if (shading != null) {
            return shading.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDShading shading) throws IOException {
        this.shadings.put(indirect, new SoftReference<PDShading>(shading));
    }

    @Override
    public PDAbstractPattern getPattern(COSObject indirect) throws IOException {
        SoftReference<PDAbstractPattern> pattern = this.patterns.get(indirect);
        if (pattern != null) {
            return pattern.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDAbstractPattern pattern) throws IOException {
        this.patterns.put(indirect, new SoftReference<PDAbstractPattern>(pattern));
    }

    @Override
    public PDPropertyList getProperties(COSObject indirect) {
        SoftReference<PDPropertyList> propertyList = this.properties.get(indirect);
        if (propertyList != null) {
            return propertyList.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDPropertyList propertyList) {
        this.properties.put(indirect, new SoftReference<PDPropertyList>(propertyList));
    }

    @Override
    public PDXObject getXObject(COSObject indirect) throws IOException {
        SoftReference<PDXObject> xobject = this.xobjects.get(indirect);
        if (xobject != null) {
            return xobject.get();
        }
        return null;
    }

    @Override
    public void put(COSObject indirect, PDXObject xobject) throws IOException {
        this.xobjects.put(indirect, new SoftReference<PDXObject>(xobject));
    }
}

