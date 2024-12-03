/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import org.jdom2.Content;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;
import org.jdom2.output.support.WalkerNORMALIZE;
import org.jdom2.output.support.WalkerPRESERVE;
import org.jdom2.output.support.WalkerTRIM;
import org.jdom2.output.support.WalkerTRIM_FULL_WHITE;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractOutputProcessor {
    protected Walker buildWalker(FormatStack fstack, List<? extends Content> content, boolean escape) {
        switch (fstack.getTextMode()) {
            case PRESERVE: {
                return new WalkerPRESERVE(content);
            }
            case NORMALIZE: {
                return new WalkerNORMALIZE(content, fstack, escape);
            }
            case TRIM: {
                return new WalkerTRIM(content, fstack, escape);
            }
            case TRIM_FULL_WHITE: {
                return new WalkerTRIM_FULL_WHITE(content, fstack, escape);
            }
        }
        return new WalkerPRESERVE(content);
    }
}

