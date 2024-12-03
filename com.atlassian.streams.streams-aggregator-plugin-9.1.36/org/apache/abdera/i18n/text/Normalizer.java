/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text;

import java.io.IOException;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.Codepoint;
import org.apache.abdera.i18n.text.CodepointIterator;
import org.apache.abdera.i18n.text.data.UnicodeCharacterDatabase;

public final class Normalizer {
    private Normalizer() {
    }

    public static String normalize(CharSequence source) {
        return Normalizer.normalize(source, Form.KC);
    }

    public static String normalize(CharSequence source, Form form) {
        return Normalizer.normalize(source, form, new StringBuilder());
    }

    public static String normalize(CharSequence source, Form form, StringBuilder buf) {
        if (source.length() != 0) {
            try {
                Normalizer.decompose(source, form, buf);
                Normalizer.compose(form, buf);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return buf.toString();
    }

    private static void decompose(CharSequence source, Form form, StringBuilder buf) throws IOException {
        StringBuilder internal = new StringBuilder();
        CodepointIterator ci = CodepointIterator.forCharSequence(source);
        boolean canonical = form.isCanonical();
        while (ci.hasNext()) {
            Codepoint c = ci.next();
            internal.setLength(0);
            UnicodeCharacterDatabase.decompose(c.getValue(), canonical, internal);
            CodepointIterator ii = CodepointIterator.forCharSequence(internal);
            while (ii.hasNext()) {
                Codepoint ch = ii.next();
                int i = Normalizer.findInsertionPoint(buf, ch.getValue());
                buf.insert(i, CharUtils.toString(ch.getValue()));
            }
        }
    }

    private static int findInsertionPoint(StringBuilder buf, int c) {
        int i;
        int cc = UnicodeCharacterDatabase.getCanonicalClass(c);
        if (cc != 0) {
            int ch;
            for (i = buf.length(); i > 0 && UnicodeCharacterDatabase.getCanonicalClass(ch = CharUtils.codepointAt(buf, i - 1).getValue()) > cc; i -= CharUtils.length(c)) {
            }
        }
        return i;
    }

    private static void compose(Form form, StringBuilder buf) throws IOException {
        int c;
        if (!form.isComposition()) {
            return;
        }
        int pos = 0;
        int lc = CharUtils.codepointAt(buf, pos).getValue();
        int cpos = CharUtils.length(lc);
        int lcc = UnicodeCharacterDatabase.getCanonicalClass(lc);
        if (lcc != 0) {
            lcc = 256;
        }
        int len = buf.length();
        for (int dpos = cpos; dpos < buf.length(); dpos += CharUtils.length(c)) {
            c = CharUtils.codepointAt(buf, dpos).getValue();
            int cc = UnicodeCharacterDatabase.getCanonicalClass(c);
            char composite = UnicodeCharacterDatabase.getPairComposition(lc, c);
            if (composite != '\uffff' && (lcc < cc || lcc == 0)) {
                CharUtils.setChar((CharSequence)buf, pos, composite);
                lc = composite;
                continue;
            }
            if (cc == 0) {
                pos = cpos;
                lc = c;
            }
            lcc = cc;
            CharUtils.setChar((CharSequence)buf, cpos, c);
            if (buf.length() != len) {
                dpos += buf.length() - len;
                len = buf.length();
            }
            cpos += CharUtils.length(c);
        }
        buf.setLength(cpos);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Form {
        D(new Mask[0]),
        C(Mask.COMPOSITION),
        KD(Mask.COMPATIBILITY),
        KC(Mask.COMPATIBILITY, Mask.COMPOSITION);

        private int mask = 0;

        private Form(Mask ... masks) {
            for (Mask mask : masks) {
                this.mask |= mask.ordinal();
            }
        }

        public boolean isCompatibility() {
            return (this.mask & Mask.COMPATIBILITY.ordinal()) != 0;
        }

        public boolean isCanonical() {
            return !this.isCompatibility();
        }

        public boolean isComposition() {
            return (this.mask & Mask.COMPOSITION.ordinal()) != 0;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Mask {
        NONE,
        COMPATIBILITY,
        COMPOSITION;

    }
}

