/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import org.jdom2.Text;
import org.jdom2.located.Located;

public class LocatedText
extends Text
implements Located {
    private static final long serialVersionUID = 200L;
    private int line;
    private int col;

    public LocatedText(String str) {
        super(str);
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

