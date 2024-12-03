/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.csskit.AbstractRule;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.Locale;
import org.unbescape.css.CssEscape;

public class MediaQueryImpl
extends AbstractRule<MediaExpression>
implements MediaQuery {
    protected boolean negative;
    protected String type;

    public MediaQueryImpl() {
        this.negative = false;
        this.type = null;
    }

    public MediaQueryImpl(String type, boolean negative) {
        this.negative = negative;
        this.type = type.trim().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean isNegative() {
        return this.negative;
    }

    @Override
    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isNegative()) {
            sb.append("NOT ");
        }
        if (this.getType() != null) {
            sb.append(CssEscape.escapeCssIdentifier((String)this.getType()));
            if (!this.isEmpty()) {
                sb.append(" AND ");
            }
        }
        sb = OutputUtil.appendList(sb, this.list, " AND ");
        return sb.toString();
    }
}

