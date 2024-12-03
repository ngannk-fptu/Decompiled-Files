/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Collections;
import java.util.Iterator;
import javanet.staxutils.ExtendedNamespaceContext;
import javanet.staxutils.StaticNamespaceContext;
import javax.xml.namespace.NamespaceContext;

public final class EmptyNamespaceContext
implements ExtendedNamespaceContext,
StaticNamespaceContext {
    public static final EmptyNamespaceContext INSTANCE = new EmptyNamespaceContext();

    public static final NamespaceContext getInstance() {
        return INSTANCE;
    }

    public String getNamespaceURI(String prefix) {
        return null;
    }

    public String getPrefix(String nsURI) {
        return null;
    }

    public Iterator getPrefixes(String nsURI) {
        return Collections.EMPTY_SET.iterator();
    }

    public NamespaceContext getParent() {
        return null;
    }

    public boolean isPrefixDeclared(String prefix) {
        return false;
    }

    public Iterator getPrefixes() {
        return Collections.EMPTY_LIST.iterator();
    }

    public Iterator getDeclaredPrefixes() {
        return Collections.EMPTY_LIST.iterator();
    }
}

