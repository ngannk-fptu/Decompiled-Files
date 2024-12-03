/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.CommonAST
 *  antlr.TokenStream
 *  antlr.collections.AST
 *  org.jboss.logging.Logger
 */
package org.hibernate.sql.ordering.antlr;

import antlr.ASTFactory;
import antlr.CommonAST;
import antlr.TokenStream;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.sql.ordering.antlr.CollationSpecification;
import org.hibernate.sql.ordering.antlr.ColumnReference;
import org.hibernate.sql.ordering.antlr.Factory;
import org.hibernate.sql.ordering.antlr.FormulaReference;
import org.hibernate.sql.ordering.antlr.GeneratedOrderByFragmentParser;
import org.hibernate.sql.ordering.antlr.OrderingSpecification;
import org.hibernate.sql.ordering.antlr.SortKey;
import org.hibernate.sql.ordering.antlr.SortSpecification;
import org.hibernate.sql.ordering.antlr.SqlValueReference;
import org.hibernate.sql.ordering.antlr.TranslationContext;
import org.jboss.logging.Logger;

public class OrderByFragmentParser
extends GeneratedOrderByFragmentParser {
    private static final Logger LOG = Logger.getLogger((String)OrderByFragmentParser.class.getName());
    private final TranslationContext context;
    private Set<String> columnReferences = new HashSet<String>();
    private static final int TEMPLATE_MARKER_LENGTH = "$PlaceHolder$".length();
    private int traceDepth = 0;

    public OrderByFragmentParser(TokenStream lexer, TranslationContext context) {
        super(lexer);
        super.setASTFactory((ASTFactory)new Factory());
        this.context = context;
    }

    public Set<String> getColumnReferences() {
        return this.columnReferences;
    }

    @Override
    protected AST quotedIdentifier(AST ident) {
        String columnName = this.context.getDialect().quote('`' + ident.getText() + '`');
        this.columnReferences.add(columnName);
        String marker = '{' + columnName + '}';
        return this.getASTFactory().create(21, marker);
    }

    @Override
    protected AST quotedString(AST ident) {
        return this.getASTFactory().create(21, this.context.getDialect().quote(ident.getText()));
    }

    @Override
    protected boolean isFunctionName(AST ast) {
        AST child = ast.getFirstChild();
        if (child != null && "{param list}".equals(child.getText())) {
            return true;
        }
        SQLFunction function = this.context.getSqlFunctionRegistry().findSQLFunction(ast.getText());
        if (function == null) {
            return false;
        }
        return !function.hasParenthesesIfNoArguments();
    }

    @Override
    protected AST resolveFunction(AST ast) {
        AST child = ast.getFirstChild();
        if (child != null) {
            assert ("{param list}".equals(child.getText()));
            child = child.getFirstChild();
        }
        String functionName = ast.getText();
        SQLFunction function = this.context.getSqlFunctionRegistry().findSQLFunction(functionName);
        if (function == null) {
            String text = functionName;
            if (child != null) {
                text = text + '(';
                while (child != null) {
                    text = text + this.resolveFunctionArgument(child);
                    if ((child = child.getNextSibling()) == null) continue;
                    text = text + ", ";
                }
                text = text + ')';
            }
            return this.getASTFactory().create(21, text);
        }
        ArrayList<String> expressions = new ArrayList<String>();
        while (child != null) {
            expressions.add(this.resolveFunctionArgument(child));
            child = child.getNextSibling();
        }
        String text = function.render(null, expressions, this.context.getSessionFactory());
        return this.getASTFactory().create(21, text);
    }

    private String resolveFunctionArgument(AST argumentNode) {
        String adjustedText;
        String nodeText = argumentNode.getText();
        if (nodeText.contains("$PlaceHolder$")) {
            adjustedText = this.adjustTemplateReferences(nodeText);
        } else {
            if (nodeText.startsWith("{") && nodeText.endsWith("}")) {
                this.columnReferences.add(nodeText.substring(1, nodeText.length() - 1));
                return nodeText;
            }
            adjustedText = nodeText;
            Pattern pattern = Pattern.compile("\\{(.*)\\}");
            Matcher matcher = pattern.matcher(adjustedText);
            while (matcher.find()) {
                this.columnReferences.add(matcher.group(1));
            }
        }
        return adjustedText;
    }

    @Override
    protected AST resolveIdent(AST ident) {
        SqlValueReference[] sqlValueReferences;
        String text = ident.getText();
        try {
            sqlValueReferences = this.context.getColumnMapper().map(text);
        }
        catch (Throwable t) {
            sqlValueReferences = null;
        }
        if (sqlValueReferences == null || sqlValueReferences.length == 0) {
            return this.getASTFactory().create(21, this.makeColumnReference(text));
        }
        if (sqlValueReferences.length == 1) {
            return this.processSqlValueReference(sqlValueReferences[0]);
        }
        AST root = this.getASTFactory().create(11, "{ident list}");
        for (SqlValueReference sqlValueReference : sqlValueReferences) {
            root.addChild(this.processSqlValueReference(sqlValueReference));
        }
        return root;
    }

    private AST processSqlValueReference(SqlValueReference sqlValueReference) {
        if (ColumnReference.class.isInstance(sqlValueReference)) {
            String columnName = ((ColumnReference)sqlValueReference).getColumnName();
            return this.getASTFactory().create(21, this.makeColumnReference(columnName));
        }
        String formulaFragment = ((FormulaReference)sqlValueReference).getFormulaFragment();
        String adjustedText = this.adjustTemplateReferences(formulaFragment);
        return this.getASTFactory().create(21, adjustedText);
    }

    private String makeColumnReference(String text) {
        this.columnReferences.add(text);
        return "{" + text + "}";
    }

    private String adjustTemplateReferences(String template) {
        int templateLength = template.length();
        int startPos = template.indexOf("$PlaceHolder$");
        while (startPos != -1 && startPos < templateLength) {
            int pos;
            int dotPos = startPos + TEMPLATE_MARKER_LENGTH;
            for (pos = dotPos + 1; pos < templateLength && OrderByFragmentParser.isValidIdentifierCharacter(template.charAt(pos)); ++pos) {
            }
            String columnReference = template.substring(dotPos + 1, pos);
            String replacement = "{" + columnReference + "}";
            template = template.replace(template.substring(startPos, pos), replacement);
            this.columnReferences.add(columnReference);
            startPos = template.indexOf("$PlaceHolder$", pos - TEMPLATE_MARKER_LENGTH + 1);
            templateLength = template.length();
        }
        return template;
    }

    private static boolean isValidIdentifierCharacter(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || '_' == c || '\"' == c;
    }

    @Override
    protected AST postProcessSortSpecification(AST sortSpec) {
        assert (5 == sortSpec.getType());
        SortSpecification sortSpecification = (SortSpecification)sortSpec;
        SortKey sortKey = sortSpecification.getSortKey();
        if (11 == sortKey.getFirstChild().getType()) {
            AST identList = sortKey.getFirstChild();
            AST ident = identList.getFirstChild();
            CommonAST holder = new CommonAST();
            do {
                holder.addChild((AST)this.createSortSpecification(ident, sortSpecification.getCollation(), sortSpecification.getOrdering()));
            } while ((ident = ident.getNextSibling()) != null);
            sortSpec = holder.getFirstChild();
        }
        return sortSpec;
    }

    private SortSpecification createSortSpecification(AST ident, CollationSpecification collationSpecification, OrderingSpecification orderingSpecification) {
        AST sortSpecification = this.getASTFactory().create(5, "{{sort specification}}");
        AST sortKey = this.getASTFactory().create(8, "{{sort key}}");
        AST newIdent = this.getASTFactory().create(ident.getType(), ident.getText());
        sortKey.setFirstChild(newIdent);
        sortSpecification.setFirstChild(sortKey);
        if (collationSpecification != null) {
            sortSpecification.addChild((AST)collationSpecification);
        }
        if (orderingSpecification != null) {
            sortSpecification.addChild((AST)orderingSpecification);
        }
        return (SortSpecification)sortSpecification;
    }

    public void traceIn(String ruleName) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2) + "-> ";
        LOG.trace((Object)(prefix + ruleName));
    }

    public void traceOut(String ruleName) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = "<-" + StringHelper.repeat('-', --this.traceDepth * 2) + " ";
        LOG.trace((Object)(prefix + ruleName));
    }

    @Override
    protected void trace(String msg) {
        LOG.trace((Object)msg);
    }
}

