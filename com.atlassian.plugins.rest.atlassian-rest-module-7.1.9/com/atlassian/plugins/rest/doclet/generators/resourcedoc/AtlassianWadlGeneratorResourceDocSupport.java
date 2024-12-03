/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.doclet.generators.resourcedoc;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugins.rest.common.version.ApiVersion;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AtlassianWadlGeneratorResourceDocSupport
extends WadlGeneratorResourceDocSupport {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private HashMap<String, ResourcePathInformation> resourcePathInformation;
    private static final Logger LOG = LoggerFactory.getLogger(AtlassianWadlGeneratorResourceDocSupport.class);
    private static final String ATLASSIAN_PLUGIN_XML = "atlassian-plugin.xml";
    private boolean generateSchemas = true;

    public AtlassianWadlGeneratorResourceDocSupport() {
    }

    public AtlassianWadlGeneratorResourceDocSupport(WadlGenerator wadlGenerator, ResourceDocType resourceDoc) {
        super(wadlGenerator, resourceDoc);
    }

    @Override
    public void init() throws Exception {
        super.init();
        this.parseAtlassianPluginXML();
    }

    public void setGenerateSchemas(Boolean generateSchemas) {
        this.generateSchemas = generateSchemas;
    }

    private void parseAtlassianPluginXML() {
        this.resourcePathInformation = new HashMap();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            String loadPath = System.getProperty("atlassian-plugin.xml.path", "target/classes");
            File loadDir = new File(loadPath);
            File atlassianPluginXMLFile = new File(loadDir, ATLASSIAN_PLUGIN_XML);
            if (atlassianPluginXMLFile.exists()) {
                this.parseAtlassianPluginXML(dbf, atlassianPluginXMLFile);
                return;
            }
            LOG.info("{} file not found in path {}", (Object)ATLASSIAN_PLUGIN_XML, (Object)loadDir.getAbsolutePath());
            Enumeration<URL> resources = this.loadClasspathPluginXML();
            if (!resources.hasMoreElements()) {
                LOG.info("{} file not found in classpath", (Object)ATLASSIAN_PLUGIN_XML);
                return;
            }
            this.parseAtlassianPluginXML(dbf, resources);
        }
        catch (Exception ex) {
            LOG.error("Failed to read {} and parse rest plugin module descriptor information", (Object)ATLASSIAN_PLUGIN_XML, (Object)ex);
        }
    }

    @Override
    public Resource createResource(AbstractResource r, String path) {
        this.removeMethodsExcludedFromDocs(r);
        if (this.allMethodsExcluded(r)) {
            return new Resource();
        }
        Resource result = super.createResource(r, path);
        boolean resourcePathChanged = false;
        for (String packageName : this.resourcePathInformation.keySet()) {
            if (!r.getResourceClass().getPackage().getName().startsWith(packageName)) continue;
            ResourcePathInformation pathInformation = this.resourcePathInformation.get(packageName);
            String newPath = this.buildResourcePath(result, pathInformation);
            result.setPath(newPath);
            resourcePathChanged = true;
            LOG.info("Setting resource path of rest end point '{}' to '{}'", (Object)r.getResourceClass().getCanonicalName(), (Object)newPath);
            break;
        }
        if (!resourcePathChanged) {
            LOG.info("Resource path of rest end point '{}' unchanged no mapping to rest plugin module descriptor found.", (Object)r.getResourceClass().getCanonicalName());
        }
        return result;
    }

    protected Enumeration<URL> loadClasspathPluginXML() throws IOException {
        return Thread.currentThread().getContextClassLoader().getResources(ATLASSIAN_PLUGIN_XML);
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

    private String buildResourcePath(Resource result, ResourcePathInformation pathInformation) {
        String resultPath = result.getPath();
        if (resultPath.startsWith("/")) {
            resultPath = resultPath.substring(1);
        }
        String version = pathInformation.getVersion();
        String path = pathInformation.getPath();
        if (ApiVersion.isNone(version)) {
            return path + "/" + resultPath;
        }
        if (resultPath.contains(path + "/" + version)) {
            return resultPath;
        }
        return path + "/" + version + "/" + resultPath;
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

    private void parseAtlassianPluginXML(DocumentBuilderFactory dbf, Enumeration<URL> resources) {
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            LOG.info("Looking for rest plugin module descriptors in {}", (Object)resource.toString());
            try {
                this.parseAtlassianPluginXML(dbf, resource.openStream());
            }
            catch (Exception e) {
                LOG.warn("Failed to read {}", (Object)resource.toString(), (Object)e);
            }
        }
    }

    private void parseAtlassianPluginXML(DocumentBuilderFactory dbf, File atlassianPluginXMLFile) throws ParserConfigurationException, SAXException, IOException {
        this.parseAtlassianPluginXML(dbf, new FileInputStream(atlassianPluginXMLFile));
    }

    private void parseAtlassianPluginXML(DocumentBuilderFactory dbf, InputStream atlassianPluginXml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(atlassianPluginXml);
        NodeList restPluginModuleDescriptors = document.getElementsByTagName("rest");
        int numPluginModuleDescriptors = restPluginModuleDescriptors.getLength();
        LOG.info("Found {} rest plugin module descriptors.", (Object)numPluginModuleDescriptors);
        for (int i = 0; i < numPluginModuleDescriptors; ++i) {
            Node node = restPluginModuleDescriptors.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Node pathItem = attributes.getNamedItem("path");
            Node versionItem = attributes.getNamedItem("version");
            if (pathItem == null || versionItem == null) continue;
            String resourcePath = pathItem.getNodeValue();
            String version = versionItem.getNodeValue();
            LOG.info("Found rest end point with path '{}' and version '{}'", (Object)resourcePath, (Object)version);
            if (resourcePath.startsWith("/")) {
                resourcePath = resourcePath.substring(1);
            }
            if (resourcePath.endsWith("/")) {
                resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
            }
            NodeList list = node.getChildNodes();
            for (int j = 0; j < list.getLength(); ++j) {
                Node child = list.item(j);
                if (!child.getNodeName().equals("package")) continue;
                String packageName = child.getFirstChild().getNodeValue();
                LOG.info("Map package '{}' to resource path '{}' and version '{}'", new Object[]{packageName, resourcePath, version});
                this.resourcePathInformation.put(packageName, new ResourcePathInformation(resourcePath, version));
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

    public class ResourcePathInformation {
        private final String path;
        private final String version;

        public ResourcePathInformation(String path, String version) {
            this.path = path;
            this.version = version;
        }

        public String getVersion() {
            return this.version;
        }

        public String getPath() {
            return this.path;
        }
    }
}

