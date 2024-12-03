/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.tuple.AbstractNonIdentifierAttribute;
import org.hibernate.tuple.BaselineAttributeInformation;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.hibernate.tuple.ValueGeneration;
import org.hibernate.type.Type;

@Deprecated
public class StandardProperty
extends AbstractNonIdentifierAttribute
implements NonIdentifierAttribute {
    public StandardProperty(String name, Type type, boolean lazy, boolean insertable, boolean updateable, ValueGeneration valueGenerationStrategy, boolean nullable, boolean checkable, boolean versionable, CascadeStyle cascadeStyle, FetchMode fetchMode) {
        super(null, null, -1, name, type, new BaselineAttributeInformation.Builder().setLazy(lazy).setInsertable(insertable).setUpdateable(updateable).setValueGenerationStrategy(valueGenerationStrategy).setNullable(nullable).setDirtyCheckable(checkable).setVersionable(versionable).setCascadeStyle(cascadeStyle).setFetchMode(fetchMode).createInformation());
    }
}

