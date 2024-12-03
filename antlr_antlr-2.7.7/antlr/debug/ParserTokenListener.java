/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.ParserTokenEvent;

public interface ParserTokenListener
extends ListenerBase {
    public void parserConsume(ParserTokenEvent var1);

    public void parserLA(ParserTokenEvent var1);
}

