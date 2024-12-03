/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.stax.ext.stax.XMLSecCharacters;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public abstract class XMLSecEventBaseImpl
implements XMLSecEvent {
    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
    protected XMLSecStartElement parentXMLSecStartELement;

    protected static <T> EmptyIterator<T> getEmptyIterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public void setParentXMLSecStartElement(XMLSecStartElement xmlSecStartElement) {
        this.parentXMLSecStartELement = xmlSecStartElement;
    }

    @Override
    public XMLSecStartElement getParentXMLSecStartElement() {
        return this.parentXMLSecStartELement;
    }

    @Override
    public int getDocumentLevel() {
        if (this.parentXMLSecStartELement != null) {
            return this.parentXMLSecStartELement.getDocumentLevel();
        }
        return 0;
    }

    @Override
    public void getElementPath(List<QName> list) {
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getElementPath(list);
        }
    }

    @Override
    public List<QName> getElementPath() {
        ArrayList<QName> elementPath = new ArrayList<QName>();
        this.getElementPath(elementPath);
        return elementPath;
    }

    @Override
    public XMLSecStartElement getStartElementAtLevel(int level) {
        if (this.getDocumentLevel() < level) {
            return null;
        }
        return this.parentXMLSecStartELement.getStartElementAtLevel(level);
    }

    @Override
    public Location getLocation() {
        return new LocationImpl();
    }

    @Override
    public boolean isStartElement() {
        return false;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isNamespace() {
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
    public boolean isProcessingInstruction() {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return false;
    }

    @Override
    public boolean isStartDocument() {
        return false;
    }

    @Override
    public boolean isEndDocument() {
        return false;
    }

    @Override
    public XMLSecStartElement asStartElement() {
        throw new ClassCastException();
    }

    @Override
    public XMLSecEndElement asEndElement() {
        throw new ClassCastException();
    }

    @Override
    public XMLSecCharacters asCharacters() {
        throw new ClassCastException();
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        throw new UnsupportedOperationException("writeAsEncodedUnicode not implemented for " + this.getClass().getName());
    }

    private static final class EmptyIterator<E>
    implements Iterator<E> {
        private EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }

    static final class LocationImpl
    implements Location {
        LocationImpl() {
        }

        @Override
        public int getLineNumber() {
            return 0;
        }

        @Override
        public int getColumnNumber() {
            return 0;
        }

        @Override
        public int getCharacterOffset() {
            return 0;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getSystemId() {
            return null;
        }
    }
}

