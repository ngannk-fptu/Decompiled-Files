/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.StreamPumper;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Appendable;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.util.StringUtils;

public class SQLExec
extends JDBCTask {
    private int goodSql = 0;
    private int totalSql = 0;
    private Connection conn = null;
    private Union resources;
    private Statement statement = null;
    private File srcFile = null;
    private String sqlCommand = "";
    private List<Transaction> transactions = new Vector<Transaction>();
    private String delimiter = ";";
    private String delimiterType = "normal";
    private boolean print = false;
    private boolean showheaders = true;
    private boolean showtrailers = true;
    private Resource output = null;
    private String outputEncoding = null;
    private String onError = "abort";
    private String encoding = null;
    private boolean append = false;
    private boolean keepformat = false;
    private boolean escapeProcessing = true;
    private boolean expandProperties = true;
    private boolean rawBlobs;
    private boolean strictDelimiterMatching = true;
    private boolean showWarnings = false;
    private String csvColumnSep = ",";
    private String csvQuoteChar = null;
    private boolean treatWarningsAsErrors = false;
    private String errorProperty = null;
    private String warningProperty = null;
    private String rowCountProperty = null;
    private boolean forceCsvQuoteChar = false;

    public void setSrc(File srcFile) {
        this.srcFile = srcFile;
    }

    public void setExpandProperties(boolean expandProperties) {
        this.expandProperties = expandProperties;
    }

    public boolean getExpandProperties() {
        return this.expandProperties;
    }

    public void addText(String sql) {
        this.sqlCommand = this.sqlCommand + sql;
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(ResourceCollection rc) {
        if (rc == null) {
            throw new BuildException("Cannot add null ResourceCollection");
        }
        SQLExec sQLExec = this;
        synchronized (sQLExec) {
            if (this.resources == null) {
                this.resources = new Union();
            }
        }
        this.resources.add(rc);
    }

    public Transaction createTransaction() {
        Transaction t = new Transaction();
        this.transactions.add(t);
        return t;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setDelimiterType(DelimiterType delimiterType) {
        this.delimiterType = delimiterType.getValue();
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public void setShowheaders(boolean showheaders) {
        this.showheaders = showheaders;
    }

    public void setShowtrailers(boolean showtrailers) {
        this.showtrailers = showtrailers;
    }

    public void setOutput(File output) {
        this.setOutput(new FileResource(this.getProject(), output));
    }

    public void setOutput(Resource output) {
        this.output = output;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setOnerror(OnError action) {
        this.onError = action.getValue();
    }

    public void setKeepformat(boolean keepformat) {
        this.keepformat = keepformat;
    }

    public void setEscapeProcessing(boolean enable) {
        this.escapeProcessing = enable;
    }

    public void setRawBlobs(boolean rawBlobs) {
        this.rawBlobs = rawBlobs;
    }

    public void setStrictDelimiterMatching(boolean b) {
        this.strictDelimiterMatching = b;
    }

    public void setShowWarnings(boolean b) {
        this.showWarnings = b;
    }

    public void setTreatWarningsAsErrors(boolean b) {
        this.treatWarningsAsErrors = b;
    }

    public void setCsvColumnSeparator(String s) {
        this.csvColumnSep = s;
    }

    public void setCsvQuoteCharacter(String s) {
        if (s != null && s.length() > 1) {
            throw new BuildException("The quote character must be a single character.");
        }
        this.csvQuoteChar = s;
    }

    public void setErrorProperty(String errorProperty) {
        this.errorProperty = errorProperty;
    }

    public void setWarningProperty(String warningProperty) {
        this.warningProperty = warningProperty;
    }

    public void setRowCountProperty(String rowCountProperty) {
        this.rowCountProperty = rowCountProperty;
    }

    public void setForceCsvQuoteChar(boolean forceCsvQuoteChar) {
        this.forceCsvQuoteChar = forceCsvQuoteChar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        Vector<Transaction> savedTransaction = new Vector<Transaction>(this.transactions);
        String savedSqlCommand = this.sqlCommand;
        this.sqlCommand = this.sqlCommand.trim();
        try {
            if (this.srcFile == null && this.sqlCommand.isEmpty() && this.resources == null && this.transactions.isEmpty()) {
                throw new BuildException("Source file or resource collection, transactions or sql statement must be set!", this.getLocation());
            }
            if (this.srcFile != null && !this.srcFile.isFile()) {
                throw new BuildException("Source file " + this.srcFile + " is not a file!", this.getLocation());
            }
            if (this.resources != null) {
                for (Resource r : this.resources) {
                    Transaction t = this.createTransaction();
                    t.setSrcResource(r);
                }
            }
            Transaction t = this.createTransaction();
            t.setSrc(this.srcFile);
            t.addText(this.sqlCommand);
            if (this.getConnection() == null) {
                return;
            }
            try {
                PrintStream out = KeepAliveOutputStream.wrapSystemOut();
                try {
                    if (this.output != null) {
                        this.log("Opening PrintStream to output Resource " + this.output, 3);
                        OutputStream os = null;
                        FileProvider fp = this.output.as(FileProvider.class);
                        if (fp != null) {
                            os = FileUtils.newOutputStream(fp.getFile().toPath(), this.append);
                        } else {
                            Appendable a;
                            if (this.append && (a = this.output.as(Appendable.class)) != null) {
                                os = a.getAppendOutputStream();
                            }
                            if (os == null) {
                                os = this.output.getOutputStream();
                                if (this.append) {
                                    this.log("Ignoring append=true for non-appendable resource " + this.output, 1);
                                }
                            }
                        }
                        out = this.outputEncoding != null ? new PrintStream((OutputStream)new BufferedOutputStream(os), false, this.outputEncoding) : new PrintStream(new BufferedOutputStream(os));
                    }
                    for (Transaction txn : this.transactions) {
                        txn.runTransaction(out);
                        if (this.isAutocommit()) continue;
                        this.log("Committing transaction", 3);
                        this.getConnection().commit();
                    }
                }
                finally {
                    FileUtils.close(out);
                }
            }
            catch (IOException | SQLException e) {
                this.closeQuietly();
                this.setErrorProperty();
                if ("abort".equals(this.onError)) {
                    throw new BuildException(e, this.getLocation());
                }
            }
            finally {
                try {
                    FileUtils.close(this.getStatement());
                }
                catch (SQLException sQLException) {}
                FileUtils.close(this.getConnection());
            }
            this.log(this.goodSql + " of " + this.totalSql + " SQL statements executed successfully");
        }
        finally {
            this.transactions = savedTransaction;
            this.sqlCommand = savedSqlCommand;
        }
    }

    protected void runStatements(Reader reader, PrintStream out) throws SQLException, IOException {
        String line;
        StringBuffer sql = new StringBuffer();
        BufferedReader in = new BufferedReader(reader);
        while ((line = in.readLine()) != null) {
            int lastDelimPos;
            String token;
            StringTokenizer st;
            if (!this.keepformat) {
                line = line.trim();
            }
            if (this.expandProperties) {
                line = this.getProject().replaceProperties(line);
            }
            if (!this.keepformat && (line.startsWith("//") || line.startsWith("--") || (st = new StringTokenizer(line)).hasMoreTokens() && "REM".equalsIgnoreCase(token = st.nextToken()))) continue;
            sql.append(this.keepformat ? "\n" : " ").append(line);
            if (!this.keepformat && line.contains("--")) {
                sql.append("\n");
            }
            if ((lastDelimPos = this.lastDelimiterPosition(sql, line)) <= -1) continue;
            this.execSQL(sql.substring(0, lastDelimPos), out);
            sql.replace(0, sql.length(), "");
        }
        if (sql.length() > 0) {
            this.execSQL(sql.toString(), out);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void execSQL(String sql, PrintStream out) throws SQLException {
        if (sql.trim().isEmpty()) {
            return;
        }
        ResultSet resultSet = null;
        try {
            ++this.totalSql;
            this.log("SQL: " + sql, 3);
            int updateCount = 0;
            int updateCountTotal = 0;
            boolean ret = this.getStatement().execute(sql);
            updateCount = this.getStatement().getUpdateCount();
            do {
                if (updateCount != -1) {
                    updateCountTotal += updateCount;
                }
                if (ret) {
                    resultSet = this.getStatement().getResultSet();
                    this.printWarnings(resultSet.getWarnings(), false);
                    resultSet.clearWarnings();
                    if (this.print) {
                        this.printResults(resultSet, out);
                    }
                }
                ret = this.getStatement().getMoreResults();
                updateCount = this.getStatement().getUpdateCount();
            } while (ret || updateCount != -1);
            this.printWarnings(this.getStatement().getWarnings(), false);
            this.getStatement().clearWarnings();
            this.log(updateCountTotal + " rows affected", 3);
            if (updateCountTotal != -1) {
                this.setRowCountProperty(updateCountTotal);
            }
            if (this.print && this.showtrailers) {
                out.println(updateCountTotal + " rows affected");
            }
            SQLWarning warning = this.getConnection().getWarnings();
            this.printWarnings(warning, true);
            this.getConnection().clearWarnings();
            ++this.goodSql;
            FileUtils.close(resultSet);
        }
        catch (SQLException e) {
            this.log("Failed to execute: " + sql, 0);
            this.setErrorProperty();
            if (!"abort".equals(this.onError)) {
                this.log(e.toString(), 0);
            }
            if (!"continue".equals(this.onError)) {
                throw e;
            }
        }
        finally {
            FileUtils.close(resultSet);
        }
    }

    @Deprecated
    protected void printResults(PrintStream out) throws SQLException {
        try (ResultSet rs = this.getStatement().getResultSet();){
            this.printResults(rs, out);
        }
    }

    protected void printResults(ResultSet rs, PrintStream out) throws SQLException {
        if (rs != null) {
            this.log("Processing new result set.", 3);
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            if (columnCount > 0) {
                int col;
                if (this.showheaders) {
                    out.print(this.maybeQuote(md.getColumnName(1)));
                    for (col = 2; col <= columnCount; ++col) {
                        out.print(this.csvColumnSep);
                        out.print(this.maybeQuote(md.getColumnName(col)));
                    }
                    out.println();
                }
                while (rs.next()) {
                    this.printValue(rs, 1, out);
                    for (col = 2; col <= columnCount; ++col) {
                        out.print(this.csvColumnSep);
                        this.printValue(rs, col, out);
                    }
                    out.println();
                    this.printWarnings(rs.getWarnings(), false);
                }
            }
        }
        out.println();
    }

    private void printValue(ResultSet rs, int col, PrintStream out) throws SQLException {
        if (this.rawBlobs && rs.getMetaData().getColumnType(col) == 2004) {
            Blob blob = rs.getBlob(col);
            if (blob != null) {
                new StreamPumper(rs.getBlob(col).getBinaryStream(), out).run();
            }
        } else {
            out.print(this.maybeQuote(rs.getString(col)));
        }
    }

    private String maybeQuote(String s) {
        if (this.csvQuoteChar == null || s == null || !this.forceCsvQuoteChar && !s.contains(this.csvColumnSep) && !s.contains(this.csvQuoteChar)) {
            return s;
        }
        StringBuilder sb = new StringBuilder(this.csvQuoteChar);
        char q = this.csvQuoteChar.charAt(0);
        for (char c : s.toCharArray()) {
            if (c == q) {
                sb.append(q);
            }
            sb.append(c);
        }
        return sb.append(this.csvQuoteChar).toString();
    }

    private void closeQuietly() {
        if (!this.isAutocommit() && this.getConnection() != null && "abort".equals(this.onError)) {
            try {
                this.getConnection().rollback();
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    @Override
    protected Connection getConnection() {
        if (this.conn == null) {
            this.conn = super.getConnection();
            if (!this.isValidRdbms(this.conn)) {
                this.conn = null;
            }
        }
        return this.conn;
    }

    protected Statement getStatement() throws SQLException {
        if (this.statement == null) {
            this.statement = this.getConnection().createStatement();
            this.statement.setEscapeProcessing(this.escapeProcessing);
        }
        return this.statement;
    }

    public int lastDelimiterPosition(StringBuffer buf, String currentLine) {
        if (this.strictDelimiterMatching) {
            if (this.delimiterType.equals("normal") && StringUtils.endsWith(buf, this.delimiter) || this.delimiterType.equals("row") && currentLine.equals(this.delimiter)) {
                return buf.length() - this.delimiter.length();
            }
            return -1;
        }
        String d = this.delimiter.trim().toLowerCase(Locale.ENGLISH);
        if ("normal".equals(this.delimiterType)) {
            int bufferIndex;
            int endIndex = this.delimiter.length() - 1;
            for (bufferIndex = buf.length() - 1; bufferIndex >= 0 && Character.isWhitespace(buf.charAt(bufferIndex)); --bufferIndex) {
            }
            if (bufferIndex < endIndex) {
                return -1;
            }
            while (endIndex >= 0) {
                if (buf.substring(bufferIndex, bufferIndex + 1).toLowerCase(Locale.ENGLISH).charAt(0) != d.charAt(endIndex)) {
                    return -1;
                }
                --bufferIndex;
                --endIndex;
            }
            return bufferIndex + 1;
        }
        return currentLine.trim().toLowerCase(Locale.ENGLISH).equals(d) ? buf.length() - currentLine.length() : -1;
    }

    private void printWarnings(SQLWarning warning, boolean force) throws SQLException {
        SQLWarning initialWarning = warning;
        if (this.showWarnings || force) {
            while (warning != null) {
                this.log(warning + " sql warning", this.showWarnings ? 1 : 3);
                warning = warning.getNextWarning();
            }
        }
        if (initialWarning != null) {
            this.setWarningProperty();
        }
        if (this.treatWarningsAsErrors && initialWarning != null) {
            throw initialWarning;
        }
    }

    protected final void setErrorProperty() {
        this.setProperty(this.errorProperty, "true");
    }

    protected final void setWarningProperty() {
        this.setProperty(this.warningProperty, "true");
    }

    protected final void setRowCountProperty(int rowCount) {
        this.setProperty(this.rowCountProperty, Integer.toString(rowCount));
    }

    private void setProperty(String name, String value) {
        if (name != null) {
            this.getProject().setNewProperty(name, value);
        }
    }

    public static class DelimiterType
    extends EnumeratedAttribute {
        public static final String NORMAL = "normal";
        public static final String ROW = "row";

        @Override
        public String[] getValues() {
            return new String[]{NORMAL, ROW};
        }
    }

    public class Transaction {
        private Resource tSrcResource = null;
        private String tSqlCommand = "";

        public void setSrc(File src) {
            if (src != null) {
                this.setSrcResource(new FileResource(src));
            }
        }

        public void setSrcResource(Resource src) {
            if (this.tSrcResource != null) {
                throw new BuildException("only one resource per transaction");
            }
            this.tSrcResource = src;
        }

        public void addText(String sql) {
            if (sql != null) {
                this.tSqlCommand = this.tSqlCommand + sql;
            }
        }

        public void addConfigured(ResourceCollection a) {
            if (a.size() != 1) {
                throw new BuildException("only single argument resource collections are supported.");
            }
            this.setSrcResource((Resource)a.iterator().next());
        }

        private void runTransaction(PrintStream out) throws IOException, SQLException {
            if (!this.tSqlCommand.isEmpty()) {
                SQLExec.this.log("Executing commands", 2);
                SQLExec.this.runStatements(new StringReader(this.tSqlCommand), out);
            }
            if (this.tSrcResource != null) {
                SQLExec.this.log("Executing resource: " + this.tSrcResource.toString(), 2);
                Charset charset = SQLExec.this.encoding == null ? Charset.defaultCharset() : Charset.forName(SQLExec.this.encoding);
                try (InputStreamReader reader = new InputStreamReader(this.tSrcResource.getInputStream(), charset);){
                    SQLExec.this.runStatements(reader, out);
                }
            }
        }
    }

    public static class OnError
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"continue", "stop", "abort"};
        }
    }
}

