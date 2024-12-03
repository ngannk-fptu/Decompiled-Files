/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.tree.ComponentJoin;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementFactory;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.util.ASTIterator;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class FromClause
extends HqlSqlWalkerNode
implements HqlSqlTokenTypes,
DisplayableNode {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(FromClause.class);
    public static final int ROOT_LEVEL = 1;
    private int level = 1;
    private Set<FromElement> fromElements = new LinkedHashSet<FromElement>();
    private Map<String, FromElement> fromElementByClassAlias = new HashMap<String, FromElement>();
    private Map<String, FromElement> fromElementByTableAlias = new HashMap<String, FromElement>();
    private Map<String, FromElement> fromElementsByPath = new HashMap<String, FromElement>();
    private Map collectionJoinFromElementsByPath = new HashMap();
    private FromClause parentFromClause;
    private Set<FromClause> childFromClauses;
    private int fromElementCounter;
    private List impliedElements = new LinkedList();
    private static ASTUtil.FilterPredicate fromElementPredicate = new ASTUtil.IncludePredicate(){

        @Override
        public boolean include(AST node) {
            FromElement fromElement = (FromElement)node;
            return fromElement.isFromOrJoinFragment();
        }
    };
    private static ASTUtil.FilterPredicate projectionListPredicate = new ASTUtil.IncludePredicate(){

        @Override
        public boolean include(AST node) {
            FromElement fromElement = (FromElement)node;
            return fromElement.inProjectionList();
        }
    };
    private static ASTUtil.FilterPredicate collectionFetchPredicate = new ASTUtil.IncludePredicate(){

        @Override
        public boolean include(AST node) {
            FromElement fromElement = (FromElement)node;
            return fromElement.isFetch() && fromElement.getQueryableCollection() != null;
        }
    };
    private static ASTUtil.FilterPredicate explicitFromPredicate = new ASTUtil.IncludePredicate(){

        @Override
        public boolean include(AST node) {
            FromElement fromElement = (FromElement)node;
            return !fromElement.isImplied();
        }
    };

    public FromElement addFromElement(String path, AST alias) throws SemanticException {
        String classAlias = alias == null ? null : alias.getText();
        this.checkForDuplicateClassAlias(classAlias);
        FromElementFactory factory = new FromElementFactory(this, null, path, classAlias, null, false);
        return factory.addFromElement();
    }

    void registerFromElement(FromElement element) {
        String tableAlias;
        this.fromElements.add(element);
        String classAlias = element.getClassAlias();
        if (classAlias != null) {
            this.fromElementByClassAlias.put(classAlias, element);
        }
        if ((tableAlias = element.getTableAlias()) != null) {
            this.fromElementByTableAlias.put(tableAlias, element);
        }
    }

    void moveFromElementToEnd(FromElement element) {
        this.fromElements.remove(element);
        this.fromElements.add(element);
        for (FromElement fromElement : element.getDestinations()) {
            if (this != fromElement.getFromClause()) continue;
            this.fromElements.remove(fromElement);
            this.fromElements.add(fromElement);
        }
    }

    public void finishInit() {
        FromElement lastFromElement = null;
        for (FromElement fromElement : this.fromElements) {
            if (fromElement instanceof ComponentJoin) continue;
            fromElement.setFirstChild(null);
            fromElement.setNextSibling(null);
            if (lastFromElement != null) {
                ASTUtil.appendChild((AST)lastFromElement, (AST)fromElement);
            }
            lastFromElement = fromElement;
        }
    }

    void addDuplicateAlias(String alias, FromElement element) {
        if (alias != null) {
            this.fromElementByClassAlias.put(alias, element);
        }
    }

    private void checkForDuplicateClassAlias(String classAlias) throws SemanticException {
        if (classAlias != null && this.fromElementByClassAlias.containsKey(classAlias)) {
            throw new SemanticException("Duplicate definition of alias '" + classAlias + "'");
        }
    }

    public FromElement getFromElement(String aliasOrClassName) {
        FromElement fromElement = this.fromElementByClassAlias.get(aliasOrClassName);
        if (fromElement == null && this.getSessionFactoryHelper().isStrictJPAQLComplianceEnabled()) {
            fromElement = this.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(aliasOrClassName);
        }
        if (fromElement == null && this.parentFromClause != null) {
            fromElement = this.parentFromClause.getFromElement(aliasOrClassName);
        }
        return fromElement;
    }

    public FromElement findFromElementBySqlAlias(String sqlAlias) {
        FromElement fromElement = this.fromElementByTableAlias.get(sqlAlias);
        if (fromElement == null && this.parentFromClause != null) {
            fromElement = this.parentFromClause.getFromElement(sqlAlias);
        }
        return fromElement;
    }

    public FromElement findFromElementByUserOrSqlAlias(String userAlias, String sqlAlias) {
        FromElement fromElement = null;
        if (userAlias != null) {
            fromElement = this.getFromElement(userAlias);
        }
        if (fromElement == null) {
            fromElement = this.findFromElementBySqlAlias(sqlAlias);
        }
        return fromElement;
    }

    private FromElement findIntendedAliasedFromElementBasedOnCrazyJPARequirements(String specifiedAlias) {
        for (Map.Entry<String, FromElement> entry : this.fromElementByClassAlias.entrySet()) {
            String alias = entry.getKey();
            if (!alias.equalsIgnoreCase(specifiedAlias)) continue;
            return entry.getValue();
        }
        return null;
    }

    public boolean isFromElementAlias(String possibleAlias) {
        boolean isAlias = this.containsClassAlias(possibleAlias);
        if (!isAlias && this.parentFromClause != null) {
            isAlias = this.parentFromClause.isFromElementAlias(possibleAlias);
        }
        return isAlias;
    }

    public List getFromElements() {
        return ASTUtil.collectChildren((AST)this, fromElementPredicate);
    }

    public FromElement getFromElement() {
        return (FromElement)this.getFromElements().get(0);
    }

    public List getProjectionList() {
        return ASTUtil.collectChildren((AST)this, projectionListPredicate);
    }

    public List getCollectionFetches() {
        return ASTUtil.collectChildren((AST)this, collectionFetchPredicate);
    }

    public boolean hasCollectionFecthes() {
        return this.getCollectionFetches().size() > 0;
    }

    public List getExplicitFromElements() {
        return ASTUtil.collectChildren((AST)this, explicitFromPredicate);
    }

    FromElement findCollectionJoin(String path) {
        return (FromElement)this.collectionJoinFromElementsByPath.get(path);
    }

    FromElement findJoinByPath(String path) {
        FromElement elem = this.findJoinByPathLocal(path);
        if (elem == null && this.parentFromClause != null) {
            elem = this.parentFromClause.findJoinByPath(path);
        }
        return elem;
    }

    FromElement findJoinByPathLocal(String path) {
        Map<String, FromElement> joinsByPath = this.fromElementsByPath;
        return joinsByPath.get(path);
    }

    void addJoinByPathMap(String path, FromElement destination) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("addJoinByPathMap() : %s -> %s", path, destination.getDisplayText());
        }
        this.fromElementsByPath.put(path, destination);
    }

    public boolean containsClassAlias(String alias) {
        boolean isAlias = this.fromElementByClassAlias.containsKey(alias);
        if (!isAlias && this.getSessionFactoryHelper().isStrictJPAQLComplianceEnabled()) {
            isAlias = this.findIntendedAliasedFromElementBasedOnCrazyJPARequirements(alias) != null;
        }
        return isAlias;
    }

    public boolean containsTableAlias(String alias) {
        return this.fromElementByTableAlias.keySet().contains(alias);
    }

    @Override
    public String getDisplayText() {
        return "FromClause{level=" + this.level + ", fromElementCounter=" + this.fromElementCounter + ", fromElements=" + this.fromElements.size() + ", fromElementByClassAlias=" + this.fromElementByClassAlias.keySet() + ", fromElementByTableAlias=" + this.fromElementByTableAlias.keySet() + ", fromElementsByPath=" + this.fromElementsByPath.keySet() + ", collectionJoinFromElementsByPath=" + this.collectionJoinFromElementsByPath.keySet() + ", impliedElements=" + this.impliedElements + "}";
    }

    public void setParentFromClause(FromClause parentFromClause) {
        this.parentFromClause = parentFromClause;
        if (parentFromClause != null) {
            this.level = parentFromClause.getLevel() + 1;
            parentFromClause.addChild(this);
        }
    }

    private void addChild(FromClause fromClause) {
        if (this.childFromClauses == null) {
            this.childFromClauses = new HashSet<FromClause>();
        }
        this.childFromClauses.add(fromClause);
    }

    public FromClause locateChildFromClauseWithJoinByPath(String path) {
        if (this.childFromClauses != null && !this.childFromClauses.isEmpty()) {
            for (FromClause child : this.childFromClauses) {
                if (child.findJoinByPathLocal(path) == null) continue;
                return child;
            }
        }
        return null;
    }

    public void promoteJoin(FromElement elem) {
        LOG.debugf("Promoting [%s] to [%s]", elem, this);
    }

    public boolean isSubQuery() {
        return this.parentFromClause != null;
    }

    void addCollectionJoinFromElementByPath(String path, FromElement destination) {
        LOG.debugf("addCollectionJoinFromElementByPath() : %s -> %s", path, destination);
        this.collectionJoinFromElementsByPath.put(path, destination);
    }

    public FromClause getParentFromClause() {
        return this.parentFromClause;
    }

    public int getLevel() {
        return this.level;
    }

    public int nextFromElementCounter() {
        return this.fromElementCounter++;
    }

    public void resolve() {
        ASTIterator iter = new ASTIterator(this.getFirstChild());
        HashSet<Object> childrenInTree = new HashSet<Object>();
        while (iter.hasNext()) {
            childrenInTree.add(iter.next());
        }
        for (FromElement fromElement : this.fromElements) {
            if (childrenInTree.contains(fromElement)) continue;
            throw new IllegalStateException("Element not in AST: " + fromElement);
        }
    }

    public void addImpliedFromElement(FromElement element) {
        this.impliedElements.add(element);
    }

    public String toString() {
        return "FromClause{level=" + this.level + "}";
    }
}

