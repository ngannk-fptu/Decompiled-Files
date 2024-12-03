/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.io.Serializable;

public class TimeZoneName
implements Comparable<TimeZoneName>,
Serializable {
    public String name;
    public String id;

    public TimeZoneName(String name) {
        this.name = name;
        this.id = name;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public int compareTo(TimeZoneName that) {
        if (that == this) {
            return 0;
        }
        return this.name.compareTo(that.name);
    }

    public boolean equals(Object o) {
        if (!(o instanceof TimeZoneName)) {
            return false;
        }
        return this.compareTo((TimeZoneName)o) == 0;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

