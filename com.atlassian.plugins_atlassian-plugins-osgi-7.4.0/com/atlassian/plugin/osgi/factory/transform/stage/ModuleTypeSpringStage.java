/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.InstallationMode
 *  com.atlassian.plugin.util.PluginUtils
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Node
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.InstallationMode;
import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import com.atlassian.plugin.osgi.factory.transform.stage.SpringHelper;
import com.atlassian.plugin.util.PluginUtils;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class ModuleTypeSpringStage
implements TransformStage {
    private static final String SPRING_XML = "META-INF/spring/atlassian-plugins-module-types.xml";
    static final String HOST_CONTAINER = "springHostContainer";
    static final String SPRING_HOST_CONTAINER = "com.atlassian.plugin.osgi.bridge.external.SpringHostContainer";
    public static final String BEAN_SOURCE = "Module Type";
    private static final String CLASS = "class";
    private static final String BEANS_CONSTRUCTOR_ARG = "beans:constructor-arg";
    private static final String INDEX = "index";

    @Override
    public void execute(TransformContext context) {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML)) {
            Document doc = SpringHelper.createSpringDocument();
            Element root = doc.getRootElement();
            List elements = context.getDescriptorDocument().getRootElement().elements("module-type");
            if (elements.size() > 0) {
                context.getExtraImports().add("com.atlassian.plugin.osgi.external");
                context.getExtraImports().add("com.atlassian.plugin.osgi.bridge.external");
                context.getExtraImports().add("com.atlassian.plugin");
                Element hostContainerBean = root.addElement("beans:bean");
                context.trackBean(HOST_CONTAINER, BEAN_SOURCE);
                hostContainerBean.addAttribute("id", HOST_CONTAINER);
                hostContainerBean.addAttribute(CLASS, SPRING_HOST_CONTAINER);
                ValidationPattern validation = ValidationPattern.createPattern().rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@key").withError("The key is required"), ValidationPattern.test((String)"@class").withError("The class is required")});
                for (Element e : elements) {
                    if (!PluginUtils.doesModuleElementApplyToApplication((Element)e, context.getApplications(), (InstallationMode)context.getInstallationMode())) continue;
                    validation.evaluate((Node)e);
                    Element bean = root.addElement("beans:bean");
                    String beanId = this.getBeanId(e);
                    context.trackBean(beanId, BEAN_SOURCE);
                    bean.addAttribute("id", beanId);
                    bean.addAttribute(CLASS, "com.atlassian.plugin.osgi.external.SingleModuleDescriptorFactory");
                    Element arg = bean.addElement(BEANS_CONSTRUCTOR_ARG);
                    arg.addAttribute(INDEX, "0");
                    arg.addAttribute("ref", HOST_CONTAINER);
                    Element arg2 = bean.addElement(BEANS_CONSTRUCTOR_ARG);
                    arg2.addAttribute(INDEX, "1");
                    Element value2 = arg2.addElement("beans:value");
                    value2.setText(e.attributeValue("key"));
                    Element arg3 = bean.addElement(BEANS_CONSTRUCTOR_ARG);
                    arg3.addAttribute(INDEX, "2");
                    Element value3 = arg3.addElement("beans:value");
                    value3.setText(e.attributeValue(CLASS));
                    Element osgiService = root.addElement("osgi:service");
                    String serviceBeanId = this.getBeanId(e) + "_osgiService";
                    context.trackBean(serviceBeanId, BEAN_SOURCE);
                    osgiService.addAttribute("id", serviceBeanId);
                    osgiService.addAttribute("ref", beanId);
                    osgiService.addAttribute("auto-export", "interfaces");
                }
            }
            if (root.elements().size() > 0) {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(doc));
            }
        }
    }

    private String getBeanId(Element e) {
        return "moduleType-" + e.attributeValue("key");
    }
}

