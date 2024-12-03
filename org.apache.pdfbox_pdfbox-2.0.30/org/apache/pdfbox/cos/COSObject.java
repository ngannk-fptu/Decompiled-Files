/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.cos.ICOSVisitor;

public class COSObject
extends COSBase
implements COSUpdateInfo {
    private COSBase baseObject;
    private long objectNumber;
    private int generationNumber;
    private boolean needToBeUpdated;
    private boolean dereferencingInProgress = false;

    public COSObject(COSBase object) throws IOException {
        this.setObject(object);
    }

    @Deprecated
    public COSBase getDictionaryObject(COSName key) {
        COSBase retval = null;
        if (this.baseObject instanceof COSDictionary) {
            retval = ((COSDictionary)this.baseObject).getDictionaryObject(key);
        }
        return retval;
    }

    @Deprecated
    public COSBase getItem(COSName key) {
        COSBase retval = null;
        if (this.baseObject instanceof COSDictionary) {
            retval = ((COSDictionary)this.baseObject).getItem(key);
        }
        return retval;
    }

    public COSBase getObject() {
        return this.baseObject;
    }

    public final void setObject(COSBase object) throws IOException {
        this.baseObject = object;
    }

    public boolean derefencingInProgress() {
        return this.dereferencingInProgress;
    }

    public void dereferencingStarted() {
        this.dereferencingInProgress = true;
    }

    public void dereferencingFinished() {
        this.dereferencingInProgress = false;
    }

    public String toString() {
        return "COSObject{" + this.objectNumber + ", " + this.generationNumber + "}";
    }

    public long getObjectNumber() {
        return this.objectNumber;
    }

    public void setObjectNumber(long objectNum) {
        this.objectNumber = objectNum;
    }

    public int getGenerationNumber() {
        return this.generationNumber;
    }

    public void setGenerationNumber(int generationNumberValue) {
        this.generationNumber = generationNumberValue;
    }

    @Override
    public Object accept(ICOSVisitor visitor) throws IOException {
        COSBase object = this.getObject();
        return object != null ? object.accept(visitor) : COSNull.NULL.accept(visitor);
    }

    @Override
    public boolean isNeedToBeUpdated() {
        return this.needToBeUpdated;
    }

    @Override
    public void setNeedToBeUpdated(boolean flag) {
        this.needToBeUpdated = flag;
    }
}

