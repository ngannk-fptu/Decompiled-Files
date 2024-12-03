/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.lang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import org.apache.abdera.i18n.lang.InvalidLangTagSyntax;
import org.apache.abdera.i18n.text.CharUtils;
import org.apache.abdera.i18n.text.InvalidCharacterException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Lang
implements Iterable<String>,
Serializable,
Cloneable {
    public static final Lang ANY = new Lang();
    private static final long serialVersionUID = -4620499451615533855L;
    protected final String[] tags;
    protected final Locale locale;

    private Lang() {
        this.tags = new String[]{"*"};
        this.locale = null;
    }

    public Lang(Locale locale) {
        this.tags = locale.toString().replace("_", "-").split("-");
        this.locale = locale;
    }

    public Lang(String tag) {
        this(Lang.parse(tag));
    }

    public Lang(String ... tags) {
        Lang.verify(tags);
        this.tags = tags;
        this.locale = this.initLocale();
    }

    private Locale initLocale() {
        Locale locale = null;
        switch (this.tags.length) {
            case 0: {
                break;
            }
            case 1: {
                locale = new Locale(this.tags[0]);
                break;
            }
            case 2: {
                locale = new Locale(this.tags[0], this.tags[1]);
                break;
            }
            default: {
                locale = new Locale(this.tags[0], this.tags[1], this.tags[2]);
            }
        }
        return locale;
    }

    public String getPrimary() {
        return this.tags[0];
    }

    public String getSubtag(int n) {
        if (n + 1 > this.tags.length) {
            throw new ArrayIndexOutOfBoundsException(n);
        }
        return this.tags[n + 1];
    }

    public int getSubtagCount() {
        return this.tags.length - 1;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (String s : this.tags) {
            if (buf.length() > 0) {
                buf.append('-');
            }
            buf.append(s);
        }
        return buf.toString();
    }

    public static boolean matches(Lang lang, String range) {
        if (range.equals("*")) {
            return true;
        }
        return Lang.matches(lang, new Lang(range));
    }

    public static boolean matches(Lang lang, Lang range) {
        if (range.equals("*")) {
            return true;
        }
        if (lang.equals(range)) {
            return true;
        }
        if (lang.tags.length <= range.tags.length) {
            return false;
        }
        for (int n = 0; n < range.tags.length; ++n) {
            if (lang.tags[n].equalsIgnoreCase(range.tags[n])) continue;
            return false;
        }
        return true;
    }

    public boolean matches(String range) {
        return Lang.matches(this, range);
    }

    public boolean matches(Lang range) {
        return Lang.matches(this, range);
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        result = 31 * result + (this.locale == null ? 0 : this.locale.hashCode());
        for (String tag : this.tags) {
            result = 31 * result + tag.hashCode();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            String s = (String)obj;
            if (s.equals("*")) {
                obj = ANY;
            } else {
                try {
                    obj = new Lang(s);
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Lang other = (Lang)obj;
        if (this.tags.length != other.tags.length) {
            return false;
        }
        for (int n = 0; n < this.tags.length; ++n) {
            if (this.tags[n].equalsIgnoreCase(other.tags[n])) continue;
            return false;
        }
        return true;
    }

    private static void verify(String[] tags) {
        if (tags.length == 0) {
            throw new InvalidLangTagSyntax();
        }
        String primary = tags[0];
        try {
            CharUtils.verify(primary, CharUtils.Profile.ALPHA);
        }
        catch (InvalidCharacterException e) {
            throw new InvalidLangTagSyntax();
        }
        for (int n = 1; n < tags.length; ++n) {
            try {
                CharUtils.verify(tags[n], CharUtils.Profile.ALPHANUM);
                continue;
            }
            catch (InvalidCharacterException e) {
                throw new InvalidLangTagSyntax();
            }
        }
    }

    private static String[] parse(String tag) {
        String[] tags = tag.split("-");
        Lang.verify(tags);
        return tags;
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(this.tags).iterator();
    }
}

