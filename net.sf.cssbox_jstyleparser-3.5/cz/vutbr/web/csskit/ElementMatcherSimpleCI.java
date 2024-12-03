/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.ElementMatcher;
import cz.vutbr.web.css.Selector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class ElementMatcherSimpleCI
implements ElementMatcher {
    public static final String CLASS_DELIM = " ";
    public static final String CLASS_ATTR = "class";
    public static final String ID_ATTR = "id";

    @Override
    public String getAttribute(Element e, String name) {
        return e.getAttribute(name);
    }

    @Override
    public Collection<String> elementClasses(Element e) {
        String classNames = e.getAttribute(CLASS_ATTR);
        if (!classNames.isEmpty()) {
            ArrayList<String> list = new ArrayList<String>();
            for (String cname : classNames.toLowerCase().split(CLASS_DELIM)) {
                if ((cname = cname.trim()).length() <= 0) continue;
                list.add(cname);
            }
            return list;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean matchesClass(Element e, String className) {
        String classNames = e.getAttribute(CLASS_ATTR).toLowerCase();
        if (!classNames.isEmpty()) {
            String search = className.toLowerCase();
            int len = className.length();
            int lastIndex = 0;
            while ((lastIndex = classNames.indexOf(search, lastIndex)) != -1) {
                if ((lastIndex == 0 || Character.isWhitespace(classNames.charAt(lastIndex - 1))) && (lastIndex + len == classNames.length() || Character.isWhitespace(classNames.charAt(lastIndex + len)))) {
                    return true;
                }
                lastIndex += len;
            }
            return false;
        }
        return false;
    }

    @Override
    public String elementID(Element e) {
        return e.getAttribute(ID_ATTR);
    }

    @Override
    public boolean matchesID(Element e, String id) {
        return id.equalsIgnoreCase(e.getAttribute(ID_ATTR));
    }

    @Override
    public String elementName(Element e) {
        return e.getNodeName();
    }

    @Override
    public boolean matchesName(Element e, String name) {
        return name.equalsIgnoreCase(e.getNodeName());
    }

    @Override
    public boolean matchesAttribute(Element e, String name, String value, Selector.Operator o) {
        Attr attributeNode = e.getAttributeNode(name);
        if (attributeNode != null && o != null) {
            String attributeValue = attributeNode.getNodeValue();
            switch (o) {
                case EQUALS: {
                    return attributeValue.equals(value);
                }
                case INCLUDES: {
                    if (value.isEmpty() || ElementMatcherSimpleCI.containsWhitespace(value)) {
                        return false;
                    }
                    attributeValue = CLASS_DELIM + attributeValue + CLASS_DELIM;
                    return attributeValue.matches(".* " + value + " .*");
                }
                case DASHMATCH: {
                    return attributeValue.matches("^" + value + "(-.*|$)");
                }
                case CONTAINS: {
                    return !value.isEmpty() && attributeValue.matches(".*" + value + ".*");
                }
                case STARTSWITH: {
                    return !value.isEmpty() && attributeValue.matches("^" + value + ".*");
                }
                case ENDSWITH: {
                    return !value.isEmpty() && attributeValue.matches(".*" + value + "$");
                }
            }
            return true;
        }
        return false;
    }

    private static boolean containsWhitespace(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) continue;
            return true;
        }
        return false;
    }
}

