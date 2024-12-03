/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import java.util.ArrayList;
import java.util.List;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.SVGItem;

public class ListBuilder
implements ListHandler {
    private final AbstractSVGList abstractSVGList;
    protected List list;

    public ListBuilder(AbstractSVGList abstractSVGList) {
        this.abstractSVGList = abstractSVGList;
    }

    public List getList() {
        return this.list;
    }

    @Override
    public void startList() {
        this.list = new ArrayList();
    }

    @Override
    public void item(SVGItem item) {
        item.setParent(this.abstractSVGList);
        this.list.add(item);
    }

    @Override
    public void endList() {
    }
}

