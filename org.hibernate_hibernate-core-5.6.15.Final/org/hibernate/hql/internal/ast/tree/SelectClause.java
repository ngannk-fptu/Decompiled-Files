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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.hql.internal.ast.tree.MapEntryNode;
import org.hibernate.hql.internal.ast.tree.MapKeyNode;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.SelectExpression;
import org.hibernate.hql.internal.ast.tree.SelectExpressionImpl;
import org.hibernate.hql.internal.ast.tree.SelectExpressionList;
import org.hibernate.hql.internal.ast.util.ASTAppender;
import org.hibernate.hql.internal.ast.util.ASTIterator;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.type.Type;

public class SelectClause
extends SelectExpressionList {
    private boolean prepared;
    private boolean scalarSelect;
    private List fromElementsForLoad = new ArrayList();
    private List alreadyRenderedIdentifiers = new ArrayList();
    private Type[] queryReturnTypes;
    private String[][] columnNames;
    private List collectionFromElements;
    private String[] aliases;
    private int[] columnNamesStartPositions;
    private AggregatedSelectExpression aggregatedSelectExpression;
    public static boolean VERSION2_SQL;

    public boolean isScalarSelect() {
        return this.scalarSelect;
    }

    public boolean isDistinct() {
        return this.getFirstChild() != null && this.getFirstChild().getType() == 16;
    }

    public List getFromElementsForLoad() {
        return this.fromElementsForLoad;
    }

    public Type[] getQueryReturnTypes() {
        return this.queryReturnTypes;
    }

    public String[] getQueryReturnAliases() {
        return this.aliases;
    }

    public String[][] getColumnNames() {
        return this.columnNames;
    }

    public AggregatedSelectExpression getAggregatedSelectExpression() {
        return this.aggregatedSelectExpression;
    }

    public void initializeExplicitSelectClause(FromClause fromClause) throws SemanticException {
        FromElement fromElement;
        if (this.prepared) {
            throw new IllegalStateException("SelectClause was already prepared!");
        }
        ArrayList<Type> queryReturnTypeList = new ArrayList<Type>();
        SelectExpression[] selectExpressions = this.collectSelectExpressions();
        if (this.getParameterPositions().size() > 0 && this.getWalker().getStatementType() != 30) {
            throw new QueryException("Parameters are only supported in SELECT clauses when used as part of a INSERT INTO DML statement");
        }
        if (!this.getWalker().isShallowQuery() && this.getWalker().hasAnyForcibleNotFoundImplicitJoins()) {
            for (SelectExpression selectExpression : selectExpressions) {
                FromReferenceNode selectedPath;
                if (!(selectExpression instanceof FromReferenceNode) || !this.isFromElementSelection(selectedPath = (FromReferenceNode)selectExpression)) continue;
                fromElement = selectedPath.getFromElement();
                this.applyForcibleImplicitNotFoundJoins(fromElement);
            }
        }
        for (SelectExpression selectExpression : selectExpressions) {
            boolean inSubquery;
            if (selectExpression instanceof AggregatedSelectExpression) {
                this.aggregatedSelectExpression = (AggregatedSelectExpression)selectExpression;
                queryReturnTypeList.addAll(this.aggregatedSelectExpression.getAggregatedSelectionTypeList());
                this.scalarSelect = true;
                continue;
            }
            boolean bl = inSubquery = selectExpression instanceof QueryNode && ((QueryNode)selectExpression).getFromClause().getParentFromClause() != null;
            if (this.getWalker().getStatementType() == 30 && inSubquery && ((QueryNode)selectExpression).getSelectClause().getParameterPositions().size() > 0) {
                throw new QueryException("Use of parameters in subqueries of INSERT INTO DML statements is not supported.");
            }
            Type type = selectExpression.getDataType();
            if (type == null) {
                throw new QueryException("No data type for node: " + selectExpression.getClass().getName() + " " + TokenPrinters.SQL_TOKEN_PRINTER.showAsString((AST)selectExpression, ""));
            }
            if (selectExpression.isScalar()) {
                this.scalarSelect = true;
            }
            if (this.isReturnableEntity(selectExpression)) {
                this.fromElementsForLoad.add(selectExpression.getFromElement());
            }
            queryReturnTypeList.add(type);
        }
        this.initAliases(selectExpressions);
        if (!this.getWalker().isShallowQuery()) {
            List fromElements = fromClause.getProjectionList();
            ASTAppender appender = new ASTAppender(this.getASTFactory(), (AST)this);
            int size = fromElements.size();
            Iterator iterator = fromElements.iterator();
            int k = 0;
            while (iterator.hasNext()) {
                fromElement = (FromElement)iterator.next();
                if (fromElement.isFetch()) {
                    boolean collectionOfElements;
                    FromElement origin;
                    if (fromElement.getRealOrigin() == null) {
                        if (fromElement.getOrigin() == null) {
                            throw new QueryException("Unable to determine origin of join fetch [" + fromElement.getDisplayText() + "]");
                        }
                        origin = fromElement.getOrigin();
                    } else {
                        origin = fromElement.getRealOrigin();
                    }
                    if (!this.fromElementsForLoad.contains(origin) && !this.fromElementsForLoad.contains(fromElement.getFetchOrigin())) {
                        throw new QueryException("query specified join fetching, but the owner of the fetched association was not present in the select list [" + fromElement.getDisplayText() + "]");
                    }
                    Type type = fromElement.getSelectType();
                    this.addCollectionFromElement(fromElement);
                    if (type != null && !(collectionOfElements = fromElement.isCollectionOfValuesOrComponents())) {
                        fromElement.setIncludeSubclasses(true);
                        this.fromElementsForLoad.add(fromElement);
                        String text = fromElement.renderIdentifierSelect(size, k);
                        this.alreadyRenderedIdentifiers.add(text);
                        SelectExpressionImpl generatedExpr = (SelectExpressionImpl)appender.append(152, text, false);
                        if (generatedExpr != null) {
                            generatedExpr.setFromElement(fromElement);
                        }
                    }
                }
                ++k;
            }
            this.renderNonScalarSelects(this.collectSelectExpressions(), fromClause);
        }
        if (this.scalarSelect || this.getWalker().isShallowQuery()) {
            this.renderScalarSelects(selectExpressions, fromClause);
        }
        this.finishInitialization(queryReturnTypeList);
    }

    private boolean isFromElementSelection(FromReferenceNode selectedPath) {
        if (selectedPath.getType() == 148) {
            return true;
        }
        return selectedPath instanceof SelectExpressionImpl;
    }

    private void applyForcibleImplicitNotFoundJoins(FromElement fromElement) {
        List<FromElement> destinations = fromElement.getDestinations();
        for (int i = 0; i < destinations.size(); ++i) {
            ImpliedFromElement impliedJoin;
            FromElement destination = destinations.get(i);
            if (destination instanceof ImpliedFromElement && (impliedJoin = (ImpliedFromElement)destination).isForcedNotFoundFetch()) {
                impliedJoin.setInProjectionList(true);
                impliedJoin.setFetch(true);
            }
            this.applyForcibleImplicitNotFoundJoins(destination);
        }
    }

    private void finishInitialization(ArrayList queryReturnTypeList) {
        this.queryReturnTypes = queryReturnTypeList.toArray(new Type[queryReturnTypeList.size()]);
        this.initializeColumnNames();
        this.prepared = true;
    }

    private void initializeColumnNames() {
        this.columnNames = this.getSessionFactoryHelper().generateColumnNames(this.queryReturnTypes);
        this.columnNamesStartPositions = new int[this.columnNames.length];
        int startPosition = 1;
        for (int i = 0; i < this.columnNames.length; ++i) {
            this.columnNamesStartPositions[i] = startPosition;
            startPosition += this.columnNames[i].length;
        }
    }

    public int getColumnNamesStartPosition(int i) {
        return this.columnNamesStartPositions[i];
    }

    public void initializeDerivedSelectClause(FromClause fromClause) throws SemanticException {
        if (this.prepared) {
            throw new IllegalStateException("SelectClause was already prepared!");
        }
        List fromElements = fromClause.getProjectionList();
        ASTAppender appender = new ASTAppender(this.getASTFactory(), (AST)this);
        int size = fromElements.size();
        ArrayList<Type> queryReturnTypeList = new ArrayList<Type>(size);
        Iterator iterator = fromElements.iterator();
        int k = 0;
        while (iterator.hasNext()) {
            boolean collectionOfElements;
            FromElement fromElement = (FromElement)iterator.next();
            Type type = fromElement.getSelectType();
            this.addCollectionFromElement(fromElement);
            if (type != null && !(collectionOfElements = fromElement.isCollectionOfValuesOrComponents())) {
                if (!fromElement.isFetch()) {
                    queryReturnTypeList.add(type);
                }
                this.fromElementsForLoad.add(fromElement);
                String text = fromElement.renderIdentifierSelect(size, k);
                SelectExpressionImpl generatedExpr = (SelectExpressionImpl)appender.append(152, text, false);
                if (generatedExpr != null) {
                    generatedExpr.setFromElement(fromElement);
                }
            }
            ++k;
        }
        SelectExpression[] selectExpressions = this.collectSelectExpressions();
        if (this.getWalker().isShallowQuery()) {
            this.renderScalarSelects(selectExpressions, fromClause);
        } else {
            this.renderNonScalarSelects(selectExpressions, fromClause);
        }
        this.finishInitialization(queryReturnTypeList);
    }

    private void addCollectionFromElement(FromElement fromElement) {
        if (fromElement.isFetch() && fromElement.getQueryableCollection() != null) {
            String suffix;
            if (this.collectionFromElements == null) {
                this.collectionFromElements = new ArrayList();
                suffix = VERSION2_SQL ? "__" : "0__";
            } else {
                suffix = Integer.toString(this.collectionFromElements.size()) + "__";
            }
            this.collectionFromElements.add(fromElement);
            fromElement.setCollectionSuffix(suffix);
        }
    }

    @Override
    protected AST getFirstSelectExpression() {
        AST n;
        for (n = this.getFirstChild(); n != null && (n.getType() == 16 || n.getType() == 4); n = n.getNextSibling()) {
        }
        return n;
    }

    private boolean isReturnableEntity(SelectExpression selectExpression) throws SemanticException {
        boolean isFetchOrValueCollection;
        FromElement fromElement = selectExpression.getFromElement();
        boolean bl = isFetchOrValueCollection = fromElement != null && (fromElement.isFetch() || fromElement.isCollectionOfValuesOrComponents());
        if (isFetchOrValueCollection) {
            return false;
        }
        return selectExpression.isReturnableEntity();
    }

    private void renderScalarSelects(SelectExpression[] se, FromClause currentFromClause) throws SemanticException {
        if (!currentFromClause.isSubQuery()) {
            for (int i = 0; i < se.length; ++i) {
                SelectExpression expr = se[i];
                expr.setScalarColumn(i);
            }
        }
    }

    private void initAliases(SelectExpression[] selectExpressions) {
        if (this.aggregatedSelectExpression == null) {
            this.aliases = new String[selectExpressions.length];
            for (int i = 0; i < selectExpressions.length; ++i) {
                this.aliases[i] = selectExpressions[i].getAlias();
            }
        } else {
            this.aliases = this.aggregatedSelectExpression.getAggregatedAliases();
        }
    }

    private void renderNonScalarSelects(SelectExpression[] selectExpressions, FromClause currentFromClause) throws SemanticException {
        FromElement fromElement;
        ASTAppender appender = new ASTAppender(this.getASTFactory(), (AST)this);
        int size = selectExpressions.length;
        int nonscalarSize = 0;
        for (int i = 0; i < size; ++i) {
            if (selectExpressions[i].isScalar()) continue;
            ++nonscalarSize;
        }
        int j = 0;
        for (int i = 0; i < size; ++i) {
            SelectExpression expr;
            if (selectExpressions[i].isScalar() || (fromElement = (expr = selectExpressions[i]).getFromElement()) == null) continue;
            this.renderNonScalarIdentifiers(fromElement, nonscalarSize, j, expr, appender);
            ++j;
        }
        if (!currentFromClause.isSubQuery()) {
            int k = 0;
            for (int i = 0; i < size; ++i) {
                if (selectExpressions[i].isScalar() || (fromElement = selectExpressions[i].getFromElement()) == null) continue;
                this.renderNonScalarProperties(appender, selectExpressions[i], fromElement, nonscalarSize, k);
                ++k;
            }
        }
    }

    private void renderNonScalarIdentifiers(FromElement fromElement, int nonscalarSize, int j, SelectExpression expr, ASTAppender appender) {
        if (!fromElement.getFromClause().isSubQuery()) {
            if (!this.scalarSelect && !this.getWalker().isShallowQuery()) {
                String text = fromElement.renderIdentifierSelect(nonscalarSize, j);
                expr.setText(text);
            } else {
                String text = fromElement.renderIdentifierSelect(nonscalarSize, j);
                if (!this.alreadyRenderedIdentifiers.contains(text)) {
                    appender.append(150, text, false);
                    this.alreadyRenderedIdentifiers.add(text);
                }
            }
        }
    }

    private void renderNonScalarProperties(ASTAppender appender, SelectExpression selectExpression, FromElement fromElement, int nonscalarSize, int k) {
        MapKeyNode mapKeyNode;
        String text = selectExpression instanceof MapKeyNode ? ((mapKeyNode = (MapKeyNode)selectExpression).getMapKeyEntityFromElement() != null ? mapKeyNode.getMapKeyEntityFromElement().renderMapKeyPropertySelectFragment(nonscalarSize, k) : fromElement.renderPropertySelect(nonscalarSize, k)) : (selectExpression instanceof MapEntryNode ? fromElement.renderMapEntryPropertySelectFragment(nonscalarSize, k) : fromElement.renderPropertySelect(nonscalarSize, k));
        appender.append(150, text, false);
        if (fromElement.getQueryableCollection() != null && fromElement.isFetch()) {
            String subText1 = fromElement.renderCollectionSelectFragment(nonscalarSize, k);
            appender.append(150, subText1, false);
        }
        ASTIterator itr = new ASTIterator((AST)fromElement);
        while (itr.hasNext()) {
            FromElement child = (FromElement)itr.next();
            if (!child.isCollectionOfValuesOrComponents() || !child.isFetch()) continue;
            String subText2 = child.renderValueCollectionSelectFragment(nonscalarSize, nonscalarSize + k);
            appender.append(150, subText2, false);
        }
    }

    public List getCollectionFromElements() {
        return this.collectionFromElements;
    }
}

