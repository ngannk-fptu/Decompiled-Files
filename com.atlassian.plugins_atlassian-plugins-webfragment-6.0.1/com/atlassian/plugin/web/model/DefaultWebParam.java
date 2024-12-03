/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.model.WebParam
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.model;

import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.model.AbstractWebItem;
import com.atlassian.plugin.web.model.WebParam;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.dom4j.Element;

public class DefaultWebParam
extends AbstractWebItem
implements WebParam {
    protected SortedMap<String, String> params;

    public DefaultWebParam(Element element, WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) {
        super(webFragmentHelper, contextProvider, descriptor);
        this.params = new TreeMap<String, String>(LoaderUtils.getParams((Element)element));
    }

    public DefaultWebParam(Map<String, String> params, WebFragmentHelper webFragmentHelper, ContextProvider contextProvider, WebFragmentModuleDescriptor descriptor) {
        super(webFragmentHelper, contextProvider, descriptor);
        this.params = new TreeMap<String, String>(params);
    }

    public SortedMap<String, String> getParams() {
        return this.params;
    }

    public Object get(String key) {
        return this.params.get(key);
    }

    public String getRenderedParam(String paramKey, Map<String, Object> context) {
        context.putAll(this.getContextMap(context));
        return this.getWebFragmentHelper().renderVelocityFragment((String)this.params.get(paramKey), context);
    }
}

