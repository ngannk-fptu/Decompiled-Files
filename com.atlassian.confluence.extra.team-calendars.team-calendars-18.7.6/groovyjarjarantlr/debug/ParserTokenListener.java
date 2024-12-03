/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.ParserTokenEvent;

public interface ParserTokenListener
extends ListenerBase {
    public void parserConsume(ParserTokenEvent var1);

    public void parserLA(ParserTokenEvent var1);
}

