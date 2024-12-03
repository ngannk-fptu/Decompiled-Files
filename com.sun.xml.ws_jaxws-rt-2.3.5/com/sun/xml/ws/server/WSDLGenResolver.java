/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.stream.buffer.MutableXMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 *  com.sun.xml.stream.buffer.XMLStreamBufferResult
 *  javax.xml.ws.Holder
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server;

import com.oracle.webservices.api.databinding.WSDLResolver;
import com.sun.istack.NotNull;
import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.ws.api.server.SDDocument;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.server.SDDocumentImpl;
import com.sun.xml.ws.server.ServerRtException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

final class WSDLGenResolver
implements WSDLResolver {
    private final Collection<SDDocumentImpl> docs;
    private final List<SDDocumentSource> newDocs = new ArrayList<SDDocumentSource>();
    private SDDocumentSource concreteWsdlSource;
    private SDDocumentImpl abstractWsdl;
    private SDDocumentImpl concreteWsdl;
    private final Map<String, List<SDDocumentImpl>> nsMapping = new HashMap<String, List<SDDocumentImpl>>();
    private final QName serviceName;
    private final QName portTypeName;

    public WSDLGenResolver(@NotNull Collection<SDDocumentImpl> docs, QName serviceName, QName portTypeName) {
        this.docs = docs;
        this.serviceName = serviceName;
        this.portTypeName = portTypeName;
        for (SDDocumentImpl doc : docs) {
            SDDocument.WSDL wsdl;
            if (doc.isWSDL() && (wsdl = (SDDocument.WSDL)((Object)doc)).hasPortType()) {
                this.abstractWsdl = doc;
            }
            if (!doc.isSchema()) continue;
            SDDocument.Schema schema = (SDDocument.Schema)((Object)doc);
            List<SDDocumentImpl> sysIds = this.nsMapping.get(schema.getTargetNamespace());
            if (sysIds == null) {
                sysIds = new ArrayList<SDDocumentImpl>();
                this.nsMapping.put(schema.getTargetNamespace(), sysIds);
            }
            sysIds.add(doc);
        }
    }

    @Override
    public Result getWSDL(String filename) {
        URL url = this.createURL(filename);
        MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        xsb.setSystemId(url.toExternalForm());
        this.concreteWsdlSource = SDDocumentSource.create(url, (XMLStreamBuffer)xsb);
        this.newDocs.add(this.concreteWsdlSource);
        XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
        r.setSystemId(filename);
        return r;
    }

    private URL createURL(String filename) {
        try {
            return new URL("file:///" + filename);
        }
        catch (MalformedURLException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    @Override
    public Result getAbstractWSDL(Holder<String> filename) {
        if (this.abstractWsdl != null) {
            filename.value = this.abstractWsdl.getURL().toString();
            return null;
        }
        URL url = this.createURL((String)filename.value);
        MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        xsb.setSystemId(url.toExternalForm());
        SDDocumentSource abstractWsdlSource = SDDocumentSource.create(url, (XMLStreamBuffer)xsb);
        this.newDocs.add(abstractWsdlSource);
        XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
        r.setSystemId((String)filename.value);
        return r;
    }

    @Override
    public Result getSchemaOutput(String namespace, Holder<String> filename) {
        List<SDDocumentImpl> schemas = this.nsMapping.get(namespace);
        if (schemas != null) {
            if (schemas.size() > 1) {
                throw new ServerRtException("server.rt.err", "More than one schema for the target namespace " + namespace);
            }
            filename.value = schemas.get(0).getURL().toExternalForm();
            return null;
        }
        URL url = this.createURL((String)filename.value);
        MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        xsb.setSystemId(url.toExternalForm());
        SDDocumentSource sd = SDDocumentSource.create(url, (XMLStreamBuffer)xsb);
        this.newDocs.add(sd);
        XMLStreamBufferResult r = new XMLStreamBufferResult(xsb);
        r.setSystemId((String)filename.value);
        return r;
    }

    public SDDocumentImpl updateDocs() {
        for (SDDocumentSource doc : this.newDocs) {
            SDDocumentImpl docImpl = SDDocumentImpl.create(doc, this.serviceName, this.portTypeName);
            if (doc == this.concreteWsdlSource) {
                this.concreteWsdl = docImpl;
            }
            this.docs.add(docImpl);
        }
        return this.concreteWsdl;
    }
}

