/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredInitializer;
import org.eclipse.jdt.internal.compiler.parser.RecoveredLocalVariable;
import org.eclipse.jdt.internal.compiler.parser.RecoveredMethod;
import org.eclipse.jdt.internal.compiler.parser.RecoveredStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

public class RecoveredBlock
extends RecoveredStatement
implements TerminalTokens {
    public Block blockDeclaration;
    public RecoveredStatement[] statements;
    public int statementCount;
    public boolean preserveContent = false;
    public RecoveredLocalVariable pendingArgument;
    int pendingModifiers;
    int pendingModifersSourceStart = -1;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;

    public RecoveredBlock(Block block, RecoveredElement parent, int bracketBalance) {
        super(block, parent, bracketBalance);
        this.blockDeclaration = block;
        this.foundOpeningBrace = true;
        this.preserveContent = this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated;
    }

    @Override
    public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {
        if (this.parent != null && this.parent instanceof RecoveredMethod) {
            RecoveredMethod enclosingRecoveredMethod = (RecoveredMethod)this.parent;
            if (enclosingRecoveredMethod.methodBody == this && enclosingRecoveredMethod.parent == null) {
                this.resetPendingModifiers();
                return this;
            }
        }
        return super.add(methodDeclaration, bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.blockDeclaration.sourceEnd != 0 && nestedBlockDeclaration.sourceStart > this.blockDeclaration.sourceEnd) {
            return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
        }
        RecoveredBlock element = new RecoveredBlock(nestedBlockDeclaration, (RecoveredElement)this, bracketBalanceValue);
        if (this.pendingArgument != null) {
            element.attach(this.pendingArgument);
            this.pendingArgument = null;
        }
        if (this.parser().statementRecoveryActivated) {
            this.addBlockStatement(element);
        }
        this.attach(element);
        if (nestedBlockDeclaration.sourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue) {
        return this.add(localDeclaration, bracketBalanceValue, false);
    }

    public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue, boolean delegatedByParent) {
        if (localDeclaration.isRecoveredFromLoneIdentifier()) {
            return this;
        }
        if (this.blockDeclaration.sourceEnd != 0 && localDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            this.resetPendingModifiers();
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(localDeclaration, bracketBalanceValue);
        }
        RecoveredLocalVariable element = new RecoveredLocalVariable(localDeclaration, (RecoveredElement)this, bracketBalanceValue);
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (localDeclaration instanceof Argument) {
            this.pendingArgument = element;
            return this;
        }
        this.attach(element);
        if (localDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement add(Statement stmt, int bracketBalanceValue) {
        return this.add(stmt, bracketBalanceValue, false);
    }

    public RecoveredElement add(Statement stmt, int bracketBalanceValue, boolean delegatedByParent) {
        this.resetPendingModifiers();
        if (this.blockDeclaration.sourceEnd != 0 && stmt.sourceStart > this.blockDeclaration.sourceEnd) {
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(stmt, bracketBalanceValue);
        }
        RecoveredStatement element = new RecoveredStatement(stmt, this, bracketBalanceValue);
        this.attach(element);
        if (!this.isEndKnown(stmt)) {
            return element;
        }
        return this;
    }

    boolean isEndKnown(Statement stmt) {
        if (stmt instanceof ForeachStatement && ((ForeachStatement)stmt).action == null) {
            return false;
        }
        return stmt.sourceEnd != 0;
    }

    @Override
    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
        return this.add(typeDeclaration, bracketBalanceValue, false);
    }

    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue, boolean delegatedByParent) {
        if (this.blockDeclaration.sourceEnd != 0 && typeDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            this.resetPendingModifiers();
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(typeDeclaration, bracketBalanceValue);
        }
        RecoveredType element = new RecoveredType(typeDeclaration, (RecoveredElement)this, bracketBalanceValue);
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        this.attach(element);
        if (typeDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
        if (this.pendingAnnotations == null) {
            this.pendingAnnotations = new RecoveredAnnotation[5];
            this.pendingAnnotationCount = 0;
        } else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
            this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount];
            System.arraycopy(this.pendingAnnotations, 0, this.pendingAnnotations, 0, this.pendingAnnotationCount);
        }
        RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
        this.pendingAnnotations[this.pendingAnnotationCount++] = element;
        return element;
    }

    @Override
    public void addModifier(int flag, int modifiersSourceStart) {
        this.pendingModifiers |= flag;
        if (this.pendingModifersSourceStart < 0) {
            this.pendingModifersSourceStart = modifiersSourceStart;
        }
    }

    void attach(RecoveredStatement recoveredStatement) {
        if (this.statements == null) {
            this.statements = new RecoveredStatement[5];
            this.statementCount = 0;
        } else if (this.statementCount == this.statements.length) {
            this.statements = new RecoveredStatement[2 * this.statementCount];
            System.arraycopy(this.statements, 0, this.statements, 0, this.statementCount);
        }
        this.statements[this.statementCount++] = recoveredStatement;
    }

    void attachPendingModifiers(RecoveredAnnotation[] pendingAnnots, int pendingAnnotCount, int pendingMods, int pendingModsSourceStart) {
        this.pendingAnnotations = pendingAnnots;
        this.pendingAnnotationCount = pendingAnnotCount;
        this.pendingModifiers = pendingMods;
        this.pendingModifersSourceStart = pendingModsSourceStart;
    }

    @Override
    public ASTNode parseTree() {
        return this.blockDeclaration;
    }

    @Override
    public void resetPendingModifiers() {
        this.pendingAnnotations = null;
        this.pendingAnnotationCount = 0;
        this.pendingModifiers = 0;
        this.pendingModifersSourceStart = -1;
    }

    @Override
    public String toString(int tab) {
        StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered block:\n");
        this.blockDeclaration.print(tab + 1, result);
        if (this.statements != null) {
            int i = 0;
            while (i < this.statementCount) {
                result.append("\n");
                result.append(this.statements[i].toString(tab + 1));
                ++i;
            }
        }
        return result.toString();
    }

    public Block updatedBlock(int depth, Set<TypeDeclaration> knownTypes) {
        if (!this.preserveContent || this.statementCount == 0) {
            return null;
        }
        Statement[] updatedStatements = new Statement[this.statementCount];
        int updatedCount = 0;
        RecoveredStatement lastStatement = this.statements[this.statementCount - 1];
        RecoveredMethod enclosingMethod = this.enclosingMethod();
        RecoveredInitializer enclosingIntializer = this.enclosingInitializer();
        int bodyEndValue = 0;
        if (enclosingMethod != null) {
            bodyEndValue = enclosingMethod.methodDeclaration.bodyEnd;
            if (enclosingIntializer != null && enclosingMethod.methodDeclaration.sourceStart < enclosingIntializer.fieldDeclaration.sourceStart) {
                bodyEndValue = enclosingIntializer.fieldDeclaration.declarationSourceEnd;
            }
        } else {
            bodyEndValue = enclosingIntializer != null ? enclosingIntializer.fieldDeclaration.declarationSourceEnd : this.blockDeclaration.sourceEnd - 1;
        }
        if (lastStatement instanceof RecoveredLocalVariable) {
            RecoveredLocalVariable lastLocalVariable = (RecoveredLocalVariable)lastStatement;
            if (lastLocalVariable.localDeclaration.declarationSourceEnd == 0) {
                lastLocalVariable.localDeclaration.declarationSourceEnd = bodyEndValue;
                lastLocalVariable.localDeclaration.declarationEnd = bodyEndValue;
            }
        } else if (lastStatement instanceof RecoveredBlock) {
            RecoveredBlock lastBlock = (RecoveredBlock)lastStatement;
            if (lastBlock.blockDeclaration.sourceEnd == 0) {
                lastBlock.blockDeclaration.sourceEnd = bodyEndValue;
            }
        } else if (!(lastStatement instanceof RecoveredType) && lastStatement.statement.sourceEnd == 0) {
            lastStatement.statement.sourceEnd = bodyEndValue;
        }
        int lastEnd = this.blockDeclaration.sourceStart;
        int i = 0;
        while (i < this.statementCount) {
            block28: {
                Statement updatedStatement = this.statements[i].updatedStatement(depth, knownTypes);
                if (updatedStatement != null) {
                    int j = 0;
                    while (j < i) {
                        if (updatedStatements[j] instanceof LocalDeclaration) {
                            LocalDeclaration local = (LocalDeclaration)updatedStatements[j];
                            if (local.initialization != null && updatedStatement.sourceStart >= local.initialization.sourceStart && updatedStatement.sourceEnd <= local.initialization.sourceEnd) break block28;
                        }
                        ++j;
                    }
                    updatedStatements[updatedCount++] = updatedStatement;
                    if (updatedStatement instanceof LocalDeclaration) {
                        LocalDeclaration localDeclaration = (LocalDeclaration)updatedStatement;
                        if (localDeclaration.declarationSourceEnd > lastEnd) {
                            lastEnd = localDeclaration.declarationSourceEnd;
                        }
                    } else if (updatedStatement instanceof TypeDeclaration) {
                        TypeDeclaration typeDeclaration = (TypeDeclaration)updatedStatement;
                        if (typeDeclaration.declarationSourceEnd > lastEnd) {
                            lastEnd = typeDeclaration.declarationSourceEnd;
                        }
                    } else if (updatedStatement.sourceEnd > lastEnd) {
                        lastEnd = updatedStatement.sourceEnd;
                    }
                }
            }
            ++i;
        }
        if (updatedCount == 0) {
            return null;
        }
        if (updatedCount != this.statementCount) {
            this.blockDeclaration.statements = new Statement[updatedCount];
            System.arraycopy(updatedStatements, 0, this.blockDeclaration.statements, 0, updatedCount);
        } else {
            this.blockDeclaration.statements = updatedStatements;
        }
        if (this.blockDeclaration.sourceEnd == 0) {
            this.blockDeclaration.sourceEnd = lastEnd < bodyEndValue ? bodyEndValue : lastEnd;
        }
        return this.blockDeclaration;
    }

    @Override
    public Statement updatedStatement(int depth, Set<TypeDeclaration> knownTypes) {
        return this.updatedBlock(depth, knownTypes);
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (--this.bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            RecoveredMethod method = this.enclosingMethod();
            if (method != null && method.methodBody == this) {
                return this.parent.updateOnClosingBrace(braceStart, braceEnd);
            }
            RecoveredInitializer initializer = this.enclosingInitializer();
            if (initializer != null && initializer.initializerBody == this) {
                return this.parent.updateOnClosingBrace(braceStart, braceEnd);
            }
            return this.parent;
        }
        return this;
    }

    @Override
    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        Block block = new Block(0);
        block.sourceStart = this.parser().scanner.startPosition;
        return this.add(block, 1);
    }

    @Override
    public void updateParseTree() {
        this.updatedBlock(0, new HashSet<TypeDeclaration>());
    }

    @Override
    public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
        char[][] fieldTypeName;
        this.resetPendingModifiers();
        if ((fieldDeclaration.modifiers & 0xFFFFFFEF) != 0 || fieldDeclaration.type == null || (fieldTypeName = fieldDeclaration.type.getTypeName()).length == 1 && CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName())) {
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        if (this.blockDeclaration.sourceEnd != 0 && fieldDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        return this;
    }
}

