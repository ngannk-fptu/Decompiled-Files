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
public class WalkerTRIM_FULL_WHITE
extends AbstractFormattedWalker {
    public WalkerTRIM_FULL_WHITE(List<? extends Content> content, FormatStack fstack, boolean escape) {
        super(content, fstack, escape);
    }

    @Override
    protected void analyzeMultiText(AbstractFormattedWalker.MultiText mtext, int offset, int len) {
        Content c;
        int ln = len;
        while (--ln >= 0 && (c = this.get(offset + ln)) instanceof Text && Verifier.isAllXMLWhitespace(c.getValue())) {
        }
        if (ln < 0) {
            return;
        }
        block5: for (int i = 0; i < len; ++i) {
            Content c2 = this.get(offset + i);
            switch (c2.getCType()) {
                case Text: {
                    mtext.appendText(AbstractFormattedWalker.Trim.NONE, c2.getValue());
                    continue block5;
                }
                case CDATA: {
                    mtext.appendCDATA(AbstractFormattedWalker.Trim.NONE, c2.getValue());
                    continue block5;
                }
                default: {
                    mtext.appendRaw(c2);
                }
            }
        }
    }
}

