/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.MappingException;
import org.hibernate.engine.internal.JoinHelper;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.loader.PropertyPath;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.EntityType;

public final class OuterJoinableAssociation {
    private final PropertyPath propertyPath;
    private final AssociationType joinableType;
    private final Joinable joinable;
    private final String lhsAlias;
    private final String[] lhsColumns;
    private final String rhsAlias;
    private final String[] rhsColumns;
    private final JoinType joinType;
    private final String on;
    private final Map enabledFilters;
    private final boolean hasRestriction;

    public static OuterJoinableAssociation createRoot(AssociationType joinableType, String alias, SessionFactoryImplementor factory) {
        return new OuterJoinableAssociation(new PropertyPath(), joinableType, null, null, alias, JoinType.LEFT_OUTER_JOIN, null, false, factory, Collections.EMPTY_MAP);
    }

    public OuterJoinableAssociation(PropertyPath propertyPath, AssociationType joinableType, String lhsAlias, String[] lhsColumns, String rhsAlias, JoinType joinType, String withClause, boolean hasRestriction, SessionFactoryImplementor factory, Map enabledFilters) throws MappingException {
        this.propertyPath = propertyPath;
        this.joinableType = joinableType;
        this.lhsAlias = lhsAlias;
        this.lhsColumns = lhsColumns;
        this.rhsAlias = rhsAlias;
        this.joinType = joinType;
        this.joinable = joinableType.getAssociatedJoinable(factory);
        this.rhsColumns = JoinHelper.getRHSColumnNames(joinableType, factory);
        this.on = joinableType.getOnCondition(rhsAlias, factory, enabledFilters) + (StringHelper.isBlank(withClause) ? "" : " and ( " + withClause + " )");
        this.hasRestriction = hasRestriction;
        this.enabledFilters = enabledFilters;
    }

    public PropertyPath getPropertyPath() {
        return this.propertyPath;
    }

    public JoinType getJoinType() {
        return this.joinType;
    }

    public String getLhsAlias() {
        return this.lhsAlias;
    }

    public String getRHSAlias() {
        return this.rhsAlias;
    }

    public String getRhsAlias() {
        return this.rhsAlias;
    }

    private boolean isOneToOne() {
        if (this.joinableType.isEntityType()) {
            EntityType etype = (EntityType)this.joinableType;
            return etype.isOneToOne();
        }
        return false;
    }

    public AssociationType getJoinableType() {
        return this.joinableType;
    }

    public String getRHSUniqueKeyName() {
        return this.joinableType.getRHSUniqueKeyPropertyName();
    }

    public boolean isCollection() {
        return this.joinableType.isCollectionType();
    }

    public Joinable getJoinable() {
        return this.joinable;
    }

    public boolean hasRestriction() {
        return this.hasRestriction;
    }

    public int getOwner(List associations) {
        if (this.isOneToOne() || this.isCollection()) {
            return OuterJoinableAssociation.getPosition(this.lhsAlias, associations);
        }
        return -1;
    }

    private static int getPosition(String lhsAlias, List associations) {
        int result = 0;
        for (Object association : associations) {
            OuterJoinableAssociation oj = (OuterJoinableAssociation)association;
            if (!oj.getJoinable().consumesEntityAlias()) continue;
            if (oj.rhsAlias.equals(lhsAlias)) {
                return result;
            }
            ++result;
        }
        return -1;
    }

    public void addJoins(JoinFragment outerjoin) throws MappingException {
        outerjoin.addJoin(this.joinable.getTableName(), this.rhsAlias, this.lhsColumns, this.rhsColumns, this.joinType, this.on);
        outerjoin.addJoins(this.joinable.fromJoinFragment(this.rhsAlias, false, true), this.joinable.whereJoinFragment(this.rhsAlias, false, true));
    }

    public void validateJoin(String path) throws MappingException {
        if (this.rhsColumns == null || this.lhsColumns == null || this.lhsColumns.length != this.rhsColumns.length || this.lhsColumns.length == 0) {
            throw new MappingException("invalid join columns for association: " + path);
        }
    }

    public boolean isManyToManyWith(OuterJoinableAssociation other) {
        QueryableCollection persister;
        if (this.joinable.isCollection() && (persister = (QueryableCollection)this.joinable).isManyToMany()) {
            return persister.getElementType() == other.getJoinableType();
        }
        return false;
    }

    public void addManyToManyJoin(JoinFragment outerjoin, QueryableCollection collection) throws MappingException {
        String manyToManyFilter = collection.getManyToManyFilterFragment(this.rhsAlias, this.enabledFilters);
        String condition = manyToManyFilter != null && manyToManyFilter.isEmpty() ? this.on : (this.on != null && this.on.isEmpty() ? manyToManyFilter : this.on + " and " + manyToManyFilter);
        outerjoin.addJoin(this.joinable.getTableName(), this.rhsAlias, this.lhsColumns, this.rhsColumns, this.joinType, condition);
        outerjoin.addJoins(this.joinable.fromJoinFragment(this.rhsAlias, false, true), this.joinable.whereJoinFragment(this.rhsAlias, false, true));
    }
}

