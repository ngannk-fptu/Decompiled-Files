/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.collection.ArrayUtils;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Collection;

public abstract class AbstractMultiValueGetter
extends Getter {
    public static final String REDUCER_ANY_TOKEN = "any";
    public static final int DO_NOT_REDUCE = -1;
    public static final int REDUCE_EVERYTHING = -2;
    private final int modifier;
    private final Class resultType;

    public AbstractMultiValueGetter(Getter parent, String modifierSuffix, Class<?> inputType, Class resultType) {
        super(parent);
        boolean isArray = inputType.isArray();
        boolean isCollection = Collection.class.isAssignableFrom(inputType);
        this.modifier = modifierSuffix == null ? -1 : this.parseModifier(modifierSuffix, isArray, isCollection);
        this.resultType = this.getResultType(inputType, resultType);
    }

    protected abstract Object extractFrom(Object var1) throws IllegalAccessException, InvocationTargetException;

    @Override
    Class getReturnType() {
        return this.resultType;
    }

    @Override
    Object getValue(Object obj) throws Exception {
        Object parentObject = this.getParentObject(obj);
        if (parentObject == null) {
            return null;
        }
        if (parentObject instanceof MultiResult) {
            return this.extractFromMultiResult((MultiResult)parentObject);
        }
        Object o = this.extractFrom(parentObject);
        if (this.modifier == -1) {
            return o;
        }
        if (this.modifier == -2) {
            MultiResult collector = new MultiResult();
            this.reduceInto(collector, o);
            return collector;
        }
        return this.getItemAtPositionOrNull(o, this.modifier);
    }

    protected int getModifier() {
        return this.modifier;
    }

    private Class getResultType(Class inputType, Class resultType) {
        if (resultType != null) {
            return resultType;
        }
        if (this.modifier == -1) {
            return inputType;
        }
        if (!inputType.isArray()) {
            throw new IllegalArgumentException("Cannot infer a return type with modifier " + this.modifier + " on type " + inputType.getName());
        }
        return inputType.getComponentType();
    }

    private void collectResult(MultiResult collector, Object parentObject) throws IllegalAccessException, InvocationTargetException {
        if (parentObject == null) {
            collector.add(null);
        } else {
            Object currentObject = this.extractFrom(parentObject);
            if (this.shouldReduce()) {
                this.reduceInto(collector, currentObject);
            } else {
                collector.add(currentObject);
            }
        }
    }

    private Object extractFromMultiResult(MultiResult parentMultiResult) throws IllegalAccessException, InvocationTargetException {
        MultiResult collector = new MultiResult();
        collector.setNullOrEmptyTarget(parentMultiResult.isNullEmptyTarget());
        int size = parentMultiResult.getResults().size();
        for (int i = 0; i < size; ++i) {
            this.collectResult(collector, parentMultiResult.getResults().get(i));
        }
        return collector;
    }

    private boolean shouldReduce() {
        return this.modifier != -1;
    }

    private int parseModifier(String modifierSuffix, boolean isArray, boolean isCollection) {
        if (!isArray && !isCollection) {
            throw new IllegalArgumentException("Reducer is allowed only when extracting from arrays or collections");
        }
        return AbstractMultiValueGetter.parseModifier(modifierSuffix);
    }

    private Object getItemAtPositionOrNull(Object object, int position) {
        if (object == null) {
            return null;
        }
        if (object instanceof Collection) {
            return CollectionUtil.getItemAtPositionOrNull((Collection)object, position);
        }
        if (object instanceof Object[]) {
            return ArrayUtils.getItemAtPositionOrNull((Object[])object, position);
        }
        if (object.getClass().isArray()) {
            return Array.get(object, position);
        }
        throw new IllegalArgumentException("Cannot extract an element from class of type" + object.getClass() + " Collections and Arrays are supported only");
    }

    private Object getParentObject(Object obj) throws Exception {
        return this.parent != null ? this.parent.getValue(obj) : obj;
    }

    private void reduceArrayInto(MultiResult collector, Object[] currentObject) {
        Object[] array = currentObject;
        if (array.length == 0) {
            collector.addNullOrEmptyTarget();
        } else {
            for (int i = 0; i < array.length; ++i) {
                collector.add(array[i]);
            }
        }
    }

    private void reducePrimitiveArrayInto(MultiResult collector, Object primitiveArray) {
        if (primitiveArray instanceof long[]) {
            long[] array = (long[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (long value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof int[]) {
            int[] array = (int[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (int value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof short[]) {
            short[] array = (short[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (short value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof byte[]) {
            byte[] array = (byte[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (byte value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof char[]) {
            char[] array = (char[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (char value : array) {
                    collector.add(Character.valueOf(value));
                }
            }
        } else if (primitiveArray instanceof boolean[]) {
            boolean[] array = (boolean[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (boolean value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof double[]) {
            double[] array = (double[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (double value : array) {
                    collector.add(value);
                }
            }
        } else if (primitiveArray instanceof float[]) {
            float[] array = (float[])primitiveArray;
            if (array.length == 0) {
                collector.addNullOrEmptyTarget();
            } else {
                for (float value : array) {
                    collector.add(Float.valueOf(value));
                }
            }
        } else {
            throw new IllegalArgumentException("unexpected primitive array: " + primitiveArray);
        }
    }

    protected void reduceCollectionInto(MultiResult collector, Collection currentObject) {
        Collection collection = currentObject;
        if (collection.isEmpty()) {
            collector.addNullOrEmptyTarget();
        } else {
            for (Object o : collection) {
                collector.add(o);
            }
        }
    }

    protected void reduceInto(MultiResult collector, Object currentObject) {
        if (this.modifier != -2) {
            Object item = this.getItemAtPositionOrNull(currentObject, this.modifier);
            collector.add(item);
            return;
        }
        if (currentObject == null) {
            collector.addNullOrEmptyTarget();
        } else if (currentObject instanceof Collection) {
            this.reduceCollectionInto(collector, (Collection)currentObject);
        } else if (currentObject instanceof Object[]) {
            this.reduceArrayInto(collector, (Object[])currentObject);
        } else if (currentObject.getClass().isArray()) {
            this.reducePrimitiveArrayInto(collector, currentObject);
        } else {
            throw new IllegalArgumentException("Can't reduce result from a type " + currentObject.getClass() + " Only Collections and Arrays are supported.");
        }
    }

    private static int parseModifier(String modifier) {
        String stringValue = modifier.substring(1, modifier.length() - 1);
        if (REDUCER_ANY_TOKEN.equals(stringValue)) {
            return -2;
        }
        int pos = Integer.parseInt(stringValue);
        if (pos < 0) {
            throw new IllegalArgumentException("Position argument cannot be negative. Passed argument: " + modifier);
        }
        return pos;
    }

    static void validateModifier(String modifier) {
        AbstractMultiValueGetter.parseModifier(modifier);
    }

    protected static String composeAttributeValueExtractionFailedMessage(Member member) {
        return "Attribute value extraction failed for: " + member + ". Make sure attribute values or collection/array attribute value elements are all of the same concrete type. Consider custom attribute extractors if it's impossible or undesirable to reduce the variety of types to a single type, see Custom Attributes section in the reference manual for more details.";
    }
}

