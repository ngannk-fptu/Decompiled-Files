/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import org.apache.jackrabbit.spi.IdFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.PropertyInfo;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.ItemInfoImpl;

public class PropertyInfoImpl
extends ItemInfoImpl
implements PropertyInfo {
    private final PropertyId propertyId;
    private final int type;
    private final boolean isMultiValued;
    private final QValue[] values;

    public static PropertyInfo createSerializablePropertyInfo(PropertyInfo propertyInfo, IdFactory idFactory) {
        if (propertyInfo instanceof Serializable) {
            return propertyInfo;
        }
        NodeId parentId = propertyInfo.getId().getParentId();
        parentId = idFactory.createNodeId(parentId.getUniqueID(), parentId.getPath());
        PropertyId propId = idFactory.createPropertyId(parentId, propertyInfo.getId().getName());
        return new PropertyInfoImpl(propertyInfo.getPath(), propId, propertyInfo.getType(), propertyInfo.isMultiValued(), propertyInfo.getValues());
    }

    public PropertyInfoImpl(NodeId parentId, Name name, Path path, PropertyId id, int type, boolean isMultiValued, QValue[] values) {
        this(path, id, type, isMultiValued, values);
    }

    public PropertyInfoImpl(Path path, PropertyId id, int type, boolean isMultiValued, QValue[] values) {
        super(path, false);
        this.propertyId = id;
        this.type = type;
        this.isMultiValued = isMultiValued;
        this.values = values;
    }

    @Override
    public PropertyId getId() {
        return this.propertyId;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public boolean isMultiValued() {
        return this.isMultiValued;
    }

    @Override
    public QValue[] getValues() {
        QValue[] vals = new QValue[this.values.length];
        System.arraycopy(this.values, 0, vals, 0, this.values.length);
        return vals;
    }
}

