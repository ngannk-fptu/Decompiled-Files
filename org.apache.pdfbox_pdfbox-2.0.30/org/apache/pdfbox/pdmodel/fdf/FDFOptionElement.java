/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class FDFOptionElement
implements COSObjectable {
    private final COSArray option;

    public FDFOptionElement() {
        this.option = new COSArray();
        this.option.add(new COSString(""));
        this.option.add(new COSString(""));
    }

    public FDFOptionElement(COSArray o) {
        this.option = o;
    }

    @Override
    public COSBase getCOSObject() {
        return this.option;
    }

    public COSArray getCOSArray() {
        return this.option;
    }

    public String getOption() {
        return ((COSString)this.option.getObject(0)).getString();
    }

    public void setOption(String opt) {
        this.option.set(0, new COSString(opt));
    }

    public String getDefaultAppearanceString() {
        return ((COSString)this.option.getObject(1)).getString();
    }

    public void setDefaultAppearanceString(String da) {
        this.option.set(1, new COSString(da));
    }
}

