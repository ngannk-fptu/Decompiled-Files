/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.InputBufferEvent;
import antlr.debug.ListenerBase;

public interface InputBufferListener
extends ListenerBase {
    public void inputBufferConsume(InputBufferEvent var1);

    public void inputBufferLA(InputBufferEvent var1);

    public void inputBufferMark(InputBufferEvent var1);

    public void inputBufferRewind(InputBufferEvent var1);
}

