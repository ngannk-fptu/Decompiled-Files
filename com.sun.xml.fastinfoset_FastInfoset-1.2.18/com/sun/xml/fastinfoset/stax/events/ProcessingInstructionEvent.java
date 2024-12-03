/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.EventBase;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent
extends EventBase
implements ProcessingInstruction {
    private String targetName;
    private String _data;

    public ProcessingInstructionEvent() {
        this.init();
    }

    public ProcessingInstructionEvent(String targetName, String data) {
        this.targetName = targetName;
        this._data = data;
        this.init();
    }

    protected void init() {
        this.setEventType(3);
    }

    @Override
    public String getTarget() {
        return this.targetName;
    }

    public void setTarget(String targetName) {
        this.targetName = targetName;
    }

    public void setData(String data) {
        this._data = data;
    }

    @Override
    public String getData() {
        return this._data;
    }

    public String toString() {
        if (this._data != null && this.targetName != null) {
            return "<?" + this.targetName + " " + this._data + "?>";
        }
        if (this.targetName != null) {
            return "<?" + this.targetName + "?>";
        }
        if (this._data != null) {
            return "<?" + this._data + "?>";
        }
        return "<??>";
    }
}

