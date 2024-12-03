/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.ds;

import java.io.IOException;
import java.io.Reader;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSource;
import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;

public class WrappedTextNodeOMDataSourceFromReader
extends WrappedTextNodeOMDataSource {
    private final Reader reader;
    private boolean isAccessed;

    public WrappedTextNodeOMDataSourceFromReader(QName wrapperElementName, Reader reader) {
        super(wrapperElementName);
        this.reader = reader;
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        this.isAccessed = true;
        return new WrappedTextNodeStreamReader(this.wrapperElementName, this.reader);
    }

    public Object getObject() {
        return this.isAccessed ? null : this.reader;
    }

    public boolean isDestructiveRead() {
        return true;
    }

    public void close() {
        try {
            this.reader.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

