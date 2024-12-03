/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;
import com.hazelcast.query.impl.Extractable;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.AbstractJsonGetter;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@BinaryInterface
public abstract class AbstractPredicate<K, V>
implements Predicate<K, V>,
IdentifiedDataSerializable {
    String attributeName;
    private volatile transient AttributeType attributeType;

    protected AbstractPredicate() {
    }

    protected AbstractPredicate(String attributeName) {
        this.attributeName = PredicateUtils.canonicalizeAttribute(attributeName);
    }

    @Override
    public boolean apply(Map.Entry<K, V> mapEntry) {
        Object attributeValue = this.readAttributeValue(mapEntry);
        if (attributeValue instanceof MultiResult) {
            return this.applyForMultiResult((MultiResult)attributeValue);
        }
        if (attributeValue instanceof Collection || attributeValue instanceof Object[]) {
            throw new IllegalArgumentException(String.format("Cannot use %s predicate with an array or a collection attribute", this.getClass().getSimpleName()));
        }
        return this.convertAndApplyForSingleAttributeValue(attributeValue);
    }

    private boolean applyForMultiResult(MultiResult result) {
        List results = result.getResults();
        for (Object o : results) {
            Comparable entryValue = (Comparable)o;
            boolean satisfied = this.convertAndApplyForSingleAttributeValue(entryValue);
            if (!satisfied) continue;
            return true;
        }
        return false;
    }

    private boolean convertAndApplyForSingleAttributeValue(Object attributeValue) {
        if (attributeValue instanceof JsonValue) {
            if (attributeValue == NonTerminalJsonValue.INSTANCE) {
                return false;
            }
            attributeValue = AbstractJsonGetter.convertFromJsonValue((JsonValue)attributeValue);
        }
        return this.applyForSingleAttributeValue((Comparable)attributeValue);
    }

    protected abstract boolean applyForSingleAttributeValue(Comparable var1);

    protected Comparable convert(Comparable entryAttributeValue, Comparable givenAttributeValue) {
        if (PredicateUtils.isNull(givenAttributeValue)) {
            return givenAttributeValue;
        }
        AttributeType type = this.attributeType;
        if (type == null) {
            if (entryAttributeValue == null) {
                return givenAttributeValue;
            }
            this.attributeType = type = QueryableEntry.extractAttributeType(entryAttributeValue);
        }
        return this.convert(type, entryAttributeValue, givenAttributeValue);
    }

    private Comparable convert(AttributeType entryAttributeType, Comparable entryAttributeValue, Comparable givenAttributeValue) {
        Class<?> entryAttributeClass;
        Class<?> clazz = entryAttributeClass = entryAttributeValue != null ? entryAttributeValue.getClass() : null;
        if (entryAttributeType == AttributeType.ENUM) {
            return entryAttributeType.getConverter().convert(givenAttributeValue);
        }
        if (entryAttributeClass != null && entryAttributeClass.isAssignableFrom(givenAttributeValue.getClass())) {
            return givenAttributeValue;
        }
        if (entryAttributeType != null) {
            return entryAttributeType.getConverter().convert(givenAttributeValue);
        }
        throw new QueryException("Unknown attribute type: " + givenAttributeValue.getClass().getName() + " for attribute: " + this.attributeName);
    }

    private Object readAttributeValue(Map.Entry entry) {
        Extractable extractable = (Extractable)((Object)entry);
        return extractable.getAttributeValue(this.attributeName);
    }

    Object convertEnumValue(Object attributeValue) {
        if (attributeValue != null && attributeValue.getClass().isEnum()) {
            attributeValue = attributeValue.toString();
        }
        return attributeValue;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributeName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributeName = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AbstractPredicate)) {
            return false;
        }
        AbstractPredicate that = (AbstractPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        return this.attributeName != null ? this.attributeName.equals(that.attributeName) : that.attributeName == null;
    }

    public boolean canEqual(Object other) {
        return other instanceof AbstractPredicate;
    }

    public int hashCode() {
        return this.attributeName != null ? this.attributeName.hashCode() : 0;
    }
}

