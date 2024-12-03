/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;

public interface QNameSetSpecification {
    public boolean contains(QName var1);

    public boolean isAll();

    public boolean isEmpty();

    public boolean containsAll(QNameSetSpecification var1);

    public boolean isDisjoint(QNameSetSpecification var1);

    public QNameSet intersect(QNameSetSpecification var1);

    public QNameSet union(QNameSetSpecification var1);

    public QNameSet inverse();

    public Set<String> excludedURIs();

    public Set<String> includedURIs();

    public Set<QName> excludedQNamesInIncludedURIs();

    public Set<QName> includedQNamesInExcludedURIs();
}

