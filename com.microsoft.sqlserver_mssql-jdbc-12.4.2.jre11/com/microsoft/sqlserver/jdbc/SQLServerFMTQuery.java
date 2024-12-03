/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.antlr.v4.runtime.ANTLRErrorListener
 *  org.antlr.v4.runtime.CharStreams
 *  org.antlr.v4.runtime.Token
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerErrorListener;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerLexer;
import com.microsoft.sqlserver.jdbc.SQLServerParser;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import com.microsoft.sqlserver.jdbc.SQLServerTokenIterator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

class SQLServerFMTQuery {
    private static final String FMT_ON = "SET FMTONLY ON;";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String FMT_OFF = ";SET FMTONLY OFF;";
    private String prefix = "";
    private ArrayList<? extends Token> tokenList = null;
    private List<String> userColumns = new ArrayList<String>();
    private List<String> tableTarget = new ArrayList<String>();
    private List<String> possibleAliases = new ArrayList<String>();
    private List<List<String>> valuesList = new ArrayList<List<String>>();

    List<String> getColumns() {
        return this.userColumns;
    }

    List<String> getTableTarget() {
        return this.tableTarget;
    }

    List<List<String>> getValuesList() {
        return this.valuesList;
    }

    List<String> getAliases() {
        return this.possibleAliases;
    }

    String constructColumnTargets() {
        if (this.userColumns.contains("?")) {
            return this.userColumns.stream().filter(s -> !"?".equals(s)).map(s -> "".equals(s) ? "NULL" : s).collect(Collectors.joining(","));
        }
        return this.userColumns.isEmpty() ? "*" : this.userColumns.stream().map(s -> "".equals(s) ? "NULL" : s).collect(Collectors.joining(","));
    }

    String constructTableTargets() {
        return this.tableTarget.stream().distinct().filter(s -> !this.possibleAliases.contains(s)).collect(Collectors.joining(","));
    }

    String getFMTQuery() {
        StringBuilder sb = new StringBuilder(FMT_ON);
        if (!"".equals(this.prefix)) {
            sb.append(this.prefix);
        }
        sb.append(SELECT);
        sb.append(this.constructColumnTargets());
        if (!this.tableTarget.isEmpty()) {
            sb.append(FROM);
            sb.append(this.constructTableTargets());
        }
        sb.append(FMT_OFF);
        return sb.toString();
    }

    private SQLServerFMTQuery() {
    }

    SQLServerFMTQuery(String userSql) throws SQLServerException {
        if (null != userSql && 0 != userSql.length()) {
            ByteArrayInputStream stream = new ByteArrayInputStream(userSql.getBytes(StandardCharsets.UTF_8));
            SQLServerLexer lexer = null;
            try {
                lexer = new SQLServerLexer(CharStreams.fromStream((InputStream)stream));
            }
            catch (IOException e) {
                SQLServerException.makeFromDriverError(null, userSql, e.getLocalizedMessage(), null, false);
            }
            if (null != lexer) {
                lexer.removeErrorListeners();
                lexer.addErrorListener((ANTLRErrorListener)new SQLServerErrorListener());
                this.tokenList = (ArrayList)lexer.getAllTokens();
                if (this.tokenList.isEmpty()) {
                    SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
                }
                SQLServerTokenIterator iter = new SQLServerTokenIterator(this.tokenList);
                this.prefix = SQLServerParser.getCTE(iter);
                SQLServerParser.parseQuery(iter, this);
            } else {
                SQLServerException.makeFromDriverError(null, userSql, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
            }
        } else {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
        }
    }
}

