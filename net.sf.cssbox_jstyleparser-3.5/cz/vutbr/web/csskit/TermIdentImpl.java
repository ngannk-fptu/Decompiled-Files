/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.csskit.TermImpl;
import org.unbescape.css.CssEscape;

public class TermIdentImpl
extends TermImpl<String>
implements TermIdent {
    protected TermIdentImpl() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        if (this.value != null) {
            sb.append(CssEscape.escapeCssIdentifier((String)((String)this.value)));
        }
        return sb.toString();
    }
}

