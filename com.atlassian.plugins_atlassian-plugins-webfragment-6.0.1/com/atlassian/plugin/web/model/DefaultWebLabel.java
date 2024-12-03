/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  javax.servlet.http.HttpServletRequest
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.DefaultWebParam;
import com.atlassian.plugin.web.model.WebLabel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dom4j.Element;

public class DefaultWebLabel
extends DefaultWebParam
implements WebLabel {
    private final String key;
    private final String noKeyValue;

    public DefaultWebLabel(Element labelEl, WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) throws PluginParseException {
        super(labelEl, webFragmentHelper, contextProvider, descriptor);
        if (labelEl == null) {
            throw new PluginParseException("You must specify a label for the section.");
        }
        this.key = labelEl.attributeValue("key");
        this.noKeyValue = this.key == null ? labelEl.getTextTrim() : null;
    }

    public String getKey() {
        return this.key;
    }

    public String getNoKeyValue() {
        return this.noKeyValue;
    }

    public String getDisplayableLabel(HttpServletRequest req, Map<String, Object> origContext) {
        HashMap<String, Object> tmpContext = new HashMap<String, Object>(origContext);
        tmpContext.putAll(this.getContextMap(tmpContext));
        if (this.key != null) {
            if (this.params == null || this.params.isEmpty()) {
                return this.getWebFragmentHelper().getI18nValue(this.key, null, tmpContext);
            }
            ArrayList<String> arguments = new ArrayList<String>();
            for (Map.Entry entry : this.params.entrySet()) {
                if (!((String)entry.getKey()).startsWith("param")) continue;
                arguments.add(this.getWebFragmentHelper().renderVelocityFragment((String)entry.getValue(), tmpContext));
            }
            return this.getWebFragmentHelper().getI18nValue(this.key, arguments, tmpContext);
        }
        return this.getWebFragmentHelper().renderVelocityFragment(this.noKeyValue, tmpContext);
    }
}

