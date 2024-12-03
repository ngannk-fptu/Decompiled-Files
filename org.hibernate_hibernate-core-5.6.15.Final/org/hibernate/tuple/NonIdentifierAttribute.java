/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.tuple.Attribute;
import org.hibernate.tuple.ValueGeneration;

public interface NonIdentifierAttribute
extends Attribute,
AttributeDefinition {
    public boolean isLazy();

    public boolean isInsertable();

    public boolean isUpdateable();

    public ValueGeneration getValueGenerationStrategy();

    @Override
    public boolean isNullable();

    @Deprecated
    public boolean isDirtyCheckable(boolean var1);

    public boolean isDirtyCheckable();

    public boolean isVersionable();

    public CascadeStyle getCascadeStyle();

    public FetchMode getFetchMode();
}

