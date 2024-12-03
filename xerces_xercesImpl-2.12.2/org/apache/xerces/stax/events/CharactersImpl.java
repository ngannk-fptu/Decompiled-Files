/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import org.apache.xerces.stax.events.XMLEventImpl;
import org.apache.xerces.util.XMLChar;

public final class CharactersImpl
extends XMLEventImpl
implements Characters {
    private final String fData;

    public CharactersImpl(String string, int n, Location location) {
        super(n, location);
        this.fData = string != null ? string : "";
    }

    @Override
    public String getData() {
        return this.fData;
    }

    @Override
    public boolean isWhiteSpace() {
        int n;
        int n2 = n = this.fData != null ? this.fData.length() : 0;
        if (n == 0) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (XMLChar.isSpace(this.fData.charAt(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isCData() {
        return 12 == this.getEventType();
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return 6 == this.getEventType();
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(this.fData);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }
}

