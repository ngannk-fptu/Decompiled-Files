/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xhtmlrenderer.css.newmatch.Selector;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class Ruleset {
    private int _origin;
    private List _props;
    private List _fsSelectors = new ArrayList();

    public Ruleset(int orig) {
        this._origin = orig;
        this._props = new LinkedList();
        this._fsSelectors = new LinkedList();
    }

    public List getPropertyDeclarations() {
        return Collections.unmodifiableList(this._props);
    }

    public void addProperty(PropertyDeclaration decl) {
        this._props.add(decl);
    }

    public void addAllProperties(List props) {
        this._props.addAll(props);
    }

    public void addFSSelector(Selector selector) {
        this._fsSelectors.add(selector);
    }

    public List getFSSelectors() {
        return this._fsSelectors;
    }

    public int getOrigin() {
        return this._origin;
    }
}

