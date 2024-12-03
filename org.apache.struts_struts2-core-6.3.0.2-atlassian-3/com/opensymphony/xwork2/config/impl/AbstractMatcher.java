/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.PatternMatcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractMatcher<E>
implements Serializable {
    private static final Logger LOG = LogManager.getLogger(AbstractMatcher.class);
    private static final Pattern WILDCARD_PATTERN = Pattern.compile("\\{(.)}");
    PatternMatcher<Object> wildcard;
    List<Mapping<E>> compiledPatterns = new ArrayList<Mapping<E>>();
    private final boolean appendNamedParameters;

    public AbstractMatcher(PatternMatcher<?> helper, boolean appendNamedParameters) {
        this.wildcard = helper;
        this.appendNamedParameters = appendNamedParameters;
    }

    @Deprecated
    public AbstractMatcher(PatternMatcher<?> helper) {
        this(helper, true);
    }

    public void addPattern(String name, E target, boolean looseMatch) {
        if (!this.wildcard.isLiteral(name)) {
            int lastStar;
            if (looseMatch && name.length() > 0 && name.charAt(0) == '/') {
                name = name.substring(1);
            }
            LOG.debug("Compiling pattern '{}'", (Object)name);
            Object pattern = this.wildcard.compilePattern(name);
            this.compiledPatterns.add(new Mapping<E>(name, pattern, target));
            if (looseMatch && (lastStar = name.lastIndexOf(42)) > 1 && lastStar == name.length() - 1 && name.charAt(lastStar - 1) != '*') {
                pattern = this.wildcard.compilePattern(name.substring(0, lastStar - 1));
                this.compiledPatterns.add(new Mapping<E>(name, pattern, target));
            }
        }
    }

    public void freeze() {
        this.compiledPatterns = Collections.unmodifiableList(new ArrayList());
    }

    public E match(String potentialMatch) {
        E config = null;
        if (this.compiledPatterns.size() > 0) {
            LOG.debug("Attempting to match '{}' to a wildcard pattern, {} available", (Object)potentialMatch, (Object)this.compiledPatterns.size());
            LinkedHashMap<String, String> vars = new LinkedHashMap<String, String>();
            for (Mapping<E> m : this.compiledPatterns) {
                if (!this.wildcard.match(vars, potentialMatch, m.getPattern())) continue;
                LOG.debug("Value matches pattern '{}'", (Object)m.getOriginalPattern());
                config = this.convert(potentialMatch, m.getTarget(), vars);
                break;
            }
        }
        return config;
    }

    protected abstract E convert(String var1, E var2, Map<String, String> var3);

    protected Map<String, String> replaceParameters(Map<String, String> orig, Map<String, String> vars) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : orig.entrySet()) {
            map.put(entry.getKey(), this.convertParam(entry.getValue(), vars));
        }
        if (this.appendNamedParameters) {
            LOG.debug("Appending named parameters to the result map");
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                if (NumberUtils.isCreatable((String)entry.getKey())) continue;
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    protected String convertParam(String val, Map<String, String> vars) {
        if (val == null) {
            return null;
        }
        Matcher wildcardMatcher = WILDCARD_PATTERN.matcher(val);
        StringBuffer result = new StringBuffer();
        while (wildcardMatcher.find()) {
            wildcardMatcher.appendReplacement(result, vars.getOrDefault(wildcardMatcher.group(1), ""));
        }
        wildcardMatcher.appendTail(result);
        return result.toString();
    }

    private static class Mapping<E>
    implements Serializable {
        private final String original;
        private final Object pattern;
        private final E config;

        public Mapping(String original, Object pattern, E config) {
            this.original = original;
            this.pattern = pattern;
            this.config = config;
        }

        public Object getPattern() {
            return this.pattern;
        }

        public E getTarget() {
            return this.config;
        }

        public String getOriginalPattern() {
            return this.original;
        }
    }
}

