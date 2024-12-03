/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.util;

import antlr.collections.AST;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.AssertionFailure;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.hql.internal.antlr.SqlTokenTypes;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.hql.internal.ast.tree.ParameterContainer;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.SqlFragment;
import org.hibernate.hql.internal.ast.tree.TableReferenceNode;
import org.hibernate.hql.internal.ast.util.ASTIterator;
import org.hibernate.hql.internal.ast.util.SyntheticAndFactory;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FilterImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.param.DynamicFilterParameterSpecification;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.JoinType;
import org.hibernate.type.Type;

public class JoinProcessor
implements SqlTokenTypes {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JoinProcessor.class);
    private static final Pattern DYNAMIC_FILTER_PATTERN = Pattern.compile(":(\\w+\\S*)\\s");
    private static final String LITERAL_DELIMITER = "'";
    private final HqlSqlWalker walker;
    private final SyntheticAndFactory syntheticAndFactory;

    public JoinProcessor(HqlSqlWalker walker) {
        this.walker = walker;
        this.syntheticAndFactory = new SyntheticAndFactory(walker);
    }

    public static JoinType toHibernateJoinType(int astJoinType) {
        switch (astJoinType) {
            case 146: {
                return JoinType.LEFT_OUTER_JOIN;
            }
            case 29: {
                return JoinType.INNER_JOIN;
            }
            case 147: {
                return JoinType.RIGHT_OUTER_JOIN;
            }
            case 24: {
                return JoinType.FULL_JOIN;
            }
        }
        throw new AssertionFailure("undefined join type " + astJoinType);
    }

    private Set<String> findQueryReferencedTables(QueryNode query) {
        AbstractEntityPersister aep;
        EntityPersister entityPersister;
        if (!this.walker.getSessionFactoryHelper().getFactory().getSessionFactoryOptions().isOmitJoinOfSuperclassTablesEnabled()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Finding of query referenced tables is skipped because the feature is disabled. See %s", "hibernate.query.omit_join_of_superclass_tables"));
            }
            return null;
        }
        if (CollectionHelper.isNotEmpty(this.walker.getEnabledFilters())) {
            LOG.debug("Finding of query referenced tables is skipped because filters are enabled.");
            return null;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(TokenPrinters.REFERENCED_TABLES_PRINTER.showAsString((AST)query, "Tables referenced from query nodes:"));
        }
        HashSet<String> result = new HashSet<String>();
        this.collectReferencedTables(new ASTIterator((AST)query), result);
        for (FromElement fromElement : query.getFromClause().getFromElements()) {
            AST withClauseAst;
            String role = fromElement.getRole();
            if (role != null) {
                result.add(fromElement.getOrigin().getPropertyTableName(role.substring(role.lastIndexOf(46) + 1)));
            }
            if ((entityPersister = fromElement.getEntityPersister()) instanceof AbstractEntityPersister) {
                aep = (AbstractEntityPersister)entityPersister;
                while (!aep.filterFragment("", Collections.emptyMap()).isEmpty() && aep.getMappedSuperclass() != null) {
                    Collections.addAll(result, aep.getTableNames());
                    aep = (AbstractEntityPersister)this.walker.getSessionFactoryHelper().findEntityPersisterByName(aep.getMappedSuperclass());
                }
            }
            if ((withClauseAst = fromElement.getWithClauseAst()) == null) continue;
            this.collectReferencedTables(new ASTIterator(withClauseAst), result);
        }
        if (query.getSelectClause() != null) {
            for (Object element : query.getSelectClause().getFromElementsForLoad()) {
                FromElement fromElement = (FromElement)element;
                entityPersister = fromElement.getEntityPersister();
                if (entityPersister == null || !(entityPersister instanceof AbstractEntityPersister)) continue;
                aep = (AbstractEntityPersister)entityPersister;
                String[] tables = aep.getTableNames();
                Collections.addAll(result, tables);
            }
        }
        return result;
    }

    private void collectReferencedTables(ASTIterator iterator, Set<String> result) {
        while (iterator.hasNext()) {
            AST withClauseAst;
            SqlFragment sqlFragment;
            FromElement fromElement;
            TableReferenceNode fromReferenceNode;
            String[] tables;
            AST node = iterator.nextNode();
            if (node instanceof TableReferenceNode && (tables = (fromReferenceNode = (TableReferenceNode)node).getReferencedTables()) != null) {
                Collections.addAll(result, tables);
            }
            if (!(node instanceof SqlFragment) || (fromElement = (sqlFragment = (SqlFragment)node).getFromElement()) == null) continue;
            String role = fromElement.getRole();
            if (role != null) {
                result.add(fromElement.getOrigin().getPropertyTableName(role.substring(role.lastIndexOf(46) + 1)));
            }
            if ((withClauseAst = fromElement.getWithClauseAst()) == null) continue;
            this.collectReferencedTables(new ASTIterator(withClauseAst), result);
        }
    }

    public void processJoins(QueryNode query) {
        ListIterator liter;
        ArrayList fromElements;
        final FromClause fromClause = query.getFromClause();
        Set<String> queryReferencedTables = this.findQueryReferencedTables(query);
        if (DotNode.useThetaStyleImplicitJoins) {
            fromElements = new ArrayList();
            liter = fromClause.getFromElements().listIterator(fromClause.getFromElements().size());
            while (liter.hasPrevious()) {
                fromElements.add(liter.previous());
            }
        } else {
            fromElements = new ArrayList(fromClause.getFromElements().size());
            liter = fromClause.getFromElements().listIterator();
            while (liter.hasNext()) {
                final FromElement fromElement = (FromElement)liter.next();
                if (fromElement instanceof ImpliedFromElement && fromElement.getOrigin().getWithClauseFragment() != null && fromElement.getOrigin().getWithClauseFragment().contains(fromElement.getTableAlias())) {
                    fromElement.getOrigin().getJoinSequence().addJoin((ImpliedFromElement)fromElement);
                    fromElement.setText("");
                    continue;
                }
                fromElements.add(fromElement);
            }
        }
        for (final FromElement fromElement : fromElements) {
            JoinSequence join = fromElement.getJoinSequence();
            join.setQueryReferencedTables(queryReferencedTables);
            join.setSelector(new JoinSequence.Selector(){

                @Override
                public boolean includeSubclasses(String alias) {
                    boolean containsTableAlias = fromClause.containsTableAlias(alias);
                    if (fromElement.isDereferencedBySubclassProperty()) {
                        LOG.tracev("Forcing inclusion of extra joins [alias={0}, containsTableAlias={1}]", alias, containsTableAlias);
                        return true;
                    }
                    boolean shallowQuery = JoinProcessor.this.walker.isShallowQuery();
                    boolean includeSubclasses = fromElement.isIncludeSubclasses();
                    boolean subQuery = fromClause.isSubQuery();
                    return includeSubclasses && containsTableAlias && !subQuery && !shallowQuery;
                }
            });
            this.addJoinNodes(query, join, fromElement);
        }
    }

    private void addJoinNodes(QueryNode query, JoinSequence join, FromElement fromElement) {
        JoinFragment joinFragment = join.toJoinFragment(this.walker.getEnabledFilters(), fromElement.useFromFragment() || fromElement.isDereferencedBySuperclassOrSubclassProperty(), fromElement.getWithClauseFragment());
        String frag = joinFragment.toFromFragmentString();
        String whereFrag = joinFragment.toWhereFragmentString();
        if (fromElement.getType() == 143 && (join.isThetaStyle() || StringHelper.isNotEmpty(whereFrag))) {
            fromElement.setType(141);
            fromElement.getJoinSequence().setUseThetaStyle(true);
        }
        if (fromElement.useFromFragment() || fromElement.getFromClause().isSubQuery() && fromElement.isDereferencedBySuperclassOrSubclassProperty()) {
            String fromFragment = this.processFromFragment(frag, join).trim();
            LOG.debugf("Using FROM fragment [%s]", fromFragment);
            JoinProcessor.processDynamicFilterParameters(fromFragment, fromElement, this.walker);
        }
        this.syntheticAndFactory.addWhereFragment(joinFragment, whereFrag, query, fromElement, this.walker);
    }

    private String processFromFragment(String frag, JoinSequence join) {
        String fromFragment = frag.trim();
        if (fromFragment.startsWith(", ")) {
            fromFragment = fromFragment.substring(2);
        }
        return fromFragment;
    }

    public static void processDynamicFilterParameters(String sqlFragment, ParameterContainer container, HqlSqlWalker walker) {
        if (walker.getEnabledFilters().isEmpty() && !JoinProcessor.hasDynamicFilterParam(walker, sqlFragment) && !JoinProcessor.hasCollectionFilterParam(sqlFragment)) {
            return;
        }
        Dialect dialect = walker.getDialect();
        String symbols = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\" + dialect.openQuote() + dialect.closeQuote();
        StringTokenizer tokens = new StringTokenizer(sqlFragment, symbols, true);
        StringBuilder result = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.startsWith(":")) {
                String filterParameterName = token.substring(1);
                String[] parts = LoadQueryInfluencers.parseFilterParameterName(filterParameterName);
                FilterImpl filter = (FilterImpl)walker.getEnabledFilters().get(parts[0]);
                Object value = filter.getParameter(parts[1]);
                Type type = filter.getFilterDefinition().getParameterType(parts[1]);
                String typeBindFragment = String.join((CharSequence)",", ArrayHelper.fillArray("?", type.getColumnSpan(walker.getSessionFactoryHelper().getFactory())));
                String bindFragment = value != null && Collection.class.isInstance(value) ? String.join((CharSequence)",", ArrayHelper.fillArray(typeBindFragment, ((Collection)value).size())) : typeBindFragment;
                result.append(bindFragment);
                container.addEmbeddedParameter(new DynamicFilterParameterSpecification(parts[0], parts[1], type));
                continue;
            }
            result.append(token);
        }
        container.setText(result.toString());
    }

    private static boolean hasDynamicFilterParam(HqlSqlWalker walker, String sqlFragment) {
        String closeQuote = String.valueOf(walker.getDialect().closeQuote());
        Matcher matcher = DYNAMIC_FILTER_PATTERN.matcher(sqlFragment);
        if (matcher.find() && matcher.groupCount() > 0) {
            String match = matcher.group(1);
            return match.endsWith(closeQuote) || match.endsWith(LITERAL_DELIMITER);
        }
        return true;
    }

    private static boolean hasCollectionFilterParam(String sqlFragment) {
        return !sqlFragment.contains("?");
    }
}

