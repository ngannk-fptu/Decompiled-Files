/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;

public class PDPostScriptXObject
extends PDXObject {
    public PDPostScriptXObject(COSStream stream) {
        super(stream, COSName.PS);
    }
}

