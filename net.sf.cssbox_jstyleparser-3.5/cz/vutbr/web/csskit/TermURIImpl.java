/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.csskit.TermImpl;
import java.net.URL;
import org.unbescape.css.CssEscape;

public class TermURIImpl
extends TermImpl<String>
implements TermURI {
    protected URL base;

    protected TermURIImpl() {
    }

    public TermURI setValue(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Invalid uri for TermURI(null)");
        }
        this.value = uri;
        return this;
    }

    @Override
    public TermURI setBase(URL base) {
        this.base = base;
        return this;
    }

    @Override
    public URL getBase() {
        return this.base;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        sb.append("url('").append(CssEscape.escapeCssString((String)((String)this.value))).append("')");
        return sb.toString();
    }
}

