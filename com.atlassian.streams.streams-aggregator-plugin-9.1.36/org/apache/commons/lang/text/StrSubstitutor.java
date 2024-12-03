/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrMatcher;

public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
    public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
    private char escapeChar;
    private StrMatcher prefixMatcher;
    private StrMatcher suffixMatcher;
    private StrLookup variableResolver;

    public static String replace(Object source, Map valueMap) {
        return new StrSubstitutor(valueMap).replace(source);
    }

    public static String replace(Object source, Map valueMap, String prefix, String suffix) {
        return new StrSubstitutor(valueMap, prefix, suffix).replace(source);
    }

    public static String replaceSystemProperties(Object source) {
        return new StrSubstitutor(StrLookup.systemPropertiesLookup()).replace(source);
    }

    public StrSubstitutor() {
        this((StrLookup)null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(Map valueMap) {
        this(StrLookup.mapLookup(valueMap), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(Map valueMap, String prefix, String suffix) {
        this(StrLookup.mapLookup(valueMap), prefix, suffix, '$');
    }

    public StrSubstitutor(Map valueMap, String prefix, String suffix, char escape) {
        this(StrLookup.mapLookup(valueMap), prefix, suffix, escape);
    }

    public StrSubstitutor(StrLookup variableResolver) {
        this(variableResolver, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(StrLookup variableResolver, String prefix, String suffix, char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefix(prefix);
        this.setVariableSuffix(suffix);
        this.setEscapeChar(escape);
    }

    public StrSubstitutor(StrLookup variableResolver, StrMatcher prefixMatcher, StrMatcher suffixMatcher, char escape) {
        this.setVariableResolver(variableResolver);
        this.setVariablePrefixMatcher(prefixMatcher);
        this.setVariableSuffixMatcher(suffixMatcher);
        this.setEscapeChar(escape);
    }

    public String replace(String source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source);
        if (!this.substitute(buf, 0, source.length())) {
            return source;
        }
        return buf.toString();
    }

    public String replace(String source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return source.substring(offset, offset + length);
        }
        return buf.toString();
    }

    public String replace(char[] source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length).append(source);
        this.substitute(buf, 0, source.length);
        return buf.toString();
    }

    public String replace(char[] source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StringBuffer source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StringBuffer source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(StrBuilder source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(source.length()).append(source);
        this.substitute(buf, 0, buf.length());
        return buf.toString();
    }

    public String replace(StrBuilder source, int offset, int length) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        this.substitute(buf, 0, length);
        return buf.toString();
    }

    public String replace(Object source) {
        if (source == null) {
            return null;
        }
        StrBuilder buf = new StrBuilder().append(source);
        this.substitute(buf, 0, buf.length());
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
        StrBuilder buf = new StrBuilder(length).append(source, offset, length);
        if (!this.substitute(buf, 0, length)) {
            return false;
        }
        source.replace(offset, offset + length, buf.toString());
        return true;
    }

    public boolean replaceIn(StrBuilder source) {
        if (source == null) {
            return false;
        }
        return this.substitute(source, 0, source.length());
    }

    public boolean replaceIn(StrBuilder source, int offset, int length) {
        if (source == null) {
            return false;
        }
        return this.substitute(source, offset, length);
    }

    protected boolean substitute(StrBuilder buf, int offset, int length) {
        return this.substitute(buf, offset, length, null) > 0;
    }

    private int substitute(StrBuilder buf, int offset, int length, List priorVariables) {
        StrMatcher prefixMatcher = this.getVariablePrefixMatcher();
        StrMatcher suffixMatcher = this.getVariableSuffixMatcher();
        char escape = this.getEscapeChar();
        boolean top = priorVariables == null;
        boolean altered = false;
        int lengthChange = 0;
        char[] chars = buf.buffer;
        int bufEnd = offset + length;
        int pos = offset;
        block0: while (pos < bufEnd) {
            int startMatchLen = prefixMatcher.isMatch(chars, pos, offset, bufEnd);
            if (startMatchLen == 0) {
                ++pos;
                continue;
            }
            if (pos > offset && chars[pos - 1] == escape) {
                buf.deleteCharAt(pos - 1);
                chars = buf.buffer;
                --lengthChange;
                altered = true;
                --bufEnd;
                continue;
            }
            int startPos = pos;
            pos += startMatchLen;
            int endMatchLen = 0;
            while (pos < bufEnd) {
                endMatchLen = suffixMatcher.isMatch(chars, pos, offset, bufEnd);
                if (endMatchLen == 0) {
                    ++pos;
                    continue;
                }
                String varName = new String(chars, startPos + startMatchLen, pos - startPos - startMatchLen);
                int endPos = pos += endMatchLen;
                if (priorVariables == null) {
                    priorVariables = new ArrayList<String>();
                    priorVariables.add(new String(chars, offset, length));
                }
                this.checkCyclicSubstitution(varName, priorVariables);
                priorVariables.add(varName);
                String varValue = this.resolveVariable(varName, buf, startPos, endPos);
                if (varValue != null) {
                    int varLen = varValue.length();
                    buf.replace(startPos, endPos, varValue);
                    altered = true;
                    int change = this.substitute(buf, startPos, varLen, priorVariables);
                    pos += (change += varLen - (endPos - startPos));
                    bufEnd += change;
                    lengthChange += change;
                    chars = buf.buffer;
                }
                priorVariables.remove(priorVariables.size() - 1);
                continue block0;
            }
        }
        if (top) {
            return altered ? 1 : 0;
        }
        return lengthChange;
    }

    private void checkCyclicSubstitution(String varName, List priorVariables) {
        if (!priorVariables.contains(varName)) {
            return;
        }
        StrBuilder buf = new StrBuilder(256);
        buf.append("Infinite loop in property interpolation of ");
        buf.append(priorVariables.remove(0));
        buf.append(": ");
        buf.appendWithSeparators(priorVariables, "->");
        throw new IllegalStateException(buf.toString());
    }

    protected String resolveVariable(String variableName, StrBuilder buf, int startPos, int endPos) {
        StrLookup resolver = this.getVariableResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.lookup(variableName);
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(char escapeCharacter) {
        this.escapeChar = escapeCharacter;
    }

    public StrMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StrSubstitutor setVariablePrefixMatcher(StrMatcher prefixMatcher) {
        if (prefixMatcher == null) {
            throw new IllegalArgumentException("Variable prefix matcher must not be null!");
        }
        this.prefixMatcher = prefixMatcher;
        return this;
    }

    public StrSubstitutor setVariablePrefix(char prefix) {
        return this.setVariablePrefixMatcher(StrMatcher.charMatcher(prefix));
    }

    public StrSubstitutor setVariablePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Variable prefix must not be null!");
        }
        return this.setVariablePrefixMatcher(StrMatcher.stringMatcher(prefix));
    }

    public StrMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public StrSubstitutor setVariableSuffixMatcher(StrMatcher suffixMatcher) {
        if (suffixMatcher == null) {
            throw new IllegalArgumentException("Variable suffix matcher must not be null!");
        }
        this.suffixMatcher = suffixMatcher;
        return this;
    }

    public StrSubstitutor setVariableSuffix(char suffix) {
        return this.setVariableSuffixMatcher(StrMatcher.charMatcher(suffix));
    }

    public StrSubstitutor setVariableSuffix(String suffix) {
        if (suffix == null) {
            throw new IllegalArgumentException("Variable suffix must not be null!");
        }
        return this.setVariableSuffixMatcher(StrMatcher.stringMatcher(suffix));
    }

    public StrLookup getVariableResolver() {
        return this.variableResolver;
    }

    public void setVariableResolver(StrLookup variableResolver) {
        this.variableResolver = variableResolver;
    }
}

