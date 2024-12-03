/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.util;

import java.io.Serializable;
import java.util.Locale;
import javax.media.jai.util.JaiI18N;

public final class CaselessStringKey
implements Cloneable,
Serializable {
    private String name;
    private String lowerCaseName;

    public CaselessStringKey(String name) {
        this.setName(name);
    }

    public int hashCode() {
        return this.lowerCaseName.hashCode();
    }

    public String getName() {
        return this.name;
    }

    private String getLowerCaseName() {
        return this.lowerCaseName;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("CaselessStringKey0"));
        }
        this.name = name;
        this.lowerCaseName = name.toLowerCase(Locale.ENGLISH);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof CaselessStringKey) {
            return this.lowerCaseName.equals(((CaselessStringKey)o).getLowerCaseName());
        }
        return false;
    }

    public String toString() {
        return this.getName();
    }
}

