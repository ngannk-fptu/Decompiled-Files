/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import org.codehaus.groovy.antlr.GroovySourceAST;

public interface Visitor {
    public static final int OPENING_VISIT = 1;
    public static final int SECOND_VISIT = 2;
    public static final int SUBSEQUENT_VISIT = 3;
    public static final int CLOSING_VISIT = 4;

    public void setUp();

    public void visitAbstract(GroovySourceAST var1, int var2);

    public void visitAnnotation(GroovySourceAST var1, int var2);

    public void visitAnnotations(GroovySourceAST var1, int var2);

    public void visitAnnotationArrayInit(GroovySourceAST var1, int var2);

    public void visitAnnotationDef(GroovySourceAST var1, int var2);

    public void visitAnnotationFieldDef(GroovySourceAST var1, int var2);

    public void visitAnnotationMemberValuePair(GroovySourceAST var1, int var2);

    public void visitArrayDeclarator(GroovySourceAST var1, int var2);

    public void visitAssign(GroovySourceAST var1, int var2);

    public void visitAt(GroovySourceAST var1, int var2);

    public void visitBand(GroovySourceAST var1, int var2);

    public void visitBandAssign(GroovySourceAST var1, int var2);

    public void visitBigSuffix(GroovySourceAST var1, int var2);

    public void visitBlock(GroovySourceAST var1, int var2);

    public void visitBnot(GroovySourceAST var1, int var2);

    public void visitBor(GroovySourceAST var1, int var2);

    public void visitBorAssign(GroovySourceAST var1, int var2);

    public void visitBsr(GroovySourceAST var1, int var2);

    public void visitBsrAssign(GroovySourceAST var1, int var2);

    public void visitBxor(GroovySourceAST var1, int var2);

    public void visitBxorAssign(GroovySourceAST var1, int var2);

    public void visitCaseGroup(GroovySourceAST var1, int var2);

    public void visitClassDef(GroovySourceAST var1, int var2);

    public void visitClosedBlock(GroovySourceAST var1, int var2);

    public void visitClosureList(GroovySourceAST var1, int var2);

    public void visitClosureOp(GroovySourceAST var1, int var2);

    public void visitColon(GroovySourceAST var1, int var2);

    public void visitComma(GroovySourceAST var1, int var2);

    public void visitCompareTo(GroovySourceAST var1, int var2);

    public void visitCtorCall(GroovySourceAST var1, int var2);

    public void visitCtorIdent(GroovySourceAST var1, int var2);

    public void visitDec(GroovySourceAST var1, int var2);

    public void visitDigit(GroovySourceAST var1, int var2);

    public void visitDiv(GroovySourceAST var1, int var2);

    public void visitDivAssign(GroovySourceAST var1, int var2);

    public void visitDollar(GroovySourceAST var1, int var2);

    public void visitDot(GroovySourceAST var1, int var2);

    public void visitDynamicMember(GroovySourceAST var1, int var2);

    public void visitElist(GroovySourceAST var1, int var2);

    public void visitEmptyStat(GroovySourceAST var1, int var2);

    public void visitEnumConstantDef(GroovySourceAST var1, int var2);

    public void visitEnumDef(GroovySourceAST var1, int var2);

    public void visitEof(GroovySourceAST var1, int var2);

    public void visitEqual(GroovySourceAST var1, int var2);

    public void visitEsc(GroovySourceAST var1, int var2);

    public void visitExponent(GroovySourceAST var1, int var2);

    public void visitExpr(GroovySourceAST var1, int var2);

    public void visitExtendsClause(GroovySourceAST var1, int var2);

    public void visitFinal(GroovySourceAST var1, int var2);

    public void visitFloatSuffix(GroovySourceAST var1, int var2);

    public void visitForCondition(GroovySourceAST var1, int var2);

    public void visitForEachClause(GroovySourceAST var1, int var2);

