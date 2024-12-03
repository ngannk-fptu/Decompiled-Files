/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.text.translate.CodePointTranslator;

public class UnicodeUnpairedSurrogateRemover
extends CodePointTranslator {
    @Override
    public boolean translate(int codePoint, Writer writer) throws IOException {
        return codePoint >= 55296 && codePoint <= 57343;
    }
}

