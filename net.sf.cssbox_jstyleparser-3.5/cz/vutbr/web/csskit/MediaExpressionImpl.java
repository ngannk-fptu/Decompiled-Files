/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.MediaExpression;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.csskit.AbstractRule;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.Locale;
import org.unbescape.css.CssEscape;

public class MediaExpressionImpl
extends AbstractRule<Term<?>>
implements MediaExpression {
    protected String feature;

    @Override
    public String getFeature() {
        return this.feature;
    }

    @Override
    public void setFeature(String feature) {
        this.feature = feature.trim().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(CssEscape.escapeCssIdentifier((String)this.getFeature())).append(": ");
        sb = OutputUtil.appendList(sb, this.list, " ");
        sb.append(")");
        return sb.toString();
    }
}

