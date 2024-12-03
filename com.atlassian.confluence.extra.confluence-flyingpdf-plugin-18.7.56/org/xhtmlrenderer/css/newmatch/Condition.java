/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.newmatch;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.css.extend.TreeResolver;
import org.xhtmlrenderer.css.parser.CSSParseException;

abstract class Condition {
    Condition() {
    }

    abstract boolean matches(Object var1, AttributeResolver var2, TreeResolver var3);

    static Condition createAttributeExistsCondition(String namespaceURI, String name) {
        return new AttributeExistsCondition(namespaceURI, name);
    }

    static Condition createAttributePrefixCondition(String namespaceURI, String name, String value) {
        return new AttributePrefixCondition(namespaceURI, name, value);
    }

    static Condition createAttributeSuffixCondition(String namespaceURI, String name, String value) {
        return new AttributeSuffixCondition(namespaceURI, name, value);
    }

    static Condition createAttributeSubstringCondition(String namespaceURI, String name, String value) {
        return new AttributeSubstringCondition(namespaceURI, name, value);
    }

    static Condition createAttributeEqualsCondition(String namespaceURI, String name, String value) {
        return new AttributeEqualsCondition(namespaceURI, name, value);
    }

    static Condition createAttributeMatchesListCondition(String namespaceURI, String name, String value) {
        return new AttributeMatchesListCondition(namespaceURI, name, value);
    }

    static Condition createAttributeMatchesFirstPartCondition(String namespaceURI, String name, String value) {
        return new AttributeMatchesFirstPartCondition(namespaceURI, name, value);
    }

    static Condition createClassCondition(String className) {
        return new ClassCondition(className);
    }

    static Condition createIDCondition(String id) {
        return new IDCondition(id);
    }

    static Condition createLangCondition(String lang) {
        return new LangCondition(lang);
    }

    static Condition createFirstChildCondition() {
        return new FirstChildCondition();
    }

    static Condition createLastChildCondition() {
        return new LastChildCondition();
    }

    static Condition createNthChildCondition(String number) {
        return NthChildCondition.fromString(number);
    }

    static Condition createEvenChildCondition() {
        return new EvenChildCondition();
    }

    static Condition createOddChildCondition() {
        return new OddChildCondition();
    }

    static Condition createLinkCondition() {
        return new LinkCondition();
    }

    static Condition createUnsupportedCondition() {
        return new UnsupportedCondition();
    }

    private static String[] split(String s, char ch) {
        if (s.indexOf(ch) == -1) {
            return new String[]{s};
        }
        ArrayList<String> result = new ArrayList<String>();
        int last = 0;
        int next = 0;
        while ((next = s.indexOf(ch, last)) != -1) {
            if (next != last) {
                result.add(s.substring(last, next));
            }
            last = next + 1;
        }
        if (last != s.length()) {
            result.add(s.substring(last));
        }
        return result.toArray(new String[result.size()]);
    }

