/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.ParserMatchEvent;

public interface ParserMatchListener
extends ListenerBase {
    public void parserMatch(ParserMatchEvent var1);

    public void parserMatchNot(ParserMatchEvent var1);

    public void parserMismatch(ParserMatchEvent var1);

    public void parserMismatchNot(ParserMatchEvent var1);
}

