/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Application
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.parsers.XmlDescriptorParser
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.io.IOUtils
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform;

import com.atlassian.plugin.Application;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.osgi.factory.transform.JarUtils;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.model.ComponentImport;
import com.atlassian.plugin.osgi.factory.transform.model.SystemExports;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.parsers.XmlDescriptorParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransformContext {
    private final Manifest manifest;
    private final List<HostComponentRegistration> regs;
    private final Map<String, byte[]> fileOverrides;
    private final Map<String, String> bndInstructions;
    private final Document descriptorDocument;
    private final List<String> extraImports;
    private final List<String> extraExports;
    private final Set<String> bundleClassPathJars;
    private final PluginArtifact pluginArtifact;
    private final Map<String, ComponentImport> componentImports;
    private final SystemExports systemExports;
    private final Set<Application> applications;
    private boolean shouldRequireSpring = false;
    private final OsgiContainerManager osgiContainerManager;
    private final Set<HostComponentRegistration> requiredHostComponents;
    private static final Logger LOG = LoggerFactory.getLogger(TransformContext.class);
    private final Map<String, String> beanSourceMap = new LinkedHashMap<String, String>();

    public TransformContext(List<HostComponentRegistration> regs, SystemExports systemExports, PluginArtifact pluginArtifact, Set<Application> applications, String descriptorPath, OsgiContainerManager osgiContainerManager) {
        this.regs = regs;
        this.systemExports = (SystemExports)Preconditions.checkNotNull((Object)systemExports);
        this.osgiContainerManager = (OsgiContainerManager)Preconditions.checkNotNull((Object)osgiContainerManager);
        this.pluginArtifact = (PluginArtifact)Preconditions.checkNotNull((Object)pluginArtifact);
        this.applications = applications == null ? ImmutableSet.of() : applications;
        this.manifest = JarUtils.getManifest(pluginArtifact.toFile());
        this.fileOverrides = new LinkedHashMap<String, byte[]>();
        this.bndInstructions = new LinkedHashMap<String, String>();
        this.descriptorDocument = TransformContext.retrieveDocFromJar(pluginArtifact, (String)Preconditions.checkNotNull((Object)descriptorPath));
        this.extraImports = new ArrayList<String>();
        this.extraExports = new ArrayList<String>();
        this.bundleClassPathJars = new LinkedHashSet<String>();
        this.componentImports = TransformContext.parseComponentImports(this.descriptorDocument);
        this.requiredHostComponents = new LinkedHashSet<HostComponentRegistration>();
    }

    public File getPluginFile() {
        return this.pluginArtifact.toFile();
    }

    public PluginArtifact getPluginArtifact() {
        return this.pluginArtifact;
    }

    public List<HostComponentRegistration> getHostComponentRegistrations() {
        return this.regs;
    }

    public Map<String, byte[]> getFileOverrides() {
        return this.fileOverrides;
    }

    public Map<String, String> getBndInstructions() {
        return this.bndInstructions;
    }

    public Document getDescriptorDocument() {
        return this.descriptorDocument;
    }

    public Manifest getManifest() {
        return this.manifest;
    }

    public List<String> getExtraImports() {
        return this.extraImports;
    }

    public List<String> getExtraExports() {
        return this.extraExports;
    }

    public void addBundleClasspathJar(String classpath) {
        this.bundleClassPathJars.add(classpath);
    }

    public Set<String> getBundleClassPathJars() {
        return Collections.unmodifiableSet(this.bundleClassPathJars);
    }

    public Map<String, ComponentImport> getComponentImports() {
        return this.componentImports;
    }

    public SystemExports getSystemExports() {
        return this.systemExports;
    }

    public Set<Application> getApplications() {
        return ImmutableSet.copyOf(this.applications);
    }

    public boolean shouldRequireSpring() {
        return this.shouldRequireSpring;
    }

    public void setShouldRequireSpring(boolean shouldRequireSpring) {
        this.shouldRequireSpring = shouldRequireSpring;
        if (shouldRequireSpring) {
            this.getFileOverrides().put("META-INF/spring/", new byte[0]);
        }
    }

    public OsgiContainerManager getOsgiContainerManager() {
        return this.osgiContainerManager;
    }

    public Iterable<JarEntry> getPluginJarEntries() {
        return JarUtils.getEntries(this.pluginArtifact.toFile());
    }

    public JarEntry getPluginJarEntry(String path) {
        return JarUtils.getEntry(this.pluginArtifact.toFile(), path);
    }

    public void addRequiredHostComponent(HostComponentRegistration hostComponent) {
        this.requiredHostComponents.add(hostComponent);
    }

    public Set<HostComponentRegistration> getRequiredHostComponents() {
        return this.requiredHostComponents;
    }

    public void trackBean(String name, String source) {
        Preconditions.checkNotNull((Object)name, (Object)"empty bean name");
        Preconditions.checkNotNull((Object)source, (Object)"source of bean is required");
        if (this.beanSourceMap.containsKey(name)) {
            String message = String.format("The bean identifier '%s' is used by two different beans from %s and %s.This is a bad practice and may not be supported in newer plugin framework version.", name, source, this.beanSourceMap.get(name));
            LOG.warn(message);
        }
        this.beanSourceMap.put(name, source);
    }

    public boolean beanExists(String name) {
        return this.beanSourceMap.containsKey(name);
    }

    private static Map<String, ComponentImport> parseComponentImports(Document descriptorDocument) {
        LinkedHashMap<String, ComponentImport> componentImports = new LinkedHashMap<String, ComponentImport>();
        List elements = descriptorDocument.getRootElement().elements("component-import");
        for (Element component : elements) {
            ComponentImport ci = new ComponentImport(component);
            componentImports.put(ci.getKey(), ci);
        }
        return ImmutableMap.copyOf(componentImports);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Document retrieveDocFromJar(PluginArtifact pluginArtifact, String descriptorPath) {
        InputStream stream = null;
        try {
            stream = pluginArtifact.getResourceAsStream(descriptorPath);
            if (stream == null) {
                throw new PluginTransformationException("Unable to access descriptor " + descriptorPath);
            }
            Document document = new DocumentExposingDescriptorParser(stream).getDocument();
            return document;
        }
        finally {
            IOUtils.closeQuietly((InputStream)stream);
        }
    }

    public InstallationMode getInstallationMode() {
        return JarUtils.hasManifestEntry(this.manifest, "Remote-Plugin") ? InstallationMode.REMOTE : InstallationMode.LOCAL;
    }

    private static class DocumentExposingDescriptorParser
    extends XmlDescriptorParser {
        DocumentExposingDescriptorParser(InputStream source) {
            super(source, (Set)ImmutableSet.of());
        }

        public Document getDocument() {
            return super.getDocument();
        }
    }
}

