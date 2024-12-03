/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.ProcessingInstruction;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class ProcInstrEventImpl
extends BaseEventImpl
implements ProcessingInstruction {
    final String mTarget;
    final String mData;

    public ProcInstrEventImpl(Location loc, String target, String data) {
        super(loc);
        this.mTarget = target;
        this.mData = data;
    }

    @Override
    public String getData() {
        return this.mData;
    }

    @Override
    public String getTarget() {
        return this.mTarget;
    }

    @Override
    public int getEventType() {
        return 3;
    }

    @Override
    public boolean isProcessingInstruction() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("<?");
            w.write(this.mTarget);
            if (this.mData != null && this.mData.length() > 0) {
                w.write(this.mData);
            }
            w.write("?>");
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        if (this.mData != null && this.mData.length() > 0) {
            w.writeProcessingInstruction(this.mTarget, this.mData);
        } else {
            w.writeProcessingInstruction(this.mTarget);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof ProcessingInstruction)) {
            return false;
        }
        ProcessingInstruction other = (ProcessingInstruction)o;
        return this.mTarget.equals(other.getTarget()) && ProcInstrEventImpl.stringsWithNullsEqual(this.mData, other.getData());
    }

    @Override
    public int hashCode() {
        int hash = this.mTarget.hashCode();
        if (this.mData != null) {
            hash ^= this.mData.hashCode();
        }
        return hash;
    }
}

