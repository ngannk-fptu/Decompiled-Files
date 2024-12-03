/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.util.Charsets;

public class PDActionURI
extends PDAction {
    public static final String SUB_TYPE = "URI";

    public PDActionURI() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionURI(COSDictionary a) {
        super(a);
    }

    @Deprecated
    public String getS() {
        return this.action.getNameAsString(COSName.S);
    }

    @Deprecated
    public void setS(String s) {
        this.action.setName(COSName.S, s);
    }

    public String getURI() {
        COSBase base = this.action.getDictionaryObject(COSName.URI);
        if (base instanceof COSString) {
            byte[] bytes = ((COSString)base).getBytes();
            if (bytes.length >= 2) {
                if ((bytes[0] & 0xFF) == 254 && (bytes[1] & 0xFF) == 255) {
                    return this.action.getString(COSName.URI);
                }
                if ((bytes[0] & 0xFF) == 255 && (bytes[1] & 0xFF) == 254) {
                    return this.action.getString(COSName.URI);
                }
            }
            return new String(bytes, Charsets.UTF_8);
        }
        return null;
    }

    public void setURI(String uri) {
        this.action.setString(COSName.URI, uri);
    }

    public boolean shouldTrackMousePosition() {
        return this.action.getBoolean("IsMap", false);
    }

    public void setTrackMousePosition(boolean value) {
        this.action.setBoolean("IsMap", value);
    }
}

