/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.server.wadl.generators.resourcedoc;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.resourcedoc.ResourceDocAccessor;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ClassDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.MethodDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ParamDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.RepresentationDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResourceDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResponseDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.WadlParamType;
import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.Elements;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class WadlGeneratorResourceDocSupport
implements WadlGenerator {
    private WadlGenerator _delegate;
    private File resourceDocFile;
    private InputStream resourceDocStream;
    private ResourceDocAccessor resourceDoc;

    public WadlGeneratorResourceDocSupport() {
    }

    public WadlGeneratorResourceDocSupport(WadlGenerator wadlGenerator, ResourceDocType resourceDoc) {
        this._delegate = wadlGenerator;
        this.resourceDoc = new ResourceDocAccessor(resourceDoc);
    }

    @Override
    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        this._delegate = delegate;
    }

    @Override
    public void setEnvironment(WadlGenerator.Environment env) {
        this._delegate.setEnvironment(env);
    }

    public void setResourceDocFile(File resourceDocFile) {
        if (this.resourceDocStream != null) {
            throw new IllegalStateException("The resourceDocStream property is already set, therefore you cannot set the resourceDocFile property. Only one of both can be set at a time.");
        }
        this.resourceDocFile = resourceDocFile;
    }

    public void setResourceDocStream(InputStream resourceDocStream) {
        if (this.resourceDocStream != null) {
            throw new IllegalStateException("The resourceDocFile property is already set, therefore you cannot set the resourceDocStream property. Only one of both can be set at a time.");
        }
        this.resourceDocStream = resourceDocStream;
    }

    @Override
    public void init() throws Exception {
        if (this.resourceDocFile == null && this.resourceDocStream == null) {
            throw new IllegalStateException("Neither the resourceDocFile nor the resourceDocStream is set, one of both is required.");
        }
        this._delegate.init();
        JAXBContext c = JAXBContext.newInstance((Class[])new Class[]{ResourceDocType.class});
        Unmarshaller m = c.createUnmarshaller();
        Object resourceDocObj = this.resourceDocFile != null ? m.unmarshal(this.resourceDocFile) : m.unmarshal(this.resourceDocStream);
        ResourceDocType resourceDoc = (ResourceDocType)ResourceDocType.class.cast(resourceDocObj);
        this.resourceDoc = new ResourceDocAccessor(resourceDoc);
        this.resourceDocFile = null;
        this.resourceDocStream = null;
    }

    @Override
    public String getRequiredJaxbContextPath() {
        String name = Elements.class.getName();
        name = name.substring(0, name.lastIndexOf(46));
        return this._delegate.getRequiredJaxbContextPath() == null ? name : this._delegate.getRequiredJaxbContextPath() + ":" + name;
    }

    @Override
    public Application createApplication(UriInfo requestInfo) {
        return this._delegate.createApplication(requestInfo);
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        Resource result = this._delegate.createResource(r, path);
        ClassDocType classDoc = this.resourceDoc.getClassDoc(r.getResourceClass());
        if (classDoc != null && !this.isEmpty(classDoc.getCommentText())) {
            Doc doc = new Doc();
            doc.getContent().add(classDoc.getCommentText());
            result.getDoc().add(doc);
        }
        return result;
    }

    @Override
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        Method result = this._delegate.createMethod(r, m);
        MethodDocType methodDoc = this.resourceDoc.getMethodDoc(r.getResourceClass(), m.getMethod());
        if (methodDoc != null && !this.isEmpty(methodDoc.getCommentText())) {
            Doc doc = new Doc();
            doc.getContent().add(methodDoc.getCommentText());
            result.getDoc().add(doc);
        }
        return result;
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        Representation result = this._delegate.createRequestRepresentation(r, m, mediaType);
        RepresentationDocType requestRepresentation = this.resourceDoc.getRequestRepresentation(r.getResourceClass(), m.getMethod(), result.getMediaType());
        if (requestRepresentation != null) {
            result.setElement(requestRepresentation.getElement());
            this.addDocForExample(result.getDoc(), requestRepresentation.getExample());
        }
        return result;
    }

    @Override
    public Request createRequest(AbstractResource r, AbstractResourceMethod m) {
        return this._delegate.createRequest(r, m);
    }

    @Override
    public List<Response> createResponses(AbstractResource r, AbstractResourceMethod m) {
        ResponseDocType responseDoc = this.resourceDoc.getResponse(r.getResourceClass(), m.getMethod());
        ArrayList<Response> responses = new ArrayList();
        if (responseDoc != null && responseDoc.hasRepresentations()) {
            for (RepresentationDocType representationDoc : responseDoc.getRepresentations()) {
                Response response = new Response();
                Representation wadlRepresentation = new Representation();
                wadlRepresentation.setElement(representationDoc.getElement());
                wadlRepresentation.setMediaType(representationDoc.getMediaType());
                this.addDocForExample(wadlRepresentation.getDoc(), representationDoc.getExample());
                this.addDoc(wadlRepresentation.getDoc(), representationDoc.getDoc());
                response.getStatus().add(representationDoc.getStatus());
                response.getRepresentation().add(wadlRepresentation);
                responses.add(response);
            }
            if (!responseDoc.getWadlParams().isEmpty()) {
                for (WadlParamType wadlParamType : responseDoc.getWadlParams()) {
                    Param param = new Param();
                    param.setName(wadlParamType.getName());
                    param.setStyle(ParamStyle.fromValue(wadlParamType.getStyle()));
                    param.setType(wadlParamType.getType());
                    this.addDoc(param.getDoc(), wadlParamType.getDoc());
                    for (Response response : responses) {
                        response.getParam().add(param);
                    }
                }
            }
            if (!this.isEmpty(responseDoc.getReturnDoc())) {
                for (Response response : responses) {
                    this.addDoc(response.getDoc(), responseDoc.getReturnDoc());
                }
            }
        } else {
            responses = this._delegate.createResponses(r, m);
        }
        return responses;
    }

    private void addDocForExample(List<Doc> docs, String example) {
        if (!this.isEmpty(example)) {
            Doc doc = new Doc();
            Elements pElement = Elements.el("p").add(Elements.val("h6", "Example")).add(new Object[]{Elements.el("pre").add(Elements.val("code", example))});
            doc.getContent().add((Object)pElement);
            docs.add(doc);
        }
    }

    private void addDoc(List<Doc> docs, String text) {
        if (!this.isEmpty(text)) {
            Doc doc = new Doc();
            doc.getContent().add(text);
            docs.add(doc);
        }
    }

    @Override
    public Param createParam(AbstractResource r, AbstractMethod m, Parameter p) {
        ParamDocType paramDoc;
        Param result = this._delegate.createParam(r, m, p);
        if (result != null && (paramDoc = this.resourceDoc.getParamDoc(r.getResourceClass(), m == null ? null : m.getMethod(), p)) != null && !this.isEmpty(paramDoc.getCommentText())) {
            Doc doc = new Doc();
            doc.getContent().add(paramDoc.getCommentText());
            result.getDoc().add(doc);
        }
        return result;
    }

    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }

    private boolean isEmpty(String text) {
        return text == null || text.length() == 0 || "".equals(text.trim());
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

