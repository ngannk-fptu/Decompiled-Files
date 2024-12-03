/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646.enums;

import java.util.Locale;
import org.apache.abdera.i18n.rfc4646.Subtag;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Extlang {

    private final String deprecated;
    private final String preferred;
    private final String prefix;
    private final String[] descriptions;

    private Extlang(String dep, String pref, String prefix, String ... desc) {
        this.deprecated = dep;
        this.preferred = pref;
        this.prefix = prefix;
        this.descriptions = desc;
    }

    public String getDeprecated() {
        return this.deprecated;
    }

    public boolean isDeprecated() {
        return this.deprecated != null;
    }

    public String getPreferredValue() {
        return this.preferred;
    }

    public Extlang getPreferred() {
        return this.preferred != null ? Extlang.valueOf(this.preferred.toUpperCase(Locale.US)) : this;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getDescription() {
        return this.descriptions.length > 0 ? this.descriptions[0] : null;
    }

    public String[] getDescriptions() {
        return this.descriptions;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
    }

    public static Extlang valueOf(Subtag subtag) {
        if (subtag == null) {
            return null;
        }
        if (subtag.getType() == Subtag.Type.PRIMARY) {
            return Extlang.valueOf(subtag.getName().toUpperCase(Locale.US));
        }
        throw new IllegalArgumentException("Wrong subtag type");
    }
}

