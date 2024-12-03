/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package org.apache.axiom.om.ds;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSource;
import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;

public class WrappedTextNodeOMDataSourceFromDataSource
extends WrappedTextNodeOMDataSource {
    private final DataSource binaryData;
    private final Charset charset;

    public WrappedTextNodeOMDataSourceFromDataSource(QName wrapperElementName, DataSource binaryData, Charset charset) {
        super(wrapperElementName);
        this.binaryData = binaryData;
        this.charset = charset;
    }

    public XMLStreamReader getReader() throws XMLStreamException {
        InputStream is;
        try {
            is = this.binaryData.getInputStream();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
        return new WrappedTextNodeStreamReader(this.wrapperElementName, new InputStreamReader(is, this.charset));
    }

    public Object getObject() {
        return this.binaryData;
    }

    public boolean isDestructiveRead() {
        return false;
    }

    public OMDataSourceExt copy() {
        return new WrappedTextNodeOMDataSourceFromDataSource(this.wrapperElementName, this.binaryData, this.charset);
    }
}

