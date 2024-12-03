/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent
extends AbstractXMLEvent
implements ProcessingInstruction {
    protected String target;
    protected String data;

    public ProcessingInstructionEvent(String target, String data) {
        this.target = target;
        this.data = data;
    }

    public ProcessingInstructionEvent(String target, String data, Location location) {
        super(location);
        this.target = target;
        this.data = data;
    }

    public ProcessingInstructionEvent(ProcessingInstruction that) {
        super(that);
        this.target = that.getTarget();
        this.data = that.getData();
    }

    public int getEventType() {
        return 3;
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.data;
    }
}

