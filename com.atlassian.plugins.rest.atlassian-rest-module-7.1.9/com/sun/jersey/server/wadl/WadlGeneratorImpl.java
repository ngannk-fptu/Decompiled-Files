/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.wadl;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

public class WadlGeneratorImpl
implements WadlGenerator {
    @Override
    public String getRequiredJaxbContextPath() {
        String name = Application.class.getName();
        return name.substring(0, name.lastIndexOf(46));
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void setEnvironment(WadlGenerator.Environment env) {
    }

    @Override
    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        throw new UnsupportedOperationException("No delegate supported.");
    }

    @Override
    public Resources createResources() {
        return new Resources();
    }

    @Override
    public Application createApplication(UriInfo requestInfo) {
        return new Application();
    }

    @Override
    public Method createMethod(AbstractResource r, AbstractResourceMethod m) {
        Method wadlMethod = new Method();
        wadlMethod.setName(m.getHttpMethod());
        wadlMethod.setId(m.getMethod().getName());
        return wadlMethod;
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }

    @Override
    public Request createRequest(AbstractResource r, AbstractResourceMethod m) {
        return new Request();
    }

    @Override
    public Param createParam(AbstractResource r, AbstractMethod m, Parameter p) {
        Class<?> pClass;
        if (p.getSource() == Parameter.Source.UNKNOWN) {
            return null;
        }
        Param wadlParam = new Param();
        wadlParam.setName(p.getSourceName());
        switch (p.getSource()) {
            case FORM: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case QUERY: {
                wadlParam.setStyle(ParamStyle.QUERY);
                break;
            }
            case MATRIX: {
                wadlParam.setStyle(ParamStyle.MATRIX);
                break;
            }
            case PATH: {
                wadlParam.setStyle(ParamStyle.TEMPLATE);
                break;
            }
            case HEADER: {
                wadlParam.setStyle(ParamStyle.HEADER);
                break;
            }
            case COOKIE: {
                wadlParam.setStyle(ParamStyle.HEADER);
                wadlParam.setName("Cookie");
                wadlParam.setPath(p.getSourceName());
                break;
            }
        }
        if (p.hasDefaultValue()) {
            wadlParam.setDefault(p.getDefaultValue());
        }
        if ((pClass = p.getParameterClass()).isArray()) {
            wadlParam.setRepeating(true);
            pClass = pClass.getComponentType();
        }
        if (pClass.equals(Integer.TYPE) || pClass.equals(Integer.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "int", "xs"));
        } else if (pClass.equals(Boolean.TYPE) || pClass.equals(Boolean.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
        } else if (pClass.equals(Long.TYPE) || pClass.equals(Long.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
        } else if (pClass.equals(Short.TYPE) || pClass.equals(Short.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
        } else if (pClass.equals(Byte.TYPE) || pClass.equals(Byte.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
        } else if (pClass.equals(Float.TYPE) || pClass.equals(Float.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
        } else if (pClass.equals(Double.TYPE) || pClass.equals(Double.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
        } else {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
        }
        return wadlParam;
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        Resource wadlResource = new Resource();
        if (path != null) {
            wadlResource.setPath(path);
        } else if (r.isRootResource()) {
            wadlResource.setPath(r.getPath().getValue());
        }
        return wadlResource;
    }

    @Override
    public List<Response> createResponses(AbstractResource r, AbstractResourceMethod m) {
        Response response = new Response();
        for (MediaType mediaType : m.getSupportedOutputTypes()) {
            if (MediaType.WILDCARD_TYPE.equals(mediaType) && this.hasEmptyProducibleMediaTypeSet(m)) continue;
            Representation wadlRepresentation = this.createResponseRepresentation(r, m, mediaType);
            response.getRepresentation().add(wadlRepresentation);
        }
        ArrayList<Response> responses = new ArrayList<Response>();
        responses.add(response);
        return responses;
    }

    private boolean hasEmptyProducibleMediaTypeSet(AbstractResourceMethod method) {
        Produces produces = method.getMethod().getAnnotation(Produces.class);
        return produces != null && this.getProducibleMediaTypes(method).isEmpty();
    }

    private List<String> getProducibleMediaTypes(AbstractResourceMethod method) {
        List<String> mediaTypes = Collections.emptyList();
        Produces produces = method.getMethod().getAnnotation(Produces.class);
        if (produces != null && produces.value() != null) {
            mediaTypes = Arrays.asList(produces.value());
        }
        return mediaTypes;
    }

    public Representation createResponseRepresentation(AbstractResource r, AbstractResourceMethod m, MediaType mediaType) {
        Representation wadlRepresentation = new Representation();
        wadlRepresentation.setMediaType(mediaType.toString());
        return wadlRepresentation;
    }

    @Override
    public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
        return new WadlGenerator.ExternalGrammarDefinition();
    }

    @Override
    public void attachTypes(ApplicationDescription egd) {
    }
}

