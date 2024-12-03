/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.xerces.dom.DOMMessageFormatter;

public final class HTMLdtd {
    public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
    public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
    public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
    public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
    private static Hashtable _byChar;
    private static Hashtable _byName;
    private static Hashtable _boolAttrs;
    private static Hashtable _elemDefs;
    private static final String ENTITIES_RESOURCE = "HTMLEntities.res";
    private static final int ONLY_OPENING = 1;
    private static final int ELEM_CONTENT = 2;
    private static final int PRESERVE = 4;
    private static final int OPT_CLOSING = 8;
    private static final int EMPTY = 17;
    private static final int ALLOWED_HEAD = 32;
    private static final int CLOSE_P = 64;
    private static final int CLOSE_DD_DT = 128;
    private static final int CLOSE_SELF = 256;
    private static final int CLOSE_TABLE = 512;
    private static final int CLOSE_TH_TD = 16384;

    public static boolean isEmptyTag(String string) {
        return HTMLdtd.isElement(string, 17);
    }

    public static boolean isElementContent(String string) {
        return HTMLdtd.isElement(string, 2);
    }

    public static boolean isPreserveSpace(String string) {
        return HTMLdtd.isElement(string, 4);
    }

    public static boolean isOptionalClosing(String string) {
        return HTMLdtd.isElement(string, 8);
    }

    public static boolean isOnlyOpening(String string) {
        return HTMLdtd.isElement(string, 1);
    }

    public static boolean isClosing(String string, String string2) {
        if (string2.equalsIgnoreCase("HEAD")) {
            return !HTMLdtd.isElement(string, 32);
        }
        if (string2.equalsIgnoreCase("P")) {
            return HTMLdtd.isElement(string, 64);
        }
        if (string2.equalsIgnoreCase("DT") || string2.equalsIgnoreCase("DD")) {
            return HTMLdtd.isElement(string, 128);
        }
        if (string2.equalsIgnoreCase("LI") || string2.equalsIgnoreCase("OPTION")) {
            return HTMLdtd.isElement(string, 256);
        }
        if (string2.equalsIgnoreCase("THEAD") || string2.equalsIgnoreCase("TFOOT") || string2.equalsIgnoreCase("TBODY") || string2.equalsIgnoreCase("TR") || string2.equalsIgnoreCase("COLGROUP")) {
            return HTMLdtd.isElement(string, 512);
        }
        if (string2.equalsIgnoreCase("TH") || string2.equalsIgnoreCase("TD")) {
            return HTMLdtd.isElement(string, 16384);
        }
        return false;
    }

    public static boolean isURI(String string, String string2) {
        return string2.equalsIgnoreCase("href") || string2.equalsIgnoreCase("src");
    }

    public static boolean isBoolean(String string, String string2) {
        String[] stringArray = (String[])_boolAttrs.get(string.toUpperCase(Locale.ENGLISH));
        if (stringArray == null) {
            return false;
        }
        for (int i = 0; i < stringArray.length; ++i) {
            if (!stringArray[i].equalsIgnoreCase(string2)) continue;
            return true;
        }
        return false;
    }

    public static int charFromName(String string) {
        HTMLdtd.initialize();
        Object v = _byName.get(string);
        if (v != null && v instanceof Integer) {
            return (Integer)v;
        }
        return -1;
    }

    public static String fromChar(int n) {
        if (n > 65535) {
            return null;
        }
        HTMLdtd.initialize();
        String string = (String)_byChar.get(new Integer(n));
        return string;
    }

    private static void initialize() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        if (_byName != null) {
            return;
        }
        try {
            _byName = new Hashtable();
            _byChar = new Hashtable();
            inputStream = HTMLdtd.class.getResourceAsStream(ENTITIES_RESOURCE);
            if (inputStream == null) {
                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotFound", new Object[]{ENTITIES_RESOURCE}));
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ASCII"));
            String string = bufferedReader.readLine();
            while (string != null) {
                if (string.length() == 0 || string.charAt(0) == '#') {
                    string = bufferedReader.readLine();
                    continue;
                }
                int n = string.indexOf(32);
                if (n > 1) {
                    String string2 = string.substring(0, n);
                    if (++n < string.length()) {
                        String string3 = string.substring(n);
                        if ((n = string3.indexOf(32)) > 0) {
                            string3 = string3.substring(0, n);
                        }
                        int n2 = Integer.parseInt(string3);
                        HTMLdtd.defineEntity(string2, (char)n2);
                    }
                }
                string = bufferedReader.readLine();
            }
            inputStream.close();
        }
        catch (Exception exception) {
            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "ResourceNotLoaded", new Object[]{ENTITIES_RESOURCE, exception.toString()}));
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private static void defineEntity(String string, char c) {
        if (_byName.get(string) == null) {
            _byName.put(string, new Integer(c));
            _byChar.put(new Integer(c), string);
        }
    }

    private static void defineElement(String string, int n) {
        _elemDefs.put(string, new Integer(n));
    }

    private static void defineBoolean(String string, String string2) {
        HTMLdtd.defineBoolean(string, new String[]{string2});
    }

