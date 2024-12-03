/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Stack;
import java.util.Vector;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;
import org.apache.xalan.xsltc.compiler.AbsoluteLocationPath;
import org.apache.xalan.xsltc.compiler.AbsolutePathPattern;
import org.apache.xalan.xsltc.compiler.AlternativePattern;
import org.apache.xalan.xsltc.compiler.AncestorPattern;
import org.apache.xalan.xsltc.compiler.BinOpExpr;
import org.apache.xalan.xsltc.compiler.BooleanCall;
import org.apache.xalan.xsltc.compiler.BooleanExpr;
import org.apache.xalan.xsltc.compiler.CastCall;
import org.apache.xalan.xsltc.compiler.CeilingCall;
import org.apache.xalan.xsltc.compiler.ConcatCall;
import org.apache.xalan.xsltc.compiler.ContainsCall;
import org.apache.xalan.xsltc.compiler.CurrentCall;
import org.apache.xalan.xsltc.compiler.DocumentCall;
import org.apache.xalan.xsltc.compiler.ElementAvailableCall;
import org.apache.xalan.xsltc.compiler.EqualityExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.FilterExpr;
import org.apache.xalan.xsltc.compiler.FilterParentPath;
import org.apache.xalan.xsltc.compiler.FloorCall;
import org.apache.xalan.xsltc.compiler.FormatNumberCall;
import org.apache.xalan.xsltc.compiler.FunctionAvailableCall;
import org.apache.xalan.xsltc.compiler.FunctionCall;
import org.apache.xalan.xsltc.compiler.GenerateIdCall;
import org.apache.xalan.xsltc.compiler.IdKeyPattern;
import org.apache.xalan.xsltc.compiler.IdPattern;
import org.apache.xalan.xsltc.compiler.IntExpr;
import org.apache.xalan.xsltc.compiler.KeyCall;
import org.apache.xalan.xsltc.compiler.KeyPattern;
import org.apache.xalan.xsltc.compiler.LangCall;
import org.apache.xalan.xsltc.compiler.LastCall;
import org.apache.xalan.xsltc.compiler.LiteralExpr;
import org.apache.xalan.xsltc.compiler.LocalNameCall;
import org.apache.xalan.xsltc.compiler.LogicalExpr;
import org.apache.xalan.xsltc.compiler.NameCall;
import org.apache.xalan.xsltc.compiler.NamespaceUriCall;
import org.apache.xalan.xsltc.compiler.NotCall;
import org.apache.xalan.xsltc.compiler.NumberCall;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.ParameterRef;
import org.apache.xalan.xsltc.compiler.ParentLocationPath;
import org.apache.xalan.xsltc.compiler.ParentPattern;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.PositionCall;
import org.apache.xalan.xsltc.compiler.Predicate;
import org.apache.xalan.xsltc.compiler.ProcessingInstructionPattern;
import org.apache.xalan.xsltc.compiler.QName;
import org.apache.xalan.xsltc.compiler.RealExpr;
import org.apache.xalan.xsltc.compiler.RelationalExpr;
import org.apache.xalan.xsltc.compiler.RelativeLocationPath;
import org.apache.xalan.xsltc.compiler.RelativePathPattern;
import org.apache.xalan.xsltc.compiler.RoundCall;
import org.apache.xalan.xsltc.compiler.StartsWithCall;
import org.apache.xalan.xsltc.compiler.Step;
import org.apache.xalan.xsltc.compiler.StepPattern;
import org.apache.xalan.xsltc.compiler.StringCall;
import org.apache.xalan.xsltc.compiler.StringLengthCall;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.UnaryOpExpr;
import org.apache.xalan.xsltc.compiler.UnionPathExpr;
import org.apache.xalan.xsltc.compiler.UnparsedEntityUriCall;
import org.apache.xalan.xsltc.compiler.UnresolvedRef;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.VariableRef;
import org.apache.xalan.xsltc.compiler.VariableRefBase;
import org.apache.xalan.xsltc.compiler.XPathParser;

