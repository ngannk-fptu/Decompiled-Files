/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.util.filter.AbstractListParseFilter;

public class WhiteListParseFilter
extends AbstractListParseFilter {
    private static final long serialVersionUID = -2126524829459798481L;
    private final boolean listAttributesExplicitly;

    public WhiteListParseFilter() {
        this(false);
    }

    public WhiteListParseFilter(boolean listAttributesExplicitly) {
        this.listAttributesExplicitly = listAttributesExplicitly;
    }

    public boolean acceptable(QName qname) {
        return this.contains(qname);
    }

    public boolean acceptable(QName qname, QName attribute) {
        return this.listAttributesExplicitly ? this.contains(qname, attribute) && this.acceptable(qname) : this.acceptable(qname);
    }
}

