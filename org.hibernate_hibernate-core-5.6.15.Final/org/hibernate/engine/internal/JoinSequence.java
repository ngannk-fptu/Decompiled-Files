/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.engine.internal.JoinHelper;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.sql.QueryJoinFragment;
import org.hibernate.type.AssociationType;

public class JoinSequence {
    private final SessionFactoryImplementor factory;
    private final boolean collectionJoinSubquery;
    private final StringBuilder conditions = new StringBuilder();
    private final List<Join> joins = new ArrayList<Join>();
    private boolean useThetaStyle;
    private String rootAlias;
    private Joinable rootJoinable;
    private Selector selector;
    private JoinSequence next;
    private boolean isFromPart;
    private Set<String> queryReferencedTables;
    private Set<String> treatAsDeclarations;

    public JoinSequence(SessionFactoryImplementor factory) {
        this.factory = factory;
        this.collectionJoinSubquery = factory.getSessionFactoryOptions().isCollectionJoinSubqueryRewriteEnabled();
    }

    public JoinSequence getFromPart() {
        JoinSequence fromPart = new JoinSequence(this.factory);
        fromPart.joins.addAll(this.joins);
        fromPart.useThetaStyle = this.useThetaStyle;
        fromPart.rootAlias = this.rootAlias;
        fromPart.rootJoinable = this.rootJoinable;
        fromPart.selector = this.selector;
        fromPart.next = this.next == null ? null : this.next.getFromPart();
        fromPart.isFromPart = true;
        return fromPart;
    }

    public void applyTreatAsDeclarations(Set<String> treatAsDeclarations) {
        if (treatAsDeclarations == null || treatAsDeclarations.isEmpty()) {
            return;
        }
        if (this.treatAsDeclarations == null) {
            this.treatAsDeclarations = new HashSet<String>();
        }
        this.treatAsDeclarations.addAll(treatAsDeclarations);
    }

    protected Set<String> getTreatAsDeclarations() {
        return this.treatAsDeclarations;
    }

    public JoinSequence copy() {
        JoinSequence copy = new JoinSequence(this.factory);
        copy.joins.addAll(this.joins);
        copy.useThetaStyle = this.useThetaStyle;
        copy.rootAlias = this.rootAlias;
        copy.rootJoinable = this.rootJoinable;
        copy.selector = this.selector;
        copy.next = this.next == null ? null : this.next.copy();
        copy.isFromPart = this.isFromPart;
        copy.conditions.append(this.conditions.toString());
        return copy;
    }

    public JoinSequence addJoin(AssociationType associationType, String alias, JoinType joinType, String[] referencingKey) throws MappingException {
        this.joins.add(new Join(this.factory, associationType, alias, joinType, new String[][]{referencingKey}));
        return this;
    }

    public JoinSequence addJoin(AssociationType associationType, String alias, JoinType joinType, String[][] referencingKeys) throws MappingException {
        this.joins.add(new Join(this.factory, associationType, alias, joinType, referencingKeys));
        return this;
    }

    public JoinSequence addJoin(ImpliedFromElement fromElement) {
        this.joins.addAll(fromElement.getJoinSequence().joins);
        return this;
    }

    public JoinFragment toJoinFragment() throws MappingException {
        return this.toJoinFragment(Collections.EMPTY_MAP, true);
    }

