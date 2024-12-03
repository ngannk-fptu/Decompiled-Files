/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public interface ExtendedNamespaceContext
extends NamespaceContext {
    public NamespaceContext getParent();

    public boolean isPrefixDeclared(String var1);

    public Iterator getPrefixes();

    public Iterator getDeclaredPrefixes();
}

