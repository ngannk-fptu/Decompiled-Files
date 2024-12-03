/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.opensymphony.xwork2.config.ConfigurationManager
 *  com.opensymphony.xwork2.config.ContainerProvider
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.DOMWriter
 *  org.dom4j.tree.DefaultDocument
 */
package com.atlassian.confluence.plugin.descriptor;

import com.atlassian.confluence.event.events.plugin.XWorkStateChangeEvent;
import com.atlassian.confluence.impl.struts.MultipartUploadConfigurator;
import com.atlassian.confluence.impl.struts.PluginModuleXmlConfigurationProvider;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ContainerProvider;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMWriter;
import org.dom4j.tree.DefaultDocument;

public class StrutsModuleDescriptor
extends AbstractModuleDescriptor {
    private final EventPublisher eventPublisher;
    private final ConfigurationManager configurationManager;
    private PluginModuleXmlConfigurationProvider configProvider;
    private Set<Pattern> multipartAllowlistPatterns;
    private final MultipartUploadConfigurator multipartUploadConfigurator;

    public StrutsModuleDescriptor(ModuleFactory moduleFactory, EventPublisher eventPublisher, ConfigurationManager configurationManager, MultipartUploadConfigurator multipartUploadConfigurator) {
        super(moduleFactory);
        this.eventPublisher = eventPublisher;
        this.configurationManager = configurationManager;
        this.multipartUploadConfigurator = multipartUploadConfigurator;
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.multipartAllowlistPatterns = StrutsModuleDescriptor.parseChildrenPatterns(element.element("multipart-upload-allowlist"));
        this.configProvider = new PluginModuleXmlConfigurationProvider(StrutsModuleDescriptor.convertElement(element), plugin, this::disabled);
    }

    static org.w3c.dom.Document convertElement(Element element) {
        Element clonedElement = (Element)element.clone();
        clonedElement.elements().stream().filter(child -> !child.getName().equals("package")).forEach(Node::detach);
        try {
            return new DOMWriter().write((Document)new DefaultDocument(clonedElement));
        }
        catch (DocumentException e) {
            throw new PluginParseException((Throwable)e);
        }
    }

    public static Set<Pattern> parseChildrenPatterns(Element element) {
        if (element == null) {
            return null;
        }
        List regexExpElements = element.elements("regex");
        HashSet<Pattern> set = new HashSet<Pattern>();
        for (Element regExpEl : regexExpElements) {
            String regExpText = regExpEl.getText();
            try {
                set.add(Pattern.compile(regExpText));
            }
            catch (PatternSyntaxException e) {
                throw new PluginParseException("Multipart allowlist RegEx entry could not be parsed: " + regExpText, (Throwable)e);
            }
        }
        return set;
    }

    public void enabled() {
        if (this.isEnabled()) {
            return;
        }
        super.enabled();
        if (this.multipartAllowlistPatterns != null) {
            this.multipartUploadConfigurator.registerPluginPatterns(this.getCompleteKey(), this.multipartAllowlistPatterns);
        }
        this.configurationManager.addContainerProvider((ContainerProvider)this.configProvider);
        this.eventPublisher.publish((Object)new XWorkStateChangeEvent((Object)this));
    }

    public void disabled() {
        if (!this.isEnabled()) {
            return;
        }
        super.disabled();
        if (this.multipartAllowlistPatterns != null) {
            this.multipartUploadConfigurator.clearPluginPatterns(this.getCompleteKey());
        }
        this.configurationManager.removeContainerProvider((ContainerProvider)this.configProvider);
        this.eventPublisher.publish((Object)new XWorkStateChangeEvent((Object)this));
    }

    public Object getModule() {
        return null;
    }
}

