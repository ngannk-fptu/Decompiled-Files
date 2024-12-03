/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.transform;

import java.util.Arrays;
import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

public class AliasToBeanResultTransformer
extends AliasedTupleSubsetResultTransformer {
    private final Class resultClass;
    private boolean isInitialized;
    private String[] aliases;
    private Setter[] setters;

    public AliasToBeanResultTransformer(Class resultClass) {
        if (resultClass == null) {
            throw new IllegalArgumentException("resultClass cannot be null");
        }
        this.isInitialized = false;
        this.resultClass = resultClass;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result;
        try {
            if (!this.isInitialized) {
                this.initialize(aliases);
            } else {
                this.check(aliases);
            }
            result = this.resultClass.newInstance();
            for (int i = 0; i < aliases.length; ++i) {
                if (this.setters[i] == null) continue;
                this.setters[i].set(result, tuple[i], null);
            }
        }
        catch (InstantiationException e) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
        }
        catch (IllegalAccessException e) {
            throw new HibernateException("Could not instantiate resultclass: " + this.resultClass.getName());
        }
        return result;
    }

    private void initialize(String[] aliases) {
        PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(PropertyAccessStrategyBasicImpl.INSTANCE, PropertyAccessStrategyFieldImpl.INSTANCE, PropertyAccessStrategyMapImpl.INSTANCE);
        this.aliases = new String[aliases.length];
        this.setters = new Setter[aliases.length];
        for (int i = 0; i < aliases.length; ++i) {
            String alias = aliases[i];
            if (alias == null) continue;
            this.aliases[i] = alias;
            this.setters[i] = propertyAccessStrategy.buildPropertyAccess(this.resultClass, alias).getSetter();
        }
        this.isInitialized = true;
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException("aliases are different from what is cached; aliases=" + Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AliasToBeanResultTransformer that = (AliasToBeanResultTransformer)o;
        if (!this.resultClass.equals(that.resultClass)) {
            return false;
        }
        return Arrays.equals(this.aliases, that.aliases);
    }

    public int hashCode() {
        int result = this.resultClass.hashCode();
        result = 31 * result + (this.aliases != null ? Arrays.hashCode(this.aliases) : 0);
        return result;
    }
}

