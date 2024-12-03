/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public abstract class COSBase
implements COSObjectable {
    private boolean direct;

    @Override
    public COSBase getCOSObject() {
        return this;
    }

    public abstract Object accept(ICOSVisitor var1) throws IOException;

    public boolean isDirect() {
        return this.direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }
}

