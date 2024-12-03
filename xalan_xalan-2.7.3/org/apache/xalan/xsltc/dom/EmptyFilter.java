/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.Filter;

public final class EmptyFilter
implements Filter {
    @Override
    public boolean test(int node) {
        return true;
    }
}

