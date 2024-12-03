/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;

public class RecoveredField
extends RecoveredElement {
    public FieldDeclaration fieldDeclaration;
    boolean alreadyCompletedFieldInitialization;
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public RecoveredType[] anonymousTypes;
    public int anonymousTypeCount;

    public RecoveredField(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance) {
        this(fieldDeclaration, parent, bracketBalance, null);
    }

    public RecoveredField(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance, Parser parser) {
        super(parent, bracketBalance, parser);
        this.fieldDeclaration = fieldDeclaration;
        this.alreadyCompletedFieldInitialization = fieldDeclaration.initialization != null;
    }

    @Override
    public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue) {
        if (this.lambdaNestLevel > 0) {
            return this;
        }
        return super.add(localDeclaration, bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(FieldDeclaration addedfieldDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        if (this.fieldDeclaration.declarationSourceStart == addedfieldDeclaration.declarationSourceStart) {
            if (this.fieldDeclaration.initialization != null) {
                this.updateSourceEndIfNecessary(this.fieldDeclaration.initialization.sourceEnd);
            } else {
                this.updateSourceEndIfNecessary(this.fieldDeclaration.sourceEnd);
            }
        } else {
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(addedfieldDeclaration.declarationSourceStart - 1));
        }
        return this.parent.add(addedfieldDeclaration, bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(Statement statement, int bracketBalanceValue) {
        if (this.alreadyCompletedFieldInitialization || !(statement instanceof Expression) || !((Expression)statement).isTrulyExpression()) {
            return super.add(statement, bracketBalanceValue);
        }
        if (statement.sourceEnd > 0) {
            this.alreadyCompletedFieldInitialization = true;
        }
        if (!(statement instanceof AllocationExpression) && this.fieldDeclaration.getKind() == 3) {
            AllocationExpression alloc = new AllocationExpression();
            alloc.arguments = new Expression[]{(Expression)statement};
            this.fieldDeclaration.initialization = alloc;
        } else {
            this.fieldDeclaration.initialization = (Expression)statement;
            this.fieldDeclaration.declarationSourceEnd = statement.sourceEnd;
            this.fieldDeclaration.declarationEnd = statement.sourceEnd;
        }
        return this;
    }

    @Override
    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
        if (this.alreadyCompletedFieldInitialization || (typeDeclaration.bits & 0x200) == 0 || this.fieldDeclaration.declarationSourceEnd != 0 && typeDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd) {
            return super.add(typeDeclaration, bracketBalanceValue);
        }
        if (this.anonymousTypes == null) {
            this.anonymousTypes = new RecoveredType[5];
            this.anonymousTypeCount = 0;
        } else if (this.anonymousTypeCount == this.anonymousTypes.length) {
            this.anonymousTypes = new RecoveredType[2 * this.anonymousTypeCount];
            System.arraycopy(this.anonymousTypes, 0, this.anonymousTypes, 0, this.anonymousTypeCount);
        }
        RecoveredType element = new RecoveredType(typeDeclaration, (RecoveredElement)this, bracketBalanceValue);
        this.anonymousTypes[this.anonymousTypeCount++] = element;
        return element;
    }

    public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
        if (annotCount > 0) {
            Annotation[] existingAnnotations = this.fieldDeclaration.annotations;
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
        return this.fieldDeclaration;
    }

    @Override
    public int sourceEnd() {
        return this.fieldDeclaration.declarationSourceEnd;
    }

    @Override
    public String toString(int tab) {
        int i;
        StringBuffer buffer = new StringBuffer(this.tabString(tab));
        buffer.append("Recovered field:\n");
        this.fieldDeclaration.print(tab + 1, buffer);
        if (this.annotations != null) {
            i = 0;
            while (i < this.annotationCount) {
                buffer.append("\n");
                buffer.append(this.annotations[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.anonymousTypes != null) {
            i = 0;
            while (i < this.anonymousTypeCount) {
                buffer.append("\n");
                buffer.append(this.anonymousTypes[i].toString(tab + 1));
                ++i;
            }
        }
        return buffer.toString();
    }

    public FieldDeclaration updatedFieldDeclaration(int depth, Set<TypeDeclaration> knownTypes) {
        block17: {
            block18: {
                int i;
                if (this.modifiers != 0) {
                    this.fieldDeclaration.modifiers |= this.modifiers;
                    if (this.modifiersStart < this.fieldDeclaration.declarationSourceStart) {
                        this.fieldDeclaration.declarationSourceStart = this.modifiersStart;
                    }
                }
                if (this.annotationCount > 0) {
                    int existingCount = this.fieldDeclaration.annotations == null ? 0 : this.fieldDeclaration.annotations.length;
                    Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
                    if (existingCount > 0) {
                        System.arraycopy(this.fieldDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
                    }
                    i = 0;
                    while (i < this.annotationCount) {
                        annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
                        ++i;
                    }
                    this.fieldDeclaration.annotations = annotationReferences;
                    int start = this.annotations[0].annotation.sourceStart;
                    if (start < this.fieldDeclaration.declarationSourceStart) {
                        this.fieldDeclaration.declarationSourceStart = start;
                    }
                }
                if (this.anonymousTypes == null) break block17;
                if (this.fieldDeclaration.initialization != null) break block18;
                ArrayInitializer recoveredInitializers = null;
                int recoveredInitializersCount = 0;
                if (this.anonymousTypeCount > 1) {
                    recoveredInitializers = new ArrayInitializer();
                    recoveredInitializers.expressions = new Expression[this.anonymousTypeCount];
                }
                i = 0;
                while (i < this.anonymousTypeCount) {
                    TypeDeclaration anonymousType;
                    RecoveredType recoveredType = this.anonymousTypes[i];
                    TypeDeclaration typeDeclaration = recoveredType.typeDeclaration;
                    if (typeDeclaration.declarationSourceEnd == 0) {
                        typeDeclaration.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
                        typeDeclaration.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
                    }
                    if (recoveredType.preserveContent && (anonymousType = recoveredType.updatedTypeDeclaration(depth + 1, knownTypes)) != null) {
                        if (this.anonymousTypeCount > 1) {
                            if (recoveredInitializersCount == 0) {
                                this.fieldDeclaration.initialization = recoveredInitializers;
                            }
                            recoveredInitializers.expressions[recoveredInitializersCount++] = anonymousType.allocation;
                        } else {
                            this.fieldDeclaration.initialization = anonymousType.allocation;
                        }
                        int end = anonymousType.declarationSourceEnd;
                        if (end > this.fieldDeclaration.declarationSourceEnd) {
                            this.fieldDeclaration.declarationSourceEnd = end;
                            this.fieldDeclaration.declarationEnd = end;
                        }
                    }
                    ++i;
                }
                if (this.anonymousTypeCount <= 0) break block17;
                this.fieldDeclaration.bits |= 2;
                if (recoveredInitializers == null) break block17;
                recoveredInitializers.sourceStart = this.anonymousTypes[0].typeDeclaration.sourceStart;
                recoveredInitializers.sourceEnd = this.anonymousTypes[this.anonymousTypeCount - 1].typeDeclaration.sourceEnd;
                break block17;
            }
            if (this.fieldDeclaration.getKind() == 3) {
                int i = 0;
                while (i < this.anonymousTypeCount) {
                    RecoveredType recoveredType = this.anonymousTypes[i];
                    TypeDeclaration typeDeclaration = recoveredType.typeDeclaration;
                    if (typeDeclaration.declarationSourceEnd == 0) {
                        typeDeclaration.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
                        typeDeclaration.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
                    }
                    recoveredType.updatedTypeDeclaration(depth, knownTypes);
                    ++i;
                }
            }
        }
        return this.fieldDeclaration;
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (this.bracketBalance > 0) {
            --this.bracketBalance;
            if (this.bracketBalance == 0) {
                if (this.fieldDeclaration.getKind() == 3) {
                    this.updateSourceEndIfNecessary(braceEnd);
                    return this.parent;
                }
                if (this.fieldDeclaration.declarationSourceEnd > 0) {
                    this.alreadyCompletedFieldInitialization = true;
                }
            }
            return this;
        }
        if (this.bracketBalance == 0) {
            this.alreadyCompletedFieldInitialization = true;
            this.updateSourceEndIfNecessary(braceEnd - 1);
        }
        if (this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }

    @Override
    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        if (this.fieldDeclaration.declarationSourceEnd == 0) {
            if (this.fieldDeclaration.type instanceof ArrayTypeReference || this.fieldDeclaration.type instanceof ArrayQualifiedTypeReference) {
                if (!this.alreadyCompletedFieldInitialization) {
                    ++this.bracketBalance;
                    return null;
                }
            } else {
                ++this.bracketBalance;
                return null;
            }
        }
        if (this.fieldDeclaration.declarationSourceEnd == 0 && this.fieldDeclaration.getKind() == 3) {
            ++this.bracketBalance;
            return null;
        }
        this.updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
        return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
    }

    @Override
    public void updateParseTree() {
        this.updatedFieldDeclaration(0, new HashSet<TypeDeclaration>());
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.fieldDeclaration.declarationSourceEnd == 0) {
            this.fieldDeclaration.declarationSourceEnd = bodyEnd;
            this.fieldDeclaration.declarationEnd = bodyEnd;
        }
    }
}

