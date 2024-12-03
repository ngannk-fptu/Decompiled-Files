/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.TraceEvent;

public interface TraceListener
extends ListenerBase {
    public void enterRule(TraceEvent var1);

    public void exitRule(TraceEvent var1);
}