    public void visitForInit(GroovySourceAST var1, int var2);

    public void visitForInIterable(GroovySourceAST var1, int var2);

    public void visitForIterator(GroovySourceAST var1, int var2);

    public void visitGe(GroovySourceAST var1, int var2);

    public void visitGt(GroovySourceAST var1, int var2);

    public void visitHexDigit(GroovySourceAST var1, int var2);

    public void visitIdent(GroovySourceAST var1, int var2);

    public void visitImplementsClause(GroovySourceAST var1, int var2);

    public void visitImplicitParameters(GroovySourceAST var1, int var2);

    public void visitImport(GroovySourceAST var1, int var2);

    public void visitInc(GroovySourceAST var1, int var2);

    public void visitIndexOp(GroovySourceAST var1, int var2);

    public void visitInstanceInit(GroovySourceAST var1, int var2);

    public void visitInterfaceDef(GroovySourceAST var1, int var2);

    public void visitLabeledArg(GroovySourceAST var1, int var2);

    public void visitLabeledStat(GroovySourceAST var1, int var2);

    public void visitLand(GroovySourceAST var1, int var2);

    public void visitLbrack(GroovySourceAST var1, int var2);

    public void visitLcurly(GroovySourceAST var1, int var2);

    public void visitLe(GroovySourceAST var1, int var2);

    public void visitLetter(GroovySourceAST var1, int var2);

    public void visitListConstructor(GroovySourceAST var1, int var2);

    public void visitLiteralAs(GroovySourceAST var1, int var2);

    public void visitLiteralAssert(GroovySourceAST var1, int var2);

    public void visitLiteralBoolean(GroovySourceAST var1, int var2);

    public void visitLiteralBreak(GroovySourceAST var1, int var2);

    public void visitLiteralByte(GroovySourceAST var1, int var2);

    public void visitLiteralCase(GroovySourceAST var1, int var2);

    public void visitLiteralCatch(GroovySourceAST var1, int var2);

    public void visitLiteralChar(GroovySourceAST var1, int var2);

    public void visitLiteralClass(GroovySourceAST var1, int var2);

    public void visitLiteralContinue(GroovySourceAST var1, int var2);

    public void visitLiteralDef(GroovySourceAST var1, int var2);

    public void visitLiteralDefault(GroovySourceAST var1, int var2);

    public void visitLiteralDouble(GroovySourceAST var1, int var2);

    public void visitLiteralElse(GroovySourceAST var1, int var2);

    public void visitLiteralEnum(GroovySourceAST var1, int var2);

    public void visitLiteralExtends(GroovySourceAST var1, int var2);

    public void visitLiteralFalse(GroovySourceAST var1, int var2);

    public void visitLiteralFinally(GroovySourceAST var1, int var2);

    public void visitLiteralFloat(GroovySourceAST var1, int var2);

    public void visitLiteralFor(GroovySourceAST var1, int var2);

    public void visitLiteralIf(GroovySourceAST var1, int var2);

    public void visitLiteralImplements(GroovySourceAST var1, int var2);

    public void visitLiteralImport(GroovySourceAST var1, int var2);

    public void visitLiteralIn(GroovySourceAST var1, int var2);

    public void visitLiteralInstanceof(GroovySourceAST var1, int var2);

    public void visitLiteralInt(GroovySourceAST var1, int var2);

    public void visitLiteralInterface(GroovySourceAST var1, int var2);

    public void visitLiteralLong(GroovySourceAST var1, int var2);

    public void visitLiteralNative(GroovySourceAST var1, int var2);

    public void visitLiteralNew(GroovySourceAST var1, int var2);

    public void visitLiteralNull(GroovySourceAST var1, int var2);

    public void visitLiteralPackage(GroovySourceAST var1, int var2);

    public void visitLiteralPrivate(GroovySourceAST var1, int var2);

    public void visitLiteralProtected(GroovySourceAST var1, int var2);

