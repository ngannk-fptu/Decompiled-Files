/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.asm;

final class $Item {
    int a;
    int b;
    int c;
    long d;
    String g;
    String h;
    String i;
    int j;
    $Item k;

    $Item() {
    }

    $Item(int n) {
        this.a = n;
    }

    $Item(int n, $Item $Item) {
        this.a = n;
        this.b = $Item.b;
        this.c = $Item.c;
        this.d = $Item.d;
        this.g = $Item.g;
        this.h = $Item.h;
        this.i = $Item.i;
        this.j = $Item.j;
    }

    void a(int n) {
        this.b = 3;
        this.c = n;
        this.j = Integer.MAX_VALUE & this.b + n;
    }

    void a(long l) {
        this.b = 5;
        this.d = l;
        this.j = Integer.MAX_VALUE & this.b + (int)l;
    }

    void a(float f) {
        this.b = 4;
        this.c = Float.floatToRawIntBits(f);
        this.j = Integer.MAX_VALUE & this.b + (int)f;
    }

    void a(double d) {
        this.b = 6;
        this.d = Double.doubleToRawLongBits(d);
        this.j = Integer.MAX_VALUE & this.b + (int)d;
    }

    void a(int n, String string, String string2, String string3) {
        this.b = n;
        this.g = string;
        this.h = string2;
        this.i = string3;
        switch (n) {
            case 1: 
            case 7: 
            case 8: 
            case 13: {
                this.j = Integer.MAX_VALUE & n + string.hashCode();
                return;
            }
            case 12: {
                this.j = Integer.MAX_VALUE & n + string.hashCode() * string2.hashCode();
                return;
            }
        }
        this.j = Integer.MAX_VALUE & n + string.hashCode() * string2.hashCode() * string3.hashCode();
    }

    boolean a($Item $Item) {
        if ($Item.b == this.b) {
            switch (this.b) {
                case 3: 
                case 4: {
                    return $Item.c == this.c;
                }
                case 5: 
                case 6: 
                case 15: {
                    return $Item.d == this.d;
                }
                case 1: 
                case 7: 
                case 8: 
                case 13: {
                    return $Item.g.equals(this.g);
                }
                case 14: {
                    return $Item.c == this.c && $Item.g.equals(this.g);
                }
                case 12: {
                    return $Item.g.equals(this.g) && $Item.h.equals(this.h);
                }
            }
            return $Item.g.equals(this.g) && $Item.h.equals(this.h) && $Item.i.equals(this.i);
        }
        return false;
    }
}

