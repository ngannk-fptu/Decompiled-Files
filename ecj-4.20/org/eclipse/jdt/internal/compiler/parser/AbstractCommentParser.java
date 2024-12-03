/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.util.Util;

public abstract class AbstractCommentParser
implements JavadocTagConstants {
    public static final int COMPIL_PARSER = 1;
    public static final int DOM_PARSER = 2;
    public static final int SELECTION_PARSER = 4;
    public static final int COMPLETION_PARSER = 8;
    public static final int SOURCE_PARSER = 16;
    public static final int FORMATTER_COMMENT_PARSER = 32;
    protected static final int PARSER_KIND = 255;
    protected static final int TEXT_PARSE = 256;
    protected static final int TEXT_VERIF = 512;
    protected static final int QUALIFIED_NAME_RECOVERY = 1;
    protected static final int ARGUMENT_RECOVERY = 2;
    protected static final int ARGUMENT_TYPE_RECOVERY = 3;
    protected static final int EMPTY_ARGUMENT_RECOVERY = 4;
    public Scanner scanner;
    public char[] source;
    protected Parser sourceParser;
    private int currentTokenType = -1;
    public boolean checkDocComment = false;
    public boolean setJavadocPositions = false;
    public boolean reportProblems;
    protected long complianceLevel;
    protected long sourceLevel;
    protected long[] inheritedPositions;
    protected int inheritedPositionsPtr;
    private static final int INHERITED_POSITIONS_ARRAY_INCREMENT = 4;
    protected boolean deprecated;
    protected Object returnStatement;
    protected int javadocStart;
    protected int javadocEnd;
    protected int javadocTextStart;
    protected int javadocTextEnd = -1;
    protected int firstTagPosition;
    protected int index;
    protected int lineEnd;
    protected int tokenPreviousPosition;
    protected int lastIdentifierEndPosition;
    protected int starPosition;
    protected int textStart;
    protected int memberStart;
    protected int tagSourceStart;
    protected int tagSourceEnd;
    protected int inlineTagStart;
    protected int[] lineEnds;
    protected boolean lineStarted = false;
    protected boolean inlineTagStarted = false;
    protected boolean abort = false;
    protected int kind;
    protected int tagValue = 0;
    protected int lastBlockTagValue = 0;
    private int linePtr;
    private int lastLinePtr;
    protected int identifierPtr;
    protected char[][] identifierStack;
    protected int identifierLengthPtr;
    protected int[] identifierLengthStack;
    protected long[] identifierPositionStack;
    protected static final int AST_STACK_INCREMENT = 10;
    protected int astPtr;
    protected Object[] astStack;
    protected int astLengthPtr;
    protected int[] astLengthStack;
    protected int usesReferencesPtr = -1;
    protected TypeReference[] usesReferencesStack;
    protected int providesReferencesPtr = -1;
    protected TypeReference[] providesReferencesStack;

    protected AbstractCommentParser(Parser sourceParser) {
        this.sourceParser = sourceParser;
        this.scanner = new Scanner(false, false, false, 0x2F0000L, null, null, true, sourceParser != null ? this.sourceParser.options.enablePreviewFeatures : false);
        this.identifierStack = new char[20][];
        this.identifierPositionStack = new long[20];
        this.identifierLengthStack = new int[10];
        this.astStack = new Object[30];
        this.astLengthStack = new int[20];
        boolean bl = this.reportProblems = sourceParser != null;
        if (sourceParser != null) {
            this.checkDocComment = this.sourceParser.options.docCommentSupport;
            this.scanner.sourceLevel = this.sourceLevel = this.sourceParser.options.sourceLevel;
            this.complianceLevel = this.sourceParser.options.complianceLevel;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean commentParse() {
        validComment = true;
        try {
            block80: {
                block78: {
                    block79: {
                        this.astLengthPtr = -1;
                        this.astPtr = -1;
                        this.identifierPtr = -1;
                        this.currentTokenType = -1;
                        this.setInlineTagStarted(false);
                        this.inlineTagStart = -1;
                        this.lineStarted = false;
                        this.returnStatement = null;
                        this.inheritedPositions = null;
                        this.lastBlockTagValue = 0;
                        this.deprecated = false;
                        this.lastLinePtr = this.getLineNumber(this.javadocEnd);
                        this.textStart = -1;
                        this.abort = false;
                        previousChar = '\u0000';
                        invalidTagLineEnd = -1;
                        invalidInlineTagLineEnd = -1;
                        lineHasStar = true;
                        verifText = (this.kind & 512) != 0;
                        isDomParser = (this.kind & 2) != 0;
                        isFormatterParser = (this.kind & 32) != 0;
                        lastStarPosition = -1;
                        this.linePtr = this.getLineNumber(this.firstTagPosition);
                        v0 = realStart = this.linePtr == 1 ? this.javadocStart : this.scanner.getLineEnd(this.linePtr - 1) + 1;
                        if (realStart < this.javadocStart) {
                            realStart = this.javadocStart;
                        }
                        this.scanner.resetTo(realStart, this.javadocEnd);
                        this.index = realStart;
                        if (realStart == this.javadocStart) {
                            this.readChar();
                            this.readChar();
                        }
                        previousPosition = this.index;
                        nextCharacter = '\u0000';
                        if (realStart == this.javadocStart) {
                            nextCharacter = this.readChar();
                            while (true) {
                                if (this.peekChar() != '*') {
                                    this.javadocTextStart = this.index;
                                    break;
                                }
                                nextCharacter = this.readChar();
                            }
                        }
                        this.lineEnd = this.linePtr == this.lastLinePtr ? this.javadocEnd : this.scanner.getLineEnd(this.linePtr) - 1;
                        this.javadocTextEnd = this.javadocEnd - 2;
                        considerTagAsPlainText = false;
                        openingBraces = 0;
                        textEndPosition = -1;
                        block18: while (true) lbl-1000:
                        // 17 sources

                        {
                            if (this.abort || this.index >= this.javadocEnd) {
                                this.javadocTextEnd = this.starPosition - 1;
                                if (!this.inlineTagStarted && !considerTagAsPlainText) break block78;
                                if (this.reportProblems) {
                                    v1 = end = this.javadocTextEnd < invalidInlineTagLineEnd ? this.javadocTextEnd : invalidInlineTagLineEnd;
                                }
                                break block79;
                            }
                            previousPosition = this.index;
                            previousChar = nextCharacter;
                            if (this.index > this.lineEnd + 1) {
                                this.updateLineEnd();
                            }
                            if (this.currentTokenType < 0) {
                                nextCharacter = this.readChar();
                            } else {
                                previousPosition = this.scanner.getCurrentTokenStartPosition();
                                switch (this.currentTokenType) {
                                    case 33: {
                                        nextCharacter = '}';
                                        break;
                                    }
                                    case 8: {
                                        nextCharacter = '*';
                                        break;
                                    }
                                    default: {
                                        nextCharacter = this.scanner.currentCharacter;
                                    }
                                }
                                this.consumeToken();
                            }
                            switch (nextCharacter) {
                                case 64: {
                                    if (!considerTagAsPlainText) ** GOTO lbl86
                                    if (!this.lineStarted) {
                                        if (openingBraces > 0 && this.reportProblems) {
                                            this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, invalidInlineTagLineEnd);
                                        }
                                        considerTagAsPlainText = false;
                                        this.inlineTagStarted = false;
                                        openingBraces = 0;
                                    }
                                    ** GOTO lbl129
lbl86:
                                    // 1 sources

                                    if (this.lineStarted && previousChar != '{') ** GOTO lbl123
                                    if (this.inlineTagStarted) {
                                        this.setInlineTagStarted(false);
                                        if (this.reportProblems) {
                                            end = previousPosition < invalidInlineTagLineEnd ? previousPosition : invalidInlineTagLineEnd;
                                            this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                                        }
                                        validComment = false;
                                        if (this.textStart != -1 && this.textStart < textEndPosition) {
                                            this.pushText(this.textStart, textEndPosition);
                                        }
                                        if (isDomParser || isFormatterParser) {
                                            this.refreshInlineTagPosition(textEndPosition);
                                        }
                                    }
                                    if (previousChar == '{') {
                                        if (this.textStart != -1 && this.textStart < textEndPosition) {
                                            this.pushText(this.textStart, textEndPosition);
                                        }
                                        this.setInlineTagStarted(true);
                                        invalidInlineTagLineEnd = this.lineEnd;
                                    } else if (this.textStart != -1 && this.textStart < invalidTagLineEnd) {
                                        this.pushText(this.textStart, invalidTagLineEnd);
                                    }
                                    this.scanner.resetTo(this.index, this.javadocEnd);
                                    this.currentTokenType = -1;
                                    try {
                                        if (!this.parseTag(previousPosition)) {
                                            validComment = false;
                                            if (isDomParser) {
                                                this.createTag();
                                            }
                                            this.textStart = this.tagSourceEnd + 1;
                                            invalidTagLineEnd = this.lineEnd;
                                            textEndPosition = this.index;
                                        }
                                        if (!(isFormatterParser || this.tagValue != 19 && this.tagValue != 18)) {
                                            considerTagAsPlainText = true;
                                            ++openingBraces;
                                        }
                                        ** GOTO lbl129
                                    }
                                    catch (InvalidInputException v2) {
                                        this.consumeToken();
                                    }
                                    ** GOTO lbl129
lbl123:
                                    // 1 sources

                                    textEndPosition = this.index;
                                    if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                                        this.refreshReturnStatement();
                                    } else if (isFormatterParser && this.textStart == -1) {
                                        this.textStart = previousPosition;
                                    }
lbl129:
                                    // 8 sources

                                    this.lineStarted = true;
                                    ** break;
                                }
                                case 10: 
                                case 13: {
                                    if (this.lineStarted) {
                                        if (isFormatterParser && !ScannerHelper.isWhitespace(previousChar)) {
                                            textEndPosition = previousPosition;
                                        }
                                        if (this.textStart != -1 && this.textStart < textEndPosition) {
                                            this.pushText(this.textStart, textEndPosition);
                                        }
                                    }
                                    this.lineStarted = false;
                                    lineHasStar = false;
                                    this.textStart = -1;
                                    ** break;
                                }
                                case 125: {
                                    if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                                        this.refreshReturnStatement();
                                    }
                                    if (considerTagAsPlainText) {
                                        invalidInlineTagLineEnd = this.lineEnd;
                                        if (--openingBraces == 0) {
                                            considerTagAsPlainText = false;
                                        }
                                    }
                                    if (this.inlineTagStarted) {
                                        textEndPosition = this.index - 1;
                                        if (!considerTagAsPlainText) {
                                            if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                                                this.pushText(this.textStart, textEndPosition);
                                            }
                                            this.refreshInlineTagPosition(previousPosition);
                                        }
                                        if (!isFormatterParser && !considerTagAsPlainText) {
                                            this.textStart = this.index;
                                        }
                                        this.setInlineTagStarted(false);
                                    } else if (!this.lineStarted) {
                                        this.textStart = previousPosition;
                                    }
                                    this.lineStarted = true;
                                    textEndPosition = this.index;
                                    ** break;
                                }
                                case 123: {
                                    if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                                        this.refreshReturnStatement();
                                    }
                                    if (considerTagAsPlainText) {
                                        ++openingBraces;
                                    } else if (this.inlineTagStarted) {
                                        this.setInlineTagStarted(false);
                                        if (this.reportProblems) {
                                            end = previousPosition < invalidInlineTagLineEnd ? previousPosition : invalidInlineTagLineEnd;
                                            this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                                        }
                                        if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                                            this.pushText(this.textStart, textEndPosition);
                                        }
                                        this.refreshInlineTagPosition(textEndPosition);
                                        textEndPosition = this.index;
                                    } else if (this.peekChar() != '@') {
                                        if (this.textStart == -1) {
                                            this.textStart = previousPosition;
                                        }
                                        textEndPosition = this.index;
                                    }
                                    if (!this.lineStarted) {
                                        this.textStart = previousPosition;
                                    }
                                    this.lineStarted = true;
                                    if (considerTagAsPlainText) continue block18;
                                    this.inlineTagStart = previousPosition;
                                    ** break;
                                }
                                case 42: {
                                    lastStarPosition = previousPosition;
                                    if (previousChar == '*') continue block18;
                                    this.starPosition = previousPosition;
                                    if (!isDomParser && !isFormatterParser) continue block18;
                                    if (lineHasStar) {
                                        this.lineStarted = true;
                                        if (this.textStart == -1) {
                                            this.textStart = previousPosition;
                                            if (this.index <= this.javadocTextEnd) {
                                                textEndPosition = this.index;
                                            }
                                        }
                                    }
                                    if (this.lineStarted) continue block18;
                                    lineHasStar = true;
                                    ** break;
                                }
                                case 9: 
                                case 12: 
                                case 32: {
                                    if (isFormatterParser) {
                                        if (ScannerHelper.isWhitespace(previousChar)) continue block18;
                                        textEndPosition = previousPosition;
                                        ** break;
                                    }
                                    if (!this.lineStarted || !isDomParser) continue block18;
                                    textEndPosition = this.index;
                                    ** break;
                                }
                                case 47: {
                                    if (previousChar != '*') break;
                                    ** break;
                                }
                            }
                            if (isFormatterParser && nextCharacter == '<') {
                                initialIndex = this.index;
                                this.scanner.resetTo(this.index, this.javadocEnd);
                                if (!ScannerHelper.isWhitespace(previousChar)) {
                                    textEndPosition = previousPosition;
                                }
                                if (this.parseHtmlTag(previousPosition, textEndPosition)) continue;
                                if (this.abort) {
                                    return false;
                                }
                                this.scanner.currentPosition = initialIndex;
                                this.index = initialIndex;
                            }
                            if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                                this.refreshReturnStatement();
                            }
                            if (!this.lineStarted || this.textStart == -1) {
                                this.textStart = previousPosition;
                            }
                            this.lineStarted = true;
                            textEndPosition = this.index;
                        }
                        if (this.index >= this.javadocEnd) {
                            end = invalidInlineTagLineEnd;
                        }
                        this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                    }
                    if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                        this.pushText(this.textStart, textEndPosition);
                    }
                    this.refreshInlineTagPosition(textEndPosition);
                    this.setInlineTagStarted(false);
                    break block80;
                }
                if (this.lineStarted && this.textStart != -1 && this.textStart <= textEndPosition && (this.textStart < this.starPosition || this.starPosition == lastStarPosition)) {
                    this.pushText(this.textStart, textEndPosition);
                }
            }
            this.updateDocComment();
            return validComment;
        }
        catch (Exception v3) {
            return false;
        }
    }

    protected void consumeToken() {
        this.currentTokenType = -1;
        this.updateLineEnd();
    }

    protected abstract Object createArgumentReference(char[] var1, int var2, boolean var3, Object var4, long[] var5, long var6) throws InvalidInputException;

    protected boolean createFakeReference(int start) {
        return true;
    }

    protected abstract Object createFieldReference(Object var1) throws InvalidInputException;

    protected abstract Object createMethodReference(Object var1, List var2) throws InvalidInputException;

    protected Object createReturnStatement() {
        return null;
    }

    protected abstract void createTag();

    protected abstract Object createTypeReference(int var1);

    protected abstract Object createModuleTypeReference(int var1, int var2);

    private int getIndexPosition() {
        if (this.index > this.lineEnd) {
            return this.lineEnd;
        }
        return this.index - 1;
    }

    private int getLineNumber(int position) {
        if (this.scanner.linePtr != -1) {
            return Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr);
        }
        if (this.lineEnds == null) {
            return 1;
        }
        return Util.getLineNumber(position, this.lineEnds, 0, this.lineEnds.length - 1);
    }

    protected int getTokenEndPosition() {
        if (this.scanner.getCurrentTokenEndPosition() > this.lineEnd) {
            return this.lineEnd;
        }
        return this.scanner.getCurrentTokenEndPosition();
    }

    protected int getCurrentTokenType() {
        return this.currentTokenType;
    }

    protected Object parseArguments(Object receiver) throws InvalidInputException {
        int modulo = 0;
        int iToken = 0;
        char[] argName = null;
        ArrayList<Object> arguments = new ArrayList<Object>(10);
        int start = this.scanner.getCurrentTokenStartPosition();
        Object typeRef = null;
        int dim = 0;
        boolean isVarargs = false;
        long[] dimPositions = new long[20];
        char[] name = null;
        long argNamePos = -1L;
        block2: while (this.index < this.scanner.eofPosition) {
            Object argument;
            int dimStart;
            boolean firstArg;
            try {
                typeRef = this.parseQualifiedName(false);
                if (this.abort) {
                    return null;
                }
            }
            catch (InvalidInputException invalidInputException) {
                break;
            }
            boolean bl = firstArg = modulo == 0;
            if (!firstArg ? iToken % modulo != 0 : iToken != 0) break;
            if (typeRef == null) {
                if (!firstArg || this.currentTokenType != 26) break;
                if (!this.verifySpaceOrEndComment()) {
                    int end;
                    int n = end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
                    if (this.source[end] == '\n') {
                        --end;
                    }
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
                    }
                    return null;
                }
                this.lineStarted = true;
                return this.createMethodReference(receiver, null);
            }
            ++iToken;
            dim = 0;
            isVarargs = false;
            if (this.readToken() == 6) {
                while (this.readToken() == 6) {
                    dimStart = this.scanner.getCurrentTokenStartPosition();
                    this.consumeToken();
                    if (this.readToken() != 69) break block2;
                    this.consumeToken();
                    dimPositions[dim++] = ((long)dimStart << 32) + (long)this.scanner.getCurrentTokenEndPosition();
                }
            } else if (this.readToken() == 118) {
                dimStart = this.scanner.getCurrentTokenStartPosition();
                dimPositions[dim++] = ((long)dimStart << 32) + (long)this.scanner.getCurrentTokenEndPosition();
                this.consumeToken();
                isVarargs = true;
            }
            argNamePos = -1L;
            if (this.readToken() == 22) {
                this.consumeToken();
                if (!firstArg ? iToken % modulo != 1 : iToken != 1) break;
                if (argName == null && !firstArg) break;
                argName = this.scanner.getCurrentIdentifierSource();
                argNamePos = ((long)this.scanner.getCurrentTokenStartPosition() << 32) + (long)this.scanner.getCurrentTokenEndPosition();
                ++iToken;
            } else if (argName != null) break;
            if (firstArg) {
                modulo = iToken + 1;
            } else if (iToken % modulo != modulo - 1) break;
            int token = this.readToken();
            char[] cArray = name = argName == null ? CharOperation.NO_CHAR : argName;
            if (token == 32) {
                argument = this.createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
                if (this.abort) {
                    return null;
                }
                arguments.add(argument);
                this.consumeToken();
                ++iToken;
                continue;
            }
            if (token != 26) break;
            if (!this.verifySpaceOrEndComment()) {
                int end;
                int n = end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
                if (this.source[end] == '\n') {
                    --end;
                }
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
                }
                return null;
            }
            argument = this.createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
            if (this.abort) {
                return null;
            }
            arguments.add(argument);
            this.consumeToken();
            return this.createMethodReference(receiver, arguments);
        }
        throw new InvalidInputException();
    }

    protected boolean parseHtmlTag(int previousPosition, int endTextPosition) throws InvalidInputException {
        return false;
    }

    /*
     * Exception decompiling
     */
    protected boolean parseHref() throws InvalidInputException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK], 0[TRYBLOCK]], but top level block is 8[DOLOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected boolean parseIdentifierTag(boolean report) {
        int token = this.readTokenSafely();
        switch (token) {
            case 22: {
                this.pushIdentifier(true, false);
                return true;
            }
        }
        if (report) {
            this.sourceParser.problemReporter().javadocMissingIdentifier(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
        }
        return false;
    }

    protected Object parseMember(Object receiver) throws InvalidInputException {
        int start;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.memberStart = start = this.scanner.getCurrentTokenStartPosition();
        if (this.readToken() == 22) {
            if (this.scanner.currentCharacter == '.') {
                this.parseQualifiedName(true);
            } else {
                this.consumeToken();
                this.pushIdentifier(true, false);
            }
            int previousPosition = this.index;
            if (this.readToken() == 23) {
                this.consumeToken();
                start = this.scanner.getCurrentTokenStartPosition();
                try {
                    return this.parseArguments(receiver);
                }
                catch (InvalidInputException invalidInputException) {
                    int end = this.scanner.getCurrentTokenEndPosition() < this.lineEnd ? this.scanner.getCurrentTokenEndPosition() : this.scanner.getCurrentTokenStartPosition();
                    int n = end = end < this.lineEnd ? end : this.lineEnd;
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidSeeReferenceArgs(start, end);
                    }
                    return null;
                }
            }
            this.index = previousPosition;
            this.scanner.currentPosition = previousPosition;
            this.currentTokenType = -1;
            if (!this.verifySpaceOrEndComment()) {
                int end;
                int n = end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
                if (this.source[end] == '\n') {
                    --end;
                }
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
                }
                return null;
            }
            return this.createFieldReference(receiver);
        }
        int end = this.getTokenEndPosition() - 1;
        int n = end = start > end ? start : end;
        if (this.reportProblems) {
            this.sourceParser.problemReporter().javadocInvalidReference(start, end);
        }
        this.index = this.tokenPreviousPosition;
        this.scanner.currentPosition = this.tokenPreviousPosition;
        this.currentTokenType = -1;
        return null;
    }

    /*
     * Unable to fully structure code
     */
    protected boolean parseParam() throws InvalidInputException {
        start = this.tagSourceStart;
        end = this.tagSourceEnd;
        tokenWhiteSpace = this.scanner.tokenizeWhiteSpace;
        this.scanner.tokenizeWhiteSpace = true;
        try {
            v0 = isCompletionParser = (this.kind & 8) != 0;
            if (this.scanner.currentCharacter != ' ' && !ScannerHelper.isWhitespace(this.scanner.currentCharacter)) {
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocInvalidTag(start, this.scanner.getCurrentTokenEndPosition());
                }
                if (!isCompletionParser) {
                    this.scanner.currentPosition = start;
                    this.index = start;
                }
                this.currentTokenType = -1;
                return false;
            }
            this.identifierPtr = -1;
            this.identifierLengthPtr = -1;
            hasMultiLines = this.scanner.currentPosition > this.lineEnd + 1;
            isTypeParam = false;
            valid = true;
            empty = true;
            mayBeGeneric = this.sourceLevel >= 0x310000L;
            token = -1;
            block35: while (true) {
                this.currentTokenType = -1;
                try {
                    token = this.readToken();
                }
                catch (InvalidInputException v1) {
                    valid = false;
                }
                switch (token) {
                    case 22: {
                        if (valid) {
                            this.pushIdentifier(true, false);
                            start = this.scanner.getCurrentTokenStartPosition();
                            end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            break block35;
                        }
                    }
                    case 11: {
                        if (valid && mayBeGeneric) {
                            this.pushIdentifier(true, true);
                            start = this.scanner.getCurrentTokenStartPosition();
                            end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            isTypeParam = true;
                            break block35;
                        }
                    }
                    default: {
                        if (token == 18) {
                            isTypeParam = true;
                        }
                        if (valid && !hasMultiLines) {
                            start = this.scanner.getCurrentTokenStartPosition();
                        }
                        valid = false;
                        if (!hasMultiLines) {
                            empty = false;
                            end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            continue block35;
                        }
                        end = this.lineEnd;
                    }
                    case 1000: {
                        if (this.scanner.currentPosition > this.lineEnd + 1) {
                            hasMultiLines = true;
                        }
                        if (!valid) ** break;
                        continue block35;
                    }
                    case 64: {
                        if (this.reportProblems) {
                            if (empty) {
                                this.sourceParser.problemReporter().javadocMissingParamName(start, end, this.sourceParser.modifiers);
                            } else if (mayBeGeneric && isTypeParam) {
                                this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                            } else {
                                this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
                            }
                        }
                        if (!isCompletionParser) {
                            this.scanner.currentPosition = start;
                            this.index = start;
                        }
                        this.currentTokenType = -1;
                        return false;
                    }
                }
                break;
            }
            if (isTypeParam && mayBeGeneric) {
                block36: while (true) {
                    this.currentTokenType = -1;
                    try {
                        token = this.readToken();
                    }
                    catch (InvalidInputException v2) {
                        valid = false;
                    }
                    switch (token) {
                        case 1000: {
                            if (valid && this.scanner.currentPosition <= this.lineEnd + 1) continue block36;
                        }
                        case 64: {
                            if (this.reportProblems) {
                                this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                            }
                            if (!isCompletionParser) {
                                this.scanner.currentPosition = start;
                                this.index = start;
                            }
                            this.currentTokenType = -1;
                            return false;
                        }
                        case 22: {
                            v3 = end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            if (!valid) continue block36;
                            this.pushIdentifier(false, false);
                            break block36;
                        }
                        default: {
                            end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            valid = false;
                            continue block36;
                        }
                    }
                    break;
                }
                spaces = false;
                block37: while (true) {
                    this.currentTokenType = -1;
                    try {
                        token = this.readToken();
                    }
                    catch (InvalidInputException v4) {
                        valid = false;
                    }
                    switch (token) {
                        case 1000: {
                            if (this.scanner.currentPosition > this.lineEnd + 1) {
                                hasMultiLines = true;
                                valid = false;
                            }
                            spaces = true;
                            if (valid) continue block37;
                        }
                        case 64: {
                            if (this.reportProblems) {
                                this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                            }
                            if (!isCompletionParser) {
                                this.scanner.currentPosition = start;
                                this.index = start;
                            }
                            this.currentTokenType = -1;
                            return false;
                        }
                        case 15: {
                            v5 = end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            if (!valid) continue block37;
                            this.pushIdentifier(false, true);
                            break block37;
                        }
                        default: {
                            if (!spaces) {
                                end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                            }
                            valid = false;
                            continue block37;
                        }
                    }
                    break;
                }
            }
            if (valid) {
                this.currentTokenType = -1;
                restart = this.scanner.currentPosition;
                try {
                    token = this.readTokenAndConsume();
                }
                catch (InvalidInputException v6) {
                    valid = false;
                }
                if (token == 1000) {
                    this.scanner.resetTo(restart, this.javadocEnd);
                    this.index = restart;
                    var13_12 = this.pushParamName(isTypeParam);
                    return var13_12;
                }
            }
            this.currentTokenType = -1;
            if (isCompletionParser) {
                return false;
            }
            if (this.reportProblems) {
                end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                try {
                    while ((token = this.readToken()) != 1000 && token != 64) {
                        this.currentTokenType = -1;
                        end = hasMultiLines != false ? this.lineEnd : this.scanner.getCurrentTokenEndPosition();
                    }
                }
                catch (InvalidInputException v7) {
                    end = this.lineEnd;
                }
                if (mayBeGeneric && isTypeParam) {
                    this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                } else {
                    this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
                }
            }
            this.scanner.currentPosition = start;
            this.index = start;
            this.currentTokenType = -1;
            return false;
        }
        finally {
            this.scanner.tokenizeWhiteSpace = tokenWhiteSpace;
        }
    }

    private boolean isTokenModule(int token, int moduleRefTokenCount) {
        return token == 10 && moduleRefTokenCount > 0;
    }

    protected Object parseQualifiedName(boolean reset) throws InvalidInputException {
        return this.parseQualifiedName(reset, false);
    }

    protected Object parseQualifiedName(boolean reset, boolean allowModule) throws InvalidInputException {
        if (reset) {
            this.identifierPtr = -1;
            this.identifierLengthPtr = -1;
        }
        int primitiveToken = -1;
        int parserKind = this.kind & 0xFF;
        int prevToken = 0;
        int curToken = 0;
        int moduleRefTokenCount = 0;
        boolean lookForModule = false;
        boolean parsingJava15Plus = this.scanner != null ? this.scanner.sourceLevel >= 0x3B0000L : false;
        boolean stop = false;
        int iToken = 0;
        block11: while (true) {
            int token;
            if (iToken == 0) {
                lookForModule = false;
                prevToken = 0;
            } else {
                prevToken = curToken;
            }
            if (stop) break;
            curToken = token = this.readTokenSafely();
            switch (token) {
                case 22: {
                    if (iToken & true) break block11;
                    this.pushIdentifier(iToken == 0, false);
                    this.consumeToken();
                    if (!allowModule || !parsingJava15Plus || this.getChar() != '/') break;
                    lookForModule = true;
                    break;
                }
                case 80: {
                    throw new InvalidInputException();
                }
                case 1: {
                    if (!(iToken & true)) {
                        throw new InvalidInputException();
                    }
                    this.consumeToken();
                    break;
                }
                case 17: 
                case 34: 
                case 35: 
                case 37: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 44: 
                case 45: 
                case 46: 
                case 47: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 63: 
                case 70: 
                case 73: 
                case 76: 
                case 78: 
                case 79: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 88: 
                case 90: 
                case 91: 
                case 92: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 113: 
                case 114: 
                case 115: 
                case 116: 
                case 119: 
                case 127: 
                case 130: {
                    if (iToken == 0) {
                        this.pushIdentifier(true, true);
                        primitiveToken = token;
                        this.consumeToken();
                        break block11;
                    }
                }
                case 10: {
                    if (parsingJava15Plus && lookForModule) {
                        if (!(iToken & true) || moduleRefTokenCount > 0) {
                            throw new InvalidInputException();
                        }
                        moduleRefTokenCount = (iToken + 1) / 2;
                        this.consumeToken();
                        lookForModule = false;
                        if (this.considerNextChar()) break;
                        stop = true;
                        break;
                    }
                }
                default: {
                    if (iToken == 0) {
                        if (this.identifierPtr >= 0) {
                            this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
                        }
                        return null;
                    }
                    if (iToken & true || this.isTokenModule(prevToken, moduleRefTokenCount)) break block11;
                    switch (parserKind) {
                        case 8: {
                            if (this.identifierPtr >= 0) {
                                this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
                            }
                            if (moduleRefTokenCount > 0) {
                                return this.syntaxRecoverModuleQualifiedName(primitiveToken, moduleRefTokenCount);
                            }
                            return this.syntaxRecoverQualifiedName(primitiveToken);
                        }
                        case 2: {
                            if (this.currentTokenType == -1) break;
                            this.index = this.tokenPreviousPosition;
                            this.scanner.currentPosition = this.tokenPreviousPosition;
                            this.currentTokenType = -1;
                        }
                    }
                    throw new InvalidInputException();
                }
            }
            ++iToken;
        }
        if (parserKind != 8 && this.currentTokenType != -1) {
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
        }
        if (this.identifierPtr >= 0) {
            this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
        }
        if (moduleRefTokenCount > 0) {
            return this.createModuleTypeReference(primitiveToken, moduleRefTokenCount);
        }
        return this.createTypeReference(primitiveToken);
    }

    protected boolean parseReference() throws InvalidInputException {
        return this.parseReference(false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean parseReference(boolean allowModule) throws InvalidInputException {
        int currentPosition = this.scanner.currentPosition;
        try {
            int end;
            Object typeRef = null;
            Object reference = null;
            int previousPosition = -1;
            int typeRefStartPosition = -1;
            block12: while (this.index < this.scanner.eofPosition) {
                previousPosition = this.index;
                int token = this.readTokenSafely();
                switch (token) {
                    case 60: {
                        if (typeRef != null) break block12;
                        this.consumeToken();
                        int start = this.scanner.getCurrentTokenStartPosition();
                        if (this.tagValue == 10) {
                            if (!this.reportProblems) return false;
                            this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getTokenEndPosition(), this.sourceParser.modifiers);
                            return false;
                        }
                        if (this.verifyEndLine(previousPosition)) {
                            return this.createFakeReference(start);
                        }
                        if (!this.reportProblems) return false;
                        this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
                        return false;
                    }
                    case 11: {
                        if (typeRef != null) break block12;
                        this.consumeToken();
                        int start = this.scanner.getCurrentTokenStartPosition();
                        if (!this.parseHref()) {
                            if (this.tagValue != 10) return false;
                            if (!this.reportProblems) return false;
                            this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getIndexPosition(), this.sourceParser.modifiers);
                            return false;
                        }
                        this.consumeToken();
                        if (this.tagValue == 10) {
                            if (!this.reportProblems) return false;
                            this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getIndexPosition(), this.sourceParser.modifiers);
                            return false;
                        }
                        if (this.verifyEndLine(previousPosition)) {
                            return this.createFakeReference(start);
                        }
                        if (!this.reportProblems) return false;
                        this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
                        return false;
                    }
                    case 135: {
                        this.consumeToken();
                        if (this.scanner.currentCharacter == '#') {
                            reference = this.parseMember(typeRef);
                            if (reference == null) return false;
                            return this.pushSeeRef(reference);
                        }
                        char[] currentError = this.scanner.getCurrentIdentifierSource();
                        if (currentError.length <= 0 || currentError[0] != '\"') break block12;
                        if (!this.reportProblems) return false;
                        boolean isUrlRef = false;
                        if (this.tagValue == 6) {
                            int length = currentError.length;
                            int i = 1;
                            while (true) {
                                if (i >= length || !ScannerHelper.isLetter(currentError[i])) {
                                    if (i >= length - 2 || currentError[i] != ':' || currentError[i + 1] != '/' || currentError[i + 2] != '/') break;
                                    isUrlRef = true;
                                    break;
                                }
                                ++i;
                            }
                        }
                        if (isUrlRef) {
                            this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(this.scanner.getCurrentTokenStartPosition(), this.getTokenEndPosition());
                            return false;
                        }
                        this.sourceParser.problemReporter().javadocInvalidReference(this.scanner.getCurrentTokenStartPosition(), this.getTokenEndPosition());
                        return false;
                    }
                    case 22: {
                        if (typeRef != null) break block12;
                        typeRefStartPosition = this.scanner.getCurrentTokenStartPosition();
                        typeRef = this.parseQualifiedName(true, allowModule);
                        if (!this.abort) continue block12;
                        return false;
                    }
                }
            }
            if (reference == null) {
                reference = typeRef;
            }
            if (reference == null) {
                this.index = this.tokenPreviousPosition;
                this.scanner.currentPosition = this.tokenPreviousPosition;
                this.currentTokenType = -1;
                if (this.tagValue == 10) {
                    if ((this.kind & 2) == 0) return true;
                    this.createTag();
                    return true;
                }
                if (!this.reportProblems) return false;
                this.sourceParser.problemReporter().javadocMissingReference(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                return false;
            }
            if (this.lastIdentifierEndPosition > this.javadocStart) {
                this.scanner.currentPosition = this.index = this.lastIdentifierEndPosition + 1;
            }
            this.currentTokenType = -1;
            if (this.tagValue == 10) {
                if (!this.reportProblems) return false;
                this.sourceParser.problemReporter().javadocInvalidReference(typeRefStartPosition, this.lineEnd);
                return false;
            }
            int currentIndex = this.index;
            char ch = this.readChar();
            switch (ch) {
                case '(': {
                    if (!this.reportProblems) return false;
                    this.sourceParser.problemReporter().javadocMissingHashCharacter(typeRefStartPosition, this.lineEnd, String.valueOf(this.source, typeRefStartPosition, this.lineEnd - typeRefStartPosition + 1));
                    return false;
                }
                case ':': {
                    ch = this.readChar();
                    if (ch != '/' || ch != this.readChar() || !this.reportProblems) break;
                    this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(typeRefStartPosition, this.lineEnd);
                    return false;
                }
            }
            this.index = currentIndex;
            if (this.verifySpaceOrEndComment()) return this.pushSeeRef(reference);
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
            int n = end = this.starPosition == -1 ? this.lineEnd : this.starPosition;
            if (this.source[end] == '\n') {
                --end;
            }
            if (!this.reportProblems) return false;
            this.sourceParser.problemReporter().javadocMalformedSeeReference(typeRefStartPosition, end);
            return false;
        }
        catch (InvalidInputException invalidInputException) {
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocInvalidReference(currentPosition, this.getTokenEndPosition());
            }
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
            return false;
        }
    }

    protected abstract boolean parseTag(int var1) throws InvalidInputException;

    protected boolean parseThrows() {
        boolean isCompletionParser;
        int start;
        block7: {
            Object typeRef;
            block6: {
                start = this.scanner.currentPosition;
                isCompletionParser = (this.kind & 8) != 0;
                typeRef = this.parseQualifiedName(true);
                if (!this.abort) break block6;
                return false;
            }
            try {
                if (typeRef == null) {
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocMissingThrowsClassName(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                    }
                    break block7;
                }
                return this.pushThrowName(typeRef);
            }
            catch (InvalidInputException invalidInputException) {
                if (!this.reportProblems) break block7;
                this.sourceParser.problemReporter().javadocInvalidThrowsClass(start, this.getTokenEndPosition());
            }
        }
        if (!isCompletionParser) {
            this.scanner.currentPosition = start;
            this.index = start;
        }
        this.currentTokenType = -1;
        return false;
    }

    protected char peekChar() {
        char c;
        int idx = this.index;
        if ((c = this.source[idx++]) == '\\' && this.source[idx] == 'u') {
            int c4;
            int c3;
            int c2;
            int c1;
            ++idx;
            while (this.source[idx] == 'u') {
                ++idx;
            }
            if ((c1 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c4 >= 0) {
                c = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
            }
        }
        return c;
    }

    protected void pushIdentifier(boolean newLength, boolean isToken) {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            char[][] cArrayArray = new char[stackLength + 10][];
            this.identifierStack = cArrayArray;
            System.arraycopy(this.identifierStack, 0, cArrayArray, 0, stackLength);
            this.identifierPositionStack = new long[stackLength + 10];
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack, 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = isToken ? this.scanner.getCurrentTokenSource() : this.scanner.getCurrentIdentifierSource();
        this.identifierPositionStack[this.identifierPtr] = ((long)this.scanner.startPosition << 32) + (long)(this.scanner.currentPosition - 1);
        if (newLength) {
            stackLength = this.identifierLengthStack.length;
            if (++this.identifierLengthPtr >= stackLength) {
                this.identifierLengthStack = new int[stackLength + 10];
                System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack, 0, stackLength);
            }
            this.identifierLengthStack[this.identifierLengthPtr] = 1;
        } else {
            int n = this.identifierLengthPtr;
            this.identifierLengthStack[n] = this.identifierLengthStack[n] + 1;
        }
    }

    protected void pushOnAstStack(Object node, boolean newLength) {
        if (node == null) {
            int stackLength = this.astLengthStack.length;
            if (++this.astLengthPtr >= stackLength) {
                this.astLengthStack = new int[stackLength + 10];
                System.arraycopy(this.astLengthStack, 0, this.astLengthStack, 0, stackLength);
            }
            this.astLengthStack[this.astLengthPtr] = 0;
            return;
        }
        int stackLength = this.astStack.length;
        if (++this.astPtr >= stackLength) {
            this.astStack = new Object[stackLength + 10];
            System.arraycopy(this.astStack, 0, this.astStack, 0, stackLength);
            this.astPtr = stackLength;
        }
        this.astStack[this.astPtr] = node;
        if (newLength) {
            stackLength = this.astLengthStack.length;
            if (++this.astLengthPtr >= stackLength) {
                this.astLengthStack = new int[stackLength + 10];
                System.arraycopy(this.astLengthStack, 0, this.astLengthStack, 0, stackLength);
            }
            this.astLengthStack[this.astLengthPtr] = 1;
        } else {
            int n = this.astLengthPtr;
            this.astLengthStack[n] = this.astLengthStack[n] + 1;
        }
    }

    protected abstract boolean pushParamName(boolean var1);

    protected abstract boolean pushSeeRef(Object var1);

    protected void pushText(int start, int end) {
    }

    protected abstract boolean pushThrowName(Object var1);

    protected char readChar() {
        char c;
        if ((c = this.source[this.index++]) == '\\' && this.source[this.index] == 'u') {
            int c4;
            int c3;
            int c2;
            int c1;
            int pos = this.index++;
            while (this.source[this.index] == 'u') {
                ++this.index;
            }
            if ((c1 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c4 >= 0) {
                c = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
            } else {
                this.index = pos;
            }
        }
        return c;
    }

    private char getChar() {
        char c;
        int indexVal = this.index;
        if ((c = this.source[indexVal++]) == '\\' && this.source[indexVal] == 'u') {
            int c4;
            int c3;
            int c2;
            int c1;
            int pos = indexVal++;
            while (this.source[indexVal] == 'u') {
                ++indexVal;
            }
            if ((c1 = ScannerHelper.getHexadecimalValue(this.source[indexVal++])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(this.source[indexVal++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[indexVal++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[indexVal++])) <= 15 && c4 >= 0) {
                c = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
            } else {
                indexVal = pos;
            }
        }
        return c;
    }

    private boolean considerNextChar() {
        boolean consider = true;
        char ch = this.getChar();
        if (ch == ' ' || System.lineSeparator().indexOf(ch) == 0) {
            consider = false;
        }
        return consider;
    }

    protected int readToken() throws InvalidInputException {
        if (this.currentTokenType < 0) {
            this.tokenPreviousPosition = this.scanner.currentPosition;
            this.currentTokenType = this.scanner.getNextToken();
            if (this.scanner.currentPosition > this.lineEnd + 1) {
                this.lineStarted = false;
                while (this.currentTokenType == 8) {
                    this.currentTokenType = this.scanner.getNextToken();
                }
            }
            this.index = this.scanner.currentPosition;
            this.lineStarted = true;
        }
        return this.currentTokenType;
    }

    protected int readTokenAndConsume() throws InvalidInputException {
        int token = this.readToken();
        this.consumeToken();
        return token;
    }

    protected int readTokenSafely() {
        int token = 135;
        try {
            token = this.readToken();
        }
        catch (InvalidInputException invalidInputException) {}
        return token;
    }

    protected void recordInheritedPosition(long position) {
        if (this.inheritedPositions == null) {
            this.inheritedPositions = new long[4];
            this.inheritedPositionsPtr = 0;
        } else if (this.inheritedPositionsPtr == this.inheritedPositions.length) {
            this.inheritedPositions = new long[this.inheritedPositionsPtr + 4];
            System.arraycopy(this.inheritedPositions, 0, this.inheritedPositions, 0, this.inheritedPositionsPtr);
        }
        this.inheritedPositions[this.inheritedPositionsPtr++] = position;
    }

    protected void refreshInlineTagPosition(int previousPosition) {
    }

    protected void refreshReturnStatement() {
    }

    protected void setInlineTagStarted(boolean started) {
        this.inlineTagStarted = started;
    }

    protected Object syntaxRecoverQualifiedName(int primitiveToken) throws InvalidInputException {
        return null;
    }

    protected Object syntaxRecoverModuleQualifiedName(int primitiveToken, int moduleTokenCount) throws InvalidInputException {
        return null;
    }

    public String toString() {
        char[] middle;
        int endPos;
        StringBuffer buffer = new StringBuffer();
        int startPos = this.scanner.currentPosition < this.index ? this.scanner.currentPosition : this.index;
        int n = endPos = this.scanner.currentPosition < this.index ? this.index : this.scanner.currentPosition;
        if (startPos == this.source.length) {
            return "EOF\n\n" + new String(this.source);
        }
        if (endPos > this.source.length) {
            return "behind the EOF\n\n" + new String(this.source);
        }
        char[] front = new char[startPos];
        System.arraycopy(this.source, 0, front, 0, startPos);
        int middleLength = endPos - 1 - startPos + 1;
        if (middleLength > -1) {
            middle = new char[middleLength];
            System.arraycopy(this.source, startPos, middle, 0, middleLength);
        } else {
            middle = CharOperation.NO_CHAR;
        }
        char[] end = new char[this.source.length - (endPos - 1)];
        System.arraycopy(this.source, endPos - 1 + 1, end, 0, this.source.length - (endPos - 1) - 1);
        buffer.append(front);
        if (this.scanner.currentPosition < this.index) {
            buffer.append("\n===============================\nScanner current position here -->");
        } else {
            buffer.append("\n===============================\nParser index here -->");
        }
        buffer.append(middle);
        if (this.scanner.currentPosition < this.index) {
            buffer.append("<-- Parser index here\n===============================\n");
        } else {
            buffer.append("<-- Scanner current position here\n===============================\n");
        }
        buffer.append(end);
        return buffer.toString();
    }

    protected abstract void updateDocComment();

    protected void updateLineEnd() {
        while (this.index > this.lineEnd + 1) {
            if (this.linePtr < this.lastLinePtr) {
                this.lineEnd = this.scanner.getLineEnd(++this.linePtr) - 1;
                continue;
            }
            this.lineEnd = this.javadocEnd;
            return;
        }
    }

    protected boolean verifyEndLine(int textPosition) {
        boolean domParser;
        boolean bl = domParser = (this.kind & 2) != 0;
        if (this.inlineTagStarted) {
            if (this.peekChar() == '}') {
                if (domParser) {
                    this.createTag();
                    this.pushText(textPosition, this.index);
                }
                return true;
            }
            return false;
        }
        int startPosition = this.index;
        int previousPosition = this.index;
        this.starPosition = -1;
        char ch = this.readChar();
        block6: while (true) {
            switch (ch) {
                case '\n': 
                case '\r': {
                    if (domParser) {
                        this.createTag();
                        this.pushText(textPosition, previousPosition);
                    }
                    this.index = previousPosition;
                    return true;
                }
                case '\t': 
                case '\f': 
                case ' ': {
                    if (this.starPosition < 0) break;
                    break block6;
                }
                case '*': {
                    this.starPosition = previousPosition;
                    break;
                }
                case '/': {
                    if (this.starPosition < textPosition) break block6;
                    if (domParser) {
                        this.createTag();
                        this.pushText(textPosition, this.starPosition);
                    }
                    return true;
                }
                default: {
                    break block6;
                }
            }
            previousPosition = this.index;
            ch = this.readChar();
        }
        this.index = startPosition;
        return false;
    }

    protected boolean verifySpaceOrEndComment() {
        this.starPosition = -1;
        int startPosition = this.index;
        char ch = this.peekChar();
        switch (ch) {
            case '}': {
                return this.inlineTagStarted;
            }
        }
        if (ScannerHelper.isWhitespace(ch)) {
            return true;
        }
        int previousPosition = this.index;
        ch = this.readChar();
        while (this.index < this.source.length) {
            switch (ch) {
                case '*': {
                    this.starPosition = previousPosition;
                    break;
                }
                case '/': {
                    if (this.starPosition >= startPosition) {
                        return true;
                    }
                }
                default: {
                    this.index = startPosition;
                    return false;
                }
            }
            previousPosition = this.index;
            ch = this.readChar();
        }
        this.index = startPosition;
        return false;
    }
}

