/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;

public class CompactConstructorDeclaration
extends ConstructorDeclaration {
    public TypeDeclaration recordDeclaration;

    public CompactConstructorDeclaration(CompilationResult compilationResult) {
        super(compilationResult);
    }

    @Override
    public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
        this.constructorCall = SuperReference.implicitSuperConstructorCall();
        parser.parse(this, unit, false);
        this.containsSwitchWithTry = parser.switchWithTry;
    }

    @Override
    public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo, int initialReachMode) {
        try {
            this.scope.isCompactConstructorScope = true;
            super.analyseCode(classScope, initializerFlowContext, flowInfo, initialReachMode);
        }
        finally {
            this.scope.isCompactConstructorScope = false;
        }
    }

    @Override
    protected void doFieldReachAnalysis(FlowInfo flowInfo, FieldBinding[] fields) {
    }

    @Override
    protected void checkAndGenerateFieldAssignment(FlowContext flowContext, FlowInfo flowInfo, FieldBinding[] fields) {
        this.scope.isCompactConstructorScope = false;
        if (fields == null) {
            return;
        }
        ArrayList<Assignment> fieldAssignments = new ArrayList<Assignment>();
        FieldBinding[] fieldBindingArray = fields;
        int n = fields.length;
        int n2 = 0;
        while (n2 < n) {
            FieldBinding field = fieldBindingArray[n2];
            if (!field.isStatic()) {
                assert (field.isFinal());
                FieldReference lhs = new FieldReference(field.name, 0L);
                lhs.receiver = new ThisReference(0, 0);
                Assignment assignment = new Assignment(lhs, new SingleNameReference(field.name, 0L), 0);
                assignment.resolveType(this.scope);
                assignment.analyseCode(this.scope, flowContext, flowInfo);
                assignment.bits |= 0x400;
                assert (flowInfo.isDefinitelyAssigned(field));
                fieldAssignments.add(assignment);
            }
            ++n2;
        }
        if (fieldAssignments.isEmpty()) {
            return;
        }
        Statement[] fa = fieldAssignments.toArray(new Statement[0]);
        if (this.statements == null) {
            this.statements = fa;
            return;
        }
        int len = this.statements.length;
        int fLen = fa.length;
        Statement[] stmts = new Statement[len + fLen];
        System.arraycopy(this.statements, 0, stmts, 0, len);
        System.arraycopy(fa, 0, stmts, len, fLen);
        this.statements = stmts;
    }
}

