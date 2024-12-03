/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.util.Collections;
import org.apache.pdfbox.cos.COSDictionary;

final class UnmodifiableCOSDictionary
extends COSDictionary {
    UnmodifiableCOSDictionary(COSDictionary dict) {
        this.items = Collections.unmodifiableMap(dict.items);
    }

    @Override
    public void mergeInto(COSDictionary dic) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNeedToBeUpdated(boolean flag) {
        throw new UnsupportedOperationException();
    }
}

