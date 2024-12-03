/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.util.xml.DomUtils
 *  org.springframework.web.cors.CorsConfiguration
 */
package org.springframework.web.servlet.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.MvcNamespaceUtils;
import org.w3c.dom.Element;

public class CorsBeanDefinitionParser
implements BeanDefinitionParser {
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        LinkedHashMap<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<String, CorsConfiguration>();
        List mappings = DomUtils.getChildElementsByTagName((Element)element, (String)"mapping");
        if (mappings.isEmpty()) {
            CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
            corsConfigurations.put("/**", config);
        } else {
            for (Element mapping : mappings) {
                CorsConfiguration config = new CorsConfiguration();
                if (mapping.hasAttribute("allowed-origins")) {
                    String[] allowedOrigins = StringUtils.tokenizeToStringArray((String)mapping.getAttribute("allowed-origins"), (String)",");
                    config.setAllowedOrigins(Arrays.asList(allowedOrigins));
                }
                if (mapping.hasAttribute("allowed-origin-patterns")) {
                    String[] patterns = StringUtils.tokenizeToStringArray((String)mapping.getAttribute("allowed-origin-patterns"), (String)",");
                    config.setAllowedOriginPatterns(Arrays.asList(patterns));
                }
                if (mapping.hasAttribute("allowed-methods")) {
                    String[] allowedMethods = StringUtils.tokenizeToStringArray((String)mapping.getAttribute("allowed-methods"), (String)",");
                    config.setAllowedMethods(Arrays.asList(allowedMethods));
                }
                if (mapping.hasAttribute("allowed-headers")) {
                    String[] allowedHeaders = StringUtils.tokenizeToStringArray((String)mapping.getAttribute("allowed-headers"), (String)",");
                    config.setAllowedHeaders(Arrays.asList(allowedHeaders));
                }
                if (mapping.hasAttribute("exposed-headers")) {
                    String[] exposedHeaders = StringUtils.tokenizeToStringArray((String)mapping.getAttribute("exposed-headers"), (String)",");
                    config.setExposedHeaders(Arrays.asList(exposedHeaders));
                }
                if (mapping.hasAttribute("allow-credentials")) {
                    config.setAllowCredentials(Boolean.valueOf(Boolean.parseBoolean(mapping.getAttribute("allow-credentials"))));
                }
                if (mapping.hasAttribute("max-age")) {
                    config.setMaxAge(Long.valueOf(Long.parseLong(mapping.getAttribute("max-age"))));
                }
                config.applyPermitDefaultValues();
                config.validateAllowCredentials();
                corsConfigurations.put(mapping.getAttribute("path"), config);
            }
        }
        MvcNamespaceUtils.registerCorsConfigurations(corsConfigurations, parserContext, parserContext.extractSource((Object)element));
        return null;
    }
}

