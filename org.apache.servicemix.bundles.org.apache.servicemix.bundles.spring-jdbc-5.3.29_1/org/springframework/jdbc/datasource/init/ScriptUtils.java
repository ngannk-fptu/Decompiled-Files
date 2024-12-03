/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.EncodedResource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.datasource.init;

import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptParseException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class ScriptUtils {
    public static final String DEFAULT_STATEMENT_SEPARATOR = ";";
    public static final String FALLBACK_STATEMENT_SEPARATOR = "\n";
    public static final String EOF_STATEMENT_SEPARATOR = "^^^ END OF SCRIPT ^^^";
    public static final String DEFAULT_COMMENT_PREFIX = "--";
    public static final String[] DEFAULT_COMMENT_PREFIXES = new String[]{"--"};
    public static final String DEFAULT_BLOCK_COMMENT_START_DELIMITER = "/*";
    public static final String DEFAULT_BLOCK_COMMENT_END_DELIMITER = "*/";
    private static final Log logger = LogFactory.getLog(ScriptUtils.class);

    public static void executeSqlScript(Connection connection, Resource resource) throws ScriptException {
        ScriptUtils.executeSqlScript(connection, new EncodedResource(resource));
    }

    public static void executeSqlScript(Connection connection, EncodedResource resource) throws ScriptException {
        ScriptUtils.executeSqlScript(connection, resource, false, false, DEFAULT_COMMENT_PREFIX, DEFAULT_STATEMENT_SEPARATOR, DEFAULT_BLOCK_COMMENT_START_DELIMITER, DEFAULT_BLOCK_COMMENT_END_DELIMITER);
    }

    public static void executeSqlScript(Connection connection, EncodedResource resource, boolean continueOnError, boolean ignoreFailedDrops, String commentPrefix, @Nullable String separator, String blockCommentStartDelimiter, String blockCommentEndDelimiter) throws ScriptException {
        ScriptUtils.executeSqlScript(connection, resource, continueOnError, ignoreFailedDrops, new String[]{commentPrefix}, separator, blockCommentStartDelimiter, blockCommentEndDelimiter);
    }

    /*
     * Exception decompiling
     */
    public static void executeSqlScript(Connection connection, EncodedResource resource, boolean continueOnError, boolean ignoreFailedDrops, String[] commentPrefixes, @Nullable String separator, String blockCommentStartDelimiter, String blockCommentEndDelimiter) throws ScriptException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    static String readScript(EncodedResource resource, @Nullable String separator, String[] commentPrefixes, String blockCommentEndDelimiter) throws IOException {
        try (LineNumberReader lnr = new LineNumberReader(resource.getReader());){
            String string = ScriptUtils.readScript(lnr, commentPrefixes, separator, blockCommentEndDelimiter);
            return string;
        }
    }

    @Deprecated
    public static String readScript(LineNumberReader lineNumberReader, @Nullable String commentPrefix, @Nullable String separator, @Nullable String blockCommentEndDelimiter) throws IOException {
        String[] stringArray;
        if (commentPrefix != null) {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = commentPrefix;
        } else {
            stringArray = null;
        }
        String[] commentPrefixes = stringArray;
        return ScriptUtils.readScript(lineNumberReader, commentPrefixes, separator, blockCommentEndDelimiter);
    }

    @Deprecated
    public static String readScript(LineNumberReader lineNumberReader, @Nullable String[] commentPrefixes, @Nullable String separator, @Nullable String blockCommentEndDelimiter) throws IOException {
        String currentStatement = lineNumberReader.readLine();
        StringBuilder scriptBuilder = new StringBuilder();
        while (currentStatement != null) {
            if (blockCommentEndDelimiter != null && currentStatement.contains(blockCommentEndDelimiter) || commentPrefixes != null && !ScriptUtils.startsWithAny(currentStatement, commentPrefixes, 0)) {
                if (scriptBuilder.length() > 0) {
                    scriptBuilder.append('\n');
                }
                scriptBuilder.append(currentStatement);
            }
            currentStatement = lineNumberReader.readLine();
        }
        ScriptUtils.appendSeparatorToScriptIfNecessary(scriptBuilder, separator);
        return scriptBuilder.toString();
    }

    private static void appendSeparatorToScriptIfNecessary(StringBuilder scriptBuilder, @Nullable String separator) {
        if (separator == null) {
            return;
        }
        String trimmed = separator.trim();
        if (trimmed.length() == separator.length()) {
            return;
        }
        if (scriptBuilder.lastIndexOf(trimmed) == scriptBuilder.length() - trimmed.length()) {
            scriptBuilder.append(separator.substring(trimmed.length()));
        }
    }

    @Deprecated
    public static boolean containsSqlScriptDelimiters(String script, String delimiter) {
        return ScriptUtils.containsStatementSeparator(null, script, delimiter, DEFAULT_COMMENT_PREFIXES, DEFAULT_BLOCK_COMMENT_START_DELIMITER, DEFAULT_BLOCK_COMMENT_END_DELIMITER);
    }

    private static boolean containsStatementSeparator(@Nullable EncodedResource resource, String script, String separator, String[] commentPrefixes, String blockCommentStartDelimiter, String blockCommentEndDelimiter) throws ScriptException {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;
        for (int i = 0; i < script.length(); ++i) {
            char c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                continue;
            }
            if (c == '\\') {
                inEscape = true;
                continue;
            }
            if (!inDoubleQuote && c == '\'') {
                inSingleQuote = !inSingleQuote;
            } else if (!inSingleQuote && c == '\"') {
                boolean bl = inDoubleQuote = !inDoubleQuote;
            }
            if (inSingleQuote || inDoubleQuote) continue;
            if (script.startsWith(separator, i)) {
                return true;
            }
            if (ScriptUtils.startsWithAny(script, commentPrefixes, i)) {
                int indexOfNextNewline = script.indexOf(10, i);
                if (indexOfNextNewline <= i) break;
                i = indexOfNextNewline;
                continue;
            }
            if (!script.startsWith(blockCommentStartDelimiter, i)) continue;
            int indexOfCommentEnd = script.indexOf(blockCommentEndDelimiter, i);
            if (indexOfCommentEnd > i) {
                i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                continue;
            }
            throw new ScriptParseException("Missing block comment end delimiter: " + blockCommentEndDelimiter, resource);
        }
        return false;
    }

    @Deprecated
    public static void splitSqlScript(String script, char separator, List<String> statements) throws ScriptException {
        ScriptUtils.splitSqlScript(script, String.valueOf(separator), statements);
    }

    @Deprecated
    public static void splitSqlScript(String script, String separator, List<String> statements) throws ScriptException {
        ScriptUtils.splitSqlScript(null, script, separator, DEFAULT_COMMENT_PREFIX, DEFAULT_BLOCK_COMMENT_START_DELIMITER, DEFAULT_BLOCK_COMMENT_END_DELIMITER, statements);
    }

    @Deprecated
    public static void splitSqlScript(@Nullable EncodedResource resource, String script, String separator, String commentPrefix, String blockCommentStartDelimiter, String blockCommentEndDelimiter, List<String> statements) throws ScriptException {
        Assert.hasText((String)commentPrefix, (String)"'commentPrefix' must not be null or empty");
        ScriptUtils.splitSqlScript(resource, script, separator, new String[]{commentPrefix}, blockCommentStartDelimiter, blockCommentEndDelimiter, statements);
    }

    @Deprecated
    public static void splitSqlScript(@Nullable EncodedResource resource, String script, String separator, String[] commentPrefixes, String blockCommentStartDelimiter, String blockCommentEndDelimiter, List<String> statements) throws ScriptException {
        Assert.hasText((String)script, (String)"'script' must not be null or empty");
        Assert.notNull((Object)separator, (String)"'separator' must not be null");
        Assert.notEmpty((Object[])commentPrefixes, (String)"'commentPrefixes' must not be null or empty");
        for (String commentPrefix : commentPrefixes) {
            Assert.hasText((String)commentPrefix, (String)"'commentPrefixes' must not contain null or empty elements");
        }
        Assert.hasText((String)blockCommentStartDelimiter, (String)"'blockCommentStartDelimiter' must not be null or empty");
        Assert.hasText((String)blockCommentEndDelimiter, (String)"'blockCommentEndDelimiter' must not be null or empty");
        StringBuilder sb = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inEscape = false;
        for (int i = 0; i < script.length(); ++i) {
            int c = script.charAt(i);
            if (inEscape) {
                inEscape = false;
                sb.append((char)c);
                continue;
            }
            if (c == 92) {
                inEscape = true;
                sb.append((char)c);
                continue;
            }
            if (!inDoubleQuote && c == 39) {
                inSingleQuote = !inSingleQuote;
            } else if (!inSingleQuote && c == 34) {
                boolean bl = inDoubleQuote = !inDoubleQuote;
            }
            if (!inSingleQuote && !inDoubleQuote) {
                if (script.startsWith(separator, i)) {
                    if (sb.length() > 0) {
                        statements.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    i += separator.length() - 1;
                    continue;
                }
                if (ScriptUtils.startsWithAny(script, commentPrefixes, i)) {
                    int indexOfNextNewline = script.indexOf(10, i);
                    if (indexOfNextNewline <= i) break;
                    i = indexOfNextNewline;
                    continue;
                }
                if (script.startsWith(blockCommentStartDelimiter, i)) {
                    int indexOfCommentEnd = script.indexOf(blockCommentEndDelimiter, i);
                    if (indexOfCommentEnd > i) {
                        i = indexOfCommentEnd + blockCommentEndDelimiter.length() - 1;
                        continue;
                    }
                    throw new ScriptParseException("Missing block comment end delimiter: " + blockCommentEndDelimiter, resource);
                }
                if (c == 32 || c == 13 || c == 10 || c == 9) {
                    if (sb.length() <= 0 || sb.charAt(sb.length() - 1) == ' ') continue;
                    c = 32;
                }
            }
            sb.append((char)c);
        }
        if (StringUtils.hasText((CharSequence)sb)) {
            statements.add(sb.toString());
        }
    }

    private static boolean startsWithAny(String script, String[] prefixes, int offset) {
        for (String prefix : prefixes) {
            if (!script.startsWith(prefix, offset)) continue;
            return true;
        }
        return false;
    }
}

