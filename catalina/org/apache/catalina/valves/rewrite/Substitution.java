/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.valves.rewrite;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.valves.rewrite.Resolver;
import org.apache.catalina.valves.rewrite.RewriteMap;

public class Substitution {
    protected SubstitutionElement[] elements = null;
    protected String sub = null;
    private boolean escapeBackReferences;

    public String getSub() {
        return this.sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    void setEscapeBackReferences(boolean escapeBackReferences) {
        this.escapeBackReferences = escapeBackReferences;
    }

    public void parse(Map<String, RewriteMap> maps) {
        this.elements = this.parseSubstitution(this.sub, maps);
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private SubstitutionElement[] parseSubstitution(String sub, Map<String, RewriteMap> maps) {
        ArrayList<Object> elements = new ArrayList<Object>();
        int pos = 0;
        int percentPos = 0;
        int dollarPos = 0;
        int backslashPos = 0;
        while (pos < sub.length()) {
            void var8_24;
            int colon;
            int open;
            percentPos = sub.indexOf(37, pos);
            dollarPos = sub.indexOf(36, pos);
            backslashPos = sub.indexOf(92, pos);
            if (percentPos == -1 && dollarPos == -1 && backslashPos == -1) {
                StaticElement staticElement = new StaticElement();
                staticElement.value = sub.substring(pos);
                pos = sub.length();
                elements.add(staticElement);
                continue;
            }
            if (this.isFirstPos(backslashPos, dollarPos, percentPos)) {
                if (backslashPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                StaticElement staticElement = new StaticElement();
                staticElement.value = sub.substring(pos, backslashPos) + sub.substring(backslashPos + 1, backslashPos + 2);
                pos = backslashPos + 2;
                elements.add(staticElement);
                continue;
            }
            if (this.isFirstPos(dollarPos, percentPos)) {
                if (dollarPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < dollarPos) {
                    StaticElement staticElement = new StaticElement();
                    staticElement.value = sub.substring(pos, dollarPos);
                    elements.add(staticElement);
                }
                if (Character.isDigit(sub.charAt(dollarPos + 1))) {
                    RewriteRuleBackReferenceElement rewriteRuleBackReferenceElement = new RewriteRuleBackReferenceElement();
                    rewriteRuleBackReferenceElement.n = Character.digit(sub.charAt(dollarPos + 1), 10);
                    pos = dollarPos + 2;
                    elements.add(rewriteRuleBackReferenceElement);
                    continue;
                }
                if (sub.charAt(dollarPos + 1) != '{') throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                MapElement mapElement = new MapElement();
                open = sub.indexOf(123, dollarPos);
                colon = Substitution.findMatchingColonOrBar(true, sub, open);
                int def = Substitution.findMatchingColonOrBar(false, sub, open);
                int close = Substitution.findMatchingBrace(sub, open);
                if (-1 >= open || open >= colon || colon >= close) {
                    throw new IllegalArgumentException(sub);
                }
                mapElement.map = maps.get(sub.substring(open + 1, colon));
                if (mapElement.map == null) {
                    throw new IllegalArgumentException(sub + ": No map: " + sub.substring(open + 1, colon));
                }
                String key = null;
                String defaultValue = null;
                if (def > -1) {
                    if (colon >= def || def >= close) {
                        throw new IllegalArgumentException(sub);
                    }
                    key = sub.substring(colon + 1, def);
                    defaultValue = sub.substring(def + 1, close);
                } else {
                    key = sub.substring(colon + 1, close);
                }
                mapElement.key = this.parseSubstitution(key, maps);
                if (defaultValue != null) {
                    mapElement.defaultValue = this.parseSubstitution(defaultValue, maps);
                }
                pos = close + 1;
                elements.add(mapElement);
                continue;
            }
            if (percentPos + 1 == sub.length()) {
                throw new IllegalArgumentException(sub);
            }
            if (pos < percentPos) {
                StaticElement staticElement = new StaticElement();
                staticElement.value = sub.substring(pos, percentPos);
                elements.add(staticElement);
            }
            if (Character.isDigit(sub.charAt(percentPos + 1))) {
                RewriteCondBackReferenceElement rewriteCondBackReferenceElement = new RewriteCondBackReferenceElement();
                rewriteCondBackReferenceElement.n = Character.digit(sub.charAt(percentPos + 1), 10);
                pos = percentPos + 2;
                elements.add(rewriteCondBackReferenceElement);
                continue;
            }
            if (sub.charAt(percentPos + 1) != '{') throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
            Object var8_18 = null;
            open = sub.indexOf(123, percentPos);
            colon = Substitution.findMatchingColonOrBar(true, sub, open);
            int close = Substitution.findMatchingBrace(sub, open);
            if (-1 >= open || open >= close) {
                throw new IllegalArgumentException(sub);
            }
            if (colon > -1 && open < colon && colon < close) {
                String type = sub.substring(open + 1, colon);
                if (type.equals("ENV")) {
                    ServerVariableEnvElement serverVariableEnvElement = new ServerVariableEnvElement();
                    serverVariableEnvElement.key = sub.substring(colon + 1, close);
                } else if (type.equals("SSL")) {
                    ServerVariableSslElement serverVariableSslElement = new ServerVariableSslElement();
                    serverVariableSslElement.key = sub.substring(colon + 1, close);
                } else {
                    if (!type.equals("HTTP")) throw new IllegalArgumentException(sub + ": Bad type: " + type);
                    ServerVariableHttpElement serverVariableHttpElement = new ServerVariableHttpElement();
                    serverVariableHttpElement.key = sub.substring(colon + 1, close);
                }
            } else {
                ServerVariableElement serverVariableElement = new ServerVariableElement();
                serverVariableElement.key = sub.substring(open + 1, close);
            }
            pos = close + 1;
            elements.add(var8_24);
        }
        return elements.toArray(new SubstitutionElement[0]);
    }

    private static int findMatchingBrace(String sub, int start) {
        int nesting = 1;
        for (int i = start + 1; i < sub.length(); ++i) {
            char c = sub.charAt(i);
            if (c == '{') {
                char previousChar = sub.charAt(i - 1);
                if (previousChar != '$' && previousChar != '%') continue;
                ++nesting;
                continue;
            }
            if (c != '}' || --nesting != 0) continue;
            return i;
        }
        return -1;
    }

    private static int findMatchingColonOrBar(boolean colon, String sub, int start) {
        int nesting = 0;
        for (int i = start + 1; i < sub.length(); ++i) {
            char c = sub.charAt(i);
            if (c == '{') {
                char previousChar = sub.charAt(i - 1);
                if (previousChar != '$' && previousChar != '%') continue;
                ++nesting;
                continue;
            }
            if (c == '}') {
                --nesting;
                continue;
            }
            if (!(colon ? c == ':' : c == '|') || nesting != 0) continue;
            return i;
        }
        return -1;
    }

    public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
        return this.evaluateSubstitution(this.elements, rule, cond, resolver);
    }

    private String evaluateSubstitution(SubstitutionElement[] elements, Matcher rule, Matcher cond, Resolver resolver) {
        StringBuilder buf = new StringBuilder();
        for (SubstitutionElement element : elements) {
            buf.append(element.evaluate(rule, cond, resolver));
        }
        return buf.toString();
    }

    private boolean isFirstPos(int testPos, int ... others) {
        if (testPos < 0) {
            return false;
        }
        for (int other : others) {
            if (other < 0 || other >= testPos) continue;
            return false;
        }
        return true;
    }

    public static abstract class SubstitutionElement {
        public abstract String evaluate(Matcher var1, Matcher var2, Resolver var3);
    }

    public static class StaticElement
    extends SubstitutionElement {
        public String value;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return this.value;
        }
    }

    public class RewriteRuleBackReferenceElement
    extends SubstitutionElement {
        public int n;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = rule.group(this.n);
            if (result == null) {
                result = "";
            }
            if (Substitution.this.escapeBackReferences) {
                return URLEncoder.DEFAULT.encode(result, resolver.getUriCharset());
            }
            return result;
        }
    }

    public class MapElement
    extends SubstitutionElement {
        public RewriteMap map = null;
        public SubstitutionElement[] defaultValue = null;
        public SubstitutionElement[] key = null;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = this.map.lookup(Substitution.this.evaluateSubstitution(this.key, rule, cond, resolver));
            if (result == null && this.defaultValue != null) {
                result = Substitution.this.evaluateSubstitution(this.defaultValue, rule, cond, resolver);
            }
            return result;
        }
    }

    public static class RewriteCondBackReferenceElement
    extends SubstitutionElement {
        public int n;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return cond.group(this.n) == null ? "" : cond.group(this.n);
        }
    }

    public static class ServerVariableEnvElement
    extends SubstitutionElement {
        public String key;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveEnv(this.key);
        }
    }

    public static class ServerVariableSslElement
    extends SubstitutionElement {
        public String key;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveSsl(this.key);
        }
    }

    public static class ServerVariableHttpElement
    extends SubstitutionElement {
        public String key;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveHttp(this.key);
        }
    }

    public static class ServerVariableElement
    extends SubstitutionElement {
        public String key;

        @Override
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolve(this.key);
        }
    }
}

