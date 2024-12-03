/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.wadl;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

public interface WadlGenerator {
    public void setWadlGeneratorDelegate(WadlGenerator var1);

    public void init() throws Exception;

    public String getRequiredJaxbContextPath();

    public void setEnvironment(Environment var1);

    public Application createApplication(UriInfo var1);

    public Resources createResources();

    public Resource createResource(AbstractResource var1, String var2);

    public Method createMethod(AbstractResource var1, AbstractResourceMethod var2);

    public Request createRequest(AbstractResource var1, AbstractResourceMethod var2);

    public Representation createRequestRepresentation(AbstractResource var1, AbstractResourceMethod var2, MediaType var3);

    public List<Response> createResponses(AbstractResource var1, AbstractResourceMethod var2);

    public Param createParam(AbstractResource var1, AbstractMethod var2, Parameter var3);

    public ExternalGrammarDefinition createExternalGrammar();

    public void attachTypes(ApplicationDescription var1);

    public static class ExternalGrammarDefinition {
        public final Map<String, ApplicationDescription.ExternalGrammar> map = new LinkedHashMap<String, ApplicationDescription.ExternalGrammar>();
        private List<Resolver> typeResolvers = new ArrayList<Resolver>();

        public void addResolver(Resolver resolver) {
            assert (!this.typeResolvers.contains(resolver)) : "Already in list";
            this.typeResolvers.add(0, resolver);
        }

        public <T> T resolve(Class type, MediaType mt, Class<T> resolvedType) {
            Resolver resolver;
            T name = null;
            Iterator<Resolver> iterator = this.typeResolvers.iterator();
            while (iterator.hasNext() && (name = (T)(resolver = iterator.next()).resolve(type, mt, resolvedType)) == null) {
            }
            return name;
        }
    }

    public static interface Resolver {
        public <T> T resolve(Class var1, MediaType var2, Class<T> var3);
    }

    public static class Environment {
        private Providers providers;
        private FeaturesAndProperties fap;

        public Environment setProviders(Providers providers) {
            this.providers = providers;
            return this;
        }

        public Providers getProviders() {
            return this.providers;
        }

        public Environment setFeaturesAndProperties(FeaturesAndProperties fap) {
            this.fap = fap;
            return this;
        }

        public FeaturesAndProperties getFeaturesAndProperties() {
            return this.fap;
        }
    }
}

