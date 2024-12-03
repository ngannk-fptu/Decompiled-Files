/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.TraceEvent;
import java.util.EventListener;

public interface ListenerBase
extends EventListener {
    public void doneParsing(TraceEvent var1);

    public void refresh();
}

