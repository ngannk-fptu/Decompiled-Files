/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.uri;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriPattern;
import com.sun.jersey.api.uri.UriTemplateParser;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UriTemplate {
    private static String[] EMPTY_VALUES = new String[0];
    public static final Comparator<UriTemplate> COMPARATOR = new Comparator<UriTemplate>(){

        @Override
        public int compare(UriTemplate o1, UriTemplate o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 == EMPTY && o2 == EMPTY) {
                return 0;
            }
            if (o1 == EMPTY) {
                return 1;
            }
            if (o2 == EMPTY) {
                return -1;
            }
            int i = o2.getNumberOfExplicitCharacters() - o1.getNumberOfExplicitCharacters();
            if (i != 0) {
                return i;
            }
            i = o2.getNumberOfTemplateVariables() - o1.getNumberOfTemplateVariables();
            if (i != 0) {
                return i;
            }
            i = o2.getNumberOfExplicitRegexes() - o1.getNumberOfExplicitRegexes();
            if (i != 0) {
                return i;
            }
            return o2.pattern.getRegex().compareTo(o1.pattern.getRegex());
        }
    };
    private static final Pattern TEMPLATE_NAMES_PATTERN = Pattern.compile("\\{(\\w[-\\w\\.]*)\\}");
    public static final UriTemplate EMPTY = new UriTemplate();
    private final String template;
    private final String normalizedTemplate;
    private final UriPattern pattern;
    private final boolean endsWithSlash;
    private final List<String> templateVariables;
    private final int numOfExplicitRegexes;
    private final int numOfCharacters;

    private UriTemplate() {
        this.normalizedTemplate = "";
        this.template = "";
        this.pattern = UriPattern.EMPTY;
        this.endsWithSlash = false;
        this.templateVariables = Collections.emptyList();
        this.numOfCharacters = 0;
        this.numOfExplicitRegexes = 0;
    }

    public UriTemplate(String template) throws PatternSyntaxException, IllegalArgumentException {
        this(new UriTemplateParser(template));
    }

    protected UriTemplate(UriTemplateParser templateParser) throws PatternSyntaxException, IllegalArgumentException {
        this.template = templateParser.getTemplate();
        this.normalizedTemplate = templateParser.getNormalizedTemplate();
        this.pattern = this.createUriPattern(templateParser);
        this.numOfExplicitRegexes = templateParser.getNumberOfExplicitRegexes();
        this.numOfCharacters = templateParser.getNumberOfLiteralCharacters();
        this.endsWithSlash = this.template.charAt(this.template.length() - 1) == '/';
        this.templateVariables = Collections.unmodifiableList(templateParser.getNames());
    }

    protected UriPattern createUriPattern(UriTemplateParser templateParser) {
        return new UriPattern(templateParser.getPattern(), templateParser.getGroupIndexes());
    }

    public final String getTemplate() {
        return this.template;
    }

    public final UriPattern getPattern() {
        return this.pattern;
    }

    public final boolean endsWithSlash() {
        return this.endsWithSlash;
    }

    public final List<String> getTemplateVariables() {
        return this.templateVariables;
    }

    public final boolean isTemplateVariablePresent(String name) {
        for (String s : this.templateVariables) {
            if (!s.equals(name)) continue;
            return true;
        }
        return false;
    }

    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }

    public final int getNumberOfExplicitCharacters() {
        return this.numOfCharacters;
    }

    public final int getNumberOfTemplateVariables() {
        return this.templateVariables.size();
    }

    public final boolean match(CharSequence uri, Map<String, String> templateVariableToValue) throws IllegalArgumentException {
        if (templateVariableToValue == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, this.templateVariables, templateVariableToValue);
    }

    public final boolean match(CharSequence uri, List<String> groupValues) throws IllegalArgumentException {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, groupValues);
    }

    public final String createURI(Map<String, String> values) {
        StringBuilder b = new StringBuilder();
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(this.normalizedTemplate);
        int i = 0;
        while (m.find()) {
            b.append(this.normalizedTemplate, i, m.start());
            String tValue = values.get(m.group(1));
            if (tValue != null) {
                b.append(tValue);
            }
            i = m.end();
        }
        b.append(this.normalizedTemplate, i, this.normalizedTemplate.length());
        return b.toString();
    }

    public final String createURI(String ... values) {
        return this.createURI(values, 0, values.length);
    }

    public final String createURI(String[] values, int offset, int length) {
        HashMap<String, String> mapValues = new HashMap<String, String>();
        StringBuilder b = new StringBuilder();
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(this.normalizedTemplate);
        int v = offset;
        length += offset;
        int i = 0;
        while (m.find()) {
            b.append(this.normalizedTemplate, i, m.start());
            String tVariable = m.group(1);
            String tValue = (String)mapValues.get(tVariable);
            if (tValue != null) {
                b.append(tValue);
            } else if (v < length && (tValue = values[v++]) != null) {
                mapValues.put(tVariable, tValue);
                b.append(tValue);
            }
            i = m.end();
        }
        b.append(this.normalizedTemplate, i, this.normalizedTemplate.length());
        return b.toString();
    }

    public final String toString() {
        return this.pattern.toString();
    }

    public final int hashCode() {
        return this.pattern.hashCode();
    }

    public final boolean equals(Object o) {
        if (o instanceof UriTemplate) {
            UriTemplate that = (UriTemplate)o;
            return this.pattern.equals(that.pattern);
        }
        return false;
    }

    public static final String createURI(String scheme, String userInfo, String host, String port, String path, String query, String fragment, Map<String, ? extends Object> values, boolean encode) {
        return UriTemplate.createURI(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }

    public static final String createURI(String scheme, String authority, String userInfo, String host, String port, String path, String query, String fragment, Map<String, ? extends Object> values, boolean encode) {
        HashMap<String, String> stringValues = new HashMap<String, String>();
        for (Map.Entry<String, ? extends Object> e : values.entrySet()) {
            if (e.getValue() == null) continue;
            stringValues.put(e.getKey(), e.getValue().toString());
        }
        return UriTemplate.createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode);
    }

    public static final String createURIWithStringValues(String scheme, String userInfo, String host, String port, String path, String query, String fragment, Map<String, ? extends Object> values, boolean encode) {
        return UriTemplate.createURIWithStringValues(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }

    public static final String createURIWithStringValues(String scheme, String authority, String userInfo, String host, String port, String path, String query, String fragment, Map<String, ? extends Object> values, boolean encode) {
        return UriTemplate.createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, EMPTY_VALUES, encode, values);
    }

    public static final String createURI(String scheme, String userInfo, String host, String port, String path, String query, String fragment, Object[] values, boolean encode) {
        return UriTemplate.createURI(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }

    public static final String createURI(String scheme, String authority, String userInfo, String host, String port, String path, String query, String fragment, Object[] values, boolean encode) {
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == null) continue;
            stringValues[i] = values[i].toString();
        }
        return UriTemplate.createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode);
    }

    public static final String createURIWithStringValues(String scheme, String userInfo, String host, String port, String path, String query, String fragment, String[] values, boolean encode) {
        return UriTemplate.createURIWithStringValues(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }

    public static final String createURIWithStringValues(String scheme, String authority, String userInfo, String host, String port, String path, String query, String fragment, String[] values, boolean encode) {
        return UriTemplate.createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, values, encode, new HashMap<String, Object>());
    }

    private static String createURIWithStringValues(String scheme, String authority, String userInfo, String host, String port, String path, String query, String fragment, String[] values, boolean encode, Map<String, Object> mapValues) {
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        if (scheme != null) {
            offset = UriTemplate.createURIComponent(UriComponent.Type.SCHEME, scheme, values, offset, false, mapValues, sb);
            sb.append(':');
        }
        if (userInfo != null || host != null || port != null) {
            sb.append("//");
            if (userInfo != null && userInfo.length() > 0) {
                offset = UriTemplate.createURIComponent(UriComponent.Type.USER_INFO, userInfo, values, offset, encode, mapValues, sb);
                sb.append('@');
            }
            if (host != null) {
                offset = UriTemplate.createURIComponent(UriComponent.Type.HOST, host, values, offset, encode, mapValues, sb);
            }
            if (port != null && port.length() > 0) {
                sb.append(':');
                offset = UriTemplate.createURIComponent(UriComponent.Type.PORT, port, values, offset, false, mapValues, sb);
            }
        } else if (authority != null) {
            sb.append("//");
            offset = UriTemplate.createURIComponent(UriComponent.Type.AUTHORITY, authority, values, offset, encode, mapValues, sb);
        }
        if (path != null && path.length() > 0) {
            if (sb.length() > 0 && path.charAt(0) != '/') {
                sb.append('/');
            }
            offset = UriTemplate.createURIComponent(UriComponent.Type.PATH, path, values, offset, encode, mapValues, sb);
        }
        if (query != null && query.length() > 0) {
            sb.append('?');
            offset = UriTemplate.createURIComponent(UriComponent.Type.QUERY_PARAM, query, values, offset, encode, mapValues, sb);
        }
        if (fragment != null && fragment.length() > 0) {
            sb.append('#');
            offset = UriTemplate.createURIComponent(UriComponent.Type.FRAGMENT, fragment, values, offset, encode, mapValues, sb);
        }
        return sb.toString();
    }

    private static int createURIComponent(UriComponent.Type t, String template, String[] values, int offset, boolean encode, Map<String, Object> mapValues, StringBuilder b) {
        if (template.indexOf(123) == -1) {
            b.append(template);
            return offset;
        }
        template = new UriTemplateParser(template).getNormalizedTemplate();
        Matcher m = TEMPLATE_NAMES_PATTERN.matcher(template);
        int v = offset;
        int i = 0;
        while (m.find()) {
            b.append(template, i, m.start());
            String tVariable = m.group(1);
            Object tValue = mapValues.get(tVariable);
            if (tValue == null && v < values.length) {
                tValue = values[v++];
            }
            if (tValue == null) {
                throw UriTemplate.templateVariableHasNoValue(tVariable);
            }
            mapValues.put(tVariable, tValue);
            tValue = encode ? UriComponent.encode(tValue.toString(), t) : UriComponent.contextualEncode(tValue.toString(), t);
            b.append(tValue);
            i = m.end();
        }
        b.append(template, i, template.length());
        return v;
    }

    private static IllegalArgumentException templateVariableHasNoValue(String tVariable) {
        return new IllegalArgumentException("The template variable, " + tVariable + ", has no value");
    }
}

