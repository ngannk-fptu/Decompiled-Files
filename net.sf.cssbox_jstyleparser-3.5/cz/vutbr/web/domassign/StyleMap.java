/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.domassign.MultiMap;
import org.w3c.dom.Element;

public class StyleMap
extends MultiMap<Element, Selector.PseudoElementType, NodeData> {
    public StyleMap(int size) {
        super(size);
    }

    @Override
    protected NodeData createDataInstance() {
        return CSSFactory.createNodeData();
    }
}