    private static void defineBoolean(String string, String[] stringArray) {
        _boolAttrs.put(string, stringArray);
    }

    private static boolean isElement(String string, int n) {
        Integer n2 = (Integer)_elemDefs.get(string.toUpperCase(Locale.ENGLISH));
        if (n2 == null) {
            return false;
        }
        return (n2 & n) == n;
    }

    static {
        _elemDefs = new Hashtable();
        HTMLdtd.defineElement("ADDRESS", 64);
        HTMLdtd.defineElement("AREA", 17);
        HTMLdtd.defineElement("BASE", 49);
        HTMLdtd.defineElement("BASEFONT", 17);
        HTMLdtd.defineElement("BLOCKQUOTE", 64);
        HTMLdtd.defineElement("BODY", 8);
        HTMLdtd.defineElement("BR", 17);
        HTMLdtd.defineElement("COL", 17);
        HTMLdtd.defineElement("COLGROUP", 522);
        HTMLdtd.defineElement("DD", 137);
        HTMLdtd.defineElement("DIV", 64);
        HTMLdtd.defineElement("DL", 66);
        HTMLdtd.defineElement("DT", 137);
        HTMLdtd.defineElement("FIELDSET", 64);
        HTMLdtd.defineElement("FORM", 64);
        HTMLdtd.defineElement("FRAME", 25);
        HTMLdtd.defineElement("H1", 64);
        HTMLdtd.defineElement("H2", 64);
        HTMLdtd.defineElement("H3", 64);
        HTMLdtd.defineElement("H4", 64);
        HTMLdtd.defineElement("H5", 64);
        HTMLdtd.defineElement("H6", 64);
        HTMLdtd.defineElement("HEAD", 10);
        HTMLdtd.defineElement("HR", 81);
        HTMLdtd.defineElement("HTML", 10);
        HTMLdtd.defineElement("IMG", 17);
        HTMLdtd.defineElement("INPUT", 17);
        HTMLdtd.defineElement("ISINDEX", 49);
        HTMLdtd.defineElement("LI", 265);
        HTMLdtd.defineElement("LINK", 49);
        HTMLdtd.defineElement("MAP", 32);
        HTMLdtd.defineElement("META", 49);
        HTMLdtd.defineElement("OL", 66);
        HTMLdtd.defineElement("OPTGROUP", 2);
        HTMLdtd.defineElement("OPTION", 265);
        HTMLdtd.defineElement("P", 328);
        HTMLdtd.defineElement("PARAM", 17);
        HTMLdtd.defineElement("PRE", 68);
        HTMLdtd.defineElement("SCRIPT", 36);
        HTMLdtd.defineElement("NOSCRIPT", 36);
        HTMLdtd.defineElement("SELECT", 2);
        HTMLdtd.defineElement("STYLE", 36);
        HTMLdtd.defineElement("TABLE", 66);
        HTMLdtd.defineElement("TBODY", 522);
        HTMLdtd.defineElement("TD", 16392);
        HTMLdtd.defineElement("TEXTAREA", 4);
        HTMLdtd.defineElement("TFOOT", 522);
        HTMLdtd.defineElement("TH", 16392);
        HTMLdtd.defineElement("THEAD", 522);
        HTMLdtd.defineElement("TITLE", 32);
        HTMLdtd.defineElement("TR", 522);
        HTMLdtd.defineElement("UL", 66);
        _boolAttrs = new Hashtable();
        HTMLdtd.defineBoolean("AREA", "href");
        HTMLdtd.defineBoolean("BUTTON", "disabled");
        HTMLdtd.defineBoolean("DIR", "compact");
        HTMLdtd.defineBoolean("DL", "compact");
        HTMLdtd.defineBoolean("FRAME", "noresize");
        HTMLdtd.defineBoolean("HR", "noshade");
        HTMLdtd.defineBoolean("IMAGE", "ismap");
        HTMLdtd.defineBoolean("INPUT", new String[]{"defaultchecked", "checked", "readonly", "disabled"});
        HTMLdtd.defineBoolean("LINK", "link");
        HTMLdtd.defineBoolean("MENU", "compact");
        HTMLdtd.defineBoolean("OBJECT", "declare");
        HTMLdtd.defineBoolean("OL", "compact");
        HTMLdtd.defineBoolean("OPTGROUP", "disabled");
        HTMLdtd.defineBoolean("OPTION", new String[]{"default-selected", "selected", "disabled"});
        HTMLdtd.defineBoolean("SCRIPT", "defer");
        HTMLdtd.defineBoolean("SELECT", new String[]{"multiple", "disabled"});
        HTMLdtd.defineBoolean("STYLE", "disabled");
        HTMLdtd.defineBoolean("TD", "nowrap");
        HTMLdtd.defineBoolean("TH", "nowrap");
        HTMLdtd.defineBoolean("TEXTAREA", new String[]{"disabled", "readonly"});
        HTMLdtd.defineBoolean("UL", "compact");
        HTMLdtd.initialize();
    }
}

