/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 */
package com.sun.xml.ws.api.server;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class SDDocumentSource {
    public abstract XMLStreamReader read(XMLInputFactory var1) throws IOException, XMLStreamException;

    public abstract XMLStreamReader read() throws IOException, XMLStreamException;

    public abstract URL getSystemId();

    public static SDDocumentSource create(Class<?> implClass, String wsdlLocation) {
        ClassLoader cl = implClass.getClassLoader();
        URL url = cl.getResource(wsdlLocation);
        if (url != null) {
            return SDDocumentSource.create(url);
        }
        return SDDocumentSource.create(wsdlLocation, implClass);
    }

    public static SDDocumentSource create(final URL url) {
        return new SDDocumentSource(){
            private final URL systemId;
            {
                this.systemId = url;
            }

            @Override
            public XMLStreamReader read(XMLInputFactory xif) throws IOException, XMLStreamException {
                InputStream is = url.openStream();
                return new TidyXMLStreamReader(xif.createXMLStreamReader(this.systemId.toExternalForm(), is), is);
            }

            @Override
            public XMLStreamReader read() throws IOException, XMLStreamException {
                InputStream is = url.openStream();
                return new TidyXMLStreamReader(XMLStreamReaderFactory.create(this.systemId.toExternalForm(), is, false), is);
            }

            @Override
            public URL getSystemId() {
                return this.systemId;
            }
        };
    }

    private static SDDocumentSource create(final String path, final Class<?> resolvingClass) {
        return new SDDocumentSource(){

            @Override
            public XMLStreamReader read(XMLInputFactory xif) throws IOException, XMLStreamException {
                InputStream is = this.inputStream();
                return new TidyXMLStreamReader(xif.createXMLStreamReader(path, is), is);
            }

            @Override
            public XMLStreamReader read() throws IOException, XMLStreamException {
                InputStream is = this.inputStream();
                return new TidyXMLStreamReader(XMLStreamReaderFactory.create(path, is, false), is);
            }

            @Override
            public URL getSystemId() {
                try {
                    return new URL("file://" + path);
                }
                catch (MalformedURLException e) {
                    return null;
                }
            }

            private InputStream inputStream() throws IOException {
                Module module = resolvingClass.getModule();
                InputStream stream = module.getResourceAsStream(path);
                if (stream != null) {
                    return stream;
                }
                throw new ServerRtException("cannot.load.wsdl", path);
            }
        };
    }

    public static SDDocumentSource create(final URL systemId, final XMLStreamBuffer xsb) {
        return new SDDocumentSource(){

            @Override
            public XMLStreamReader read(XMLInputFactory xif) throws XMLStreamException {
                return xsb.readAsXMLStreamReader();
            }

            @Override
            public XMLStreamReader read() throws XMLStreamException {
                return xsb.readAsXMLStreamReader();
            }

            @Override
            public URL getSystemId() {
                return systemId;
            }
        };
    }
}

