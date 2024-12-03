/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.TraceEvent;

public interface TraceListener
extends ListenerBase {
    public void enterRule(TraceEvent var1);

    public void exitRule(TraceEvent var1);
}

