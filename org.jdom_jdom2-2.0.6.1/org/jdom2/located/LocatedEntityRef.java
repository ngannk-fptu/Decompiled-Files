/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import org.jdom2.EntityRef;
import org.jdom2.located.Located;

public class LocatedEntityRef
extends EntityRef
implements Located {
    private static final long serialVersionUID = 200L;
    private int line;
    private int col;

    public LocatedEntityRef(String name) {
        super(name);
    }

    public LocatedEntityRef(String name, String systemID) {
        super(name, systemID);
    }

    public LocatedEntityRef(String name, String publicID, String systemID) {
        super(name, publicID, systemID);
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

