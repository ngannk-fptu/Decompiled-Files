/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;

public class EnumeratedParameter
implements Serializable {
    private String name;
    private int value;

    public EnumeratedParameter(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public int hashCode() {
        return (this.getClass().getName() + new Integer(this.value)).hashCode();
    }

    public boolean equals(Object o) {
        return o != null && this.getClass().equals(o.getClass()) && (this.name.equals(((EnumeratedParameter)o).getName()) || this.value == ((EnumeratedParameter)o).getValue());
    }

    public String toString() {
        return this.getClass().getName() + ":" + this.name + "=" + String.valueOf(this.value);
    }
}

