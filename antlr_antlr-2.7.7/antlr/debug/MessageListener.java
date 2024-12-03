/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.ListenerBase;
import antlr.debug.MessageEvent;

public interface MessageListener
extends ListenerBase {
    public void reportError(MessageEvent var1);

    public void reportWarning(MessageEvent var1);
}

