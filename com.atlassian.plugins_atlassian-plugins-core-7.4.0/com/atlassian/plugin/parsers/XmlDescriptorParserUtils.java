/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.ModuleDescriptorFactory
 *  com.atlassian.plugin.Plugin
 *  org.dom4j.Attribute
 *  org.dom4j.Document
 *  org.dom4j.Element
 *  org.dom4j.Namespace
 *  org.dom4j.Visitor
 *  org.dom4j.VisitorSupport
 *  org.dom4j.tree.DefaultElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.ModuleDescriptorFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.atlassian.plugin.descriptors.UnrecognisedModuleDescriptorFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XmlDescriptorParserUtils {
    private static final Logger log = LoggerFactory.getLogger(XmlDescriptorParserUtils.class);

    public static Document removeAllNamespaces(Document doc) {
        doc.accept((Visitor)new NamespaceCleaner());
        return doc;
    }

    public static ModuleDescriptor<?> addModule(ModuleDescriptorFactory moduleDescriptorFactory, Plugin plugin, Element module) {
        ModuleDescriptor<?> moduleDescriptor = XmlDescriptorParserUtils.newModuleDescriptor(plugin, module, moduleDescriptorFactory);
        moduleDescriptor.init(plugin, module);
        return moduleDescriptor;
    }

    static ModuleDescriptor<?> newModuleDescriptor(Plugin plugin, Element element, ModuleDescriptorFactory moduleDescriptorFactory) {
        ModuleDescriptor moduleDescriptor;
        String name = element.getName();
        try {
            moduleDescriptor = moduleDescriptorFactory.getModuleDescriptor(name);
        }
        catch (Throwable e) {
            UnrecognisedModuleDescriptor unrecognisedModuleDescriptor = UnrecognisedModuleDescriptorFactory.createUnrecognisedModuleDescriptor(plugin, element, e, moduleDescriptorFactory);
            log.error("There were problems loading the module '{}' in plugin '{}'. The module has been disabled.", (Object)name, (Object)plugin.getName());
            log.error(unrecognisedModuleDescriptor.getErrorText(), e);
            return unrecognisedModuleDescriptor;
        }
        return moduleDescriptor;
    }

    private static final class NamespaceCleaner
    extends VisitorSupport {
        private NamespaceCleaner() {
        }

        public void visit(Document document) {
            ((DefaultElement)document.getRootElement()).setNamespace(Namespace.NO_NAMESPACE);
            document.getRootElement().additionalNamespaces().clear();
        }

        public void visit(Namespace namespace) {
            namespace.detach();
        }

        public void visit(Attribute node) {
            if (node.toString().contains("xmlns") || node.toString().contains("xsi:")) {
                node.detach();
            }
        }

        public void visit(Element node) {
            if (node instanceof DefaultElement) {
                ((DefaultElement)node).setNamespace(Namespace.NO_NAMESPACE);
            }
        }
    }
}

