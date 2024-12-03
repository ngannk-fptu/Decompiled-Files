/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.state;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.util.Matrix;

public final class PDSoftMask
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDSoftMask.class);
    private final COSDictionary dictionary;
    private COSName subType = null;
    private PDTransparencyGroup group = null;
    private COSArray backdropColor = null;
    private PDFunction transferFunction = null;
    private Matrix ctm;

    public static PDSoftMask create(COSBase dictionary) {
        if (dictionary instanceof COSName) {
            if (COSName.NONE.equals(dictionary)) {
                return null;
            }
            LOG.warn((Object)("Invalid SMask " + dictionary));
            return null;
        }
        if (dictionary instanceof COSDictionary) {
            return new PDSoftMask((COSDictionary)dictionary);
        }
        LOG.warn((Object)("Invalid SMask " + dictionary));
        return null;
    }

    public PDSoftMask(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public COSName getSubType() {
        if (this.subType == null) {
            this.subType = (COSName)this.getCOSObject().getDictionaryObject(COSName.S);
        }
        return this.subType;
    }

    public PDTransparencyGroup getGroup() throws IOException {
        PDXObject x;
        COSBase cosGroup;
        if (this.group == null && (cosGroup = this.getCOSObject().getDictionaryObject(COSName.G)) != null && (x = PDXObject.createXObject(cosGroup, null)) instanceof PDTransparencyGroup) {
            this.group = (PDTransparencyGroup)x;
        }
        return this.group;
    }

    public COSArray getBackdropColor() {
        if (this.backdropColor == null) {
            this.backdropColor = (COSArray)this.getCOSObject().getDictionaryObject(COSName.BC);
        }
        return this.backdropColor;
    }

    public PDFunction getTransferFunction() throws IOException {
        COSBase cosTF;
        if (this.transferFunction == null && (cosTF = this.getCOSObject().getDictionaryObject(COSName.TR)) != null) {
            this.transferFunction = PDFunction.create(cosTF);
        }
        return this.transferFunction;
    }

    void setInitialTransformationMatrix(Matrix ctm) {
        this.ctm = ctm;
    }

    public Matrix getInitialTransformationMatrix() {
        return this.ctm;
    }
}

