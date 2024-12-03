/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.ParserMatchEvent;

public interface ParserMatchListener
extends ListenerBase {
    public void parserMatch(ParserMatchEvent var1);

    public void parserMatchNot(ParserMatchEvent var1);

    public void parserMismatch(ParserMatchEvent var1);

    public void parserMismatchNot(ParserMatchEvent var1);
}

