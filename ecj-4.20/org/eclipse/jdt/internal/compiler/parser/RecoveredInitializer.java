/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredBlock;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredField;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

public class RecoveredInitializer
extends RecoveredField
implements TerminalTokens {
    public RecoveredType[] localTypes;
    public int localTypeCount;
    public RecoveredBlock initializerBody;
    int pendingModifiers;
    int pendingModifersSourceStart = -1;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;

    public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance) {
        this(fieldDeclaration, parent, bracketBalance, null);
    }

    public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance, Parser parser) {
        super(fieldDeclaration, parent, bracketBalance, parser);
        this.foundOpeningBrace = true;
    }

    @Override
    public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
        if (this.fieldDeclaration.declarationSourceEnd > 0 && nestedBlockDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
        }
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (this.initializerBody == null) {
            this.initializerBody = new RecoveredBlock(nestedBlockDeclaration, (RecoveredElement)this, bracketBalanceValue);
            return this.initializerBody;
        }
        this.initializerBody.blockDeclaration.sourceEnd = 0;
        if (nestedBlockDeclaration.sourceEnd == 0) {
            return this.initializerBody;
        }
        return this.initializerBody.add(nestedBlockDeclaration, bracketBalanceValue, true);
    }

    @Override
    public RecoveredElement add(FieldDeclaration newFieldDeclaration, int bracketBalanceValue) {
        char[][] fieldTypeName;
        this.resetPendingModifiers();
        if ((newFieldDeclaration.modifiers & 0xFFFFFFEF) != 0 || newFieldDeclaration.type == null || (fieldTypeName = newFieldDeclaration.type.getTypeName()).length == 1 && CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName())) {
            if (this.parent == null) {
                return this;
            }
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(newFieldDeclaration.declarationSourceStart - 1));
            return this.parent.add(newFieldDeclaration, bracketBalanceValue);
        }
        if (this.fieldDeclaration.declarationSourceEnd > 0 && newFieldDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd) {
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(newFieldDeclaration, bracketBalanceValue);
        }
        return this;
    }

    @Override
    public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue) {
        if (this.fieldDeclaration.declarationSourceEnd != 0 && localDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(localDeclaration, bracketBalanceValue);
        }
        if (this.initializerBody == null) {
            Block block = new Block(0);
            block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
            RecoveredElement element = this.add(block, 1);
            if (this.bracketBalance > 0) {
                int i = 0;
                while (i < this.bracketBalance - 1) {
                    element = element.add(new Block(0), 1);
                    ++i;
                }
                this.bracketBalance = 1;
            }
            return element.add(localDeclaration, bracketBalanceValue);
        }
        this.initializerBody.attachPendingModifiers(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        this.resetPendingModifiers();
        return this.initializerBody.add(localDeclaration, bracketBalanceValue, true);
    }

    @Override
    public RecoveredElement add(Statement statement, int bracketBalanceValue) {
        if (this.fieldDeclaration.declarationSourceEnd != 0 && statement.sourceStart > this.fieldDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(statement, bracketBalanceValue);
        }
        Block block = new Block(0);
        block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
        RecoveredElement element = this.add(block, 1);
        if (this.initializerBody != null) {
            this.initializerBody.attachPendingModifiers(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        return element.add(statement, bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
        if (this.fieldDeclaration.declarationSourceEnd != 0 && typeDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(typeDeclaration, bracketBalanceValue);
        }
        if ((typeDeclaration.bits & 0x100) != 0 || this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated) {
            Block block = new Block(0);
            block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
            RecoveredElement element = this.add(block, 1);
            if (this.initializerBody != null) {
                this.initializerBody.attachPendingModifiers(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
            }
            this.resetPendingModifiers();
            return element.add(typeDeclaration, bracketBalanceValue);
        }
        if (this.localTypes == null) {
            this.localTypes = new RecoveredType[5];
            this.localTypeCount = 0;
        } else if (this.localTypeCount == this.localTypes.length) {
            this.localTypes = new RecoveredType[2 * this.localTypeCount];
            System.arraycopy(this.localTypes, 0, this.localTypes, 0, this.localTypeCount);
        }
        RecoveredType element = new RecoveredType(typeDeclaration, (RecoveredElement)this, bracketBalanceValue);
        this.localTypes[this.localTypeCount++] = element;
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        return element;
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
        result.append("Recovered initializer:\n");
        this.fieldDeclaration.print(tab + 1, result);
        if (this.annotations != null) {
            int i = 0;
            while (i < this.annotationCount) {
                result.append("\n");
                result.append(this.annotations[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.initializerBody != null) {
            result.append("\n");
            result.append(this.initializerBody.toString(tab + 1));
        }
        return result.toString();
    }

    @Override
    public FieldDeclaration updatedFieldDeclaration(int depth, Set<TypeDeclaration> knownTypes) {
        if (this.initializerBody != null) {
            Block block = this.initializerBody.updatedBlock(depth, knownTypes);
            if (block != null) {
                Initializer initializer = (Initializer)this.fieldDeclaration;
                initializer.block = block;
                if (initializer.declarationSourceEnd == 0) {
                    initializer.declarationSourceEnd = block.sourceEnd;
                    initializer.bodyEnd = block.sourceEnd;
                }
            }
            if (this.localTypeCount > 0) {
                this.fieldDeclaration.bits |= 2;
            }
        }
        if (this.fieldDeclaration.sourceEnd == 0) {
            this.fieldDeclaration.sourceEnd = this.fieldDeclaration.declarationSourceEnd;
        }
        return this.fieldDeclaration;
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (--this.bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            return this.parent;
        }
        return this;
    }

    @Override
    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        ++this.bracketBalance;
        return this;
    }

    @Override
    public void updateSourceEndIfNecessary(int braceStart, int braceEnd) {
        if (this.fieldDeclaration.declarationSourceEnd == 0) {
            Initializer initializer = (Initializer)this.fieldDeclaration;
            if (this.parser().rBraceSuccessorStart >= braceEnd) {
                initializer.declarationSourceEnd = initializer.bodyStart < this.parser().rBraceEnd ? this.parser().rBraceEnd : initializer.bodyStart;
                initializer.bodyEnd = initializer.bodyStart < this.parser().rBraceStart ? this.parser().rBraceStart : initializer.bodyStart;
            } else {
                if (braceEnd < initializer.declarationSourceStart) {
                    initializer.bodyEnd = initializer.declarationSourceEnd = initializer.declarationSourceStart;
                } else {
                    initializer.declarationSourceEnd = braceEnd;
                    initializer.bodyEnd = braceStart - 1;
                }
                if (initializer.bodyStart > initializer.declarationSourceEnd) {
                    initializer.bodyStart = initializer.declarationSourceEnd;
                    if (initializer.block != null) {
                        initializer.block.sourceStart = initializer.declarationSourceStart;
                    }
                }
            }
            if (initializer.block != null) {
                initializer.block.sourceEnd = initializer.declarationSourceEnd;
            }
        }
    }
}

