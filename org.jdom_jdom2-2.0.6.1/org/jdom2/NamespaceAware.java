/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.List;
import org.jdom2.Namespace;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface NamespaceAware {
    public List<Namespace> getNamespacesInScope();

    public List<Namespace> getNamespacesIntroduced();

    public List<Namespace> getNamespacesInherited();
}

