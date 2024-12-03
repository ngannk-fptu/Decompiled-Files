/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredImport;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModule;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;

public class RecoveredUnit
extends RecoveredElement {
    public CompilationUnitDeclaration unitDeclaration;
    public RecoveredImport[] imports;
    public int importCount;
    public RecoveredModule module;
    public RecoveredType[] types;
    public int typeCount;
    int pendingModifiers;
    int pendingModifersSourceStart = -1;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;

    public RecoveredUnit(CompilationUnitDeclaration unitDeclaration, int bracketBalance, Parser parser) {
        super(null, bracketBalance, parser);
        this.unitDeclaration = unitDeclaration;
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
    public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {
        if (this.typeCount > 0) {
            RecoveredType type = this.types[this.typeCount - 1];
            int start = type.bodyEnd;
            int end = type.typeDeclaration.bodyEnd;
            type.bodyEnd = 0;
            type.typeDeclaration.declarationSourceEnd = 0;
            type.typeDeclaration.bodyEnd = 0;
            int kind = TypeDeclaration.kind(type.typeDeclaration.modifiers);
            if (start > 0 && start < end && kind != 2 && kind != 4) {
                Block block = new Block(0);
                block.sourceStart = block.sourceEnd = end;
                Initializer initializer = new Initializer(block, 0);
                initializer.bodyStart = end;
                initializer.bodyEnd = end;
                initializer.declarationSourceStart = end;
                initializer.declarationSourceEnd = end;
                initializer.sourceStart = end;
                initializer.sourceEnd = end;
                type.add(initializer, bracketBalanceValue);
            }
            this.resetPendingModifiers();
            return type.add(methodDeclaration, bracketBalanceValue);
        }
        return this;
    }

    @Override
    public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
        if (this.typeCount > 0) {
            RecoveredType type = this.types[this.typeCount - 1];
            type.bodyEnd = 0;
            type.typeDeclaration.declarationSourceEnd = 0;
            type.typeDeclaration.bodyEnd = 0;
            this.resetPendingModifiers();
            return type.add(fieldDeclaration, bracketBalanceValue);
        }
        return this;
    }

    public RecoveredElement add(ExportsStatement exportReference, int bracketBalanceValue) {
        return this.module != null ? this.module.add(exportReference, bracketBalanceValue) : null;
    }

    @Override
    public RecoveredElement add(ImportReference importReference, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.imports == null) {
            this.imports = new RecoveredImport[5];
            this.importCount = 0;
        } else if (this.importCount == this.imports.length) {
            this.imports = new RecoveredImport[2 * this.importCount];
            System.arraycopy(this.imports, 0, this.imports, 0, this.importCount);
        }
        RecoveredImport element = new RecoveredImport(importReference, this, bracketBalanceValue);
        this.imports[this.importCount++] = element;
        if (importReference.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement add(ModuleDeclaration moduleDeclaration, int bracketBalanceValue) {
        this.module = new RecoveredModule(moduleDeclaration, this, bracketBalanceValue);
        return this.module;
    }

    @Override
    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
        if ((typeDeclaration.bits & 0x200) != 0 && this.typeCount > 0) {
            RecoveredType lastType = this.types[this.typeCount - 1];
            lastType.bodyEnd = 0;
            lastType.typeDeclaration.bodyEnd = 0;
            lastType.typeDeclaration.declarationSourceEnd = 0;
            lastType.bracketBalance = lastType.bracketBalance + 1;
            this.resetPendingModifiers();
            return lastType.add(typeDeclaration, bracketBalanceValue);
        }
        if (this.types == null) {
            this.types = new RecoveredType[5];
            this.typeCount = 0;
        } else if (this.typeCount == this.types.length) {
            this.types = new RecoveredType[2 * this.typeCount];
            System.arraycopy(this.types, 0, this.types, 0, this.typeCount);
        }
        RecoveredType element = new RecoveredType(typeDeclaration, (RecoveredElement)this, bracketBalanceValue);
        this.types[this.typeCount++] = element;
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (typeDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public ASTNode parseTree() {
        return this.unitDeclaration;
    }

    @Override
    public void resetPendingModifiers() {
        this.pendingAnnotations = null;
        this.pendingAnnotationCount = 0;
        this.pendingModifiers = 0;
        this.pendingModifersSourceStart = -1;
    }

    @Override
    public int sourceEnd() {
        return this.unitDeclaration.sourceEnd;
    }

    @Override
    public int getLastStart() {
        int lastTypeStart = -1;
        if (this.typeCount > 0) {
            TypeDeclaration lastType = this.types[this.typeCount - 1].typeDeclaration;
            if (lastTypeStart < lastType.declarationSourceStart && lastType.declarationSourceStart != 0) {
                lastTypeStart = lastType.declarationSourceStart;
            }
        }
        return lastTypeStart;
    }

    @Override
    public String toString(int tab) {
        int i;
        StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered unit: [\n");
        this.unitDeclaration.print(tab + 1, result);
        result.append(this.tabString(tab + 1));
        result.append("]");
        if (this.imports != null) {
            i = 0;
            while (i < this.importCount) {
                result.append("\n");
                result.append(this.imports[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.types != null) {
            i = 0;
            while (i < this.typeCount) {
                result.append("\n");
                result.append(this.types[i].toString(tab + 1));
                ++i;
            }
        }
        return result.toString();
    }

    public CompilationUnitDeclaration updatedCompilationUnitDeclaration() {
        if (this.importCount > 0) {
            ImportReference[] importRefences = new ImportReference[this.importCount];
            int i = 0;
            while (i < this.importCount) {
                importRefences[i] = this.imports[i].updatedImportReference();
                ++i;
            }
            this.unitDeclaration.imports = importRefences;
        }
        if (this.module != null) {
            this.unitDeclaration.moduleDeclaration = this.module.updatedModuleDeclaration();
        }
        if (this.typeCount > 0) {
            int existingCount = this.unitDeclaration.types == null ? 0 : this.unitDeclaration.types.length;
            TypeDeclaration[] typeDeclarations = new TypeDeclaration[existingCount + this.typeCount];
            if (existingCount > 0) {
                System.arraycopy(this.unitDeclaration.types, 0, typeDeclarations, 0, existingCount);
            }
            if (this.types[this.typeCount - 1].typeDeclaration.declarationSourceEnd == 0) {
                this.types[this.typeCount - 1].typeDeclaration.declarationSourceEnd = this.unitDeclaration.sourceEnd;
                this.types[this.typeCount - 1].typeDeclaration.bodyEnd = this.unitDeclaration.sourceEnd;
            }
            HashSet<TypeDeclaration> knownTypes = new HashSet<TypeDeclaration>();
            int actualCount = existingCount;
            int i = 0;
            while (i < this.typeCount) {
                TypeDeclaration typeDecl = this.types[i].updatedTypeDeclaration(0, knownTypes);
                if (typeDecl != null && (typeDecl.bits & 0x100) == 0) {
                    typeDeclarations[actualCount++] = typeDecl;
                }
                ++i;
            }
            if (actualCount != this.typeCount) {
                TypeDeclaration[] typeDeclarationArray = typeDeclarations;
                typeDeclarations = new TypeDeclaration[existingCount + actualCount];
                System.arraycopy(typeDeclarationArray, 0, typeDeclarations, 0, existingCount + actualCount);
            }
            this.unitDeclaration.types = typeDeclarations;
        }
        return this.unitDeclaration;
    }

    @Override
    public void updateParseTree() {
        this.updatedCompilationUnitDeclaration();
    }

    @Override
    public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd) {
        if (this.unitDeclaration.sourceEnd == 0) {
            this.unitDeclaration.sourceEnd = bodyEnd;
        }
    }
}

