/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.ContextSubBatchResourceUrl;
import com.atlassian.plugin.webresource.WebResourceSubBatchUrl;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public final class BatchResourceContentsWebFormatter {
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("(<\\w+)[\\s|>]");

    public static String insertBatchResourceContents(ResourceUrls resource, String formattedResource) {
        Map<String, String> dependencyAttributes = BatchResourceContentsWebFormatter.createDependencyAttributes(resource);
        if (dependencyAttributes.isEmpty()) {
            return formattedResource;
        }
        ArrayList<String> nameValues = new ArrayList<String>(dependencyAttributes.size());
        for (Map.Entry<String, String> attr : dependencyAttributes.entrySet()) {
            String nameValuePair = StringEscapeUtils.escapeHtml4((String)attr.getKey()) + "=\"" + StringEscapeUtils.escapeHtml4((String)attr.getValue()) + '\"';
            nameValues.add(nameValuePair);
        }
        String nameValuesStr = StringUtils.join(nameValues, (char)' ');
        Matcher matcher = TAG_NAME_PATTERN.matcher(formattedResource);
        if (matcher.find()) {
            StringBuilder builder = new StringBuilder(formattedResource);
            builder.insert(matcher.end(1), ' ' + nameValuesStr);
            return builder.toString();
        }
        return formattedResource;
    }

    private static Map<String, String> createDependencyAttributes(ResourceUrls resource) {
        ArrayList<Object> dependencyValues;
        HashMap<String, String> dependencyAttributes = new HashMap<String, String>();
        if (resource.getResourceUrl() instanceof WebResourceSubBatchUrl) {
            WebResourceSubBatchUrl webResourceBatchUrl = (WebResourceSubBatchUrl)resource.getResourceUrl();
            Bundle bundle = webResourceBatchUrl.getBundle();
            dependencyValues = new ArrayList<Object>(1);
            String attributeValue = bundle.getKey() + '[' + bundle.getVersion() + ']';
            dependencyValues.add(attributeValue);
            if (!dependencyValues.isEmpty()) {
                dependencyAttributes.put("data-atlassian-webresource-contents", StringUtils.join(dependencyValues, (char)','));
            }
        }
        if (resource.getResourceUrl() instanceof ContextSubBatchResourceUrl) {
            Iterator excludedContextsIterator;
            Iterator<String> contextsIterator;
            ContextSubBatchResourceUrl contextBatchResourceUrl = (ContextSubBatchResourceUrl)resource.getResourceUrl();
            List<Bundle> batchedBundles = contextBatchResourceUrl.getBatchedBundles();
            dependencyValues = new ArrayList(batchedBundles.size());
            for (Bundle descriptor : batchedBundles) {
                String attributeValue = descriptor.getKey() + '[' + descriptor.getVersion() + ']';
                dependencyValues.add(attributeValue);
            }
            if (!dependencyValues.isEmpty()) {
                dependencyAttributes.put("data-atlassian-webresource-contents", StringUtils.join(dependencyValues, (char)','));
            }
            if ((contextsIterator = contextBatchResourceUrl.getIncludedContexts().iterator()).hasNext()) {
                dependencyAttributes.put("data-atlassian-webresource-contexts", StringUtils.join(contextsIterator, (char)','));
            }
            if ((excludedContextsIterator = contextBatchResourceUrl.getExcludedContexts().iterator()).hasNext()) {
                dependencyAttributes.put("data-atlassian-webresource-excluded-contexts", StringUtils.join(excludedContextsIterator, (char)','));
            }
        }
        return dependencyAttributes;
    }
}

