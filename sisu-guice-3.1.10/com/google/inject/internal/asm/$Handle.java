/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

public final class $Handle {
    final int a;
    final String b;
    final String c;
    final String d;

    public $Handle(int n, String string, String string2, String string3) {
        this.a = n;
        this.b = string;
        this.c = string2;
        this.d = string3;
    }

    public int getTag() {
        return this.a;
    }

    public String getOwner() {
        return this.b;
    }

    public String getName() {
        return this.c;
    }

    public String getDesc() {
        return this.d;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof $Handle)) {
            return false;
        }
        $Handle $Handle = ($Handle)object;
        return this.a == $Handle.a && this.b.equals($Handle.b) && this.c.equals($Handle.c) && this.d.equals($Handle.d);
    }

    public int hashCode() {
        return this.a + this.b.hashCode() * this.c.hashCode() * this.d.hashCode();
    }

    public String toString() {
        return this.b + '.' + this.c + this.d + " (" + this.a + ')';
    }
}

