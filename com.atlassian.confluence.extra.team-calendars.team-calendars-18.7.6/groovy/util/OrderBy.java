/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.codehaus.groovy.runtime.NumberAwareComparator;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class OrderBy<T>
implements Comparator<T>,
Serializable {
    private static final long serialVersionUID = 8385130064804116654L;
    private final List<Closure> closures;
    private boolean equalityCheck;
    private final NumberAwareComparator<Object> numberAwareComparator = new NumberAwareComparator();

    public OrderBy() {
        this(new ArrayList<Closure>(), false);
    }

    public OrderBy(boolean equalityCheck) {
        this(new ArrayList<Closure>(), equalityCheck);
    }

    public OrderBy(Closure closure) {
        this(closure, false);
    }

    public OrderBy(Closure closure, boolean equalityCheck) {
        this(new ArrayList<Closure>(), equalityCheck);
        this.closures.add(closure);
    }

    public OrderBy(List<Closure> closures) {
        this(closures, false);
    }

    public OrderBy(List<Closure> closures, boolean equalityCheck) {
        this.equalityCheck = equalityCheck;
        this.closures = closures;
    }

    public void add(Closure closure) {
        this.closures.add(closure);
    }

    @Override
    public int compare(T object1, T object2) {
        for (Closure closure : this.closures) {
            int result;
            Object value1 = closure.call((Object)object1);
            Object value2 = closure.call((Object)object2);
            if (!this.equalityCheck || value1 instanceof Comparable && value2 instanceof Comparable) {
                result = this.numberAwareComparator.compare(value1, value2);
            } else {
                int n = result = DefaultTypeTransformation.compareEqual(value1, value2) ? 0 : -1;
            }
            if (result == 0) continue;
            return result;
        }
        return 0;
    }

    public boolean isEqualityCheck() {
        return this.equalityCheck;
    }

    public void setEqualityCheck(boolean equalityCheck) {
        this.equalityCheck = equalityCheck;
    }
}

