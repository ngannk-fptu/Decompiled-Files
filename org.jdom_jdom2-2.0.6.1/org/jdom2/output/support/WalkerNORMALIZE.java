/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import org.jdom2.Content;
import org.jdom2.Verifier;
import org.jdom2.output.support.AbstractFormattedWalker;
import org.jdom2.output.support.FormatStack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WalkerNORMALIZE
extends AbstractFormattedWalker {
    public WalkerNORMALIZE(List<? extends Content> content, FormatStack fstack, boolean escape) {
        super(content, fstack, escape);
    }

    private boolean isSpaceFirst(String text) {
        if (text.length() > 0) {
            return Verifier.isXMLWhitespace(text.charAt(0));
        }
        return false;
    }

    private boolean isSpaceLast(String text) {
        int tlen = text.length();
        return tlen > 0 && Verifier.isXMLWhitespace(text.charAt(tlen - 1));
    }

    @Override
    protected void analyzeMultiText(AbstractFormattedWalker.MultiText mtext, int offset, int len) {
        boolean needspace = false;
        boolean between = false;
        String ttext = null;
        block4: for (int i = 0; i < len; ++i) {
            Content c = this.get(offset + i);
            switch (c.getCType()) {
                case Text: {
                    ttext = c.getValue();
                    if (Verifier.isAllXMLWhitespace(ttext)) {
                        if (!between || ttext.length() <= 0) continue block4;
                        needspace = true;
                        continue block4;
                    }
                    if (between && (needspace || this.isSpaceFirst(ttext))) {
                        mtext.appendText(AbstractFormattedWalker.Trim.NONE, " ");
                    }
                    mtext.appendText(AbstractFormattedWalker.Trim.COMPACT, ttext);
                    between = true;
                    needspace = this.isSpaceLast(ttext);
                    continue block4;
                }
                case CDATA: {
                    ttext = c.getValue();
                    if (Verifier.isAllXMLWhitespace(ttext)) {
                        if (!between || ttext.length() <= 0) continue block4;
                        needspace = true;
                        continue block4;
                    }
                    if (between && (needspace || this.isSpaceFirst(ttext))) {
                        mtext.appendText(AbstractFormattedWalker.Trim.NONE, " ");
                    }
                    mtext.appendCDATA(AbstractFormattedWalker.Trim.COMPACT, ttext);
                    between = true;
                    needspace = this.isSpaceLast(ttext);
                    continue block4;
                }
                default: {
                    ttext = null;
                    if (between && needspace) {
                        mtext.appendText(AbstractFormattedWalker.Trim.NONE, " ");
                    }
                    mtext.appendRaw(c);
                    between = true;
                    needspace = false;
                }
            }
        }
    }
}

