/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.el.ELException;
import org.apache.el.parser.AstAnd;
import org.apache.el.parser.AstAssign;
import org.apache.el.parser.AstBracketSuffix;
import org.apache.el.parser.AstChoice;
import org.apache.el.parser.AstCompositeExpression;
import org.apache.el.parser.AstConcatenation;
import org.apache.el.parser.AstDeferredExpression;
import org.apache.el.parser.AstDiv;
import org.apache.el.parser.AstDotSuffix;
import org.apache.el.parser.AstDynamicExpression;
import org.apache.el.parser.AstEmpty;
import org.apache.el.parser.AstEqual;
import org.apache.el.parser.AstFalse;
import org.apache.el.parser.AstFloatingPoint;
import org.apache.el.parser.AstFunction;
import org.apache.el.parser.AstGreaterThan;
import org.apache.el.parser.AstGreaterThanEqual;
import org.apache.el.parser.AstIdentifier;
import org.apache.el.parser.AstInteger;
import org.apache.el.parser.AstLambdaExpression;
import org.apache.el.parser.AstLambdaParameters;
import org.apache.el.parser.AstLessThan;
import org.apache.el.parser.AstLessThanEqual;
import org.apache.el.parser.AstListData;
import org.apache.el.parser.AstLiteralExpression;
import org.apache.el.parser.AstMapData;
import org.apache.el.parser.AstMapEntry;
import org.apache.el.parser.AstMethodParameters;
import org.apache.el.parser.AstMinus;
import org.apache.el.parser.AstMod;
import org.apache.el.parser.AstMult;
import org.apache.el.parser.AstNegative;
import org.apache.el.parser.AstNot;
import org.apache.el.parser.AstNotEqual;
import org.apache.el.parser.AstNull;
import org.apache.el.parser.AstOr;
import org.apache.el.parser.AstPlus;
import org.apache.el.parser.AstSemicolon;
import org.apache.el.parser.AstSetData;
import org.apache.el.parser.AstString;
import org.apache.el.parser.AstTrue;
import org.apache.el.parser.AstValue;
import org.apache.el.parser.ELParserConstants;
import org.apache.el.parser.ELParserTokenManager;
import org.apache.el.parser.ELParserTreeConstants;
import org.apache.el.parser.JJTELParserState;
import org.apache.el.parser.Node;
import org.apache.el.parser.ParseException;
import org.apache.el.parser.SimpleCharStream;
import org.apache.el.parser.Token;

