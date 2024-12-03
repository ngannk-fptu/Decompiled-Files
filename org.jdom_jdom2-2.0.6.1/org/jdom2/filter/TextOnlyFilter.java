/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.Content;
import org.jdom2.Text;
import org.jdom2.filter.AbstractFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class TextOnlyFilter
extends AbstractFilter<Text> {
    private static final long serialVersionUID = 200L;

    TextOnlyFilter() {
    }

    @Override
    public Text filter(Object content) {
        Text txt;
        if (content instanceof Text && (txt = (Text)content).getCType() == Content.CType.Text) {
            return txt;
        }
        return null;
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof TextOnlyFilter;
    }
}