    public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins) throws MappingException {
        return this.toJoinFragment(enabledFilters, includeAllSubclassJoins, null);
    }

    public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins, String withClauseFragment) throws MappingException {
        return this.toJoinFragment(enabledFilters, includeAllSubclassJoins, true, withClauseFragment);
    }

    public JoinFragment toJoinFragment(Map enabledFilters, boolean includeAllSubclassJoins, boolean renderSubclassJoins, String withClauseFragment) throws MappingException {
        Joinable last;
        QueryJoinFragment joinFragment = new QueryJoinFragment(this.factory.getDialect(), this.useThetaStyle);
        if (this.rootJoinable != null) {
            joinFragment.addCrossJoin(this.rootJoinable.getTableName(), this.rootAlias);
            String filterCondition = this.rootJoinable.filterFragment(this.rootAlias, enabledFilters, this.treatAsDeclarations);
            joinFragment.setHasFilterCondition(joinFragment.addCondition(filterCondition));
            this.addSubclassJoins(joinFragment, this.rootAlias, this.rootJoinable, true, includeAllSubclassJoins, this.treatAsDeclarations);
            last = this.rootJoinable;
        } else {
            if (this.needsTableGroupJoin(this.joins, withClauseFragment)) {
                String joinString;
                Iterator<Join> iter = this.joins.iterator();
                Join first = iter.next();
                switch (first.joinType) {
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
                joinFragment.addFromFragmentString(joinString);
                joinFragment.addFromFragmentString(" (");
                joinFragment.addFromFragmentString(first.joinable.getTableName());
                joinFragment.addFromFragmentString(" ");
                joinFragment.addFromFragmentString(first.getAlias());
                for (Join join : this.joins) {
                    if (join != first) {
                        joinFragment.addJoin(join.getJoinable().getTableName(), join.getAlias(), join.getLHSColumns(), JoinHelper.getRHSColumnNames(join.getAssociationType(), this.factory), join.joinType);
                    }
                    this.addSubclassJoins(joinFragment, join.getAlias(), join.getJoinable(), join.joinType == JoinType.INNER_JOIN, includeAllSubclassJoins, this.treatAsDeclarations);
                }
                joinFragment.addFromFragmentString(")");
                joinFragment.addFromFragmentString(" on ");
                String rhsAlias = first.getAlias();
                String[][] lhsColumns = first.getLHSColumns();
                String[] rhsColumns = JoinHelper.getRHSColumnNames(first.getAssociationType(), this.factory);
                if (lhsColumns.length > 1) {
                    joinFragment.addFromFragmentString("(");
                }
                for (int i = 0; i < lhsColumns.length; ++i) {
                    for (int j = 0; j < lhsColumns[i].length; ++j) {
                        joinFragment.addFromFragmentString(lhsColumns[i][j]);
                        joinFragment.addFromFragmentString("=");
                        joinFragment.addFromFragmentString(rhsAlias);
                        joinFragment.addFromFragmentString(".");
                        joinFragment.addFromFragmentString(rhsColumns[j]);
                        if (j >= lhsColumns[i].length - 1) continue;
                        joinFragment.addFromFragmentString(" and ");
                    }
                    if (i >= lhsColumns.length - 1) continue;
                    joinFragment.addFromFragmentString(" or ");
                }
                if (lhsColumns.length > 1) {
                    joinFragment.addFromFragmentString(")");
                }
                joinFragment.addFromFragmentString(" and ");
                joinFragment.addFromFragmentString(withClauseFragment);
                return joinFragment;
            }
            last = null;
        }
        for (Join join : this.joins) {
            String manyToManyFilter;
            String on = join.getAssociationType().getOnCondition(join.getAlias(), this.factory, enabledFilters, this.treatAsDeclarations);
            String condition = last != null && this.isManyToManyRoot(last) && ((QueryableCollection)last).getElementType() == join.getAssociationType() ? ((manyToManyFilter = ((QueryableCollection)last).getManyToManyFilterFragment(join.getAlias(), enabledFilters)) != null && manyToManyFilter.isEmpty() ? on : (on != null && on.isEmpty() ? manyToManyFilter : on + " and " + manyToManyFilter)) : on;
            if (withClauseFragment != null && !this.isManyToManyRoot(join.joinable)) {
                condition = condition + " and " + withClauseFragment;
            }
            joinFragment.addJoin(join.getJoinable().getTableName(), join.getAlias(), join.getLHSColumns(), JoinHelper.getRHSColumnNames(join.getAssociationType(), this.factory), join.joinType, condition);
            if (renderSubclassJoins) {
                this.addSubclassJoins(joinFragment, join.getAlias(), join.getJoinable(), join.joinType == JoinType.INNER_JOIN, includeAllSubclassJoins, this.treatAsDeclarations);
            }
            last = join.getJoinable();
        }
        if (this.next != null) {
            joinFragment.addFragment(this.next.toJoinFragment(enabledFilters, includeAllSubclassJoins));
        }
        joinFragment.addCondition(this.conditions.toString());
        if (this.isFromPart) {
            joinFragment.clearWherePart();
        }
        return joinFragment;
    }

    private boolean needsTableGroupJoin(List<Join> joins, String withClauseFragment) {
        if (!this.collectionJoinSubquery || StringHelper.isEmpty(withClauseFragment)) {
            return false;
        }
        if (joins.size() < 2) {
            return this.isSubclassAliasDereferenced(joins.get(0), withClauseFragment);
        }
        if (joins.get(0).getJoinType() != JoinType.INNER_JOIN) {
            return true;
        }
        if (this.isSubclassAliasDereferenced(joins.get(0), withClauseFragment)) {
            return true;
        }
        for (int i = 1; i < joins.size(); ++i) {
            Join join = joins.get(i);
            if (!this.isAliasDereferenced(withClauseFragment, join.getAlias()) && !this.isSubclassAliasDereferenced(join, withClauseFragment)) continue;
            return true;
        }
        return false;
    }

    private boolean isSubclassAliasDereferenced(Join join, String withClauseFragment) {
        AbstractCollectionPersister collectionPersister;
        Object joinable = join.getJoinable();
        if (joinable instanceof AbstractCollectionPersister && (collectionPersister = (AbstractCollectionPersister)joinable).getElementType().isEntityType()) {
            joinable = collectionPersister.getElementPersister();
        }
        if (joinable instanceof AbstractEntityPersister) {
            AbstractEntityPersister persister = (AbstractEntityPersister)joinable;
            int subclassTableSpan = persister.getSubclassTableSpan();
            for (int j = 1; j < subclassTableSpan; ++j) {
                String subclassAlias = AbstractEntityPersister.generateTableAlias(join.getAlias(), j);
                if (!this.isAliasDereferenced(withClauseFragment, subclassAlias)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isAliasDereferenced(String withClauseFragment, String alias) {
        int index = withClauseFragment.indexOf(alias);
        int dotIndex = index + alias.length();
        return index != -1 && (index == 0 || !Character.isLetterOrDigit(withClauseFragment.charAt(index - 1))) && dotIndex < withClauseFragment.length() && withClauseFragment.charAt(dotIndex) == '.';
    }

    private boolean isManyToManyRoot(Joinable joinable) {
        if (joinable != null && joinable.isCollection()) {
            return ((QueryableCollection)joinable).isManyToMany();
        }
        return false;
    }

    private void addSubclassJoins(JoinFragment joinFragment, String alias, Joinable joinable, boolean innerJoin, boolean includeSubclassJoins, Set<String> treatAsDeclarations) {
        boolean include = includeSubclassJoins && this.isIncluded(alias);
        joinFragment.addJoins(joinable.fromJoinFragment(alias, innerJoin, include, treatAsDeclarations, this.queryReferencedTables), joinable.whereJoinFragment(alias, innerJoin, include, treatAsDeclarations));
    }

    protected boolean isIncluded(String alias) {
        return this.selector != null && this.selector.includeSubclasses(alias);
    }

    public JoinSequence addCondition(String condition) {
        if (!StringHelper.isBlank(condition)) {
            if (!condition.startsWith(" and ")) {
                this.conditions.append(" and ");
            }
            this.conditions.append(condition);
        }
        return this;
    }

    public JoinSequence addCondition(String alias, String[] columns, String condition) {
        for (String column : columns) {
            this.conditions.append(" and ").append(alias).append('.').append(column).append(condition);
        }
        return this;
    }

    public JoinSequence setRoot(Joinable joinable, String alias) {
        this.rootAlias = alias;
        this.rootJoinable = joinable;
        return this;
    }

    public JoinSequence setNext(JoinSequence next) {
        this.next = next;
        return this;
    }

    public JoinSequence setSelector(Selector selector) {
        this.selector = selector;
        return this;
    }

    public JoinSequence setUseThetaStyle(boolean useThetaStyle) {
        this.useThetaStyle = useThetaStyle;
        return this;
    }

    public boolean isThetaStyle() {
        return this.useThetaStyle;
    }

    public void setQueryReferencedTables(Set<String> queryReferencedTables) {
        this.queryReferencedTables = queryReferencedTables;
    }

    public Join getFirstJoin() {
        return this.joins.get(0);
    }

    public JoinSequence copyForCollectionProperty() {
        JoinSequence copy = this.copy();
        copy.joins.clear();
        for (Join join : this.joins) {
            copy.addJoin(join.getAssociationType(), join.getAlias(), JoinType.INNER_JOIN, join.getLHSColumns());
        }
        return copy;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("JoinSequence{");
        if (this.rootJoinable != null) {
            buf.append(this.rootJoinable).append('[').append(this.rootAlias).append(']');
        }
        for (Join join : this.joins) {
            buf.append("->").append(join);
        }
        return buf.append('}').toString();
    }

    public static final class Join {
        private final AssociationType associationType;
        private final Joinable joinable;
        private final JoinType joinType;
        private final String alias;
        private final String[][] lhsColumns;

        Join(SessionFactoryImplementor factory, AssociationType associationType, String alias, JoinType joinType, String[][] lhsColumns) throws MappingException {
            this.associationType = associationType;
            this.joinable = associationType.getAssociatedJoinable(factory);
            this.alias = alias;
            this.joinType = joinType;
            this.lhsColumns = lhsColumns;
        }

        public String getAlias() {
            return this.alias;
        }

        public AssociationType getAssociationType() {
            return this.associationType;
        }

        public Joinable getJoinable() {
            return this.joinable;
        }

        public JoinType getJoinType() {
            return this.joinType;
        }

        public String[][] getLHSColumns() {
            return this.lhsColumns;
        }

        public String toString() {
            return this.joinable.toString() + '[' + this.alias + ']';
        }
    }

    public static interface Selector {
        public boolean includeSubclasses(String var1);
    }
}

