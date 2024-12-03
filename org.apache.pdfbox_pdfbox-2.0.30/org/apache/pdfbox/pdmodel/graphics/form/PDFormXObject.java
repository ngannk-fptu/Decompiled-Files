/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.form;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroupAttributes;
import org.apache.pdfbox.util.Matrix;

public class PDFormXObject
extends PDXObject
implements PDContentStream {
    private PDTransparencyGroupAttributes group;
    private final ResourceCache cache;

    public PDFormXObject(PDStream stream) {
        super(stream, COSName.FORM);
        this.cache = null;
    }

    public PDFormXObject(COSStream stream) {
        super(stream, COSName.FORM);
        this.cache = null;
    }

    public PDFormXObject(COSStream stream, ResourceCache cache) {
        super(stream, COSName.FORM);
        this.cache = cache;
    }

    public PDFormXObject(PDDocument document) {
        super(document, COSName.FORM);
        this.cache = null;
    }

    public int getFormType() {
        return this.getCOSObject().getInt(COSName.FORMTYPE, 1);
    }

    public void setFormType(int formType) {
        this.getCOSObject().setInt(COSName.FORMTYPE, formType);
    }

    public PDTransparencyGroupAttributes getGroup() {
        COSDictionary dic;
        if (this.group == null && (dic = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.GROUP)) != null) {
            this.group = new PDTransparencyGroupAttributes(dic);
        }
        return this.group;
    }

    public PDStream getContentStream() {
        return new PDStream(this.getCOSObject());
    }

    @Override
    public InputStream getContents() throws IOException {
        return this.getCOSObject().createInputStream();
    }

    @Override
    public PDResources getResources() {
        COSDictionary resources = this.getCOSObject().getCOSDictionary(COSName.RESOURCES);
        if (resources != null) {
            return new PDResources(resources, this.cache);
        }
        if (this.getCOSObject().containsKey(COSName.RESOURCES)) {
            return new PDResources();
        }
        return null;
    }

    public void setResources(PDResources resources) {
        this.getCOSObject().setItem(COSName.RESOURCES, (COSObjectable)resources);
    }

    @Override
    public PDRectangle getBBox() {
        PDRectangle retval = null;
        COSArray array = (COSArray)this.getCOSObject().getDictionaryObject(COSName.BBOX);
        if (array != null) {
            retval = new PDRectangle(array);
        }
        return retval;
    }

    public void setBBox(PDRectangle bbox) {
        if (bbox == null) {
            this.getCOSObject().removeItem(COSName.BBOX);
        } else {
            this.getCOSObject().setItem(COSName.BBOX, (COSBase)bbox.getCOSArray());
        }
    }

    @Override
    public Matrix getMatrix() {
        return Matrix.createMatrix(this.getCOSObject().getDictionaryObject(COSName.MATRIX));
    }

    public void setMatrix(AffineTransform transform) {
        COSArray matrix = new COSArray();
        double[] values = new double[6];
        transform.getMatrix(values);
        for (double v : values) {
            matrix.add(new COSFloat((float)v));
        }
        this.getCOSObject().setItem(COSName.MATRIX, (COSBase)matrix);
    }

    public int getStructParents() {
        return this.getCOSObject().getInt(COSName.STRUCT_PARENTS);
    }

    public void setStructParents(int structParent) {
        this.getCOSObject().setInt(COSName.STRUCT_PARENTS, structParent);
    }

    public PDPropertyList getOptionalContent() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.OC);
        if (base instanceof COSDictionary) {
            return PDPropertyList.create((COSDictionary)base);
        }
        return null;
    }

    public void setOptionalContent(PDPropertyList oc) {
        this.getCOSObject().setItem(COSName.OC, (COSObjectable)oc);
    }
}

