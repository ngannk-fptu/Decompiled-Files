/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.EntityType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.Type;

public class WhereParser
implements Parser {
    private final PathExpressionParser pathExpressionParser = new PathExpressionParser();
    private static final Set<String> EXPRESSION_TERMINATORS = new HashSet<String>();
    private static final Set<String> EXPRESSION_OPENERS = new HashSet<String>();
    private static final Set<String> BOOLEAN_OPERATORS = new HashSet<String>();
    private static final Map<String, String> NEGATIONS = new HashMap<String, String>();
    private boolean betweenSpecialCase;
    private boolean negated;
    private boolean inSubselect;
    private int bracketsSinceSelect;
    private StringBuilder subselect;
    private boolean expectingPathContinuation;
    private int expectingIndex;
    private LinkedList<Boolean> nots;
    private LinkedList<StringBuilder> joins;
    private LinkedList<Boolean> booleanTests;

    public WhereParser() {
        this.pathExpressionParser.setUseThetaStyleJoin(true);
        this.nots = new LinkedList();
        this.joins = new LinkedList();
        this.booleanTests = new LinkedList();
    }

    private String getElementName(PathExpressionParser.CollectionElement element, QueryTranslatorImpl q) throws QueryException {
        String name;
        if (element.isOneToMany) {
            name = element.alias;
        } else {
            Type type = element.elementType;
            if (type.isEntityType()) {
                String entityName = ((EntityType)type).getAssociatedEntityName();
                name = this.pathExpressionParser.continueFromManyToMany(entityName, element.elementColumns, q);
            } else {
                throw new QueryException("illegally dereferenced collection element");
            }
        }
        return name;
    }

    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        boolean pathExpressionContinuesFurther;
        String lcToken = token.toLowerCase(Locale.ROOT);
        if (token.equals("[") && !this.expectingPathContinuation) {
            this.expectingPathContinuation = false;
            if (this.expectingIndex == 0) {
                throw new QueryException("unexpected [");
            }
            return;
        }
        if (token.equals("]")) {
            --this.expectingIndex;
            this.expectingPathContinuation = true;
            return;
        }
        if (this.expectingPathContinuation && (pathExpressionContinuesFurther = this.continuePathExpression(token, q))) {
            return;
        }
        if (!this.inSubselect && (lcToken.equals("select") || lcToken.equals("from"))) {
            this.inSubselect = true;
            this.subselect = new StringBuilder(20);
        }
        if (this.inSubselect && token.equals(")")) {
            --this.bracketsSinceSelect;
            if (this.bracketsSinceSelect == -1) {
                QueryTranslatorImpl subq = new QueryTranslatorImpl(this.subselect.toString(), q.getEnabledFilters(), q.getFactory());
                try {
                    subq.compile(q);
                }
                catch (MappingException me) {
                    throw new QueryException("MappingException occurred compiling subquery", (Exception)((Object)me));
                }
                this.appendToken(q, subq.getSQLString());
                this.inSubselect = false;
                this.bracketsSinceSelect = 0;
            }
        }
        if (this.inSubselect) {
            if (token.equals("(")) {
                ++this.bracketsSinceSelect;
            }
            this.subselect.append(token).append(' ');
            return;
        }
        this.specialCasesBefore(lcToken);
        if (!this.betweenSpecialCase && EXPRESSION_TERMINATORS.contains(lcToken)) {
            this.closeExpression(q, lcToken);
        }
        if (BOOLEAN_OPERATORS.contains(lcToken)) {
            this.booleanTests.removeLast();
            this.booleanTests.addLast(Boolean.TRUE);
        }
        if (lcToken.equals("not")) {
            this.nots.addLast(this.nots.removeLast() == false);
            this.negated = !this.negated;
            return;
        }
        this.doToken(token, q);
        if (!this.betweenSpecialCase && EXPRESSION_OPENERS.contains(lcToken)) {
            this.openExpression(q, lcToken);
        }
        this.specialCasesAfter(lcToken);
    }

    @Override
    public void start(QueryTranslatorImpl q) throws QueryException {
        this.token("(", q);
    }

    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
        if (this.expectingPathContinuation) {
            this.expectingPathContinuation = false;
            PathExpressionParser.CollectionElement element = this.pathExpressionParser.lastCollectionElement();
            if (element.elementColumns.length != 1) {
                throw new QueryException("path expression ended in composite collection element");
            }
            this.appendToken(q, element.elementColumns[0]);
            this.addToCurrentJoin(element);
        }
        this.token(")", q);
    }

    private void closeExpression(QueryTranslatorImpl q, String lcToken) {
        if (this.booleanTests.removeLast().booleanValue()) {
            if (this.booleanTests.size() > 0) {
                this.booleanTests.removeLast();
                this.booleanTests.addLast(Boolean.TRUE);
            }
            this.appendToken(q, this.joins.removeLast().toString());
        } else {
            StringBuilder join = this.joins.removeLast();
            this.joins.getLast().append(join.toString());
        }
        if (this.nots.removeLast().booleanValue()) {
            boolean bl = this.negated = !this.negated;
        }
        if (!")".equals(lcToken)) {
            this.appendToken(q, ")");
        }
    }

    private void openExpression(QueryTranslatorImpl q, String lcToken) {
        this.nots.addLast(Boolean.FALSE);
        this.booleanTests.addLast(Boolean.FALSE);
        this.joins.addLast(new StringBuilder());
        if (!"(".equals(lcToken)) {
            this.appendToken(q, "(");
        }
    }

    private void preprocess(String token, QueryTranslatorImpl q) throws QueryException {
        String[] tokens = StringHelper.split(".", token, true);
        if (tokens.length > 5 && ("elements".equals(tokens[tokens.length - 1]) || "indices".equals(tokens[tokens.length - 1]))) {
            this.pathExpressionParser.start(q);
            for (int i = 0; i < tokens.length - 3; ++i) {
                this.pathExpressionParser.token(tokens[i], q);
            }
            this.pathExpressionParser.token(null, q);
            this.pathExpressionParser.end(q);
            this.addJoin(this.pathExpressionParser.getWhereJoin(), q);
            this.pathExpressionParser.ignoreInitialJoin();
        }
    }

    private void doPathExpression(String token, QueryTranslatorImpl q) throws QueryException {
        this.preprocess(token, q);
        StringTokenizer tokens = new StringTokenizer(token, ".", true);
        this.pathExpressionParser.start(q);
        while (tokens.hasMoreTokens()) {
            this.pathExpressionParser.token(tokens.nextToken(), q);
        }
        this.pathExpressionParser.end(q);
        if (this.pathExpressionParser.isCollectionValued()) {
            this.openExpression(q, "");
            this.appendToken(q, this.pathExpressionParser.getCollectionSubquery(q.getEnabledFilters()));
            this.closeExpression(q, "");
            q.addQuerySpaces(q.getCollectionPersister(this.pathExpressionParser.getCollectionRole()).getCollectionSpaces());
        } else if (this.pathExpressionParser.isExpectingCollectionIndex()) {
            ++this.expectingIndex;
        } else {
            this.addJoin(this.pathExpressionParser.getWhereJoin(), q);
            this.appendToken(q, this.pathExpressionParser.getWhereColumn());
        }
    }

    private void addJoin(JoinSequence joinSequence, QueryTranslatorImpl q) throws QueryException {
        q.addFromJoinOnly(this.pathExpressionParser.getName(), joinSequence);
        try {
            this.addToCurrentJoin(joinSequence.toJoinFragment(q.getEnabledFilters(), true).toWhereFragmentString());
        }
        catch (MappingException me) {
            throw new QueryException((Exception)((Object)me));
        }
    }

    private void doToken(String token, QueryTranslatorImpl q) throws QueryException {
        if (q.isName(StringHelper.root(token))) {
            this.doPathExpression(q.unalias(token), q);
        } else if (token.startsWith(":")) {
            q.addNamedParameter(token.substring(1));
            this.appendToken(q, "?");
        } else if (token.startsWith("?")) {
            if (token.length() == 1) {
                q.addLegacyPositionalParameter();
                this.appendToken(q, "?");
            } else {
                String labelString = token.substring(1);
                try {
                    int label = Integer.parseInt(labelString);
                    q.addOrdinalParameter(label);
                    this.appendToken(q, "?");
                }
                catch (NumberFormatException e) {
                    throw new QueryException("Ordinal parameter label must be numeric : " + labelString, e);
                }
            }
        } else {
            Queryable persister = q.getEntityPersisterUsingImports(token);
            if (persister != null) {
                String discrim = persister.getDiscriminatorSQLValue();
                if ("null".equals(discrim) || "not null".equals(discrim)) {
                    throw new QueryException("subclass test not allowed for null or not null discriminator");
                }
                this.appendToken(q, discrim);
            } else {
                String negatedToken;
                Object constant;
                if (token.indexOf(46) > -1 && (constant = ReflectHelper.getConstantValue(token, q.getFactory())) != null) {
                    Type type;
                    try {
                        type = q.getFactory().getTypeResolver().heuristicType(constant.getClass().getName());
                    }
                    catch (MappingException me) {
                        throw new QueryException((Exception)((Object)me));
                    }
                    if (type == null) {
                        throw new QueryException("Could not determine type of: " + token);
                    }
                    try {
                        this.appendToken(q, ((LiteralType)((Object)type)).objectToSQLString(constant, q.getFactory().getDialect()));
                    }
                    catch (Exception e) {
                        throw new QueryException("Could not format constant value to SQL literal: " + token, e);
                    }
                }
                String string = negatedToken = this.negated ? NEGATIONS.get(token.toLowerCase(Locale.ROOT)) : null;
                if (!(negatedToken == null || this.betweenSpecialCase && "or".equals(negatedToken))) {
                    this.appendToken(q, negatedToken);
                } else {
                    this.appendToken(q, token);
                }
            }
        }
    }

    private void addToCurrentJoin(String sql) {
        this.joins.getLast().append(sql);
    }

    private void addToCurrentJoin(PathExpressionParser.CollectionElement ce) throws QueryException {
        try {
            this.addToCurrentJoin(ce.joinSequence.toJoinFragment().toWhereFragmentString() + ce.indexValue.toString());
        }
        catch (MappingException me) {
            throw new QueryException((Exception)((Object)me));
        }
    }

    private void specialCasesBefore(String lcToken) {
        if (lcToken.equals("between") || lcToken.equals("not between")) {
            this.betweenSpecialCase = true;
        }
    }

    private void specialCasesAfter(String lcToken) {
        if (this.betweenSpecialCase && lcToken.equals("and")) {
            this.betweenSpecialCase = false;
        }
    }

    void appendToken(QueryTranslatorImpl q, String token) {
        if (this.expectingIndex > 0) {
            this.pathExpressionParser.setLastCollectionElementIndexValue(token);
        } else {
            q.appendWhereToken(token);
        }
    }

    private boolean continuePathExpression(String token, QueryTranslatorImpl q) throws QueryException {
        this.expectingPathContinuation = false;
        PathExpressionParser.CollectionElement element = this.pathExpressionParser.lastCollectionElement();
        if (token.startsWith(".")) {
            this.doPathExpression(this.getElementName(element, q) + token, q);
            this.addToCurrentJoin(element);
            return true;
        }
        if (element.elementColumns.length != 1) {
            throw new QueryException("path expression ended in composite collection element");
        }
        this.appendToken(q, element.elementColumns[0]);
        this.addToCurrentJoin(element);
        return false;
    }

    static {
        EXPRESSION_TERMINATORS.add("and");
        EXPRESSION_TERMINATORS.add("or");
        EXPRESSION_TERMINATORS.add(")");
        EXPRESSION_OPENERS.add("and");
        EXPRESSION_OPENERS.add("or");
        EXPRESSION_OPENERS.add("(");
        BOOLEAN_OPERATORS.add("<");
        BOOLEAN_OPERATORS.add("=");
        BOOLEAN_OPERATORS.add(">");
        BOOLEAN_OPERATORS.add("#");
        BOOLEAN_OPERATORS.add("~");
        BOOLEAN_OPERATORS.add("like");
        BOOLEAN_OPERATORS.add("ilike");
        BOOLEAN_OPERATORS.add("regexp");
        BOOLEAN_OPERATORS.add("rlike");
        BOOLEAN_OPERATORS.add("is");
        BOOLEAN_OPERATORS.add("in");
        BOOLEAN_OPERATORS.add("any");
        BOOLEAN_OPERATORS.add("some");
        BOOLEAN_OPERATORS.add("all");
        BOOLEAN_OPERATORS.add("exists");
        BOOLEAN_OPERATORS.add("between");
        BOOLEAN_OPERATORS.add("<=");
        BOOLEAN_OPERATORS.add(">=");
        BOOLEAN_OPERATORS.add("=>");
        BOOLEAN_OPERATORS.add("=<");
        BOOLEAN_OPERATORS.add("!=");
        BOOLEAN_OPERATORS.add("<>");
        BOOLEAN_OPERATORS.add("!#");
        BOOLEAN_OPERATORS.add("!~");
        BOOLEAN_OPERATORS.add("!<");
        BOOLEAN_OPERATORS.add("!>");
        BOOLEAN_OPERATORS.add("is not");
        BOOLEAN_OPERATORS.add("not like");
        BOOLEAN_OPERATORS.add("not ilike");
        BOOLEAN_OPERATORS.add("not regexp");
        BOOLEAN_OPERATORS.add("not rlike");
        BOOLEAN_OPERATORS.add("not in");
        BOOLEAN_OPERATORS.add("not between");
        BOOLEAN_OPERATORS.add("not exists");
        NEGATIONS.put("and", "or");
        NEGATIONS.put("or", "and");
        NEGATIONS.put("<", ">=");
        NEGATIONS.put("=", "<>");
        NEGATIONS.put(">", "<=");
        NEGATIONS.put("#", "!#");
        NEGATIONS.put("~", "!~");
        NEGATIONS.put("like", "not like");
        NEGATIONS.put("ilike", "not ilike");
        NEGATIONS.put("regexp", "not regexp");
        NEGATIONS.put("rlike", "not rlike");
        NEGATIONS.put("is", "is not");
        NEGATIONS.put("in", "not in");
        NEGATIONS.put("exists", "not exists");
        NEGATIONS.put("between", "not between");
        NEGATIONS.put("<=", ">");
        NEGATIONS.put(">=", "<");
        NEGATIONS.put("=>", "<");
        NEGATIONS.put("=<", ">");
        NEGATIONS.put("!=", "=");
        NEGATIONS.put("<>", "=");
        NEGATIONS.put("!#", "#");
        NEGATIONS.put("!~", "~");
        NEGATIONS.put("!<", "<");
        NEGATIONS.put("!>", ">");
        NEGATIONS.put("is not", "is");
        NEGATIONS.put("not like", "like");
        NEGATIONS.put("not ilike", "ilike");
        NEGATIONS.put("not regexp", "regexp");
        NEGATIONS.put("not rlike", "rlike");
        NEGATIONS.put("not in", "in");
        NEGATIONS.put("not between", "between");
        NEGATIONS.put("not exists", "exists");
    }
}

