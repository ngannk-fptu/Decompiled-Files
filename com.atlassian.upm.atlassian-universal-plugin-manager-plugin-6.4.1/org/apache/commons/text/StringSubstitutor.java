/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package org.apache.commons.text;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.TextStringBuilder;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.commons.text.lookup.StringLookupFactory;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class StringSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final String DEFAULT_VAR_DEFAULT = ":-";
    public static final String DEFAULT_VAR_END = "}";
    public static final String DEFAULT_VAR_START = "${";
    public static final StringMatcher DEFAULT_PREFIX = StringMatcherFactory.INSTANCE.stringMatcher("${");
    public static final StringMatcher DEFAULT_SUFFIX = StringMatcherFactory.INSTANCE.stringMatcher("}");
    public static final StringMatcher DEFAULT_VALUE_DELIMITER = StringMatcherFactory.INSTANCE.stringMatcher(":-");
    private boolean disableSubstitutionInValues;
    private boolean enableSubstitutionInVariables;
    private boolean enableUndefinedVariableException;
    private char escapeChar;
    private StringMatcher prefixMatcher;
    private boolean preserveEscapes;
    private StringMatcher suffixMatcher;
    private StringMatcher valueDelimiterMatcher;
    private StringLookup variableResolver;

    public static StringSubstitutor createInterpolator() {
        return new StringSubstitutor(StringLookupFactory.INSTANCE.interpolatorStringLookup());
    }

    public static <V> String replace(Object source, Map<String, V> valueMap) {
        return new StringSubstitutor(valueMap).replace(source);
    }

    public static <V> String replace(Object source, Map<String, V> valueMap, String prefix, String suffix) {
        return new StringSubstitutor(valueMap, prefix, suffix).replace(source);
    }

    public static String replace(Object source, Properties valueProperties) {
        if (valueProperties == null) {
            return source.toString();
        }
        HashMap<String, String> valueMap = new HashMap<String, String>();
        Enumeration<?> propNames = valueProperties.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = String.valueOf(propNames.nextElement());
            String propValue = valueProperties.getProperty(propName);
            valueMap.put(propName, propValue);
        }
        return StringSubstitutor.replace(source, valueMap);
    }

    public static String replaceSystemProperties(Object source) {
        return new StringSubstitutor(StringLookupFactory.INSTANCE.systemPropertyStringLookup()).replace(source);
    }

    public StringSubstitutor() {
        this((StringLookup)null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, '$');
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape);
    }

    public <V> StringSubstitutor(Map<String, V> valueMap, String prefix, String suffix, char escape, String valueDelimiter) {
        this(StringLookupFactory.INSTANCE.mapStringLookup(valueMap), prefix, suffix, escape, valueDelimiter);
    }

    public StringSubstitutor(StringLookup variableResolver) {
        this(variableResolver, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StringSubstitutor(StringLookup variableResolver, String prefix, String suffix, char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(DEFAULT_VALUE_DELIMITER);
    }

    public StringSubstitutor(StringLookup variableResolver, String prefix, String suffix, char escape, String valueDelimiter) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
        this.setValueDelimiter(valueDelimiter);
    }

    public StringSubstitutor(StringLookup variableResolver, StringMatcher prefixMatcher, StringMatcher suffixMatcher, char escape) {
        this(variableResolver, prefixMatcher, suffixMatcher, escape, DEFAULT_VALUE_DELIMITER);
    }

    public StringSubstitutor(StringLookup variableResolver, StringMatcher prefixMatcher, StringMatcher suffixMatcher, char escape, StringMatcher valueDelimiterMatcher) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefixMatcher(prefixMatcher);
        this.setVariableSuffixMatcher(suffixMatcher);
        this.setEscapeChar(escape);
        this.setValueDelimiterMatcher(valueDelimiterMatcher);
    }

    public StringSubstitutor(StringSubstitutor other) {
        this.disableSubstitutionInValues = other.isDisableSubstitutionInValues();
        this.enableSubstitutionInVariables = other.isEnableSubstitutionInVariables();
        this.enableUndefinedVariableException = other.isEnableUndefinedVariableException();
        this.escapeChar = other.getEscapeChar();
        this.prefixMatcher = other.getVariablePrefixMatcher();
        this.preserveEscapes = other.isPreserveEscapes();
        this.suffixMatcher = other.getVariableSuffixMatcher();
        this.valueDelimiterMatcher = other.getValueDelimiterMatcher();
        this.variableResolver = other.getStringLookup();
    }

    private void checkCyclicSubstitution(String varName, List<String> priorVariables) {
        if (!priorVariables.contains(varName)) {
            return;
        }
        TextStringBuilder buf = new TextStringBuilder(256);
        buf.append("Infinite loop in property interpolation of ");
        buf.append(priorVariables.remove(0));
        buf.append(": ");
        buf.appendWithSeparators(priorVariables, "->");
        throw new IllegalStateException(buf.toString());
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public StringLookup getStringLookup() {
        return this.variableResolver;
    }

    public StringMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }

    public StringMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StringMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public boolean isDisableSubstitutionInValues() {
        return this.disableSubstitutionInValues;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }

    public boolean isEnableUndefinedVariableException() {
        return this.enableUndefinedVariableException;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public String replace(char[] source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source.length).append(source);
        this.substitute(buf, 0, source.length);
        return buf.toString();
    }

    public String replace(char[] source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(CharSequence source) {
        if (source == null) {
            return null;
        }
        return this.replace(source, 0, source.length());
    }

    public String replace(CharSequence source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source.toString(), offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(Object source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder().append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(String source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source);
        if (!this.substitute(buf, 0, source.length())) {
            return source;
        }
        return buf.toString();
    }

    public String replace(String source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return source.substring(offset, offset + length);
        }
        return buf.toString();
    }

    public String replace(StringBuffer source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(source.length()).append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StringBuffer source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(TextStringBuilder source) {
        if (source == null) {
            return null;
        }
        TextStringBuilder builder = new TextStringBuilder(source.length()).append(source);
        this.substitute(builder, 0, builder.length());
        return builder.toString();
    }

    public String replace(TextStringBuilder source, int offset, int length) {
        if (source == null) {
            return null;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public boolean replaceIn(StringBuffer source) {
        if (source == null) {
            return false;
        }
        return this.replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuffer source, int offset, int length) {
        if (source == null) {
            return false;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StringBuilder source) {
        if (source == null) {
            return false;
        }
        return this.replaceIn(source, 0, source.length());
    }

    public boolean replaceIn(StringBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        TextStringBuilder buf = new TextStringBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(TextStringBuilder source) {
        if (source == null) {
            return false;
        }
        return this.substitute(source, 0, source.length());
    }

    public boolean replaceIn(TextStringBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        return this.substitute(source, offset, length);
    }

    protected String resolveVariable(String variableName, TextStringBuilder buf, int startPos, int endPos) {
        StringLookup resolver = this.getStringLookup();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public StringSubstitutor setDisableSubstitutionInValues(boolean disableSubstitutionInValues) {
        this.disableSubstitutionInValues = disableSubstitutionInValues;
        return this;
    }

    public StringSubstitutor setEnableSubstitutionInVariables(boolean enableSubstitutionInVariables) {
        this.enableSubstitutionInVariables = enableSubstitutionInVariables;
        return this;
    }

    public StringSubstitutor setEnableUndefinedVariableException(boolean failOnUndefinedVariable) {
        this.enableUndefinedVariableException = failOnUndefinedVariable;
        return this;
    }

    public StringSubstitutor setEscapeChar(char escapeCharacter) {
        this.escapeChar = escapeCharacter;
        return this;
    }

    public StringSubstitutor setPreserveEscapes(boolean preserveEscapes) {
        this.preserveEscapes = preserveEscapes;
        return this;
    }

    public StringSubstitutor setValueDelimiter(char valueDelimiter) {
        return this.setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.charMatcher(valueDelimiter));
    }

    public StringSubstitutor setValueDelimiter(String valueDelimiter) {
        if (valueDelimiter == null || valueDelimiter.isEmpty()) {
            this.setValueDelimiterMatcher(null);
            return this;
        }
        return this.setValueDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(valueDelimiter));
    }

    public StringSubstitutor setValueDelimiterMatcher(StringMatcher valueDelimiterMatcher) {
        this.valueDelimiterMatcher = valueDelimiterMatcher;
        return this;
    }

    public StringSubstitutor setVariablePrefix(char prefix) {
        return this.setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.charMatcher(prefix));
    }

    public StringSubstitutor setVariablePrefix(String prefix) {
        Validate.isTrue((prefix != null ? 1 : 0) != 0, (String)"Variable prefix must not be null!", (Object[])new Object[0]);
        return this.setVariablePrefixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(prefix));
    }

    public StringSubstitutor setVariablePrefixMatcher(StringMatcher prefixMatcher) {
        Validate.isTrue((prefixMatcher != null ? 1 : 0) != 0, (String)"Variable prefix matcher must not be null!", (Object[])new Object[0]);
        this.prefixMatcher = prefixMatcher;
        return this;
    }

    public StringSubstitutor setVariableResolver(StringLookup variableResolver) {
        this.variableResolver = variableResolver;
        return this;
    }

    public StringSubstitutor setVariableSuffix(char suffix) {
        return this.setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.charMatcher(suffix));
    }

    public StringSubstitutor setVariableSuffix(String suffix) {
        Validate.isTrue((suffix != null ? 1 : 0) != 0, (String)"Variable suffix must not be null!", (Object[])new Object[0]);
        return this.setVariableSuffixMatcher(StringMatcherFactory.INSTANCE.stringMatcher(suffix));
    }

    public StringSubstitutor setVariableSuffixMatcher(StringMatcher suffixMatcher) {
        Validate.isTrue((suffixMatcher != null ? 1 : 0) != 0, (String)"Variable suffix matcher must not be null!", (Object[])new Object[0]);
        this.suffixMatcher = suffixMatcher;
        return this;
    }

    protected boolean substitute(TextStringBuilder builder, int offset, int length) {
        return this.substitute((TextStringBuilder)builder, (int)offset, (int)length, null).altered;
    }

    private Result substitute(TextStringBuilder builder, int offset, int length, List<String> priorVariables) {
        Objects.requireNonNull(builder, "builder");
        StringMatcher prefixMatcher = this.getVariablePrefixMatcher();
        StringMatcher suffixMatcher = this.getVariableSuffixMatcher();
        char escapeCh = this.getEscapeChar();
        StringMatcher valueDelimMatcher = this.getValueDelimiterMatcher();
        boolean substitutionInVariablesEnabled = this.isEnableSubstitutionInVariables();
        boolean substitutionInValuesDisabled = this.isDisableSubstitutionInValues();
        boolean undefinedVariableException = this.isEnableUndefinedVariableException();
        boolean preserveEscapes = this.isPreserveEscapes();
        boolean altered = false;
        int lengthChange = 0;
        int bufEnd = offset + length;
        int pos = offset;
        int escPos = -1;
        block0: while (pos < bufEnd) {
            int startMatchLen = prefixMatcher.isMatch(builder, pos, offset, bufEnd);
            if (startMatchLen == 0) {
                ++pos;
                continue;
            }
            if (pos > offset && builder.charAt(pos - 1) == escapeCh) {
                if (preserveEscapes) {
                    ++pos;
                    continue;
                }
                escPos = pos - 1;
            }
            int startPos = pos;
            pos += startMatchLen;
            int endMatchLen = 0;
            int nestedVarCount = 0;
            while (pos < bufEnd) {
                if (substitutionInVariablesEnabled && prefixMatcher.isMatch(builder, pos, offset, bufEnd) != 0) {
                    endMatchLen = prefixMatcher.isMatch(builder, pos, offset, bufEnd);
                    ++nestedVarCount;
                    pos += endMatchLen;
                    continue;
                }
                endMatchLen = suffixMatcher.isMatch(builder, pos, offset, bufEnd);
                if (endMatchLen == 0) {
                    ++pos;
                    continue;
                }
                if (nestedVarCount == 0) {
                    if (escPos >= 0) {
                        builder.deleteCharAt(escPos);
                        escPos = -1;
                        --lengthChange;
                        altered = true;
                        --bufEnd;
                        pos = startPos + 1;
                        --startPos;
                        continue block0;
                    }
                    String varNameExpr = builder.midString(startPos + startMatchLen, pos - startPos - startMatchLen);
                    if (substitutionInVariablesEnabled) {
                        TextStringBuilder bufName = new TextStringBuilder(varNameExpr);
                        this.substitute(bufName, 0, bufName.length());
                        varNameExpr = bufName.toString();
                    }
                    int endPos = pos += endMatchLen;
                    String varName = varNameExpr;
                    String varDefaultValue = null;
                    if (valueDelimMatcher != null) {
                        char[] varNameExprChars = varNameExpr.toCharArray();
                        int valueDelimiterMatchLen = 0;
                        for (int i = 0; i < varNameExprChars.length && (substitutionInVariablesEnabled || prefixMatcher.isMatch(varNameExprChars, i, i, varNameExprChars.length) == 0); ++i) {
                            if (valueDelimMatcher.isMatch(varNameExprChars, i, 0, varNameExprChars.length) == 0) continue;
                            valueDelimiterMatchLen = valueDelimMatcher.isMatch(varNameExprChars, i, 0, varNameExprChars.length);
                            varName = varNameExpr.substring(0, i);
                            varDefaultValue = varNameExpr.substring(i + valueDelimiterMatchLen);
                            break;
                        }
                    }
                    if (priorVariables == null) {
                        priorVariables = new ArrayList<String>();
                        priorVariables.add(builder.midString(offset, length));
                    }
                    this.checkCyclicSubstitution(varName, priorVariables);
                    priorVariables.add(varName);
                    String varValue = this.resolveVariable(varName, builder, startPos, endPos);
                    if (varValue == null) {
                        varValue = varDefaultValue;
                    }
                    if (varValue != null) {
                        int varLen = varValue.length();
                        builder.replace(startPos, endPos, varValue);
                        altered = true;
                        int change = 0;
                        if (!substitutionInValuesDisabled) {
                            change = this.substitute((TextStringBuilder)builder, (int)startPos, (int)varLen, priorVariables).lengthChange;
                        }
                        change = change + varLen - (endPos - startPos);
                        pos += change;
                        bufEnd += change;
                        lengthChange += change;
                    } else if (undefinedVariableException) {
                        throw new IllegalArgumentException(String.format("Cannot resolve variable '%s' (enableSubstitutionInVariables=%s).", varName, substitutionInVariablesEnabled));
                    }
                    priorVariables.remove(priorVariables.size() - 1);
                    continue block0;
                }
                --nestedVarCount;
                pos += endMatchLen;
            }
        }
        return new Result(altered, lengthChange);
    }

    private static final class Result {
        public final boolean altered;
        public final int lengthChange;

        private Result(boolean altered, int lengthChange) {
            this.altered = altered;
            this.lengthChange = lengthChange;
        }

        public String toString() {
            return "Result [altered=" + this.altered + ", lengthChange=" + this.lengthChange + "]";
        }
    }
}

