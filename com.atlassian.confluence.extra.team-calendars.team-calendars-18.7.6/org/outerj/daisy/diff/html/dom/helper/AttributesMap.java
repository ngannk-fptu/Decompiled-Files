/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.dom.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

public class AttributesMap
extends HashMap<String, String> {
    private static final long serialVersionUID = -6165499554111988049L;
    protected static final String STYLE_ATTR = "style";
    protected static final String CLASS_ATTR = "class";
    protected static final String SPACE = " ";
    protected static final String NL_TAB_REGEXP = "\\n|\\t";

    public AttributesMap() {
    }

    public AttributesMap(Attributes attributes) {
        for (int i = 0; i < attributes.getLength(); ++i) {
            this.put(attributes.getQName(i).toLowerCase(), attributes.getValue(i));
        }
    }

    public boolean hasSameAttributes(Attributes attributes) {
        if (attributes.getLength() != this.size()) {
            return false;
        }
        for (int i = 0; i < attributes.getLength(); ++i) {
            String qName = attributes.getQName(i).toLowerCase();
            String value = attributes.getValue(i);
            String localValue = (String)this.get(qName);
            if (localValue == null) {
                return false;
            }
            if (localValue.equals(value) || qName.equals(STYLE_ATTR) && AttributesMap.equivalentStyles(value, localValue) || qName.equals(CLASS_ATTR) && AttributesMap.sameClassSet(value, localValue)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj instanceof AttributesMap) {
            AttributesMap attributesMap = (AttributesMap)obj;
            if (this.size() == attributesMap.size()) {
                equals = true;
                for (Map.Entry entry : this.entrySet()) {
                    String attrib = (String)entry.getKey();
                    String localValue = (String)entry.getValue();
                    String externalValue = (String)attributesMap.get(attrib);
                    if (externalValue != null && externalValue.equals(localValue) || (attrib.equals(STYLE_ATTR) ? AttributesMap.equivalentStyles(localValue, externalValue) : attrib.equals(CLASS_ATTR) && AttributesMap.sameClassSet(localValue, externalValue))) continue;
                    equals = false;
                    break;
                }
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int simple = 19;
        int result = 0;
        for (String attr : this.keySet()) {
            result += attr.hashCode() * simple;
            if (attr.equals(STYLE_ATTR)) {
                result += AttributesMap.normalizeStyleString((String)this.get(attr)).hashCode();
                continue;
            }
            if (attr.equals(CLASS_ATTR)) {
                result += AttributesMap.normalizeClassString((String)this.get(attr)).hashCode();
                continue;
            }
            result += ((String)this.get(attr)).hashCode();
        }
        return result;
    }

    public static boolean equivalentStyles(String style1, String style2) {
        Object[] styleRules2;
        if (style1 == null) {
            return style2 == null;
        }
        if (style2 == null) {
            return false;
        }
        style1 = style1.replaceAll(NL_TAB_REGEXP, SPACE);
        style2 = style2.replaceAll(NL_TAB_REGEXP, SPACE);
        style1 = style1.replaceAll(" ++", SPACE);
        style2 = style2.replaceAll(" ++", SPACE);
        if ((style1 = style1.trim()).equals(style2 = style2.trim())) {
            return true;
        }
        int SEMICOLON = 59;
        String DELIM = " *+(?>; *+)++";
        Object[] styleRules1 = style1.split(" *+(?>; *+)++");
        if (styleRules1.length != (styleRules2 = style2.split(" *+(?>; *+)++")).length) {
            return false;
        }
        Arrays.sort(styleRules1);
        Arrays.sort(styleRules2);
        String COLON_W_SPACES = " *+: *+";
        String COLON = ":";
        for (int i = 0; i < styleRules1.length; ++i) {
            styleRules1[i] = ((String)styleRules1[i]).replaceFirst(" *+: *+", ":");
            styleRules2[i] = ((String)styleRules2[i]).replaceFirst(" *+: *+", ":");
            if (((String)styleRules1[i]).equals(styleRules2[i])) continue;
            return false;
        }
        return true;
    }

    public static boolean sameClassSet(String classSet1, String classSet2) {
        Object[] set2;
        if (classSet1 == null) {
            return classSet2 == null;
        }
        if (classSet2 == null) {
            return false;
        }
        classSet1 = classSet1.replaceAll(NL_TAB_REGEXP, SPACE);
        classSet2 = classSet2.replaceAll(NL_TAB_REGEXP, SPACE);
        if ((classSet1 = classSet1.trim()).equals(classSet2 = classSet2.trim())) {
            return true;
        }
        String DELIM = " ++";
        Object[] set1 = classSet1.split(" ++");
        if (set1.length != (set2 = classSet2.split(" ++")).length) {
            return false;
        }
        Arrays.sort(set1);
        Arrays.sort(set2);
        return Arrays.equals(set1, set2);
    }

    public static String normalizeStyleString(String styleVal) {
        if (styleVal == null || styleVal.length() == 0) {
            return styleVal;
        }
        styleVal = styleVal.replaceAll(NL_TAB_REGEXP, SPACE);
        styleVal = styleVal.replaceAll(" ++", SPACE);
        if ((styleVal = styleVal.trim()).length() == 0) {
            return styleVal;
        }
        int SEMICOLON = 59;
        String DELIM = " *+(?>; *+)++";
        Object[] styleRules = styleVal.split(" *+(?>; *+)++");
        Arrays.sort(styleRules);
        String COLON_W_SPACES = " *+: *+";
        String COLON = ":";
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < styleRules.length; ++i) {
            result.append(((String)styleRules[i]).replaceFirst(" *+: *+", ":")).append("; ");
        }
        result.setLength(result.length() - 2);
        return result.toString();
    }

    public static String normalizeClassString(String classVal) {
        if (classVal == null || classVal.length() == 0) {
            return classVal;
        }
        classVal = classVal.replaceAll(NL_TAB_REGEXP, SPACE);
        classVal = classVal.trim();
        String DELIM = " ++";
        Object[] classNames = classVal.split(" ++");
        Arrays.sort(classNames);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < classNames.length; ++i) {
            result.append((String)classNames[i]).append(SPACE);
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    public static void main(String[] args) {
        String s1 = "margin-left:50px;font-size:16pt;";
        String s2 = "    font-size  :  16pt    ;  ;   ;  ; margin-left  : 50px   ";
        System.out.println("equal? -" + AttributesMap.equivalentStyles(s1, s2));
    }
}

