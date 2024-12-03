/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Comment;
import org.apache.xerces.stax.events.XMLEventImpl;

public final class CommentImpl
extends XMLEventImpl
implements Comment {
    private final String fText;

    public CommentImpl(String string, Location location) {
        super(5, location);
        this.fText = string != null ? string : "";
    }

    @Override
    public String getText() {
        return this.fText;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("<!--");
            writer.write(this.fText);
            writer.write("-->");
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

