/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.doclet.generators.resourcedoc;

import com.atlassian.plugins.rest.doclet.generators.resourcedoc.JsonOperations;
import com.atlassian.plugins.rest.doclet.generators.resourcedoc.RestMethod;
import com.atlassian.plugins.rest.doclet.generators.schema.RichClass;
import com.atlassian.plugins.rest.doclet.generators.schema.SchemaGenerator;
import com.atlassian.rest.annotation.ExcludeFromDoc;
import com.atlassian.rest.annotation.RestProperty;
import com.google.common.collect.Lists;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.resourcedoc.WadlGeneratorResourceDocSupport;
import com.sun.jersey.server.wadl.generators.resourcedoc.model.ResourceDocType;
import com.sun.jersey.server.wadl.generators.resourcedoc.xhtml.Elements;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtlassianRuntimeWadlGeneratorResourceDocSupport
extends WadlGeneratorResourceDocSupport {
    private static final Logger LOG = LoggerFactory.getLogger(AtlassianRuntimeWadlGeneratorResourceDocSupport.class);
    private boolean generateSchemas = true;

    public AtlassianRuntimeWadlGeneratorResourceDocSupport() {
    }

    public AtlassianRuntimeWadlGeneratorResourceDocSupport(WadlGenerator wadlGenerator, ResourceDocType resourceDoc) {
        super(wadlGenerator, resourceDoc);
    }

    public void setGenerateSchemas(Boolean generateSchemas) {
        this.generateSchemas = generateSchemas;
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        this.removeMethodsExcludedFromDocs(r);
        if (this.allMethodsExcluded(r)) {
            return new Resource();
        }
        return super.createResource(r, path);
    }

    private boolean allMethodsExcluded(AbstractResource r) {
        return r.getResourceMethods().isEmpty() && r.getSubResourceMethods().isEmpty();
    }

    private void removeMethodsExcludedFromDocs(AbstractResource r) {
        Set<AbstractResourceMethod> excludedMethods = Stream.concat(r.getResourceMethods().stream().filter(method -> this.isMethodExcluded(r, (AbstractResourceMethod)method)), r.getSubResourceMethods().stream().filter(method -> this.isMethodExcluded(r, (AbstractResourceMethod)method))).collect(Collectors.toSet());
        excludedMethods.forEach(method -> {
            r.getResourceMethods().remove(method);
            r.getSubResourceMethods().remove(method);
        });
    }

    private boolean isMethodExcluded(AbstractResource r, AbstractResourceMethod method) {
        return method.isAnnotationPresent(ExcludeFromDoc.class) || r.getResourceClass().isAnnotationPresent(ExcludeFromDoc.class);
    }

    @Override
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        Method method = super.createMethod(r, m);
        RestMethod restMethod = RestMethod.restMethod(r.getResourceClass(), m.getMethod());
        if (restMethod.isExperimental()) {
            method.getOtherAttributes().put(new QName("experimental"), Boolean.TRUE.toString());
        }
        if (restMethod.isDeprecated()) {
            method.getOtherAttributes().put(new QName("deprecated"), Boolean.TRUE.toString());
        }
        return method;
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        Representation representation = super.createRequestRepresentation(r, m, mediaType);
        if (this.generateSchemas) {
            RestMethod.restMethod(r.getResourceClass(), m.getMethod()).getRequestType().ifPresent(richClass -> representation.getDoc().add(this.schemaDoc((RichClass)richClass, RestProperty.Scope.REQUEST)));
        }
        return representation;
    }

    @Override
    public List<Response> createResponses(AbstractResource r, AbstractResourceMethod m) {
        ArrayList result = Lists.newArrayList();
        for (Response response : super.createResponses(r, m)) {
            if (this.generateSchemas) {
                this.addSchemaIfDefinedForStatus(r, m, response);
            }
            result.add(response);
        }
        return result;
    }

    private void addSchemaIfDefinedForStatus(AbstractResource resource, AbstractResourceMethod method, Response response) {
        for (Long status : response.getStatus()) {
            for (RichClass responseType : RestMethod.restMethod(resource.getResourceClass(), method.getMethod()).responseTypesFor(status.intValue())) {
                for (Representation representation : response.getRepresentation()) {
                    representation.getDoc().add(this.schemaDoc(responseType, RestProperty.Scope.RESPONSE));
                }
            }
        }
    }

    private Doc schemaDoc(RichClass model, RestProperty.Scope scope) {
        String schema = JsonOperations.toJson(SchemaGenerator.generateSchema(model, scope));
        Doc doc = new Doc();
        Elements element = Elements.el("p").add(Elements.val("h6", "Schema")).add(new Object[]{Elements.el("pre").add(Elements.val("code", schema))});
        doc.getContent().add((Object)element);
        return doc;
    }
}

