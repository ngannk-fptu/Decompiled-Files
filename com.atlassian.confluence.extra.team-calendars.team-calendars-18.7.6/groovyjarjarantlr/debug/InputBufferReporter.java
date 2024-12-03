/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.InputBufferEvent;
import groovyjarjarantlr.debug.InputBufferListener;
import groovyjarjarantlr.debug.TraceEvent;

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

