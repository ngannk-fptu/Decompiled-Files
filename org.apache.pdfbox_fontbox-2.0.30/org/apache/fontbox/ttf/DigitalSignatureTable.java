/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class DigitalSignatureTable
extends TTFTable {
    public static final String TAG = "DSIG";

    DigitalSignatureTable(TrueTypeFont font) {
        super(font);
    }
}

