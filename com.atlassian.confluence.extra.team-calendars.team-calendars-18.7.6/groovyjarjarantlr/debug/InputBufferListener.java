/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.InputBufferEvent;
import groovyjarjarantlr.debug.ListenerBase;

public interface InputBufferListener
extends ListenerBase {
    public void inputBufferConsume(InputBufferEvent var1);

    public void inputBufferLA(InputBufferEvent var1);

    public void inputBufferMark(InputBufferEvent var1);

    public void inputBufferRewind(InputBufferEvent var1);
}

