/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib.sql;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.lib.sql.QueryParameter;
import org.apache.xalan.lib.sql.XConnection;
import org.apache.xml.utils.QName;
import org.apache.xpath.objects.XObject;

public class SQLQueryParser {
    private boolean m_InlineVariables = false;
    private boolean m_IsCallable = false;
    private String m_OrigQuery = null;
    private StringBuffer m_ParsedQuery = null;
    private Vector m_Parameters = null;
    private boolean m_hasOutput = false;
    private boolean m_HasParameters;
    public static final int NO_OVERRIDE = 0;
    public static final int NO_INLINE_PARSER = 1;
    public static final int INLINE_PARSER = 2;

    public SQLQueryParser() {
        this.init();
    }

    private SQLQueryParser(String query) {
        this.m_OrigQuery = query;
    }

    private void init() {
    }

    public SQLQueryParser parse(XConnection xconn, String query, int override) {
        SQLQueryParser parser = new SQLQueryParser(query);
        parser.parse(xconn, override);
        return parser;
    }

    private void parse(XConnection xconn, int override) {
        this.m_InlineVariables = "true".equals(xconn.getFeature("inline-variables"));
        if (override == 1) {
            this.m_InlineVariables = false;
        } else if (override == 2) {
            this.m_InlineVariables = true;
        }
        if (this.m_InlineVariables) {
            this.inlineParser();
        }
    }

    public boolean hasParameters() {
        return this.m_HasParameters;
    }

    public boolean isCallable() {
        return this.m_IsCallable;
    }

    public Vector getParameters() {
        return this.m_Parameters;
    }

    public void setParameters(Vector p) {
        this.m_HasParameters = true;
        this.m_Parameters = p;
    }

    public String getSQLQuery() {
        if (this.m_InlineVariables) {
            return this.m_ParsedQuery.toString();
        }
        return this.m_OrigQuery;
    }

    public void populateStatement(PreparedStatement stmt, ExpressionContext ctx) {
        for (int indx = 0; indx < this.m_Parameters.size(); ++indx) {
            QueryParameter parm = (QueryParameter)this.m_Parameters.elementAt(indx);
            try {
                Object value;
                if (this.m_InlineVariables) {
                    value = ctx.getVariableOrParam(new QName(parm.getName()));
                    if (value != null) {
                        stmt.setObject(indx + 1, ((XObject)value).object(), parm.getType(), 4);
                        continue;
                    }
                    stmt.setNull(indx + 1, parm.getType());
                    continue;
                }
                value = parm.getValue();
                if (value != null) {
                    stmt.setObject(indx + 1, value, parm.getType(), 4);
                    continue;
                }
                stmt.setNull(indx + 1, parm.getType());
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void registerOutputParameters(CallableStatement cstmt) throws SQLException {
        if (this.m_IsCallable && this.m_hasOutput) {
            for (int indx = 0; indx < this.m_Parameters.size(); ++indx) {
                QueryParameter parm = (QueryParameter)this.m_Parameters.elementAt(indx);
                if (!parm.isOutput()) continue;
                cstmt.registerOutParameter(indx + 1, parm.getType());
            }
        }
    }

    protected void inlineParser() {
        QueryParameter curParm = null;
        int state = 0;
        StringBuffer tok = new StringBuffer();
        boolean firstword = true;
        if (this.m_Parameters == null) {
            this.m_Parameters = new Vector();
        }
        if (this.m_ParsedQuery == null) {
            this.m_ParsedQuery = new StringBuffer();
        }
        block11: for (int idx = 0; idx < this.m_OrigQuery.length(); ++idx) {
            char ch = this.m_OrigQuery.charAt(idx);
            switch (state) {
                case 0: {
                    if (ch == '\'') {
                        state = 1;
                    } else if (ch == '?') {
                        state = 4;
                    } else if (firstword && (Character.isLetterOrDigit(ch) || ch == '#')) {
                        tok.append(ch);
                        state = 3;
                    }
                    this.m_ParsedQuery.append(ch);
                    continue block11;
                }
                case 1: {
                    if (ch == '\'') {
                        state = 0;
                    } else if (ch == '\\') {
                        state = 2;
                    }
                    this.m_ParsedQuery.append(ch);
                    continue block11;
                }
                case 2: {
                    state = 1;
                    this.m_ParsedQuery.append(ch);
                    continue block11;
                }
                case 3: {
                    if (Character.isLetterOrDigit(ch) || ch == '#' || ch == '_') {
                        tok.append(ch);
                    } else {
                        if (tok.toString().equalsIgnoreCase("call")) {
                            this.m_IsCallable = true;
                            if (curParm != null) {
                                curParm.setIsOutput(true);
                            }
                        }
                        firstword = false;
                        tok = new StringBuffer();
                        state = ch == '\'' ? 1 : (ch == '?' ? 4 : 0);
                    }
                    this.m_ParsedQuery.append(ch);
                    continue block11;
                }
                case 4: {
                    if (ch != '[') continue block11;
                    state = 5;
                    continue block11;
                }
                case 5: {
                    if (!Character.isWhitespace(ch) && ch != '=') {
                        tok.append(Character.toUpperCase(ch));
                        continue block11;
                    }
                    if (tok.length() <= 0) continue block11;
                    this.m_HasParameters = true;
                    curParm = new QueryParameter();
                    curParm.setTypeName(tok.toString());
                    this.m_Parameters.addElement(curParm);
                    tok = new StringBuffer();
                    if (ch == '=') {
                        state = 7;
                        continue block11;
                    }
                    state = 6;
                    continue block11;
                }
                case 6: {
                    if (ch != '=') continue block11;
                    state = 7;
                    continue block11;
                }
                case 7: {
                    if (!Character.isWhitespace(ch) && ch != ']') {
                        tok.append(ch);
                        continue block11;
                    }
                    if (tok.length() <= 0) continue block11;
                    curParm.setName(tok.toString());
                    tok = new StringBuffer();
                    if (ch == ']') {
                        state = 0;
                        continue block11;
                    }
                    state = 8;
                    continue block11;
                }
                case 8: {
                    if (!Character.isWhitespace(ch) && ch != ']') {
                        tok.append(ch);
                        continue block11;
                    }
                    if (tok.length() <= 0) continue block11;
                    tok.setLength(3);
                    if (tok.toString().equalsIgnoreCase("OUT")) {
                        curParm.setIsOutput(true);
                        this.m_hasOutput = true;
                    }
                    tok = new StringBuffer();
                    if (ch != ']') continue block11;
                    state = 0;
                }
            }
        }
        if (this.m_IsCallable) {
            this.m_ParsedQuery.insert(0, '{');
            this.m_ParsedQuery.append('}');
        }
    }
}

