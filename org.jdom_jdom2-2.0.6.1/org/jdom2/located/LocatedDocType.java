/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import org.jdom2.DocType;
import org.jdom2.located.Located;

public class LocatedDocType
extends DocType
implements Located {
    private static final long serialVersionUID = 200L;
    private int line;
    private int col;

    public LocatedDocType(String elementName, String publicID, String systemID) {
        super(elementName, publicID, systemID);
    }

    public LocatedDocType(String elementName, String systemID) {
        super(elementName, systemID);
    }

    public LocatedDocType(String elementName) {
        super(elementName);
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

