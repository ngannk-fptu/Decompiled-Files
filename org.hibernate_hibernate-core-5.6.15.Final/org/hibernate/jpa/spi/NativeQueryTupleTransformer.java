/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.TupleElement
 */
package org.hibernate.jpa.spi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.hibernate.HibernateException;
import org.hibernate.transform.BasicTransformerAdapter;

public class NativeQueryTupleTransformer
extends BasicTransformerAdapter {
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        return new NativeTupleImpl(tuple, aliases);
    }

    private static class NativeTupleImpl
    implements Tuple {
        private Object[] tuple;
        private Map<String, Object> aliasToValue = new LinkedHashMap<String, Object>();
        private Map<String, String> aliasReferences = new LinkedHashMap<String, String>();

        public NativeTupleImpl(Object[] tuple, String[] aliases) {
            if (tuple == null) {
                throw new HibernateException("Tuple must not be null");
            }
            if (aliases == null) {
                throw new HibernateException("Aliases must not be null");
            }
            if (tuple.length != aliases.length) {
                throw new HibernateException("Got different size of tuples and aliases");
            }
            this.tuple = tuple;
            for (int i = 0; i < tuple.length; ++i) {
                this.aliasToValue.put(aliases[i], tuple[i]);
                this.aliasReferences.put(aliases[i].toLowerCase(), aliases[i]);
            }
        }

        public <X> X get(String alias, Class<X> type) {
            Object untyped = this.get(alias);
            return untyped != null ? (X)type.cast(untyped) : null;
        }

        public Object get(String alias) {
            String aliasReference = this.aliasReferences.get(alias.toLowerCase());
            if (aliasReference != null && this.aliasToValue.containsKey(aliasReference)) {
                return this.aliasToValue.get(aliasReference);
            }
            throw new IllegalArgumentException("Unknown alias [" + alias + "]");
        }

        public <X> X get(int i, Class<X> type) {
            Object untyped = this.get(i);
            return untyped != null ? (X)type.cast(untyped) : null;
        }

        public Object get(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("requested tuple index must be greater than zero");
            }
            if (i >= this.aliasToValue.size()) {
                throw new IllegalArgumentException("requested tuple index exceeds actual tuple size");
            }
            return this.tuple[i];
        }

        public Object[] toArray() {
            return this.tuple;
        }

        public List<TupleElement<?>> getElements() {
            ArrayList elements = new ArrayList(this.aliasToValue.size());
            for (Map.Entry<String, Object> entry : this.aliasToValue.entrySet()) {
                elements.add(new NativeTupleElementImpl(this.getValueClass(entry.getValue()), entry.getKey()));
            }
            return elements;
        }

        private Class<?> getValueClass(Object value) {
            Class valueClass = Object.class;
            if (value != null) {
                valueClass = value.getClass();
            }
            return valueClass;
        }

        public <X> X get(TupleElement<X> tupleElement) {
            return this.get(tupleElement.getAlias(), tupleElement.getJavaType());
        }
    }

    private static class NativeTupleElementImpl<X>
    implements TupleElement<X> {
        private final Class<? extends X> javaType;
        private final String alias;

        public NativeTupleElementImpl(Class<? extends X> javaType, String alias) {
            this.javaType = javaType;
            this.alias = alias;
        }

        public Class<? extends X> getJavaType() {
            return this.javaType;
        }

        public String getAlias() {
            return this.alias;
        }
    }
}

