/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.rfc4646.enums;

import java.util.Locale;
import org.apache.abdera.i18n.rfc4646.Subtag;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Singleton {
    A("Undefined", -1, null, null),
    B("Undefined", -1, null, null),
    C("Undefined", -1, null, null),
    D("Undefined", -1, null, null),
    E("Undefined", -1, null, null),
    F("Undefined", -1, null, null),
    G("Undefined", -1, null, null),
    H("Undefined", -1, null, null),
    I("Undefined", -1, null, null),
    J("Undefined", -1, null, null),
    K("Undefined", -1, null, null),
    L("Undefined", -1, null, null),
    M("Undefined", -1, null, null),
    N("Undefined", -1, null, null),
    O("Undefined", -1, null, null),
    P("Undefined", -1, null, null),
    Q("Undefined", -1, null, null),
    R("Undefined", -1, null, null),
    S("Undefined", -1, null, null),
    T("Undefined", -1, null, null),
    U("Undefined", -1, null, null),
    V("Undefined", -1, null, null),
    W("Undefined", -1, null, null),
    X("Private Use", 4646, null, null),
    Y("Undefined", -1, null, null),
    Z("Undefined", -1, null, null);

    private final String description;
    private final int rfc;
    private final String deprecated;
    private final String preferred;

    private Singleton(String description, int rfc, String deprecated, String preferred) {
        this.description = description;
        this.rfc = rfc;
        this.deprecated = deprecated;
        this.preferred = preferred;
    }

    public String getDescription() {
        return this.description;
    }

    public int getRFC() {
        return this.rfc;
    }

    public Subtag newSubtag() {
        return new Subtag(this);
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

    public Singleton getPreferred() {
        return this.preferred != null ? Singleton.valueOf(this.preferred.toUpperCase(Locale.US)) : this;
    }

    public static Singleton valueOf(Subtag subtag) {
        if (subtag == null) {
            return null;
        }
        if (subtag.getType() == Subtag.Type.SINGLETON) {
            return Singleton.valueOf(subtag.getName().toUpperCase(Locale.US));
        }
        throw new IllegalArgumentException("Wrong subtag type");
    }
}

