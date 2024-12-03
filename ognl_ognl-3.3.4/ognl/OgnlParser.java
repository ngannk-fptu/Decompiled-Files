/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import ognl.ASTAdd;
import ognl.ASTAnd;
import ognl.ASTAssign;
import ognl.ASTBitAnd;
import ognl.ASTBitNegate;
import ognl.ASTBitOr;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTCtor;
import ognl.ASTDivide;
import ognl.ASTEq;
import ognl.ASTEval;
import ognl.ASTGreater;
import ognl.ASTGreaterEq;
import ognl.ASTIn;
import ognl.ASTInstanceof;
import ognl.ASTKeyValue;
import ognl.ASTLess;
import ognl.ASTLessEq;
import ognl.ASTList;
import ognl.ASTMap;
import ognl.ASTMethod;
import ognl.ASTMultiply;
import ognl.ASTNegate;
import ognl.ASTNot;
import ognl.ASTNotEq;
import ognl.ASTNotIn;
import ognl.ASTOr;
import ognl.ASTProject;
import ognl.ASTProperty;
import ognl.ASTRemainder;
import ognl.ASTRootVarRef;
import ognl.ASTSelect;
import ognl.ASTSelectFirst;
import ognl.ASTSelectLast;
import ognl.ASTSequence;
import ognl.ASTShiftLeft;
import ognl.ASTShiftRight;
import ognl.ASTStaticField;
import ognl.ASTStaticMethod;
import ognl.ASTSubtract;
import ognl.ASTTest;
import ognl.ASTThisVarRef;
import ognl.ASTUnsignedShiftRight;
import ognl.ASTVarRef;
import ognl.ASTXor;
import ognl.JJTOgnlParserState;
import ognl.JavaCharStream;
import ognl.Node;
import ognl.OgnlParserConstants;
import ognl.OgnlParserTokenManager;
import ognl.OgnlParserTreeConstants;
import ognl.ParseException;
import ognl.Token;

