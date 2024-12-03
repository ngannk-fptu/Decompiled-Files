/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformer
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssSubstitutionTransformer
implements WebResourceTransformer {
    private static final Logger log = LoggerFactory.getLogger(CssSubstitutionTransformer.class);
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("@([a-zA-Z][a-zA-Z0-9_]*)");

    public DownloadableResource transform(Element configElement, ResourceLocation location, String filePath, DownloadableResource nextResource) {
        return new CssSubstitutionDownloadableResource(nextResource);
    }

    static class CssSubstitutionDownloadableResource
    extends CharSequenceDownloadableResource {
        private final VariableMap variableMap = new VariableMap();

        public CssSubstitutionDownloadableResource(DownloadableResource originalResource) {
            super(originalResource);
        }

        protected CharSequence transform(CharSequence input) {
            Map<String, String> variables = this.variableMap.getVariableMap(true);
            Matcher matcher = VARIABLE_PATTERN.matcher(input);
            int start = 0;
            StringBuilder out = null;
            while (matcher.find()) {
                if (out == null) {
                    out = new StringBuilder();
                }
                out.append(input.subSequence(start, matcher.start()));
                String token = matcher.group(1);
                String subst = variables.get(token);
                if (subst != null) {
                    out.append(subst);
                } else {
                    out.append(matcher.group());
                }
                start = matcher.end();
            }
            if (out == null) {
                return input;
            }
            out.append(input.subSequence(start, input.length()));
            return out;
        }
    }

    public static class VariableMap {
        public Map<String, String> getVariableMap(boolean addLegacyVars) {
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
            Map<String, Object> beanProperties = this.getLookAndFeelProperties();
            for (Map.Entry<String, Object> entry : beanProperties.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (!(value instanceof String)) continue;
                String stringValue = (String)value;
                result.put(name, stringValue);
                if (!addLegacyVars) continue;
                result.put(name + "NoHash", StringUtils.strip((String)stringValue, (String)"#"));
            }
            String contextPath = RequestCacheThreadLocal.getContextPath();
            if (contextPath == null) {
                contextPath = "";
            }
            result.put("contextPath", contextPath);
            return result;
        }

        private Map<String, Object> getLookAndFeelProperties() {
            try {
                return Collections.emptyMap();
            }
            catch (Exception e) {
                log.warn("Could not read LookAndFeelBean", (Throwable)e);
                return Collections.emptyMap();
            }
        }
    }
}

