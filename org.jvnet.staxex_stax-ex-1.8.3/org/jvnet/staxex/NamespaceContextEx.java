/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public interface NamespaceContextEx
extends NamespaceContext,
Iterable<Binding> {
    @Override
    public Iterator<Binding> iterator();

    public static interface Binding {
        public String getPrefix();

        public String getNamespaceURI();
    }
}

