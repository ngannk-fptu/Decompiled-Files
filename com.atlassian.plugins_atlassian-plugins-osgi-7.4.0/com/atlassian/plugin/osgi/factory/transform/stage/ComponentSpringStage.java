/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.util.PluginUtils
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Node
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.osgi.factory.transform.PluginTransformationException;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.stage.SpringHelper;
import com.atlassian.plugin.osgi.factory.transform.stage.TransformStageUtils;
import com.atlassian.plugin.util.PluginUtils;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarInputStream;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class ComponentSpringStage
implements TransformStage {
    private static final String SPRING_XML = "META-INF/spring/atlassian-plugins-components.xml";
    public static final String BEAN_SOURCE = "Plugin Component";
    private static final String ALIAS = "alias";
    private static final String INTERFACE = "interface";

    @Override
    public void execute(TransformContext context) {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML)) {
            Set<String> requiredInterfaces;
            Document springDoc = SpringHelper.createSpringDocument();
            Element root = springDoc.getRootElement();
            List elements = context.getDescriptorDocument().getRootElement().elements("component");
            ValidationPattern validation = ValidationPattern.createPattern().rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@key").withError("The key is required"), ValidationPattern.test((String)"@class").withError("The class is required"), ValidationPattern.test((String)"not(@public='true') or interface or @interface").withError("Interfaces must be declared for public components"), ValidationPattern.test((String)"not(service-properties) or count(service-properties/entry[@key and @value]) > 0").withError("The service-properties element must contain at least one entry element with key and value attributes")});
            LinkedHashSet<String> declaredInterfaces = new LinkedHashSet<String>();
            for (Element component : elements) {
                if (!PluginUtils.doesModuleElementApplyToApplication((Element)component, context.getApplications(), (InstallationMode)context.getInstallationMode())) continue;
                validation.evaluate((Node)component);
                String beanId = component.attributeValue("key");
                context.trackBean(beanId, BEAN_SOURCE);
                Element bean = root.addElement("beans:bean");
                bean.addAttribute("id", beanId);
                bean.addAttribute("autowire", "default");
                if (!StringUtils.isBlank((CharSequence)component.attributeValue(ALIAS))) {
                    Element alias = root.addElement("beans:alias");
                    alias.addAttribute("name", beanId);
                    alias.addAttribute(ALIAS, component.attributeValue(ALIAS));
                }
                ArrayList<String> interfaceNames = new ArrayList<String>();
                List compInterfaces = component.elements(INTERFACE);
                for (Element inf : compInterfaces) {
                    interfaceNames.add(inf.getTextTrim());
                }
                if (component.attributeValue(INTERFACE) != null) {
                    interfaceNames.add(component.attributeValue(INTERFACE));
                }
                bean.addAttribute("class", component.attributeValue("class"));
                if (!"true".equalsIgnoreCase(component.attributeValue("public"))) continue;
                Element osgiService = root.addElement("osgi:service");
                osgiService.addAttribute("id", component.attributeValue("key") + "_osgiService");
                osgiService.addAttribute("ref", component.attributeValue("key"));
                declaredInterfaces.addAll(interfaceNames);
                Element interfaces = osgiService.addElement("osgi:interfaces");
                for (String name : interfaceNames) {
                    this.ensureExported(name, context);
                    Element e = interfaces.addElement("beans:value");
                    e.setText(name);
                }
                Element svcprops = component.element("service-properties");
                if (svcprops == null) continue;
                Element targetSvcprops = osgiService.addElement("osgi:service-properties");
                for (Element prop : new ArrayList(svcprops.elements("entry"))) {
                    Element e = targetSvcprops.addElement("beans:entry");
                    e.addAttribute("key", prop.attributeValue("key"));
                    e.addAttribute("value", prop.attributeValue("value"));
                }
            }
            if (root.elements().size() > 0) {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(springDoc));
            }
            try {
                requiredInterfaces = this.calculateRequiredImports(context.getPluginFile(), declaredInterfaces, context.getBundleClassPathJars());
            }
            catch (PluginTransformationException e) {
                throw new PluginTransformationException("Error while calculating import manifest", e);
            }
            context.getExtraImports().addAll(TransformStageUtils.getPackageNames(requiredInterfaces));
        }
    }

    private void ensureExported(String className, TransformContext context) {
        String pkg = className.substring(0, className.lastIndexOf(46));
        if (!context.getExtraExports().contains(pkg)) {
            String fileName = className.replace('.', '/') + ".class";
            if (context.getPluginArtifact().doesResourceExist(fileName)) {
                context.getExtraExports().add(pkg);
            }
        }
    }

    private Set<String> calculateRequiredImports(File pluginFile, Set<String> declaredInterfaces, Set<String> innerJars) {
        if (declaredInterfaces.size() > 0) {
            Set<String> shallowMatches;
            try (FileInputStream fis = new FileInputStream(pluginFile);
                 JarInputStream jarStream = new JarInputStream(fis);){
                shallowMatches = TransformStageUtils.scanJarForItems(jarStream, declaredInterfaces, TransformStageUtils.JarEntryToClassName.INSTANCE);
            }
            catch (IOException ioe) {
                throw new PluginTransformationException("Error reading jar:" + pluginFile.getName(), ioe);
            }
            LinkedHashSet remainders = Sets.newLinkedHashSet((Iterable)Sets.difference(declaredInterfaces, shallowMatches));
            if (remainders.size() > 0 && innerJars.size() > 0) {
                remainders.removeAll(TransformStageUtils.scanInnerJars(pluginFile, innerJars, remainders));
            }
            return Collections.unmodifiableSet(remainders);
        }
        return Collections.emptySet();
    }
}

