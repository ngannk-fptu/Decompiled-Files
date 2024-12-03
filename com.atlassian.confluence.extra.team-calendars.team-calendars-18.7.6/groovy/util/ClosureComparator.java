/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import java.io.Serializable;
import java.util.Comparator;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ClosureComparator<T>
implements Comparator<T>,
Serializable {
    private static final long serialVersionUID = -4593521535656429522L;
    Closure closure;

    public ClosureComparator(Closure closure) {
        this.closure = closure;
    }

    @Override
    public int compare(T object1, T object2) {
        Object value = this.closure.call(object1, object2);
        return DefaultTypeTransformation.intUnbox(value);
    }
}

