/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.dom.Filter;
import org.apache.xalan.xsltc.dom.StepIterator;
import org.apache.xml.dtm.DTMAxisIterator;

public final class FilteredStepIterator
extends StepIterator {
    private Filter _filter;

    public FilteredStepIterator(DTMAxisIterator source, DTMAxisIterator iterator, Filter filter) {
        super(source, iterator);
        this._filter = filter;
    }

    @Override
    public int next() {
        int node;
        while ((node = super.next()) != -1) {
            if (!this._filter.test(node)) continue;
            return this.returnNode(node);
        }
        return node;
    }
}

