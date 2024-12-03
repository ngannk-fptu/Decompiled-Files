/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.util.AbstractList;
import java.util.List;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class Tuple
extends AbstractList {
    private final Object[] contents;
    private int hashCode;

    public Tuple(Object[] contents) {
        if (contents == null) {
            throw new NullPointerException();
        }
        this.contents = contents;
    }

    @Override
    public Object get(int index) {
        return this.contents[index];
    }

    @Override
    public int size() {
        return this.contents.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Tuple)) {
            return false;
        }
        Tuple that = (Tuple)o;
        if (this.size() != that.size()) {
            return false;
        }
        for (int i = 0; i < this.contents.length; ++i) {
            if (DefaultTypeTransformation.compareEqual(this.contents[i], that.contents[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            for (int i = 0; i < this.contents.length; ++i) {
                Object value = this.contents[i];
                int hash = value != null ? value.hashCode() : 47806;
                this.hashCode ^= hash;
            }
            if (this.hashCode == 0) {
                this.hashCode = 47806;
            }
        }
        return this.hashCode;
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        Object[] newContent = new Object[size];
        System.arraycopy(this.contents, fromIndex, newContent, 0, size);
        return new Tuple(newContent);
    }
}

