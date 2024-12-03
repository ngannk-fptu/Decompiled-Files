/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class DTDImpl
extends XMLEventImpl
implements DTD {
    private final String fDTD;

    public DTDImpl(String string, Location location) {
        super(11, location);
        this.fDTD = string != null ? string : null;
    }

    @Override
    public String getDocumentTypeDeclaration() {
        return this.fDTD;
    }

    @Override
    public Object getProcessedDTD() {
        return null;
    }

    public List getNotations() {
        return Collections.EMPTY_LIST;
    }

    public List getEntities() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(this.fDTD);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

