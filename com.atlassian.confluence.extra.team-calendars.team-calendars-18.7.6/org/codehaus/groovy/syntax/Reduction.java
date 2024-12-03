/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.syntax.CSTNode;
import org.codehaus.groovy.syntax.Token;

public class Reduction
extends CSTNode {
    public static final Reduction EMPTY = new Reduction();
    private List elements = null;
    private boolean marked = false;

    public Reduction(Token root) {
        this.elements = new ArrayList();
        this.set(0, root);
    }

    private Reduction() {
        this.elements = Collections.EMPTY_LIST;
    }

    public static Reduction newContainer() {
        return new Reduction(Token.NULL);
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public CSTNode get(int index) {
        CSTNode element = null;
        if (index < this.size()) {
            element = (CSTNode)this.elements.get(index);
        }
        return element;
    }

    @Override
    public Token getRoot() {
        if (this.size() > 0) {
            return (Token)this.elements.get(0);
        }
        return null;
    }

    @Override
    public void markAsExpression() {
        this.marked = true;
    }

    @Override
    public boolean isAnExpression() {
        if (this.isA(1911)) {
            return true;
        }
        return this.marked;
    }

    @Override
    public CSTNode add(CSTNode element) {
        return this.set(this.size(), element);
    }

    @Override
    public CSTNode set(int index, CSTNode element) {
        if (this.elements == null) {
            throw new GroovyBugError("attempt to set() on a EMPTY Reduction");
        }
        if (index == 0 && !(element instanceof Token)) {
            throw new GroovyBugError("attempt to set() a non-Token as root of a Reduction");
        }
        int count = this.elements.size();
        if (index >= count) {
            for (int i = count; i <= index; ++i) {
                this.elements.add(null);
            }
        }
        this.elements.set(index, element);
        return element;
    }

    public CSTNode remove(int index) {
        if (index < 1) {
            throw new GroovyBugError("attempt to remove() root node of Reduction");
        }
        return (CSTNode)this.elements.remove(index);
    }

    @Override
    public Reduction asReduction() {
        return this;
    }
}

