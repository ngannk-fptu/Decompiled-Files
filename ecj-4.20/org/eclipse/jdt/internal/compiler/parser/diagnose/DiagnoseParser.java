/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser.diagnose;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.ConflictedParser;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.ParserBasicInformation;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.internal.compiler.parser.diagnose.LexStream;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class DiagnoseParser
implements ParserBasicInformation,
TerminalTokens,
ConflictedParser {
    private static final boolean DEBUG = false;
    private boolean DEBUG_PARSECHECK = false;
    private static final int STACK_INCREMENT = 256;
    private static final int BEFORE_CODE = 2;
    private static final int INSERTION_CODE = 3;
    private static final int INVALID_CODE = 4;
    private static final int SUBSTITUTION_CODE = 5;
    private static final int DELETION_CODE = 6;
    private static final int MERGE_CODE = 7;
    private static final int MISPLACED_CODE = 8;
    private static final int SCOPE_CODE = 9;
    private static final int SECONDARY_CODE = 10;
    private static final int EOF_CODE = 11;
    private static final int BUFF_UBOUND = 31;
    private static final int BUFF_SIZE = 32;
    private static final int MAX_DISTANCE = 30;
    private static final int MIN_DISTANCE = 3;
    private CompilerOptions options;
    private LexStream lexStream;
    private int errorToken;
    private int errorTokenStart;
    private int currentToken = 0;
    private int stackLength;
    private int stateStackTop;
    private int[] stack;
    private int[] locationStack;
    private int[] locationStartStack;
    private int tempStackTop;
    private int[] tempStack;
    private int prevStackTop;
    private int[] prevStack;
    private int nextStackTop;
    private int[] nextStack;
    private int scopeStackTop;
    private int[] scopeIndex;
    private int[] scopePosition;
    int[] list = new int[559];
    int[] buffer = new int[32];
    private static final int NIL = -1;
    int[] stateSeen;
    int statePoolTop;
    StateInfo[] statePool;
    private Parser parser;
    private RecoveryScanner recoveryScanner;
    private boolean reportProblem;

    public DiagnoseParser(Parser parser, int firstToken, int start, int end, CompilerOptions options) {
        this(parser, firstToken, start, end, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, Util.EMPTY_INT_ARRAY, options);
    }

    public DiagnoseParser(Parser parser, int firstToken, int start, int end, int[] intervalStartToSkip, int[] intervalEndToSkip, int[] intervalFlagsToSkip, CompilerOptions options) {
        this.parser = parser;
        this.options = options;
        this.lexStream = new LexStream(32, parser.scanner, intervalStartToSkip, intervalEndToSkip, intervalFlagsToSkip, firstToken, start, end);
        this.recoveryScanner = parser.recoveryScanner;
    }

    private ProblemReporter problemReporter() {
        return this.parser.problemReporter();
    }

    private void reallocateStacks() {
        int old_stack_length = this.stackLength;
        this.stackLength += 256;
        if (old_stack_length == 0) {
            this.stack = new int[this.stackLength];
            this.locationStack = new int[this.stackLength];
            this.locationStartStack = new int[this.stackLength];
            this.tempStack = new int[this.stackLength];
            this.prevStack = new int[this.stackLength];
            this.nextStack = new int[this.stackLength];
            this.scopeIndex = new int[this.stackLength];
            this.scopePosition = new int[this.stackLength];
        } else {
            this.stack = new int[this.stackLength];
            System.arraycopy(this.stack, 0, this.stack, 0, old_stack_length);
            this.locationStack = new int[this.stackLength];
            System.arraycopy(this.locationStack, 0, this.locationStack, 0, old_stack_length);
            this.locationStartStack = new int[this.stackLength];
            System.arraycopy(this.locationStartStack, 0, this.locationStartStack, 0, old_stack_length);
            this.tempStack = new int[this.stackLength];
            System.arraycopy(this.tempStack, 0, this.tempStack, 0, old_stack_length);
            this.prevStack = new int[this.stackLength];
            System.arraycopy(this.prevStack, 0, this.prevStack, 0, old_stack_length);
            this.nextStack = new int[this.stackLength];
            System.arraycopy(this.nextStack, 0, this.nextStack, 0, old_stack_length);
            this.scopeIndex = new int[this.stackLength];
            System.arraycopy(this.scopeIndex, 0, this.scopeIndex, 0, old_stack_length);
            this.scopePosition = new int[this.stackLength];
            System.arraycopy(this.scopePosition, 0, this.scopePosition, 0, old_stack_length);
        }
    }

    public void diagnoseParse(boolean record) {
        this.reportProblem = true;
        boolean oldRecord = false;
        if (this.recoveryScanner != null) {
            oldRecord = this.recoveryScanner.record;
            this.recoveryScanner.record = record;
        }
        this.parser.scanner.setActiveParser(this);
        try {
            this.lexStream.reset();
            this.currentToken = this.lexStream.getToken();
            int act = 982;
            this.reallocateStacks();
            this.stateStackTop = 0;
            this.stack[this.stateStackTop] = act;
            int tok = this.lexStream.kind(this.currentToken);
            this.locationStack[this.stateStackTop] = this.currentToken;
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
            boolean forceRecoveryAfterLBracketMissing = false;
            do {
                int prev_pos = -1;
                this.prevStackTop = -1;
                int next_pos = -1;
                this.nextStackTop = -1;
                int pos = this.stateStackTop;
                this.tempStackTop = this.stateStackTop - 1;
                int i = 0;
                while (i <= this.stateStackTop) {
                    this.tempStack[i] = this.stack[i];
                    ++i;
                }
                act = Parser.tAction(act, tok);
                while (act <= 919) {
                    do {
                        this.tempStackTop -= Parser.rhs[act] - 1;
                    } while ((act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act])) <= 919);
                    if (this.tempStackTop + 1 >= this.stackLength) {
                        this.reallocateStacks();
                    }
                    pos = pos < this.tempStackTop ? pos : this.tempStackTop;
                    this.tempStack[this.tempStackTop + 1] = act;
                    act = Parser.tAction(act, tok);
                }
                while (act > 17934 || act < 17933) {
                    this.nextStackTop = this.tempStackTop + 1;
                    i = next_pos + 1;
                    while (i <= this.nextStackTop) {
                        this.nextStack[i] = this.tempStack[i];
                        ++i;
                    }
                    i = pos + 1;
                    while (i <= this.nextStackTop) {
                        this.locationStack[i] = this.locationStack[this.stateStackTop];
                        this.locationStartStack[i] = this.locationStartStack[this.stateStackTop];
                        ++i;
                    }
                    if (act > 17934) {
                        act -= 17934;
                        do {
                            this.nextStackTop -= Parser.rhs[act] - 1;
                        } while ((act = Parser.ntAction(this.nextStack[this.nextStackTop], Parser.lhs[act])) <= 919);
                        int n = pos = pos < this.nextStackTop ? pos : this.nextStackTop;
                    }
                    if (this.nextStackTop + 1 >= this.stackLength) {
                        this.reallocateStacks();
                    }
                    this.tempStackTop = this.nextStackTop++;
                    this.nextStack[this.nextStackTop] = act;
                    next_pos = this.nextStackTop;
                    this.currentToken = this.lexStream.getToken();
                    tok = this.lexStream.kind(this.currentToken);
                    act = Parser.tAction(act, tok);
                    while (act <= 919) {
                        char lhs_symbol;
                        do {
                            lhs_symbol = Parser.lhs[act];
                            this.tempStackTop -= Parser.rhs[act] - 1;
                            int n = act = this.tempStackTop > next_pos ? this.tempStack[this.tempStackTop] : this.nextStack[this.tempStackTop];
                        } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
                        if (this.tempStackTop + 1 >= this.stackLength) {
                            this.reallocateStacks();
                        }
                        next_pos = next_pos < this.tempStackTop ? next_pos : this.tempStackTop;
                        this.tempStack[this.tempStackTop + 1] = act;
                        act = Parser.tAction(act, tok);
                    }
                    if (act == 17934) continue;
                    this.prevStackTop = this.stateStackTop;
                    i = prev_pos + 1;
                    while (i <= this.prevStackTop) {
                        this.prevStack[i] = this.stack[i];
                        ++i;
                    }
                    prev_pos = pos;
                    this.stateStackTop = this.nextStackTop;
                    i = pos + 1;
                    while (i <= this.stateStackTop) {
                        this.stack[i] = this.nextStack[i];
                        ++i;
                    }
                    this.locationStack[this.stateStackTop] = this.currentToken;
                    this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
                    pos = next_pos;
                }
                if (act != 17934) continue;
                RepairCandidate candidate = this.errorRecovery(this.currentToken, forceRecoveryAfterLBracketMissing);
                forceRecoveryAfterLBracketMissing = false;
                if (this.parser.reportOnlyOneSyntaxError) {
                    return;
                }
                if (this.parser.problemReporter().options.maxProblemsPerUnit < this.parser.compilationUnit.compilationResult.problemCount) {
                    if (this.recoveryScanner == null || !this.recoveryScanner.record) {
                        return;
                    }
                    this.reportProblem = false;
                }
                act = this.stack[this.stateStackTop];
                if (candidate.symbol == 0) {
                    break;
                }
                if (candidate.symbol > 135) {
                    int lhs_symbol = candidate.symbol - 135;
                    act = Parser.ntAction(act, lhs_symbol);
                    while (act <= 919) {
                        this.stateStackTop -= Parser.rhs[act] - 1;
                        act = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act]);
                    }
                    this.stack[++this.stateStackTop] = act;
                    this.currentToken = this.lexStream.getToken();
                    tok = this.lexStream.kind(this.currentToken);
                    this.locationStack[this.stateStackTop] = this.currentToken;
                    this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.currentToken);
                    continue;
                }
                tok = candidate.symbol;
                this.locationStack[this.stateStackTop] = candidate.location;
                this.locationStartStack[this.stateStackTop] = this.lexStream.start(candidate.location);
            } while (act != 17933);
        }
        finally {
            if (this.recoveryScanner != null) {
                this.recoveryScanner.record = oldRecord;
            }
            this.parser.scanner.setActiveParser(null);
        }
    }

    private static char[] displayEscapeCharacters(char[] tokenSource, int start, int end) {
        StringBuffer tokenSourceBuffer = new StringBuffer();
        int i = 0;
        while (i < start) {
            tokenSourceBuffer.append(tokenSource[i]);
            ++i;
        }
        i = start;
        while (i < end) {
            char c = tokenSource[i];
            Util.appendEscapedChar(tokenSourceBuffer, c, true);
            ++i;
        }
        i = end;
        while (i < tokenSource.length) {
            tokenSourceBuffer.append(tokenSource[i]);
            ++i;
        }
        return tokenSourceBuffer.toString().toCharArray();
    }

    /*
     * Unable to fully structure code
     */
    private RepairCandidate errorRecovery(int error_token, boolean forcedError) {
        this.errorToken = error_token;
        this.errorTokenStart = this.lexStream.start(error_token);
        prevtok = this.lexStream.previous(error_token);
        prevtokKind = this.lexStream.kind(prevtok);
        if (forcedError) {
            name_index = Parser.terminal_index[38];
            this.reportError(3, name_index, prevtok, prevtok);
            candidate = new RepairCandidate();
            candidate.symbol = 38;
            candidate.location = error_token;
            this.lexStream.reset(error_token);
            this.stateStackTop = this.nextStackTop;
            j = 0;
            while (j <= this.stateStackTop) {
                this.stack[j] = this.nextStack[j];
                ++j;
            }
            this.locationStack[this.stateStackTop] = error_token;
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(error_token);
            return candidate;
        }
        candidate = this.primaryPhase(error_token);
        if (candidate.symbol != 0) {
            return candidate;
        }
        candidate = this.secondaryPhase(error_token);
        if (candidate.symbol != 0) {
            return candidate;
        }
        if (this.lexStream.kind(error_token) != 64) ** GOTO lbl35
        this.reportError(11, Parser.terminal_index[64], prevtok, prevtok);
        candidate.symbol = 0;
        candidate.location = error_token;
        return candidate;
lbl-1000:
        // 1 sources

        {
            candidate = this.secondaryPhase(this.buffer[29]);
            if (candidate.symbol == 0) continue;
            return candidate;
lbl35:
            // 2 sources

            ** while (this.lexStream.kind((int)this.buffer[31]) != 64)
        }
lbl36:
        // 1 sources

        i = 31;
        while (this.lexStream.kind(this.buffer[i]) == 64) {
            --i;
        }
        this.reportError(6, Parser.terminal_index[prevtokKind], error_token, this.buffer[i]);
        candidate.symbol = 0;
        candidate.location = this.buffer[i];
        return candidate;
    }

    private RepairCandidate primaryPhase(int error_token) {
        PrimaryRepairInfo repair = new PrimaryRepairInfo();
        RepairCandidate candidate = new RepairCandidate();
        int i = this.nextStackTop >= 0 ? 3 : 2;
        this.buffer[i] = error_token;
        int j = i;
        while (j > 0) {
            this.buffer[j - 1] = this.lexStream.previous(this.buffer[j]);
            --j;
        }
        int k = i + 1;
        while (k < 32) {
            this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
            ++k;
        }
        if (this.nextStackTop >= 0) {
            repair.bufferPosition = 3;
            repair = this.checkPrimaryDistance(this.nextStack, this.nextStackTop, repair);
        }
        PrimaryRepairInfo new_repair = repair.copy();
        new_repair.bufferPosition = 2;
        new_repair = this.checkPrimaryDistance(this.stack, this.stateStackTop, new_repair);
        if (new_repair.distance > repair.distance || new_repair.misspellIndex > repair.misspellIndex) {
            repair = new_repair;
        }
        if (this.prevStackTop >= 0) {
            new_repair = repair.copy();
            new_repair.bufferPosition = 1;
            new_repair = this.checkPrimaryDistance(this.prevStack, this.prevStackTop, new_repair);
            if (new_repair.distance > repair.distance || new_repair.misspellIndex > repair.misspellIndex) {
                repair = new_repair;
            }
        }
        if (this.nextStackTop >= 0 ? this.secondaryCheck(this.nextStack, this.nextStackTop, 3, repair.distance) : this.secondaryCheck(this.stack, this.stateStackTop, 2, repair.distance)) {
            return candidate;
        }
        repair.distance = repair.distance - repair.bufferPosition + 1;
        if (repair.code == 4 || repair.code == 6 || repair.code == 5 || repair.code == 7) {
            --repair.distance;
        }
        if (repair.distance < 3) {
            return candidate;
        }
        if (repair.code == 3 && this.buffer[repair.bufferPosition - 1] == 0) {
            repair.code = 2;
        }
        if (repair.bufferPosition == 1) {
            this.stateStackTop = this.prevStackTop;
            int j2 = 0;
            while (j2 <= this.stateStackTop) {
                this.stack[j2] = this.prevStack[j2];
                ++j2;
            }
        } else if (this.nextStackTop >= 0 && repair.bufferPosition >= 3) {
            this.stateStackTop = this.nextStackTop;
            int j3 = 0;
            while (j3 <= this.stateStackTop) {
                this.stack[j3] = this.nextStack[j3];
                ++j3;
            }
            this.locationStack[this.stateStackTop] = this.buffer[3];
            this.locationStartStack[this.stateStackTop] = this.lexStream.start(this.buffer[3]);
        }
        return this.primaryDiagnosis(repair);
    }

    private int mergeCandidate(int state, int buffer_position) {
        char[] name1 = this.lexStream.name(this.buffer[buffer_position]);
        char[] name2 = this.lexStream.name(this.buffer[buffer_position + 1]);
        int len = name1.length + name2.length;
        char[] str = CharOperation.concat(name1, name2);
        int k = Parser.asi(state);
        while (Parser.asr[k] != '\u0000') {
            char[] name;
            char l = Parser.terminal_index[Parser.asr[k]];
            if (len == Parser.name[l].length() && CharOperation.equals(str, name = Parser.name[l].toCharArray(), false)) {
                return Parser.asr[k];
            }
            ++k;
        }
        return 0;
    }

    private PrimaryRepairInfo checkPrimaryDistance(int[] stck, int stack_top, PrimaryRepairInfo repair) {
        int j;
        int symbol;
        PrimaryRepairInfo scope_repair = this.scopeTrial(stck, stack_top, repair.copy());
        if (scope_repair.distance > repair.distance) {
            repair = scope_repair;
        }
        if (this.buffer[repair.bufferPosition] != 0 && this.buffer[repair.bufferPosition + 1] != 0 && (symbol = this.mergeCandidate(stck[stack_top], repair.bufferPosition)) != 0 && ((j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 2)) > repair.distance || j == repair.distance && repair.misspellIndex < 10)) {
            repair.misspellIndex = 10;
            repair.symbol = symbol;
            repair.distance = j;
            repair.code = 7;
        }
        j = this.parseCheck(stck, stack_top, this.lexStream.kind(this.buffer[repair.bufferPosition + 1]), repair.bufferPosition + 2);
        int k = this.lexStream.kind(this.buffer[repair.bufferPosition]) == 64 && this.lexStream.afterEol(this.buffer[repair.bufferPosition + 1]) ? 10 : 0;
        if (j > repair.distance || j == repair.distance && k > repair.misspellIndex) {
            repair.misspellIndex = k;
            repair.code = 6;
            repair.distance = j;
        }
        int next_state = stck[stack_top];
        int max_pos = stack_top;
        this.tempStackTop = stack_top - 1;
        int tok = this.lexStream.kind(this.buffer[repair.bufferPosition]);
        this.lexStream.reset(this.buffer[repair.bufferPosition + 1]);
        int act = Parser.tAction(next_state, tok);
        while (act <= 919) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                symbol = Parser.lhs[act];
                int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
            } while ((act = Parser.ntAction(act, symbol)) <= 919);
            max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
            this.tempStack[this.tempStackTop + 1] = act;
            next_state = act;
            act = Parser.tAction(next_state, tok);
        }
        int root = 0;
        int i = Parser.asi(next_state);
        while (Parser.asr[i] != '\u0000') {
            symbol = Parser.asr[i];
            if (symbol != 64 && symbol != 135) {
                if (root == 0) {
                    this.list[symbol] = symbol;
                } else {
                    this.list[symbol] = this.list[root];
                    this.list[root] = symbol;
                }
                root = symbol;
            }
            ++i;
        }
        if (stck[stack_top] != next_state) {
            i = Parser.asi(stck[stack_top]);
            while (Parser.asr[i] != '\u0000') {
                symbol = Parser.asr[i];
                if (symbol != 64 && symbol != 135 && this.list[symbol] == 0) {
                    if (root == 0) {
                        this.list[symbol] = symbol;
                    } else {
                        this.list[symbol] = this.list[root];
                        this.list[root] = symbol;
                    }
                    root = symbol;
                }
                ++i;
            }
        }
        i = this.list[root];
        this.list[root] = 0;
        symbol = root = i;
        while (symbol != 0) {
            k = symbol == 64 && this.lexStream.afterEol(this.buffer[repair.bufferPosition]) ? 10 : 0;
            j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition);
            if (j > repair.distance) {
                repair.misspellIndex = k;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            } else if (j == repair.distance && k > repair.misspellIndex) {
                repair.misspellIndex = k;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            }
            symbol = this.list[symbol];
        }
        symbol = root;
        if (this.buffer[repair.bufferPosition] != 0) {
            while (symbol != 0) {
                k = symbol == 64 && this.lexStream.afterEol(this.buffer[repair.bufferPosition + 1]) ? 10 : this.misspell(symbol, this.buffer[repair.bufferPosition]);
                j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
                if (j > repair.distance) {
                    repair.misspellIndex = k;
                    repair.distance = j;
                    repair.symbol = symbol;
                    repair.code = 5;
                } else if (j == repair.distance && k > repair.misspellIndex) {
                    repair.misspellIndex = k;
                    repair.symbol = symbol;
                    repair.code = 5;
                }
                i = symbol;
                symbol = this.list[symbol];
                this.list[i] = 0;
            }
        }
        i = Parser.nasi(stck[stack_top]);
        while (Parser.nasr[i] != '\u0000') {
            symbol = Parser.nasr[i] + 135;
            j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition + 1);
            if (j > repair.distance) {
                repair.misspellIndex = 0;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 4;
            }
            if ((j = this.parseCheck(stck, stack_top, symbol, repair.bufferPosition)) > repair.distance || j == repair.distance && repair.code == 4) {
                repair.misspellIndex = 0;
                repair.distance = j;
                repair.symbol = symbol;
                repair.code = 3;
            }
            ++i;
        }
        return repair;
    }

    private RepairCandidate primaryDiagnosis(PrimaryRepairInfo repair) {
        int prevtok = this.buffer[repair.bufferPosition - 1];
        int curtok = this.buffer[repair.bufferPosition];
        switch (repair.code) {
            case 2: 
            case 3: {
                int name_index = repair.symbol > 135 ? this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition) : this.getTermIndex(this.stack, this.stateStackTop, repair.symbol, repair.bufferPosition);
                int t = repair.code == 3 ? prevtok : curtok;
                this.reportError(repair.code, name_index, t, t);
                break;
            }
            case 4: {
                int name_index = this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition + 1);
                this.reportError(repair.code, name_index, curtok, curtok);
                break;
            }
            case 5: {
                int name_index;
                if (repair.misspellIndex >= 6) {
                    name_index = Parser.terminal_index[repair.symbol];
                } else {
                    name_index = this.getTermIndex(this.stack, this.stateStackTop, repair.symbol, repair.bufferPosition + 1);
                    if (name_index != Parser.terminal_index[repair.symbol]) {
                        repair.code = 4;
                    }
                }
                this.reportError(repair.code, name_index, curtok, curtok);
                break;
            }
            case 7: {
                this.reportError(repair.code, Parser.terminal_index[repair.symbol], curtok, this.lexStream.next(curtok));
                break;
            }
            case 9: {
                int i = 0;
                while (i < this.scopeStackTop) {
                    this.reportError(repair.code, -this.scopeIndex[i], this.locationStack[this.scopePosition[i]], prevtok, Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
                    ++i;
                }
                repair.symbol = Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 135;
                this.stateStackTop = this.scopePosition[this.scopeStackTop];
                this.reportError(repair.code, -this.scopeIndex[this.scopeStackTop], this.locationStack[this.scopePosition[this.scopeStackTop]], prevtok, this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition));
                break;
            }
            default: {
                this.reportError(repair.code, Parser.terminal_index[135], curtok, curtok);
            }
        }
        RepairCandidate candidate = new RepairCandidate();
        switch (repair.code) {
            case 2: 
            case 3: 
            case 9: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition]);
                break;
            }
            case 4: 
            case 5: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition + 1]);
                break;
            }
            case 7: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition + 2]);
                break;
            }
            default: {
                candidate.location = this.buffer[repair.bufferPosition + 1];
                candidate.symbol = this.lexStream.kind(this.buffer[repair.bufferPosition + 1]);
                this.lexStream.reset(this.buffer[repair.bufferPosition + 2]);
            }
        }
        return candidate;
    }

    private int getTermIndex(int[] stck, int stack_top, int tok, int buffer_position) {
        int act = stck[stack_top];
        int max_pos = stack_top;
        int highest_symbol = tok;
        this.tempStackTop = stack_top - 1;
        this.lexStream.reset(this.buffer[buffer_position]);
        act = Parser.tAction(act, tok);
        while (act <= 919) {
            char lhs_symbol;
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                lhs_symbol = Parser.lhs[act];
                int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
            } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
            max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        ++this.tempStackTop;
        int threshold = this.tempStackTop;
        tok = this.lexStream.kind(this.buffer[buffer_position]);
        this.lexStream.reset(this.buffer[buffer_position + 1]);
        if (act > 17934) {
            act -= 17934;
        } else {
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        while (act <= 919) {
            char lhs_symbol;
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.tempStackTop < threshold) {
                    return highest_symbol > 135 ? Parser.non_terminal_index[highest_symbol - 135] : Parser.terminal_index[highest_symbol];
                }
                lhs_symbol = Parser.lhs[act];
                if (this.tempStackTop == threshold) {
                    highest_symbol = lhs_symbol + 135;
                }
                int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
            } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        return highest_symbol > 135 ? Parser.non_terminal_index[highest_symbol - 135] : Parser.terminal_index[highest_symbol];
    }

    private int getNtermIndex(int start, int sym, int buffer_position) {
        int highest_symbol = sym - 135;
        int tok = this.lexStream.kind(this.buffer[buffer_position]);
        this.lexStream.reset(this.buffer[buffer_position + 1]);
        this.tempStackTop = 0;
        this.tempStack[this.tempStackTop] = start;
        int act = Parser.ntAction(start, highest_symbol);
        if (act > 919) {
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        while (act <= 919) {
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.tempStackTop < 0) {
                    return Parser.non_terminal_index[highest_symbol];
                }
                if (this.tempStackTop != 0) continue;
                highest_symbol = Parser.lhs[act];
            } while ((act = Parser.ntAction(this.tempStack[this.tempStackTop], Parser.lhs[act])) <= 919);
            this.tempStack[this.tempStackTop + 1] = act;
            act = Parser.tAction(act, tok);
        }
        return Parser.non_terminal_index[highest_symbol];
    }

    private int misspell(int sym, int tok) {
        char[] name = Parser.name[Parser.terminal_index[sym]].toCharArray();
        int n = name.length;
        char[] s1 = new char[n + 1];
        int k = 0;
        while (k < n) {
            char c = name[k];
            s1[k] = ScannerHelper.toLowerCase(c);
            ++k;
        }
        s1[n] = '\u0000';
        char[] tokenName = this.lexStream.name(tok);
        int len = tokenName.length;
        int m = len < 53 ? len : 53;
        char[] s2 = new char[m + 1];
        int k2 = 0;
        while (k2 < m) {
            char c = tokenName[k2];
            s2[k2] = ScannerHelper.toLowerCase(c);
            ++k2;
        }
        s2[m] = '\u0000';
        if (n == 1 && m == 1 && (s1[0] == ';' && s2[0] == ',' || s1[0] == ',' && s2[0] == ';' || s1[0] == ';' && s2[0] == ':' || s1[0] == ':' && s2[0] == ';' || s1[0] == '.' && s2[0] == ',' || s1[0] == ',' && s2[0] == '.' || s1[0] == '\'' && s2[0] == '\"' || s1[0] == '\"' && s2[0] == '\'')) {
            return 3;
        }
        int count = 0;
        int prefix_length = 0;
        int num_errors = 0;
        int i = 0;
        int j = 0;
        while (i < n && j < m) {
            if (s1[i] == s2[j]) {
                ++count;
                ++i;
                ++j;
                if (num_errors != 0) continue;
                ++prefix_length;
                continue;
            }
            if (s1[i + 1] == s2[j] && s1[i] == s2[j + 1]) {
                count += 2;
                i += 2;
                j += 2;
                ++num_errors;
                continue;
            }
            if (s1[i + 1] == s2[j + 1]) {
                ++i;
                ++j;
                ++num_errors;
                continue;
            }
            if (n - i > m - j) {
                ++i;
            } else if (m - j > n - i) {
                ++j;
            } else {
                ++i;
                ++j;
            }
            ++num_errors;
        }
        if (i < n || j < m) {
            ++num_errors;
        }
        if (num_errors > (n < m ? n : m) / 6 + 1) {
            count = prefix_length;
        }
        return count * 10 / ((n < len ? len : n) + num_errors);
    }

    private PrimaryRepairInfo scopeTrial(int[] stck, int stack_top, PrimaryRepairInfo repair) {
        this.stateSeen = new int[this.stackLength];
        int i = 0;
        while (i < this.stackLength) {
            this.stateSeen[i] = -1;
            ++i;
        }
        this.statePoolTop = 0;
        this.statePool = new StateInfo[this.stackLength];
        this.scopeTrialCheck(stck, stack_top, repair, 0);
        this.stateSeen = null;
        this.statePoolTop = 0;
        repair.code = 9;
        repair.misspellIndex = 10;
        return repair;
    }

    private void scopeTrialCheck(int[] stck, int stack_top, PrimaryRepairInfo repair, int indx) {
        if (indx > 20) {
            return;
        }
        int act = stck[stack_top];
        int i = this.stateSeen[stack_top];
        while (i != -1) {
            if (this.statePool[i].state == act) {
                return;
            }
            i = this.statePool[i].next;
        }
        int old_state_pool_top = this.statePoolTop++;
        if (this.statePoolTop >= this.statePool.length) {
            this.statePool = new StateInfo[this.statePoolTop * 2];
            System.arraycopy(this.statePool, 0, this.statePool, 0, this.statePoolTop);
        }
        this.statePool[old_state_pool_top] = new StateInfo(act, this.stateSeen[stack_top]);
        this.stateSeen[stack_top] = old_state_pool_top;
        int i2 = 0;
        while (i2 < 314) {
            block22: {
                act = stck[stack_top];
                this.tempStackTop = stack_top - 1;
                int max_pos = stack_top;
                char tok = Parser.scope_la[i2];
                this.lexStream.reset(this.buffer[repair.bufferPosition]);
                act = Parser.tAction(act, tok);
                while (act <= 919) {
                    char lhs_symbol;
                    do {
                        this.tempStackTop -= Parser.rhs[act] - 1;
                        lhs_symbol = Parser.lhs[act];
                        int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
                    } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
                    if (this.tempStackTop + 1 >= this.stackLength) {
                        return;
                    }
                    max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
                    this.tempStack[this.tempStackTop + 1] = act;
                    act = Parser.tAction(act, tok);
                }
                if (act != 17934) {
                    int marked_pos;
                    int k = Parser.scope_prefix[i2];
                    int j = this.tempStackTop + 1;
                    while (j >= max_pos + 1 && Parser.in_symbol(this.tempStack[j]) == Parser.scope_rhs[k]) {
                        ++k;
                        --j;
                    }
                    if (j == max_pos) {
                        j = max_pos;
                        while (j >= 1 && Parser.in_symbol(stck[j]) == Parser.scope_rhs[k]) {
                            ++k;
                            --j;
                        }
                    }
                    int n = marked_pos = max_pos < stack_top ? max_pos + 1 : stack_top;
                    if (Parser.scope_rhs[k] == '\u0000' && j < marked_pos) {
                        int stack_position = j;
                        j = Parser.scope_state_set[i2];
                        while (stck[stack_position] != Parser.scope_state[j] && Parser.scope_state[j] != '\u0000') {
                            ++j;
                        }
                        if (Parser.scope_state[j] != '\u0000') {
                            int previous_distance = repair.distance;
                            int distance = this.parseCheck(stck, stack_position, Parser.scope_lhs[i2] + 135, repair.bufferPosition);
                            if (distance - repair.bufferPosition + 1 < 3) {
                                int top = stack_position;
                                act = Parser.ntAction(stck[top], Parser.scope_lhs[i2]);
                                while (act <= 919) {
                                    if (Parser.rules_compliance[act] <= this.options.sourceLevel) {
                                        act = Parser.ntAction(stck[top -= Parser.rhs[act] - 1], Parser.lhs[act]);
                                        continue;
                                    }
                                    break block22;
                                }
                                j = act;
                                act = stck[++top];
                                stck[top] = j;
                                this.scopeTrialCheck(stck, top, repair, indx + 1);
                                stck[top] = act;
                            } else if (distance > repair.distance) {
                                this.scopeStackTop = indx;
                                repair.distance = distance;
                            }
                            if (this.lexStream.kind(this.buffer[repair.bufferPosition]) == 64 && repair.distance == previous_distance) {
                                this.scopeStackTop = indx;
                                repair.distance = 30;
                            }
                            if (repair.distance > previous_distance) {
                                this.scopeIndex[indx] = i2;
                                this.scopePosition[indx] = stack_position;
                                return;
                            }
                        }
                    }
                }
            }
            ++i2;
        }
    }

    private boolean secondaryCheck(int[] stck, int stack_top, int buffer_position, int distance) {
        int top = stack_top - 1;
        while (top >= 0) {
            int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[buffer_position]), buffer_position + 1);
            if (j - buffer_position + 1 > 3 && j > distance) {
                return true;
            }
            --top;
        }
        PrimaryRepairInfo repair = new PrimaryRepairInfo();
        repair.bufferPosition = buffer_position + 1;
        repair.distance = distance;
        repair = this.scopeTrial(stck, stack_top, repair);
        return repair.distance - buffer_position > 3 && repair.distance > distance;
    }

    private RepairCandidate secondaryPhase(int error_token) {
        int i;
        int k;
        SecondaryRepairInfo repair = new SecondaryRepairInfo();
        SecondaryRepairInfo misplaced = new SecondaryRepairInfo();
        RepairCandidate candidate = new RepairCandidate();
        int next_last_index = 0;
        candidate.symbol = 0;
        repair.code = 0;
        repair.distance = 0;
        repair.recoveryOnNextStack = false;
        misplaced.distance = 0;
        misplaced.recoveryOnNextStack = false;
        if (this.nextStackTop >= 0) {
            this.buffer[2] = error_token;
            this.buffer[1] = this.lexStream.previous(this.buffer[2]);
            this.buffer[0] = this.lexStream.previous(this.buffer[1]);
            k = 3;
            while (k < 31) {
                this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
                ++k;
            }
            this.buffer[31] = this.lexStream.badtoken();
            next_last_index = 29;
            while (next_last_index >= 1 && this.lexStream.kind(this.buffer[next_last_index]) == 64) {
                --next_last_index;
            }
            ++next_last_index;
            int save_location = this.locationStack[this.nextStackTop];
            int save_location_start = this.locationStartStack[this.nextStackTop];
            this.locationStack[this.nextStackTop] = this.buffer[2];
            this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
            misplaced.numDeletions = this.nextStackTop;
            misplaced = this.misplacementRecovery(this.nextStack, this.nextStackTop, next_last_index, misplaced, true);
            if (misplaced.recoveryOnNextStack) {
                ++misplaced.distance;
            }
            repair.numDeletions = this.nextStackTop + 31;
            repair = this.secondaryRecovery(this.nextStack, this.nextStackTop, next_last_index, repair, true);
            if (repair.recoveryOnNextStack) {
                ++repair.distance;
            }
            this.locationStack[this.nextStackTop] = save_location;
            this.locationStartStack[this.nextStackTop] = save_location_start;
        } else {
            misplaced.numDeletions = this.stateStackTop;
            repair.numDeletions = this.stateStackTop + 31;
        }
        this.buffer[3] = error_token;
        this.buffer[2] = this.lexStream.previous(this.buffer[3]);
        this.buffer[1] = this.lexStream.previous(this.buffer[2]);
        this.buffer[0] = this.lexStream.previous(this.buffer[1]);
        k = 4;
        while (k < 32) {
            this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
            ++k;
        }
        int last_index = 29;
        while (last_index >= 1 && this.lexStream.kind(this.buffer[last_index]) == 64) {
            --last_index;
        }
        misplaced = this.misplacementRecovery(this.stack, this.stateStackTop, ++last_index, misplaced, false);
        repair = this.secondaryRecovery(this.stack, this.stateStackTop, last_index, repair, false);
        if (misplaced.distance > 3 && (misplaced.numDeletions <= repair.numDeletions || misplaced.distance - misplaced.numDeletions >= repair.distance - repair.numDeletions)) {
            repair.code = 8;
            repair.stackPosition = misplaced.stackPosition;
            repair.bufferPosition = 2;
            repair.numDeletions = misplaced.numDeletions;
            repair.distance = misplaced.distance;
            repair.recoveryOnNextStack = misplaced.recoveryOnNextStack;
        }
        if (repair.recoveryOnNextStack) {
            this.stateStackTop = this.nextStackTop;
            i = 0;
            while (i <= this.stateStackTop) {
                this.stack[i] = this.nextStack[i];
                ++i;
            }
            this.buffer[2] = error_token;
            this.buffer[1] = this.lexStream.previous(this.buffer[2]);
            this.buffer[0] = this.lexStream.previous(this.buffer[1]);
            k = 3;
            while (k < 31) {
                this.buffer[k] = this.lexStream.next(this.buffer[k - 1]);
                ++k;
            }
            this.buffer[31] = this.lexStream.badtoken();
            this.locationStack[this.nextStackTop] = this.buffer[2];
            this.locationStartStack[this.nextStackTop] = this.lexStream.start(this.buffer[2]);
            last_index = next_last_index;
        }
        if (repair.code == 10 || repair.code == 6) {
            PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
            scope_repair.distance = 0;
            scope_repair.bufferPosition = 2;
            while (scope_repair.bufferPosition <= repair.bufferPosition && repair.code != 9) {
                scope_repair = this.scopeTrial(this.stack, this.stateStackTop, scope_repair);
                int j = scope_repair.distance == 30 ? last_index : scope_repair.distance;
                if (j - (k = scope_repair.bufferPosition - 1) > 3 && j - k > repair.distance - repair.numDeletions) {
                    repair.code = 9;
                    i = this.scopeIndex[this.scopeStackTop];
                    repair.symbol = Parser.scope_lhs[i] + 135;
                    repair.stackPosition = this.stateStackTop;
                    repair.bufferPosition = scope_repair.bufferPosition;
                }
                ++scope_repair.bufferPosition;
            }
        }
        if (repair.code == 0 && this.lexStream.kind(this.buffer[last_index]) == 64) {
            PrimaryRepairInfo scope_repair = new PrimaryRepairInfo();
            scope_repair.bufferPosition = last_index;
            scope_repair.distance = 0;
            int top = this.stateStackTop;
            while (top >= 0 && repair.code == 0) {
                scope_repair = this.scopeTrial(this.stack, top, scope_repair);
                if (scope_repair.distance > 0) {
                    repair.code = 9;
                    i = this.scopeIndex[this.scopeStackTop];
                    repair.symbol = Parser.scope_lhs[i] + 135;
                    repair.stackPosition = top;
                    repair.bufferPosition = scope_repair.bufferPosition;
                }
                --top;
            }
        }
        if (repair.code == 0) {
            return candidate;
        }
        this.secondaryDiagnosis(repair);
        switch (repair.code) {
            case 8: {
                candidate.location = this.buffer[2];
                candidate.symbol = this.lexStream.kind(this.buffer[2]);
                this.lexStream.reset(this.lexStream.next(this.buffer[2]));
                break;
            }
            case 6: {
                candidate.location = this.buffer[repair.bufferPosition];
                candidate.symbol = this.lexStream.kind(this.buffer[repair.bufferPosition]);
                this.lexStream.reset(this.lexStream.next(this.buffer[repair.bufferPosition]));
                break;
            }
            default: {
                candidate.symbol = repair.symbol;
                candidate.location = this.buffer[repair.bufferPosition];
                this.lexStream.reset(this.buffer[repair.bufferPosition]);
            }
        }
        return candidate;
    }

    private SecondaryRepairInfo misplacementRecovery(int[] stck, int stack_top, int last_index, SecondaryRepairInfo repair, boolean stack_flag) {
        int previous_loc = this.buffer[2];
        int stack_deletions = 0;
        int top = stack_top - 1;
        while (top >= 0) {
            if (this.locationStack[top] < previous_loc) {
                ++stack_deletions;
            }
            previous_loc = this.locationStack[top];
            int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[2]), 3);
            if (j == 30) {
                j = last_index;
            }
            if (j > 3 && j - stack_deletions > repair.distance - repair.numDeletions) {
                repair.stackPosition = top;
                repair.distance = j;
                repair.numDeletions = stack_deletions;
                repair.recoveryOnNextStack = stack_flag;
            }
            --top;
        }
        return repair;
    }

    private SecondaryRepairInfo secondaryRecovery(int[] stck, int stack_top, int last_index, SecondaryRepairInfo repair, boolean stack_flag) {
        int stack_deletions = 0;
        int previous_loc = this.buffer[2];
        int top = stack_top;
        while (top >= 0 && repair.numDeletions >= stack_deletions) {
            if (this.locationStack[top] < previous_loc) {
                ++stack_deletions;
            }
            previous_loc = this.locationStack[top];
            int i = 2;
            while (i <= last_index - 3 + 1 && repair.numDeletions >= stack_deletions + i - 1) {
                int k;
                int j = this.parseCheck(stck, top, this.lexStream.kind(this.buffer[i]), i + 1);
                if (j == 30) {
                    j = last_index;
                }
                if (j - i + 1 > 3 && ((k = stack_deletions + i - 1) < repair.numDeletions || j - k > repair.distance - repair.numDeletions || repair.code == 10 && j - k == repair.distance - repair.numDeletions)) {
                    repair.code = 6;
                    repair.distance = j;
                    repair.stackPosition = top;
                    repair.bufferPosition = i;
                    repair.numDeletions = k;
                    repair.recoveryOnNextStack = stack_flag;
                }
                int l = Parser.nasi(stck[top]);
                while (l >= 0 && Parser.nasr[l] != '\u0000') {
                    int k2;
                    int symbol = Parser.nasr[l] + 135;
                    j = this.parseCheck(stck, top, symbol, i);
                    if (j == 30) {
                        j = last_index;
                    }
                    if (j - i + 1 > 3 && ((k2 = stack_deletions + i - 1) < repair.numDeletions || j - k2 > repair.distance - repair.numDeletions)) {
                        repair.code = 10;
                        repair.symbol = symbol;
                        repair.distance = j;
                        repair.stackPosition = top;
                        repair.bufferPosition = i;
                        repair.numDeletions = k2;
                        repair.recoveryOnNextStack = stack_flag;
                    }
                    ++l;
                }
                ++i;
            }
            --top;
        }
        return repair;
    }

    private void secondaryDiagnosis(SecondaryRepairInfo repair) {
        switch (repair.code) {
            case 9: {
                if (repair.stackPosition < this.stateStackTop) {
                    this.reportError(6, Parser.terminal_index[135], this.locationStack[repair.stackPosition], this.buffer[1]);
                }
                int i = 0;
                while (i < this.scopeStackTop) {
                    this.reportError(9, -this.scopeIndex[i], this.locationStack[this.scopePosition[i]], this.buffer[1], Parser.non_terminal_index[Parser.scope_lhs[this.scopeIndex[i]]]);
                    ++i;
                }
                repair.symbol = Parser.scope_lhs[this.scopeIndex[this.scopeStackTop]] + 135;
                this.stateStackTop = this.scopePosition[this.scopeStackTop];
                this.reportError(9, -this.scopeIndex[this.scopeStackTop], this.locationStack[this.scopePosition[this.scopeStackTop]], this.buffer[1], this.getNtermIndex(this.stack[this.stateStackTop], repair.symbol, repair.bufferPosition));
                break;
            }
            default: {
                this.reportError(repair.code, repair.code == 10 ? this.getNtermIndex(this.stack[repair.stackPosition], repair.symbol, repair.bufferPosition) : Parser.terminal_index[135], this.locationStack[repair.stackPosition], this.buffer[repair.bufferPosition - 1]);
                this.stateStackTop = repair.stackPosition;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private int parseCheck(int[] stck, int stack_top, int first_token, int buffer_position) {
        int lhs_symbol;
        int ct;
        int indx;
        int max_pos;
        int act;
        block18: {
            block19: {
                block17: {
                    act = stck[stack_top];
                    if (first_token <= 135) break block17;
                    this.tempStackTop = stack_top;
                    if (this.DEBUG_PARSECHECK) {
                        System.out.println(this.tempStackTop);
                    }
                    max_pos = stack_top;
                    indx = buffer_position;
                    ct = this.lexStream.kind(this.buffer[indx]);
                    this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
                    lhs_symbol = first_token - 135;
                    act = Parser.ntAction(act, lhs_symbol);
                    if (act > 919) break block18;
                    break block19;
                }
                this.tempStackTop = stack_top - 1;
                if (this.DEBUG_PARSECHECK) {
                    System.out.println(this.tempStackTop);
                }
                max_pos = this.tempStackTop;
                indx = buffer_position - 1;
                ct = first_token;
                this.lexStream.reset(this.buffer[buffer_position]);
                break block18;
            }
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.DEBUG_PARSECHECK) {
                    System.out.print(this.tempStackTop);
                    System.out.print(" (");
                    System.out.print(-(Parser.rhs[act] - 1));
                    System.out.print(") [max:");
                    System.out.print(max_pos);
                    System.out.print("]\tprocess_non_terminal\t");
                    System.out.print(act);
                    System.out.print("\t");
                    System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
                    System.out.println();
                }
                if (Parser.rules_compliance[act] > this.options.sourceLevel) {
                    return 0;
                }
                lhs_symbol = Parser.lhs[act];
                int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
            } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
            int n = max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
        }
        while (true) {
            block21: {
                block22: {
                    block20: {
                        if (this.DEBUG_PARSECHECK) {
                            System.out.print(this.tempStackTop + 1);
                            System.out.print(" (+1) [max:");
                            System.out.print(max_pos);
                            System.out.print("]\tprocess_terminal    \t");
                            System.out.print(ct);
                            System.out.print("\t");
                            System.out.print(Parser.name[Parser.terminal_index[ct]]);
                            System.out.println();
                        }
                        if (++this.tempStackTop >= this.stackLength) {
                            return indx;
                        }
                        this.tempStack[this.tempStackTop] = act;
                        if ((act = Parser.tAction(act, ct)) > 919) break block20;
                        --this.tempStackTop;
                        if (this.DEBUG_PARSECHECK) {
                            System.out.print(this.tempStackTop);
                            System.out.print(" (-1) [max:");
                            System.out.print(max_pos);
                            System.out.print("]\treduce");
                            System.out.println();
                        }
                        break block21;
                    }
                    if (act >= 17933 && act <= 17934) break block22;
                    if (indx == 30) {
                        return indx;
                    }
                    ct = this.lexStream.kind(this.buffer[++indx]);
                    this.lexStream.reset(this.lexStream.next(this.buffer[indx]));
                    if (act > 17934) {
                        act -= 17934;
                        if (this.DEBUG_PARSECHECK) {
                            System.out.print(this.tempStackTop);
                            System.out.print("\tshift reduce");
                            System.out.println();
                        }
                        break block21;
                    } else {
                        if (!this.DEBUG_PARSECHECK) continue;
                        System.out.println("\tshift");
                        continue;
                    }
                }
                if (act == 17933) {
                    return 30;
                }
                return indx;
            }
            do {
                this.tempStackTop -= Parser.rhs[act] - 1;
                if (this.DEBUG_PARSECHECK) {
                    System.out.print(this.tempStackTop);
                    System.out.print(" (");
                    System.out.print(-(Parser.rhs[act] - 1));
                    System.out.print(") [max:");
                    System.out.print(max_pos);
                    System.out.print("]\tprocess_non_terminal\t");
                    System.out.print(act);
                    System.out.print("\t");
                    System.out.print(Parser.name[Parser.non_terminal_index[Parser.lhs[act]]]);
                    System.out.println();
                }
                if (act <= 919 && Parser.rules_compliance[act] > this.options.sourceLevel) {
                    return 0;
                }
                lhs_symbol = Parser.lhs[act];
                int n = act = this.tempStackTop > max_pos ? this.tempStack[this.tempStackTop] : stck[this.tempStackTop];
            } while ((act = Parser.ntAction(act, lhs_symbol)) <= 919);
            max_pos = max_pos < this.tempStackTop ? max_pos : this.tempStackTop;
        }
    }

    private void reportError(int msgCode, int nameIndex, int leftToken, int rightToken) {
        this.reportError(msgCode, nameIndex, leftToken, rightToken, 0);
    }

    private void reportError(int msgCode, int nameIndex, int leftToken, int rightToken, int scopeNameIndex) {
        int lToken;
        int n = lToken = leftToken > rightToken ? rightToken : leftToken;
        if (lToken < rightToken) {
            this.reportSecondaryError(msgCode, nameIndex, lToken, rightToken, scopeNameIndex);
        } else {
            this.reportPrimaryError(msgCode, nameIndex, rightToken, scopeNameIndex);
        }
    }

    private void reportPrimaryError(int msgCode, int nameIndex, int token, int scopeNameIndex) {
        String name = nameIndex >= 0 ? Parser.readableName[nameIndex] : Util.EMPTY_STRING;
        int errorStart = this.lexStream.start(token);
        int errorEnd = this.lexStream.end(token);
        int currentKind = this.lexStream.kind(token);
        String errorTokenName = Parser.name[Parser.terminal_index[this.lexStream.kind(token)]];
        char[] errorTokenSource = this.lexStream.name(token);
        if (currentKind == 60) {
            errorTokenSource = DiagnoseParser.displayEscapeCharacters(errorTokenSource, 1, errorTokenSource.length - 1);
        }
        int addedToken = -1;
        if (this.recoveryScanner != null && nameIndex >= 0) {
            addedToken = Parser.reverse_index[nameIndex];
        }
        switch (msgCode) {
            case 2: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.insertToken(addedToken, -1, errorStart);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.insertTokens(template, -1, errorStart);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorInsertBeforeToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                break;
            }
            case 3: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.insertToken(addedToken, -1, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.insertTokens(template, -1, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorInsertAfterToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                break;
            }
            case 6: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorDeleteToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName);
                break;
            }
            case 4: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (!this.reportProblem) break;
                    this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                    break;
                }
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorInvalidToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                break;
            }
            case 5: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
                break;
            }
            case 9: {
                StringBuffer buf = new StringBuffer();
                int[] addedTokens = null;
                int addedTokenCount = 0;
                if (this.recoveryScanner != null) {
                    addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[-nameIndex]];
                }
                int insertedToken = 0;
                int i = Parser.scope_suffix[-nameIndex];
                while (Parser.scope_rhs[i] != '\u0000') {
                    buf.append(Parser.readableName[Parser.scope_rhs[i]]);
                    if (Parser.scope_rhs[i + '\u0001'] != '\u0000') {
                        buf.append(' ');
                    } else {
                        insertedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                    }
                    if (addedTokens != null) {
                        int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                        if (tmpAddedToken > -1) {
                            int length = addedTokens.length;
                            if (addedTokenCount == length) {
                                int[] nArray = addedTokens;
                                addedTokens = new int[length * 2];
                                System.arraycopy(nArray, 0, addedTokens, 0, length);
                            }
                            addedTokens[addedTokenCount++] = tmpAddedToken;
                        } else {
                            int[] template = this.getNTermTemplate(-tmpAddedToken);
                            if (template != null) {
                                int j = 0;
                                while (j < template.length) {
                                    int length = addedTokens.length;
                                    if (addedTokenCount == length) {
                                        int[] nArray = addedTokens;
                                        addedTokens = new int[length * 2];
                                        System.arraycopy(nArray, 0, addedTokens, 0, length);
                                    }
                                    addedTokens[addedTokenCount++] = template[j];
                                    ++j;
                                }
                            } else {
                                addedTokenCount = 0;
                                addedTokens = null;
                            }
                        }
                    }
                    ++i;
                }
                if (addedTokenCount > 0) {
                    int[] nArray = addedTokens;
                    addedTokens = new int[addedTokenCount];
                    System.arraycopy(nArray, 0, addedTokens, 0, addedTokenCount);
                    int completedToken = -1;
                    if (scopeNameIndex != 0) {
                        completedToken = -Parser.reverse_index[scopeNameIndex];
                    }
                    this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
                }
                if (scopeNameIndex != 0) {
                    if (insertedToken == 71 || !this.reportProblem) break;
                    this.problemReporter().parseErrorInsertToComplete(errorStart, errorEnd, buf.toString(), Parser.readableName[scopeNameIndex]);
                    break;
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorInsertToCompleteScope(errorStart, errorEnd, buf.toString());
                break;
            }
            case 11: {
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorUnexpectedEnd(errorStart, errorEnd);
                break;
            }
            case 7: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorMergeTokens(errorStart, errorEnd, name);
                break;
            }
            case 8: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorMisplacedConstruct(errorStart, errorEnd);
                break;
            }
            default: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (!this.reportProblem) break;
                    this.problemReporter().parseErrorNoSuggestion(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName);
                    break;
                }
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorReplaceToken(errorStart, errorEnd, currentKind, errorTokenSource, errorTokenName, name);
            }
        }
    }

    private void reportSecondaryError(int msgCode, int nameIndex, int leftToken, int rightToken, int scopeNameIndex) {
        String name = nameIndex >= 0 ? Parser.readableName[nameIndex] : Util.EMPTY_STRING;
        int errorStart = -1;
        if (this.lexStream.isInsideStream(leftToken)) {
            errorStart = leftToken == 0 ? this.lexStream.start(leftToken + 1) : this.lexStream.start(leftToken);
        } else {
            if (leftToken == this.errorToken) {
                errorStart = this.errorTokenStart;
            } else {
                int i = 0;
                while (i <= this.stateStackTop) {
                    if (this.locationStack[i] == leftToken) {
                        errorStart = this.locationStartStack[i];
                    }
                    ++i;
                }
            }
            if (errorStart == -1) {
                errorStart = this.lexStream.start(rightToken);
            }
        }
        int errorEnd = this.lexStream.end(rightToken);
        int addedToken = -1;
        if (this.recoveryScanner != null && nameIndex >= 0) {
            addedToken = Parser.reverse_index[nameIndex];
        }
        switch (msgCode) {
            case 8: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorMisplacedConstruct(errorStart, errorEnd);
                break;
            }
            case 9: {
                errorStart = this.lexStream.start(rightToken);
                StringBuffer buf = new StringBuffer();
                int[] addedTokens = null;
                int addedTokenCount = 0;
                if (this.recoveryScanner != null) {
                    addedTokens = new int[Parser.scope_rhs.length - Parser.scope_suffix[-nameIndex]];
                }
                int insertedToken = 0;
                int i = Parser.scope_suffix[-nameIndex];
                while (Parser.scope_rhs[i] != '\u0000') {
                    buf.append(Parser.readableName[Parser.scope_rhs[i]]);
                    if (Parser.scope_rhs[i + '\u0001'] != '\u0000') {
                        buf.append(' ');
                    } else {
                        insertedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                    }
                    if (addedTokens != null) {
                        int tmpAddedToken = Parser.reverse_index[Parser.scope_rhs[i]];
                        if (tmpAddedToken > -1) {
                            int length = addedTokens.length;
                            if (addedTokenCount == length) {
                                int[] nArray = addedTokens;
                                addedTokens = new int[length * 2];
                                System.arraycopy(nArray, 0, addedTokens, 0, length);
                            }
                            addedTokens[addedTokenCount++] = tmpAddedToken;
                        } else {
                            int[] template = this.getNTermTemplate(-tmpAddedToken);
                            if (template != null) {
                                int j = 0;
                                while (j < template.length) {
                                    int length = addedTokens.length;
                                    if (addedTokenCount == length) {
                                        int[] nArray = addedTokens;
                                        addedTokens = new int[length * 2];
                                        System.arraycopy(nArray, 0, addedTokens, 0, length);
                                    }
                                    addedTokens[addedTokenCount++] = template[j];
                                    ++j;
                                }
                            } else {
                                addedTokenCount = 0;
                                addedTokens = null;
                            }
                        }
                    }
                    ++i;
                }
                if (addedTokenCount > 0) {
                    int[] nArray = addedTokens;
                    addedTokens = new int[addedTokenCount];
                    System.arraycopy(nArray, 0, addedTokens, 0, addedTokenCount);
                    int completedToken = -1;
                    if (scopeNameIndex != 0) {
                        completedToken = -Parser.reverse_index[scopeNameIndex];
                    }
                    this.recoveryScanner.insertTokens(addedTokens, completedToken, errorEnd);
                }
                if (scopeNameIndex != 0) {
                    if (insertedToken == 71 || !this.reportProblem) break;
                    this.problemReporter().parseErrorInsertToComplete(errorStart, errorEnd, buf.toString(), Parser.readableName[scopeNameIndex]);
                    break;
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorInsertToCompletePhrase(errorStart, errorEnd, buf.toString());
                break;
            }
            case 7: {
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorMergeTokens(errorStart, errorEnd, name);
                break;
            }
            case 6: {
                if (this.recoveryScanner != null) {
                    this.recoveryScanner.removeTokens(errorStart, errorEnd);
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorDeleteTokens(errorStart, errorEnd);
                break;
            }
            default: {
                if (name.length() == 0) {
                    if (this.recoveryScanner != null) {
                        this.recoveryScanner.removeTokens(errorStart, errorEnd);
                    }
                    if (!this.reportProblem) break;
                    this.problemReporter().parseErrorNoSuggestionForTokens(errorStart, errorEnd);
                    break;
                }
                if (this.recoveryScanner != null) {
                    if (addedToken > -1) {
                        this.recoveryScanner.replaceTokens(addedToken, errorStart, errorEnd);
                    } else {
                        int[] template = this.getNTermTemplate(-addedToken);
                        if (template != null) {
                            this.recoveryScanner.replaceTokens(template, errorStart, errorEnd);
                        }
                    }
                }
                if (!this.reportProblem) break;
                this.problemReporter().parseErrorReplaceTokens(errorStart, errorEnd, name);
            }
        }
    }

    private int[] getNTermTemplate(int sym) {
        int templateIndex = Parser.recovery_templates_index[sym];
        if (templateIndex > 0) {
            int[] result = new int[Parser.recovery_templates.length];
            int count = 0;
            int j = templateIndex;
            while (Parser.recovery_templates[j] != '\u0000') {
                result[count++] = Parser.recovery_templates[j];
                ++j;
            }
            int[] nArray = result;
            result = new int[count];
            System.arraycopy(nArray, 0, result, 0, count);
            return result;
        }
        return null;
    }

    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(this.lexStream.toString());
        return res.toString();
    }

    @Override
    public boolean atConflictScenario(int token) {
        return token == 23 || token == 36 || token == 11 && !this.lexStream.awaitingColonColon();
    }

    @Override
    public boolean isParsingModuleDeclaration() {
        return this.parser.isParsingModuleDeclaration();
    }

    @Override
    public boolean isParsingJava14() {
        return this.parser.isParsingJava14();
    }

    private static class PrimaryRepairInfo {
        public int distance = 0;
        public int misspellIndex = 0;
        public int code = 0;
        public int bufferPosition = 0;
        public int symbol = 0;

        public PrimaryRepairInfo copy() {
            PrimaryRepairInfo c = new PrimaryRepairInfo();
            c.distance = this.distance;
            c.misspellIndex = this.misspellIndex;
            c.code = this.code;
            c.bufferPosition = this.bufferPosition;
            c.symbol = this.symbol;
            return c;
        }
    }

    private static class RepairCandidate {
        public int symbol = 0;
        public int location = 0;
    }

    static class SecondaryRepairInfo {
        public int code;
        public int distance;
        public int bufferPosition;
        public int stackPosition;
        public int numDeletions;
        public int symbol;
        boolean recoveryOnNextStack;

        SecondaryRepairInfo() {
        }
    }

    private static class StateInfo {
        int state;
        int next;

        public StateInfo(int state, int next) {
            this.state = state;
            this.next = next;
        }
    }
}

