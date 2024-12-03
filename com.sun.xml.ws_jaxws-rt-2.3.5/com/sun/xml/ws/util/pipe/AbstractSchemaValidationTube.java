/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.api.server.DocumentAddressResolver;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.developer.SchemaValidationFeature;
import com.sun.xml.ws.developer.ValidationErrorHandler;
import com.sun.xml.ws.server.SDDocumentImpl;
import com.sun.xml.ws.util.ByteArrayBuffer;
import com.sun.xml.ws.util.xml.XmlUtil;
import com.sun.xml.ws.wsdl.SDDocumentResolver;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractSchemaValidationTube
extends AbstractFilterTubeImpl {
    private static final Logger LOGGER = Logger.getLogger(AbstractSchemaValidationTube.class.getName());
    protected final WSBinding binding;
    protected final SchemaValidationFeature feature;
    protected final DocumentAddressResolver resolver = new ValidationDocumentAddressResolver();
    protected final SchemaFactory sf;

    public AbstractSchemaValidationTube(WSBinding binding, Tube next) {
        super(next);
        this.binding = binding;
        this.feature = binding.getFeature(SchemaValidationFeature.class);
        this.sf = XmlUtil.allowExternalAccess(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"), "all", false);
    }

    protected AbstractSchemaValidationTube(AbstractSchemaValidationTube that, TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.feature = that.feature;
        this.sf = that.sf;
    }

    protected abstract Validator getValidator();

    protected abstract boolean isNoValidation();

    private Document createDOM(SDDocument doc) {
        ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            doc.writeTo(null, this.resolver, bab);
        }
        catch (IOException ioe) {
            throw new WebServiceException((Throwable)ioe);
        }
        Transformer trans = XmlUtil.newTransformer();
        StreamSource source = new StreamSource(bab.newInputStream(), null);
        DOMResult result = new DOMResult();
        try {
            trans.transform(source, result);
        }
        catch (TransformerException te) {
            throw new WebServiceException((Throwable)te);
        }
        return (Document)result.getNode();
    }

    private void updateMultiSchemaForTns(String tns, String systemId, Map<String, List<String>> schemas) {
        List<String> docIdList = schemas.get(tns);
        if (docIdList == null) {
            docIdList = new ArrayList<String>();
            schemas.put(tns, docIdList);
        }
        docIdList.add(systemId);
    }

    protected Source[] getSchemaSources(Iterable<SDDocument> docs, MetadataResolverImpl mdresolver) {
        HashMap<String, DOMSource> inlinedSchemas = new HashMap<String, DOMSource>();
        HashMap<String, List<String>> multiSchemaForTns = new HashMap<String, List<String>>();
        for (SDDocument sdoc : docs) {
            if (sdoc.isWSDL()) {
                Document dom = this.createDOM(sdoc);
                this.addSchemaFragmentSource(dom, sdoc.getURL().toExternalForm(), inlinedSchemas);
                continue;
            }
            if (!sdoc.isSchema()) continue;
            this.updateMultiSchemaForTns(((SDDocument.Schema)sdoc).getTargetNamespace(), sdoc.getURL().toExternalForm(), multiSchemaForTns);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "WSDL inlined schema fragment documents(these are used to create a pseudo schema) = {0}", inlinedSchemas.keySet());
        }
        for (DOMSource src : inlinedSchemas.values()) {
            String tns = this.getTargetNamespace(src);
            this.updateMultiSchemaForTns(tns, src.getSystemId(), multiSchemaForTns);
        }
        if (multiSchemaForTns.isEmpty()) {
            return new Source[0];
        }
        if (multiSchemaForTns.size() == 1 && ((List)multiSchemaForTns.values().iterator().next()).size() == 1) {
            String systemId = (String)((List)multiSchemaForTns.values().iterator().next()).get(0);
            return new Source[]{(Source)inlinedSchemas.get(systemId)};
        }
        mdresolver.addSchemas(inlinedSchemas.values());
        HashMap<String, String> oneSchemaForTns = new HashMap<String, String>();
        int i = 0;
        for (Map.Entry entry : multiSchemaForTns.entrySet()) {
            String systemId;
            List sameTnsSchemas = (List)entry.getValue();
            if (sameTnsSchemas.size() > 1) {
                systemId = "file:x-jax-ws-include-" + i++;
                Source src = this.createSameTnsPseudoSchema((String)entry.getKey(), sameTnsSchemas, systemId);
                mdresolver.addSchema(src);
            } else {
                systemId = (String)sameTnsSchemas.get(0);
            }
            oneSchemaForTns.put((String)entry.getKey(), systemId);
        }
        Source pseudoSchema = this.createMasterPseudoSchema(oneSchemaForTns);
        return new Source[]{pseudoSchema};
    }

    @Nullable
    private void addSchemaFragmentSource(Document doc, String systemId, Map<String, DOMSource> map) {
        Element e = doc.getDocumentElement();
        assert (e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/"));
        assert (e.getLocalName().equals("definitions"));
        NodeList typesList = e.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "types");
        for (int i = 0; i < typesList.getLength(); ++i) {
            NodeList schemaList = ((Element)typesList.item(i)).getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");
            for (int j = 0; j < schemaList.getLength(); ++j) {
                Element elem = (Element)schemaList.item(j);
                NamespaceSupport nss = new NamespaceSupport();
                this.buildNamespaceSupport(nss, elem);
                this.patchDOMFragment(nss, elem);
                String docId = systemId + "#schema" + j;
                map.put(docId, new DOMSource(elem, docId));
            }
        }
    }

    private void buildNamespaceSupport(NamespaceSupport nss, Node node) {
        if (node == null || node.getNodeType() != 1) {
            return;
        }
        this.buildNamespaceSupport(nss, node.getParentNode());
        nss.pushContext();
        NamedNodeMap atts = node.getAttributes();
        for (int i = 0; i < atts.getLength(); ++i) {
            Attr a = (Attr)atts.item(i);
            if ("xmlns".equals(a.getPrefix())) {
                nss.declarePrefix(a.getLocalName(), a.getValue());
                continue;
            }
            if (!"xmlns".equals(a.getName())) continue;
            nss.declarePrefix("", a.getValue());
        }
    }

    @Nullable
    private void patchDOMFragment(NamespaceSupport nss, Element elem) {
        NamedNodeMap atts = elem.getAttributes();
        Enumeration<String> en = nss.getPrefixes();
        while (en.hasMoreElements()) {
            String prefix = en.nextElement();
            for (int i = 0; i < atts.getLength(); ++i) {
                Attr a = (Attr)atts.item(i);
                if ("xmlns".equals(a.getPrefix()) && a.getLocalName().equals(prefix)) continue;
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Patching with xmlns:{0}={1}", new Object[]{prefix, nss.getURI(prefix)});
                }
                elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, nss.getURI(prefix));
            }
        }
    }

    @Nullable
    private Source createSameTnsPseudoSchema(String tns, Collection<String> docs, String pseudoSystemId) {
        assert (docs.size() > 1);
        final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'");
        if (tns != null && !"".equals(tns) && !"null".equals(tns)) {
            sb.append(" targetNamespace='").append(tns).append("'");
        }
        sb.append(">\n");
        for (String systemId : docs) {
            sb.append("<xsd:include schemaLocation='").append(systemId).append("'/>\n");
        }
        sb.append("</xsd:schema>\n");
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Pseudo Schema for the same tns={0}is {1}", new Object[]{tns, sb});
        }
        return new StreamSource(pseudoSystemId){

            @Override
            public Reader getReader() {
                return new StringReader(sb.toString());
            }
        };
    }

    private Source createMasterPseudoSchema(Map<String, String> docs) {
        final StringBuilder sb = new StringBuilder("<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:x-jax-ws-master'>\n");
        for (Map.Entry<String, String> e : docs.entrySet()) {
            String systemId = e.getValue();
            String ns = e.getKey();
            sb.append("<xsd:import schemaLocation='").append(systemId).append("'");
            if (ns != null && !"".equals(ns)) {
                sb.append(" namespace='").append(ns).append("'");
            }
            sb.append("/>\n");
        }
        sb.append("</xsd:schema>");
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Master Pseudo Schema = {0}", sb);
        }
        return new StreamSource("file:x-jax-ws-master-doc"){

            @Override
            public Reader getReader() {
                return new StringReader(sb.toString());
            }
        };
    }

    protected void doProcess(Packet packet) throws SAXException {
        ValidationErrorHandler handler;
        this.getValidator().reset();
        Class<? extends ValidationErrorHandler> handlerClass = this.feature.getErrorHandler();
        try {
            handler = handlerClass.newInstance();
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        handler.setPacket(packet);
        this.getValidator().setErrorHandler(handler);
        Message msg = packet.getMessage().copy();
        Source source = msg.readPayloadAsSource();
        try {
            this.getValidator().validate(source);
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private String getTargetNamespace(DOMSource src) {
        Element elem = (Element)src.getNode();
        return elem.getAttribute("targetNamespace");
    }

    protected class MetadataResolverImpl
    implements SDDocumentResolver,
    LSResourceResolver {
        final Map<String, SDDocument> docs = new HashMap<String, SDDocument>();
        final Map<String, SDDocument> nsMapping = new HashMap<String, SDDocument>();

        public MetadataResolverImpl() {
        }

        public MetadataResolverImpl(Iterable<SDDocument> it) {
            for (SDDocument doc : it) {
                if (!doc.isSchema()) continue;
                this.docs.put(doc.getURL().toExternalForm(), doc);
                this.nsMapping.put(((SDDocument.Schema)doc).getTargetNamespace(), doc);
            }
        }

        void addSchema(Source schema) {
            assert (schema.getSystemId() != null);
            String systemId = schema.getSystemId();
            try {
                XMLStreamBufferResult xsbr = XmlUtil.identityTransform(schema, new XMLStreamBufferResult());
                SDDocumentSource sds = SDDocumentSource.create(new URL(systemId), (XMLStreamBuffer)xsbr.getXMLStreamBuffer());
                SDDocumentImpl sdoc = SDDocumentImpl.create(sds, new QName(""), new QName(""));
                this.docs.put(systemId, sdoc);
                this.nsMapping.put(((SDDocument.Schema)((Object)sdoc)).getTargetNamespace(), sdoc);
            }
            catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Exception in adding schemas to resolver", ex);
            }
        }

        void addSchemas(Collection<? extends Source> schemas) {
            for (Source source : schemas) {
                this.addSchema(source);
            }
        }

        @Override
        public SDDocument resolve(String systemId) {
            SDDocument sdi = this.docs.get(systemId);
            if (sdi == null) {
                SDDocumentSource sds;
                try {
                    sds = SDDocumentSource.create(new URL(systemId));
                }
                catch (MalformedURLException e) {
                    throw new WebServiceException((Throwable)e);
                }
                sdi = SDDocumentImpl.create(sds, new QName(""), new QName(""));
                this.docs.put(systemId, sdi);
            }
            return sdi;
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "type={0} namespaceURI={1} publicId={2} systemId={3} baseURI={4}", new Object[]{type, namespaceURI, publicId, systemId, baseURI});
            }
            try {
                SDDocument doc;
                if (systemId == null) {
                    doc = this.nsMapping.get(namespaceURI);
                } else {
                    URI rel = baseURI != null ? new URI(baseURI).resolve(systemId) : new URI(systemId);
                    doc = this.docs.get(rel.toString());
                }
                if (doc != null) {
                    return new LSInput(){

                        @Override
                        public Reader getCharacterStream() {
                            return null;
                        }

                        @Override
                        public void setCharacterStream(Reader characterStream) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public InputStream getByteStream() {
                            ByteArrayBuffer bab = new ByteArrayBuffer();
                            try {
                                doc.writeTo(null, AbstractSchemaValidationTube.this.resolver, bab);
                            }
                            catch (IOException ioe) {
                                throw new WebServiceException((Throwable)ioe);
                            }
                            return bab.newInputStream();
                        }

                        @Override
                        public void setByteStream(InputStream byteStream) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getStringData() {
                            return null;
                        }

                        @Override
                        public void setStringData(String stringData) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getSystemId() {
                            return doc.getURL().toExternalForm();
                        }

                        @Override
                        public void setSystemId(String systemId) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getPublicId() {
                            return null;
                        }

                        @Override
                        public void setPublicId(String publicId) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getBaseURI() {
                            return doc.getURL().toExternalForm();
                        }

                        @Override
                        public void setBaseURI(String baseURI) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getEncoding() {
                            return null;
                        }

                        @Override
                        public void setEncoding(String encoding) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean getCertifiedText() {
                            return false;
                        }

                        @Override
                        public void setCertifiedText(boolean certifiedText) {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            }
            catch (Exception e) {
                LOGGER.log(Level.WARNING, "Exception in LSResourceResolver impl", e);
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Don''t know about systemId={0} baseURI={1}", new Object[]{systemId, baseURI});
            }
            return null;
        }
    }

    private static class ValidationDocumentAddressResolver
    implements DocumentAddressResolver {
        private ValidationDocumentAddressResolver() {
        }

        @Override
        @Nullable
        public String getRelativeAddressFor(@NotNull SDDocument current, @NotNull SDDocument referenced) {
            LOGGER.log(Level.FINE, "Current = {0} resolved relative={1}", new Object[]{current.getURL(), referenced.getURL()});
            return referenced.getURL().toExternalForm();
        }
    }
}

