/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.osgi.Builder
 *  aQute.bnd.osgi.Jar
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.parsers.XmlDescriptorParser
 *  com.atlassian.plugin.util.PluginUtils
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Version
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Jar;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.model.SystemExports;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.parsers.XmlDescriptorParser;
import com.atlassian.plugin.util.PluginUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateManifestStage
implements TransformStage {
    private static final Logger log = LoggerFactory.getLogger(GenerateManifestStage.class);
    public static final String SPRING_CONTEXT = "Spring-Context";
    private static final String SPRING_CONTEXT_TIMEOUT = "timeout:=";
    private static final String SPRING_CONTEXT_DELIM = ";";
    private static final String RESOLUTION_DIRECTIVE = "resolution:";
    private static final String EXCLUDE_PLUGIN_XML = "!atlassian-plugin.xml";
    private static final String OPTIONAL_CATCHALL_KEY = "*";
    private static final Map<String, String> OPTIONAL_CATCHALL_VALUE = ImmutableMap.of((Object)"resolution:", (Object)"optional");

    @Override
    public void execute(TransformContext context) {
        Joiner joiner = Joiner.on((String)",").skipNulls();
        try (Builder builder = new Builder();){
            Manifest mf;
            builder.setJar(context.getPluginFile());
            XmlDescriptorParser parser = new XmlDescriptorParser(context.getDescriptorDocument(), (Set)ImmutableSet.of());
            Manifest contextManifest = context.getManifest();
            if (this.isOsgiBundle(contextManifest)) {
                if (context.getExtraImports().isEmpty()) {
                    boolean modified = false;
                    Manifest mf2 = builder.getJar().getManifest();
                    for (Map.Entry<String, String> entry : this.getRequiredOsgiHeaders(context, parser.getKey()).entrySet()) {
                        if (!this.manifestDoesntHaveRequiredOsgiHeader(mf2, entry)) continue;
                        mf2.getMainAttributes().putValue(entry.getKey(), entry.getValue());
                        modified = true;
                    }
                    this.validateOsgiVersionIsValid(mf2);
                    if (modified) {
                        this.writeManifestOverride(context, mf2);
                    }
                    return;
                }
                this.assertSpringAvailableIfRequired(context);
                mf = builder.getJar().getManifest();
                Map<String, Map<String, String>> importsByPackage = this.addExtraImports(builder.getJar().getManifest().getMainAttributes().getValue("Import-Package"), context.getExtraImports());
                importsByPackage = OsgiHeaderUtil.stripDuplicatePackages(importsByPackage, parser.getKey(), "import");
                String imports = OsgiHeaderUtil.buildHeader(importsByPackage);
                mf.getMainAttributes().putValue("Import-Package", imports);
                for (Map.Entry<String, String> entry : this.getRequiredOsgiHeaders(context, parser.getKey()).entrySet()) {
                    mf.getMainAttributes().putValue(entry.getKey(), entry.getValue());
                }
            } else {
                PluginInformation info = parser.getPluginInformation();
                Properties properties = new Properties();
                for (Map.Entry<String, String> entry : this.getRequiredOsgiHeaders(context, parser.getKey()).entrySet()) {
                    properties.put(entry.getKey(), entry.getValue());
                }
                Set scanFolders = info.getModuleScanFolders();
                if (!scanFolders.isEmpty()) {
                    properties.put("Atlassian-Scan-Folders", StringUtils.join((Iterable)scanFolders, (String)","));
                }
                properties.put("Bundle-SymbolicName", parser.getKey());
                properties.put("Bundle-Version", info.getVersion());
                properties.put("-noee", "true");
                properties.put("-removeheaders", "Include-Resource");
                GenerateManifestStage.header(properties, "Bundle-Description", info.getDescription());
                GenerateManifestStage.header(properties, "Bundle-Name", parser.getKey());
                GenerateManifestStage.header(properties, "Bundle-Vendor", info.getVendorName());
                GenerateManifestStage.header(properties, "Bundle-DocURL", info.getVendorUrl());
                ArrayList<String> bundleClassPaths = new ArrayList<String>();
                bundleClassPaths.add(".");
                ArrayList<String> innerClassPaths = new ArrayList<String>(context.getBundleClassPathJars());
                Collections.sort(innerClassPaths);
                bundleClassPaths.addAll(innerClassPaths);
                GenerateManifestStage.header(properties, "Bundle-ClassPath", StringUtils.join(bundleClassPaths, (char)','));
                properties.putAll(context.getBndInstructions());
                Map<String, Map<String, String>> importsByPackage = this.addExtraImports(properties.getProperty("Import-Package"), context.getExtraImports());
                importsByPackage = OsgiHeaderUtil.moveStarPackageToEnd(importsByPackage, parser.getKey());
                if (!importsByPackage.containsKey(OPTIONAL_CATCHALL_KEY)) {
                    importsByPackage.put(OPTIONAL_CATCHALL_KEY, OPTIONAL_CATCHALL_VALUE);
                }
                importsByPackage = OsgiHeaderUtil.stripDuplicatePackages(importsByPackage, parser.getKey(), "import");
                String imports = OsgiHeaderUtil.buildHeader(importsByPackage);
                properties.put("Import-Package", imports);
                if (!properties.containsKey("Export-Package")) {
                    properties.put("Export-Package", StringUtils.join(context.getExtraExports(), (char)','));
                }
                properties.put("Export-Package", joiner.join((Object)EXCLUDE_PLUGIN_XML, (Object)properties.getProperty("Export-Package"), new Object[0]));
                builder.setProperties(properties);
                builder.calcManifest();
                Jar jar = builder.build();
                Object object = null;
                try {
                    mf = jar.getManifest();
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (jar != null) {
                        if (object != null) {
                            try {
                                jar.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            jar.close();
                        }
                    }
                }
                Attributes attributes = mf.getMainAttributes();
                for (Map.Entry entry : contextManifest.getMainAttributes().entrySet()) {
                    Object name = entry.getKey();
                    if (attributes.containsKey(name)) {
                        log.debug("Ignoring manifest header {} from {} due to transformer override", name, (Object)context.getPluginArtifact());
                        continue;
                    }
                    attributes.put(name, entry.getValue());
                }
            }
            this.enforceHostVersionsForUnknownImports(mf, context.getSystemExports());
            this.validateOsgiVersionIsValid(mf);
            this.writeManifestOverride(context, mf);
        }
        catch (Exception t) {
            throw new PluginParseException("Unable to process plugin to generate OSGi manifest", (Throwable)t);
        }
    }

    private Map<String, String> getRequiredOsgiHeaders(TransformContext context, String pluginKey) {
        LinkedHashMap<String, String> props = new LinkedHashMap<String, String>();
        props.put("Atlassian-Plugin-Key", pluginKey);
        String springHeader = this.getDesiredSpringContextValue(context);
        if (springHeader != null) {
            props.put(SPRING_CONTEXT, springHeader);
        }
        return props;
    }

    private String getDesiredSpringContextValue(TransformContext context) {
        String header = context.getManifest().getMainAttributes().getValue(SPRING_CONTEXT);
        if (header != null) {
            return this.ensureDefaultTimeout(header);
        }
        if (context.getPluginArtifact().doesResourceExist("META-INF/spring/") || context.shouldRequireSpring() || context.getDescriptorDocument() != null) {
            return "*;timeout:=" + PluginUtils.getDefaultEnablingWaitPeriod();
        }
        return null;
    }

    private String ensureDefaultTimeout(String header) {
        StringBuilder headerBuf;
        boolean noTimeOutSpecified = StringUtils.isEmpty((CharSequence)System.getProperty("atlassian.plugins.enable.wait"));
        if (noTimeOutSpecified) {
            return header;
        }
        if (header.contains(SPRING_CONTEXT_TIMEOUT)) {
            StringTokenizer tokenizer = new StringTokenizer(header, SPRING_CONTEXT_DELIM);
            headerBuf = new StringBuilder();
            while (tokenizer.hasMoreElements()) {
                String directive = (String)tokenizer.nextElement();
                if (directive.startsWith(SPRING_CONTEXT_TIMEOUT)) {
                    if (!directive.equals("timeout:=300")) {
                        log.debug("Overriding configured timeout {} seconds", (Object)directive.substring(SPRING_CONTEXT_TIMEOUT.length()));
                    }
                    directive = SPRING_CONTEXT_TIMEOUT + PluginUtils.getDefaultEnablingWaitPeriod();
                }
                headerBuf.append(directive);
                if (!tokenizer.hasMoreElements()) continue;
                headerBuf.append(SPRING_CONTEXT_DELIM);
            }
        } else {
            headerBuf = new StringBuilder(header);
            headerBuf.append(";timeout:=");
            headerBuf.append(PluginUtils.getDefaultEnablingWaitPeriod());
        }
        return headerBuf.toString();
    }

    private void validateOsgiVersionIsValid(Manifest mf) {
        String version = mf.getMainAttributes().getValue("Bundle-Version");
        try {
            if (Version.parseVersion((String)version) == Version.emptyVersion) {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Plugin version '" + version + "' is required and must be able to be parsed as an OSGi version - MAJOR.MINOR.MICRO.QUALIFIER");
        }
    }

    private void writeManifestOverride(TransformContext context, Manifest mf) throws IOException {
        Attributes.Name lastModifiedKey = new Attributes.Name("Bnd-LastModified");
        mf.getMainAttributes().remove(lastModifiedKey);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        mf.write(bout);
        context.getFileOverrides().put("META-INF/MANIFEST.MF", bout.toByteArray());
    }

    private void enforceHostVersionsForUnknownImports(Manifest manifest, SystemExports exports) {
        String origImports = manifest.getMainAttributes().getValue("Import-Package");
        if (origImports != null) {
            StringBuilder imports = new StringBuilder();
            Map<String, Map<String, String>> header = OsgiHeaderUtil.parseHeader(origImports);
            for (Map.Entry<String, Map<String, String>> pkgImport : header.entrySet()) {
                String export;
                String imp = null;
                if (pkgImport.getValue().isEmpty() && !(export = exports.getFullExport(pkgImport.getKey())).equals(imp)) {
                    imp = export;
                }
                if (imp == null) {
                    imp = OsgiHeaderUtil.buildHeader(pkgImport.getKey(), pkgImport.getValue());
                }
                imports.append(imp);
                imports.append(",");
            }
            if (imports.length() > 0) {
                imports.deleteCharAt(imports.length() - 1);
            }
            manifest.getMainAttributes().putValue("Import-Package", imports.toString());
        }
    }

    private boolean isOsgiBundle(Manifest manifest) {
        return manifest.getMainAttributes().getValue("Bundle-SymbolicName") != null;
    }

    private Map<String, Map<String, String>> addExtraImports(String importsLine, List<String> extraImports) {
        Map<String, Map<String, String>> imports = OsgiHeaderUtil.parseHeader(importsLine);
        for (String exImport : extraImports) {
            if (exImport.startsWith("java.")) continue;
            String extraImportPackage = StringUtils.split((String)exImport, (char)';')[0];
            Map<String, String> attrs = imports.get(extraImportPackage);
            if (attrs != null) {
                String resolution = attrs.get(RESOLUTION_DIRECTIVE);
                if (!"optional".equals(resolution)) continue;
                attrs.put(RESOLUTION_DIRECTIVE, "mandatory");
                continue;
            }
            imports.put(exImport, Collections.emptyMap());
        }
        return imports;
    }

    private boolean manifestDoesntHaveRequiredOsgiHeader(Manifest mf, Map.Entry<String, String> entry) {
        if (mf.getMainAttributes().containsKey(new Attributes.Name(entry.getKey()))) {
            return !entry.getValue().equals(mf.getMainAttributes().getValue(entry.getKey()));
        }
        return true;
    }

    private static void header(Properties properties, String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Collection && ((Collection)value).isEmpty()) {
            return;
        }
        properties.put(key, value.toString().replaceAll("[\r\n]", ""));
    }

    private void assertSpringAvailableIfRequired(TransformContext context) {
        if (PluginUtils.isAtlassianDevMode() && context.shouldRequireSpring()) {
            String header = context.getManifest().getMainAttributes().getValue(SPRING_CONTEXT);
            if (header == null) {
                log.debug("Manifest has no 'Spring-Context:' header. Prefer the header 'Spring-Context: *' in the jar '{}'.", (Object)context.getPluginArtifact());
            } else if (header.contains(";timeout:=")) {
                log.warn("Manifest contains a 'Spring-Context:' header with a timeout, namely '{}'. This can cause problems as the timeout is server specific. Use the header 'Spring-Context: *' in the jar '{}'.", (Object)header, (Object)context.getPluginArtifact());
            }
        }
    }
}

