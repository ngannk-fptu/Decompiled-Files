/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.located.Located;

public class LocatedElement
extends Element
implements Located {
    private static final long serialVersionUID = 200L;
    private int line;
    private int col;

    public LocatedElement(String name, Namespace namespace) {
        super(name, namespace);
    }

    public LocatedElement(String name) {
        super(name);
    }

    public LocatedElement(String name, String uri) {
        super(name, uri);
    }

    public LocatedElement(String name, String prefix, String uri) {
        super(name, prefix, uri);
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.col;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int col) {
        this.col = col;
    }
}

