/*
 * Decompiled with CFR 0.152.
 */
package org.dom4j.tree;

import java.util.Iterator;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.FilterIterator;

public class ElementIterator
extends FilterIterator<Node> {
    public ElementIterator(Iterator<Node> proxy) {
        super(proxy);
    }

    @Override
    protected boolean matches(Node element) {
        return element instanceof Element;
    }
}

