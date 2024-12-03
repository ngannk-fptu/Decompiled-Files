/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.hql.internal.QuerySplitter;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.hql.internal.classic.SelectPathExpressionParser;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class SelectParser
implements Parser {
    private static final Set<String> COUNT_MODIFIERS = new HashSet<String>();
    private LinkedList<String> aggregateFuncTokenList = new LinkedList();
    private boolean ready;
    private boolean aggregate;
    private boolean first;
    private boolean afterNew;
    private boolean insideNew;
    private boolean aggregateAddSelectScalar;
    private Class holderClass;
    private final SelectPathExpressionParser pathExpressionParser = new SelectPathExpressionParser();
    private final PathExpressionParser aggregatePathExpressionParser = new PathExpressionParser();

    public SelectParser() {
        this.pathExpressionParser.setUseThetaStyleJoin(true);
        this.aggregatePathExpressionParser.setUseThetaStyleJoin(true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        String lctoken = token.toLowerCase(Locale.ROOT);
        if (this.first) {
            this.first = false;
            if ("distinct".equals(lctoken)) {
                q.setDistinct(true);
                return;
            }
            if ("all".equals(lctoken)) {
                q.setDistinct(false);
                return;
            }
        }
        if (this.afterNew) {
            this.afterNew = false;
            try {
                this.holderClass = q.getFactory().getServiceRegistry().getService(ClassLoaderService.class).classForName(QuerySplitter.getImportedClass(token, q.getFactory()));
            }
            catch (ClassLoadingException e) {
                throw new QueryException((Exception)((Object)e));
            }
            if (this.holderClass == null) {
                throw new QueryException("class not found: " + token);
            }
            q.setHolderClass(this.holderClass);
            this.insideNew = true;
            return;
        }
        if (token.equals(",")) {
            if (!this.aggregate && this.ready) {
                throw new QueryException("alias or expression expected in SELECT");
            }
            q.appendScalarSelectToken(", ");
            this.ready = true;
            return;
        }
        if ("new".equals(lctoken)) {
            this.afterNew = true;
            this.ready = false;
            return;
        }
        if ("(".equals(token)) {
            if (this.insideNew && !this.aggregate && !this.ready) {
                this.ready = true;
            } else {
                if (!this.aggregate) throw new QueryException("aggregate function expected before ( in SELECT");
                q.appendScalarSelectToken(token);
            }
            this.ready = true;
            return;
        }
        if (")".equals(token)) {
            if (this.insideNew && !this.aggregate && !this.ready) {
                this.insideNew = false;
                return;
            } else {
                if (!this.aggregate || !this.ready) throw new QueryException("( expected before ) in select");
                q.appendScalarSelectToken(token);
                this.aggregateFuncTokenList.removeLast();
                if (this.aggregateFuncTokenList.size() >= 1) return;
                this.aggregate = false;
                this.ready = false;
            }
            return;
        }
        if (COUNT_MODIFIERS.contains(lctoken)) {
            if (!this.ready || !this.aggregate) {
                throw new QueryException(token + " only allowed inside aggregate function in SELECT");
            }
            q.appendScalarSelectToken(token);
            if (!"*".equals(token)) return;
            q.addSelectScalar(this.getFunction("count", q).getReturnType(StandardBasicTypes.LONG, q.getFactory()));
            return;
        }
        if (this.getFunction(lctoken, q) != null && token.equals(q.unalias(token))) {
            if (!this.ready) {
                throw new QueryException(", expected before aggregate function in SELECT: " + token);
            }
            this.aggregate = true;
            this.aggregateAddSelectScalar = true;
            this.aggregateFuncTokenList.add(lctoken);
            this.ready = false;
            q.appendScalarSelectToken(token);
            if (this.aggregateHasArgs(lctoken, q)) return;
            q.addSelectScalar(this.aggregateType(this.aggregateFuncTokenList, null, q));
            if (this.aggregateFuncNoArgsHasParenthesis(lctoken, q)) return;
            this.aggregateFuncTokenList.removeLast();
            if (this.aggregateFuncTokenList.size() < 1) {
                this.aggregate = false;
                this.ready = false;
                return;
            } else {
                this.ready = true;
            }
            return;
        }
        if (this.aggregate) {
            boolean constantToken = false;
            if (!this.ready) {
                throw new QueryException("( expected after aggregate function in SELECT");
            }
            try {
                ParserHelper.parse(this.aggregatePathExpressionParser, q.unalias(token), ".", q);
            }
            catch (QueryException qex) {
                constantToken = true;
            }
            if (constantToken) {
                q.appendScalarSelectToken(token);
                return;
            } else {
                if (this.aggregatePathExpressionParser.isCollectionValued()) {
                    q.addCollection(this.aggregatePathExpressionParser.getCollectionName(), this.aggregatePathExpressionParser.getCollectionRole());
                }
                q.appendScalarSelectToken(this.aggregatePathExpressionParser.getWhereColumn());
                if (this.aggregateAddSelectScalar) {
                    q.addSelectScalar(this.aggregateType(this.aggregateFuncTokenList, this.aggregatePathExpressionParser.getWhereColumnType(), q));
                    this.aggregateAddSelectScalar = false;
                }
                this.aggregatePathExpressionParser.addAssociation(q);
            }
            return;
        }
        if (!this.ready) {
            throw new QueryException(", expected in SELECT");
        }
        ParserHelper.parse(this.pathExpressionParser, q.unalias(token), ".", q);
        if (this.pathExpressionParser.isCollectionValued()) {
            q.addCollection(this.pathExpressionParser.getCollectionName(), this.pathExpressionParser.getCollectionRole());
        } else if (this.pathExpressionParser.getWhereColumnType().isEntityType()) {
            q.addSelectClass(this.pathExpressionParser.getSelectName());
        }
        q.appendScalarSelectTokens(this.pathExpressionParser.getWhereColumns());
        q.addSelectScalar(this.pathExpressionParser.getWhereColumnType());
        this.pathExpressionParser.addAssociation(q);
        this.ready = false;
    }

    public boolean aggregateHasArgs(String funcToken, QueryTranslatorImpl q) {
        return this.getFunction(funcToken, q).hasArguments();
    }

    public boolean aggregateFuncNoArgsHasParenthesis(String funcToken, QueryTranslatorImpl q) {
        return this.getFunction(funcToken, q).hasParenthesesIfNoArguments();
    }

    public Type aggregateType(List funcTokenList, Type type, QueryTranslatorImpl q) throws QueryException {
        Type retType = type;
        for (int i = funcTokenList.size() - 1; i >= 0; --i) {
            Type argType = retType;
            String funcToken = (String)funcTokenList.get(i);
            retType = this.getFunction(funcToken, q).getReturnType(argType, q.getFactory());
        }
        return retType;
    }

    private SQLFunction getFunction(String name, QueryTranslatorImpl q) {
        return q.getFactory().getSqlFunctionRegistry().findSQLFunction(name);
    }

    @Override
    public void start(QueryTranslatorImpl q) {
        this.ready = true;
        this.first = true;
        this.aggregate = false;
        this.afterNew = false;
        this.insideNew = false;
        this.holderClass = null;
        this.aggregateFuncTokenList.clear();
    }

    @Override
    public void end(QueryTranslatorImpl q) {
    }

    static {
        COUNT_MODIFIERS.add("distinct");
        COUNT_MODIFIERS.add("all");
        COUNT_MODIFIERS.add("*");
    }
}

