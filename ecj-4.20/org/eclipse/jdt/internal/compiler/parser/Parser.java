/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ReadManager;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CombinedBinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CompactConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.IntersectionCastTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.TextBlock;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.ast.YieldStatement;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.JavaFeature;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.parser.ConflictedParser;
import org.eclipse.jdt.internal.compiler.parser.JavadocParser;
import org.eclipse.jdt.internal.compiler.parser.ParserBasicInformation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredAnnotation;
import org.eclipse.jdt.internal.compiler.parser.RecoveredBlock;
import org.eclipse.jdt.internal.compiler.parser.RecoveredElement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredField;
import org.eclipse.jdt.internal.compiler.parser.RecoveredInitializer;
import org.eclipse.jdt.internal.compiler.parser.RecoveredLocalVariable;
import org.eclipse.jdt.internal.compiler.parser.RecoveredMethod;
import org.eclipse.jdt.internal.compiler.parser.RecoveredModule;
import org.eclipse.jdt.internal.compiler.parser.RecoveredPackageVisibilityStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredProvidesStatement;
import org.eclipse.jdt.internal.compiler.parser.RecoveredType;
import org.eclipse.jdt.internal.compiler.parser.RecoveredUnit;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScannerData;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
import org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Parser
implements TerminalTokens,
ParserBasicInformation,
ConflictedParser,
OperatorIds,
TypeIds {
    protected static final int THIS_CALL = 3;
    protected static final int SUPER_CALL = 2;
    public static final char[] FALL_THROUGH_TAG = "$FALL-THROUGH$".toCharArray();
    public static final char[] CASES_OMITTED_TAG = "$CASES-OMITTED$".toCharArray();
    public static char[] asb = null;
    public static char[] asr = null;
    protected static final int AstStackIncrement = 100;
    public static char[] base_action = null;
    public static final int BracketKinds = 3;
    public static short[] check_table = null;
    public static final int CurlyBracket = 2;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_AUTOMATON = false;
    private static final String EOF_TOKEN = "$eof";
    private static final String ERROR_TOKEN = "$error";
    protected static final int ExpressionStackIncrement = 100;
    protected static final int GenericsStackIncrement = 10;
    private static final String FILEPREFIX = "parser";
    public static char[] in_symb = null;
    private static final String INVALID_CHARACTER = "Invalid Character";
    public static char[] lhs = null;
    public static String[] name = null;
    public static char[] nasb = null;
    public static char[] nasr = null;
    public static char[] non_terminal_index = null;
    private static final String READABLE_NAMES_FILE = "readableNames";
    public static String[] readableName = null;
    public static byte[] rhs = null;
    public static int[] reverse_index = null;
    public static char[] recovery_templates_index = null;
    public static char[] recovery_templates = null;
    public static char[] statements_recovery_filter = null;
    public static long[] rules_compliance = null;
    public static final int RoundBracket = 0;
    public static char[] scope_la = null;
    public static char[] scope_lhs = null;
    public static char[] scope_prefix = null;
    public static char[] scope_rhs = null;
    public static char[] scope_state = null;
    public static char[] scope_state_set = null;
    public static char[] scope_suffix = null;
    public static final int SquareBracket = 1;
    protected static final int StackIncrement = 255;
    public static char[] term_action = null;
    public static char[] term_check = null;
    public static char[] terminal_index = null;
    private static final String UNEXPECTED_EOF = "Unexpected End Of File";
    public static boolean VERBOSE_RECOVERY = false;
    protected static final int HALT = 0;
    protected static final int RESTART = 1;
    protected static final int RESUME = 2;
    private static final short TYPE_CLASS = 1;
    private static final short TYPE_RECORD = 2;
    public Scanner scanner;
    public int currentToken;
    protected int astLengthPtr;
    protected int[] astLengthStack;
    protected int astPtr;
    protected ASTNode[] astStack = new ASTNode[100];
    protected int patternLengthPtr;
    protected int[] patternLengthStack;
    protected int patternPtr;
    protected ASTNode[] patternStack = new ASTNode[100];
    public CompilationUnitDeclaration compilationUnit;
    protected RecoveredElement currentElement;
    protected boolean diet = false;
    protected int dietInt = 0;
    protected int endPosition;
    protected int endStatementPosition;
    protected int expressionLengthPtr;
    protected int[] expressionLengthStack;
    protected int expressionPtr;
    protected Expression[] expressionStack = new Expression[100];
    protected int rBracketPosition;
    public int firstToken;
    protected int typeAnnotationPtr;
    protected int typeAnnotationLengthPtr;
    protected Annotation[] typeAnnotationStack = new Annotation[100];
    protected int[] typeAnnotationLengthStack;
    protected static final int TypeAnnotationStackIncrement = 100;
    protected int genericsIdentifiersLengthPtr;
    protected int[] genericsIdentifiersLengthStack = new int[10];
    protected int genericsLengthPtr;
    protected int[] genericsLengthStack = new int[10];
    protected int genericsPtr;
    protected ASTNode[] genericsStack = new ASTNode[10];
    protected boolean hasError;
    protected boolean hasReportedError;
    protected int identifierLengthPtr;
    protected int[] identifierLengthStack;
    protected long[] identifierPositionStack;
    protected int identifierPtr;
    protected char[][] identifierStack;
    protected boolean ignoreNextOpeningBrace;
    protected boolean ignoreNextClosingBrace;
    protected int intPtr;
    protected int[] intStack;
    public int lastAct;
    protected int lastCheckPoint;
    protected int lastErrorEndPosition;
    protected int lastErrorEndPositionBeforeRecovery = -1;
    protected int lastIgnoredToken;
    protected int nextIgnoredToken;
    protected int listLength;
    protected int listTypeParameterLength;
    protected int lParenPos;
    protected int rParenPos;
    protected int modifiers;
    protected int modifiersSourceStart;
    protected int annotationAsModifierSourceStart = -1;
    protected int colonColonStart = -1;
    protected int[] nestedMethod;
    protected int forStartPosition = 0;
    protected int nestedType;
    protected int dimensions;
    protected int switchNestingLevel;
    int caseLevel;
    protected int casePtr;
    protected int[] caseStack;
    ASTNode[] noAstNodes = new ASTNode[100];
    public boolean switchWithTry = false;
    Expression[] noExpressions = new Expression[100];
    protected boolean optimizeStringLiterals = true;
    protected CompilerOptions options;
    protected ProblemReporter problemReporter;
    protected int rBraceStart;
    protected int rBraceEnd;
    protected int rBraceSuccessorStart;
    protected int realBlockPtr;
    protected int[] realBlockStack;
    protected int recoveredStaticInitializerStart;
    public ReferenceContext referenceContext;
    public boolean reportOnlyOneSyntaxError = false;
    public boolean reportSyntaxErrorIsRequired = true;
    protected boolean restartRecovery;
    protected boolean annotationRecoveryActivated = true;
    protected int lastPosistion;
    public boolean methodRecoveryActivated = false;
    protected boolean statementRecoveryActivated = false;
    protected TypeDeclaration[] recoveredTypes;
    protected int recoveredTypePtr;
    protected int nextTypeStart;
    protected TypeDeclaration pendingRecoveredType;
    public RecoveryScanner recoveryScanner;
    protected int[] stack = new int[255];
    protected int stateStackTop;
    protected int synchronizedBlockSourceStart;
    protected int[] variablesCounter;
    protected boolean checkExternalizeStrings;
    protected boolean recordStringLiterals;
    public Javadoc javadoc;
    public JavadocParser javadocParser;
    protected int lastJavadocEnd;
    public ReadManager readManager;
    protected int valueLambdaNestDepth = -1;
    private int[] stateStackLengthStack = new int[0];
    protected boolean parsingJava8Plus;
    protected boolean parsingJava9Plus;
    protected boolean parsingJava14Plus;
    protected boolean parsingJava15Plus;
    protected boolean previewEnabled;
    protected boolean parsingJava11Plus;
    protected int unstackedAct = 17934;
    private boolean haltOnSyntaxError = false;
    private boolean tolerateDefaultClassMethods = false;
    private boolean processingLambdaParameterList = false;
    private boolean expectTypeAnnotation = false;
    private boolean reparsingLambdaExpression = false;
    private Map<TypeDeclaration, Integer[]> recordNestedMethodLevels;
    protected boolean caseFlagSet = false;

    static {
        try {
            Parser.initTables();
        }
        catch (IOException ex) {
            throw new ExceptionInInitializerError(ex.getMessage());
        }
    }

    public static int asi(int state) {
        return asb[Parser.original_state(state)];
    }

    public static final short base_check(int i) {
        return check_table[i - 920];
    }

    private static final void buildFile(String filename, List listToDump) {
        block15: {
            BufferedWriter writer = null;
            try {
                try {
                    writer = new BufferedWriter(new FileWriter(filename));
                    Iterator iterator = listToDump.iterator();
                    while (iterator.hasNext()) {
                        writer.write(String.valueOf(iterator.next()));
                    }
                    writer.flush();
                }
                catch (IOException iOException) {
                    if (writer != null) {
                        try {
                            writer.close();
                        }
                        catch (IOException iOException2) {}
                    }
                    break block15;
                }
            }
            catch (Throwable throwable) {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException iOException) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }

    private static void buildFileForCompliance(String file, int length, String[] tokens) {
        byte[] result = new byte[length * 8];
        int i = 0;
        while (i < tokens.length) {
            if ("2".equals(tokens[i])) {
                int index = Integer.parseInt(tokens[i + 1]);
                String token = tokens[i + 2].trim();
                long compliance = 0L;
                if ("1.4".equals(token)) {
                    compliance = 0x300000L;
                } else if ("1.5".equals(token)) {
                    compliance = 0x310000L;
                } else if ("1.6".equals(token)) {
                    compliance = 0x320000L;
                } else if ("1.7".equals(token)) {
                    compliance = 0x330000L;
                } else if ("1.8".equals(token)) {
                    compliance = 0x340000L;
                } else if ("9".equals(token)) {
                    compliance = 0x350000L;
                } else if ("10".equals(token)) {
                    compliance = 0x360000L;
                } else if ("11".equals(token)) {
                    compliance = 0x370000L;
                } else if ("12".equals(token)) {
                    compliance = 0x380000L;
                } else if ("13".equals(token)) {
                    compliance = 0x390000L;
                } else if ("14".equals(token)) {
                    compliance = 0x3A0000L;
                } else if ("15".equals(token)) {
                    compliance = 0x3B0000L;
                } else if ("16".equals(token)) {
                    compliance = 0x3C0000L;
                } else if ("recovery".equals(token)) {
                    compliance = Long.MAX_VALUE;
                }
                int j = index * 8;
                result[j] = (byte)(compliance >>> 56);
                result[j + 1] = (byte)(compliance >>> 48);
                result[j + 2] = (byte)(compliance >>> 40);
                result[j + 3] = (byte)(compliance >>> 32);
                result[j + 4] = (byte)(compliance >>> 24);
                result[j + 5] = (byte)(compliance >>> 16);
                result[j + 6] = (byte)(compliance >>> 8);
                result[j + 7] = (byte)compliance;
            }
            i += 3;
        }
        Parser.buildFileForTable(file, result);
    }

    private static final String[] buildFileForName(String filename, String contents) {
        String[] result = new String[contents.length()];
        result[0] = null;
        int resultCount = 1;
        StringBuffer buffer = new StringBuffer();
        int start = contents.indexOf("name[]");
        start = contents.indexOf(34, start);
        int end = contents.indexOf("};", start);
        contents = contents.substring(start, end);
        boolean addLineSeparator = false;
        int tokenStart = -1;
        StringBuffer currentToken = new StringBuffer();
        int i = 0;
        while (i < contents.length()) {
            char c = contents.charAt(i);
            if (c == '\"') {
                if (tokenStart == -1) {
                    tokenStart = i + 1;
                } else {
                    String token;
                    if (addLineSeparator) {
                        buffer.append('\n');
                        result[resultCount++] = currentToken.toString();
                        currentToken = new StringBuffer();
                    }
                    if ((token = contents.substring(tokenStart, i)).equals(ERROR_TOKEN)) {
                        token = INVALID_CHARACTER;
                    } else if (token.equals(EOF_TOKEN)) {
                        token = UNEXPECTED_EOF;
                    }
                    buffer.append(token);
                    currentToken.append(token);
                    addLineSeparator = true;
                    tokenStart = -1;
                }
            }
            if (tokenStart == -1 && c == '+') {
                addLineSeparator = false;
            }
            ++i;
        }
        if (currentToken.length() > 0) {
            result[resultCount++] = currentToken.toString();
        }
        Parser.buildFileForTable(filename, buffer.toString().toCharArray());
        String[] stringArray = result;
        result = new String[resultCount];
        System.arraycopy(stringArray, 0, result, 0, resultCount);
        return result;
    }

    private static void buildFileForReadableName(String file, char[] newLhs, char[] newNonTerminalIndex, String[] newName, String[] tokens) {
        ArrayList<String> entries = new ArrayList<String>();
        boolean[] alreadyAdded = new boolean[newName.length];
        int i = 0;
        while (i < tokens.length) {
            if ("1".equals(tokens[i])) {
                char index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[i + 1])]];
                StringBuffer buffer = new StringBuffer();
                if (!alreadyAdded[index]) {
                    alreadyAdded[index] = true;
                    buffer.append(newName[index]);
                    buffer.append('=');
                    buffer.append(tokens[i + 2].trim());
                    buffer.append('\n');
                    entries.add(String.valueOf(buffer));
                }
            }
            i += 3;
        }
        i = 1;
        while (!INVALID_CHARACTER.equals(newName[i])) {
            ++i;
        }
        ++i;
        while (i < alreadyAdded.length) {
            if (!alreadyAdded[i]) {
                System.out.println(String.valueOf(newName[i]) + " has no readable name");
            }
            ++i;
        }
        Collections.sort(entries);
        Parser.buildFile(file, entries);
    }

    private static final void buildFileForTable(String filename, byte[] bytes) {
        block14: {
            FileOutputStream stream = null;
            try {
                try {
                    stream = new FileOutputStream(filename);
                    stream.write(bytes);
                }
                catch (IOException iOException) {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (IOException iOException2) {}
                    }
                    break block14;
                }
            }
            catch (Throwable throwable) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }

    private static final void buildFileForTable(String filename, char[] chars) {
        block15: {
            byte[] bytes = new byte[chars.length * 2];
            int i = 0;
            while (i < chars.length) {
                bytes[2 * i] = (byte)(chars[i] >>> 8);
                bytes[2 * i + 1] = (byte)(chars[i] & 0xFF);
                ++i;
            }
            FileOutputStream stream = null;
            try {
                try {
                    stream = new FileOutputStream(filename);
                    stream.write(bytes);
                }
                catch (IOException iOException) {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (IOException iOException2) {}
                    }
                    break block15;
                }
            }
            catch (Throwable throwable) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException iOException) {}
            }
        }
        System.out.println(String.valueOf(filename) + " creation complete");
    }

    private static final byte[] buildFileOfByteFor(String filename, String tag, String[] tokens) {
        String token;
        int i = 0;
        while (!tokens[i++].equals(tag)) {
        }
        byte[] bytes = new byte[tokens.length];
        int ic = 0;
        while (!(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            bytes[ic++] = (byte)c;
        }
        byte[] byArray = bytes;
        bytes = new byte[ic];
        System.arraycopy(byArray, 0, bytes, 0, ic);
        Parser.buildFileForTable(filename, bytes);
        return bytes;
    }

    private static final char[] buildFileOfIntFor(String filename, String tag, String[] tokens) {
        String token;
        int i = 0;
        while (!tokens[i++].equals(tag)) {
        }
        char[] chars = new char[tokens.length];
        int ic = 0;
        while (!(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            chars[ic++] = (char)c;
        }
        char[] cArray = chars;
        chars = new char[ic];
        System.arraycopy(cArray, 0, chars, 0, ic);
        Parser.buildFileForTable(filename, chars);
        return chars;
    }

    private static final void buildFileOfShortFor(String filename, String tag, String[] tokens) {
        String token;
        int i = 0;
        while (!tokens[i++].equals(tag)) {
        }
        char[] chars = new char[tokens.length];
        int ic = 0;
        while (!(token = tokens[i++]).equals("}")) {
            int c = Integer.parseInt(token);
            chars[ic++] = (char)(c + 32768);
        }
        char[] cArray = chars;
        chars = new char[ic];
        System.arraycopy(cArray, 0, chars, 0, ic);
        Parser.buildFileForTable(filename, chars);
    }

    private static void buildFilesForRecoveryTemplates(String indexFilename, String templatesFilename, char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName, char[] newLhs, String[] tokens) {
        int[] newReverse = Parser.computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);
        char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
        char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
        int newRecoveyTemplatesPtr = 0;
        int i = 0;
        while (i < tokens.length) {
            if ("3".equals(tokens[i])) {
                int length = newRecoveyTemplates.length;
                if (length == newRecoveyTemplatesPtr + 1) {
                    char[] cArray = newRecoveyTemplates;
                    newRecoveyTemplates = new char[length * 2];
                    System.arraycopy(cArray, 0, newRecoveyTemplates, 0, length);
                }
                newRecoveyTemplates[newRecoveyTemplatesPtr++] = '\u0000';
                char index = newLhs[Integer.parseInt(tokens[i + 1])];
                newRecoveyTemplatesIndex[index] = (char)newRecoveyTemplatesPtr;
                String token = tokens[i + 2].trim();
                StringTokenizer st = new StringTokenizer(token, " ");
                String[] terminalNames = new String[st.countTokens()];
                int t = 0;
                while (st.hasMoreTokens()) {
                    terminalNames[t++] = st.nextToken();
                }
                int j = 0;
                while (j < terminalNames.length) {
                    int symbol = Parser.getSymbol(terminalNames[j], newName, newReverse);
                    if (symbol > -1) {
                        length = newRecoveyTemplates.length;
                        if (length == newRecoveyTemplatesPtr + 1) {
                            char[] cArray = newRecoveyTemplates;
                            newRecoveyTemplates = new char[length * 2];
                            System.arraycopy(cArray, 0, newRecoveyTemplates, 0, length);
                        }
                        newRecoveyTemplates[newRecoveyTemplatesPtr++] = (char)symbol;
                    }
                    ++j;
                }
            }
            i += 3;
        }
        newRecoveyTemplates[newRecoveyTemplatesPtr++] = '\u0000';
        char[] cArray = newRecoveyTemplates;
        newRecoveyTemplates = new char[newRecoveyTemplatesPtr];
        System.arraycopy(cArray, 0, newRecoveyTemplates, 0, newRecoveyTemplatesPtr);
        Parser.buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
        Parser.buildFileForTable(templatesFilename, newRecoveyTemplates);
    }

    private static void buildFilesForStatementsRecoveryFilter(String filename, char[] newNonTerminalIndex, char[] newLhs, String[] tokens) {
        char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];
        int i = 0;
        while (i < tokens.length) {
            if ("4".equals(tokens[i])) {
                char index = newLhs[Integer.parseInt(tokens[i + 1])];
                newStatementsRecoveryFilter[index] = '\u0001';
            }
            i += 3;
        }
        Parser.buildFileForTable(filename, newStatementsRecoveryFilter);
    }

    public static final void buildFilesFromLPG(String dataFilename, String dataFilename2) {
        char[] contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename), null);
        }
        catch (IOException iOException) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        StringTokenizer st = new StringTokenizer(new String(contents), " \t\n\r[]={,;");
        String[] tokens = new String[st.countTokens()];
        int j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        int i = 0;
        char[] newLhs = Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "lhs", tokens);
        Parser.buildFileOfShortFor(FILEPREFIX + ++i + ".rsc", "check_table", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "asb", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "asr", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "nasb", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "nasr", tokens);
        char[] newTerminalIndex = Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "terminal_index", tokens);
        char[] newNonTerminalIndex = Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "non_terminal_index", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "term_action", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_prefix", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_suffix", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_lhs", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_state_set", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_rhs", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_state", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "in_symb", tokens);
        byte[] newRhs = Parser.buildFileOfByteFor(FILEPREFIX + ++i + ".rsc", "rhs", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "term_check", tokens);
        Parser.buildFileOfIntFor(FILEPREFIX + ++i + ".rsc", "scope_la", tokens);
        String[] newName = Parser.buildFileForName(FILEPREFIX + ++i + ".rsc", new String(contents));
        contents = CharOperation.NO_CHAR;
        try {
            contents = Util.getFileCharContent(new File(dataFilename2), null);
        }
        catch (IOException iOException) {
            System.out.println(Messages.parser_incorrectPath);
            return;
        }
        st = new StringTokenizer(new String(contents), "\t\n\r#");
        tokens = new String[st.countTokens()];
        j = 0;
        while (st.hasMoreTokens()) {
            tokens[j++] = st.nextToken();
        }
        Parser.buildFileForCompliance(FILEPREFIX + ++i + ".rsc", newRhs.length, tokens);
        Parser.buildFileForReadableName("readableNames.props", newLhs, newNonTerminalIndex, newName, tokens);
        Parser.buildFilesForRecoveryTemplates(FILEPREFIX + ++i + ".rsc", FILEPREFIX + ++i + ".rsc", newTerminalIndex, newNonTerminalIndex, newName, newLhs, tokens);
        Parser.buildFilesForStatementsRecoveryFilter(FILEPREFIX + ++i + ".rsc", newNonTerminalIndex, newLhs, tokens);
        System.out.println(Messages.parser_moveFiles);
    }

    protected static int[] computeReverseTable(char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName) {
        int[] newReverseTable = new int[newName.length];
        int j = 0;
        while (j < newName.length) {
            block5: {
                int k = 0;
                while (k < newTerminalIndex.length) {
                    if (newTerminalIndex[k] == j) {
                        newReverseTable[j] = k;
                        break block5;
                    }
                    ++k;
                }
                k = 0;
                while (k < newNonTerminalIndex.length) {
                    if (newNonTerminalIndex[k] == j) {
                        newReverseTable[j] = -k;
                        break;
                    }
                    ++k;
                }
            }
            ++j;
        }
        return newReverseTable;
    }

    private static int getSymbol(String terminalName, String[] newName, int[] newReverse) {
        int j = 0;
        while (j < newName.length) {
            if (terminalName.equals(newName[j])) {
                return newReverse[j];
            }
            ++j;
        }
        return -1;
    }

    public static int in_symbol(int state) {
        return in_symb[Parser.original_state(state)];
    }

    public static final void initTables() throws IOException {
        int i = 0;
        lhs = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        char[] chars = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        check_table = new short[chars.length];
        int c = chars.length;
        while (c-- > 0) {
            Parser.check_table[c] = (short)(chars[c] - 32768);
        }
        asb = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        asr = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        nasb = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        nasr = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        terminal_index = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        non_terminal_index = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        term_action = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_prefix = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_suffix = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_lhs = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_state_set = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_rhs = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_state = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        in_symb = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        rhs = Parser.readByteTable(FILEPREFIX + ++i + ".rsc");
        term_check = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        scope_la = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        name = Parser.readNameTable(FILEPREFIX + ++i + ".rsc");
        rules_compliance = Parser.readLongTable(FILEPREFIX + ++i + ".rsc");
        readableName = Parser.readReadableNameTable("readableNames.props");
        reverse_index = Parser.computeReverseTable(terminal_index, non_terminal_index, name);
        recovery_templates_index = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        recovery_templates = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        statements_recovery_filter = Parser.readTable(FILEPREFIX + ++i + ".rsc");
        base_action = lhs;
    }

    public static int nasi(int state) {
        return nasb[Parser.original_state(state)];
    }

    public static int ntAction(int state, int sym) {
        return base_action[state + sym];
    }

    protected static int original_state(int state) {
        return -Parser.base_check(state);
    }

    protected static byte[] readByteTable(String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        catch (Throwable throwable) {
            try {
                stream.close();
            }
            catch (IOException iOException) {}
            throw throwable;
        }
        try {
            stream.close();
        }
        catch (IOException iOException) {}
        return bytes;
    }

    protected static long[] readLongTable(String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        catch (Throwable throwable) {
            try {
                stream.close();
            }
            catch (IOException iOException) {}
            throw throwable;
        }
        try {
            stream.close();
        }
        catch (IOException iOException) {}
        int length = bytes.length;
        if (length % 8 != 0) {
            throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        }
        long[] longs = new long[length / 8];
        int i = 0;
        int longIndex = 0;
        do {
            longs[longIndex++] = ((long)(bytes[i++] & 0xFF) << 56) + ((long)(bytes[i++] & 0xFF) << 48) + ((long)(bytes[i++] & 0xFF) << 40) + ((long)(bytes[i++] & 0xFF) << 32) + ((long)(bytes[i++] & 0xFF) << 24) + ((long)(bytes[i++] & 0xFF) << 16) + ((long)(bytes[i++] & 0xFF) << 8) + (long)(bytes[i++] & 0xFF);
        } while (i != length);
        return longs;
    }

    protected static String[] readNameTable(String filename) throws IOException {
        char[] contents = Parser.readTable(filename);
        char[][] nameAsChar = CharOperation.splitOn('\n', contents);
        String[] result = new String[nameAsChar.length + 1];
        result[0] = null;
        int i = 0;
        while (i < nameAsChar.length) {
            result[i + 1] = new String(nameAsChar[i]);
            ++i;
        }
        return result;
    }

    protected static String[] readReadableNameTable(String filename) {
        String[] result = new String[name.length];
        Properties props = new Properties();
        try {
            Throwable throwable = null;
            Object var4_6 = null;
            try (InputStream is = Parser.class.getResourceAsStream(filename);){
                props.load(is);
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException iOException) {
            result = name;
            return result;
        }
        System.arraycopy(name, 0, result, 0, 136);
        int i = 135;
        while (i < name.length) {
            String n = props.getProperty(name[i]);
            result[i] = n != null && n.length() > 0 ? n : name[i];
            ++i;
        }
        return result;
    }

    protected static char[] readTable(String filename) throws IOException {
        InputStream stream = Parser.class.getResourceAsStream(filename);
        if (stream == null) {
            throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
        }
        byte[] bytes = null;
        try {
            stream = new BufferedInputStream(stream);
            bytes = Util.getInputStreamAsByteArray(stream, -1);
        }
        catch (Throwable throwable) {
            try {
                stream.close();
            }
            catch (IOException iOException) {}
            throw throwable;
        }
        try {
            stream.close();
        }
        catch (IOException iOException) {}
        int length = bytes.length;
        if ((length & 1) != 0) {
            throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
        }
        char[] chars = new char[length / 2];
        int i = 0;
        int charIndex = 0;
        do {
            chars[charIndex++] = (char)(((bytes[i++] & 0xFF) << 8) + (bytes[i++] & 0xFF));
        } while (i != length);
        return chars;
    }

    public static int tAction(int state, int sym) {
        return term_action[term_check[base_action[state] + sym] == sym ? base_action[state] + sym : base_action[state]];
    }

    protected int actFromTokenOrSynthetic(int previousAct) {
        return Parser.tAction(previousAct, this.currentToken);
    }

    public Parser() {
    }

    public Parser(ProblemReporter problemReporter, boolean optimizeStringLiterals) {
        this.problemReporter = problemReporter;
        this.options = problemReporter.options;
        this.optimizeStringLiterals = optimizeStringLiterals;
        this.initializeScanner();
        this.parsingJava8Plus = this.options.sourceLevel >= 0x340000L;
        this.parsingJava9Plus = this.options.sourceLevel >= 0x350000L;
        this.parsingJava11Plus = this.options.sourceLevel >= 0x370000L;
        this.parsingJava14Plus = this.options.sourceLevel >= 0x3A0000L;
        this.parsingJava15Plus = this.options.sourceLevel >= 0x3B0000L;
        this.previewEnabled = this.options.sourceLevel == ClassFileConstants.getLatestJDKLevel() && this.options.enablePreviewFeatures;
        this.astLengthStack = new int[50];
        this.patternLengthStack = new int[20];
        this.expressionLengthStack = new int[30];
        this.typeAnnotationLengthStack = new int[30];
        this.intStack = new int[50];
        this.caseStack = new int[16];
        this.identifierStack = new char[30][];
        this.identifierLengthStack = new int[30];
        this.nestedMethod = new int[30];
        this.realBlockStack = new int[30];
        this.identifierPositionStack = new long[30];
        this.variablesCounter = new int[30];
        this.recordNestedMethodLevels = new HashMap<TypeDeclaration, Integer[]>();
        this.javadocParser = this.createJavadocParser();
    }

    protected void annotationRecoveryCheckPoint(int start, int end) {
        if (this.lastCheckPoint < end) {
            this.lastCheckPoint = end + 1;
        }
    }

    public void arrayInitializer(int length) {
        ArrayInitializer ai = new ArrayInitializer();
        if (length != 0) {
            this.expressionPtr -= length;
            ai.expressions = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ai.expressions, 0, length);
        }
        this.pushOnExpressionStack(ai);
        ai.sourceEnd = this.endStatementPosition;
        ai.sourceStart = this.intStack[this.intPtr--];
    }

    protected void blockReal() {
        int n = this.realBlockPtr;
        this.realBlockStack[n] = this.realBlockStack[n] + 1;
    }

    public RecoveredElement buildInitialRecoveryState() {
        this.lastCheckPoint = 0;
        this.lastErrorEndPositionBeforeRecovery = this.scanner.currentPosition;
        RecoveredElement element = null;
        if (this.referenceContext instanceof CompilationUnitDeclaration) {
            element = new RecoveredUnit(this.compilationUnit, 0, this);
            this.compilationUnit.currentPackage = null;
            this.compilationUnit.imports = null;
            this.compilationUnit.types = null;
            this.currentToken = 0;
            this.listLength = 0;
            this.listTypeParameterLength = 0;
            this.endPosition = 0;
            this.endStatementPosition = 0;
            return element;
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            element = new RecoveredMethod((AbstractMethodDeclaration)this.referenceContext, null, 0, this);
            this.lastCheckPoint = ((AbstractMethodDeclaration)this.referenceContext).bodyStart;
            if (this.statementRecoveryActivated) {
                element = element.add(new Block(0), 0);
            }
        } else if (this.referenceContext instanceof TypeDeclaration) {
            TypeDeclaration type = (TypeDeclaration)this.referenceContext;
            FieldDeclaration[] fieldDeclarations = type.fields;
            int length = fieldDeclarations == null ? 0 : fieldDeclarations.length;
            int i = 0;
            while (i < length) {
                FieldDeclaration field = fieldDeclarations[i];
                if (field != null && field.getKind() == 2 && ((Initializer)field).block != null && field.declarationSourceStart <= this.scanner.initialPosition && this.scanner.initialPosition <= field.declarationSourceEnd && this.scanner.eofPosition <= field.declarationSourceEnd + 1) {
                    element = new RecoveredInitializer(field, null, 1, this);
                    this.lastCheckPoint = field.declarationSourceStart;
                    break;
                }
                ++i;
            }
        }
        if (element == null) {
            return element;
        }
        int i = 0;
        while (i <= this.astPtr) {
            ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration) {
                AbstractMethodDeclaration method = (AbstractMethodDeclaration)node;
                if (method.declarationSourceEnd == 0) {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.bodyStart;
                } else {
                    element = element.add(method, 0);
                    this.lastCheckPoint = method.declarationSourceEnd + 1;
                }
            } else if (node instanceof Initializer) {
                Initializer initializer = (Initializer)node;
                if (initializer.block != null) {
                    if (initializer.declarationSourceEnd == 0) {
                        element = element.add(initializer, 1);
                        this.lastCheckPoint = initializer.sourceStart;
                    } else {
                        element = element.add(initializer, 0);
                        this.lastCheckPoint = initializer.declarationSourceEnd + 1;
                    }
                }
            } else if (node instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration)node;
                if (field.declarationSourceEnd == 0) {
                    element = element.add(field, 0);
                    this.lastCheckPoint = field.initialization == null ? field.sourceEnd + 1 : field.initialization.sourceEnd + 1;
                } else {
                    element = element.add(field, 0);
                    this.lastCheckPoint = field.declarationSourceEnd + 1;
                }
            } else if (node instanceof TypeDeclaration) {
                TypeDeclaration type = (TypeDeclaration)node;
                if ((type.modifiers & 0x4000) == 0) {
                    if (type.declarationSourceEnd == 0) {
                        element = element.add(type, 0);
                        this.lastCheckPoint = type.bodyStart;
                    } else {
                        element = element.add(type, 0);
                        this.lastCheckPoint = type.declarationSourceEnd + 1;
                    }
                }
            } else {
                if (node instanceof ImportReference) {
                    ImportReference importRef = (ImportReference)node;
                    element = element.add(importRef, 0);
                    this.lastCheckPoint = importRef.declarationSourceEnd + 1;
                }
                if (this.statementRecoveryActivated) {
                    if (node instanceof Block) {
                        Block block = (Block)node;
                        element = element.add(block, 0);
                        this.lastCheckPoint = block.sourceEnd + 1;
                    } else if (node instanceof LocalDeclaration) {
                        LocalDeclaration statement = (LocalDeclaration)node;
                        element = element.add(statement, 0);
                        this.lastCheckPoint = statement.sourceEnd + 1;
                    } else if (node instanceof Expression && ((Expression)node).isTrulyExpression()) {
                        if (node instanceof Assignment || node instanceof PrefixExpression || node instanceof PostfixExpression || node instanceof MessageSend || node instanceof AllocationExpression) {
                            Expression statement = (Expression)node;
                            element = element.add(statement, 0);
                            this.lastCheckPoint = statement.statementEnd != -1 ? statement.statementEnd + 1 : statement.sourceEnd + 1;
                        }
                    } else if (node instanceof Statement) {
                        Statement statement = (Statement)node;
                        element = element.add(statement, 0);
                        this.lastCheckPoint = statement.sourceEnd + 1;
                    }
                }
            }
            ++i;
        }
        if (this.statementRecoveryActivated && this.pendingRecoveredType != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
            element = element.add(this.pendingRecoveredType, 0);
            this.lastCheckPoint = this.pendingRecoveredType.declarationSourceEnd + 1;
            this.pendingRecoveredType = null;
        }
        return element;
    }

    protected void checkAndSetModifiers(int flag) {
        if ((this.modifiers & flag) != 0) {
            this.modifiers |= 0x400000;
        }
        this.modifiers |= flag;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = this.scanner.startPosition;
        }
        if (this.currentElement != null) {
            this.currentElement.addModifier(flag, this.modifiersSourceStart);
        }
        if (flag == 0x10000000 || flag == 0x4000000) {
            this.problemReporter().validateJavaFeatureSupport(JavaFeature.SEALED_CLASSES, this.scanner.startPosition, this.scanner.currentPosition - 1);
        }
    }

    public void checkComment() {
        if (!(this.diet && this.dietInt == 0 || this.scanner.commentPtr < 0)) {
            this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        }
        int lastComment = this.scanner.commentPtr;
        if (this.modifiersSourceStart >= 0 && this.modifiersSourceStart > this.annotationAsModifierSourceStart) {
            while (lastComment >= 0) {
                int commentSourceStart = this.scanner.commentStarts[lastComment];
                if (commentSourceStart < 0) {
                    commentSourceStart = -commentSourceStart;
                }
                if (commentSourceStart <= this.modifiersSourceStart) break;
                --lastComment;
            }
        }
        if (lastComment >= 0) {
            int lastCommentStart = this.scanner.commentStarts[0];
            if (lastCommentStart < 0) {
                lastCommentStart = -lastCommentStart;
            }
            if (this.forStartPosition != 0 || this.forStartPosition < lastCommentStart) {
                this.modifiersSourceStart = lastCommentStart;
            }
            while (lastComment >= 0 && this.scanner.commentStops[lastComment] < 0) {
                --lastComment;
            }
            if (lastComment >= 0 && this.javadocParser != null) {
                int commentEnd = this.scanner.commentStops[lastComment] - 1;
                this.javadocParser.reportProblems = this.javadocParser.shouldReportProblems ? this.currentElement == null || commentEnd > this.lastJavadocEnd : false;
                if (this.javadocParser.checkDeprecation(lastComment)) {
                    this.checkAndSetModifiers(0x100000);
                }
                this.javadoc = this.javadocParser.docComment;
                if (this.currentElement == null) {
                    this.lastJavadocEnd = commentEnd;
                }
            }
        }
    }

    protected void checkNonNLSAfterBodyEnd(int declarationEnd) {
        if (this.scanner.currentPosition - 1 <= declarationEnd) {
            this.scanner.eofPosition = declarationEnd < Integer.MAX_VALUE ? declarationEnd + 1 : declarationEnd;
            try {
                while (this.scanner.getNextToken() != 64) {
                }
            }
            catch (InvalidInputException invalidInputException) {}
        }
    }

    protected void classInstanceCreation(boolean isQualified) {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            AllocationExpression alloc = this.newAllocationExpression(isQualified);
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                alloc.arguments = new Expression[length];
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments, 0, length);
            }
            alloc.type = this.getTypeReference(0);
            this.checkForDiamond(alloc.type);
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        } else {
            this.dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (anonymousTypeDeclaration.allocation != null) {
                anonymousTypeDeclaration.allocation.sourceEnd = this.endStatementPosition;
                this.checkForDiamond(anonymousTypeDeclaration.allocation.type);
            }
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= 8;
            }
            --this.astPtr;
            --this.astLengthPtr;
        }
    }

    protected AllocationExpression newAllocationExpression(boolean isQualified) {
        AllocationExpression alloc = isQualified ? new QualifiedAllocationExpression() : new AllocationExpression();
        return alloc;
    }

    protected void checkForDiamond(TypeReference allocType) {
        if (allocType instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference type = (ParameterizedSingleTypeReference)allocType;
            if (type.typeArguments == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < 0x330000L) {
                    this.problemReporter().diamondNotBelow17(allocType);
                }
                if (this.options.sourceLevel > 0x300000L) {
                    type.bits |= 0x80000;
                }
            }
        } else if (allocType instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference type = (ParameterizedQualifiedTypeReference)allocType;
            if (type.typeArguments[type.typeArguments.length - 1] == TypeReference.NO_TYPE_ARGUMENTS) {
                if (this.options.sourceLevel < 0x330000L) {
                    this.problemReporter().diamondNotBelow17(allocType, type.typeArguments.length - 1);
                }
                if (this.options.sourceLevel > 0x300000L) {
                    type.bits |= 0x80000;
                }
            }
        }
    }

    protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(TypeReference rightSide, int dim, Annotation[][] annotationsOnDimensions) {
        int nameSize;
        int tokensSize = nameSize = this.identifierLengthStack[this.identifierLengthPtr];
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            ++tokensSize;
        } else if (rightSide instanceof SingleTypeReference) {
            ++tokensSize;
        } else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
        } else if (rightSide instanceof QualifiedTypeReference) {
            tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
        }
        TypeReference[][] typeArguments = new TypeReference[tokensSize][];
        char[][] tokens = new char[tokensSize][];
        long[] positions = new long[tokensSize];
        Annotation[][] typeAnnotations = null;
        if (rightSide instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference)rightSide;
            tokens[nameSize] = singleParameterizedTypeReference.token;
            positions[nameSize] = ((long)singleParameterizedTypeReference.sourceStart << 32) + (long)singleParameterizedTypeReference.sourceEnd;
            typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
            if (singleParameterizedTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleParameterizedTypeReference.annotations[0];
            }
        } else if (rightSide instanceof SingleTypeReference) {
            SingleTypeReference singleTypeReference = (SingleTypeReference)rightSide;
            tokens[nameSize] = singleTypeReference.token;
            positions[nameSize] = ((long)singleTypeReference.sourceStart << 32) + (long)singleTypeReference.sourceEnd;
            if (singleTypeReference.annotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                typeAnnotations[nameSize] = singleTypeReference.annotations[0];
            }
        } else if (rightSide instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference)rightSide;
            TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
            System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
            char[][] rightSideTokens = parameterizedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
            Annotation[][] rightSideAnnotations = parameterizedTypeReference.annotations;
            if (rightSideAnnotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations, 0, typeAnnotations, nameSize, rightSideAnnotations.length);
            }
        } else if (rightSide instanceof QualifiedTypeReference) {
            QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference)rightSide;
            char[][] rightSideTokens = qualifiedTypeReference.tokens;
            System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
            long[] rightSidePositions = qualifiedTypeReference.sourcePositions;
            System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
            Annotation[][] rightSideAnnotations = qualifiedTypeReference.annotations;
            if (rightSideAnnotations != null) {
                typeAnnotations = new Annotation[tokensSize][];
                System.arraycopy(rightSideAnnotations, 0, typeAnnotations, nameSize, rightSideAnnotations.length);
            }
        }
        int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] currentTypeArguments = new TypeReference[currentTypeArgumentsLength];
        this.genericsPtr -= currentTypeArgumentsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, currentTypeArguments, 0, currentTypeArgumentsLength);
        if (nameSize == 1) {
            tokens[0] = this.identifierStack[this.identifierPtr];
            positions[0] = this.identifierPositionStack[this.identifierPtr--];
            typeArguments[0] = currentTypeArguments;
        } else {
            this.identifierPtr -= nameSize;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, nameSize);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, nameSize);
            typeArguments[nameSize - 1] = currentTypeArguments;
        }
        --this.identifierLengthPtr;
        ParameterizedQualifiedTypeReference typeRef = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, annotationsOnDimensions, positions);
        while (nameSize > 0) {
            int length;
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (typeAnnotations == null) {
                    typeAnnotations = new Annotation[tokensSize][];
                }
                Annotation[] annotationArray = new Annotation[length];
                typeAnnotations[nameSize - 1] = annotationArray;
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, annotationArray, 0, length);
                if (nameSize == 1) {
                    typeRef.sourceStart = typeAnnotations[0][0].sourceStart;
                }
            }
            --nameSize;
        }
        typeRef.annotations = typeAnnotations;
        if (typeAnnotations != null) {
            typeRef.bits |= 0x100000;
        }
        return typeRef;
    }

    protected void concatExpressionLists() {
        this.expressionLengthStack[--this.expressionLengthPtr] = this.expressionLengthStack[this.expressionLengthPtr] + 1;
    }

    protected void concatGenericsLists() {
        int n = this.genericsLengthPtr - 1;
        this.genericsLengthStack[n] = this.genericsLengthStack[n] + this.genericsLengthStack[this.genericsLengthPtr--];
    }

    protected void concatNodeLists() {
        int n = this.astLengthPtr - 1;
        this.astLengthStack[n] = this.astLengthStack[n] + this.astLengthStack[this.astLengthPtr--];
    }

    protected void consumeAdditionalBound() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeAdditionalBound1() {
    }

    protected void consumeAdditionalBoundList() {
        this.concatGenericsLists();
    }

    protected void consumeAdditionalBoundList1() {
        this.concatGenericsLists();
    }

    protected boolean isIndirectlyInsideLambdaExpression() {
        return false;
    }

    protected void consumeAllocationHeader() {
        if (this.currentElement == null) {
            return;
        }
        if (this.currentToken == 38) {
            TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            anonymousType.bits |= 0x300;
            anonymousType.declarationSourceStart = anonymousType.sourceStart = this.intStack[this.intPtr--];
            anonymousType.sourceEnd = this.rParenPos;
            QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
            alloc.type = this.getTypeReference(0);
            alloc.sourceStart = anonymousType.sourceStart;
            alloc.sourceEnd = anonymousType.sourceEnd;
            this.lastCheckPoint = anonymousType.bodyStart = this.scanner.currentPosition;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            this.lastIgnoredToken = -1;
            if (this.isIndirectlyInsideLambdaExpression()) {
                this.ignoreNextOpeningBrace = true;
            } else {
                this.currentToken = 0;
            }
            return;
        }
        this.lastCheckPoint = this.scanner.startPosition;
        this.restartRecovery = true;
    }

    protected void consumeAnnotationAsModifier() {
        Expression expression = this.expressionStack[this.expressionPtr];
        int sourceStart = expression.sourceStart;
        if (this.modifiersSourceStart < 0) {
            this.modifiersSourceStart = sourceStart;
            this.annotationAsModifierSourceStart = sourceStart;
        }
    }

    protected void consumeAnnotationName() {
        if (this.currentElement != null && !this.expectTypeAnnotation) {
            int start = this.intStack[this.intPtr];
            int end = (int)(this.identifierPositionStack[this.identifierPtr] & 0xFFFFFFFFL);
            this.annotationRecoveryCheckPoint(start, end);
            if (this.annotationRecoveryActivated) {
                this.currentElement = this.currentElement.addAnnotationName(this.identifierPtr, this.identifierLengthPtr, start, 0);
            }
        }
        this.recordStringLiterals = false;
        this.expectTypeAnnotation = false;
    }

    protected void consumeAnnotationTypeDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.checkConstructors(this);
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= 1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= 8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeAnnotationTypeDeclarationHeader() {
        TypeDeclaration annotationTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            annotationTypeDeclaration.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }

    protected void consumeAnnotationTypeDeclarationHeaderName() {
        int length;
        TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                annotationTypeDeclaration.bits |= 0x400;
            }
        } else {
            annotationTypeDeclaration.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int)pos;
        annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        --this.intPtr;
        --this.intPtr;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | 0x2000 | 0x200;
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            --this.intPtr;
        } else {
            int atPosition;
            annotationTypeDeclaration.declarationSourceStart = atPosition = this.intStack[this.intPtr--];
        }
        if ((annotationTypeDeclaration.bits & 0x400) == 0 && (annotationTypeDeclaration.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
            annotationTypeDeclaration.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            annotationTypeDeclaration.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, annotationTypeDeclaration.annotations, 0, length);
        }
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.sourceEnd + 1;
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        this.pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters() {
        TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        annotationTypeDeclaration.typeParameters = new TypeParameter[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, annotationTypeDeclaration.typeParameters, 0, length);
        this.problemReporter().invalidUsageOfTypeParametersForAnnotationDeclaration(annotationTypeDeclaration);
        annotationTypeDeclaration.bodyStart = annotationTypeDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                annotationTypeDeclaration.bits |= 0x400;
            }
        } else {
            annotationTypeDeclaration.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        annotationTypeDeclaration.sourceEnd = (int)pos;
        annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
        annotationTypeDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        --this.intPtr;
        --this.intPtr;
        annotationTypeDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        annotationTypeDeclaration.modifiers = this.intStack[this.intPtr--] | 0x2000 | 0x200;
        if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
            annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
            --this.intPtr;
        } else {
            int atPosition;
            annotationTypeDeclaration.declarationSourceStart = atPosition = this.intStack[this.intPtr--];
        }
        if ((annotationTypeDeclaration.bits & 0x400) == 0 && (annotationTypeDeclaration.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())) {
            annotationTypeDeclaration.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            annotationTypeDeclaration.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, annotationTypeDeclaration.annotations, 0, length);
        }
        annotationTypeDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
        this.pushOnAstStack(annotationTypeDeclaration);
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeAnnotationTypeMemberDeclaration() {
        MethodDeclaration annotationTypeMemberDeclaration = (MethodDeclaration)this.astStack[this.astPtr];
        annotationTypeMemberDeclaration.modifiers |= 0x1000000;
        int declarationEndPosition = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        annotationTypeMemberDeclaration.bodyStart = this.endStatementPosition;
        annotationTypeMemberDeclaration.bodyEnd = declarationEndPosition;
        annotationTypeMemberDeclaration.declarationSourceEnd = declarationEndPosition;
    }

    protected void consumeAnnotationTypeMemberDeclarations() {
        this.concatNodeLists();
    }

    protected void consumeAnnotationTypeMemberDeclarationsopt() {
        --this.nestedType;
    }

    protected void consumeArgumentList() {
        this.concatExpressionLists();
    }

    protected void consumeArguments() {
        this.pushOnIntStack(this.rParenPos);
    }

    protected void consumeArrayAccess(boolean unspecifiedReference) {
        ArrayReference exp;
        if (unspecifiedReference) {
            this.expressionStack[this.expressionPtr] = new ArrayReference(this.getUnspecifiedReferenceOptimized(), this.expressionStack[this.expressionPtr]);
            exp = this.expressionStack[this.expressionPtr];
        } else {
            --this.expressionPtr;
            --this.expressionLengthPtr;
            this.expressionStack[this.expressionPtr] = new ArrayReference(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
            exp = this.expressionStack[this.expressionPtr];
        }
        exp.sourceEnd = this.endStatementPosition;
    }

    protected void consumeArrayCreationExpressionWithInitializer() {
        ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        --this.expressionLengthPtr;
        arrayAllocation.initializer = (ArrayInitializer)this.expressionStack[this.expressionPtr--];
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        this.expressionPtr -= length;
        arrayAllocation.dimensions = new Expression[length];
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions, 0, length);
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = this.getTypeReference(0);
        arrayAllocation.type.bits |= 0x40000000;
        if (annotationsOnDimensions != null) {
            arrayAllocation.bits |= 0x100000;
            arrayAllocation.type.bits |= 0x100000;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        arrayAllocation.sourceEnd = arrayAllocation.initializer == null ? this.endStatementPosition : arrayAllocation.initializer.sourceEnd;
        this.pushOnExpressionStack(arrayAllocation);
    }

    protected void consumeArrayCreationExpressionWithoutInitializer() {
        ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        this.expressionPtr -= length;
        arrayAllocation.dimensions = new Expression[length];
        System.arraycopy(this.expressionStack, this.expressionPtr + 1, arrayAllocation.dimensions, 0, length);
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(length);
        arrayAllocation.annotationsOnDimensions = annotationsOnDimensions;
        arrayAllocation.type = this.getTypeReference(0);
        arrayAllocation.type.bits |= 0x40000000;
        if (annotationsOnDimensions != null) {
            arrayAllocation.bits |= 0x100000;
            arrayAllocation.type.bits |= 0x100000;
        }
        arrayAllocation.sourceStart = this.intStack[this.intPtr--];
        arrayAllocation.sourceEnd = arrayAllocation.initializer == null ? this.endStatementPosition : arrayAllocation.initializer.sourceEnd;
        this.pushOnExpressionStack(arrayAllocation);
    }

    protected void consumeArrayCreationHeader() {
    }

    protected void consumeArrayInitializer() {
        this.arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }

    protected void consumeArrayTypeWithTypeArgumentsName() {
        int n = this.genericsIdentifiersLengthPtr;
        this.genericsIdentifiersLengthStack[n] = this.genericsIdentifiersLengthStack[n] + this.identifierLengthStack[this.identifierLengthPtr];
        this.pushOnGenericsLengthStack(0);
    }

    protected void consumeAssertStatement() {
        this.expressionLengthPtr -= 2;
        this.pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeAssignment() {
        int op = this.intStack[this.intPtr--];
        --this.expressionPtr;
        --this.expressionLengthPtr;
        Expression expression = this.expressionStack[this.expressionPtr + 1];
        Expression expression2 = this.expressionStack[this.expressionPtr] = op != 30 ? new CompoundAssignment(this.expressionStack[this.expressionPtr], expression, op, expression.sourceEnd) : new Assignment(this.expressionStack[this.expressionPtr], expression, expression.sourceEnd);
        if (this.pendingRecoveredType != null) {
            if (this.pendingRecoveredType.allocation != null && this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd) {
                this.expressionStack[this.expressionPtr] = this.pendingRecoveredType.allocation;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }

    protected void consumeAssignmentOperator(int pos) {
        this.pushOnIntStack(pos);
    }

    protected void consumeBinaryExpression(int op) {
        --this.expressionPtr;
        --this.expressionLengthPtr;
        Expression expr1 = this.expressionStack[this.expressionPtr];
        Expression expr2 = this.expressionStack[this.expressionPtr + 1];
        switch (op) {
            case 1: {
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            }
            case 0: {
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            }
            case 14: {
                if (this.optimizeStringLiterals) {
                    if (expr1 instanceof StringLiteral) {
                        if ((expr1.bits & 0x1FE00000) >> 21 == 0) {
                            if (expr2 instanceof CharLiteral) {
                                this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
                                break;
                            }
                            if (expr2 instanceof StringLiteral) {
                                this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
                                break;
                            }
                            this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                            break;
                        }
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                        break;
                    }
                    if (expr1 instanceof CombinedBinaryExpression) {
                        CombinedBinaryExpression cursor = (CombinedBinaryExpression)expr1;
                        if (cursor.arity < cursor.arityMax) {
                            cursor.left = new BinaryExpression(cursor);
                            ++cursor.arity;
                        } else {
                            cursor.left = new CombinedBinaryExpression(cursor);
                            cursor.arity = 0;
                            cursor.tuneArityMax();
                        }
                        cursor.right = expr2;
                        cursor.sourceEnd = expr2.sourceEnd;
                        this.expressionStack[this.expressionPtr] = cursor;
                        break;
                    }
                    if (expr1 instanceof BinaryExpression && (expr1.bits & 0x3F00) >> 8 == 14) {
                        this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, 14, 1);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                if (expr1 instanceof StringLiteral) {
                    if (expr2 instanceof StringLiteral && (expr1.bits & 0x1FE00000) >> 21 == 0) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                if (expr1 instanceof CombinedBinaryExpression) {
                    CombinedBinaryExpression cursor = (CombinedBinaryExpression)expr1;
                    if (cursor.arity < cursor.arityMax) {
                        cursor.left = new BinaryExpression(cursor);
                        cursor.bits &= 0xE01FFFFF;
                        ++cursor.arity;
                    } else {
                        cursor.left = new CombinedBinaryExpression(cursor);
                        cursor.bits &= 0xE01FFFFF;
                        cursor.arity = 0;
                        cursor.tuneArityMax();
                    }
                    cursor.right = expr2;
                    cursor.sourceEnd = expr2.sourceEnd;
                    this.expressionStack[this.expressionPtr] = cursor;
                    break;
                }
                if (expr1 instanceof BinaryExpression && (expr1.bits & 0x3F00) >> 8 == 14) {
                    this.expressionStack[this.expressionPtr] = new CombinedBinaryExpression(expr1, expr2, 14, 1);
                    break;
                }
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                break;
            }
            case 4: 
            case 15: {
                --this.intPtr;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
            default: {
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
            }
        }
    }

    protected void consumeBinaryExpressionWithName(int op) {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        --this.expressionPtr;
        --this.expressionLengthPtr;
        Expression expr1 = this.expressionStack[this.expressionPtr + 1];
        Expression expr2 = this.expressionStack[this.expressionPtr];
        switch (op) {
            case 1: {
                this.expressionStack[this.expressionPtr] = new OR_OR_Expression(expr1, expr2, op);
                break;
            }
            case 0: {
                this.expressionStack[this.expressionPtr] = new AND_AND_Expression(expr1, expr2, op);
                break;
            }
            case 14: {
                if (this.optimizeStringLiterals) {
                    if (expr1 instanceof StringLiteral && (expr1.bits & 0x1FE00000) >> 21 == 0) {
                        if (expr2 instanceof CharLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
                            break;
                        }
                        if (expr2 instanceof StringLiteral) {
                            this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
                            break;
                        }
                        this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
                    break;
                }
                if (expr1 instanceof StringLiteral) {
                    if (expr2 instanceof StringLiteral && (expr1.bits & 0x1FE00000) >> 21 == 0) {
                        this.expressionStack[this.expressionPtr] = ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
                        break;
                    }
                    this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                    break;
                }
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
            case 4: 
            case 15: {
                --this.intPtr;
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
                break;
            }
            default: {
                this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, op);
            }
        }
    }

    protected void consumeBlock() {
        Block block;
        int statementsLength;
        if ((statementsLength = this.astLengthStack[this.astLengthPtr--]) == 0) {
            block = new Block(0);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
            if (!this.containsComment(block.sourceStart, block.sourceEnd)) {
                block.bits |= 8;
            }
            --this.realBlockPtr;
        } else {
            block = new Block(this.realBlockStack[this.realBlockPtr--]);
            this.astPtr -= statementsLength;
            block.statements = new Statement[statementsLength];
            System.arraycopy(this.astStack, this.astPtr + 1, block.statements, 0, statementsLength);
            block.sourceStart = this.intStack[this.intPtr--];
            block.sourceEnd = this.endStatementPosition;
        }
        if (this.currentElement instanceof RecoveredBlock && this.currentElement.getLastStart() == block.sourceStart) {
            this.currentElement.updateSourceEndIfNecessary(block.sourceEnd);
        }
        this.pushOnAstStack(block);
    }

    protected void consumeBlockStatement() {
    }

    protected void consumeBlockStatements() {
        this.concatNodeLists();
    }

    protected void consumeCaseLabel() {
        Expression[] constantExpressions = null;
        int length = 0;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            constantExpressions = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, constantExpressions, 0, length);
        }
        CaseStatement caseStatement = new CaseStatement((Expression)constantExpressions[0], constantExpressions[length - 1].sourceEnd, this.intStack[this.intPtr--]);
        if (constantExpressions.length > 1 && !this.parsingJava14Plus) {
            this.problemReporter().multiConstantCaseLabelsNotSupported(caseStatement);
        }
        caseStatement.constantExpressions = constantExpressions;
        if (this.hasLeadingTagComment(FALL_THROUGH_TAG, caseStatement.sourceStart)) {
            caseStatement.bits |= 0x20000000;
        }
        --this.casePtr;
        this.scanner.caseStartPosition = this.casePtr >= 0 ? this.caseStack[this.casePtr] : -1;
        this.pushOnAstStack(caseStatement);
    }

    protected void consumeCastExpressionLL1() {
        --this.expressionPtr;
        Expression exp = this.expressionStack[this.expressionPtr + 1];
        CastExpression cast = new CastExpression(exp, (TypeReference)this.expressionStack[this.expressionPtr]);
        this.expressionStack[this.expressionPtr] = cast;
        --this.expressionLengthPtr;
        this.updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }

    public IntersectionCastTypeReference createIntersectionCastTypeReference(TypeReference[] typeReferences) {
        if (this.options.sourceLevel < 0x340000L) {
            this.problemReporter().intersectionCastNotBelow18(typeReferences);
        }
        return new IntersectionCastTypeReference(typeReferences);
    }

    protected void consumeCastExpressionLL1WithBounds() {
        Expression exp = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        int length = this.expressionLengthStack[this.expressionLengthPtr];
        TypeReference[] bounds = new TypeReference[length];
        System.arraycopy(this.expressionStack, this.expressionPtr -= length - 1, bounds, 0, length);
        CastExpression cast = new CastExpression(exp, this.createIntersectionCastTypeReference(bounds));
        this.expressionStack[this.expressionPtr] = cast;
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
        this.updateSourcePosition(cast);
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithGenericsArray() {
        TypeReference castType;
        int additionalBoundsLength;
        TypeReference[] bounds = null;
        if ((additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        int end = this.intStack[this.intPtr--];
        int dim = this.intStack[this.intPtr--];
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(dim);
            castType = this.createIntersectionCastTypeReference(bounds);
        } else {
            castType = this.getTypeReference(dim);
        }
        Expression exp = this.expressionStack[this.expressionPtr];
        CastExpression cast = new CastExpression(exp, castType);
        this.expressionStack[this.expressionPtr] = cast;
        --this.intPtr;
        castType.sourceEnd = end - 1;
        cast.sourceStart = this.intStack[this.intPtr--];
        castType.sourceStart = cast.sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithNameArray() {
        TypeReference castType;
        int additionalBoundsLength;
        int end = this.intStack[this.intPtr--];
        TypeReference[] bounds = null;
        if ((additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(this.intStack[this.intPtr--]);
            castType = this.createIntersectionCastTypeReference(bounds);
        } else {
            castType = this.getTypeReference(this.intStack[this.intPtr--]);
        }
        Expression exp = this.expressionStack[this.expressionPtr];
        CastExpression cast = new CastExpression(exp, castType);
        this.expressionStack[this.expressionPtr] = cast;
        castType.sourceEnd = end - 1;
        cast.sourceStart = this.intStack[this.intPtr--];
        castType.sourceStart = cast.sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithPrimitiveType() {
        TypeReference castType;
        int additionalBoundsLength;
        TypeReference[] bounds = null;
        if ((additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        int end = this.intStack[this.intPtr--];
        if (additionalBoundsLength > 0) {
            bounds[0] = this.getTypeReference(this.intStack[this.intPtr--]);
            castType = this.createIntersectionCastTypeReference(bounds);
        } else {
            castType = this.getTypeReference(this.intStack[this.intPtr--]);
        }
        Expression exp = this.expressionStack[this.expressionPtr];
        CastExpression cast = new CastExpression(exp, castType);
        this.expressionStack[this.expressionPtr] = cast;
        castType.sourceEnd = end - 1;
        cast.sourceStart = this.intStack[this.intPtr--];
        castType.sourceStart = cast.sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCastExpressionWithQualifiedGenericsArray() {
        int dim;
        int additionalBoundsLength;
        TypeReference[] bounds = null;
        if ((additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            bounds = new TypeReference[additionalBoundsLength + 1];
            this.genericsPtr -= additionalBoundsLength;
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        }
        int end = this.intStack[this.intPtr--];
        Annotation[][] annotationsOnDimensions = (dim = this.intStack[this.intPtr--]) == 0 ? null : this.getAnnotationsOnDimensions(dim);
        TypeReference rightSide = this.getTypeReference(0);
        TypeReference castType = this.computeQualifiedGenericsFromRightSide(rightSide, dim, annotationsOnDimensions);
        if (additionalBoundsLength > 0) {
            bounds[0] = castType;
            castType = this.createIntersectionCastTypeReference(bounds);
        }
        --this.intPtr;
        Expression exp = this.expressionStack[this.expressionPtr];
        CastExpression cast = new CastExpression(exp, castType);
        this.expressionStack[this.expressionPtr] = cast;
        castType.sourceEnd = end - 1;
        cast.sourceStart = this.intStack[this.intPtr--];
        castType.sourceStart = cast.sourceStart + 1;
        cast.sourceEnd = exp.sourceEnd;
    }

    protected void consumeCatches() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeCatchFormalParameter() {
        int length;
        --this.identifierLengthPtr;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        int extendedDimensions = this.intStack[this.intPtr--];
        TypeReference type = (TypeReference)this.astStack[this.astPtr--];
        if (extendedDimensions > 0) {
            type = this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, null, false);
            type.sourceEnd = this.endPosition;
            if (type instanceof UnionTypeReference) {
                this.problemReporter().illegalArrayOfUnionType(identifierName, type);
            }
        }
        --this.astLengthPtr;
        int modifierPositions = this.intStack[this.intPtr--];
        --this.intPtr;
        Argument arg = new Argument(identifierName, namePositions, type, this.intStack[this.intPtr + 1] & 0xFFEFFFFF);
        arg.bits &= 0xFFFFFFFB;
        arg.declarationSourceStart = modifierPositions;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            arg.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, arg.annotations, 0, length);
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
    }

    protected void consumeCatchHeader() {
        if (this.currentElement == null) {
            return;
        }
        if (!(this.currentElement instanceof RecoveredBlock)) {
            if (!(this.currentElement instanceof RecoveredMethod)) {
                return;
            }
            RecoveredMethod rMethod = (RecoveredMethod)this.currentElement;
            if (rMethod.methodBody != null || rMethod.bracketBalance <= 0) {
                return;
            }
        }
        Argument arg = (Argument)this.astStack[this.astPtr--];
        LocalDeclaration localDeclaration = new LocalDeclaration(arg.name, arg.sourceStart, arg.sourceEnd);
        localDeclaration.type = arg.type;
        localDeclaration.declarationSourceStart = arg.declarationSourceStart;
        localDeclaration.declarationSourceEnd = arg.declarationSourceEnd;
        this.currentElement = this.currentElement.add(localDeclaration, 0);
        this.lastCheckPoint = this.scanner.startPosition;
        this.restartRecovery = true;
        this.lastIgnoredToken = -1;
    }

    protected void consumeCatchType() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 1) {
            TypeReference[] typeReferences = new TypeReference[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, typeReferences, 0, length);
            UnionTypeReference typeReference = new UnionTypeReference(typeReferences);
            this.pushOnAstStack(typeReference);
            if (this.options.sourceLevel < 0x330000L) {
                this.problemReporter().multiCatchNotBelow17(typeReference);
            }
        } else {
            this.pushOnAstLengthStack(1);
        }
    }

    protected void consumeClassBodyDeclaration() {
        int javadocCommentStart;
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] - 1;
        Block block = (Block)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        if (this.diet) {
            block.bits &= 0xFFFFFFF7;
        }
        Initializer initializer = (Initializer)this.astStack[this.astPtr];
        initializer.declarationSourceStart = initializer.sourceStart = block.sourceStart;
        initializer.block = block;
        --this.intPtr;
        initializer.bodyStart = this.intStack[this.intPtr--];
        --this.realBlockPtr;
        if ((javadocCommentStart = this.intStack[this.intPtr--]) != -1) {
            initializer.declarationSourceStart = javadocCommentStart;
            initializer.javadoc = this.javadoc;
            this.javadoc = null;
        }
        initializer.bodyEnd = this.endPosition;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeClassBodyDeclarations() {
        this.concatNodeLists();
    }

    protected void consumeClassBodyDeclarationsopt() {
        --this.nestedType;
    }

    protected void consumeClassBodyopt() {
        this.pushOnAstStack(null);
        this.endPosition = this.rParenPos;
    }

    protected void consumeClassDeclaration() {
        TypeDeclaration typeDecl;
        boolean hasConstructor;
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        if (!(hasConstructor = (typeDecl = (TypeDeclaration)this.astStack[this.astPtr]).checkConstructors(this))) {
            switch (TypeDeclaration.kind(typeDecl.modifiers)) {
                case 1: 
                case 3: {
                    boolean insideFieldInitializer = false;
                    if (this.diet) {
                        int i = this.nestedType;
                        while (i > 0) {
                            if (this.variablesCounter[i] > 0) {
                                insideFieldInitializer = true;
                                break;
                            }
                            --i;
                        }
                    }
                    typeDecl.createDefaultConstructor(!this.diet || this.dietInt != 0 || insideFieldInitializer, true);
                }
            }
        }
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= 1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= 8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeClassHeader() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }

    protected void consumeClassHeaderExtends() {
        TypeReference superClass = this.getTypeReference(0);
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.bits |= superClass.bits & 0x100000;
        typeDecl.superclass = superClass;
        superClass.bits |= 0x10;
        typeDecl.bodyStart = typeDecl.superclass.sourceEnd + 1;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeClassHeaderImplements() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.superInterfaces = new TypeReference[length];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces, 0, length);
        TypeReference[] superinterfaces = typeDecl.superInterfaces;
        int i = 0;
        int max = superinterfaces.length;
        while (i < max) {
            TypeReference typeReference = superinterfaces[i];
            typeDecl.bits |= typeReference.bits & 0x100000;
            typeReference.bits |= 0x10;
            ++i;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    private void consumeClassOrRecordHeaderName1(boolean isRecord) {
        int length;
        TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                typeDecl.bits |= 0x400;
            }
        } else {
            typeDecl.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int)pos;
        typeDecl.sourceStart = (int)(pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        if (isRecord) {
            typeDecl.restrictedIdentifierStart = typeDecl.declarationSourceStart;
        }
        --this.intPtr;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--];
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        if ((typeDecl.bits & 0x400) == 0 && (typeDecl.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
            typeDecl.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            typeDecl.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeDecl.annotations, 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        if (isRecord) {
            typeDecl.modifiers |= 0x1000000;
        }
        this.pushOnAstStack(typeDecl);
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeClassHeaderName1() {
        this.consumeClassOrRecordHeaderName1(false);
    }

    protected void consumeClassHeaderPermittedSubclasses() {
        this.populatePermittedTypes();
    }

    protected void consumeClassInstanceCreationExpression() {
        this.classInstanceCreation(false);
        this.consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }

    protected void consumeClassInstanceCreationExpressionQualified() {
        this.classInstanceCreation(true);
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            --this.expressionLengthPtr;
            --this.expressionPtr;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        this.consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            QualifiedAllocationExpression alloc = new QualifiedAllocationExpression();
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                alloc.arguments = new Expression[length];
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments, 0, length);
            }
            alloc.type = this.getTypeReference(0);
            this.checkForDiamond(alloc.type);
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            alloc.typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments, 0, length);
            --this.intPtr;
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        } else {
            this.dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= 8;
            }
            --this.astPtr;
            --this.astLengthPtr;
            QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                allocationExpression.typeArguments = new TypeReference[length];
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments, 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                this.checkForDiamond(allocationExpression.type);
            }
        }
        QualifiedAllocationExpression qae = (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
        if (qae.anonymousType == null) {
            --this.expressionLengthPtr;
            --this.expressionPtr;
            qae.enclosingInstance = this.expressionStack[this.expressionPtr];
            this.expressionStack[this.expressionPtr] = qae;
        }
        qae.sourceStart = qae.enclosingInstance.sourceStart;
        this.consumeInvocationExpression();
    }

    protected void consumeClassInstanceCreationExpressionWithTypeArguments() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 1 && this.astStack[this.astPtr] == null) {
            --this.astPtr;
            AllocationExpression alloc = new AllocationExpression();
            alloc.sourceEnd = this.endPosition;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                alloc.arguments = new Expression[length];
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments, 0, length);
            }
            alloc.type = this.getTypeReference(0);
            this.checkForDiamond(alloc.type);
            length = this.genericsLengthStack[this.genericsLengthPtr--];
            this.genericsPtr -= length;
            alloc.typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments, 0, length);
            --this.intPtr;
            alloc.sourceStart = this.intStack[this.intPtr--];
            this.pushOnExpressionStack(alloc);
        } else {
            this.dispatchDeclarationInto(length);
            TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
            anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
            anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
            if (length == 0 && !this.containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd)) {
                anonymousTypeDeclaration.bits |= 8;
            }
            --this.astPtr;
            --this.astLengthPtr;
            QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
            if (allocationExpression != null) {
                allocationExpression.sourceEnd = this.endStatementPosition;
                length = this.genericsLengthStack[this.genericsLengthPtr--];
                this.genericsPtr -= length;
                allocationExpression.typeArguments = new TypeReference[length];
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments, 0, length);
                allocationExpression.sourceStart = this.intStack[this.intPtr--];
                this.checkForDiamond(allocationExpression.type);
            }
        }
        this.consumeInvocationExpression();
    }

    protected void consumeClassOrInterface() {
        int n = this.genericsIdentifiersLengthPtr;
        this.genericsIdentifiersLengthStack[n] = this.genericsIdentifiersLengthStack[n] + this.identifierLengthStack[this.identifierLengthPtr];
        this.pushOnGenericsLengthStack(0);
    }

    protected void consumeClassOrInterfaceName() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
    }

    protected void consumeClassTypeElt() {
        this.pushOnAstStack(this.getTypeReference(0));
        ++this.listLength;
    }

    protected void consumeClassTypeList() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeCompilationUnit() {
    }

    protected void consumeConditionalExpression(int op) {
        this.intPtr -= 2;
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr + 2]);
    }

    protected void consumeConditionalExpressionWithName(int op) {
        this.intPtr -= 2;
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        this.expressionPtr -= 2;
        this.expressionLengthPtr -= 2;
        this.expressionStack[this.expressionPtr] = new ConditionalExpression(this.expressionStack[this.expressionPtr + 2], this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1]);
    }

    protected void consumeConstructorBlockStatements() {
        this.concatNodeLists();
    }

    protected void consumeConstructorBody() {
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] - 1;
    }

    protected void consumeConstructorDeclaration() {
        int length;
        --this.intPtr;
        --this.intPtr;
        --this.realBlockPtr;
        ExplicitConstructorCall constructorCall = null;
        Statement[] statements = null;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                    statements = new Statement[length - 1];
                    System.arraycopy(this.astStack, this.astPtr + 2, statements, 0, length - 1);
                    constructorCall = (ExplicitConstructorCall)this.astStack[this.astPtr + 1];
                } else {
                    statements = new Statement[length];
                    System.arraycopy(this.astStack, this.astPtr + 1, statements, 0, length);
                    constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        } else {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                int i = this.nestedType;
                while (i > 0) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                    --i;
                }
            }
            if (!(this.options.ignoreMethodBodies || this.diet && !insideFieldInitializer)) {
                constructorCall = SuperReference.implicitSuperConstructorCall();
            }
        }
        ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
        cd.constructorCall = constructorCall;
        cd.statements = statements;
        if (constructorCall != null && cd.constructorCall.sourceEnd == 0) {
            cd.constructorCall.sourceEnd = cd.sourceEnd;
            cd.constructorCall.sourceStart = cd.sourceStart;
        }
        if (!(this.diet && this.dietInt == 0 || statements != null || constructorCall != null && !constructorCall.isImplicitSuper() || this.containsComment(cd.bodyStart, this.endPosition))) {
            cd.bits |= 8;
        }
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeConstructorHeader() {
        AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            method.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            if (this.currentToken == 25) {
                method.modifiers |= 0x1000000;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            this.restartRecovery = true;
        }
    }

    protected void consumeConstructorHeaderName() {
        int length;
        if (this.currentElement != null && this.lastIgnoredToken == 37) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        cd.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            cd.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, cd.annotations, 0, length);
        }
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        cd.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if (this.currentElement instanceof RecoveredType && this.lastIgnoredToken != 1 || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    private void populateCompactConstructor(CompactConstructorDeclaration ccd) {
        int length;
        ccd.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        ccd.declarationSourceStart = this.intStack[this.intPtr--];
        ccd.modifiers = this.intStack[this.intPtr--];
        ccd.modifiers |= 0x800000;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            ccd.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, ccd.annotations, 0, length);
        }
        ccd.javadoc = this.javadoc;
        this.javadoc = null;
        ccd.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(ccd);
        ccd.sourceEnd = ccd.sourceStart + ccd.selector.length - 1;
        ccd.bodyStart = ccd.sourceStart + ccd.selector.length;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = ccd.bodyStart;
            if (this.currentElement instanceof RecoveredType && this.lastIgnoredToken != 1 || ccd.modifiers != 0) {
                this.currentElement = this.currentElement.add(ccd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeConstructorHeaderNameWithTypeParameters() {
        if (this.currentElement != null && this.lastIgnoredToken == 37) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
        this.helperConstructorHeaderNameWithTypeParameters(cd);
    }

    private void helperConstructorHeaderNameWithTypeParameters(ConstructorDeclaration cd) {
        cd.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        cd.typeParameters = new TypeParameter[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, cd.typeParameters, 0, length);
        cd.declarationSourceStart = this.intStack[this.intPtr--];
        cd.modifiers = this.intStack[this.intPtr--];
        length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length != 0) {
            cd.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, cd.annotations, 0, length);
        }
        cd.javadoc = this.javadoc;
        this.javadoc = null;
        cd.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(cd);
        cd.sourceEnd = this.lParenPos;
        cd.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = cd.bodyStart;
            if (this.currentElement instanceof RecoveredType && this.lastIgnoredToken != 1 || cd.modifiers != 0) {
                this.currentElement = this.currentElement.add(cd, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeCreateInitializer() {
        this.pushOnAstStack(new Initializer(null, 0));
    }

    protected void consumeDefaultLabel() {
        CaseStatement defaultStatement = new CaseStatement(null, this.intStack[this.intPtr--], this.intStack[this.intPtr--]);
        if (this.hasLeadingTagComment(FALL_THROUGH_TAG, defaultStatement.sourceStart)) {
            defaultStatement.bits |= 0x20000000;
        }
        if (this.hasLeadingTagComment(CASES_OMITTED_TAG, defaultStatement.sourceStart)) {
            defaultStatement.bits |= 0x40000000;
        }
        this.pushOnAstStack(defaultStatement);
    }

    protected void consumeDefaultModifiers() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeDiet() {
        this.checkComment();
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.jumpOverMethodBody();
    }

    protected void consumeDims() {
        this.pushOnIntStack(this.dimensions);
        this.dimensions = 0;
    }

    protected void consumeDimWithOrWithOutExpr() {
        this.pushOnExpressionStack(null);
        if (this.currentElement != null && this.currentToken == 38) {
            this.ignoreNextOpeningBrace = true;
            ++this.currentElement.bracketBalance;
        }
    }

    protected void consumeDimWithOrWithOutExprs() {
        this.concatExpressionLists();
    }

    protected void consumeUnionType() {
        this.pushOnAstStack(this.getTypeReference(this.intStack[this.intPtr--]));
        this.optimizedConcatNodeLists();
    }

    protected void consumeUnionTypeAsClassType() {
        this.pushOnAstStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeEmptyAnnotationTypeMemberDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyArgumentListopt() {
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyArguments() {
        FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        this.pushOnIntStack(fieldDeclaration.sourceEnd);
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyArrayInitializer() {
        this.arrayInitializer(0);
    }

    protected void consumeEmptyArrayInitializeropt() {
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyBlockStatementsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyCatchesopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyClassBodyDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyDimsopt() {
        this.pushOnIntStack(0);
    }

    protected void consumeEmptyEnumDeclarations() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyExpression() {
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyForInitopt() {
        this.pushOnAstLengthStack(0);
        this.forStartPosition = 0;
    }

    protected void consumeEmptyForUpdateopt() {
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumeEmptyInterfaceMemberDeclarationsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyInternalCompilationUnit() {
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }

    protected void consumeEmptyMemberValueArrayInitializer() {
        this.arrayInitializer(0);
    }

    protected void consumeEmptyMemberValuePairsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyMethodHeaderDefaultValue() {
        AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (method.isAnnotationMethod()) {
            this.pushOnExpressionStackLengthStack(0);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeEmptyStatement() {
        char[] source = this.scanner.source;
        if (source[this.endStatementPosition] == ';') {
            this.pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
        } else {
            if (source.length > 5) {
                int c1 = 0;
                int c2 = 0;
                int c3 = 0;
                int c4 = 0;
                int pos = this.endStatementPosition - 4;
                while (source[pos] == 'u') {
                    --pos;
                }
                if (source[pos] == '\\' && (c1 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 3])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 2])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition - 1])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(source[this.endStatementPosition])) <= 15 && c4 >= 0 && (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4) == ';') {
                    this.pushOnAstStack(new EmptyStatement(pos, this.endStatementPosition));
                    return;
                }
            }
            this.pushOnAstStack(new EmptyStatement(this.endPosition + 1, this.endStatementPosition));
        }
    }

    protected void consumeEmptySwitchBlock() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeEmptyTypeDeclaration() {
        this.pushOnAstLengthStack(0);
        if (!this.statementRecoveryActivated) {
            this.problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
        }
        this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnhancedForStatement() {
        --this.astLengthPtr;
        Statement statement = (Statement)this.astStack[this.astPtr--];
        ForeachStatement foreachStatement = (ForeachStatement)this.astStack[this.astPtr];
        foreachStatement.action = statement;
        if (statement instanceof EmptyStatement) {
            statement.bits |= 1;
        }
        foreachStatement.sourceEnd = this.endStatementPosition;
    }

    protected void consumeEnhancedForStatementHeader() {
        Expression collection;
        ForeachStatement statement = (ForeachStatement)this.astStack[this.astPtr];
        --this.expressionLengthPtr;
        statement.collection = collection = this.expressionStack[this.expressionPtr--];
        statement.elementVariable.declarationSourceEnd = collection.sourceEnd;
        statement.elementVariable.declarationEnd = collection.sourceEnd;
        statement.sourceEnd = this.rParenPos;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfForeachStatements(statement.elementVariable, collection);
        }
    }

    protected void consumeEnhancedForStatementHeaderInit(boolean hasModifiers) {
        int extraDims;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        LocalDeclaration localDeclaration = this.createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;
        localDeclaration.bits |= 0x10;
        Annotation[][] annotationsOnExtendedDimensions = (extraDims = this.intStack[this.intPtr--]) == 0 ? null : this.getAnnotationsOnDimensions(extraDims);
        --this.identifierPtr;
        --this.identifierLengthPtr;
        int declarationSourceStart = 0;
        int modifiersValue = 0;
        if (hasModifiers) {
            declarationSourceStart = this.intStack[this.intPtr--];
            modifiersValue = this.intStack[this.intPtr--];
        } else {
            this.intPtr -= 2;
        }
        TypeReference type = this.getTypeReference(this.intStack[this.intPtr--]);
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length != 0) {
            localDeclaration.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, localDeclaration.annotations, 0, length);
            localDeclaration.bits |= 0x100000;
        }
        if (extraDims != 0) {
            type = this.augmentTypeWithAdditionalDimensions(type, extraDims, annotationsOnExtendedDimensions, false);
        }
        if (hasModifiers) {
            localDeclaration.declarationSourceStart = declarationSourceStart;
            localDeclaration.modifiers = modifiersValue;
        } else {
            localDeclaration.declarationSourceStart = type.sourceStart;
        }
        localDeclaration.type = type;
        localDeclaration.bits |= type.bits & 0x100000;
        ForeachStatement iteratorForStatement = new ForeachStatement(localDeclaration, this.intStack[this.intPtr--]);
        this.pushOnAstStack(iteratorForStatement);
        iteratorForStatement.sourceEnd = localDeclaration.declarationSourceEnd;
        this.forStartPosition = 0;
    }

    protected void consumeEnterAnonymousClassBody(boolean qualified) {
        TypeReference typeReference = this.getTypeReference(0);
        TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
        anonymousType.name = CharOperation.NO_CHAR;
        anonymousType.bits |= 0x300;
        anonymousType.bits |= typeReference.bits & 0x100000;
        QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
        this.markEnclosingMemberWithLocalType();
        this.pushOnAstStack(anonymousType);
        alloc.sourceEnd = this.rParenPos;
        int argumentLength = this.expressionLengthStack[this.expressionLengthPtr--];
        if (argumentLength != 0) {
            this.expressionPtr -= argumentLength;
            alloc.arguments = new Expression[argumentLength];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, alloc.arguments, 0, argumentLength);
        }
        if (qualified) {
            --this.expressionLengthPtr;
            alloc.enclosingInstance = this.expressionStack[this.expressionPtr--];
        }
        alloc.type = typeReference;
        anonymousType.sourceEnd = alloc.sourceEnd;
        anonymousType.sourceStart = anonymousType.declarationSourceStart = alloc.type.sourceStart;
        alloc.sourceStart = this.intStack[this.intPtr--];
        this.pushOnExpressionStack(alloc);
        anonymousType.bodyStart = this.scanner.currentPosition;
        this.listLength = 0;
        this.scanner.commentPtr = -1;
        if (this.currentElement != null) {
            this.lastCheckPoint = anonymousType.bodyStart;
            this.currentElement = this.currentElement.add(anonymousType, 0);
            if (!(this.currentElement instanceof RecoveredAnnotation)) {
                if (this.isIndirectlyInsideLambdaExpression()) {
                    this.ignoreNextOpeningBrace = true;
                } else {
                    this.currentToken = 0;
                }
            } else {
                this.ignoreNextOpeningBrace = true;
                ++this.currentElement.bracketBalance;
            }
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnterCompilationUnit() {
    }

    protected void consumeEnterMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = true;
        }
    }

    protected void consumeEnterMemberValueArrayInitializer() {
        if (this.currentElement != null) {
            this.ignoreNextOpeningBrace = true;
            ++this.currentElement.bracketBalance;
        }
    }

    private boolean isAFieldDeclarationInRecord() {
        ASTNode node;
        if (this.options.sourceLevel < 0x3C0000L) {
            return false;
        }
        int recordIndex = -1;
        Integer[] nestingTypeAndMethod = null;
        int i = this.astPtr;
        while (i >= 0) {
            if (this.astStack[i] instanceof TypeDeclaration && (node = (TypeDeclaration)this.astStack[i]).isRecord() && (nestingTypeAndMethod = this.recordNestedMethodLevels.get(node)) != null) {
                recordIndex = i;
                break;
            }
            --i;
        }
        if (recordIndex < 0) {
            return false;
        }
        i = recordIndex + 1;
        while (i <= this.astPtr) {
            node = this.astStack[i];
            if (node instanceof TypeDeclaration) {
                if (node.sourceEnd < 0) {
                    return false;
                }
            } else if (node instanceof AbstractMethodDeclaration) {
                if (this.nestedType != nestingTypeAndMethod[0] || this.nestedMethod[this.nestedType] != nestingTypeAndMethod[1]) {
                    return false;
                }
            } else if (!(node instanceof FieldDeclaration)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    protected void consumeEnterVariable() {
        TypeReference type;
        int extendedDimensions;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        Annotation[][] annotationsOnExtendedDimensions = (extendedDimensions = this.intStack[this.intPtr--]) == 0 ? null : this.getAnnotationsOnDimensions(extendedDimensions);
        boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0 && !this.isAFieldDeclarationInRecord();
        AbstractVariableDeclaration declaration = isLocalDeclaration ? this.createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition) : this.createFieldDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        --this.identifierPtr;
        --this.identifierLengthPtr;
        int variableIndex = this.variablesCounter[this.nestedType];
        if (variableIndex == 0) {
            int length;
            if (isLocalDeclaration) {
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                    declaration.annotations = new Annotation[length];
                    System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, declaration.annotations, 0, length);
                }
                type = this.getTypeReference(this.intStack[this.intPtr--]);
                if (declaration.declarationSourceStart == -1) {
                    declaration.declarationSourceStart = type.sourceStart;
                }
                this.pushOnAstStack(type);
            } else {
                type = this.getTypeReference(this.intStack[this.intPtr--]);
                this.pushOnAstStack(type);
                declaration.declarationSourceStart = this.intStack[this.intPtr--];
                declaration.modifiers = this.intStack[this.intPtr--];
                length = this.expressionLengthStack[this.expressionLengthPtr--];
                if (length != 0) {
                    declaration.annotations = new Annotation[length];
                    System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, declaration.annotations, 0, length);
                }
                FieldDeclaration fieldDeclaration = (FieldDeclaration)declaration;
                fieldDeclaration.javadoc = this.javadoc;
            }
            this.javadoc = null;
        } else {
            type = (TypeReference)this.astStack[this.astPtr - variableIndex];
            AbstractVariableDeclaration previousVariable = (AbstractVariableDeclaration)this.astStack[this.astPtr];
            declaration.declarationSourceStart = previousVariable.declarationSourceStart;
            declaration.modifiers = previousVariable.modifiers;
            Annotation[] annotations = previousVariable.annotations;
            if (annotations != null) {
                int annotationsLength = annotations.length;
                declaration.annotations = new Annotation[annotationsLength];
                System.arraycopy(annotations, 0, declaration.annotations, 0, annotationsLength);
            }
            declaration.bits |= 0x400000;
        }
        declaration.type = extendedDimensions == 0 ? type : this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
        declaration.bits |= type.bits & 0x100000;
        int n = this.nestedType;
        this.variablesCounter[n] = this.variablesCounter[n] + 1;
        this.pushOnAstStack(declaration);
        if (this.currentElement != null) {
            if (!(this.currentElement instanceof RecoveredType || this.currentToken != 1 && Util.getLineNumber(declaration.type.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber((int)(namePosition >>> 32), this.scanner.lineEnds, 0, this.scanner.linePtr))) {
                this.lastCheckPoint = (int)(namePosition >>> 32);
                this.restartRecovery = true;
                return;
            }
            if (isLocalDeclaration) {
                LocalDeclaration localDecl = (LocalDeclaration)this.astStack[this.astPtr];
                this.lastCheckPoint = localDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(localDecl, 0);
            } else {
                FieldDeclaration fieldDecl = (FieldDeclaration)this.astStack[this.astPtr];
                this.lastCheckPoint = fieldDecl.sourceEnd + 1;
                this.currentElement = this.currentElement.add(fieldDecl, 0);
            }
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeEnumBodyNoConstants() {
    }

    protected void consumeEnumBodyWithConstants() {
        this.concatNodeLists();
    }

    protected void consumeEnumConstantHeader() {
        TypeDeclaration anonymousType;
        boolean foundOpeningBrace;
        FieldDeclaration enumConstant = (FieldDeclaration)this.astStack[this.astPtr];
        boolean bl = foundOpeningBrace = this.currentToken == 38;
        if (foundOpeningBrace) {
            int start;
            anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
            anonymousType.name = CharOperation.NO_CHAR;
            anonymousType.bits |= 0x300;
            anonymousType.declarationSourceStart = start = this.scanner.startPosition;
            anonymousType.sourceStart = start;
            anonymousType.sourceEnd = start;
            anonymousType.modifiers = 0;
            anonymousType.bodyStart = this.scanner.currentPosition;
            this.markEnclosingMemberWithLocalType();
            this.consumeNestedType();
            int n = this.nestedType;
            this.variablesCounter[n] = this.variablesCounter[n] + 1;
            this.pushOnAstStack(anonymousType);
            QualifiedAllocationExpression allocationExpression = new QualifiedAllocationExpression(anonymousType);
            allocationExpression.enumConstant = enumConstant;
            int length = this.expressionLengthStack[this.expressionLengthPtr--];
            if (length != 0) {
                this.expressionPtr -= length;
                allocationExpression.arguments = new Expression[length];
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression.arguments, 0, length);
            }
            enumConstant.initialization = allocationExpression;
        } else {
            int length;
            AllocationExpression allocationExpression = new AllocationExpression();
            allocationExpression.enumConstant = enumConstant;
            if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
                this.expressionPtr -= length;
                allocationExpression.arguments = new Expression[length];
                System.arraycopy(this.expressionStack, this.expressionPtr + 1, allocationExpression.arguments, 0, length);
            }
            enumConstant.initialization = allocationExpression;
        }
        enumConstant.initialization.sourceStart = enumConstant.declarationSourceStart;
        if (this.currentElement != null) {
            if (foundOpeningBrace) {
                anonymousType = (TypeDeclaration)this.astStack[this.astPtr];
                this.currentElement = this.currentElement.add(anonymousType, 0);
                this.lastCheckPoint = anonymousType.bodyStart;
                this.lastIgnoredToken = -1;
                if (this.isIndirectlyInsideLambdaExpression()) {
                    this.ignoreNextOpeningBrace = true;
                } else {
                    this.currentToken = 0;
                }
            } else {
                RecoveredType currentType;
                if (this.currentToken == 25 && (currentType = this.currentRecoveryType()) != null) {
                    currentType.insideEnumConstantPart = false;
                }
                this.lastCheckPoint = this.scanner.startPosition;
                this.lastIgnoredToken = -1;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeEnumConstantHeaderName() {
        int length;
        if (!(this.currentElement == null || (this.currentElement instanceof RecoveredType || this.currentElement instanceof RecoveredField && ((RecoveredField)this.currentElement).fieldDeclaration.type == null) && this.lastIgnoredToken != 1)) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        char[] constantName = this.identifierStack[this.identifierPtr];
        int sourceEnd = (int)namePosition;
        FieldDeclaration enumConstant = this.createFieldDeclaration(constantName, (int)(namePosition >>> 32), sourceEnd);
        --this.identifierPtr;
        --this.identifierLengthPtr;
        enumConstant.modifiersSourceStart = this.intStack[this.intPtr--];
        enumConstant.modifiers = this.intStack[this.intPtr--];
        enumConstant.declarationSourceStart = enumConstant.modifiersSourceStart;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            enumConstant.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumConstant.annotations, 0, length);
            enumConstant.bits |= 0x100000;
        }
        this.pushOnAstStack(enumConstant);
        if (this.currentElement != null) {
            this.lastCheckPoint = enumConstant.sourceEnd + 1;
            this.currentElement = this.currentElement.add(enumConstant, 0);
        }
        enumConstant.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEnumConstantNoClassBody() {
        int endOfEnumConstant = this.intStack[this.intPtr--];
        FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        fieldDeclaration.declarationEnd = endOfEnumConstant;
        fieldDeclaration.declarationSourceEnd = endOfEnumConstant;
        Expression initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = endOfEnumConstant;
        }
    }

    protected void consumeEnumConstants() {
        this.concatNodeLists();
    }

    protected void consumeEnumConstantWithClassBody() {
        int declarationSourceEnd;
        this.dispatchDeclarationInto(this.astLengthStack[this.astLengthPtr--]);
        TypeDeclaration anonymousType = (TypeDeclaration)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        anonymousType.bodyEnd = this.endPosition;
        anonymousType.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
        fieldDeclaration.declarationEnd = this.endStatementPosition;
        fieldDeclaration.declarationSourceEnd = declarationSourceEnd = anonymousType.declarationSourceEnd;
        --this.intPtr;
        this.variablesCounter[this.nestedType] = 0;
        --this.nestedType;
        Expression initialization = fieldDeclaration.initialization;
        if (initialization != null) {
            initialization.sourceEnd = declarationSourceEnd;
        }
    }

    protected void consumeEnumDeclaration() {
        TypeDeclaration enumDeclaration;
        boolean hasConstructor;
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationIntoEnumDeclaration(length);
        }
        if (!(hasConstructor = (enumDeclaration = (TypeDeclaration)this.astStack[this.astPtr]).checkConstructors(this))) {
            boolean insideFieldInitializer = false;
            if (this.diet) {
                int i = this.nestedType;
                while (i > 0) {
                    if (this.variablesCounter[i] > 0) {
                        insideFieldInitializer = true;
                        break;
                    }
                    --i;
                }
            }
            enumDeclaration.createDefaultConstructor(!this.diet || insideFieldInitializer, true);
        }
        if (this.scanner.containsAssertKeyword) {
            enumDeclaration.bits |= 1;
        }
        enumDeclaration.addClinit();
        enumDeclaration.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd)) {
            enumDeclaration.bits |= 8;
        }
        enumDeclaration.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeEnumDeclarations() {
    }

    protected void consumeEnumHeader() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }

    protected void consumeEnumHeaderName() {
        int length;
        TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                enumDeclaration.bits |= 0x400;
            }
        } else {
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int)pos;
        enumDeclaration.sourceStart = (int)(pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = this.intStack[this.intPtr--] | 0x4000;
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        if ((enumDeclaration.bits & 0x400) == 0 && (enumDeclaration.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
            enumDeclaration.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            enumDeclaration.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumDeclaration.annotations, 0, length);
        }
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        this.pushOnAstStack(enumDeclaration);
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEnumHeaderNameWithTypeParameters() {
        TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        enumDeclaration.typeParameters = new TypeParameter[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, enumDeclaration.typeParameters, 0, length);
        this.problemReporter().invalidUsageOfTypeParametersForEnumDeclaration(enumDeclaration);
        enumDeclaration.bodyStart = enumDeclaration.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                enumDeclaration.bits |= 0x400;
            }
        } else {
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        enumDeclaration.sourceEnd = (int)pos;
        enumDeclaration.sourceStart = (int)(pos >>> 32);
        enumDeclaration.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        enumDeclaration.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        enumDeclaration.modifiersSourceStart = this.intStack[this.intPtr--];
        enumDeclaration.modifiers = this.intStack[this.intPtr--] | 0x4000;
        if (enumDeclaration.modifiersSourceStart >= 0) {
            enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
        }
        if ((enumDeclaration.bits & 0x400) == 0 && (enumDeclaration.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(enumDeclaration.name, this.compilationUnit.getMainTypeName())) {
            enumDeclaration.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            enumDeclaration.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, enumDeclaration.annotations, 0, length);
        }
        enumDeclaration.bodyStart = enumDeclaration.sourceEnd + 1;
        this.pushOnAstStack(enumDeclaration);
        this.listLength = 0;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = enumDeclaration.bodyStart;
            this.currentElement = this.currentElement.add(enumDeclaration, 0);
            this.lastIgnoredToken = -1;
        }
        enumDeclaration.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeEqualityExpression(int op) {
        --this.expressionPtr;
        --this.expressionLengthPtr;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr], this.expressionStack[this.expressionPtr + 1], op);
    }

    protected void consumeEqualityExpressionWithName(int op) {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        --this.expressionPtr;
        --this.expressionLengthPtr;
        this.expressionStack[this.expressionPtr] = new EqualExpression(this.expressionStack[this.expressionPtr + 1], this.expressionStack[this.expressionPtr], op);
    }

    protected void consumeExitMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.hasPendingMemberValueName = false;
            recoveredAnnotation.memberValuPairEqualEnd = -1;
        }
    }

    protected void consumeExitTryBlock() {
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
    }

    protected void consumeExitVariableWithInitialization() {
        --this.expressionLengthPtr;
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
        variableDecl.initialization = this.expressionStack[this.expressionPtr--];
        variableDecl.declarationSourceEnd = variableDecl.initialization.sourceEnd;
        variableDecl.declarationEnd = variableDecl.initialization.sourceEnd;
        this.recoveryExitFromVariable();
    }

    protected void consumeExitVariableWithoutInitialization() {
        AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
        variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
        if (this.currentElement != null && this.currentElement instanceof RecoveredField && this.endStatementPosition > variableDecl.sourceEnd) {
            this.currentElement.updateSourceEndIfNecessary(this.endStatementPosition);
        }
        this.recoveryExitFromVariable();
    }

    protected void consumeExplicitConstructorInvocation(int flag, int recFlag) {
        int length;
        int startPosition = this.intStack[this.intPtr--];
        ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            ecc.arguments = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments, 0, length);
        }
        switch (flag) {
            case 0: {
                ecc.sourceStart = startPosition;
                break;
            }
            case 1: {
                --this.expressionLengthPtr;
                ecc.qualification = this.expressionStack[this.expressionPtr--];
                ecc.sourceStart = ecc.qualification.sourceStart;
                break;
            }
            case 2: {
                ecc.qualification = this.getUnspecifiedReferenceOptimized();
                ecc.sourceStart = ecc.qualification.sourceStart;
            }
        }
        this.pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }

    protected void consumeExplicitConstructorInvocationWithTypeArguments(int flag, int recFlag) {
        int length;
        int startPosition = this.intStack[this.intPtr--];
        ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            ecc.arguments = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments, 0, length);
        }
        length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        ecc.typeArguments = new TypeReference[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, ecc.typeArguments, 0, length);
        ecc.typeArgumentsSourceStart = this.intStack[this.intPtr--];
        switch (flag) {
            case 0: {
                ecc.sourceStart = startPosition;
                break;
            }
            case 1: {
                --this.expressionLengthPtr;
                ecc.qualification = this.expressionStack[this.expressionPtr--];
                ecc.sourceStart = ecc.qualification.sourceStart;
                break;
            }
            case 2: {
                ecc.qualification = this.getUnspecifiedReferenceOptimized();
                ecc.sourceStart = ecc.qualification.sourceStart;
            }
        }
        this.pushOnAstStack(ecc);
        ecc.sourceEnd = this.endStatementPosition;
    }

    protected void consumeExpressionStatement() {
        --this.expressionLengthPtr;
        Expression expression = this.expressionStack[this.expressionPtr--];
        expression.statementEnd = this.endStatementPosition;
        expression.bits |= 0x100000;
        this.pushOnAstStack(expression);
    }

    protected void consumeFieldAccess(boolean isSuperAccess) {
        FieldReference fr = new FieldReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        --this.identifierLengthPtr;
        if (isSuperAccess) {
            fr.sourceStart = this.intStack[this.intPtr--];
            fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
            this.pushOnExpressionStack(fr);
        } else {
            fr.receiver = this.expressionStack[this.expressionPtr];
            fr.sourceStart = fr.receiver.sourceStart;
            this.expressionStack[this.expressionPtr] = fr;
        }
    }

    protected void consumeFieldDeclaration() {
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        int i = variableDeclaratorsCounter - 1;
        while (i >= 0) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr - i];
            fieldDeclaration.declarationSourceEnd = this.endStatementPosition;
            fieldDeclaration.declarationEnd = this.endStatementPosition;
            --i;
        }
        this.updateSourceDeclarationParts(variableDeclaratorsCounter);
        int endPos = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (endPos != this.endStatementPosition) {
            int i2 = 0;
            while (i2 < variableDeclaratorsCounter) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr - i2];
                fieldDeclaration.declarationSourceEnd = endPos;
                ++i2;
            }
        }
        int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        --this.astPtr;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        if (this.currentElement != null) {
            this.lastCheckPoint = endPos + 1;
            if (this.currentElement.parent != null && this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer)) {
                this.currentElement = this.currentElement.parent;
            }
            this.restartRecovery = true;
        }
        this.variablesCounter[this.nestedType] = 0;
    }

    protected void consumeForceNoDiet() {
        ++this.dietInt;
    }

    protected void consumeForInit() {
        this.pushOnAstLengthStack(-1);
        this.forStartPosition = 0;
    }

    protected void consumeFormalParameter(boolean isVarArgs) {
        int length;
        int extendedDimensions;
        boolean isReceiver;
        NameReference qualifyingNameReference = null;
        boolean bl = isReceiver = this.intStack[this.intPtr--] == 0;
        if (isReceiver) {
            qualifyingNameReference = (NameReference)this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
        }
        --this.identifierLengthPtr;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        Annotation[][] annotationsOnExtendedDimensions = (extendedDimensions = this.intStack[this.intPtr--]) == 0 ? null : this.getAnnotationsOnDimensions(extendedDimensions);
        Annotation[] varArgsAnnotations = null;
        int endOfEllipsis = 0;
        if (isVarArgs) {
            endOfEllipsis = this.intStack[this.intPtr--];
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                varArgsAnnotations = new Annotation[length];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, varArgsAnnotations, 0, length);
            }
        }
        int firstDimensions = this.intStack[this.intPtr--];
        TypeReference type = this.getTypeReference(firstDimensions);
        if (isVarArgs || extendedDimensions != 0) {
            if (isVarArgs) {
                Annotation[][] annotationArray;
                if (varArgsAnnotations != null) {
                    Annotation[][] annotationArray2 = new Annotation[1][];
                    annotationArray = annotationArray2;
                    annotationArray2[0] = varArgsAnnotations;
                } else {
                    annotationArray = null;
                }
                type = this.augmentTypeWithAdditionalDimensions(type, 1, annotationArray, true);
            }
            if (extendedDimensions != 0) {
                type = this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
            }
            int n = type.sourceEnd = type.isParameterizedTypeReference() ? this.endStatementPosition : this.endPosition;
        }
        if (isVarArgs) {
            if (extendedDimensions == 0) {
                type.sourceEnd = endOfEllipsis;
            }
            type.bits |= 0x4000;
        }
        int modifierPositions = this.intStack[this.intPtr--];
        Argument arg = isReceiver ? new Receiver(identifierName, namePositions, type, qualifyingNameReference, this.intStack[this.intPtr--] & 0xFFEFFFFF) : new Argument(identifierName, namePositions, type, this.intStack[this.intPtr--] & 0xFFEFFFFF);
        arg.declarationSourceStart = modifierPositions;
        arg.bits |= type.bits & 0x100000;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            arg.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, arg.annotations, 0, length);
            arg.bits |= 0x100000;
            RecoveredType currentRecoveryType = this.currentRecoveryType();
            if (currentRecoveryType != null) {
                currentRecoveryType.annotationsConsumed(arg.annotations);
            }
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
        if (isVarArgs) {
            if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                this.problemReporter().invalidUsageOfVarargs(arg);
            } else if (!this.statementRecoveryActivated && extendedDimensions > 0) {
                this.problemReporter().illegalExtendedDimensions(arg);
            }
        }
    }

    protected Annotation[][] getAnnotationsOnDimensions(int dimensionsCount) {
        Annotation[][] dimensionsAnnotations = null;
        if (dimensionsCount > 0) {
            int i = 0;
            while (i < dimensionsCount) {
                int length;
                Annotation[] annotations = null;
                if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                    annotations = new Annotation[length];
                    System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, annotations, 0, length);
                    if (dimensionsAnnotations == null) {
                        dimensionsAnnotations = new Annotation[dimensionsCount][];
                    }
                    dimensionsAnnotations[dimensionsCount - i - 1] = annotations;
                }
                ++i;
            }
        }
        return dimensionsAnnotations;
    }

    protected void consumeFormalParameterList() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeFormalParameterListopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeGenericType() {
    }

    protected void consumeGenericTypeArrayType() {
    }

    protected void consumeGenericTypeNameArrayType() {
    }

    protected void consumeGenericTypeWithDiamond() {
        this.pushOnGenericsLengthStack(-1);
        this.concatGenericsLists();
        --this.intPtr;
    }

    protected void consumeImportDeclaration() {
        ImportReference impt = (ImportReference)this.astStack[this.astPtr];
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeImportDeclarations() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeInsideCastExpression() {
    }

    protected void consumeInsideCastExpressionLL1() {
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnExpressionStack(this.getTypeReference(0));
    }

    protected void consumeInsideCastExpressionLL1WithBounds() {
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength + 1];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 1, additionalBoundsLength);
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        bounds[0] = this.getTypeReference(0);
        int i = 0;
        while (i <= additionalBoundsLength) {
            this.pushOnExpressionStack(bounds[i]);
            if (i > 0) {
                this.expressionLengthStack[--this.expressionLengthPtr] = this.expressionLengthStack[this.expressionLengthPtr] + 1;
            }
            ++i;
        }
    }

    protected void consumeInsideCastExpressionWithQualifiedGenerics() {
    }

    private LocalDeclaration getInstanceOfVar(TypeReference type) {
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePosition = this.identifierPositionStack[this.identifierPtr];
        LocalDeclaration local = this.createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
        local.declarationSourceEnd = local.declarationEnd;
        --this.identifierPtr;
        --this.identifierLengthPtr;
        local.declarationSourceStart = type.sourceStart;
        local.type = type;
        this.problemReporter().validateJavaFeatureSupport(JavaFeature.PATTERN_MATCHING_IN_INSTANCEOF, type.sourceStart, local.declarationEnd);
        local.modifiers |= 0x10;
        return local;
    }

    protected void consumeInstanceOfExpression() {
        InstanceOfExpression exp;
        int length;
        int n = length = this.patternLengthPtr >= 0 ? this.patternLengthStack[this.patternLengthPtr--] : 0;
        if (length > 0) {
            LocalDeclaration typeDecl = (LocalDeclaration)this.patternStack[this.patternPtr--];
            exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], typeDecl);
            this.expressionStack[this.expressionPtr] = exp;
            typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
            typeDecl.modifiers = this.intStack[this.intPtr--];
        } else {
            TypeReference typeRef = (TypeReference)this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
            exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], typeRef);
            this.expressionStack[this.expressionPtr] = exp;
            --this.intPtr;
            --this.intPtr;
        }
        if (exp.sourceEnd == 0) {
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    }

    protected void consumeInstanceOfExpressionHelper() {
        int length;
        Annotation[] typeAnnotations = null;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            typeAnnotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeAnnotations, 0, length);
        }
        TypeReference ref = this.getTypeReference(this.intStack[this.intPtr--]);
        if (typeAnnotations != null) {
            int levels = ref.getAnnotatableLevels();
            if (ref.annotations == null) {
                ref.annotations = new Annotation[levels][];
            }
            ref.annotations[0] = typeAnnotations;
            ref.sourceStart = ref.annotations[0][0].sourceStart;
            ref.bits |= 0x100000;
        }
        this.pushOnExpressionStack(ref);
    }

    protected void consumeInstanceOfRHS() {
    }

    protected void consumeInstanceOfClassic() {
        this.consumeInstanceOfExpressionHelper();
    }

    protected void consumeInstanceofPattern() {
        TypeReference typeRef = (TypeReference)this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        LocalDeclaration local = this.getInstanceOfVar(typeRef);
        this.pushOnPatternStack(local);
        if (this.realBlockPtr != -1) {
            this.blockReal();
        }
    }

    protected void consumeInstanceOfExpressionWithName() {
        InstanceOfExpression exp;
        int length;
        int n = length = this.patternLengthPtr >= 0 ? this.patternLengthStack[this.patternLengthPtr--] : 0;
        if (length != 0) {
            LocalDeclaration typeDecl = (LocalDeclaration)this.patternStack[this.patternPtr--];
            this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
            exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], typeDecl);
            this.expressionStack[this.expressionPtr] = exp;
            typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
            typeDecl.modifiers = this.intStack[this.intPtr--];
        } else {
            TypeReference typeRef = (TypeReference)this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
            this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
            exp = new InstanceOfExpression(this.expressionStack[this.expressionPtr], typeRef);
            this.expressionStack[this.expressionPtr] = exp;
            --this.intPtr;
            --this.intPtr;
        }
        if (exp.sourceEnd == 0) {
            exp.sourceEnd = this.scanner.startPosition - 1;
        }
    }

    protected void consumeInterfaceDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationInto(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.checkConstructors(this);
        FieldDeclaration[] fields = typeDecl.fields;
        int fieldCount = fields == null ? 0 : fields.length;
        int i = 0;
        while (i < fieldCount) {
            FieldDeclaration field = fields[i];
            if (field instanceof Initializer) {
                this.problemReporter().interfaceCannotHaveInitializers(typeDecl.name, field);
            }
            ++i;
        }
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= 1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= 8;
        }
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeInterfaceHeader() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            typeDecl.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            this.restartRecovery = true;
        }
        this.scanner.commentPtr = -1;
    }

    protected void consumeInterfaceHeaderExtends() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.superInterfaces = new TypeReference[length];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.superInterfaces, 0, length);
        TypeReference[] superinterfaces = typeDecl.superInterfaces;
        int i = 0;
        int max = superinterfaces.length;
        while (i < max) {
            TypeReference typeReference = superinterfaces[i];
            typeDecl.bits |= typeReference.bits & 0x100000;
            typeReference.bits |= 0x10;
            ++i;
        }
        typeDecl.bodyStart = typeDecl.superInterfaces[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeInterfaceHeaderName1() {
        int length;
        TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
        if (this.nestedMethod[this.nestedType] == 0) {
            if (this.nestedType != 0) {
                typeDecl.bits |= 0x400;
            }
        } else {
            typeDecl.bits |= 0x100;
            this.markEnclosingMemberWithLocalType();
            this.blockReal();
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        typeDecl.sourceEnd = (int)pos;
        typeDecl.sourceStart = (int)(pos >>> 32);
        typeDecl.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        --this.intPtr;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--] | 0x200;
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        if ((typeDecl.bits & 0x400) == 0 && (typeDecl.bits & 0x100) == 0 && this.compilationUnit != null && !CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())) {
            typeDecl.bits |= 0x1000;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            typeDecl.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeDecl.annotations, 0, length);
        }
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        this.pushOnAstStack(typeDecl);
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
        typeDecl.javadoc = this.javadoc;
        this.javadoc = null;
    }

    protected void consumeInterfaceHeaderPermittedSubClassesAndSubInterfaces() {
        this.populatePermittedTypes();
    }

    private void populatePermittedTypes() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        typeDecl.restrictedIdentifierStart = this.intStack[this.intPtr--];
        typeDecl.permittedTypes = new TypeReference[length];
        System.arraycopy(this.astStack, this.astPtr + 1, typeDecl.permittedTypes, 0, length);
        TypeReference[] permittedTypes = typeDecl.permittedTypes;
        int i = 0;
        int max = permittedTypes.length;
        while (i < max) {
            TypeReference typeReference = permittedTypes[i];
            typeDecl.bits |= typeReference.bits & 0x100000;
            ++i;
        }
        typeDecl.bodyStart = typeDecl.permittedTypes[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
        }
    }

    protected void consumeInterfaceMemberDeclarations() {
        this.concatNodeLists();
    }

    protected void consumeInterfaceMemberDeclarationsopt() {
        --this.nestedType;
    }

    protected void consumeInterfaceType() {
        this.pushOnAstStack(this.getTypeReference(0));
        ++this.listLength;
    }

    protected void consumeInterfaceTypeList() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeInternalCompilationUnit() {
        if (this.compilationUnit.isPackageInfo()) {
            this.compilationUnit.types = new TypeDeclaration[1];
            this.compilationUnit.createPackageInfoType();
        }
    }

    protected void consumeInternalCompilationUnitWithTypes() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.compilationUnit.isPackageInfo()) {
                this.compilationUnit.types = new TypeDeclaration[length + 1];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 1, length);
                this.compilationUnit.createPackageInfoType();
            } else {
                this.compilationUnit.types = new TypeDeclaration[length];
                this.astPtr -= length;
                System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 0, length);
            }
        }
    }

    protected void consumeInvalidAnnotationTypeDeclaration() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }

    protected void consumeInvalidConstructorDeclaration() {
        ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
        cd.bodyEnd = this.endPosition;
        cd.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        cd.modifiers |= 0x1000000;
    }

    protected void consumeInvalidConstructorDeclaration(boolean hasBody) {
        int length;
        if (hasBody) {
            --this.intPtr;
        }
        if (hasBody) {
            --this.realBlockPtr;
        }
        if (hasBody && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
        }
        ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)this.astStack[this.astPtr];
        constructorDeclaration.bodyEnd = this.endStatementPosition;
        constructorDeclaration.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (!hasBody) {
            constructorDeclaration.modifiers |= 0x1000000;
        }
    }

    protected void consumeInvalidEnumDeclaration() {
        if (this.options.sourceLevel >= 0x3C0000L) {
            return;
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }

    protected void consumeInvalidInterfaceDeclaration() {
        if (this.options.sourceLevel >= 0x3C0000L) {
            return;
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (!this.statementRecoveryActivated) {
            this.problemReporter().illegalLocalTypeDeclaration(typeDecl);
        }
        --this.astPtr;
        this.pushOnAstLengthStack(-1);
        this.concatNodeLists();
    }

    protected void consumeInterfaceMethodDeclaration(boolean hasSemicolonBody) {
        boolean bodyAllowed;
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (!hasSemicolonBody) {
            int length;
            --this.intPtr;
            --this.intPtr;
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                } else {
                    statements = new Statement[length];
                    System.arraycopy(this.astStack, (this.astPtr -= length) + 1, statements, 0, length);
                }
            }
        }
        MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        boolean isDefault = (md.modifiers & 0x10000) != 0;
        boolean isStatic = (md.modifiers & 8) != 0;
        boolean isPrivate = (md.modifiers & 2) != 0;
        boolean bl = bodyAllowed = this.parsingJava9Plus && isPrivate || isDefault || isStatic;
        if (this.parsingJava8Plus) {
            if (bodyAllowed && hasSemicolonBody) {
                md.modifiers |= 0x1000000;
            }
        } else {
            if (isDefault) {
                this.problemReporter().defaultMethodsNotBelow18(md);
            }
            if (isStatic) {
                this.problemReporter().staticInterfaceMethodsNotBelow18(md);
            }
        }
        if (!(bodyAllowed || this.statementRecoveryActivated || hasSemicolonBody)) {
            this.problemReporter().abstractMethodNeedingNoBody(md);
        }
    }

    protected void consumeLabel() {
    }

    protected void consumeLeftParen() {
        this.pushOnIntStack(this.lParenPos);
    }

    protected void consumeLocalVariableDeclaration() {
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
        System.arraycopy(this.astStack, startIndex, this.astStack, startIndex - 1, variableDeclaratorsCounter);
        --this.astPtr;
        this.astLengthStack[--this.astLengthPtr] = variableDeclaratorsCounter;
        this.variablesCounter[this.nestedType] = 0;
        this.forStartPosition = 0;
    }

    protected void consumeLocalVariableDeclarationStatement() {
        LocalDeclaration localDeclaration;
        int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
        if (variableDeclaratorsCounter == 1 && (localDeclaration = (LocalDeclaration)this.astStack[this.astPtr]).isRecoveredFromLoneIdentifier()) {
            NameReference left;
            if (localDeclaration.type instanceof QualifiedTypeReference) {
                QualifiedTypeReference qtr = (QualifiedTypeReference)localDeclaration.type;
                left = new QualifiedNameReference(qtr.tokens, qtr.sourcePositions, 0, 0);
            } else {
                left = new SingleNameReference(localDeclaration.type.getLastToken(), 0L);
            }
            left.sourceStart = localDeclaration.type.sourceStart;
            left.sourceEnd = localDeclaration.type.sourceEnd;
            SingleNameReference right = new SingleNameReference(localDeclaration.name, 0L);
            right.sourceStart = localDeclaration.sourceStart;
            right.sourceEnd = localDeclaration.sourceEnd;
            Assignment assignment = new Assignment(left, right, 0);
            int end = this.endStatementPosition;
            assignment.sourceEnd = end == localDeclaration.sourceEnd ? ++end : end;
            assignment.statementEnd = end;
            this.astStack[this.astPtr] = assignment;
            if (this.recoveryScanner != null) {
                RecoveryScannerData data = this.recoveryScanner.getData();
                int position = data.insertedTokensPtr;
                while (position > 0) {
                    if (data.insertedTokensPosition[position] != data.insertedTokensPosition[position - 1]) break;
                    --position;
                }
                if (position >= 0) {
                    this.recoveryScanner.insertTokenAhead(77, position);
                }
            }
            if (this.currentElement != null) {
                this.lastCheckPoint = assignment.sourceEnd + 1;
                this.currentElement = this.currentElement.add(assignment, 0);
            }
            return;
        }
        int n = this.realBlockPtr;
        this.realBlockStack[n] = this.realBlockStack[n] + 1;
        int i = variableDeclaratorsCounter - 1;
        while (i >= 0) {
            LocalDeclaration localDeclaration2 = (LocalDeclaration)this.astStack[this.astPtr - i];
            localDeclaration2.declarationSourceEnd = this.endStatementPosition;
            localDeclaration2.declarationEnd = this.endStatementPosition;
            --i;
        }
    }

    protected void consumeMarkerAnnotation(boolean isTypeAnnotation) {
        MarkerAnnotation markerAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = this.getAnnotationType();
        markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[this.intPtr--]);
        markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(markerAnnotation);
        } else {
            this.pushOnExpressionStack(markerAnnotation);
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(markerAnnotation);
        }
        this.recordStringLiterals = true;
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(markerAnnotation, oldIndex);
        }
    }

    protected void consumeMemberValueArrayInitializer() {
        this.arrayInitializer(this.expressionLengthStack[this.expressionLengthPtr--]);
    }

    protected void consumeMemberValueAsName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }

    protected void consumeMemberValuePair() {
        char[] simpleName = this.identifierStack[this.identifierPtr];
        long position = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int end = (int)position;
        int start = (int)(position >>> 32);
        Expression value = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        MemberValuePair memberValuePair = new MemberValuePair(simpleName, start, end, value);
        this.pushOnAstStack(memberValuePair);
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.setKind(1);
        }
    }

    protected void consumeMemberValuePairs() {
        this.concatNodeLists();
    }

    protected void consumeMemberValues() {
        this.concatExpressionLists();
    }

    protected void consumeMethodBody() {
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] - 1;
    }

    protected void consumeMethodDeclaration(boolean isNotAbstract, boolean isDefaultMethod) {
        if (isNotAbstract) {
            --this.intPtr;
            --this.intPtr;
        }
        int explicitDeclarations = 0;
        Statement[] statements = null;
        if (isNotAbstract) {
            int length;
            explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
            if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
                if (this.options.ignoreMethodBodies) {
                    this.astPtr -= length;
                } else {
                    statements = new Statement[length];
                    System.arraycopy(this.astStack, (this.astPtr -= length) + 1, statements, 0, length);
                }
            }
        }
        MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        md.statements = statements;
        md.explicitDeclarations = explicitDeclarations;
        if (!isNotAbstract) {
            md.modifiers |= 0x1000000;
        } else if (!(this.diet && this.dietInt == 0 || statements != null || this.containsComment(md.bodyStart, this.endPosition))) {
            md.bits |= 8;
        }
        md.bodyEnd = this.endPosition;
        md.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        if (isDefaultMethod && !this.tolerateDefaultClassMethods) {
            if (this.options.sourceLevel >= 0x340000L) {
                this.problemReporter().defaultModifierIllegallySpecified(md.sourceStart, md.sourceEnd);
            } else {
                this.problemReporter().illegalModifierForMethod(md);
            }
        }
    }

    protected void consumeMethodHeader() {
        AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            method.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            if (this.currentToken == 25) {
                method.modifiers |= 0x1000000;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            } else if (this.currentToken == 38 && this.currentElement instanceof RecoveredMethod && ((RecoveredMethod)this.currentElement).methodDeclaration != method) {
                this.ignoreNextOpeningBrace = true;
                ++this.currentElement.bracketBalance;
            }
            this.restartRecovery = true;
        }
    }

    protected void consumeMethodHeaderDefaultValue() {
        int length;
        MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) == 1) {
            --this.intPtr;
            --this.intPtr;
            if (md.isAnnotationMethod()) {
                ((AnnotationMethodDeclaration)md).defaultValue = this.expressionStack[this.expressionPtr];
                md.modifiers |= 0x20000;
            }
            --this.expressionPtr;
            this.recordStringLiterals = true;
        }
        if (this.currentElement != null && md.isAnnotationMethod()) {
            this.currentElement.updateSourceEndIfNecessary(((AnnotationMethodDeclaration)md).defaultValue.sourceEnd);
        }
    }

    protected void consumeMethodHeaderExtendedDims() {
        MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
        int extendedDimensions = this.intStack[this.intPtr--];
        if (md.isAnnotationMethod()) {
            ((AnnotationMethodDeclaration)md).extendedDimensions = extendedDimensions;
        }
        if (extendedDimensions != 0) {
            md.sourceEnd = this.endPosition;
            md.returnType = this.augmentTypeWithAdditionalDimensions(md.returnType, extendedDimensions, this.getAnnotationsOnDimensions(extendedDimensions), false);
            md.bits |= md.returnType.bits & 0x100000;
            if (this.currentToken == 38) {
                md.bodyStart = this.endPosition + 1;
            }
            if (this.currentElement != null) {
                this.lastCheckPoint = md.bodyStart;
            }
        }
    }

    protected void consumeMethodHeaderName(boolean isAnnotationMethod) {
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        } else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        md.returnType = this.getTypeReference(this.intStack[this.intPtr--]);
        md.bits |= md.returnType.bits & 0x100000;
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        int length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length != 0) {
            md.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, md.annotations, 0, length);
        }
        md.javadoc = this.javadoc;
        this.javadoc = null;
        md.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType || Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            } else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeMethodHeaderNameWithTypeParameters(boolean isAnnotationMethod) {
        MethodDeclaration md = null;
        if (isAnnotationMethod) {
            md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
            this.recordStringLiterals = false;
        } else {
            md = new MethodDeclaration(this.compilationUnit.compilationResult);
        }
        md.selector = this.identifierStack[this.identifierPtr];
        long selectorSource = this.identifierPositionStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        TypeReference returnType = this.getTypeReference(this.intStack[this.intPtr--]);
        if (isAnnotationMethod) {
            this.rejectIllegalLeadingTypeAnnotations(returnType);
        }
        md.returnType = returnType;
        md.bits |= returnType.bits & 0x100000;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        md.typeParameters = new TypeParameter[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, md.typeParameters, 0, length);
        md.declarationSourceStart = this.intStack[this.intPtr--];
        md.modifiers = this.intStack[this.intPtr--];
        length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length != 0) {
            md.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, md.annotations, 0, length);
        }
        md.javadoc = this.javadoc;
        this.javadoc = null;
        md.sourceStart = (int)(selectorSource >>> 32);
        this.pushOnAstStack(md);
        md.sourceEnd = this.lParenPos;
        md.bodyStart = this.lParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            boolean isType = this.currentElement instanceof RecoveredType;
            if (isType || Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
                if (isType) {
                    ((RecoveredType)this.currentElement).pendingTypeParameters = null;
                }
                this.lastCheckPoint = md.bodyStart;
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            } else {
                this.lastCheckPoint = md.sourceStart;
                this.restartRecovery = true;
            }
        }
    }

    protected void consumeMethodHeaderRightParen() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        md.sourceEnd = this.rParenPos;
        if (length != 0) {
            Argument arg = (Argument)this.astStack[this.astPtr + 1];
            if (arg.isReceiver()) {
                Annotation[] annotations;
                md.receiver = (Receiver)arg;
                if (length > 1) {
                    md.arguments = new Argument[length - 1];
                    System.arraycopy(this.astStack, this.astPtr + 2, md.arguments, 0, length - 1);
                }
                if ((annotations = arg.annotations) != null && annotations.length > 0) {
                    TypeReference type = arg.type;
                    if (type.annotations == null) {
                        type.bits |= 0x100000;
                        type.annotations = new Annotation[type.getAnnotatableLevels()][];
                        md.bits |= 0x100000;
                    }
                    type.annotations[0] = annotations;
                    int annotationSourceStart = annotations[0].sourceStart;
                    if (type.sourceStart > annotationSourceStart) {
                        type.sourceStart = annotationSourceStart;
                    }
                    arg.annotations = null;
                }
                md.bits |= arg.type.bits & 0x100000;
            } else {
                md.arguments = new Argument[length];
                System.arraycopy(this.astStack, this.astPtr + 1, md.arguments, 0, length);
                int i = 0;
                int max = md.arguments.length;
                while (i < max) {
                    if ((md.arguments[i].bits & 0x100000) != 0) {
                        md.bits |= 0x100000;
                        break;
                    }
                    ++i;
                }
            }
        }
        md.bodyStart = this.rParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
            if (this.currentElement.parseTree() == md) {
                return;
            }
            if (md.isConstructor() && (length != 0 || this.currentToken == 38 || this.currentToken == 117)) {
                this.currentElement = this.currentElement.add(md, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeMethodHeaderThrowsClause() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        md.thrownExceptions = new TypeReference[length];
        System.arraycopy(this.astStack, this.astPtr + 1, md.thrownExceptions, 0, length);
        md.sourceEnd = md.thrownExceptions[length - 1].sourceEnd;
        md.bodyStart = md.thrownExceptions[length - 1].sourceEnd + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = md.bodyStart;
        }
    }

    protected void consumeInvocationExpression() {
    }

    protected void consumeMethodInvocationName() {
        MessageSend m = this.newMessageSend();
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.sourceStart = (int)(m.nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        if (this.identifierLengthStack[this.identifierLengthPtr] == 1) {
            m.receiver = ThisReference.implicitThis();
            --this.identifierLengthPtr;
        } else {
            int n = this.identifierLengthPtr;
            this.identifierLengthStack[n] = this.identifierLengthStack[n] - 1;
            m.receiver = this.getUnspecifiedReference();
            m.sourceStart = m.receiver.sourceStart;
        }
        int length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--];
        if (length != 0) {
            Annotation[] typeAnnotations = new Annotation[length];
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeAnnotations, 0, length);
            this.problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
        }
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }

    protected void consumeMethodInvocationNameWithTypeArguments() {
        MessageSend m = this.newMessageSendWithTypeArguments();
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.sourceStart = (int)(m.nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        m.typeArguments = new TypeReference[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments, 0, length);
        --this.intPtr;
        m.receiver = this.getUnspecifiedReference();
        m.sourceStart = m.receiver.sourceStart;
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }

    protected void consumeMethodInvocationPrimary() {
        MessageSend m = this.newMessageSend();
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.sourceStart = (int)(m.nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        this.consumeInvocationExpression();
    }

    protected void consumeMethodInvocationPrimaryWithTypeArguments() {
        MessageSend m = this.newMessageSendWithTypeArguments();
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.sourceStart = (int)(m.nameSourcePosition >>> 32);
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        m.typeArguments = new TypeReference[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments, 0, length);
        --this.intPtr;
        m.receiver = this.expressionStack[this.expressionPtr];
        m.sourceStart = m.receiver.sourceStart;
        m.sourceEnd = this.rParenPos;
        this.expressionStack[this.expressionPtr] = m;
        this.consumeInvocationExpression();
    }

    protected void consumeMethodInvocationSuper() {
        MessageSend m = this.newMessageSend();
        m.sourceStart = this.intStack[this.intPtr--];
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }

    protected void consumeMethodInvocationSuperWithTypeArguments() {
        MessageSend m = this.newMessageSendWithTypeArguments();
        --this.intPtr;
        m.sourceEnd = this.rParenPos;
        m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
        m.selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        m.typeArguments = new TypeReference[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments, 0, length);
        m.sourceStart = this.intStack[this.intPtr--];
        m.receiver = new SuperReference(m.sourceStart, this.endPosition);
        this.pushOnExpressionStack(m);
        this.consumeInvocationExpression();
    }

    protected void consumeModifiers() {
        int savedModifiersSourceStart = this.modifiersSourceStart;
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
    }

    protected void consumeModifiers2() {
        int n = this.expressionLengthPtr - 1;
        this.expressionLengthStack[n] = this.expressionLengthStack[n] + this.expressionLengthStack[this.expressionLengthPtr--];
    }

    protected void consumeMultipleResources() {
        this.concatNodeLists();
    }

    protected void consumeTypeAnnotation() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x340000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            Annotation annotation = this.typeAnnotationStack[this.typeAnnotationPtr];
            this.problemReporter().invalidUsageOfTypeAnnotations(annotation);
        }
        this.dimensions = this.intStack[this.intPtr--];
    }

    protected void consumeOneMoreTypeAnnotation() {
        this.typeAnnotationLengthStack[--this.typeAnnotationLengthPtr] = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] + 1;
    }

    protected void consumeNameArrayType() {
        this.pushOnGenericsLengthStack(0);
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
    }

    protected void consumeNestedMethod() {
        this.jumpOverMethodBody();
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] + 1;
        this.pushOnIntStack(this.scanner.currentPosition);
        this.consumeOpenBlock();
    }

    protected void consumeNestedType() {
        int length = this.nestedMethod.length;
        if (++this.nestedType >= length) {
            this.nestedMethod = new int[length + 30];
            System.arraycopy(this.nestedMethod, 0, this.nestedMethod, 0, length);
            this.variablesCounter = new int[length + 30];
            System.arraycopy(this.variablesCounter, 0, this.variablesCounter, 0, length);
        }
        this.nestedMethod[this.nestedType] = 0;
        this.variablesCounter[this.nestedType] = 0;
    }

    protected void consumeNormalAnnotation(boolean isTypeAnnotation) {
        int length;
        NormalAnnotation normalAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = this.getAnnotationType();
        normalAnnotation = new NormalAnnotation(typeReference, this.intStack[this.intPtr--]);
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            normalAnnotation.memberValuePairs = new MemberValuePair[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, normalAnnotation.memberValuePairs, 0, length);
        }
        normalAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(normalAnnotation);
        } else {
            this.pushOnExpressionStack(normalAnnotation);
        }
        if (this.currentElement != null) {
            this.annotationRecoveryCheckPoint(normalAnnotation.sourceStart, normalAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(normalAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(normalAnnotation);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeOneDimLoop(boolean isAnnotated) {
        ++this.dimensions;
        if (!isAnnotated) {
            this.pushOnTypeAnnotationLengthStack(0);
        }
    }

    protected void consumeOnlySynchronized() {
        this.pushOnIntStack(this.synchronizedBlockSourceStart);
        this.resetModifiers();
        --this.expressionLengthPtr;
    }

    protected void consumeOnlyTypeArguments() {
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference)this.genericsStack[this.genericsPtr - length + 1], (TypeReference)this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeOnlyTypeArgumentsForCastExpression() {
    }

    protected void consumeOpenBlock() {
        this.pushOnIntStack(this.scanner.startPosition);
        int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            this.realBlockStack = new int[stackLength + 255];
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack, 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = 0;
    }

    protected void consumePackageComment() {
        if (this.options.sourceLevel >= 0x310000L) {
            this.checkComment();
            this.resetModifiers();
        }
    }

    protected void consumeInternalCompilationUnitWithModuleDeclaration() {
        this.compilationUnit.moduleDeclaration = (ModuleDeclaration)this.astStack[this.astPtr--];
        this.astLengthStack[this.astLengthPtr--] = 0;
    }

    protected void consumeRequiresStatement() {
        RequiresStatement req = (RequiresStatement)this.astStack[this.astPtr];
        req.declarationEnd = req.declarationSourceEnd = this.endStatementPosition;
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = req.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(req, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeSingleRequiresModuleName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ModuleReference impt = new ModuleReference(tokens, positions);
        RequiresStatement req = new RequiresStatement(impt);
        req.declarationSourceEnd = this.currentToken == 25 ? impt.sourceEnd + 1 : impt.sourceEnd;
        req.declarationEnd = req.declarationSourceEnd;
        req.modifiersSourceStart = this.intStack[this.intPtr--];
        req.modifiers |= this.intStack[this.intPtr--];
        req.sourceStart = req.declarationSourceStart = this.intStack[this.intPtr--];
        req.sourceEnd = impt.sourceEnd;
        this.pushOnAstStack(req);
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = req.declarationSourceEnd;
        }
    }

    protected void consumeExportsStatement() {
        ExportsStatement expt = (ExportsStatement)this.astStack[this.astPtr];
        expt.declarationEnd = expt.declarationSourceEnd = this.endStatementPosition;
        if (this.currentElement instanceof RecoveredPackageVisibilityStatement) {
            this.lastCheckPoint = expt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.parent;
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeExportsHeader() {
        ImportReference impt = (ImportReference)this.astStack[this.astPtr];
        impt.bits |= 0x40000;
        ExportsStatement expt = new ExportsStatement(impt);
        expt.sourceStart = expt.declarationSourceStart = this.intStack[this.intPtr--];
        expt.sourceEnd = impt.sourceEnd;
        expt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : expt.sourceEnd;
        expt.declarationEnd = expt.declarationSourceEnd;
        this.astStack[this.astPtr] = expt;
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = expt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(expt, 0);
        }
    }

    protected void consumeOpensHeader() {
        ImportReference impt = (ImportReference)this.astStack[this.astPtr];
        impt.bits |= 0x40000;
        OpensStatement stmt = new OpensStatement(impt);
        stmt.sourceStart = stmt.declarationSourceStart = this.intStack[this.intPtr--];
        stmt.sourceEnd = impt.sourceEnd;
        stmt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : stmt.sourceEnd;
        stmt.declarationEnd = stmt.declarationSourceEnd;
        this.astStack[this.astPtr] = stmt;
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = stmt.declarationSourceEnd + 1;
            this.lastCheckPoint = stmt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(stmt, 0);
        }
    }

    protected void consumeOpensStatement() {
        OpensStatement expt = (OpensStatement)this.astStack[this.astPtr];
        expt.declarationEnd = expt.declarationSourceEnd = this.endStatementPosition;
        if (this.currentElement instanceof RecoveredPackageVisibilityStatement) {
            this.lastCheckPoint = expt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.parent;
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeSingleTargetModuleName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ModuleReference reference = new ModuleReference(tokens, positions);
        this.pushOnAstStack(reference);
        if (this.currentElement != null) {
            this.lastCheckPoint = reference.sourceEnd + 1;
        }
    }

    protected void consumeTargetModuleList() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        PackageVisibilityStatement node = (PackageVisibilityStatement)this.astStack[this.astPtr];
        if (length > 0) {
            node.targets = new ModuleReference[length];
            System.arraycopy(this.astStack, this.astPtr + 1, node.targets, 0, length);
            node.sourceEnd = node.targets[length - 1].sourceEnd;
            node.declarationSourceEnd = this.currentToken == 25 ? node.sourceEnd + 1 : node.sourceEnd;
        }
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = node.sourceEnd;
        }
    }

    protected void consumeTargetModuleNameList() {
        ++this.listLength;
        this.optimizedConcatNodeLists();
    }

    protected void consumeSinglePkgName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ImportReference impt = new ImportReference(tokens, positions, false, 0);
        this.pushOnAstStack(impt);
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = impt.sourceEnd + 1;
        }
    }

    protected void consumeUsesStatement() {
        UsesStatement stmt = (UsesStatement)this.astStack[this.astPtr];
        stmt.declarationEnd = stmt.declarationSourceEnd = this.endStatementPosition;
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = stmt.declarationSourceEnd;
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeUsesHeader() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference siName = this.getTypeReference(0);
        if (siName.annotations != null) {
            int j = 0;
            while (j < siName.annotations.length) {
                Annotation[] qualifierAnnot = siName.annotations[j];
                if (qualifierAnnot != null && qualifierAnnot.length > 0) {
                    this.problemReporter().misplacedTypeAnnotations(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
                    siName.annotations[j] = null;
                }
                ++j;
            }
        }
        UsesStatement stmt = new UsesStatement(siName);
        stmt.declarationSourceEnd = this.currentToken == 25 ? siName.sourceEnd + 1 : siName.sourceEnd;
        stmt.declarationEnd = stmt.declarationSourceEnd;
        stmt.sourceStart = stmt.declarationSourceStart = this.intStack[this.intPtr--];
        stmt.sourceEnd = siName.sourceEnd;
        this.pushOnAstStack(stmt);
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = stmt.sourceEnd + 1;
            this.currentElement = this.currentElement.add(stmt, 0);
        }
    }

    protected void consumeProvidesInterface() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference siName = this.getTypeReference(0);
        if (siName.annotations != null) {
            int j = 0;
            while (j < siName.annotations.length) {
                Annotation[] qualifierAnnot = siName.annotations[j];
                if (qualifierAnnot != null && qualifierAnnot.length > 0) {
                    this.problemReporter().misplacedTypeAnnotations(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
                    siName.annotations[j] = null;
                }
                ++j;
            }
        }
        ProvidesStatement ref = new ProvidesStatement();
        ref.serviceInterface = siName;
        this.pushOnAstStack(ref);
        ref.sourceStart = ref.declarationSourceStart = this.intStack[this.intPtr--];
        ref.declarationSourceEnd = ref.sourceEnd = siName.sourceEnd;
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = siName.sourceEnd + 1;
            this.currentElement = this.currentElement.add(ref, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeSingleServiceImplName() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference siName = this.getTypeReference(0);
        if (siName.annotations != null) {
            int j = 0;
            while (j < siName.annotations.length) {
                Annotation[] qualifierAnnot = siName.annotations[j];
                if (qualifierAnnot != null && qualifierAnnot.length > 0) {
                    this.problemReporter().misplacedTypeAnnotations(qualifierAnnot[0], qualifierAnnot[qualifierAnnot.length - 1]);
                    siName.annotations[j] = null;
                }
                ++j;
            }
        }
        this.pushOnAstStack(siName);
        if (this.currentElement instanceof RecoveredModule) {
            this.lastCheckPoint = siName.sourceEnd + 1;
        }
    }

    protected void consumeServiceImplNameList() {
        ++this.listLength;
        this.optimizedConcatNodeLists();
    }

    protected void consumeProvidesStatement() {
        ProvidesStatement ref = (ProvidesStatement)this.astStack[this.astPtr];
        ref.declarationEnd = ref.declarationSourceEnd = this.endStatementPosition;
        if (this.currentElement instanceof RecoveredProvidesStatement) {
            this.lastIgnoredToken = -1;
            this.currentElement = this.currentElement.parent;
            this.restartRecovery = true;
        }
    }

    protected void consumeWithClause() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        ProvidesStatement service = (ProvidesStatement)this.astStack[this.astPtr];
        service.implementations = new TypeReference[length];
        System.arraycopy(this.astStack, this.astPtr + 1, service.implementations, 0, length);
        service.sourceEnd = service.implementations[length - 1].sourceEnd;
        service.declarationSourceEnd = this.currentToken == 25 ? service.sourceEnd + 1 : service.sourceEnd;
        this.listLength = 0;
        if (this.currentElement instanceof RecoveredProvidesStatement) {
            this.lastCheckPoint = service.declarationSourceEnd;
        }
    }

    protected void consumeEmptyModuleStatementsOpt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeModuleStatements() {
        this.concatNodeLists();
    }

    protected void consumeModuleModifiers() {
        this.checkComment();
        int n = this.intPtr - 1;
        this.intStack[n] = this.intStack[n] | this.modifiers;
        this.resetModifiers();
        int n2 = this.expressionLengthPtr - 1;
        this.expressionLengthStack[n2] = this.expressionLengthStack[n2] + this.expressionLengthStack[this.expressionLengthPtr--];
    }

    protected void consumeModuleHeader() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        ModuleDeclaration typeDecl = new ModuleDeclaration(this.compilationUnit.compilationResult, tokens, positions);
        typeDecl.declarationSourceStart = this.intStack[this.intPtr--];
        typeDecl.bodyStart = typeDecl.sourceEnd + 1;
        typeDecl.modifiersSourceStart = this.intStack[this.intPtr--];
        typeDecl.modifiers = this.intStack[this.intPtr--];
        if (typeDecl.modifiersSourceStart >= 0) {
            typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
        }
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            typeDecl.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, typeDecl.annotations, 0, length);
        }
        this.pushOnAstStack(typeDecl);
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            this.currentElement = this.currentElement.add(typeDecl, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeModuleDeclaration() {
        this.compilationUnit.javadoc = this.javadoc;
        this.javadoc = null;
        int length = this.astLengthStack[this.astLengthPtr--];
        int[] flag = new int[length + 1];
        int size1 = 0;
        int size2 = 0;
        int size3 = 0;
        int size4 = 0;
        int size5 = 0;
        if (length != 0) {
            int i = length - 1;
            while (i >= 0) {
                ASTNode astNode;
                if ((astNode = this.astStack[this.astPtr--]) instanceof RequiresStatement) {
                    flag[i] = 1;
                    ++size1;
                } else if (astNode instanceof ExportsStatement) {
                    flag[i] = 2;
                    ++size2;
                } else if (astNode instanceof UsesStatement) {
                    flag[i] = 3;
                    ++size3;
                } else if (astNode instanceof ProvidesStatement) {
                    flag[i] = 4;
                    ++size4;
                } else if (astNode instanceof OpensStatement) {
                    flag[i] = 5;
                    ++size5;
                }
                --i;
            }
        }
        ModuleDeclaration modul = (ModuleDeclaration)this.astStack[this.astPtr];
        modul.requiresCount = size1;
        modul.exportsCount = size2;
        modul.usesCount = size3;
        modul.servicesCount = size4;
        modul.opensCount = size5;
        modul.requires = new RequiresStatement[size1];
        modul.exports = new ExportsStatement[size2];
        modul.uses = new UsesStatement[size3];
        modul.services = new ProvidesStatement[size4];
        modul.opens = new OpensStatement[size5];
        size5 = 0;
        size4 = 0;
        size3 = 0;
        size2 = 0;
        size1 = 0;
        int flagI = flag[0];
        int start = 0;
        int end = 0;
        while (end <= length) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, modul.requires, (size1 += length2) - length2, length2);
                        break;
                    }
                    case 2: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, modul.exports, (size2 += length2) - length2, length2);
                        break;
                    }
                    case 3: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, modul.uses, (size3 += length2) - length2, length2);
                        break;
                    }
                    case 4: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, modul.services, (size4 += length2) - length2, length2);
                        break;
                    }
                    case 5: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, modul.opens, (size5 += length2) - length2, length2);
                    }
                }
                start = end;
                flagI = flag[start];
            }
            ++end;
        }
        modul.bodyEnd = this.endStatementPosition;
        modul.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumePackageDeclaration() {
        ImportReference impt = this.compilationUnit.currentPackage;
        this.compilationUnit.javadoc = this.javadoc;
        this.javadoc = null;
        impt.declarationEnd = this.endStatementPosition;
        impt.declarationSourceEnd = this.flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
        if (this.firstToken == 29) {
            this.unstackedAct = 17933;
        }
    }

    protected void consumePackageDeclarationName() {
        ImportReference impt;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        this.compilationUnit.currentPackage = impt = new ImportReference(tokens, positions, false, 0);
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.javadoc != null) {
            impt.declarationSourceStart = this.javadoc.sourceStart;
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.restartRecovery = true;
        }
    }

    protected void consumePackageDeclarationNameWithModifiers() {
        ImportReference impt;
        int packageModifiersSourceStart;
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr--, positions, 0, length);
        int packageModifiersSourceEnd = packageModifiersSourceStart = this.intStack[this.intPtr--];
        int packageModifiers = this.intStack[this.intPtr--];
        this.compilationUnit.currentPackage = impt = new ImportReference(tokens, positions, false, packageModifiers);
        length = this.expressionLengthStack[this.expressionLengthPtr--];
        if (length != 0) {
            impt.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, impt.annotations, 0, length);
            impt.declarationSourceStart = packageModifiersSourceStart;
            packageModifiersSourceEnd = this.intStack[this.intPtr--] - 2;
        } else {
            impt.declarationSourceStart = this.intStack[this.intPtr--];
            packageModifiersSourceEnd = impt.declarationSourceStart - 2;
            if (this.javadoc != null) {
                impt.declarationSourceStart = this.javadoc.sourceStart;
            }
        }
        if (packageModifiers != 0) {
            this.problemReporter().illegalModifiers(packageModifiersSourceStart, packageModifiersSourceEnd);
        }
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.restartRecovery = true;
        }
    }

    protected void consumePostfixExpression() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
    }

    protected void consumePrimaryNoNewArray() {
        Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        this.updateSourcePosition(parenthesizedExpression);
        int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
        parenthesizedExpression.bits &= 0xE01FFFFF;
        parenthesizedExpression.bits |= numberOfParenthesis + 1 << 21;
    }

    protected void consumePrimaryNoNewArrayArrayType() {
        --this.intPtr;
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        ClassLiteralAccess cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(this.intStack[this.intPtr--]));
        this.pushOnExpressionStack(cla);
        this.rejectIllegalTypeAnnotations(cla.type);
    }

    protected void consumePrimaryNoNewArrayName() {
        --this.intPtr;
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new ClassLiteralAccess(this.intStack[this.intPtr--], typeReference));
    }

    protected void rejectIllegalLeadingTypeAnnotations(TypeReference typeReference) {
        Annotation[][] annotations = typeReference.annotations;
        if (annotations != null && annotations[0] != null) {
            this.problemReporter().misplacedTypeAnnotations(annotations[0][0], annotations[0][annotations[0].length - 1]);
            annotations[0] = null;
        }
    }

    private void rejectIllegalTypeAnnotations(TypeReference typeReference) {
        Annotation[] misplacedAnnotations;
        Annotation[][] annotations = typeReference.annotations;
        int i = 0;
        int length = annotations == null ? 0 : annotations.length;
        while (i < length) {
            misplacedAnnotations = annotations[i];
            if (misplacedAnnotations != null) {
                this.problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
            }
            ++i;
        }
        annotations = typeReference.getAnnotationsOnDimensions(true);
        i = 0;
        length = annotations == null ? 0 : annotations.length;
        while (i < length) {
            misplacedAnnotations = annotations[i];
            if (misplacedAnnotations != null) {
                this.problemReporter().misplacedTypeAnnotations(misplacedAnnotations[0], misplacedAnnotations[misplacedAnnotations.length - 1]);
            }
            ++i;
        }
        typeReference.annotations = null;
        typeReference.setAnnotationsOnDimensions(null);
        typeReference.bits &= 0xFFEFFFFF;
    }

    protected void consumeQualifiedSuperReceiver() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new QualifiedSuperReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayNameThis() {
        this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
        this.pushOnGenericsLengthStack(0);
        TypeReference typeReference = this.getTypeReference(0);
        this.rejectIllegalTypeAnnotations(typeReference);
        this.pushOnExpressionStack(new QualifiedThisReference(typeReference, this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
        --this.intPtr;
        ClassLiteralAccess cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(this.intStack[this.intPtr--]));
        this.pushOnExpressionStack(cla);
        this.rejectIllegalTypeAnnotations(cla.type);
    }

    protected void consumePrimaryNoNewArrayPrimitiveType() {
        --this.intPtr;
        ClassLiteralAccess cla = new ClassLiteralAccess(this.intStack[this.intPtr--], this.getTypeReference(0));
        this.pushOnExpressionStack(cla);
        this.rejectIllegalTypeAnnotations(cla.type);
    }

    protected void consumePrimaryNoNewArrayThis() {
        this.pushOnExpressionStack(new ThisReference(this.intStack[this.intPtr--], this.endPosition));
    }

    protected void consumePrimaryNoNewArrayWithName() {
        this.pushOnExpressionStack(this.getUnspecifiedReferenceOptimized());
        Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
        this.updateSourcePosition(parenthesizedExpression);
        int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
        parenthesizedExpression.bits &= 0xE01FFFFF;
        parenthesizedExpression.bits |= numberOfParenthesis + 1 << 21;
    }

    protected void consumePrimitiveArrayType() {
    }

    protected void consumePrimitiveType() {
        this.pushOnIntStack(0);
    }

    protected void consumePushLeftBrace() {
        this.pushOnIntStack(this.endPosition);
    }

    protected void consumePushModifiers() {
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumePushCombineModifiers() {
        --this.intPtr;
        int newModifiers = this.intStack[this.intPtr--] | 0x10000;
        this.intPtr -= 2;
        if ((this.intStack[this.intPtr - 1] & newModifiers) != 0) {
            newModifiers |= 0x400000;
        }
        int n = this.intPtr - 1;
        this.intStack[n] = this.intStack[n] | newModifiers;
        int n2 = this.expressionLengthPtr - 1;
        this.expressionLengthStack[n2] = this.expressionLengthStack[n2] + this.expressionLengthStack[this.expressionLengthPtr--];
        if (this.currentElement != null) {
            this.currentElement.addModifier(newModifiers, this.intStack[this.intPtr]);
        }
    }

    protected void consumePushModifiersForHeader() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
        this.pushOnExpressionStackLengthStack(0);
    }

    protected void consumePushPosition() {
        this.pushOnIntStack(this.endPosition);
    }

    protected void consumePushRealModifiers() {
        this.checkComment();
        this.pushOnIntStack(this.modifiers);
        this.pushOnIntStack(this.modifiersSourceStart);
        this.resetModifiers();
    }

    protected void consumeQualifiedName(boolean qualifiedNameIsAnnotated) {
        this.identifierLengthStack[--this.identifierLengthPtr] = this.identifierLengthStack[this.identifierLengthPtr] + 1;
        if (!qualifiedNameIsAnnotated) {
            this.pushOnTypeAnnotationLengthStack(0);
        }
    }

    protected void consumeUnannotatableQualifiedName() {
        this.identifierLengthStack[--this.identifierLengthPtr] = this.identifierLengthStack[this.identifierLengthPtr] + 1;
    }

    protected void consumeRecoveryMethodHeaderName() {
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0;
        } else {
            RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & 0x2000) != 0;
            }
        }
        this.consumeMethodHeaderName(isAnnotationMethod);
    }

    protected void consumeRecoveryMethodHeaderNameWithTypeParameters() {
        boolean isAnnotationMethod = false;
        if (this.currentElement instanceof RecoveredType) {
            isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0;
        } else {
            RecoveredType recoveredType = this.currentElement.enclosingType();
            if (recoveredType != null) {
                isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & 0x2000) != 0;
            }
        }
        this.consumeMethodHeaderNameWithTypeParameters(isAnnotationMethod);
    }

    protected void consumeReduceImports() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            this.compilationUnit.imports = new ImportReference[length];
            System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.imports, 0, length);
        }
    }

    protected void consumeReferenceType() {
        this.pushOnIntStack(0);
    }

    protected void consumeReferenceType1() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeReferenceType2() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeReferenceType3() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeResourceAsLocalVariable() {
        NameReference ref = this.getUnspecifiedReference(true);
        this.pushOnAstStack(ref);
    }

    protected void consumeResourceAsFieldAccess() {
        FieldReference ref = (FieldReference)this.expressionStack[this.expressionPtr--];
        this.pushOnAstStack(ref);
    }

    protected void consumeResourceAsLocalVariableDeclaration() {
        this.consumeLocalVariableDeclaration();
    }

    protected void consumeResourceSpecification() {
    }

    protected void consumeResourceOptionalTrailingSemiColon(boolean punctuated) {
        Statement statement = (Statement)this.astStack[this.astPtr];
        if (punctuated && statement instanceof LocalDeclaration) {
            ((LocalDeclaration)statement).declarationSourceEnd = this.endStatementPosition;
        }
    }

    protected void consumeRestoreDiet() {
        --this.dietInt;
    }

    protected void consumeRightParen() {
        this.pushOnIntStack(this.rParenPos);
    }

    protected void consumeNonTypeUseName() {
        int i = this.identifierLengthStack[this.identifierLengthPtr];
        while (i > 0 && this.typeAnnotationLengthPtr >= 0) {
            int length;
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                Annotation[] typeAnnotations = new Annotation[length];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeAnnotations, 0, length);
                this.problemReporter().misplacedTypeAnnotations(typeAnnotations[0], typeAnnotations[typeAnnotations.length - 1]);
            }
            --i;
        }
    }

    protected void consumeZeroTypeAnnotations() {
        this.pushOnTypeAnnotationLengthStack(0);
    }

    protected void consumeRule(int act) {
        switch (act) {
            case 41: {
                this.consumePrimitiveType();
                break;
            }
            case 55: {
                this.consumeReferenceType();
                break;
            }
            case 59: {
                this.consumeClassOrInterfaceName();
                break;
            }
            case 60: {
                this.consumeClassOrInterface();
                break;
            }
            case 61: {
                this.consumeGenericType();
                break;
            }
            case 62: {
                this.consumeGenericTypeWithDiamond();
                break;
            }
            case 63: {
                this.consumeArrayTypeWithTypeArgumentsName();
                break;
            }
            case 64: {
                this.consumePrimitiveArrayType();
                break;
            }
            case 65: {
                this.consumeNameArrayType();
                break;
            }
            case 66: {
                this.consumeGenericTypeNameArrayType();
                break;
            }
            case 67: {
                this.consumeGenericTypeArrayType();
                break;
            }
            case 69: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 74: {
                this.consumeUnannotatableQualifiedName();
                break;
            }
            case 75: {
                this.consumeQualifiedName(false);
                break;
            }
            case 76: {
                this.consumeQualifiedName(true);
                break;
            }
            case 77: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 81: {
                this.consumeOneMoreTypeAnnotation();
                break;
            }
            case 82: {
                this.consumeTypeAnnotation();
                break;
            }
            case 83: {
                this.consumeTypeAnnotation();
                break;
            }
            case 84: {
                this.consumeTypeAnnotation();
                break;
            }
            case 85: {
                this.consumeAnnotationName();
                break;
            }
            case 86: {
                this.consumeNormalAnnotation(true);
                break;
            }
            case 87: {
                this.consumeMarkerAnnotation(true);
                break;
            }
            case 88: {
                this.consumeSingleMemberAnnotation(true);
                break;
            }
            case 89: {
                this.consumeNonTypeUseName();
                break;
            }
            case 90: {
                this.consumeZeroTypeAnnotations();
                break;
            }
            case 91: {
                this.consumeExplicitThisParameter(false);
                break;
            }
            case 92: {
                this.consumeExplicitThisParameter(true);
                break;
            }
            case 93: {
                this.consumeVariableDeclaratorIdParameter();
                break;
            }
            case 94: {
                this.consumeCompilationUnit();
                break;
            }
            case 95: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 96: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 97: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 98: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 99: {
                this.consumeInternalCompilationUnit();
                break;
            }
            case 100: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 101: {
                this.consumeInternalCompilationUnitWithTypes();
                break;
            }
            case 102: {
                this.consumeEmptyInternalCompilationUnit();
                break;
            }
            case 103: {
                this.consumeInternalCompilationUnitWithModuleDeclaration();
                break;
            }
            case 104: {
                this.consumeInternalCompilationUnitWithModuleDeclaration();
                break;
            }
            case 105: {
                this.consumeModuleDeclaration();
                break;
            }
            case 106: {
                this.consumeModuleHeader();
                break;
            }
            case 108: {
                this.consumeModuleModifiers();
                break;
            }
            case 111: {
                this.consumeEmptyModuleStatementsOpt();
                break;
            }
            case 114: {
                this.consumeModuleStatements();
                break;
            }
            case 120: {
                this.consumeRequiresStatement();
                break;
            }
            case 121: {
                this.consumeSingleRequiresModuleName();
                break;
            }
            case 122: {
                this.consumeModifiers();
                break;
            }
            case 123: {
                this.consumeDefaultModifiers();
                break;
            }
            case 125: {
                this.consumeModifiers2();
                break;
            }
            case 128: {
                this.consumeExportsStatement();
                break;
            }
            case 129: {
                this.consumeExportsHeader();
                break;
            }
            case 131: {
                this.consumeTargetModuleList();
                break;
            }
            case 132: {
                this.consumeSingleTargetModuleName();
                break;
            }
            case 134: {
                this.consumeTargetModuleNameList();
                break;
            }
            case 135: {
                this.consumeSinglePkgName();
                break;
            }
            case 136: {
                this.consumeOpensStatement();
                break;
            }
            case 137: {
                this.consumeOpensHeader();
                break;
            }
            case 138: {
                this.consumeUsesStatement();
                break;
            }
            case 139: {
                this.consumeUsesHeader();
                break;
            }
            case 140: {
                this.consumeProvidesStatement();
                break;
            }
            case 141: {
                this.consumeProvidesInterface();
                break;
            }
            case 142: {
                this.consumeSingleServiceImplName();
                break;
            }
            case 144: {
                this.consumeServiceImplNameList();
                break;
            }
            case 145: {
                this.consumeWithClause();
                break;
            }
            case 146: {
                this.consumeReduceImports();
                break;
            }
            case 147: {
                this.consumeEnterCompilationUnit();
                break;
            }
            case 170: {
                this.consumeCatchHeader();
                break;
            }
            case 172: {
                this.consumeImportDeclarations();
                break;
            }
            case 174: {
                this.consumeTypeDeclarations();
                break;
            }
            case 175: {
                this.consumePackageDeclaration();
                break;
            }
            case 176: {
                this.consumePackageDeclarationNameWithModifiers();
                break;
            }
            case 177: {
                this.consumePackageDeclarationName();
                break;
            }
            case 178: {
                this.consumePackageComment();
                break;
            }
            case 183: {
                this.consumeImportDeclaration();
                break;
            }
            case 184: {
                this.consumeSingleTypeImportDeclarationName();
                break;
            }
            case 185: {
                this.consumeImportDeclaration();
                break;
            }
            case 186: {
                this.consumeTypeImportOnDemandDeclarationName();
                break;
            }
            case 189: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 194: {
                this.consumeModifiers2();
                break;
            }
            case 208: {
                this.consumeAnnotationAsModifier();
                break;
            }
            case 209: {
                this.consumeClassDeclaration();
                break;
            }
            case 210: {
                this.consumeClassHeader();
                break;
            }
            case 211: {
                this.consumeTypeHeaderNameWithTypeParameters();
                break;
            }
            case 213: {
                this.consumeClassHeaderName1();
                break;
            }
            case 214: {
                this.consumeClassHeaderExtends();
                break;
            }
            case 215: {
                this.consumeClassHeaderImplements();
                break;
            }
            case 217: {
                this.consumeInterfaceTypeList();
                break;
            }
            case 218: {
                this.consumeInterfaceType();
                break;
            }
            case 221: {
                this.consumeClassBodyDeclarations();
                break;
            }
            case 225: {
                this.consumeClassBodyDeclaration();
                break;
            }
            case 226: {
                this.consumeDiet();
                break;
            }
            case 227: {
                this.consumeClassBodyDeclaration();
                break;
            }
            case 228: {
                this.consumeCreateInitializer();
                break;
            }
            case 236: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 239: {
                this.consumeFieldDeclaration();
                break;
            }
            case 241: {
                this.consumeVariableDeclarators();
                break;
            }
            case 244: {
                this.consumeEnterVariable();
                break;
            }
            case 245: {
                this.consumeExitVariableWithInitialization();
                break;
            }
            case 246: {
                this.consumeExitVariableWithoutInitialization();
                break;
            }
            case 247: {
                this.consumeForceNoDiet();
                break;
            }
            case 248: {
                this.consumeRestoreDiet();
                break;
            }
            case 253: {
                this.consumeMethodDeclaration(true, false);
                break;
            }
            case 254: {
                this.consumeMethodDeclaration(true, true);
                break;
            }
            case 255: {
                this.consumeMethodDeclaration(false, false);
                break;
            }
            case 256: {
                this.consumeMethodHeader();
                break;
            }
            case 257: {
                this.consumeMethodHeader();
                break;
            }
            case 258: {
                this.consumeMethodHeaderNameWithTypeParameters(false);
                break;
            }
            case 259: {
                this.consumeMethodHeaderName(false);
                break;
            }
            case 260: {
                this.consumeMethodHeaderNameWithTypeParameters(false);
                break;
            }
            case 261: {
                this.consumeMethodHeaderName(false);
                break;
            }
            case 262: {
                this.consumePushCombineModifiers();
                break;
            }
            case 263: {
                this.consumeMethodHeaderRightParen();
                break;
            }
            case 264: {
                this.consumeMethodHeaderExtendedDims();
                break;
            }
            case 265: {
                this.consumeMethodHeaderThrowsClause();
                break;
            }
            case 266: {
                this.consumeConstructorHeader();
                break;
            }
            case 267: {
                this.consumeConstructorHeaderNameWithTypeParameters();
                break;
            }
            case 268: {
                this.consumeConstructorHeaderName();
                break;
            }
            case 270: {
                this.consumeFormalParameterList();
                break;
            }
            case 271: {
                this.consumeFormalParameter(false);
                break;
            }
            case 272: {
                this.consumeFormalParameter(true);
                break;
            }
            case 273: {
                this.consumeFormalParameter(true);
                break;
            }
            case 274: {
                this.consumeCatchFormalParameter();
                break;
            }
            case 275: {
                this.consumeCatchType();
                break;
            }
            case 276: {
                this.consumeUnionTypeAsClassType();
                break;
            }
            case 277: {
                this.consumeUnionType();
                break;
            }
            case 279: {
                this.consumeClassTypeList();
                break;
            }
            case 280: {
                this.consumeClassTypeElt();
                break;
            }
            case 281: {
                this.consumeMethodBody();
                break;
            }
            case 282: {
                this.consumeNestedMethod();
                break;
            }
            case 283: {
                this.consumeStaticInitializer();
                break;
            }
            case 284: {
                this.consumeStaticOnly();
                break;
            }
            case 285: {
                this.consumeConstructorDeclaration();
                break;
            }
            case 286: {
                this.consumeInvalidConstructorDeclaration();
                break;
            }
            case 287: {
                this.consumeExplicitConstructorInvocation(0, 3);
                break;
            }
            case 288: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(0, 3);
                break;
            }
            case 289: {
                this.consumeExplicitConstructorInvocation(0, 2);
                break;
            }
            case 290: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(0, 2);
                break;
            }
            case 291: {
                this.consumeExplicitConstructorInvocation(1, 2);
                break;
            }
            case 292: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(1, 2);
                break;
            }
            case 293: {
                this.consumeExplicitConstructorInvocation(2, 2);
                break;
            }
            case 294: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(2, 2);
                break;
            }
            case 295: {
                this.consumeExplicitConstructorInvocation(1, 3);
                break;
            }
            case 296: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(1, 3);
                break;
            }
            case 297: {
                this.consumeExplicitConstructorInvocation(2, 3);
                break;
            }
            case 298: {
                this.consumeExplicitConstructorInvocationWithTypeArguments(2, 3);
                break;
            }
            case 299: {
                this.consumeInterfaceDeclaration();
                break;
            }
            case 300: {
                this.consumeInterfaceHeader();
                break;
            }
            case 301: {
                this.consumeTypeHeaderNameWithTypeParameters();
                break;
            }
            case 303: {
                this.consumeInterfaceHeaderName1();
                break;
            }
            case 304: {
                this.consumeInterfaceHeaderExtends();
                break;
            }
            case 307: {
                this.consumeInterfaceMemberDeclarations();
                break;
            }
            case 308: {
                this.consumeEmptyTypeDeclaration();
                break;
            }
            case 310: {
                this.consumeInterfaceMethodDeclaration(false);
                break;
            }
            case 311: {
                this.consumeInterfaceMethodDeclaration(false);
                break;
            }
            case 312: {
                this.consumeInterfaceMethodDeclaration(true);
                break;
            }
            case 313: {
                this.consumeInvalidConstructorDeclaration(true);
                break;
            }
            case 314: {
                this.consumeInvalidConstructorDeclaration(false);
                break;
            }
            case 325: {
                this.consumeRecordDeclaration();
                break;
            }
            case 326: {
                this.consumeRecordHeaderPart();
                break;
            }
            case 327: {
                this.consumeRecordHeaderNameWithTypeParameters();
                break;
            }
            case 329: {
                this.consumeRecordHeaderName1();
                break;
            }
            case 330: {
                this.consumeRecordComponentHeaderRightParen();
                break;
            }
            case 331: {
                this.consumeRecordHeader();
                break;
            }
            case 332: {
                this.consumeRecordComponentsopt();
                break;
            }
            case 335: {
                this.consumeRecordComponents();
                break;
            }
            case 337: {
                this.consumeRecordComponent(false);
                break;
            }
            case 338: {
                this.consumeRecordComponent(true);
                break;
            }
            case 339: {
                this.consumeRecordComponent(true);
                break;
            }
            case 340: {
                this.consumeRecordBody();
                break;
            }
            case 341: {
                this.consumeEmptyRecordBodyDeclaration();
                break;
            }
            case 344: {
                this.consumeRecordBodyDeclarations();
                break;
            }
            case 345: {
                this.consumeRecordBodyDeclaration();
                break;
            }
            case 346: {
                this.consumeRecordBodyDeclaration();
                break;
            }
            case 347: {
                this.consumeCompactConstructorDeclaration();
                break;
            }
            case 348: {
                this.consumeCompactConstructorHeader();
                break;
            }
            case 349: {
                this.consumeCompactConstructorHeaderName();
                break;
            }
            case 350: {
                this.consumeCompactConstructorHeaderNameWithTypeParameters();
                break;
            }
            case 352: {
                this.consumeInstanceOfExpression();
                break;
            }
            case 354: {
                this.consumeInstanceOfRHS();
                break;
            }
            case 355: {
                this.consumeInstanceOfClassic();
                break;
            }
            case 356: {
                this.consumeInstanceofPattern();
                break;
            }
            case 358: {
                this.consumePushLeftBrace();
                break;
            }
            case 359: {
                this.consumeEmptyArrayInitializer();
                break;
            }
            case 360: {
                this.consumeArrayInitializer();
                break;
            }
            case 361: {
                this.consumeArrayInitializer();
                break;
            }
            case 363: {
                this.consumeVariableInitializers();
                break;
            }
            case 364: {
                this.consumeBlock();
                break;
            }
            case 365: {
                this.consumeOpenBlock();
                break;
            }
            case 366: {
                this.consumeBlockStatement();
                break;
            }
            case 367: {
                this.consumeBlockStatements();
                break;
            }
            case 375: {
                this.consumeInvalidInterfaceDeclaration();
                break;
            }
            case 376: {
                this.consumeInvalidAnnotationTypeDeclaration();
                break;
            }
            case 377: {
                this.consumeInvalidEnumDeclaration();
                break;
            }
            case 378: {
                this.consumeLocalVariableDeclarationStatement();
                break;
            }
            case 379: {
                this.consumeLocalVariableDeclaration();
                break;
            }
            case 380: {
                this.consumeLocalVariableDeclaration();
                break;
            }
            case 381: {
                this.consumePushModifiers();
                break;
            }
            case 382: {
                this.consumePushModifiersForHeader();
                break;
            }
            case 383: {
                this.consumePushRealModifiers();
                break;
            }
            case 411: {
                this.consumeEmptyStatement();
                break;
            }
            case 412: {
                this.consumeStatementLabel();
                break;
            }
            case 413: {
                this.consumeStatementLabel();
                break;
            }
            case 414: {
                this.consumeLabel();
                break;
            }
            case 415: {
                this.consumeExpressionStatement();
                break;
            }
            case 424: {
                this.consumeStatementIfNoElse();
                break;
            }
            case 425: {
                this.consumeStatementIfWithElse();
                break;
            }
            case 426: {
                this.consumeStatementIfWithElse();
                break;
            }
            case 427: {
                this.consumeStatementSwitch();
                break;
            }
            case 428: {
                this.consumeEmptySwitchBlock();
                break;
            }
            case 431: {
                this.consumeSwitchBlock();
                break;
            }
            case 433: {
                this.consumeSwitchBlockStatements();
                break;
            }
            case 435: {
                this.consumeSwitchBlockStatement();
                break;
            }
            case 437: {
                this.consumeSwitchLabels();
                break;
            }
            case 438: {
                this.consumeCaseLabel();
                break;
            }
            case 439: {
                this.consumeDefaultLabel();
                break;
            }
            case 442: {
                this.consumeSwitchExpression();
                break;
            }
            case 445: {
                this.consumeSwitchLabeledRule();
                break;
            }
            case 446: {
                this.consumeSwitchLabeledExpression();
                break;
            }
            case 447: {
                this.consumeSwitchLabeledBlock();
                break;
            }
            case 448: {
                this.consumeSwitchLabeledThrowStatement();
                break;
            }
            case 449: {
                this.consumeDefaultLabelExpr();
                break;
            }
            case 450: {
                this.consumeCaseLabelExpr();
                break;
            }
            case 451: {
                this.consumeSwitchLabelCaseLhs();
                break;
            }
            case 452: {
                this.consumeStatementYield();
                break;
            }
            case 453: {
                this.consumeStatementWhile();
                break;
            }
            case 454: {
                this.consumeStatementWhile();
                break;
            }
            case 455: {
                this.consumeStatementDo();
                break;
            }
            case 456: {
                this.consumeStatementFor();
                break;
            }
            case 457: {
                this.consumeStatementFor();
                break;
            }
            case 458: {
                this.consumeForInit();
                break;
            }
            case 462: {
                this.consumeStatementExpressionList();
                break;
            }
            case 463: {
                this.consumeSimpleAssertStatement();
                break;
            }
            case 464: {
                this.consumeAssertStatement();
                break;
            }
            case 465: {
                this.consumeStatementBreak();
                break;
            }
            case 466: {
                this.consumeStatementBreakWithLabel();
                break;
            }
            case 467: {
                this.consumeStatementContinue();
                break;
            }
            case 468: {
                this.consumeStatementContinueWithLabel();
                break;
            }
            case 469: {
                this.consumeStatementReturn();
                break;
            }
            case 470: {
                this.consumeStatementThrow();
                break;
            }
            case 471: {
                this.consumeThrowExpression();
                break;
            }
            case 472: {
                this.consumeStatementSynchronized();
                break;
            }
            case 473: {
                this.consumeOnlySynchronized();
                break;
            }
            case 474: {
                this.consumeStatementTry(false, false);
                break;
            }
            case 475: {
                this.consumeStatementTry(true, false);
                break;
            }
            case 476: {
                this.consumeStatementTry(false, true);
                break;
            }
            case 477: {
                this.consumeStatementTry(true, true);
                break;
            }
            case 478: {
                this.consumeResourceSpecification();
                break;
            }
            case 479: {
                this.consumeResourceOptionalTrailingSemiColon(false);
                break;
            }
            case 480: {
                this.consumeResourceOptionalTrailingSemiColon(true);
                break;
            }
            case 481: {
                this.consumeSingleResource();
                break;
            }
            case 482: {
                this.consumeMultipleResources();
                break;
            }
            case 483: {
                this.consumeResourceOptionalTrailingSemiColon(true);
                break;
            }
            case 484: {
                this.consumeResourceAsLocalVariableDeclaration();
                break;
            }
            case 485: {
                this.consumeResourceAsLocalVariableDeclaration();
                break;
            }
            case 486: {
                this.consumeResourceAsLocalVariable();
                break;
            }
            case 487: {
                this.consumeResourceAsFieldAccess();
                break;
            }
            case 489: {
                this.consumeExitTryBlock();
                break;
            }
            case 491: {
                this.consumeCatches();
                break;
            }
            case 492: {
                this.consumeStatementCatch();
                break;
            }
            case 494: {
                this.consumeLeftParen();
                break;
            }
            case 495: {
                this.consumeRightParen();
                break;
            }
            case 500: {
                this.consumePrimaryNoNewArrayThis();
                break;
            }
            case 501: {
                this.consumePrimaryNoNewArray();
                break;
            }
            case 502: {
                this.consumePrimaryNoNewArrayWithName();
                break;
            }
            case 505: {
                this.consumePrimaryNoNewArrayNameThis();
                break;
            }
            case 506: {
                this.consumeQualifiedSuperReceiver();
                break;
            }
            case 507: {
                this.consumePrimaryNoNewArrayName();
                break;
            }
            case 508: {
                this.consumePrimaryNoNewArrayArrayType();
                break;
            }
            case 509: {
                this.consumePrimaryNoNewArrayPrimitiveArrayType();
                break;
            }
            case 510: {
                this.consumePrimaryNoNewArrayPrimitiveType();
                break;
            }
            case 516: {
                this.consumeReferenceExpressionTypeArgumentsAndTrunk(false);
                break;
            }
            case 517: {
                this.consumeReferenceExpressionTypeArgumentsAndTrunk(true);
                break;
            }
            case 518: {
                this.consumeReferenceExpressionTypeForm(true);
                break;
            }
            case 519: {
                this.consumeReferenceExpressionTypeForm(false);
                break;
            }
            case 520: {
                this.consumeReferenceExpressionGenericTypeForm();
                break;
            }
            case 521: {
                this.consumeReferenceExpressionPrimaryForm();
                break;
            }
            case 522: {
                this.consumeReferenceExpressionPrimaryForm();
                break;
            }
            case 523: {
                this.consumeReferenceExpressionSuperForm();
                break;
            }
            case 524: {
                this.consumeEmptyTypeArguments();
                break;
            }
            case 526: {
                this.consumeIdentifierOrNew(false);
                break;
            }
            case 527: {
                this.consumeIdentifierOrNew(true);
                break;
            }
            case 528: {
                this.consumeLambdaExpression();
                break;
            }
            case 529: {
                this.consumeNestedLambda();
                break;
            }
            case 530: {
                this.consumeTypeElidedLambdaParameter(false);
                break;
            }
            case 536: {
                this.consumeFormalParameterList();
                break;
            }
            case 537: {
                this.consumeTypeElidedLambdaParameter(true);
                break;
            }
            case 540: {
                this.consumeElidedLeftBraceAndReturn();
                break;
            }
            case 541: {
                this.consumeAllocationHeader();
                break;
            }
            case 542: {
                this.consumeClassInstanceCreationExpressionWithTypeArguments();
                break;
            }
            case 543: {
                this.consumeClassInstanceCreationExpression();
                break;
            }
            case 544: {
                this.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            }
            case 545: {
                this.consumeClassInstanceCreationExpressionQualified();
                break;
            }
            case 546: {
                this.consumeClassInstanceCreationExpressionQualified();
                break;
            }
            case 547: {
                this.consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
                break;
            }
            case 548: {
                this.consumeEnterInstanceCreationArgumentList();
                break;
            }
            case 549: {
                this.consumeClassInstanceCreationExpressionName();
                break;
            }
            case 550: {
                this.consumeClassBodyopt();
                break;
            }
            case 552: {
                this.consumeEnterAnonymousClassBody(false);
                break;
            }
            case 553: {
                this.consumeClassBodyopt();
                break;
            }
            case 555: {
                this.consumeEnterAnonymousClassBody(true);
                break;
            }
            case 557: {
                this.consumeArgumentList();
                break;
            }
            case 558: {
                this.consumeArrayCreationHeader();
                break;
            }
            case 559: {
                this.consumeArrayCreationHeader();
                break;
            }
            case 560: {
                this.consumeArrayCreationExpressionWithoutInitializer();
                break;
            }
            case 561: {
                this.consumeArrayCreationExpressionWithInitializer();
                break;
            }
            case 562: {
                this.consumeArrayCreationExpressionWithoutInitializer();
                break;
            }
            case 563: {
                this.consumeArrayCreationExpressionWithInitializer();
                break;
            }
            case 565: {
                this.consumeDimWithOrWithOutExprs();
                break;
            }
            case 567: {
                this.consumeDimWithOrWithOutExpr();
                break;
            }
            case 568: {
                this.consumeDims();
                break;
            }
            case 571: {
                this.consumeOneDimLoop(false);
                break;
            }
            case 572: {
                this.consumeOneDimLoop(true);
                break;
            }
            case 573: {
                this.consumeFieldAccess(false);
                break;
            }
            case 574: {
                this.consumeFieldAccess(true);
                break;
            }
            case 575: {
                this.consumeFieldAccess(false);
                break;
            }
            case 576: {
                this.consumeMethodInvocationName();
                break;
            }
            case 577: {
                this.consumeMethodInvocationNameWithTypeArguments();
                break;
            }
            case 578: {
                this.consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            }
            case 579: {
                this.consumeMethodInvocationPrimary();
                break;
            }
            case 580: {
                this.consumeMethodInvocationPrimary();
                break;
            }
            case 581: {
                this.consumeMethodInvocationPrimaryWithTypeArguments();
                break;
            }
            case 582: {
                this.consumeMethodInvocationSuperWithTypeArguments();
                break;
            }
            case 583: {
                this.consumeMethodInvocationSuper();
                break;
            }
            case 584: {
                this.consumeArrayAccess(true);
                break;
            }
            case 585: {
                this.consumeArrayAccess(false);
                break;
            }
            case 586: {
                this.consumeArrayAccess(false);
                break;
            }
            case 588: {
                this.consumePostfixExpression();
                break;
            }
            case 591: {
                this.consumeUnaryExpression(14, true);
                break;
            }
            case 592: {
                this.consumeUnaryExpression(13, true);
                break;
            }
            case 593: {
                this.consumePushPosition();
                break;
            }
            case 596: {
                this.consumeUnaryExpression(14);
                break;
            }
            case 597: {
                this.consumeUnaryExpression(13);
                break;
            }
            case 599: {
                this.consumeUnaryExpression(14, false);
                break;
            }
            case 600: {
                this.consumeUnaryExpression(13, false);
                break;
            }
            case 602: {
                this.consumeUnaryExpression(12);
                break;
            }
            case 603: {
                this.consumeUnaryExpression(11);
                break;
            }
            case 605: {
                this.consumeCastExpressionWithPrimitiveType();
                break;
            }
            case 606: {
                this.consumeCastExpressionWithGenericsArray();
                break;
            }
            case 607: {
                this.consumeCastExpressionWithQualifiedGenericsArray();
                break;
            }
            case 608: {
                this.consumeCastExpressionLL1();
                break;
            }
            case 609: {
                this.consumeCastExpressionLL1WithBounds();
                break;
            }
            case 610: {
                this.consumeCastExpressionWithNameArray();
                break;
            }
            case 611: {
                this.consumeZeroAdditionalBounds();
                break;
            }
            case 615: {
                this.consumeOnlyTypeArgumentsForCastExpression();
                break;
            }
            case 616: {
                this.consumeInsideCastExpression();
                break;
            }
            case 617: {
                this.consumeInsideCastExpressionLL1();
                break;
            }
            case 618: {
                this.consumeInsideCastExpressionLL1WithBounds();
                break;
            }
            case 619: {
                this.consumeInsideCastExpressionWithQualifiedGenerics();
                break;
            }
            case 621: {
                this.consumeBinaryExpression(15);
                break;
            }
            case 622: {
                this.consumeBinaryExpression(9);
                break;
            }
            case 623: {
                this.consumeBinaryExpression(16);
                break;
            }
            case 625: {
                this.consumeBinaryExpression(14);
                break;
            }
            case 626: {
                this.consumeBinaryExpression(13);
                break;
            }
            case 628: {
                this.consumeBinaryExpression(10);
                break;
            }
            case 629: {
                this.consumeBinaryExpression(17);
                break;
            }
            case 630: {
                this.consumeBinaryExpression(19);
                break;
            }
            case 632: {
                this.consumeBinaryExpression(4);
                break;
            }
            case 633: {
                this.consumeBinaryExpression(6);
                break;
            }
            case 634: {
                this.consumeBinaryExpression(5);
                break;
            }
            case 635: {
                this.consumeBinaryExpression(7);
                break;
            }
            case 637: {
                this.consumeEqualityExpression(18);
                break;
            }
            case 638: {
                this.consumeEqualityExpression(29);
                break;
            }
            case 640: {
                this.consumeBinaryExpression(2);
                break;
            }
            case 642: {
                this.consumeBinaryExpression(8);
                break;
            }
            case 644: {
                this.consumeBinaryExpression(3);
                break;
            }
            case 646: {
                this.consumeBinaryExpression(0);
                break;
            }
            case 648: {
                this.consumeBinaryExpression(1);
                break;
            }
            case 650: {
                this.consumeConditionalExpression(23);
                break;
            }
            case 653: {
                this.consumeAssignment();
                break;
            }
            case 655: {
                this.ignoreExpressionAssignment();
                break;
            }
            case 656: {
                this.consumeAssignmentOperator(30);
                break;
            }
            case 657: {
                this.consumeAssignmentOperator(15);
                break;
            }
            case 658: {
                this.consumeAssignmentOperator(9);
                break;
            }
            case 659: {
                this.consumeAssignmentOperator(16);
                break;
            }
            case 660: {
                this.consumeAssignmentOperator(14);
                break;
            }
            case 661: {
                this.consumeAssignmentOperator(13);
                break;
            }
            case 662: {
                this.consumeAssignmentOperator(10);
                break;
            }
            case 663: {
                this.consumeAssignmentOperator(17);
                break;
            }
            case 664: {
                this.consumeAssignmentOperator(19);
                break;
            }
            case 665: {
                this.consumeAssignmentOperator(2);
                break;
            }
            case 666: {
                this.consumeAssignmentOperator(8);
                break;
            }
            case 667: {
                this.consumeAssignmentOperator(3);
                break;
            }
            case 668: {
                this.consumeExpression();
                break;
            }
            case 671: {
                this.consumeEmptyExpression();
                break;
            }
            case 674: {
                this.consumeConstantExpressions();
                break;
            }
            case 678: {
                this.consumeEmptyClassBodyDeclarationsopt();
                break;
            }
            case 679: {
                this.consumeClassBodyDeclarationsopt();
                break;
            }
            case 680: {
                this.consumeDefaultModifiers();
                break;
            }
            case 681: {
                this.consumeModifiers();
                break;
            }
            case 682: {
                this.consumeEmptyBlockStatementsopt();
                break;
            }
            case 684: {
                this.consumeEmptyDimsopt();
                break;
            }
            case 686: {
                this.consumeEmptyArgumentListopt();
                break;
            }
            case 690: {
                this.consumeFormalParameterListopt();
                break;
            }
            case 697: {
                this.consumeClassHeaderPermittedSubclasses();
                break;
            }
            case 700: {
                this.consumeInterfaceHeaderPermittedSubClassesAndSubInterfaces();
                break;
            }
            case 701: {
                this.consumeEmptyInterfaceMemberDeclarationsopt();
                break;
            }
            case 702: {
                this.consumeInterfaceMemberDeclarationsopt();
                break;
            }
            case 703: {
                this.consumeNestedType();
                break;
            }
            case 704: {
                this.consumeEmptyForInitopt();
                break;
            }
            case 706: {
                this.consumeEmptyForUpdateopt();
                break;
            }
            case 710: {
                this.consumeEmptyCatchesopt();
                break;
            }
            case 712: {
                this.consumeEnumDeclaration();
                break;
            }
            case 713: {
                this.consumeEnumHeader();
                break;
            }
            case 714: {
                this.consumeEnumHeaderName();
                break;
            }
            case 715: {
                this.consumeEnumHeaderNameWithTypeParameters();
                break;
            }
            case 716: {
                this.consumeEnumBodyNoConstants();
                break;
            }
            case 717: {
                this.consumeEnumBodyNoConstants();
                break;
            }
            case 718: {
                this.consumeEnumBodyWithConstants();
                break;
            }
            case 719: {
                this.consumeEnumBodyWithConstants();
                break;
            }
            case 721: {
                this.consumeEnumConstants();
                break;
            }
            case 722: {
                this.consumeEnumConstantHeaderName();
                break;
            }
            case 723: {
                this.consumeEnumConstantHeader();
                break;
            }
            case 724: {
                this.consumeEnumConstantWithClassBody();
                break;
            }
            case 725: {
                this.consumeEnumConstantNoClassBody();
                break;
            }
            case 726: {
                this.consumeArguments();
                break;
            }
            case 727: {
                this.consumeEmptyArguments();
                break;
            }
            case 729: {
                this.consumeEnumDeclarations();
                break;
            }
            case 730: {
                this.consumeEmptyEnumDeclarations();
                break;
            }
            case 732: {
                this.consumeEnhancedForStatement();
                break;
            }
            case 733: {
                this.consumeEnhancedForStatement();
                break;
            }
            case 734: {
                this.consumeEnhancedForStatementHeaderInit(false);
                break;
            }
            case 735: {
                this.consumeEnhancedForStatementHeaderInit(true);
                break;
            }
            case 736: {
                this.consumeEnhancedForStatementHeader();
                break;
            }
            case 737: {
                this.consumeImportDeclaration();
                break;
            }
            case 738: {
                this.consumeSingleStaticImportDeclarationName();
                break;
            }
            case 739: {
                this.consumeImportDeclaration();
                break;
            }
            case 740: {
                this.consumeStaticImportOnDemandDeclarationName();
                break;
            }
            case 741: {
                this.consumeTypeArguments();
                break;
            }
            case 742: {
                this.consumeOnlyTypeArguments();
                break;
            }
            case 744: {
                this.consumeTypeArgumentList1();
                break;
            }
            case 746: {
                this.consumeTypeArgumentList();
                break;
            }
            case 747: {
                this.consumeTypeArgument();
                break;
            }
            case 751: {
                this.consumeReferenceType1();
                break;
            }
            case 752: {
                this.consumeTypeArgumentReferenceType1();
                break;
            }
            case 754: {
                this.consumeTypeArgumentList2();
                break;
            }
            case 757: {
                this.consumeReferenceType2();
                break;
            }
            case 758: {
                this.consumeTypeArgumentReferenceType2();
                break;
            }
            case 760: {
                this.consumeTypeArgumentList3();
                break;
            }
            case 763: {
                this.consumeReferenceType3();
                break;
            }
            case 764: {
                this.consumeWildcard();
                break;
            }
            case 765: {
                this.consumeWildcardWithBounds();
                break;
            }
            case 766: {
                this.consumeWildcardBoundsExtends();
                break;
            }
            case 767: {
                this.consumeWildcardBoundsSuper();
                break;
            }
            case 768: {
                this.consumeWildcard1();
                break;
            }
            case 769: {
                this.consumeWildcard1WithBounds();
                break;
            }
            case 770: {
                this.consumeWildcardBounds1Extends();
                break;
            }
            case 771: {
                this.consumeWildcardBounds1Super();
                break;
            }
            case 772: {
                this.consumeWildcard2();
                break;
            }
            case 773: {
                this.consumeWildcard2WithBounds();
                break;
            }
            case 774: {
                this.consumeWildcardBounds2Extends();
                break;
            }
            case 775: {
                this.consumeWildcardBounds2Super();
                break;
            }
            case 776: {
                this.consumeWildcard3();
                break;
            }
            case 777: {
                this.consumeWildcard3WithBounds();
                break;
            }
            case 778: {
                this.consumeWildcardBounds3Extends();
                break;
            }
            case 779: {
                this.consumeWildcardBounds3Super();
                break;
            }
            case 780: {
                this.consumeTypeParameterHeader();
                break;
            }
            case 781: {
                this.consumeTypeParameters();
                break;
            }
            case 783: {
                this.consumeTypeParameterList();
                break;
            }
            case 785: {
                this.consumeTypeParameterWithExtends();
                break;
            }
            case 786: {
                this.consumeTypeParameterWithExtendsAndBounds();
                break;
            }
            case 788: {
                this.consumeAdditionalBoundList();
                break;
            }
            case 789: {
                this.consumeAdditionalBound();
                break;
            }
            case 791: {
                this.consumeTypeParameterList1();
                break;
            }
            case 792: {
                this.consumeTypeParameter1();
                break;
            }
            case 793: {
                this.consumeTypeParameter1WithExtends();
                break;
            }
            case 794: {
                this.consumeTypeParameter1WithExtendsAndBounds();
                break;
            }
            case 796: {
                this.consumeAdditionalBoundList1();
                break;
            }
            case 797: {
                this.consumeAdditionalBound1();
                break;
            }
            case 803: {
                this.consumeUnaryExpression(14);
                break;
            }
            case 804: {
                this.consumeUnaryExpression(13);
                break;
            }
            case 807: {
                this.consumeUnaryExpression(12);
                break;
            }
            case 808: {
                this.consumeUnaryExpression(11);
                break;
            }
            case 811: {
                this.consumeBinaryExpression(15);
                break;
            }
            case 812: {
                this.consumeBinaryExpressionWithName(15);
                break;
            }
            case 813: {
                this.consumeBinaryExpression(9);
                break;
            }
            case 814: {
                this.consumeBinaryExpressionWithName(9);
                break;
            }
            case 815: {
                this.consumeBinaryExpression(16);
                break;
            }
            case 816: {
                this.consumeBinaryExpressionWithName(16);
                break;
            }
            case 818: {
                this.consumeBinaryExpression(14);
                break;
            }
            case 819: {
                this.consumeBinaryExpressionWithName(14);
                break;
            }
            case 820: {
                this.consumeBinaryExpression(13);
                break;
            }
            case 821: {
                this.consumeBinaryExpressionWithName(13);
                break;
            }
            case 823: {
                this.consumeBinaryExpression(10);
                break;
            }
            case 824: {
                this.consumeBinaryExpressionWithName(10);
                break;
            }
            case 825: {
                this.consumeBinaryExpression(17);
                break;
            }
            case 826: {
                this.consumeBinaryExpressionWithName(17);
                break;
            }
            case 827: {
                this.consumeBinaryExpression(19);
                break;
            }
            case 828: {
                this.consumeBinaryExpressionWithName(19);
                break;
            }
            case 830: {
                this.consumeBinaryExpression(4);
                break;
            }
            case 831: {
                this.consumeBinaryExpressionWithName(4);
                break;
            }
            case 832: {
                this.consumeBinaryExpression(6);
                break;
            }
            case 833: {
                this.consumeBinaryExpressionWithName(6);
                break;
            }
            case 834: {
                this.consumeBinaryExpression(5);
                break;
            }
            case 835: {
                this.consumeBinaryExpressionWithName(5);
                break;
            }
            case 836: {
                this.consumeBinaryExpression(7);
                break;
            }
            case 837: {
                this.consumeBinaryExpressionWithName(7);
                break;
            }
            case 839: {
                this.consumeInstanceOfExpressionWithName();
                break;
            }
            case 840: {
                this.consumeInstanceOfExpression();
                break;
            }
            case 842: {
                this.consumeEqualityExpression(18);
                break;
            }
            case 843: {
                this.consumeEqualityExpressionWithName(18);
                break;
            }
            case 844: {
                this.consumeEqualityExpression(29);
                break;
            }
            case 845: {
                this.consumeEqualityExpressionWithName(29);
                break;
            }
            case 847: {
                this.consumeBinaryExpression(2);
                break;
            }
            case 848: {
                this.consumeBinaryExpressionWithName(2);
                break;
            }
            case 850: {
                this.consumeBinaryExpression(8);
                break;
            }
            case 851: {
                this.consumeBinaryExpressionWithName(8);
                break;
            }
            case 853: {
                this.consumeBinaryExpression(3);
                break;
            }
            case 854: {
                this.consumeBinaryExpressionWithName(3);
                break;
            }
            case 856: {
                this.consumeBinaryExpression(0);
                break;
            }
            case 857: {
                this.consumeBinaryExpressionWithName(0);
                break;
            }
            case 859: {
                this.consumeBinaryExpression(1);
                break;
            }
            case 860: {
                this.consumeBinaryExpressionWithName(1);
                break;
            }
            case 862: {
                this.consumeConditionalExpression(23);
                break;
            }
            case 863: {
                this.consumeConditionalExpressionWithName(23);
                break;
            }
            case 867: {
                this.consumeAnnotationTypeDeclarationHeaderName();
                break;
            }
            case 868: {
                this.consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            }
            case 869: {
                this.consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
                break;
            }
            case 870: {
                this.consumeAnnotationTypeDeclarationHeaderName();
                break;
            }
            case 871: {
                this.consumeAnnotationTypeDeclarationHeader();
                break;
            }
            case 872: {
                this.consumeAnnotationTypeDeclaration();
                break;
            }
            case 874: {
                this.consumeEmptyAnnotationTypeMemberDeclarationsopt();
                break;
            }
            case 875: {
                this.consumeAnnotationTypeMemberDeclarationsopt();
                break;
            }
            case 877: {
                this.consumeAnnotationTypeMemberDeclarations();
                break;
            }
            case 878: {
                this.consumeMethodHeaderNameWithTypeParameters(true);
                break;
            }
            case 879: {
                this.consumeMethodHeaderName(true);
                break;
            }
            case 880: {
                this.consumeEmptyMethodHeaderDefaultValue();
                break;
            }
            case 881: {
                this.consumeMethodHeaderDefaultValue();
                break;
            }
            case 882: {
                this.consumeMethodHeader();
                break;
            }
            case 883: {
                this.consumeAnnotationTypeMemberDeclaration();
                break;
            }
            case 891: {
                this.consumeAnnotationName();
                break;
            }
            case 892: {
                this.consumeNormalAnnotation(false);
                break;
            }
            case 893: {
                this.consumeEmptyMemberValuePairsopt();
                break;
            }
            case 896: {
                this.consumeMemberValuePairs();
                break;
            }
            case 897: {
                this.consumeMemberValuePair();
                break;
            }
            case 898: {
                this.consumeEnterMemberValue();
                break;
            }
            case 899: {
                this.consumeExitMemberValue();
                break;
            }
            case 901: {
                this.consumeMemberValueAsName();
                break;
            }
            case 904: {
                this.consumeMemberValueArrayInitializer();
                break;
            }
            case 905: {
                this.consumeMemberValueArrayInitializer();
                break;
            }
            case 906: {
                this.consumeEmptyMemberValueArrayInitializer();
                break;
            }
            case 907: {
                this.consumeEmptyMemberValueArrayInitializer();
                break;
            }
            case 908: {
                this.consumeEnterMemberValueArrayInitializer();
                break;
            }
            case 910: {
                this.consumeMemberValues();
                break;
            }
            case 911: {
                this.consumeMarkerAnnotation(false);
                break;
            }
            case 912: {
                this.consumeSingleMemberAnnotationMemberValue();
                break;
            }
            case 913: {
                this.consumeSingleMemberAnnotation(false);
                break;
            }
            case 914: {
                this.consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            }
            case 915: {
                this.consumeRecoveryMethodHeaderName();
                break;
            }
            case 916: {
                this.consumeRecoveryMethodHeaderNameWithTypeParameters();
                break;
            }
            case 917: {
                this.consumeRecoveryMethodHeaderName();
                break;
            }
            case 918: {
                this.consumeMethodHeader();
                break;
            }
            case 919: {
                this.consumeMethodHeader();
            }
        }
    }

    protected void consumeVariableDeclaratorIdParameter() {
        this.pushOnIntStack(1);
    }

    protected void consumeExplicitThisParameter(boolean isQualified) {
        NameReference qualifyingNameReference = null;
        if (isQualified) {
            qualifyingNameReference = this.getUnspecifiedReference(false);
        }
        this.pushOnExpressionStack(qualifyingNameReference);
        int thisStart = this.intStack[this.intPtr--];
        this.pushIdentifier(ConstantPool.This, ((long)thisStart << 32) + (long)(thisStart + 3));
        this.pushOnIntStack(0);
        this.pushOnIntStack(0);
    }

    protected boolean isAssistParser() {
        return false;
    }

    protected void consumeNestedLambda() {
        this.consumeNestedType();
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] + 1;
        LambdaExpression lambda = new LambdaExpression(this.compilationUnit.compilationResult, this.isAssistParser());
        this.pushOnAstStack(lambda);
        this.processingLambdaParameterList = true;
    }

    protected void consumeLambdaHeader() {
        int arrowPosition = this.scanner.currentPosition - 1;
        Argument[] arguments = null;
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        if (length != 0) {
            arguments = new Argument[length];
            System.arraycopy(this.astStack, this.astPtr + 1, arguments, 0, length);
        }
        int i = 0;
        while (i < length) {
            void argument = arguments[i];
            if (argument.isReceiver()) {
                this.problemReporter().illegalThis((Argument)argument);
            }
            if (argument.name.length == 1 && argument.name[0] == '_') {
                this.problemReporter().illegalUseOfUnderscoreAsAnIdentifier(argument.sourceStart, argument.sourceEnd, true);
            }
            ++i;
        }
        LambdaExpression lexp = (LambdaExpression)this.astStack[this.astPtr];
        lexp.setArguments(arguments);
        lexp.setArrowPosition(arrowPosition);
        lexp.sourceEnd = this.intStack[this.intPtr--];
        lexp.sourceStart = this.intStack[this.intPtr--];
        lexp.hasParentheses = this.scanner.getSource()[lexp.sourceStart] == '(';
        this.listLength -= arguments == null ? 0 : arguments.length;
        this.processingLambdaParameterList = false;
        if (this.currentElement != null) {
            this.lastCheckPoint = arrowPosition + 1;
            ++this.currentElement.lambdaNestLevel;
        }
    }

    private void setArgumentsTypeVar(LambdaExpression lexp) {
        Argument[] args = lexp.arguments;
        if (!this.parsingJava11Plus || args == null || args.length == 0) {
            lexp.argumentsTypeVar = false;
            return;
        }
        boolean isVar = false;
        boolean mixReported = false;
        int i = 0;
        int l = args.length;
        while (i < l) {
            Argument arg = args[i];
            TypeReference type = arg.type;
            char[][] typeName = type != null ? type.getTypeName() : null;
            boolean prev = isVar;
            isVar = typeName != null && typeName.length == 1 && CharOperation.equals(typeName[0], TypeConstants.VAR);
            lexp.argumentsTypeVar |= isVar;
            if (i > 0 && prev != isVar && !mixReported) {
                this.problemReporter().varCannotBeMixedWithNonVarParams(isVar ? arg : args[i - 1]);
                mixReported = true;
            }
            if (isVar && (type.dimensions() > 0 || type.extraDimensions() > 0)) {
                this.problemReporter().varLocalCannotBeArray(arg);
            }
            ++i;
        }
    }

    protected void consumeLambdaExpression() {
        Statement body;
        --this.nestedType;
        --this.astLengthPtr;
        if ((body = (Statement)this.astStack[this.astPtr--]) instanceof Block && this.options.ignoreMethodBodies) {
            Statement oldBody = body;
            body = new Block(0);
            body.sourceStart = oldBody.sourceStart;
            body.sourceEnd = oldBody.sourceEnd;
        }
        LambdaExpression lexp = (LambdaExpression)this.astStack[this.astPtr--];
        --this.astLengthPtr;
        lexp.setBody(body);
        lexp.sourceEnd = body.sourceEnd;
        if (body instanceof Expression && ((Expression)body).isTrulyExpression()) {
            Expression expression = (Expression)body;
            expression.statementEnd = body.sourceEnd;
        }
        if (!this.parsingJava8Plus) {
            this.problemReporter().lambdaExpressionsNotBelow18(lexp);
        }
        this.setArgumentsTypeVar(lexp);
        this.pushOnExpressionStack(lexp);
        if (this.currentElement != null) {
            this.lastCheckPoint = body.sourceEnd + 1;
            --this.currentElement.lambdaNestLevel;
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LAMBDA);
        if (lexp.compilationResult.getCompilationUnit() == null) {
            int length = lexp.sourceEnd - lexp.sourceStart + 1;
            lexp.text = new char[length];
            System.arraycopy(this.scanner.getSource(), lexp.sourceStart, lexp.text, 0, length);
        }
    }

    protected Argument typeElidedArgument() {
        --this.identifierLengthPtr;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        Argument arg = new Argument(identifierName, namePositions, null, 0, true);
        arg.declarationSourceStart = (int)(namePositions >>> 32);
        return arg;
    }

    protected void consumeTypeElidedLambdaParameter(boolean parenthesized) {
        int modifier = 0;
        int annotationLength = 0;
        int modifiersStart = 0;
        if (parenthesized) {
            modifiersStart = this.intStack[this.intPtr--];
            modifier = this.intStack[this.intPtr--];
            annotationLength = this.expressionLengthStack[this.expressionLengthPtr--];
            this.expressionPtr -= annotationLength;
        }
        Argument arg = this.typeElidedArgument();
        if (modifier != 0 || annotationLength != 0) {
            this.problemReporter().illegalModifiersForElidedType(arg);
            arg.declarationSourceStart = modifiersStart;
        }
        if (!parenthesized) {
            this.pushOnIntStack(arg.declarationSourceStart);
            this.pushOnIntStack(arg.declarationSourceEnd);
        }
        this.pushOnAstStack(arg);
        ++this.listLength;
    }

    protected void consumeElidedLeftBraceAndReturn() {
        int stackLength = this.stateStackLengthStack.length;
        if (++this.valueLambdaNestDepth >= stackLength) {
            this.stateStackLengthStack = new int[stackLength + 4];
            System.arraycopy(this.stateStackLengthStack, 0, this.stateStackLengthStack, 0, stackLength);
        }
        this.stateStackLengthStack[this.valueLambdaNestDepth] = this.stateStackTop;
    }

    protected void consumeExpression() {
        if (this.valueLambdaNestDepth >= 0 && this.stateStackLengthStack[this.valueLambdaNestDepth] == this.stateStackTop - 1) {
            --this.valueLambdaNestDepth;
            this.scanner.ungetToken(this.currentToken);
            this.currentToken = 71;
            Expression exp = this.expressionStack[this.expressionPtr--];
            --this.expressionLengthPtr;
            this.pushOnAstStack(exp);
        }
    }

    protected void consumeIdentifierOrNew(boolean newForm) {
        if (newForm) {
            int newStart = this.intStack[this.intPtr--];
            this.pushIdentifier(ConstantPool.Init, ((long)newStart << 32) + (long)(newStart + 2));
        }
    }

    protected void consumeEmptyTypeArguments() {
        this.pushOnGenericsLengthStack(0);
    }

    public ReferenceExpression newReferenceExpression() {
        return new ReferenceExpression(this.scanner);
    }

    protected void consumeReferenceExpressionTypeForm(boolean isPrimitive) {
        int length;
        ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        if ((length = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            this.genericsPtr -= length;
            typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, length);
            --this.intPtr;
        }
        int dimension = this.intStack[this.intPtr--];
        boolean typeAnnotatedName = false;
        int i = this.identifierLengthStack[this.identifierLengthPtr];
        int j = 0;
        while (i > 0 && this.typeAnnotationLengthPtr >= 0) {
            length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr - j];
            if (length != 0) {
                typeAnnotatedName = true;
                break;
            }
            --i;
            ++j;
        }
        if (dimension > 0 || typeAnnotatedName) {
            if (!isPrimitive) {
                this.pushOnGenericsLengthStack(0);
                this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            }
            referenceExpression.initialize(this.compilationUnit.compilationResult, this.getTypeReference(dimension), typeArguments, selector, sourceEnd);
        } else {
            referenceExpression.initialize(this.compilationUnit.compilationResult, this.getUnspecifiedReference(), typeArguments, selector, sourceEnd);
        }
        if (CharOperation.equals(selector, TypeConstants.INIT) && referenceExpression.lhs instanceof NameReference) {
            referenceExpression.lhs.bits &= 0xFFFFFFFC;
        }
        this.consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpressionPrimaryForm() {
        int length;
        ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        if ((length = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            this.genericsPtr -= length;
            typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, length);
            --this.intPtr;
        }
        Expression primary = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        referenceExpression.initialize(this.compilationUnit.compilationResult, primary, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpressionSuperForm() {
        int length;
        ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        if ((length = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            this.genericsPtr -= length;
            typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, length);
            --this.intPtr;
        }
        SuperReference superReference = new SuperReference(this.intStack[this.intPtr--], this.endPosition);
        referenceExpression.initialize(this.compilationUnit.compilationResult, superReference, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }

    protected void consumeReferenceExpression(ReferenceExpression referenceExpression) {
        this.pushOnExpressionStack(referenceExpression);
        if (!this.parsingJava8Plus) {
            this.problemReporter().referenceExpressionsNotBelow18(referenceExpression);
        }
        if (referenceExpression.compilationResult.getCompilationUnit() == null) {
            int length = referenceExpression.sourceEnd - referenceExpression.sourceStart + 1;
            referenceExpression.text = new char[length];
            System.arraycopy(this.scanner.getSource(), referenceExpression.sourceStart, referenceExpression.text, 0, length);
        }
        this.referenceContext.compilationResult().hasFunctionalTypes = true;
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.METHOD_REFERENCE);
    }

    protected void consumeReferenceExpressionTypeArgumentsAndTrunk(boolean qualified) {
        this.pushOnIntStack(qualified ? 1 : 0);
        this.pushOnIntStack(this.scanner.startPosition - 1);
    }

    protected void consumeReferenceExpressionGenericTypeForm() {
        TypeReference type;
        int length;
        ReferenceExpression referenceExpression = this.newReferenceExpression();
        TypeReference[] typeArguments = null;
        int sourceEnd = (int)this.identifierPositionStack[this.identifierPtr];
        referenceExpression.nameSourceStart = (int)(this.identifierPositionStack[this.identifierPtr] >>> 32);
        char[] selector = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        if ((length = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
            this.genericsPtr -= length;
            typeArguments = new TypeReference[length];
            System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, length);
            --this.intPtr;
        }
        int typeSourceEnd = this.intStack[this.intPtr--];
        boolean qualified = this.intStack[this.intPtr--] != 0;
        int dims = this.intStack[this.intPtr--];
        if (qualified) {
            Annotation[][] annotationsOnDimensions = dims == 0 ? null : this.getAnnotationsOnDimensions(dims);
            TypeReference rightSide = this.getTypeReference(0);
            type = this.computeQualifiedGenericsFromRightSide(rightSide, dims, annotationsOnDimensions);
        } else {
            this.pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
            type = this.getTypeReference(dims);
        }
        --this.intPtr;
        type.sourceEnd = typeSourceEnd;
        referenceExpression.initialize(this.compilationUnit.compilationResult, type, typeArguments, selector, sourceEnd);
        this.consumeReferenceExpression(referenceExpression);
    }

    protected void consumeEnterInstanceCreationArgumentList() {
    }

    protected void consumeSimpleAssertStatement() {
        --this.expressionLengthPtr;
        this.pushOnAstStack(new AssertStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--]));
    }

    protected void consumeSingleMemberAnnotation(boolean isTypeAnnotation) {
        SingleMemberAnnotation singleMemberAnnotation = null;
        int oldIndex = this.identifierPtr;
        TypeReference typeReference = this.getAnnotationType();
        singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[this.intPtr--]);
        singleMemberAnnotation.memberValue = this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
        if (isTypeAnnotation) {
            this.pushOnTypeAnnotationStack(singleMemberAnnotation);
        } else {
            this.pushOnExpressionStack(singleMemberAnnotation);
        }
        if (this.currentElement != null) {
            this.annotationRecoveryCheckPoint(singleMemberAnnotation.sourceStart, singleMemberAnnotation.declarationSourceEnd);
            if (this.currentElement instanceof RecoveredAnnotation) {
                this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(singleMemberAnnotation, oldIndex);
            }
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            this.problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
        }
        this.recordStringLiterals = true;
    }

    protected void consumeSingleMemberAnnotationMemberValue() {
        if (this.currentElement != null && this.currentElement instanceof RecoveredAnnotation) {
            RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
            recoveredAnnotation.setKind(2);
        }
    }

    protected void consumeSingleResource() {
    }

    protected void consumeSingleStaticImportDeclarationName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ImportReference impt = new ImportReference(tokens, positions, false, 8);
        this.pushOnAstStack(impt);
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            impt.modifiers = 0;
            this.problemReporter().invalidUsageOfStaticImports(impt);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeSingleTypeImportDeclarationName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ImportReference impt = new ImportReference(tokens, positions, false, 0);
        this.pushOnAstStack(impt);
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeStatementBreak() {
        this.pushOnAstStack(new BreakStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        if (this.pendingRecoveredType != null) {
            if (this.pendingRecoveredType.allocation == null && this.endPosition <= this.pendingRecoveredType.declarationSourceEnd) {
                this.astStack[this.astPtr] = this.pendingRecoveredType;
                this.pendingRecoveredType = null;
                return;
            }
            this.pendingRecoveredType = null;
        }
    }

    protected void consumeStatementBreakWithLabel() {
        this.pushOnAstStack(new BreakStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        --this.identifierLengthPtr;
    }

    protected void consumeStatementYield() {
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            Expression expr = this.expressionStack[this.expressionPtr--];
            YieldStatement yieldStatement = new YieldStatement(expr, this.intStack[this.intPtr--], this.endStatementPosition);
            this.pushOnAstStack(yieldStatement);
        }
    }

    protected void consumeStatementCatch() {
        --this.astLengthPtr;
        this.listLength = 0;
    }

    protected void consumeStatementContinue() {
        this.pushOnAstStack(new ContinueStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementContinueWithLabel() {
        this.pushOnAstStack(new ContinueStatement(this.identifierStack[this.identifierPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        --this.identifierLengthPtr;
    }

    protected void consumeStatementDo() {
        --this.intPtr;
        Statement statement = (Statement)this.astStack[this.astPtr];
        --this.expressionLengthPtr;
        this.astStack[this.astPtr] = new DoStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementExpressionList() {
        this.concatExpressionLists();
    }

    protected void consumeStatementFor() {
        Statement[] inits;
        Statement[] updates;
        int length;
        Expression cond = null;
        boolean scope = true;
        --this.astLengthPtr;
        Statement statement = (Statement)this.astStack[this.astPtr--];
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) == 0) {
            updates = null;
        } else {
            this.expressionPtr -= length;
            updates = new Statement[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, updates, 0, length);
        }
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            cond = this.expressionStack[this.expressionPtr--];
        }
        if ((length = this.astLengthStack[this.astLengthPtr--]) == 0) {
            inits = null;
            scope = false;
        } else if (length == -1) {
            scope = false;
            length = this.expressionLengthStack[this.expressionLengthPtr--];
            this.expressionPtr -= length;
            inits = new Statement[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, inits, 0, length);
        } else {
            this.astPtr -= length;
            inits = new Statement[length];
            System.arraycopy(this.astStack, this.astPtr + 1, inits, 0, length);
        }
        this.pushOnAstStack(new ForStatement(inits, cond, updates, statement, scope, this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementIfNoElse() {
        --this.expressionLengthPtr;
        Statement thenStatement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], thenStatement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementIfWithElse() {
        --this.expressionLengthPtr;
        --this.astLengthPtr;
        this.astStack[--this.astPtr] = new IfStatement(this.expressionStack[this.expressionPtr--], (Statement)this.astStack[this.astPtr], (Statement)this.astStack[this.astPtr + 1], this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStatementLabel() {
        Statement statement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new LabeledStatement(this.identifierStack[this.identifierPtr], statement, this.identifierPositionStack[this.identifierPtr--], this.endStatementPosition);
        --this.identifierLengthPtr;
    }

    protected void consumeStatementReturn() {
        if (this.expressionLengthStack[this.expressionLengthPtr--] != 0) {
            this.pushOnAstStack(new ReturnStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
        } else {
            this.pushOnAstStack(new ReturnStatement(null, this.intStack[this.intPtr--], this.endStatementPosition));
        }
    }

    private SwitchStatement createSwitchStatementOrExpression(boolean isStmt) {
        int length;
        --this.nestedType;
        --this.switchNestingLevel;
        this.scanner.breakPreviewAllowed = this.switchNestingLevel > 0;
        SwitchStatement switchStatement = isStmt ? new SwitchStatement() : new SwitchExpression();
        --this.expressionLengthPtr;
        switchStatement.expression = this.expressionStack[this.expressionPtr--];
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            switchStatement.statements = new Statement[length];
            System.arraycopy(this.astStack, this.astPtr + 1, switchStatement.statements, 0, length);
        }
        switchStatement.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        this.pushOnAstStack(switchStatement);
        switchStatement.blockStart = this.intStack[this.intPtr--];
        switchStatement.sourceStart = this.intStack[this.intPtr--];
        switchStatement.sourceEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(switchStatement.blockStart, switchStatement.sourceEnd)) {
            switchStatement.bits |= 8;
        }
        return switchStatement;
    }

    protected void consumeStatementSwitch() {
        this.createSwitchStatementOrExpression(true);
    }

    protected void consumeStatementSynchronized() {
        if (this.astLengthStack[this.astLengthPtr] == 0) {
            this.astLengthStack[this.astLengthPtr] = 1;
            --this.expressionLengthPtr;
            this.astStack[++this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], null, this.intStack[this.intPtr--], this.endStatementPosition);
        } else {
            --this.expressionLengthPtr;
            this.astStack[this.astPtr] = new SynchronizedStatement(this.expressionStack[this.expressionPtr--], (Block)this.astStack[this.astPtr], this.intStack[this.intPtr--], this.endStatementPosition);
        }
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
    }

    protected void consumeStatementThrow() {
        --this.expressionLengthPtr;
        this.pushOnAstStack(new ThrowStatement(this.expressionStack[this.expressionPtr--], this.intStack[this.intPtr--], this.endStatementPosition));
    }

    protected void consumeStatementTry(boolean withFinally, boolean hasResources) {
        int length;
        TryStatement tryStmt = new TryStatement();
        if (withFinally) {
            --this.astLengthPtr;
            tryStmt.finallyBlock = (Block)this.astStack[this.astPtr--];
        }
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (length == 1) {
                tryStmt.catchBlocks = new Block[]{(Block)this.astStack[this.astPtr--]};
                tryStmt.catchArguments = new Argument[]{(Argument)this.astStack[this.astPtr--]};
            } else {
                tryStmt.catchBlocks = new Block[length];
                Block[] bks = tryStmt.catchBlocks;
                tryStmt.catchArguments = new Argument[length];
                Argument[] args = tryStmt.catchArguments;
                while (length-- > 0) {
                    bks[length] = (Block)this.astStack[this.astPtr--];
                    args[length] = (Argument)this.astStack[this.astPtr--];
                }
            }
        }
        --this.astLengthPtr;
        tryStmt.tryBlock = (Block)this.astStack[this.astPtr--];
        if (hasResources) {
            length = this.astLengthStack[this.astLengthPtr--];
            Statement[] stmts = new Statement[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, stmts, 0, length);
            tryStmt.resources = stmts;
            if (this.options.sourceLevel < 0x330000L) {
                this.problemReporter().autoManagedResourcesNotBelow17(stmts);
            }
            if (this.options.sourceLevel < 0x350000L) {
                int i = 0;
                int l = stmts.length;
                while (i < l) {
                    Statement stmt = stmts[i];
                    if (stmt instanceof FieldReference || stmt instanceof NameReference) {
                        this.problemReporter().autoManagedVariableResourcesNotBelow9((Expression)stmt);
                    }
                    ++i;
                }
            }
        }
        tryStmt.sourceEnd = this.endStatementPosition;
        tryStmt.sourceStart = this.intStack[this.intPtr--];
        this.pushOnAstStack(tryStmt);
    }

    protected void consumeStatementWhile() {
        --this.expressionLengthPtr;
        Statement statement = (Statement)this.astStack[this.astPtr];
        this.astStack[this.astPtr] = new WhileStatement(this.expressionStack[this.expressionPtr--], statement, this.intStack[this.intPtr--], this.endStatementPosition);
    }

    protected void consumeStaticImportOnDemandDeclarationName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ImportReference impt = new ImportReference(tokens, positions, true, 8);
        this.pushOnAstStack(impt);
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            impt.modifiers = 0;
            this.problemReporter().invalidUsageOfStaticImports(impt);
        }
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeStaticInitializer() {
        Block block = (Block)this.astStack[this.astPtr];
        if (this.diet) {
            block.bits &= 0xFFFFFFF7;
        }
        Initializer initializer = new Initializer(block, 8);
        this.astStack[this.astPtr] = initializer;
        initializer.sourceEnd = this.endStatementPosition;
        initializer.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] - 1;
        initializer.declarationSourceStart = this.intStack[this.intPtr--];
        initializer.bodyStart = this.intStack[this.intPtr--];
        initializer.bodyEnd = this.endPosition;
        initializer.javadoc = this.javadoc;
        this.javadoc = null;
        if (this.currentElement != null) {
            this.lastCheckPoint = initializer.declarationSourceEnd;
            this.currentElement = this.currentElement.add(initializer, 0);
            this.lastIgnoredToken = -1;
        }
    }

    protected void consumeStaticOnly() {
        int savedModifiersSourceStart = this.modifiersSourceStart;
        this.checkComment();
        if (this.modifiersSourceStart >= savedModifiersSourceStart) {
            this.modifiersSourceStart = savedModifiersSourceStart;
        }
        this.pushOnIntStack(this.scanner.currentPosition);
        this.pushOnIntStack(this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
        this.jumpOverMethodBody();
        int n = this.nestedType;
        this.nestedMethod[n] = this.nestedMethod[n] + 1;
        this.resetModifiers();
        --this.expressionLengthPtr;
        if (this.currentElement != null) {
            this.recoveredStaticInitializerStart = this.intStack[this.intPtr];
        }
    }

    protected void consumeTextBlock() {
        this.problemReporter().validateJavaFeatureSupport(JavaFeature.TEXT_BLOCKS, this.scanner.startPosition, this.scanner.currentPosition - 1);
        char[] textBlock2 = this.scanner.getCurrentTextBlock();
        TextBlock textBlock = this.recordStringLiterals && !this.reparsingLambdaExpression && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated ? new TextBlock(textBlock2, this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr)) : new TextBlock(textBlock2, this.scanner.startPosition, this.scanner.currentPosition - 1, 0);
        this.pushOnExpressionStack(textBlock);
    }

    protected void consumeSwitchBlock() {
        this.concatNodeLists();
    }

    protected void consumeSwitchBlockStatement() {
        this.concatNodeLists();
    }

    protected void consumeSwitchBlockStatements() {
        this.concatNodeLists();
    }

    protected void consumeSwitchLabels() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeSwitchLabelCaseLhs() {
        if (this.scanner.lookBack[1] == 65) {
            this.scanner.yieldColons = 1;
        }
    }

    protected void consumeCaseLabelExpr() {
        this.consumeCaseLabel();
        CaseStatement caseStatement = (CaseStatement)this.astStack[this.astPtr];
        if (!this.parsingJava14Plus) {
            this.problemReporter().arrowInCaseStatementsNotSupported(caseStatement);
        }
        caseStatement.isExpr = true;
    }

    protected void consumeDefaultLabelExpr() {
        this.consumeDefaultLabel();
        CaseStatement defaultStatement = (CaseStatement)this.astStack[this.astPtr];
        if (!this.parsingJava14Plus) {
            this.problemReporter().arrowInCaseStatementsNotSupported(defaultStatement);
        }
        defaultStatement.isExpr = true;
    }

    void collectResultExpressionsYield(SwitchExpression s) {
        if (s.resultExpressions != null) {
            return;
        }
        s.resultExpressions = new ArrayList<Expression>(0);
        int l = s.statements == null ? 0 : s.statements.length;
        int i = 0;
        while (i < l) {
            block6: {
                Statement stmt;
                block5: {
                    block7: {
                        stmt = s.statements[i];
                        if (!(stmt instanceof CaseStatement)) break block5;
                        CaseStatement caseStatement = (CaseStatement)stmt;
                        if (!caseStatement.isExpr) break block6;
                        if (!((stmt = s.statements[++i]) instanceof Expression) || !((Expression)stmt).isTrulyExpression()) break block7;
                        s.resultExpressions.add((Expression)stmt);
                        break block6;
                    }
                    if (stmt instanceof ThrowStatement) break block6;
                }
                class ResultExpressionsCollector
                extends ASTVisitor {
                    Stack<SwitchExpression> targetSwitchExpressions;
                    Stack<TryStatement> tryStatements;

                    public ResultExpressionsCollector(SwitchExpression se) {
                        if (this.targetSwitchExpressions == null) {
                            this.targetSwitchExpressions = new Stack();
                        }
                        this.targetSwitchExpressions.push(se);
                    }

                    @Override
                    public boolean visit(SwitchExpression switchExpression, BlockScope blockScope) {
                        if (switchExpression.resultExpressions == null) {
                            switchExpression.resultExpressions = new ArrayList<Expression>(0);
                        }
                        this.targetSwitchExpressions.push(switchExpression);
                        return false;
                    }

                    @Override
                    public void endVisit(SwitchExpression switchExpression, BlockScope blockScope) {
                        this.targetSwitchExpressions.pop();
                    }

                    @Override
                    public boolean visit(YieldStatement yieldStatement, BlockScope blockScope) {
                        SwitchExpression targetSwitchExpression = this.targetSwitchExpressions.peek();
                        if (yieldStatement.expression != null) {
                            targetSwitchExpression.resultExpressions.add(yieldStatement.expression);
                            yieldStatement.switchExpression = this.targetSwitchExpressions.peek();
                        } else {
                            yieldStatement.switchExpression = targetSwitchExpression;
                        }
                        if (this.tryStatements != null && !this.tryStatements.empty()) {
                            yieldStatement.tryStatement = this.tryStatements.peek();
                        }
                        return true;
                    }

                    @Override
                    public boolean visit(SwitchStatement stmt, BlockScope blockScope) {
                        return true;
                    }

                    @Override
                    public boolean visit(TypeDeclaration stmt, BlockScope blockScope) {
                        return false;
                    }

                    @Override
                    public boolean visit(LambdaExpression stmt, BlockScope blockScope) {
                        return false;
                    }

                    @Override
                    public boolean visit(TryStatement stmt, BlockScope blockScope) {
                        if (this.tryStatements == null) {
                            this.tryStatements = new Stack();
                        }
                        this.tryStatements.push(stmt);
                        SwitchExpression targetSwitchExpression = this.targetSwitchExpressions.peek();
                        targetSwitchExpression.containsTry = true;
                        stmt.enclosingSwitchExpression = targetSwitchExpression;
                        return true;
                    }

                    @Override
                    public void endVisit(TryStatement stmt, BlockScope blockScope) {
                        this.tryStatements.pop();
                    }
                }
                ResultExpressionsCollector reCollector = new ResultExpressionsCollector(s);
                stmt.traverse(reCollector, null);
            }
            ++i;
        }
    }

    protected void consumeSwitchExpression() {
        this.createSwitchStatementOrExpression(false);
        if (this.astLengthStack[this.astLengthPtr--] != 0) {
            SwitchExpression s = (SwitchExpression)this.astStack[this.astPtr--];
            if (!this.parsingJava14Plus) {
                this.problemReporter().switchExpressionsNotSupported(s);
            }
            this.collectResultExpressionsYield(s);
            this.switchWithTry |= s.containsTry;
            this.pushOnExpressionStack(s);
        }
    }

    protected void consumeSwitchExprThrowDefaultArm() {
        this.consumeStatementThrow();
    }

    protected void consumeConstantExpression() {
    }

    protected void consumeConstantExpressions() {
        this.concatExpressionLists();
    }

    protected void consumeSwitchLabeledRules() {
        this.concatNodeLists();
    }

    protected void consumeSwitchLabeledRule() {
    }

    protected void consumeSwitchLabeledRuleToBlockStatement() {
    }

    protected void consumeSwitchLabeledExpression() {
        this.consumeExpressionStatement();
        Expression expr = (Expression)this.astStack[this.astPtr];
        expr.bits &= 0xFFEFFFFF;
        YieldStatement yieldStatement = new YieldStatement(expr, expr.sourceStart, this.endStatementPosition);
        yieldStatement.isImplicit = true;
        this.astStack[this.astPtr] = yieldStatement;
        this.concatNodeLists();
    }

    protected void consumeSwitchLabeledBlock() {
        this.concatNodeLists();
    }

    protected void consumeSwitchLabeledThrowStatement() {
        this.consumeStatementThrow();
        this.concatNodeLists();
    }

    protected void consumeThrowExpression() {
    }

    protected void consumeToken(int type) {
        switch (type) {
            case 104: {
                if (!this.caseFlagSet && this.scanner.lookBack[0] != 76) {
                    this.consumeLambdaHeader();
                }
                this.caseFlagSet = false;
                break;
            }
            case 7: {
                this.colonColonStart = this.scanner.currentPosition - 2;
                break;
            }
            case 72: {
                this.caseFlagSet = true;
                break;
            }
            case 62: {
                this.flushCommentsDefinedPriorTo(this.scanner.currentPosition);
                break;
            }
            case 22: {
                long positions;
                this.pushIdentifier();
                if (this.scanner.useAssertAsAnIndentifier && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                    positions = this.identifierPositionStack[this.identifierPtr];
                    if (!this.statementRecoveryActivated) {
                        this.problemReporter().useAssertAsAnIdentifier((int)(positions >>> 32), (int)positions);
                    }
                }
                if (!this.scanner.useEnumAsAnIndentifier || this.lastErrorEndPositionBeforeRecovery >= this.scanner.currentPosition) break;
                positions = this.identifierPositionStack[this.identifierPtr];
                if (this.statementRecoveryActivated) break;
                this.problemReporter().useEnumAsAnIdentifier((int)(positions >>> 32), (int)positions);
                break;
            }
            case 73: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 42: {
                this.checkAndSetModifiers(1024);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 49: {
                this.checkAndSetModifiers(2048);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 43: {
                this.checkAndSetModifiers(16);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 44: {
                this.checkAndSetModifiers(256);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 45: {
                this.checkAndSetModifiers(0x4000000);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 121: {
                this.checkAndSetModifiers(32);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 46: {
                this.checkAndSetModifiers(2);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 47: {
                this.checkAndSetModifiers(4);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 48: {
                this.checkAndSetModifiers(1);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 41: {
                this.checkAndSetModifiers(0x10000000);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 50: {
                this.checkAndSetModifiers(128);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 128: {
                this.checkAndSetModifiers(32);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 51: {
                this.checkAndSetModifiers(64);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 39: {
                if (this.isParsingModuleDeclaration()) {
                    this.checkAndSetModifiers(64);
                } else {
                    this.checkAndSetModifiers(8);
                }
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 40: {
                this.synchronizedBlockSourceStart = this.scanner.startPosition;
                this.checkAndSetModifiers(32);
                this.pushOnExpressionStackLengthStack(0);
                break;
            }
            case 115: {
                this.pushIdentifier(-6);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 105: {
                this.pushIdentifier(-5);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 106: {
                this.pushIdentifier(-3);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 108: {
                this.pushIdentifier(-2);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 109: {
                this.pushIdentifier(-8);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 110: {
                this.pushIdentifier(-9);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 112: {
                this.pushIdentifier(-10);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 113: {
                this.pushIdentifier(-7);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 114: {
                this.pushIdentifier(-4);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 55: {
                this.pushOnExpressionStack(IntLiteral.buildIntLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 56: {
                this.pushOnExpressionStack(LongLiteral.buildLongLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 57: {
                this.pushOnExpressionStack(new FloatLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 58: {
                this.pushOnExpressionStack(new DoubleLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 59: {
                this.pushOnExpressionStack(new CharLiteral(this.scanner.getCurrentTokenSource(), this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 60: {
                StringLiteral stringLiteral;
                if (this.recordStringLiterals && !this.reparsingLambdaExpression && this.checkExternalizeStrings && this.lastPosistion < this.scanner.currentPosition && !this.statementRecoveryActivated) {
                    stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                    this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                } else {
                    stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, 0);
                }
                this.pushOnExpressionStack(stringLiteral);
                break;
            }
            case 61: {
                this.consumeTextBlock();
                break;
            }
            case 52: {
                this.pushOnExpressionStack(new FalseLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 54: {
                this.pushOnExpressionStack(new TrueLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 53: {
                this.pushOnExpressionStack(new NullLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
                break;
            }
            case 34: 
            case 35: {
                this.endPosition = this.scanner.currentPosition - 1;
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 85: {
                this.forStartPosition = this.scanner.startPosition;
            }
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 86: 
            case 87: 
            case 88: 
            case 90: 
            case 111: 
            case 120: 
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 127: {
                this.problemReporter().validateJavaFeatureSupport(JavaFeature.SEALED_CLASSES, this.scanner.startPosition, this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 91: {
                this.caseLevel = this.switchNestingLevel;
                this.pushOnIntStack(this.scanner.startPosition);
                this.pushOnCaseStack(this.scanner.startPosition);
                break;
            }
            case 63: {
                this.consumeNestedType();
                ++this.switchNestingLevel;
                int n = this.nestedType;
                this.nestedMethod[n] = this.nestedMethod[n] + 1;
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 37: {
                this.resetModifiers();
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 70: 
            case 75: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 74: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 76: {
                this.pushOnIntStack(this.scanner.startPosition);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 69: {
                this.rBracketPosition = this.scanner.startPosition;
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            }
            case 38: {
                this.endStatementPosition = this.scanner.currentPosition - 1;
            }
            case 4: 
            case 5: 
            case 66: 
            case 67: {
                this.endPosition = this.scanner.startPosition;
                break;
            }
            case 2: 
            case 3: {
                this.endPosition = this.scanner.startPosition;
                this.endStatementPosition = this.scanner.currentPosition - 1;
                break;
            }
            case 25: 
            case 33: {
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
                break;
            }
            case 26: {
                this.rParenPos = this.scanner.currentPosition - 1;
                break;
            }
            case 23: {
                this.lParenPos = this.scanner.startPosition;
                break;
            }
            case 27: {
                this.expectTypeAnnotation = true;
                this.pushOnIntStack(this.dimensions);
                this.dimensions = 0;
            }
            case 36: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 29: {
                this.pushOnIntStack(this.scanner.startPosition);
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 11: {
                this.pushOnIntStack(this.scanner.startPosition);
                break;
            }
            case 118: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
                break;
            }
            case 77: {
                if (this.currentElement == null || !(this.currentElement instanceof RecoveredAnnotation)) break;
                RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
                if (recoveredAnnotation.memberValuPairEqualEnd != -1) break;
                recoveredAnnotation.memberValuPairEqualEnd = this.scanner.currentPosition - 1;
                break;
            }
            case 8: {
                this.pushOnIntStack(this.scanner.currentPosition - 1);
            }
        }
    }

    protected void consumeTypeArgument() {
        this.pushOnGenericsStack(this.getTypeReference(this.intStack[this.intPtr--]));
    }

    protected void consumeTypeArgumentList() {
        this.concatGenericsLists();
    }

    protected void consumeTypeArgumentList1() {
        this.concatGenericsLists();
    }

    protected void consumeTypeArgumentList2() {
        this.concatGenericsLists();
    }

    protected void consumeTypeArgumentList3() {
        this.concatGenericsLists();
    }

    protected void consumeTypeArgumentReferenceType1() {
        this.concatGenericsLists();
        this.pushOnGenericsStack(this.getTypeReference(0));
        --this.intPtr;
    }

    protected void consumeTypeArgumentReferenceType2() {
        this.concatGenericsLists();
        this.pushOnGenericsStack(this.getTypeReference(0));
        --this.intPtr;
    }

    protected void consumeTypeArguments() {
        this.concatGenericsLists();
        --this.intPtr;
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeArguments((TypeReference)this.genericsStack[this.genericsPtr - length + 1], (TypeReference)this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeTypeDeclarations() {
        this.concatNodeLists();
    }

    protected void consumeTypeHeaderNameWithTypeParameters() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        int length = this.genericsLengthStack[this.genericsLengthPtr--];
        this.genericsPtr -= length;
        typeDecl.typeParameters = new TypeParameter[length];
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeDecl.typeParameters, 0, length);
        typeDecl.bodyStart = typeDecl.typeParameters[length - 1].declarationSourceEnd + 1;
        this.listTypeParameterLength = 0;
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType) {
                RecoveredType recoveredType = (RecoveredType)this.currentElement;
                recoveredType.pendingTypeParameters = null;
                this.lastCheckPoint = typeDecl.bodyStart;
            } else {
                this.lastCheckPoint = typeDecl.bodyStart;
                this.currentElement = this.currentElement.add(typeDecl, 0);
                this.lastIgnoredToken = -1;
            }
        }
    }

    protected void consumeTypeImportOnDemandDeclarationName() {
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        ImportReference impt = new ImportReference(tokens, positions, true, 0);
        this.pushOnAstStack(impt);
        impt.trailingStarPosition = this.intStack[this.intPtr--];
        impt.declarationSourceEnd = this.currentToken == 25 ? this.scanner.currentPosition - 1 : impt.sourceEnd;
        impt.declarationEnd = impt.declarationSourceEnd;
        impt.declarationSourceStart = this.intStack[this.intPtr--];
        if (this.currentElement != null) {
            this.lastCheckPoint = impt.declarationSourceEnd + 1;
            this.currentElement = this.currentElement.add(impt, 0);
            this.lastIgnoredToken = -1;
            this.restartRecovery = true;
        }
    }

    protected void consumeTypeParameter1() {
    }

    protected void consumeTypeParameter1WithExtends() {
        TypeReference superType = (TypeReference)this.genericsStack[this.genericsPtr--];
        --this.genericsLengthPtr;
        TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        superType.bits |= 0x10;
        typeParameter.bits |= superType.bits & 0x100000;
        this.genericsStack[this.genericsPtr] = typeParameter;
    }

    protected void consumeTypeParameter1WithExtendsAndBounds() {
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        typeParameter.type = superType;
        typeParameter.bits |= superType.bits & 0x100000;
        superType.bits |= 0x10;
        typeParameter.bounds = bounds;
        int i = 0;
        int max = bounds.length;
        while (i < max) {
            TypeReference bound = bounds[i];
            bound.bits |= 0x10;
            typeParameter.bits |= bound.bits & 0x100000;
            ++i;
        }
    }

    protected void consumeTypeParameterHeader() {
        int start;
        int end;
        int length;
        TypeParameter typeParameter = new TypeParameter();
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            typeParameter.annotations = new Annotation[length];
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, typeParameter.annotations, 0, length);
            typeParameter.bits |= 0x100000;
        }
        long pos = this.identifierPositionStack[this.identifierPtr];
        typeParameter.declarationSourceEnd = end = (int)pos;
        typeParameter.sourceEnd = end;
        typeParameter.declarationSourceStart = start = (int)(pos >>> 32);
        typeParameter.sourceStart = start;
        typeParameter.name = this.identifierStack[this.identifierPtr--];
        --this.identifierLengthPtr;
        this.pushOnGenericsStack(typeParameter);
        ++this.listTypeParameterLength;
    }

    protected void consumeTypeParameterList() {
        this.concatGenericsLists();
    }

    protected void consumeTypeParameterList1() {
        this.concatGenericsLists();
    }

    protected void consumeTypeParameters() {
        int startPos = this.intStack[this.intPtr--];
        if (this.currentElement != null && this.currentElement instanceof RecoveredType) {
            RecoveredType recoveredType = (RecoveredType)this.currentElement;
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            TypeParameter[] typeParameters = new TypeParameter[length];
            System.arraycopy(this.genericsStack, this.genericsPtr - length + 1, typeParameters, 0, length);
            recoveredType.add(typeParameters, startPos);
        }
        if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
            int length = this.genericsLengthStack[this.genericsLengthPtr];
            this.problemReporter().invalidUsageOfTypeParameters((TypeParameter)this.genericsStack[this.genericsPtr - length + 1], (TypeParameter)this.genericsStack[this.genericsPtr]);
        }
    }

    protected void consumeTypeParameterWithExtends() {
        TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.declarationSourceEnd = superType.sourceEnd;
        typeParameter.type = superType;
        typeParameter.bits |= superType.bits & 0x100000;
        superType.bits |= 0x10;
    }

    protected void consumeTypeParameterWithExtendsAndBounds() {
        int additionalBoundsLength = this.genericsLengthStack[this.genericsLengthPtr--];
        TypeReference[] bounds = new TypeReference[additionalBoundsLength];
        this.genericsPtr -= additionalBoundsLength;
        System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
        TypeReference superType = this.getTypeReference(this.intStack[this.intPtr--]);
        TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
        typeParameter.type = superType;
        typeParameter.bits |= superType.bits & 0x100000;
        superType.bits |= 0x10;
        typeParameter.bounds = bounds;
        typeParameter.declarationSourceEnd = bounds[additionalBoundsLength - 1].sourceEnd;
        int i = 0;
        int max = bounds.length;
        while (i < max) {
            TypeReference bound = bounds[i];
            bound.bits |= 0x10;
            typeParameter.bits |= bound.bits & 0x100000;
            ++i;
        }
    }

    protected void consumeZeroAdditionalBounds() {
        if (this.currentToken == 26) {
            this.pushOnGenericsLengthStack(0);
        }
    }

    protected void consumeUnaryExpression(int op) {
        LongLiteral longLiteral;
        IntLiteral intLiteral;
        IntLiteral convertToMinValue;
        Expression exp = this.expressionStack[this.expressionPtr];
        Expression r = op == 13 ? (exp instanceof IntLiteral ? ((convertToMinValue = (intLiteral = (IntLiteral)exp).convertToMinValue()) == intLiteral ? new UnaryExpression(exp, op) : convertToMinValue) : (exp instanceof LongLiteral ? ((convertToMinValue = (longLiteral = (LongLiteral)exp).convertToMinValue()) == longLiteral ? new UnaryExpression(exp, op) : convertToMinValue) : new UnaryExpression(exp, op))) : new UnaryExpression(exp, op);
        r.sourceStart = this.intStack[this.intPtr--];
        r.sourceEnd = exp.sourceEnd;
        this.expressionStack[this.expressionPtr] = r;
    }

    protected void consumeUnaryExpression(int op, boolean post) {
        Expression leftHandSide = this.expressionStack[this.expressionPtr];
        if (leftHandSide instanceof Reference) {
            this.expressionStack[this.expressionPtr] = post ? new PostfixExpression(leftHandSide, IntLiteral.One, op, this.endStatementPosition) : new PrefixExpression(leftHandSide, IntLiteral.One, op, this.intStack[this.intPtr--]);
        } else {
            if (!post) {
                --this.intPtr;
            }
            if (!this.statementRecoveryActivated) {
                this.problemReporter().invalidUnaryExpression(leftHandSide);
            }
        }
    }

    protected void consumeVariableDeclarators() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeVariableInitializers() {
        this.concatExpressionLists();
    }

    protected void consumeWildcard() {
        Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard1() {
        Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard1WithBounds() {
    }

    protected void consumeWildcard2() {
        Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard2WithBounds() {
    }

    protected void consumeWildcard3() {
        Wildcard wildcard = new Wildcard(0);
        wildcard.sourceEnd = this.intStack[this.intPtr--];
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcard3WithBounds() {
    }

    protected void consumeWildcardBounds1Extends() {
        Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds1Super() {
        Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds2Extends() {
        Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds2Super() {
        Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds3Extends() {
        Wildcard wildcard = new Wildcard(1);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBounds3Super() {
        Wildcard wildcard = new Wildcard(2);
        wildcard.bound = (TypeReference)this.genericsStack[this.genericsPtr];
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.genericsStack[this.genericsPtr] = wildcard;
    }

    protected void consumeWildcardBoundsExtends() {
        Wildcard wildcard = new Wildcard(1);
        wildcard.bound = this.getTypeReference(this.intStack[this.intPtr--]);
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcardBoundsSuper() {
        Wildcard wildcard = new Wildcard(2);
        wildcard.bound = this.getTypeReference(this.intStack[this.intPtr--]);
        --this.intPtr;
        wildcard.sourceEnd = wildcard.bound.sourceEnd;
        --this.intPtr;
        wildcard.sourceStart = this.intStack[this.intPtr--];
        this.annotateTypeReference(wildcard);
        this.pushOnGenericsStack(wildcard);
    }

    protected void consumeWildcardWithBounds() {
    }

    protected void consumeRecordDeclaration() {
        int length;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.dispatchDeclarationIntoRecordDeclaration(length);
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        this.recordNestedMethodLevels.remove(typeDecl);
        this.problemReporter().validateJavaFeatureSupport(JavaFeature.RECORDS, typeDecl.sourceStart, typeDecl.sourceEnd);
        ConstructorDeclaration cd = typeDecl.getConstructor(this);
        if (cd == null) {
            cd = typeDecl.createDefaultConstructor(!this.diet || this.dietInt != 0, true);
        } else if (cd instanceof CompactConstructorDeclaration || (typeDecl.recordComponents == null || typeDecl.recordComponents.length == 0) && (cd.arguments == null || cd.arguments.length == 0)) {
            cd.bits |= 0x200;
        }
        if (this.scanner.containsAssertKeyword) {
            typeDecl.bits |= 1;
        }
        typeDecl.addClinit();
        typeDecl.bodyEnd = this.endStatementPosition;
        if (length == 0 && !this.containsComment(typeDecl.bodyStart, typeDecl.bodyEnd)) {
            typeDecl.bits |= 8;
        }
        QualifiedTypeReference superClass = new QualifiedTypeReference(TypeConstants.JAVA_LANG_RECORD, new long[1]);
        superClass.bits |= 0x10;
        typeDecl.superclass = superClass;
        typeDecl.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeRecordHeaderPart() {
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        assert (typeDecl.isRecord());
    }

    protected void consumeRecordHeaderNameWithTypeParameters() {
        this.consumeTypeHeaderNameWithTypeParameters();
    }

    protected void consumeRecordHeaderName1() {
        this.consumeClassOrRecordHeaderName1(true);
    }

    protected void consumeRecordComponentHeaderRightParen() {
        int length = this.astLengthStack[this.astLengthPtr--];
        this.astPtr -= length;
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        int nestedMethodLevel = this.nestedMethod[this.nestedType];
        this.recordNestedMethodLevels.put(typeDecl, new Integer[]{this.nestedType, nestedMethodLevel});
        this.astStack[this.astPtr] = typeDecl;
        if (length != 0) {
            RecordComponent[] recComps = new RecordComponent[length];
            System.arraycopy(this.astStack, this.astPtr + 1, recComps, 0, length);
            typeDecl.recordComponents = recComps;
            this.convertToFields(typeDecl, recComps);
        } else {
            typeDecl.recordComponents = ASTNode.NO_RECORD_COMPONENTS;
        }
        typeDecl.bodyStart = this.rParenPos + 1;
        this.listLength = 0;
        if (this.currentElement != null) {
            this.lastCheckPoint = typeDecl.bodyStart;
            if (this.currentElement.parseTree() == typeDecl) {
                return;
            }
        }
        this.resetModifiers();
    }

    private void convertToFields(TypeDeclaration typeDecl, RecordComponent[] recComps) {
        int length = recComps.length;
        FieldDeclaration[] fields = new FieldDeclaration[length];
        int nFields = 0;
        HashSet<String> argsSet = new HashSet<String>();
        int i = 0;
        int max = recComps.length;
        while (i < max) {
            RecordComponent recComp = recComps[i];
            String argName = new String(recComp.name);
            if (TypeDeclaration.disallowedComponentNames.contains(argName)) {
                this.problemReporter().recordIllegalComponentNameInRecord(recComp, typeDecl);
            } else if (!argsSet.contains(argName)) {
                if (recComp.type.getLastToken() == TypeConstants.VOID) {
                    this.problemReporter().recordComponentCannotBeVoid(recComp);
                } else {
                    if (recComp.isVarArgs() && i < max - 1) {
                        this.problemReporter().recordIllegalVararg(recComp, typeDecl);
                    }
                    argsSet.add(argName);
                    int n = nFields++;
                    FieldDeclaration fieldDeclaration = this.createFieldDeclaration(recComp.name, recComp.sourceStart, recComp.sourceEnd);
                    fields[n] = fieldDeclaration;
                    FieldDeclaration f = fieldDeclaration;
                    f.bits = recComp.bits;
                    f.declarationSourceStart = recComp.declarationSourceStart;
                    f.declarationEnd = recComp.declarationEnd;
                    f.declarationSourceEnd = recComp.declarationSourceEnd;
                    f.endPart1Position = recComp.sourceEnd;
                    f.endPart2Position = recComp.declarationSourceEnd;
                    f.modifiers = 18;
                    f.isARecordComponent = true;
                    f.modifiers |= 0x12;
                    f.modifiers |= 0x1000000;
                    f.modifiersSourceStart = recComp.modifiersSourceStart;
                    f.sourceStart = recComp.sourceStart;
                    f.sourceEnd = recComp.sourceEnd;
                    f.type = recComp.type;
                    if ((recComp.bits & 0x100000) != 0) {
                        f.bits |= 0x100000;
                    }
                }
            }
            ++i;
        }
        if (nFields < fields.length) {
            FieldDeclaration[] tmp = new FieldDeclaration[nFields];
            System.arraycopy(fields, 0, tmp, 0, nFields);
            fields = tmp;
        }
        typeDecl.fields = fields;
        typeDecl.nRecordComponents = fields.length;
    }

    protected void consumeRecordHeader() {
    }

    protected void consumeRecordComponentsopt() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeRecordComponents() {
        this.optimizedConcatNodeLists();
    }

    protected void consumeRecordComponent(boolean isVarArgs) {
        int length;
        int extendedDimensions;
        --this.identifierLengthPtr;
        char[] identifierName = this.identifierStack[this.identifierPtr];
        long namePositions = this.identifierPositionStack[this.identifierPtr--];
        Annotation[][] annotationsOnExtendedDimensions = (extendedDimensions = this.intStack[this.intPtr--]) == 0 ? null : this.getAnnotationsOnDimensions(extendedDimensions);
        Annotation[] varArgsAnnotations = null;
        int endOfEllipsis = 0;
        int firstDimensions = 0;
        if (isVarArgs) {
            endOfEllipsis = this.intStack[this.intPtr--];
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                varArgsAnnotations = new Annotation[length];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, varArgsAnnotations, 0, length);
            }
        }
        firstDimensions = this.intStack[this.intPtr--];
        TypeReference type = this.getTypeReference(firstDimensions);
        if (isVarArgs || extendedDimensions != 0) {
            if (isVarArgs) {
                Annotation[][] annotationArray;
                if (varArgsAnnotations != null) {
                    Annotation[][] annotationArray2 = new Annotation[1][];
                    annotationArray = annotationArray2;
                    annotationArray2[0] = varArgsAnnotations;
                } else {
                    annotationArray = null;
                }
                type = this.augmentTypeWithAdditionalDimensions(type, 1, annotationArray, true);
            }
            if (extendedDimensions != 0) {
                type = this.augmentTypeWithAdditionalDimensions(type, extendedDimensions, annotationsOnExtendedDimensions, false);
            }
            int n = type.sourceEnd = type.isParameterizedTypeReference() ? this.endStatementPosition : this.endPosition;
        }
        if (isVarArgs) {
            if (extendedDimensions == 0) {
                type.sourceEnd = endOfEllipsis;
            }
            type.bits |= 0x4000;
        }
        int modifierPositions = this.intStack[this.intPtr--];
        RecordComponent recordComponent = new RecordComponent(identifierName, namePositions, type, this.intStack[this.intPtr--] & 0xFFEFFFFF);
        recordComponent.declarationSourceStart = modifierPositions;
        recordComponent.bits |= type.bits & 0x100000;
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            recordComponent.annotations = new Annotation[length];
            System.arraycopy(this.expressionStack, (this.expressionPtr -= length) + 1, recordComponent.annotations, 0, length);
            recordComponent.bits |= 0x100000;
            RecoveredType currentRecoveryType = this.currentRecoveryType();
            if (currentRecoveryType != null) {
                currentRecoveryType.annotationsConsumed(recordComponent.annotations);
            }
        }
        this.pushOnAstStack(recordComponent);
        ++this.listLength;
        if (isVarArgs) {
            if (!this.statementRecoveryActivated && this.options.sourceLevel < 0x310000L && this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition) {
                this.problemReporter().invalidUsageOfVarargs(recordComponent);
            } else if (!this.statementRecoveryActivated && extendedDimensions > 0) {
                this.problemReporter().illegalExtendedDimensions(recordComponent);
            }
        } else if (!this.statementRecoveryActivated && extendedDimensions > 0) {
            this.problemReporter().recordIllegalExtendedDimensionsForRecordComponent(recordComponent);
        }
    }

    protected void consumeRecordBody() {
    }

    protected void consumeEmptyRecordBodyDeclaration() {
        this.pushOnAstLengthStack(0);
    }

    protected void consumeRecordBodyDeclarations() {
        this.concatNodeLists();
    }

    protected void consumeRecordBodyDeclaration() {
    }

    protected void consumeCompactConstructorDeclaration() {
        int length;
        --this.intPtr;
        --this.intPtr;
        --this.realBlockPtr;
        Statement[] statements = null;
        if ((length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                statements = new Statement[length];
                System.arraycopy(this.astStack, this.astPtr + 1, statements, 0, length);
            }
        }
        CompactConstructorDeclaration ccd = (CompactConstructorDeclaration)this.astStack[this.astPtr];
        ccd.statements = statements;
        if (!(this.diet && this.dietInt == 0 || statements != null || this.containsComment(ccd.bodyStart, this.endPosition))) {
            ccd.bits |= 8;
        }
        ccd.constructorCall = SuperReference.implicitSuperConstructorCall();
        ccd.bodyEnd = this.endPosition;
        ccd.declarationSourceEnd = this.flushCommentsDefinedPriorTo(this.endStatementPosition);
    }

    protected void consumeCompactConstructorHeader() {
        AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
        if (this.currentToken == 38) {
            method.bodyStart = this.scanner.currentPosition;
        }
        if (this.currentElement != null) {
            if (this.currentToken == 25) {
                method.modifiers |= 0x1000000;
                method.declarationSourceEnd = this.scanner.currentPosition - 1;
                method.bodyEnd = this.scanner.currentPosition - 1;
                if (this.currentElement.parseTree() == method && this.currentElement.parent != null) {
                    this.currentElement = this.currentElement.parent;
                }
            }
            this.restartRecovery = true;
        }
    }

    protected void consumeCompactConstructorHeaderName() {
        if (this.currentElement != null && this.lastIgnoredToken == 37) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        CompactConstructorDeclaration ccd = new CompactConstructorDeclaration(this.compilationUnit.compilationResult);
        this.populateCompactConstructor(ccd);
    }

    protected void consumeCompactConstructorHeaderNameWithTypeParameters() {
        if (this.currentElement != null && this.lastIgnoredToken == 37) {
            this.lastCheckPoint = this.scanner.startPosition;
            this.restartRecovery = true;
            return;
        }
        CompactConstructorDeclaration ccd = new CompactConstructorDeclaration(this.compilationUnit.compilationResult);
        this.helperConstructorHeaderNameWithTypeParameters(ccd);
    }

    protected void dispatchDeclarationIntoRecordDeclaration(int length) {
        int nCreatedFields;
        if (length == 0) {
            return;
        }
        int[] flag = new int[length + 1];
        int nFields = 0;
        int size2 = 0;
        int size3 = 0;
        boolean hasAbstractMethods = false;
        int i = length - 1;
        while (i >= 0) {
            ASTNode astNode;
            if ((astNode = this.astStack[this.astPtr--]) instanceof AbstractMethodDeclaration) {
                flag[i] = 2;
                ++size2;
                if (((AbstractMethodDeclaration)astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                ++size3;
            } else {
                flag[i] = 1;
                ++nFields;
            }
            --i;
        }
        TypeDeclaration recordDecl = (TypeDeclaration)this.astStack[this.astPtr];
        int n = nCreatedFields = recordDecl.fields != null ? recordDecl.fields.length : 0;
        if (nFields != 0) {
            FieldDeclaration[] tmp = new FieldDeclaration[(recordDecl.fields != null ? recordDecl.fields.length : 0) + nFields];
            if (recordDecl.fields != null) {
                System.arraycopy(recordDecl.fields, 0, tmp, 0, recordDecl.fields.length);
            }
            recordDecl.fields = tmp;
        }
        if (size2 != 0) {
            recordDecl.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods) {
                recordDecl.bits |= 0x800;
            }
        }
        if (size3 != 0) {
            recordDecl.memberTypes = new TypeDeclaration[size3];
        }
        nFields = nCreatedFields;
        size3 = 0;
        size2 = 0;
        int flagI = flag[0];
        int start = 0;
        int end = 0;
        while (end <= length) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, recordDecl.fields, (nFields += length2) - length2, length2);
                        break;
                    }
                    case 2: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, recordDecl.methods, (size2 += length2) - length2, length2);
                        break;
                    }
                    case 3: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, recordDecl.memberTypes, (size3 += length2) - length2, length2);
                    }
                }
                start = end;
                flagI = flag[start];
            }
            ++end;
        }
        this.checkForRecordMemberErrors(recordDecl, nCreatedFields);
        if (recordDecl.memberTypes != null) {
            int i2 = recordDecl.memberTypes.length - 1;
            while (i2 >= 0) {
                recordDecl.memberTypes[i2].enclosingType = recordDecl;
                --i2;
            }
        }
    }

    private void checkForRecordMemberErrors(TypeDeclaration typeDecl, int nCreatedFields) {
        if (typeDecl.fields == null) {
            return;
        }
        int i = nCreatedFields;
        while (i < typeDecl.fields.length) {
            FieldDeclaration f = typeDecl.fields[i];
            if (f != null && !f.isStatic()) {
                if (f instanceof Initializer) {
                    this.problemReporter().recordInstanceInitializerBlockInRecord((Initializer)f);
                } else {
                    this.problemReporter().recordNonStaticFieldDeclarationInRecord(f);
                }
            }
            ++i;
        }
        if (typeDecl.methods != null) {
            i = 0;
            while (i < typeDecl.methods.length) {
                AbstractMethodDeclaration method = typeDecl.methods[i];
                if ((method.modifiers & 0x100) != 0) {
                    this.problemReporter().recordIllegalNativeModifierInRecord(method);
                }
                ++i;
            }
        }
    }

    public boolean containsComment(int sourceStart, int sourceEnd) {
        int iComment = this.scanner.commentPtr;
        while (iComment >= 0) {
            int commentStart = this.scanner.commentStarts[iComment];
            if (commentStart < 0) {
                commentStart = -commentStart;
            }
            if (commentStart >= sourceStart && commentStart <= sourceEnd) {
                return true;
            }
            --iComment;
        }
        return false;
    }

    public MethodDeclaration convertToMethodDeclaration(ConstructorDeclaration c, CompilationResult compilationResult) {
        MethodDeclaration m = new MethodDeclaration(compilationResult);
        m.typeParameters = c.typeParameters;
        m.sourceStart = c.sourceStart;
        m.sourceEnd = c.sourceEnd;
        m.bodyStart = c.bodyStart;
        m.bodyEnd = c.bodyEnd;
        m.declarationSourceEnd = c.declarationSourceEnd;
        m.declarationSourceStart = c.declarationSourceStart;
        m.selector = c.selector;
        m.statements = c.statements;
        m.modifiers = c.modifiers;
        m.annotations = c.annotations;
        m.arguments = c.arguments;
        m.thrownExceptions = c.thrownExceptions;
        m.explicitDeclarations = c.explicitDeclarations;
        m.returnType = null;
        m.javadoc = c.javadoc;
        m.bits = c.bits;
        return m;
    }

    protected TypeReference augmentTypeWithAdditionalDimensions(TypeReference typeReference, int additionalDimensions, Annotation[][] additionalAnnotations, boolean isVarargs) {
        return typeReference.augmentTypeWithAdditionalDimensions(additionalDimensions, additionalAnnotations, isVarargs);
    }

    protected FieldDeclaration createFieldDeclaration(char[] fieldDeclarationName, int sourceStart, int sourceEnd) {
        return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
    }

    protected JavadocParser createJavadocParser() {
        return new JavadocParser(this);
    }

    protected LocalDeclaration createLocalDeclaration(char[] localDeclarationName, int sourceStart, int sourceEnd) {
        return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
    }

    protected StringLiteral createStringLiteral(char[] token, int start, int end, int lineNumber) {
        return new StringLiteral(token, start, end, lineNumber);
    }

    protected RecoveredType currentRecoveryType() {
        if (this.currentElement != null) {
            if (this.currentElement instanceof RecoveredType) {
                return (RecoveredType)this.currentElement;
            }
            return this.currentElement.enclosingType();
        }
        return null;
    }

    public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        CompilationUnitDeclaration parsedUnit;
        boolean old = this.diet;
        int oldInt = this.dietInt;
        try {
            this.dietInt = 0;
            this.diet = true;
            parsedUnit = this.parse(sourceUnit, compilationResult);
        }
        finally {
            this.diet = old;
            this.dietInt = oldInt;
        }
        return parsedUnit;
    }

    protected void dispatchDeclarationInto(int length) {
        if (length == 0) {
            return;
        }
        int[] flag = new int[length + 1];
        int size1 = 0;
        int size2 = 0;
        int size3 = 0;
        boolean hasAbstractMethods = false;
        int i = length - 1;
        while (i >= 0) {
            ASTNode astNode;
            if ((astNode = this.astStack[this.astPtr--]) instanceof AbstractMethodDeclaration) {
                flag[i] = 2;
                ++size2;
                if (((AbstractMethodDeclaration)astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                ++size3;
            } else {
                flag[i] = 1;
                ++size1;
            }
            --i;
        }
        TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
        if (size1 != 0) {
            typeDecl.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            typeDecl.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods) {
                typeDecl.bits |= 0x800;
            }
        }
        if (size3 != 0) {
            typeDecl.memberTypes = new TypeDeclaration[size3];
        }
        size3 = 0;
        size2 = 0;
        size1 = 0;
        int flagI = flag[0];
        int start = 0;
        int end = 0;
        while (end <= length) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.fields, (size1 += length2) - length2, length2);
                        break;
                    }
                    case 2: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.methods, (size2 += length2) - length2, length2);
                        break;
                    }
                    case 3: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, typeDecl.memberTypes, (size3 += length2) - length2, length2);
                    }
                }
                start = end;
                flagI = flag[start];
            }
            ++end;
        }
        if (typeDecl.memberTypes != null) {
            int i2 = typeDecl.memberTypes.length - 1;
            while (i2 >= 0) {
                TypeDeclaration memberType = typeDecl.memberTypes[i2];
                memberType.enclosingType = typeDecl;
                --i2;
            }
        }
    }

    protected void dispatchDeclarationIntoEnumDeclaration(int length) {
        if (length == 0) {
            return;
        }
        int[] flag = new int[length + 1];
        int size1 = 0;
        int size2 = 0;
        int size3 = 0;
        TypeDeclaration enumDeclaration = (TypeDeclaration)this.astStack[this.astPtr - length];
        boolean hasAbstractMethods = false;
        int enumConstantsCounter = 0;
        int i = length - 1;
        while (i >= 0) {
            ASTNode astNode;
            if ((astNode = this.astStack[this.astPtr--]) instanceof AbstractMethodDeclaration) {
                flag[i] = 2;
                ++size2;
                if (((AbstractMethodDeclaration)astNode).isAbstract()) {
                    hasAbstractMethods = true;
                }
            } else if (astNode instanceof TypeDeclaration) {
                flag[i] = 3;
                ++size3;
            } else if (astNode instanceof FieldDeclaration) {
                flag[i] = 1;
                ++size1;
                if (((FieldDeclaration)astNode).getKind() == 3) {
                    ++enumConstantsCounter;
                }
            }
            --i;
        }
        if (size1 != 0) {
            enumDeclaration.fields = new FieldDeclaration[size1];
        }
        if (size2 != 0) {
            enumDeclaration.methods = new AbstractMethodDeclaration[size2];
            if (hasAbstractMethods) {
                enumDeclaration.bits |= 0x800;
            }
        }
        if (size3 != 0) {
            enumDeclaration.memberTypes = new TypeDeclaration[size3];
        }
        size3 = 0;
        size2 = 0;
        size1 = 0;
        int flagI = flag[0];
        int start = 0;
        int end = 0;
        while (end <= length) {
            if (flagI != flag[end]) {
                switch (flagI) {
                    case 1: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.fields, (size1 += length2) - length2, length2);
                        break;
                    }
                    case 2: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.methods, (size2 += length2) - length2, length2);
                        break;
                    }
                    case 3: {
                        int length2 = end - start;
                        System.arraycopy(this.astStack, this.astPtr + start + 1, enumDeclaration.memberTypes, (size3 += length2) - length2, length2);
                    }
                }
                start = end;
                flagI = flag[start];
            }
            ++end;
        }
        if (enumDeclaration.memberTypes != null) {
            int i2 = enumDeclaration.memberTypes.length - 1;
            while (i2 >= 0) {
                enumDeclaration.memberTypes[i2].enclosingType = enumDeclaration;
                --i2;
            }
        }
        enumDeclaration.enumConstantsCounter = enumConstantsCounter;
    }

    protected CompilationUnitDeclaration endParse(int act) {
        this.lastAct = act;
        if (this.statementRecoveryActivated) {
            RecoveredElement recoveredElement = this.buildInitialRecoveryState();
            if (recoveredElement != null) {
                recoveredElement.topElement().updateParseTree();
            }
            if (this.hasError) {
                this.resetStacks();
            }
        } else if (this.currentElement != null) {
            if (VERBOSE_RECOVERY) {
                System.out.print(Messages.parser_syntaxRecovery);
                System.out.println("--------------------------");
                System.out.println(this.compilationUnit);
                System.out.println("----------------------------------");
            }
            this.currentElement.topElement().updateParseTree();
        } else if (this.diet & VERBOSE_RECOVERY) {
            System.out.print(Messages.parser_regularParse);
            System.out.println("--------------------------");
            System.out.println(this.compilationUnit);
            System.out.println("----------------------------------");
        }
        this.persistLineSeparatorPositions();
        int i = 0;
        while (i < this.scanner.foundTaskCount) {
            if (!this.statementRecoveryActivated) {
                this.problemReporter().task(new String(this.scanner.foundTaskTags[i]), new String(this.scanner.foundTaskMessages[i]), this.scanner.foundTaskPriorities[i] == null ? null : new String(this.scanner.foundTaskPriorities[i]), this.scanner.foundTaskPositions[i][0], this.scanner.foundTaskPositions[i][1]);
            }
            ++i;
        }
        this.javadoc = null;
        return this.compilationUnit;
    }

    public int flushCommentsDefinedPriorTo(int position) {
        int immediateCommentEnd;
        int lastCommentIndex = this.scanner.commentPtr;
        if (lastCommentIndex < 0) {
            return position;
        }
        int index = lastCommentIndex;
        int validCount = 0;
        while (index >= 0) {
            int commentEnd = this.scanner.commentStops[index];
            if (commentEnd < 0) {
                commentEnd = -commentEnd;
            }
            if (commentEnd <= position) break;
            --index;
            ++validCount;
        }
        if (validCount > 0 && (immediateCommentEnd = -this.scanner.commentStops[index + 1]) > 0 && Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr) == Util.getLineNumber(--immediateCommentEnd, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
            position = immediateCommentEnd;
            --validCount;
            ++index;
        }
        if (index < 0) {
            return position;
        }
        switch (validCount) {
            case 0: {
                break;
            }
            case 2: {
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                this.scanner.commentStarts[1] = this.scanner.commentStarts[index + 2];
                this.scanner.commentStops[1] = this.scanner.commentStops[index + 2];
                this.scanner.commentTagStarts[1] = this.scanner.commentTagStarts[index + 2];
                break;
            }
            case 1: {
                this.scanner.commentStarts[0] = this.scanner.commentStarts[index + 1];
                this.scanner.commentStops[0] = this.scanner.commentStops[index + 1];
                this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[index + 1];
                break;
            }
            default: {
                System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
                System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
                System.arraycopy(this.scanner.commentTagStarts, index + 1, this.scanner.commentTagStarts, 0, validCount);
            }
        }
        this.scanner.commentPtr = validCount - 1;
        return position;
    }

    protected TypeReference getAnnotationType() {
        int length;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            return new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        }
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        long[] positions = new long[length];
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        return new QualifiedTypeReference(tokens, positions);
    }

    public int getFirstToken() {
        return this.firstToken;
    }

    public int[] getJavaDocPositions() {
        int javadocCount = 0;
        int max = this.scanner.commentPtr;
        int i = 0;
        while (i <= max) {
            if (this.scanner.commentStarts[i] >= 0 && this.scanner.commentStops[i] > 0) {
                ++javadocCount;
            }
            ++i;
        }
        if (javadocCount == 0) {
            return null;
        }
        int[] positions = new int[2 * javadocCount];
        int index = 0;
        int i2 = 0;
        while (i2 <= max) {
            int commentStop;
            int commentStart = this.scanner.commentStarts[i2];
            if (commentStart >= 0 && (commentStop = this.scanner.commentStops[i2]) > 0) {
                positions[index++] = commentStart;
                positions[index++] = commentStop - 1;
            }
            ++i2;
        }
        return positions;
    }

    public void getMethodBodies(CompilationUnitDeclaration unit) {
        if (unit == null) {
            return;
        }
        if (unit.ignoreMethodBodies) {
            unit.ignoreFurtherInvestigation = true;
            return;
        }
        if ((unit.bits & 0x10) != 0) {
            return;
        }
        int[] oldLineEnds = this.scanner.lineEnds;
        int oldLinePtr = this.scanner.linePtr;
        CompilationResult compilationResult = unit.compilationResult;
        char[] contents = this.readManager != null ? this.readManager.getContents(compilationResult.compilationUnit) : compilationResult.compilationUnit.getContents();
        this.scanner.setSource(contents, compilationResult);
        if (this.javadocParser != null && this.javadocParser.checkDocComment) {
            this.javadocParser.scanner.setSource(contents);
        }
        if (unit.types != null) {
            int i = 0;
            int length = unit.types.length;
            while (i < length) {
                unit.types[i].parseMethods(this, unit);
                ++i;
            }
        }
        unit.bits |= 0x10;
        this.scanner.lineEnds = oldLineEnds;
        this.scanner.linePtr = oldLinePtr;
    }

    protected char getNextCharacter(char[] comment, int[] index) {
        int n = index[0];
        index[0] = n + 1;
        char nextCharacter = comment[n];
        switch (nextCharacter) {
            case '\\': {
                index[0] = index[0] + 1;
                while (comment[index[0]] == 'u') {
                    index[0] = index[0] + 1;
                }
                int n2 = index[0];
                index[0] = n2 + 1;
                int c1 = ScannerHelper.getHexadecimalValue(comment[n2]);
                if (c1 > 15 || c1 < 0) break;
                int n3 = index[0];
                index[0] = n3 + 1;
                int c2 = ScannerHelper.getHexadecimalValue(comment[n3]);
                if (c2 > 15 || c2 < 0) break;
                int n4 = index[0];
                index[0] = n4 + 1;
                int c3 = ScannerHelper.getHexadecimalValue(comment[n4]);
                if (c3 > 15 || c3 < 0) break;
                int n5 = index[0];
                index[0] = n5 + 1;
                int c4 = ScannerHelper.getHexadecimalValue(comment[n5]);
                if (c4 > 15 || c4 < 0) break;
                nextCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
            }
        }
        return nextCharacter;
    }

    protected Expression getTypeReference(Expression exp) {
        exp.bits &= 0xFFFFFFF8;
        exp.bits |= 4;
        return exp;
    }

    protected void annotateTypeReference(Wildcard ref) {
        int length;
        if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
            if (ref.annotations == null) {
                ref.annotations = new Annotation[ref.getAnnotatableLevels()][];
            }
            ref.annotations[0] = new Annotation[length];
            System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, ref.annotations[0], 0, length);
            if (ref.sourceStart > ref.annotations[0][0].sourceStart) {
                ref.sourceStart = ref.annotations[0][0].sourceStart;
            }
            ref.bits |= 0x100000;
        }
        if (ref.bound != null) {
            ref.bits |= ref.bound.bits & 0x100000;
        }
    }

    protected TypeReference getTypeReference(int dim) {
        int numberOfIdentifiers;
        TypeReference ref;
        int length;
        Annotation[][] annotationsOnDimensions = null;
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) < 0) {
            if (dim > 0) {
                annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
            }
            ref = TypeReference.baseTypeReference(-length, dim, annotationsOnDimensions);
            ref.sourceStart = this.intStack[this.intPtr--];
            if (dim == 0) {
                ref.sourceEnd = this.intStack[this.intPtr--];
            } else {
                --this.intPtr;
                ref.sourceEnd = this.rBracketPosition;
            }
        } else if (length != (numberOfIdentifiers = this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr--]) || this.genericsLengthStack[this.genericsLengthPtr] != 0) {
            ref = this.getTypeReferenceForGenericType(dim, length, numberOfIdentifiers);
        } else if (length == 1) {
            --this.genericsLengthPtr;
            if (dim == 0) {
                ref = new SingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
            } else {
                annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
                ref = new ArrayTypeReference(this.identifierStack[this.identifierPtr], dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
                ref.sourceEnd = this.endPosition;
                if (annotationsOnDimensions != null) {
                    ref.bits |= 0x100000;
                }
            }
        } else {
            --this.genericsLengthPtr;
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            long[] positions = new long[length];
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            if (dim == 0) {
                ref = new QualifiedTypeReference(tokens, positions);
            } else {
                annotationsOnDimensions = this.getAnnotationsOnDimensions(dim);
                ref = new ArrayQualifiedTypeReference(tokens, dim, annotationsOnDimensions, positions);
                ref.sourceEnd = this.endPosition;
                if (annotationsOnDimensions != null) {
                    ref.bits |= 0x100000;
                }
            }
        }
        int levels = ref.getAnnotatableLevels();
        int i = levels - 1;
        while (i >= 0) {
            if ((length = this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr--]) != 0) {
                if (ref.annotations == null) {
                    ref.annotations = new Annotation[levels][];
                }
                ref.annotations[i] = new Annotation[length];
                System.arraycopy(this.typeAnnotationStack, (this.typeAnnotationPtr -= length) + 1, ref.annotations[i], 0, length);
                if (i == 0) {
                    ref.sourceStart = ref.annotations[0][0].sourceStart;
                }
                ref.bits |= 0x100000;
            }
            --i;
        }
        return ref;
    }

    protected TypeReference getTypeReferenceForGenericType(int dim, int identifierLength, int numberOfIdentifiers) {
        Annotation[][] annotationsOnDimensions;
        Annotation[][] annotationArray = annotationsOnDimensions = dim == 0 ? null : this.getAnnotationsOnDimensions(dim);
        if (identifierLength == 1 && numberOfIdentifiers == 1) {
            int currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--];
            TypeReference[] typeArguments = null;
            if (currentTypeArgumentsLength < 0) {
                typeArguments = TypeReference.NO_TYPE_ARGUMENTS;
            } else {
                typeArguments = new TypeReference[currentTypeArgumentsLength];
                this.genericsPtr -= currentTypeArgumentsLength;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
            }
            ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, annotationsOnDimensions, this.identifierPositionStack[this.identifierPtr--]);
            if (dim != 0) {
                parameterizedSingleTypeReference.sourceEnd = this.endStatementPosition;
            }
            return parameterizedSingleTypeReference;
        }
        TypeReference[][] typeArguments = new TypeReference[numberOfIdentifiers][];
        char[][] tokens = new char[numberOfIdentifiers][];
        long[] positions = new long[numberOfIdentifiers];
        int index = numberOfIdentifiers;
        int currentIdentifiersLength = identifierLength;
        while (index > 0) {
            int currentTypeArgumentsLength;
            if ((currentTypeArgumentsLength = this.genericsLengthStack[this.genericsLengthPtr--]) > 0) {
                this.genericsPtr -= currentTypeArgumentsLength;
                TypeReference[] typeReferenceArray = new TypeReference[currentTypeArgumentsLength];
                typeArguments[index - 1] = typeReferenceArray;
                System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeReferenceArray, 0, currentTypeArgumentsLength);
            } else if (currentTypeArgumentsLength < 0) {
                typeArguments[index - 1] = TypeReference.NO_TYPE_ARGUMENTS;
            }
            switch (currentIdentifiersLength) {
                case 1: {
                    tokens[index - 1] = this.identifierStack[this.identifierPtr];
                    positions[index - 1] = this.identifierPositionStack[this.identifierPtr--];
                    break;
                }
                default: {
                    this.identifierPtr -= currentIdentifiersLength;
                    System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, index - currentIdentifiersLength, currentIdentifiersLength);
                    System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, index - currentIdentifiersLength, currentIdentifiersLength);
                }
            }
            if ((index -= currentIdentifiersLength) <= 0) continue;
            currentIdentifiersLength = this.identifierLengthStack[this.identifierLengthPtr--];
        }
        ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, annotationsOnDimensions, positions);
        if (dim != 0) {
            parameterizedQualifiedTypeReference.sourceEnd = this.endStatementPosition;
        }
        return parameterizedQualifiedTypeReference;
    }

    protected NameReference getUnspecifiedReference() {
        return this.getUnspecifiedReference(true);
    }

    protected NameReference getUnspecifiedReference(boolean rejectTypeAnnotations) {
        NameReference ref;
        int length;
        if (rejectTypeAnnotations) {
            this.consumeNonTypeUseName();
        }
        if ((length = this.identifierLengthStack[this.identifierLengthPtr--]) == 1) {
            ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
        } else {
            char[][] tokens = new char[length][];
            this.identifierPtr -= length;
            System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
            long[] positions = new long[length];
            System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
            ref = new QualifiedNameReference(tokens, positions, (int)(this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int)this.identifierPositionStack[this.identifierPtr + length]);
        }
        return ref;
    }

    protected NameReference getUnspecifiedReferenceOptimized() {
        this.consumeNonTypeUseName();
        int length = this.identifierLengthStack[this.identifierLengthPtr--];
        if (length == 1) {
            SingleNameReference ref = new SingleNameReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr--]);
            ref.bits &= 0xFFFFFFF8;
            ref.bits |= 3;
            return ref;
        }
        char[][] tokens = new char[length][];
        this.identifierPtr -= length;
        System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
        long[] positions = new long[length];
        System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
        QualifiedNameReference ref = new QualifiedNameReference(tokens, positions, (int)(this.identifierPositionStack[this.identifierPtr + 1] >> 32), (int)this.identifierPositionStack[this.identifierPtr + length]);
        ref.bits &= 0xFFFFFFF8;
        ref.bits |= 3;
        return ref;
    }

    public void goForBlockStatementsopt() {
        this.firstToken = 67;
        this.scanner.recordLineSeparator = false;
    }

    public void goForBlockStatementsOrCatchHeader() {
        this.firstToken = 8;
        this.scanner.recordLineSeparator = false;
    }

    public void goForClassBodyDeclarations() {
        this.firstToken = 21;
        this.scanner.recordLineSeparator = true;
    }

    public void goForCompilationUnit() {
        this.firstToken = 2;
        this.scanner.foundTaskCount = 0;
        this.scanner.recordLineSeparator = true;
    }

    public void goForExpression(boolean recordLineSeparator) {
        this.firstToken = 9;
        this.scanner.recordLineSeparator = recordLineSeparator;
    }

    public void goForFieldDeclaration() {
        this.firstToken = 30;
        this.scanner.recordLineSeparator = true;
    }

    public void goForGenericMethodDeclaration() {
        this.firstToken = 10;
        this.scanner.recordLineSeparator = true;
    }

    public void goForHeaders() {
        RecoveredType currentType = this.currentRecoveryType();
        this.firstToken = currentType != null && currentType.insideEnumConstantPart ? 66 : 16;
        this.scanner.recordLineSeparator = true;
        this.scanner.scanContext = null;
    }

    public void goForImportDeclaration() {
        this.firstToken = 31;
        this.scanner.recordLineSeparator = true;
    }

    public void goForInitializer() {
        this.firstToken = 14;
        this.scanner.recordLineSeparator = false;
    }

    public void goForMemberValue() {
        this.firstToken = 31;
        this.scanner.recordLineSeparator = true;
    }

    public void goForMethodBody() {
        this.firstToken = 3;
        this.scanner.recordLineSeparator = false;
    }

    public void goForPackageDeclaration() {
        this.goForPackageDeclaration(true);
    }

    public void goForPackageDeclaration(boolean recordLineSeparators) {
        this.firstToken = 29;
        this.scanner.recordLineSeparator = recordLineSeparators;
    }

    public void goForRecordBodyDeclarations() {
        this.firstToken = 5;
        this.scanner.recordLineSeparator = true;
    }

    public void goForTypeDeclaration() {
        this.firstToken = 4;
        this.scanner.recordLineSeparator = true;
    }

    public boolean hasLeadingTagComment(char[] commentPrefixTag, int rangeEnd) {
        int iComment = this.scanner.commentPtr;
        if (iComment < 0) {
            return false;
        }
        int iStatement = this.astLengthPtr;
        if (iStatement < 0 || this.astLengthStack[iStatement] <= 1) {
            return false;
        }
        ASTNode lastNode = this.astStack[this.astPtr];
        int rangeStart = lastNode.sourceEnd;
        while (iComment >= 0) {
            block10: {
                int commentStart = this.scanner.commentStarts[iComment];
                if (commentStart < 0) {
                    commentStart = -commentStart;
                }
                if (commentStart < rangeStart) {
                    return false;
                }
                if (commentStart <= rangeEnd) {
                    char[] source = this.scanner.source;
                    int charPos = commentStart + 2;
                    while (charPos < rangeEnd) {
                        char c = source[charPos];
                        if (c >= '\u0080' || (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) == 0) break;
                        ++charPos;
                    }
                    int iTag = 0;
                    int length = commentPrefixTag.length;
                    while (iTag < length) {
                        if (charPos >= rangeEnd || source[charPos] != commentPrefixTag[iTag]) {
                            if (iTag == 0) {
                                return false;
                            }
                            break block10;
                        }
                        ++iTag;
                        ++charPos;
                    }
                    return true;
                }
            }
            --iComment;
        }
        return false;
    }

    protected void ignoreNextClosingBrace() {
        this.ignoreNextClosingBrace = true;
    }

    protected void ignoreExpressionAssignment() {
        --this.intPtr;
        ArrayInitializer arrayInitializer = (ArrayInitializer)this.expressionStack[this.expressionPtr--];
        --this.expressionLengthPtr;
        if (!this.statementRecoveryActivated) {
            this.problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd);
        }
    }

    public void initialize() {
        this.initialize(false);
    }

    public void initialize(boolean parsingCompilationUnit) {
        boolean checkNLS;
        this.javadoc = null;
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.patternPtr = -1;
        this.patternLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.casePtr = -1;
        this.nestedType = 0;
        this.nestedMethod[0] = 0;
        this.switchNestingLevel = 0;
        this.switchWithTry = false;
        this.variablesCounter[this.nestedType] = 0;
        this.dimensions = 0;
        this.realBlockPtr = -1;
        this.compilationUnit = null;
        this.referenceContext = null;
        this.endStatementPosition = 0;
        this.valueLambdaNestDepth = -1;
        int astLength = this.astStack.length;
        if (this.noAstNodes.length < astLength) {
            this.noAstNodes = new ASTNode[astLength];
        }
        System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);
        int expressionLength = this.expressionStack.length;
        if (this.noExpressions.length < expressionLength) {
            this.noExpressions = new Expression[expressionLength];
        }
        System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        this.scanner.eofPosition = Integer.MAX_VALUE;
        this.recordStringLiterals = true;
        this.checkExternalizeStrings = checkNLS = this.options.getSeverity(256) != 256;
        this.scanner.checkNonExternalizedStringLiterals = parsingCompilationUnit && checkNLS;
        this.scanner.checkUninternedIdentityComparison = parsingCompilationUnit && this.options.complainOnUninternedIdentityComparison;
        this.scanner.lastPosition = -1;
        this.scanner.caseStartPosition = -1;
        this.resetModifiers();
        this.lastCheckPoint = -1;
        this.currentElement = null;
        this.restartRecovery = false;
        this.hasReportedError = false;
        this.recoveredStaticInitializerStart = 0;
        this.lastIgnoredToken = -1;
        this.lastErrorEndPosition = -1;
        this.lastErrorEndPositionBeforeRecovery = -1;
        this.lastJavadocEnd = -1;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.lastPosistion = -1;
        this.rBraceStart = 0;
        this.rBraceEnd = 0;
        this.rBraceSuccessorStart = 0;
        this.rBracketPosition = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
    }

    public void initializeScanner() {
        this.scanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, this.options.taskTags, this.options.taskPriorities, this.options.isTaskCaseSensitive, this.options.enablePreviewFeatures);
    }

    public void jumpOverMethodBody() {
        if (this.diet && this.dietInt == 0) {
            this.scanner.diet = true;
        }
    }

    private void jumpOverType() {
        if (this.recoveredTypes != null && this.nextTypeStart > -1 && this.nextTypeStart < this.scanner.currentPosition) {
            TypeDeclaration typeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
            boolean isAnonymous = typeDeclaration.allocation != null;
            this.scanner.startPosition = typeDeclaration.declarationSourceEnd + 1;
            this.scanner.currentPosition = typeDeclaration.declarationSourceEnd + 1;
            this.scanner.diet = false;
            if (!isAnonymous) {
                ((RecoveryScanner)this.scanner).setPendingTokens(new int[]{25, 82});
            } else {
                ((RecoveryScanner)this.scanner).setPendingTokens(new int[]{22, 77, 22});
            }
            this.pendingRecoveredType = typeDeclaration;
            try {
                this.currentToken = this.scanner.getNextToken();
            }
            catch (InvalidInputException invalidInputException) {}
            if (++this.recoveredTypePtr < this.recoveredTypes.length) {
                TypeDeclaration nextTypeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
                this.nextTypeStart = nextTypeDeclaration.allocation == null ? nextTypeDeclaration.declarationSourceStart : nextTypeDeclaration.allocation.sourceStart;
            } else {
                this.nextTypeStart = Integer.MAX_VALUE;
            }
        }
    }

    protected void markEnclosingMemberWithLocalType() {
        if (this.currentElement != null) {
            return;
        }
        this.markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind.LOCAL);
    }

    protected void markEnclosingMemberWithLocalOrFunctionalType(LocalTypeKind context) {
        int i = this.astPtr;
        while (i >= 0) {
            ASTNode node = this.astStack[i];
            if (node instanceof AbstractMethodDeclaration || node instanceof FieldDeclaration || node instanceof TypeDeclaration && ((TypeDeclaration)node).declarationSourceEnd == 0) {
                switch (context) {
                    case METHOD_REFERENCE: {
                        node.bits |= 0x200000;
                        break;
                    }
                    case LAMBDA: {
                        node.bits |= 0x200000;
                    }
                    case LOCAL: {
                        node.bits |= 2;
                    }
                }
                return;
            }
            --i;
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration || this.referenceContext instanceof TypeDeclaration) {
            ASTNode node = (ASTNode)((Object)this.referenceContext);
            switch (context) {
                case METHOD_REFERENCE: {
                    node.bits |= 0x200000;
                    break;
                }
                case LAMBDA: {
                    node.bits |= 0x200000;
                }
                case LOCAL: {
                    node.bits |= 2;
                }
            }
        }
    }

    protected boolean moveRecoveryCheckpoint() {
        int pos;
        this.scanner.startPosition = pos = this.lastCheckPoint;
        this.scanner.currentPosition = pos;
        this.scanner.diet = false;
        if (this.restartRecovery) {
            this.lastIgnoredToken = -1;
            this.scanner.insideRecovery = true;
            return true;
        }
        this.lastIgnoredToken = this.nextIgnoredToken;
        this.nextIgnoredToken = -1;
        do {
            try {
                try {
                    this.scanner.lookBack[1] = 0;
                    this.scanner.lookBack[0] = 0;
                    this.nextIgnoredToken = this.scanner.getNextNotFakedToken();
                }
                catch (InvalidInputException invalidInputException) {
                    pos = this.scanner.currentPosition;
                    this.scanner.lookBack[1] = 0;
                    this.scanner.lookBack[0] = 0;
                    continue;
                }
            }
            catch (Throwable throwable) {
                this.scanner.lookBack[1] = 0;
                this.scanner.lookBack[0] = 0;
                throw throwable;
            }
            this.scanner.lookBack[1] = 0;
            this.scanner.lookBack[0] = 0;
        } while (this.nextIgnoredToken < 0);
        if (this.nextIgnoredToken == 64 && this.currentToken == 64) {
            return false;
        }
        if (this.lastCheckPoint == this.scanner.currentPosition) {
            return false;
        }
        this.lastCheckPoint = this.scanner.currentPosition;
        this.scanner.startPosition = pos;
        this.scanner.currentPosition = pos;
        this.scanner.commentPtr = -1;
        this.scanner.foundTaskCount = 0;
        return true;
    }

    protected MessageSend newMessageSend() {
        int length;
        MessageSend m = new MessageSend();
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            m.arguments = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments, 0, length);
        }
        return m;
    }

    protected MessageSend newMessageSendWithTypeArguments() {
        int length;
        MessageSend m = new MessageSend();
        if ((length = this.expressionLengthStack[this.expressionLengthPtr--]) != 0) {
            this.expressionPtr -= length;
            m.arguments = new Expression[length];
            System.arraycopy(this.expressionStack, this.expressionPtr + 1, m.arguments, 0, length);
        }
        return m;
    }

    protected void optimizedConcatNodeLists() {
        this.astLengthStack[--this.astLengthPtr] = this.astLengthStack[this.astLengthPtr] + 1;
    }

    @Override
    public boolean atConflictScenario(int token) {
        if (this.unstackedAct == 17934) {
            return false;
        }
        if (token != 36) {
            token = token == 23 ? 62 : 89;
        }
        return this.automatonWillShift(token, this.unstackedAct);
    }

    /*
     * Unable to fully structure code
     */
    protected void parse() {
        block36: {
            isDietParse = this.diet;
            oldFirstToken = this.getFirstToken();
            this.hasError = false;
            this.hasReportedError = false;
            act = 982;
            this.unstackedAct = 17934;
            this.stateStackTop = -1;
            this.currentToken = this.getFirstToken();
            try {
                this.scanner.setActiveParser(this);
                block12: while (true) {
                    if (++this.stateStackTop >= (stackLength = this.stack.length)) {
                        this.stack = new int[stackLength + 255];
                        System.arraycopy(this.stack, 0, this.stack, 0, stackLength);
                    }
                    this.stack[this.stateStackTop] = act;
                    this.unstackedAct = act = this.actFromTokenOrSynthetic(act);
                    if (act != 17934 && !this.restartRecovery) ** GOTO lbl-1000
                    errorPos = this.scanner.currentPosition - 1;
                    if (!this.hasReportedError) {
                        this.hasError = true;
                    }
                    previousToken = this.currentToken;
                    switch (this.resumeOnSyntaxError()) {
                        case 0: {
                            act = 17934;
                            break block36;
                        }
                        case 1: {
                            if (act == 17934 && previousToken != 0) {
                                this.lastErrorEndPosition = errorPos;
                            }
                            act = 982;
                            this.stateStackTop = -1;
                            this.currentToken = this.getFirstToken();
                            break;
                        }
                        case 2: {
                            if (act == 17934) {
                                act = this.stack[this.stateStackTop--];
                                break;
                            }
                        }
                        default: lbl-1000:
                        // 2 sources

                        {
                            if (act <= 919) {
                                --this.stateStackTop;
                            } else if (act > 17934) {
                                this.consumeToken(this.currentToken);
                                if (this.currentElement != null) {
                                    oldValue = this.recordStringLiterals;
                                    this.recordStringLiterals = false;
                                    this.recoveryTokenCheck();
                                    this.recordStringLiterals = oldValue;
                                }
                                try {
                                    this.currentToken = this.fetchNextToken();
                                }
                                catch (InvalidInputException e) {
                                    if (!this.hasReportedError) {
                                        this.problemReporter().scannerError(this, e.getMessage());
                                        this.hasReportedError = true;
                                    }
                                    this.lastCheckPoint = this.scanner.currentPosition;
                                    this.currentToken = 0;
                                    this.restartRecovery = true;
                                }
                                if (this.statementRecoveryActivated) {
                                    this.jumpOverType();
                                }
                                this.unstackedAct = act -= 17934;
                            } else {
                                if (act >= 17933) break block36;
                                this.consumeToken(this.currentToken);
                                if (this.currentElement != null) {
                                    oldValue = this.recordStringLiterals;
                                    this.recordStringLiterals = false;
                                    this.recoveryTokenCheck();
                                    this.recordStringLiterals = oldValue;
                                }
                                try {
                                    this.currentToken = this.fetchNextToken();
                                }
                                catch (InvalidInputException e) {
                                    if (!this.hasReportedError) {
                                        this.problemReporter().scannerError(this, e.getMessage());
                                        this.hasReportedError = true;
                                    }
                                    this.lastCheckPoint = this.scanner.currentPosition;
                                    this.currentToken = 0;
                                    this.restartRecovery = true;
                                }
                                if (!this.statementRecoveryActivated) continue block12;
                                this.jumpOverType();
                                break;
                            }
                            do {
                                this.stateStackTop -= Parser.rhs[act] - 1;
                                this.unstackedAct = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act]);
                                this.consumeRule(act);
                                act = this.unstackedAct;
                                if (act != 17933) continue;
                                break block36;
                            } while (act <= 919);
                            break;
                        }
                    }
                }
            }
            finally {
                this.unstackedAct = 17934;
                this.scanner.setActiveParser(null);
            }
        }
        this.endParse(act);
        tags = this.scanner.getNLSTags();
        if (tags != null) {
            this.compilationUnit.nlsTags = tags;
        }
        this.scanner.checkNonExternalizedStringLiterals = false;
        if (this.scanner.checkUninternedIdentityComparison) {
            this.compilationUnit.validIdentityComparisonLines = this.scanner.getIdentityComparisonLines();
            this.scanner.checkUninternedIdentityComparison = false;
        }
        if (this.reportSyntaxErrorIsRequired && this.hasError && !this.statementRecoveryActivated) {
            if (!this.options.performStatementsRecovery) {
                this.reportSyntaxErrors(isDietParse, oldFirstToken);
            } else {
                data = this.referenceContext.compilationResult().recoveryScannerData;
                if (this.recoveryScanner == null) {
                    this.recoveryScanner = new RecoveryScanner(this.scanner, data);
                } else {
                    this.recoveryScanner.setData(data);
                }
                this.recoveryScanner.setSource(this.scanner.source);
                this.recoveryScanner.lineEnds = this.scanner.lineEnds;
                this.recoveryScanner.linePtr = this.scanner.linePtr;
                this.reportSyntaxErrors(isDietParse, oldFirstToken);
                if (data == null) {
                    this.referenceContext.compilationResult().recoveryScannerData = this.recoveryScanner.getData();
                }
                if (this.methodRecoveryActivated && this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = false;
                    this.recoverStatements();
                    this.methodRecoveryActivated = true;
                    this.lastAct = 17934;
                }
            }
        }
        this.problemReporter.referenceContext = null;
    }

    protected int fetchNextToken() throws InvalidInputException {
        return this.scanner.getNextToken();
    }

    public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit, boolean recordLineSeparator) {
        ExplicitConstructorCall explicitConstructorCall;
        int length;
        block19: {
            boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
            if (this.options.performMethodsFullRecovery) {
                this.methodRecoveryActivated = true;
                this.ignoreNextOpeningBrace = true;
            }
            this.initialize();
            this.goForBlockStatementsopt();
            if (recordLineSeparator) {
                this.scanner.recordLineSeparator = true;
            }
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.pushOnRealBlockStack(0);
            this.referenceContext = cd;
            this.compilationUnit = unit;
            this.scanner.resetTo(cd.bodyStart, cd.bodyEnd);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    if (this.options.performStatementsRecovery) {
                        this.methodRecoveryActivated = oldMethodRecoveryActivated;
                    }
                    break block19;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);
        if (this.lastAct == 17934) {
            cd.bits |= 0x80000;
            this.initialize();
            return;
        }
        cd.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            this.astPtr -= length;
            if (!this.options.ignoreMethodBodies) {
                if (this.astStack[this.astPtr + 1] instanceof ExplicitConstructorCall) {
                    cd.statements = new Statement[length - 1];
                    System.arraycopy(this.astStack, this.astPtr + 2, cd.statements, 0, length - 1);
                    cd.constructorCall = (ExplicitConstructorCall)this.astStack[this.astPtr + 1];
                } else {
                    cd.statements = new Statement[length];
                    System.arraycopy(this.astStack, this.astPtr + 1, cd.statements, 0, length);
                    cd.constructorCall = SuperReference.implicitSuperConstructorCall();
                }
            }
        } else {
            if (!this.options.ignoreMethodBodies) {
                cd.constructorCall = SuperReference.implicitSuperConstructorCall();
            }
            if (!this.containsComment(cd.bodyStart, cd.bodyEnd)) {
                cd.bits |= 8;
            }
        }
        if ((explicitConstructorCall = cd.constructorCall) != null && explicitConstructorCall.sourceEnd == 0) {
            explicitConstructorCall.sourceEnd = cd.sourceEnd;
            explicitConstructorCall.sourceStart = cd.sourceStart;
        }
    }

    public void parse(FieldDeclaration field, TypeDeclaration type, CompilationUnitDeclaration unit, char[] initializationSource) {
        block7: {
            this.initialize();
            this.goForExpression(true);
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.referenceContext = type;
            this.compilationUnit = unit;
            this.scanner.setSource(initializationSource);
            this.scanner.resetTo(0, initializationSource.length - 1);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    break block7;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
        }
        if (this.lastAct == 17934) {
            field.bits |= 0x80000;
            return;
        }
        field.initialization = this.expressionStack[this.expressionPtr];
        if ((type.bits & 2) != 0) {
            field.bits |= 2;
        }
    }

    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
        return this.parse(sourceUnit, compilationResult, -1, -1);
    }

    public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end) {
        try {
            char[] contents;
            this.initialize(true);
            this.goForCompilationUnit();
            this.compilationUnit = new CompilationUnitDeclaration(this.problemReporter, compilationResult, 0);
            this.referenceContext = this.compilationUnit;
            try {
                contents = this.readManager != null ? this.readManager.getContents(sourceUnit) : sourceUnit.getContents();
            }
            catch (AbortCompilationUnit abortException) {
                this.problemReporter().cannotReadSource(this.compilationUnit, abortException, this.options.verbose);
                contents = CharOperation.NO_CHAR;
            }
            this.scanner.setSource(contents);
            this.compilationUnit.sourceEnd = this.scanner.source.length - 1;
            if (end != -1) {
                this.scanner.resetTo(start, end);
            }
            if (this.javadocParser != null && this.javadocParser.checkDocComment) {
                this.javadocParser.scanner.setSource(contents);
                if (end != -1) {
                    this.javadocParser.scanner.resetTo(start, end);
                }
            }
            this.parse();
        }
        catch (Throwable throwable) {
            CompilationUnitDeclaration unit = this.compilationUnit;
            this.compilationUnit = null;
            if (!this.diet) {
                unit.bits |= 0x10;
            }
            throw throwable;
        }
        CompilationUnitDeclaration unit = this.compilationUnit;
        this.compilationUnit = null;
        if (!this.diet) {
            unit.bits |= 0x10;
        }
        return unit;
    }

    public void parse(Initializer initializer, TypeDeclaration type, CompilationUnitDeclaration unit) {
        int length;
        block14: {
            boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
            if (this.options.performMethodsFullRecovery) {
                this.methodRecoveryActivated = true;
            }
            this.initialize();
            this.goForBlockStatementsopt();
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.pushOnRealBlockStack(0);
            this.referenceContext = type;
            this.compilationUnit = unit;
            this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    if (this.options.performStatementsRecovery) {
                        this.methodRecoveryActivated = oldMethodRecoveryActivated;
                    }
                    break block14;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);
        if (this.lastAct == 17934) {
            initializer.bits |= 0x80000;
            return;
        }
        initializer.block.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) > 0) {
            initializer.block.statements = new Statement[length];
            System.arraycopy(this.astStack, (this.astPtr -= length) + 1, initializer.block.statements, 0, length);
        } else if (!this.containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
            initializer.block.bits |= 8;
        }
        if ((type.bits & 2) != 0) {
            initializer.bits |= 2;
        }
    }

    public void parse(MethodDeclaration md, CompilationUnitDeclaration unit) {
        int length;
        block18: {
            if (md.isAbstract()) {
                return;
            }
            if (md.isNative()) {
                return;
            }
            if ((md.modifiers & 0x1000000) != 0) {
                return;
            }
            boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
            if (this.options.performMethodsFullRecovery) {
                this.ignoreNextOpeningBrace = true;
                this.methodRecoveryActivated = true;
                this.rParenPos = md.sourceEnd;
            }
            this.initialize();
            this.goForBlockStatementsopt();
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.pushOnRealBlockStack(0);
            this.referenceContext = md;
            this.compilationUnit = unit;
            this.scanner.resetTo(md.bodyStart, md.bodyEnd);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    if (this.options.performStatementsRecovery) {
                        this.methodRecoveryActivated = oldMethodRecoveryActivated;
                    }
                    break block18;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                if (this.options.performStatementsRecovery) {
                    this.methodRecoveryActivated = oldMethodRecoveryActivated;
                }
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
            if (this.options.performStatementsRecovery) {
                this.methodRecoveryActivated = oldMethodRecoveryActivated;
            }
        }
        this.checkNonNLSAfterBodyEnd(md.declarationSourceEnd);
        if (this.lastAct == 17934) {
            md.bits |= 0x80000;
            return;
        }
        md.explicitDeclarations = this.realBlockStack[this.realBlockPtr--];
        if (this.astLengthPtr > -1 && (length = this.astLengthStack[this.astLengthPtr--]) != 0) {
            if (this.options.ignoreMethodBodies) {
                this.astPtr -= length;
            } else {
                md.statements = new Statement[length];
                System.arraycopy(this.astStack, (this.astPtr -= length) + 1, md.statements, 0, length);
            }
        } else if (!this.containsComment(md.bodyStart, md.bodyEnd)) {
            md.bits |= 8;
        }
    }

    public ASTNode[] parseClassBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        this.initialize();
        this.goForClassBodyDeclarations();
        return this.parseBodyDeclarations(source, offset, length, unit, (short)1);
    }

    public ASTNode[] parseRecordBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        this.initialize();
        this.goForRecordBodyDeclarations();
        return this.parseBodyDeclarations(source, offset, length, unit, (short)2);
    }

    private ASTNode[] parseBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit, short classRecordType) {
        int astLength;
        TypeDeclaration referenceContextTypeDeclaration;
        block26: {
            boolean oldDiet = this.diet;
            int oldInt = this.dietInt;
            boolean oldTolerateDefaultClassMethods = this.tolerateDefaultClassMethods;
            this.scanner.setSource(source);
            this.scanner.resetTo(offset, offset + length - 1);
            if (this.javadocParser != null && this.javadocParser.checkDocComment) {
                this.javadocParser.scanner.setSource(source);
                this.javadocParser.scanner.resetTo(offset, offset + length - 1);
            }
            this.nestedType = 1;
            referenceContextTypeDeclaration = new TypeDeclaration(unit.compilationResult);
            referenceContextTypeDeclaration.name = Util.EMPTY_STRING.toCharArray();
            referenceContextTypeDeclaration.fields = new FieldDeclaration[0];
            this.compilationUnit = unit;
            unit.types = new TypeDeclaration[1];
            unit.types[0] = referenceContextTypeDeclaration;
            this.referenceContext = unit;
            try {
                try {
                    this.diet = true;
                    this.dietInt = 0;
                    this.tolerateDefaultClassMethods = this.parsingJava8Plus;
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    this.diet = oldDiet;
                    this.dietInt = oldInt;
                    this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
                    break block26;
                }
            }
            catch (Throwable throwable) {
                this.diet = oldDiet;
                this.dietInt = oldInt;
                this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
                throw throwable;
            }
            this.diet = oldDiet;
            this.dietInt = oldInt;
            this.tolerateDefaultClassMethods = oldTolerateDefaultClassMethods;
        }
        ASTNode[] result = null;
        if (this.lastAct == 17934) {
            if (!this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                return null;
            }
            final ArrayList bodyDeclarations = new ArrayList();
            unit.ignoreFurtherInvestigation = false;
            final Predicate<MethodDeclaration> methodPred = classRecordType == 1 ? mD -> !mD.isDefaultConstructor() : mD -> (mD.bits & 0x400) == 0;
            final Consumer<FieldDeclaration> fieldAction = classRecordType == 1 ? fD -> {
                boolean bl = bodyDeclarations.add(fD);
            } : fD -> {
                if ((fD.bits & 0x400) == 0) {
                    bodyDeclarations.add(fD);
                }
            };
            ASTVisitor visitor = new ASTVisitor(){

                @Override
                public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                    if (methodPred.test(methodDeclaration)) {
                        bodyDeclarations.add(methodDeclaration);
                    }
                    return false;
                }

                @Override
                public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
                    fieldAction.accept(fieldDeclaration);
                    return false;
                }

                @Override
                public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
                    bodyDeclarations.add(memberTypeDeclaration);
                    return false;
                }
            };
            unit.traverse(visitor, unit.scope);
            unit.ignoreFurtherInvestigation = true;
            result = bodyDeclarations.toArray(new ASTNode[bodyDeclarations.size()]);
        } else if (this.astLengthPtr > -1 && (astLength = this.astLengthStack[this.astLengthPtr--]) != 0) {
            result = new ASTNode[astLength];
            this.astPtr -= astLength;
            System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
        } else {
            result = new ASTNode[]{};
        }
        boolean containsInitializers = false;
        TypeDeclaration typeDeclaration = null;
        int i = 0;
        int max = result.length;
        while (i < max) {
            ASTNode node = result[i];
            if (node instanceof TypeDeclaration) {
                ((TypeDeclaration)node).parseMethods(this, unit);
            } else if (node instanceof AbstractMethodDeclaration) {
                ((AbstractMethodDeclaration)node).parseStatements(this, unit);
            } else if (node instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration)node;
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        containsInitializers = true;
                        if (typeDeclaration == null) {
                            typeDeclaration = referenceContextTypeDeclaration;
                        }
                        if (typeDeclaration.fields == null) {
                            typeDeclaration.fields = new FieldDeclaration[1];
                            typeDeclaration.fields[0] = fieldDeclaration;
                            break;
                        }
                        int length2 = typeDeclaration.fields.length;
                        FieldDeclaration[] temp = new FieldDeclaration[length2 + 1];
                        System.arraycopy(typeDeclaration.fields, 0, temp, 0, length2);
                        temp[length2] = fieldDeclaration;
                        typeDeclaration.fields = temp;
                    }
                }
            }
            if ((node.bits & 0x80000) != 0 && !this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                return null;
            }
            ++i;
        }
        if (containsInitializers) {
            FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
            int i2 = 0;
            int max2 = fieldDeclarations.length;
            while (i2 < max2) {
                Initializer initializer = (Initializer)fieldDeclarations[i2];
                initializer.parseStatements(this, typeDeclaration, unit);
                if ((initializer.bits & 0x80000) != 0 && !this.options.performMethodsFullRecovery && !this.options.performStatementsRecovery) {
                    return null;
                }
                ++i2;
            }
        }
        return result;
    }

    public Expression parseLambdaExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit, boolean recordLineSeparators) {
        this.haltOnSyntaxError = true;
        this.reparsingLambdaExpression = true;
        return this.parseExpression(source, offset, length, unit, recordLineSeparators);
    }

    public char[][] parsePackageDeclaration(char[] source, CompilationResult result) {
        this.initialize();
        this.goForPackageDeclaration(false);
        this.compilationUnit = new CompilationUnitDeclaration(this.problemReporter(), result, source.length);
        this.referenceContext = this.compilationUnit;
        this.scanner.setSource(source);
        try {
            this.parse();
        }
        catch (AbortCompilation abortCompilation) {
            this.lastAct = 17934;
        }
        if (this.lastAct == 17934) {
            return null;
        }
        return this.compilationUnit.currentPackage == null ? null : this.compilationUnit.currentPackage.getImportName();
    }

    public Expression parseExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit, boolean recordLineSeparators) {
        block6: {
            this.initialize();
            this.goForExpression(recordLineSeparators);
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.referenceContext = unit;
            this.compilationUnit = unit;
            this.scanner.setSource(source);
            this.scanner.resetTo(offset, offset + length - 1);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    break block6;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
        }
        if (this.lastAct == 17934) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }

    public Expression parseMemberValue(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
        block6: {
            this.initialize();
            this.goForMemberValue();
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.referenceContext = unit;
            this.compilationUnit = unit;
            this.scanner.setSource(source);
            this.scanner.resetTo(offset, offset + length - 1);
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    break block6;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
        }
        if (this.lastAct == 17934) {
            return null;
        }
        return this.expressionStack[this.expressionPtr];
    }

    public void parseStatements(ReferenceContext rc, int start, int end, TypeDeclaration[] types, CompilationUnitDeclaration unit) {
        block7: {
            boolean oldStatementRecoveryEnabled = this.statementRecoveryActivated;
            this.statementRecoveryActivated = true;
            this.initialize();
            this.goForBlockStatementsopt();
            int n = this.nestedType;
            this.nestedMethod[n] = this.nestedMethod[n] + 1;
            this.pushOnRealBlockStack(0);
            this.pushOnAstLengthStack(0);
            this.referenceContext = rc;
            this.compilationUnit = unit;
            this.pendingRecoveredType = null;
            if (types != null && types.length > 0) {
                this.recoveredTypes = types;
                this.recoveredTypePtr = 0;
                this.nextTypeStart = this.recoveredTypes[0].allocation == null ? this.recoveredTypes[0].declarationSourceStart : this.recoveredTypes[0].allocation.sourceStart;
            } else {
                this.recoveredTypes = null;
                this.recoveredTypePtr = -1;
                this.nextTypeStart = -1;
            }
            this.scanner.resetTo(start, end);
            this.lastCheckPoint = this.scanner.initialPosition;
            this.stateStackTop = -1;
            try {
                try {
                    this.parse();
                }
                catch (AbortCompilation abortCompilation) {
                    this.lastAct = 17934;
                    int n2 = this.nestedType;
                    this.nestedMethod[n2] = this.nestedMethod[n2] - 1;
                    this.recoveredTypes = null;
                    this.statementRecoveryActivated = oldStatementRecoveryEnabled;
                    break block7;
                }
            }
            catch (Throwable throwable) {
                int n3 = this.nestedType;
                this.nestedMethod[n3] = this.nestedMethod[n3] - 1;
                this.recoveredTypes = null;
                this.statementRecoveryActivated = oldStatementRecoveryEnabled;
                throw throwable;
            }
            int n4 = this.nestedType;
            this.nestedMethod[n4] = this.nestedMethod[n4] - 1;
            this.recoveredTypes = null;
            this.statementRecoveryActivated = oldStatementRecoveryEnabled;
        }
        this.checkNonNLSAfterBodyEnd(end);
    }

    public void persistLineSeparatorPositions() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
    }

    protected void prepareForBlockStatements() {
        this.nestedType = 0;
        this.nestedMethod[0] = 1;
        this.variablesCounter[this.nestedType] = 0;
        this.realBlockPtr = 1;
        this.realBlockStack[1] = 0;
        this.switchNestingLevel = 0;
        this.switchWithTry = false;
    }

    public ProblemReporter problemReporter() {
        if (this.scanner.recordLineSeparator) {
            this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        }
        this.problemReporter.referenceContext = this.referenceContext;
        return this.problemReporter;
    }

    protected void pushIdentifier(char[] identifier, long position) {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            char[][] cArrayArray = new char[stackLength + 20][];
            this.identifierStack = cArrayArray;
            System.arraycopy(this.identifierStack, 0, cArrayArray, 0, stackLength);
            this.identifierPositionStack = new long[stackLength + 20];
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack, 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = identifier;
        this.identifierPositionStack[this.identifierPtr] = position;
        stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            this.identifierLengthStack = new int[stackLength + 10];
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack, 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = 1;
        if (this.parsingJava8Plus && identifier.length == 1 && identifier[0] == '_' && !this.processingLambdaParameterList) {
            this.problemReporter().illegalUseOfUnderscoreAsAnIdentifier((int)(position >>> 32), (int)position, this.parsingJava9Plus);
        }
    }

    protected void pushIdentifier() {
        this.pushIdentifier(this.scanner.getCurrentIdentifierSource(), ((long)this.scanner.startPosition << 32) + (long)(this.scanner.currentPosition - 1));
    }

    protected void pushIdentifier(int flag) {
        int stackLength = this.identifierLengthStack.length;
        if (++this.identifierLengthPtr >= stackLength) {
            this.identifierLengthStack = new int[stackLength + 10];
            System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack, 0, stackLength);
        }
        this.identifierLengthStack[this.identifierLengthPtr] = flag;
    }

    protected void pushOnAstLengthStack(int pos) {
        int stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            this.astLengthStack = new int[stackLength + 255];
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack, 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = pos;
    }

    protected void pushOnPatternStack(ASTNode pattern) {
        int stackLength = this.patternStack.length;
        if (++this.patternPtr >= stackLength) {
            this.patternStack = new ASTNode[stackLength + 100];
            System.arraycopy(this.patternStack, 0, this.patternStack, 0, stackLength);
            this.patternPtr = stackLength;
        }
        this.patternStack[this.patternPtr] = pattern;
        stackLength = this.patternLengthStack.length;
        if (++this.patternLengthPtr >= stackLength) {
            this.patternLengthStack = new int[stackLength + 100];
            System.arraycopy(this.patternLengthStack, 0, this.patternLengthStack, 0, stackLength);
        }
        this.patternLengthStack[this.patternLengthPtr] = 1;
    }

    protected void pushOnAstStack(ASTNode node) {
        int stackLength = this.astStack.length;
        if (++this.astPtr >= stackLength) {
            this.astStack = new ASTNode[stackLength + 100];
            System.arraycopy(this.astStack, 0, this.astStack, 0, stackLength);
            this.astPtr = stackLength;
        }
        this.astStack[this.astPtr] = node;
        stackLength = this.astLengthStack.length;
        if (++this.astLengthPtr >= stackLength) {
            this.astLengthStack = new int[stackLength + 100];
            System.arraycopy(this.astLengthStack, 0, this.astLengthStack, 0, stackLength);
        }
        this.astLengthStack[this.astLengthPtr] = 1;
    }

    protected void pushOnTypeAnnotationStack(Annotation annotation) {
        int stackLength = this.typeAnnotationStack.length;
        if (++this.typeAnnotationPtr >= stackLength) {
            this.typeAnnotationStack = new Annotation[stackLength + 100];
            System.arraycopy(this.typeAnnotationStack, 0, this.typeAnnotationStack, 0, stackLength);
        }
        this.typeAnnotationStack[this.typeAnnotationPtr] = annotation;
        stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            this.typeAnnotationLengthStack = new int[stackLength + 100];
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack, 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = 1;
    }

    protected void pushOnTypeAnnotationLengthStack(int pos) {
        int stackLength = this.typeAnnotationLengthStack.length;
        if (++this.typeAnnotationLengthPtr >= stackLength) {
            this.typeAnnotationLengthStack = new int[stackLength + 100];
            System.arraycopy(this.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack, 0, stackLength);
        }
        this.typeAnnotationLengthStack[this.typeAnnotationLengthPtr] = pos;
    }

    protected void pushOnExpressionStack(Expression expr) {
        int stackLength = this.expressionStack.length;
        if (++this.expressionPtr >= stackLength) {
            this.expressionStack = new Expression[stackLength + 100];
            System.arraycopy(this.expressionStack, 0, this.expressionStack, 0, stackLength);
        }
        this.expressionStack[this.expressionPtr] = expr;
        stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            this.expressionLengthStack = new int[stackLength + 100];
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack, 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = 1;
    }

    protected void pushOnExpressionStackLengthStack(int pos) {
        int stackLength = this.expressionLengthStack.length;
        if (++this.expressionLengthPtr >= stackLength) {
            this.expressionLengthStack = new int[stackLength + 255];
            System.arraycopy(this.expressionLengthStack, 0, this.expressionLengthStack, 0, stackLength);
        }
        this.expressionLengthStack[this.expressionLengthPtr] = pos;
    }

    protected void pushOnGenericsIdentifiersLengthStack(int pos) {
        int stackLength = this.genericsIdentifiersLengthStack.length;
        if (++this.genericsIdentifiersLengthPtr >= stackLength) {
            this.genericsIdentifiersLengthStack = new int[stackLength + 10];
            System.arraycopy(this.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack, 0, stackLength);
        }
        this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] = pos;
    }

    protected void pushOnGenericsLengthStack(int pos) {
        int stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            this.genericsLengthStack = new int[stackLength + 10];
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack, 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = pos;
    }

    protected void pushOnGenericsStack(ASTNode node) {
        int stackLength = this.genericsStack.length;
        if (++this.genericsPtr >= stackLength) {
            this.genericsStack = new ASTNode[stackLength + 10];
            System.arraycopy(this.genericsStack, 0, this.genericsStack, 0, stackLength);
        }
        this.genericsStack[this.genericsPtr] = node;
        stackLength = this.genericsLengthStack.length;
        if (++this.genericsLengthPtr >= stackLength) {
            this.genericsLengthStack = new int[stackLength + 10];
            System.arraycopy(this.genericsLengthStack, 0, this.genericsLengthStack, 0, stackLength);
        }
        this.genericsLengthStack[this.genericsLengthPtr] = 1;
    }

    protected void pushOnIntStack(int pos) {
        int stackLength = this.intStack.length;
        if (++this.intPtr >= stackLength) {
            this.intStack = new int[stackLength + 255];
            System.arraycopy(this.intStack, 0, this.intStack, 0, stackLength);
        }
        this.intStack[this.intPtr] = pos;
    }

    protected void pushOnCaseStack(int pos) {
        int stackLength = this.caseStack.length;
        if (++this.casePtr >= stackLength) {
            this.caseStack = new int[stackLength + 255];
            System.arraycopy(this.caseStack, 0, this.caseStack, 0, stackLength);
        }
        this.caseStack[this.casePtr] = pos;
    }

    protected void pushOnRealBlockStack(int i) {
        int stackLength = this.realBlockStack.length;
        if (++this.realBlockPtr >= stackLength) {
            this.realBlockStack = new int[stackLength + 255];
            System.arraycopy(this.realBlockStack, 0, this.realBlockStack, 0, stackLength);
        }
        this.realBlockStack[this.realBlockPtr] = i;
    }

    protected void recoverStatements() {
        class MethodVisitor
        extends ASTVisitor {
            public ASTVisitor typeVisitor;
            TypeDeclaration enclosingType;
            TypeDeclaration[] types = new TypeDeclaration[0];
            int typePtr = -1;

            MethodVisitor() {
            }

            @Override
            public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                this.endVisitMethod(constructorDeclaration, scope);
            }

            @Override
            public void endVisit(Initializer initializer, MethodScope scope) {
                if (initializer.block == null) {
                    return;
                }
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
                Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                Parser.this.parseStatements(this.enclosingType, initializer.bodyStart, initializer.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                int i = 0;
                while (i < length) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                    ++i;
                }
            }

            @Override
            public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
                this.endVisitMethod(methodDeclaration, scope);
            }

            private void endVisitMethod(AbstractMethodDeclaration methodDeclaration, ClassScope scope) {
                TypeDeclaration[] foundTypes = null;
                int length = 0;
                if (this.typePtr > -1) {
                    length = this.typePtr + 1;
                    foundTypes = new TypeDeclaration[length];
                    System.arraycopy(this.types, 0, foundTypes, 0, length);
                }
                ReferenceContext oldContext = Parser.this.referenceContext;
                Parser.this.recoveryScanner.resetTo(methodDeclaration.bodyStart, methodDeclaration.bodyEnd);
                Scanner oldScanner = Parser.this.scanner;
                Parser.this.scanner = Parser.this.recoveryScanner;
                Parser.this.parseStatements(methodDeclaration, methodDeclaration.bodyStart, methodDeclaration.bodyEnd, foundTypes, Parser.this.compilationUnit);
                Parser.this.scanner = oldScanner;
                Parser.this.referenceContext = oldContext;
                int i = 0;
                while (i < length) {
                    foundTypes[i].traverse(this.typeVisitor, scope);
                    ++i;
                }
            }

            @Override
            public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                this.typePtr = -1;
                return true;
            }

            @Override
            public boolean visit(Initializer initializer, MethodScope scope) {
                this.typePtr = -1;
                return initializer.block != null;
            }

            @Override
            public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                this.typePtr = -1;
                return true;
            }

            private boolean visit(TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    int length = this.typePtr;
                    this.types = new TypeDeclaration[length * 2 + 1];
                    System.arraycopy(this.types, 0, this.types, 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return false;
            }

            @Override
            public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
                return this.visit(typeDeclaration);
            }

            @Override
            public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        MethodVisitor methodVisitor = new MethodVisitor();
        class TypeVisitor
        extends ASTVisitor {
            public MethodVisitor methodVisitor;
            TypeDeclaration[] types = new TypeDeclaration[0];
            int typePtr = -1;

            TypeVisitor() {
            }

            @Override
            public void endVisit(TypeDeclaration typeDeclaration, BlockScope scope) {
                this.endVisitType();
            }

            @Override
            public void endVisit(TypeDeclaration typeDeclaration, ClassScope scope) {
                this.endVisitType();
            }

            private void endVisitType() {
                --this.typePtr;
            }

            @Override
            public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
                if (constructorDeclaration.isDefaultConstructor()) {
                    return false;
                }
                constructorDeclaration.traverse((ASTVisitor)this.methodVisitor, scope);
                return false;
            }

            @Override
            public boolean visit(Initializer initializer, MethodScope scope) {
                if (initializer.block == null) {
                    return false;
                }
                this.methodVisitor.enclosingType = this.types[this.typePtr];
                initializer.traverse((ASTVisitor)this.methodVisitor, scope);
                return false;
            }

            @Override
            public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
                methodDeclaration.traverse((ASTVisitor)this.methodVisitor, scope);
                return false;
            }

            private boolean visit(TypeDeclaration typeDeclaration) {
                if (this.types.length <= ++this.typePtr) {
                    int length = this.typePtr;
                    this.types = new TypeDeclaration[length * 2 + 1];
                    System.arraycopy(this.types, 0, this.types, 0, length);
                }
                this.types[this.typePtr] = typeDeclaration;
                return true;
            }

            @Override
            public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
                return this.visit(typeDeclaration);
            }

            @Override
            public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
                return this.visit(typeDeclaration);
            }
        }
        TypeVisitor typeVisitor = new TypeVisitor();
        methodVisitor.typeVisitor = typeVisitor;
        typeVisitor.methodVisitor = methodVisitor;
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration)this.referenceContext).traverse((ASTVisitor)methodVisitor, (ClassScope)null);
        } else if (this.referenceContext instanceof TypeDeclaration) {
            TypeDeclaration typeContext = (TypeDeclaration)this.referenceContext;
            int length = typeContext.fields.length;
            int i = 0;
            while (i < length) {
                FieldDeclaration fieldDeclaration = typeContext.fields[i];
                switch (fieldDeclaration.getKind()) {
                    case 2: {
                        Initializer initializer = (Initializer)fieldDeclaration;
                        if (initializer.block == null) break;
                        methodVisitor.enclosingType = typeContext;
                        initializer.traverse((ASTVisitor)methodVisitor, null);
                    }
                }
                ++i;
            }
        }
    }

    public void recoveryExitFromVariable() {
        if (this.currentElement != null && this.currentElement.parent != null) {
            if (this.currentElement instanceof RecoveredLocalVariable) {
                int end = ((RecoveredLocalVariable)this.currentElement).localDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            } else if (this.currentElement instanceof RecoveredField && !(this.currentElement instanceof RecoveredInitializer) && this.currentElement.bracketBalance <= 0) {
                int end = ((RecoveredField)this.currentElement).fieldDeclaration.sourceEnd;
                this.currentElement.updateSourceEndIfNecessary(end);
                this.currentElement = this.currentElement.parent;
            }
        }
    }

    public void recoveryTokenCheck() {
        switch (this.currentToken) {
            case 60: {
                if (!this.recordStringLiterals || !this.checkExternalizeStrings || this.lastPosistion >= this.scanner.currentPosition || this.statementRecoveryActivated) break;
                StringLiteral stringLiteral = this.createStringLiteral(this.scanner.getCurrentTokenSourceString(), this.scanner.startPosition, this.scanner.currentPosition - 1, Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
                this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
                break;
            }
            case 38: {
                RecoveredElement newElement = null;
                if (!this.ignoreNextOpeningBrace) {
                    newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
                }
                this.lastCheckPoint = this.scanner.currentPosition;
                if (newElement == null) break;
                this.restartRecovery = true;
                this.currentElement = newElement;
                break;
            }
            case 33: {
                if (this.ignoreNextClosingBrace) {
                    this.ignoreNextClosingBrace = false;
                    break;
                }
                this.rBraceStart = this.scanner.startPosition - 1;
                this.rBraceEnd = this.scanner.currentPosition - 1;
                this.endPosition = this.flushCommentsDefinedPriorTo(this.rBraceEnd);
                RecoveredElement newElement = this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
                this.lastCheckPoint = this.scanner.currentPosition;
                if (newElement == this.currentElement) break;
                this.currentElement = newElement;
                break;
            }
            case 25: {
                this.endStatementPosition = this.scanner.currentPosition - 1;
                this.endPosition = this.scanner.startPosition - 1;
                RecoveredType currentType = this.currentRecoveryType();
                if (currentType != null) {
                    currentType.insideEnumConstantPart = false;
                }
            }
            default: {
                if (this.rBraceEnd <= this.rBraceSuccessorStart || this.scanner.currentPosition == this.scanner.startPosition) break;
                this.rBraceSuccessorStart = this.scanner.startPosition;
            }
        }
        this.ignoreNextOpeningBrace = false;
    }

    protected void reportSyntaxErrors(boolean isDietParse, int oldFirstToken) {
        int end;
        if (this.referenceContext instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration)this.referenceContext;
            if ((methodDeclaration.bits & 0x20) != 0) {
                return;
            }
        }
        this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
        this.scanner.recordLineSeparator = false;
        int start = this.scanner.initialPosition;
        int n = end = this.scanner.eofPosition == Integer.MAX_VALUE ? this.scanner.eofPosition : this.scanner.eofPosition - 1;
        if (isDietParse) {
            TypeDeclaration[] types = this.compilationUnit.types;
            int[][] intervalToSkip = RangeUtil.computeDietRange(types);
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
            diagnoseParser.diagnoseParse(false);
            this.reportSyntaxErrorsForSkippedMethod(types);
            this.scanner.resetTo(start, end);
        } else {
            DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
            diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
        }
    }

    private void reportSyntaxErrorsForSkippedMethod(TypeDeclaration[] types) {
        if (types != null) {
            int i = 0;
            while (i < types.length) {
                FieldDeclaration[] fields;
                AbstractMethodDeclaration[] methods;
                TypeDeclaration[] memberTypes = types[i].memberTypes;
                if (memberTypes != null) {
                    this.reportSyntaxErrorsForSkippedMethod(memberTypes);
                }
                if ((methods = types[i].methods) != null) {
                    int j = 0;
                    while (j < methods.length) {
                        AbstractMethodDeclaration method = methods[j];
                        if ((method.bits & 0x20) != 0) {
                            DiagnoseParser diagnoseParser;
                            if (method.isAnnotationMethod()) {
                                diagnoseParser = new DiagnoseParser(this, 29, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            } else {
                                diagnoseParser = new DiagnoseParser(this, 10, method.declarationSourceStart, method.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                        ++j;
                    }
                }
                if ((fields = types[i].fields) != null) {
                    int length = fields.length;
                    int j = 0;
                    while (j < length) {
                        if (fields[j] instanceof Initializer) {
                            Initializer initializer = (Initializer)fields[j];
                            if ((initializer.bits & 0x20) != 0) {
                                DiagnoseParser diagnoseParser = new DiagnoseParser(this, 14, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
                                diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
                            }
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
    }

    protected void resetModifiers() {
        this.modifiers = 0;
        this.modifiersSourceStart = -1;
        this.annotationAsModifierSourceStart = -1;
        this.scanner.commentPtr = -1;
    }

    protected void resetStacks() {
        this.astPtr = -1;
        this.astLengthPtr = -1;
        this.patternPtr = -1;
        this.patternLengthPtr = -1;
        this.expressionPtr = -1;
        this.expressionLengthPtr = -1;
        this.typeAnnotationLengthPtr = -1;
        this.typeAnnotationPtr = -1;
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        this.intPtr = -1;
        this.nestedType = 0;
        this.nestedMethod[0] = 0;
        this.variablesCounter[this.nestedType] = 0;
        this.switchNestingLevel = 0;
        this.switchWithTry = false;
        this.dimensions = 0;
        this.realBlockPtr = 0;
        this.realBlockStack[0] = 0;
        this.recoveredStaticInitializerStart = 0;
        this.listLength = 0;
        this.listTypeParameterLength = 0;
        this.genericsIdentifiersLengthPtr = -1;
        this.genericsLengthPtr = -1;
        this.genericsPtr = -1;
        this.valueLambdaNestDepth = -1;
        this.recordNestedMethodLevels = new HashMap<TypeDeclaration, Integer[]>();
    }

    protected int resumeAfterRecovery() {
        if (!this.methodRecoveryActivated && !this.statementRecoveryActivated) {
            this.resetStacks();
            this.resetModifiers();
            if (!this.moveRecoveryCheckpoint()) {
                return 0;
            }
            if (this.referenceContext instanceof CompilationUnitDeclaration) {
                this.goForHeaders();
                this.diet = true;
                this.dietInt = 0;
                return 1;
            }
            return 0;
        }
        if (!this.statementRecoveryActivated) {
            this.resetStacks();
            this.resetModifiers();
            if (!this.moveRecoveryCheckpoint()) {
                return 0;
            }
            this.goForHeaders();
            return 1;
        }
        return 0;
    }

    protected int resumeOnSyntaxError() {
        if (this.haltOnSyntaxError) {
            return 0;
        }
        if (this.currentElement == null) {
            this.javadoc = null;
            if (this.statementRecoveryActivated) {
                return 0;
            }
            this.currentElement = this.buildInitialRecoveryState();
        }
        if (this.currentElement == null) {
            return 0;
        }
        if (this.restartRecovery) {
            this.restartRecovery = false;
        }
        this.updateRecoveryState();
        if (this.getFirstToken() == 21 && this.referenceContext instanceof CompilationUnitDeclaration) {
            TypeDeclaration typeDeclaration = new TypeDeclaration(this.referenceContext.compilationResult());
            typeDeclaration.name = Util.EMPTY_STRING.toCharArray();
            this.currentElement = this.currentElement.add(typeDeclaration, 0);
        }
        if (this.lastPosistion < this.scanner.currentPosition) {
            this.lastPosistion = this.scanner.currentPosition;
            this.scanner.lastPosition = this.scanner.currentPosition;
        }
        return this.resumeAfterRecovery();
    }

    public void setMethodsFullRecovery(boolean enabled) {
        this.options.performMethodsFullRecovery = enabled;
    }

    public void setStatementsRecovery(boolean enabled) {
        if (enabled) {
            this.options.performMethodsFullRecovery = true;
        }
        this.options.performStatementsRecovery = enabled;
    }

    public String toString() {
        String s = "lastCheckpoint : int = " + String.valueOf(this.lastCheckPoint) + "\n";
        s = String.valueOf(s) + "identifierStack : char[" + (this.identifierPtr + 1) + "][] = {";
        int i = 0;
        while (i <= this.identifierPtr) {
            s = String.valueOf(s) + "\"" + String.valueOf(this.identifierStack[i]) + "\",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "identifierLengthStack : int[" + (this.identifierLengthPtr + 1) + "] = {";
        i = 0;
        while (i <= this.identifierLengthPtr) {
            s = String.valueOf(s) + this.identifierLengthStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "astLengthStack : int[" + (this.astLengthPtr + 1) + "] = {";
        i = 0;
        while (i <= this.astLengthPtr) {
            s = String.valueOf(s) + this.astLengthStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "astPtr : int = " + String.valueOf(this.astPtr) + "\n";
        s = String.valueOf(s) + "intStack : int[" + (this.intPtr + 1) + "] = {";
        i = 0;
        while (i <= this.intPtr) {
            s = String.valueOf(s) + this.intStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "expressionLengthStack : int[" + (this.expressionLengthPtr + 1) + "] = {";
        i = 0;
        while (i <= this.expressionLengthPtr) {
            s = String.valueOf(s) + this.expressionLengthStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n";
        s = String.valueOf(s) + "genericsIdentifiersLengthStack : int[" + (this.genericsIdentifiersLengthPtr + 1) + "] = {";
        i = 0;
        while (i <= this.genericsIdentifiersLengthPtr) {
            s = String.valueOf(s) + this.genericsIdentifiersLengthStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "genericsLengthStack : int[" + (this.genericsLengthPtr + 1) + "] = {";
        i = 0;
        while (i <= this.genericsLengthPtr) {
            s = String.valueOf(s) + this.genericsLengthStack[i] + ",";
            ++i;
        }
        s = String.valueOf(s) + "}\n";
        s = String.valueOf(s) + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n";
        s = String.valueOf(s) + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        return s;
    }

    protected void updateRecoveryState() {
        this.currentElement.updateFromParserState();
        this.recoveryTokenCheck();
    }

    protected void updateSourceDeclarationParts(int variableDeclaratorsCounter) {
        FieldDeclaration field;
        int endTypeDeclarationPosition = -1 + this.astStack[this.astPtr - variableDeclaratorsCounter + 1].sourceStart;
        int i = 0;
        while (i < variableDeclaratorsCounter - 1) {
            field = (FieldDeclaration)this.astStack[this.astPtr - i - 1];
            field.endPart1Position = endTypeDeclarationPosition;
            field.endPart2Position = -1 + this.astStack[this.astPtr - i].sourceStart;
            ++i;
        }
        field = (FieldDeclaration)this.astStack[this.astPtr];
        ((FieldDeclaration)this.astStack[this.astPtr]).endPart1Position = endTypeDeclarationPosition;
        field.endPart2Position = field.declarationSourceEnd;
    }

    protected void updateSourcePosition(Expression exp) {
        exp.sourceEnd = this.intStack[this.intPtr--];
        exp.sourceStart = this.intStack[this.intPtr--];
    }

    public void copyState(Parser from) {
        Parser parser = from;
        this.stateStackTop = parser.stateStackTop;
        this.unstackedAct = parser.unstackedAct;
        this.identifierPtr = parser.identifierPtr;
        this.identifierLengthPtr = parser.identifierLengthPtr;
        this.astPtr = parser.astPtr;
        this.astLengthPtr = parser.astLengthPtr;
        this.patternPtr = parser.patternPtr;
        this.patternLengthPtr = parser.patternLengthPtr;
        this.expressionPtr = parser.expressionPtr;
        this.expressionLengthPtr = parser.expressionLengthPtr;
        this.genericsPtr = parser.genericsPtr;
        this.genericsLengthPtr = parser.genericsLengthPtr;
        this.genericsIdentifiersLengthPtr = parser.genericsIdentifiersLengthPtr;
        this.typeAnnotationPtr = parser.typeAnnotationPtr;
        this.typeAnnotationLengthPtr = parser.typeAnnotationLengthPtr;
        this.intPtr = parser.intPtr;
        this.nestedType = parser.nestedType;
        this.switchNestingLevel = parser.switchNestingLevel;
        this.switchWithTry = parser.switchWithTry;
        this.realBlockPtr = parser.realBlockPtr;
        this.valueLambdaNestDepth = parser.valueLambdaNestDepth;
        int length = parser.stack.length;
        this.stack = new int[length];
        System.arraycopy(parser.stack, 0, this.stack, 0, length);
        length = parser.identifierStack.length;
        char[][] cArrayArray = new char[length][];
        this.identifierStack = cArrayArray;
        System.arraycopy(parser.identifierStack, 0, cArrayArray, 0, length);
        length = parser.identifierLengthStack.length;
        this.identifierLengthStack = new int[length];
        System.arraycopy(parser.identifierLengthStack, 0, this.identifierLengthStack, 0, length);
        length = parser.identifierPositionStack.length;
        this.identifierPositionStack = new long[length];
        System.arraycopy(parser.identifierPositionStack, 0, this.identifierPositionStack, 0, length);
        length = parser.astStack.length;
        this.astStack = new ASTNode[length];
        System.arraycopy(parser.astStack, 0, this.astStack, 0, length);
        length = parser.astLengthStack.length;
        this.astLengthStack = new int[length];
        System.arraycopy(parser.astLengthStack, 0, this.astLengthStack, 0, length);
        length = parser.expressionStack.length;
        this.expressionStack = new Expression[length];
        System.arraycopy(parser.expressionStack, 0, this.expressionStack, 0, length);
        length = parser.expressionLengthStack.length;
        this.expressionLengthStack = new int[length];
        System.arraycopy(parser.expressionLengthStack, 0, this.expressionLengthStack, 0, length);
        length = parser.genericsStack.length;
        this.genericsStack = new ASTNode[length];
        System.arraycopy(parser.genericsStack, 0, this.genericsStack, 0, length);
        length = parser.genericsLengthStack.length;
        this.genericsLengthStack = new int[length];
        System.arraycopy(parser.genericsLengthStack, 0, this.genericsLengthStack, 0, length);
        length = parser.genericsIdentifiersLengthStack.length;
        this.genericsIdentifiersLengthStack = new int[length];
        System.arraycopy(parser.genericsIdentifiersLengthStack, 0, this.genericsIdentifiersLengthStack, 0, length);
        length = parser.typeAnnotationStack.length;
        this.typeAnnotationStack = new Annotation[length];
        System.arraycopy(parser.typeAnnotationStack, 0, this.typeAnnotationStack, 0, length);
        length = parser.typeAnnotationLengthStack.length;
        this.typeAnnotationLengthStack = new int[length];
        System.arraycopy(parser.typeAnnotationLengthStack, 0, this.typeAnnotationLengthStack, 0, length);
        length = parser.intStack.length;
        this.intStack = new int[length];
        System.arraycopy(parser.intStack, 0, this.intStack, 0, length);
        length = parser.nestedMethod.length;
        this.nestedMethod = new int[length];
        System.arraycopy(parser.nestedMethod, 0, this.nestedMethod, 0, length);
        length = parser.realBlockStack.length;
        this.realBlockStack = new int[length];
        System.arraycopy(parser.realBlockStack, 0, this.realBlockStack, 0, length);
        length = parser.stateStackLengthStack.length;
        this.stateStackLengthStack = new int[length];
        System.arraycopy(parser.stateStackLengthStack, 0, this.stateStackLengthStack, 0, length);
        length = parser.variablesCounter.length;
        this.variablesCounter = new int[length];
        System.arraycopy(parser.variablesCounter, 0, this.variablesCounter, 0, length);
        length = parser.stack.length;
        this.stack = new int[length];
        System.arraycopy(parser.stack, 0, this.stack, 0, length);
        length = parser.stack.length;
        this.stack = new int[length];
        System.arraycopy(parser.stack, 0, this.stack, 0, length);
        length = parser.stack.length;
        this.stack = new int[length];
        System.arraycopy(parser.stack, 0, this.stack, 0, length);
        this.listLength = parser.listLength;
        this.listTypeParameterLength = parser.listTypeParameterLength;
        this.dimensions = parser.dimensions;
        this.recoveredStaticInitializerStart = parser.recoveredStaticInitializerStart;
    }

    public int automatonState() {
        return this.stack[this.stateStackTop];
    }

    public boolean automatonWillShift(int token, int lastAction) {
        int stackTop = this.stateStackTop;
        int stackTopState = this.stack[stackTop];
        int highWaterMark = stackTop--;
        if (lastAction <= 919) {
            lastAction += 17934;
        }
        while (true) {
            if (lastAction > 17934) {
                lastAction -= 17934;
                do {
                    if ((stackTop -= rhs[lastAction] - 1) >= highWaterMark) continue;
                    highWaterMark = stackTop;
                    stackTopState = this.stack[highWaterMark];
                } while ((lastAction = Parser.ntAction(stackTopState, lhs[lastAction])) <= 919);
            }
            highWaterMark = ++stackTop;
            stackTopState = lastAction;
            if ((lastAction = Parser.tAction(lastAction, token)) > 919) break;
            --stackTop;
            lastAction += 17934;
        }
        return lastAction != 17934;
    }

    @Override
    public boolean isParsingJava14() {
        return this.parsingJava14Plus;
    }

    @Override
    public boolean isParsingModuleDeclaration() {
        return this.parsingJava9Plus && this.compilationUnit != null && this.compilationUnit.isModuleInfo();
    }

    private static enum LocalTypeKind {
        LOCAL,
        METHOD_REFERENCE,
        LAMBDA;

    }
}

