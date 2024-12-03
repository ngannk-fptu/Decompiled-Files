/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FastInfosetSource
extends SAXSource {
    public FastInfosetSource(InputStream inputStream) {
        super(new InputSource(inputStream));
    }

    @Override
    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAXDocumentParser();
            this.setXMLReader(reader);
        }
        ((SAXDocumentParser)reader).setInputStream(this.getInputStream());
        return reader;
    }

    public InputStream getInputStream() {
        return this.getInputSource().getByteStream();
    }

    public void setInputStream(InputStream inputStream) {
        this.setInputSource(new InputSource(inputStream));
    }
}