public class OgnlParser
implements OgnlParserTreeConstants,
OgnlParserConstants {
    protected JJTOgnlParserState jjtree = new JJTOgnlParserState();
    public OgnlParserTokenManager token_source;
    JavaCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private boolean jj_lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    private final int[] jj_la1 = new int[64];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private final JJCalls[] jj_2_rtns = new JJCalls[16];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List jj_expentries = new ArrayList();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public final Node topLevelExpression() throws ParseException {
        this.expression();
        this.jj_consume_token(0);
        return this.jjtree.rootNode();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void expression() throws ParseException {
        this.assignmentExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 1: {
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(1);
            ASTSequence jjtn001 = new ASTSequence(1);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.assignmentExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    public final void assignmentExpression() throws ParseException {
        this.conditionalTestExpression();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 2: {
                this.jj_consume_token(2);
                ASTAssign jjtn001 = new ASTAssign(2);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.assignmentExpression();
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
                        this.jjtree.closeNodeScope((Node)jjtn001, 2);
                    }
                }
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
    }

    public final void conditionalTestExpression() throws ParseException {
        this.logicalOrExpression();
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 3: {
                this.jj_consume_token(3);
                this.conditionalTestExpression();
                this.jj_consume_token(4);
                ASTTest jjtn001 = new ASTTest(3);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.conditionalTestExpression();
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
                        this.jjtree.closeNodeScope((Node)jjtn001, 3);
                    }
                }
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void logicalOrExpression() throws ParseException {
        this.logicalAndExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: 
                case 6: {
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 5: {
                    this.jj_consume_token(5);
                    break;
                }
                case 6: {
                    this.jj_consume_token(6);
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            ASTOr jjtn001 = new ASTOr(4);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.logicalAndExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void logicalAndExpression() throws ParseException {
        this.inclusiveOrExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: 
                case 8: {
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 7: {
                    this.jj_consume_token(7);
                    break;
                }
                case 8: {
                    this.jj_consume_token(8);
                    break;
                }
                default: {
                    this.jj_la1[6] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            ASTAnd jjtn001 = new ASTAnd(5);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.inclusiveOrExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void inclusiveOrExpression() throws ParseException {
        this.exclusiveOrExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: 
                case 10: {
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    break;
                }
                case 10: {
                    this.jj_consume_token(10);
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            ASTBitOr jjtn001 = new ASTBitOr(6);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.exclusiveOrExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void exclusiveOrExpression() throws ParseException {
        this.andExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 11: 
                case 12: {
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 11: {
                    this.jj_consume_token(11);
                    break;
                }
                case 12: {
                    this.jj_consume_token(12);
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            ASTXor jjtn001 = new ASTXor(7);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.andExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void andExpression() throws ParseException {
        this.equalityExpression();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: 
                case 14: {
                    break;
                }
                default: {
                    this.jj_la1[11] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: {
                    this.jj_consume_token(13);
                    break;
                }
                case 14: {
                    this.jj_consume_token(14);
                    break;
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            ASTBitAnd jjtn001 = new ASTBitAnd(8);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.equalityExpression();
                continue;
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
                if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
                throw (ParseException)jjte001;
            }
            finally {
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 2);
                continue;
            }
            break;
        }
    }

    public final void equalityExpression() throws ParseException {
        block34: {
            this.relationalExpression();
            block25: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 15: 
                    case 16: 
                    case 17: 
                    case 18: {
                        break;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                        break block34;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 15: 
                    case 16: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 15: {
                                this.jj_consume_token(15);
                                break;
                            }
                            case 16: {
                                this.jj_consume_token(16);
                                break;
                            }
                            default: {
                                this.jj_la1[14] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTEq jjtn001 = new ASTEq(9);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.relationalExpression();
                            continue block25;
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
                            if (!jjtc001) continue block25;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block25;
                        }
                    }
                    case 17: 
                    case 18: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 17: {
                                this.jj_consume_token(17);
                                break;
                            }
                            case 18: {
                                this.jj_consume_token(18);
                                break;
                            }
                            default: {
                                this.jj_la1[15] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTNotEq jjtn002 = new ASTNotEq(10);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.relationalExpression();
                            continue block25;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block25;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block25;
                        }
                    }
                }
                break;
            }
            this.jj_la1[16] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void relationalExpression() throws ParseException {
        block82: {
            this.shiftExpression();
            block57: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 19: 
                    case 20: 
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: {
                        break;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                        break block82;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 19: 
                    case 20: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 19: {
                                this.jj_consume_token(19);
                                break;
                            }
                            case 20: {
                                this.jj_consume_token(20);
                                break;
                            }
                            default: {
                                this.jj_la1[18] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTLess jjtn001 = new ASTLess(11);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.shiftExpression();
                            continue block57;
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
                            if (!jjtc001) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block57;
                        }
                    }
                    case 21: 
                    case 22: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 21: {
                                this.jj_consume_token(21);
                                break;
                            }
                            case 22: {
                                this.jj_consume_token(22);
                                break;
                            }
                            default: {
                                this.jj_la1[19] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTGreater jjtn002 = new ASTGreater(12);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.shiftExpression();
                            continue block57;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block57;
                        }
                    }
                    case 23: 
                    case 24: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 23: {
                                this.jj_consume_token(23);
                                break;
                            }
                            case 24: {
                                this.jj_consume_token(24);
                                break;
                            }
                            default: {
                                this.jj_la1[20] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTLessEq jjtn003 = new ASTLessEq(13);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.shiftExpression();
                            continue block57;
                        }
                        catch (Throwable jjte003) {
                            if (jjtc003) {
                                this.jjtree.clearNodeScope(jjtn003);
                                jjtc003 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte003 instanceof RuntimeException) {
                                throw (RuntimeException)jjte003;
                            }
                            if (jjte003 instanceof ParseException) {
                                throw (ParseException)jjte003;
                            }
                            throw (Error)jjte003;
                        }
                        finally {
                            if (!jjtc003) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block57;
                        }
                    }
                    case 25: 
                    case 26: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 25: {
                                this.jj_consume_token(25);
                                break;
                            }
                            case 26: {
                                this.jj_consume_token(26);
                                break;
                            }
                            default: {
                                this.jj_la1[21] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTGreaterEq jjtn004 = new ASTGreaterEq(14);
                        boolean jjtc004 = true;
                        this.jjtree.openNodeScope(jjtn004);
                        try {
                            this.shiftExpression();
                            continue block57;
                        }
                        catch (Throwable jjte004) {
                            if (jjtc004) {
                                this.jjtree.clearNodeScope(jjtn004);
                                jjtc004 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte004 instanceof RuntimeException) {
                                throw (RuntimeException)jjte004;
                            }
                            if (jjte004 instanceof ParseException) {
                                throw (ParseException)jjte004;
                            }
                            throw (Error)jjte004;
                        }
                        finally {
                            if (!jjtc004) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn004, 2);
                            continue block57;
                        }
                    }
                    case 27: {
                        this.jj_consume_token(27);
                        ASTIn jjtn005 = new ASTIn(15);
                        boolean jjtc005 = true;
                        this.jjtree.openNodeScope(jjtn005);
                        try {
                            this.shiftExpression();
                            continue block57;
                        }
                        catch (Throwable jjte005) {
                            if (jjtc005) {
                                this.jjtree.clearNodeScope(jjtn005);
                                jjtc005 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte005 instanceof RuntimeException) {
                                throw (RuntimeException)jjte005;
                            }
                            if (jjte005 instanceof ParseException) {
                                throw (ParseException)jjte005;
                            }
                            throw (Error)jjte005;
                        }
                        finally {
                            if (!jjtc005) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn005, 2);
                            continue block57;
                        }
                    }
                    case 28: {
                        this.jj_consume_token(28);
                        this.jj_consume_token(27);
                        ASTNotIn jjtn006 = new ASTNotIn(16);
                        boolean jjtc006 = true;
                        this.jjtree.openNodeScope(jjtn006);
                        try {
                            this.shiftExpression();
                            continue block57;
                        }
                        catch (Throwable jjte006) {
                            if (jjtc006) {
                                this.jjtree.clearNodeScope(jjtn006);
                                jjtc006 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte006 instanceof RuntimeException) {
                                throw (RuntimeException)jjte006;
                            }
                            if (jjte006 instanceof ParseException) {
                                throw (ParseException)jjte006;
                            }
                            throw (Error)jjte006;
                        }
                        finally {
                            if (!jjtc006) continue block57;
                            this.jjtree.closeNodeScope((Node)jjtn006, 2);
                            continue block57;
                        }
                    }
                }
                break;
            }
            this.jj_la1[22] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void shiftExpression() throws ParseException {
        block48: {
            this.additiveExpression();
            block35: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 29: 
                    case 30: 
                    case 31: 
                    case 32: 
                    case 33: 
                    case 34: {
                        break;
                    }
                    default: {
                        this.jj_la1[23] = this.jj_gen;
                        break block48;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 29: 
                    case 30: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 29: {
                                this.jj_consume_token(29);
                                break;
                            }
                            case 30: {
                                this.jj_consume_token(30);
                                break;
                            }
                            default: {
                                this.jj_la1[24] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTShiftLeft jjtn001 = new ASTShiftLeft(17);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.additiveExpression();
                            continue block35;
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
                            if (!jjtc001) continue block35;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block35;
                        }
                    }
                    case 31: 
                    case 32: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 31: {
                                this.jj_consume_token(31);
                                break;
                            }
                            case 32: {
                                this.jj_consume_token(32);
                                break;
                            }
                            default: {
                                this.jj_la1[25] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTShiftRight jjtn002 = new ASTShiftRight(18);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.additiveExpression();
                            continue block35;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block35;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block35;
                        }
                    }
                    case 33: 
                    case 34: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 33: {
                                this.jj_consume_token(33);
                                break;
                            }
                            case 34: {
                                this.jj_consume_token(34);
                                break;
                            }
                            default: {
                                this.jj_la1[26] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        ASTUnsignedShiftRight jjtn003 = new ASTUnsignedShiftRight(19);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.additiveExpression();
                            continue block35;
                        }
                        catch (Throwable jjte003) {
                            if (jjtc003) {
                                this.jjtree.clearNodeScope(jjtn003);
                                jjtc003 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte003 instanceof RuntimeException) {
                                throw (RuntimeException)jjte003;
                            }
                            if (jjte003 instanceof ParseException) {
                                throw (ParseException)jjte003;
                            }
                            throw (Error)jjte003;
                        }
                        finally {
                            if (!jjtc003) continue block35;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block35;
                        }
                    }
                }
                break;
            }
            this.jj_la1[27] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void additiveExpression() throws ParseException {
        block26: {
            this.multiplicativeExpression();
            block17: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 35: 
                    case 36: {
                        break;
                    }
                    default: {
                        this.jj_la1[28] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 35: {
                        this.jj_consume_token(35);
                        ASTAdd jjtn001 = new ASTAdd(20);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.multiplicativeExpression();
                            continue block17;
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
                            if (!jjtc001) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block17;
                        }
                    }
                    case 36: {
                        this.jj_consume_token(36);
                        ASTSubtract jjtn002 = new ASTSubtract(21);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.multiplicativeExpression();
                            continue block17;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block17;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block17;
                        }
                    }
                }
                break;
            }
            this.jj_la1[29] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void multiplicativeExpression() throws ParseException {
        block36: {
            this.unaryExpression();
            block23: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 37: 
                    case 38: 
                    case 39: {
                        break;
                    }
                    default: {
                        this.jj_la1[30] = this.jj_gen;
                        break block36;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 37: {
                        this.jj_consume_token(37);
                        ASTMultiply jjtn001 = new ASTMultiply(22);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.unaryExpression();
                            continue block23;
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
                            if (!jjtc001) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block23;
                        }
                    }
                    case 38: {
                        this.jj_consume_token(38);
                        ASTDivide jjtn002 = new ASTDivide(23);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.unaryExpression();
                            continue block23;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block23;
                        }
                    }
                    case 39: {
                        this.jj_consume_token(39);
                        ASTRemainder jjtn003 = new ASTRemainder(24);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.unaryExpression();
                            continue block23;
                        }
                        catch (Throwable jjte003) {
                            if (jjtc003) {
                                this.jjtree.clearNodeScope(jjtn003);
                                jjtc003 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte003 instanceof RuntimeException) {
                                throw (RuntimeException)jjte003;
                            }
                            if (jjte003 instanceof ParseException) {
                                throw (ParseException)jjte003;
                            }
                            throw (Error)jjte003;
                        }
                        finally {
                            if (!jjtc003) continue block23;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block23;
                        }
                    }
                }
                break;
            }
            this.jj_la1[31] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void unaryExpression() throws ParseException {
        block7 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 36: {
                this.jj_consume_token(36);
                ASTNegate jjtn001 = new ASTNegate(25);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.unaryExpression();
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
                        this.jjtree.closeNodeScope((Node)jjtn001, 1);
                    }
                }
            }
            case 35: {
                this.jj_consume_token(35);
                this.unaryExpression();
                break;
            }
            case 40: {
                this.jj_consume_token(40);
                ASTBitNegate jjtn002 = new ASTBitNegate(26);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.unaryExpression();
                    break;
                }
                catch (Throwable jjte002) {
                    if (jjtc002) {
                        this.jjtree.clearNodeScope(jjtn002);
                        jjtc002 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte002 instanceof RuntimeException) {
                        throw (RuntimeException)jjte002;
                    }
                    if (jjte002 instanceof ParseException) {
                        throw (ParseException)jjte002;
                    }
                    throw (Error)jjte002;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, 1);
                    }
                }
            }
            case 28: 
            case 41: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 41: {
                        this.jj_consume_token(41);
                        break;
                    }
                    case 28: {
                        this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[32] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                ASTNot jjtn003 = new ASTNot(27);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.unaryExpression();
                    break;
                }
                catch (Throwable jjte003) {
                    if (jjtc003) {
                        this.jjtree.clearNodeScope(jjtn003);
                        jjtc003 = false;
                    } else {
                        this.jjtree.popNode();
                    }
                    if (jjte003 instanceof RuntimeException) {
                        throw (RuntimeException)jjte003;
                    }
                    if (jjte003 instanceof ParseException) {
                        throw (ParseException)jjte003;
                    }
                    throw (Error)jjte003;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, 1);
                    }
                }
            }
            case 4: 
            case 44: 
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 54: 
            case 56: 
            case 57: 
            case 64: 
            case 67: 
            case 73: 
            case 76: 
            case 79: 
            case 80: 
            case 81: {
                this.navigationChain();
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 42: {
                        ASTInstanceof ionode;
                        StringBuffer sb;
                        this.jj_consume_token(42);
                        Token t = this.jj_consume_token(64);
                        ASTInstanceof jjtn004 = new ASTInstanceof(28);
                        boolean jjtc004 = true;
                        this.jjtree.openNodeScope(jjtn004);
                        try {
                            this.jjtree.closeNodeScope((Node)jjtn004, 1);
                            jjtc004 = false;
                            sb = new StringBuffer(t.image);
                            ionode = jjtn004;
                        }
                        finally {
                            if (jjtc004) {
                                this.jjtree.closeNodeScope((Node)jjtn004, 1);
                            }
                        }
                        block35: while (true) {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 43: {
                                    break;
                                }
                                default: {
                                    this.jj_la1[33] = this.jj_gen;
                                    break block35;
                                }
                            }
                            this.jj_consume_token(43);
                            t = this.jj_consume_token(64);
                            sb.append('.').append(t.image);
                        }
                        ionode.setTargetType(new String(sb));
                        break block7;
                    }
                }
                this.jj_la1[34] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[35] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public final void navigationChain() throws ParseException {
        block43: {
            this.primaryExpression();
            block32: while (true) lbl-1000:
            // 13 sources

            {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 43: 
                    case 44: 
                    case 52: 
                    case 67: {
                        break;
                    }
                    default: {
                        this.jj_la1[36] = this.jj_gen;
                        break block43;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 43: {
                        this.jj_consume_token(43);
                        jjtn001 = new ASTChain(29);
                        jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 64: {
                                    if (this.jj_2_1(2)) {
                                        this.methodCall();
                                        ** break;
                                    }
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 64: {
                                            this.propertyName();
                                            ** break;
                                        }
                                    }
                                    this.jj_la1[37] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                                case 54: {
                                    if (this.jj_2_2(2)) {
                                        this.projection();
                                        ** break;
                                    }
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 54: {
                                            this.selection();
                                            ** break;
                                        }
                                    }
                                    this.jj_la1[38] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                                case 44: {
                                    this.jj_consume_token(44);
                                    this.expression();
                                    this.jj_consume_token(45);
                                    ** break;
                                }
                                default: {
                                    this.jj_la1[39] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
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
                            if (!jjtc001) continue block32;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block32;
                        }
                    }
                    case 52: 
                    case 67: {
                        jjtn002 = new ASTChain(29);
                        jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.index();
                            continue block32;
                        }
                        catch (Throwable jjte002) {
                            if (jjtc002) {
                                this.jjtree.clearNodeScope(jjtn002);
                                jjtc002 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte002 instanceof RuntimeException) {
                                throw (RuntimeException)jjte002;
                            }
                            if (jjte002 instanceof ParseException) {
                                throw (ParseException)jjte002;
                            }
                            throw (Error)jjte002;
                        }
                        finally {
                            if (!jjtc002) continue block32;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block32;
                        }
                    }
                    case 44: {
                        this.jj_consume_token(44);
                        this.expression();
                        jjtn003 = new ASTEval(30);
                        jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.jj_consume_token(45);
                            continue block32;
                        }
                        finally {
                            if (!jjtc003) continue block32;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block32;
                        }
                    }
                }
                break;
            }
            this.jj_la1[40] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    public final void primaryExpression() throws ParseException {
        className = null;
        block12 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 73: 
            case 76: 
            case 79: 
            case 80: 
            case 81: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 73: {
                        this.jj_consume_token(73);
                        break;
                    }
                    case 76: {
                        this.jj_consume_token(76);
                        break;
                    }
                    case 79: {
                        this.jj_consume_token(79);
                        break;
                    }
                    case 80: {
                        this.jj_consume_token(80);
                        break;
                    }
                    case 81: {
                        this.jj_consume_token(81);
                        break;
                    }
                    default: {
                        this.jj_la1[41] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                jjtn001 = new ASTConst(31);
                jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn001, 0);
                    jjtc001 = false;
                    jjtn001.setValue(this.token_source.literalValue);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, 0);
                    }
                }
            }
            case 46: {
                this.jj_consume_token(46);
                jjtn002 = new ASTConst(31);
                jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn002, 0);
                    jjtc002 = false;
                    jjtn002.setValue(Boolean.TRUE);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, 0);
                    }
                }
            }
            case 47: {
                this.jj_consume_token(47);
                jjtn003 = new ASTConst(31);
                jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.jjtree.closeNodeScope((Node)jjtn003, 0);
                    jjtc003 = false;
                    jjtn003.setValue(Boolean.FALSE);
                    break;
                }
                finally {
                    if (jjtc003) {
                        this.jjtree.closeNodeScope((Node)jjtn003, 0);
                    }
                }
            }
            case 48: {
                jjtn004 = new ASTConst(31);
                jjtc004 = true;
                this.jjtree.openNodeScope(jjtn004);
                try {
                    this.jj_consume_token(48);
                    break;
                }
                finally {
                    if (jjtc004) {
                        this.jjtree.closeNodeScope((Node)jjtn004, 0);
                    }
                }
            }
            default: {
                this.jj_la1[48] = this.jj_gen;
                if (this.jj_2_4(2)) {
                    this.jj_consume_token(49);
                    jjtn005 = new ASTThisVarRef(32);
                    jjtc005 = true;
                    this.jjtree.openNodeScope(jjtn005);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn005, 0);
                        jjtc005 = false;
                        jjtn005.setName("this");
                        break;
                    }
                    finally {
                        if (jjtc005) {
                            this.jjtree.closeNodeScope((Node)jjtn005, 0);
                        }
                    }
                }
                if (this.jj_2_5(2)) {
                    this.jj_consume_token(50);
                    jjtn006 = new ASTRootVarRef(33);
                    jjtc006 = true;
                    this.jjtree.openNodeScope(jjtn006);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn006, 0);
                        jjtc006 = false;
                        jjtn006.setName("root");
                        break;
                    }
                    finally {
                        if (jjtc006) {
                            this.jjtree.closeNodeScope((Node)jjtn006, 0);
                        }
                    }
                }
                if (this.jj_2_6(2)) {
                    this.jj_consume_token(51);
                    t = this.jj_consume_token(64);
                    jjtn007 = new ASTVarRef(34);
                    jjtc007 = true;
                    this.jjtree.openNodeScope(jjtn007);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn007, 0);
                        jjtc007 = false;
                        jjtn007.setName(t.image);
                        break;
                    }
                    finally {
                        if (jjtc007) {
                            this.jjtree.closeNodeScope((Node)jjtn007, 0);
                        }
                    }
                }
                if (this.jj_2_7(2)) {
                    this.jj_consume_token(4);
                    this.jj_consume_token(52);
                    this.expression();
                    this.jj_consume_token(53);
                    jjtn008 = new ASTConst(31);
                    jjtc008 = true;
                    this.jjtree.openNodeScope(jjtn008);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn008, 1);
                        jjtc008 = false;
                        jjtn008.setValue(jjtn008.jjtGetChild(0));
                        break;
                    }
                    finally {
                        if (jjtc008) {
                            this.jjtree.closeNodeScope((Node)jjtn008, 1);
                        }
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 56: {
                        this.staticReference();
                        break block12;
                    }
                }
                this.jj_la1[49] = this.jj_gen;
                if (this.jj_2_8(2)) {
                    this.constructorCall();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 64: {
                        if (this.jj_2_3(2)) {
                            this.methodCall();
                            break block12;
                        }
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 64: {
                                this.propertyName();
                                break block12;
                            }
                        }
                        this.jj_la1[42] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                    case 52: 
                    case 67: {
                        this.index();
                        break block12;
                    }
                    case 44: {
                        this.jj_consume_token(44);
                        this.expression();
                        this.jj_consume_token(45);
                        break block12;
                    }
                    case 54: {
                        this.jj_consume_token(54);
                        jjtn009 = new ASTList(35);
                        jjtc009 = true;
                        this.jjtree.openNodeScope(jjtn009);
                        try {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 4: 
                                case 28: 
                                case 35: 
                                case 36: 
                                case 40: 
                                case 41: 
                                case 44: 
                                case 46: 
                                case 47: 
                                case 48: 
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 54: 
                                case 56: 
                                case 57: 
                                case 64: 
                                case 67: 
                                case 73: 
                                case 76: 
                                case 79: 
                                case 80: 
                                case 81: {
                                    this.assignmentExpression();
                                    while (true) {
                                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                            case 1: {
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[43] = this.jj_gen;
                                                ** break;
lbl200:
                                                // 1 sources

                                                ** GOTO lbl224
                                            }
                                        }
                                        this.jj_consume_token(1);
                                        this.assignmentExpression();
                                    }
                                }
                                default: {
                                    this.jj_la1[44] = this.jj_gen;
                                    break;
                                }
                            }
                        }
                        catch (Throwable jjte009) {
                            if (jjtc009) {
                                this.jjtree.clearNodeScope(jjtn009);
                                jjtc009 = false;
                            } else {
                                this.jjtree.popNode();
                            }
                            if (jjte009 instanceof RuntimeException) {
                                throw (RuntimeException)jjte009;
                            }
                            if (jjte009 instanceof ParseException) {
                                throw (ParseException)jjte009;
                            }
                            throw (Error)jjte009;
                        }
                        finally {
                            if (jjtc009) {
                                this.jjtree.closeNodeScope((Node)jjtn009, true);
                            }
                        }
