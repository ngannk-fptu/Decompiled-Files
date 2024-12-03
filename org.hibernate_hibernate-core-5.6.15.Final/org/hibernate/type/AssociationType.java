/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Map;
import java.util.Set;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public interface AssociationType
extends Type {
    public ForeignKeyDirection getForeignKeyDirection();

    public boolean useLHSPrimaryKey();

    public String getLHSPropertyName();

    public String getRHSUniqueKeyPropertyName();

    public Joinable getAssociatedJoinable(SessionFactoryImplementor var1) throws MappingException;

    public String getAssociatedEntityName(SessionFactoryImplementor var1) throws MappingException;

    public String getOnCondition(String var1, SessionFactoryImplementor var2, Map var3) throws MappingException;

    public String getOnCondition(String var1, SessionFactoryImplementor var2, Map var3, Set<String> var4);

    public boolean isAlwaysDirtyChecked();
}

