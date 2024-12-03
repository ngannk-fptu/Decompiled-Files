/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class VerticalAlignment
implements Serializable {
    private static final long serialVersionUID = 7272397034325429853L;
    public static final VerticalAlignment TOP = new VerticalAlignment("VerticalAlignment.TOP");
    public static final VerticalAlignment BOTTOM = new VerticalAlignment("VerticalAlignment.BOTTOM");
    public static final VerticalAlignment CENTER = new VerticalAlignment("VerticalAlignment.CENTER");
    private String name;

    private VerticalAlignment(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerticalAlignment)) {
            return false;
        }
        VerticalAlignment alignment = (VerticalAlignment)o;
        return this.name.equals(alignment.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(TOP)) {
            return TOP;
        }
        if (this.equals(BOTTOM)) {
            return BOTTOM;
        }
        if (this.equals(CENTER)) {
            return CENTER;
        }
        return null;
    }
}

