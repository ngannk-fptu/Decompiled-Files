/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor
 *  com.atlassian.plugins.navlink.spi.weights.ApplicationWeights
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  org.dom4j.Element
 */
package com.atlassian.plugins.navlink.producer.navigation.plugin;

import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugins.navlink.producer.capabilities.services.ApplicationTypeService;
import com.atlassian.plugins.navlink.producer.navigation.links.LinkSource;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLink;
import com.atlassian.plugins.navlink.producer.navigation.services.RawNavigationLinkBuilder;
import com.atlassian.plugins.navlink.spi.weights.ApplicationWeights;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import javax.annotation.Nullable;
import org.dom4j.Element;

public class NavigationLinkModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<RawNavigationLink> {
    private final ApplicationWeights applicationWeights;
    private final ApplicationTypeService applicationTypeService;
    private volatile boolean enabled = false;

    public NavigationLinkModuleDescriptor(ModuleFactory moduleClassFactory, WebInterfaceManager webInterfaceManager, ApplicationWeights applicationWeights, ApplicationTypeService applicationTypeService) {
        super((ModuleFactory)Preconditions.checkNotNull((Object)moduleClassFactory), (WebInterfaceManager)Preconditions.checkNotNull((Object)webInterfaceManager));
        this.applicationWeights = applicationWeights;
        this.applicationTypeService = applicationTypeService;
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@menu-key").withError("menu-key attribute is mandatory"), ValidationPattern.test((String)"not(@menu-key) or string-length(normalize-space(@menu-key)) > 0").withError("menu-key is empty"), ValidationPattern.test((String)"link").withError("link tag is mandatory"), ValidationPattern.test((String)"string-length(normalize-space(link)) > 0").withError("link tag requires a link as content"), ValidationPattern.test((String)"label").withError("label tag is mandatory"), ValidationPattern.test((String)"string-length(normalize-space(label/@key)) > 0").withError("label tag requires a key attribute"), ValidationPattern.test((String)"not(tooltip) or string-length(normalize-space(tooltip/@key)) > 0").withError("tooltip tag requires a key attribute"), ValidationPattern.test((String)"not(icon) or string-length(normalize-space(icon)) > 0").withError("icon tag requires an image url as content"), ValidationPattern.test((String)"not(application-type) or string-length(normalize-space(application-type)) > 0").withError("application-type requires the type name as content"), ValidationPattern.test((String)"not(@weight) or string-length(normalize-space(@weight)) > 0").withError("weight attribute must have a value"), ValidationPattern.test((String)"not(@weight) or number(@weight) = @weight").withError("weight attribute must be a number")});
    }

    public void enabled() {
        super.enabled();
        this.enabled = true;
    }

    public void disabled() {
        super.disabled();
        this.enabled = false;
    }

    public RawNavigationLink getModule() {
        return this.enabled ? this.parseNavigationLinkEntity() : null;
    }

    private RawNavigationLink parseNavigationLinkEntity() {
        return ((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)((RawNavigationLinkBuilder)new RawNavigationLinkBuilder().key(this.parseMenuKey(this.element))).href(this.parseLink(this.element.element("link")))).labelKey(this.parseKeyAttributeFromElement(this.element.element("label"))).tooltipKey(this.parseKeyAttributeFromElement(this.element.element("tooltip"))).iconUrl(this.parseWebIcon(this.element.element("icon")))).weight(this.parseWeight())).applicationType(this.parseApplicationType())).self(this.isTrue(this.parseAttribute(this.element, "self")))).source(LinkSource.localDefault())).build();
    }

    private String parseMenuKey(Element navigationLinkElement) {
        return this.parseAttribute(navigationLinkElement, "menu-key");
    }

    private String parseLink(@Nullable Element linkElement) {
        return this.parseElementContent(linkElement);
    }

    private String parseKeyAttributeFromElement(@Nullable Element tagElement) {
        return Strings.emptyToNull((String)this.parseAttribute(tagElement, "key"));
    }

    private String parseWebIcon(@Nullable Element iconElement) {
        if (iconElement == null) {
            return null;
        }
        return this.parseElementContent(iconElement);
    }

    private int parseWeight() {
        if (this.element.attributeValue("weight") != null) {
            return Integer.parseInt(this.parseAttribute(this.element, "weight"));
        }
        return this.applicationWeights.getApplicationWeight();
    }

    private String parseApplicationType() {
        String applicationType = this.parseElementContent(this.element.element("application-type"));
        return Strings.isNullOrEmpty((String)applicationType) ? this.applicationTypeService.get() : applicationType;
    }

    private String parseElementContent(@Nullable Element element) {
        String elementContent = element != null ? element.getTextTrim() : null;
        return Strings.nullToEmpty((String)elementContent).trim();
    }

    private String parseAttribute(@Nullable Element element, String attributeName) {
        String attributeValue = element != null ? element.attributeValue(attributeName) : null;
        return Strings.nullToEmpty((String)attributeValue).trim();
    }

    private boolean isTrue(String val) {
        return Boolean.TRUE.toString().equalsIgnoreCase(val);
    }
}