public class ELParser
implements ELParserTreeConstants,
ELParserConstants {
    protected JJTELParserState jjtree = new JJTELParserState();
    public ELParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[52];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns = new JJCalls[8];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private static final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;
    private boolean trace_enabled;

    public static Node parse(String ref) throws ELException {
        try {
            return new ELParser(new StringReader(ref)).CompositeExpression();
        }
        catch (ParseException pe) {
            throw new ELException(pe.getMessage());
        }
    }

    public final AstCompositeExpression CompositeExpression() throws ParseException {
        AstCompositeExpression jjtn000 = new AstCompositeExpression(0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            block19: {
                block13: while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 1: 
                        case 2: 
                        case 3: {
                            break;
                        }
                        default: {
                            this.jj_la1[0] = this.jj_gen;
                            break block19;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 3: {
                            this.DeferredExpression();
                            continue block13;
                        }
                        case 2: {
                            this.DynamicExpression();
                            continue block13;
                        }
                        case 1: {
                            this.LiteralExpression();
                            continue block13;
                        }
                    }
                    break;
                }
                this.jj_la1[1] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            this.jj_consume_token(0);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            AstCompositeExpression astCompositeExpression = jjtn000;
            return astCompositeExpression;
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
     */
    public final void LiteralExpression() throws ParseException {
        AstLiteralExpression jjtn000 = new AstLiteralExpression(1);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            t = this.jj_consume_token(1);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void DeferredExpression() throws ParseException {
        AstDeferredExpression jjtn000 = new AstDeferredExpression(2);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(3);
            this.Expression();
            this.jj_consume_token(9);
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

    public final void DynamicExpression() throws ParseException {
        AstDynamicExpression jjtn000 = new AstDynamicExpression(3);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(2);
            this.Expression();
            this.jj_consume_token(9);
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

    public final void Expression() throws ParseException {
        this.Semicolon();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void Semicolon() throws ParseException {
        this.Assignment();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 23: {
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(23);
            AstSemicolon jjtn001 = new AstSemicolon(5);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.Assignment();
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

    public final void Assignment() throws ParseException {
        if (this.jj_2_2(4)) {
            this.LambdaExpression();
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.Choice();
                    while (this.jj_2_1(2)) {
                        this.jj_consume_token(54);
                        AstAssign jjtn001 = new AstAssign(6);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.Assignment();
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
                            if (!jjtc001) continue;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void LambdaExpression() throws ParseException {
        AstLambdaExpression jjtn000 = new AstLambdaExpression(7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.LambdaParameters();
            this.jj_consume_token(55);
            if (this.jj_2_3(3)) {
                this.LambdaExpression();
                return;
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.Choice();
                    return;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
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
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void LambdaParameters() throws ParseException {
        AstLambdaParameters jjtn000 = new AstLambdaParameters(8);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 56: {
                    this.Identifier();
                    return;
                }
                case 18: {
                    this.jj_consume_token(18);
                    block6 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 56: {
                            this.Identifier();
                            while (true) {
                                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                    case 24: {
                                        break;
                                    }
                                    default: {
                                        this.jj_la1[5] = this.jj_gen;
                                        break block6;
                                    }
                                }
                                this.jj_consume_token(24);
                                this.Identifier();
                            }
                        }
                        default: {
                            this.jj_la1[6] = this.jj_gen;
                        }
                    }
                    this.jj_consume_token(19);
                    return;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
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

    public final void LambdaExpressionOrInvocation() throws ParseException {
        block19: {
            AstLambdaExpression jjtn000 = new AstLambdaExpression(7);
            boolean jjtc000 = true;
            this.jjtree.openNodeScope(jjtn000);
            try {
                this.jj_consume_token(18);
                this.LambdaParameters();
                this.jj_consume_token(55);
                if (this.jj_2_4(3)) {
                    this.LambdaExpression();
                } else {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 8: 
                        case 10: 
                        case 11: 
                        case 13: 
                        case 14: 
                        case 15: 
                        case 16: 
                        case 18: 
                        case 20: 
                        case 37: 
                        case 38: 
                        case 43: 
                        case 47: 
                        case 56: {
                            this.Choice();
                            break;
                        }
                        default: {
                            this.jj_la1[8] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
                this.jj_consume_token(19);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 18: {
                            break;
                        }
                        default: {
                            this.jj_la1[9] = this.jj_gen;
                            break block19;
                        }
                    }
                    this.MethodParameters();
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

    public final void Choice() throws ParseException {
        this.Or();
        while (this.jj_2_5(3)) {
            this.jj_consume_token(48);
            this.Choice();
            this.jj_consume_token(22);
            AstChoice jjtn001 = new AstChoice(9);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.Choice();
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
                if (!jjtc001) continue;
                this.jjtree.closeNodeScope((Node)jjtn001, 3);
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void Or() throws ParseException {
        this.And();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 41: 
                case 42: {
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 41: {
                    this.jj_consume_token(41);
                    break;
                }
                case 42: {
                    this.jj_consume_token(42);
                    break;
                }
                default: {
                    this.jj_la1[11] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            AstOr jjtn001 = new AstOr(10);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.And();
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
    public final void And() throws ParseException {
        this.Equality();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 39: 
                case 40: {
                    break;
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
                    return;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 39: {
                    this.jj_consume_token(39);
                    break;
                }
                case 40: {
                    this.jj_consume_token(40);
                    break;
                }
                default: {
                    this.jj_la1[13] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            AstAnd jjtn001 = new AstAnd(11);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.Equality();
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

    public final void Equality() throws ParseException {
        block34: {
            this.Compare();
            block25: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 33: 
                    case 34: 
                    case 35: 
                    case 36: {
                        break;
                    }
                    default: {
                        this.jj_la1[14] = this.jj_gen;
                        break block34;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 33: 
                    case 34: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 33: {
                                this.jj_consume_token(33);
                                break;
                            }
                            case 34: {
                                this.jj_consume_token(34);
                                break;
                            }
                            default: {
                                this.jj_la1[15] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstEqual jjtn001 = new AstEqual(12);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.Compare();
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
                    case 35: 
                    case 36: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 35: {
                                this.jj_consume_token(35);
                                break;
                            }
                            case 36: {
                                this.jj_consume_token(36);
                                break;
                            }
                            default: {
                                this.jj_la1[16] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstNotEqual jjtn002 = new AstNotEqual(13);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.Compare();
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
            this.jj_la1[17] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void Compare() throws ParseException {
        block62: {
            this.Concatenation();
            block45: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: 
                    case 30: 
                    case 31: 
                    case 32: {
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        break block62;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 27: 
                    case 28: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 27: {
                                this.jj_consume_token(27);
                                break;
                            }
                            case 28: {
                                this.jj_consume_token(28);
                                break;
                            }
                            default: {
                                this.jj_la1[19] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstLessThan jjtn001 = new AstLessThan(14);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.Concatenation();
                            continue block45;
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
                            if (!jjtc001) continue block45;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block45;
                        }
                    }
                    case 25: 
                    case 26: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 25: {
                                this.jj_consume_token(25);
                                break;
                            }
                            case 26: {
                                this.jj_consume_token(26);
                                break;
                            }
                            default: {
                                this.jj_la1[20] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstGreaterThan jjtn002 = new AstGreaterThan(15);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.Concatenation();
                            continue block45;
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
                            if (!jjtc002) continue block45;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block45;
                        }
                    }
                    case 31: 
                    case 32: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 31: {
                                this.jj_consume_token(31);
                                break;
                            }
                            case 32: {
                                this.jj_consume_token(32);
                                break;
                            }
                            default: {
                                this.jj_la1[21] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstLessThanEqual jjtn003 = new AstLessThanEqual(16);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.Concatenation();
                            continue block45;
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
                            if (!jjtc003) continue block45;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block45;
                        }
                    }
                    case 29: 
                    case 30: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 29: {
                                this.jj_consume_token(29);
                                break;
                            }
                            case 30: {
                                this.jj_consume_token(30);
                                break;
                            }
                            default: {
                                this.jj_la1[22] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstGreaterThanEqual jjtn004 = new AstGreaterThanEqual(17);
                        boolean jjtc004 = true;
                        this.jjtree.openNodeScope(jjtn004);
                        try {
                            this.Concatenation();
                            continue block45;
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
                            if (!jjtc004) continue block45;
                            this.jjtree.closeNodeScope((Node)jjtn004, 2);
                            continue block45;
                        }
                    }
                }
                break;
            }
            this.jj_la1[23] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void Concatenation() throws ParseException {
        this.Math();
        while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 53: {
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                    return;
                }
            }
            this.jj_consume_token(53);
            AstConcatenation jjtn001 = new AstConcatenation(18);
            boolean jjtc001 = true;
            this.jjtree.openNodeScope(jjtn001);
            try {
                this.Math();
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

    public final void Math() throws ParseException {
        block26: {
            this.Multiplication();
            block17: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 46: 
                    case 47: {
                        break;
                    }
                    default: {
                        this.jj_la1[25] = this.jj_gen;
                        break block26;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 46: {
                        this.jj_consume_token(46);
                        AstPlus jjtn001 = new AstPlus(19);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.Multiplication();
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
                    case 47: {
                        this.jj_consume_token(47);
                        AstMinus jjtn002 = new AstMinus(20);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.Multiplication();
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
            this.jj_la1[26] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void Multiplication() throws ParseException {
        block44: {
            this.Unary();
            block31: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 45: 
                    case 49: 
                    case 50: 
                    case 51: 
                    case 52: {
                        break;
                    }
                    default: {
                        this.jj_la1[27] = this.jj_gen;
                        break block44;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 45: {
                        this.jj_consume_token(45);
                        AstMult jjtn001 = new AstMult(21);
                        boolean jjtc001 = true;
                        this.jjtree.openNodeScope(jjtn001);
                        try {
                            this.Unary();
                            continue block31;
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
                            if (!jjtc001) continue block31;
                            this.jjtree.closeNodeScope((Node)jjtn001, 2);
                            continue block31;
                        }
                    }
                    case 49: 
                    case 50: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 49: {
                                this.jj_consume_token(49);
                                break;
                            }
                            case 50: {
                                this.jj_consume_token(50);
                                break;
                            }
                            default: {
                                this.jj_la1[28] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstDiv jjtn002 = new AstDiv(22);
                        boolean jjtc002 = true;
                        this.jjtree.openNodeScope(jjtn002);
                        try {
                            this.Unary();
                            continue block31;
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
                            if (!jjtc002) continue block31;
                            this.jjtree.closeNodeScope((Node)jjtn002, 2);
                            continue block31;
                        }
                    }
                    case 51: 
                    case 52: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 51: {
                                this.jj_consume_token(51);
                                break;
                            }
                            case 52: {
                                this.jj_consume_token(52);
                                break;
                            }
                            default: {
                                this.jj_la1[29] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        AstMod jjtn003 = new AstMod(23);
                        boolean jjtc003 = true;
                        this.jjtree.openNodeScope(jjtn003);
                        try {
                            this.Unary();
                            continue block31;
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
                            if (!jjtc003) continue block31;
                            this.jjtree.closeNodeScope((Node)jjtn003, 2);
                            continue block31;
                        }
                    }
                }
                break;
            }
            this.jj_la1[30] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
    }

    public final void Unary() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 47: {
                this.jj_consume_token(47);
                AstNegative jjtn001 = new AstNegative(24);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.Unary();
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
            case 37: 
            case 38: {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 37: {
                        this.jj_consume_token(37);
                        break;
                    }
                    case 38: {
                        this.jj_consume_token(38);
                        break;
                    }
                    default: {
                        this.jj_la1[31] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                AstNot jjtn002 = new AstNot(25);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.Unary();
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
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            case 43: {
                this.jj_consume_token(43);
                AstEmpty jjtn003 = new AstEmpty(26);
                boolean jjtc003 = true;
                this.jjtree.openNodeScope(jjtn003);
                try {
                    this.Unary();
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
                        this.jjtree.closeNodeScope((Node)jjtn003, true);
                    }
                }
            }
            case 8: 
            case 10: 
            case 11: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 18: 
            case 20: 
            case 56: {
                this.Value();
                break;
            }
            default: {
                this.jj_la1[32] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void Value() throws ParseException {
        AstValue jjtn001 = new AstValue(27);
        boolean jjtc001 = true;
        this.jjtree.openNodeScope(jjtn001);
        try {
            this.ValuePrefix();
            block7: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 17: 
                    case 20: {
                        break;
                    }
                    default: {
                        this.jj_la1[33] = this.jj_gen;
                        break block7;
                    }
                }
                this.ValueSuffix();
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

    public final void ValuePrefix() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 10: 
            case 11: 
            case 13: 
            case 14: 
            case 15: 
            case 16: {
                this.Literal();
                break;
            }
            case 8: 
            case 18: 
            case 20: 
            case 56: {
                this.NonLiteral();
                break;
            }
            default: {
                this.jj_la1[34] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final void ValueSuffix() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 17: {
                this.DotSuffix();
                break;
            }
            case 20: {
                this.BracketSuffix();
                break;
            }
            default: {
                this.jj_la1[35] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 18: {
                this.MethodParameters();
                break;
            }
            default: {
                this.jj_la1[36] = this.jj_gen;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void DotSuffix() throws ParseException {
        AstDotSuffix jjtn000 = new AstDotSuffix(28);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            this.jj_consume_token(17);
            t = this.jj_consume_token(56);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void BracketSuffix() throws ParseException {
        AstBracketSuffix jjtn000 = new AstBracketSuffix(29);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(20);
            this.Expression();
            this.jj_consume_token(21);
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

    public final void MethodParameters() throws ParseException {
        AstMethodParameters jjtn000 = new AstMethodParameters(30);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(18);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 24: {
                                break;
                            }
                            default: {
                                this.jj_la1[37] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(24);
                        this.Expression();
                    }
                }
                default: {
                    this.jj_la1[38] = this.jj_gen;
                }
            }
            this.jj_consume_token(19);
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

    public final void NonLiteral() throws ParseException {
        if (this.jj_2_6(5)) {
            this.LambdaExpressionOrInvocation();
        } else {
            block0 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 18: {
                    this.jj_consume_token(18);
                    this.Expression();
                    this.jj_consume_token(19);
                    break;
                }
                default: {
                    this.jj_la1[39] = this.jj_gen;
                    if (this.jj_2_7(Integer.MAX_VALUE)) {
                        this.Function();
                        break;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 56: {
                            this.Identifier();
                            break block0;
                        }
                    }
                    this.jj_la1[40] = this.jj_gen;
                    if (this.jj_2_8(5)) {
                        this.SetData();
                        break;
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 20: {
                            this.ListData();
                            break block0;
                        }
                        case 8: {
                            this.MapData();
                            break block0;
                        }
                    }
                    this.jj_la1[41] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
    }

    public final void SetData() throws ParseException {
        AstSetData jjtn000 = new AstSetData(31);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(8);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 24: {
                                break;
                            }
                            default: {
                                this.jj_la1[42] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(24);
                        this.Expression();
                    }
                }
                default: {
                    this.jj_la1[43] = this.jj_gen;
                }
            }
            this.jj_consume_token(9);
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

    public final void ListData() throws ParseException {
        AstListData jjtn000 = new AstListData(32);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(20);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.Expression();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 24: {
                                break;
                            }
                            default: {
                                this.jj_la1[44] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(24);
                        this.Expression();
                    }
                }
                default: {
                    this.jj_la1[45] = this.jj_gen;
                }
            }
            this.jj_consume_token(21);
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

    public final void MapData() throws ParseException {
        AstMapData jjtn000 = new AstMapData(33);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(8);
            block2 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 8: 
                case 10: 
                case 11: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 20: 
                case 37: 
                case 38: 
                case 43: 
                case 47: 
                case 56: {
                    this.MapEntry();
                    while (true) {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 24: {
                                break;
                            }
                            default: {
                                this.jj_la1[46] = this.jj_gen;
                                break block2;
                            }
                        }
                        this.jj_consume_token(24);
                        this.MapEntry();
                    }
                }
                default: {
                    this.jj_la1[47] = this.jj_gen;
                }
            }
            this.jj_consume_token(9);
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

    public final void MapEntry() throws ParseException {
        AstMapEntry jjtn000 = new AstMapEntry(34);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Expression();
            this.jj_consume_token(22);
            this.Expression();
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
     */
    public final void Identifier() throws ParseException {
        AstIdentifier jjtn000 = new AstIdentifier(35);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            t = this.jj_consume_token(56);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Function() throws ParseException {
        AstFunction jjtn000 = new AstFunction(36);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t0 = null;
        Token t1 = null;
        try {
            t0 = this.jj_consume_token(56);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 22: {
                    this.jj_consume_token(22);
                    t1 = this.jj_consume_token(56);
                    break;
                }
                default: {
                    this.jj_la1[48] = this.jj_gen;
                }
            }
            if (t1 != null) {
                jjtn000.setPrefix(t0.image);
                jjtn000.setLocalName(t1.image);
            } else {
                jjtn000.setLocalName(t0.image);
            }
            block11: while (true) {
                this.MethodParameters();
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 18: {
                        continue block11;
                    }
                }
                break;
            }
            this.jj_la1[49] = this.jj_gen;
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

    public final void Literal() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 14: 
            case 15: {
                this.Boolean();
                break;
            }
            case 11: {
                this.FloatingPoint();
                break;
            }
            case 10: {
                this.Integer();
                break;
            }
            case 13: {
                this.String();
                break;
            }
            case 16: {
                this.Null();
                break;
            }
            default: {
                this.jj_la1[50] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void Boolean() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 14: {
                AstTrue jjtn001 = new AstTrue(37);
                boolean jjtc001 = true;
                this.jjtree.openNodeScope(jjtn001);
                try {
                    this.jj_consume_token(14);
                    break;
                }
                finally {
                    if (jjtc001) {
                        this.jjtree.closeNodeScope((Node)jjtn001, true);
                    }
                }
            }
            case 15: {
                AstFalse jjtn002 = new AstFalse(38);
                boolean jjtc002 = true;
                this.jjtree.openNodeScope(jjtn002);
                try {
                    this.jj_consume_token(15);
                    break;
                }
                finally {
                    if (jjtc002) {
                        this.jjtree.closeNodeScope((Node)jjtn002, true);
                    }
                }
            }
            default: {
                this.jj_la1[51] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void FloatingPoint() throws ParseException {
        AstFloatingPoint jjtn000 = new AstFloatingPoint(39);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            t = this.jj_consume_token(11);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
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
    public final void Integer() throws ParseException {
        AstInteger jjtn000 = new AstInteger(40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            t = this.jj_consume_token(10);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
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
    public final void String() throws ParseException {
        AstString jjtn000 = new AstString(41);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Token t = null;
        try {
            t = this.jj_consume_token(13);
            this.jjtree.closeNodeScope((Node)jjtn000, true);
            jjtc000 = false;
            jjtn000.setImage(t.image);
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope((Node)jjtn000, true);
            }
        }
    }

    public final void Null() throws ParseException {
        AstNull jjtn000 = new AstNull(42);
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

    private boolean jj_3R_And_173_17_41() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(39)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(40)) {
                return true;
            }
        }
        return this.jj_3R_Equality_182_5_40();
    }

    private boolean jj_3R_LambdaExpressionOrInvocation_144_45_30() {
        return this.jj_3R_Choice_155_5_22();
    }

    private boolean jj_3R_Equality_182_5_40() {
        Token xsp;
        if (this.jj_3R_Compare_196_5_44()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Equality_184_9_45());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_ListData_350_26_109() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        return this.jj_3R_Expression_99_5_36();
    }

    private boolean jj_3R_MapEntry_368_5_107() {
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        if (this.jj_scan_token(22)) {
            return true;
        }
        return this.jj_3R_Expression_99_5_36();
    }

    private boolean jj_3R_MapData_362_11_105() {
        return this.jj_3R_MapEntry_368_5_107();
    }

    private boolean jj_3R_LambdaParameters_132_46_43() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        return this.jj_3R_Identifier_377_5_38();
    }

    private boolean jj_3R_And_173_5_34() {
        Token xsp;
        if (this.jj_3R_Equality_182_5_40()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_And_173_17_41());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_SetData_343_26_37() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        return this.jj_3R_Expression_99_5_36();
    }

    private boolean jj_3R_Or_164_12_35() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(41)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(42)) {
                return true;
            }
        }
        return this.jj_3R_And_173_5_34();
    }

    private boolean jj_3R_MapData_361_5_99() {
        if (this.jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_MapData_362_11_105()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(9);
    }

    private boolean jj_3R_ListData_350_11_104() {
        Token xsp;
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_ListData_350_26_109());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Or_164_5_29() {
        Token xsp;
        if (this.jj_3R_And_173_5_34()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Or_164_12_35());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_5() {
        if (this.jj_scan_token(48)) {
            return true;
        }
        if (this.jj_3R_Choice_155_5_22()) {
            return true;
        }
        return this.jj_scan_token(22);
    }

    private boolean jj_3R_ListData_349_5_98() {
        if (this.jj_scan_token(20)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_ListData_350_11_104()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(21);
    }

    private boolean jj_3R_LambdaParameters_132_31_39() {
        Token xsp;
        if (this.jj_3R_Identifier_377_5_38()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_LambdaParameters_132_46_43());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_SetData_343_11_31() {
        Token xsp;
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_SetData_343_26_37());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Choice_155_5_22() {
        Token xsp;
        if (this.jj_3R_Or_164_5_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_5());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_3() {
        return this.jj_3R_LambdaExpression_124_5_21();
    }

    private boolean jj_3R_MethodParameters_317_31_111() {
        return this.jj_scan_token(24);
    }

    private boolean jj_3R_SetData_342_5_25() {
        if (this.jj_scan_token(8)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_SetData_343_11_31()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(9);
    }

    private boolean jj_3_4() {
        return this.jj_3R_LambdaExpression_124_5_21();
    }

    private boolean jj_3R_null_328_18_24() {
        if (this.jj_scan_token(56)) {
            return true;
        }
        return this.jj_scan_token(22);
    }

    private boolean jj_3_7() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_null_328_18_24()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_scan_token(56)) {
            return true;
        }
        return this.jj_scan_token(18);
    }

    private boolean jj_3R_LambdaParameters_132_20_33() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_LambdaParameters_132_31_39()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(19);
    }

    private boolean jj_3R_NonLiteral_332_7_89() {
        return this.jj_3R_MapData_361_5_99();
    }

    private boolean jj_3R_NonLiteral_331_7_88() {
        return this.jj_3R_ListData_349_5_98();
    }

    private boolean jj_3R_LambdaExpressionOrInvocation_141_5_23() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        if (this.jj_3R_LambdaParameters_132_5_27()) {
            return true;
        }
        if (this.jj_scan_token(55)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_4()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_LambdaExpressionOrInvocation_144_45_30()) {
                return true;
            }
        }
        return this.jj_scan_token(19);
    }

    private boolean jj_3_8() {
        return this.jj_3R_SetData_342_5_25();
    }

    private boolean jj_3R_NonLiteral_329_7_87() {
        return this.jj_3R_Identifier_377_5_38();
    }

    private boolean jj_3R_NonLiteral_328_7_86() {
        return this.jj_3R_Function_390_5_97();
    }

    private boolean jj_3R_NonLiteral_327_7_85() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        return this.jj_scan_token(19);
    }

    private boolean jj_3R_MethodParameters_317_16_110() {
        Token xsp;
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_MethodParameters_317_31_111());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_ValueSuffix_291_41_108() {
        return this.jj_3R_MethodParameters_317_5_106();
    }

    private boolean jj_3R_NonLiteral_326_5_77() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_6()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_NonLiteral_327_7_85()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_NonLiteral_328_7_86()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_NonLiteral_329_7_87()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3_8()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_NonLiteral_331_7_88()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_NonLiteral_332_7_89()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3_6() {
        return this.jj_3R_LambdaExpressionOrInvocation_141_5_23();
    }

    private boolean jj_3R_LambdaParameters_132_5_32() {
        return this.jj_3R_Identifier_377_5_38();
    }

    private boolean jj_3R_LambdaParameters_132_5_27() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_LambdaParameters_132_5_32()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_LambdaParameters_132_20_33()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_1() {
        if (this.jj_scan_token(54)) {
            return true;
        }
        return this.jj_3R_Assignment_115_5_20();
    }

    private boolean jj_3R_MethodParameters_317_5_106() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_MethodParameters_317_16_110()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(19);
    }

    private boolean jj_3R_LambdaExpression_124_5_21() {
        if (this.jj_3R_LambdaParameters_132_5_27()) {
            return true;
        }
        if (this.jj_scan_token(55)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3_3()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_LambdaExpression_124_68_28()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_Semicolon_107_20_46() {
        if (this.jj_scan_token(23)) {
            return true;
        }
        return this.jj_3R_Assignment_115_5_20();
    }

    private boolean jj_3R_BracketSuffix_309_5_91() {
        if (this.jj_scan_token(20)) {
            return true;
        }
        if (this.jj_3R_Expression_99_5_36()) {
            return true;
        }
        return this.jj_scan_token(21);
    }

    private boolean jj_3R_ValueSuffix_291_21_79() {
        return this.jj_3R_BracketSuffix_309_5_91();
    }

    private boolean jj_3R_Assignment_116_5_26() {
        Token xsp;
        if (this.jj_3R_Choice_155_5_22()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3_1());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_2() {
        return this.jj_3R_LambdaExpression_124_5_21();
    }

    private boolean jj_3R_Assignment_115_5_20() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3_2()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Assignment_116_5_26()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_DotSuffix_300_5_90() {
        if (this.jj_scan_token(17)) {
            return true;
        }
        return this.jj_scan_token(56);
    }

    private boolean jj_3R_Semicolon_107_5_42() {
        Token xsp;
        if (this.jj_3R_Assignment_115_5_20()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Semicolon_107_20_46());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_ValueSuffix_291_7_78() {
        return this.jj_3R_DotSuffix_300_5_90();
    }

    private boolean jj_3R_ValueSuffix_291_5_75() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_ValueSuffix_291_7_78()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_ValueSuffix_291_21_79()) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_ValueSuffix_291_41_108()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_Expression_99_5_36() {
        return this.jj_3R_Semicolon_107_5_42();
    }

    private boolean jj_3R_Value_272_21_72() {
        return this.jj_3R_ValueSuffix_291_5_75();
    }

    private boolean jj_3R_ValuePrefix_282_7_74() {
        return this.jj_3R_NonLiteral_326_5_77();
    }

    private boolean jj_3R_ValuePrefix_281_5_71() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_ValuePrefix_281_5_73()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_ValuePrefix_282_7_74()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_ValuePrefix_281_5_73() {
        return this.jj_3R_Literal_408_5_76();
    }

    private boolean jj_3R_Value_272_5_70() {
        Token xsp;
        if (this.jj_3R_ValuePrefix_281_5_71()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Value_272_21_72());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Null_458_5_96() {
        return this.jj_scan_token(16);
    }

    private boolean jj_3R_Unary_263_9_66() {
        return this.jj_3R_Value_272_5_70();
    }

    private boolean jj_3R_Unary_261_9_65() {
        if (this.jj_scan_token(43)) {
            return true;
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_Unary_259_9_64() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(37)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(38)) {
                return true;
            }
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_Unary_257_9_59() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Unary_257_9_63()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Unary_259_9_64()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_Unary_261_9_65()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_Unary_263_9_66()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_Unary_257_9_63() {
        if (this.jj_scan_token(47)) {
            return true;
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_String_449_5_95() {
        return this.jj_scan_token(13);
    }

    private boolean jj_3R_Multiplication_247_9_69() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(51)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(52)) {
                return true;
            }
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_Integer_440_5_94() {
        return this.jj_scan_token(10);
    }

    private boolean jj_3R_Multiplication_245_9_68() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(49)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(50)) {
                return true;
            }
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_Multiplication_243_9_60() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Multiplication_243_9_67()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Multiplication_245_9_68()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_Multiplication_247_9_69()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3R_Multiplication_243_9_67() {
        if (this.jj_scan_token(45)) {
            return true;
        }
        return this.jj_3R_Unary_257_9_59();
    }

    private boolean jj_3R_Multiplication_241_5_57() {
        Token xsp;
        if (this.jj_3R_Unary_257_9_59()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Multiplication_243_9_60());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_FloatingPoint_431_5_93() {
        return this.jj_scan_token(11);
    }

    private boolean jj_3R_Math_231_9_62() {
        if (this.jj_scan_token(47)) {
            return true;
        }
        return this.jj_3R_Multiplication_241_5_57();
    }

    private boolean jj_3R_Boolean_423_7_101() {
        return this.jj_scan_token(15);
    }

    private boolean jj_3R_Math_229_9_58() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Math_229_9_61()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Math_231_9_62()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_Math_229_9_61() {
        if (this.jj_scan_token(46)) {
            return true;
        }
        return this.jj_3R_Multiplication_241_5_57();
    }

    private boolean jj_3R_Boolean_421_5_100() {
        return this.jj_scan_token(14);
    }

    private boolean jj_3R_Boolean_421_5_92() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Boolean_421_5_100()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Boolean_423_7_101()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_Math_227_5_51() {
        Token xsp;
        if (this.jj_3R_Multiplication_241_5_57()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Math_229_9_58());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Literal_412_7_84() {
        return this.jj_3R_Null_458_5_96();
    }

    private boolean jj_3R_Literal_411_7_83() {
        return this.jj_3R_String_449_5_95();
    }

    private boolean jj_3R_Concatenation_217_10_52() {
        if (this.jj_scan_token(53)) {
            return true;
        }
        return this.jj_3R_Math_227_5_51();
    }

    private boolean jj_3R_Literal_410_7_82() {
        return this.jj_3R_Integer_440_5_94();
    }

    private boolean jj_3R_Literal_409_7_81() {
        return this.jj_3R_FloatingPoint_431_5_93();
    }

    private boolean jj_3R_Function_390_24_102() {
        if (this.jj_scan_token(22)) {
            return true;
        }
        return this.jj_scan_token(56);
    }

    private boolean jj_3R_Literal_408_5_80() {
        return this.jj_3R_Boolean_421_5_92();
    }

    private boolean jj_3R_Literal_408_5_76() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Literal_408_5_80()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Literal_409_7_81()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_Literal_410_7_82()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_Literal_411_7_83()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_Literal_412_7_84()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_Concatenation_215_6_47() {
        Token xsp;
        if (this.jj_3R_Math_227_5_51()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Concatenation_217_10_52());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Function_399_7_103() {
        return this.jj_3R_MethodParameters_317_5_106();
    }

    private boolean jj_3R_Compare_204_9_56() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(29)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(30)) {
                return true;
            }
        }
        return this.jj_3R_Concatenation_215_6_47();
    }

    private boolean jj_3R_Compare_202_9_55() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(31)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(32)) {
                return true;
            }
        }
        return this.jj_3R_Concatenation_215_6_47();
    }

    private boolean jj_3R_Compare_200_9_54() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(25)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(26)) {
                return true;
            }
        }
        return this.jj_3R_Concatenation_215_6_47();
    }

    private boolean jj_3R_Compare_198_9_48() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Compare_198_9_53()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Compare_200_9_54()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_Compare_202_9_55()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_Compare_204_9_56()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_Compare_198_9_53() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(27)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(28)) {
                return true;
            }
        }
        return this.jj_3R_Concatenation_215_6_47();
    }

    private boolean jj_3R_Function_390_5_97() {
        if (this.jj_scan_token(56)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Function_390_24_102()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_3R_Function_399_7_103()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Function_399_7_103());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Compare_196_5_44() {
        Token xsp;
        if (this.jj_3R_Concatenation_215_6_47()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_Compare_198_9_48());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_Equality_186_9_50() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(35)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(36)) {
                return true;
            }
        }
        return this.jj_3R_Compare_196_5_44();
    }

    private boolean jj_3R_Equality_184_9_45() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_Equality_184_9_49()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_Equality_186_9_50()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_Equality_184_9_49() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(33)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(34)) {
                return true;
            }
        }
        return this.jj_3R_Compare_196_5_44();
    }

    private boolean jj_3R_LambdaExpression_124_68_28() {
        return this.jj_3R_Choice_155_5_22();
    }

    private boolean jj_3R_Identifier_377_5_38() {
        return this.jj_scan_token(56);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{14, 14, 0x800000, 1436928, 1436928, 0x1000000, 0, 262144, 1436928, 262144, 0, 0, 0, 0, 0, 0, 0, 0, -33554432, 0x18000000, 0x6000000, Integer.MIN_VALUE, 0x60000000, -33554432, 0, 0, 0, 0, 0, 0, 0, 0, 1436928, 0x120000, 1436928, 0x120000, 262144, 0x1000000, 1436928, 262144, 0, 0x100100, 0x1000000, 1436928, 0x1000000, 1436928, 0x1000000, 1436928, 0x400000, 262144, 125952, 49152};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 16812128, 16812128, 0, 0x1000000, 0x1000000, 16812128, 0, 1536, 1536, 384, 384, 30, 6, 24, 30, 1, 0, 0, 1, 0, 1, 0x200000, 49152, 49152, 1974272, 393216, 0x180000, 1974272, 96, 16812128, 0, 0x1000000, 0, 0, 0, 16812128, 0, 0x1000000, 0, 0, 16812128, 0, 16812128, 0, 16812128, 0, 0, 0, 0};
    }

    public ELParser(InputStream stream) {
        this(stream, null);
    }

    public ELParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ELParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 52; ++i) {
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
        for (i = 0; i < 52; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ELParser(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ELParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 52; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        int i;
        if (this.jj_input_stream == null) {
            this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        } else {
            this.jj_input_stream.ReInit(stream, 1, 1);
        }
        if (this.token_source == null) {
            this.token_source = new ELParserTokenManager(this.jj_input_stream);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 52; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ELParser(ELParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 52; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ELParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (i = 0; i < 52; ++i) {
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
            throw jj_ls;
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
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk_f() {
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
            for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                boolean isMatched = true;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] == this.jj_expentry[i]) continue;
                    isMatched = false;
                    break;
                }
                if (!isMatched) continue;
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
        boolean[] la1tokens = new boolean[62];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 52; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 62; ++i) {
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
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final boolean trace_enabled() {
        return this.trace_enabled;
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 8; ++i) {
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
        ELParser.jj_la1_init_0();
        ELParser.jj_la1_init_1();
        jj_ls = new LookaheadSuccess();
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }
}

