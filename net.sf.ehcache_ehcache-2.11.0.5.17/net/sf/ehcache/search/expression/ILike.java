/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.expression;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.expression.BaseCriteria;

public class ILike
extends BaseCriteria {
    private final String attributeName;
    private final String regex;
    private final Pattern pattern;

    public ILike(String attributeName, String regex) {
        if (attributeName == null || regex == null) {
            throw new SearchException("Both the attribute name and regex must be non null.");
        }
        this.attributeName = attributeName;
        this.regex = regex;
        this.pattern = ILike.convertRegex(regex.trim());
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public String getRegex() {
        return this.regex;
    }

    private static Pattern convertRegex(String expr) {
        if (expr.length() == 0) {
            throw new SearchException("Zero length regex");
        }
        StringBuilder javaRegex = new StringBuilder("^");
        boolean escape = false;
        block8: for (int i = 0; i < expr.length(); ++i) {
            char ch = expr.charAt(i);
            if (escape) {
                switch (ch) {
                    case '*': 
                    case '?': 
                    case '\\': {
                        javaRegex.append(Pattern.quote(ILike.lowerCase(ch)));
                        break;
                    }
                    default: {
                        throw new SearchException("Illegal escape character (" + ch + ") in regex: " + expr);
                    }
                }
                escape = false;
                continue;
            }
            switch (ch) {
                case '\\': {
                    escape = true;
                    continue block8;
                }
                case '?': {
                    javaRegex.append(".");
                    continue block8;
                }
                case '*': {
                    javaRegex.append(".*");
                    continue block8;
                }
                default: {
                    javaRegex.append(Pattern.quote(ILike.lowerCase(ch)));
                }
            }
        }
        javaRegex.append("$");
        return Pattern.compile(javaRegex.toString(), 32);
    }

    private static String lowerCase(char ch) {
        return Character.toString(ch).toLowerCase();
    }

    @Override
    public boolean execute(Element e, Map<String, AttributeExtractor> attributeExtractors) {
        Object value = ILike.getExtractor(this.attributeName, attributeExtractors).attributeFor(e, this.attributeName);
        if (value == null) {
            return false;
        }
        String asString = value.toString().toLowerCase();
        return this.pattern.matcher(asString).matches();
    }

    @Override
    public Set<Attribute<?>> getAttributes() {
        return Collections.singleton(new Attribute(this.attributeName));
    }
}

