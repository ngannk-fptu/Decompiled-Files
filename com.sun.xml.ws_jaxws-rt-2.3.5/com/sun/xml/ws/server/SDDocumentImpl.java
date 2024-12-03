/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 */
package com.sun.xml.ws.server;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.PortAddressResolver;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentFilter;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.util.RuntimeVersion;
import com.sun.xml.ws.wsdl.SDDocumentResolver;
import com.sun.xml.ws.wsdl.parser.ParserUtil;
import com.sun.xml.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.ws.wsdl.writer.WSDLPatcher;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.WebServiceException;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

public class SDDocumentImpl
extends SDDocumentSource
implements SDDocument {
    private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
    private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
    private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
    private static final String VERSION_COMMENT = " Published by JAX-WS RI (https://github.com/eclipse-ee4j/metro-jax-ws). RI's version is " + RuntimeVersion.VERSION + ". ";
    private final QName rootName;
    private final SDDocumentSource source;
    @Nullable
    List<SDDocumentFilter> filters;
    @Nullable
    SDDocumentResolver sddocResolver;
    private final URL url;
    private final Set<String> imports;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static SDDocumentImpl create(SDDocumentSource src, QName serviceName, QName portTypeName) {
        URL systemId = src.getSystemId();
        try {
            QName rootName;
            XMLStreamReader reader;
            block18: {
                block17: {
                    reader = src.read();
                    XMLStreamReaderUtil.nextElementContent(reader);
                    rootName = reader.getName();
                    if (rootName.equals(WSDLConstants.QNAME_SCHEMA)) break block17;
                    if (!rootName.equals(WSDLConstants.QNAME_DEFINITIONS)) {
                        SDDocumentImpl sDDocumentImpl = new SDDocumentImpl(rootName, systemId, src);
                        return sDDocumentImpl;
                    }
                    break block18;
                    finally {
                        reader.close();
                    }
                }
                String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
                HashSet<String> importedDocs = new HashSet<String>();
                while (true) {
                    String importedDoc;
                    Object name;
                    if (XMLStreamReaderUtil.nextContent(reader) == 8) {
                        name = new SchemaImpl(rootName, systemId, src, tns, importedDocs);
                        return name;
                    }
                    if (reader.getEventType() != 1 || !SCHEMA_INCLUDE_QNAME.equals(name = reader.getName()) && !SCHEMA_IMPORT_QNAME.equals(name) && !SCHEMA_REDEFINE_QNAME.equals(name) || (importedDoc = reader.getAttributeValue(null, "schemaLocation")) == null) continue;
                    importedDocs.add(new URL(src.getSystemId(), importedDoc).toString());
                }
            }
            String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
            boolean hasPortType = false;
            boolean hasService = false;
            HashSet<String> importedDocs = new HashSet<String>();
            HashSet<QName> allServices = new HashSet<QName>();
            while (true) {
                String importedDoc;
                if (XMLStreamReaderUtil.nextContent(reader) == 8) {
                    WSDLImpl wSDLImpl = new WSDLImpl(rootName, systemId, src, tns, hasPortType, hasService, importedDocs, allServices);
                    return wSDLImpl;
                }
                if (reader.getEventType() != 1) continue;
                QName name = reader.getName();
                if (WSDLConstants.QNAME_PORT_TYPE.equals(name)) {
                    String pn = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                    if (portTypeName == null || !portTypeName.getLocalPart().equals(pn) || !portTypeName.getNamespaceURI().equals(tns)) continue;
                    hasPortType = true;
                    continue;
                }
                if (WSDLConstants.QNAME_SERVICE.equals(name)) {
                    String sn = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                    QName sqn = new QName(tns, sn);
                    allServices.add(sqn);
                    if (!serviceName.equals(sqn)) continue;
                    hasService = true;
                    continue;
                }
                if (WSDLConstants.QNAME_IMPORT.equals(name)) {
                    importedDoc = reader.getAttributeValue(null, "location");
                    if (importedDoc == null) continue;
                    importedDocs.add(new URL(src.getSystemId(), importedDoc).toString());
                    continue;
                }
                if (!SCHEMA_INCLUDE_QNAME.equals(name) && !SCHEMA_IMPORT_QNAME.equals(name) && !SCHEMA_REDEFINE_QNAME.equals(name) || (importedDoc = reader.getAttributeValue(null, "schemaLocation")) == null) continue;
                importedDocs.add(new URL(src.getSystemId(), importedDoc).toString());
            }
        }
        catch (WebServiceException e) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[]{systemId, e});
        }
        catch (IOException e) {
            throw new ServerRtException("runtime.parser.wsdl", systemId, e);
        }
        catch (XMLStreamException e) {
            throw new ServerRtException("runtime.parser.wsdl", systemId, e);
        }
    }

    protected SDDocumentImpl(QName rootName, URL url, SDDocumentSource source) {
        this(rootName, url, source, new HashSet<String>());
    }

    protected SDDocumentImpl(QName rootName, URL url, SDDocumentSource source, Set<String> imports) {
        if (url == null) {
            throw new IllegalArgumentException("Cannot construct SDDocument with null URL.");
        }
        this.rootName = rootName;
        this.source = source;
        this.url = url;
        this.imports = imports;
    }

    void setFilters(List<SDDocumentFilter> filters) {
        this.filters = filters;
    }

    void setResolver(SDDocumentResolver sddocResolver) {
        this.sddocResolver = sddocResolver;
    }

    @Override
    public QName getRootName() {
        return this.rootName;
    }

    @Override
    public boolean isWSDL() {
        return false;
    }

    @Override
    public boolean isSchema() {
        return false;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public XMLStreamReader read(XMLInputFactory xif) throws IOException, XMLStreamException {
        return this.source.read(xif);
    }

    @Override
    public XMLStreamReader read() throws IOException, XMLStreamException {
        return this.source.read();
    }

    @Override
    public URL getSystemId() {
        return this.url;
    }

    @Override
    public Set<String> getImports() {
        return this.imports;
    }

    public void writeTo(OutputStream os) throws IOException {
        XMLStreamWriter w = null;
        try {
            w = XMLStreamWriterFactory.create(os, "UTF-8");
            w.writeStartDocument("UTF-8", "1.0");
            new XMLStreamReaderToXMLStreamWriter().bridge(this.source.read(), w);
            w.writeEndDocument();
        }
        catch (XMLStreamException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        finally {
            try {
                if (w != null) {
                    w.close();
                }
            }
            catch (XMLStreamException e) {
                IOException ioe = new IOException(e.getMessage());
                ioe.initCause(e);
                throw ioe;
            }
        }
    }

    @Override
    public void writeTo(PortAddressResolver portAddressResolver, DocumentAddressResolver resolver, OutputStream os) throws IOException {
        XMLStreamWriter w = null;
        try {
            w = XMLStreamWriterFactory.create(os, "UTF-8");
            w.writeStartDocument("UTF-8", "1.0");
            this.writeTo(portAddressResolver, resolver, w);
            w.writeEndDocument();
        }
        catch (XMLStreamException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        finally {
            try {
                if (w != null) {
                    w.close();
                }
            }
            catch (XMLStreamException e) {
                IOException ioe = new IOException(e.getMessage());
                ioe.initCause(e);
                throw ioe;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(PortAddressResolver portAddressResolver, DocumentAddressResolver resolver, XMLStreamWriter out) throws XMLStreamException, IOException {
        if (this.filters != null) {
            for (SDDocumentFilter f : this.filters) {
                out = f.filter(this, out);
            }
        }
        try (XMLStreamReader xsr = this.source.read();){
            out.writeComment(VERSION_COMMENT);
            new WSDLPatcher(portAddressResolver, new DocumentLocationResolverImpl(resolver)).bridge(xsr, out);
        }
    }

    private class DocumentLocationResolverImpl
    implements DocumentLocationResolver {
        private DocumentAddressResolver delegate;

        DocumentLocationResolverImpl(DocumentAddressResolver delegate) {
            this.delegate = delegate;
        }

        @Override
        public String getLocationFor(String namespaceURI, String systemId) {
            if (SDDocumentImpl.this.sddocResolver == null) {
                return systemId;
            }
            try {
                URL ref = new URL(SDDocumentImpl.this.getURL(), systemId);
                SDDocument refDoc = SDDocumentImpl.this.sddocResolver.resolve(ref.toExternalForm());
                if (refDoc == null) {
                    return systemId;
                }
                return this.delegate.getRelativeAddressFor(SDDocumentImpl.this, refDoc);
            }
            catch (MalformedURLException mue) {
                return null;
            }
        }
    }

    private static final class WSDLImpl
    extends SDDocumentImpl
    implements SDDocument.WSDL {
        private final String targetNamespace;
        private final boolean hasPortType;
        private final boolean hasService;
        private final Set<QName> allServices;

        public WSDLImpl(QName rootName, URL url, SDDocumentSource source, String targetNamespace, boolean hasPortType, boolean hasService, Set<String> imports, Set<QName> allServices) {
            super(rootName, url, source, imports);
            this.targetNamespace = targetNamespace;
            this.hasPortType = hasPortType;
            this.hasService = hasService;
            this.allServices = allServices;
        }

        @Override
        public String getTargetNamespace() {
            return this.targetNamespace;
        }

        @Override
        public boolean hasPortType() {
            return this.hasPortType;
        }

        @Override
        public boolean hasService() {
            return this.hasService;
        }

        @Override
        public Set<QName> getAllServices() {
            return this.allServices;
        }

        @Override
        public boolean isWSDL() {
            return true;
        }
    }

    private static final class SchemaImpl
    extends SDDocumentImpl
    implements SDDocument.Schema {
        private final String targetNamespace;

        public SchemaImpl(QName rootName, URL url, SDDocumentSource source, String targetNamespace, Set<String> imports) {
            super(rootName, url, source, imports);
            this.targetNamespace = targetNamespace;
        }

        @Override
        public String getTargetNamespace() {
            return this.targetNamespace;
        }

        @Override
        public boolean isSchema() {
            return true;
        }
    }
}

