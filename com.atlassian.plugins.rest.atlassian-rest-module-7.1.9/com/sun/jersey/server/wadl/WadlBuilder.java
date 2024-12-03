/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.wadl;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.BuildId;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.generators.WadlGeneratorJAXBGrammarGenerator;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import javax.xml.namespace.QName;

public class WadlBuilder {
    private WadlGenerator _wadlGenerator;

    public WadlBuilder() {
        this(WadlBuilder.createDefaultGenerator());
    }

    public WadlBuilder(WadlGenerator wadlGenerator) {
        this._wadlGenerator = wadlGenerator;
    }

    public ApplicationDescription generate(UriInfo info, Set<AbstractResource> resources) {
        return this.generate(null, null, info, resources);
    }

    public ApplicationDescription generate(Providers providers, FeaturesAndProperties fap, UriInfo info, Set<AbstractResource> resources) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        Application wadlApplication = this._wadlGenerator.createApplication(info);
        Resources wadlResources = this._wadlGenerator.createResources();
        for (AbstractResource r : resources) {
            Resource wadlResource = this.generateResource(r, null);
            wadlResources.getResource().add(wadlResource);
        }
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        WadlGenerator.ExternalGrammarDefinition external = this._wadlGenerator.createExternalGrammar();
        ApplicationDescription description = new ApplicationDescription(wadlApplication, external);
        this._wadlGenerator.attachTypes(description);
        return description;
    }

    public Application generate(Providers providers, FeaturesAndProperties fap, UriInfo info, ApplicationDescription description, AbstractResource resource) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        Application wadlApplication = this._wadlGenerator.createApplication(info);
        Resources wadlResources = this._wadlGenerator.createResources();
        Resource wadlResource = this.generateResource(resource, null);
        wadlResources.getResource().add(wadlResource);
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        this._wadlGenerator.attachTypes(description);
        return wadlApplication;
    }

    public Application generate(Providers providers, FeaturesAndProperties fap, UriInfo info, ApplicationDescription description, AbstractResource resource, String path) {
        this._wadlGenerator.setEnvironment(new WadlGenerator.Environment().setProviders(providers).setFeaturesAndProperties(fap));
        Application wadlApplication = this._wadlGenerator.createApplication(info);
        Resources wadlResources = this._wadlGenerator.createResources();
        Resource wadlResource = this.generateSubResource(resource, path);
        wadlResources.getResource().add(wadlResource);
        wadlApplication.getResources().add(wadlResources);
        this.addVersion(wadlApplication);
        this._wadlGenerator.attachTypes(description);
        return wadlApplication;
    }

    private void addVersion(Application wadlApplication) {
        Doc d = new Doc();
        d.getOtherAttributes().put(new QName("http://jersey.java.net/", "generatedBy", "jersey"), BuildId.getBuildId());
        wadlApplication.getDoc().add(0, d);
    }

    private Method generateMethod(AbstractResource r, Map<String, Param> wadlResourceParams, AbstractResourceMethod m) {
        List<Response> responses;
        Method wadlMethod = this._wadlGenerator.createMethod(r, m);
        Request wadlRequest = this.generateRequest(r, m, wadlResourceParams);
        if (wadlRequest != null) {
            wadlMethod.setRequest(wadlRequest);
        }
        if ((responses = this.generateResponses(r, m)) != null) {
            wadlMethod.getResponse().addAll(responses);
        }
        return wadlMethod;
    }

    private Request generateRequest(AbstractResource r, AbstractResourceMethod m, Map<String, Param> wadlResourceParams) {
        if (m.getParameters().isEmpty()) {
            return null;
        }
        Request wadlRequest = this._wadlGenerator.createRequest(r, m);
        for (Parameter p : m.getParameters()) {
            Param wadlParam;
            Representation wadlRepresentation;
            List<MediaType> supportedInputTypes;
            if (p.getSource() == Parameter.Source.ENTITY) {
                for (MediaType mediaType : m.getSupportedInputTypes()) {
                    this.setRepresentationForMediaType(r, m, mediaType, wadlRequest);
                }
                continue;
            }
            if (p.getAnnotation().annotationType() == FormParam.class) {
                supportedInputTypes = m.getSupportedInputTypes();
                if (supportedInputTypes.isEmpty() || supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType()) {
                    supportedInputTypes = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
                }
                for (MediaType mediaType : supportedInputTypes) {
                    wadlRepresentation = this.setRepresentationForMediaType(r, m, mediaType, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), p.getSourceName()) != null || (wadlParam = this.generateParam(r, m, p)) == null) continue;
                    wadlRepresentation.getParam().add(wadlParam);
                }
                continue;
            }
            if (p.getAnnotation().annotationType().getName().equals("com.sun.jersey.multipart.FormDataParam")) {
                supportedInputTypes = m.getSupportedInputTypes();
                if (supportedInputTypes.isEmpty() || supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType()) {
                    supportedInputTypes = Collections.singletonList(MediaType.MULTIPART_FORM_DATA_TYPE);
                }
                for (MediaType mediaType : supportedInputTypes) {
                    wadlRepresentation = this.setRepresentationForMediaType(r, m, mediaType, wadlRequest);
                    if (this.getParamByName(wadlRepresentation.getParam(), p.getSourceName()) != null || (wadlParam = this.generateParam(r, m, p)) == null) continue;
                    wadlRepresentation.getParam().add(wadlParam);
                }
                continue;
            }
            Param wadlParam2 = this.generateParam(r, m, p);
            if (wadlParam2 == null) continue;
            if (wadlParam2.getStyle() == ParamStyle.TEMPLATE || wadlParam2.getStyle() == ParamStyle.MATRIX) {
                wadlResourceParams.put(wadlParam2.getName(), wadlParam2);
                continue;
            }
            wadlRequest.getParam().add(wadlParam2);
        }
        if (wadlRequest.getRepresentation().size() + wadlRequest.getParam().size() == 0) {
            return null;
        }
        return wadlRequest;
    }

    private Param getParamByName(List<Param> params, String name) {
        for (Param param : params) {
            if (!param.getName().equals(name)) continue;
            return param;
        }
        return null;
    }

    private Representation setRepresentationForMediaType(AbstractResource r, AbstractResourceMethod m, MediaType mediaType, Request wadlRequest) {
        Representation wadlRepresentation = this.getRepresentationByMediaType(wadlRequest.getRepresentation(), mediaType);
        if (wadlRepresentation == null) {
            wadlRepresentation = this._wadlGenerator.createRequestRepresentation(r, m, mediaType);
            wadlRequest.getRepresentation().add(wadlRepresentation);
        }
        return wadlRepresentation;
    }

    private Representation getRepresentationByMediaType(List<Representation> representations, MediaType mediaType) {
        for (Representation representation : representations) {
            if (!mediaType.toString().equals(representation.getMediaType())) continue;
            return representation;
        }
        return null;
    }

    private Param generateParam(AbstractResource r, AbstractMethod m, Parameter p) {
        if (p.getSource() == Parameter.Source.ENTITY || p.getSource() == Parameter.Source.CONTEXT) {
            return null;
        }
        Param wadlParam = this._wadlGenerator.createParam(r, m, p);
        return wadlParam;
    }

    private Resource generateResource(AbstractResource r, String path) {
        return this.generateResource(r, path, Collections.emptySet());
    }

    private Resource generateResource(AbstractResource r, String path, Set<Class<?>> visitedClasses) {
        HashMap wadlSubResourceParams;
        Resource wadlSubResource;
        String template;
        Resource wadlResource = this._wadlGenerator.createResource(r, path);
        if (visitedClasses.contains(r.getResourceClass())) {
            return wadlResource;
        }
        visitedClasses = new HashSet(visitedClasses);
        visitedClasses.add(r.getResourceClass());
        HashMap<String, Param> wadlResourceParams = new HashMap<String, Param>();
        LinkedList<Parameterized> fieldsOrSetters = new LinkedList<Parameterized>();
        if (r.getFields() != null) {
            fieldsOrSetters.addAll(r.getFields());
        }
        if (r.getSetterMethods() != null) {
            fieldsOrSetters.addAll(r.getSetterMethods());
        }
        for (Parameterized f : fieldsOrSetters) {
            for (Parameter parameter : f.getParameters()) {
                Param wadlParam = this.generateParam(r, null, parameter);
                if (wadlParam == null) continue;
                wadlResource.getParam().add(wadlParam);
            }
        }
        for (AbstractResourceMethod m : r.getResourceMethods()) {
            Method wadlMethod = this.generateMethod(r, wadlResourceParams, m);
            wadlResource.getMethodOrResource().add(wadlMethod);
        }
        for (Param wadlParam : wadlResourceParams.values()) {
            wadlResource.getParam().add(wadlParam);
        }
        HashMap<String, Resource> wadlSubResources = new HashMap<String, Resource>();
        HashMap wadlSubResourcesParams = new HashMap();
        for (AbstractSubResourceMethod abstractSubResourceMethod : r.getSubResourceMethods()) {
            template = abstractSubResourceMethod.getPath().getValue();
            wadlSubResource = (Resource)wadlSubResources.get(template);
            wadlSubResourceParams = (HashMap)wadlSubResourcesParams.get(template);
            if (wadlSubResource == null) {
                wadlSubResource = new Resource();
                wadlSubResource.setPath(template);
                wadlSubResources.put(template, wadlSubResource);
                wadlSubResourceParams = new HashMap();
                wadlSubResourcesParams.put(template, wadlSubResourceParams);
                wadlResource.getMethodOrResource().add(wadlSubResource);
            }
            Method wadlMethod = this.generateMethod(r, wadlSubResourceParams, abstractSubResourceMethod);
            wadlSubResource.getMethodOrResource().add(wadlMethod);
        }
        for (Map.Entry entry : wadlSubResources.entrySet()) {
            template = (String)entry.getKey();
            wadlSubResource = (Resource)entry.getValue();
            wadlSubResourceParams = (Map)wadlSubResourcesParams.get(template);
            for (Param wadlParam : wadlSubResourceParams.values()) {
                wadlSubResource.getParam().add(wadlParam);
            }
        }
        for (AbstractSubResourceLocator abstractSubResourceLocator : r.getSubResourceLocators()) {
            AbstractResource subResource = IntrospectionModeller.createResource(abstractSubResourceLocator.getMethod().getReturnType());
            wadlSubResource = this.generateResource(subResource, abstractSubResourceLocator.getPath().getValue(), visitedClasses);
            wadlResource.getMethodOrResource().add(wadlSubResource);
            for (Parameter p : abstractSubResourceLocator.getParameters()) {
                Param wadlParam;
                wadlParam = this.generateParam(r, abstractSubResourceLocator, p);
                if (wadlParam == null || wadlParam.getStyle() != ParamStyle.TEMPLATE) continue;
                wadlSubResource.getParam().add(wadlParam);
            }
        }
        return wadlResource;
    }

    private Resource generateSubResource(AbstractResource r, String path) {
        Resource wadlResource = new Resource();
        if (r.isRootResource()) {
            StringBuilder b = new StringBuilder(r.getPath().getValue());
            if (!r.getPath().getValue().endsWith("/") && !path.startsWith("/")) {
                b.append("/");
            }
            b.append(path);
            wadlResource.setPath(b.toString());
        }
        HashMap<String, Param> wadlSubResourceParams = new HashMap<String, Param>();
        for (AbstractSubResourceMethod m : r.getSubResourceMethods()) {
            String template = m.getPath().getValue();
            if (!template.equals(path) && !template.equals('/' + path)) continue;
            Method wadlMethod = this.generateMethod(r, wadlSubResourceParams, m);
            wadlResource.getMethodOrResource().add(wadlMethod);
        }
        for (Param wadlParam : wadlSubResourceParams.values()) {
            wadlResource.getParam().add(wadlParam);
        }
        return wadlResource;
    }

    private List<Response> generateResponses(AbstractResource r, AbstractResourceMethod m) {
        if (m.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return this._wadlGenerator.createResponses(r, m);
    }

    private static WadlGeneratorJAXBGrammarGenerator createDefaultGenerator() throws RuntimeException {
        WadlGeneratorJAXBGrammarGenerator wadlGeneratorJAXBGrammarGenerator = new WadlGeneratorJAXBGrammarGenerator();
        try {
            wadlGeneratorJAXBGrammarGenerator.init();
        }
        catch (Exception ex) {
            throw new RuntimeException(ImplMessages.ERROR_CREATING_DEFAULT_WADL_GENERATOR(), ex);
        }
        return wadlGeneratorJAXBGrammarGenerator;
    }
}

