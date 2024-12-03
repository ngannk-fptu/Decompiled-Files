/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast;

import antlr.RecognitionException;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.NullPrecedence;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.antlr.SqlGeneratorBase;
import org.hibernate.hql.internal.ast.ErrorReporter;
import org.hibernate.hql.internal.ast.ErrorTracker;
import org.hibernate.hql.internal.ast.ParseErrorHandler;
import org.hibernate.hql.internal.ast.tree.CollectionSizeNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FunctionNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterContainer;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.util.TokenPrinters;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.type.Type;

public class SqlGenerator
extends SqlGeneratorBase
implements ErrorReporter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SqlGenerator.class);
    public static boolean REGRESSION_STYLE_CROSS_JOINS;
    private SqlWriter writer = new DefaultWriter();
    private ParseErrorHandler parseErrorHandler;
    private SessionFactoryImplementor sessionFactory;
    private LinkedList<SqlWriter> outputStack = new LinkedList();
    private List<ParameterSpecification> collectedParameters = new ArrayList<ParameterSpecification>();
    private int traceDepth;

    public void traceIn(String ruleName, AST tree) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2) + "-> ";
        String traceText = ruleName + " (" + this.buildTraceNodeName(tree) + ")";
        LOG.trace(prefix + traceText);
    }

    private String buildTraceNodeName(AST tree) {
        return tree == null ? "???" : tree.getText() + " [" + TokenPrinters.SQL_TOKEN_PRINTER.getTokenTypeName(tree.getType()) + "]";
    }

    public void traceOut(String ruleName, AST tree) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = "<-" + StringHelper.repeat('-', --this.traceDepth * 2) + " ";
        LOG.trace(prefix + ruleName);
    }

    public List<ParameterSpecification> getCollectedParameters() {
        return this.collectedParameters;
    }

    @Override
    protected void out(String s) {
        if (this.exprs.size() > 1) {
            super.out(s);
        } else {
            this.writer.clause(s);
        }
    }

    @Override
    protected void out(AST n) {
        ParameterSpecification[] specifications;
        if (n instanceof Node) {
            this.out(((Node)n).getRenderText(this.sessionFactory));
        } else {
            super.out(n);
        }
        if (n instanceof ParameterNode) {
            this.collectedParameters.add(((ParameterNode)n).getHqlParameterSpecification());
        } else if (n instanceof ParameterContainer && ((ParameterContainer)n).hasEmbeddedParameters() && (specifications = ((ParameterContainer)n).getEmbeddedParameters()) != null) {
            this.collectedParameters.addAll(Arrays.asList(specifications));
        }
    }

    @Override
    protected void betweenFunctionArguments() {
        this.writer.betweenFunctionArguments();
    }

    @Override
    public void reportError(RecognitionException e) {
        this.parseErrorHandler.reportError(e);
    }

    @Override
    public void reportError(String s) {
        this.parseErrorHandler.reportError(s);
    }

    @Override
    public void reportWarning(String s) {
        this.parseErrorHandler.reportWarning(s);
    }

    public ParseErrorHandler getParseErrorHandler() {
        return this.parseErrorHandler;
    }

    public SqlGenerator(SessionFactoryImplementor sfi) {
        this.parseErrorHandler = new ErrorTracker();
        this.sessionFactory = sfi;
    }

    public String getSQL() {
        return this.getStringBuilder().toString();
    }

    @Override
    protected void optionalSpace() {
        int c = this.getLastChar();
        switch (c) {
            case -1: {
                return;
            }
            case 32: {
                return;
            }
            case 41: {
                return;
            }
            case 40: {
                return;
            }
        }
        this.out(" ");
    }

    @Override
    protected void beginFunctionTemplate(AST node, AST nameNode) {
        FunctionNode functionNode = (FunctionNode)node;
        SQLFunction sqlFunction = functionNode.getSQLFunction();
        this.outputStack.addFirst(this.writer);
        if (sqlFunction == null) {
            this.writer = new StandardFunctionArguments();
            super.beginFunctionTemplate(node, nameNode);
        } else {
            this.writer = node.getType() == 78 ? new CastFunctionArguments() : new StandardFunctionArguments();
        }
    }

    @Override
    protected void endFunctionTemplate(AST node) {
        FunctionNode functionNode = (FunctionNode)node;
        SQLFunction sqlFunction = functionNode.getSQLFunction();
        FunctionArgumentsCollectingWriter functionArguments = (FunctionArgumentsCollectingWriter)this.writer;
        if (sqlFunction == null) {
            super.endFunctionTemplate(node);
            this.writer = this.outputStack.removeFirst();
            this.out(StringHelper.join(",", functionArguments.getArgs().iterator()));
        } else {
            Type functionType = functionNode.getFirstArgumentType();
            this.writer = this.outputStack.removeFirst();
            this.out(sqlFunction.render(functionType, functionArguments.getArgs(), this.sessionFactory));
        }
    }

    public static void panic() {
        throw new QueryException("TreeWalker: panic");
    }

    @Override
    protected void fromFragmentSeparator(AST a) {
        FromElement right;
        AST next = a.getNextSibling();
        if (next == null || !this.hasText(a)) {
            return;
        }
        FromElement left = (FromElement)a;
        for (right = (FromElement)next; right != null && !this.hasText((AST)right); right = (FromElement)right.getNextSibling()) {
        }
        if (right == null) {
            return;
        }
        if (!this.hasText((AST)right)) {
            return;
        }
        if (right.getType() == 144) {
            this.out(" ");
        } else if (right.getRealOrigin() == left || right.getRealOrigin() != null && right.getRealOrigin() == left.getRealOrigin()) {
            if (right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle()) {
                this.writeCrossJoinSeparator();
            } else {
                this.out(" ");
            }
        } else {
            this.writeCrossJoinSeparator();
        }
    }

    private void writeCrossJoinSeparator() {
        if (REGRESSION_STYLE_CROSS_JOINS) {
            this.out(", ");
        } else {
            this.out(this.sessionFactory.getDialect().getCrossJoinSeparator());
        }
    }

    @Override
    protected void nestedFromFragment(AST d, AST parent) {
        if (d != null && this.hasText(d)) {
            if (parent != null && this.hasText(parent)) {
                FromElement left = (FromElement)parent;
                FromElement right = (FromElement)d;
                if (right.getRealOrigin() == left) {
                    if (right.getJoinSequence() != null && right.getJoinSequence().isThetaStyle()) {
                        this.writeCrossJoinSeparator();
                    } else {
                        this.out(" ");
                    }
                } else {
                    this.writeCrossJoinSeparator();
                }
            }
            this.out(d);
        }
    }

    @Override
    protected String renderOrderByElement(String expression, String order, String nulls) {
        NullPrecedence nullPrecedence = NullPrecedence.parse(nulls, this.sessionFactory.getSettings().getDefaultNullPrecedence());
        return this.sessionFactory.getDialect().renderOrderByElement(expression, null, order, nullPrecedence);
    }

    @Override
    protected void renderCollectionSize(AST ast) {
        assert (ast instanceof CollectionSizeNode);
        CollectionSizeNode collectionSizeNode = (CollectionSizeNode)ast;
        try {
            this.writer.clause(collectionSizeNode.toSqlExpression());
        }
        catch (QueryException qe) {
            throw qe;
        }
        catch (Exception e) {
            throw new QueryException("Unable to render collection-size node");
        }
    }

    class DefaultWriter
    implements SqlWriter {
        DefaultWriter() {
        }

        @Override
        public void clause(String clause) {
            SqlGenerator.this.getStringBuilder().append(clause);
        }

        @Override
        public void betweenFunctionArguments() {
            SqlGenerator.this.getStringBuilder().append(", ");
        }
    }

    static class CastFunctionArguments
    implements FunctionArgumentsCollectingWriter {
        private String castExpression;
        private String castTargetType;
        private boolean startedType;

        CastFunctionArguments() {
        }

        @Override
        public void clause(String clause) {
            if (this.startedType) {
                this.castTargetType = this.castTargetType == null ? clause : this.castTargetType + clause;
            } else {
                this.castExpression = this.castExpression == null ? clause : this.castExpression + clause;
            }
        }

        @Override
        public void betweenFunctionArguments() {
            if (this.startedType) {
                throw new QueryException("CAST function should only have 2 arguments");
            }
            this.startedType = true;
        }

        @Override
        public List<String> getArgs() {
            ArrayList<String> rtn = CollectionHelper.arrayList(2);
            rtn.add(this.castExpression);
            rtn.add(this.castTargetType);
            return rtn;
        }
    }

    static class StandardFunctionArguments
    implements FunctionArgumentsCollectingWriter {
        private int argInd;
        private final List<String> args = new ArrayList<String>(3);

        StandardFunctionArguments() {
        }

        @Override
        public void clause(String clause) {
            if (this.argInd == this.args.size()) {
                this.args.add(clause);
            } else {
                this.args.set(this.argInd, this.args.get(this.argInd) + clause);
            }
        }

        @Override
        public void betweenFunctionArguments() {
            ++this.argInd;
        }

        @Override
        public List<String> getArgs() {
            return this.args;
        }
    }

    static interface FunctionArgumentsCollectingWriter
    extends SqlWriter {
        public List<String> getArgs();
    }

    static interface SqlWriter {
        public void clause(String var1);

        public void betweenFunctionArguments();
    }
}

