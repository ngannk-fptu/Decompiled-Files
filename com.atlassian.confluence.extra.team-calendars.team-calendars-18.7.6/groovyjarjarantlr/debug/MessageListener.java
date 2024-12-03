/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.MessageEvent;

public interface MessageListener
extends ListenerBase {
    public void reportError(MessageEvent var1);

    public void reportWarning(MessageEvent var1);
}

