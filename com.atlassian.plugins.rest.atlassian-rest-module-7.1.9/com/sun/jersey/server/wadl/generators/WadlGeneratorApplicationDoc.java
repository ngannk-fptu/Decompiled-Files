/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.ApplicationDocs;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class WadlGeneratorApplicationDoc
implements WadlGenerator {
    private WadlGenerator _delegate;
    private File _applicationDocsFile;
    private InputStream _applicationDocsStream;
    private ApplicationDocs _applicationDocs;

    public WadlGeneratorApplicationDoc() {
    }

    public WadlGeneratorApplicationDoc(WadlGenerator wadlGenerator, ApplicationDocs applicationDocs) {
        this._delegate = wadlGenerator;
        this._applicationDocs = applicationDocs;
    }

    @Override
    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        this._delegate = delegate;
    }

    @Override
    public String getRequiredJaxbContextPath() {
        return this._delegate.getRequiredJaxbContextPath();
    }

    @Override
    public void setEnvironment(WadlGenerator.Environment env) {
        this._delegate.setEnvironment(env);
    }

    public void setApplicationDocsFile(File applicationDocsFile) {
        if (this._applicationDocsStream != null) {
            throw new IllegalStateException("The applicationDocsStream property is already set, therefore you cannot set the applicationDocsFile property. Only one of both can be set at a time.");
        }
        this._applicationDocsFile = applicationDocsFile;
    }

    public void setApplicationDocsStream(InputStream applicationDocsStream) {
        if (this._applicationDocsFile != null) {
            throw new IllegalStateException("The applicationDocsFile property is already set, therefore you cannot set the applicationDocsStream property. Only one of both can be set at a time.");
        }
        this._applicationDocsStream = applicationDocsStream;
    }

    @Override
    public void init() throws Exception {
        if (this._applicationDocsFile == null && this._applicationDocsStream == null) {
            throw new IllegalStateException("Neither the applicationDocsFile nor the applicationDocsStream is set, one of both is required.");
        }
        this._delegate.init();
        String name = ApplicationDocs.class.getName();
        int i = name.lastIndexOf(46);
        name = i != -1 ? name.substring(0, i) : "";
        JAXBContext c = JAXBContext.newInstance((String)name, (ClassLoader)Thread.currentThread().getContextClassLoader());
        Unmarshaller m = c.createUnmarshaller();
        Object obj = this._applicationDocsFile != null ? m.unmarshal(this._applicationDocsFile) : m.unmarshal(this._applicationDocsStream);
        this._applicationDocs = (ApplicationDocs)ApplicationDocs.class.cast(obj);
    }

    @Override
    public Application createApplication(UriInfo requestInfo) {
        Application result = this._delegate.createApplication(requestInfo);
        if (this._applicationDocs != null && this._applicationDocs.getDocs() != null && !this._applicationDocs.getDocs().isEmpty()) {
            result.getDoc().addAll(this._applicationDocs.getDocs());
        }
        return result;
    }

    @Override
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        return this._delegate.createMethod(r, m);
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        return this._delegate.createRequestRepresentation(r, m, mediaType);
    }

    @Override
    public Request createRequest(AbstractResource r, AbstractResourceMethod m) {
        return this._delegate.createRequest(r, m);
    }

    @Override
    public Param createParam(AbstractResource r, AbstractMethod m, Parameter p) {
        return this._delegate.createParam(r, m, p);
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        return this._delegate.createResource(r, path);
    }

    @Override
    public List<Response> createResponses(AbstractResource r, AbstractResourceMethod m) {
        return this._delegate.createResponses(r, m);
    }

    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }

    @Override
    public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
        return this._delegate.createExternalGrammar();
    }

    @Override
    public void attachTypes(ApplicationDescription egd) {
        this._delegate.attachTypes(egd);
    }
}

