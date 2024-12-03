/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import org.apache.xerces.stax.events.ElementImpl;

public final class EndElementImpl
extends ElementImpl
implements EndElement {
    public EndElementImpl(QName qName, Iterator iterator, Location location) {
        super(qName, false, iterator, location);
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            QName qName = this.getName();
            String string = qName.getPrefix();
            if (string != null && string.length() > 0) {
                writer.write(string);
                writer.write(58);
            }
            writer.write(qName.getLocalPart());
            writer.write(62);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

