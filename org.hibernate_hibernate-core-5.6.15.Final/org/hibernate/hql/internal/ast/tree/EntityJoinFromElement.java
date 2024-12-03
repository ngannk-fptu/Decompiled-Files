/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementType;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.type.EntityType;

public class EntityJoinFromElement
extends FromElement {
    public EntityJoinFromElement(HqlSqlWalker walker, FromClause fromClause, EntityPersister entityPersister, JoinType joinType, boolean fetchProperties, String alias) {
        this.initialize(walker);
        String tableName = ((Joinable)((Object)entityPersister)).getTableName();
        String tableAlias = fromClause.getAliasGenerator().createName(entityPersister.getEntityName());
        EntityType entityType = (EntityType)((Queryable)entityPersister).getType();
        this.initializeEntity(fromClause, entityPersister.getEntityName(), entityPersister, entityType, alias, tableAlias);
        EntityJoinJoinSequenceImpl joinSequence = new EntityJoinJoinSequenceImpl(this.getSessionFactoryHelper().getFactory(), entityType, tableName, tableAlias, joinType);
        this.setJoinSequence(joinSequence);
        this.setAllPropertyFetch(fetchProperties);
        fromClause.getWalker().addQuerySpaces(entityPersister.getQuerySpaces());
        this.setType(144);
        this.setText(tableName);
    }

    @Override
    public void setText(String s) {
        super.setText(s);
    }

    @Override
    protected void initializeComponentJoin(FromElementType elementType) {
    }

    @Override
    public void initializeCollection(FromClause fromClause, String classAlias, String tableAlias) {
    }

    @Override
    public String getCollectionSuffix() {
        return null;
    }

    @Override
    public void setCollectionSuffix(String suffix) {
    }

    private static class EntityJoinJoinFragment
    extends JoinFragment {
        private final String fragmentString;
        private final String whereFragment;

        public EntityJoinJoinFragment(String fragmentString, String whereFragment) {
            this.fragmentString = fragmentString;
            this.whereFragment = whereFragment;
        }

        @Override
        public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {
        }

        @Override
        public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType, String on) {
        }

        @Override
        public void addCrossJoin(String tableName, String alias) {
        }

        @Override
        public void addJoins(String fromFragment, String whereFragment) {
        }

        @Override
        public String toFromFragmentString() {
            return this.fragmentString;
        }

        @Override
        public String toWhereFragmentString() {
            return this.whereFragment;
        }

        @Override
        public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
        }

        @Override
        public boolean addCondition(String condition) {
            return false;
        }

        @Override
        public JoinFragment copy() {
            return null;
        }
    }

    private static class EntityJoinJoinSequenceImpl
    extends JoinSequence {
        private final SessionFactoryImplementor factory;
        private final String entityTableText;
        private final String entityTableAlias;
        private final EntityType entityType;
        private final JoinType joinType;

        public EntityJoinJoinSequenceImpl(SessionFactoryImplementor factory, EntityType entityType, String entityTableText, String entityTableAlias, JoinType joinType) {
            super(factory);
            this.factory = factory;
            this.entityType = entityType;
            this.entityTableText = entityTableText;
            this.entityTableAlias = entityTableAlias;
            this.joinType = joinType;
            this.setUseThetaStyle(false);
        }

        @Override
        public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins, String withClauseFragment) throws MappingException {
            boolean renderTableGroup;
            String joinString;
            switch (this.joinType) {
                case INNER_JOIN: {
                    joinString = " inner join ";
                    break;
                }
                case LEFT_OUTER_JOIN: {
                    joinString = " left outer join ";
                    break;
                }
                case RIGHT_OUTER_JOIN: {
                    joinString = " right outer join ";
                    break;
                }
                case FULL_JOIN: {
                    joinString = " full outer join ";
                    break;
                }
                default: {
                    throw new AssertionFailure("undefined join type");
                }
            }
            StringBuilder buffer = new StringBuilder();
            AbstractEntityPersister joinable = (AbstractEntityPersister)this.entityType.getAssociatedJoinable(this.factory);
            buffer.append(joinString);
            Set<String> treatAsDeclarations = this.getTreatAsDeclarations();
            boolean include = includeAllSubclassJoins && this.isIncluded(this.entityTableAlias);
            String fromFragment = joinable.fromJoinFragment(this.entityTableAlias, true, include, treatAsDeclarations);
            String whereFragment = joinable.whereJoinFragment(this.entityTableAlias, true, include, treatAsDeclarations);
            boolean bl = renderTableGroup = !fromFragment.isEmpty() && this.joinType != JoinType.INNER_JOIN;
            if (renderTableGroup) {
                buffer.append('(');
            }
            buffer.append(this.entityTableText).append(' ').append(this.entityTableAlias);
            if (renderTableGroup) {
                buffer.append(fromFragment).append(')');
            }
            buffer.append(" on ");
            String filters = this.entityType.getOnCondition(this.entityTableAlias, this.factory, enabledFilters, Collections.emptySet());
            if (fromFragment.isEmpty() || renderTableGroup) {
                buffer.append(filters);
                if (withClauseFragment != null) {
                    if (StringHelper.isNotEmpty(filters)) {
                        buffer.append(" and ");
                    }
                    buffer.append(withClauseFragment);
                }
            } else {
                buffer.append("1=1");
                buffer.append(fromFragment);
                StringBuilder whereBuffer = new StringBuilder(10 + whereFragment.length() + filters.length() + withClauseFragment.length());
                whereBuffer.append(whereFragment);
                if (!filters.isEmpty()) {
                    whereBuffer.append(" and ");
                    whereBuffer.append(filters);
                }
                if (!withClauseFragment.isEmpty()) {
                    whereBuffer.append(" and ");
                    whereBuffer.append(withClauseFragment);
                }
                whereFragment = whereBuffer.toString();
            }
            return new EntityJoinJoinFragment(buffer.toString(), whereFragment);
        }
    }
}

