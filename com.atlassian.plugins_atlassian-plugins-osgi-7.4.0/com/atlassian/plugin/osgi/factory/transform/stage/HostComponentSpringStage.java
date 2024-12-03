/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.osgi.Analyzer
 *  aQute.bnd.osgi.Clazz
 *  aQute.bnd.osgi.Resource
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.atlassian.plugin.util.PluginUtils
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Resource;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.model.ComponentImport;
import com.atlassian.plugin.osgi.factory.transform.model.SystemExports;
import com.atlassian.plugin.osgi.factory.transform.stage.SpringHelper;
import com.atlassian.plugin.osgi.factory.transform.stage.TransformStageUtils;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.osgi.util.ClassBinaryScanner;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.atlassian.plugin.util.PluginUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostComponentSpringStage
implements TransformStage {
    private static final Logger log = LoggerFactory.getLogger(HostComponentSpringStage.class);
    private static final String SPRING_XML = "META-INF/spring/atlassian-plugins-host-components.xml";
    public static final String BEAN_SOURCE = "Host Component";
    private static final String CLASS_FILE_EXTENSION = ".class";
    public static final String BEANS_PROPERTY = "beans:property";

    @Override
    public void execute(TransformContext context) {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML)) {
            Document doc = SpringHelper.createSpringDocument();
            Set<String> hostComponentInterfaceNames = this.convertRegistrationsToSet(context.getHostComponentRegistrations());
            Set<String> matchedInterfaceNames = context.getRequiredHostComponents().stream().flatMap(reg -> Arrays.stream(reg.getMainInterfaces())).collect(Collectors.toSet());
            List<String> innerJarPaths = this.findJarPaths(context.getManifest());
            try (FileInputStream pluginStream = new FileInputStream(context.getPluginFile());){
                this.findUsedHostComponents(hostComponentInterfaceNames, matchedInterfaceNames, innerJarPaths, pluginStream);
            }
            catch (IOException e) {
                throw new PluginParseException("Unable to scan for host components in plugin classes", (Throwable)e);
            }
            ArrayList<HostComponentRegistration> matchedRegistrations = new ArrayList<HostComponentRegistration>();
            Element root = doc.getRootElement();
            TreeMap<String, Element> generatedBeans = new TreeMap<String, Element>();
            if (context.getHostComponentRegistrations() != null) {
                int index = -1;
                for (HostComponentRegistration reg2 : context.getHostComponentRegistrations()) {
                    ++index;
                    boolean found = false;
                    for (String name : reg2.getMainInterfaces()) {
                        if (!matchedInterfaceNames.contains(name) && !this.isRequiredHostComponent(context, name)) continue;
                        found = true;
                    }
                    LinkedHashSet<String> regInterfaces = new LinkedHashSet<String>(Arrays.asList(reg2.getMainInterfaces()));
                    for (ComponentImport compImport : context.getComponentImports().values()) {
                        if (!PluginUtils.doesModuleElementApplyToApplication((Element)compImport.getSource(), context.getApplications(), (InstallationMode)context.getInstallationMode()) || !regInterfaces.containsAll(compImport.getInterfaces())) continue;
                        found = false;
                        break;
                    }
                    if (!found) continue;
                    matchedRegistrations.add(reg2);
                    String beanName = reg2.getProperties().get("bean-name");
                    Element osgiService = root.addElement("beans:bean");
                    String beanId = this.determineId(context.getComponentImports().keySet(), beanName, index);
                    context.trackBean(beanId, BEAN_SOURCE);
                    osgiService.addAttribute("id", beanId);
                    osgiService.addAttribute("lazy-init", "true");
                    osgiService.addAttribute("class", "com.atlassian.plugin.osgi.bridge.external.HostComponentFactoryBean");
                    context.getExtraImports().add("com.atlassian.plugin.osgi.bridge.external");
                    Element e = osgiService.addElement(BEANS_PROPERTY);
                    e.addAttribute("name", "filter");
                    e.addAttribute("value", "(&(bean-name=" + beanName + ")(" + "plugins-host" + "=true))");
                    Element listProp = osgiService.addElement(BEANS_PROPERTY);
                    listProp.addAttribute("name", "interfaces");
                    Element list = listProp.addElement("beans:list");
                    for (String inf : reg2.getMainInterfaces()) {
                        if (!matchedInterfaceNames.contains(inf)) continue;
                        Element tmp = list.addElement("beans:value");
                        tmp.setText(inf);
                    }
                    Element bundleContextProp = osgiService.addElement(BEANS_PROPERTY);
                    bundleContextProp.addAttribute("name", "bundleContext");
                    bundleContextProp.addAttribute("ref", "bundleContext");
                    osgiService.detach();
                    generatedBeans.put(beanId, osgiService);
                }
            }
            for (Element generatedBean : generatedBeans.values()) {
                root.add(generatedBean);
            }
            this.addImportsForMatchedHostComponents(matchedInterfaceNames, matchedRegistrations, context.getSystemExports(), context.getExtraImports());
            if (!root.elements().isEmpty()) {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(doc));
            }
        }
    }

    private void addImportsForMatchedHostComponents(Set<String> matchedInterfaceNames, List<HostComponentRegistration> matchedRegistrations, SystemExports systemExports, List<String> extraImports) {
        try {
            Set<Class<?>> interfacesToScan = matchedRegistrations.stream().flatMap(reg -> Arrays.stream(reg.getMainInterfaceClasses())).filter(inf -> matchedInterfaceNames.contains(inf.getName())).collect(Collectors.toSet());
            Set<String> referredPackages = OsgiHeaderUtil.findReferredPackageNames(interfacesToScan);
            for (String pkg : referredPackages) {
                extraImports.add(systemExports.getFullExport(pkg));
            }
        }
        catch (IOException e) {
            throw new PluginTransformationException("Unable to scan for host component referred packages", e);
        }
    }

    private Set<String> convertRegistrationsToSet(List<HostComponentRegistration> regs) {
        TreeSet<String> interfaceNames = new TreeSet<String>();
        if (regs != null) {
            for (HostComponentRegistration reg : regs) {
                interfaceNames.addAll(Arrays.asList(reg.getMainInterfaces()));
            }
        }
        return interfaceNames;
    }

    /*
     * Unable to fully structure code
     */
    private void findUsedHostComponents(Set<String> allHostComponents, Set<String> matchedHostComponents, List<String> innerJarPaths, InputStream jarStream) throws IOException {
        entries = new LinkedHashSet<String>();
        superClassNames = new LinkedHashSet<String>();
        analyzer = new Analyzer();
        var8_8 = null;
        try {
            zin = new ZipInputStream(new BufferedInputStream(jarStream));
            var10_12 = null;
lbl8:
            // 2 sources

            try {
                while ((zipEntry = zin.getNextEntry()) != null) {
                    block27: {
                        path = zipEntry.getName();
                        if (!path.endsWith(".class")) break block27;
                        entries.add(path.substring(0, path.length() - ".class".length()));
                        cls = new Clazz(analyzer, path, (Resource)new ClassBinaryScanner.InputStreamResource(new BufferedInputStream(new UnclosableFilterInputStream(zin))));
                        scanResult = ClassBinaryScanner.scanClassBinary(cls);
                        superClassNames.add(scanResult.getSuperClass());
                        for (String ref : scanResult.getReferredClasses()) {
                            name = TransformStageUtils.jarPathToClassName(ref + ".class");
                            if (!allHostComponents.contains(name)) continue;
                            matchedHostComponents.add(name);
                        }
                        ** GOTO lbl8
                    }
                    if (!path.endsWith(".jar") || !innerJarPaths.contains(path)) continue;
                    this.findUsedHostComponents(allHostComponents, matchedHostComponents, Collections.emptyList(), new UnclosableFilterInputStream(zin));
                }
            }
            catch (Throwable var11_15) {
                var10_12 = var11_15;
                throw var11_15;
            }
            finally {
                if (zin != null) {
                    if (var10_12 != null) {
                        try {
                            zin.close();
                        }
                        catch (Throwable var11_14) {
                            var10_12.addSuppressed(var11_14);
                        }
                    } else {
                        zin.close();
                    }
                }
            }
        }
        catch (Throwable var9_11) {
            var8_8 = var9_11;
            throw var9_11;
        }
        finally {
            if (analyzer != null) {
                if (var8_8 != null) {
                    try {
                        analyzer.close();
                    }
                    catch (Throwable var9_10) {
                        var8_8.addSuppressed(var9_10);
                    }
                } else {
                    analyzer.close();
                }
            }
        }
        HostComponentSpringStage.addHostComponentsUsedInSuperClasses(allHostComponents, matchedHostComponents, entries, superClassNames);
    }

    @VisibleForTesting
    static void addHostComponentsUsedInSuperClasses(Set<String> allHostComponents, Set<String> matchedHostComponents, Set<String> entries, Set<String> superClassNames) {
        for (String superClassName : superClassNames) {
            String cls;
            if (superClassName == null || entries.contains(superClassName) || (cls = superClassName.replace('/', '.')).startsWith("java.") || cls.startsWith("javax.")) continue;
            try {
                Class spr = ClassLoaderUtils.loadClass((String)cls, HostComponentSpringStage.class);
                for (Method m : spr.getMethods()) {
                    for (Class<?> param : m.getParameterTypes()) {
                        if (!allHostComponents.contains(param.getName())) continue;
                        matchedHostComponents.add(param.getName());
                    }
                }
            }
            catch (ClassNotFoundException | NoClassDefFoundError throwable) {
            }
        }
    }

    private List<String> findJarPaths(Manifest mf) {
        ArrayList<String> paths = new ArrayList<String>();
        String cp = mf.getMainAttributes().getValue("Bundle-ClassPath");
        if (cp != null) {
            for (String entry : cp.split(",")) {
                if ((entry = entry.trim()).length() != 1 && entry.endsWith(".jar")) {
                    paths.add(entry);
                    continue;
                }
                if (".".equals(entry)) continue;
                log.warn("Non-jar classpath elements not supported: {}", (Object)entry);
            }
        }
        return paths;
    }

    private String determineId(Set<String> hostComponentNames, String beanName, int iteration) {
        String id = beanName;
        if (id == null) {
            id = "bean" + iteration;
        }
        if (hostComponentNames.contains(id = id.replaceAll("#", "LB"))) {
            id = id + iteration;
        }
        return id;
    }

    private boolean isRequiredHostComponent(TransformContext context, String name) {
        for (HostComponentRegistration registration : context.getRequiredHostComponents()) {
            if (!Arrays.asList(registration.getMainInterfaces()).contains(name)) continue;
            return true;
        }
        return false;
    }

    private static class UnclosableFilterInputStream
    extends FilterInputStream {
        public UnclosableFilterInputStream(InputStream delegate) {
            super(delegate);
        }

        @Override
        public void close() {
        }
    }
}

