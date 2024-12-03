/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import java.io.InputStream;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class MessageCatalog {
    private String bundleName;
    private Hashtable cache = new Hashtable(5);

    protected MessageCatalog(Class packageMember) {
        this(packageMember, "Messages");
    }

    private MessageCatalog(Class packageMember, String bundle) {
        this.bundleName = packageMember.getName();
        int index = this.bundleName.lastIndexOf(46);
        this.bundleName = index == -1 ? "" : this.bundleName.substring(0, index) + ".";
        this.bundleName = this.bundleName + "resources." + bundle;
    }

    public String getMessage(Locale locale, String messageId) {
        ResourceBundle bundle;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {
            bundle = ResourceBundle.getBundle(this.bundleName, locale);
        }
        catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle(this.bundleName, Locale.ENGLISH);
        }
        return bundle.getString(messageId);
    }

    public String getMessage(Locale locale, String messageId, Object[] parameters) {
        ResourceBundle bundle;
        if (parameters == null) {
            return this.getMessage(locale, messageId);
        }
        for (int i = 0; i < parameters.length; ++i) {
            if (parameters[i] instanceof String || parameters[i] instanceof Number || parameters[i] instanceof Date) continue;
            parameters[i] = parameters[i] == null ? "(null)" : parameters[i].toString();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {
            bundle = ResourceBundle.getBundle(this.bundleName, locale);
        }
        catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle(this.bundleName, Locale.ENGLISH);
        }
        MessageFormat format = new MessageFormat(bundle.getString(messageId));
        format.setLocale(locale);
        StringBuffer result = new StringBuffer();
        result = format.format(parameters, result, new FieldPosition(0));
        return result.toString();
    }

    public Locale chooseLocale(String[] languages) {
        if ((languages = this.canonicalize(languages)) != null) {
            for (int i = 0; i < languages.length; ++i) {
                if (!this.isLocaleSupported(languages[i])) continue;
                return this.getLocale(languages[i]);
            }
        }
        return null;
    }

    private String[] canonicalize(String[] languages) {
        boolean didClone = false;
        int trimCount = 0;
        if (languages == null) {
            return languages;
        }
        for (int i = 0; i < languages.length; ++i) {
            String lang = languages[i];
            int len = lang.length();
            if (len != 2 && len != 5) {
                if (!didClone) {
                    languages = (String[])languages.clone();
                    didClone = true;
                }
                languages[i] = null;
                ++trimCount;
                continue;
            }
            if (len == 2) {
                if ((lang = lang.toLowerCase()) == languages[i]) continue;
                if (!didClone) {
                    languages = (String[])languages.clone();
                    didClone = true;
                }
                languages[i] = lang;
                continue;
            }
            char[] buf = new char[]{Character.toLowerCase(lang.charAt(0)), Character.toLowerCase(lang.charAt(1)), '_', Character.toUpperCase(lang.charAt(3)), Character.toUpperCase(lang.charAt(4))};
            if (!didClone) {
                languages = (String[])languages.clone();
                didClone = true;
            }
            languages[i] = new String(buf);
        }
        if (trimCount != 0) {
            String[] temp = new String[languages.length - trimCount];
            trimCount = 0;
            for (int i = 0; i < temp.length; ++i) {
                while (languages[i + trimCount] == null) {
                    ++trimCount;
                }
                temp[i] = languages[i + trimCount];
            }
            languages = temp;
        }
        return languages;
    }

    private Locale getLocale(String localeName) {
        String country;
        String language;
        int index = localeName.indexOf(95);
        if (index == -1) {
            if (localeName.equals("de")) {
                return Locale.GERMAN;
            }
            if (localeName.equals("en")) {
                return Locale.ENGLISH;
            }
            if (localeName.equals("fr")) {
                return Locale.FRENCH;
            }
            if (localeName.equals("it")) {
                return Locale.ITALIAN;
            }
            if (localeName.equals("ja")) {
                return Locale.JAPANESE;
            }
            if (localeName.equals("ko")) {
                return Locale.KOREAN;
            }
            if (localeName.equals("zh")) {
                return Locale.CHINESE;
            }
            language = localeName;
            country = "";
        } else {
            if (localeName.equals("zh_CN")) {
                return Locale.SIMPLIFIED_CHINESE;
            }
            if (localeName.equals("zh_TW")) {
                return Locale.TRADITIONAL_CHINESE;
            }
            language = localeName.substring(0, index);
            country = localeName.substring(index + 1);
        }
        return new Locale(language, country);
    }

    public boolean isLocaleSupported(String localeName) {
        Boolean value = (Boolean)this.cache.get(localeName);
        if (value != null) {
            return value;
        }
        ClassLoader loader = null;
        while (true) {
            String name = this.bundleName + "_" + localeName;
            try {
                Class.forName(name);
                this.cache.put(localeName, Boolean.TRUE);
                return true;
            }
            catch (Exception e) {
                if (loader == null) {
                    loader = this.getClass().getClassLoader();
                }
                name = name.replace('.', '/');
                name = name + ".properties";
                InputStream in = loader == null ? ClassLoader.getSystemResourceAsStream(name) : loader.getResourceAsStream(name);
                if (in != null) {
                    this.cache.put(localeName, Boolean.TRUE);
                    return true;
                }
                int index = localeName.indexOf(95);
                if (index > 0) {
                    localeName = localeName.substring(0, index);
                    continue;
                }
                this.cache.put(localeName, Boolean.FALSE);
                return false;
            }
            break;
        }
    }
}

