/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlSeeAlso
 */
package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.api.JResponse;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.annotation.XmlSeeAlso;

public abstract class AbstractWadlGeneratorGrammarGenerator<T>
implements WadlGenerator {
    private static final Logger LOGGER = Logger.getLogger(AbstractWadlGeneratorGrammarGenerator.class.getName());
    public static final Set<Class> SPECIAL_GENERIC_TYPES = new HashSet<Class>(){
        {
            this.add(JResponse.class);
            this.add(List.class);
        }
    };
    private WadlGenerator _delegate;
    protected Set<Class> _seeAlso;
    protected List<Pair> _hasTypeWantsName;
    protected URI _root;
    protected URI _wadl;
    protected Providers _providers;
    protected FeaturesAndProperties _fap;
    protected Class<T> _resolvedType;

    protected static HasType parameter(final Parameter param, final MediaType mt) {
        return new HasType(){

            @Override
            public Class getPrimaryClass() {
                return param.getParameterClass();
            }

            @Override
            public Type getType() {
                return param.getParameterType();
            }

            @Override
            public MediaType getMediaType() {
                return mt;
            }
        };
    }

    protected AbstractWadlGeneratorGrammarGenerator(WadlGenerator delegate, Class<T> resolvedType) {
        this._delegate = delegate;
        this._resolvedType = resolvedType;
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
    public void init() throws Exception {
        this._delegate.init();
        this._seeAlso = new HashSet<Class>();
        this._hasTypeWantsName = new ArrayList<Pair>();
    }

    @Override
    public void setEnvironment(WadlGenerator.Environment env) {
        this._delegate.setEnvironment(env);
        this._providers = env.getProviders();
        this._fap = env.getFeaturesAndProperties();
    }

    public abstract boolean acceptMediaType(MediaType var1);

    @Override
    public Application createApplication(UriInfo requestInfo) {
        if (requestInfo != null) {
            this._root = requestInfo.getBaseUri();
            this._wadl = requestInfo.getRequestUri();
        }
        return this._delegate.createApplication(requestInfo);
    }

    @Override
    public Method createMethod(AbstractResource ar, AbstractResourceMethod arm) {
        return this._delegate.createMethod(ar, arm);
    }

    @Override
    public Request createRequest(AbstractResource ar, AbstractResourceMethod arm) {
        return this._delegate.createRequest(ar, arm);
    }

    @Override
    public Param createParam(AbstractResource ar, AbstractMethod am, Parameter p) {
        Param param = this._delegate.createParam(ar, am, p);
        if (p.getSource() == Parameter.Source.ENTITY) {
            this._hasTypeWantsName.add(new Pair(AbstractWadlGeneratorGrammarGenerator.parameter(p, MediaType.APPLICATION_XML_TYPE), this.createParmWantsName(param)));
        }
        return param;
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource ar, AbstractResourceMethod arm, MediaType mt) {
        Representation rt = this._delegate.createRequestRepresentation(ar, arm, mt);
        for (Parameter p : arm.getParameters()) {
            if (p.getSource() != Parameter.Source.ENTITY || !this.acceptMediaType(mt)) continue;
            this._hasTypeWantsName.add(new Pair(AbstractWadlGeneratorGrammarGenerator.parameter(p, mt), this.createRepresentationWantsName(rt)));
        }
        return rt;
    }

    @Override
    public Resource createResource(AbstractResource ar, String path) {
        Class<?> cls = ar.getResourceClass();
        XmlSeeAlso seeAlso = cls.getAnnotation(XmlSeeAlso.class);
        if (seeAlso != null) {
            Collections.addAll(this._seeAlso, seeAlso.value());
        }
        return this._delegate.createResource(ar, path);
    }

    @Override
    public Resources createResources() {
        return this._delegate.createResources();
    }

    @Override
    public List<Response> createResponses(AbstractResource ar, final AbstractResourceMethod arm) {
        List<Response> responses = this._delegate.createResponses(ar, arm);
        if (responses != null) {
            for (Response response : responses) {
                for (final Representation representation : response.getRepresentation()) {
                    if (representation.getMediaType() == null || !this.acceptMediaType(MediaType.valueOf(representation.getMediaType()))) continue;
                    HasType hasType = new HasType(){

                        @Override
                        public Class getPrimaryClass() {
                            return arm.getReturnType();
                        }

                        @Override
                        public Type getType() {
                            return arm.getGenericReturnType();
                        }

                        @Override
                        public MediaType getMediaType() {
                            return MediaType.valueOf(representation.getMediaType());
                        }
                    };
                    this._hasTypeWantsName.add(new Pair(hasType, this.createRepresentationWantsName(representation)));
                }
            }
        }
        return responses;
    }

    @Override
    public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
        WadlGenerator.ExternalGrammarDefinition previous = this._delegate.createExternalGrammar();
        HashMap<String, ApplicationDescription.ExternalGrammar> extraFiles = new HashMap<String, ApplicationDescription.ExternalGrammar>();
        WadlGenerator.Resolver resolver = this.buildModelAndSchemas(extraFiles);
        previous.map.putAll(extraFiles);
        if (resolver != null) {
            previous.addResolver(resolver);
        }
        return previous;
    }

    protected abstract WadlGenerator.Resolver buildModelAndSchemas(Map<String, ApplicationDescription.ExternalGrammar> var1);

    @Override
    public void attachTypes(ApplicationDescription introspector) {
        this._delegate.attachTypes(introspector);
        if (introspector != null) {
            int i = this._hasTypeWantsName.size();
            for (int j = 0; j < i; ++j) {
                T name;
                HasType nextType;
                Class parameterClass;
                Pair pair = this._hasTypeWantsName.get(j);
                WantsName nextToProcess = pair.wantsName;
                if (!nextToProcess.isElement()) {
                    LOGGER.info("Type references are not supported as yet");
                }
                if (SPECIAL_GENERIC_TYPES.contains(parameterClass = (nextType = pair.hasType).getPrimaryClass())) {
                    Type type = nextType.getType();
                    if (ParameterizedType.class.isAssignableFrom(type.getClass()) && Class.class.isAssignableFrom(((ParameterizedType)type).getActualTypeArguments()[0].getClass())) {
                        parameterClass = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
                    } else {
                        LOGGER.info("Couldn't find grammar element due to nested parameterized type " + type);
                        return;
                    }
                }
                if ((name = introspector.resolve(parameterClass, nextType.getMediaType(), this._resolvedType)) != null) {
                    nextToProcess.setName(name);
                    continue;
                }
                LOGGER.info("Couldn't find grammar element for class " + parameterClass.getName());
            }
        }
    }

    protected abstract WantsName<T> createParmWantsName(Param var1);

    protected abstract WantsName<T> createRepresentationWantsName(Representation var1);

    protected class Pair {
        public HasType hasType;
        public WantsName wantsName;

        public Pair(HasType hasType, WantsName wantsName) {
            this.hasType = hasType;
            this.wantsName = wantsName;
        }
    }

    protected static interface WantsName<T> {
        public boolean isElement();

        public void setName(T var1);
    }

    protected static interface HasType {
        public Class getPrimaryClass();

        public Type getType();

        public MediaType getMediaType();
    }
}

