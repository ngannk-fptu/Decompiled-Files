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

    public ProcInstrEventImpl(Location location, String string, String string2) {
        super(location);
        this.mTarget = string;
        this.mData = string2;
    }

    public String getData() {
        return this.mData;
    }

    public String getTarget() {
        return this.mTarget;
    }

    public int getEventType() {
        return 3;
    }

    public boolean isProcessingInstruction() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(this.mTarget);
            if (this.mData != null && this.mData.length() > 0) {
                writer.write(this.mData);
            }
            writer.write("?>");
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        if (this.mData != null && this.mData.length() > 0) {
            xMLStreamWriter2.writeProcessingInstruction(this.mTarget, this.mData);
        } else {
            xMLStreamWriter2.writeProcessingInstruction(this.mTarget);
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof ProcessingInstruction)) {
            return false;
        }
        ProcessingInstruction processingInstruction = (ProcessingInstruction)object;
        return this.mTarget.equals(processingInstruction.getTarget()) && ProcInstrEventImpl.stringsWithNullsEqual(this.mData, processingInstruction.getData());
    }

    public int hashCode() {
        int n = this.mTarget.hashCode();
        if (this.mData != null) {
            n ^= this.mData.hashCode();
        }
        return n;
    }
}

