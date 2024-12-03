/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredField;
import org.eclipse.jdt.internal.compiler.parser.RecoveredInitializer;
import org.eclipse.jdt.internal.compiler.parser.RecoveredMethod;
import org.eclipse.jdt.internal.compiler.parser.RecoveredStatement;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

public class RecoveredType
extends RecoveredStatement
implements TerminalTokens {
    public static final int MAX_TYPE_DEPTH = 256;
    public TypeDeclaration typeDeclaration;
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public RecoveredType[] memberTypes;
    public int memberTypeCount;
    public RecoveredField[] fields;
    public int fieldCount;
    public RecoveredMethod[] methods;
    public int methodCount;
    public boolean preserveContent = false;
    public int bodyEnd;
    public boolean insideEnumConstantPart = false;
    public TypeParameter[] pendingTypeParameters;
    public int pendingTypeParametersStart;
    int pendingModifiers;
    int pendingModifersSourceStart = -1;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;

    public RecoveredType(TypeDeclaration typeDeclaration, RecoveredElement parent, int bracketBalance) {
        super(typeDeclaration, parent, bracketBalance);
        this.typeDeclaration = typeDeclaration;
        this.foundOpeningBrace = typeDeclaration.allocation != null && typeDeclaration.allocation.type == null ? true : !this.bodyStartsAtHeaderEnd();
        boolean bl = this.insideEnumConstantPart = TypeDeclaration.kind(typeDeclaration.modifiers) == 3;
        if (this.foundOpeningBrace) {
            ++this.bracketBalance;
        }
        this.preserveContent = this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated;
    }

    @Override
    public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {
        if (this.typeDeclaration.declarationSourceEnd != 0 && methodDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.pendingTypeParameters = null;
            this.resetPendingModifiers();
            return this.parent.add(methodDeclaration, bracketBalanceValue);
        }
        if (this.methods == null) {
            this.methods = new RecoveredMethod[5];
            this.methodCount = 0;
        } else if (this.methodCount == this.methods.length) {
            this.methods = new RecoveredMethod[2 * this.methodCount];
            System.arraycopy(this.methods, 0, this.methods, 0, this.methodCount);
        }
        RecoveredMethod element = new RecoveredMethod(methodDeclaration, this, bracketBalanceValue, this.recoveringParser);
        this.methods[this.methodCount++] = element;
        if (this.pendingTypeParameters != null) {
            element.attach(this.pendingTypeParameters, this.pendingTypeParametersStart);
            this.pendingTypeParameters = null;
        }
        if (this.pendingAnnotationCount > 0 || this.pendingModifiers != 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        this.insideEnumConstantPart = false;
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (methodDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
        this.pendingTypeParameters = null;
        this.resetPendingModifiers();
        int mods = 0;
        if (this.parser().recoveredStaticInitializerStart != 0) {
            mods = 8;
        }
        return this.add(new Initializer(nestedBlockDeclaration, mods), bracketBalanceValue);
    }

    @Override
    public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
        RecoveredField element;
        this.pendingTypeParameters = null;
        if (this.typeDeclaration.declarationSourceEnd != 0 && fieldDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        if (this.fields == null) {
            this.fields = new RecoveredField[5];
            this.fieldCount = 0;
        } else if (this.fieldCount == this.fields.length) {
            this.fields = new RecoveredField[2 * this.fieldCount];
            System.arraycopy(this.fields, 0, this.fields, 0, this.fieldCount);
        }
        switch (fieldDeclaration.getKind()) {
            case 1: 
            case 3: {
                element = new RecoveredField(fieldDeclaration, this, bracketBalanceValue);
                break;
            }
            case 2: {
                element = new RecoveredInitializer(fieldDeclaration, this, bracketBalanceValue);
                break;
            }
            default: {
                return this;
            }
        }
        this.fields[this.fieldCount++] = element;
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (fieldDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    @Override
    public RecoveredElement add(TypeDeclaration memberTypeDeclaration, int bracketBalanceValue) {
        this.pendingTypeParameters = null;
        if (this.typeDeclaration.declarationSourceEnd != 0 && memberTypeDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            return this.parent.add(memberTypeDeclaration, bracketBalanceValue);
        }
        this.insideEnumConstantPart = false;
        if ((memberTypeDeclaration.bits & 0x200) != 0) {
            if (this.methodCount > 0) {
                RecoveredMethod lastMethod = this.methods[this.methodCount - 1];
                lastMethod.methodDeclaration.bodyEnd = 0;
                lastMethod.methodDeclaration.declarationSourceEnd = 0;
                lastMethod.bracketBalance = lastMethod.bracketBalance + 1;
                this.resetPendingModifiers();
                return lastMethod.add(memberTypeDeclaration, bracketBalanceValue);
            }
            return this;
        }
        if (this.memberTypes == null) {
            this.memberTypes = new RecoveredType[5];
            this.memberTypeCount = 0;
        } else if (this.memberTypeCount == this.memberTypes.length) {
            this.memberTypes = new RecoveredType[2 * this.memberTypeCount];
            System.arraycopy(this.memberTypes, 0, this.memberTypes, 0, this.memberTypeCount);
        }
        RecoveredType element = new RecoveredType(memberTypeDeclaration, (RecoveredElement)this, bracketBalanceValue);
        this.memberTypes[this.memberTypeCount++] = element;
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (memberTypeDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }

    public void add(TypeParameter[] parameters, int startPos) {
        this.pendingTypeParameters = parameters;
        this.pendingTypeParametersStart = startPos;
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

    public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
        if (annotCount > 0) {
            Annotation[] existingAnnotations = this.typeDeclaration.annotations;
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

    public int bodyEnd() {
        if (this.bodyEnd == 0) {
            return this.typeDeclaration.declarationSourceEnd;
        }
        return this.bodyEnd;
    }

    public boolean bodyStartsAtHeaderEnd() {
        if (this.typeDeclaration.superInterfaces == null) {
            if (this.typeDeclaration.superclass == null) {
                if (this.typeDeclaration.typeParameters == null) {
                    return this.typeDeclaration.bodyStart == this.typeDeclaration.sourceEnd + 1;
                }
                return this.typeDeclaration.bodyStart == this.typeDeclaration.typeParameters[this.typeDeclaration.typeParameters.length - 1].sourceEnd + 1;
            }
            return this.typeDeclaration.bodyStart == this.typeDeclaration.superclass.sourceEnd + 1;
        }
        if (this.typeDeclaration.permittedTypes != null) {
            return this.typeDeclaration.bodyStart == this.typeDeclaration.permittedTypes[this.typeDeclaration.permittedTypes.length - 1].sourceEnd + 1;
        }
        return this.typeDeclaration.bodyStart == this.typeDeclaration.superInterfaces[this.typeDeclaration.superInterfaces.length - 1].sourceEnd + 1;
    }

    @Override
    public RecoveredType enclosingType() {
        RecoveredElement current = this.parent;
        while (current != null) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
            current = current.parent;
        }
        return null;
    }

    public int lastMemberEnd() {
        int lastMemberEnd = this.typeDeclaration.bodyStart;
        if (this.fieldCount > 0) {
            FieldDeclaration lastField = this.fields[this.fieldCount - 1].fieldDeclaration;
            if (lastMemberEnd < lastField.declarationSourceEnd && lastField.declarationSourceEnd != 0) {
                lastMemberEnd = lastField.declarationSourceEnd;
            }
        }
        if (this.methodCount > 0) {
            AbstractMethodDeclaration lastMethod = this.methods[this.methodCount - 1].methodDeclaration;
            if (lastMemberEnd < lastMethod.declarationSourceEnd && lastMethod.declarationSourceEnd != 0) {
                lastMemberEnd = lastMethod.declarationSourceEnd;
            }
        }
        if (this.memberTypeCount > 0) {
            TypeDeclaration lastType = this.memberTypes[this.memberTypeCount - 1].typeDeclaration;
            if (lastMemberEnd < lastType.declarationSourceEnd && lastType.declarationSourceEnd != 0) {
                lastMemberEnd = lastType.declarationSourceEnd;
            }
        }
        return lastMemberEnd;
    }

    @Override
    public int getLastStart() {
        int lastMemberStart = this.typeDeclaration.bodyStart;
        if (this.fieldCount > 0) {
            FieldDeclaration lastField = this.fields[this.fieldCount - 1].fieldDeclaration;
            if (lastMemberStart < lastField.declarationSourceStart && lastField.declarationSourceStart != 0) {
                lastMemberStart = lastField.declarationSourceStart;
            }
        }
        if (this.methodCount > 0) {
            AbstractMethodDeclaration lastMethod = this.methods[this.methodCount - 1].methodDeclaration;
            if (lastMemberStart < lastMethod.declarationSourceStart && lastMethod.declarationSourceStart != 0) {
                lastMemberStart = lastMethod.declarationSourceStart;
            }
        }
        if (this.memberTypeCount > 0) {
            TypeDeclaration lastType = this.memberTypes[this.memberTypeCount - 1].typeDeclaration;
            if (lastMemberStart < lastType.declarationSourceStart && lastType.declarationSourceStart != 0) {
                lastMemberStart = lastType.declarationSourceStart;
            }
        }
        return lastMemberStart;
    }

    public char[] name() {
        return this.typeDeclaration.name;
    }

    @Override
    public ASTNode parseTree() {
        return this.typeDeclaration;
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
        return this.typeDeclaration.declarationSourceEnd;
    }

    @Override
    public String toString(int tab) {
        int i;
        StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered type:\n");
        if ((this.typeDeclaration.bits & 0x200) != 0) {
            result.append(this.tabString(tab));
            result.append(" ");
        }
        this.typeDeclaration.print(tab + 1, result);
        if (this.annotations != null) {
            i = 0;
            while (i < this.annotationCount) {
                result.append("\n");
                result.append(this.annotations[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.memberTypes != null) {
            i = 0;
            while (i < this.memberTypeCount) {
                result.append("\n");
                result.append(this.memberTypes[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.fields != null) {
            i = 0;
            while (i < this.fieldCount) {
                result.append("\n");
                result.append(this.fields[i].toString(tab + 1));
                ++i;
            }
        }
        if (this.methods != null) {
            i = 0;
            while (i < this.methodCount) {
                result.append("\n");
                result.append(this.methods[i].toString(tab + 1));
                ++i;
            }
        }
        return result.toString();
    }

    @Override
    public void updateBodyStart(int bodyStart) {
        this.foundOpeningBrace = true;
        this.typeDeclaration.bodyStart = bodyStart;
    }

    public Statement updatedStatement(int depth, Set knownTypes) {
        if ((this.typeDeclaration.bits & 0x200) != 0 && !this.preserveContent) {
            return null;
        }
        TypeDeclaration updatedType = this.updatedTypeDeclaration(depth + 1, knownTypes);
        if (updatedType != null && (updatedType.bits & 0x200) != 0) {
            QualifiedAllocationExpression allocation = updatedType.allocation;
            if (allocation.statementEnd == -1) {
                allocation.statementEnd = updatedType.declarationSourceEnd;
            }
            return allocation;
        }
        return updatedType;
    }

    public TypeDeclaration updatedTypeDeclaration(int depth, Set<TypeDeclaration> knownTypes) {
        int i;
        int i2;
        int existingCount;
        if (depth >= 256) {
            return null;
        }
        if (knownTypes.contains(this.typeDeclaration)) {
            return null;
        }
        knownTypes.add(this.typeDeclaration);
        int lastEnd = this.typeDeclaration.bodyStart;
        if (this.modifiers != 0) {
            this.typeDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.typeDeclaration.declarationSourceStart) {
                this.typeDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            existingCount = this.typeDeclaration.annotations == null ? 0 : this.typeDeclaration.annotations.length;
            Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            i2 = 0;
            while (i2 < this.annotationCount) {
                annotationReferences[i2] = this.annotations[i2].updatedAnnotationReference();
                ++i2;
            }
            this.typeDeclaration.annotations = annotationReferences;
            int start = this.annotations[0].annotation.sourceStart;
            if (start < this.typeDeclaration.declarationSourceStart) {
                this.typeDeclaration.declarationSourceStart = start;
            }
        }
        if (this.memberTypeCount > 0) {
            existingCount = this.typeDeclaration.memberTypes == null ? 0 : this.typeDeclaration.memberTypes.length;
            TypeDeclaration[] memberTypeDeclarations = new TypeDeclaration[existingCount + this.memberTypeCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.memberTypes, 0, memberTypeDeclarations, 0, existingCount);
            }
            if (this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd == 0) {
                int bodyEndValue;
                this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd = bodyEndValue = this.bodyEnd();
                this.memberTypes[this.memberTypeCount - 1].typeDeclaration.bodyEnd = bodyEndValue;
            }
            int updatedCount = 0;
            int i3 = 0;
            while (i3 < this.memberTypeCount) {
                TypeDeclaration updatedTypeDeclaration = this.memberTypes[i3].updatedTypeDeclaration(depth + 1, knownTypes);
                if (updatedTypeDeclaration != null) {
                    memberTypeDeclarations[existingCount + updatedCount++] = updatedTypeDeclaration;
                }
                ++i3;
            }
            if (updatedCount < this.memberTypeCount) {
                int length = existingCount + updatedCount;
                TypeDeclaration[] typeDeclarationArray = memberTypeDeclarations;
                memberTypeDeclarations = new TypeDeclaration[length];
                System.arraycopy(typeDeclarationArray, 0, memberTypeDeclarations, 0, length);
            }
            if (memberTypeDeclarations.length > 0) {
                this.typeDeclaration.memberTypes = memberTypeDeclarations;
                if (memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                    lastEnd = memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd;
                }
            }
        }
        if (this.fieldCount > 0) {
            existingCount = this.typeDeclaration.fields == null ? 0 : this.typeDeclaration.fields.length;
            FieldDeclaration[] fieldDeclarations = new FieldDeclaration[existingCount + this.fieldCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.fields, 0, fieldDeclarations, 0, existingCount);
            }
            if (this.fields[this.fieldCount - 1].fieldDeclaration.declarationSourceEnd == 0) {
                int temp = this.bodyEnd();
                FieldDeclaration fieldDeclaration = this.fields[this.fieldCount - 1].fieldDeclaration;
                if (temp == 0 && fieldDeclaration.sourceEnd > 0 && lastEnd > (temp = fieldDeclaration.sourceEnd)) {
                    lastEnd = temp;
                }
                fieldDeclaration.declarationSourceEnd = temp;
                fieldDeclaration.declarationEnd = temp;
            }
            i2 = 0;
            while (i2 < this.fieldCount) {
                fieldDeclarations[existingCount + i2] = this.fields[i2].updatedFieldDeclaration(depth, knownTypes);
                ++i2;
            }
            i2 = this.fieldCount - 1;
            while (i2 > 0) {
                if (fieldDeclarations[existingCount + i2 - 1].declarationSourceStart == fieldDeclarations[existingCount + i2].declarationSourceStart) {
                    fieldDeclarations[existingCount + i2 - 1].declarationSourceEnd = fieldDeclarations[existingCount + i2].declarationSourceEnd;
                    fieldDeclarations[existingCount + i2 - 1].declarationEnd = fieldDeclarations[existingCount + i2].declarationEnd;
                }
                --i2;
            }
            this.typeDeclaration.fields = fieldDeclarations;
            if (fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                lastEnd = fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd;
            }
        }
        existingCount = this.typeDeclaration.methods == null ? 0 : this.typeDeclaration.methods.length;
        boolean hasConstructor = false;
        boolean hasRecoveredConstructor = false;
        boolean hasAbstractMethods = false;
        int defaultConstructorIndex = -1;
        if (this.methodCount > 0) {
            AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[existingCount + this.methodCount];
            i = 0;
            while (i < existingCount) {
                AbstractMethodDeclaration m = this.typeDeclaration.methods[i];
                if (m.isDefaultConstructor()) {
                    defaultConstructorIndex = i;
                }
                if (m.isAbstract()) {
                    hasAbstractMethods = true;
                }
                methodDeclarations[i] = m;
                ++i;
            }
            if (this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd == 0) {
                int bodyEndValue;
                this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd = bodyEndValue = this.bodyEnd();
                this.methods[this.methodCount - 1].methodDeclaration.bodyEnd = bodyEndValue;
            }
            int totalMethods = existingCount;
            int i4 = 0;
            while (i4 < this.methodCount) {
                block55: {
                    int j = 0;
                    while (j < existingCount) {
                        if (methodDeclarations[j] != this.methods[i4].methodDeclaration) {
                            ++j;
                            continue;
                        }
                        break block55;
                    }
                    AbstractMethodDeclaration updatedMethod = this.methods[i4].updatedMethodDeclaration(depth, knownTypes);
                    if (updatedMethod.isConstructor()) {
                        hasRecoveredConstructor = true;
                    }
                    if (updatedMethod.isAbstract()) {
                        hasAbstractMethods = true;
                    }
                    methodDeclarations[totalMethods++] = updatedMethod;
                }
                ++i4;
            }
            if (totalMethods != methodDeclarations.length) {
                AbstractMethodDeclaration[] abstractMethodDeclarationArray = methodDeclarations;
                methodDeclarations = new AbstractMethodDeclaration[totalMethods];
                System.arraycopy(abstractMethodDeclarationArray, 0, methodDeclarations, 0, totalMethods);
            }
            this.typeDeclaration.methods = methodDeclarations;
            if (methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                lastEnd = methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd;
            }
            if (hasAbstractMethods) {
                this.typeDeclaration.bits |= 0x800;
            }
            hasConstructor = this.typeDeclaration.checkConstructors(this.parser());
        } else {
            int i5 = 0;
            while (i5 < existingCount) {
                if (this.typeDeclaration.methods[i5].isConstructor()) {
                    hasConstructor = true;
                }
                ++i5;
            }
        }
        if (this.typeDeclaration.needClassInitMethod()) {
            boolean alreadyHasClinit = false;
            i = 0;
            while (i < existingCount) {
                if (this.typeDeclaration.methods[i].isClinit()) {
                    alreadyHasClinit = true;
                    break;
                }
                ++i;
            }
            if (!alreadyHasClinit) {
                this.typeDeclaration.addClinit();
            }
        }
        if (defaultConstructorIndex >= 0 && hasRecoveredConstructor) {
            AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[this.typeDeclaration.methods.length - 1];
            if (defaultConstructorIndex != 0) {
                System.arraycopy(this.typeDeclaration.methods, 0, methodDeclarations, 0, defaultConstructorIndex);
            }
            if (defaultConstructorIndex != this.typeDeclaration.methods.length - 1) {
                System.arraycopy(this.typeDeclaration.methods, defaultConstructorIndex + 1, methodDeclarations, defaultConstructorIndex, this.typeDeclaration.methods.length - defaultConstructorIndex - 1);
            }
            this.typeDeclaration.methods = methodDeclarations;
        } else {
            int kind = TypeDeclaration.kind(this.typeDeclaration.modifiers);
            if (!hasConstructor && kind != 2 && kind != 4 && kind != 5 && this.typeDeclaration.allocation == null) {
                boolean insideFieldInitializer = false;
                RecoveredElement parentElement = this.parent;
                while (parentElement != null) {
                    if (parentElement instanceof RecoveredField) {
                        insideFieldInitializer = true;
                        break;
                    }
                    parentElement = parentElement.parent;
                }
                this.typeDeclaration.createDefaultConstructor(!this.parser().diet || insideFieldInitializer, true);
            }
        }
        if (this.parent instanceof RecoveredType) {
            this.typeDeclaration.bits |= 0x400;
        } else if (this.parent instanceof RecoveredMethod) {
            this.typeDeclaration.bits |= 0x100;
        }
        if (this.typeDeclaration.declarationSourceEnd == 0) {
            this.typeDeclaration.declarationSourceEnd = lastEnd;
            this.typeDeclaration.bodyEnd = lastEnd;
        }
        return this.typeDeclaration;
    }

    @Override
    public void updateFromParserState() {
        if (this.bodyStartsAtHeaderEnd() && this.typeDeclaration.allocation == null) {
            Parser parser = this.parser();
            if (parser.listLength > 0 && parser.astLengthPtr > 0) {
                boolean canConsume;
                int length = parser.astLengthStack[parser.astLengthPtr];
                int astPtr = parser.astPtr - length;
                boolean bl = canConsume = astPtr >= 0;
                if (canConsume) {
                    if (!(parser.astStack[astPtr] instanceof TypeDeclaration)) {
                        canConsume = false;
                    }
                    int i = 1;
                    int max = length + 1;
                    while (i < max) {
                        if (!(parser.astStack[astPtr + i] instanceof TypeReference)) {
                            canConsume = false;
                            break;
                        }
                        ++i;
                    }
                }
                if (canConsume) {
                    parser.consumeClassHeaderImplements();
                }
            } else if (parser.listTypeParameterLength > 0) {
                boolean canConsume;
                int genericsPtr = parser.genericsPtr;
                int length = parser.listTypeParameterLength;
                boolean bl = canConsume = genericsPtr + 1 >= length && parser.astPtr > -1;
                if (canConsume) {
                    if (!(parser.astStack[parser.astPtr] instanceof TypeDeclaration)) {
                        canConsume = false;
                    }
                    while (genericsPtr + 1 > length && !(parser.genericsStack[genericsPtr] instanceof TypeParameter)) {
                        --genericsPtr;
                    }
                    int i = 0;
                    while (i < length) {
                        if (!(parser.genericsStack[genericsPtr - i] instanceof TypeParameter)) {
                            canConsume = false;
                            break;
                        }
                        ++i;
                    }
                }
                if (canConsume) {
                    TypeDeclaration typeDecl = (TypeDeclaration)parser.astStack[parser.astPtr];
                    typeDecl.typeParameters = new TypeParameter[length];
                    System.arraycopy(parser.genericsStack, genericsPtr - length + 1, typeDecl.typeParameters, 0, length);
                    typeDecl.bodyStart = typeDecl.typeParameters[length - 1].declarationSourceEnd + 1;
                    parser.listTypeParameterLength = 0;
                    parser.lastCheckPoint = typeDecl.bodyStart;
                }
            }
        }
    }

    @Override
    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (--this.bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            this.bodyEnd = braceStart - 1;
            return this.parent;
        }
        return this;
    }

    @Override
    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        if (this.bracketBalance == 0) {
            Parser parser = this.parser();
            switch (parser.lastIgnoredToken) {
                case -1: 
                case 14: 
                case 15: 
                case 16: 
                case 92: 
                case 127: 
                case 130: {
                    if (parser.recoveredStaticInitializerStart == 0) break;
                }
                default: {
                    this.foundOpeningBrace = true;
                    this.bracketBalance = 1;
                }
            }
        }
        if (this.bracketBalance == 1) {
            Initializer init;
            Block block = new Block(0);
            Parser parser = this.parser();
            block.sourceStart = parser.scanner.startPosition;
            if (parser.recoveredStaticInitializerStart == 0) {
                init = new Initializer(block, 0);
            } else {
                init = new Initializer(block, 8);
                init.declarationSourceStart = parser.recoveredStaticInitializerStart;
            }
            init.bodyStart = parser.scanner.currentPosition;
            return this.add(init, 1);
        }
        return super.updateOnOpeningBrace(braceStart, braceEnd);
    }

    @Override
    public void updateParseTree() {
        this.updatedTypeDeclaration(0, new HashSet<TypeDeclaration>());
    }

    @Override
    public void updateSourceEndIfNecessary(int start, int end) {
        if (this.typeDeclaration.declarationSourceEnd == 0) {
            this.bodyEnd = 0;
            this.typeDeclaration.declarationSourceEnd = end;
            this.typeDeclaration.bodyEnd = end;
        }
    }

    public void annotationsConsumed(Annotation[] consumedAnnotations) {
        RecoveredAnnotation[] keep = new RecoveredAnnotation[this.pendingAnnotationCount];
        int numKeep = 0;
        int pendingCount = this.pendingAnnotationCount;
        int consumedLength = consumedAnnotations.length;
        int i = 0;
        while (i < pendingCount) {
            block4: {
                Annotation pendingAnnotationAST = this.pendingAnnotations[i].annotation;
                int j = 0;
                while (j < consumedLength) {
                    if (consumedAnnotations[j] != pendingAnnotationAST) {
                        ++j;
                        continue;
                    }
                    break block4;
                }
                keep[numKeep++] = this.pendingAnnotations[i];
            }
            ++i;
        }
        if (numKeep != this.pendingAnnotationCount) {
            this.pendingAnnotations = keep;
            this.pendingAnnotationCount = numKeep;
        }
    }
}

