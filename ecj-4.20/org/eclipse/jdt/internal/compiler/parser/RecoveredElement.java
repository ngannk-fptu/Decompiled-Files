/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveredBlock;
import org.eclipse.jdt.internal.compiler.parser.RecoveredInitializer;
import org.eclipse.jdt.internal.compiler.parser.RecoveredMethod;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.util.Util;

public class RecoveredElement {
    public RecoveredElement parent;
    public int bracketBalance;
    public boolean foundOpeningBrace;
    protected Parser recoveringParser;
    public int lambdaNestLevel;

    public RecoveredElement(RecoveredElement parent, int bracketBalance) {
        this(parent, bracketBalance, null);
    }

    public RecoveredElement(RecoveredElement parent, int bracketBalance, Parser parser) {
        this.parent = parent;
        this.bracketBalance = bracketBalance;
        this.recoveringParser = parser;
    }

    public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(annotationStart - 1));
        return this.parent.addAnnotationName(identifierPtr, identifierLengthPtr, annotationStart, bracketBalanceValue);
    }

    public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(methodDeclaration.declarationSourceStart - 1));
        return this.parent.add(methodDeclaration, bracketBalanceValue);
    }

    public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(nestedBlockDeclaration.sourceStart - 1));
        return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
    }

    public RecoveredElement add(ModuleStatement moduleStatement, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(moduleStatement.declarationSourceStart - 1));
        return this.parent.add(moduleStatement, bracketBalanceValue);
    }

    public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
        return this.parent.add(fieldDeclaration, bracketBalanceValue);
    }

    public RecoveredElement add(ImportReference importReference, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(importReference.declarationSourceStart - 1));
        return this.parent.add(importReference, bracketBalanceValue);
    }

    public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(localDeclaration.declarationSourceStart - 1));
        return this.parent.add(localDeclaration, bracketBalanceValue);
    }

    public RecoveredElement add(Statement statement, int bracketBalanceValue) {
        TypeDeclaration typeDeclaration;
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        if (this instanceof RecoveredType && (typeDeclaration = ((RecoveredType)this).typeDeclaration) != null && (typeDeclaration.bits & 0x200) != 0 && statement.sourceStart > typeDeclaration.sourceStart && statement.sourceEnd < typeDeclaration.sourceEnd) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(statement.sourceStart - 1));
        return this.parent.add(statement, bracketBalanceValue);
    }

    public RecoveredElement add(ModuleDeclaration moduleDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(moduleDeclaration.declarationSourceStart - 1));
        return this.parent.add(moduleDeclaration, bracketBalanceValue);
    }

    public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(typeDeclaration.declarationSourceStart - 1));
        return this.parent.add(typeDeclaration, bracketBalanceValue);
    }

    protected void addBlockStatement(RecoveredBlock recoveredBlock) {
        Block block = recoveredBlock.blockDeclaration;
        if (block.statements != null) {
            Statement[] statements = block.statements;
            int i = 0;
            while (i < statements.length) {
                recoveredBlock.add(statements[i], 0);
                ++i;
            }
        }
    }

    public void addModifier(int flag, int modifiersSourceStart) {
    }

    public int depth() {
        int depth = 0;
        RecoveredElement current = this;
        while ((current = current.parent) != null) {
            ++depth;
        }
        return depth;
    }

    public RecoveredInitializer enclosingInitializer() {
        RecoveredElement current = this;
        while (current != null) {
            if (current instanceof RecoveredInitializer) {
                return (RecoveredInitializer)current;
            }
            current = current.parent;
        }
        return null;
    }

    public RecoveredMethod enclosingMethod() {
        RecoveredElement current = this;
        while (current != null) {
            if (current instanceof RecoveredMethod) {
                return (RecoveredMethod)current;
            }
            current = current.parent;
        }
        return null;
    }

    public RecoveredType enclosingType() {
        RecoveredElement current = this;
        while (current != null) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
            current = current.parent;
        }
        return null;
    }

    public Parser parser() {
        RecoveredElement current = this;
        while (current != null) {
            if (current.recoveringParser != null) {
                return current.recoveringParser;
            }
            current = current.parent;
        }
        return null;
    }

    public ASTNode parseTree() {
        return null;
    }

    public void resetPendingModifiers() {
    }

    public void preserveEnclosingBlocks() {
        RecoveredElement current = this;
        while (current != null) {
            if (current instanceof RecoveredBlock) {
                ((RecoveredBlock)current).preserveContent = true;
            }
            if (current instanceof RecoveredType) {
                ((RecoveredType)current).preserveContent = true;
            }
            current = current.parent;
        }
    }

    public int previousAvailableLineEnd(int position) {
        Parser parser = this.parser();
        if (parser == null) {
            return position;
        }
        Scanner scanner = parser.scanner;
        if (scanner.lineEnds == null) {
            return position;
        }
        int index = Util.getLineNumber(position, scanner.lineEnds, 0, scanner.linePtr);
        if (index < 2) {
            return position;
        }
        int previousLineEnd = scanner.lineEnds[index - 2];
        char[] source = scanner.source;
        int i = previousLineEnd + 1;
        while (i < position) {
            if (source[i] != ' ' && source[i] != '\t') {
                return position;
            }
            ++i;
        }
        return previousLineEnd;
    }

    public int sourceEnd() {
        return 0;
    }

    public int getLastStart() {
        ASTNode parseTree = this.parseTree();
        return parseTree == null ? -1 : parseTree.sourceStart;
    }

    protected String tabString(int tab) {
        StringBuffer result = new StringBuffer();
        int i = tab;
        while (i > 0) {
            result.append("  ");
            --i;
        }
        return result.toString();
    }

    public RecoveredElement topElement() {
        RecoveredElement current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    public String toString() {
        return this.toString(0);
    }

    public String toString(int tab) {
        return super.toString();
    }

    public RecoveredType type() {
        RecoveredElement current = this;
        while (current != null) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
            current = current.parent;
        }
        return null;
    }

    public void updateBodyStart(int bodyStart) {
        this.foundOpeningBrace = true;
    }

    public void updateFromParserState() {
    }

    public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
        if (--this.bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            return this.parent;
        }
        return this;
    }

    public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd) {
        if (this.bracketBalance++ == 0) {
            this.updateBodyStart(braceEnd + 1);
            return this;
        }
        return null;
    }

    public void updateParseTree() {
    }

    public void updateSourceEndIfNecessary(int braceStart, int braceEnd) {
    }

    public void updateSourceEndIfNecessary(int sourceEnd) {
        this.updateSourceEndIfNecessary(sourceEnd + 1, sourceEnd);
    }
}

