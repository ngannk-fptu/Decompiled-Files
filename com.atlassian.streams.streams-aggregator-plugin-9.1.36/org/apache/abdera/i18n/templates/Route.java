/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.Evaluator;
import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.ObjectContext;
import org.apache.abdera.i18n.templates.URIRoute;
import org.apache.abdera.i18n.text.CharUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Route
implements Iterable<String>,
Cloneable,
Serializable {
    private static final long serialVersionUID = -8979172281494208841L;
    private static final Evaluator EVALUATOR = new Evaluator();
    private static final Pattern VARIABLE = Pattern.compile("[\\*\\:](?:\\()?[0-9a-zA-Z]+(?:\\))?");
    private static final String VARIABLE_CONTENT_MATCH = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]+)";
    private static final String VARIABLE_CONTENT_PARSE = "([^:/\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;\\=]*)";
    private final String name;
    private final String pattern;
    private final String[] tokens;
    private final String[] variables;
    private final Pattern regexMatch;
    private final Pattern regexParse;
    private Map<String, String> requirements;
    private Map<String, String> defaultValues;

    public Route(String name, String pattern) {
        this(name, pattern, null, null);
    }

    public Route(String name, String pattern, Map<String, String> defaultValues, Map<String, String> requirements) {
        this.name = name;
        this.pattern = CharUtils.stripBidiInternal(pattern);
        this.tokens = this.initTokens();
        this.variables = this.initVariables();
        this.defaultValues = defaultValues;
        this.requirements = requirements;
        this.regexMatch = this.initRegexMatch();
        this.regexParse = this.initRegexParse();
    }

    private String[] initTokens() {
        Matcher matcher = VARIABLE.matcher(this.pattern);
        ArrayList<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            String token = matcher.group();
            if (tokens.contains(token)) continue;
            tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private String[] initVariables() {
        ArrayList<String> list = new ArrayList<String>();
        for (String token : this) {
            String var = this.var(token);
            if (list.contains(var)) continue;
            list.add(var);
        }
        Object[] vars = list.toArray(new String[list.size()]);
        Arrays.sort(vars);
        return vars;
    }

    private Pattern initRegexMatch() {
        StringBuffer match = new StringBuffer();
        int cnt = 0;
        for (String part : VARIABLE.split(this.pattern)) {
            match.append(Pattern.quote(part));
            if (cnt++ >= this.tokens.length) continue;
            match.append(VARIABLE_CONTENT_MATCH);
        }
        return Pattern.compile(match.toString());
    }

    private Pattern initRegexParse() {
        StringBuffer parse = new StringBuffer();
        int cnt = 0;
        for (String part : VARIABLE.split(this.pattern)) {
            parse.append(Pattern.quote(part));
            if (cnt++ >= this.tokens.length) continue;
            parse.append(VARIABLE_CONTENT_PARSE);
        }
        return Pattern.compile(parse.toString());
    }

    public boolean match(String uri) {
        return this.regexMatch.matcher(uri).matches() && this.matchRequirements(uri);
    }

    public Map<String, String> parse(String uri) {
        HashMap<String, String> vars = new HashMap<String, String>();
        Matcher matcher = this.regexParse.matcher(uri);
        if (matcher.matches()) {
            for (int i = 0; i < matcher.groupCount(); ++i) {
                vars.put(this.var(this.tokens[i]), matcher.group(i + 1).length() > 0 ? matcher.group(i + 1) : null);
            }
        }
        return vars;
    }

    public String expand(Context context) {
        String pattern = this.pattern;
        for (String token : this) {
            String var = this.var(token);
            pattern = this.replace(pattern, token, EVALUATOR.evaluate(var, this.getDefaultValue(var), context));
        }
        StringBuffer buf = new StringBuffer(pattern);
        boolean qs = false;
        for (String var : context) {
            if (Arrays.binarySearch(this.variables, var) >= 0) continue;
            if (!qs) {
                buf.append("?");
                qs = true;
            } else {
                buf.append("&");
            }
            buf.append(var).append("=").append(EVALUATOR.evaluate(var, context));
        }
        return buf.toString();
    }

    public String getDefaultValue(String var) {
        if (this.defaultValues == null) {
            return null;
        }
        return this.defaultValues.get(var);
    }

    public String getRequirement(String var) {
        if (this.requirements == null) {
            return null;
        }
        return this.requirements.get(var);
    }

    private String var(String token) {
        if ((token = token.substring(1)).startsWith("(")) {
            token = token.substring(1);
        }
        if (token.endsWith(")")) {
            token = token.substring(0, token.length() - 1);
        }
        return token;
    }

    public String expand(Object object) {
        return this.expand(object, false);
    }

    public String expand(Object object, boolean isiri) {
        return this.expand(object instanceof Context ? (Context)object : (object instanceof Map ? new HashMapContext((Map)object, isiri) : new ObjectContext(object, isiri)));
    }

    private String replace(String pattern, String token, String value) {
        return pattern.replace(token, value);
    }

    public String getName() {
        return this.name;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(this.tokens).iterator();
    }

    public String[] getVariables() {
        return this.variables;
    }

    public Map<String, String> getDefaultValues() {
        return this.defaultValues;
    }

    public Map<String, String> getRequirements() {
        return this.requirements;
    }

    public Route clone() {
        try {
            return (Route)super.clone();
        }
        catch (Throwable e) {
            return new Route(this.name, this.pattern);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + (this.pattern == null ? 0 : this.pattern.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Route other = (Route)obj;
        if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
            return false;
        }
        return !(this.pattern == null ? other.pattern != null : !this.pattern.equals(other.pattern));
    }

    public String toString() {
        return this.pattern;
    }

    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null) {
            throw new IllegalArgumentException();
        }
        Route route = new Route(null, pattern);
        return route.expand(context);
    }

    public static String expand(String pattern, Object object) {
        return Route.expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null) {
            throw new IllegalArgumentException();
        }
        Route route = new Route(null, pattern);
        return route.expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        Class<?> _class = object.getClass();
        URIRoute uriroute = _class.getAnnotation(URIRoute.class);
        if (uriroute != null) {
            return Route.expand(uriroute.value(), object, uriroute.isiri());
        }
        throw new IllegalArgumentException("No Route provided");
    }

    private boolean matchRequirements(String uri) {
        if (this.requirements != null && !this.requirements.isEmpty()) {
            Map<String, String> parsedUri = this.parse(uri);
            for (Map.Entry<String, String> requirement : this.requirements.entrySet()) {
                Pattern patt = Pattern.compile(requirement.getValue());
                if (!parsedUri.containsKey(requirement.getKey()) || patt.matcher(parsedUri.get(requirement.getKey())).matches()) continue;
                return false;
            }
        }
        return true;
    }
}

