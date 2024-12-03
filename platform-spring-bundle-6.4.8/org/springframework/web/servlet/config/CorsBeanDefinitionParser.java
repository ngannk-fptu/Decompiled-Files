/*
 * Decompiled with CFR 0.152.
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
    @Override
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        LinkedHashMap<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<String, CorsConfiguration>();
        List<Element> mappings = DomUtils.getChildElementsByTagName(element, "mapping");
        if (mappings.isEmpty()) {
            CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
            corsConfigurations.put("/**", config);
        } else {
            for (Element mapping : mappings) {
                CorsConfiguration config = new CorsConfiguration();
                if (mapping.hasAttribute("allowed-origins")) {
                    String[] allowedOrigins = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-origins"), ",");
                    config.setAllowedOrigins(Arrays.asList(allowedOrigins));
                }
                if (mapping.hasAttribute("allowed-origin-patterns")) {
                    String[] patterns = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-origin-patterns"), ",");
                    config.setAllowedOriginPatterns(Arrays.asList(patterns));
                }
                if (mapping.hasAttribute("allowed-methods")) {
                    String[] allowedMethods = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-methods"), ",");
                    config.setAllowedMethods(Arrays.asList(allowedMethods));
                }
                if (mapping.hasAttribute("allowed-headers")) {
                    String[] allowedHeaders = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-headers"), ",");
                    config.setAllowedHeaders(Arrays.asList(allowedHeaders));
                }
                if (mapping.hasAttribute("exposed-headers")) {
                    String[] exposedHeaders = StringUtils.tokenizeToStringArray(mapping.getAttribute("exposed-headers"), ",");
                    config.setExposedHeaders(Arrays.asList(exposedHeaders));
                }
                if (mapping.hasAttribute("allow-credentials")) {
                    config.setAllowCredentials(Boolean.parseBoolean(mapping.getAttribute("allow-credentials")));
                }
                if (mapping.hasAttribute("max-age")) {
                    config.setMaxAge(Long.parseLong(mapping.getAttribute("max-age")));
                }
                config.applyPermitDefaultValues();
                config.validateAllowCredentials();
                corsConfigurations.put(mapping.getAttribute("path"), config);
            }
        }
        MvcNamespaceUtils.registerCorsConfigurations(corsConfigurations, parserContext, parserContext.extractSource(element));
        return null;
    }
}

