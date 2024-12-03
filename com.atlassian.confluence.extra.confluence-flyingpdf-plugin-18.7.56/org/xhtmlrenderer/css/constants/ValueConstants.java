/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.constants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.util.GeneralUtil;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRRuntimeException;

public final class ValueConstants {
    private static final List TYPE_DESCRIPTIONS;
    private static final Map sacTypesStrings;

    public static String cssType(int cssType, int primitiveValueType) {
        String desc = null;
        if (cssType == 1) {
            if (primitiveValueType >= TYPE_DESCRIPTIONS.size()) {
                desc = "{unknown: " + primitiveValueType + "}";
            } else {
                desc = (String)TYPE_DESCRIPTIONS.get(primitiveValueType);
                if (desc == null) {
                    desc = "{UNKNOWN VALUE TYPE}";
                }
            }
        } else {
            desc = "{value list}";
        }
        return desc;
    }

    public static short sacPrimitiveTypeForString(String type) {
        if ("em".equals(type)) {
            return 3;
        }
        if ("ex".equals(type)) {
            return 4;
        }
        if ("px".equals(type)) {
            return 5;
        }
        if ("%".equals(type)) {
            return 2;
        }
        if ("in".equals(type)) {
            return 8;
        }
        if ("cm".equals(type)) {
            return 6;
        }
        if ("mm".equals(type)) {
            return 7;
        }
        if ("pt".equals(type)) {
            return 9;
        }
        if ("pc".equals(type)) {
            return 10;
        }
        if (type == null) {
            return 5;
        }
        throw new XRRuntimeException("Unknown type on CSS value: " + type);
    }

    public static String stringForSACPrimitiveType(short type) {
        return (String)sacTypesStrings.get(new Short(type));
    }

    public static boolean isAbsoluteUnit(CSSPrimitiveValue primitive) {
        short type = 0;
        type = primitive.getPrimitiveType();
        return ValueConstants.isAbsoluteUnit(type);
    }

    public static boolean isAbsoluteUnit(short type) {
        switch (type) {
            case 2: {
                return false;
            }
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: {
                return true;
            }
            case 0: {
                XRLog.cascade(Level.WARNING, "Asked whether type was absolute, given CSS_UNKNOWN as the type. Might be one of those funny values like background-position.");
                GeneralUtil.dumpShortException(new Exception());
            }
        }
        return false;
    }

    public static String getCssValueTypeDesc(CSSValue cssValue) {
        switch (cssValue.getCssValueType()) {
            case 3: {
                return "CSS_CUSTOM";
            }
            case 0: {
                return "CSS_INHERIT";
            }
            case 1: {
                return "CSS_PRIMITIVE_VALUE";
            }
            case 2: {
                return "CSS_VALUE_LIST";
            }
        }
        return "UNKNOWN";
    }

    public static boolean isNumber(short cssPrimitiveType) {
        switch (cssPrimitiveType) {
            case 2: 
            case 3: 
            case 4: {
                return false;
            }
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: {
                return true;
            }
        }
        return false;
    }

    public static short guessType(String value) {
        int type = 19;
        if (value != null && value.length() > 1) {
            if (value.endsWith("%")) {
                type = 2;
            } else if (value.startsWith("rgb") || value.startsWith("#")) {
                type = 25;
            } else {
                String hmm = value.substring(value.length() - 2);
                if ("pt".equals(hmm)) {
                    type = 9;
                } else if ("px".equals(hmm)) {
                    type = 5;
                } else if ("em".equals(hmm)) {
                    type = 3;
                } else if ("ex".equals(hmm)) {
                    type = 4;
                } else if ("in".equals(hmm)) {
                    type = 8;
                } else if ("cm".equals(hmm)) {
                    type = 6;
                } else if ("mm".equals(hmm)) {
                    type = 7;
                } else if (Character.isDigit(value.charAt(value.length() - 1))) {
                    try {
                        new Float(value);
                        type = 1;
                    }
                    catch (NumberFormatException ex) {
                        type = 19;
                    }
                } else {
                    type = 19;
                }
            }
        }
        return (short)type;
    }

    static {
        TreeMap<Short, String> map = new TreeMap<Short, String>();
        TYPE_DESCRIPTIONS = new ArrayList();
        try {
            Field[] fields = CSSPrimitiveValue.class.getFields();
            for (int i = 0; i < fields.length; ++i) {
                Field f = fields[i];
                int mod = f.getModifiers();
                if (!Modifier.isFinal(mod) || !Modifier.isStatic(mod) || !Modifier.isPublic(mod)) continue;
                Short val = (Short)f.get(null);
                String name = f.getName();
                if (!name.startsWith("CSS_") || name.equals("CSS_INHERIT") || name.equals("CSS_PRIMITIVE_VALUE") || name.equals("CSS_VALUE_LIST") || name.equals("CSS_CUSTOM")) continue;
                map.put(val, name.substring("CSS_".length()));
            }
            ArrayList keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                TYPE_DESCRIPTIONS.add(map.get(iter.next()));
            }
        }
        catch (Exception ex) {
            throw new XRRuntimeException("Could not build static list of CSS type descriptions.", ex);
        }
        sacTypesStrings = new HashMap(25);
        sacTypesStrings.put(new Short(3), "em");
        sacTypesStrings.put(new Short(4), "ex");
        sacTypesStrings.put(new Short(5), "px");
        sacTypesStrings.put(new Short(2), "%");
        sacTypesStrings.put(new Short(8), "in");
        sacTypesStrings.put(new Short(6), "cm");
        sacTypesStrings.put(new Short(7), "mm");
        sacTypesStrings.put(new Short(9), "pt");
        sacTypesStrings.put(new Short(10), "pc");
    }
}

