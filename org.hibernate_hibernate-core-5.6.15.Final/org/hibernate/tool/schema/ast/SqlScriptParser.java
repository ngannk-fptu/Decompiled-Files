/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 *  antlr.Token
 *  antlr.TokenStream
 */
package org.hibernate.tool.schema.ast;

import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.tool.schema.ast.GeneratedSqlScriptParser;
import org.hibernate.tool.schema.ast.GeneratedSqlScriptParserTokenTypes;
import org.hibernate.tool.schema.ast.SqlScriptLexer;
import org.hibernate.tool.schema.ast.SqlScriptLogging;
import org.hibernate.tool.schema.ast.SqlScriptParserException;

public class SqlScriptParser
extends GeneratedSqlScriptParser {
    private static String[] TOKEN_NAMES = ASTUtil.generateTokenNameCache(GeneratedSqlScriptParserTokenTypes.class);
    private final List<String> errorList = new LinkedList<String>();
    private final Consumer<String> commandConsumer;
    private StringBuilder currentStatementBuffer;
    private static final int depthIndent = 2;
    private int traceDepth;

    public static List<String> extractCommands(Reader reader) {
        ArrayList<String> statementList = new ArrayList<String>();
        SqlScriptLexer lexer = new SqlScriptLexer(reader);
        SqlScriptParser parser = new SqlScriptParser(statementList::add, lexer);
        parser.parseScript();
        return statementList;
    }

    public SqlScriptParser(Consumer<String> commandConsumer, TokenStream lexer) {
        super(lexer);
        this.commandConsumer = commandConsumer;
    }

    private void parseScript() {
        try {
            this.script();
        }
        catch (Exception e) {
            throw new SqlScriptParserException("Error during import script parsing.", e);
        }
        this.failIfAnyErrors();
    }

    @Override
    protected void out(String text) {
        SqlScriptLogging.SCRIPT_LOGGER.tracef("#out(`%s`) [text]", (Object)text);
        this.currentStatementBuffer.append(text);
    }

    @Override
    protected void out(Token token) {
        SqlScriptLogging.SCRIPT_LOGGER.tracef("#out(`%s`) [token]", (Object)token.getText());
        this.currentStatementBuffer.append(token.getText());
    }

    @Override
    protected void statementStarted() {
        if (this.currentStatementBuffer != null) {
            SqlScriptLogging.SCRIPT_LOGGER.debugf("`#currentStatementBuffer` was not null at `#statementStart`", new Object[0]);
        }
        this.currentStatementBuffer = new StringBuilder();
    }

    @Override
    protected void statementEnded() {
        String statementText = this.currentStatementBuffer.toString().trim();
        SqlScriptLogging.AST_LOGGER.debugf("Import statement : %s", (Object)statementText);
        this.commandConsumer.accept(statementText);
        this.currentStatementBuffer = null;
    }

    private void failIfAnyErrors() {
        if (this.errorList.isEmpty()) {
            return;
        }
        throw new SqlScriptParserException(this.buildErrorMessage());
    }

    public String buildErrorMessage() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.errorList.size(); ++i) {
            buf.append(this.errorList.get(i));
            if (i >= this.errorList.size() - 1) continue;
            buf.append(System.lineSeparator());
        }
        return buf.toString();
    }

    public void reportError(RecognitionException e) {
        String textBase = "RecognitionException(@" + e.getLine() + ":" + e.getColumn() + ")";
        String message = e.toString();
        if (message.contains("expecting DELIMITER")) {
            message = "Import script Sql statements must terminate with a ';' char";
        }
        this.errorList.add(textBase + " : " + message);
    }

    public void reportError(String message) {
        if (message.contains("expecting DELIMITER")) {
            message = "Import script Sql statements must terminate with a ';' char";
        }
        this.errorList.add(message);
    }

    public void reportWarning(String message) {
        SqlScriptLogging.SCRIPT_LOGGER.debugf("SqlScriptParser recognition warning : " + message, new Object[0]);
    }

    public void traceIn(String ruleName) {
        if (!SqlScriptLogging.AST_TRACE_ENABLED) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', this.traceDepth++ * 2);
        SqlScriptLogging.AST_LOGGER.tracef("%s-> %s", (Object)prefix, (Object)ruleName);
    }

    public void traceOut(String ruleName) {
        if (!SqlScriptLogging.AST_TRACE_ENABLED) {
            return;
        }
        if (this.inputState.guessing > 0) {
            return;
        }
        String prefix = StringHelper.repeat('-', --this.traceDepth * 2);
        SqlScriptLogging.AST_LOGGER.tracef("<-%s %s", (Object)prefix, (Object)ruleName);
    }
}

