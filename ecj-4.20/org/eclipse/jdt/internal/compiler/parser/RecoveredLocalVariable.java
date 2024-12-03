/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredStatement;

public class RecoveredLocalVariable
extends RecoveredStatement {
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public LocalDeclaration localDeclaration;
    public boolean alreadyCompletedLocalInitialization;

    public RecoveredLocalVariable(LocalDeclaration localDeclaration, RecoveredElement parent, int bracketBalance) {
        super(localDeclaration, parent, bracketBalance);
        this.localDeclaration = localDeclaration;
        this.alreadyCompletedLocalInitialization = localDeclaration.initialization != null;
    }

    @Override
    public RecoveredElement add(Statement stmt, int bracketBalanceValue) {
        if (this.alreadyCompletedLocalInitialization || !(stmt instanceof Expression) || !((Expression)stmt).isTrulyExpression()) {
            return super.add(stmt, bracketBalanceValue);
        }
        this.alreadyCompletedLocalInitialization = true;
        this.localDeclaration.initialization = (Expression)stmt;
        this.localDeclaration.declarationSourceEnd = stmt.sourceEnd;
        this.localDeclaration.declarationEnd = stmt.sourceEnd;
        return this;
    }

    public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
        if (annotCount > 0) {
            Annotation[] existingAnnotations = this.localDeclaration.annotations;
            if (existingAnnotations != null) {
                this.annotations = new RecoveredAnnotation[annotCount];
                this.annotationCount = 0;
                int i = 0;
                while (i < annotCount) {
                    block7: {
                        int j = 0;
                        while (j < existingAnnotations.length) {
                            if (annots[i].annotation != existingAnnotations[j]) {
                                ++j;
                                continue;
                            }
                            break block7;
                        }
                        this.annotations[this.annotationCount++] = annots[i];
                    }
                    ++i;
                }
            } else {
                this.annotations = annots;
                this.annotationCount = annotCount;
            }
        }
        if (mods != 0) {
            this.modifiers = mods;
            this.modifiersStart = modsSourceStart;
        }
    }

    @Override
    public ASTNode parseTree() {
        return this.localDeclaration;
    }

    @Override
    public int sourceEnd() {
        return this.localDeclaration.declarationSourceEnd;
    }

    @Override
    public String toString(int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered local variable:\n" + this.localDeclaration.print(tab + 1, new StringBuffer(10));
    }

    public Statement updatedStatement(int depth, Set knownTypes) {
        if (this.modifiers != 0) {
            this.localDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.localDeclaration.declarationSourceStart) {
                this.localDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            int existingCount = this.localDeclaration.annotations == null ? 0 : this.localDeclaration.annotations.length;
            Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.localDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            int i = 0;
            while (i < this.annotationCount) {
                annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
                ++i;
            }
            this.localDeclaration.annotations = annotationReferences;
            int start = this.annotations[0].annotation.sourceStart;
            if (start < this.localDeclaration.declarationSourceStart) {
                this.localDeclaration.declarationSourceStart = start;
            }
        }
        return this.localDeclaration;
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (this.bracketBalance > 0) {
            --this.bracketBalance;
            if (this.bracketBalance == 0) {
                this.alreadyCompletedLocalInitialization = true;
            }
            return this;
        }
        if (this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }

    @Override
    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        if (this.localDeclaration.declarationSourceEnd == 0 && (this.localDeclaration.type instanceof ArrayTypeReference || this.localDeclaration.type instanceof ArrayQualifiedTypeReference) && !this.alreadyCompletedLocalInitialization) {
            ++this.bracketBalance;
            return null;
        }
        this.updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
        return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
    }

    @Override
    public void updateParseTree() {
        this.updatedStatement(0, (Set)new HashSet());
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.localDeclaration.declarationSourceEnd == 0) {
            this.localDeclaration.declarationSourceEnd = bodyEnd;
            this.localDeclaration.declarationEnd = bodyEnd;
        }
    }
}

