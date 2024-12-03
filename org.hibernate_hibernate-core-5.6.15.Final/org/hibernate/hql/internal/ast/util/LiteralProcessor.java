/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.hql.internal.ast.util;

import antlr.SemanticException;
import antlr.collections.AST;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.InvalidPathException;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.IdentNode;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.type.LiteralType;
import org.hibernate.type.Type;
import org.jboss.logging.Logger;

public class LiteralProcessor
implements HqlSqlTokenTypes {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)LiteralProcessor.class.getName());
    public static final DecimalLiteralFormat DECIMAL_LITERAL_FORMAT = DecimalLiteralFormat.EXACT;
    private HqlSqlWalker walker;

    public LiteralProcessor(HqlSqlWalker hqlSqlWalker) {
        this.walker = hqlSqlWalker;
    }

    public boolean isAlias(String alias) {
        FromClause from = this.walker.getCurrentFromClause();
        while (from.isSubQuery()) {
            if (from.containsClassAlias(alias)) {
                return true;
            }
            from = from.getParentFromClause();
        }
        return from.containsClassAlias(alias);
    }

    public void processConstant(AST constant, boolean resolveIdent) throws SemanticException {
        boolean isIdent;
        boolean bl = isIdent = constant.getType() == 111 || constant.getType() == 99;
        if (resolveIdent && isIdent && this.isAlias(constant.getText())) {
            IdentNode ident = (IdentNode)constant;
            ident.resolve(false, true);
        } else {
            Queryable queryable = this.walker.getSessionFactoryHelper().findQueryableUsingImports(constant.getText());
            if (isIdent && queryable != null) {
                constant.setText(queryable.getDiscriminatorSQLValue());
            } else {
                this.processLiteral(constant);
            }
        }
    }

    public void lookupConstant(DotNode node) throws SemanticException {
        String text = ASTUtil.getPathText((AST)node);
        Queryable persister = this.walker.getSessionFactoryHelper().findQueryableUsingImports(text);
        if (persister != null) {
            String discrim = persister.getDiscriminatorSQLValue();
            node.setDataType(persister.getDiscriminatorType());
            if ("null".equals(discrim) || "not null".equals(discrim)) {
                throw new InvalidPathException("subclass test not allowed for null or not null discriminator: '" + text + "'");
            }
            this.setSQLValue(node, text, discrim);
        } else {
            Object value = ReflectHelper.getConstantValue(text, this.walker.getSessionFactoryHelper().getFactory());
            if (value == null) {
                throw new InvalidPathException("Invalid path: '" + text + "'");
            }
            this.setConstantValue(node, text, value);
        }
    }

    private void setSQLValue(DotNode node, String text, String value) {
        LOG.debugf("setSQLValue() %s -> %s", text, value);
        node.setFirstChild(null);
        node.setType(150);
        node.setText(value);
        node.setResolvedConstant(text);
    }

    private void setConstantValue(DotNode node, String text, Object value) {
        Type type;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("setConstantValue() %s -> %s %s", text, value, value.getClass().getName());
        }
        node.setFirstChild(null);
        if (value instanceof String) {
            node.setType(130);
        } else if (value instanceof Character) {
            node.setType(130);
        } else if (value instanceof Byte) {
            node.setType(133);
        } else if (value instanceof Short) {
            node.setType(133);
        } else if (value instanceof Integer) {
            node.setType(133);
        } else if (value instanceof Long) {
            node.setType(103);
        } else if (value instanceof Double) {
            node.setType(101);
        } else if (value instanceof Float) {
            node.setType(102);
        } else {
            node.setType(100);
        }
        try {
            type = this.walker.getSessionFactoryHelper().getFactory().getTypeResolver().heuristicType(value.getClass().getName());
        }
        catch (MappingException me) {
            throw new QueryException((Exception)((Object)me));
        }
        if (type == null) {
            throw new QueryException("Could not determine type of: " + node.getText());
        }
        try {
            LiteralType literalType = (LiteralType)((Object)type);
            Dialect dialect = this.walker.getSessionFactoryHelper().getFactory().getDialect();
            node.setText(literalType.objectToSQLString(value, dialect));
        }
        catch (Exception e) {
            throw new QueryException("Could not format constant value to SQL literal: " + node.getText(), e);
        }
        node.setDataType(type);
        node.setResolvedConstant(text);
    }

    public void processBoolean(AST constant) {
        String replacement = (String)this.walker.getTokenReplacements().get(constant.getText());
        if (replacement != null) {
            constant.setText(replacement);
        }
    }

    public void processNull(AST constant) {
        constant.setText("null");
    }

    private void processLiteral(AST constant) {
        String replacement = (String)this.walker.getTokenReplacements().get(constant.getText());
        if (replacement != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debugf("processConstant() : Replacing '%s' with '%s'", constant.getText(), replacement);
            }
            constant.setText(replacement);
        }
    }

    public void processNumeric(AST literal) {
        if (literal.getType() == 133 || literal.getType() == 103 || literal.getType() == 104) {
            literal.setText(this.determineIntegerRepresentation(literal.getText(), literal.getType()));
        } else if (literal.getType() == 102 || literal.getType() == 101 || literal.getType() == 105) {
            literal.setText(this.determineDecimalRepresentation(literal.getText(), literal.getType()));
        } else {
            LOG.unexpectedLiteralTokenType(literal.getType());
        }
    }

    private String determineIntegerRepresentation(String text, int type) {
        Class javaTypeClass = Integer.class;
        try {
            String literalValue;
            if (type == 104) {
                String literalValue2 = text;
                if (literalValue2.endsWith("bi") || literalValue2.endsWith("BI")) {
                    literalValue2 = literalValue2.substring(0, literalValue2.length() - 2);
                }
                javaTypeClass = BigInteger.class;
                return new BigInteger(literalValue2).toString();
            }
            if (type == 133) {
                try {
                    return Integer.valueOf(text).toString();
                }
                catch (NumberFormatException e) {
                    LOG.tracev("Could not format incoming text [{0}] as a NUM_INT; assuming numeric overflow and attempting as NUM_LONG", text);
                }
            }
            if ((literalValue = text).endsWith("l") || literalValue.endsWith("L")) {
                literalValue = literalValue.substring(0, literalValue.length() - 1);
            }
            javaTypeClass = Long.class;
            return Long.valueOf(literalValue).toString();
        }
        catch (Throwable t) {
            throw new HibernateException("Could not parse literal [" + text + "] as " + javaTypeClass.getName(), t);
        }
    }

    public String determineDecimalRepresentation(String text, int type) {
        BigDecimal number;
        String literalValue = text;
        if (type == 102) {
            if (literalValue.endsWith("f") || literalValue.endsWith("F")) {
                literalValue = literalValue.substring(0, literalValue.length() - 1);
            }
        } else if (type == 101) {
            if (literalValue.endsWith("d") || literalValue.endsWith("D")) {
                literalValue = literalValue.substring(0, literalValue.length() - 1);
            }
        } else if (type == 105 && (literalValue.endsWith("bd") || literalValue.endsWith("BD"))) {
            literalValue = literalValue.substring(0, literalValue.length() - 2);
        }
        try {
            number = new BigDecimal(literalValue);
        }
        catch (Throwable t) {
            throw new HibernateException("Could not parse literal [" + text + "] as big-decimal", t);
        }
        return DECIMAL_LITERAL_FORMAT.getFormatter().format(number);
    }

    public static enum DecimalLiteralFormat {
        EXACT{

            @Override
            public DecimalFormatter getFormatter() {
                return ExactDecimalFormatter.INSTANCE;
            }
        }
        ,
        APPROXIMATE{

            @Override
            public DecimalFormatter getFormatter() {
                return ApproximateDecimalFormatter.INSTANCE;
            }
        };


        public abstract DecimalFormatter getFormatter();
    }

    private static class ApproximateDecimalFormatter
    implements DecimalFormatter {
        public static final ApproximateDecimalFormatter INSTANCE = new ApproximateDecimalFormatter();
        private static final String FORMAT_STRING = "#0.0E0";

        private ApproximateDecimalFormatter() {
        }

        @Override
        public String format(BigDecimal number) {
            try {
                DecimalFormat jdkFormatter = new DecimalFormat(FORMAT_STRING);
                jdkFormatter.setMinimumIntegerDigits(1);
                jdkFormatter.setMaximumFractionDigits(Integer.MAX_VALUE);
                return jdkFormatter.format(number);
            }
            catch (Throwable t) {
                throw new HibernateException("Unable to format decimal literal in approximate format [" + number.toString() + "]", t);
            }
        }
    }

    private static class ExactDecimalFormatter
    implements DecimalFormatter {
        public static final ExactDecimalFormatter INSTANCE = new ExactDecimalFormatter();

        private ExactDecimalFormatter() {
        }

        @Override
        public String format(BigDecimal number) {
            return number.toString();
        }
    }

    private static interface DecimalFormatter {
        public String format(BigDecimal var1);
    }
}

