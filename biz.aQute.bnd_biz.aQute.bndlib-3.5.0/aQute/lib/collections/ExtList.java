/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import java.util.ArrayList;
import java.util.Collection;

public class ExtList<T>
extends ArrayList<T> {
    private static final long serialVersionUID = 1L;

    @SafeVarargs
    public ExtList(T ... ts) {
        super(ts.length);
        for (T t : ts) {
            this.add(t);
        }
    }

    public ExtList(int size) {
        super(size);
    }

    public ExtList(Collection<? extends T> col) {
        super(col);
    }

    public ExtList(Iterable<? extends T> col) {
        for (T t : col) {
            this.add(t);
        }
    }

    public static ExtList<String> from(String s) {
        return ExtList.from(s, "\\s*,\\s*");
    }

    public static ExtList<String> from(String s, String delimeter) {
        String[] parts;
        ExtList<String> result = new ExtList<String>(new String[0]);
        for (String p : parts = s.split(delimeter)) {
            result.add(p);
        }
        return result;
    }

    public String join() {
        return this.join(",");
    }

    public String join(String del) {
        StringBuilder sb = new StringBuilder();
        String d = "";
        for (Object t : this) {
            sb.append(d);
            d = del;
            if (t == null) continue;
            sb.append(t);
        }
        return sb.toString();
    }
}

