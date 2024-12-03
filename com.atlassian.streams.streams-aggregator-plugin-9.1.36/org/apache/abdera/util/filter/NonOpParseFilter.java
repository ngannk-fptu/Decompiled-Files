/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.util.filter.AbstractParseFilter;

public class NonOpParseFilter
extends AbstractParseFilter {
    private static final long serialVersionUID = -1895875728388522456L;

    public boolean acceptable(QName qname) {
        return true;
    }

    public boolean acceptable(QName qname, QName attribute) {
        return true;
    }
}

