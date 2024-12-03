/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.TupleElement
 */
package org.hibernate.jpa.spi;

import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.hibernate.internal.util.type.PrimitiveWrapperHelper;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.transform.BasicTransformerAdapter;

public class CriteriaQueryTupleTransformer
extends BasicTransformerAdapter {
    private final List<ValueHandlerFactory.ValueHandler> valueHandlers;
    private final List tupleElements;

    public CriteriaQueryTupleTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
        this.valueHandlers = valueHandlers;
        this.tupleElements = tupleElements;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object[] valueHandlerResult;
        if (this.valueHandlers == null) {
            valueHandlerResult = tuple;
        } else {
            valueHandlerResult = new Object[tuple.length];
            for (int i = 0; i < tuple.length; ++i) {
                ValueHandlerFactory.ValueHandler valueHandler = this.valueHandlers.get(i);
                valueHandlerResult[i] = valueHandler == null ? tuple[i] : valueHandler.convert(tuple[i]);
            }
        }
        return this.tupleElements == null ? (valueHandlerResult.length == 1 ? valueHandlerResult[0] : valueHandlerResult) : new TupleImpl(tuple);
    }

    private class TupleImpl
    implements Tuple {
        private final Object[] tuples;

        private TupleImpl(Object[] tuples) {
            if (tuples.length != CriteriaQueryTupleTransformer.this.tupleElements.size()) {
                throw new IllegalArgumentException("Size mismatch between tuple result [" + tuples.length + "] and expected tuple elements [" + CriteriaQueryTupleTransformer.this.tupleElements.size() + "]");
            }
            this.tuples = tuples;
        }

        public <X> X get(TupleElement<X> tupleElement) {
            int index = CriteriaQueryTupleTransformer.this.tupleElements.indexOf(tupleElement);
            if (index < 0) {
                throw new IllegalArgumentException("Requested tuple element did not correspond to element in the result tuple");
            }
            return (X)this.tuples[index];
        }

        public Object get(String alias) {
            int index = -1;
            if (alias != null && (alias = alias.trim()).length() > 0) {
                int i = 0;
                for (TupleElement selection : CriteriaQueryTupleTransformer.this.tupleElements) {
                    if (alias.equals(selection.getAlias())) {
                        index = i;
                        break;
                    }
                    ++i;
                }
            }
            if (index < 0) {
                throw new IllegalArgumentException("Given alias [" + alias + "] did not correspond to an element in the result tuple");
            }
            return this.tuples[index];
        }

        public <X> X get(String alias, Class<X> type) {
            Object untyped = this.get(alias);
            if (untyped != null && !this.elementTypeMatches(type, untyped)) {
                throw new IllegalArgumentException(String.format("Requested tuple value [alias=%s, value=%s] cannot be assigned to requested type [%s]", alias, untyped, type.getName()));
            }
            return (X)untyped;
        }

        public Object get(int i) {
            if (i >= this.tuples.length) {
                throw new IllegalArgumentException("Given index [" + i + "] was outside the range of result tuple size [" + this.tuples.length + "] ");
            }
            return this.tuples[i];
        }

        public <X> X get(int i, Class<X> type) {
            Object result = this.get(i);
            if (result != null && !this.elementTypeMatches(type, result)) {
                throw new IllegalArgumentException(String.format("Requested tuple value [index=%s, realType=%s] cannot be assigned to requested type [%s]", i, result.getClass().getName(), type.getName()));
            }
            return (X)result;
        }

        private <X> boolean elementTypeMatches(Class<X> type, Object untyped) {
            return type.isInstance(untyped) || type.isPrimitive() && PrimitiveWrapperHelper.getDescriptorByPrimitiveType(type).getWrapperClass().isInstance(untyped);
        }

        public Object[] toArray() {
            return this.tuples;
        }

        public List<TupleElement<?>> getElements() {
            return CriteriaQueryTupleTransformer.this.tupleElements;
        }
    }
}

