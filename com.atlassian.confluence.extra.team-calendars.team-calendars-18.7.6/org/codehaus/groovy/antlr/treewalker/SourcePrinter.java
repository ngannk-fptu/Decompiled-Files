/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import groovyjarjarantlr.collections.AST;
import java.io.PrintStream;
import java.util.Stack;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.VisitorAdapter;

public class SourcePrinter
extends VisitorAdapter {
    private final String[] tokenNames;
    private int tabLevel;
    private int lastLinePrinted;
    private final boolean newLines;
    protected final PrintStream out;
    private String className;
    private final Stack stack;
    private int stringConstructorCounter;

    public SourcePrinter(PrintStream out, String[] tokenNames) {
        this(out, tokenNames, true);
    }

    public SourcePrinter(PrintStream out, String[] tokenNames, boolean newLines) {
        this.tokenNames = tokenNames;
        this.tabLevel = 0;
        this.lastLinePrinted = 0;
        this.out = out;
        this.newLines = newLines;
        this.stack = new Stack();
    }

    @Override
    public void visitAbstract(GroovySourceAST t, int visit) {
        this.print(t, visit, "abstract ", null, null);
    }

    @Override
    public void visitAnnotation(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.print(t, visit, "@");
        }
        if (visit == 2) {
            this.print(t, visit, "(");
        }
        if (visit == 3) {
            this.print(t, visit, ", ");
        }
        if (visit == 4) {
            if (t.getNumberOfChildren() > 1) {
                this.print(t, visit, ") ");
            } else {
                this.print(t, visit, " ");
            }
        }
    }

    @Override
    public void visitAnnotations(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitAnnotationDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "@interface ", null, null);
    }

    @Override
    public void visitAnnotationFieldDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "() ", "default ", null);
    }

    @Override
    public void visitAnnotationMemberValuePair(GroovySourceAST t, int visit) {
        this.print(t, visit, " = ", null, null);
    }

    @Override
    public void visitArrayDeclarator(GroovySourceAST t, int visit) {
        if (this.getParentNode().getType() == 12 || this.getParentNode().getType() == 23) {
            this.print(t, visit, null, null, "[]");
        } else {
            this.print(t, visit, "[", null, "]");
        }
    }

    @Override
    public void visitAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " = ", null, null);
    }

    @Override
    public void visitBand(GroovySourceAST t, int visit) {
        this.print(t, visit, " & ", null, null);
    }

    @Override
    public void visitBandAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " &= ", null, null);
    }

    @Override
    public void visitBnot(GroovySourceAST t, int visit) {
        this.print(t, visit, "~", null, null);
    }

    @Override
    public void visitBor(GroovySourceAST t, int visit) {
        this.print(t, visit, " | ", null, null);
    }

    @Override
    public void visitBorAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " |= ", null, null);
    }

    @Override
    public void visitBsr(GroovySourceAST t, int visit) {
        this.print(t, visit, " >>> ", null, null);
    }

    @Override
    public void visitBsrAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " >>>= ", null, null);
    }

    @Override
    public void visitBxor(GroovySourceAST t, int visit) {
        this.print(t, visit, " ^ ", null, null);
    }

    @Override
    public void visitBxorAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " ^= ", null, null);
    }

    @Override
    public void visitCaseGroup(GroovySourceAST t, int visit) {
        if (visit == 1) {
            ++this.tabLevel;
        }
        if (visit == 4) {
            --this.tabLevel;
        }
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "class ", null, null);
        if (visit == 1) {
            this.className = t.childOfType(87).getText();
        }
    }

    @Override
    public void visitClosedBlock(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "{", "-> ", "}");
    }

    @Override
    public void visitClosureList(GroovySourceAST t, int visit) {
        this.print(t, visit, "(", "; ", ")");
    }

    @Override
    public void visitCompareTo(GroovySourceAST t, int visit) {
        this.print(t, visit, " <=> ", null, null);
    }

    @Override
    public void visitCtorCall(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "this(", " ", ")");
    }

    @Override
    public void visitCtorIdent(GroovySourceAST t, int visit) {
        this.print(t, visit, this.className, null, null);
    }

    @Override
    public void visitDec(GroovySourceAST t, int visit) {
        this.print(t, visit, "--", null, null);
    }

    @Override
    public void visitDiv(GroovySourceAST t, int visit) {
        this.print(t, visit, " / ", null, null);
    }

    @Override
    public void visitDivAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " /= ", null, null);
    }

    @Override
    public void visitDot(GroovySourceAST t, int visit) {
        this.print(t, visit, ".", null, null);
    }

    @Override
    public void visitDynamicMember(GroovySourceAST t, int visit) {
        if (t.childOfType(48) == null) {
            this.printUpdatingTabLevel(t, visit, "(", null, ")");
        }
    }

    @Override
    public void visitElist(GroovySourceAST t, int visit) {
        if (this.getParentNode().getType() == 62) {
            this.print(t, visit, "(", ", ", ")");
        } else {
            this.print(t, visit, null, ", ", null);
        }
    }

    @Override
    public void visitEnumConstantDef(GroovySourceAST t, int visit) {
        GroovySourceAST sibling = (GroovySourceAST)t.getNextSibling();
        if (sibling != null && sibling.getType() == 62) {
            this.print(t, visit, null, null, ", ");
        }
    }

    @Override
    public void visitEnumDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "enum ", null, null);
    }

    @Override
    public void visitEqual(GroovySourceAST t, int visit) {
        this.print(t, visit, " == ", null, null);
    }

    @Override
    public void visitExpr(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitExtendsClause(GroovySourceAST t, int visit) {
        if (visit == 1 && t.getNumberOfChildren() != 0) {
            this.print(t, visit, " extends ");
        }
    }

    @Override
    public void visitFinal(GroovySourceAST t, int visit) {
        this.print(t, visit, "final ", null, null);
    }

    @Override
    public void visitForCondition(GroovySourceAST t, int visit) {
        this.print(t, visit, " ; ", null, null);
    }

    @Override
    public void visitForInit(GroovySourceAST t, int visit) {
        this.print(t, visit, "(", null, null);
    }

    @Override
    public void visitForInIterable(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "(", " in ", ") ");
    }

    @Override
    public void visitForIterator(GroovySourceAST t, int visit) {
        this.print(t, visit, " ; ", null, ")");
    }

    @Override
    public void visitGe(GroovySourceAST t, int visit) {
        this.print(t, visit, " >= ", null, null);
    }

    @Override
    public void visitGt(GroovySourceAST t, int visit) {
        this.print(t, visit, " > ", null, null);
    }

    @Override
    public void visitIdent(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitImplementsClause(GroovySourceAST t, int visit) {
        if (visit == 1 && t.getNumberOfChildren() != 0) {
            this.print(t, visit, " implements ");
        }
        if (visit == 4) {
            this.print(t, visit, " ");
        }
    }

    @Override
    public void visitImplicitParameters(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitImport(GroovySourceAST t, int visit) {
        this.print(t, visit, "import ", null, null);
    }

    @Override
    public void visitInc(GroovySourceAST t, int visit) {
        this.print(t, visit, "++", null, null);
    }

    @Override
    public void visitIndexOp(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "[", null, "]");
    }

    @Override
    public void visitInterfaceDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "interface ", null, null);
    }

    @Override
    public void visitInstanceInit(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitLabeledArg(GroovySourceAST t, int visit) {
        this.print(t, visit, ":", null, null);
    }

    @Override
    public void visitLabeledStat(GroovySourceAST t, int visit) {
        this.print(t, visit, ":", null, null);
    }

    @Override
    public void visitLand(GroovySourceAST t, int visit) {
        this.print(t, visit, " && ", null, null);
    }

    @Override
    public void visitLe(GroovySourceAST t, int visit) {
        this.print(t, visit, " <= ", null, null);
    }

    @Override
    public void visitListConstructor(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "[", null, "]");
    }

    @Override
    public void visitLiteralAs(GroovySourceAST t, int visit) {
        this.print(t, visit, " as ", null, null);
    }

    @Override
    public void visitLiteralAssert(GroovySourceAST t, int visit) {
        if (t.getNumberOfChildren() > 1) {
            this.print(t, visit, "assert ", null, " : ");
        } else {
            this.print(t, visit, "assert ", null, null);
        }
    }

    @Override
    public void visitLiteralBoolean(GroovySourceAST t, int visit) {
        this.print(t, visit, "boolean", null, null);
    }

    @Override
    public void visitLiteralBreak(GroovySourceAST t, int visit) {
        this.print(t, visit, "break ", null, null);
    }

    @Override
    public void visitLiteralByte(GroovySourceAST t, int visit) {
        this.print(t, visit, "byte", null, null);
    }

    @Override
    public void visitLiteralCase(GroovySourceAST t, int visit) {
        this.print(t, visit, "case ", null, ":");
    }

    @Override
    public void visitLiteralCatch(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, " catch (", null, ") ");
    }

    @Override
    public void visitLiteralChar(GroovySourceAST t, int visit) {
        this.print(t, visit, "char", null, null);
    }

    @Override
    public void visitLiteralContinue(GroovySourceAST t, int visit) {
        this.print(t, visit, "continue ", null, null);
    }

    @Override
    public void visitLiteralDefault(GroovySourceAST t, int visit) {
        this.print(t, visit, "default", null, ":");
    }

    @Override
    public void visitLiteralDouble(GroovySourceAST t, int visit) {
        this.print(t, visit, "double", null, null);
    }

    @Override
    public void visitLiteralFalse(GroovySourceAST t, int visit) {
        this.print(t, visit, "false", null, null);
    }

    @Override
    public void visitLiteralFinally(GroovySourceAST t, int visit) {
        this.print(t, visit, "finally ", null, null);
    }

    @Override
    public void visitLiteralFloat(GroovySourceAST t, int visit) {
        this.print(t, visit, "float", null, null);
    }

    @Override
    public void visitLiteralFor(GroovySourceAST t, int visit) {
        this.print(t, visit, "for ", null, null);
    }

    @Override
    public void visitLiteralIf(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "if (", " else ", ") ");
    }

    @Override
    public void visitLiteralIn(GroovySourceAST t, int visit) {
        this.print(t, visit, " in ", null, null);
    }

    @Override
    public void visitLiteralInstanceof(GroovySourceAST t, int visit) {
        this.print(t, visit, " instanceof ", null, null);
    }

    @Override
    public void visitLiteralInt(GroovySourceAST t, int visit) {
        this.print(t, visit, "int", null, null);
    }

    @Override
    public void visitLiteralLong(GroovySourceAST t, int visit) {
        this.print(t, visit, "long", null, null);
    }

    @Override
    public void visitLiteralNative(GroovySourceAST t, int visit) {
        this.print(t, visit, "native ", null, null);
    }

    @Override
    public void visitLiteralNew(GroovySourceAST t, int visit) {
        if (t.childOfType(17) == null) {
            this.print(t, visit, "new ", "(", ")");
        } else {
            this.print(t, visit, "new ", null, null);
        }
    }

    @Override
    public void visitLiteralNull(GroovySourceAST t, int visit) {
        this.print(t, visit, "null", null, null);
    }

    @Override
    public void visitLiteralPrivate(GroovySourceAST t, int visit) {
        this.print(t, visit, "private ", null, null);
    }

    @Override
    public void visitLiteralProtected(GroovySourceAST t, int visit) {
        this.print(t, visit, "protected ", null, null);
    }

    @Override
    public void visitLiteralPublic(GroovySourceAST t, int visit) {
        this.print(t, visit, "public ", null, null);
    }

    @Override
    public void visitLiteralReturn(GroovySourceAST t, int visit) {
        this.print(t, visit, "return ", null, null);
    }

    @Override
    public void visitLiteralShort(GroovySourceAST t, int visit) {
        this.print(t, visit, "short", null, null);
    }

    @Override
    public void visitLiteralStatic(GroovySourceAST t, int visit) {
        this.print(t, visit, "static ", null, null);
    }

    @Override
    public void visitLiteralSuper(GroovySourceAST t, int visit) {
        this.print(t, visit, "super", null, null);
    }

    @Override
    public void visitLiteralSwitch(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.print(t, visit, "switch (");
            ++this.tabLevel;
        }
        if (visit == 3) {
            this.print(t, visit, ") {");
        }
        if (visit == 4) {
            --this.tabLevel;
            this.print(t, visit, "}");
        }
    }

    @Override
    public void visitLiteralSynchronized(GroovySourceAST t, int visit) {
        if (t.getNumberOfChildren() > 0) {
            this.print(t, visit, "synchronized (", null, ") ");
        } else {
            this.print(t, visit, "synchronized ", null, null);
        }
    }

    @Override
    public void visitLiteralThis(GroovySourceAST t, int visit) {
        this.print(t, visit, "this", null, null);
    }

    @Override
    public void visitLiteralThreadsafe(GroovySourceAST t, int visit) {
        this.print(t, visit, "threadsafe ", null, null);
    }

    @Override
    public void visitLiteralThrow(GroovySourceAST t, int visit) {
        this.print(t, visit, "throw ", null, null);
    }

    @Override
    public void visitLiteralThrows(GroovySourceAST t, int visit) {
        this.print(t, visit, "throws ", null, null);
    }

    @Override
    public void visitLiteralTransient(GroovySourceAST t, int visit) {
        this.print(t, visit, "transient ", null, null);
    }

    @Override
    public void visitLiteralTrue(GroovySourceAST t, int visit) {
        this.print(t, visit, "true", null, null);
    }

    @Override
    public void visitLiteralTry(GroovySourceAST t, int visit) {
        this.print(t, visit, "try ", null, null);
    }

    @Override
    public void visitLiteralVoid(GroovySourceAST t, int visit) {
        this.print(t, visit, "void", null, null);
    }

    @Override
    public void visitLiteralVolatile(GroovySourceAST t, int visit) {
        this.print(t, visit, "volatile ", null, null);
    }

    @Override
    public void visitLiteralWhile(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "while (", null, ") ");
    }

    @Override
    public void visitLnot(GroovySourceAST t, int visit) {
        this.print(t, visit, "!", null, null);
    }

    @Override
    public void visitLor(GroovySourceAST t, int visit) {
        this.print(t, visit, " || ", null, null);
    }

    @Override
    public void visitLt(GroovySourceAST t, int visit) {
        this.print(t, visit, " < ", null, null);
    }

    @Override
    public void visitMapConstructor(GroovySourceAST t, int visit) {
        if (t.getNumberOfChildren() == 0) {
            this.print(t, visit, "[:]", null, null);
        } else {
            this.printUpdatingTabLevel(t, visit, "[", null, "]");
        }
    }

    @Override
    public void visitMemberPointer(GroovySourceAST t, int visit) {
        this.print(t, visit, ".&", null, null);
    }

    @Override
    public void visitMethodCall(GroovySourceAST t, int visit) {
        if ("<command>".equals(t.getText())) {
            this.printUpdatingTabLevel(t, visit, " ", " ", null);
        } else {
            this.printUpdatingTabLevel(t, visit, "(", " ", ")");
        }
    }

    @Override
    public void visitMethodDef(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitMinus(GroovySourceAST t, int visit) {
        this.print(t, visit, " - ", null, null);
    }

    @Override
    public void visitMinusAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " -= ", null, null);
    }

    @Override
    public void visitMod(GroovySourceAST t, int visit) {
        this.print(t, visit, " % ", null, null);
    }

    @Override
    public void visitModifiers(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitModAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " %= ", null, null);
    }

    @Override
    public void visitMultiCatch(GroovySourceAST t, int visit) {
        if (visit == 4) {
            AST child = t.getFirstChild();
            if ("MULTICATCH_TYPES".equals(child.getText())) {
                this.print(t, visit, null, null, " " + child.getNextSibling().getText());
            } else {
                this.print(t, visit, null, null, " " + child.getFirstChild().getText());
            }
        }
    }

    @Override
    public void visitMultiCatchTypes(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitNotEqual(GroovySourceAST t, int visit) {
        this.print(t, visit, " != ", null, null);
    }

    @Override
    public void visitNumBigDecimal(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitNumBigInt(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitNumDouble(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitNumInt(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitNumFloat(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitNumLong(GroovySourceAST t, int visit) {
        this.print(t, visit, t.getText(), null, null);
    }

    @Override
    public void visitObjblock(GroovySourceAST t, int visit) {
        if (visit == 1) {
            ++this.tabLevel;
            this.print(t, visit, "{");
        } else {
            --this.tabLevel;
            this.print(t, visit, "}");
        }
    }

    @Override
    public void visitOptionalDot(GroovySourceAST t, int visit) {
        this.print(t, visit, "?.", null, null);
    }

    @Override
    public void visitPackageDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "package ", null, null);
    }

    @Override
    public void visitParameterDef(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitParameters(GroovySourceAST t, int visit) {
        if (this.getParentNode().getType() == 50) {
            this.printUpdatingTabLevel(t, visit, null, ",", " ");
        } else {
            this.printUpdatingTabLevel(t, visit, "(", ", ", ") ");
        }
    }

    @Override
    public void visitPlus(GroovySourceAST t, int visit) {
        this.print(t, visit, " + ", null, null);
    }

    @Override
    public void visitPlusAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " += ", null, null);
    }

    @Override
    public void visitPostDec(GroovySourceAST t, int visit) {
        this.print(t, visit, null, null, "--");
    }

    @Override
    public void visitPostInc(GroovySourceAST t, int visit) {
        this.print(t, visit, null, null, "++");
    }

    @Override
    public void visitQuestion(GroovySourceAST t, int visit) {
        this.print(t, visit, "?", ":", null);
    }

    @Override
    public void visitRangeExclusive(GroovySourceAST t, int visit) {
        this.print(t, visit, "..<", null, null);
    }

    @Override
    public void visitRangeInclusive(GroovySourceAST t, int visit) {
        this.print(t, visit, "..", null, null);
    }

    @Override
    public void visitRegexFind(GroovySourceAST t, int visit) {
        this.print(t, visit, " =~ ", null, null);
    }

    @Override
    public void visitRegexMatch(GroovySourceAST t, int visit) {
        this.print(t, visit, " ==~ ", null, null);
    }

    @Override
    public void visitSelectSlot(GroovySourceAST t, int visit) {
        this.print(t, visit, "@", null, null);
    }

    @Override
    public void visitSl(GroovySourceAST t, int visit) {
        this.print(t, visit, " << ", null, null);
    }

    @Override
    public void visitSlAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " <<= ", null, null);
    }

    @Override
    public void visitSlist(GroovySourceAST t, int visit) {
        if (visit == 1) {
            ++this.tabLevel;
            this.print(t, visit, "{");
        } else {
            --this.tabLevel;
            this.print(t, visit, "}");
        }
    }

    @Override
    public void visitSpreadArg(GroovySourceAST t, int visit) {
        this.print(t, visit, "*", null, null);
    }

    @Override
    public void visitSpreadDot(GroovySourceAST t, int visit) {
        this.print(t, visit, "*.", null, null);
    }

    @Override
    public void visitSpreadMapArg(GroovySourceAST t, int visit) {
        this.print(t, visit, "*:", null, null);
    }

    @Override
    public void visitSr(GroovySourceAST t, int visit) {
        this.print(t, visit, " >> ", null, null);
    }

    @Override
    public void visitSrAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " >>= ", null, null);
    }

    @Override
    public void visitStar(GroovySourceAST t, int visit) {
        this.print(t, visit, "*", null, null);
    }

    @Override
    public void visitStarAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " *= ", null, null);
    }

    @Override
    public void visitStarStar(GroovySourceAST t, int visit) {
        this.print(t, visit, "**", null, null);
    }

    @Override
    public void visitStarStarAssign(GroovySourceAST t, int visit) {
        this.print(t, visit, " **= ", null, null);
    }

    @Override
    public void visitStaticInit(GroovySourceAST t, int visit) {
        this.print(t, visit, "static ", null, null);
    }

    @Override
    public void visitStaticImport(GroovySourceAST t, int visit) {
        this.print(t, visit, "import static ", null, null);
    }

    @Override
    public void visitStrictfp(GroovySourceAST t, int visit) {
        this.print(t, visit, "strictfp ", null, null);
    }

    @Override
    public void visitStringConstructor(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.stringConstructorCounter = 0;
            this.print(t, visit, "\"");
        }
        if (visit == 3) {
            if (this.stringConstructorCounter % 2 == 0) {
                this.print(t, visit, "$");
            }
            ++this.stringConstructorCounter;
        }
        if (visit == 4) {
            this.print(t, visit, "\"");
        }
    }

    @Override
    public void visitStringLiteral(GroovySourceAST t, int visit) {
        if (visit == 1) {
            String theString = SourcePrinter.escape(t.getText());
            if (this.getParentNode().getType() != 54 && this.getParentNode().getType() != 48) {
                theString = "\"" + theString + "\"";
            }
            this.print(t, visit, theString);
        }
    }

    private static String escape(String literal) {
        literal = literal.replaceAll("\n", "\\\\<<REMOVE>>n");
        literal = literal.replaceAll("<<REMOVE>>", "");
        return literal;
    }

    @Override
    public void visitSuperCtorCall(GroovySourceAST t, int visit) {
        this.printUpdatingTabLevel(t, visit, "super(", " ", ")");
    }

    @Override
    public void visitTraitDef(GroovySourceAST t, int visit) {
        this.print(t, visit, "trait ", null, null);
        if (visit == 1) {
            this.className = t.childOfType(87).getText();
        }
    }

    @Override
    public void visitType(GroovySourceAST t, int visit) {
        GroovySourceAST parent = this.getParentNode();
        GroovySourceAST modifiers = parent.childOfType(5);
        if (modifiers == null || modifiers.getNumberOfChildren() == 0) {
            if (visit == 1 && t.getNumberOfChildren() == 0 && parent.getType() != 21) {
                this.print(t, visit, "def");
            }
            if (visit == 4 && (parent.getType() == 9 || parent.getType() == 8 || parent.getType() == 68 || parent.getType() == 21 && t.getNumberOfChildren() != 0)) {
                this.print(t, visit, " ");
            }
        } else if (visit == 4 && t.getNumberOfChildren() != 0) {
            this.print(t, visit, " ");
        }
    }

    @Override
    public void visitTypeArgument(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitTypeArguments(GroovySourceAST t, int visit) {
        this.print(t, visit, "<", ", ", ">");
    }

    @Override
    public void visitTypecast(GroovySourceAST t, int visit) {
        this.print(t, visit, "(", null, ")");
    }

    @Override
    public void visitTypeLowerBounds(GroovySourceAST t, int visit) {
        this.print(t, visit, " super ", " & ", null);
    }

    @Override
    public void visitTypeParameter(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitTypeParameters(GroovySourceAST t, int visit) {
        this.print(t, visit, "<", ", ", ">");
    }

    @Override
    public void visitTypeUpperBounds(GroovySourceAST t, int visit) {
        this.print(t, visit, " extends ", " & ", null);
    }

    @Override
    public void visitUnaryMinus(GroovySourceAST t, int visit) {
        this.print(t, visit, "-", null, null);
    }

    @Override
    public void visitUnaryPlus(GroovySourceAST t, int visit) {
        this.print(t, visit, "+", null, null);
    }

    @Override
    public void visitVariableDef(GroovySourceAST t, int visit) {
    }

    @Override
    public void visitVariableParameterDef(GroovySourceAST t, int visit) {
        this.print(t, visit, null, "... ", null);
    }

    @Override
    public void visitWildcardType(GroovySourceAST t, int visit) {
        this.print(t, visit, "?", null, null);
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        if (visit == 1) {
            this.print(t, visit, "<" + this.tokenNames[t.getType()] + ">");
        } else {
            this.print(t, visit, "</" + this.tokenNames[t.getType()] + ">");
        }
    }

    protected void printUpdatingTabLevel(GroovySourceAST t, int visit, String opening, String subsequent, String closing) {
        if (visit == 1 && opening != null) {
            this.print(t, visit, opening);
            ++this.tabLevel;
        }
        if (visit == 3 && subsequent != null) {
            this.print(t, visit, subsequent);
        }
        if (visit == 4 && closing != null) {
            --this.tabLevel;
            this.print(t, visit, closing);
        }
    }

    protected void print(GroovySourceAST t, int visit, String opening, String subsequent, String closing) {
        if (visit == 1 && opening != null) {
            this.print(t, visit, opening);
        }
        if (visit == 3 && subsequent != null) {
            this.print(t, visit, subsequent);
        }
        if (visit == 4 && closing != null) {
            this.print(t, visit, closing);
        }
    }

    protected void print(GroovySourceAST t, int visit, String value) {
        if (visit == 1) {
            this.printNewlineAndIndent(t, visit);
        }
        if (visit == 4) {
            this.printNewlineAndIndent(t, visit);
        }
        this.out.print(value);
    }

    protected void printNewlineAndIndent(GroovySourceAST t, int visit) {
        int currentLine = t.getLine();
        if (this.lastLinePrinted == 0) {
            this.lastLinePrinted = currentLine;
        }
        if (this.lastLinePrinted != currentLine) {
            if (this.newLines && (visit != 1 || t.getType() != 7)) {
                int i;
                for (i = this.lastLinePrinted; i < currentLine; ++i) {
                    this.out.println();
                }
                if (this.lastLinePrinted > currentLine) {
                    this.out.println();
                    this.lastLinePrinted = currentLine;
                }
                if (visit == 1 || visit == 4 && this.lastLinePrinted > currentLine) {
                    for (i = 0; i < this.tabLevel; ++i) {
                        this.out.print("    ");
                    }
                }
            }
            this.lastLinePrinted = Math.max(currentLine, this.lastLinePrinted);
        }
    }

    @Override
    public void push(GroovySourceAST t) {
        this.stack.push(t);
    }

    @Override
    public GroovySourceAST pop() {
        if (!this.stack.empty()) {
            return (GroovySourceAST)this.stack.pop();
        }
        return null;
    }

    private GroovySourceAST getParentNode() {
        Object currentNode = this.stack.pop();
        Object parentNode = this.stack.peek();
        this.stack.push(currentNode);
        return (GroovySourceAST)parentNode;
    }
}

