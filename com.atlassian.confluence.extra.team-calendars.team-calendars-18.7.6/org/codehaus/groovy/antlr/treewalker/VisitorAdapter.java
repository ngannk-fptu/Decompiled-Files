/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.Visitor;

public class VisitorAdapter
implements Visitor {
    @Override
    public void setUp() {
    }

    @Override
    public void visitAbstract(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotation(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotations(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotationArrayInit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotationDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotationFieldDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAnnotationMemberValuePair(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitArrayDeclarator(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitAt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBand(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBandAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBigSuffix(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBlock(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBnot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBorAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBsr(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBsrAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBxor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitBxorAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitCaseGroup(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitClosedBlock(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitClosureOp(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitClosureList(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitColon(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitComma(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitCompareTo(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitCtorCall(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitCtorIdent(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDec(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDigit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDiv(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDivAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDollar(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDynamicMember(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitElist(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEmptyStat(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEnumConstantDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEnumDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEof(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEqual(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitEsc(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitExponent(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitExpr(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitExtendsClause(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitFinal(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitFloatSuffix(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitForCondition(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitForEachClause(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitForInit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitForInIterable(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitForIterator(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitGe(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitGt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitHexDigit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitIdent(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitImplementsClause(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitImplicitParameters(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitImport(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitInc(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitIndexOp(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitInstanceInit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitInterfaceDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLabeledArg(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLabeledStat(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLand(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLbrack(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLcurly(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLe(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLetter(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitListConstructor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralAs(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralAssert(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralBoolean(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralBreak(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralByte(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralCase(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralCatch(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralChar(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralClass(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralContinue(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralDefault(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralDouble(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralElse(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralEnum(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralExtends(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralFalse(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralFinally(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralFloat(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralFor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralIf(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralImplements(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralImport(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralIn(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralInstanceof(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralInt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralInterface(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralLong(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralNative(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralNew(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralNull(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralPackage(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralPrivate(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralProtected(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralPublic(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralReturn(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralShort(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralStatic(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralSuper(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralSwitch(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralSynchronized(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralThis(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralThreadsafe(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralThrow(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralThrows(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralTransient(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralTrue(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralTry(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralVoid(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralVolatile(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLiteralWhile(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLnot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLparen(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitLt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMapConstructor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMemberPointer(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMethodCall(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMethodDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMinus(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMinusAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMlComment(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMod(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitModifiers(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitModAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMultiCatch(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitMultiCatchTypes(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNls(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNotEqual(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNullTreeLookahead(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumBigDecimal(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumBigInt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumDouble(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumFloat(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumInt(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitNumLong(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitObjblock(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitOneNl(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitOptionalDot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitPackageDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitParameters(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitParameterDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitPlus(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitPlusAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitPostDec(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitPostInc(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitQuestion(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRangeExclusive(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRangeInclusive(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRbrack(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRcurly(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRegexpCtorEnd(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRegexpLiteral(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRegexpSymbol(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRegexFind(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRegexMatch(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitRparen(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSelectSlot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSemi(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitShComment(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSl(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSlist(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSlAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSlComment(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSpreadArg(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSpreadDot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSpreadMapArg(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSr(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSrAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStar(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStarAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStarStar(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStarStarAssign(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStaticImport(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStaticInit(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStrictfp(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringCh(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringConstructor(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringCtorEnd(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringCtorMiddle(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringCtorStart(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringLiteral(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitStringNl(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitSuperCtorCall(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTraitDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTripleDot(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitType(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypecast(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeArgument(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeArguments(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeLowerBounds(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeParameter(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeParameters(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitTypeUpperBounds(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitUnaryMinus(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitUnaryPlus(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitUnusedConst(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitUnusedDo(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitUnusedGoto(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitVariableDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitVariableParameterDef(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitVocab(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitWildcardType(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitWs(GroovySourceAST t, int visit) {
        this.visitDefault(t, visit);
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
    }

    @Override
    public void tearDown() {
    }

    @Override
    public void push(GroovySourceAST t) {
    }

    @Override
    public GroovySourceAST pop() {
        return null;
    }
}