class CUP$XPathParser$actions {
    private final XPathParser parser;

    CUP$XPathParser$actions(XPathParser parser) {
        this.parser = parser;
    }

    public final Symbol CUP$XPathParser$do_action(int CUP$XPathParser$act_num, lr_parser CUP$XPathParser$parser, Stack CUP$XPathParser$stack, int CUP$XPathParser$top) throws Exception {
        switch (CUP$XPathParser$act_num) {
            case 140: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("id");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 139: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("self");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 138: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("preceding-sibling");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 137: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("preceding");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 136: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("parent");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 135: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("namespace");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 134: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("following-sibling");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 133: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("following");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 132: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("decendant-or-self");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 131: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("decendant");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 130: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("child");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 129: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("attribute");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 128: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("ancestor-or-self");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 127: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("child");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 126: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("key");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 125: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("mod");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 124: {
                QName RESULT = null;
                RESULT = this.parser.getQNameIgnoreDefaultNs("div");
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 123: {
                QName RESULT = null;
                int qnameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int qnameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                String qname = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = this.parser.getQNameIgnoreDefaultNs(qname);
                Symbol CUP$XPathParser$result = new Symbol(37, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 122: {
                QName qn;
                QName RESULT = null;
                int qnleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int qnright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = qn = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(26, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 121: {
                Object RESULT = null;
                RESULT = null;
                Symbol CUP$XPathParser$result = new Symbol(26, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 120: {
                Integer RESULT = null;
                RESULT = new Integer(7);
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 119: {
                Step RESULT = null;
                int lleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int lright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                String l = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                QName name = this.parser.getQNameIgnoreDefaultNs("name");
                EqualityExpr exp = new EqualityExpr(0, new NameCall(name), new LiteralExpr(l));
                Vector<Predicate> predicates = new Vector<Predicate>();
                predicates.addElement(new Predicate(exp));
                RESULT = new Step(3, 7, predicates);
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 118: {
                Integer RESULT = null;
                RESULT = new Integer(8);
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 117: {
                Integer RESULT = null;
                RESULT = new Integer(3);
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 116: {
                Integer RESULT = null;
                RESULT = new Integer(-1);
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 115: {
                Object nt;
                Object RESULT = null;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(25, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 114: {
                Expression ex;
                Expression RESULT = null;
                int exleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int exright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ex = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(3, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 113: {
                QName vname;
                QName RESULT = null;
                int vnameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int vnameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = vname = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(39, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 112: {
                QName fname;
                QName RESULT = null;
                int fnameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int fnameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = fname = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(38, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 111: {
                Vector RESULT = null;
                int argleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int argright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression arg = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int arglleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int arglright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector argl = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                argl.insertElementAt(arg, 0);
                RESULT = argl;
                Symbol CUP$XPathParser$result = new Symbol(36, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 110: {
                Vector<Expression> RESULT = null;
                int argleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int argright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression arg = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Vector<Expression> temp = new Vector<Expression>();
                temp.addElement(arg);
                RESULT = temp;
                Symbol CUP$XPathParser$result = new Symbol(36, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 109: {
                FunctionCall RESULT = null;
                int fnameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left;
                int fnameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).right;
                QName fname = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).value;
                int arglleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int arglright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Vector argl = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                if (this.parser.getQNameIgnoreDefaultNs("concat").equals(fname)) {
                    RESULT = new ConcatCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("number").equals(fname)) {
                    RESULT = new NumberCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("document").equals(fname)) {
                    this.parser.setMultiDocument(true);
                    RESULT = new DocumentCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("string").equals(fname)) {
                    RESULT = new StringCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("boolean").equals(fname)) {
                    RESULT = new BooleanCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("name").equals(fname)) {
                    RESULT = new NameCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("generate-id").equals(fname)) {
                    RESULT = new GenerateIdCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("not").equals(fname)) {
                    RESULT = new NotCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("format-number").equals(fname)) {
                    RESULT = new FormatNumberCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("unparsed-entity-uri").equals(fname)) {
                    RESULT = new UnparsedEntityUriCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("key").equals(fname)) {
                    RESULT = new KeyCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("id").equals(fname)) {
                    RESULT = new KeyCall(fname, argl);
                    this.parser.setHasIdCall(true);
                } else if (this.parser.getQNameIgnoreDefaultNs("ceiling").equals(fname)) {
                    RESULT = new CeilingCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("round").equals(fname)) {
                    RESULT = new RoundCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("floor").equals(fname)) {
                    RESULT = new FloorCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("contains").equals(fname)) {
                    RESULT = new ContainsCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("string-length").equals(fname)) {
                    RESULT = new StringLengthCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("starts-with").equals(fname)) {
                    RESULT = new StartsWithCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("function-available").equals(fname)) {
                    RESULT = new FunctionAvailableCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("element-available").equals(fname)) {
                    RESULT = new ElementAvailableCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("local-name").equals(fname)) {
                    RESULT = new LocalNameCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("lang").equals(fname)) {
                    RESULT = new LangCall(fname, argl);
                } else if (this.parser.getQNameIgnoreDefaultNs("namespace-uri").equals(fname)) {
                    RESULT = new NamespaceUriCall(fname, argl);
                } else if (this.parser.getQName("http://xml.apache.org/xalan/xsltc", "xsltc", "cast").equals(fname)) {
                    RESULT = new CastCall(fname, argl);
                } else if (fname.getLocalPart().equals("nodeset") || fname.getLocalPart().equals("node-set")) {
                    this.parser.setCallsNodeset(true);
                    RESULT = new FunctionCall(fname, argl);
                } else {
                    RESULT = new FunctionCall(fname, argl);
                }
                Symbol CUP$XPathParser$result = new Symbol(16, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 108: {
                Expression RESULT = null;
                int fnameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int fnameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                QName fname = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                RESULT = this.parser.getQNameIgnoreDefaultNs("current").equals(fname) ? new CurrentCall(fname) : (this.parser.getQNameIgnoreDefaultNs("number").equals(fname) ? new NumberCall(fname, XPathParser.EmptyArgs) : (this.parser.getQNameIgnoreDefaultNs("string").equals(fname) ? new StringCall(fname, XPathParser.EmptyArgs) : (this.parser.getQNameIgnoreDefaultNs("concat").equals(fname) ? new ConcatCall(fname, XPathParser.EmptyArgs) : (this.parser.getQNameIgnoreDefaultNs("true").equals(fname) ? new BooleanExpr(true) : (this.parser.getQNameIgnoreDefaultNs("false").equals(fname) ? new BooleanExpr(false) : (this.parser.getQNameIgnoreDefaultNs("name").equals(fname) ? new NameCall(fname) : (this.parser.getQNameIgnoreDefaultNs("generate-id").equals(fname) ? new GenerateIdCall(fname, XPathParser.EmptyArgs) : (this.parser.getQNameIgnoreDefaultNs("string-length").equals(fname) ? new StringLengthCall(fname, XPathParser.EmptyArgs) : (this.parser.getQNameIgnoreDefaultNs("position").equals(fname) ? new PositionCall(fname) : (this.parser.getQNameIgnoreDefaultNs("last").equals(fname) ? new LastCall(fname) : (this.parser.getQNameIgnoreDefaultNs("local-name").equals(fname) ? new LocalNameCall(fname) : (this.parser.getQNameIgnoreDefaultNs("namespace-uri").equals(fname) ? new NamespaceUriCall(fname) : new FunctionCall(fname, XPathParser.EmptyArgs)))))))))))));
                Symbol CUP$XPathParser$result = new Symbol(16, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 107: {
                VariableRefBase RESULT = null;
                int varNameleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int varNameright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                QName varName = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                SyntaxTreeNode node = this.parser.lookupName(varName);
                if (node != null) {
                    RESULT = node instanceof Variable ? new VariableRef((Variable)node) : (node instanceof Param ? new ParameterRef((Param)node) : new UnresolvedRef(varName));
                }
                if (node == null) {
                    RESULT = new UnresolvedRef(varName);
                }
                Symbol CUP$XPathParser$result = new Symbol(15, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 106: {
                Expression fc;
                Expression RESULT = null;
                int fcleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int fcright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = fc = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 105: {
                RealExpr RESULT = null;
                int numleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int numright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Double num = (Double)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new RealExpr(num);
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 104: {
                Expression RESULT = null;
                int numleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int numright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Long num = (Long)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                long value = num;
                RESULT = value < Integer.MIN_VALUE || value > Integer.MAX_VALUE ? new RealExpr(value) : (num.doubleValue() == 0.0 ? new RealExpr(num.doubleValue()) : (num.intValue() == 0 ? new IntExpr(num.intValue()) : (num.doubleValue() == 0.0 ? new RealExpr(num.doubleValue()) : new IntExpr(num.intValue()))));
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 103: {
                LiteralExpr RESULT = null;
                int stringleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int stringright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                String string = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                String namespace = null;
                int index = string.lastIndexOf(58);
                if (index > 0) {
                    String prefix = string.substring(0, index);
                    namespace = this.parser._symbolTable.lookupNamespace(prefix);
                }
                RESULT = namespace == null ? new LiteralExpr(string) : new LiteralExpr(string, namespace);
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 102: {
                Expression ex;
                Expression RESULT = null;
                int exleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int exright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                RESULT = ex = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 101: {
                Expression vr;
                Expression RESULT = null;
                int vrleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int vrright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = vr = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(17, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 100: {
                FilterExpr RESULT = null;
                int primaryleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int primaryright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Expression primary = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new FilterExpr(primary, pp);
                Symbol CUP$XPathParser$result = new Symbol(6, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 99: {
                Expression primary;
                Expression RESULT = null;
                int primaryleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int primaryright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = primary = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(6, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 98: {
                Step RESULT = null;
                RESULT = new Step(10, -1, null);
                Symbol CUP$XPathParser$result = new Symbol(20, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 97: {
                Step RESULT = null;
                RESULT = new Step(13, -1, null);
                Symbol CUP$XPathParser$result = new Symbol(20, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 96: {
                Integer RESULT = null;
                RESULT = new Integer(13);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 95: {
                Integer RESULT = null;
                RESULT = new Integer(12);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 94: {
                Integer RESULT = null;
                RESULT = new Integer(11);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 93: {
                Integer RESULT = null;
                RESULT = new Integer(10);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 92: {
                Integer RESULT = null;
                RESULT = new Integer(9);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 91: {
                Integer RESULT = null;
                RESULT = new Integer(7);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 90: {
                Integer RESULT = null;
                RESULT = new Integer(6);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 89: {
                Integer RESULT = null;
                RESULT = new Integer(5);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 88: {
                Integer RESULT = null;
                RESULT = new Integer(4);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 87: {
                Integer RESULT = null;
                RESULT = new Integer(3);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 86: {
                Integer RESULT = null;
                RESULT = new Integer(2);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 85: {
                Integer RESULT = null;
                RESULT = new Integer(1);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 84: {
                Integer RESULT = null;
                RESULT = new Integer(0);
                Symbol CUP$XPathParser$result = new Symbol(40, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 83: {
                Integer RESULT = null;
                RESULT = new Integer(2);
                Symbol CUP$XPathParser$result = new Symbol(41, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 82: {
                Integer an;
                Integer RESULT = null;
                int anleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int anright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                RESULT = an = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                Symbol CUP$XPathParser$result = new Symbol(41, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 81: {
                Expression abbrev;
                Expression RESULT = null;
                int abbrevleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int abbrevright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = abbrev = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(7, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 80: {
                Step RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ntestleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntestright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Object ntest = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new Step(axis, this.parser.findNodeType(axis, ntest), null);
                Symbol CUP$XPathParser$result = new Symbol(7, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 79: {
                Step RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int ntestleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int ntestright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Object ntest = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new Step(axis, this.parser.findNodeType(axis, ntest), pp);
                Symbol CUP$XPathParser$result = new Symbol(7, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 78: {
                Step RESULT = null;
                int ntestleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int ntestright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Object ntest = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                if (ntest instanceof Step) {
                    Step step = (Step)ntest;
                    step.addPredicates(pp);
                    RESULT = (Step)ntest;
                } else {
                    RESULT = new Step(3, this.parser.findNodeType(3, ntest), pp);
                }
                Symbol CUP$XPathParser$result = new Symbol(7, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 77: {
                Step RESULT = null;
                int ntestleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntestright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Object ntest = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = ntest instanceof Step ? (Step)ntest : new Step(3, this.parser.findNodeType(3, ntest), null);
                Symbol CUP$XPathParser$result = new Symbol(7, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 76: {
                AbsoluteLocationPath RESULT = null;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                int nodeType = -1;
                if (rlp instanceof Step && this.parser.isElementAxis(((Step)rlp).getAxis())) {
                    nodeType = 1;
                }
                Step step = new Step(5, nodeType, null);
                RESULT = new AbsoluteLocationPath(this.parser.insertStep(step, (RelativeLocationPath)rlp));
                Symbol CUP$XPathParser$result = new Symbol(24, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 75: {
                RelativeLocationPath RESULT = null;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int stepleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int stepright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression step = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Step right = (Step)step;
                int axis = right.getAxis();
                int type = right.getNodeType();
                Vector predicates = right.getPredicates();
                if (axis == 3 && type != 2) {
                    if (predicates == null) {
                        right.setAxis(4);
                        if (rlp instanceof Step && ((Step)rlp).isAbbreviatedDot()) {
                            RESULT = right;
                        } else {
                            RelativeLocationPath left = (RelativeLocationPath)rlp;
                            RESULT = new ParentLocationPath(left, right);
                        }
                    } else if (rlp instanceof Step && ((Step)rlp).isAbbreviatedDot()) {
                        Step left = new Step(5, 1, null);
                        RESULT = new ParentLocationPath(left, right);
                    } else {
                        RelativeLocationPath left = (RelativeLocationPath)rlp;
                        Step mid = new Step(5, 1, null);
                        ParentLocationPath ppl = new ParentLocationPath(mid, right);
                        RESULT = new ParentLocationPath(left, ppl);
                    }
                } else if (axis == 2 || type == 2) {
                    RelativeLocationPath left = (RelativeLocationPath)rlp;
                    Step middle = new Step(5, 1, null);
                    ParentLocationPath ppl = new ParentLocationPath(middle, right);
                    RESULT = new ParentLocationPath(left, ppl);
                } else {
                    RelativeLocationPath left = (RelativeLocationPath)rlp;
                    Step middle = new Step(5, -1, null);
                    ParentLocationPath ppl = new ParentLocationPath(middle, right);
                    RESULT = new ParentLocationPath(left, ppl);
                }
                Symbol CUP$XPathParser$result = new Symbol(22, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 74: {
                Expression aalp;
                Expression RESULT = null;
                int aalpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aalpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = aalp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(23, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 73: {
                AbsoluteLocationPath RESULT = null;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AbsoluteLocationPath(rlp);
                Symbol CUP$XPathParser$result = new Symbol(23, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 72: {
                AbsoluteLocationPath RESULT = null;
                RESULT = new AbsoluteLocationPath();
                Symbol CUP$XPathParser$result = new Symbol(23, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 71: {
                Expression arlp;
                Expression RESULT = null;
                int arlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int arlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = arlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(21, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 70: {
                Expression RESULT = null;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int stepleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int stepright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression step = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = rlp instanceof Step && ((Step)rlp).isAbbreviatedDot() ? step : (((Step)step).isAbbreviatedDot() ? rlp : new ParentLocationPath((RelativeLocationPath)rlp, step));
                Symbol CUP$XPathParser$result = new Symbol(21, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 69: {
                Expression step;
                Expression RESULT = null;
                int stepleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int stepright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = step = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(21, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 68: {
                Expression alp;
                Expression RESULT = null;
                int alpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int alpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = alp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(4, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 67: {
                Expression rlp;
                Expression RESULT = null;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(4, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 66: {
                FilterParentPath RESULT = null;
                int fexpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int fexpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression fexp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                int nodeType = -1;
                if (rlp instanceof Step && this.parser.isElementAxis(((Step)rlp).getAxis())) {
                    nodeType = 1;
                }
                Step step = new Step(5, nodeType, null);
                FilterParentPath fpp = new FilterParentPath(fexp, step);
                fpp = new FilterParentPath(fpp, rlp);
                if (!(fexp instanceof KeyCall)) {
                    fpp.setDescendantAxis();
                }
                RESULT = fpp;
                Symbol CUP$XPathParser$result = new Symbol(19, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 65: {
                FilterParentPath RESULT = null;
                int fexpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int fexpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression fexp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rlpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rlpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression rlp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new FilterParentPath(fexp, rlp);
                Symbol CUP$XPathParser$result = new Symbol(19, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 64: {
                Expression fexp;
                Expression RESULT = null;
                int fexpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int fexpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = fexp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(19, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 63: {
                Expression lp;
                Expression RESULT = null;
                int lpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int lpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = lp = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(19, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 62: {
                UnionPathExpr RESULT = null;
                int peleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int peright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression pe = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int restleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int restright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression rest = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new UnionPathExpr(pe, rest);
                Symbol CUP$XPathParser$result = new Symbol(18, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 61: {
                Expression pe;
                Expression RESULT = null;
                int peleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int peright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = pe = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(18, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 60: {
                UnaryOpExpr RESULT = null;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new UnaryOpExpr(ue);
                Symbol CUP$XPathParser$result = new Symbol(14, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 59: {
                Expression ue;
                Expression RESULT = null;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(14, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 58: {
                BinOpExpr RESULT = null;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new BinOpExpr(4, me, ue);
                Symbol CUP$XPathParser$result = new Symbol(13, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 57: {
                BinOpExpr RESULT = null;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new BinOpExpr(3, me, ue);
                Symbol CUP$XPathParser$result = new Symbol(13, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 56: {
                BinOpExpr RESULT = null;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new BinOpExpr(2, me, ue);
                Symbol CUP$XPathParser$result = new Symbol(13, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 55: {
                Expression ue;
                Expression RESULT = null;
                int ueleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ueright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ue = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(13, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 54: {
                BinOpExpr RESULT = null;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new BinOpExpr(1, ae, me);
                Symbol CUP$XPathParser$result = new Symbol(12, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 53: {
                BinOpExpr RESULT = null;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new BinOpExpr(0, ae, me);
                Symbol CUP$XPathParser$result = new Symbol(12, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 52: {
                Expression me;
                Expression RESULT = null;
                int meleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int meright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = me = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(12, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 51: {
                RelationalExpr RESULT = null;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new RelationalExpr(4, re, ae);
                Symbol CUP$XPathParser$result = new Symbol(11, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 50: {
                RelationalExpr RESULT = null;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new RelationalExpr(5, re, ae);
                Symbol CUP$XPathParser$result = new Symbol(11, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 49: {
                RelationalExpr RESULT = null;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new RelationalExpr(2, re, ae);
                Symbol CUP$XPathParser$result = new Symbol(11, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 48: {
                RelationalExpr RESULT = null;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new RelationalExpr(3, re, ae);
                Symbol CUP$XPathParser$result = new Symbol(11, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 47: {
                Expression ae;
                Expression RESULT = null;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(11, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 46: {
                EqualityExpr RESULT = null;
                int eeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int eeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression ee = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new EqualityExpr(1, ee, re);
                Symbol CUP$XPathParser$result = new Symbol(10, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 45: {
                EqualityExpr RESULT = null;
                int eeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int eeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression ee = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new EqualityExpr(0, ee, re);
                Symbol CUP$XPathParser$result = new Symbol(10, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 44: {
                Expression re;
                Expression RESULT = null;
                int releft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int reright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = re = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(10, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 43: {
                LogicalExpr RESULT = null;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int eeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int eeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ee = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new LogicalExpr(1, ae, ee);
                Symbol CUP$XPathParser$result = new Symbol(9, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 42: {
                Expression e;
                Expression RESULT = null;
                int eleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int eright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = e = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(9, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 41: {
                LogicalExpr RESULT = null;
                int oeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int oeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Expression oe = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new LogicalExpr(0, oe, ae);
                Symbol CUP$XPathParser$result = new Symbol(8, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 40: {
                Expression ae;
                Expression RESULT = null;
                int aeleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int aeright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ae = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(8, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 39: {
                Expression ex;
                Expression RESULT = null;
                int exleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int exright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ex = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(2, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 38: {
                Predicate RESULT = null;
                int eleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int eright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Expression e = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                RESULT = new Predicate(e);
                Symbol CUP$XPathParser$result = new Symbol(5, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 37: {
                Vector RESULT = null;
                int pleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int pright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Expression p = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                pp.insertElementAt(p, 0);
                RESULT = pp;
                Symbol CUP$XPathParser$result = new Symbol(35, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 36: {
                Vector<Expression> RESULT = null;
                int pleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int pright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Expression p = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Vector<Expression> temp = new Vector<Expression>();
                temp.addElement(p);
                RESULT = temp;
                Symbol CUP$XPathParser$result = new Symbol(35, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 35: {
                Integer RESULT = null;
                RESULT = new Integer(2);
                Symbol CUP$XPathParser$result = new Symbol(42, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 34: {
                Integer RESULT = null;
                RESULT = new Integer(3);
                Symbol CUP$XPathParser$result = new Symbol(42, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 33: {
                Integer RESULT = null;
                RESULT = new Integer(2);
                Symbol CUP$XPathParser$result = new Symbol(42, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 32: {
                QName qn;
                QName RESULT = null;
                int qnleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int qnright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = qn = (QName)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(34, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 31: {
                Object RESULT = null;
                RESULT = null;
                Symbol CUP$XPathParser$result = new Symbol(34, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 30: {
                Integer RESULT = null;
                RESULT = new Integer(7);
                Symbol CUP$XPathParser$result = new Symbol(33, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 29: {
                Integer RESULT = null;
                RESULT = new Integer(8);
                Symbol CUP$XPathParser$result = new Symbol(33, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 28: {
                Integer RESULT = null;
                RESULT = new Integer(3);
                Symbol CUP$XPathParser$result = new Symbol(33, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 27: {
                Integer RESULT = null;
                RESULT = new Integer(-1);
                Symbol CUP$XPathParser$result = new Symbol(33, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 26: {
                Object nt;
                Object RESULT = null;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(33, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 25: {
                ProcessingInstructionPattern RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int pipleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int pipright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                StepPattern pip = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = (ProcessingInstructionPattern)pip.setPredicates(pp);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 24: {
                StepPattern pip;
                StepPattern RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int pipleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int pipright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = pip = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 23: {
                StepPattern RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Object nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = this.parser.createStepPattern(axis, nt, pp);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 22: {
                StepPattern RESULT = null;
                int axisleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int axisright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Integer axis = (Integer)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Object nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = this.parser.createStepPattern(axis, nt, null);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 21: {
                ProcessingInstructionPattern RESULT = null;
                int pipleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int pipright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                StepPattern pip = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = (ProcessingInstructionPattern)pip.setPredicates(pp);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 20: {
                StepPattern pip;
                StepPattern RESULT = null;
                int pipleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int pipright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = pip = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 19: {
                StepPattern RESULT = null;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                Object nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                int ppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Vector pp = (Vector)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = this.parser.createStepPattern(3, nt, pp);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 18: {
                StepPattern RESULT = null;
                int ntleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ntright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Object nt = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = this.parser.createStepPattern(3, nt, null);
                Symbol CUP$XPathParser$result = new Symbol(32, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 17: {
                AncestorPattern RESULT = null;
                int spleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int spright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                StepPattern sp = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AncestorPattern(sp, rpp);
                Symbol CUP$XPathParser$result = new Symbol(31, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 16: {
                ParentPattern RESULT = null;
                int spleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int spright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                StepPattern sp = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new ParentPattern(sp, rpp);
                Symbol CUP$XPathParser$result = new Symbol(31, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 15: {
                StepPattern sp;
                StepPattern RESULT = null;
                int spleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int spright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = sp = (StepPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(31, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 14: {
                ProcessingInstructionPattern RESULT = null;
                int lleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int lright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                String l = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                RESULT = new ProcessingInstructionPattern(l);
                Symbol CUP$XPathParser$result = new Symbol(30, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 13: {
                KeyPattern RESULT = null;
                int l1left = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left;
                int l1right = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).right;
                String l1 = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).value;
                int l2left = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int l2right = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                String l2 = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                RESULT = new KeyPattern(l1, l2);
                Symbol CUP$XPathParser$result = new Symbol(27, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 5))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 12: {
                IdPattern RESULT = null;
                int lleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int lright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                String l = (String)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                RESULT = new IdPattern(l);
                this.parser.setHasIdCall(true);
                Symbol CUP$XPathParser$result = new Symbol(27, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 3))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 11: {
                RelativePathPattern rpp;
                RelativePathPattern RESULT = null;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 10: {
                AncestorPattern RESULT = null;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AncestorPattern(rpp);
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 9: {
                AncestorPattern RESULT = null;
                int ikpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int ikpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                IdKeyPattern ikp = (IdKeyPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AncestorPattern(ikp, rpp);
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 8: {
                ParentPattern RESULT = null;
                int ikpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int ikpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                IdKeyPattern ikp = (IdKeyPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new ParentPattern(ikp, rpp);
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 7: {
                IdKeyPattern ikp;
                IdKeyPattern RESULT = null;
                int ikpleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int ikpright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = ikp = (IdKeyPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 6: {
                AbsolutePathPattern RESULT = null;
                int rppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int rppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RelativePathPattern rpp = (RelativePathPattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AbsolutePathPattern(rpp);
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 5: {
                AbsolutePathPattern RESULT = null;
                RESULT = new AbsolutePathPattern(null);
                Symbol CUP$XPathParser$result = new Symbol(29, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 4: {
                AlternativePattern RESULT = null;
                int lppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left;
                int lppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).right;
                Pattern lpp = (Pattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).value;
                int pleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int pright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                Pattern p = (Pattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                RESULT = new AlternativePattern(lpp, p);
                Symbol CUP$XPathParser$result = new Symbol(28, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 2))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 3: {
                Pattern lpp;
                Pattern RESULT = null;
                int lppleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int lppright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = lpp = (Pattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(28, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 2: {
                Expression expr;
                Expression RESULT = null;
                int exprleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int exprright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = expr = (Expression)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(1, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 1: {
                Pattern pattern;
                Pattern RESULT = null;
                int patternleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).left;
                int patternright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right;
                RESULT = pattern = (Pattern)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).value;
                Symbol CUP$XPathParser$result = new Symbol(1, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                return CUP$XPathParser$result;
            }
            case 0: {
                SyntaxTreeNode start_val;
                SyntaxTreeNode RESULT = null;
                int start_valleft = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left;
                int start_valright = ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).right;
                RESULT = start_val = (SyntaxTreeNode)((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).value;
                Symbol CUP$XPathParser$result = new Symbol(0, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 1))).left, ((Symbol)CUP$XPathParser$stack.elementAt((int)(CUP$XPathParser$top - 0))).right, RESULT);
                CUP$XPathParser$parser.done_parsing();
                return CUP$XPathParser$result;
            }
        }
        throw new Exception("Invalid action number found in internal parse table");
    }
}

