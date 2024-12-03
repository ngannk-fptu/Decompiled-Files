/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.soy.renderer.SoyException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.soy.impl.webpanel;

import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.soy.renderer.SoyException;
import org.apache.commons.lang3.StringUtils;

class TemplateAddressing {
    TemplateAddressing() {
    }

    public static Address parseTemplateAddress(String templateAddress, String callingPluginKey) throws SoyException {
        return TemplateAddressing.parseAtlassianTemplateAddress(templateAddress, callingPluginKey);
    }

    private static Address parseAtlassianTemplateAddress(String templateAddress, String callingPluginKey) throws SoyException {
        String completeKey = StringUtils.substringBefore((String)templateAddress, (String)"/");
        String templateName = StringUtils.substringAfter((String)templateAddress, (String)"/");
        if (StringUtils.isEmpty((CharSequence)completeKey) || StringUtils.isEmpty((CharSequence)templateName)) {
            throw TemplateAddressing.badTemplateName(templateAddress);
        }
        if (StringUtils.countMatches((CharSequence)completeKey, (CharSequence)":") != 1) {
            throw TemplateAddressing.badTemplateName(templateAddress);
        }
        String pluginKey = StringUtils.substringBefore((String)completeKey, (String)":");
        String moduleKey = StringUtils.substringAfter((String)completeKey, (String)":");
        if (StringUtils.isEmpty((CharSequence)pluginKey) || ".".equals(pluginKey)) {
            pluginKey = callingPluginKey;
        }
        if (StringUtils.isEmpty((CharSequence)pluginKey) || StringUtils.isEmpty((CharSequence)moduleKey)) {
            throw TemplateAddressing.badTemplateName(templateAddress);
        }
        return new Address(new ModuleCompleteKey(pluginKey, moduleKey), templateName);
    }

    private static SoyException badTemplateName(String templateAddress) {
        return new SoyException(String.format("Template name must be in the form 'pluginKey:moduleKey/templateAddress' - '%s'", templateAddress));
    }

    static class Address {
        private final ModuleCompleteKey completeKey;
        private final String templateName;

        Address(ModuleCompleteKey completeKey, String templateName) {
            this.completeKey = completeKey;
            this.templateName = templateName;
        }

        public ModuleCompleteKey getCompleteKey() {
            return this.completeKey;
        }

        public String getTemplateName() {
            return this.templateName;
        }
    }
}

