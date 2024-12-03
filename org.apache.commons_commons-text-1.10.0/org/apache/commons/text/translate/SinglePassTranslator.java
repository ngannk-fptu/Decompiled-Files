/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.text.translate.CharSequenceTranslator;

abstract class SinglePassTranslator
extends CharSequenceTranslator {
    SinglePassTranslator() {
    }

    private String getClassName() {
        Class<?> clazz = this.getClass();
        return clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName();
    }

    @Override
    public int translate(CharSequence input, int index, Writer writer) throws IOException {
        if (index != 0) {
            throw new IllegalArgumentException(this.getClassName() + ".translate(final CharSequence input, final int index, final Writer out) can not handle a non-zero index.");
        }
        this.translateWhole(input, writer);
        return Character.codePointCount(input, index, input.length());
    }

    abstract void translateWhole(CharSequence var1, Writer var2) throws IOException;
}

