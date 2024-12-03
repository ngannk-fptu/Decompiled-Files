/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.jsr223;

import groovy.lang.GroovySystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

public class GroovyScriptEngineFactory
implements ScriptEngineFactory {
    private static final String VERSION = "2.0";
    private static final String SHORT_NAME = "groovy";
    private static final String LANGUAGE_NAME = "Groovy";
    private static final List<String> NAMES;
    private static final List<String> EXTENSIONS;
    private static final List<String> MIME_TYPES;

    @Override
    public String getEngineName() {
        return "Groovy Scripting Engine";
    }

    @Override
    public String getEngineVersion() {
        return VERSION;
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    @Override
    public String getLanguageVersion() {
        return GroovySystem.getVersion();
    }

    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    @Override
    public List<String> getMimeTypes() {
        return MIME_TYPES;
    }

    @Override
    public List<String> getNames() {
        return NAMES;
    }

    @Override
    public Object getParameter(String key) {
        if ("javax.script.name".equals(key)) {
            return SHORT_NAME;
        }
        if ("javax.script.engine".equals(key)) {
            return this.getEngineName();
        }
        if ("javax.script.engine_version".equals(key)) {
            return VERSION;
        }
        if ("javax.script.language".equals(key)) {
            return LANGUAGE_NAME;
        }
        if ("javax.script.language_version".equals(key)) {
            return GroovySystem.getVersion();
        }
        if ("THREADING".equals(key)) {
            return "MULTITHREADED";
        }
        throw new IllegalArgumentException("Invalid key");
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new GroovyScriptEngineImpl(this);
    }

    @Override
    public String getMethodCallSyntax(String obj, String method, String ... args) {
        StringBuilder ret = new StringBuilder(obj + "." + method + "(");
        int len = args.length;
        if (len == 0) {
            ret.append(")");
            return ret.toString();
        }
        for (int i = 0; i < len; ++i) {
            ret.append(args[i]);
            if (i != len - 1) {
                ret.append(",");
                continue;
            }
            ret.append(")");
        }
        return ret.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        StringBuilder buf = new StringBuilder();
        buf.append("println(\"");
        int len = toDisplay.length();
        block4: for (int i = 0; i < len; ++i) {
            char ch = toDisplay.charAt(i);
            switch (ch) {
                case '\"': {
                    buf.append("\\\"");
                    continue block4;
                }
                case '\\': {
                    buf.append("\\\\");
                    continue block4;
                }
                default: {
                    buf.append(ch);
                }
            }
        }
        buf.append("\")");
        return buf.toString();
    }

    @Override
    public String getProgram(String ... statements) {
        StringBuilder ret = new StringBuilder();
        for (String statement : statements) {
            ret.append(statement).append('\n');
        }
        return ret.toString();
    }

    static {
        ArrayList<String> n = new ArrayList<String>(2);
        n.add(SHORT_NAME);
        n.add(LANGUAGE_NAME);
        NAMES = Collections.unmodifiableList(n);
        n = new ArrayList(1);
        n.add(SHORT_NAME);
        EXTENSIONS = Collections.unmodifiableList(n);
        n = new ArrayList(1);
        n.add("application/x-groovy");
        MIME_TYPES = Collections.unmodifiableList(n);
    }
}

