/*
 * Decompiled with CFR 0.152.
 */
package java_cup.runtime;

public class Symbol {
    public int sym;
    public int parse_state;
    boolean used_by_parser = false;
    public int left;
    public int right;
    public Object value;

    public Symbol(int n) {
        this(n, -1);
        this.left = -1;
        this.right = -1;
        this.value = null;
    }

    Symbol(int n, int n2) {
        this.sym = n;
        this.parse_state = n2;
    }

    public Symbol(int n, int n2, int n3) {
        this(n, n2, n3, null);
    }

    public Symbol(int n, int n2, int n3, Object object) {
        this(n);
        this.left = n2;
        this.right = n3;
        this.value = object;
    }

    public Symbol(int n, Object object) {
        this(n, -1, -1, object);
    }

    public String toString() {
        return "#" + this.sym;
    }
}

