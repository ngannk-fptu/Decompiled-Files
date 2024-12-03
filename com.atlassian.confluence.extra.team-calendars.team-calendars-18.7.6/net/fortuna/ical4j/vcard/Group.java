/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Group
implements Serializable {
    private static final long serialVersionUID = -424118146831940666L;
    public static final Group WORK = new Group(Id.WORK);
    public static final Group HOME = new Group(Id.HOME);
    private final Id id;
    private String extendedName = "";

    public Group(String extendedName) {
        this(Id.EXTENDED);
        this.extendedName = extendedName;
    }

    public Group(Id id) {
        this.id = id;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public final String toString() {
        StringBuilder b = new StringBuilder();
        if (Id.EXTENDED.equals((Object)this.id)) {
            b.append(this.extendedName);
        } else {
            b.append((Object)this.id);
        }
        return b.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Id {
        WORK,
        HOME,
        EXTENDED;

    }
}