    public void visitLiteralPublic(GroovySourceAST var1, int var2);

    public void visitLiteralReturn(GroovySourceAST var1, int var2);

    public void visitLiteralShort(GroovySourceAST var1, int var2);

    public void visitLiteralStatic(GroovySourceAST var1, int var2);

    public void visitLiteralSuper(GroovySourceAST var1, int var2);

    public void visitLiteralSwitch(GroovySourceAST var1, int var2);

    public void visitLiteralSynchronized(GroovySourceAST var1, int var2);

    public void visitLiteralThis(GroovySourceAST var1, int var2);

    public void visitLiteralThreadsafe(GroovySourceAST var1, int var2);

    public void visitLiteralThrow(GroovySourceAST var1, int var2);

    public void visitLiteralThrows(GroovySourceAST var1, int var2);

    public void visitLiteralTransient(GroovySourceAST var1, int var2);

    public void visitLiteralTrue(GroovySourceAST var1, int var2);

    public void visitLiteralTry(GroovySourceAST var1, int var2);

    public void visitLiteralVoid(GroovySourceAST var1, int var2);

    public void visitLiteralVolatile(GroovySourceAST var1, int var2);

    public void visitLiteralWhile(GroovySourceAST var1, int var2);

    public void visitLnot(GroovySourceAST var1, int var2);

    public void visitLor(GroovySourceAST var1, int var2);

    public void visitLparen(GroovySourceAST var1, int var2);

    public void visitLt(GroovySourceAST var1, int var2);

    public void visitMapConstructor(GroovySourceAST var1, int var2);

    public void visitMemberPointer(GroovySourceAST var1, int var2);

    public void visitMethodCall(GroovySourceAST var1, int var2);

    public void visitMethodDef(GroovySourceAST var1, int var2);

    public void visitMinus(GroovySourceAST var1, int var2);

    public void visitMinusAssign(GroovySourceAST var1, int var2);

    public void visitMlComment(GroovySourceAST var1, int var2);

    public void visitMod(GroovySourceAST var1, int var2);

    public void visitModifiers(GroovySourceAST var1, int var2);

    public void visitModAssign(GroovySourceAST var1, int var2);

    public void visitMultiCatch(GroovySourceAST var1, int var2);

    public void visitMultiCatchTypes(GroovySourceAST var1, int var2);

    public void visitNls(GroovySourceAST var1, int var2);

    public void visitNotEqual(GroovySourceAST var1, int var2);

    public void visitNullTreeLookahead(GroovySourceAST var1, int var2);

    public void visitNumBigDecimal(GroovySourceAST var1, int var2);

    public void visitNumBigInt(GroovySourceAST var1, int var2);

    public void visitNumDouble(GroovySourceAST var1, int var2);

    public void visitNumFloat(GroovySourceAST var1, int var2);

    public void visitNumInt(GroovySourceAST var1, int var2);

    public void visitNumLong(GroovySourceAST var1, int var2);

    public void visitObjblock(GroovySourceAST var1, int var2);

    public void visitOneNl(GroovySourceAST var1, int var2);

    public void visitOptionalDot(GroovySourceAST var1, int var2);

    public void visitPackageDef(GroovySourceAST var1, int var2);

    public void visitParameters(GroovySourceAST var1, int var2);

    public void visitParameterDef(GroovySourceAST var1, int var2);

    public void visitPlus(GroovySourceAST var1, int var2);

    public void visitPlusAssign(GroovySourceAST var1, int var2);

    public void visitPostDec(GroovySourceAST var1, int var2);

    public void visitPostInc(GroovySourceAST var1, int var2);

    public void visitQuestion(GroovySourceAST var1, int var2);

    public void visitRangeExclusive(GroovySourceAST var1, int var2);

    public void visitRangeInclusive(GroovySourceAST var1, int var2);

    public void visitRbrack(GroovySourceAST var1, int var2);

