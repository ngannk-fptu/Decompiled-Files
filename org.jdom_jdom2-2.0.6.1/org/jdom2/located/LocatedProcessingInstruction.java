/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.located;

import java.util.Map;
import org.jdom2.ProcessingInstruction;
import org.jdom2.located.Located;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LocatedProcessingInstruction
extends ProcessingInstruction
implements Located {
    private static final long serialVersionUID = 200L;
    private int line;
    private int col;

    public LocatedProcessingInstruction(String target) {
        super(target);
    }

    public LocatedProcessingInstruction(String target, Map<String, String> data) {
        super(target, data);
    }

    public LocatedProcessingInstruction(String target, String data) {
        super(target, data);
    }

    @Override
    public int getLine() {
        return this.line;
    }

    @Override
    public int getColumn() {
        return this.col;
    }

    @Override
    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public void setColumn(int col) {
        this.col = col;
    }
}

