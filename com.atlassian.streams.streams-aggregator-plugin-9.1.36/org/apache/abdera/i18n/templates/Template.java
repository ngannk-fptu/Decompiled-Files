/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.Evaluator;
import org.apache.abdera.i18n.templates.HashMapContext;
import org.apache.abdera.i18n.templates.ObjectContext;
import org.apache.abdera.i18n.templates.URITemplate;
import org.apache.abdera.i18n.text.CharUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Template
implements Iterable<String>,
Cloneable,
Serializable {
    private static final long serialVersionUID = -613907262632631896L;
    private static final Evaluator EVALUATOR = new Evaluator();
    private static final Pattern VARIABLE = Pattern.compile("\\{[^{}]+\\}");
    private static final String TOKEN_START = "\\{";
    private static final String TOKEN_STOP = "\\}";
    private final String pattern;
    private final String[] tokens;
    private final String[] variables;

    public Template(String pattern) {
        this.pattern = CharUtils.stripBidiInternal(pattern);
        this.tokens = this.initTokens();
        this.variables = this.initVariables();
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getPatternForDisplay() {
        String pattern = this.pattern;
        for (String token : this) {
            pattern = this.replace(pattern, token, Template.forDisplay(token));
        }
        return CharUtils.wrapBidi(pattern, '\u202d');
    }

    private static String forDisplay(String token) {
        String[] splits = token.split("\\|");
        StringBuilder buf = new StringBuilder();
        buf.append('{');
        if (splits.length == 1) {
            String[] pair = splits[0].split("\\s*=\\s*");
            buf.append(CharUtils.wrapBidi(pair[0], '\u202a'));
            if (pair.length > 1) {
                buf.append('=');
                buf.append(pair[1]);
            }
        } else {
            buf.append(splits[0]);
            buf.append('|');
            buf.append(splits[1]);
            buf.append('|');
            String[] vars = splits[2].split("\\s*,\\s*");
            int i = 0;
            for (String var : vars) {
                if (i++ > 0) {
                    buf.append(",");
                }
                String[] pair = var.split("\\s*=\\s*");
                buf.append(CharUtils.wrapBidi(pair[0], '\u202a'));
                if (pair.length <= 1) continue;
                buf.append('=');
                buf.append(pair[1]);
            }
        }
        buf.append('}');
        return buf.toString();
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(this.tokens).iterator();
    }

    private String[] initTokens() {
        Matcher matcher = VARIABLE.matcher(this.pattern);
        ArrayList<String> tokens = new ArrayList<String>();
        while (matcher.find()) {
            String token = matcher.group();
            if (tokens.contains(token = token.substring(1, token.length() - 1))) continue;
            tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private String[] initVariables() {
        ArrayList<String> list = new ArrayList<String>();
        for (String token : this) {
            String[] vars;
            for (String var : vars = EVALUATOR.getVariables(token)) {
                if (list.contains(var)) continue;
                list.add(var);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public String[] getVariables() {
        return this.variables;
    }

    public String expand(Context context) {
        String pattern = this.pattern;
        for (String token : this) {
            pattern = this.replace(pattern, token, EVALUATOR.evaluate(token, context));
        }
        return pattern;
    }

    public String expand(Object object) {
        return this.expand(object, false);
    }

    public String expand(Object object, boolean isiri) {
        return this.expand(object instanceof Context ? (Context)object : (object instanceof Map ? new HashMapContext((Map)object, isiri) : new ObjectContext(object, isiri)));
    }

    private String replace(String pattern, String token, String value) {
        return pattern.replaceAll(TOKEN_START + Pattern.quote(token) + TOKEN_STOP, value);
    }

    public Template clone() {
        try {
            return (Template)super.clone();
        }
        catch (Throwable e) {
            return new Template(this.pattern);
        }
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
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
        Template other = (Template)obj;
        return !(this.pattern == null ? other.pattern != null : !this.pattern.equals(other.pattern));
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("V:" + this.getPatternForDisplay());
        buf.append('\n');
        buf.append("L:" + CharUtils.wrapBidi(this.getPattern(), '\u202d'));
        buf.append('\n');
        return buf.toString();
    }

    public String explain() {
        StringBuilder buf = new StringBuilder();
        try {
            this.explain(buf);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buf.toString();
    }

    public void explain(Appendable buf) throws IOException {
        int i;
        String[] vars;
        buf.append("Template:");
        buf.append('\n');
        buf.append("\t" + this.getPatternForDisplay());
        buf.append('\n');
        buf.append('\n');
        buf.append(" Variables:");
        buf.append('\n');
        for (String var : vars = this.getVariables()) {
            buf.append('\t');
            buf.append(var);
            buf.append('\n');
        }
        buf.append('\n');
        buf.append(" Tokens:");
        buf.append('\n');
        for (String token : this) {
            buf.append('\t');
            buf.append(Template.forDisplay(token) + " \n\t\t ");
            EVALUATOR.explain(token, buf);
            buf.append('\n');
        }
        buf.append('\n');
        buf.append(" Example:");
        buf.append('\n');
        HashMapContext c = new HashMapContext();
        for (String var : vars) {
            c.put(var, "foo");
            buf.append("\t" + var + " = " + "foo");
            buf.append('\n');
        }
        buf.append('\n');
        buf.append("\t" + this.expand(c));
        buf.append('\n');
        buf.append('\n');
        c.clear();
        for (i = 0; i < vars.length; ++i) {
            String var = vars[i];
            if (i % 2 == 1) {
                c.put(var, "foo");
                buf.append("\t" + var + " = " + "foo");
                buf.append('\n');
                continue;
            }
            buf.append("\t" + var + " = null");
            buf.append('\n');
        }
        buf.append('\n');
        buf.append("\t" + this.expand(c));
        buf.append('\n');
        buf.append('\n');
        c.clear();
        for (i = 0; i < vars.length; ++i) {
            String var = vars[i];
            if (i % 2 == 0) {
                c.put(var, "foo");
                buf.append("\t" + var + " = " + "foo");
                buf.append('\n');
                continue;
            }
            buf.append("\t" + var + " = null");
            buf.append('\n');
        }
        buf.append('\n');
        buf.append("\t" + this.expand(c));
    }

    public static String expand(String pattern, Context context) {
        if (context == null || pattern == null) {
            throw new IllegalArgumentException();
        }
        Template template = new Template(pattern);
        return template.expand(context);
    }

    public static String expand(String pattern, Object object) {
        return Template.expand(pattern, object, false);
    }

    public static String expand(String pattern, Object object, boolean isiri) {
        if (object == null || pattern == null) {
            throw new IllegalArgumentException();
        }
        Template template = new Template(pattern);
        return template.expand(object, isiri);
    }

    public static String expandAnnotated(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        Class<?> _class = object.getClass();
        URITemplate uritemplate = _class.getAnnotation(URITemplate.class);
        if (uritemplate != null) {
            return Template.expand(uritemplate.value(), object, uritemplate.isiri());
        }
        throw new IllegalArgumentException("No URI Template provided");
    }

    public static String explain(String pattern) {
        return new Template(pattern).explain();
    }

    public static void explain(String pattern, Appendable buf) throws IOException {
        new Template(pattern).explain(buf);
    }
}

