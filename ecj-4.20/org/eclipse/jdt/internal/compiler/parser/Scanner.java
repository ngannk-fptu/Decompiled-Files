/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.JavaFeature;
import org.eclipse.jdt.internal.compiler.parser.ConflictedParser;
import org.eclipse.jdt.internal.compiler.parser.NLSTag;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Scanner
implements TerminalTokens {
    public long sourceLevel;
    public long complianceLevel;
    public boolean useAssertAsAnIndentifier = false;
    public boolean containsAssertKeyword = false;
    public boolean previewEnabled;
    public boolean useEnumAsAnIndentifier = false;
    public boolean recordLineSeparator = false;
    public char currentCharacter;
    public int startPosition;
    public int currentPosition;
    public int initialPosition;
    public int eofPosition;
    public boolean skipComments = false;
    public boolean tokenizeComments = false;
    public boolean tokenizeWhiteSpace = false;
    public char[] source;
    public char[] withoutUnicodeBuffer;
    public int withoutUnicodePtr;
    public boolean unicodeAsBackSlash = false;
    public boolean scanningFloatLiteral = false;
    public static final int COMMENT_ARRAYS_SIZE = 30;
    public int[] commentStops = new int[30];
    public int[] commentStarts = new int[30];
    public int[] commentTagStarts = new int[30];
    public int commentPtr = -1;
    public int lastCommentLinePosition = -1;
    public char[][] foundTaskTags = null;
    public char[][] foundTaskMessages;
    public char[][] foundTaskPriorities = null;
    public int[][] foundTaskPositions;
    public int foundTaskCount = 0;
    public char[][] taskTags = null;
    public char[][] taskPriorities = null;
    public boolean isTaskCaseSensitive = true;
    public boolean diet = false;
    public int[] lineEnds = new int[250];
    public int linePtr = -1;
    public boolean wasAcr = false;
    public boolean fakeInModule = false;
    int caseStartPosition = -1;
    boolean inCondition = false;
    int yieldColons = -1;
    boolean breakPreviewAllowed = false;
    protected ScanContext scanContext = null;
    protected boolean insideModuleInfo = false;
    public static final String END_OF_SOURCE = "End_Of_Source";
    public static final String INVALID_HEXA = "Invalid_Hexa_Literal";
    public static final String INVALID_OCTAL = "Invalid_Octal_Literal";
    public static final String INVALID_CHARACTER_CONSTANT = "Invalid_Character_Constant";
    public static final String INVALID_ESCAPE = "Invalid_Escape";
    public static final String INVALID_INPUT = "Invalid_Input";
    public static final String INVALID_TEXTBLOCK = "Invalid_Textblock";
    public static final String INVALID_UNICODE_ESCAPE = "Invalid_Unicode_Escape";
    public static final String INVALID_FLOAT = "Invalid_Float_Literal";
    public static final String INVALID_LOW_SURROGATE = "Invalid_Low_Surrogate";
    public static final String INVALID_HIGH_SURROGATE = "Invalid_High_Surrogate";
    public static final String NULL_SOURCE_STRING = "Null_Source_String";
    public static final String UNTERMINATED_STRING = "Unterminated_String";
    public static final String UNTERMINATED_TEXT_BLOCK = "Unterminated_Text_Block";
    public static final String UNTERMINATED_COMMENT = "Unterminated_Comment";
    public static final String INVALID_CHAR_IN_STRING = "Invalid_Char_In_String";
    public static final String INVALID_DIGIT = "Invalid_Digit";
    private static final int[] EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
    public static final String INVALID_BINARY = "Invalid_Binary_Literal";
    public static final String BINARY_LITERAL_NOT_BELOW_17 = "Binary_Literal_Not_Below_17";
    public static final String ILLEGAL_HEXA_LITERAL = "Illegal_Hexa_Literal";
    public static final String INVALID_UNDERSCORE = "Invalid_Underscore";
    public static final String UNDERSCORES_IN_LITERALS_NOT_BELOW_17 = "Underscores_In_Literals_Not_Below_17";
    static final char[] charArray_a = new char[]{'a'};
    static final char[] charArray_b = new char[]{'b'};
    static final char[] charArray_c = new char[]{'c'};
    static final char[] charArray_d = new char[]{'d'};
    static final char[] charArray_e = new char[]{'e'};
    static final char[] charArray_f = new char[]{'f'};
    static final char[] charArray_g = new char[]{'g'};
    static final char[] charArray_h = new char[]{'h'};
    static final char[] charArray_i = new char[]{'i'};
    static final char[] charArray_j = new char[]{'j'};
    static final char[] charArray_k = new char[]{'k'};
    static final char[] charArray_l = new char[]{'l'};
    static final char[] charArray_m = new char[]{'m'};
    static final char[] charArray_n = new char[]{'n'};
    static final char[] charArray_o = new char[]{'o'};
    static final char[] charArray_p = new char[]{'p'};
    static final char[] charArray_q = new char[]{'q'};
    static final char[] charArray_r = new char[]{'r'};
    static final char[] charArray_s = new char[]{'s'};
    static final char[] charArray_t = new char[]{'t'};
    static final char[] charArray_u = new char[]{'u'};
    static final char[] charArray_v = new char[]{'v'};
    static final char[] charArray_w = new char[]{'w'};
    static final char[] charArray_x = new char[]{'x'};
    static final char[] charArray_y = new char[]{'y'};
    static final char[] charArray_z = new char[]{'z'};
    static final char[] initCharArray = new char[6];
    static final int TableSize = 30;
    static final int InternalTableSize = 6;
    public static final int OptimizedLength = 7;
    public final char[][][][] charArray_length = new char[7][30][6][];
    public static final char[] TAG_PREFIX = "//$NON-NLS-".toCharArray();
    public static final int TAG_PREFIX_LENGTH = TAG_PREFIX.length;
    public static final char TAG_POSTFIX = '$';
    public static final int TAG_POSTFIX_LENGTH = 1;
    public static final char[] IDENTITY_COMPARISON_TAG = "//$IDENTITY-COMPARISON$".toCharArray();
    public boolean[] validIdentityComparisonLines;
    public boolean checkUninternedIdentityComparison;
    private NLSTag[] nlsTags = null;
    protected int nlsTagsPtr;
    public boolean checkNonExternalizedStringLiterals;
    protected int lastPosition;
    public boolean returnOnlyGreater = false;
    int newEntry2;
    int newEntry3;
    int newEntry4;
    int newEntry5;
    int newEntry6;
    public boolean insideRecovery;
    int[] lookBack;
    protected int nextToken;
    private VanguardScanner vanguardScanner;
    private VanguardParser vanguardParser;
    ConflictedParser activeParser;
    private boolean consumingEllipsisAnnotations;
    public static final int RoundBracket = 0;
    public static final int SquareBracket = 1;
    public static final int CurlyBracket = 2;
    public static final int BracketKinds = 3;
    public static final int LOW_SURROGATE_MIN_VALUE = 56320;
    public static final int HIGH_SURROGATE_MIN_VALUE = 55296;
    public static final int HIGH_SURROGATE_MAX_VALUE = 56319;
    public static final int LOW_SURROGATE_MAX_VALUE = 57343;
    int rawStart;
    Map<String, Integer> _Keywords;

    public Scanner() {
        this(false, false, false, 0x2F0000L, null, null, true);
    }

    public Scanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, long complianceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive, boolean isPreviewEnabled) {
        int i = 0;
        while (i < 6) {
            int j = 0;
            while (j < 30) {
                int k = 0;
                while (k < 6) {
                    this.charArray_length[i][j][k] = initCharArray;
                    ++k;
                }
                ++j;
            }
            ++i;
        }
        this.newEntry2 = 0;
        this.newEntry3 = 0;
        this.newEntry4 = 0;
        this.newEntry5 = 0;
        this.newEntry6 = 0;
        this.insideRecovery = false;
        this.lookBack = new int[2];
        this.nextToken = 0;
        this.activeParser = null;
        this.consumingEllipsisAnnotations = false;
        this.rawStart = -1;
        this._Keywords = null;
        this.eofPosition = Integer.MAX_VALUE;
        this.tokenizeComments = tokenizeComments;
        this.tokenizeWhiteSpace = tokenizeWhiteSpace;
        this.sourceLevel = sourceLevel;
        this.nextToken = 0;
        this.lookBack[1] = 0;
        this.lookBack[0] = 0;
        this.consumingEllipsisAnnotations = false;
        this.complianceLevel = complianceLevel;
        this.checkNonExternalizedStringLiterals = checkNonExternalizedStringLiterals;
        this.previewEnabled = isPreviewEnabled;
        this.caseStartPosition = -1;
        if (taskTags != null) {
            int taskTagsLength;
            int length = taskTagsLength = ((char[][])taskTags).length;
            if (taskPriorities != null) {
                int taskPrioritiesLength = ((char[][])taskPriorities).length;
                if (taskPrioritiesLength != taskTagsLength) {
                    if (taskPrioritiesLength > taskTagsLength) {
                        char[][] cArray = taskPriorities;
                        char[][] cArrayArray = new char[taskTagsLength][];
                        taskPriorities = cArrayArray;
                        System.arraycopy(cArray, 0, cArrayArray, 0, taskTagsLength);
                    } else {
                        char[][] cArray = taskTags;
                        char[][] cArrayArray = new char[taskPrioritiesLength][];
                        taskTags = cArrayArray;
                        System.arraycopy(cArray, 0, cArrayArray, 0, taskPrioritiesLength);
                        length = taskPrioritiesLength;
                    }
                }
                int[] initialIndexes = new int[length];
                int i2 = 0;
                while (i2 < length) {
                    initialIndexes[i2] = i2;
                    ++i2;
                }
                Util.reverseQuickSort(taskTags, 0, length - 1, initialIndexes);
                char[][] temp = new char[length][];
                int i3 = 0;
                while (i3 < length) {
                    temp[i3] = taskPriorities[initialIndexes[i3]];
                    ++i3;
                }
                this.taskPriorities = temp;
            } else {
                Util.reverseQuickSort(taskTags, 0, length - 1);
            }
            this.taskTags = taskTags;
            this.isTaskCaseSensitive = isTaskCaseSensitive;
        }
    }

    public Scanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive, boolean isPreviewEnabled) {
        this(tokenizeComments, tokenizeWhiteSpace, checkNonExternalizedStringLiterals, sourceLevel, sourceLevel, taskTags, taskPriorities, isTaskCaseSensitive, isPreviewEnabled);
    }

    public Scanner(boolean tokenizeComments, boolean tokenizeWhiteSpace, boolean checkNonExternalizedStringLiterals, long sourceLevel, char[][] taskTags, char[][] taskPriorities, boolean isTaskCaseSensitive) {
        this(tokenizeComments, tokenizeWhiteSpace, checkNonExternalizedStringLiterals, sourceLevel, sourceLevel, taskTags, taskPriorities, isTaskCaseSensitive, false);
    }

    public final boolean atEnd() {
        return this.eofPosition <= this.currentPosition;
    }

    /*
     * Unable to fully structure code
     */
    public void checkTaskTag(int commentStart, int commentEnd) throws InvalidInputException {
        src = this.source;
        if (this.foundTaskCount > 0 && this.foundTaskPositions[this.foundTaskCount - 1][0] >= commentStart) {
            return;
        }
        foundTaskIndex = this.foundTaskCount;
        previous = src[commentStart + 1];
        i = commentStart + 2;
        while (i < commentEnd && i < this.eofPosition) {
            tag = null;
            priority = null;
            if (previous != '@') {
                itag = 0;
                while (itag < this.taskTags.length) {
                    block25: {
                        tag = this.taskTags[itag];
                        tagLength = tag.length;
                        if (!(tagLength == 0 || ScannerHelper.isJavaIdentifierStart(this.complianceLevel, tag[0]) && ScannerHelper.isJavaIdentifierPart(this.complianceLevel, previous))) {
                            t = 0;
                            while (t < tagLength) {
                                x = i + t;
                                if (x < this.eofPosition && x < commentEnd && ((sc = src[i + t]) == (tc = tag[t]) || !this.isTaskCaseSensitive && ScannerHelper.toLowerCase(sc) == ScannerHelper.toLowerCase(tc))) {
                                    ++t;
                                    continue;
                                }
                                break block25;
                            }
                            if (i + tagLength >= commentEnd || !ScannerHelper.isJavaIdentifierPart(this.complianceLevel, src[i + tagLength - 1]) || !ScannerHelper.isJavaIdentifierPart(this.complianceLevel, src[i + tagLength])) {
                                if (this.foundTaskTags == null) {
                                    this.foundTaskTags = new char[5][];
                                    this.foundTaskMessages = new char[5][];
                                    this.foundTaskPriorities = new char[5][];
                                    this.foundTaskPositions = new int[5][];
                                } else if (this.foundTaskCount == this.foundTaskTags.length) {
                                    v0 = new char[this.foundTaskCount * 2][];
                                    this.foundTaskTags = v0;
                                    System.arraycopy(this.foundTaskTags, 0, v0, 0, this.foundTaskCount);
                                    v1 = new char[this.foundTaskCount * 2][];
                                    this.foundTaskMessages = v1;
                                    System.arraycopy(this.foundTaskMessages, 0, v1, 0, this.foundTaskCount);
                                    v2 = new char[this.foundTaskCount * 2][];
                                    this.foundTaskPriorities = v2;
                                    System.arraycopy(this.foundTaskPriorities, 0, v2, 0, this.foundTaskCount);
                                    v3 = new int[this.foundTaskCount * 2][];
                                    this.foundTaskPositions = v3;
                                    System.arraycopy(this.foundTaskPositions, 0, v3, 0, this.foundTaskCount);
                                }
                                priority = this.taskPriorities != null && itag < this.taskPriorities.length ? this.taskPriorities[itag] : null;
                                this.foundTaskTags[this.foundTaskCount] = tag;
                                this.foundTaskPriorities[this.foundTaskCount] = priority;
                                this.foundTaskPositions[this.foundTaskCount] = new int[]{i, i + tagLength - 1};
                                this.foundTaskMessages[this.foundTaskCount] = CharOperation.NO_CHAR;
                                ++this.foundTaskCount;
                                i += tagLength - 1;
                                break;
                            }
                        }
                    }
                    ++itag;
                }
            }
            previous = src[i];
            ++i;
        }
        containsEmptyTask = false;
        i = foundTaskIndex;
        while (i < this.foundTaskCount) {
            block26: {
                msgStart = this.foundTaskPositions[i][0] + this.foundTaskTags[i].length;
                v4 = max_value = i + 1 < this.foundTaskCount ? this.foundTaskPositions[i + 1][0] - 1 : commentEnd - 1;
                if (max_value < msgStart) {
                    max_value = msgStart;
                }
                end = -1;
                j = msgStart;
                while (j < max_value) {
                    c = src[j];
                    if (c == '\n' || c == '\r') {
                        end = j - 1;
                        break;
                    }
                    ++j;
                }
                if (end == -1) {
                    j = max_value;
                    while (j > msgStart) {
                        c = src[j];
                        if (c == '*') {
                            end = j - 1;
                            break;
                        }
                        --j;
                    }
                    if (end == -1) {
                        end = max_value;
                    }
                }
                if (msgStart != end) ** GOTO lbl88
                containsEmptyTask = true;
                break block26;
lbl-1000:
                // 1 sources

                {
                    --end;
lbl88:
                    // 2 sources

                    ** while (CharOperation.isWhitespace((char)src[end]) && msgStart <= end)
                }
lbl89:
                // 1 sources

                this.foundTaskPositions[i][1] = end;
                messageLength = end - msgStart + 1;
                message = new char[messageLength];
                System.arraycopy(src, msgStart, message, 0, messageLength);
                this.foundTaskMessages[i] = message;
            }
            ++i;
        }
        if (containsEmptyTask) {
            i = foundTaskIndex;
            max = this.foundTaskCount;
            while (i < max) {
                if (this.foundTaskMessages[i].length == 0) {
                    j = i + 1;
                    while (j < max) {
                        if (this.foundTaskMessages[j].length != 0) {
                            this.foundTaskMessages[i] = this.foundTaskMessages[j];
                            this.foundTaskPositions[i][1] = this.foundTaskPositions[j][1];
                            break;
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
    }

    public char[] getCurrentIdentifierSource() {
        if (this.withoutUnicodePtr != 0) {
            char[] result = new char[this.withoutUnicodePtr];
            System.arraycopy(this.withoutUnicodeBuffer, 1, result, 0, this.withoutUnicodePtr);
            return result;
        }
        int length = this.currentPosition - this.startPosition;
        if (length == this.eofPosition) {
            return this.source;
        }
        switch (length) {
            case 1: {
                return this.optimizedCurrentTokenSource1();
            }
            case 2: {
                return this.optimizedCurrentTokenSource2();
            }
            case 3: {
                return this.optimizedCurrentTokenSource3();
            }
            case 4: {
                return this.optimizedCurrentTokenSource4();
            }
            case 5: {
                return this.optimizedCurrentTokenSource5();
            }
            case 6: {
                return this.optimizedCurrentTokenSource6();
            }
        }
        char[] result = new char[length];
        System.arraycopy(this.source, this.startPosition, result, 0, length);
        return result;
    }

    public int getCurrentTokenEndPosition() {
        return this.currentPosition - 1;
    }

    public char[] getCurrentTokenSource() {
        char[] result;
        if (this.withoutUnicodePtr != 0) {
            result = new char[this.withoutUnicodePtr];
            System.arraycopy(this.withoutUnicodeBuffer, 1, result, 0, this.withoutUnicodePtr);
        } else {
            int length = this.currentPosition - this.startPosition;
            result = new char[length];
            System.arraycopy(this.source, this.startPosition, result, 0, length);
        }
        return result;
    }

    public final String getCurrentTokenString() {
        if (this.withoutUnicodePtr != 0) {
            return new String(this.withoutUnicodeBuffer, 1, this.withoutUnicodePtr);
        }
        return new String(this.source, this.startPosition, this.currentPosition - this.startPosition);
    }

    public char[] getCurrentTokenSourceString() {
        char[] result;
        if (this.withoutUnicodePtr != 0) {
            result = new char[this.withoutUnicodePtr - 2];
            System.arraycopy(this.withoutUnicodeBuffer, 2, result, 0, this.withoutUnicodePtr - 2);
        } else {
            int length = this.currentPosition - this.startPosition - 2;
            result = new char[length];
            System.arraycopy(this.source, this.startPosition + 1, result, 0, length);
        }
        return result;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected final boolean scanForTextBlockBeginning() {
        try {
            int temp = this.currentPosition;
            if (this.source[temp++] != '\"') return false;
            if (this.source[temp++] != '\"') return false;
            char c = this.source[temp++];
            while (true) {
                if (!ScannerHelper.isWhitespace(c)) {
                    return false;
                }
                switch (c) {
                    case '\n': {
                        this.currentCharacter = c;
                        this.currentPosition = temp;
                        return true;
                    }
                }
                c = this.source[temp++];
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
        return false;
    }

    protected final boolean scanForTextBlockClose() throws InvalidInputException {
        try {
            if (this.source[this.currentPosition] == '\"' && this.source[this.currentPosition + 1] == '\"') {
                return true;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {}
        return false;
    }

    public char[] getCurrentTextBlock() {
        char[] all;
        if (this.withoutUnicodePtr != 0) {
            all = CharOperation.subarray(this.withoutUnicodeBuffer, this.rawStart + 1, this.withoutUnicodePtr + 1);
        } else {
            all = CharOperation.subarray(this.source, this.startPosition + this.rawStart, this.currentPosition - 3);
            if (all == null) {
                all = new char[]{};
            }
        }
        all = this.normalize(all);
        char[][] lines = CharOperation.splitOn('\n', all);
        int size = lines.length;
        ArrayList<char[]> list = new ArrayList<char[]>(lines.length);
        int i = 0;
        while (i < lines.length) {
            char[] line = lines[i];
            if (i + 1 == size && line.length == 0) {
                list.add(line);
                break;
            }
            char[][] sub = CharOperation.splitOn('\r', line);
            if (sub.length == 0) {
                list.add(line);
            } else {
                char[][] cArray = sub;
                int n = sub.length;
                int n2 = 0;
                while (n2 < n) {
                    char[] cs = cArray[n2];
                    list.add(cs);
                    ++n2;
                }
            }
            ++i;
        }
        size = list.size();
        lines = (char[][])list.toArray((T[])new char[size][]);
        int prefix = -1;
        int i2 = 0;
        while (i2 < size) {
            char[] line = lines[i2];
            boolean blank = true;
            int whitespaces = 0;
            char[] cArray = line;
            int n = line.length;
            int n3 = 0;
            while (n3 < n) {
                char c = cArray[n3];
                if (blank) {
                    if (ScannerHelper.isWhitespace(c)) {
                        ++whitespaces;
                    } else {
                        blank = false;
                    }
                }
                ++n3;
            }
            if (!(blank && i2 + 1 != size || prefix >= 0 && whitespaces >= prefix)) {
                prefix = whitespaces;
            }
            ++i2;
        }
        if (prefix == -1) {
            prefix = 0;
        }
        StringBuilder result = new StringBuilder();
        boolean newLine = false;
        int i3 = 0;
        while (i3 < lines.length) {
            int length;
            char[] l = lines[i3];
            int trail = length = l.length;
            while (trail > 0) {
                if (!ScannerHelper.isWhitespace(l[trail - 1])) break;
                --trail;
            }
            if (i3 >= size - 1) {
                if (newLine) {
                    result.append('\n');
                }
                if (trail >= prefix) {
                    newLine = this.getLineContent(result, l, prefix, trail - 1, false, true);
                }
            } else {
                if (i3 > 0 && newLine) {
                    result.append('\n');
                }
                if (trail <= prefix) {
                    newLine = true;
                } else {
                    boolean merge = length > 0 && l[length - 1] == '\\';
                    newLine = this.getLineContent(result, l, prefix, trail - 1, merge, false);
                }
            }
            ++i3;
        }
        this.rawStart = -1;
        return result.toString().toCharArray();
    }

    private char[] normalize(char[] content) {
        StringBuilder result = new StringBuilder();
        boolean isCR = false;
        char[] cArray = content;
        int n = content.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            switch (c) {
                case '\r': {
                    result.append(c);
                    isCR = true;
                    break;
                }
                case '\n': {
                    if (!isCR) {
                        result.append(c);
                    }
                    isCR = false;
                    break;
                }
                default: {
                    result.append(c);
                    isCR = false;
                }
            }
            ++n2;
        }
        return result.toString().toCharArray();
    }

    private boolean getLineContent(StringBuilder result, char[] line, int start, int end, boolean merge, boolean lastLine) {
        char[] chars;
        int lastPointer = 0;
        int i = start;
        block11: while (i < end) {
            char c = line[i];
            if (c != '\\') {
                ++i;
                continue;
            }
            if (i >= end) continue;
            if (lastPointer + 1 <= i) {
                result.append(CharOperation.subarray(line, lastPointer == 0 ? start : lastPointer, i));
            }
            char next = line[++i];
            switch (next) {
                case '\\': {
                    result.append('\\');
                    if (i != end) break;
                    merge = false;
                    break;
                }
                case 's': {
                    result.append(' ');
                    break;
                }
                case 'b': {
                    result.append('\b');
                    break;
                }
                case 'n': {
                    result.append('\n');
                    break;
                }
                case 'r': {
                    result.append('\r');
                    break;
                }
                case 't': {
                    result.append('\t');
                    break;
                }
                case 'f': {
                    result.append('\f');
                    break;
                }
                default: {
                    int pos = i + 1;
                    int number = ScannerHelper.getHexadecimalValue(next);
                    if (number >= 0 && number <= 7) {
                        boolean zeroToThreeNot = number > 3;
                        try {
                            next = line[pos];
                            if (ScannerHelper.isDigit(next)) {
                                ++pos;
                                int digit = ScannerHelper.getHexadecimalValue(next);
                                if (digit >= 0 && digit <= 7) {
                                    number = number * 8 + digit;
                                    next = line[pos];
                                    if (ScannerHelper.isDigit(next)) {
                                        ++pos;
                                        if (!zeroToThreeNot && (digit = ScannerHelper.getHexadecimalValue(next)) >= 0 && digit <= 7) {
                                            number = number * 8 + digit;
                                        }
                                    }
                                }
                            }
                        }
                        catch (InvalidInputException invalidInputException) {}
                        if (number < 255) {
                            next = (char)number;
                        }
                        result.append(next);
                        lastPointer = i = pos;
                        continue block11;
                    }
                    result.append(c);
                    lastPointer = i;
                    continue block11;
                }
            }
            lastPointer = ++i;
        }
        end = merge ? end : (end >= line.length ? end : end + 1);
        char[] cArray = chars = lastPointer == 0 ? CharOperation.subarray(line, start, end) : CharOperation.subarray(line, lastPointer, end);
        if (chars != null && chars.length > 0) {
            result.append(chars);
        }
        return !merge && !lastLine;
    }

    public final String getCurrentStringLiteral() {
        if (this.withoutUnicodePtr != 0) {
            return new String(this.withoutUnicodeBuffer, 2, this.withoutUnicodePtr - 2);
        }
        return new String(this.source, this.startPosition + 1, this.currentPosition - this.startPosition - 2);
    }

    public final char[] getRawTokenSource() {
        int length = this.currentPosition - this.startPosition;
        char[] tokenSource = new char[length];
        System.arraycopy(this.source, this.startPosition, tokenSource, 0, length);
        return tokenSource;
    }

    public final char[] getRawTokenSourceEnd() {
        int length = this.eofPosition - this.currentPosition - 1;
        char[] sourceEnd = new char[length];
        System.arraycopy(this.source, this.currentPosition, sourceEnd, 0, length);
        return sourceEnd;
    }

    public int getCurrentTokenStartPosition() {
        return this.startPosition;
    }

    public final int getLineEnd(int lineNumber) {
        if (this.lineEnds == null || this.linePtr == -1) {
            return -1;
        }
        if (lineNumber > this.lineEnds.length + 1) {
            return -1;
        }
        if (lineNumber <= 0) {
            return -1;
        }
        if (lineNumber == this.lineEnds.length + 1) {
            return this.eofPosition;
        }
        return this.lineEnds[lineNumber - 1];
    }

    public final int[] getLineEnds() {
        if (this.linePtr == -1) {
            return EMPTY_LINE_ENDS;
        }
        int[] copy = new int[this.linePtr + 1];
        System.arraycopy(this.lineEnds, 0, copy, 0, this.linePtr + 1);
        return copy;
    }

    public final int getLineStart(int lineNumber) {
        if (this.lineEnds == null || this.linePtr == -1) {
            return -1;
        }
        if (lineNumber > this.lineEnds.length + 1) {
            return -1;
        }
        if (lineNumber <= 0) {
            return -1;
        }
        if (lineNumber == 1) {
            return this.initialPosition;
        }
        return this.lineEnds[lineNumber - 2] + 1;
    }

    public final int getNextChar() {
        try {
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
            } else {
                this.unicodeAsBackSlash = false;
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
            }
            return this.currentCharacter;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            return -1;
        }
    }

    public final int getNextCharWithBoundChecks() {
        if (this.currentPosition >= this.eofPosition) {
            return -1;
        }
        this.currentCharacter = this.source[this.currentPosition++];
        if (this.currentPosition >= this.eofPosition) {
            this.unicodeAsBackSlash = false;
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return this.currentCharacter;
        }
        if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
            try {
                this.getNextUnicodeChar();
            }
            catch (InvalidInputException invalidInputException) {
                return -1;
            }
        } else {
            this.unicodeAsBackSlash = false;
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
        }
        return this.currentCharacter;
    }

    public final boolean getNextChar(char testedChar) {
        int temp;
        block8: {
            block7: {
                if (this.currentPosition >= this.eofPosition) {
                    this.unicodeAsBackSlash = false;
                    return false;
                }
                temp = this.currentPosition;
                this.currentCharacter = this.source[this.currentPosition++];
                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != 'u') break block7;
                this.getNextUnicodeChar();
                if (this.currentCharacter != testedChar) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            if (this.currentCharacter == testedChar) break block8;
            this.currentPosition = temp;
            return false;
        }
        try {
            this.unicodeAsBackSlash = false;
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return true;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            this.unicodeAsBackSlash = false;
            this.currentPosition = temp;
            return false;
        }
    }

    public final int getNextChar(char testedChar1, char testedChar2) {
        int result;
        int temp;
        block12: {
            if (this.currentPosition >= this.eofPosition) {
                return -1;
            }
            temp = this.currentPosition;
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                int result2;
                this.getNextUnicodeChar();
                if (this.currentCharacter == testedChar1) {
                    result2 = 0;
                } else if (this.currentCharacter == testedChar2) {
                    result2 = 1;
                } else {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    result2 = -1;
                }
                return result2;
            }
            if (this.currentCharacter == testedChar1) {
                result = 0;
                break block12;
            }
            if (this.currentCharacter == testedChar2) {
                result = 1;
                break block12;
            }
            this.currentPosition = temp;
            return -1;
        }
        try {
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return result;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            this.currentPosition = temp;
            return -1;
        }
    }

    private final void consumeDigits(int radix) throws InvalidInputException {
        this.consumeDigits(radix, false);
    }

    private final void consumeDigits(int radix, boolean expectingDigitFirst) throws InvalidInputException {
        switch (this.consumeDigits0(radix, 1, 2, expectingDigitFirst)) {
            case 1: {
                if (this.sourceLevel >= 0x330000L) break;
                throw new InvalidInputException(UNDERSCORES_IN_LITERALS_NOT_BELOW_17);
            }
            case 2: {
                if (this.sourceLevel < 0x330000L) {
                    throw new InvalidInputException(UNDERSCORES_IN_LITERALS_NOT_BELOW_17);
                }
                throw new InvalidInputException(INVALID_UNDERSCORE);
            }
        }
    }

    private final int consumeDigits0(int radix, int usingUnderscore, int invalidPosition, boolean expectingDigitFirst) throws InvalidInputException {
        int kind = 0;
        if (this.getNextChar('_')) {
            if (expectingDigitFirst) {
                return invalidPosition;
            }
            kind = usingUnderscore;
            while (this.getNextChar('_')) {
            }
        }
        if (this.getNextCharAsDigit(radix)) {
            while (this.getNextCharAsDigit(radix)) {
            }
            int kind2 = this.consumeDigits0(radix, usingUnderscore, invalidPosition, false);
            if (kind2 == 0) {
                return kind;
            }
            return kind2;
        }
        if (kind == usingUnderscore) {
            return invalidPosition;
        }
        return kind;
    }

    public final boolean getNextCharAsDigit() throws InvalidInputException {
        int temp;
        block8: {
            block7: {
                if (this.currentPosition >= this.eofPosition) {
                    return false;
                }
                temp = this.currentPosition;
                this.currentCharacter = this.source[this.currentPosition++];
                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != 'u') break block7;
                this.getNextUnicodeChar();
                if (!ScannerHelper.isDigit(this.currentCharacter)) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            if (ScannerHelper.isDigit(this.currentCharacter)) break block8;
            this.currentPosition = temp;
            return false;
        }
        try {
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return true;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            this.currentPosition = temp;
            return false;
        }
    }

    public final boolean getNextCharAsDigit(int radix) {
        int temp;
        block8: {
            block7: {
                if (this.currentPosition >= this.eofPosition) {
                    return false;
                }
                temp = this.currentPosition;
                this.currentCharacter = this.source[this.currentPosition++];
                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != 'u') break block7;
                this.getNextUnicodeChar();
                if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            if (ScannerHelper.digit(this.currentCharacter, radix) != -1) break block8;
            this.currentPosition = temp;
            return false;
        }
        try {
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return true;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            this.currentPosition = temp;
            return false;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean getNextCharAsJavaIdentifierPartWithBoundCheck() {
        int pos = this.currentPosition;
        if (pos >= this.eofPosition) {
            return false;
        }
        int temp2 = this.withoutUnicodePtr;
        try {
            boolean unicode = false;
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentPosition < this.eofPosition && this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                unicode = true;
            }
            char c = this.currentCharacter;
            boolean isJavaIdentifierPart = false;
            if (c >= '\ud800' && c <= '\udbff') {
                if (this.complianceLevel < 0x310000L) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                char low = (char)this.getNextCharWithBoundChecks();
                if (low < '\udc00' || low > '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c, low);
            } else {
                if (c >= '\udc00' && c <= '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c);
            }
            if (unicode) {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                return true;
            }
            if (!isJavaIdentifierPart) {
                this.currentPosition = pos;
                return false;
            }
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return true;
        }
        catch (InvalidInputException invalidInputException) {
            this.currentPosition = pos;
            this.withoutUnicodePtr = temp2;
            return false;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean getNextCharAsJavaIdentifierPart() {
        int pos = this.currentPosition;
        if (pos >= this.eofPosition) {
            return false;
        }
        int temp2 = this.withoutUnicodePtr;
        try {
            boolean unicode = false;
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                unicode = true;
            }
            char c = this.currentCharacter;
            boolean isJavaIdentifierPart = false;
            if (c >= '\ud800' && c <= '\udbff') {
                if (this.complianceLevel < 0x310000L) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                char low = (char)this.getNextChar();
                if (low < '\udc00' || low > '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c, low);
            } else {
                if (c >= '\udc00' && c <= '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c);
            }
            if (unicode) {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                return true;
            }
            if (!isJavaIdentifierPart) {
                this.currentPosition = pos;
                return false;
            }
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return true;
        }
        catch (IndexOutOfBoundsException | InvalidInputException exception) {
            this.currentPosition = pos;
            this.withoutUnicodePtr = temp2;
            return false;
        }
    }

    public int scanIdentifier() throws InvalidInputException {
        boolean isJavaIdStart;
        char c;
        int offset;
        int unicodePtr;
        boolean isWhiteSpace;
        int whiteStart = 0;
        this.withoutUnicodePtr = 0;
        whiteStart = this.currentPosition;
        boolean hasWhiteSpaces = false;
        boolean checkIfUnicode = false;
        do {
            unicodePtr = this.withoutUnicodePtr;
            offset = this.currentPosition;
            this.startPosition = this.currentPosition;
            if (this.currentPosition >= this.eofPosition) {
                if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                    --this.currentPosition;
                    this.startPosition = whiteStart;
                    return 1000;
                }
                return 64;
            }
            this.currentCharacter = this.source[this.currentPosition++];
            checkIfUnicode = this.currentPosition < this.eofPosition && this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u';
            if (checkIfUnicode) {
                isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                offset = this.currentPosition - offset;
            } else {
                offset = this.currentPosition - offset;
                switch (this.currentCharacter) {
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': {
                        isWhiteSpace = true;
                        break;
                    }
                    default: {
                        isWhiteSpace = false;
                    }
                }
            }
            if (!isWhiteSpace) continue;
            hasWhiteSpaces = true;
        } while (isWhiteSpace);
        if (hasWhiteSpaces) {
            if (this.tokenizeWhiteSpace) {
                this.currentPosition -= offset;
                this.startPosition = whiteStart;
                if (checkIfUnicode) {
                    this.withoutUnicodePtr = unicodePtr;
                }
                return 1000;
            }
            if (checkIfUnicode) {
                this.withoutUnicodePtr = 0;
                this.unicodeStore();
            } else {
                this.withoutUnicodePtr = 0;
            }
        }
        if ((c = this.currentCharacter) < '\u0080') {
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0) {
                return this.scanIdentifierOrKeywordWithBoundCheck();
            }
            return 135;
        }
        if (c >= '\ud800' && c <= '\udbff') {
            if (this.complianceLevel < 0x310000L) {
                throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
            }
            char low = (char)this.getNextCharWithBoundChecks();
            if (low < '\udc00' || low > '\udfff') {
                throw new InvalidInputException(INVALID_LOW_SURROGATE);
            }
            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
        } else {
            if (c >= '\udc00' && c <= '\udfff') {
                if (this.complianceLevel < 0x310000L) {
                    throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
                }
                throw new InvalidInputException(INVALID_HIGH_SURROGATE);
            }
            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
        }
        if (isJavaIdStart) {
            return this.scanIdentifierOrKeywordWithBoundCheck();
        }
        return 135;
    }

    public void ungetToken(int unambiguousToken) {
        if (this.nextToken != 0) {
            throw new ArrayIndexOutOfBoundsException("Single cell array overflow");
        }
        this.nextToken = unambiguousToken;
    }

    private void updateCase(int token) {
        if (token == 91) {
            this.caseStartPosition = this.startPosition;
            this.breakPreviewAllowed = true;
        }
    }

    public int getNextToken() throws InvalidInputException {
        if (this.nextToken != 0) {
            int token = this.nextToken;
            this.nextToken = 0;
            return token;
        }
        if (this.scanContext == null) {
            this.scanContext = this.isInModuleDeclaration() ? ScanContext.EXPECTING_KEYWORD : ScanContext.INACTIVE;
        }
        int token = this.getNextToken0();
        if (this.areRestrictedModuleKeywordsActive()) {
            if (Scanner.isRestrictedKeyword(token)) {
                token = this.disambiguatedRestrictedKeyword(token);
            }
            this.updateScanContext(token);
        }
        if (this.activeParser == null) {
            return token;
        }
        if (token == 23 || token == 11 || token == 36 || token == 104) {
            token = this.disambiguatedToken(token);
        } else if (token == 118) {
            this.consumingEllipsisAnnotations = false;
        }
        this.lookBack[0] = this.lookBack[1];
        this.lookBack[1] = token;
        this.updateCase(token);
        return token;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected int getNextToken0() throws InvalidInputException {
        this.wasAcr = false;
        if (this.diet) {
            this.jumpOverMethodBody();
            this.diet = false;
            if (this.currentPosition <= this.eofPosition) return 33;
            return 64;
        }
        whiteStart = 0;
        try {
            block47: while (true) lbl-1000:
            // 4 sources

            {
                this.withoutUnicodePtr = 0;
                whiteStart = ++this.currentPosition;
                hasWhiteSpaces = false;
                checkIfUnicode = false;
                do {
                    block135: {
                        unicodePtr = this.withoutUnicodePtr;
                        offset = this.currentPosition;
                        this.startPosition = this.currentPosition;
                        try {
                            this.currentCharacter = this.source[this.currentPosition++];
                            checkIfUnicode = this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u';
                        }
                        catch (IndexOutOfBoundsException v0) {
                            if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                                --this.currentPosition;
                                this.startPosition = whiteStart;
                                return 1000;
                            }
                            if (this.currentPosition <= this.eofPosition) break block135;
                            return 64;
                        }
                    }
                    if (this.currentPosition > this.eofPosition) {
                        if (this.tokenizeWhiteSpace == false) return 64;
                        if (whiteStart == this.currentPosition - 1) return 64;
                        --this.currentPosition;
                        this.startPosition = whiteStart;
                        return 1000;
                    }
                    if (checkIfUnicode) {
                        isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                        offset = this.currentPosition - offset;
                    } else {
                        offset = this.currentPosition - offset;
                        if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                            this.pushLineSeparator();
                        }
                        switch (this.currentCharacter) {
                            case '\t': 
                            case '\n': 
                            case '\f': 
                            case '\r': 
                            case ' ': {
                                isWhiteSpace = true;
                                break;
                            }
                            default: {
                                isWhiteSpace = false;
                            }
                        }
                    }
                    if (!isWhiteSpace) continue;
                    hasWhiteSpaces = true;
                } while (isWhiteSpace);
                if (hasWhiteSpaces) {
                    if (this.tokenizeWhiteSpace) {
                        this.currentPosition -= offset;
                        this.startPosition = whiteStart;
                        if (checkIfUnicode == false) return 1000;
                        this.withoutUnicodePtr = unicodePtr;
                        return 1000;
                    }
                    if (checkIfUnicode) {
                        this.withoutUnicodePtr = 0;
                        this.unicodeStore();
                    } else {
                        this.withoutUnicodePtr = 0;
                    }
                }
                switch (this.currentCharacter) {
                    case '@': {
                        return 36;
                    }
                    case '(': {
                        return 23;
                    }
                    case ')': {
                        return 26;
                    }
                    case '{': {
                        return 38;
                    }
                    case '}': {
                        return 33;
                    }
                    case '[': {
                        return 6;
                    }
                    case ']': {
                        return 69;
                    }
                    case ';': {
                        return 25;
                    }
                    case ',': {
                        return 32;
                    }
                    case '.': {
                        if (this.getNextCharAsDigit()) {
                            return this.scanNumber(true);
                        }
                        temp = this.currentPosition;
                        if (!this.getNextChar('.')) {
                            this.currentPosition = temp;
                            return 1;
                        }
                        if (this.getNextChar('.')) {
                            return 118;
                        }
                        this.currentPosition = temp;
                        return 1;
                    }
                    case '+': {
                        test = this.getNextChar('+', '=');
                        if (test == 0) {
                            return 2;
                        }
                        if (test <= 0) return 4;
                        return 93;
                    }
                    case '-': {
                        test = this.getNextChar('-', '=');
                        if (test == 0) {
                            return 3;
                        }
                        if (test > 0) {
                            return 94;
                        }
                        if (this.getNextChar('>') == false) return 5;
                        return 104;
                    }
                    case '~': {
                        return 67;
                    }
                    case '!': {
                        if (this.getNextChar('=') == false) return 66;
                        return 20;
                    }
                    case '*': {
                        if (this.getNextChar('=') == false) return 8;
                        return 95;
                    }
                    case '%': {
                        if (this.getNextChar('=') == false) return 9;
                        return 100;
                    }
                    case '<': {
                        test = this.getNextChar('=', '<');
                        if (test == 0) {
                            return 12;
                        }
                        if (test <= 0) return 11;
                        if (this.getNextChar('=') == false) return 18;
                        return 101;
                    }
                    case '>': {
                        if (this.returnOnlyGreater) {
                            return 15;
                        }
                        test = this.getNextChar('=', '>');
                        if (test == 0) {
                            return 13;
                        }
                        if (test <= 0) return 15;
                        test = this.getNextChar('=', '>');
                        if (test == 0) {
                            return 102;
                        }
                        if (test <= 0) return 14;
                        if (this.getNextChar('=') == false) return 16;
                        return 103;
                    }
                    case '=': {
                        if (this.getNextChar('=') == false) return 77;
                        return 19;
                    }
                    case '&': {
                        test = this.getNextChar('&', '=');
                        if (test == 0) {
                            return 30;
                        }
                        if (test <= 0) return 21;
                        return 97;
                    }
                    case '|': {
                        test = this.getNextChar('|', '=');
                        if (test == 0) {
                            return 31;
                        }
                        if (test <= 0) return 28;
                        return 98;
                    }
                    case '^': {
                        if (this.getNextChar('=') == false) return 24;
                        return 99;
                    }
                    case '?': {
                        return 29;
                    }
                    case ':': {
                        if (this.getNextChar(':')) {
                            return 7;
                        }
                        ++this.yieldColons;
                        return 65;
                    }
                    case '\'': {
                        test = this.getNextChar('\n', '\r');
                        if (test == 0) {
                            throw new InvalidInputException("Invalid_Character_Constant");
                        }
                        if (test <= 0) ** GOTO lbl171
                        lookAhead = 0;
                        if (true) ** GOTO lbl387
lbl171:
                        // 1 sources

                        if (!this.getNextChar('\'')) ** GOTO lbl174
                        lookAhead = 0;
                        if (true) ** GOTO lbl398
lbl174:
                        // 1 sources

                        if (!this.getNextChar('\\')) ** GOTO lbl186
                        if (!this.unicodeAsBackSlash) ** GOTO lbl183
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        } else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                        ** GOTO lbl184
lbl183:
                        // 1 sources

                        this.currentCharacter = this.source[this.currentPosition++];
lbl184:
                        // 3 sources

                        this.scanEscapeCharacter();
                        ** GOTO lbl200
lbl186:
                        // 1 sources

                        this.unicodeAsBackSlash = false;
                        checkIfUnicode = false;
                        try {
                            this.currentCharacter = this.source[this.currentPosition++];
                            checkIfUnicode = this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u';
                        }
                        catch (IndexOutOfBoundsException v1) {
                            --this.currentPosition;
                            throw new InvalidInputException("Invalid_Character_Constant");
                        }
                        if (checkIfUnicode) {
                            this.getNextUnicodeChar();
                        } else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
lbl200:
                        // 5 sources

                        if (this.getNextChar('\'')) {
                            return 59;
                        }
                        lookAhead = 0;
                        while (true) {
                            if (lookAhead >= 20) {
                                throw new InvalidInputException("Invalid_Character_Constant");
                            }
                            if (this.currentPosition + lookAhead == this.eofPosition) {
                                throw new InvalidInputException("Invalid_Character_Constant");
                            }
                            if (this.source[this.currentPosition + lookAhead] == '\n') {
                                throw new InvalidInputException("Invalid_Character_Constant");
                            }
                            if (this.source[this.currentPosition + lookAhead] == '\'') {
                                this.currentPosition += lookAhead + 1;
                                throw new InvalidInputException("Invalid_Character_Constant");
                            }
                            ++lookAhead;
                        }
                    }
                    case '\"': {
                        return this.scanForStringLiteral();
                    }
                    case '/': {
                        if (this.skipComments) ** GOTO lbl343
                        test = this.getNextChar('/', '*');
                        if (test != 0) ** GOTO lbl271
                        this.lastCommentLinePosition = this.currentPosition--;
                        try {
                            this.currentCharacter = this.source[this.currentPosition++];
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                            }
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                ++this.currentPosition;
                            }
                            isUnicode = false;
                            while (true) {
                                if (this.currentCharacter != '\r' && this.currentCharacter != '\n') ** GOTO lbl236
                                if (this.currentCharacter == '\r' && this.eofPosition > this.currentPosition) {
                                    if (this.source[this.currentPosition] != '\n') break;
                                    ++this.currentPosition;
                                    this.currentCharacter = (char)10;
                                }
                                ** GOTO lbl250
lbl236:
                                // 1 sources

                                if (this.currentPosition >= this.eofPosition) {
                                    this.lastCommentLinePosition = this.currentPosition++;
                                    throw new IndexOutOfBoundsException();
                                }
                                this.lastCommentLinePosition = this.currentPosition;
                                isUnicode = false;
                                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode = true;
                                }
                                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') continue;
                                ++this.currentPosition;
                            }
                            if (this.source[this.currentPosition] == '\\' && this.source[this.currentPosition + 1] == 'u') {
                                this.getNextUnicodeChar();
                                isUnicode = true;
                            }
lbl250:
                            // 4 sources

                            this.recordComment(1001);
                            if (this.taskTags != null) {
                                this.checkTaskTag(this.startPosition, this.currentPosition);
                            }
                            if (this.currentCharacter == '\r' || this.currentCharacter == '\n') {
                                if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                    this.parseTags();
                                }
                                if (this.recordLineSeparator) {
                                    if (isUnicode) {
                                        this.pushUnicodeLineSeparator();
                                    } else {
                                        this.pushLineSeparator();
                                    }
                                }
                            }
                            if (!this.tokenizeComments) ** GOTO lbl-1000
                            return 1001;
                        }
                        catch (IndexOutOfBoundsException v2) {
                            this.recordComment(1001);
                            if (this.taskTags != null) {
                                this.checkTaskTag(this.startPosition, this.currentPosition);
                            }
                            if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                this.parseTags();
                            }
                            if (!this.tokenizeComments) ** GOTO lbl-1000
                            return 1001;
                        }
lbl271:
                        // 1 sources

                        if (test <= 0) ** GOTO lbl343
                        try {
                            isJavadoc = false;
                            star = false;
                            isUnicode = false;
                            this.unicodeAsBackSlash = false;
                            this.currentCharacter = this.source[this.currentPosition++];
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                                isUnicode = true;
                            } else {
                                isUnicode = false;
                                if (this.withoutUnicodePtr != 0) {
                                    this.unicodeStore();
                                }
                            }
                            if (this.currentCharacter == '*') {
                                isJavadoc = true;
                                star = true;
                            }
                            if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                if (isUnicode) {
                                    this.pushUnicodeLineSeparator();
                                } else {
                                    this.pushLineSeparator();
                                }
                            }
                            isUnicode = false;
                            previous = this.currentPosition;
                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                                isUnicode = true;
                            } else {
                                isUnicode = false;
                            }
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                ++this.currentPosition;
                            }
                            if (this.currentCharacter == '/') {
                                isJavadoc = false;
                            }
                            firstTag = 0;
                            while (true) {
                                if (this.currentCharacter != '/' || !star) ** GOTO lbl307
lbl307:
                                // 1 sources

                                if (this.currentPosition >= this.eofPosition) {
                                    throw new InvalidInputException("Unterminated_Comment");
                                }
                                if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                    if (isUnicode) {
                                        this.pushUnicodeLineSeparator();
                                    } else {
                                        this.pushLineSeparator();
                                    }
                                }
                                switch (this.currentCharacter) {
                                    case '*': {
                                        star = true;
                                        break;
                                    }
                                    case '@': {
                                        if (firstTag == 0 && this.isFirstTag()) {
                                            firstTag = previous;
                                        }
                                    }
                                    default: {
                                        star = false;
                                    }
                                }
                                previous = this.currentPosition;
                                this.currentCharacter = this.source[this.currentPosition++];
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode = true;
                                } else {
                                    isUnicode = false;
                                }
                                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') continue;
                                ++this.currentPosition;
                            }
                            token = isJavadoc != false ? 1003 : 1002;
                            this.recordComment(token);
                            this.commentTagStarts[this.commentPtr] = firstTag;
                            if (this.taskTags == null) continue block47;
                            this.checkTaskTag(this.startPosition, this.currentPosition);
                            if (!this.tokenizeComments) continue block47;
                            return token;
                        }
                        catch (IndexOutOfBoundsException v3) {
                            --this.currentPosition;
                            throw new InvalidInputException("Unterminated_Comment");
                        }
lbl343:
                        // 2 sources

                        if (this.getNextChar('=') == false) return 10;
                        return 96;
                    }
                    case '\u001a': {
                        if (this.atEnd() == false) throw new InvalidInputException("Ctrl-Z");
                        return 64;
                    }
                    default: {
                        c = this.currentCharacter;
                        if (c < '\u0080') {
                            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 64) != 0) {
                                return this.scanIdentifierOrKeyword();
                            }
                            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 4) == 0) return 135;
                            return this.scanNumber(false);
                        }
                        if (c >= '\ud800' && c <= '\udbff') {
                            if (this.complianceLevel < 0x310000L) {
                                throw new InvalidInputException("Invalid_Unicode_Escape");
                            }
                            low = (char)this.getNextChar();
                            if (low < '\udc00') throw new InvalidInputException("Invalid_Low_Surrogate");
                            if (low > '\udfff') {
                                throw new InvalidInputException("Invalid_Low_Surrogate");
                            }
                            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
                        } else {
                            if (c >= '\udc00' && c <= '\udfff') {
                                if (this.complianceLevel >= 0x310000L) throw new InvalidInputException("Invalid_High_Surrogate");
                                throw new InvalidInputException("Invalid_Unicode_Escape");
                            }
                            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
                        }
                        if (isJavaIdStart) {
                            return this.scanIdentifierOrKeyword();
                        }
                        if (ScannerHelper.isDigit(this.currentCharacter) == false) return 135;
                        return this.scanNumber(false);
                    }
                }
                break;
            }
        }
        catch (IndexOutOfBoundsException v4) {
            if (this.tokenizeWhiteSpace == false) return 64;
            if (whiteStart == this.currentPosition - 1) return 64;
            --this.currentPosition;
            this.startPosition = whiteStart;
            return 1000;
        }
        do {
            if (this.currentPosition + lookAhead == this.eofPosition) {
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            if (this.source[this.currentPosition + lookAhead] == '\n') {
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            if (this.source[this.currentPosition + lookAhead] == '\'') {
                this.currentPosition += lookAhead + 1;
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            ++lookAhead;
lbl387:
            // 2 sources

        } while (lookAhead < 3);
        throw new InvalidInputException("Invalid_Character_Constant");
        do {
            if (this.currentPosition + lookAhead == this.eofPosition) {
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            if (this.source[this.currentPosition + lookAhead] == '\n') {
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            if (this.source[this.currentPosition + lookAhead] == '\'') {
                this.currentPosition += lookAhead + 1;
                throw new InvalidInputException("Invalid_Character_Constant");
            }
            ++lookAhead;
lbl398:
            // 2 sources

        } while (lookAhead < 3);
        throw new InvalidInputException("Invalid_Character_Constant");
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private int scanForStringLiteral() throws InvalidInputException {
        block49: {
            isTextBlock = false;
            lastQuotePos = 0;
            this.unicodeAsBackSlash = false;
            isUnicode = false;
            isTextBlock = this.scanForTextBlockBeginning();
            if (!isTextBlock) break block49;
            try {
                this.rawStart = this.currentPosition - this.startPosition;
                while (true) {
                    block50: {
                        if (this.currentPosition > this.eofPosition) {
                            if (lastQuotePos <= 0) break;
                            this.currentPosition = lastQuotePos;
                        }
                        if (this.currentCharacter == '\"') {
                            lastQuotePos = this.currentPosition;
                            if (this.scanForTextBlockClose()) {
                                this.currentPosition += 2;
                                return 61;
                            }
                            if (this.withoutUnicodePtr != 0) {
                                this.unicodeStore();
                            }
                        } else if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                            this.pushLineSeparator();
                        }
                        if (this.currentCharacter != '\\') break block50;
                        switch (this.source[this.currentPosition]) {
                            case 'f': 
                            case 'n': 
                            case 'r': {
                                break block50;
                            }
                            case '\n': 
                            case '\r': {
                                this.currentCharacter = (char)92;
                                ++this.currentPosition;
                                break;
                            }
                            case '\\': {
                                ++this.currentPosition;
                                break;
                            }
                            default: {
                                if (!this.unicodeAsBackSlash) ** GOTO lbl46
                                --this.withoutUnicodePtr;
                                if (this.currentPosition >= this.eofPosition) break;
                                this.unicodeAsBackSlash = false;
                                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode = true;
                                    --this.withoutUnicodePtr;
                                } else {
                                    isUnicode = false;
                                }
                                ** GOTO lbl50
lbl46:
                                // 1 sources

                                if (this.withoutUnicodePtr == 0) {
                                    this.unicodeInitializeBuffer(this.currentPosition - this.startPosition);
                                }
                                --this.withoutUnicodePtr;
                                this.currentCharacter = this.source[this.currentPosition++];
lbl50:
                                // 3 sources

                                oldPos = this.currentPosition - 1;
                                this.scanEscapeCharacter();
                                if (!ScannerHelper.isWhitespace(this.currentCharacter)) break;
                                if (this.withoutUnicodePtr == 0) {
                                    this.unicodeInitializeBuffer(this.currentPosition - this.startPosition);
                                }
                                this.unicodeStore('\\');
                                this.currentPosition = oldPos;
                                this.currentCharacter = this.source[this.currentPosition];
                                break block50;
                            }
                        }
                        if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                    }
                    this.unicodeAsBackSlash = false;
                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                        this.getNextUnicodeChar();
                        isUnicode = true;
                        continue;
                    }
                    isUnicode = false;
                    if (this.currentCharacter == '\"' || this.withoutUnicodePtr == 0) continue;
                    this.unicodeStore();
                }
                this.currentPosition = lastQuotePos > 0 ? lastQuotePos : this.startPosition + this.rawStart;
                throw new InvalidInputException("Unterminated_Text_Block");
            }
            catch (IndexOutOfBoundsException v0) {
                this.currentPosition = lastQuotePos > 0 ? lastQuotePos : this.startPosition + this.rawStart;
                throw new InvalidInputException("Unterminated_Text_Block");
            }
        }
        try {
            this.unicodeAsBackSlash = false;
            isUnicode = false;
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                isUnicode = true;
            } else if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            while (true) {
                block51: {
                    if (this.currentCharacter == '\"') {
                        return 60;
                    }
                    if (this.currentPosition >= this.eofPosition) {
                        throw new InvalidInputException("Unterminated_String");
                    }
                    if (this.currentCharacter != '\n' && this.currentCharacter != '\r') break block51;
                    if (!isUnicode) {
                        --this.currentPosition;
                        throw new InvalidInputException("Invalid_Char_In_String");
                    }
                    start = this.currentPosition;
                    lookAhead = 0;
                    if (true) ** GOTO lbl151
                }
                if (this.currentCharacter == '\\') {
                    if (this.unicodeAsBackSlash) {
                        --this.withoutUnicodePtr;
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                            isUnicode = true;
                            --this.withoutUnicodePtr;
                        } else {
                            isUnicode = false;
                        }
                    } else {
                        if (this.withoutUnicodePtr == 0) {
                            this.unicodeInitializeBuffer(this.currentPosition - this.startPosition);
                        }
                        --this.withoutUnicodePtr;
                        this.currentCharacter = this.source[this.currentPosition++];
                    }
                    this.scanEscapeCharacter();
                    if (this.withoutUnicodePtr != 0) {
                        this.unicodeStore();
                    }
                }
                this.unicodeAsBackSlash = false;
                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                    this.getNextUnicodeChar();
                    isUnicode = true;
                    continue;
                }
                isUnicode = false;
                if (this.withoutUnicodePtr == 0) continue;
                this.unicodeStore();
            }
        }
        catch (IndexOutOfBoundsException v1) {
            --this.currentPosition;
            throw new InvalidInputException("Unterminated_String");
        }
        catch (InvalidInputException e) {
            if (e.getMessage().equals("Invalid_Escape") == false) throw e;
            lookAhead = 0;
            if (true) ** GOTO lbl162
        }
        do {
            if (this.currentPosition >= this.eofPosition) {
                this.currentPosition = start;
                throw new InvalidInputException("Invalid_Char_In_String");
            }
            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                isUnicode = true;
                this.getNextUnicodeChar();
            } else {
                isUnicode = false;
            }
            if (!isUnicode && this.currentCharacter == '\n') {
                --this.currentPosition;
                throw new InvalidInputException("Invalid_Char_In_String");
            }
            if (this.currentCharacter == '\"') {
                throw new InvalidInputException("Invalid_Char_In_String");
            }
            ++lookAhead;
