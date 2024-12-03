/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;

public class PDObjectReference
implements COSObjectable {
    public static final String TYPE = "OBJR";
    private final COSDictionary dictionary;

    public PDObjectReference() {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, TYPE);
    }

    public PDObjectReference(COSDictionary theDictionary) {
        this.dictionary = theDictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public COSObjectable getReferencedObject() {
        COSBase obj = this.getCOSObject().getDictionaryObject(COSName.OBJ);
        if (!(obj instanceof COSDictionary)) {
            return null;
        }
        try {
            PDXObject xobject;
            if (obj instanceof COSStream && (xobject = PDXObject.createXObject(obj, null)) != null) {
                return xobject;
            }
            COSDictionary objDictionary = (COSDictionary)obj;
            PDAnnotation annotation = PDAnnotation.createAnnotation(obj);
            if (!(annotation instanceof PDAnnotationUnknown) || COSName.ANNOT.equals(objDictionary.getDictionaryObject(COSName.TYPE))) {
                return annotation;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    public void setReferencedObject(PDAnnotation annotation) {
        this.getCOSObject().setItem(COSName.OBJ, (COSObjectable)annotation);
    }

    public void setReferencedObject(PDXObject xobject) {
        this.getCOSObject().setItem(COSName.OBJ, (COSObjectable)xobject);
    }
}

