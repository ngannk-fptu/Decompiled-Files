/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr.iter;

import java.util.Iterator;
import org.jaxen.ContextSupport;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.expr.iter.IterableAxis;

public class IterableNamespaceAxis
extends IterableAxis {
    private static final long serialVersionUID = -8022585664651357087L;

    public IterableNamespaceAxis(int value) {
        super(value);
    }

    public Iterator iterator(Object contextNode, ContextSupport support) throws UnsupportedAxisException {
        return support.getNavigator().getNamespaceAxisIterator(contextNode);
    }
}