lbl151:
            // 2 sources

        } while (lookAhead < 50);
        throw new InvalidInputException("Invalid_Char_In_String");
        do {
            if (this.currentPosition + lookAhead == this.eofPosition) {
                throw e;
            }
            if (this.source[this.currentPosition + lookAhead] == '\n') {
                throw e;
            }
            if (this.source[this.currentPosition + lookAhead] == '\"') {
                this.currentPosition += lookAhead + 1;
                throw e;
            }
            ++lookAhead;
lbl162:
            // 2 sources

        } while (lookAhead < 50);
        throw e;
    }

    public void getNextUnicodeChar() throws InvalidInputException {
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;
        int unicodeSize = 6;
        ++this.currentPosition;
        if (this.currentPosition < this.eofPosition) {
            while (this.source[this.currentPosition] == 'u') {
                ++this.currentPosition;
                if (this.currentPosition >= this.eofPosition) {
                    --this.currentPosition;
                    throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
                }
                ++unicodeSize;
            }
        } else {
            --this.currentPosition;
            throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
        }
        if (this.currentPosition + 4 > this.eofPosition) {
            this.currentPosition += this.eofPosition - this.currentPosition;
            throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
        }
        if ((c1 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c1 < 0 || (c2 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c2 < 0 || (c3 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c3 < 0 || (c4 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c4 < 0) {
            throw new InvalidInputException(INVALID_UNICODE_ESCAPE);
        }
        this.currentCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
        if (this.withoutUnicodePtr == 0) {
            this.unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
        }
        this.unicodeStore();
        this.unicodeAsBackSlash = this.currentCharacter == '\\';
    }

    public NLSTag[] getNLSTags() {
        int length = this.nlsTagsPtr;
        if (length != 0) {
            NLSTag[] result = new NLSTag[length];
            System.arraycopy(this.nlsTags, 0, result, 0, length);
            this.nlsTagsPtr = 0;
            return result;
        }
        return null;
    }

    public boolean[] getIdentityComparisonLines() {
        boolean[] retVal = this.validIdentityComparisonLines;
        this.validIdentityComparisonLines = null;
        return retVal;
    }

    public char[] getSource() {
        return this.source;
    }

    protected boolean isFirstTag() {
        return true;
    }

    /*
     * Unable to fully structure code
     */
    public final void jumpOverMethodBody() {
        this.wasAcr = false;
        found = 1;
        block37: while (true) {
            block38: while (true) {
                this.withoutUnicodePtr = 0;
                do {
                    this.startPosition = ++this.currentPosition;
                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                        isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                        continue;
                    }
                    if (this.recordLineSeparator && (this.currentCharacter == '\r' || this.currentCharacter == '\n')) {
                        this.pushLineSeparator();
                    }
                    isWhiteSpace = CharOperation.isWhitespace(this.currentCharacter);
                } while (isWhiteSpace);
                switch (this.currentCharacter) {
                    case '{': {
                        ++found;
                        continue block38;
                    }
                    case '}': {
                        if (--found != 0) continue block38;
                        return;
                    }
                    case '\'': {
                        test = this.getNextChar('\\');
                        if (test) {
                            try {
                                if (this.unicodeAsBackSlash) {
                                    this.unicodeAsBackSlash = false;
                                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                    } else if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                } else {
                                    this.currentCharacter = this.source[this.currentPosition++];
                                }
                                this.scanEscapeCharacter();
                            }
                            catch (InvalidInputException v0) {}
                        } else {
                            try {
                                this.unicodeAsBackSlash = false;
                                this.currentCharacter = this.source[this.currentPosition++];
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                } else if (this.withoutUnicodePtr != 0) {
                                    this.unicodeStore();
                                }
                            }
                            catch (InvalidInputException v1) {}
                        }
                        this.getNextChar('\'');
                        continue block38;
                    }
                    case '\"': {
                        isTextBlock = false;
                        firstClosingBrace = 0;
                        try {
                            try {
                                isTextBlock = this.scanForTextBlockBeginning();
                                if (!isTextBlock) {
                                    this.unicodeAsBackSlash = false;
                                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                    } else if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                }
                            }
                            catch (InvalidInputException v2) {}
                            while (true) {
                                if (this.currentPosition > this.eofPosition) continue block37;
                                if (!isTextBlock) ** GOTO lbl95
                                switch (this.currentCharacter) {
                                    case '\"': {
                                        if (this.scanForTextBlockClose()) {
                                            this.currentPosition += 2;
                                            this.currentCharacter = this.source[this.currentPosition];
                                            isTextBlock = false;
                                            continue block38;
                                        }
                                        ** GOTO lbl96
                                    }
                                    case '}': {
                                        if (firstClosingBrace == 0) {
                                            firstClosingBrace = this.currentPosition;
                                        }
                                        ** GOTO lbl96
                                    }
                                    case '\r': {
                                        if (this.source[this.currentPosition] == '\n') {
                                            ++this.currentPosition;
                                        }
                                    }
                                    case '\n': {
                                        this.pushLineSeparator();
                                    }
                                    default: {
                                        if (this.currentCharacter == '\\' && this.source[this.currentPosition++] == '\"') {
                                            ++this.currentPosition;
                                        }
                                        this.currentCharacter = this.source[this.currentPosition++];
                                        break;
                                    }
                                }
                                continue;
lbl95:
                                // 1 sources

                                if (this.currentCharacter == '\"') continue block38;
lbl96:
                                // 3 sources

                                if (this.currentCharacter == '\r') {
                                    if (this.source[this.currentPosition] != '\n') continue block38;
                                    continue block38;
                                }
                                if (this.currentCharacter == '\n') continue block38;
                                if (this.currentCharacter == '\\') {
                                    try {
                                        if (this.unicodeAsBackSlash) {
                                            this.unicodeAsBackSlash = false;
                                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                                this.getNextUnicodeChar();
                                            } else if (this.withoutUnicodePtr != 0) {
                                                this.unicodeStore();
                                            }
                                        } else {
                                            this.currentCharacter = this.source[this.currentPosition++];
                                        }
                                        this.scanEscapeCharacter();
                                    }
                                    catch (InvalidInputException v3) {}
                                }
                                try {
                                    this.unicodeAsBackSlash = false;
                                    this.currentCharacter = this.source[this.currentPosition++];
                                    if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                        continue;
                                    }
                                    if (this.withoutUnicodePtr == 0) continue;
                                    this.unicodeStore();
                                }
                                catch (InvalidInputException v4) {}
                            }
                        }
                        catch (IndexOutOfBoundsException v5) {
                            if (!isTextBlock || firstClosingBrace <= 0) continue block38;
                            this.currentPosition = firstClosingBrace - 1;
                        }
                        continue block38;
                    }
                    case '/': {
                        test = this.getNextChar('/', '*');
                        if (test == 0) {
                            try {
                                this.lastCommentLinePosition = this.currentPosition;
                                this.currentCharacter = this.source[this.currentPosition++];
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                }
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                    ++this.currentPosition;
                                }
                                isUnicode = false;
                                while (this.currentCharacter != '\r' && this.currentCharacter != '\n') {
                                    if (this.currentPosition >= this.eofPosition) {
                                        this.lastCommentLinePosition = this.currentPosition++;
                                        throw new IndexOutOfBoundsException();
                                    }
                                    this.lastCommentLinePosition = this.currentPosition;
                                    isUnicode = false;
                                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                        isUnicode = true;
                                        this.getNextUnicodeChar();
                                    }
                                    if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') continue;
                                    ++this.currentPosition;
                                }
                                if (this.currentCharacter == '\r' && this.eofPosition > this.currentPosition) {
                                    if (this.source[this.currentPosition] == '\n') {
                                        ++this.currentPosition;
                                        this.currentCharacter = (char)10;
                                    } else if (this.source[this.currentPosition] == '\\' && this.source[this.currentPosition + 1] == 'u') {
                                        isUnicode = true;
                                        this.getNextUnicodeChar();
                                    }
                                }
                                this.recordComment(1001);
                                if (!this.recordLineSeparator || this.currentCharacter != '\r' && this.currentCharacter != '\n') continue block38;
                                if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                    this.parseTags();
                                }
                                if (!this.recordLineSeparator) continue block38;
                                if (isUnicode) {
                                    this.pushUnicodeLineSeparator();
                                    continue block38;
                                }
                                this.pushLineSeparator();
                            }
                            catch (IndexOutOfBoundsException v6) {
                                --this.currentPosition;
                                this.recordComment(1001);
                                if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                    this.parseTags();
                                }
                                if (this.tokenizeComments) continue block38;
                                ++this.currentPosition;
                            }
                            continue block38;
                        }
                        if (test <= 0) continue block38;
                        isJavadoc = false;
                        try {
                            star = false;
                            isUnicode = false;
                            this.unicodeAsBackSlash = false;
                            this.currentCharacter = this.source[this.currentPosition++];
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                                isUnicode = true;
                            } else {
                                isUnicode = false;
                                if (this.withoutUnicodePtr != 0) {
                                    this.unicodeStore();
                                }
                            }
                            if (this.currentCharacter == '*') {
                                isJavadoc = true;
                                star = true;
                            }
                            if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                if (isUnicode) {
                                    this.pushUnicodeLineSeparator();
                                } else {
                                    this.pushLineSeparator();
                                }
                            }
                            isUnicode = false;
                            previous = this.currentPosition;
                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                                isUnicode = true;
                            } else {
                                isUnicode = false;
                            }
                            if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                ++this.currentPosition;
                            }
                            if (this.currentCharacter == '/') {
                                isJavadoc = false;
                            }
                            firstTag = 0;
                            while (this.currentCharacter != '/' || !star) {
                                if (this.currentPosition >= this.eofPosition) {
                                    return;
                                }
                                if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                    if (isUnicode) {
                                        this.pushUnicodeLineSeparator();
                                    } else {
                                        this.pushLineSeparator();
                                    }
                                }
                                switch (this.currentCharacter) {
                                    case '*': {
                                        star = true;
                                        break;
                                    }
                                    case '@': {
                                        if (firstTag == 0 && this.isFirstTag()) {
                                            firstTag = previous;
                                        }
                                    }
                                    default: {
                                        star = false;
                                    }
                                }
                                previous = this.currentPosition;
                                this.currentCharacter = this.source[this.currentPosition++];
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode = true;
                                } else {
                                    isUnicode = false;
                                }
                                if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') continue;
                                ++this.currentPosition;
                            }
                            this.recordComment(isJavadoc != false ? 1003 : 1002);
                            this.commentTagStarts[this.commentPtr] = firstTag;
                            continue block38;
                        }
                        catch (IndexOutOfBoundsException v7) {
                            return;
                        }
                    }
                }
                try {
                    c = this.currentCharacter;
                    if (c < '\u0080') {
                        if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 64) != 0) {
                            this.scanIdentifierOrKeyword();
                            continue;
                        }
                        if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 4) == 0) continue;
                        this.scanNumber(false);
                        continue;
                    }
                    if (c >= '\ud800' && c <= '\udbff') {
                        if (this.complianceLevel < 0x310000L) {
                            throw new InvalidInputException("Invalid_Unicode_Escape");
                        }
                        low = (char)this.getNextChar();
                        if (low < '\udc00' || low > '\udfff') continue;
                        isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
                    } else {
                        if (c >= '\udc00' && c <= '\udfff') continue;
                        isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
                    }
                    if (!isJavaIdStart) continue;
                    this.scanIdentifierOrKeyword();
                }
                catch (InvalidInputException v8) {}
            }
            break;
        }
        catch (IndexOutOfBoundsException | InvalidInputException v9) {
            return;
        }
    }

    public final boolean jumpOverUnicodeWhiteSpace() throws InvalidInputException {
        this.wasAcr = false;
        this.getNextUnicodeChar();
        return CharOperation.isWhitespace(this.currentCharacter);
    }

    final char[] optimizedCurrentTokenSource1() {
        char charOne = this.source[this.startPosition];
        switch (charOne) {
            case 'a': {
                return charArray_a;
            }
            case 'b': {
                return charArray_b;
            }
            case 'c': {
                return charArray_c;
            }
            case 'd': {
                return charArray_d;
            }
            case 'e': {
                return charArray_e;
            }
            case 'f': {
                return charArray_f;
            }
            case 'g': {
                return charArray_g;
            }
            case 'h': {
                return charArray_h;
            }
            case 'i': {
                return charArray_i;
            }
            case 'j': {
                return charArray_j;
            }
            case 'k': {
                return charArray_k;
            }
            case 'l': {
                return charArray_l;
            }
            case 'm': {
                return charArray_m;
            }
            case 'n': {
                return charArray_n;
            }
            case 'o': {
                return charArray_o;
            }
            case 'p': {
                return charArray_p;
            }
            case 'q': {
                return charArray_q;
            }
            case 'r': {
                return charArray_r;
            }
            case 's': {
                return charArray_s;
            }
            case 't': {
                return charArray_t;
            }
            case 'u': {
                return charArray_u;
            }
            case 'v': {
                return charArray_v;
            }
            case 'w': {
                return charArray_w;
            }
            case 'x': {
                return charArray_x;
            }
            case 'y': {
                return charArray_y;
            }
            case 'z': {
                return charArray_z;
            }
        }
        return new char[]{charOne};
    }

    final char[] optimizedCurrentTokenSource2() {
        char[] src = this.source;
        int start = this.startPosition;
        char c0 = src[start];
        char c1 = src[start + 1];
        int hash = ((c0 << 6) + c1) % 30;
        char[][] table = this.charArray_length[0][hash];
        int i = this.newEntry2;
        while (++i < 6) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1]) continue;
            return charArray;
        }
        i = -1;
        int max = this.newEntry2;
        while (++i <= max) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1]) continue;
            return charArray;
        }
        if (++max >= 6) {
            max = 0;
        }
        char[] r = new char[2];
        System.arraycopy(src, start, r, 0, 2);
        this.newEntry2 = max;
        table[this.newEntry2] = r;
        return r;
    }

    final char[] optimizedCurrentTokenSource3() {
        char[] src = this.source;
        int start = this.startPosition;
        char c1 = src[start + 1];
        char c0 = src[start];
        char c2 = src[start + 2];
        int hash = ((c0 << 6) + c2) % 30;
        char[][] table = this.charArray_length[1][hash];
        int i = this.newEntry3;
        while (++i < 6) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2]) continue;
            return charArray;
        }
        i = -1;
        int max = this.newEntry3;
        while (++i <= max) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2]) continue;
            return charArray;
        }
        if (++max >= 6) {
            max = 0;
        }
        char[] r = new char[3];
        System.arraycopy(src, start, r, 0, 3);
        this.newEntry3 = max;
        table[this.newEntry3] = r;
        return r;
    }

    final char[] optimizedCurrentTokenSource4() {
        char[] src = this.source;
        int start = this.startPosition;
        char c1 = src[start + 1];
        char c3 = src[start + 3];
        char c0 = src[start];
        char c2 = src[start + 2];
        int hash = ((c0 << 6) + c2) % 30;
        char[][] table = this.charArray_length[2][hash];
        int i = this.newEntry4;
        while (++i < 6) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3]) continue;
            return charArray;
        }
        i = -1;
        int max = this.newEntry4;
        while (++i <= max) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3]) continue;
            return charArray;
        }
        if (++max >= 6) {
            max = 0;
        }
        char[] r = new char[4];
        System.arraycopy(src, start, r, 0, 4);
        this.newEntry4 = max;
        table[this.newEntry4] = r;
        return r;
    }

    final char[] optimizedCurrentTokenSource5() {
        char[] src = this.source;
        int start = this.startPosition;
        char c1 = src[start + 1];
        char c3 = src[start + 3];
        char c0 = src[start];
        char c2 = src[start + 2];
        char c4 = src[start + 4];
        int hash = ((c0 << 12) + (c2 << 6) + c4) % 30;
        char[][] table = this.charArray_length[3][hash];
        int i = this.newEntry5;
        while (++i < 6) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3] || c4 != charArray[4]) continue;
            return charArray;
        }
        i = -1;
        int max = this.newEntry5;
        while (++i <= max) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3] || c4 != charArray[4]) continue;
            return charArray;
        }
        if (++max >= 6) {
            max = 0;
        }
        char[] r = new char[5];
        System.arraycopy(src, start, r, 0, 5);
        this.newEntry5 = max;
        table[this.newEntry5] = r;
        return r;
    }

    final char[] optimizedCurrentTokenSource6() {
        char[] src = this.source;
        int start = this.startPosition;
        char c1 = src[start + 1];
        char c3 = src[start + 3];
        char c5 = src[start + 5];
        char c0 = src[start];
        char c2 = src[start + 2];
        char c4 = src[start + 4];
        int hash = ((c0 << 12) + (c2 << 6) + c4) % 30;
        char[][] table = this.charArray_length[4][hash];
        int i = this.newEntry6;
        while (++i < 6) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3] || c4 != charArray[4] || c5 != charArray[5]) continue;
            return charArray;
        }
        i = -1;
        int max = this.newEntry6;
        while (++i <= max) {
            char[] charArray = table[i];
            if (c0 != charArray[0] || c1 != charArray[1] || c2 != charArray[2] || c3 != charArray[3] || c4 != charArray[4] || c5 != charArray[5]) continue;
            return charArray;
        }
        if (++max >= 6) {
            max = 0;
        }
        char[] r = new char[6];
        System.arraycopy(src, start, r, 0, 6);
        this.newEntry6 = max;
        table[this.newEntry6] = r;
        return r;
    }

    public boolean isInModuleDeclaration() {
        return this.fakeInModule || this.insideModuleInfo || this.activeParser != null && this.activeParser.isParsingModuleDeclaration();
    }

    protected boolean areRestrictedModuleKeywordsActive() {
        return this.scanContext != null && this.scanContext != ScanContext.INACTIVE;
    }

    void updateScanContext(int token) {
        switch (token) {
            case 25: 
            case 26: 
            case 33: {
                this.scanContext = ScanContext.EXPECTING_KEYWORD;
                break;
            }
            case 121: {
                this.scanContext = ScanContext.EXPECTING_KEYWORD;
                break;
            }
            case 122: {
                this.scanContext = ScanContext.AFTER_REQUIRES;
                break;
            }
            case 1: 
            case 27: 
            case 32: 
            case 36: 
            case 111: 
            case 120: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 128: 
            case 131: 
            case 132: {
                this.scanContext = ScanContext.EXPECTING_IDENTIFIER;
                break;
            }
            case 22: {
                this.scanContext = ScanContext.EXPECTING_KEYWORD;
                break;
            }
            case 38: {
                this.scanContext = ScanContext.EXPECTING_KEYWORD;
                break;
            }
        }
    }

    private void parseTags() {
        int pos;
        int position = 0;
        int currentStartPosition = this.startPosition;
        int currentLinePtr = this.linePtr;
        if (currentLinePtr >= 0) {
            position = this.lineEnds[currentLinePtr] + 1;
        }
        while (ScannerHelper.isWhitespace(this.source[position])) {
            ++position;
        }
        if (currentStartPosition == position) {
            return;
        }
        char[] s = null;
        int sourceEnd = this.currentPosition;
        int sourceStart = currentStartPosition;
        int sourceDelta = 0;
        if (this.withoutUnicodePtr != 0) {
            s = new char[this.withoutUnicodePtr];
            System.arraycopy(this.withoutUnicodeBuffer, 1, s, 0, this.withoutUnicodePtr);
            sourceEnd = this.withoutUnicodePtr;
            sourceStart = 1;
            sourceDelta = currentStartPosition;
        } else {
            s = this.source;
        }
        if (this.checkNonExternalizedStringLiterals && (pos = CharOperation.indexOf(TAG_PREFIX, s, true, sourceStart, sourceEnd)) != -1) {
            if (this.nlsTags == null) {
                this.nlsTags = new NLSTag[10];
                this.nlsTagsPtr = 0;
            }
            while (pos != -1) {
                int start = pos + TAG_PREFIX_LENGTH;
                int end = CharOperation.indexOf('$', s, start, sourceEnd);
                if (end != -1) {
                    NLSTag currentTag = null;
                    int currentLine = currentLinePtr + 1;
                    try {
                        currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, this.extractInt(s, start, end));
                    }
                    catch (NumberFormatException numberFormatException) {
                        currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, -1);
                    }
                    if (this.nlsTagsPtr == this.nlsTags.length) {
                        this.nlsTags = new NLSTag[this.nlsTagsPtr + 10];
                        System.arraycopy(this.nlsTags, 0, this.nlsTags, 0, this.nlsTagsPtr);
                    }
                    this.nlsTags[this.nlsTagsPtr++] = currentTag;
                } else {
                    end = start;
                }
                pos = CharOperation.indexOf(TAG_PREFIX, s, true, end, sourceEnd);
            }
        }
        if (this.checkUninternedIdentityComparison && (pos = CharOperation.indexOf(IDENTITY_COMPARISON_TAG, s, true, sourceStart, sourceEnd)) != -1) {
            if (this.validIdentityComparisonLines == null) {
                this.validIdentityComparisonLines = new boolean[0];
            }
            int currentLine = currentLinePtr + 1;
            int length = this.validIdentityComparisonLines.length;
            this.validIdentityComparisonLines = new boolean[currentLine + 1];
            System.arraycopy(this.validIdentityComparisonLines, 0, this.validIdentityComparisonLines, 0, length);
            this.validIdentityComparisonLines[currentLine] = true;
        }
    }

    private int extractInt(char[] array, int start, int end) {
        int value = 0;
        int i = start;
        while (i < end) {
            char currentChar = array[i];
            int digit = 0;
            switch (currentChar) {
                case '0': {
                    digit = 0;
                    break;
                }
                case '1': {
                    digit = 1;
                    break;
                }
                case '2': {
                    digit = 2;
                    break;
                }
                case '3': {
                    digit = 3;
                    break;
                }
                case '4': {
                    digit = 4;
                    break;
                }
                case '5': {
                    digit = 5;
                    break;
                }
                case '6': {
                    digit = 6;
                    break;
                }
                case '7': {
                    digit = 7;
                    break;
                }
                case '8': {
                    digit = 8;
                    break;
                }
                case '9': {
                    digit = 9;
                    break;
                }
                default: {
                    throw new NumberFormatException();
                }
            }
            value *= 10;
            if (digit < 0) {
                throw new NumberFormatException();
            }
            value += digit;
            ++i;
        }
        return value;
    }

    public final void pushLineSeparator() {
        block12: {
            if (this.currentCharacter == '\r') {
                int separatorPos = this.currentPosition - 1;
                if (this.linePtr >= 0 && this.lineEnds[this.linePtr] >= separatorPos) {
                    return;
                }
                int length = this.lineEnds.length;
                if (++this.linePtr >= length) {
                    this.lineEnds = new int[2 * length + 250];
                    System.arraycopy(this.lineEnds, 0, this.lineEnds, 0, length);
                }
                this.lineEnds[this.linePtr] = separatorPos;
                try {
                    if (this.source[this.currentPosition] == '\n') {
                        this.lineEnds[this.linePtr] = this.currentPosition++;
                        this.wasAcr = false;
                        break block12;
                    }
                    this.wasAcr = true;
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    this.wasAcr = true;
                }
            } else if (this.currentCharacter == '\n') {
                if (this.wasAcr && this.lineEnds[this.linePtr] == this.currentPosition - 2) {
                    this.lineEnds[this.linePtr] = this.currentPosition - 1;
                } else {
                    int separatorPos = this.currentPosition - 1;
                    if (this.linePtr >= 0 && this.lineEnds[this.linePtr] >= separatorPos) {
                        return;
                    }
                    int length = this.lineEnds.length;
                    if (++this.linePtr >= length) {
                        this.lineEnds = new int[2 * length + 250];
                        System.arraycopy(this.lineEnds, 0, this.lineEnds, 0, length);
                    }
                    this.lineEnds[this.linePtr] = separatorPos;
                }
                this.wasAcr = false;
            }
        }
    }

    public final void pushUnicodeLineSeparator() {
        if (this.currentCharacter == '\r') {
            this.wasAcr = this.source[this.currentPosition] != '\n';
        } else if (this.currentCharacter == '\n') {
            this.wasAcr = false;
        }
    }

    public void recordComment(int token) {
        int commentStart = this.startPosition;
        int stopPosition = this.currentPosition;
        switch (token) {
            case 1001: {
                commentStart = -this.startPosition;
                stopPosition = -this.lastCommentLinePosition;
                break;
            }
            case 1002: {
                stopPosition = -this.currentPosition;
            }
        }
        int length = this.commentStops.length;
        if (++this.commentPtr >= length) {
            int newLength = length + 300;
            this.commentStops = new int[newLength];
            System.arraycopy(this.commentStops, 0, this.commentStops, 0, length);
            this.commentStarts = new int[newLength];
            System.arraycopy(this.commentStarts, 0, this.commentStarts, 0, length);
            this.commentTagStarts = new int[newLength];
            System.arraycopy(this.commentTagStarts, 0, this.commentTagStarts, 0, length);
        }
        this.commentStops[this.commentPtr] = stopPosition;
        this.commentStarts[this.commentPtr] = commentStart;
    }

    public void resetTo(int begin, int end) {
        this.resetTo(begin, end, this.isInModuleDeclaration());
    }

    public void resetTo(int begin, int end, boolean isModuleInfo) {
        this.resetTo(begin, end, isModuleInfo, null);
    }

    public void resetTo(int begin, int end, boolean isModuleInfo, ScanContext context) {
        this.diet = false;
        this.startPosition = this.currentPosition = begin;
        this.initialPosition = this.currentPosition;
        this.eofPosition = this.source != null && this.source.length < end ? this.source.length : (end < Integer.MAX_VALUE ? end + 1 : end);
        this.commentPtr = -1;
        this.foundTaskCount = 0;
        this.nextToken = 0;
        this.lookBack[1] = 0;
        this.lookBack[0] = 0;
        this.consumingEllipsisAnnotations = false;
        this.insideModuleInfo = isModuleInfo;
        this.scanContext = context == null ? this.getScanContext(begin) : context;
    }

    private ScanContext getScanContext(int begin) {
        if (!this.isInModuleDeclaration()) {
            return ScanContext.INACTIVE;
        }
        if (begin == 0) {
            return ScanContext.EXPECTING_KEYWORD;
        }
        CompilerOptions options = new CompilerOptions();
        options.complianceLevel = this.complianceLevel;
        options.sourceLevel = this.sourceLevel;
        ScanContextDetector parser = new ScanContextDetector(options);
        return parser.getScanContext(this.source, begin - 1);
    }

    protected final void scanEscapeCharacter() throws InvalidInputException {
        switch (this.currentCharacter) {
            case 'b': {
                this.currentCharacter = (char)8;
                break;
            }
            case 't': {
                this.currentCharacter = (char)9;
                break;
            }
            case 'n': {
                this.currentCharacter = (char)10;
                break;
            }
            case 'f': {
                this.currentCharacter = (char)12;
                break;
            }
            case 'r': {
                this.currentCharacter = (char)13;
                break;
            }
            case '\"': {
                this.currentCharacter = (char)34;
                break;
            }
            case '\'': {
                this.currentCharacter = (char)39;
                break;
            }
            case 's': {
                this.currentCharacter = (char)32;
                break;
            }
            case '\\': {
                this.currentCharacter = (char)92;
                break;
            }
            default: {
                int number = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                if (number >= 0 && number <= 7) {
                    boolean zeroToThreeNot = number > 3;
                    if (ScannerHelper.isDigit(this.currentCharacter = this.source[this.currentPosition++])) {
                        int digit = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                        if (digit >= 0 && digit <= 7) {
                            number = number * 8 + digit;
                            if (ScannerHelper.isDigit(this.currentCharacter = this.source[this.currentPosition++])) {
                                if (zeroToThreeNot) {
                                    --this.currentPosition;
                                } else {
                                    digit = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                                    if (digit >= 0 && digit <= 7) {
                                        number = number * 8 + digit;
                                    } else {
                                        --this.currentPosition;
                                    }
                                }
                            } else {
                                --this.currentPosition;
                            }
                        } else {
                            --this.currentPosition;
                        }
                    } else {
                        --this.currentPosition;
                    }
                    if (number > 255) {
                        throw new InvalidInputException(INVALID_ESCAPE);
                    }
                    this.currentCharacter = (char)number;
                    break;
                }
                throw new InvalidInputException(INVALID_ESCAPE);
            }
        }
    }

    public int scanIdentifierOrKeywordWithBoundCheck() {
        int index;
        char[] data;
        int length;
        int pos;
        this.useAssertAsAnIndentifier = false;
        this.useEnumAsAnIndentifier = false;
        char[] src = this.source;
        int srcLength = this.eofPosition;
        while ((pos = ++this.currentPosition) < srcLength) {
            char c = src[pos];
            if (c < '\u0080') {
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x3C) != 0) {
                    if (this.withoutUnicodePtr != 0) {
                        this.currentCharacter = c;
                        this.unicodeStore();
                    }
                    continue;
                }
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0) {
                    this.currentCharacter = c;
                    break;
                }
                while (this.getNextCharAsJavaIdentifierPartWithBoundCheck()) {
                }
                break;
            }
            while (this.getNextCharAsJavaIdentifierPartWithBoundCheck()) {
            }
            break block0;
        }
        if (this.withoutUnicodePtr == 0) {
            length = this.currentPosition - this.startPosition;
            if (length == 1) {
                return 22;
            }
            data = this.source;
            index = this.startPosition;
        } else {
            length = this.withoutUnicodePtr;
            if (length == 1) {
                return 22;
            }
            data = this.withoutUnicodeBuffer;
            index = 1;
        }
        return this.internalScanIdentifierOrKeyword(index, length, data);
    }

    public int scanIdentifierOrKeyword() {
        int index;
        char[] data;
        int length;
        int pos;
        this.useAssertAsAnIndentifier = false;
        this.useEnumAsAnIndentifier = false;
        char[] src = this.source;
        int srcLength = this.eofPosition;
        while ((pos = ++this.currentPosition) < srcLength) {
            char c = src[pos];
            if (c < '\u0080') {
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x3C) != 0) {
                    if (this.withoutUnicodePtr != 0) {
                        this.currentCharacter = c;
                        this.unicodeStore();
                    }
                    continue;
                }
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0) {
                    this.currentCharacter = c;
                    break;
                }
                while (this.getNextCharAsJavaIdentifierPart()) {
                }
                break;
            }
            while (this.getNextCharAsJavaIdentifierPart()) {
            }
            break block0;
        }
        if (this.withoutUnicodePtr == 0) {
            length = this.currentPosition - this.startPosition;
            if (length == 1) {
                return 22;
            }
            data = this.source;
            index = this.startPosition;
        } else {
            length = this.withoutUnicodePtr;
            if (length == 1) {
                return 22;
            }
            data = this.withoutUnicodeBuffer;
            index = 1;
        }
        return this.internalScanIdentifierOrKeyword(index, length, data);
    }

    private int internalScanIdentifierOrKeyword(int index, int length, char[] data) {
        switch (data[index]) {
            case 'a': {
                switch (length) {
                    case 8: {
                        if (data[++index] == 'b' && data[++index] == 's' && data[++index] == 't' && data[++index] == 'r' && data[++index] == 'a' && data[++index] == 'c' && data[++index] == 't') {
                            return 42;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 's' && data[++index] == 's' && data[++index] == 'e' && data[++index] == 'r' && data[++index] == 't') {
                            if (this.sourceLevel >= 0x300000L) {
                                this.containsAssertKeyword = true;
                                return 81;
                            }
                            this.useAssertAsAnIndentifier = true;
                            return 22;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'b': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'y' && data[++index] == 't' && data[++index] == 'e') {
                            return 106;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'r' && data[++index] == 'e' && data[++index] == 'a' && data[++index] == 'k') {
                            return 82;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'o' && data[++index] == 'o' && data[++index] == 'l' && data[++index] == 'e' && data[++index] == 'a' && data[++index] == 'n') {
                            return 105;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'c': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 's' && data[++index] == 'e') {
                                return 91;
                            }
                            return 22;
                        }
                        if (data[index] == 'h' && data[++index] == 'a' && data[++index] == 'r') {
                            return 108;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 't' && data[++index] == 'c' && data[++index] == 'h') {
                                return 107;
                            }
                            return 22;
                        }
                        if (data[index] == 'l') {
                            if (data[++index] == 'a' && data[++index] == 's' && data[++index] == 's') {
                                return 70;
                            }
                            return 22;
                        }
                        if (data[index] == 'o' && data[++index] == 'n' && data[++index] == 's' && data[++index] == 't') {
                            return 133;
                        }
                        return 22;
                    }
                    case 8: {
                        if (data[++index] == 'o' && data[++index] == 'n' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'n' && data[++index] == 'u' && data[++index] == 'e') {
                            return 83;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'd': {
                switch (length) {
                    case 2: {
                        if (data[++index] == 'o') {
                            return 84;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'o' && data[++index] == 'u' && data[++index] == 'b' && data[++index] == 'l' && data[++index] == 'e') {
                            return 109;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'e' && data[++index] == 'f' && data[++index] == 'a' && data[++index] == 'u' && data[++index] == 'l' && data[++index] == 't') {
                            return 76;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'e': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'l') {
                            if (data[++index] == 's' && data[++index] == 'e') {
                                return 119;
                            }
                            return 22;
                        }
                        if (data[index] == 'n' && data[++index] == 'u' && data[++index] == 'm') {
                            if (this.sourceLevel >= 0x310000L) {
                                return 74;
                            }
                            this.useEnumAsAnIndentifier = true;
                            return 22;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'x') {
                            if (data[++index] == 't' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 'd' && data[++index] == 's') {
                                return 92;
                            }
                            if (this.areRestrictedModuleKeywordsActive() && data[index] == 'p' && data[++index] == 'o' && data[++index] == 'r' && data[++index] == 't' && data[++index] == 's') {
                                return 123;
                            }
                            return 22;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'f': {
                switch (length) {
                    case 3: {
                        if (data[++index] == 'o' && data[++index] == 'r') {
                            return 85;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'i') {
                            if (data[++index] == 'n' && data[++index] == 'a' && data[++index] == 'l') {
                                return 43;
                            }
                            return 22;
                        }
                        if (data[index] == 'l') {
                            if (data[++index] == 'o' && data[++index] == 'a' && data[++index] == 't') {
                                return 110;
                            }
                            return 22;
                        }
                        if (data[index] == 'a' && data[++index] == 'l' && data[++index] == 's' && data[++index] == 'e') {
                            return 52;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'i' && data[++index] == 'n' && data[++index] == 'a' && data[++index] == 'l' && data[++index] == 'l' && data[++index] == 'y') {
                            return 116;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'g': {
                if (length == 4 && data[++index] == 'o' && data[++index] == 't' && data[++index] == 'o') {
                    return 134;
                }
                return 22;
            }
            case 'i': {
                switch (length) {
                    case 2: {
                        if (data[++index] == 'f') {
                            return 86;
                        }
                        return 22;
                    }
                    case 3: {
                        if (data[++index] == 'n' && data[++index] == 't') {
                            return 112;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'm' && data[++index] == 'p' && data[++index] == 'o' && data[++index] == 'r' && data[++index] == 't') {
                            return 111;
                        }
                        return 22;
                    }
                    case 9: {
                        if (data[++index] == 'n' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'r' && data[++index] == 'f' && data[++index] == 'a' && data[++index] == 'c' && data[++index] == 'e') {
                            return 73;
                        }
                        return 22;
                    }
                    case 10: {
                        if (data[++index] == 'm') {
                            if (data[++index] == 'p' && data[++index] == 'l' && data[++index] == 'e' && data[++index] == 'm' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 't' && data[++index] == 's') {
                                return 130;
                            }
                            return 22;
                        }
                        if (data[index] == 'n' && data[++index] == 's' && data[++index] == 't' && data[++index] == 'a' && data[++index] == 'n' && data[++index] == 'c' && data[++index] == 'e' && data[++index] == 'o' && data[++index] == 'f') {
                            return 17;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'l': {
                if (length == 4 && data[++index] == 'o' && data[++index] == 'n' && data[++index] == 'g') {
                    return 113;
                }
                return 22;
            }
            case 'm': {
                switch (length) {
                    case 6: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'o' && data[++index] == 'd' && data[++index] == 'u' && data[++index] == 'l' && data[++index] == 'e') {
                            return 120;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'n': {
                switch (length) {
                    case 3: {
                        if (data[++index] == 'e' && data[++index] == 'w') {
                            return 37;
                        }
                        int token = this.checkFor_KeyWord(index - 1, length, data);
                        return token != 0 ? token : 22;
                    }
                    case 4: {
                        if (data[++index] == 'u' && data[++index] == 'l' && data[++index] == 'l') {
                            return 53;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'v' && data[++index] == 'e') {
                            return 44;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'o': {
                switch (length) {
                    case 4: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'p' && data[++index] == 'e' && data[++index] == 'n') {
                            return 121;
                        }
                        return 22;
                    }
                    case 5: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'p' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 's') {
                            return 124;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'p': {
                switch (length) {
                    case 6: {
                        if (data[++index] == 'u' && data[++index] == 'b' && data[++index] == 'l' && data[++index] == 'i' && data[++index] == 'c') {
                            return 48;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 'c' && data[++index] == 'k' && data[++index] == 'a' && data[++index] == 'g' && data[++index] == 'e') {
                                return 90;
                            }
                            return 22;
                        }
                        if (data[index] == 'r' && data[++index] == 'i' && data[++index] == 'v' && data[++index] == 'a' && data[++index] == 't' && data[++index] == 'e') {
                            return 46;
                        }
                        if (data[index] == 'e' && data[++index] == 'r' && data[++index] == 'm' && data[++index] == 'i' && data[++index] == 't' && data[++index] == 's') {
                            return this.disambiguatedRestrictedIdentifierpermits(127);
                        }
                        return 22;
                    }
                    case 8: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'v' && data[++index] == 'i' && data[++index] == 'd' && data[++index] == 'e' && data[++index] == 's') {
                            return 126;
                        }
                        return 22;
                    }
                    case 9: {
                        if (data[++index] == 'r' && data[++index] == 'o' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'c' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'd') {
                            return 47;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'r': {
                switch (length) {
                    case 6: {
                        if (data[++index] == 'e') {
                            if (data[++index] == 't' && data[++index] == 'u' && data[++index] == 'r' && data[++index] == 'n') {
                                return 87;
                            }
                            if (data[index] == 'c' && data[++index] == 'o' && data[++index] == 'r' && data[++index] == 'd') {
                                return this.disambiguatedRestrictedIdentifierrecord(75);
                            }
                        }
                        return 22;
                    }
                    case 8: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'e' && data[++index] == 'q' && data[++index] == 'u' && data[++index] == 'i' && data[++index] == 'r' && data[++index] == 'e' && data[++index] == 's') {
                            return 122;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 's': {
                switch (length) {
                    case 5: {
                        if (data[++index] == 'h') {
                            if (data[++index] == 'o' && data[++index] == 'r' && data[++index] == 't') {
                                return 114;
                            }
                            return 22;
                        }
                        if (data[index] == 'u' && data[++index] == 'p' && data[++index] == 'e' && data[++index] == 'r') {
                            return 34;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 't') {
                            if (data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'c') {
                                return 39;
                            }
                            return 22;
                        }
                        if (data[index] == 'w' && data[++index] == 'i' && data[++index] == 't' && data[++index] == 'c' && data[++index] == 'h') {
                            return 63;
                        }
                        if (data[index] == 'e' && data[++index] == 'a' && data[++index] == 'l' && data[++index] == 'e' && data[++index] == 'd') {
                            return this.disambiguatedRestrictedIdentifiersealed(41);
                        }
                        return 22;
                    }
                    case 8: {
                        if (data[++index] == 't' && data[++index] == 'r' && data[++index] == 'i' && data[++index] == 'c' && data[++index] == 't' && data[++index] == 'f' && data[++index] == 'p') {
                            return 49;
                        }
                        return 22;
                    }
                    case 12: {
                        if (data[++index] == 'y' && data[++index] == 'n' && data[++index] == 'c' && data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'n' && data[++index] == 'i' && data[++index] == 'z' && data[++index] == 'e' && data[++index] == 'd') {
                            return 40;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 't': {
                switch (length) {
                    case 2: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'o') {
                            return 131;
                        }
                        return 22;
                    }
                    case 3: {
                        if (data[++index] == 'r' && data[++index] == 'y') {
                            return 88;
                        }
                        return 22;
                    }
                    case 4: {
                        if (data[++index] == 'h') {
                            if (data[++index] == 'i' && data[++index] == 's') {
                                return 35;
                            }
                            return 22;
                        }
                        if (data[index] == 'r' && data[++index] == 'u' && data[++index] == 'e') {
                            return 54;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'w') {
                            return 78;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'w' && data[++index] == 's') {
                            return 117;
                        }
                        return 22;
                    }
                    case 9: {
                        if (data[++index] == 'r' && data[++index] == 'a' && data[++index] == 'n' && data[++index] == 's' && data[++index] == 'i' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 't') {
                            return 50;
                        }
                        return 22;
                    }
                    case 10: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'r' && data[++index] == 'a' && data[++index] == 'n' && data[++index] == 's' && data[++index] == 'i' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'v' && data[++index] == 'e') {
                            return 128;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'u': {
                switch (length) {
                    case 4: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 's' && data[++index] == 'e' && data[++index] == 's') {
                            return 125;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'v': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'o' && data[++index] == 'i' && data[++index] == 'd') {
                            return 115;
                        }
                        return 22;
                    }
                    case 8: {
                        if (data[++index] == 'o' && data[++index] == 'l' && data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'l' && data[++index] == 'e') {
                            return 51;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'w': {
                switch (length) {
                    case 4: {
                        if (this.areRestrictedModuleKeywordsActive() && data[++index] == 'i' && data[++index] == 't' && data[++index] == 'h') {
                            return 132;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'h' && data[++index] == 'i' && data[++index] == 'l' && data[++index] == 'e') {
                            return 79;
                        }
                        return 22;
                    }
                }
                return 22;
            }
            case 'y': {
                switch (length) {
                    case 5: {
                        if (data[++index] != 'i' || data[++index] != 'e' || data[++index] != 'l' || data[++index] != 'd') break;
                        return this.disambiguatedRestrictedIdentifierYield(80);
                    }
                }
                return 22;
            }
        }
        return 22;
    }

    private int checkFor_KeyWord(int index, int length, char[] data) {
        if (this._Keywords == null) {
            this._Keywords = new HashMap<String, Integer>(0);
            if (JavaFeature.RECORDS.isSupported(this.complianceLevel, this.previewEnabled)) {
                this._Keywords.put("non-sealed", 45);
            }
        }
        for (String key : this._Keywords.keySet()) {
            if (!CharOperation.prefixEquals(key.toCharArray(), data, true, index)) continue;
            this.currentPosition = this.currentPosition - length + key.length();
            if (this.currentPosition < this.eofPosition) {
                this.currentCharacter = data[this.currentPosition];
            }
            return this._Keywords.get(key);
        }
        return 0;
    }

    public int scanNumber(boolean dotPrefix) throws InvalidInputException {
        boolean floating = dotPrefix;
        if (!dotPrefix && this.currentCharacter == '0') {
            if (this.getNextChar('x', 'X') >= 0) {
                int start = this.currentPosition;
                this.consumeDigits(16, true);
                int end = this.currentPosition;
                if (this.getNextChar('l', 'L') >= 0) {
                    if (end == start) {
                        throw new InvalidInputException(INVALID_HEXA);
                    }
                    return 56;
                }
                if (this.getNextChar('.')) {
                    boolean hasNoDigitsBeforeDot = end == start;
                    start = this.currentPosition;
                    this.consumeDigits(16, true);
                    end = this.currentPosition;
                    if (hasNoDigitsBeforeDot && end == start) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        throw new InvalidInputException(INVALID_HEXA);
                    }
                    if (this.getNextChar('p', 'P') >= 0) {
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        } else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                        if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                            this.unicodeAsBackSlash = false;
                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                            } else if (this.withoutUnicodePtr != 0) {
                                this.unicodeStore();
                            }
                        }
                        if (!ScannerHelper.isDigit(this.currentCharacter)) {
                            if (this.sourceLevel < 0x310000L) {
                                throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                            }
                            if (this.currentCharacter == '_') {
                                this.consumeDigits(10);
                                throw new InvalidInputException(INVALID_UNDERSCORE);
                            }
                            throw new InvalidInputException(INVALID_HEXA);
                        }
                        this.consumeDigits(10);
                        if (this.getNextChar('f', 'F') >= 0) {
                            if (this.sourceLevel < 0x310000L) {
                                throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                            }
                            return 57;
                        }
                        if (this.getNextChar('d', 'D') >= 0) {
                            if (this.sourceLevel < 0x310000L) {
                                throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                            }
                            return 58;
                        }
                        if (this.getNextChar('l', 'L') >= 0) {
                            if (this.sourceLevel < 0x310000L) {
                                throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                            }
                            throw new InvalidInputException(INVALID_HEXA);
                        }
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        return 58;
                    }
                    if (this.sourceLevel < 0x310000L) {
                        throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                    }
                    throw new InvalidInputException(INVALID_HEXA);
                }
                if (this.getNextChar('p', 'P') >= 0) {
                    if (end == start) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        throw new InvalidInputException(INVALID_HEXA);
                    }
                    this.unicodeAsBackSlash = false;
                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                        this.getNextUnicodeChar();
                    } else if (this.withoutUnicodePtr != 0) {
                        this.unicodeStore();
                    }
                    if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        } else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                    }
                    if (!ScannerHelper.isDigit(this.currentCharacter)) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        if (this.currentCharacter == '_') {
                            this.consumeDigits(10);
                            throw new InvalidInputException(INVALID_UNDERSCORE);
                        }
                        throw new InvalidInputException(INVALID_FLOAT);
                    }
                    this.consumeDigits(10);
                    if (this.getNextChar('f', 'F') >= 0) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        return 57;
                    }
                    if (this.getNextChar('d', 'D') >= 0) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        return 58;
                    }
                    if (this.getNextChar('l', 'L') >= 0) {
                        if (this.sourceLevel < 0x310000L) {
                            throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                        }
                        throw new InvalidInputException(INVALID_HEXA);
                    }
                    if (this.sourceLevel < 0x310000L) {
                        throw new InvalidInputException(ILLEGAL_HEXA_LITERAL);
                    }
                    return 58;
                }
                if (end == start) {
                    throw new InvalidInputException(INVALID_HEXA);
                }
                return 55;
            }
            if (this.getNextChar('b', 'B') >= 0) {
                int start = this.currentPosition;
                this.consumeDigits(2, true);
                int end = this.currentPosition;
                if (end == start) {
                    if (this.sourceLevel < 0x330000L) {
                        throw new InvalidInputException(BINARY_LITERAL_NOT_BELOW_17);
                    }
                    throw new InvalidInputException(INVALID_BINARY);
                }
                if (this.getNextChar('l', 'L') >= 0) {
                    if (this.sourceLevel < 0x330000L) {
                        throw new InvalidInputException(BINARY_LITERAL_NOT_BELOW_17);
                    }
                    return 56;
                }
                if (this.sourceLevel < 0x330000L) {
                    throw new InvalidInputException(BINARY_LITERAL_NOT_BELOW_17);
                }
                return 55;
            }
            if (this.getNextCharAsDigit()) {
                this.consumeDigits(10);
                if (this.getNextChar('l', 'L') >= 0) {
                    return 56;
                }
                if (this.getNextChar('f', 'F') >= 0) {
                    return 57;
                }
                if (this.getNextChar('d', 'D') >= 0) {
                    return 58;
                }
                boolean isInteger = true;
                if (this.getNextChar('.')) {
                    isInteger = false;
                    this.consumeDigits(10);
                }
                if (this.getNextChar('e', 'E') >= 0) {
                    isInteger = false;
                    this.unicodeAsBackSlash = false;
                    if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                        this.getNextUnicodeChar();
                    } else if (this.withoutUnicodePtr != 0) {
                        this.unicodeStore();
                    }
                    if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        } else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                    }
                    if (!ScannerHelper.isDigit(this.currentCharacter)) {
                        if (this.currentCharacter == '_') {
                            this.consumeDigits(10);
                            throw new InvalidInputException(INVALID_UNDERSCORE);
                        }
                        throw new InvalidInputException(INVALID_FLOAT);
                    }
                    this.consumeDigits(10);
                }
                if (this.getNextChar('f', 'F') >= 0) {
                    return 57;
                }
                if (this.getNextChar('d', 'D') >= 0 || !isInteger) {
                    return 58;
                }
                return 55;
            }
        }
        this.consumeDigits(10);
        if (!dotPrefix && this.getNextChar('l', 'L') >= 0) {
            return 56;
        }
        if (!dotPrefix && this.getNextChar('.')) {
            this.consumeDigits(10, true);
            floating = true;
        }
        if (this.getNextChar('e', 'E') >= 0) {
            floating = true;
            this.unicodeAsBackSlash = false;
            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
            } else if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                this.unicodeAsBackSlash = false;
                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                    this.getNextUnicodeChar();
                } else if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
            }
            if (!ScannerHelper.isDigit(this.currentCharacter)) {
                if (this.currentCharacter == '_') {
                    this.consumeDigits(10);
                    throw new InvalidInputException(INVALID_UNDERSCORE);
                }
                throw new InvalidInputException(INVALID_FLOAT);
            }
            this.consumeDigits(10);
        }
        if (this.getNextChar('d', 'D') >= 0) {
            return 58;
        }
        if (this.getNextChar('f', 'F') >= 0) {
            return 57;
        }
        return floating ? 58 : 55;
    }

    public final int getLineNumber(int position) {
        return Util.getLineNumber(position, this.lineEnds, 0, this.linePtr);
    }

    public final void setSource(char[] sourceString) {
        int sourceLength;
        if (sourceString == null) {
            this.source = CharOperation.NO_CHAR;
            sourceLength = 0;
        } else {
            this.source = sourceString;
            sourceLength = sourceString.length;
        }
        this.startPosition = -1;
        this.eofPosition = sourceLength;
        this.currentPosition = 0;
        this.initialPosition = 0;
        this.containsAssertKeyword = false;
        this.linePtr = -1;
        this.scanContext = null;
        this.yieldColons = -1;
        this.insideModuleInfo = false;
    }

    public final void setSource(char[] contents, CompilationResult compilationResult) {
        if (contents == null) {
            char[] cuContents = compilationResult.compilationUnit.getContents();
            this.setSource(cuContents);
        } else {
            this.setSource(contents);
        }
        int[] lineSeparatorPositions = compilationResult.lineSeparatorPositions;
        if (lineSeparatorPositions != null) {
            this.lineEnds = lineSeparatorPositions;
            this.linePtr = lineSeparatorPositions.length - 1;
        }
    }

    public final void setSource(CompilationResult compilationResult) {
        this.setSource(null, compilationResult);
    }

    public String toString() {
        if (this.startPosition == this.eofPosition) {
            return "EOF\n\n" + new String(this.source);
        }
        if (this.currentPosition > this.eofPosition) {
            return "behind the EOF\n\n" + new String(this.source);
        }
        if (this.currentPosition <= 0) {
            return "NOT started!\n\n" + (this.source != null ? new String(this.source) : "");
        }
        StringBuffer buffer = new StringBuffer();
        if (this.startPosition < 1000) {
            buffer.append(this.source, 0, this.startPosition);
        } else {
            buffer.append("<source beginning>\n...\n");
            int line = Util.getLineNumber(this.startPosition - 1000, this.lineEnds, 0, this.linePtr);
            int lineStart = this.getLineStart(line);
            buffer.append(this.source, lineStart, this.startPosition - lineStart);
        }
        buffer.append("\n===============================\nStarts here -->");
        int middleLength = this.currentPosition - 1 - this.startPosition + 1;
        if (middleLength > -1) {
            buffer.append(this.source, this.startPosition, middleLength);
        }
        if (this.nextToken != 0) {
            buffer.append("<-- Ends here [in pipeline " + this.toStringAction(this.nextToken) + "]\n===============================\n");
        } else {
            buffer.append("<-- Ends here\n===============================\n");
        }
        buffer.append(this.source, this.currentPosition - 1 + 1, this.eofPosition - (this.currentPosition - 1) - 1);
        return buffer.toString();
    }

    public String toStringAction(int act) {
        switch (act) {
            case 22: {
                return "Identifier(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 80: {
                return "yield";
            }
            case 75: {
                return "record";
            }
            case 42: {
                return "abstract";
            }
            case 105: {
                return "boolean";
            }
            case 82: {
                return "break";
            }
            case 106: {
                return "byte";
            }
            case 91: {
                return "case";
            }
            case 107: {
                return "catch";
            }
            case 108: {
                return "char";
            }
            case 70: {
                return "class";
            }
            case 83: {
                return "continue";
            }
            case 76: {
                return "default";
            }
            case 84: {
                return "do";
            }
            case 109: {
                return "double";
            }
            case 119: {
                return "else";
            }
            case 92: {
                return "extends";
            }
            case 52: {
                return "false";
            }
            case 43: {
                return "final";
            }
            case 116: {
                return "finally";
            }
            case 110: {
                return "float";
            }
            case 85: {
                return "for";
            }
            case 86: {
                return "if";
            }
            case 130: {
                return "implements";
            }
            case 111: {
                return "import";
            }
            case 17: {
                return "instanceof";
            }
            case 112: {
                return "int";
            }
            case 73: {
                return "interface";
            }
            case 113: {
                return "long";
            }
            case 44: {
                return "native";
            }
            case 37: {
                return "new";
            }
            case 45: {
                return "non-sealed";
            }
            case 53: {
                return "null";
            }
            case 90: {
                return "package";
            }
            case 127: {
                return "permits";
            }
            case 46: {
                return "private";
            }
            case 47: {
                return "protected";
            }
            case 48: {
                return "public";
            }
            case 87: {
                return "return";
            }
            case 41: {
                return "sealed";
            }
            case 114: {
                return "short";
            }
            case 39: {
                return "static";
            }
            case 34: {
                return "super";
            }
            case 63: {
                return "switch";
            }
            case 40: {
                return "synchronized";
            }
            case 35: {
                return "this";
            }
            case 78: {
                return "throw";
            }
            case 117: {
                return "throws";
            }
            case 50: {
                return "transient";
            }
            case 54: {
                return "true";
            }
            case 88: {
                return "try";
            }
            case 115: {
                return "void";
            }
            case 51: {
                return "volatile";
            }
            case 79: {
                return "while";
            }
            case 120: {
                return "module";
            }
            case 122: {
                return "requires";
            }
            case 123: {
                return "exports";
            }
            case 55: {
                return "Integer(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 56: {
                return "Long(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 57: {
                return "Float(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 58: {
                return "Double(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 59: {
                return "Char(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 60: {
                return "String(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 61: {
                return "String(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 2: {
                return "++";
            }
            case 3: {
                return "--";
            }
            case 19: {
                return "==";
            }
            case 12: {
                return "<=";
            }
            case 13: {
                return ">=";
            }
            case 20: {
                return "!=";
            }
            case 18: {
                return "<<";
            }
            case 14: {
                return ">>";
            }
            case 16: {
                return ">>>";
            }
            case 93: {
                return "+=";
            }
            case 94: {
                return "-=";
            }
            case 104: {
                return "->";
            }
            case 95: {
                return "*=";
            }
            case 96: {
                return "/=";
            }
            case 97: {
                return "&=";
            }
            case 98: {
                return "|=";
            }
            case 99: {
                return "^=";
            }
            case 100: {
                return "%=";
            }
            case 101: {
                return "<<=";
            }
            case 102: {
                return ">>=";
            }
            case 103: {
                return ">>>=";
            }
            case 31: {
                return "||";
            }
            case 30: {
                return "&&";
            }
            case 4: {
                return "+";
            }
            case 5: {
                return "-";
            }
            case 66: {
                return "!";
            }
            case 9: {
                return "%";
            }
            case 24: {
                return "^";
            }
            case 21: {
                return "&";
            }
            case 8: {
                return "*";
            }
            case 28: {
                return "|";
            }
            case 67: {
                return "~";
            }
            case 10: {
                return "/";
            }
            case 15: {
                return ">";
            }
            case 11: {
                return "<";
            }
            case 23: {
                return "(";
            }
            case 26: {
                return ")";
            }
            case 38: {
                return "{";
            }
            case 33: {
                return "}";
            }
            case 6: {
                return "[";
            }
            case 69: {
                return "]";
            }
            case 25: {
                return ";";
            }
            case 29: {
                return "?";
            }
            case 65: {
                return ":";
            }
            case 7: {
                return "::";
            }
            case 32: {
                return ",";
            }
            case 1: {
                return ".";
            }
            case 77: {
                return "=";
            }
            case 64: {
                return "EOF";
            }
            case 1000: {
                return "white_space(" + new String(this.getCurrentTokenSource()) + ")";
            }
        }
        return "not-a-token";
    }

    public void unicodeInitializeBuffer(int length) {
        int bLength;
        this.withoutUnicodePtr = length;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[length + 11];
        }
        if (1 + length >= (bLength = this.withoutUnicodeBuffer.length)) {
            this.withoutUnicodeBuffer = new char[length + 11];
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer, 0, bLength);
        }
        System.arraycopy(this.source, this.startPosition, this.withoutUnicodeBuffer, 1, length);
    }

    public void unicodeStore() {
        int length;
        int pos = ++this.withoutUnicodePtr;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[10];
        }
        if (pos == (length = this.withoutUnicodeBuffer.length)) {
            this.withoutUnicodeBuffer = new char[length * 2];
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer, 0, length);
        }
        this.withoutUnicodeBuffer[pos] = this.currentCharacter;
    }

    public void unicodeStore(char character) {
        int length;
        int pos = ++this.withoutUnicodePtr;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[10];
        }
        if (pos == (length = this.withoutUnicodeBuffer.length)) {
            this.withoutUnicodeBuffer = new char[length * 2];
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer, 0, length);
        }
        this.withoutUnicodeBuffer[pos] = character;
    }

    public static boolean isIdentifier(int token) {
        return token == 22;
    }

    public static boolean isLiteral(int token) {
        switch (token) {
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: {
                return true;
            }
        }
        return false;
    }

    public static boolean isKeyword(int token) {
        switch (token) {
            case 17: 
            case 34: 
            case 35: 
            case 37: 
            case 39: 
            case 40: 
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
            case 87: 
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
            case 117: 
            case 119: 
            case 130: {
                return true;
            }
        }
        return false;
    }

    private VanguardParser getVanguardParser() {
        if (this.vanguardParser == null) {
            this.vanguardScanner = new VanguardScanner(this.sourceLevel, this.complianceLevel, this.previewEnabled);
            this.vanguardParser = new VanguardParser(this.vanguardScanner);
            this.vanguardScanner.setActiveParser(this.vanguardParser);
        }
        this.vanguardScanner.setSource(this.source);
        this.vanguardScanner.resetTo(this.startPosition, this.eofPosition - 1, this.isInModuleDeclaration(), this.scanContext);
        return this.vanguardParser;
    }

    private VanguardParser getNewVanguardParser() {
        VanguardScanner vs = this.getNewVanguardScanner();
        VanguardParser vp = new VanguardParser(vs);
        vs.setActiveParser(vp);
        return vp;
    }

    private VanguardScanner getNewVanguardScanner() {
        VanguardScanner vs = new VanguardScanner(this.sourceLevel, this.complianceLevel, this.previewEnabled);
        vs.setSource(this.source);
        vs.resetTo(this.startPosition, this.eofPosition - 1, this.isInModuleDeclaration(), this.scanContext);
        return vs;
    }

    protected final boolean mayBeAtBreakPreview() {
        return this.breakPreviewAllowed && this.lookBack[1] != 104;
    }

    protected final boolean maybeAtLambdaOrCast() {
        switch (this.lookBack[1]) {
            case 22: 
            case 34: 
            case 35: 
            case 40: 
            case 63: 
            case 79: 
            case 85: 
            case 86: 
            case 88: 
            case 107: {
                return false;
            }
        }
        return this.activeParser.atConflictScenario(23);
    }

    protected final boolean maybeAtReferenceExpression() {
        switch (this.lookBack[1]) {
            case 22: {
                switch (this.lookBack[0]) {
                    case 11: 
                    case 14: 
                    case 15: 
                    case 17: 
                    case 21: 
                    case 25: 
                    case 33: 
                    case 34: 
                    case 36: 
                    case 37: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 45: 
                    case 46: 
                    case 47: 
                    case 48: 
                    case 70: 
                    case 73: 
                    case 74: 
                    case 76: 
                    case 92: 
                    case 117: 
                    case 127: 
                    case 130: {
                        return false;
                    }
                }
                break;
            }
            case 0: {
                break;
            }
            default: {
                return false;
            }
        }
        return this.activeParser.atConflictScenario(11);
    }

    private final boolean maybeAtEllipsisAnnotationsStart() {
        if (this.consumingEllipsisAnnotations) {
            return false;
        }
        switch (this.lookBack[1]) {
            case 1: 
            case 11: 
            case 17: 
            case 21: 
            case 32: 
            case 34: 
            case 37: 
            case 38: 
            case 92: 
            case 117: 
            case 127: 
            case 130: {
                return false;
            }
        }
        return true;
    }

    protected final boolean atTypeAnnotation() {
        return !this.activeParser.atConflictScenario(36);
    }

    public void setActiveParser(ConflictedParser parser) {
        this.activeParser = parser;
        this.lookBack[1] = 0;
        this.lookBack[0] = 0;
        if (parser != null) {
            this.insideModuleInfo = parser.isParsingModuleDeclaration();
        }
    }

    public static boolean isRestrictedKeyword(int token) {
        switch (token) {
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 128: 
            case 131: 
            case 132: {
                return true;
            }
        }
        return false;
    }

    private boolean mayBeAtAnYieldStatement() {
        switch (this.lookBack[1]) {
            case 25: 
            case 26: 
            case 33: 
            case 38: 
            case 84: 
            case 119: {
                return true;
            }
            case 65: {
                return this.lookBack[0] == 76 || this.yieldColons == 1;
            }
        }
        return false;
    }

    private boolean mayBeAtARestricedIdentifier(int restrictedIdentifier) {
        switch (restrictedIdentifier) {
            case 41: {
                break;
            }
        }
        return true;
    }

    int disambiguatedRestrictedIdentifierrecord(int restrictedIdentifierToken) {
        if (restrictedIdentifierToken != 75) {
            return restrictedIdentifierToken;
        }
        if (!JavaFeature.RECORDS.isSupported(this.complianceLevel, this.previewEnabled)) {
            return 22;
        }
        return this.disambiguaterecordWithLookAhead() ? restrictedIdentifierToken : 22;
    }

    private int getNextTokenAfterTypeParameterHeader() {
        block8: {
            int count = 1;
            try {
                int token;
                while ((token = this.vanguardScanner.getNextToken()) != 0) {
                    if (token != 64) {
                        if (token == 11) {
                            ++count;
                        }
                        if (token == 15) {
                            --count;
                        }
                        if (token == 14) {
                            count -= 2;
                        }
                        if (token == 16) {
                            count -= 3;
                        }
                        if (count > 0) continue;
                        return this.vanguardScanner.getNextToken();
                    }
                    break;
                }
            }
            catch (InvalidInputException e) {
                if (e.getMessage().equals(INVALID_CHAR_IN_STRING)) break block8;
                e.printStackTrace();
            }
        }
        return 64;
    }

    private boolean disambiguaterecordWithLookAhead() {
        block4: {
            int lookAhead2;
            block5: {
                if (this.isInModuleDeclaration()) {
                    return false;
                }
                this.getVanguardParser();
                this.vanguardScanner.resetTo(this.currentPosition, this.eofPosition - 1);
                int lookAhead1 = this.vanguardScanner.getNextToken();
                if (lookAhead1 != 22) break block4;
                lookAhead2 = this.vanguardScanner.getNextToken();
                int n = lookAhead2 = lookAhead2 == 11 ? this.getNextTokenAfterTypeParameterHeader() : lookAhead2;
                if (lookAhead2 != 38) break block5;
                return true;
            }
            try {
                return lookAhead2 == 23;
            }
            catch (InvalidInputException e) {
                if (e.getMessage().equals(INVALID_CHAR_IN_STRING)) break block4;
                e.printStackTrace();
            }
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean disambiguateYieldWithLookAhead() {
        this.getVanguardParser();
        this.vanguardScanner.resetTo(this.currentPosition, this.eofPosition - 1);
        try {
            int lookAhead1 = this.vanguardScanner.getNextToken();
            switch (lookAhead1) {
                case 1: 
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 24: 
                case 25: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 36: 
                case 38: 
                case 65: 
                case 67: 
                case 69: 
                case 77: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 118: {
                    return false;
                }
                case 2: 
                case 3: {
                    int lookAhead2 = this.vanguardScanner.getNextToken();
                    return lookAhead2 == 22;
                }
            }
            return true;
        }
        catch (InvalidInputException e) {
            if (!e.getMessage().equals(INVALID_CHAR_IN_STRING)) {
                e.printStackTrace();
            }
            return false;
        }
    }

    int disambiguatedRestrictedIdentifierpermits(int restrictedIdentifierToken) {
        if (restrictedIdentifierToken != 127) {
            return restrictedIdentifierToken;
        }
        if (!JavaFeature.RECORDS.isSupported(this.complianceLevel, this.previewEnabled)) {
            return 22;
        }
        return this.disambiguatesRestrictedIdentifierWithLookAhead(this::mayBeAtARestricedIdentifier, restrictedIdentifierToken, Goal.RestrictedIdentifierPermitsGoal);
    }

    int disambiguatedRestrictedIdentifiersealed(int restrictedIdentifierToken) {
        if (restrictedIdentifierToken != 41) {
            return restrictedIdentifierToken;
        }
        if (!JavaFeature.RECORDS.isSupported(this.complianceLevel, this.previewEnabled)) {
            return 22;
        }
        return this.disambiguatesRestrictedIdentifierWithLookAhead(this::mayBeAtARestricedIdentifier, restrictedIdentifierToken, Goal.RestrictedIdentifierSealedGoal);
    }

    int disambiguatedRestrictedIdentifierYield(int restrictedIdentifierToken) {
        if (restrictedIdentifierToken != 80) {
            return restrictedIdentifierToken;
        }
        if (this.sourceLevel < 0x3A0000L) {
            return 22;
        }
        return this.mayBeAtAnYieldStatement() && this.disambiguateYieldWithLookAhead() ? restrictedIdentifierToken : 22;
    }

    int disambiguatedRestrictedKeyword(int restrictedKeywordToken) {
        int token = restrictedKeywordToken;
        if (this.scanContext == ScanContext.EXPECTING_IDENTIFIER) {
            return 22;
        }
        switch (restrictedKeywordToken) {
            case 128: {
                if (this.scanContext != ScanContext.AFTER_REQUIRES) {
                    token = 22;
                    break;
                }
                this.getVanguardParser();
                this.vanguardScanner.resetTo(this.currentPosition, this.eofPosition - 1, true, ScanContext.EXPECTING_IDENTIFIER);
                try {
                    int lookAhead = this.vanguardScanner.getNextToken();
                    if (lookAhead != 25) break;
                    token = 22;
                }
                catch (InvalidInputException invalidInputException) {}
                break;
            }
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: 
            case 131: 
            case 132: {
                if (this.scanContext == ScanContext.EXPECTING_KEYWORD) break;
                token = 22;
            }
        }
        return token;
    }

    int disambiguatesRestrictedIdentifierWithLookAhead(Predicate<Integer> checkPrecondition, int restrictedIdentifierToken, Goal goal) {
        if (checkPrecondition.test(restrictedIdentifierToken)) {
            VanguardParser vp = this.getNewVanguardParser();
            VanguardScanner vs = (VanguardScanner)vp.scanner;
            vs.resetTo(this.currentPosition, this.eofPosition - 1);
            if (vp.parse(goal)) {
                return restrictedIdentifierToken;
            }
        }
        return 22;
    }

    private VanguardScanner getNewVanguardScanner(char[] src) {
        VanguardScanner vs = new VanguardScanner(this.sourceLevel, this.complianceLevel, this.previewEnabled);
        vs.setSource(src);
        vs.resetTo(0, src.length, this.isInModuleDeclaration(), this.scanContext);
        return vs;
    }

    private VanguardParser getNewVanguardParser(char[] src) {
        VanguardScanner vs = this.getNewVanguardScanner(src);
        VanguardParser vp = new VanguardParser(vs);
        vs.setActiveParser(vp);
        return vp;
    }

    int disambiguatedToken(int token) {
        VanguardParser parser = this.getVanguardParser();
        if (token == 104 && this.mayBeAtCaseLabelExpr() && this.caseStartPosition < this.startPosition) {
            int nSz = this.startPosition - this.caseStartPosition;
            String s = new String(this.source, this.caseStartPosition, nSz);
            String modSource = s.concat(new String(new char[]{':'}));
            char[] nSource = modSource.toCharArray();
            VanguardParser vp = this.getNewVanguardParser(nSource);
            if (vp.parse(Goal.SwitchLabelCaseLhsGoal)) {
                this.nextToken = 104;
                return 72;
            }
        } else if (token == 23 && this.maybeAtLambdaOrCast()) {
            if (parser.parse(Goal.LambdaParameterListGoal)) {
                this.nextToken = 23;
                return 62;
            }
            this.vanguardScanner.resetTo(this.startPosition, this.eofPosition - 1);
            if (parser.parse(Goal.IntersectionCastGoal)) {
                this.nextToken = 23;
                return 68;
            }
        } else if (token == 11 && this.maybeAtReferenceExpression()) {
            if (parser.parse(Goal.ReferenceExpressionGoal)) {
                this.nextToken = 11;
                return 89;
            }
        } else if (token == 36 && this.atTypeAnnotation()) {
            token = 27;
            if (this.maybeAtEllipsisAnnotationsStart() && parser.parse(Goal.VarargTypeAnnotationGoal)) {
                this.consumingEllipsisAnnotations = true;
                this.nextToken = 27;
                return 129;
            }
        }
        return token;
    }

    private boolean mayBeAtCaseLabelExpr() {
        return this.lookBack[1] != 76 && this.caseStartPosition > 0;
    }

    protected boolean isAtAssistIdentifier() {
        return false;
    }

    public int fastForward(Statement unused) {
        block8: while (true) {
            int token;
            try {
                token = this.getNextToken();
            }
            catch (InvalidInputException invalidInputException) {
                return 64;
            }
            switch (token) {
                case 22: {
                    if (this.isAtAssistIdentifier()) {
                        return token;
                    }
                }
                case 2: 
                case 3: 
                case 11: 
                case 23: 
                case 27: 
                case 34: 
                case 35: 
                case 36: 
                case 37: 
                case 38: 
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
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 70: 
                case 72: 
                case 73: 
                case 74: 
                case 76: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 91: 
                case 105: 
                case 106: 
                case 108: 
                case 109: 
                case 110: 
                case 112: 
                case 113: 
                case 114: 
                case 115: {
                    if (!this.getVanguardParser().parse(Goal.BlockStatementoptGoal)) continue block8;
                    return token;
                }
                case 25: 
                case 64: {
                    return token;
                }
                case 33: {
                    this.ungetToken(token);
                    return 25;
                }
            }
        }
    }

    protected int getNextNotFakedToken() throws InvalidInputException {
        return this.getNextToken();
    }

    private static class Goal {
        int first;
        int[] follow;
        int[] rules;
        static int LambdaParameterListRule = 0;
        static int IntersectionCastRule = 0;
        static int ReferenceExpressionRule = 0;
        static int VarargTypeAnnotationsRule = 0;
        static int BlockStatementoptRule = 0;
        static int YieldStatementRule = 0;
        static int SwitchLabelCaseLhsRule = 0;
        static int[] RestrictedIdentifierSealedRule;
        static int[] RestrictedIdentifierPermitsRule;
        static Goal LambdaParameterListGoal;
        static Goal IntersectionCastGoal;
        static Goal VarargTypeAnnotationGoal;
        static Goal ReferenceExpressionGoal;
        static Goal BlockStatementoptGoal;
        static Goal YieldStatementGoal;
        static Goal SwitchLabelCaseLhsGoal;
        static Goal RestrictedIdentifierSealedGoal;
        static Goal RestrictedIdentifierPermitsGoal;
        static int[] RestrictedIdentifierSealedFollow;
        static int[] RestrictedIdentifierPermitsFollow;

        static {
            RestrictedIdentifierSealedFollow = new int[]{70, 73, 74, 75};
            RestrictedIdentifierPermitsFollow = new int[]{38};
            ArrayList<Integer> ridSealed = new ArrayList<Integer>(2);
            ArrayList<Integer> ridPermits = new ArrayList<Integer>();
            int i = 1;
            while (i <= 919) {
                if ("ParenthesizedLambdaParameterList".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    LambdaParameterListRule = i;
                } else if ("ParenthesizedCastNameAndBounds".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    IntersectionCastRule = i;
                } else if ("ReferenceExpressionTypeArgumentsAndTrunk".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    ReferenceExpressionRule = i;
                } else if ("TypeAnnotations".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    VarargTypeAnnotationsRule = i;
                } else if ("BlockStatementopt".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    BlockStatementoptRule = i;
                } else if ("YieldStatement".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    YieldStatementRule = i;
                } else if ("Modifiersopt".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    ridSealed.add(i);
                } else if ("PermittedSubclasses".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    ridPermits.add(i);
                } else if ("SwitchLabelCaseLhs".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    SwitchLabelCaseLhsRule = i;
                }
                ++i;
            }
            RestrictedIdentifierSealedRule = ridSealed.stream().mapToInt(Integer::intValue).toArray();
            RestrictedIdentifierPermitsRule = ridPermits.stream().mapToInt(Integer::intValue).toArray();
            LambdaParameterListGoal = new Goal(104, new int[]{104}, LambdaParameterListRule);
            IntersectionCastGoal = new Goal(23, Goal.followSetOfCast(), IntersectionCastRule);
            VarargTypeAnnotationGoal = new Goal(36, new int[]{118}, VarargTypeAnnotationsRule);
            ReferenceExpressionGoal = new Goal(11, new int[]{7}, ReferenceExpressionRule);
            BlockStatementoptGoal = new Goal(38, new int[0], BlockStatementoptRule);
            YieldStatementGoal = new Goal(104, new int[0], YieldStatementRule);
            SwitchLabelCaseLhsGoal = new Goal(104, new int[0], SwitchLabelCaseLhsRule);
            RestrictedIdentifierSealedGoal = new Goal(41, RestrictedIdentifierSealedFollow, RestrictedIdentifierSealedRule);
            RestrictedIdentifierPermitsGoal = new Goal(127, RestrictedIdentifierPermitsFollow, RestrictedIdentifierPermitsRule);
        }

        Goal(int first, int[] follow, int rule) {
            this.first = first;
            this.follow = follow;
            this.rules = new int[]{rule};
        }

        Goal(int first, int[] follow, int[] rules) {
            this.first = first;
            this.follow = follow;
            this.rules = rules;
        }

        boolean hasBeenReached(int act, int token) {
            boolean foundRule = false;
            int[] nArray = this.rules;
            int n = this.rules.length;
            int n2 = 0;
            while (n2 < n) {
                int i = nArray[n2];
                if (act == i) {
                    foundRule = true;
                    break;
                }
                ++n2;
            }
            if (foundRule) {
                int length = this.follow.length;
                if (length == 0) {
                    return true;
                }
                int i = 0;
                while (i < length) {
                    if (this.follow[i] == token) {
                        return true;
                    }
                    ++i;
                }
            }
            return false;
        }

        private static int[] followSetOfCast() {
            return new int[]{22, 37, 34, 35, 52, 54, 53, 55, 56, 57, 58, 59, 60, 61, 66, 67, 23};
        }
    }

    static enum ScanContext {
        EXPECTING_KEYWORD,
        EXPECTING_IDENTIFIER,
        AFTER_REQUIRES,
        INACTIVE;

    }

    private class ScanContextDetector
    extends VanguardParser {
        ScanContextDetector(CompilerOptions options) {
            super(new ProblemReporter(DefaultErrorHandlingPolicies.ignoreAllProblems(), options, new DefaultProblemFactory()));
            this.problemReporter.options.performStatementsRecovery = false;
            this.reportSyntaxErrorIsRequired = false;
            this.reportOnlyOneSyntaxError = false;
        }

        @Override
        public void initializeScanner() {
            this.scanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, this.options.taskTags, this.options.taskPriorities, this.options.isTaskCaseSensitive, this.options.enablePreviewFeatures){

                @Override
                void updateScanContext(int token) {
                    if (token != 64) {
                        super.updateScanContext(token);
                    }
                }
            };
            this.scanner.recordLineSeparator = false;
            this.scanner.setActiveParser(this);
            this.scanner.previewEnabled = this.options.enablePreviewFeatures;
        }

        @Override
        public boolean isParsingModuleDeclaration() {
            return true;
        }

        public ScanContext getScanContext(char[] src, int begin) {
            this.scanner.setSource(src);
            this.scanner.resetTo(0, begin);
            this.goForCompilationUnit();
            Goal goal = new Goal(2, null, 0){

                @Override
                boolean hasBeenReached(int act, int token) {
                    return token == 64;
                }
            };
            this.parse(goal);
            return this.scanner.scanContext;
        }
    }

    private static class VanguardParser
    extends Parser {
        public static final boolean SUCCESS = true;
        public static final boolean FAILURE = false;
        Goal currentGoal;

        public VanguardParser(VanguardScanner scanner) {
            this.scanner = scanner;
        }

        public VanguardParser(ProblemReporter reporter) {
            super(reporter, false);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        protected boolean parse(Goal goal) {
            this.currentGoal = goal;
            try {
                int act = 982;
                this.stateStackTop = -1;
                this.currentToken = goal.first;
                while (true) {
                    int stackLength;
                    if (++this.stateStackTop >= (stackLength = this.stack.length)) {
                        this.stack = new int[stackLength + 255];
                        System.arraycopy(this.stack, 0, this.stack, 0, stackLength);
                    }
                    this.stack[this.stateStackTop] = act;
                    if ((act = Parser.tAction(act, this.currentToken)) == 17934) {
                        return false;
                    }
                    if (act <= 919) {
                        --this.stateStackTop;
                    } else if (act > 17934) {
                        this.unstackedAct = act;
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        }
                        finally {
                            this.unstackedAct = 17934;
                        }
                        act -= 17934;
                    } else {
                        if (act >= 17933) {
                            return false;
                        }
                        this.unstackedAct = act;
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        }
                        finally {
                            this.unstackedAct = 17934;
                        }
                        continue;
                    }
                    do {
                        if (goal.hasBeenReached(act, this.currentToken)) {
                            return true;
                        }
                        this.stateStackTop -= Parser.rhs[act] - 1;
                    } while ((act = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act])) <= 919);
                }
            }
            catch (Exception exception) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        }
    }

    private static final class VanguardScanner
    extends Scanner {
        public VanguardScanner(long sourceLevel, long complianceLevel, boolean previewEnabled) {
            super(false, false, false, sourceLevel, complianceLevel, null, null, false, previewEnabled);
        }

        @Override
        public int getNextToken() throws InvalidInputException {
            if (this.nextToken != 0) {
                int token = this.nextToken;
                this.nextToken = 0;
                return token;
            }
            if (this.scanContext == null) {
                this.scanContext = this.isInModuleDeclaration() ? ScanContext.EXPECTING_KEYWORD : ScanContext.INACTIVE;
            }
            int token = this.getNextToken0();
            if (this.areRestrictedModuleKeywordsActive()) {
                if (VanguardScanner.isRestrictedKeyword(token)) {
                    token = this.disambiguatedRestrictedKeyword(token);
                }
                this.updateScanContext(token);
            }
            if (token == 36 && this.atTypeAnnotation()) {
                token = ((VanguardParser)this.activeParser).currentGoal == Goal.LambdaParameterListGoal ? this.disambiguatedToken(token) : 27;
            }
            return token == 64 ? 0 : token;
        }
    }
}

