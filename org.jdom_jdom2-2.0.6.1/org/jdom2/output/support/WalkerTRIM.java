/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import org.jdom2.Content;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.output.support.AbstractFormattedWalker;
import org.jdom2.output.support.FormatStack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WalkerTRIM
extends AbstractFormattedWalker {
    public WalkerTRIM(List<? extends Content> content, FormatStack fstack, boolean escape) {
        super(content, fstack, escape);
    }

    @Override
    protected void analyzeMultiText(AbstractFormattedWalker.MultiText mtext, int offset, int len) {
        Content c;
        while (len > 0 && (c = this.get(offset)) instanceof Text && Verifier.isAllXMLWhitespace(c.getValue())) {
            ++offset;
            --len;
        }
        while (len > 0 && (c = this.get(offset + len - 1)) instanceof Text && Verifier.isAllXMLWhitespace(c.getValue())) {
            --len;
        }
        block6: for (int i = 0; i < len; ++i) {
            AbstractFormattedWalker.Trim trim = AbstractFormattedWalker.Trim.NONE;
            if (i + 1 == len) {
                trim = AbstractFormattedWalker.Trim.RIGHT;
            }
            if (i == 0) {
                trim = AbstractFormattedWalker.Trim.LEFT;
            }
            if (len == 1) {
                trim = AbstractFormattedWalker.Trim.BOTH;
            }
            Content c2 = this.get(offset + i);
            switch (c2.getCType()) {
                case Text: {
                    mtext.appendText(trim, c2.getValue());
                    continue block6;
                }
                case CDATA: {
                    mtext.appendCDATA(trim, c2.getValue());
                    continue block6;
                }
                default: {
                    mtext.appendRaw(c2);
                }
            }
        }
    }
}

