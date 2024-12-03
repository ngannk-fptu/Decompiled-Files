/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import java.util.HashMap;
import java.util.Map;
import org.apache.tika.sax.xpath.AttributeMatcher;
import org.apache.tika.sax.xpath.ChildMatcher;
import org.apache.tika.sax.xpath.CompositeMatcher;
import org.apache.tika.sax.xpath.ElementMatcher;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.NamedAttributeMatcher;
import org.apache.tika.sax.xpath.NamedElementMatcher;
import org.apache.tika.sax.xpath.NodeMatcher;
import org.apache.tika.sax.xpath.SubtreeMatcher;
import org.apache.tika.sax.xpath.TextMatcher;

public class XPathParser {
    private final Map<String, String> prefixes = new HashMap<String, String>();

    public XPathParser() {
    }

    public XPathParser(String prefix, String namespace) {
        this.addPrefix(prefix, namespace);
    }

    public void addPrefix(String prefix, String namespace) {
        this.prefixes.put(prefix, namespace);
    }

    public Matcher parse(String xpath) {
        if (xpath.equals("/text()")) {
            return TextMatcher.INSTANCE;
        }
        if (xpath.equals("/node()")) {
            return NodeMatcher.INSTANCE;
        }
        if (xpath.equals("/descendant::node()") || xpath.equals("/descendant:node()")) {
            return new CompositeMatcher(TextMatcher.INSTANCE, new ChildMatcher(new SubtreeMatcher(NodeMatcher.INSTANCE)));
        }
        if (xpath.equals("/@*")) {
            return AttributeMatcher.INSTANCE;
        }
        if (xpath.length() == 0) {
            return ElementMatcher.INSTANCE;
        }
        if (xpath.startsWith("/@")) {
            String name = xpath.substring(2);
            String prefix = null;
            int colon = name.indexOf(58);
            if (colon != -1) {
                prefix = name.substring(0, colon);
                name = name.substring(colon + 1);
            }
            if (this.prefixes.containsKey(prefix)) {
                return new NamedAttributeMatcher(this.prefixes.get(prefix), name);
            }
            return Matcher.FAIL;
        }
        if (xpath.startsWith("/*")) {
            return new ChildMatcher(this.parse(xpath.substring(2)));
        }
        if (xpath.startsWith("///")) {
            return Matcher.FAIL;
        }
        if (xpath.startsWith("//")) {
            return new SubtreeMatcher(this.parse(xpath.substring(1)));
        }
        if (xpath.startsWith("/")) {
            int slash = xpath.indexOf(47, 1);
            if (slash == -1) {
                slash = xpath.length();
            }
            String name = xpath.substring(1, slash);
            String prefix = null;
            int colon = name.indexOf(58);
            if (colon != -1) {
                prefix = name.substring(0, colon);
                name = name.substring(colon + 1);
            }
            if (this.prefixes.containsKey(prefix)) {
                return new NamedElementMatcher(this.prefixes.get(prefix), name, this.parse(xpath.substring(slash)));
            }
            return Matcher.FAIL;
        }
        return Matcher.FAIL;
    }
}

