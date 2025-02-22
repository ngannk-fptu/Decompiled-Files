/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils.res;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.xml.utils.res.CharArrayWrapper;

public class XResourceBundle
extends ListResourceBundle {
    public static final String ERROR_RESOURCES = "org.apache.xalan.res.XSLTErrorResources";
    public static final String XSLT_RESOURCE = "org.apache.xml.utils.res.XResourceBundle";
    public static final String LANG_BUNDLE_NAME = "org.apache.xml.utils.res.XResources";
    public static final String MULT_ORDER = "multiplierOrder";
    public static final String MULT_PRECEDES = "precedes";
    public static final String MULT_FOLLOWS = "follows";
    public static final String LANG_ORIENTATION = "orientation";
    public static final String LANG_RIGHTTOLEFT = "rightToLeft";
    public static final String LANG_LEFTTORIGHT = "leftToRight";
    public static final String LANG_NUMBERING = "numbering";
    public static final String LANG_ADDITIVE = "additive";
    public static final String LANG_MULT_ADD = "multiplicative-additive";
    public static final String LANG_MULTIPLIER = "multiplier";
    public static final String LANG_MULTIPLIER_CHAR = "multiplierChar";
    public static final String LANG_NUMBERGROUPS = "numberGroups";
    public static final String LANG_NUM_TABLES = "tables";
    public static final String LANG_ALPHABET = "alphabet";
    public static final String LANG_TRAD_ALPHABET = "tradAlphabet";

    public static final XResourceBundle loadResourceBundle(String className, Locale locale) throws MissingResourceException {
        String suffix = XResourceBundle.getResourceSuffix(locale);
        try {
            String resourceName = className + suffix;
            return (XResourceBundle)ResourceBundle.getBundle(resourceName, locale);
        }
        catch (MissingResourceException e) {
            try {
                return (XResourceBundle)ResourceBundle.getBundle(XSLT_RESOURCE, new Locale("en", "US"));
            }
            catch (MissingResourceException e2) {
                throw new MissingResourceException("Could not load any resource bundles.", className, "");
            }
        }
    }

    private static final String getResourceSuffix(Locale locale) {
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        String suffix = "_" + locale.getLanguage();
        if (lang.equals("zh")) {
            suffix = suffix + "_" + country;
        }
        if (country.equals("JP")) {
            suffix = suffix + "_" + country + "_" + variant;
        }
        return suffix;
    }

    @Override
    public Object[][] getContents() {
        return new Object[][]{{"ui_language", "en"}, {"help_language", "en"}, {"language", "en"}, {LANG_ALPHABET, new CharArrayWrapper(new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'})}, {LANG_TRAD_ALPHABET, new CharArrayWrapper(new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'})}, {LANG_ORIENTATION, "LeftToRight"}, {LANG_NUMBERING, LANG_ADDITIVE}};
    }
}

