/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.TraceEvent;
import java.util.EventListener;

public interface ListenerBase
extends EventListener {
    public void doneParsing(TraceEvent var1);

    public void refresh();
}

