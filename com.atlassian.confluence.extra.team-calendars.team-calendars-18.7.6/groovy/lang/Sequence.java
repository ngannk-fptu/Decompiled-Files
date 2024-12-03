/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class Sequence
extends ArrayList
implements GroovyObject {
    private MetaClass metaClass = InvokerHelper.getMetaClass(this.getClass());
    private Class type;
    private int hashCode;

    public Sequence() {
        this((Class)null);
    }

    public Sequence(Class type) {
        this.type = type;
    }

    public Sequence(Class type, List content) {
        super(content.size());
        this.type = type;
        this.addAll((Collection)content);
    }

    public void set(Collection collection) {
        this.checkCollectionType(collection);
        this.clear();
        this.addAll(collection);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Sequence) {
            return this.equals((Sequence)that);
        }
        return false;
    }

    public boolean equals(Sequence that) {
        if (this.size() == that.size()) {
            for (int i = 0; i < this.size(); ++i) {
                if (DefaultTypeTransformation.compareEqual(this.get(i), that.get(i))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            for (int i = 0; i < this.size(); ++i) {
                Object value = this.get(i);
                int hash = value != null ? value.hashCode() : 47806;
                this.hashCode ^= hash;
            }
            if (this.hashCode == 0) {
                this.hashCode = 47806;
            }
        }
        return this.hashCode;
    }

    public int minimumSize() {
        return 0;
    }

    public Class type() {
        return this.type;
    }

    @Override
    public void add(int index, Object element) {
        this.checkType(element);
        this.hashCode = 0;
        super.add(index, element);
    }

    @Override
    public boolean add(Object element) {
        this.checkType(element);
        this.hashCode = 0;
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection c) {
        this.checkCollectionType(c);
        this.hashCode = 0;
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        this.checkCollectionType(c);
        this.hashCode = 0;
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        this.hashCode = 0;
        super.clear();
    }

    @Override
    public Object remove(int index) {
        this.hashCode = 0;
        return super.remove(index);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        this.hashCode = 0;
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public Object set(int index, Object element) {
        this.hashCode = 0;
        return super.set(index, element);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return this.getMetaClass().invokeMethod((Object)this, name, args);
        }
        catch (MissingMethodException e) {
            ArrayList<Object> answer = new ArrayList<Object>(this.size());
            for (Object element : this) {
                Object value = InvokerHelper.invokeMethod(element, name, args);
                answer.add(value);
            }
            return answer;
        }
    }

    @Override
    public Object getProperty(String property) {
        return this.getMetaClass().getProperty(this, property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.getMetaClass().setProperty(this, property, newValue);
    }

    @Override
    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    protected void checkCollectionType(Collection c) {
        if (this.type != null) {
            for (Object element : c) {
                this.checkType(element);
            }
        }
    }

    protected void checkType(Object object) {
        if (object == null) {
            throw new NullPointerException("Sequences cannot contain null, use a List instead");
        }
        if (this.type != null && !this.type.isInstance(object)) {
            throw new IllegalArgumentException("Invalid type of argument for sequence of type: " + this.type.getName() + " cannot add object: " + object);
        }
    }
}

