/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646;

import java.io.Serializable;
import java.util.Locale;
import org.apache.abdera.i18n.rfc4646.enums.Extlang;
import org.apache.abdera.i18n.rfc4646.enums.Language;
import org.apache.abdera.i18n.rfc4646.enums.Region;
import org.apache.abdera.i18n.rfc4646.enums.Script;
import org.apache.abdera.i18n.rfc4646.enums.Singleton;
import org.apache.abdera.i18n.rfc4646.enums.Variant;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Subtag
implements Serializable,
Cloneable,
Comparable<Subtag> {
    private static final long serialVersionUID = -4496128268514329138L;
    private final Type type;
    private final String name;
    private Subtag prev;
    private Subtag next;

    public Subtag(Language language) {
        this(Type.PRIMARY, language.name().toLowerCase(Locale.US));
    }

    public Subtag(Script script) {
        this(Type.SCRIPT, Subtag.toTitleCase(script.name()));
    }

    public Subtag(Region region) {
        this(Type.REGION, Subtag.getRegionName(region.name()));
    }

    private static String getRegionName(String name) {
        return name.startsWith("UN") && name.length() == 5 ? name.substring(2) : name;
    }

    public Subtag(Variant variant) {
        this(Type.VARIANT, Subtag.getVariantName(variant.name().toLowerCase(Locale.US)));
    }

    private static String getVariantName(String name) {
        return name.startsWith("_") ? name.substring(1) : name;
    }

    public Subtag(Extlang extlang) {
        this(Type.EXTLANG, extlang.name().toLowerCase(Locale.US));
    }

    public Subtag(Singleton singleton) {
        this(Type.SINGLETON, singleton.name().toLowerCase(Locale.US));
    }

    public Subtag(Type type, String name) {
        this(type, name, null);
    }

    Subtag() {
        this(Type.WILDCARD, "*");
    }

    public Subtag(Type type, String name, Subtag prev) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        if (prev != null) {
            prev.setNext(this);
        }
    }

    Subtag(Type type, String name, Subtag prev, Subtag next) {
        this.type = type;
        this.name = name;
        this.prev = prev;
        this.next = next;
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.toString();
    }

    void setPrevious(Subtag prev) {
        this.prev = prev;
    }

    public Subtag getPrevious() {
        return this.prev;
    }

    void setNext(Subtag next) {
        this.next = next;
        if (next != null) {
            next.setPrevious(this);
        }
    }

    public Subtag getNext() {
        return this.next;
    }

    public String toString() {
        switch (this.type) {
            case PRIMARY: {
                return this.name.toLowerCase(Locale.US);
            }
            case REGION: {
                return this.name.toUpperCase(Locale.US);
            }
            case SCRIPT: {
                return Subtag.toTitleCase(this.name);
            }
        }
        return this.name.toLowerCase(Locale.US);
    }

    private static String toTitleCase(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() == 0) {
            return string;
        }
        char[] chars = string.toLowerCase(Locale.US).toCharArray();
        chars[0] = (char)(chars[0] - 32);
        return new String(chars);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.toLowerCase(Locale.US).hashCode());
        result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Subtag other = (Subtag)obj;
        if (other.getType() == Type.WILDCARD || this.getType() == Type.WILDCARD) {
            return true;
        }
        if (this.name == null ? other.name != null : !this.name.equalsIgnoreCase(other.name)) {
            return false;
        }
        if (other.getType() == Type.SIMPLE || this.getType() == Type.SIMPLE) {
            return true;
        }
        return !(this.type == null ? other.type != null : !this.type.equals((Object)other.type));
    }

    public Subtag clone() {
        try {
            Subtag tag = (Subtag)super.clone();
            if (this.getNext() != null) {
                tag.setNext(this.getNext().clone());
            }
            return tag;
        }
        catch (CloneNotSupportedException e) {
            return new Subtag(this.type, this.name, this.prev != null ? this.prev.clone() : null, this.next != null ? this.next.clone() : null);
        }
    }

    public boolean isDeprecated() {
        switch (this.type) {
            case PRIMARY: {
                Language e = (Language)((Object)this.getEnum());
                return e.isDeprecated();
            }
            case SCRIPT: {
                Script e = (Script)((Object)this.getEnum());
                return e.isDeprecated();
            }
            case REGION: {
                Region e = (Region)((Object)this.getEnum());
                return e.isDeprecated();
            }
            case VARIANT: {
                Variant e = (Variant)((Object)this.getEnum());
                return e.isDeprecated();
            }
            case EXTLANG: {
                Extlang e = (Extlang)((Object)this.getEnum());
                return e.isDeprecated();
            }
            case EXTENSION: {
                Singleton e = (Singleton)((Object)this.getEnum());
                return e.isDeprecated();
            }
        }
        return false;
    }

    public <T extends Enum<?>> T getEnum() {
        switch (this.type) {
            case PRIMARY: {
                return (T)((Object)Language.valueOf(this));
            }
            case SCRIPT: {
                return (T)((Object)Script.valueOf(this));
            }
            case REGION: {
                return (T)((Object)Region.valueOf(this));
            }
            case VARIANT: {
                return (T)((Object)Variant.valueOf(this));
            }
            case EXTLANG: {
                return (T)((Object)Extlang.valueOf(this));
            }
            case EXTENSION: {
                return (T)((Object)Singleton.valueOf(this));
            }
        }
        return null;
    }

    public boolean isValid() {
        switch (this.type) {
            case PRIMARY: 
            case REGION: 
            case SCRIPT: 
            case VARIANT: 
            case EXTLANG: {
                try {
                    this.getEnum();
                    return true;
                }
                catch (Exception e) {
                    return false;
                }
            }
            case EXTENSION: {
                return this.name.matches("[A-Za-z0-9]{2,8}");
            }
            case GRANDFATHERED: {
                return this.name.matches("[A-Za-z]{1,3}(?:-[A-Za-z0-9]{2,8}){1,2}");
            }
            case PRIVATEUSE: {
                return this.name.matches("[A-Za-z0-9]{1,8}");
            }
            case SINGLETON: {
                return this.name.matches("[A-Za-z]");
            }
            case WILDCARD: {
                return this.name.equals("*");
            }
            case SIMPLE: {
                return this.name.matches("[A-Za-z0-9]{1,8}");
            }
        }
        return false;
    }

    public Subtag canonicalize() {
        switch (this.type) {
            case REGION: {
                Region region = (Region)((Object)this.getEnum());
                return region.getPreferred().newSubtag();
            }
            case PRIMARY: {
                Language language = (Language)((Object)this.getEnum());
                return language.getPreferred().newSubtag();
            }
            case SCRIPT: {
                Script script = (Script)((Object)this.getEnum());
                return script.getPreferred().newSubtag();
            }
            case VARIANT: {
                Variant variant = (Variant)((Object)this.getEnum());
                return variant.getPreferred().newSubtag();
            }
            case EXTLANG: {
                Extlang extlang = (Extlang)((Object)this.getEnum());
                return extlang.getPreferred().newSubtag();
            }
        }
        return this;
    }

    public static Subtag newWildcard() {
        return new Subtag();
    }

    @Override
    public int compareTo(Subtag o) {
        int c = o.type.compareTo(this.type);
        return c != 0 ? c : o.name.compareTo(this.name);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        PRIMARY,
        EXTLANG,
        SCRIPT,
        REGION,
        VARIANT,
        SINGLETON,
        EXTENSION,
        PRIVATEUSE,
        GRANDFATHERED,
        WILDCARD,
        SIMPLE;

    }
}

