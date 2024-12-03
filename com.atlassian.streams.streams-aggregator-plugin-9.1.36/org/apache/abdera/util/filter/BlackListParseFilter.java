/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.util.filter.AbstractListParseFilter;

public class BlackListParseFilter
extends AbstractListParseFilter {
    private static final long serialVersionUID = -8428373486568649179L;

    public boolean acceptable(QName qname) {
        return !this.contains(qname);
    }

    public boolean acceptable(QName qname, QName attribute) {
        return !this.contains(qname, attribute);
    }
}

