/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package org.apache.velocity.anakia;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.velocity.anakia.NodeList;
import org.jdom.Element;

public class TreeWalker {
    public NodeList allElements(Element e) {
        ArrayList theElements = new ArrayList();
        this.treeWalk(e, theElements);
        return new NodeList(theElements, false);
    }

    private final void treeWalk(Element e, Collection theElements) {
        for (Element child : e.getChildren()) {
            theElements.add(child);
            this.treeWalk(child, theElements);
        }
    }
}