    public void visitRcurly(GroovySourceAST var1, int var2);

    public void visitRegexpCtorEnd(GroovySourceAST var1, int var2);

    public void visitRegexpLiteral(GroovySourceAST var1, int var2);

    public void visitRegexpSymbol(GroovySourceAST var1, int var2);

    public void visitRegexFind(GroovySourceAST var1, int var2);

    public void visitRegexMatch(GroovySourceAST var1, int var2);

    public void visitRparen(GroovySourceAST var1, int var2);

    public void visitSelectSlot(GroovySourceAST var1, int var2);

    public void visitSemi(GroovySourceAST var1, int var2);

    public void visitShComment(GroovySourceAST var1, int var2);

    public void visitSl(GroovySourceAST var1, int var2);

    public void visitSlist(GroovySourceAST var1, int var2);

    public void visitSlAssign(GroovySourceAST var1, int var2);

    public void visitSlComment(GroovySourceAST var1, int var2);

    public void visitSpreadArg(GroovySourceAST var1, int var2);

    public void visitSpreadDot(GroovySourceAST var1, int var2);

    public void visitSpreadMapArg(GroovySourceAST var1, int var2);

    public void visitSr(GroovySourceAST var1, int var2);

    public void visitSrAssign(GroovySourceAST var1, int var2);

    public void visitStar(GroovySourceAST var1, int var2);

    public void visitStarAssign(GroovySourceAST var1, int var2);

    public void visitStarStar(GroovySourceAST var1, int var2);

    public void visitStarStarAssign(GroovySourceAST var1, int var2);

    public void visitStaticImport(GroovySourceAST var1, int var2);

    public void visitStaticInit(GroovySourceAST var1, int var2);

    public void visitStrictfp(GroovySourceAST var1, int var2);

    public void visitStringCh(GroovySourceAST var1, int var2);

    public void visitStringConstructor(GroovySourceAST var1, int var2);

    public void visitStringCtorEnd(GroovySourceAST var1, int var2);

    public void visitStringCtorMiddle(GroovySourceAST var1, int var2);

    public void visitStringCtorStart(GroovySourceAST var1, int var2);

    public void visitStringLiteral(GroovySourceAST var1, int var2);

    public void visitStringNl(GroovySourceAST var1, int var2);

    public void visitSuperCtorCall(GroovySourceAST var1, int var2);

    public void visitTraitDef(GroovySourceAST var1, int var2);

    public void visitTripleDot(GroovySourceAST var1, int var2);

    public void visitType(GroovySourceAST var1, int var2);

    public void visitTypecast(GroovySourceAST var1, int var2);

    public void visitTypeArgument(GroovySourceAST var1, int var2);

    public void visitTypeArguments(GroovySourceAST var1, int var2);

    public void visitTypeLowerBounds(GroovySourceAST var1, int var2);

    public void visitTypeParameter(GroovySourceAST var1, int var2);

    public void visitTypeParameters(GroovySourceAST var1, int var2);

    public void visitTypeUpperBounds(GroovySourceAST var1, int var2);

    public void visitUnaryMinus(GroovySourceAST var1, int var2);

    public void visitUnaryPlus(GroovySourceAST var1, int var2);

    public void visitUnusedConst(GroovySourceAST var1, int var2);

    public void visitUnusedDo(GroovySourceAST var1, int var2);

    public void visitUnusedGoto(GroovySourceAST var1, int var2);

    public void visitVariableDef(GroovySourceAST var1, int var2);

    public void visitVariableParameterDef(GroovySourceAST var1, int var2);

    public void visitVocab(GroovySourceAST var1, int var2);

    public void visitWildcardType(GroovySourceAST var1, int var2);

    public void visitWs(GroovySourceAST var1, int var2);

    public void visitDefault(GroovySourceAST var1, int var2);

    public void tearDown();

    public void push(GroovySourceAST var1);

    public GroovySourceAST pop();
}