    private static class UnsupportedCondition
    extends Condition {
        UnsupportedCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            return false;
        }
    }

    private static class LinkCondition
    extends Condition {
        LinkCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            return attRes.isLink(e);
        }
    }

    private static class OddChildCondition
    extends Condition {
        OddChildCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            int position = treeRes.getPositionOfElement(e);
            return position >= 0 && position % 2 == 1;
        }
    }

    private static class EvenChildCondition
    extends Condition {
        EvenChildCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            int position = treeRes.getPositionOfElement(e);
            return position >= 0 && position % 2 == 0;
        }
    }

    private static class NthChildCondition
    extends Condition {
        private static final Pattern pattern = Pattern.compile("([-+]?)(\\d*)n(\\s*([-+])\\s*(\\d+))?");
        private final int a;
        private final int b;

        NthChildCondition(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            int position = treeRes.getPositionOfElement(e) + 1;
            if (position == this.b) {
                return true;
            }
            if (this.a == 0) {
                return false;
            }
            return (position - this.b) % this.a == 0 && (position - this.b) / this.a >= 0;
        }

        static NthChildCondition fromString(String number) {
            if ("even".equals(number = number.trim().toLowerCase())) {
                return new NthChildCondition(2, 0);
            }
            if ("odd".equals(number)) {
                return new NthChildCondition(2, 1);
            }
            try {
                return new NthChildCondition(0, Integer.parseInt(number));
            }
            catch (NumberFormatException e) {
                int b;
                Matcher m = pattern.matcher(number);
                if (!m.matches()) {
                    throw new CSSParseException("Invalid nth-child selector: " + number, -1);
                }
                int a = m.group(2).equals("") ? 1 : Integer.parseInt(m.group(2));
                int n = b = m.group(5) == null ? 0 : Integer.parseInt(m.group(5));
                if ("-".equals(m.group(1))) {
                    a *= -1;
                }
                if ("-".equals(m.group(4))) {
                    b *= -1;
                }
                return new NthChildCondition(a, b);
            }
        }
    }

    private static class LastChildCondition
    extends Condition {
        LastChildCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            return treeRes.isLastChildElement(e);
        }
    }

    private static class FirstChildCondition
    extends Condition {
        FirstChildCondition() {
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            return treeRes.isFirstChildElement(e);
        }
    }

    private static class LangCondition
    extends Condition {
        private String _lang;

        LangCondition(String lang) {
            this._lang = lang;
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            if (attRes == null) {
                return false;
            }
            String lang = attRes.getLang(e);
            if (lang == null) {
                return false;
            }
            if (this._lang.equalsIgnoreCase(lang)) {
                return true;
            }
            String[] ca = Condition.split(lang, '-');
            return this._lang.equalsIgnoreCase(ca[0]);
        }
    }

    private static class IDCondition
    extends Condition {
        private String _id;

        IDCondition(String id) {
            this._id = id;
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            if (attRes == null) {
                return false;
            }
            return this._id.equals(attRes.getID(e));
        }
    }

    private static class ClassCondition
    extends Condition {
        private String _paddedClassName;

        ClassCondition(String className) {
            this._paddedClassName = " " + className + " ";
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            if (attRes == null) {
                return false;
            }
            String c = attRes.getClass(e);
            if (c == null) {
                return false;
            }
            return (" " + c + " ").indexOf(this._paddedClassName) != -1;
        }
    }

    private static class AttributeMatchesFirstPartCondition
    extends AttributeCompareCondition {
        AttributeMatchesFirstPartCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            String[] ca = Condition.split(attrValue, '-');
            return conditionValue.equals(ca[0]);
        }
    }

    private static class AttributeMatchesListCondition
    extends AttributeCompareCondition {
        AttributeMatchesListCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            String[] ca = Condition.split(attrValue, ' ');
            boolean matched = false;
            for (int j = 0; j < ca.length; ++j) {
                if (!conditionValue.equals(ca[j])) continue;
                matched = true;
            }
            return matched;
        }
    }

    private static class AttributeSubstringCondition
    extends AttributeCompareCondition {
        AttributeSubstringCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            return attrValue.indexOf(conditionValue) > -1;
        }
    }

    private static class AttributeSuffixCondition
    extends AttributeCompareCondition {
        AttributeSuffixCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            return attrValue.endsWith(conditionValue);
        }
    }

    private static class AttributePrefixCondition
    extends AttributeCompareCondition {
        AttributePrefixCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            return attrValue.startsWith(conditionValue);
        }
    }

    private static class AttributeEqualsCondition
    extends AttributeCompareCondition {
        AttributeEqualsCondition(String namespaceURI, String name, String value) {
            super(namespaceURI, name, value);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            return attrValue.equals(conditionValue);
        }
    }

    private static class AttributeExistsCondition
    extends AttributeCompareCondition {
        AttributeExistsCondition(String namespaceURI, String name) {
            super(namespaceURI, name, null);
        }

        @Override
        protected boolean compare(String attrValue, String conditionValue) {
            return !attrValue.equals("");
        }
    }

    private static abstract class AttributeCompareCondition
    extends Condition {
        private String _namespaceURI;
        private String _name;
        private String _value;

        protected abstract boolean compare(String var1, String var2);

        AttributeCompareCondition(String namespaceURI, String name, String value) {
            this._namespaceURI = namespaceURI;
            this._name = name;
            this._value = value;
        }

        @Override
        boolean matches(Object e, AttributeResolver attRes, TreeResolver treeRes) {
            if (attRes == null) {
                return false;
            }
            String val = attRes.getAttributeValue(e, this._namespaceURI, this._name);
            if (val == null) {
                return false;
            }
            return this.compare(val, this._value);
        }
    }
}