lbl224:
                        // 2 sources

                        this.jj_consume_token(55);
                        break block12;
                    }
                }
                this.jj_la1[50] = this.jj_gen;
                if (this.jj_2_9(2)) {
                    jjtn010 = new ASTMap(36);
                    jjtc010 = true;
                    this.jjtree.openNodeScope(jjtn010);
                    try {
                        this.jj_consume_token(51);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 56: {
                                className = this.classReference();
                                break;
                            }
                            default: {
                                this.jj_la1[45] = this.jj_gen;
                            }
                        }
                        this.jj_consume_token(54);
                        block46 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 4: 
                            case 28: 
                            case 35: 
                            case 36: 
                            case 40: 
                            case 41: 
                            case 44: 
                            case 46: 
                            case 47: 
                            case 48: 
                            case 49: 
                            case 50: 
                            case 51: 
                            case 52: 
                            case 54: 
                            case 56: 
                            case 57: 
                            case 64: 
                            case 67: 
                            case 73: 
                            case 76: 
                            case 79: 
                            case 80: 
                            case 81: {
                                this.keyValueExpression();
                                while (true) {
                                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                        case 1: {
                                            break;
                                        }
                                        default: {
                                            this.jj_la1[46] = this.jj_gen;
                                            break block46;
                                        }
                                    }
                                    this.jj_consume_token(1);
                                    this.keyValueExpression();
                                }
                            }
                            default: {
                                this.jj_la1[47] = this.jj_gen;
                            }
                        }
                        jjtn010.setClassName(className);
                        this.jj_consume_token(55);
                        break;
                    }
                    catch (Throwable jjte010) {
                        if (jjtc010) {
                            this.jjtree.clearNodeScope(jjtn010);
                            jjtc010 = false;
                        } else {
                            this.jjtree.popNode();
                        }
                        if (jjte010 instanceof RuntimeException) {
                            throw (RuntimeException)jjte010;
                        }
                        if (jjte010 instanceof ParseException) {
                            throw (ParseException)jjte010;
                        }
                        throw (Error)jjte010;
                    }
                    finally {
                        if (jjtc010) {
                            this.jjtree.closeNodeScope((Node)jjtn010, true);
                        }
                    }
                }
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void keyValueExpression() throws ParseException {
        ASTKeyValue jjtn001 = new ASTKeyValue(37);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.assignmentExpression();
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: {
                    this.jj_consume_token(4);
                    this.assignmentExpression();
                    return;
                }
                default: {
                    this.jj_la1[51] = this.jj_gen;
                    return;
                }
            }
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
            if (!(jjte001 instanceof ParseException)) throw (Error)jjte001;
            throw (ParseException)jjte001;
        }
        finally {
            if (jjtc001) {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void staticReference() throws ParseException {
        String className = "java.lang.Math";
        className = this.classReference();
        if (this.jj_2_10(2)) {
            this.staticMethodCall(className);
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 64: {
                    Token t = this.jj_consume_token(64);
                    ASTStaticField jjtn001 = new ASTStaticField(38);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, 0);
                        jjtc001 = false;
                        jjtn001.init(className, t.image);
                        break;
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, 0);
                        }
                    }
                }
                default: {
                    this.jj_la1[52] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    public final String classReference() throws ParseException {
        String result = "java.lang.Math";
        this.jj_consume_token(56);
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 64: {
                result = this.className();
                break;
            }
            default: {
                this.jj_la1[53] = this.jj_gen;
            }
        }
        this.jj_consume_token(56);
        return result;
    }

    public final String className() throws ParseException {
        Token t = this.jj_consume_token(64);
        StringBuffer result = new StringBuffer(t.image);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 43: {
                    break;
                }
                default: {
                    this.jj_la1[54] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(43);
            t = this.jj_consume_token(64);
            result.append('.').append(t.image);
        }
        return new String(result);
    }

    /*
     * Unable to fully structure code
     */
    public final void constructorCall() throws ParseException {
        block37: {
            jjtn000 = new ASTCtor(39);
            jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(57);
                className = this.className();
                if (this.jj_2_11(2)) {
                    this.jj_consume_token(44);
                    block4 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 4: 
                        case 28: 
                        case 35: 
                        case 36: 
                        case 40: 
                        case 41: 
                        case 44: 
                        case 46: 
                        case 47: 
                        case 48: 
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 54: 
                        case 56: 
                        case 57: 
                        case 64: 
                        case 67: 
                        case 73: 
                        case 76: 
                        case 79: 
                        case 80: 
                        case 81: {
                            this.assignmentExpression();
                            while (true) {
                                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                    case 1: {
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[55] = this.jj_gen;
                                        break block4;
                                    }
                                }
                                this.jj_consume_token(1);
                                this.assignmentExpression();
                            }
                        }
                        default: {
                            this.jj_la1[56] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(45);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setClassName(className);
                    break block37;
                }
                if (this.jj_2_12(2)) {
                    block38: {
                        this.jj_consume_token(52);
                        this.jj_consume_token(53);
                        this.jj_consume_token(54);
                        jjtn001 = new ASTList(35);
                        jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 4: 
                                case 28: 
                                case 35: 
                                case 36: 
                                case 40: 
                                case 41: 
                                case 44: 
                                case 46: 
                                case 47: 
                                case 48: 
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 54: 
                                case 56: 
                                case 57: 
                                case 64: 
                                case 67: 
                                case 73: 
                                case 76: 
                                case 79: 
                                case 80: 
                                case 81: {
                                    this.assignmentExpression();
                                    while (true) {
                                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                            case 1: {
                                                break;
                                            }
                                            default: {
                                                this.jj_la1[57] = this.jj_gen;
                                                ** break;
lbl54:
                                                // 1 sources

                                                break block38;
                                            }
                                        }
                                        this.jj_consume_token(1);
                                        this.assignmentExpression();
                                    }
                                }
                                default: {
                                    this.jj_la1[58] = this.jj_gen;
                                    break;
                                }
                            }
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
                    this.jj_consume_token(55);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setClassName(className);
                    jjtn000.setArray(true);
                    break block37;
                }
                if (this.jj_2_13(2)) {
                    this.jj_consume_token(52);
                    this.assignmentExpression();
                    this.jj_consume_token(53);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setClassName(className);
                    jjtn000.setArray(true);
                    break block37;
                }
                this.jj_consume_token(-1);
                throw new ParseException();
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
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void propertyName() throws ParseException {
        ASTProperty jjtn000 = new ASTProperty(40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = this.jj_consume_token(64);
            ASTConst jjtn001 = new ASTConst(31);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.jjtree.closeNodeScope((Node)jjtn001, true);
                jjtc001 = false;
                jjtn001.setValue(t.image);
            }
            finally {
                if (jjtc001) {
                    this.jjtree.closeNodeScope((Node)jjtn001, true);
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void staticMethodCall(String className) throws ParseException {
        ASTStaticMethod jjtn000 = new ASTStaticMethod(41);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = this.jj_consume_token(64);
            this.jj_consume_token(44);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 28: 
                case 35: 
                case 36: 
                case 40: 
                case 41: 
                case 44: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 54: 
                case 56: 
                case 57: 
                case 64: 
                case 67: 
                case 73: 
                case 76: 
                case 79: 
                case 80: 
                case 81: {
                    this.assignmentExpression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: {
                                break;
                            }
                            default: {
                                this.jj_la1[59] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(1);
                        this.assignmentExpression();
                    }
                }
                default: {
                    this.jj_la1[60] = this.jj_gen;
                }
            }
            this.jj_consume_token(45);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.init(className, t.image);
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

    public final void methodCall() throws ParseException {
        ASTMethod jjtn000 = new ASTMethod(42);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            Token t = this.jj_consume_token(64);
            this.jj_consume_token(44);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 4: 
                case 28: 
                case 35: 
                case 36: 
                case 40: 
                case 41: 
                case 44: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 54: 
                case 56: 
                case 57: 
                case 64: 
                case 67: 
                case 73: 
                case 76: 
                case 79: 
                case 80: 
                case 81: {
                    this.assignmentExpression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 1: {
                                break;
                            }
                            default: {
                                this.jj_la1[61] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(1);
                        this.assignmentExpression();
                    }
                }
                default: {
                    this.jj_la1[62] = this.jj_gen;
                }
            }
            this.jj_consume_token(45);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setMethodName(t.image);
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

    public final void projection() throws ParseException {
        ASTProject jjtn000 = new ASTProject(43);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            this.expression();
            this.jj_consume_token(55);
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

    public final void selection() throws ParseException {
        if (this.jj_2_14(2)) {
            this.selectAll();
        } else if (this.jj_2_15(2)) {
            this.selectFirst();
        } else if (this.jj_2_16(2)) {
            this.selectLast();
        } else {
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void selectAll() throws ParseException {
        ASTSelect jjtn000 = new ASTSelect(44);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            this.jj_consume_token(3);
            this.expression();
            this.jj_consume_token(55);
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

    public final void selectFirst() throws ParseException {
        ASTSelectFirst jjtn000 = new ASTSelectFirst(45);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            this.jj_consume_token(11);
            this.expression();
            this.jj_consume_token(55);
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

    public final void selectLast() throws ParseException {
        ASTSelectLast jjtn000 = new ASTSelectLast(46);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            this.jj_consume_token(58);
            this.expression();
            this.jj_consume_token(55);
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
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void index() throws ParseException {
        ASTProperty jjtn000 = new ASTProperty(40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 52: {
                    this.jj_consume_token(52);
                    this.expression();
                    this.jj_consume_token(53);
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setIndexedAccess(true);
                    return;
                }
                case 67: {
                    this.jj_consume_token(67);
                    ASTConst jjtn001 = new ASTConst(31);
                    boolean jjtc001 = true;
                    this.jjtree.openNodeScope(jjtn001);
                    try {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                        jjtc001 = false;
                        jjtn001.setValue(this.token_source.literalValue);
                    }
                    finally {
                        if (jjtc001) {
                            this.jjtree.closeNodeScope((Node)jjtn001, true);
                        }
                    }
                    this.jjtree.closeNodeScope((Node)jjtn000, true);
                    jjtc000 = false;
                    jjtn000.setIndexedAccess(true);
                    return;
                }
                default: {
                    this.jj_la1[63] = this.jj_gen;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_1();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(0, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_2();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(1, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_3();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(2, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_4();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(3, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_5();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(4, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_6();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(5, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_7();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(6, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_8();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(7, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_9(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_9();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(8, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_10(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_10();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(9, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_11(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_11();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(10, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_12(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_12();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(11, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_13(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_13();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(12, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_14(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_14();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(13, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_15(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_15();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(14, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_16(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_16();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(15, xla);
        }
    }

    private boolean jj_3R_56() {
        return this.jj_scan_token(48);
    }

    private boolean jj_3R_55() {
        return this.jj_scan_token(47);
    }

    private boolean jj_3R_54() {
        return this.jj_scan_token(46);
    }

    private boolean jj_3R_31() {
        return this.jj_3R_27();
    }

    private boolean jj_3_13() {
        if (this.jj_scan_token(52)) {
            return true;
        }
        return this.jj_3R_27();
    }

    private boolean jj_3R_53() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(73)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(76)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(79)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(80)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(81)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_26() {
        return this.jj_3R_27();
    }

    private boolean jj_3R_52() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_53()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_54()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_55()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_56()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3_4()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3_5()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3_6()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3_7()) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_3R_57()) {
                                            this.jj_scanpos = xsp;
                                            if (this.jj_3_8()) {
                                                this.jj_scanpos = xsp;
                                                if (this.jj_3R_58()) {
                                                    this.jj_scanpos = xsp;
                                                    if (this.jj_3R_59()) {
                                                        this.jj_scanpos = xsp;
                                                        if (this.jj_3R_60()) {
                                                            this.jj_scanpos = xsp;
                                                            if (this.jj_3R_61()) {
                                                                this.jj_scanpos = xsp;
                                                                if (this.jj_3_9()) {
                                                                    return true;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_42() {
        return this.jj_3R_43();
    }

    private boolean jj_3_12() {
        if (this.jj_scan_token(52)) {
            return true;
        }
        return this.jj_scan_token(53);
    }

    private boolean jj_3_11() {
        if (this.jj_scan_token(44)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_26()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(45);
    }

    private boolean jj_3R_67() {
        return this.jj_scan_token(67);
    }

    private boolean jj_3_2() {
        return this.jj_3R_22();
    }

    private boolean jj_3R_66() {
        return this.jj_scan_token(52);
    }

    private boolean jj_3R_64() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_66()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_67()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_1() {
        return this.jj_3R_21();
    }

    private boolean jj_3R_23() {
        if (this.jj_scan_token(57)) {
            return true;
        }
        return this.jj_3R_32();
    }

    private boolean jj_3R_41() {
        return this.jj_3R_42();
    }

    private boolean jj_3R_30() {
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_scan_token(58);
    }

    private boolean jj_3R_32() {
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_51() {
        return this.jj_3R_52();
    }

    private boolean jj_3R_29() {
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_scan_token(11);
    }

    private boolean jj_3R_40() {
        return this.jj_3R_41();
    }

    private boolean jj_3R_33() {
        return this.jj_scan_token(56);
    }

    private boolean jj_3R_63() {
        return this.jj_3R_65();
    }

    private boolean jj_3R_28() {
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_scan_token(3);
    }

    private boolean jj_3R_50() {
        return this.jj_3R_51();
    }

    private boolean jj_3R_39() {
        return this.jj_3R_40();
    }

    private boolean jj_3_10() {
        return this.jj_3R_25();
    }

    private boolean jj_3R_24() {
        return this.jj_3R_33();
    }

    private boolean jj_3R_49() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(41)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(28)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_48() {
        return this.jj_scan_token(40);
    }

    private boolean jj_3_16() {
        return this.jj_3R_30();
    }

    private boolean jj_3R_47() {
        return this.jj_scan_token(35);
    }

    private boolean jj_3_15() {
        return this.jj_3R_29();
    }

    private boolean jj_3R_38() {
        return this.jj_3R_39();
    }

    private boolean jj_3R_46() {
        return this.jj_scan_token(36);
    }

    private boolean jj_3_14() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_62() {
        return this.jj_3R_33();
    }

    private boolean jj_3R_45() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_46()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_47()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_48()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_49()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_50()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_37() {
        return this.jj_3R_38();
    }

    private boolean jj_3R_22() {
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_3R_31();
    }

    private boolean jj_3_9() {
        if (this.jj_scan_token(51)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_24()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(54);
    }

    private boolean jj_3R_36() {
        return this.jj_3R_37();
    }

    private boolean jj_3R_61() {
        return this.jj_scan_token(54);
    }

    private boolean jj_3R_60() {
        return this.jj_scan_token(44);
    }

    private boolean jj_3R_59() {
        return this.jj_3R_64();
    }

    private boolean jj_3_3() {
        return this.jj_3R_21();
    }

    private boolean jj_3R_21() {
        if (this.jj_scan_token(64)) {
            return true;
        }
        return this.jj_scan_token(44);
    }

    private boolean jj_3R_58() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_3()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_63()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_35() {
        return this.jj_3R_36();
    }

    private boolean jj_3R_44() {
        return this.jj_3R_45();
    }

    private boolean jj_3_8() {
        return this.jj_3R_23();
    }

    private boolean jj_3R_57() {
        return this.jj_3R_62();
    }

    private boolean jj_3R_34() {
        return this.jj_3R_35();
    }

    private boolean jj_3_7() {
        if (this.jj_scan_token(4)) {
            return true;
        }
        return this.jj_scan_token(52);
    }

    private boolean jj_3R_25() {
        if (this.jj_scan_token(64)) {
            return true;
        }
        return this.jj_scan_token(44);
    }

    private boolean jj_3_6() {
        if (this.jj_scan_token(51)) {
            return true;
        }
        return this.jj_scan_token(64);
    }

    private boolean jj_3_5() {
        return this.jj_scan_token(50);
    }

    private boolean jj_3R_27() {
        return this.jj_3R_34();
    }

    private boolean jj_3_4() {
        return this.jj_scan_token(49);
    }

    private boolean jj_3R_65() {
        return this.jj_scan_token(64);
    }

    private boolean jj_3R_43() {
        return this.jj_3R_44();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{2, 4, 8, 96, 96, 384, 384, 1536, 1536, 6144, 6144, 24576, 24576, 491520, 98304, 393216, 491520, 536346624, 0x180000, 0x600000, 0x1800000, 0x6000000, 536346624, -536870912, 0x60000000, Integer.MIN_VALUE, 0, -536870912, 0, 0, 0, 0, 0x10000000, 0, 0, 0x10000010, 0, 0, 0, 0, 0, 0, 0, 2, 0x10000010, 0, 2, 0x10000010, 0, 0, 0, 16, 0, 0, 0, 2, 0x10000010, 2, 0x10000010, 2, 0x10000010, 2, 0x10000010, 0};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 1, 6, 7, 24, 24, 224, 224, 512, 2048, 1024, 56611608, 0x101800, 0, 0x400000, 0x401000, 0x101800, 0, 0, 0, 56611608, 0x1000000, 0, 56611608, 114688, 0x1000000, 0x501000, 0, 0, 0, 2048, 0, 56611608, 0, 56611608, 0, 56611608, 0, 56611608, 0x100000};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 233993, 8, 1, 0, 1, 8, 233984, 1, 0, 233993, 0, 0, 233993, 233984, 0, 9, 0, 1, 1, 0, 0, 233993, 0, 233993, 0, 233993, 0, 233993, 8};
    }

    public OgnlParser(InputStream stream) {
        this(stream, null);
    }

    public OgnlParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new OgnlParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        int i;
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
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public OgnlParser(Reader stream) {
        int i;
        this.jj_input_stream = new JavaCharStream(stream, 1, 1);
        this.token_source = new OgnlParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        int i;
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public OgnlParser(OgnlParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(OgnlParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 64; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    JJCalls c = this.jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                        c = c.next;
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
                this.jj_lastpos = this.jj_scanpos.next;
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;
            while (tok != null && tok != this.jj_scanpos) {
                ++i;
                tok = tok.next;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.jj_lookingAhead ? this.jj_scanpos : this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            block1: for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] != this.jj_expentry[i]) continue block1;
                }
                this.jj_expentries.add(this.jj_expentry);
                break;
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[86];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 64; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) == 0) continue;
                la1tokens[64 + j] = true;
            }
        }
        for (i = 0; i < 86; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = (int[])this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 16; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
                            break;
                        }
                        case 2: {
                            this.jj_3_3();
                            break;
                        }
                        case 3: {
                            this.jj_3_4();
                            break;
                        }
                        case 4: {
                            this.jj_3_5();
                            break;
                        }
                        case 5: {
                            this.jj_3_6();
                            break;
                        }
                        case 6: {
                            this.jj_3_7();
                            break;
                        }
                        case 7: {
                            this.jj_3_8();
                            break;
                        }
                        case 8: {
                            this.jj_3_9();
                            break;
                        }
                        case 9: {
                            this.jj_3_10();
                            break;
                        }
                        case 10: {
                            this.jj_3_11();
                            break;
                        }
                        case 11: {
                            this.jj_3_12();
                            break;
                        }
                        case 12: {
                            this.jj_3_13();
                            break;
                        }
                        case 13: {
                            this.jj_3_14();
                            break;
                        }
                        case 14: {
                            this.jj_3_15();
                            break;
                        }
                        case 15: {
                            this.jj_3_16();
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (LookaheadSuccess lookaheadSuccess) {
                // empty catch block
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];
        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        OgnlParser.jj_la1_init_0();
        OgnlParser.jj_la1_init_1();
        OgnlParser.jj_la1_init_2();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }
    }
}

