/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.exprparse;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.template.soy.exprparse.ExpressionParserConstants;
import com.google.template.soy.exprparse.ExpressionParserTokenManager;
import com.google.template.soy.exprparse.ParseException;
import com.google.template.soy.exprparse.SimpleCharStream;
import com.google.template.soy.exprparse.Token;
import com.google.template.soy.exprparse.TokenMgrError;
import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.NullNode;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.internal.base.UnescapeUtils;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ExpressionParser
implements ExpressionParserConstants {
    public ExpressionParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[18];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[7];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final StackTracelessAtlassianLookaheadSuccess jj_ls = new StackTracelessAtlassianLookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public ExpressionParser(String input) {
        this(new StringReader(input));
    }

    public List<ExprRootNode<?>> parseExpressionList() throws TokenMgrError, ParseException {
        return this.ExprListInput();
    }

    public ExprRootNode<?> parseExpression() throws TokenMgrError, ParseException {
        return this.ExprInput();
    }

    public ExprRootNode<VarNode> parseVariable() throws TokenMgrError, ParseException {
        return this.VarInput();
    }

    public ExprRootNode<ExprNode> parseDataReference() throws TokenMgrError, ParseException {
        return this.DataRefInput();
    }

    public ExprRootNode<GlobalNode> parseGlobal() throws TokenMgrError, ParseException {
        return this.GlobalInput();
    }

    private static final ExprNode.OperatorNode createOperatorNode(String op, int numOperands) {
        try {
            return Operator.of(op, numOperands).getNodeClass().getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            Throwables.throwIfUnchecked((Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private final ExprNode.PrimitiveNode Primitive() throws ParseException {
        AbstractPrimitiveNode primitive;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: {
                this.jj_consume_token(1);
                primitive = new NullNode();
                break;
            }
            case 2: {
                Token b = this.jj_consume_token(2);
                primitive = new BooleanNode(b.image.equals("true"));
                break;
            }
            case 3: {
                Token i = this.jj_consume_token(3);
                if (i.image.startsWith("0x")) {
                    primitive = new IntegerNode(Integer.parseInt(i.image.substring(2), 16));
                    break;
                }
                primitive = new IntegerNode(Integer.parseInt(i.image, 10));
                break;
            }
            case 4: {
                Token f = this.jj_consume_token(4);
                primitive = new FloatNode(Double.parseDouble(f.image));
                break;
            }
            case 5: {
                Token str = this.jj_consume_token(5);
                String strNoQuotes = str.image.substring(1, str.image.length() - 1);
                primitive = new StringNode(UnescapeUtils.unescapeJs(strNoQuotes));
                break;
            }
            default: {
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return primitive;
    }

    private final String UnaryOp() throws ParseException {
        Token unaryOp;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 9: {
                unaryOp = this.jj_consume_token(9);
                break;
            }
            case 8: {
                unaryOp = this.jj_consume_token(8);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return unaryOp.image;
    }

    private final String BinaryOp() throws ParseException {
        Token binaryOp;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 10: {
                binaryOp = this.jj_consume_token(10);
                break;
            }
            case 8: {
                binaryOp = this.jj_consume_token(8);
                break;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return binaryOp.image;
    }

    private final List<ExprRootNode<?>> ExprListInput() throws ParseException {
        List<ExprNode> exprList = this.ExprList();
        ArrayList rootedExprList = Lists.newArrayList();
        for (ExprNode expr : exprList) {
            rootedExprList.add(new ExprRootNode<ExprNode>(expr));
        }
        this.jj_consume_token(0);
        return rootedExprList;
    }

    private final ExprRootNode<?> ExprInput() throws ParseException {
        ExprNode expr = this.Expr();
        this.jj_consume_token(0);
        return new ExprRootNode<ExprNode>(expr);
    }

    private final ExprRootNode<VarNode> VarInput() throws ParseException {
        VarNode var = this.Var();
        this.jj_consume_token(0);
        return new ExprRootNode<VarNode>(var);
    }

    private final ExprRootNode<ExprNode> DataRefInput() throws ParseException {
        ExprNode dataRef = this.DataRef();
        this.jj_consume_token(0);
        return new ExprRootNode<ExprNode>(dataRef);
    }

    private final ExprRootNode<GlobalNode> GlobalInput() throws ParseException {
        GlobalNode global = this.Global();
        this.jj_consume_token(0);
        return new ExprRootNode<GlobalNode>(global);
    }

    private final List<ExprNode> ExprList() throws ParseException {
        ArrayList exprList = Lists.newArrayList();
        ExprNode expr = this.Expr();
        exprList.add(expr);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 18: {
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(18);
            expr = this.Expr();
            exprList.add(expr);
        }
        return exprList;
    }

    private final ExprNode Expr() throws ParseException {
        ExprNode expr = this.PrecExpr1();
        return expr;
    }

    private final ExprNode PrecExpr1() throws ParseException {
        ExprNode expr = this.PrecExpr(2);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 19: 
            case 20: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 19: {
                        this.jj_consume_token(19);
                        ExprNode expr1 = this.PrecExpr1();
                        OperatorNodes.NullCoalescingOpNode nullCoal = new OperatorNodes.NullCoalescingOpNode();
                        nullCoal.addChild(expr);
                        nullCoal.addChild(expr1);
                        expr = nullCoal;
                        break block0;
                    }
                    case 20: {
                        this.jj_consume_token(20);
                        ExprNode expr1 = this.PrecExpr1();
                        this.jj_consume_token(21);
                        ExprNode expr2 = this.PrecExpr1();
                        OperatorNodes.ConditionalOpNode cond = new OperatorNodes.ConditionalOpNode();
                        cond.addChild(expr);
                        cond.addChild(expr1);
                        cond.addChild(expr2);
                        expr = cond;
                        break block0;
                    }
                }
                this.jj_la1[4] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
            }
        }
        return expr;
    }

    private final ExprNode PrecExpr(int minPrec) throws ParseException {
        ExprNode expr = this.Primary();
        while (this.jj_2_1(Integer.MAX_VALUE) && Operator.of(this.getToken((int)1).image, 2).getPrecedence() >= minPrec) {
            String binaryOp = this.BinaryOp();
            ExprNode rightOperand = this.PrecExpr(Operator.of(binaryOp, 2).getPrecedence() + 1);
            ExprNode.OperatorNode opNode = ExpressionParser.createOperatorNode(binaryOp, 2);
            opNode.addChild(expr);
            opNode.addChild(rightOperand);
            expr = opNode;
        }
        return expr;
    }

    private final ExprNode Primary() throws ParseException {
        ExprNode primary;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 22: {
                this.jj_consume_token(22);
                primary = this.Expr();
                this.jj_consume_token(23);
                break;
            }
            case 8: 
            case 9: {
                String unaryOp = this.UnaryOp();
                ExprNode operand = this.PrecExpr(Operator.of(unaryOp, 1).getPrecedence());
                ExprNode.OperatorNode opNode = ExpressionParser.createOperatorNode(unaryOp, 1);
                opNode.addChild(operand);
                primary = opNode;
                break;
            }
            default: {
                this.jj_la1[6] = this.jj_gen;
                if (this.jj_2_2(Integer.MAX_VALUE)) {
                    primary = this.FunctionCall();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 12: 
                    case 26: 
                    case 27: {
                        primary = this.DataRef();
                        break block0;
                    }
                    case 11: {
                        primary = this.Global();
                        break block0;
                    }
                }
                this.jj_la1[7] = this.jj_gen;
                if (this.jj_2_3(Integer.MAX_VALUE)) {
                    primary = this.ListLiteral();
                    break;
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 24: {
                        primary = this.MapLiteral();
                        break block0;
                    }
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        primary = this.Primitive();
                        break block0;
                    }
                }
                this.jj_la1[8] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return primary;
    }

    private final VarNode Var() throws ParseException {
        Token dollarIdent = this.jj_consume_token(12);
        String identStr = dollarIdent.image.substring(1);
        if (identStr.equals("ij")) {
            throw new ParseException("Invalid var name 'ij' ('ij' is for injected data ref).");
        }
        return new VarNode(identStr);
    }

    private final ExprNode DataRef() throws ParseException {
        AbstractExprNode dataRef;
        block18: {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 26: {
                    this.jj_consume_token(26);
                    Token ident = this.jj_consume_token(11);
                    dataRef = new VarRefNode(ident.image, true, false, null);
                    break;
                }
                case 27: {
                    this.jj_consume_token(27);
                    Token ident = this.jj_consume_token(11);
                    dataRef = new VarRefNode(ident.image, true, true, null);
                    break;
                }
                case 12: {
                    Token dollarIdent = this.jj_consume_token(12);
                    String identStr = dollarIdent.image.substring(1);
                    if (identStr.equals("ij")) {
                        throw new ParseException("Invalid param name 'ij' ('ij' is for injected data ref).");
                    }
                    dataRef = new VarRefNode(identStr, false, false, null);
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            block16: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 13: 
                    case 14: 
                    case 15: 
                    case 16: 
                    case 24: 
                    case 28: {
                        break;
                    }
                    default: {
                        this.jj_la1[10] = this.jj_gen;
                        break block18;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 13: {
                        Token dotIdent = this.jj_consume_token(13);
                        dataRef = new FieldAccessNode(dataRef, dotIdent.image.substring(1), false);
                        continue block16;
                    }
                    case 14: {
                        Token questionDotIdent = this.jj_consume_token(14);
                        dataRef = new FieldAccessNode(dataRef, questionDotIdent.image.substring(2), true);
                        continue block16;
                    }
                    case 15: {
                        Token dotIndex = this.jj_consume_token(15);
                        dataRef = new ItemAccessNode(dataRef, new IntegerNode(Integer.parseInt(dotIndex.image.substring(1))), false, true);
                        continue block16;
                    }
                    case 16: {
                        Token questionDotIndex = this.jj_consume_token(16);
                        dataRef = new ItemAccessNode(dataRef, new IntegerNode(Integer.parseInt(questionDotIndex.image.substring(2))), true, true);
                        continue block16;
                    }
                    case 24: {
                        this.jj_consume_token(24);
                        ExprNode expr = this.Expr();
                        this.jj_consume_token(25);
                        dataRef = new ItemAccessNode(dataRef, expr, false, false);
                        continue block16;
                    }
                    case 28: {
                        this.jj_consume_token(28);
                        ExprNode expr = this.Expr();
                        this.jj_consume_token(25);
                        dataRef = new ItemAccessNode(dataRef, expr, true, false);
                        continue block16;
                    }
                }
                break;
            }
            this.jj_la1[11] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        return dataRef;
    }

    private final GlobalNode Global() throws ParseException {
        StringBuilder globalNameSb = new StringBuilder();
        Token ident = this.jj_consume_token(11);
        globalNameSb.append(ident.image);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: {
                    break;
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
                    break block3;
                }
            }
            Token dotIdent = this.jj_consume_token(13);
            globalNameSb.append(dotIdent.image);
        }
        return new GlobalNode(globalNameSb.toString());
    }

    private final FunctionNode FunctionCall() throws ParseException {
        Token ident = this.jj_consume_token(11);
        this.jj_consume_token(22);
        List<ExprNode> exprList = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 11: 
            case 12: 
            case 22: 
            case 24: 
            case 26: 
            case 27: {
                exprList = this.ExprList();
                break;
            }
            default: {
                this.jj_la1[13] = this.jj_gen;
            }
        }
        this.jj_consume_token(23);
        FunctionNode fnNode = new FunctionNode(ident.image);
        if (exprList != null) {
            fnNode.addChildren((List<? extends ExprNode>)exprList);
        }
        return fnNode;
    }

    private final ListLiteralNode ListLiteral() throws ParseException {
        ArrayList items = Lists.newArrayList();
        this.jj_consume_token(24);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 11: 
            case 12: 
            case 22: 
            case 24: 
            case 26: 
            case 27: {
                ExprNode itemExpr = this.Expr();
                items.add(itemExpr);
                while (this.jj_2_4(Integer.MAX_VALUE)) {
                    this.jj_consume_token(18);
                    itemExpr = this.Expr();
                    items.add(itemExpr);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        break block0;
                    }
                }
                this.jj_la1[14] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[15] = this.jj_gen;
            }
        }
        this.jj_consume_token(25);
        return new ListLiteralNode(items);
    }

    private final MapLiteralNode MapLiteral() throws ParseException {
        ArrayList alternatingKeysAndValues = Lists.newArrayList();
        this.jj_consume_token(24);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 21: {
                this.jj_consume_token(21);
                break;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 11: 
            case 12: 
            case 22: 
            case 24: 
            case 26: 
            case 27: {
                if (this.jj_2_5(Integer.MAX_VALUE)) {
                    Token ident = this.jj_consume_token(11);
                    throw new ParseException("Disallowed single-identifier key \"" + ident.image + "\" in map literal (please surround with single quotes for string or parentheses for global).");
                }
                ExprNode keyExpr = this.Expr();
                alternatingKeysAndValues.add(keyExpr);
                this.jj_consume_token(21);
                ExprNode valueExpr = this.Expr();
                alternatingKeysAndValues.add(valueExpr);
                while (this.jj_2_6(Integer.MAX_VALUE)) {
                    this.jj_consume_token(18);
                    if (this.jj_2_7(Integer.MAX_VALUE)) {
                        Token ident = this.jj_consume_token(11);
                        throw new ParseException("Disallowed single-identifier key \"" + ident.image + "\" in map literal (please surround with single quotes for string or parentheses for global).");
                    }
                    keyExpr = this.Expr();
                    alternatingKeysAndValues.add(keyExpr);
                    this.jj_consume_token(21);
                    valueExpr = this.Expr();
                    alternatingKeysAndValues.add(valueExpr);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        break block0;
                    }
                }
                this.jj_la1[16] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[17] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.jj_consume_token(25);
        return new MapLiteralNode(alternatingKeysAndValues);
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
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
        catch (StackTracelessAtlassianLookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(6, xla);
        }
    }

    private boolean jj_3R_30() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(21)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_39()) {
                return true;
            }
        }
        return this.jj_scan_token(25);
    }

    private boolean jj_3R_16() {
        if (this.jj_scan_token(20)) {
            return true;
        }
        if (this.jj_3R_10()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        return this.jj_3R_10();
    }

    private boolean jj_3R_22() {
        return this.jj_3R_29();
    }

    private boolean jj_3R_21() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_20() {
        return this.jj_3R_27();
    }

    private boolean jj_3R_51() {
        if (this.jj_scan_token(28)) {
            return true;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        return this.jj_scan_token(25);
    }

    private boolean jj_3R_19() {
        return this.jj_3R_26();
    }

    private boolean jj_3R_15() {
        if (this.jj_scan_token(19)) {
            return true;
        }
        return this.jj_3R_10();
    }

    private boolean jj_3R_44() {
        return this.jj_scan_token(5);
    }

    private boolean jj_3R_50() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        return this.jj_scan_token(25);
    }

    private boolean jj_3R_12() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_15()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_16()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_43() {
        return this.jj_scan_token(4);
    }

    private boolean jj_3R_18() {
        if (this.jj_3R_25()) {
            return true;
        }
        return this.jj_3R_11();
    }

    private boolean jj_3_4() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        return this.jj_3R_9();
    }

    private boolean jj_3R_49() {
        return this.jj_scan_token(16);
    }

    private boolean jj_3R_17() {
        if (this.jj_scan_token(22)) {
            return true;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        return this.jj_scan_token(23);
    }

    private boolean jj_3R_10() {
        if (this.jj_3R_11()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_12()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_48() {
        return this.jj_scan_token(15);
    }

    private boolean jj_3R_42() {
        return this.jj_scan_token(3);
    }

    private boolean jj_3R_13() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_17()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_18()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_19()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_20()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_21()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_22()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_23()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_24()) {
                                        return true;
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

    private boolean jj_3R_47() {
        return this.jj_scan_token(14);
    }

    private boolean jj_3R_41() {
        return this.jj_scan_token(2);
    }

    private boolean jj_3R_52() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        return this.jj_3R_9();
    }

    private boolean jj_3R_46() {
        return this.jj_scan_token(13);
    }

    private boolean jj_3R_40() {
        return this.jj_scan_token(1);
    }

    private boolean jj_3R_36() {
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
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_51()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_38() {
        Token xsp;
        if (this.jj_3R_9()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_52());
        xsp = this.jj_scanpos = xsp;
        if (this.jj_scan_token(18)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_31() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_40()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_41()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_42()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_43()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_44()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_35() {
        return this.jj_scan_token(12);
    }

    private boolean jj_3R_9() {
        return this.jj_3R_10();
    }

    private boolean jj_3R_29() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_38()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(25);
    }

    private boolean jj_3_1() {
        return this.jj_3R_7();
    }

    private boolean jj_3R_34() {
        if (this.jj_scan_token(27)) {
            return true;
        }
        return this.jj_scan_token(11);
    }

    private boolean jj_3R_33() {
        if (this.jj_scan_token(26)) {
            return true;
        }
        return this.jj_scan_token(11);
    }

    private boolean jj_3R_14() {
        if (this.jj_3R_7()) {
            return true;
        }
        return this.jj_3R_11();
    }

    private boolean jj_3R_27() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_33()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_34()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_35()) {
                    return true;
                }
            }
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_36());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_7() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        return this.jj_scan_token(21);
    }

    private boolean jj_3R_55() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        return this.jj_3R_9();
    }

    private boolean jj_3R_32() {
        return this.jj_3R_45();
    }

    private boolean jj_3R_11() {
        Token xsp;
        if (this.jj_3R_13()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_14());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3_6() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        return this.jj_3R_9();
    }

    private boolean jj_3R_45() {
        Token xsp;
        if (this.jj_3R_9()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_55());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_56() {
        return this.jj_scan_token(11);
    }

    private boolean jj_3R_26() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        if (this.jj_scan_token(22)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_32()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(23);
    }

    private boolean jj_3R_54() {
        if (this.jj_scan_token(18)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_56()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        return this.jj_3R_9();
    }

    private boolean jj_3R_8() {
        if (this.jj_3R_9()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(18)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(25)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_5() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        return this.jj_scan_token(21);
    }

    private boolean jj_3R_7() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(10)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(8)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_37() {
        return this.jj_scan_token(13);
    }

    private boolean jj_3R_53() {
        return this.jj_scan_token(11);
    }

    private boolean jj_3_3() {
        if (this.jj_scan_token(24)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(25)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_8()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_28() {
        Token xsp;
        if (this.jj_scan_token(11)) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_37());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_39() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_53()) {
            this.jj_scanpos = xsp;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        if (this.jj_scan_token(21)) {
            return true;
        }
        if (this.jj_3R_9()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_54());
        xsp = this.jj_scanpos = xsp;
        if (this.jj_scan_token(18)) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_25() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(9)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(8)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_24() {
        return this.jj_3R_31();
    }

    private boolean jj_3_2() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        return this.jj_scan_token(22);
    }

    private boolean jj_3R_23() {
        return this.jj_3R_30();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{62, 768, 1280, 262144, 0x180000, 0x180000, 0x400300, 201332736, 16777278, 0xC001000, 0x1101E000, 0x1101E000, 8192, 222305086, 262144, 222305086, 262144, 224402238};
    }

    public ExpressionParser(InputStream stream) {
        this(stream, null);
    }

    public ExpressionParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new ExpressionParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
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
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ExpressionParser(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new ExpressionParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
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
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public ExpressionParser(ExpressionParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(ExpressionParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 18; ++i) {
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
        Token t = this.token;
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
        boolean[] la1tokens = new boolean[29];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 18; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 29; ++i) {
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

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 7; ++i) {
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
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (StackTracelessAtlassianLookaheadSuccess stackTracelessAtlassianLookaheadSuccess) {
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
        ExpressionParser.jj_la1_init_0();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class StackTracelessAtlassianLookaheadSuccess
    extends Error {
        private StackTracelessAtlassianLookaheadSuccess() {
        }
    }
}

