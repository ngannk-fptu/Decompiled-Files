/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 */
package com.sun.jersey.server.impl.wadl;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfig;
import com.sun.jersey.api.wadl.config.WadlGeneratorConfigLoader;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.jersey.server.wadl.WadlBuilder;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Grammars;
import com.sun.research.ws.wadl.Include;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import java.net.URI;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class WadlApplicationContextImpl
implements WadlApplicationContext {
    private static final Logger LOG = Logger.getLogger(WadlApplicationContextImpl.class.getName());
    private boolean wadlGenerationEnabled = true;
    private final Set<AbstractResource> rootResources;
    private final WadlGeneratorConfig wadlGeneratorConfig;
    private JAXBContext jaxbContext;
    private final Providers providers;
    private final FeaturesAndProperties fap;

    public WadlApplicationContextImpl(Set<AbstractResource> rootResources, ResourceConfig resourceConfig, Providers providers) {
        this.rootResources = rootResources;
        this.wadlGeneratorConfig = WadlGeneratorConfigLoader.loadWadlGeneratorsFromConfig(resourceConfig);
        this.providers = providers;
        this.fap = resourceConfig;
        try {
            WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator();
            String requiredJaxbContextPath = wadlGenerator.getRequiredJaxbContextPath();
            this.jaxbContext = null;
            try {
                this.jaxbContext = JAXBContext.newInstance((String)requiredJaxbContextPath, (ClassLoader)wadlGenerator.getClass().getClassLoader());
            }
            catch (JAXBException ex) {
                LOG.log(Level.FINE, ex.getMessage(), ex);
                this.jaxbContext = JAXBContext.newInstance((String)requiredJaxbContextPath);
            }
        }
        catch (JAXBException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Override
    public ApplicationDescription getApplication(UriInfo uriInfo) {
        ApplicationDescription a = this.getWadlBuilder().generate(this.providers, this.fap, uriInfo, this.rootResources);
        Application application = a.getApplication();
        for (Resources resources : application.getResources()) {
            if (resources.getBase() != null) continue;
            resources.setBase(uriInfo.getBaseUri().toString());
        }
        this.attachExternalGrammar(application, a, uriInfo.getRequestUri());
        return a;
    }

    @Override
    public Application getApplication(UriInfo info, AbstractResource resource, String path) {
        ApplicationDescription description = this.getApplication(info);
        WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator();
        Application a = path == null ? new WadlBuilder(wadlGenerator).generate(this.providers, this.fap, info, description, resource) : new WadlBuilder(wadlGenerator).generate(this.providers, this.fap, info, description, resource, path);
        for (Resources resources : a.getResources()) {
            resources.setBase(info.getBaseUri().toString());
        }
        this.attachExternalGrammar(a, description, info.getRequestUri());
        for (Resources resources : a.getResources()) {
            Resource r = resources.getResource().get(0);
            r.setPath(info.getBaseUri().relativize(info.getAbsolutePath()).toString());
            r.getParam().clear();
        }
        return a;
    }

    @Override
    public JAXBContext getJAXBContext() {
        return this.jaxbContext;
    }

    private WadlBuilder getWadlBuilder() {
        return this.wadlGenerationEnabled ? new WadlBuilder(this.wadlGeneratorConfig.createWadlGenerator()) : null;
    }

    @Override
    public void setWadlGenerationEnabled(boolean wadlGenerationEnabled) {
        this.wadlGenerationEnabled = wadlGenerationEnabled;
    }

    @Override
    public boolean isWadlGenerationEnabled() {
        return this.wadlGenerationEnabled;
    }

    private void attachExternalGrammar(Application application, ApplicationDescription applicationDescription, URI requestURI) {
        Grammars grammars;
        URI rootURI;
        String root;
        String requestURIPath = requestURI.getPath();
        if (requestURIPath.endsWith("application.wadl")) {
            requestURI = UriBuilder.fromUri(requestURI).replacePath(requestURIPath.substring(0, requestURIPath.lastIndexOf(47) + 1)).build(new Object[0]);
        }
        UriBuilder extendedPath = (root = application.getResources().get(0).getBase()) != null ? UriBuilder.fromPath(root).path("/application.wadl/") : UriBuilder.fromPath("./application.wadl/");
        URI uRI = rootURI = root != null ? UriBuilder.fromPath(root).build(new Object[0]) : null;
        if (application.getGrammars() != null) {
            LOG.info("The wadl application already contains a grammars element, we're adding elements of the provided grammars file.");
            grammars = application.getGrammars();
        } else {
            grammars = new Grammars();
            application.setGrammars(grammars);
        }
        for (String path : applicationDescription.getExternalMetadataKeys()) {
            ApplicationDescription.ExternalGrammar eg = applicationDescription.getExternalGrammar(path);
            if (!eg.isIncludedInGrammar()) continue;
            URI schemaURI = extendedPath.clone().path(path).build(new Object[0]);
            String schemaURIS = schemaURI.toString();
            String requestURIs = requestURI.toString();
            String schemaPath = rootURI != null ? requestURI.relativize(schemaURI).toString() : schemaURI.toString();
            Include include = new Include();
            include.setHref(schemaPath);
            Doc doc = new Doc();
            doc.setLang("en");
            doc.setTitle("Generated");
            include.getDoc().add(doc);
            grammars.getInclude().add(include);
        }
    }
}

