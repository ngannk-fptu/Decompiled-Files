/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.List;
import javax.persistence.TupleElement;

public interface Tuple {
    public <X> X get(TupleElement<X> var1);

    public <X> X get(String var1, Class<X> var2);

    public Object get(String var1);

    public <X> X get(int var1, Class<X> var2);

    public Object get(int var1);

    public Object[] toArray();

    public List<TupleElement<?>> getElements();
}

