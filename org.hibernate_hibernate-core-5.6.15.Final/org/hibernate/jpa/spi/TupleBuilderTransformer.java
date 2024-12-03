/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.TupleElement
 */
package org.hibernate.jpa.spi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.hibernate.Query;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.type.Type;

public class TupleBuilderTransformer
extends BasicTransformerAdapter {
    private List<TupleElement<?>> tupleElements;
    private Map<String, HqlTupleElementImpl> tupleElementsByAlias;

    public TupleBuilderTransformer(Query hqlQuery) {
        Type[] resultTypes = hqlQuery.getReturnTypes();
        int tupleSize = resultTypes.length;
        this.tupleElements = CollectionHelper.arrayList(tupleSize);
        String[] aliases = hqlQuery.getReturnAliases();
        boolean hasAliases = aliases != null && aliases.length > 0;
        this.tupleElementsByAlias = hasAliases ? CollectionHelper.mapOfSize(tupleSize) : Collections.emptyMap();
        for (int i = 0; i < tupleSize; ++i) {
            String alias;
            HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(i, aliases == null ? null : aliases[i], resultTypes[i]);
            this.tupleElements.add(tupleElement);
            if (!hasAliases || (alias = aliases[i]) == null) continue;
            this.tupleElementsByAlias.put(alias, tupleElement);
        }
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (tuple.length != this.tupleElements.size()) {
            throw new IllegalArgumentException("Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" + this.tupleElements.size() + "]");
        }
        return new HqlTupleImpl(tuple);
    }

    public class HqlTupleImpl
    implements Tuple {
        private Object[] tuple;

        public HqlTupleImpl(Object[] tuple) {
            this.tuple = tuple;
        }

        public <X> X get(String alias, Class<X> type) {
            Object untyped = this.get(alias);
            if (untyped != null && !type.isInstance(untyped)) {
                throw new IllegalArgumentException(String.format("Requested tuple value [alias=%s, value=%s] cannot be assigned to requested type [%s]", alias, untyped, type.getName()));
            }
            return (X)untyped;
        }

        public Object get(String alias) {
            HqlTupleElementImpl tupleElement = (HqlTupleElementImpl)TupleBuilderTransformer.this.tupleElementsByAlias.get(alias);
            if (tupleElement == null) {
                throw new IllegalArgumentException("Unknown alias [" + alias + "]");
            }
            return this.tuple[tupleElement.getPosition()];
        }

        public <X> X get(int i, Class<X> type) {
            Object result = this.get(i);
            if (result != null && !type.isInstance(result)) {
                throw new IllegalArgumentException(String.format("Requested tuple value [index=%s, realType=%s] cannot be assigned to requested type [%s]", i, result.getClass().getName(), type.getName()));
            }
            return (X)result;
        }

        public Object get(int i) {
            if (i < 0) {
                throw new IllegalArgumentException("requested tuple index must be greater than zero");
            }
            if (i >= this.tuple.length) {
                throw new IllegalArgumentException("requested tuple index exceeds actual tuple size");
            }
            return this.tuple[i];
        }

        public Object[] toArray() {
            return this.tuple;
        }

        public List<TupleElement<?>> getElements() {
            return TupleBuilderTransformer.this.tupleElements;
        }

        public <X> X get(TupleElement<X> tupleElement) {
            if (HqlTupleElementImpl.class.isInstance(tupleElement)) {
                return this.get(((HqlTupleElementImpl)tupleElement).getPosition(), tupleElement.getJavaType());
            }
            return this.get(tupleElement.getAlias(), tupleElement.getJavaType());
        }
    }

    public static class HqlTupleElementImpl<X>
    implements TupleElement<X> {
        private final int position;
        private final String alias;
        private final Type hibernateType;

        public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
            this.position = position;
            this.alias = alias;
            this.hibernateType = hibernateType;
        }

        public Class getJavaType() {
            return this.hibernateType.getReturnedClass();
        }

        public String getAlias() {
            return this.alias;
        }

        public int getPosition() {
            return this.position;
        }

        public Type getHibernateType() {
            return this.hibernateType;
        }
    }
}

