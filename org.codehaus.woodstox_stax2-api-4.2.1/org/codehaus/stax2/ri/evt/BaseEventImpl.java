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
    @Deprecated
    protected final Location mLocation;

    protected BaseEventImpl(Location loc) {
        this.mLocation = loc;
    }

    @Override
    public Characters asCharacters() {
        return (Characters)((Object)this);
    }

    @Override
    public EndElement asEndElement() {
        return (EndElement)((Object)this);
    }

    @Override
    public StartElement asStartElement() {
        return (StartElement)((Object)this);
    }

    @Override
    public abstract int getEventType();

    @Override
    public Location getLocation() {
        return this.mLocation;
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return false;
    }

    @Override
    public boolean isEndDocument() {
        return false;
    }

    @Override
    public boolean isEndElement() {
        return false;
    }

    @Override
    public boolean isEntityReference() {
        return false;
    }

    @Override
    public boolean isNamespace() {
        return false;
    }

    @Override
    public boolean isProcessingInstruction() {
        return false;
    }

    @Override
    public boolean isStartDocument() {
        return false;
    }

    @Override
    public boolean isStartElement() {
        return false;
    }

    @Override
    public abstract void writeAsEncodedUnicode(Writer var1) throws XMLStreamException;

    @Override
    public abstract void writeUsing(XMLStreamWriter2 var1) throws XMLStreamException;

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    public String toString() {
        return "[Stax Event #" + this.getEventType() + "]";
    }

    protected void throwFromIOE(IOException ioe) throws XMLStreamException {
        throw new XMLStreamException(ioe.getMessage(), ioe);
    }

    protected static boolean stringsWithNullsEqual(String s1, String s2) {
        if (s1 == null || s1.length() == 0) {
            return s2 == null || s2.length() == 0;
        }
        return s2 != null && s1.equals(s2);
    }

    protected static boolean iteratedEquals(Iterator<?> it1, Iterator<?> it2) {
        if (it1 == null || it2 == null) {
            return it1 == it2;
        }
        while (it1.hasNext()) {
            Object o2;
            if (!it2.hasNext()) {
                return false;
            }
            Object o1 = it1.next();
            if (o1.equals(o2 = it2.next())) continue;
            return false;
        }
        return true;
    }

    protected static int addHash(Iterator<?> it, int baseHash) {
        int hash = baseHash;
        if (it != null) {
            while (it.hasNext()) {
                hash ^= it.next().hashCode();
            }
        }
        return hash;
    }
}

