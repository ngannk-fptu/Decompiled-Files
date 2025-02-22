/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class UriTemplate
implements Serializable {
    private final String uriTemplate;
    private final UriComponents uriComponents;
    private final List<String> variableNames;
    private final Pattern matchPattern;

    public UriTemplate(String uriTemplate) {
        Assert.hasText(uriTemplate, "'uriTemplate' must not be null");
        this.uriTemplate = uriTemplate;
        this.uriComponents = UriComponentsBuilder.fromUriString(uriTemplate).build();
        TemplateInfo info = TemplateInfo.parse(uriTemplate);
        this.variableNames = Collections.unmodifiableList(info.getVariableNames());
        this.matchPattern = info.getMatchPattern();
    }

    public List<String> getVariableNames() {
        return this.variableNames;
    }

    public URI expand(Map<String, ?> uriVariables) {
        UriComponents expandedComponents = this.uriComponents.expand(uriVariables);
        UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }

    public URI expand(Object ... uriVariableValues) {
        UriComponents expandedComponents = this.uriComponents.expand(uriVariableValues);
        UriComponents encodedComponents = expandedComponents.encode();
        return encodedComponents.toUri();
    }

    public boolean matches(@Nullable String uri) {
        if (uri == null) {
            return false;
        }
        Matcher matcher = this.matchPattern.matcher(uri);
        return matcher.matches();
    }

    public Map<String, String> match(String uri) {
        Assert.notNull((Object)uri, "'uri' must not be null");
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>(this.variableNames.size());
        Matcher matcher = this.matchPattern.matcher(uri);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); ++i) {
                String name = this.variableNames.get(i - 1);
                String value = matcher.group(i);
                result.put(name, value);
            }
        }
        return result;
    }

    public String toString() {
        return this.uriTemplate;
    }

    private static class TemplateInfo {
        private final List<String> variableNames;
        private final Pattern pattern;

        private TemplateInfo(List<String> vars, Pattern pattern) {
            this.variableNames = vars;
            this.pattern = pattern;
        }

        public List<String> getVariableNames() {
            return this.variableNames;
        }

        public Pattern getMatchPattern() {
            return this.pattern;
        }

        public static TemplateInfo parse(String uriTemplate) {
            int level = 0;
            ArrayList<String> variableNames = new ArrayList<String>();
            StringBuilder pattern = new StringBuilder();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < uriTemplate.length(); ++i) {
                char c = uriTemplate.charAt(i);
                if (c == '{') {
                    if (++level == 1) {
                        pattern.append(TemplateInfo.quote(builder));
                        builder = new StringBuilder();
                        continue;
                    }
                } else if (c == '}' && --level == 0) {
                    String variable = builder.toString();
                    int idx = variable.indexOf(58);
                    if (idx == -1) {
                        pattern.append("([^/]*)");
                        variableNames.add(variable);
                    } else {
                        if (idx + 1 == variable.length()) {
                            throw new IllegalArgumentException("No custom regular expression specified after ':' in \"" + variable + "\"");
                        }
                        String regex = variable.substring(idx + 1);
                        pattern.append('(');
                        pattern.append(regex);
                        pattern.append(')');
                        variableNames.add(variable.substring(0, idx));
                    }
                    builder = new StringBuilder();
                    continue;
                }
                builder.append(c);
            }
            if (builder.length() > 0) {
                pattern.append(TemplateInfo.quote(builder));
            }
            return new TemplateInfo(variableNames, Pattern.compile(pattern.toString()));
        }

        private static String quote(StringBuilder builder) {
            return builder.length() > 0 ? Pattern.quote(builder.toString()) : "";
        }
    }
}

