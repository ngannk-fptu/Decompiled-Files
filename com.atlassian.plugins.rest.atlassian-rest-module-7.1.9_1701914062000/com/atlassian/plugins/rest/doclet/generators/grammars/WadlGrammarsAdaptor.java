/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.Unmarshaller
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.doclet.generators.grammars;

import com.atlassian.plugins.rest.doclet.generators.grammars.GrammarTransformer;
import com.atlassian.plugins.rest.doclet.generators.grammars.Grammars;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
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
import java.io.File;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WadlGrammarsAdaptor
implements WadlGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(WadlGrammarsAdaptor.class);
    private WadlGenerator delegate;
    private File grammarsFile;
    private InputStream grammarsStream;
    private Boolean overrideGrammars = false;
    private com.sun.research.ws.wadl.Grammars grammars;

    public WadlGrammarsAdaptor() {
    }

    public WadlGrammarsAdaptor(WadlGenerator delegate, com.sun.research.ws.wadl.Grammars grammars) {
        this.delegate = delegate;
        this.grammars = grammars;
    }

    @Override
    public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEnvironment(WadlGenerator.Environment env) {
        this.delegate.setEnvironment(env);
    }

    public void setOverrideGrammars(Boolean overrideGrammars) {
        this.overrideGrammars = overrideGrammars;
    }

    @Override
    public String getRequiredJaxbContextPath() {
        return this.delegate.getRequiredJaxbContextPath();
    }

    public void setGrammarsFile(File grammarsFile) {
        if (this.grammarsStream != null) {
            throw new IllegalStateException("The grammarsStream property is already set, therefore you cannot set the grammarsFile property. Only one of both can be set at a time.");
        }
        this.grammarsFile = grammarsFile;
    }

    public void setGrammarsStream(InputStream grammarsStream) {
        if (this.grammarsFile != null) {
            throw new IllegalStateException("The grammarsFile property is already set, therefore you cannot set the grammarsStream property. Only one of both can be set at a time.");
        }
        this.grammarsStream = grammarsStream;
    }

    @Override
    public void init() throws Exception {
        Object obj;
        if (this.grammarsFile == null && this.grammarsStream == null) {
            throw new IllegalStateException("Neither the grammarsFile nor the grammarsStream is set, one of both is required.");
        }
        this.delegate.init();
        JAXBContext c = JAXBContext.newInstance((Class[])new Class[]{com.sun.research.ws.wadl.Grammars.class, Grammars.class});
        Unmarshaller m = c.createUnmarshaller();
        Object object = obj = this.grammarsFile != null ? m.unmarshal(this.grammarsFile) : m.unmarshal(this.grammarsStream);
        if (obj.getClass() == Grammars.class) {
            Grammars grm = (Grammars)Grammars.class.cast(obj);
            this.grammars = GrammarTransformer.transform(grm);
        } else if (obj.getClass() == com.sun.research.ws.wadl.Grammars.class) {
            this.grammars = (com.sun.research.ws.wadl.Grammars)com.sun.research.ws.wadl.Grammars.class.cast(obj);
        } else {
            throw new RuntimeException("Unknown grammars class: " + obj.getClass());
        }
    }

    @Override
    public Application createApplication(UriInfo requestInfo) {
        Application result = this.delegate.createApplication(requestInfo);
        if (result.getGrammars() != null && !this.overrideGrammars.booleanValue()) {
            LOG.info("The wadl application created by the delegate ({}) already contains a grammars element, we're adding elements of the provided grammars file.", (Object)this.delegate);
            if (!this.grammars.getAny().isEmpty()) {
                result.getGrammars().getAny().addAll(this.grammars.getAny());
            }
            if (!this.grammars.getDoc().isEmpty()) {
                result.getGrammars().getDoc().addAll(this.grammars.getDoc());
            }
            if (!this.grammars.getInclude().isEmpty()) {
                result.getGrammars().getInclude().addAll(this.grammars.getInclude());
            }
        } else {
            result.setGrammars(this.grammars);
        }
        return result;
    }

    @Override
    public Method createMethod(AbstractResource ar, AbstractResourceMethod arm) {
        return this.delegate.createMethod(ar, arm);
    }

    @Override
    public Request createRequest(AbstractResource ar, AbstractResourceMethod arm) {
        return this.delegate.createRequest(ar, arm);
    }

    @Override
    public Param createParam(AbstractResource ar, AbstractMethod am, Parameter p) {
        return this.delegate.createParam(ar, am, p);
    }

    @Override
    public Representation createRequestRepresentation(AbstractResource ar, AbstractResourceMethod arm, MediaType mt) {
        return this.delegate.createRequestRepresentation(ar, arm, mt);
    }

    @Override
    public Resource createResource(AbstractResource ar, String path) {
        return this.delegate.createResource(ar, path);
    }

    @Override
    public Resources createResources() {
        return this.delegate.createResources();
    }

    @Override
    public List<Response> createResponses(AbstractResource ar, AbstractResourceMethod arm) {
        return this.delegate.createResponses(ar, arm);
    }

    @Override
    public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
        if (this.overrideGrammars.booleanValue()) {
            return new WadlGenerator.ExternalGrammarDefinition();
        }
        return this.delegate.createExternalGrammar();
    }

    @Override
    public void attachTypes(ApplicationDescription egd) {
        this.delegate.attachTypes(egd);
    }
}

