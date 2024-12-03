/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ParserEventSupport;
import antlr.debug.ParserListener;

public interface ParserController
extends ParserListener {
    public void checkBreak();

    public void setParserEventSupport(ParserEventSupport var1);
}

