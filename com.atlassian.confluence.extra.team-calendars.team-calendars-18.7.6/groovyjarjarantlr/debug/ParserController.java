/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ParserEventSupport;
import groovyjarjarantlr.debug.ParserListener;

public interface ParserController
extends ParserListener {
    public void checkBreak();

    public void setParserEventSupport(ParserEventSupport var1);
}

