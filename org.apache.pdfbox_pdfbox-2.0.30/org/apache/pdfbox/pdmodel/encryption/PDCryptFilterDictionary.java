/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDCryptFilterDictionary
implements COSObjectable {
    protected COSDictionary cryptFilterDictionary = null;

    public PDCryptFilterDictionary() {
        this.cryptFilterDictionary = new COSDictionary();
    }

    public PDCryptFilterDictionary(COSDictionary d) {
        this.cryptFilterDictionary = d;
    }

    @Deprecated
    public COSDictionary getCOSDictionary() {
        return this.cryptFilterDictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.cryptFilterDictionary;
    }

    public void setLength(int length) {
        this.cryptFilterDictionary.setInt(COSName.LENGTH, length);
    }

    public int getLength() {
        return this.cryptFilterDictionary.getInt(COSName.LENGTH, 40);
    }

    public void setCryptFilterMethod(COSName cfm) {
        this.cryptFilterDictionary.setItem(COSName.CFM, (COSBase)cfm);
    }

    public COSName getCryptFilterMethod() {
        return (COSName)this.cryptFilterDictionary.getDictionaryObject(COSName.CFM);
    }

    public boolean isEncryptMetaData() {
        COSBase value = this.getCOSObject().getDictionaryObject(COSName.ENCRYPT_META_DATA);
        if (value instanceof COSBoolean) {
            return ((COSBoolean)value).getValue();
        }
        return true;
    }

    public void setEncryptMetaData(boolean encryptMetaData) {
        this.getCOSObject().setBoolean(COSName.ENCRYPT_META_DATA, encryptMetaData);
    }
}

