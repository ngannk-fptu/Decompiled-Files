/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.util;

import org.jaxen.Navigator;
import org.jaxen.util.DescendantAxisIterator;
import org.jaxen.util.SingleObjectIterator;

public class DescendantOrSelfAxisIterator
extends DescendantAxisIterator {
    public DescendantOrSelfAxisIterator(Object contextNode, Navigator navigator) {
        super(navigator, new SingleObjectIterator(contextNode));
    }
}

