/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;
import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.query.sql.ASTAndExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTAscendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTBracketExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTContainsExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTDescendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTExcerptFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTFromClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTIdentifier;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLiteral;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLowerFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTNotExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderByClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTPredicate;
import org.apache.jackrabbit.spi.commons.query.sql.ASTQuery;
import org.apache.jackrabbit.spi.commons.query.sql.ASTSelectList;
import org.apache.jackrabbit.spi.commons.query.sql.ASTUpperFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTWhereClause;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserConstants;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserTokenManager;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserTreeConstants;
import org.apache.jackrabbit.spi.commons.query.sql.JJTJCRSQLParserState;
import org.apache.jackrabbit.spi.commons.query.sql.Node;
import org.apache.jackrabbit.spi.commons.query.sql.ParseException;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleCharStream;
import org.apache.jackrabbit.spi.commons.query.sql.Token;

public class JCRSQLParser
implements JCRSQLParserTreeConstants,
JCRSQLParserConstants {
    protected JJTJCRSQLParserState jjtree = new JJTJCRSQLParserState();
    private String statement;
    private NameResolver resolver;
    public JCRSQLParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1 = new int[35];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private Vector jj_expentries = new Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;

    public static void main(String[] args) throws ParseException {
        JCRSQLParser parser = new JCRSQLParser(System.in);
        parser.Query().dump("");
    }

    public static ASTQuery parse(String statement, NameResolver resolver) throws ParseException {
        StringReader sReader = new StringReader(statement);
        JCRSQLParser parser = new JCRSQLParser(sReader);
        parser.setNameResolver(resolver);
        return parser.Query();
    }

    void setNameResolver(NameResolver resolver) {
        this.resolver = resolver;
    }

    public final ASTQuery Query() throws ParseException {
        ASTQuery jjtn000 = new ASTQuery(0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(25);
            this.SelectList();
            this.TableExpression();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 21: {
                    this.OrderByClause();
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                }
            }
            this.jj_consume_token(0);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            ASTQuery aSTQuery = jjtn000;
            return aSTQuery;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void SelectList() throws ParseException {
        ASTSelectList jjtn000 = new ASTSelectList(1);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 44: {
                    this.jj_consume_token(44);
                    return;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 17: 
                case 18: 
                case 19: 
                case 21: 
                case 23: 
                case 25: 
                case 26: 
                case 27: 
                case 60: 
                case 64: {
                    this.SelectItem();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 46: {
                                break;
                            }
                            default: {
                                this.jj_la1[1] = this.jj_gen;
                                return;
                            }
                        }
                        this.jj_consume_token(46);
                        this.SelectItem();
                    }
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void SelectItem() throws ParseException {
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 27: {
                this.ExcerptFunction();
                break;
            }
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 17: 
            case 18: 
            case 19: 
            case 21: 
            case 23: 
            case 25: 
            case 26: 
            case 60: 
            case 64: {
                this.Identifier();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 48: {
                        this.jj_consume_token(48);
                        this.Identifier();
                        Node n = this.jjtree.popNode();
                        this.jjtree.popNode();
                        this.jjtree.pushNode(n);
                        break block0;
                    }
                }
                this.jj_la1[3] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[4] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void TableExpression() throws ParseException {
        this.FromClause();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 23: {
                this.WhereClause();
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
            }
        }
    }

    public final void FromClause() throws ParseException {
        block14: {
            ASTFromClause jjtn000 = new ASTFromClause(3);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(19);
                this.Identifier();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 46: {
                            break;
                        }
                        default: {
                            this.jj_la1[6] = this.jj_gen;
                            break block14;
                        }
                    }
                    this.jj_consume_token(46);
                    this.Identifier();
                }
            }
            catch (Throwable jjte000) {
                if (jjtc000) {
                    this.jjtree.clearNodeScope(jjtn000);
                    jjtc000 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw (RuntimeException)jjte000;
                }
                if (jjte000 instanceof ParseException) {
                    throw (ParseException)jjte000;
                }
                throw (Error)jjte000;
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
    }

    public final void WhereClause() throws ParseException {
        ASTWhereClause jjtn000 = new ASTWhereClause(4);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(23);
            this.SearchCondition();
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Predicate() throws ParseException {
        ASTPredicate jjtn000 = new ASTPredicate(5);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 25: 
                case 26: 
                case 60: 
                case 64: {
                    Node n;
                    Name identifier;
                    block8 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 9: 
                        case 10: 
                        case 11: 
                        case 12: 
                        case 13: 
                        case 17: 
                        case 18: 
                        case 19: 
                        case 21: 
                        case 23: 
                        case 25: 
                        case 26: 
                        case 60: 
                        case 64: {
                            identifier = this.Identifier();
                            jjtn000.setIdentifier(identifier);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 48: {
                                    this.jj_consume_token(48);
                                    identifier = this.Identifier();
                                    n = this.jjtree.popNode();
                                    this.jjtree.popNode();
                                    this.jjtree.pushNode(n);
                                    jjtn000.setIdentifier(identifier);
                                    break block8;
                                }
                            }
                            this.jj_la1[7] = this.jj_gen;
                            break;
                        }
                        case 20: 
                        case 22: {
                            identifier = this.PropertyFunction();
                            jjtn000.setIdentifier(identifier);
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 52: 
                        case 53: 
                        case 54: 
                        case 69: 
                        case 70: 
                        case 71: {
                            int operationType = this.ComparisonOperation();
                            jjtn000.setOperationType(operationType);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 74: 
                                case 76: 
                                case 81: 
                                case 98: {
                                    this.Literal();
                                    return;
                                }
                                case 9: 
                                case 10: 
                                case 11: 
                                case 12: 
                                case 13: 
                                case 17: 
                                case 18: 
                                case 19: 
                                case 21: 
                                case 23: 
                                case 25: 
                                case 26: 
                                case 60: 
                                case 64: {
                                    identifier = this.Identifier();
                                    jjtn000.setIdentifier(identifier);
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 48: {
                                            this.jj_consume_token(48);
                                            identifier = this.Identifier();
                                            n = this.jjtree.popNode();
                                            this.jjtree.popNode();
                                            this.jjtree.pushNode(n);
                                            jjtn000.setIdentifier(identifier);
                                            return;
                                        }
                                    }
                                    this.jj_la1[9] = this.jj_gen;
                                    return;
                                }
                            }
                            this.jj_la1[10] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                        case 15: 
                        case 17: 
                        case 26: {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 15: {
                                    this.jj_consume_token(15);
                                    jjtn000.setNegate(true);
                                    break;
                                }
                                default: {
                                    this.jj_la1[11] = this.jj_gen;
                                }
                            }
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 26: {
                                    this.jj_consume_token(26);
                                    jjtn000.setOperationType(24);
                                    this.Literal();
                                    this.jj_consume_token(13);
                                    this.Literal();
                                    return;
                                }
                                case 17: {
                                    this.jj_consume_token(17);
                                    jjtn000.setOperationType(23);
                                    String value = this.CharStringLiteral();
                                    ASTLiteral s = new ASTLiteral(13);
                                    s.setType(3);
                                    s.setValue(value);
                                    this.jjtree.pushNode(s);
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 24: {
                                            this.jj_consume_token(24);
                                            String escapeString = this.CharStringLiteral();
                                            jjtn000.setEscapeString(escapeString);
                                            return;
                                        }
                                    }
                                    this.jj_la1[12] = this.jj_gen;
                                    return;
                                }
                            }
                            this.jj_la1[13] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                        case 12: {
                            this.jj_consume_token(12);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 15: {
                                    this.jj_consume_token(15);
                                    jjtn000.setNegate(true);
                                    break;
                                }
                                default: {
                                    this.jj_la1[14] = this.jj_gen;
                                }
                            }
                            this.jj_consume_token(18);
                            this.jjtree.closeNodeScope((Node)jjtn000, true);
                            jjtc000 = false;
                            jjtn000.setOperationType(jjtn000.isNegate() ? 27 : 26);
                            return;
                        }
                    }
                    this.jj_la1[15] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                case 74: 
                case 76: 
                case 81: 
                case 98: {
                    Name identifier;
                    this.Literal();
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 15: {
                            this.jj_consume_token(15);
                            jjtn000.setNegate(true);
                            break;
                        }
                        default: {
                            this.jj_la1[16] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(10);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 9: 
                        case 10: 
                        case 11: 
                        case 12: 
                        case 13: 
                        case 17: 
                        case 18: 
                        case 19: 
                        case 21: 
                        case 23: 
                        case 25: 
                        case 26: 
                        case 60: 
                        case 64: {
                            identifier = this.Identifier();
                            break;
                        }
                        case 20: 
                        case 22: {
                            identifier = this.PropertyFunction();
                            break;
                        }
                        default: {
                            this.jj_la1[17] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setIdentifier(identifier);
                    jjtn000.setOperationType(jjtn000.isNegate() ? 14 : 12);
                    return;
                }
                case 28: {
                    this.jj_consume_token(28);
                    this.jj_consume_token(42);
                    jjtn000.setOperationType(28);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 48: {
                            this.jj_consume_token(48);
                            break;
                        }
                        case 9: 
                        case 10: 
                        case 11: 
                        case 12: 
                        case 13: 
                        case 17: 
                        case 18: 
                        case 19: 
                        case 21: 
                        case 23: 
                        case 25: 
                        case 26: 
                        case 60: 
                        case 64: {
                            Name identifier = this.Identifier();
                            jjtn000.setIdentifier(identifier);
                            break;
                        }
                        default: {
                            this.jj_la1[18] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    this.jj_consume_token(46);
                    String value = this.CharStringLiteral();
                    ASTLiteral s = new ASTLiteral(13);
                    s.setType(3);
                    s.setValue(value);
                    this.jjtree.pushNode(s);
                    this.jj_consume_token(43);
                    return;
                }
                case 30: {
                    this.jj_consume_token(30);
                    this.jj_consume_token(42);
                    jjtn000.setOperationType(29);
                    String value = this.CharStringLiteral();
                    ASTLiteral stmt = new ASTLiteral(13);
                    stmt.setType(3);
                    stmt.setValue(value);
                    this.jjtree.pushNode(stmt);
                    this.jj_consume_token(43);
                    return;
                }
                default: {
                    this.jj_la1[19] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final Name PropertyFunction() throws ParseException {
        Name identifier;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 20: {
                identifier = this.LowerFunction();
                break;
            }
            case 22: {
                identifier = this.UpperFunction();
                break;
            }
            default: {
                this.jj_la1[20] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return identifier;
    }

    public final Name LowerFunction() throws ParseException {
        ASTLowerFunction jjtn000 = new ASTLowerFunction(6);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(20);
            this.jj_consume_token(42);
            Name identifier = this.Identifier();
            this.jj_consume_token(43);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            Name name = identifier;
            return name;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final Name UpperFunction() throws ParseException {
        ASTUpperFunction jjtn000 = new ASTUpperFunction(7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(22);
            this.jj_consume_token(42);
            Name identifier = this.Identifier();
            this.jj_consume_token(43);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            Name name = identifier;
            return name;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final int ComparisonOperation() throws ParseException {
        int operationType;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 53: {
                this.jj_consume_token(53);
                operationType = 12;
                break;
            }
            case 69: {
                this.jj_consume_token(69);
                operationType = 14;
                break;
            }
            case 52: {
                this.jj_consume_token(52);
                operationType = 16;
                break;
            }
            case 54: {
                this.jj_consume_token(54);
                operationType = 18;
                break;
            }
            case 71: {
                this.jj_consume_token(71);
                operationType = 22;
                break;
            }
            case 70: {
                this.jj_consume_token(70);
                operationType = 20;
                break;
            }
            default: {
                this.jj_la1[21] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return operationType;
    }

    public final void SearchCondition() throws ParseException {
        this.OrExpression();
    }

    public final void OrExpression() throws ParseException {
        ASTOrExpression jjtn001 = new ASTOrExpression(8);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.AndExpression();
            block7: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 11: {
                        break;
                    }
                    default: {
                        this.jj_la1[22] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(11);
                this.AndExpression();
            }
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte001) {
            try {
                if (jjtc001) {
                    this.jjtree.clearNodeScope(jjtn001);
                    jjtc001 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw (RuntimeException)jjte001;
                }
                if (jjte001 instanceof ParseException) {
                    throw (ParseException)jjte001;
                }
                throw (Error)jjte001;
            }
            catch (Throwable throwable) {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    public final void AndExpression() throws ParseException {
        ASTAndExpression jjtn001 = new ASTAndExpression(9);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.UnaryExpression();
            block7: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 13: {
                        break;
                    }
                    default: {
                        this.jj_la1[23] = this.jj_gen;
                        break block7;
                    }
                }
                this.jj_consume_token(13);
                this.UnaryExpression();
            }
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, this.jjtree.nodeArity() > 1);
            }
        }
        catch (Throwable jjte001) {
            try {
                if (jjtc001) {
                    this.jjtree.clearNodeScope(jjtn001);
                    jjtc001 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte001 instanceof RuntimeException) {
                    throw (RuntimeException)jjte001;
                }
                if (jjte001 instanceof ParseException) {
                    throw (ParseException)jjte001;
                }
                throw (Error)jjte001;
            }
            catch (Throwable throwable) {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, this.jjtree.nodeArity() > 1);
                }
                throw throwable;
            }
        }
    }

    public final void UnaryExpression() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 15: {
                ASTNotExpression jjtn001 = new ASTNotExpression(10);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jj_consume_token(15);
                    this.UnaryExpression();
                    break;
                }
                catch (Throwable jjte001) {
                    if (jjtc001) {
                        this.jjtree.clearNodeScope(jjtn001);
                        jjtc001 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte001 instanceof RuntimeException) {
                        throw (RuntimeException)jjte001;
                    }
                    if (jjte001 instanceof ParseException) {
                        throw (ParseException)jjte001;
                    }
                    throw (Error)jjte001;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 25: 
            case 26: 
            case 28: 
            case 29: 
            case 30: 
            case 42: 
            case 60: 
            case 64: 
            case 74: 
            case 76: 
            case 81: 
            case 98: {
                this.PrimaryExpression();
                break;
            }
            default: {
                this.jj_la1[24] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void PrimaryExpression() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 25: 
            case 26: 
            case 28: 
            case 30: 
            case 60: 
            case 64: 
            case 74: 
            case 76: 
            case 81: 
            case 98: {
                this.Predicate();
                break;
            }
            case 42: {
                this.BracketExpression();
                break;
            }
            case 29: {
                this.ContainsExpression();
                break;
            }
            default: {
                this.jj_la1[25] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void BracketExpression() throws ParseException {
        ASTBracketExpression jjtn000 = new ASTBracketExpression(11);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(42);
            this.SearchCondition();
            this.jj_consume_token(43);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void ContainsExpression() throws ParseException {
        ASTContainsExpression jjtn000 = new ASTContainsExpression(12);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        Name name = null;
        try {
            this.jj_consume_token(29);
            this.jj_consume_token(42);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 44: {
                    this.jj_consume_token(44);
                    break;
                }
                case 48: {
                    this.jj_consume_token(48);
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 17: 
                case 18: 
                case 19: 
                case 21: 
                case 23: 
                case 25: 
                case 26: 
                case 60: 
                case 64: {
                    name = this.Identifier();
                    jjtn000.setPropertyName(name);
                    break;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jj_consume_token(46);
            t = this.jj_consume_token(98);
            jjtn000.setQuery(t.image.substring(1, t.image.length() - 1).replaceAll("''", "'"));
            this.jj_consume_token(43);
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void Literal() throws ParseException {
        ASTLiteral jjtn000 = new ASTLiteral(13);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 76: {
                    t = this.jj_consume_token(76);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setType(2);
                    jjtn000.setValue(t.image);
                    return;
                }
                case 74: {
                    t = this.jj_consume_token(74);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    if (t.image.indexOf(46) > -1) {
                        jjtn000.setType(2);
                    } else {
                        jjtn000.setType(1);
                    }
                    jjtn000.setValue(t.image);
                    return;
                }
                case 98: {
                    String value = this.CharStringLiteral();
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setType(3);
                    jjtn000.setValue(value);
                    return;
                }
                case 81: {
                    t = this.jj_consume_token(81);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    if (t.image.startsWith("TIMESTAMP")) {
                        jjtn000.setValue(t.image.substring(t.image.indexOf(39) + 1, t.image.length() - 1));
                        if (jjtn000.getValue().indexOf(" ") == 10) {
                            StringBuffer tmp = new StringBuffer();
                            tmp.append(jjtn000.getValue().substring(0, 10));
                            tmp.append("T").append(jjtn000.getValue().substring(11));
                            jjtn000.setValue(tmp.toString());
                        }
                        jjtn000.setType(5);
                        return;
                    } else {
                        jjtn000.setValue(t.image.substring(t.image.indexOf(39) + 1, t.image.length() - 1));
                        jjtn000.setType(4);
                        return;
                    }
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final String CharStringLiteral() throws ParseException {
        String value = "";
        Token t = this.jj_consume_token(98);
        value = value + t.image.substring(1, t.image.length() - 1);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 98: {
                    break;
                }
                default: {
                    this.jj_la1[28] = this.jj_gen;
                    break block3;
                }
            }
            t = this.jj_consume_token(98);
            value = value + t.image.substring(1, t.image.length() - 1);
        }
        return value.replaceAll("''", "'");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Name Identifier() throws ParseException {
        ASTIdentifier jjtn000 = new ASTIdentifier(14);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        Object name = null;
        boolean pseudoProperty = false;
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 60: {
                    t = this.jj_consume_token(60);
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 42: {
                            this.jj_consume_token(42);
                            this.jj_consume_token(43);
                            pseudoProperty = true;
                            break;
                        }
                        default: {
                            this.jj_la1[29] = this.jj_gen;
                        }
                    }
                    try {
                        String jcrName = t.image;
                        if (pseudoProperty) {
                            jcrName = jcrName + "()";
                        }
                        jjtn000.setName(this.resolver.getQName(jcrName));
                        break;
                    }
                    catch (NameException e) {
                        throw new ParseException(e.getMessage());
                    }
                    catch (NamespaceException e) {
                        throw new ParseException(e.getMessage());
                    }
                }
                case 64: {
                    t = this.jj_consume_token(64);
                    try {
                        jjtn000.setName(this.resolver.getQName(t.image.substring(1, t.image.length() - 1)));
                        break;
                    }
                    catch (NameException e) {
                        throw new ParseException(e.getMessage());
                    }
                    catch (NamespaceException e) {
                        throw new ParseException(e.getMessage());
                    }
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 17: 
                case 18: 
                case 19: 
                case 21: 
                case 23: 
                case 25: 
                case 26: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 9: {
                            t = this.jj_consume_token(9);
                            break;
                        }
                        case 10: {
                            t = this.jj_consume_token(10);
                            break;
                        }
                        case 11: {
                            t = this.jj_consume_token(11);
                            break;
                        }
                        case 12: {
                            t = this.jj_consume_token(12);
                            break;
                        }
                        case 13: {
                            t = this.jj_consume_token(13);
                            break;
                        }
                        case 17: {
                            t = this.jj_consume_token(17);
                            break;
                        }
                        case 18: {
                            t = this.jj_consume_token(18);
                            break;
                        }
                        case 19: {
                            t = this.jj_consume_token(19);
                            break;
                        }
                        case 21: {
                            t = this.jj_consume_token(21);
                            break;
                        }
                        case 23: {
                            t = this.jj_consume_token(23);
                            break;
                        }
                        case 25: {
                            t = this.jj_consume_token(25);
                            break;
                        }
                        case 26: {
                            t = this.jj_consume_token(26);
                            break;
                        }
                        default: {
                            this.jj_la1[30] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    try {
                        jjtn000.setName(this.resolver.getQName(t.image));
                        break;
                    }
                    catch (NameException e) {
                        throw new ParseException(e.getMessage());
                    }
                    catch (NamespaceException e) {
                        throw new ParseException(e.getMessage());
                    }
                }
                default: {
                    this.jj_la1[31] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            Name name2 = jjtn000.getName();
            return name2;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final Name ExcerptFunction() throws ParseException {
        ASTExcerptFunction jjtn000 = new ASTExcerptFunction(15);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(27);
            this.jj_consume_token(42);
            this.jj_consume_token(48);
            this.jj_consume_token(43);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            Name name = NameFactoryImpl.getInstance().create("internal", "excerpt(.)");
            return name;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void OrderByClause() throws ParseException {
        block14: {
            ASTOrderByClause jjtn000 = new ASTOrderByClause(16);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(21);
                this.jj_consume_token(9);
                this.OrderSpec();
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 46: {
                            break;
                        }
                        default: {
                            this.jj_la1[32] = this.jj_gen;
                            break block14;
                        }
                    }
                    this.jj_consume_token(46);
                    this.OrderSpec();
                }
            }
            catch (Throwable jjte000) {
                if (jjtc000) {
                    this.jjtree.clearNodeScope(jjtn000);
                    jjtc000 = false;
                } else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw (RuntimeException)jjte000;
                }
                if (jjte000 instanceof ParseException) {
                    throw (ParseException)jjte000;
                }
                throw (Error)jjte000;
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                }
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void OrderSpec() throws ParseException {
        ASTOrderSpec jjtn000 = new ASTOrderSpec(17);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Identifier();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 14: 
                case 16: {
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 14: {
                            this.AscendingOrderSpec();
                            return;
                        }
                        case 16: {
                            this.DescendingOrderSpec();
                            return;
                        }
                    }
                    this.jj_la1[33] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[34] = this.jj_gen;
                    return;
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            } else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (!(jjte000 instanceof ParseException)) throw (Error)jjte000;
            throw (ParseException)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void AscendingOrderSpec() throws ParseException {
        ASTAscendingOrderSpec jjtn000 = new ASTAscendingOrderSpec(18);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(14);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void DescendingOrderSpec() throws ParseException {
        ASTDescendingOrderSpec jjtn000 = new ASTDescendingOrderSpec(19);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(16);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    private static void jj_la1_0() {
        jj_la1_0 = new int[]{0x200000, 0, 246300160, 0, 246300160, 0x800000, 0, 0, 117325312, 0, 112082432, 32768, 0x1000000, 0x4020000, 32768, 67276800, 32768, 117325312, 112082432, 1459502592, 0x500000, 0, 2048, 8192, 1996406272, 1996373504, 112082432, 0, 0, 0, 112082432, 112082432, 0, 81920, 81920};
    }

    private static void jj_la1_1() {
        jj_la1_1 = new int[]{0, 16384, 0x10001000, 65536, 0x10000000, 0, 16384, 65536, 0x10000000, 65536, 0x10000000, 0, 0, 0, 0, 0x700000, 0, 0x10000000, 0x10010000, 0x10000000, 0, 0x700000, 0, 0, 0x10000400, 0x10000400, 0x10011000, 0, 0, 1024, 0, 0x10000000, 16384, 0, 0};
    }

    private static void jj_la1_2() {
        jj_la1_2 = new int[]{0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 136193, 0, 0, 0, 0, 224, 0, 1, 1, 136193, 0, 224, 0, 0, 136193, 136193, 1, 136192, 0, 0, 0, 1, 0, 0, 0};
    }

    private static void jj_la1_3() {
        jj_la1_3 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 4, 4, 0, 4, 4, 0, 0, 0, 0, 0, 0};
    }

    public JCRSQLParser(InputStream stream) {
        this(stream, null);
    }

    public JCRSQLParser(InputStream stream, String encoding) {
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new JCRSQLParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public JCRSQLParser(Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new JCRSQLParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public JCRSQLParser(JCRSQLParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(JCRSQLParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 35; ++i) {
            this.jj_la1[i] = -1;
        }
    }

    private final Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private final int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.removeAllElements();
        boolean[] la1tokens = new boolean[101];
        for (i = 0; i < 101; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 35; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) != 0) {
                    la1tokens[64 + j] = true;
                }
                if ((jj_la1_3[i] & 1 << j) == 0) continue;
                la1tokens[96 + j] = true;
            }
        }
        for (i = 0; i < 101; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.addElement(this.jj_expentry);
        }
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = (int[])this.jj_expentries.elementAt(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    static {
        JCRSQLParser.jj_la1_0();
        JCRSQLParser.jj_la1_1();
        JCRSQLParser.jj_la1_2();
        JCRSQLParser.jj_la1_3();
    }
}

