/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

public final class PDResources
implements COSObjectable {
    private final COSDictionary resources;
    private final ResourceCache cache;
    private final Map<COSName, SoftReference<PDFont>> directFontCache;

    public PDResources() {
        this.resources = new COSDictionary();
        this.cache = null;
        this.directFontCache = new HashMap<COSName, SoftReference<PDFont>>();
    }

    public PDResources(COSDictionary resourceDictionary) {
        if (resourceDictionary == null) {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        this.resources = resourceDictionary;
        this.cache = null;
        this.directFontCache = new HashMap<COSName, SoftReference<PDFont>>();
    }

    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache) {
        if (resourceDictionary == null) {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        this.resources = resourceDictionary;
        this.cache = resourceCache;
        this.directFontCache = new HashMap<COSName, SoftReference<PDFont>>();
    }

    public PDResources(COSDictionary resourceDictionary, ResourceCache resourceCache, Map<COSName, SoftReference<PDFont>> directFontCache) {
        if (resourceDictionary == null) {
            throw new IllegalArgumentException("resourceDictionary is null");
        }
        if (directFontCache == null) {
            throw new IllegalArgumentException("directFontCache is null");
        }
        this.resources = resourceDictionary;
        this.cache = resourceCache;
        this.directFontCache = directFontCache;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.resources;
    }

    public PDFont getFont(COSName name) throws IOException {
        PDFont cached;
        SoftReference<PDFont> ref;
        COSObject indirect = this.getIndirect(COSName.FONT, name);
        if (this.cache != null && indirect != null) {
            PDFont cached2 = this.cache.getFont(indirect);
            if (cached2 != null) {
                return cached2;
            }
        } else if (indirect == null && (ref = this.directFontCache.get(name)) != null && (cached = ref.get()) != null) {
            return cached;
        }
        PDFont font = null;
        COSBase base = this.get(COSName.FONT, name);
        if (base instanceof COSDictionary) {
            font = PDFontFactory.createFont((COSDictionary)base, this.cache);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, font);
        } else if (indirect == null) {
            this.directFontCache.put(name, new SoftReference<PDFont>(font));
        }
        return font;
    }

    public PDColorSpace getColorSpace(COSName name) throws IOException {
        return this.getColorSpace(name, false);
    }

    public PDColorSpace getColorSpace(COSName name, boolean wasDefault) throws IOException {
        PDColorSpace cached;
        COSObject indirect = this.getIndirect(COSName.COLORSPACE, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getColorSpace(indirect)) != null) {
            return cached;
        }
        COSBase object = this.get(COSName.COLORSPACE, name);
        PDColorSpace colorSpace = object != null ? PDColorSpace.create(object, this, wasDefault) : PDColorSpace.create(name, this, wasDefault);
        if (this.cache != null && indirect != null && !(colorSpace instanceof PDPattern)) {
            this.cache.put(indirect, colorSpace);
        }
        return colorSpace;
    }

    public boolean hasColorSpace(COSName name) {
        return this.get(COSName.COLORSPACE, name) != null;
    }

    public PDExtendedGraphicsState getExtGState(COSName name) {
        PDExtendedGraphicsState cached;
        COSObject indirect = this.getIndirect(COSName.EXT_G_STATE, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getExtGState(indirect)) != null) {
            return cached;
        }
        PDExtendedGraphicsState extGState = null;
        COSBase base = this.get(COSName.EXT_G_STATE, name);
        if (base instanceof COSDictionary) {
            extGState = new PDExtendedGraphicsState((COSDictionary)base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, extGState);
        }
        return extGState;
    }

    public PDShading getShading(COSName name) throws IOException {
        PDShading cached;
        COSObject indirect = this.getIndirect(COSName.SHADING, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getShading(indirect)) != null) {
            return cached;
        }
        PDShading shading = null;
        COSBase base = this.get(COSName.SHADING, name);
        if (base instanceof COSDictionary) {
            shading = PDShading.create((COSDictionary)base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, shading);
        }
        return shading;
    }

    public PDAbstractPattern getPattern(COSName name) throws IOException {
        PDAbstractPattern cached;
        COSObject indirect = this.getIndirect(COSName.PATTERN, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getPattern(indirect)) != null) {
            return cached;
        }
        PDAbstractPattern pattern = null;
        COSBase base = this.get(COSName.PATTERN, name);
        if (base instanceof COSDictionary) {
            pattern = PDAbstractPattern.create((COSDictionary)base, this.getResourceCache());
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, pattern);
        }
        return pattern;
    }

    public PDPropertyList getProperties(COSName name) {
        PDPropertyList cached;
        COSObject indirect = this.getIndirect(COSName.PROPERTIES, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getProperties(indirect)) != null) {
            return cached;
        }
        PDPropertyList propertyList = null;
        COSBase base = this.get(COSName.PROPERTIES, name);
        if (base instanceof COSDictionary) {
            propertyList = PDPropertyList.create((COSDictionary)base);
        }
        if (this.cache != null && indirect != null) {
            this.cache.put(indirect, propertyList);
        }
        return propertyList;
    }

    public boolean isImageXObject(COSName name) {
        COSBase value = this.get(COSName.XOBJECT, name);
        if (value == null) {
            return false;
        }
        if (value instanceof COSObject) {
            value = ((COSObject)value).getObject();
        }
        if (!(value instanceof COSStream)) {
            return false;
        }
        COSStream stream = (COSStream)value;
        return COSName.IMAGE.equals(stream.getCOSName(COSName.SUBTYPE));
    }

    public PDXObject getXObject(COSName name) throws IOException {
        PDXObject cached;
        COSObject indirect = this.getIndirect(COSName.XOBJECT, name);
        if (this.cache != null && indirect != null && (cached = this.cache.getXObject(indirect)) != null) {
            return cached;
        }
        COSBase value = this.get(COSName.XOBJECT, name);
        PDXObject xobject = value == null ? null : (value instanceof COSObject ? PDXObject.createXObject(((COSObject)value).getObject(), this) : PDXObject.createXObject(value, this));
        if (this.cache != null && indirect != null && this.isAllowedCache(xobject)) {
            this.cache.put(indirect, xobject);
        }
        return xobject;
    }

    private boolean isAllowedCache(PDXObject xobject) {
        COSBase colorSpace;
        if (xobject instanceof PDImageXObject && (colorSpace = xobject.getCOSObject().getDictionaryObject(COSName.COLORSPACE)) instanceof COSName) {
            COSName colorSpaceName = (COSName)colorSpace;
            if (colorSpaceName.equals(COSName.DEVICECMYK) && this.hasColorSpace(COSName.DEFAULT_CMYK)) {
                return false;
            }
            if (colorSpaceName.equals(COSName.DEVICERGB) && this.hasColorSpace(COSName.DEFAULT_RGB)) {
                return false;
            }
            if (colorSpaceName.equals(COSName.DEVICEGRAY) && this.hasColorSpace(COSName.DEFAULT_GRAY)) {
                return false;
            }
            if (this.hasColorSpace(colorSpaceName)) {
                return false;
            }
        }
        return true;
    }

    private COSObject getIndirect(COSName kind, COSName name) {
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict == null) {
            return null;
        }
        COSBase base = dict.getItem(name);
        if (base instanceof COSObject) {
            return (COSObject)base;
        }
        return null;
    }

    private COSBase get(COSName kind, COSName name) {
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict == null) {
            return null;
        }
        return dict.getDictionaryObject(name);
    }

    public Iterable<COSName> getColorSpaceNames() {
        return this.getNames(COSName.COLORSPACE);
    }

    public Iterable<COSName> getXObjectNames() {
        return this.getNames(COSName.XOBJECT);
    }

    public Iterable<COSName> getFontNames() {
        return this.getNames(COSName.FONT);
    }

    public Iterable<COSName> getPropertiesNames() {
        return this.getNames(COSName.PROPERTIES);
    }

    public Iterable<COSName> getShadingNames() {
        return this.getNames(COSName.SHADING);
    }

    public Iterable<COSName> getPatternNames() {
        return this.getNames(COSName.PATTERN);
    }

    public Iterable<COSName> getExtGStateNames() {
        return this.getNames(COSName.EXT_G_STATE);
    }

    private Iterable<COSName> getNames(COSName kind) {
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict == null) {
            return Collections.emptySet();
        }
        return dict.keySet();
    }

    public COSName add(PDFont font) {
        return this.add(COSName.FONT, "F", font);
    }

    public COSName add(PDColorSpace colorSpace) {
        return this.add(COSName.COLORSPACE, "cs", colorSpace);
    }

    public COSName add(PDExtendedGraphicsState extGState) {
        return this.add(COSName.EXT_G_STATE, "gs", extGState);
    }

    public COSName add(PDShading shading) {
        return this.add(COSName.SHADING, "sh", shading);
    }

    public COSName add(PDAbstractPattern pattern) {
        return this.add(COSName.PATTERN, "p", pattern);
    }

    public COSName add(PDPropertyList properties) {
        if (properties instanceof PDOptionalContentGroup) {
            return this.add(COSName.PROPERTIES, "oc", properties);
        }
        return this.add(COSName.PROPERTIES, "Prop", properties);
    }

    public COSName add(PDImageXObject image) {
        return this.add(COSName.XOBJECT, "Im", image);
    }

    public COSName add(PDFormXObject form) {
        return this.add(COSName.XOBJECT, "Form", form);
    }

    public COSName add(PDXObject xobject, String prefix) {
        return this.add(COSName.XOBJECT, prefix, xobject);
    }

    private COSName add(COSName kind, String prefix, COSObjectable object) {
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict != null && dict.containsValue(object.getCOSObject())) {
            return dict.getKeyForValue(object.getCOSObject());
        }
        if (dict != null && COSName.FONT.equals(kind)) {
            for (Map.Entry<COSName, COSBase> entry : dict.entrySet()) {
                if (!(entry.getValue() instanceof COSObject) || object.getCOSObject() != ((COSObject)entry.getValue()).getObject()) continue;
                return entry.getKey();
            }
        }
        COSName name = this.createKey(kind, prefix);
        this.put(kind, name, object);
        return name;
    }

    private COSName createKey(COSName kind, String prefix) {
        String key;
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict == null) {
            return COSName.getPDFName(prefix + 1);
        }
        int n = dict.keySet().size();
        while (dict.containsKey(key = prefix + ++n)) {
        }
        return COSName.getPDFName(key);
    }

    private void put(COSName kind, COSName name, COSObjectable object) {
        COSDictionary dict = this.resources.getCOSDictionary(kind);
        if (dict == null) {
            dict = new COSDictionary();
            this.resources.setItem(kind, (COSBase)dict);
        }
        dict.setItem(name, object);
    }

    public void put(COSName name, PDFont font) {
        this.put(COSName.FONT, name, font);
    }

    public void put(COSName name, PDColorSpace colorSpace) {
        this.put(COSName.COLORSPACE, name, colorSpace);
    }

    public void put(COSName name, PDExtendedGraphicsState extGState) {
        this.put(COSName.EXT_G_STATE, name, extGState);
    }

    public void put(COSName name, PDShading shading) {
        this.put(COSName.SHADING, name, shading);
    }

    public void put(COSName name, PDAbstractPattern pattern) {
        this.put(COSName.PATTERN, name, pattern);
    }

    public void put(COSName name, PDPropertyList properties) {
        this.put(COSName.PROPERTIES, name, properties);
    }

    public void put(COSName name, PDXObject xobject) {
        this.put(COSName.XOBJECT, name, xobject);
    }

    public ResourceCache getResourceCache() {
        return this.cache;
    }
}

