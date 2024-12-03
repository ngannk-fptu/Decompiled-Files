/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.evt.XMLEvent2;

public abstract class BaseEventImpl
implements XMLEvent2 {
    protected final Location mLocation;

    protected BaseEventImpl(Location location) {
        this.mLocation = location;
    }

    public Characters asCharacters() {
        return (Characters)((Object)this);
    }

    public EndElement asEndElement() {
        return (EndElement)((Object)this);
    }

    public StartElement asStartElement() {
        return (StartElement)((Object)this);
    }

    public abstract int getEventType();

    public Location getLocation() {
        return this.mLocation;
    }

    public QName getSchemaType() {
        return null;
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isStartElement() {
        return false;
    }

    public abstract void writeAsEncodedUnicode(Writer var1) throws XMLStreamException;

    public abstract void writeUsing(XMLStreamWriter2 var1) throws XMLStreamException;

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public String toString() {
        return "[Stax Event #" + this.getEventType() + "]";
    }

    protected void throwFromIOE(IOException iOException) throws XMLStreamException {
        throw new XMLStreamException(iOException.getMessage(), iOException);
    }

    protected static boolean stringsWithNullsEqual(String string, String string2) {
        if (string == null || string.length() == 0) {
            return string2 == null || string2.length() == 0;
        }
        return string2 != null && string.equals(string2);
    }

    protected static boolean iteratedEquals(Iterator iterator, Iterator iterator2) {
        if (iterator == null || iterator2 == null) {
            return iterator == iterator2;
        }
        while (iterator.hasNext()) {
            Object e;
            if (!iterator2.hasNext()) {
                return false;
            }
            Object e2 = iterator.next();
            if (e2.equals(e = iterator2.next())) continue;
            return false;
        }
        return true;
    }

    protected static int addHash(Iterator iterator, int n) {
        int n2 = n;
        if (iterator != null) {
            while (iterator.hasNext()) {
                n2 ^= iterator.next().hashCode();
            }
        }
        return n2;
    }
}

