/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.util.PluginUtils
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.model.ComponentImport;
import com.atlassian.plugin.osgi.factory.transform.stage.SpringHelper;
import com.atlassian.plugin.util.PluginUtils;
import java.util.Arrays;
import org.dom4j.Document;
import org.dom4j.Element;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentImportSpringStage
implements TransformStage {
    private static final String SPRING_XML = "META-INF/spring/atlassian-plugins-component-imports.xml";
    public static final String BEAN_SOURCE = "Component Import";
    static Logger log = LoggerFactory.getLogger(ComponentImportSpringStage.class);

    @Override
    public void execute(TransformContext context) {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML)) {
            Document springDoc = SpringHelper.createSpringDocument();
            Element root = springDoc.getRootElement();
            ServiceReference[] serviceReferences = context.getOsgiContainerManager().getRegisteredServices();
            for (ComponentImport comp : context.getComponentImports().values()) {
                if (!PluginUtils.doesModuleElementApplyToApplication((Element)comp.getSource(), context.getApplications(), (InstallationMode)context.getInstallationMode())) continue;
                Element osgiReference = root.addElement("osgi:reference");
                context.trackBean(comp.getKey(), BEAN_SOURCE);
                osgiReference.addAttribute("id", comp.getKey());
                if (comp.getFilter() != null) {
                    osgiReference.addAttribute("filter", comp.getFilter());
                }
                Element interfaces = osgiReference.addElement("osgi:interfaces");
                for (String infName : comp.getInterfaces()) {
                    this.validateInterface(infName, context.getPluginFile().getName(), serviceReferences);
                    context.getExtraImports().add(infName.substring(0, infName.lastIndexOf(46)));
                    Element e = interfaces.addElement("beans:value");
                    e.setText(infName);
                }
            }
            if (!root.elements().isEmpty()) {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(springDoc));
            }
        }
    }

    private void validateInterface(String interfaceName, String pluginName, ServiceReference[] serviceReferences) {
        if (log.isDebugEnabled() && !this.findInterface(interfaceName, serviceReferences)) {
            log.debug("Couldn't confirm that '{}' (used as a <component-import> in the plugin with name '{}') is a public component in the product's OSGi exports. If this is an interface you expect to be provided from the product, double check the spelling of '{}'; if this class is supposed to come from another plugin, you can probably ignore this warning.", new Object[]{interfaceName, pluginName, interfaceName});
        }
    }

    private boolean findInterface(String interfaceName, ServiceReference[] serviceReferences) {
        return Arrays.stream(serviceReferences).flatMap(ref -> Arrays.stream((String[])ref.getProperty("objectClass"))).anyMatch(interfaceName::equals);
    }
}

