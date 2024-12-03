/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.InputBufferEvent;
import antlr.debug.InputBufferListener;
import antlr.debug.TraceEvent;

public class InputBufferReporter
implements InputBufferListener {
    public void doneParsing(TraceEvent traceEvent) {
    }

    public void inputBufferChanged(InputBufferEvent inputBufferEvent) {
        System.out.println(inputBufferEvent);
    }

    public void inputBufferConsume(InputBufferEvent inputBufferEvent) {
        System.out.println(inputBufferEvent);
    }

    public void inputBufferLA(InputBufferEvent inputBufferEvent) {
        System.out.println(inputBufferEvent);
    }

    public void inputBufferMark(InputBufferEvent inputBufferEvent) {
        System.out.println(inputBufferEvent);
    }

    public void inputBufferRewind(InputBufferEvent inputBufferEvent) {
        System.out.println(inputBufferEvent);
    }

    public void refresh() {
    }
}

