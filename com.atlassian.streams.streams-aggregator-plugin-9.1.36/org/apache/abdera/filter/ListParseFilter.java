/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.filter.ParseFilter;

public interface ListParseFilter
extends ParseFilter {
    public ListParseFilter add(QName var1);

    public boolean contains(QName var1);

    public ListParseFilter add(QName var1, QName var2);

    public boolean contains(QName var1, QName var2);
}

