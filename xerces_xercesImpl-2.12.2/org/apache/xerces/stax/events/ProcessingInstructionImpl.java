/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.ProcessingInstruction;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class ProcessingInstructionImpl
extends XMLEventImpl
implements ProcessingInstruction {
    private final String fTarget;
    private final String fData;

    public ProcessingInstructionImpl(String string, String string2, Location location) {
        super(3, location);
        this.fTarget = string != null ? string : "";
        this.fData = string2;
    }

    @Override
    public String getTarget() {
        return this.fTarget;
    }

    @Override
    public String getData() {
        return this.fData;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<?");
            writer.write(this.fTarget);
            if (this.fData != null && this.fData.length() > 0) {
                writer.write(32);
                writer.write(this.fData);
            }
            writer.write("?>");
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

