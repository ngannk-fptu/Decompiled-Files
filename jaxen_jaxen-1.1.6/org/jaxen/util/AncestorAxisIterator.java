/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.util;

import org.jaxen.Navigator;
import org.jaxen.util.AncestorOrSelfAxisIterator;

public class AncestorAxisIterator
extends AncestorOrSelfAxisIterator {
    public AncestorAxisIterator(Object contextNode, Navigator navigator) {
        super(contextNode, navigator);
        this.next();
    }
}

