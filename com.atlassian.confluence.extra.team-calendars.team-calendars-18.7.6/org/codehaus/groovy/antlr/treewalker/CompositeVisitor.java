/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.Visitor;

public class CompositeVisitor
implements Visitor {
    final List visitors;
    final List backToFrontVisitors;

    public CompositeVisitor(List visitors) {
        this.visitors = visitors;
        this.backToFrontVisitors = new ArrayList();
        this.backToFrontVisitors.addAll(visitors);
        Collections.reverse(this.backToFrontVisitors);
    }

    private Iterator itr(int visit) {
        Iterator itr = this.visitors.iterator();
        if (visit == 4) {
            itr = this.backToFrontVisitors.iterator();
        }
        return itr;
    }

    @Override
    public void setUp() {
        Iterator itr = this.visitors.iterator();
        while (itr.hasNext()) {
            ((Visitor)itr.next()).setUp();
        }
    }

    @Override
    public void visitAbstract(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAbstract(t, visit);
        }
    }

    @Override
    public void visitAnnotation(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotation(t, visit);
        }
    }

    @Override
    public void visitAnnotations(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotations(t, visit);
        }
    }

    @Override
    public void visitAnnotationArrayInit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotationArrayInit(t, visit);
        }
    }

    @Override
    public void visitAnnotationDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotationDef(t, visit);
        }
    }

    @Override
    public void visitAnnotationFieldDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotationFieldDef(t, visit);
        }
    }

    @Override
    public void visitAnnotationMemberValuePair(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAnnotationMemberValuePair(t, visit);
        }
    }

    @Override
    public void visitArrayDeclarator(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitArrayDeclarator(t, visit);
        }
    }

    @Override
    public void visitAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAssign(t, visit);
        }
    }

    @Override
    public void visitAt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitAt(t, visit);
        }
    }

    @Override
    public void visitBand(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBand(t, visit);
        }
    }

    @Override
    public void visitBandAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBandAssign(t, visit);
        }
    }

    @Override
    public void visitBigSuffix(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBigSuffix(t, visit);
        }
    }

    @Override
    public void visitBlock(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBlock(t, visit);
        }
    }

    @Override
    public void visitBnot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBnot(t, visit);
        }
    }

    @Override
    public void visitBor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBor(t, visit);
        }
    }

    @Override
    public void visitBorAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBorAssign(t, visit);
        }
    }

    @Override
    public void visitBsr(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBsr(t, visit);
        }
    }

    @Override
    public void visitBsrAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBsrAssign(t, visit);
        }
    }

    @Override
    public void visitBxor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBxor(t, visit);
        }
    }

    @Override
    public void visitBxorAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitBxorAssign(t, visit);
        }
    }

    @Override
    public void visitCaseGroup(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitCaseGroup(t, visit);
        }
    }

    @Override
    public void visitClassDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitClassDef(t, visit);
        }
    }

    @Override
    public void visitClosedBlock(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitClosedBlock(t, visit);
        }
    }

    @Override
    public void visitClosureList(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitClosureList(t, visit);
        }
    }

    @Override
    public void visitClosureOp(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitClosureOp(t, visit);
        }
    }

    @Override
    public void visitColon(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitColon(t, visit);
        }
    }

    @Override
    public void visitComma(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitComma(t, visit);
        }
    }

    @Override
    public void visitCompareTo(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitCompareTo(t, visit);
        }
    }

    @Override
    public void visitCtorCall(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitCtorCall(t, visit);
        }
    }

    @Override
    public void visitCtorIdent(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitCtorIdent(t, visit);
        }
    }

    @Override
    public void visitDec(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDec(t, visit);
        }
    }

    @Override
    public void visitDigit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDigit(t, visit);
        }
    }

    @Override
    public void visitDiv(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDiv(t, visit);
        }
    }

    @Override
    public void visitDivAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDivAssign(t, visit);
        }
    }

    @Override
    public void visitDollar(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDollar(t, visit);
        }
    }

    @Override
    public void visitDot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDot(t, visit);
        }
    }

    @Override
    public void visitDynamicMember(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDynamicMember(t, visit);
        }
    }

    @Override
    public void visitElist(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitElist(t, visit);
        }
    }

    @Override
    public void visitEmptyStat(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEmptyStat(t, visit);
        }
    }

    @Override
    public void visitEnumConstantDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEnumConstantDef(t, visit);
        }
    }

    @Override
    public void visitEnumDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEnumDef(t, visit);
        }
    }

    @Override
    public void visitEof(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEof(t, visit);
        }
    }

    @Override
    public void visitEqual(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEqual(t, visit);
        }
    }

    @Override
    public void visitEsc(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitEsc(t, visit);
        }
    }

    @Override
    public void visitExponent(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitExponent(t, visit);
        }
    }

    @Override
    public void visitExpr(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitExpr(t, visit);
        }
    }

    @Override
    public void visitExtendsClause(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitExtendsClause(t, visit);
        }
    }

    @Override
    public void visitFinal(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitFinal(t, visit);
        }
    }

    @Override
    public void visitFloatSuffix(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitFloatSuffix(t, visit);
        }
    }

    @Override
    public void visitForCondition(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitForCondition(t, visit);
        }
    }

    @Override
    public void visitForEachClause(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitForEachClause(t, visit);
        }
    }

    @Override
    public void visitForInit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitForInit(t, visit);
        }
    }

    @Override
    public void visitForInIterable(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitForInIterable(t, visit);
        }
    }

    @Override
    public void visitForIterator(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitForIterator(t, visit);
        }
    }

    @Override
    public void visitGe(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitGe(t, visit);
        }
    }

    @Override
    public void visitGt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitGt(t, visit);
        }
    }

    @Override
    public void visitHexDigit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitHexDigit(t, visit);
        }
    }

    @Override
    public void visitIdent(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitIdent(t, visit);
        }
    }

    @Override
    public void visitImplementsClause(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitImplementsClause(t, visit);
        }
    }

    @Override
    public void visitImplicitParameters(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitImplicitParameters(t, visit);
        }
    }

    @Override
    public void visitImport(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitImport(t, visit);
        }
    }

    @Override
    public void visitInc(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitInc(t, visit);
        }
    }

    @Override
    public void visitIndexOp(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitIndexOp(t, visit);
        }
    }

    @Override
    public void visitInstanceInit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitInstanceInit(t, visit);
        }
    }

    @Override
    public void visitInterfaceDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitInterfaceDef(t, visit);
        }
    }

    @Override
    public void visitLabeledArg(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLabeledArg(t, visit);
        }
    }

    @Override
    public void visitLabeledStat(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLabeledStat(t, visit);
        }
    }

    @Override
    public void visitLand(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLand(t, visit);
        }
    }

    @Override
    public void visitLbrack(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLbrack(t, visit);
        }
    }

    @Override
    public void visitLcurly(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLcurly(t, visit);
        }
    }

    @Override
    public void visitLe(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLe(t, visit);
        }
    }

    @Override
    public void visitLetter(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLetter(t, visit);
        }
    }

    @Override
    public void visitListConstructor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitListConstructor(t, visit);
        }
    }

    @Override
    public void visitLiteralAs(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralAs(t, visit);
        }
    }

    @Override
    public void visitLiteralAssert(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralAssert(t, visit);
        }
    }

    @Override
    public void visitLiteralBoolean(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralBoolean(t, visit);
        }
    }

    @Override
    public void visitLiteralBreak(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralBreak(t, visit);
        }
    }

    @Override
    public void visitLiteralByte(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralByte(t, visit);
        }
    }

    @Override
    public void visitLiteralCase(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralCase(t, visit);
        }
    }

    @Override
    public void visitLiteralCatch(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralCatch(t, visit);
        }
    }

    @Override
    public void visitLiteralChar(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralChar(t, visit);
        }
    }

    @Override
    public void visitLiteralClass(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralClass(t, visit);
        }
    }

    @Override
    public void visitLiteralContinue(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralContinue(t, visit);
        }
    }

    @Override
    public void visitLiteralDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralDef(t, visit);
        }
    }

    @Override
    public void visitLiteralDefault(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralDefault(t, visit);
        }
    }

    @Override
    public void visitLiteralDouble(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralDouble(t, visit);
        }
    }

    @Override
    public void visitLiteralElse(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralElse(t, visit);
        }
    }

    @Override
    public void visitLiteralEnum(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralEnum(t, visit);
        }
    }

    @Override
    public void visitLiteralExtends(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralExtends(t, visit);
        }
    }

    @Override
    public void visitLiteralFalse(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralFalse(t, visit);
        }
    }

    @Override
    public void visitLiteralFinally(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralFinally(t, visit);
        }
    }

    @Override
    public void visitLiteralFloat(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralFloat(t, visit);
        }
    }

    @Override
    public void visitLiteralFor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralFor(t, visit);
        }
    }

    @Override
    public void visitLiteralIf(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralIf(t, visit);
        }
    }

    @Override
    public void visitLiteralImplements(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralImplements(t, visit);
        }
    }

    @Override
    public void visitLiteralImport(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralImport(t, visit);
        }
    }

    @Override
    public void visitLiteralIn(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralIn(t, visit);
        }
    }

    @Override
    public void visitLiteralInstanceof(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralInstanceof(t, visit);
        }
    }

    @Override
    public void visitLiteralInt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralInt(t, visit);
        }
    }

    @Override
    public void visitLiteralInterface(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralInterface(t, visit);
        }
    }

    @Override
    public void visitLiteralLong(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralLong(t, visit);
        }
    }

    @Override
    public void visitLiteralNative(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralNative(t, visit);
        }
    }

    @Override
    public void visitLiteralNew(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralNew(t, visit);
        }
    }

    @Override
    public void visitLiteralNull(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralNull(t, visit);
        }
    }

    @Override
    public void visitLiteralPackage(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralPackage(t, visit);
        }
    }

    @Override
    public void visitLiteralPrivate(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralPrivate(t, visit);
        }
    }

    @Override
    public void visitLiteralProtected(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralProtected(t, visit);
        }
    }

    @Override
    public void visitLiteralPublic(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralPublic(t, visit);
        }
    }

    @Override
    public void visitLiteralReturn(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralReturn(t, visit);
        }
    }

    @Override
    public void visitLiteralShort(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralShort(t, visit);
        }
    }

    @Override
    public void visitLiteralStatic(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralStatic(t, visit);
        }
    }

    @Override
    public void visitLiteralSuper(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralSuper(t, visit);
        }
    }

    @Override
    public void visitLiteralSwitch(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralSwitch(t, visit);
        }
    }

    @Override
    public void visitLiteralSynchronized(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralSynchronized(t, visit);
        }
    }

    @Override
    public void visitLiteralThis(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralThis(t, visit);
        }
    }

    @Override
    public void visitLiteralThreadsafe(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralThreadsafe(t, visit);
        }
    }

    @Override
    public void visitLiteralThrow(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralThrow(t, visit);
        }
    }

    @Override
    public void visitLiteralThrows(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralThrows(t, visit);
        }
    }

    @Override
    public void visitLiteralTransient(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralTransient(t, visit);
        }
    }

    @Override
    public void visitLiteralTrue(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralTrue(t, visit);
        }
    }

    @Override
    public void visitLiteralTry(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralTry(t, visit);
        }
    }

    @Override
    public void visitLiteralVoid(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralVoid(t, visit);
        }
    }

    @Override
    public void visitLiteralVolatile(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralVolatile(t, visit);
        }
    }

    @Override
    public void visitLiteralWhile(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLiteralWhile(t, visit);
        }
    }

    @Override
    public void visitLnot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLnot(t, visit);
        }
    }

    @Override
    public void visitLor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLor(t, visit);
        }
    }

    @Override
    public void visitLparen(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLparen(t, visit);
        }
    }

    @Override
    public void visitLt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitLt(t, visit);
        }
    }

    @Override
    public void visitMapConstructor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMapConstructor(t, visit);
        }
    }

    @Override
    public void visitMemberPointer(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMemberPointer(t, visit);
        }
    }

    @Override
    public void visitMethodCall(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMethodCall(t, visit);
        }
    }

    @Override
    public void visitMethodDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMethodDef(t, visit);
        }
    }

    @Override
    public void visitMinus(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMinus(t, visit);
        }
    }

    @Override
    public void visitMinusAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMinusAssign(t, visit);
        }
    }

    @Override
    public void visitMlComment(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMlComment(t, visit);
        }
    }

    @Override
    public void visitMod(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMod(t, visit);
        }
    }

    @Override
    public void visitModifiers(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitModifiers(t, visit);
        }
    }

    @Override
    public void visitModAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitModAssign(t, visit);
        }
    }

    @Override
    public void visitMultiCatch(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMultiCatch(t, visit);
        }
    }

    @Override
    public void visitMultiCatchTypes(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitMultiCatchTypes(t, visit);
        }
    }

    @Override
    public void visitNls(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNls(t, visit);
        }
    }

    @Override
    public void visitNotEqual(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNotEqual(t, visit);
        }
    }

    @Override
    public void visitNullTreeLookahead(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNullTreeLookahead(t, visit);
        }
    }

    @Override
    public void visitNumBigDecimal(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumBigDecimal(t, visit);
        }
    }

    @Override
    public void visitNumBigInt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumBigInt(t, visit);
        }
    }

    @Override
    public void visitNumDouble(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumDouble(t, visit);
        }
    }

    @Override
    public void visitNumFloat(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumFloat(t, visit);
        }
    }

    @Override
    public void visitNumInt(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumInt(t, visit);
        }
    }

    @Override
    public void visitNumLong(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitNumLong(t, visit);
        }
    }

    @Override
    public void visitObjblock(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitObjblock(t, visit);
        }
    }

    @Override
    public void visitOneNl(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitOneNl(t, visit);
        }
    }

    @Override
    public void visitOptionalDot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitOptionalDot(t, visit);
        }
    }

    @Override
    public void visitPackageDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitPackageDef(t, visit);
        }
    }

    @Override
    public void visitParameters(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitParameters(t, visit);
        }
    }

    @Override
    public void visitParameterDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitParameterDef(t, visit);
        }
    }

    @Override
    public void visitPlus(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitPlus(t, visit);
        }
    }

    @Override
    public void visitPlusAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitPlusAssign(t, visit);
        }
    }

    @Override
    public void visitPostDec(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitPostDec(t, visit);
        }
    }

    @Override
    public void visitPostInc(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitPostInc(t, visit);
        }
    }

    @Override
    public void visitQuestion(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitQuestion(t, visit);
        }
    }

    @Override
    public void visitRangeExclusive(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRangeExclusive(t, visit);
        }
    }

    @Override
    public void visitRangeInclusive(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRangeInclusive(t, visit);
        }
    }

    @Override
    public void visitRbrack(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRbrack(t, visit);
        }
    }

    @Override
    public void visitRcurly(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRcurly(t, visit);
        }
    }

    @Override
    public void visitRegexpCtorEnd(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRegexpCtorEnd(t, visit);
        }
    }

    @Override
    public void visitRegexpLiteral(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRegexpLiteral(t, visit);
        }
    }

    @Override
    public void visitRegexpSymbol(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRegexpSymbol(t, visit);
        }
    }

    @Override
    public void visitRegexFind(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRegexFind(t, visit);
        }
    }

    @Override
    public void visitRegexMatch(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRegexMatch(t, visit);
        }
    }

    @Override
    public void visitRparen(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitRparen(t, visit);
        }
    }

    @Override
    public void visitSelectSlot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSelectSlot(t, visit);
        }
    }

    @Override
    public void visitSemi(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSemi(t, visit);
        }
    }

    @Override
    public void visitShComment(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitShComment(t, visit);
        }
    }

    @Override
    public void visitSl(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSl(t, visit);
        }
    }

    @Override
    public void visitSlist(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSlist(t, visit);
        }
    }

    @Override
    public void visitSlAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSlAssign(t, visit);
        }
    }

    @Override
    public void visitSlComment(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSlComment(t, visit);
        }
    }

    @Override
    public void visitSpreadArg(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSpreadArg(t, visit);
        }
    }

    @Override
    public void visitSpreadDot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSpreadDot(t, visit);
        }
    }

    @Override
    public void visitSpreadMapArg(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSpreadMapArg(t, visit);
        }
    }

    @Override
    public void visitSr(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSr(t, visit);
        }
    }

    @Override
    public void visitSrAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSrAssign(t, visit);
        }
    }

    @Override
    public void visitStar(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStar(t, visit);
        }
    }

    @Override
    public void visitStarAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStarAssign(t, visit);
        }
    }

    @Override
    public void visitStarStar(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStarStar(t, visit);
        }
    }

    @Override
    public void visitStarStarAssign(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStarStarAssign(t, visit);
        }
    }

    @Override
    public void visitStaticImport(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStaticImport(t, visit);
        }
    }

    @Override
    public void visitStaticInit(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStaticInit(t, visit);
        }
    }

    @Override
    public void visitStrictfp(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStrictfp(t, visit);
        }
    }

    @Override
    public void visitStringCh(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringCh(t, visit);
        }
    }

    @Override
    public void visitStringConstructor(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringConstructor(t, visit);
        }
    }

    @Override
    public void visitStringCtorEnd(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringCtorEnd(t, visit);
        }
    }

    @Override
    public void visitStringCtorMiddle(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringCtorMiddle(t, visit);
        }
    }

    @Override
    public void visitStringCtorStart(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringCtorStart(t, visit);
        }
    }

    @Override
    public void visitStringLiteral(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringLiteral(t, visit);
        }
    }

    @Override
    public void visitStringNl(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitStringNl(t, visit);
        }
    }

    @Override
    public void visitSuperCtorCall(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitSuperCtorCall(t, visit);
        }
    }

    @Override
    public void visitTraitDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTraitDef(t, visit);
        }
    }

    @Override
    public void visitTripleDot(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTripleDot(t, visit);
        }
    }

    @Override
    public void visitType(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitType(t, visit);
        }
    }

    @Override
    public void visitTypecast(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypecast(t, visit);
        }
    }

    @Override
    public void visitTypeArgument(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeArgument(t, visit);
        }
    }

    @Override
    public void visitTypeArguments(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeArguments(t, visit);
        }
    }

    @Override
    public void visitTypeLowerBounds(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeLowerBounds(t, visit);
        }
    }

    @Override
    public void visitTypeParameter(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeParameter(t, visit);
        }
    }

    @Override
    public void visitTypeParameters(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeParameters(t, visit);
        }
    }

    @Override
    public void visitTypeUpperBounds(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitTypeUpperBounds(t, visit);
        }
    }

    @Override
    public void visitUnaryMinus(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitUnaryMinus(t, visit);
        }
    }

    @Override
    public void visitUnaryPlus(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitUnaryPlus(t, visit);
        }
    }

    @Override
    public void visitUnusedConst(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitUnusedConst(t, visit);
        }
    }

    @Override
    public void visitUnusedDo(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitUnusedDo(t, visit);
        }
    }

    @Override
    public void visitUnusedGoto(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitUnusedGoto(t, visit);
        }
    }

    @Override
    public void visitVariableDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitVariableDef(t, visit);
        }
    }

    @Override
    public void visitVariableParameterDef(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitVariableParameterDef(t, visit);
        }
    }

    @Override
    public void visitVocab(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitVocab(t, visit);
        }
    }

    @Override
    public void visitWildcardType(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitWildcardType(t, visit);
        }
    }

    @Override
    public void visitWs(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitWs(t, visit);
        }
    }

    @Override
    public void visitDefault(GroovySourceAST t, int visit) {
        Iterator itr = this.itr(visit);
        while (itr.hasNext()) {
            ((Visitor)itr.next()).visitDefault(t, visit);
        }
    }

    @Override
    public void tearDown() {
        Iterator itr = this.backToFrontVisitors.iterator();
        while (itr.hasNext()) {
            ((Visitor)itr.next()).tearDown();
        }
    }

    @Override
    public void push(GroovySourceAST t) {
        Iterator itr = this.visitors.iterator();
        while (itr.hasNext()) {
            ((Visitor)itr.next()).push(t);
        }
    }

    @Override
    public GroovySourceAST pop() {
        GroovySourceAST lastNodePopped = null;
        Iterator itr = this.backToFrontVisitors.iterator();
        while (itr.hasNext()) {
            lastNodePopped = ((Visitor)itr.next()).pop();
        }
        return lastNodePopped;
    }
}

