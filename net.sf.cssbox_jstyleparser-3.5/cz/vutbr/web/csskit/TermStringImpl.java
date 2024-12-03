/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermString;
import cz.vutbr.web.csskit.TermImpl;
import org.unbescape.css.CssEscape;

public class TermStringImpl
extends TermImpl<String>
implements TermString {
    protected TermStringImpl() {
    }

    public TermString setValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid value for TermString(null)");
        }
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append("'").append(CssEscape.escapeCssString((String)((String)this.value))).append("'");
        return sb.toString();
    }
}

