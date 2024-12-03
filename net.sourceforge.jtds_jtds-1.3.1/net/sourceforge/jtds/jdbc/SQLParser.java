/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.cache.SQLCacheKey;
import net.sourceforge.jtds.jdbc.cache.SimpleLRUCache;

class SQLParser {
    private static final SimpleLRUCache<SQLCacheKey, CachedSQLQuery> _Cache = new SimpleLRUCache(1000);
    private final String sql;
    private final char[] in;
    private int s;
    private final int len;
    private char[] out;
    private int d;
    private final ArrayList params;
    private char terminator;
    private String procName;
    private String keyWord;
    private String tableName;
    private final JtdsConnection connection;
    private static boolean[] identifierChar = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false};
    private static final byte[] timeMask = new byte[]{35, 35, 58, 35, 35, 58, 35, 35};
    private static final byte[] dateMask = new byte[]{35, 35, 35, 35, 45, 35, 35, 45, 35, 35};
    static final byte[] timestampMask = new byte[]{35, 35, 35, 35, 45, 35, 35, 45, 35, 35, 32, 35, 35, 58, 35, 35, 58, 35, 35};
    private static HashMap fnMap = new HashMap();
    private static HashMap msFnMap = new HashMap();
    private static HashMap cvMap = new HashMap();

    static String[] parse(String sql, ArrayList paramList, JtdsConnection connection, boolean extractTable) throws SQLException {
        String[] ret;
        if (extractTable) {
            ret = new SQLParser(sql, paramList, connection).parse(extractTable);
        } else {
            SQLCacheKey cacheKey = new SQLCacheKey(sql, connection);
            CachedSQLQuery cachedQuery = _Cache.get(cacheKey);
            if (cachedQuery == null) {
                ret = new SQLParser(sql, paramList, connection).parse(extractTable);
                _Cache.put(cacheKey, new CachedSQLQuery(ret, paramList));
            } else {
                ret = cachedQuery.parsedSql;
                int length = cachedQuery.paramNames == null ? 0 : cachedQuery.paramNames.length;
                for (int i = 0; i < length; ++i) {
                    paramList.add(new ParamInfo(cachedQuery.paramNames[i], cachedQuery.paramMarkerPos[i], cachedQuery.paramIsRetVal[i], cachedQuery.paramIsUnicode[i]));
                }
            }
        }
        return ret;
    }

    private static boolean isIdentifier(int ch) {
        return ch > 127 || identifierChar[ch];
    }

    private SQLParser(String sqlIn, ArrayList paramList, JtdsConnection connection) {
        this.sql = sqlIn;
        this.in = this.sql.toCharArray();
        this.len = this.in.length;
        this.out = new char[this.len];
        this.params = paramList;
        this.procName = "";
        this.connection = connection;
    }

    private void copyLiteral(String txt) throws SQLException {
        int len = txt.length();
        for (int i = 0; i < len; ++i) {
            char c = txt.charAt(i);
            if (c == '?') {
                if (this.params == null) {
                    throw new SQLException(Messages.get("error.parsesql.unexpectedparam", String.valueOf(this.s)), "2A000");
                }
                ParamInfo pi = new ParamInfo(this.d, this.connection.getUseUnicode());
                this.params.add(pi);
            }
            this.append(c);
        }
    }

    private void copyString() {
        char saveTc = this.terminator;
        char tc = this.in[this.s];
        if (tc == '[') {
            tc = ']';
        }
        this.terminator = tc;
        this.append(this.in[this.s++]);
        while (this.in[this.s] != tc) {
            this.append(this.in[this.s++]);
        }
        this.append(this.in[this.s++]);
        this.terminator = saveTc;
    }

    private String copyKeyWord() {
        int start = this.d;
        while (this.s < this.len && SQLParser.isIdentifier(this.in[this.s])) {
            this.append(this.in[this.s++]);
        }
        return String.valueOf(this.out, start, this.d - start).toLowerCase();
    }

    private void copyParam(String name, int pos) throws SQLException {
        if (this.params == null) {
            throw new SQLException(Messages.get("error.parsesql.unexpectedparam", String.valueOf(this.s)), "2A000");
        }
        ParamInfo pi = new ParamInfo(pos, this.connection.getUseUnicode());
        pi.name = name;
        if (pos >= 0) {
            this.append(this.in[this.s++]);
        } else {
            pi.isRetVal = true;
            ++this.s;
        }
        this.params.add(pi);
    }

    private String copyProcName() throws SQLException {
        int start = this.d;
        block0: while (true) {
            if (this.in[this.s] == '\"' || this.in[this.s] == '[') {
                this.copyString();
            } else {
                char c = this.in[this.s++];
                while (SQLParser.isIdentifier(c) || c == ';') {
                    this.append(c);
                    c = this.in[this.s++];
                }
                --this.s;
            }
            if (this.in[this.s] != '.') break;
            while (true) {
                if (this.in[this.s] != '.') continue block0;
                this.append(this.in[this.s++]);
            }
            break;
        }
        if (this.d == start) {
            throw new SQLException(Messages.get("error.parsesql.syntax", "call", String.valueOf(this.s)), "22025");
        }
        return new String(this.out, start, this.d - start);
    }

    private String copyParamName() {
        int start = this.d;
        char c = this.in[this.s++];
        while (SQLParser.isIdentifier(c)) {
            this.append(c);
            c = this.in[this.s++];
        }
        --this.s;
        return new String(this.out, start, this.d - start);
    }

    private void copyWhiteSpace() {
        while (this.s < this.in.length && Character.isWhitespace(this.in[this.s])) {
            this.append(this.in[this.s++]);
        }
    }

    private void mustbe(char c, boolean copy) throws SQLException {
        if (this.in[this.s] != c) {
            throw new SQLException(Messages.get("error.parsesql.mustbe", String.valueOf(this.s), String.valueOf(c)), "22019");
        }
        if (copy) {
            this.append(this.in[this.s++]);
        } else {
            ++this.s;
        }
    }

    private void skipWhiteSpace() throws SQLException {
        block4: while (this.s < this.len) {
            while (Character.isWhitespace(this.sql.charAt(this.s))) {
                ++this.s;
            }
            switch (this.sql.charAt(this.s)) {
                case '-': {
                    if (this.s + 1 >= this.len || this.in[this.s + 1] != '-') continue block4;
                    this.append(this.in[this.s++]);
                    this.append(this.in[this.s++]);
                    while (this.s < this.len && this.in[this.s] != '\n' && this.in[this.s] != '\r') {
                        this.append(this.in[this.s++]);
                    }
                    continue block4;
                }
                case '/': {
                    if (this.s + 1 >= this.len || this.in[this.s + 1] != '*') continue block4;
                    this.append(this.in[this.s++]);
                    this.append(this.in[this.s++]);
                    int level = 1;
                    do {
                        if (this.s >= this.len - 1) {
                            throw new SQLException(Messages.get("error.parsesql.missing", "*/"), "22025");
                        }
                        if (this.in[this.s] == '/' && this.s + 1 < this.len && this.in[this.s + 1] == '*') {
                            this.append(this.in[this.s++]);
                            ++level;
                        } else if (this.in[this.s] == '*' && this.s + 1 < this.len && this.in[this.s + 1] == '/') {
                            this.append(this.in[this.s++]);
                            --level;
                        }
                        this.append(this.in[this.s++]);
                    } while (level > 0);
                    continue block4;
                }
            }
            return;
        }
    }

    private void skipSingleComments() {
        while (this.s < this.len && this.in[this.s] != '\n' && this.in[this.s] != '\r') {
            this.append(this.in[this.s++]);
        }
    }

    private void skipMultiComments() throws SQLException {
        int block = 0;
        do {
            if (this.s < this.len - 1) {
                if (this.in[this.s] == '/' && this.in[this.s + 1] == '*') {
                    this.append(this.in[this.s++]);
                    ++block;
                } else if (this.in[this.s] == '*' && this.in[this.s + 1] == '/') {
                    this.append(this.in[this.s++]);
                    --block;
                }
            } else {
                throw new SQLException(Messages.get("error.parsesql.missing", "*/"), "22025");
            }
            this.append(this.in[this.s++]);
        } while (block > 0);
    }

    private void callEscape() throws SQLException {
        this.copyLiteral("EXECUTE ");
        this.keyWord = "execute";
        this.procName = this.copyProcName();
        this.skipWhiteSpace();
        if (this.in[this.s] == '(') {
            ++this.s;
            this.terminator = (char)41;
            this.skipWhiteSpace();
        } else {
            this.terminator = (char)125;
        }
        this.append(' ');
        while (this.in[this.s] != this.terminator) {
            String name = null;
            if (this.in[this.s] == '@') {
                name = this.copyParamName();
                this.skipWhiteSpace();
                this.mustbe('=', true);
                this.skipWhiteSpace();
                if (this.in[this.s] == '?') {
                    this.copyParam(name, this.d);
                } else {
                    this.procName = "";
                }
            } else if (this.in[this.s] == '?') {
                this.copyParam(name, this.d);
            } else {
                this.procName = "";
            }
            this.skipWhiteSpace();
            while (this.in[this.s] != this.terminator && this.in[this.s] != ',') {
                if (this.in[this.s] == '{') {
                    this.escape();
                    continue;
                }
                if (this.in[this.s] == '\'' || this.in[this.s] == '[' || this.in[this.s] == '\"') {
                    this.copyString();
                    continue;
                }
                this.append(this.in[this.s++]);
            }
            if (this.in[this.s] == ',') {
                this.append(this.in[this.s++]);
            }
            this.skipWhiteSpace();
        }
        if (this.terminator == ')') {
            ++this.s;
        }
        this.terminator = (char)125;
        this.skipWhiteSpace();
    }

    private boolean getDateTimeField(byte[] mask) throws SQLException {
        this.skipWhiteSpace();
        if (this.in[this.s] == '?') {
            this.copyParam(null, this.d);
            this.skipWhiteSpace();
            return this.in[this.s] == this.terminator;
        }
        boolean sel = this.keyWord.equals("select");
        if (sel) {
            this.append("convert(datetime,".toCharArray());
        }
        this.append('\'');
        this.terminator = (char)(this.in[this.s] == '\'' || this.in[this.s] == '\"' ? this.in[this.s++] : 125);
        this.skipWhiteSpace();
        int ptr = 0;
        while (ptr < mask.length) {
            char c;
            if ((c = this.in[this.s++]) == ' ' && this.out[this.d - 1] == ' ') continue;
            if (mask[ptr] == 35 ? !Character.isDigit(c) : mask[ptr] != c) {
                return false;
            }
            if (c != '-') {
                this.append(c);
            }
            ++ptr;
        }
        if (mask.length == 19) {
            int digits = 0;
            if (this.in[this.s] == '.') {
                this.append(this.in[this.s++]);
                while (Character.isDigit(this.in[this.s])) {
                    if (digits < 3) {
                        this.append(this.in[this.s++]);
                        ++digits;
                        continue;
                    }
                    ++this.s;
                }
            } else {
                this.append('.');
            }
            while (digits < 3) {
                this.append('0');
                ++digits;
            }
        }
        this.skipWhiteSpace();
        if (this.in[this.s] != this.terminator) {
            return false;
        }
        if (this.terminator != '}') {
            ++this.s;
        }
        this.skipWhiteSpace();
        this.append('\'');
        if (sel) {
            this.append(')');
        }
        return true;
    }

    private void outerJoinEscape() throws SQLException {
        block5: while (this.in[this.s] != '}') {
            char c = this.in[this.s];
            switch (c) {
                case '\"': 
                case '\'': 
                case '[': {
                    this.copyString();
                    continue block5;
                }
                case '{': {
                    this.escape();
                    continue block5;
                }
                case '?': {
                    this.copyParam(null, this.d);
                    continue block5;
                }
            }
            this.append(c);
            ++this.s;
        }
    }

    private void functionEscape() throws SQLException {
        String fn;
        char tc = this.terminator;
        this.skipWhiteSpace();
        StringBuilder nameBuf = new StringBuilder();
        while (SQLParser.isIdentifier(this.in[this.s])) {
            nameBuf.append(this.in[this.s++]);
        }
        String name = nameBuf.toString().toLowerCase();
        this.skipWhiteSpace();
        this.mustbe('(', false);
        int parenCnt = 1;
        int argStart = this.d;
        int arg2Start = 0;
        this.terminator = (char)41;
        block8: while (this.in[this.s] != ')' || parenCnt > 1) {
            char c = this.in[this.s];
            switch (c) {
                case '\"': 
                case '\'': 
                case '[': {
                    this.copyString();
                    continue block8;
                }
                case '{': {
                    this.escape();
                    continue block8;
                }
                case ',': {
                    if (parenCnt == 1) {
                        if (arg2Start == 0) {
                            arg2Start = this.d - argStart;
                        }
                        if ("concat".equals(name)) {
                            this.append('+');
                            ++this.s;
                            continue block8;
                        }
                        if ("mod".equals(name)) {
                            this.append('%');
                            ++this.s;
                            continue block8;
                        }
                        this.append(c);
                        ++this.s;
                        continue block8;
                    }
                    this.append(c);
                    ++this.s;
                    continue block8;
                }
                case '(': {
                    ++parenCnt;
                    this.append(c);
                    ++this.s;
                    continue block8;
                }
                case ')': {
                    --parenCnt;
                    this.append(c);
                    ++this.s;
                    continue block8;
                }
            }
            this.append(c);
            ++this.s;
        }
        String args = String.valueOf(this.out, argStart, this.d - argStart).trim();
        this.d = argStart;
        this.mustbe(')', false);
        this.terminator = tc;
        this.skipWhiteSpace();
        if ("convert".equals(name) && arg2Start < args.length() - 1) {
            String arg2 = args.substring(arg2Start + 1).trim().toLowerCase();
            String dataType = (String)cvMap.get(arg2);
            if (dataType == null) {
                dataType = arg2;
            }
            this.copyLiteral("convert(");
            this.copyLiteral(dataType);
            this.append(',');
            this.copyLiteral(args.substring(0, arg2Start));
            this.append(')');
            return;
        }
        if (this.connection.getServerType() == 1) {
            fn = (String)msFnMap.get(name);
            if (fn == null) {
                fn = (String)fnMap.get(name);
            }
        } else {
            fn = (String)fnMap.get(name);
        }
        if (fn == null) {
            this.copyLiteral(name);
            this.append('(');
            this.copyLiteral(args);
            this.append(')');
            return;
        }
        if (args.length() > 8 && args.substring(0, 8).equalsIgnoreCase("sql_tsi_") && (args = args.substring(8)).length() > 11 && args.substring(0, 11).equalsIgnoreCase("frac_second")) {
            args = "millisecond" + args.substring(11);
        }
        int len = fn.length();
        for (int i = 0; i < len; ++i) {
            char c = fn.charAt(i);
            if (c == '$') {
                this.copyLiteral(args);
                continue;
            }
            this.append(c);
        }
    }

    private void likeEscape() throws SQLException {
        this.copyLiteral("escape ");
        this.skipWhiteSpace();
        if (this.in[this.s] == '\'' || this.in[this.s] == '\"') {
            this.copyString();
        } else {
            this.mustbe('\'', true);
        }
        this.skipWhiteSpace();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void escape() throws SQLException {
        char tc = this.terminator;
        this.terminator = (char)125;
        StringBuilder escBuf = new StringBuilder();
        ++this.s;
        this.skipWhiteSpace();
        if (this.in[this.s] == '?') {
            this.copyParam("@return_status", -1);
            this.skipWhiteSpace();
            this.mustbe('=', false);
            this.skipWhiteSpace();
            while (Character.isLetter(this.in[this.s])) {
                escBuf.append(Character.toLowerCase(this.in[this.s++]));
            }
            this.skipWhiteSpace();
            String esc = escBuf.toString();
            if (!"call".equals(esc)) throw new SQLException(Messages.get("error.parsesql.syntax", "call", String.valueOf(this.s)), "22019");
            this.callEscape();
        } else {
            while (Character.isLetter(this.in[this.s])) {
                escBuf.append(Character.toLowerCase(this.in[this.s++]));
            }
            this.skipWhiteSpace();
            String esc = escBuf.toString();
            if ("call".equals(esc)) {
                this.callEscape();
            } else if ("t".equals(esc)) {
                if (!this.getDateTimeField(timeMask)) {
                    throw new SQLException(Messages.get("error.parsesql.syntax", "time", String.valueOf(this.s)), "22019");
                }
            } else if ("d".equals(esc)) {
                if (!this.getDateTimeField(dateMask)) {
                    throw new SQLException(Messages.get("error.parsesql.syntax", "date", String.valueOf(this.s)), "22019");
                }
            } else if ("ts".equals(esc)) {
                if (!this.getDateTimeField(timestampMask)) {
                    throw new SQLException(Messages.get("error.parsesql.syntax", "timestamp", String.valueOf(this.s)), "22019");
                }
            } else if ("oj".equals(esc)) {
                this.outerJoinEscape();
            } else if ("fn".equals(esc)) {
                this.functionEscape();
            } else {
                if (!"escape".equals(esc)) throw new SQLException(Messages.get("error.parsesql.badesc", esc, String.valueOf(this.s)), "22019");
                this.likeEscape();
            }
        }
        this.mustbe('}', false);
        this.terminator = tc;
    }

    private String getTableName() throws SQLException {
        int c;
        StringBuilder name = new StringBuilder(128);
        this.copyWhiteSpace();
        int n = c = this.s < this.len ? this.in[this.s] : 32;
        if (c == 123) {
            return "";
        }
        while (c == 47 || c == 45 && this.s + 1 < this.len) {
            if (c == 47) {
                if (this.in[this.s + 1] != '*') break;
                this.skipMultiComments();
            } else {
                if (this.in[this.s + 1] != '-') break;
                this.skipSingleComments();
            }
            this.copyWhiteSpace();
            c = this.s < this.len ? this.in[this.s] : 32;
        }
        if (c == 123) {
            return "";
        }
        while (this.s < this.len) {
            int start;
            if (c == 91 || c == 34) {
                start = this.d;
                this.copyString();
                name.append(String.valueOf(this.out, start, this.d - start));
                this.copyWhiteSpace();
                c = this.s < this.len ? this.in[this.s] : 32;
            } else {
                start = this.d;
                int n2 = c = this.s < this.len ? this.in[this.s++] : 32;
                while (SQLParser.isIdentifier(c) && c != 46 && c != 44) {
                    this.append((char)c);
                    c = this.s < this.len ? this.in[this.s++] : 32;
                }
                name.append(String.valueOf(this.out, start, this.d - start));
                --this.s;
                this.copyWhiteSpace();
                int n3 = c = this.s < this.len ? this.in[this.s] : 32;
            }
            if (c != 46) break;
            name.append((char)c);
            this.append((char)c);
            ++this.s;
            this.copyWhiteSpace();
            c = this.s < this.len ? this.in[this.s] : 32;
        }
        return name.toString();
    }

    private final void append(char[] chars) {
        for (char c : chars) {
            this.append(c);
        }
    }

    private final void append(char character) {
        try {
            this.out[this.d++] = character;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            char[] expanded = new char[this.out.length + 256];
            System.arraycopy(this.out, 0, expanded, 0, this.out.length);
            this.out = expanded;
            this.out[this.d - 1] = character;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    String[] parse(boolean extractTable) throws SQLException {
        boolean isSelect = false;
        boolean isModified = false;
        boolean isSlowScan = true;
        try {
            block9: while (this.s < this.len) {
                char c = this.in[this.s];
                switch (c) {
                    case '{': {
                        this.escape();
                        isModified = true;
                        continue block9;
                    }
                    case '\"': 
                    case '\'': 
                    case '[': {
                        this.copyString();
                        continue block9;
                    }
                    case '?': {
                        this.copyParam(null, this.d);
                        continue block9;
                    }
                    case '/': {
                        if (this.s + 1 < this.len && this.in[this.s + 1] == '*') {
                            this.skipMultiComments();
                            continue block9;
                        }
                        this.append(c);
                        ++this.s;
                        continue block9;
                    }
                    case '-': {
                        if (this.s + 1 < this.len && this.in[this.s + 1] == '-') {
                            this.skipSingleComments();
                            continue block9;
                        }
                        this.append(c);
                        ++this.s;
                        continue block9;
                    }
                }
                if (isSlowScan && Character.isLetter(c)) {
                    if (this.keyWord == null) {
                        this.keyWord = this.copyKeyWord();
                        if ("select".equals(this.keyWord)) {
                            isSelect = true;
                        }
                        isSlowScan = extractTable && isSelect;
                        continue;
                    }
                    if (extractTable && isSelect) {
                        String sqlWord = this.copyKeyWord();
                        if (!"from".equals(sqlWord)) continue;
                        isSlowScan = false;
                        this.tableName = this.getTableName();
                        continue;
                    }
                }
                this.append(c);
                ++this.s;
            }
            if (this.params != null && this.params.size() > 255 && this.connection.getPrepareSql() != 0 && this.procName != null) {
                int limit = 255;
                if (this.connection.getServerType() == 2) {
                    if (this.connection.getDatabaseMajorVersion() > 12 || this.connection.getDatabaseMajorVersion() == 12 && this.connection.getDatabaseMinorVersion() >= 50) {
                        limit = 2000;
                    }
                } else if (this.connection.getDatabaseMajorVersion() == 7) {
                    limit = 1000;
                } else if (this.connection.getDatabaseMajorVersion() > 7) {
                    limit = 2000;
                }
                if (this.params.size() > limit) {
                    throw new SQLException(Messages.get("error.parsesql.toomanyparams", Integer.toString(limit)), "22025");
                }
            }
            String[] result = new String[]{isModified ? new String(this.out, 0, this.d) : this.sql, this.procName, this.keyWord == null ? "" : this.keyWord, this.tableName};
            return result;
        }
        catch (IndexOutOfBoundsException e) {
            throw new SQLException(Messages.get("error.parsesql.missing", String.valueOf(this.terminator)), "22025");
        }
    }

    static {
        msFnMap.put("length", "len($)");
        msFnMap.put("truncate", "round($, 1)");
        fnMap.put("user", "user_name($)");
        fnMap.put("database", "db_name($)");
        fnMap.put("ifnull", "isnull($)");
        fnMap.put("now", "getdate($)");
        fnMap.put("atan2", "atn2($)");
        fnMap.put("mod", "($)");
        fnMap.put("length", "char_length($)");
        fnMap.put("locate", "charindex($)");
        fnMap.put("repeat", "replicate($)");
        fnMap.put("insert", "stuff($)");
        fnMap.put("lcase", "lower($)");
        fnMap.put("ucase", "upper($)");
        fnMap.put("concat", "($)");
        fnMap.put("curdate", "convert(datetime, convert(varchar, getdate(), 112))");
        fnMap.put("curtime", "convert(datetime, convert(varchar, getdate(), 108))");
        fnMap.put("dayname", "datename(weekday,$)");
        fnMap.put("dayofmonth", "datepart(day,$)");
        fnMap.put("dayofweek", "((datepart(weekday,$)+@@DATEFIRST-1)%7+1)");
        fnMap.put("dayofyear", "datepart(dayofyear,$)");
        fnMap.put("hour", "datepart(hour,$)");
        fnMap.put("minute", "datepart(minute,$)");
        fnMap.put("second", "datepart(second,$)");
        fnMap.put("year", "datepart(year,$)");
        fnMap.put("quarter", "datepart(quarter,$)");
        fnMap.put("month", "datepart(month,$)");
        fnMap.put("week", "datepart(week,$)");
        fnMap.put("monthname", "datename(month,$)");
        fnMap.put("timestampadd", "dateadd($)");
        fnMap.put("timestampdiff", "datediff($)");
        cvMap.put("binary", "varbinary");
        cvMap.put("char", "varchar");
        cvMap.put("date", "datetime");
        cvMap.put("double", "float");
        cvMap.put("longvarbinary", "image");
        cvMap.put("longvarchar", "text");
        cvMap.put("time", "datetime");
        cvMap.put("timestamp", "timestamp");
    }

    private static class CachedSQLQuery {
        final String[] parsedSql;
        final String[] paramNames;
        final int[] paramMarkerPos;
        final boolean[] paramIsRetVal;
        final boolean[] paramIsUnicode;

        CachedSQLQuery(String[] parsedSql, ArrayList params) {
            this.parsedSql = parsedSql;
            if (params != null) {
                int size = params.size();
                this.paramNames = new String[size];
                this.paramMarkerPos = new int[size];
                this.paramIsRetVal = new boolean[size];
                this.paramIsUnicode = new boolean[size];
                for (int i = 0; i < size; ++i) {
                    ParamInfo paramInfo = (ParamInfo)params.get(i);
                    this.paramNames[i] = paramInfo.name;
                    this.paramMarkerPos[i] = paramInfo.markerPos;
                    this.paramIsRetVal[i] = paramInfo.isRetVal;
                    this.paramIsUnicode[i] = paramInfo.isUnicode;
                }
            } else {
                this.paramNames = null;
                this.paramMarkerPos = null;
                this.paramIsRetVal = null;
                this.paramIsUnicode = null;
            }
        }
    }
}

