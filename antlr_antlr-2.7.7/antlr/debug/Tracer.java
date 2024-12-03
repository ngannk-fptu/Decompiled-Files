/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug;

import antlr.debug.TraceAdapter;
import antlr.debug.TraceEvent;
import antlr.debug.TraceListener;

public class Tracer
extends TraceAdapter
implements TraceListener {
    String indent = "";

    protected void dedent() {
        this.indent = this.indent.length() < 2 ? "" : this.indent.substring(2);
    }

    public void enterRule(TraceEvent traceEvent) {
        System.out.println(this.indent + traceEvent);
        this.indent();
    }

    public void exitRule(TraceEvent traceEvent) {
        this.dedent();
        System.out.println(this.indent + traceEvent);
    }

    protected void indent() {
        this.indent = this.indent + "  ";
    }
}

