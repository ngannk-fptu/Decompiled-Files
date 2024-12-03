/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.domassign.MultiMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;

public class DeclarationMap
extends MultiMap<Element, Selector.PseudoElementType, List<Declaration>> {
    public void addDeclaration(Element el, Selector.PseudoElementType pseudo, Declaration decl) {
        List list = (List)this.getOrCreate(el, pseudo);
        list.add(decl);
    }

    public void sortDeclarations(Element el, Selector.PseudoElementType pseudo) {
        List list = (List)this.get(el, pseudo);
        if (list != null) {
            Collections.sort(list);
        }
    }

    @Override
    protected List<Declaration> createDataInstance() {
        return new ArrayList<Declaration>();
    }
}

