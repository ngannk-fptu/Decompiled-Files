/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import org.hibernate.boot.jaxb.SourceType;

public class Origin
implements Serializable {
    public static final String UNKNOWN_FILE_PATH = "<unknown>";
    private final SourceType type;
    private final String name;

    public Origin(SourceType type, String name) {
        this.type = type;
        this.name = name;
    }

    public SourceType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Origin)) {
            return false;
        }
        Origin other = (Origin)o;
        return this.type == other.type && Objects.equals(this.name, other.name);
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "Origin(name=%s,type=%s)", new Object[]{this.name, this.type});
    }
}

