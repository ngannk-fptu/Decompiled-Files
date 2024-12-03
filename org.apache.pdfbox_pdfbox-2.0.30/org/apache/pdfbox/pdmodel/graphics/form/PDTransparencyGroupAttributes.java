/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public final class PDTransparencyGroupAttributes
implements COSObjectable {
    private final COSDictionary dictionary;
    private PDColorSpace colorSpace;

    public PDTransparencyGroupAttributes() {
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.S, (COSBase)COSName.TRANSPARENCY);
    }

    public PDTransparencyGroupAttributes(COSDictionary dic) {
        this.dictionary = dic;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDColorSpace getColorSpace() throws IOException {
        return this.getColorSpace(null);
    }

    public PDColorSpace getColorSpace(PDResources resources) throws IOException {
        if (this.colorSpace == null && this.getCOSObject().containsKey(COSName.CS)) {
            this.colorSpace = PDColorSpace.create(this.getCOSObject().getDictionaryObject(COSName.CS), resources);
        }
        return this.colorSpace;
    }

    public boolean isIsolated() {
        return this.getCOSObject().getBoolean(COSName.I, false);
    }

    public boolean isKnockout() {
        return this.getCOSObject().getBoolean(COSName.K, false);
    }
}

