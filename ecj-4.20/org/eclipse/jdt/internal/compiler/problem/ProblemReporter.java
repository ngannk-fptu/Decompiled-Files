/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.problem;

import java.io.CharConversionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
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
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BranchStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.impl.JavaFeature;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.ProblemHandler;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ProblemReporter
extends ProblemHandler {
    public ReferenceContext referenceContext;
    private Scanner positionScanner;
    private boolean underScoreIsError;
    private static final byte FIELD_ACCESS = 4;
    private static final byte CONSTRUCTOR_ACCESS = 8;
    private static final byte METHOD_ACCESS = 12;
    private static String RESTRICTED_IDENTIFIER_RECORD = "RestrictedIdentifierrecord";
    private static String RECORD = "record";
    private static String RESTRICTED_IDENTIFIER_SEALED = "RestrictedIdentifiersealed";
    private static String SEALED = "sealed";
    private static String RESTRICTED_IDENTIFIER_PERMITS = "RestrictedIdentifierpermits";
    private static String PERMITS = "permits";
    private static String PREVIEW_KEYWORD_NON_SEALED = "non-sealed";
    private static Map<String, String> permittedRestrictedKeyWordMap = new HashMap<String, String>();

    static {
        permittedRestrictedKeyWordMap.put(RECORD, RESTRICTED_IDENTIFIER_RECORD);
        permittedRestrictedKeyWordMap.put(SEALED, RESTRICTED_IDENTIFIER_SEALED);
        permittedRestrictedKeyWordMap.put(PERMITS, RESTRICTED_IDENTIFIER_PERMITS);
        permittedRestrictedKeyWordMap.put(PREVIEW_KEYWORD_NON_SEALED, PREVIEW_KEYWORD_NON_SEALED);
    }

    public ProblemReporter(IErrorHandlingPolicy policy, CompilerOptions options, IProblemFactory problemFactory) {
        super(policy, options, problemFactory);
    }

    private static int getElaborationId(int leadProblemId, byte elaborationVariant) {
        return leadProblemId << 8 | elaborationVariant;
    }

    public static int getIrritant(int problemID) {
        switch (problemID) {
            case 16777381: {
                return 8;
            }
            case 268435844: {
                return 1024;
            }
            case 67108974: {
                return 1;
            }
            case 67109274: {
                return 2;
            }
            case 67109277: 
            case 67109278: {
                return 16384;
            }
            case 8390037: 
            case 8390038: 
            case 0x1000005: 
            case 16778621: 
            case 33554505: 
            case 33555840: 
            case 67108967: 
            case 67109276: 
            case 67110270: 
            case 67110271: 
            case 67110273: 
            case 0x8000085: {
                return 4;
            }
            case 8390039: 
            case 8390040: 
            case 16778616: 
            case 16778626: 
            case 33555835: 
            case 33555845: 
            case 67110265: 
            case 67110266: 
            case 67110268: 
            case 67110275: 
            case 67110276: 
            case 67110278: {
                return 0x40800000;
            }
            case 536870973: {
                return 16;
            }
            case 536870974: {
                return 32;
            }
            case 0x20000055: {
                return 0x40040000;
            }
            case 536871063: {
                return 64;
            }
            case 33554622: 
            case 33554623: 
            case 0x40000C0: 
            case 67109057: {
                return 128;
            }
            case 536871173: 
            case 536871177: {
                return 256;
            }
            case 536871352: {
                return 512;
            }
            case 536871353: {
                return 0x20000010;
            }
            case 570425420: 
            case 603979893: {
                return 2048;
            }
            case 0x21000012: 
            case 570425422: 
            case 603979895: {
                return 0x10000000;
            }
            case 0x200000B2: {
                return 8192;
            }
            case 553648135: 
            case 570425421: 
            case 603979894: 
            case 603979910: {
                return 32768;
            }
            case 536871002: 
            case 536871006: 
            case 536871007: 
            case 570425435: {
                return 65536;
            }
            case 570425436: 
            case 570425437: {
                return 131072;
            }
            case 0x1000021: 
            case 16777787: 
            case 16777792: 
            case 16777793: {
                return 0x20000400;
            }
            case 536871091: {
                return 262144;
            }
            case 536871092: 
            case 553648316: {
                return 524288;
            }
            case 536871372: {
                return 0x8000000;
            }
            case 553648309: 
            case 553648311: {
                return 0x4000000;
            }
            case 536871096: {
                return 0x1000000;
            }
            case 536871097: 
            case 536871098: {
                return 0x800000;
            }
            case 570425423: {
                return 0x400000;
            }
            case 536871101: {
                return 0x20000001;
            }
            case 0x1000212: 
            case 16777747: 
            case 16777748: 
            case 16777752: 
            case 0x1000221: 
            case 16777785: 
            case 16777786: 
            case 16777801: 
            case 67109423: 
            case 67109438: 
            case 67109670: {
                return 0x20000002;
            }
            case 16777788: {
                return 0x20010000;
            }
            case 67109491: 
            case 67109500: {
                return 0x20000800;
            }
            case 536871540: 
            case 536871541: 
            case 536871542: {
                return 0x20002000;
            }
            case 16777753: {
                return 0x20000004;
            }
            case 0x20000060: {
                return 0x20000008;
            }
            case 0x1000133: {
                return 0x20000020;
            }
            case 0x1000118: {
                return 0x20004000;
            }
            case 67109665: 
            case 134218530: {
                return 0x20000040;
            }
            case 536871363: 
            case 536871373: 
            case 0x200002A0: {
                return 0x20000080;
            }
            case 33555356: 
            case 536871364: 
            case 536871371: 
            case 536871585: 
            case 536871831: 
            case 536871863: 
            case 536871864: {
                return 0x20200000;
            }
            case 536871365: 
            case 536871366: 
            case 536871367: 
            case 536871368: 
            case 536871369: 
            case 536871370: 
            case 536871582: 
            case 536871583: 
            case 536871832: 
            case 536871843: 
            case 536871844: 
            case 536871848: 
            case 536871849: 
            case 536871850: 
            case 536871853: 
            case 536871854: 
            case 536871856: 
            case 536871857: 
            case 536871873: {
                return 0x20400000;
            }
            case 975: 
            case 16778126: 
            case 33555366: 
            case 33555367: 
            case 67109778: 
            case 67109779: 
            case 67109780: 
            case 67109782: 
            case 67109803: 
            case 67109804: 
            case 67109821: 
            case 67109823: 
            case 67109836: 
            case 67109837: 
            case 67109838: 
            case 536871833: 
            case 536871841: 
            case 536871845: 
            case 536871865: 
            case 536871866: 
            case 536871876: 
            case 536871877: 
            case 536871878: {
                return 0x40000400;
            }
            case 969: 
            case 970: 
            case 976: 
            case 977: 
            case 978: 
            case 16778195: {
                return 0x40080000;
            }
            case 16778196: 
            case 16778197: {
                return 0x40100000;
            }
            case 67109781: 
            case 67109810: {
                return 0x40020000;
            }
            case 16778127: {
                return 0x40000800;
            }
            case 16778128: 
            case 67109822: 
            case 67109824: 
            case 536871867: 
            case 536871868: 
            case 536871879: {
                return 0x40001000;
            }
            case 536871895: 
            case 536871896: {
                return 0x50000000;
            }
            case 67109786: 
            case 536871837: 
            case 536871838: 
            case 536871839: 
            case 536871840: 
            case 536871855: 
            case 536871974: 
            case 536871975: {
                return 0x40002000;
            }
            case 0x200002D0: 
            case 536871633: {
                return 0x20000100;
            }
            case 33555193: 
            case 0x2000300: {
                return 0x20001000;
            }
            case 536871678: 
            case 0x200002FF: {
                return 0x40008000;
            }
            case 16777842: {
                return 0x20000200;
            }
            case 0x20000277: {
                return 0x20008000;
            }
            case 536871547: {
                return 0x22000000;
            }
            case 536871111: {
                return 0x20020000;
            }
            case -2130704982: 
            case -1610612274: 
            case -1610612273: 
            case -1610612272: 
            case -1610612271: 
            case -1610612270: 
            case -1610612269: 
            case -1610612268: 
            case -1610612267: 
            case -1610612266: 
            case -1610612264: 
            case -1610612263: 
            case -1610612262: 
            case -1610612260: 
            case -1610612258: 
            case -1610612257: 
            case -1610612256: 
            case -1610612255: 
            case -1610612254: 
            case -1610612253: 
            case -1610612252: 
            case -1610612251: 
            case -1610612249: 
            case -1610612248: 
            case -1610612247: 
            case -1610612246: 
            case -1610612245: 
            case -1610612244: 
            case -1610612243: 
            case -1610612242: 
            case -1610612241: 
            case -1610612240: 
            case -1610612239: 
            case -1610612238: 
            case -1610612237: 
            case -1610612236: 
            case -1610612235: 
            case -1610612234: 
            case -1610612233: 
            case -1610612232: 
            case -1610612231: 
            case -1610612230: 
            case -1610612229: 
            case -1610612228: 
            case -1610612227: 
            case -1610612226: 
            case -1610612225: 
            case -1610612224: 
            case -1610612223: 
            case -1610612221: 
            case -1610612220: 
            case -1610612219: 
            case -1610612218: 
            case -1610612217: 
            case -1610611886: 
            case -1610611885: 
            case -1610611884: 
            case -1610611883: 
            case -1610611882: 
            case -1610611881: 
            case -1610611880: 
            case -1610611879: 
            case -1610611878: 
            case -1610611877: 
            case -1610610935: 
            case -1610610934: 
            case -1610610933: 
            case -1610610932: 
            case -1610610930: 
            case -1610610929: 
            case -1610610928: 
            case -1610610927: 
            case -1610610926: {
                return 0x2000000;
            }
            case -1610612265: 
            case -1610612261: 
            case -1610612259: 
            case -1610610936: 
            case -1610610931: {
                return 0x200000;
            }
            case -1610612250: {
                return 0x100000;
            }
            case 536870971: {
                return 0x20040000;
            }
            case 0x200000C2: {
                return 0x20080000;
            }
            case 67109280: {
                return 0x20100000;
            }
            case 67109443: 
            case 67109524: {
                return 0x21000000;
            }
            case 16777547: {
                return 0x24000000;
            }
            case 536871123: {
                return 0x28000000;
            }
            case 67109281: {
                return 0x30000000;
            }
            case 16777548: {
                return 0x40000001;
            }
            case 536871061: {
                return 0x40000002;
            }
            case 536871362: {
                return 0x40000004;
            }
            case 536871060: {
                return 0x40000008;
            }
            case 603979897: {
                return 0x40000010;
            }
            case 603979898: {
                return 0x40000020;
            }
            case 536871799: 
            case 536871800: {
                return 0x40000080;
            }
            case 536871797: 
            case 536871798: {
                return 0x40000100;
            }
            case 536871801: {
                return 0x40000200;
            }
            case 16778100: {
                return 0x40000040;
            }
            case 536871825: 
            case 536871842: {
                return 0x40004000;
            }
            case 16777877: {
                return 0x40010000;
            }
            case 1200: {
                return 0x40200000;
            }
            case 1201: {
                return 0x40400000;
            }
            case 8390065: 
            case 8390066: 
            case 8390067: {
                return 0x41000000;
            }
            case 8390069: {
                return 0x42000000;
            }
            case 0x400450: 
            case 0x400454: {
                return 0x44000000;
            }
            case 1102: {
                return 0x48000000;
            }
        }
        return 0;
    }

    public static int getProblemCategory(int severity, int problemID) {
        if ((severity & 0x80) == 0) {
            int irritant = ProblemReporter.getIrritant(problemID);
            switch (irritant) {
                case 1: 
                case 128: 
                case 512: 
                case 2048: 
                case 0x400000: 
                case 0x8000000: 
                case 0x10000000: 
                case 0x20000004: 
                case 0x20000010: 
                case 0x20000100: 
                case 0x20000200: 
                case 0x20000800: 
                case 0x20002000: 
                case 0x20040000: 
                case 0x40000010: 
                case 0x40000020: 
                case 0x40000200: {
                    return 80;
                }
                case 8: 
                case 64: 
                case 8192: 
                case 262144: 
                case 524288: 
                case 0x1000000: 
                case 0x20000008: 
                case 0x20000040: 
                case 0x20000080: 
                case 0x20001000: 
                case 0x20080000: 
                case 0x20100000: 
                case 0x20200000: 
                case 0x20400000: 
                case 0x28000000: 
                case 0x30000000: 
                case 0x40000001: 
                case 0x40000002: 
                case 0x40000008: 
                case 0x40000080: 
                case 0x40000100: 
                case 0x40008000: 
                case 0x40080000: 
                case 0x40100000: 
                case 0x40200000: 
                case 0x40400000: 
                case 0x41000000: 
                case 0x42000000: {
                    return 90;
                }
                case 2: 
                case 16384: 
                case 65536: 
                case 131072: 
                case 0x20000400: {
                    return 100;
                }
                case 16: 
                case 32: 
                case 1024: 
                case 32768: 
                case 0x800000: 
                case 0x4000000: 
                case 0x20000001: 
                case 0x20008000: 
                case 0x20020000: 
                case 0x22000000: 
                case 0x24000000: 
                case 0x40000040: 
                case 0x40010000: 
                case 0x40040000: {
                    return 120;
                }
                case 4: 
                case 0x40800000: {
                    return 110;
                }
                case 256: {
                    return 140;
                }
                case 4096: {
                    return 0;
                }
                case 0x100000: 
                case 0x200000: 
                case 0x2000000: 
                case 0x2000004: 
                case 1115684864: {
                    return 70;
                }
                case 0x20000002: 
                case 0x20010000: {
                    return 130;
                }
                case 0x20000020: 
                case 0x20004000: {
                    return 150;
                }
                case 0x40000400: 
                case 0x40000800: 
                case 0x40001000: 
                case 0x40004000: 
                case 0x40020000: 
                case 0x50000000: {
                    return 90;
                }
                case 0x40002000: {
                    return 120;
                }
            }
        }
        switch (problemID) {
            case 8389927: 
            case 0x1000144: 
            case 536871612: 
            case 536871894: {
                return 10;
            }
            case 1102: {
                return 120;
            }
        }
        if ((problemID & 0x40000000) != 0) {
            return 20;
        }
        if ((problemID & 0x10000000) != 0) {
            return 30;
        }
        if ((problemID & 0x1000000) != 0) {
            return 40;
        }
        if ((problemID & 0xE000000) != 0) {
            return 50;
        }
        if ((problemID & 0x800000) != 0) {
            return 160;
        }
        if ((problemID & 0x400000) != 0) {
            return 170;
        }
        if ((problemID & 0x200000) != 0) {
            return 180;
        }
        return 60;
    }

    public void abortDueToInternalError(String errorMessage) {
        this.abortDueToInternalError(errorMessage, null);
    }

    public void abortDueToInternalError(String errorMessage, ASTNode location) {
        String[] arguments = new String[]{errorMessage};
        this.handle(0, arguments, arguments, 159, location == null ? 0 : location.sourceStart, location == null ? 0 : location.sourceEnd);
    }

    public void abortDueToPreviewEnablingNotAllowed(String sourceLevel, String expectedSourceLevel) {
        String[] args = new String[]{sourceLevel, expectedSourceLevel};
        this.handle(2098258, args, args, 159, 0, 0);
    }

    public void abstractMethodCannotBeOverridden(SourceTypeBinding type, MethodBinding concreteMethod) {
        this.handle(67109275, new String[]{new String(type.sourceName()), new String(CharOperation.concat(concreteMethod.declaringClass.readableName(), concreteMethod.readableName(), '.'))}, new String[]{new String(type.sourceName()), new String(CharOperation.concat(concreteMethod.declaringClass.shortReadableName(), concreteMethod.shortReadableName(), '.'))}, type.sourceStart(), type.sourceEnd());
    }

    public void abstractMethodInAbstractClass(SourceTypeBinding type, AbstractMethodDeclaration methodDecl) {
        if (type.isEnum() && type.isLocalType() && type.isAnonymousType()) {
            FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            FieldDeclaration decl = field.sourceField();
            String[] arguments = new String[]{new String(decl.name), new String(methodDecl.selector)};
            this.handle(67109629, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
        } else {
            String[] arguments = new String[]{new String(type.sourceName()), new String(methodDecl.selector)};
            this.handle(67109227, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
    }

    public void abstractMethodInConcreteClass(SourceTypeBinding type) {
        if (type.isEnum() && type.isLocalType() && type.isAnonymousType()) {
            FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            FieldDeclaration decl = field.sourceField();
            String[] arguments = new String[]{new String(decl.name)};
            this.handle(67109628, arguments, arguments, decl.sourceStart(), decl.sourceEnd());
        } else {
            String[] arguments = new String[]{new String(type.sourceName())};
            this.handle(16777549, arguments, arguments, type.sourceStart(), type.sourceEnd());
        }
    }

    public void abstractMethodMustBeImplemented(SourceTypeBinding type, MethodBinding abstractMethod) {
        if (type.isEnum() && type.isLocalType() && type.isAnonymousType()) {
            FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            FieldDeclaration decl = field.sourceField();
            this.handle(67109627, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(decl.name)}, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(decl.name)}, decl.sourceStart(), decl.sourceEnd());
        } else {
            this.handle(67109264, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName()), new String(type.readableName())}, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName()), new String(type.shortReadableName())}, type.sourceStart(), type.sourceEnd());
        }
    }

    public void abstractMethodMustBeImplemented(SourceTypeBinding type, MethodBinding abstractMethod, MethodBinding concreteMethod) {
        this.handle(67109282, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName()), new String(type.readableName()), new String(concreteMethod.selector), this.typesAsString(concreteMethod, false), new String(concreteMethod.declaringClass.readableName())}, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName()), new String(type.shortReadableName()), new String(concreteMethod.selector), this.typesAsString(concreteMethod, true), new String(concreteMethod.declaringClass.shortReadableName())}, type.sourceStart(), type.sourceEnd());
    }

    public void abstractMethodNeedingNoBody(AbstractMethodDeclaration method) {
        this.handle(603979889, NoArgument, NoArgument, method.sourceStart, method.sourceEnd, method, method.compilationResult());
    }

    public void alreadyDefinedLabel(char[] labelName, ASTNode location) {
        String[] arguments = new String[]{new String(labelName)};
        this.handle(536871083, arguments, arguments, location.sourceStart, location.sourceEnd);
    }

    public void annotationCannotOverrideMethod(MethodBinding overrideMethod, MethodBinding inheritedMethod) {
        AbstractMethodDeclaration location = overrideMethod.sourceMethod();
        this.handle(67109480, new String[]{new String(overrideMethod.declaringClass.readableName()), new String(inheritedMethod.declaringClass.readableName()), new String(inheritedMethod.selector), this.typesAsString(inheritedMethod, false)}, new String[]{new String(overrideMethod.declaringClass.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName()), new String(inheritedMethod.selector), this.typesAsString(inheritedMethod, true)}, location.sourceStart, location.sourceEnd);
    }

    public void annotationCircularity(TypeBinding sourceType, TypeBinding otherType, TypeReference reference) {
        if (TypeBinding.equalsEquals(sourceType, otherType)) {
            this.handle(16777822, new String[]{new String(sourceType.readableName())}, new String[]{new String(sourceType.shortReadableName())}, reference.sourceStart, reference.sourceEnd);
        } else {
            this.handle(16777823, new String[]{new String(sourceType.readableName()), new String(otherType.readableName())}, new String[]{new String(sourceType.shortReadableName()), new String(otherType.shortReadableName())}, reference.sourceStart, reference.sourceEnd);
        }
    }

    public void annotationMembersCannotHaveParameters(AnnotationMethodDeclaration annotationMethodDeclaration) {
        this.handle(1610613353, NoArgument, NoArgument, annotationMethodDeclaration.sourceStart, annotationMethodDeclaration.sourceEnd);
    }

    public void annotationMembersCannotHaveTypeParameters(AnnotationMethodDeclaration annotationMethodDeclaration) {
        this.handle(1610613354, NoArgument, NoArgument, annotationMethodDeclaration.sourceStart, annotationMethodDeclaration.sourceEnd);
    }

    public void annotationTypeDeclarationCannotHaveConstructor(ConstructorDeclaration constructorDeclaration) {
        this.handle(1610613360, NoArgument, NoArgument, constructorDeclaration.sourceStart, constructorDeclaration.sourceEnd);
    }

    public void annotationTypeDeclarationCannotHaveSuperclass(TypeDeclaration typeDeclaration) {
        this.handle(1610613355, NoArgument, NoArgument, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void annotationTypeDeclarationCannotHaveSuperinterfaces(TypeDeclaration typeDeclaration) {
        this.handle(1610613356, NoArgument, NoArgument, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void annotationTypeUsedAsSuperinterface(SourceTypeBinding type, TypeReference superInterfaceRef, ReferenceBinding superType) {
        this.handle(16777842, new String[]{new String(superType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(type.sourceName())}, superInterfaceRef.sourceStart, superInterfaceRef.sourceEnd);
    }

    public void annotationValueMustBeAnnotation(TypeBinding annotationType, char[] name, Expression value, TypeBinding expectedType) {
        String str = new String(name);
        this.handle(536871537, new String[]{new String(annotationType.readableName()), str, new String(expectedType.readableName())}, new String[]{new String(annotationType.shortReadableName()), str, new String(expectedType.readableName())}, value.sourceStart, value.sourceEnd);
    }

    public void annotationValueMustBeArrayInitializer(TypeBinding annotationType, char[] name, Expression value) {
        String str = new String(name);
        this.handle(536871544, new String[]{new String(annotationType.readableName()), str}, new String[]{new String(annotationType.shortReadableName()), str}, value.sourceStart, value.sourceEnd);
    }

    public void annotationValueMustBeClassLiteral(TypeBinding annotationType, char[] name, Expression value) {
        String str = new String(name);
        this.handle(536871524, new String[]{new String(annotationType.readableName()), str}, new String[]{new String(annotationType.shortReadableName()), str}, value.sourceStart, value.sourceEnd);
    }

    public void annotationValueMustBeConstant(TypeBinding annotationType, char[] name, Expression value, boolean isEnum) {
        String str = new String(name);
        if (isEnum) {
            this.handle(536871545, new String[]{new String(annotationType.readableName()), str}, new String[]{new String(annotationType.shortReadableName()), str}, value.sourceStart, value.sourceEnd);
        } else {
            this.handle(536871525, new String[]{new String(annotationType.readableName()), str}, new String[]{new String(annotationType.shortReadableName()), str}, value.sourceStart, value.sourceEnd);
        }
    }

    public void anonymousClassCannotExtendFinalClass(TypeReference reference, TypeBinding type) {
        this.handle(0x100001D, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, reference.sourceStart, reference.sourceEnd);
    }

    public void argumentTypeCannotBeVoid(ASTNode methodDecl, Argument arg) {
        String[] arguments = new String[]{new String(arg.name)};
        this.handle(67109228, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void argumentTypeCannotBeVoidArray(Argument arg) {
        this.handle(536870966, NoArgument, NoArgument, arg.type.sourceStart, arg.type.sourceEnd);
    }

    public void arrayConstantsOnlyInArrayInitializers(int sourceStart, int sourceEnd) {
        this.handle(0x600000D0, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void assignmentHasNoEffect(AbstractVariableDeclaration location, char[] name) {
        int severity = this.computeSeverity(0x200000B2);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(name)};
        int start = location.sourceStart;
        int end = location.sourceEnd;
        if (location.initialization != null) {
            end = location.initialization.sourceEnd;
        }
        this.handle(0x200000B2, arguments, arguments, severity, start, end);
    }

    public void assignmentHasNoEffect(Assignment location, char[] name) {
        int severity = this.computeSeverity(0x200000B2);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(name)};
        this.handle(0x200000B2, arguments, arguments, severity, location.sourceStart, location.sourceEnd);
    }

    public void attemptToReturnNonVoidExpression(ReturnStatement returnStatement, TypeBinding expectedType) {
        this.handle(67108969, new String[]{new String(expectedType.readableName())}, new String[]{new String(expectedType.shortReadableName())}, returnStatement.sourceStart, returnStatement.sourceEnd);
    }

    public void attemptToReturnVoidValue(ReturnStatement returnStatement) {
        this.handle(67108970, NoArgument, NoArgument, returnStatement.sourceStart, returnStatement.sourceEnd);
    }

    public void autoboxing(Expression expression, TypeBinding originalType, TypeBinding convertedType) {
        if (this.options.getSeverity(0x20000100) == 256) {
            return;
        }
        this.handle(originalType.isBaseType() ? 0x200002D0 : 536871633, new String[]{new String(originalType.readableName()), new String(convertedType.readableName())}, new String[]{new String(originalType.shortReadableName()), new String(convertedType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void boundCannotBeArray(ASTNode location, TypeBinding type) {
        this.handle(16777784, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void boundMustBeAnInterface(ASTNode location, TypeBinding type) {
        this.handle(0x1000211, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void bytecodeExceeds64KLimit(SwitchStatement switchStatement) {
        TypeBinding enumType = switchStatement.expression.resolvedType;
        this.handle(536870998, new String[]{new String(enumType.readableName())}, new String[]{new String(enumType.shortReadableName())}, 159, switchStatement.sourceStart(), switchStatement.sourceEnd());
    }

    public void bytecodeExceeds64KLimit(MethodBinding method, int start, int end) {
        this.handle(536870975, new String[]{new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.selector), this.typesAsString(method, true)}, 159, start, end);
    }

    public void bytecodeExceeds64KLimit(AbstractMethodDeclaration location) {
        MethodBinding method = location.binding;
        if (location.isConstructor()) {
            this.handle(536870981, new String[]{new String(location.selector), this.typesAsString(method, false)}, new String[]{new String(location.selector), this.typesAsString(method, true)}, 159, location.sourceStart, location.sourceEnd);
        } else {
            this.bytecodeExceeds64KLimit(method, location.sourceStart, location.sourceEnd);
        }
    }

    public void bytecodeExceeds64KLimit(LambdaExpression location) {
        this.bytecodeExceeds64KLimit(location.binding, location.sourceStart, location.diagnosticsSourceEnd());
    }

    public void bytecodeExceeds64KLimit(TypeDeclaration location) {
        this.handle(0x20000040, NoArgument, NoArgument, 159, location.sourceStart, location.sourceEnd);
    }

    public void cannotAllocateVoidArray(Expression expression) {
        this.handle(536870966, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void cannotAssignToFinalField(FieldBinding field, ASTNode location) {
        this.handle(0x2000050, new String[]{field.declaringClass == null ? "array" : new String(field.declaringClass.readableName()), new String(field.readableName())}, new String[]{field.declaringClass == null ? "array" : new String(field.declaringClass.shortReadableName()), new String(field.shortReadableName())}, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void cannotAssignToFinalLocal(LocalVariableBinding local, ASTNode location) {
        int problemId = 0;
        problemId = (local.tagBits & 0x1000L) != 0L ? 536871782 : ((local.tagBits & 0x2000L) != 0L ? 536871784 : 536870970);
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(problemId, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void cannotAssignToFinalOuterLocal(LocalVariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(536870972, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void cannotDefineDimensionsAndInitializer(ArrayAllocationExpression expresssion) {
        this.handle(536871070, NoArgument, NoArgument, expresssion.sourceStart, expresssion.sourceEnd);
    }

    public void cannotDireclyInvokeAbstractMethod(ASTNode invocationSite, MethodBinding method) {
        this.handle(67108968, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, invocationSite.sourceStart, invocationSite.sourceEnd);
    }

    public void cannotExtendEnum(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777972, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, superclass.sourceStart, superclass.sourceEnd);
    }

    public void cannotImportPackage(ImportReference importRef) {
        String[] arguments = new String[]{CharOperation.toString(importRef.tokens)};
        this.handleUntagged(268435843, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }

    public void cannotInstantiate(Expression typeRef, TypeBinding type) {
        this.handle(16777373, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, typeRef.sourceStart, typeRef.sourceEnd);
    }

    public void cannotInvokeSuperConstructorInEnum(ExplicitConstructorCall constructorCall, MethodBinding enumConstructor) {
        this.handle(67109621, new String[]{new String(enumConstructor.declaringClass.sourceName()), this.typesAsString(enumConstructor, false)}, new String[]{new String(enumConstructor.declaringClass.sourceName()), this.typesAsString(enumConstructor, true)}, constructorCall.sourceStart, constructorCall.sourceEnd);
    }

    public void cannotReadSource(CompilationUnitDeclaration unit, AbortCompilationUnit abortException, boolean verbose) {
        String fileName = new String(unit.compilationResult.fileName);
        if (abortException.exception instanceof CharConversionException) {
            String encoding = abortException.encoding;
            if (encoding == null) {
                encoding = System.getProperty("file.encoding");
            }
            String[] arguments = new String[]{fileName, encoding};
            this.handle(536871613, arguments, arguments, 0, 0);
            return;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        if (verbose) {
            abortException.exception.printStackTrace(writer);
            System.err.println(stringWriter.toString());
            stringWriter = new StringWriter();
            writer = new PrintWriter(stringWriter);
        }
        writer.print(abortException.exception.getClass().getName());
        writer.print(':');
        writer.print(abortException.exception.getMessage());
        String exceptionTrace = stringWriter.toString();
        String[] arguments = new String[]{fileName, exceptionTrace};
        this.handle(536871614, arguments, arguments, 0, 0);
    }

    public void cannotReferToNonFinalOuterLocal(LocalVariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(536870937, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void cannotReferToNonEffectivelyFinalOuterLocal(VariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(536871575, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void cannotReferToNonFinalField(VariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(536871581, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void cannotReturnInInitializer(ASTNode location) {
        this.handle(0x200000A2, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void cannotThrowNull(ASTNode expression) {
        this.handle(536871089, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void cannotThrowType(ASTNode exception, TypeBinding expectedType) {
        this.handle(0x1000140, new String[]{new String(expectedType.readableName())}, new String[]{new String(expectedType.shortReadableName())}, exception.sourceStart, exception.sourceEnd);
    }

    public void illegalArrayOfUnionType(char[] identifierName, TypeReference typeReference) {
        this.handle(16777878, NoArgument, NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }

    public void cannotUseQualifiedEnumConstantInCaseLabel(Reference location, FieldBinding field) {
        this.handle(33555187, new String[]{String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name)}, new String[]{String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name)}, location.sourceStart(), location.sourceEnd());
    }

    public void cannotUseSuperInCodeSnippet(int start, int end) {
        this.handle(536871334, NoArgument, NoArgument, 159, start, end);
    }

    public void cannotUseSuperInJavaLangObject(ASTNode reference) {
        this.handle(0x1000001, NoArgument, NoArgument, reference.sourceStart, reference.sourceEnd);
    }

    public void targetTypeIsNotAFunctionalInterface(FunctionalExpression target) {
        this.handle(553648781, NoArgument, NoArgument, target.sourceStart, target.diagnosticsSourceEnd());
    }

    public void illFormedParameterizationOfFunctionalInterface(FunctionalExpression target) {
        this.handle(553648783, NoArgument, NoArgument, target.sourceStart, target.diagnosticsSourceEnd());
    }

    public void lambdaSignatureMismatched(LambdaExpression target) {
        this.handle(553648784, new String[]{new String(target.descriptor.readableName())}, new String[]{new String(target.descriptor.shortReadableName())}, target.sourceStart, target.diagnosticsSourceEnd());
    }

    public void lambdaParameterTypeMismatched(Argument argument, TypeReference type, TypeBinding expectedParameterType) {
        String name = new String(argument.name);
        String expectedTypeFullName = new String(expectedParameterType.readableName());
        String expectedTypeShortName = new String(expectedParameterType.shortReadableName());
        this.handle(expectedParameterType.isTypeVariable() ? 553648786 : 553648785, new String[]{name, expectedTypeFullName}, new String[]{name, expectedTypeShortName}, type.sourceStart, type.sourceEnd);
    }

    public void lambdaExpressionCannotImplementGenericMethod(LambdaExpression lambda, MethodBinding sam) {
        String selector = new String(sam.selector);
        this.handle(553648787, new String[]{selector, new String(sam.declaringClass.readableName())}, new String[]{selector, new String(sam.declaringClass.shortReadableName())}, lambda.sourceStart, lambda.diagnosticsSourceEnd());
    }

    public void missingValueFromLambda(LambdaExpression lambda, TypeBinding returnType) {
        this.handle(536871916, new String[]{new String(returnType.readableName())}, new String[]{new String(returnType.shortReadableName())}, lambda.sourceStart, lambda.diagnosticsSourceEnd());
    }

    public void caseExpressionMustBeConstant(Expression expression) {
        this.handle(0x20000099, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void classExtendFinalClass(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777529, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, superclass.sourceStart, superclass.sourceEnd);
    }

    public void codeSnippetMissingClass(String missing, int start, int end) {
        String[] arguments = new String[]{missing};
        this.handle(536871332, arguments, arguments, 159, start, end);
    }

    public void codeSnippetMissingMethod(String className, String missingMethod, String argumentTypes, int start, int end) {
        String[] arguments = new String[]{className, missingMethod, argumentTypes};
        this.handle(536871333, arguments, arguments, 159, start, end);
    }

    public void comparingIdenticalExpressions(Expression comparison) {
        int severity = this.computeSeverity(536871123);
        if (severity == 256) {
            return;
        }
        this.handle(536871123, NoArgument, NoArgument, severity, comparison.sourceStart, comparison.sourceEnd);
    }

    @Override
    public int computeSeverity(int problemID) {
        switch (problemID) {
            case 67109667: 
            case 536872627: 
            case 536872629: 
            case 1073743536: {
                return 0;
            }
            case 16777538: {
                return 1;
            }
            case -1610612270: 
            case -1610612268: 
            case -1610612264: 
            case -1610612263: 
            case -1610612262: 
            case -1610612258: 
            case -1610612256: 
            case -1610612255: 
            case -1610612254: 
            case -1610612248: 
            case -1610612246: 
            case -1610612244: 
            case -1610612242: 
            case -1610612240: 
            case -1610612238: 
            case -1610612236: 
            case -1610612235: 
            case -1610612234: 
            case -1610612233: 
            case -1610612231: 
            case -1610612229: 
            case -1610612228: 
            case -1610612227: 
            case -1610612226: 
            case -1610612225: 
            case -1610612219: 
            case -1610611886: 
            case -1610611885: 
            case -1610611884: 
            case -1610611883: 
            case -1610611882: 
            case -1610611881: 
            case -1610611880: 
            case -1610611879: 
            case -1610611878: 
            case -1610611877: {
                if (this.options.reportInvalidJavadocTags) break;
                return 256;
            }
            case -1610612245: 
            case -1610612241: 
            case -1610612237: 
            case -1610612230: {
                if (this.options.reportInvalidJavadocTags && this.options.reportInvalidJavadocTagsDeprecatedRef) break;
                return 256;
            }
            case -1610612271: 
            case -1610612247: 
            case -1610612243: 
            case -1610612239: 
            case -1610612232: {
                if (this.options.reportInvalidJavadocTags && this.options.reportInvalidJavadocTagsNotVisibleRef) break;
                return 256;
            }
            case -1610612220: {
                if (!"no_tag".equals(this.options.reportMissingJavadocTagDescription)) break;
                return 256;
            }
            case -1610612273: {
                if ("all_standard_tags".equals(this.options.reportMissingJavadocTagDescription)) break;
                return 256;
            }
            case 16778125: 
            case 1610613402: {
                return 0;
            }
            case 1610613179: {
                return this.underScoreIsError ? 1 : 0;
            }
            case 536872732: {
                return 0;
            }
        }
        int irritant = ProblemReporter.getIrritant(problemID);
        if (irritant != 0) {
            if ((problemID & Integer.MIN_VALUE) != 0 && !this.options.docCommentSupport) {
                return 256;
            }
            return this.options.getSeverity(irritant);
        }
        return 129;
    }

    public void conditionalArgumentsIncompatibleTypes(ConditionalExpression expression, TypeBinding trueType, TypeBinding falseType) {
        this.handle(0x1000010, new String[]{new String(trueType.readableName()), new String(falseType.readableName())}, new String[]{new String(trueType.shortReadableName()), new String(falseType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void conflictingImport(ImportReference importRef) {
        String[] arguments = new String[]{CharOperation.toString(importRef.tokens)};
        this.handleUntagged(0x10000181, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }

    public void constantOutOfRange(Literal literal, TypeBinding literalType) {
        String[] arguments = new String[]{new String(literalType.readableName()), new String(literal.source())};
        this.handle(536871066, arguments, arguments, literal.sourceStart, literal.sourceEnd);
    }

    public void corruptedSignature(TypeBinding enclosingType, char[] signature, int position) {
        this.handle(536871612, new String[]{new String(enclosingType.readableName()), new String(signature), String.valueOf(position)}, new String[]{new String(enclosingType.shortReadableName()), new String(signature), String.valueOf(position)}, 159, 0, 0);
    }

    public void defaultMethodOverridesObjectMethod(MethodBinding currentMethod) {
        AbstractMethodDeclaration method = currentMethod.sourceMethod();
        int sourceStart = 0;
        int sourceEnd = 0;
        if (method != null) {
            sourceStart = method.sourceStart;
            sourceEnd = method.sourceEnd;
        }
        this.handle(67109915, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void defaultModifierIllegallySpecified(int sourceStart, int sourceEnd) {
        this.handle(0x4000422, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void deprecatedField(FieldBinding field, ASTNode location) {
        String fieldName = new String(field.name);
        int sourceStart = this.nodeSourceStart(field, location);
        int sourceEnd = this.nodeSourceEnd(field, location);
        String sinceValue = this.deprecatedSinceValue(() -> field.getAnnotations());
        if (sinceValue != null) {
            this.handle((field.tagBits & 0x4000000000000000L) == 0L ? 33555840 : 33555845, new String[]{new String(field.declaringClass.readableName()), fieldName, sinceValue}, new String[]{new String(field.declaringClass.shortReadableName()), fieldName, sinceValue}, sourceStart, sourceEnd);
        } else {
            this.handle((field.tagBits & 0x4000000000000000L) == 0L ? 33554505 : 33555835, new String[]{new String(field.declaringClass.readableName()), fieldName}, new String[]{new String(field.declaringClass.shortReadableName()), fieldName}, sourceStart, sourceEnd);
        }
    }

    public void deprecatedMethod(MethodBinding method, ASTNode location) {
        String readableClassName = new String(method.declaringClass.readableName());
        String shortReadableClassName = new String(method.declaringClass.shortReadableName());
        String selector = new String(method.selector);
        String signature = this.typesAsString(method, false);
        String shortSignature = this.typesAsString(method, true);
        boolean isConstructor = method.isConstructor();
        int start = -1;
        if (isConstructor) {
            if (location instanceof AllocationExpression) {
                AllocationExpression allocationExpression = (AllocationExpression)location;
                start = allocationExpression.nameSourceStart();
            }
        } else if (location instanceof MessageSend) {
            start = (int)(((MessageSend)location).nameSourcePosition >>> 32);
        }
        int sourceStart = start == -1 ? location.sourceStart : start;
        int sourceEnd = location.sourceEnd;
        boolean terminally = (method.tagBits & 0x4000000000000000L) != 0L;
        String sinceValue = this.deprecatedSinceValue(() -> method.getAnnotations());
        if (sinceValue == null && method.isConstructor()) {
            sinceValue = this.deprecatedSinceValue(() -> methodBinding.declaringClass.getAnnotations());
        }
        if (sinceValue != null) {
            if (isConstructor) {
                this.handle(terminally ? 67110276 : 67110271, new String[]{readableClassName, signature, sinceValue}, new String[]{shortReadableClassName, shortSignature, sinceValue}, sourceStart, sourceEnd);
            } else {
                this.handle(terminally ? 67110275 : 67110270, new String[]{readableClassName, selector, signature, sinceValue}, new String[]{shortReadableClassName, selector, shortSignature, sinceValue}, sourceStart, sourceEnd);
            }
        } else if (isConstructor) {
            this.handle(terminally ? 67110266 : 0x8000085, new String[]{readableClassName, signature}, new String[]{shortReadableClassName, shortSignature}, sourceStart, sourceEnd);
        } else {
            this.handle(terminally ? 67110265 : 67108967, new String[]{readableClassName, selector, signature}, new String[]{shortReadableClassName, selector, shortSignature}, sourceStart, sourceEnd);
        }
    }

    public void deprecatedType(TypeBinding type, ASTNode location) {
        this.deprecatedType(type, location, Integer.MAX_VALUE);
    }

    public void deprecatedType(TypeBinding type, ASTNode location, int index) {
        String sinceValue;
        if (location == null) {
            return;
        }
        TypeBinding leafType = type.leafComponentType();
        int sourceStart = -1;
        if (location instanceof QualifiedTypeReference) {
            QualifiedTypeReference ref = (QualifiedTypeReference)location;
            if (index < Integer.MAX_VALUE) {
                sourceStart = (int)(ref.sourcePositions[index] >> 32);
            }
        }
        if ((sinceValue = this.deprecatedSinceValue(() -> leafType.getAnnotations())) != null) {
            this.handle((leafType.tagBits & 0x4000000000000000L) == 0L ? 16778621 : 16778626, new String[]{new String(leafType.readableName()), sinceValue}, new String[]{new String(leafType.shortReadableName()), sinceValue}, sourceStart == -1 ? location.sourceStart : sourceStart, this.nodeSourceEnd(null, location, index));
        } else {
            this.handle((leafType.tagBits & 0x4000000000000000L) == 0L ? 0x1000005 : 16778616, new String[]{new String(leafType.readableName())}, new String[]{new String(leafType.shortReadableName())}, sourceStart == -1 ? location.sourceStart : sourceStart, this.nodeSourceEnd(null, location, index));
        }
    }

    public void deprecatedModule(ModuleReference moduleReference, ModuleBinding requiredModule) {
        boolean isTerminally;
        String sinceValue = this.deprecatedSinceValue(() -> requiredModule.getAnnotations());
        boolean bl = isTerminally = (requiredModule.tagBits & 0x4000000000000000L) != 0L;
        if (sinceValue != null) {
            String[] args = new String[]{String.valueOf(requiredModule.name()), sinceValue};
            this.handle(isTerminally ? 8390040 : 8390038, args, args, moduleReference.sourceStart, moduleReference.sourceEnd);
        } else {
            String[] args = new String[]{String.valueOf(requiredModule.name())};
            this.handle(isTerminally ? 8390039 : 8390037, args, args, moduleReference.sourceStart, moduleReference.sourceEnd);
        }
    }

    String deprecatedSinceValue(Supplier<AnnotationBinding[]> annotations) {
        if (this.options != null && this.options.complianceLevel >= 0x350000L) {
            ReferenceContext contextSave = this.referenceContext;
            try {
                AnnotationBinding[] annotationBindingArray = annotations.get();
                int n = annotationBindingArray.length;
                int n2 = 0;
                while (n2 < n) {
                    AnnotationBinding annotationBinding = annotationBindingArray[n2];
                    if (annotationBinding.getAnnotationType().id == 44) {
                        ElementValuePair[] elementValuePairArray = annotationBinding.getElementValuePairs();
                        int n3 = elementValuePairArray.length;
                        int n4 = 0;
                        while (n4 < n3) {
                            ElementValuePair elementValuePair = elementValuePairArray[n4];
                            if (CharOperation.equals(elementValuePair.getName(), TypeConstants.SINCE) && elementValuePair.value instanceof StringConstant) {
                                String string = ((StringConstant)elementValuePair.value).stringValue();
                                return string;
                            }
                            ++n4;
                        }
                        break;
                    }
                    ++n2;
                }
            }
            finally {
                this.referenceContext = contextSave;
            }
        }
        return null;
    }

    public void disallowedTargetForAnnotation(Annotation annotation) {
        this.handle(16777838, new String[]{new String(annotation.resolvedType.readableName())}, new String[]{new String(annotation.resolvedType.shortReadableName())}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void polymorphicMethodNotBelow17(ASTNode node) {
        this.handle(67109740, NoArgument, NoArgument, node.sourceStart, node.sourceEnd);
    }

    public void multiCatchNotBelow17(ASTNode node) {
        this.handle(1610613611, NoArgument, NoArgument, node.sourceStart, node.sourceEnd);
    }

    public void duplicateAnnotation(Annotation annotation, long sourceLevel) {
        this.handle(sourceLevel >= 0x340000L ? 16778113 : 16777824, new String[]{new String(annotation.resolvedType.readableName())}, new String[]{new String(annotation.resolvedType.shortReadableName())}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void duplicateAnnotationValue(TypeBinding annotationType, MemberValuePair memberValuePair) {
        String name = new String(memberValuePair.name);
        this.handle(0x20000262, new String[]{name, new String(annotationType.readableName())}, new String[]{name, new String(annotationType.shortReadableName())}, memberValuePair.sourceStart, memberValuePair.sourceEnd);
    }

    public void duplicateBounds(ASTNode location, TypeBinding type) {
        this.handle(16777783, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void duplicateCase(CaseStatement caseStatement) {
        this.handle(0x20000AA, NoArgument, NoArgument, caseStatement.sourceStart, caseStatement.sourceEnd);
    }

    public void duplicateDefaultCase(ASTNode statement) {
        this.handle(536871078, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void duplicateEnumSpecialMethod(SourceTypeBinding type, AbstractMethodDeclaration methodDecl) {
        MethodBinding method = methodDecl.binding;
        this.handle(67109618, new String[]{new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void duplicateFieldInType(SourceTypeBinding type, FieldDeclaration fieldDecl) {
        this.handle(33554772, new String[]{new String(type.sourceName()), new String(fieldDecl.name)}, new String[]{new String(type.shortReadableName()), new String(fieldDecl.name)}, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void duplicateImport(ImportReference importRef) {
        String[] arguments = new String[]{CharOperation.toString(importRef.tokens)};
        this.handleUntagged(268435842, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }

    public void duplicateInheritedMethods(SourceTypeBinding type, MethodBinding inheritedMethod1, MethodBinding inheritedMethod2, boolean isJava8) {
        if (TypeBinding.notEquals(inheritedMethod1.declaringClass, inheritedMethod2.declaringClass)) {
            int problemID = 67109447;
            if (inheritedMethod1.isDefaultMethod() && inheritedMethod2.isDefaultMethod()) {
                if (isJava8) {
                    problemID = 67109917;
                } else {
                    return;
                }
            }
            this.handle(problemID, new String[]{new String(inheritedMethod1.selector), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, false), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, false), new String(inheritedMethod1.declaringClass.readableName()), new String(inheritedMethod2.declaringClass.readableName())}, new String[]{new String(inheritedMethod1.selector), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, true), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, true), new String(inheritedMethod1.declaringClass.shortReadableName()), new String(inheritedMethod2.declaringClass.shortReadableName())}, type.sourceStart(), type.sourceEnd());
            return;
        }
        this.handle(67109429, new String[]{new String(inheritedMethod1.selector), new String(inheritedMethod1.declaringClass.readableName()), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, false), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, false)}, new String[]{new String(inheritedMethod1.selector), new String(inheritedMethod1.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, true), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, true)}, type.sourceStart(), type.sourceEnd());
    }

    public void duplicateInitializationOfBlankFinalField(FieldBinding field, Reference reference) {
        String[] arguments = new String[]{new String(field.readableName())};
        this.handle(0x2000052, arguments, arguments, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }

    public void duplicateInitializationOfFinalLocal(LocalVariableBinding local, ASTNode location) {
        int problemId = local.isPatternVariable() ? 536872693 : 536870969;
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(problemId, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void illegalRedeclarationOfPatternVar(LocalVariableBinding local, ASTNode location) {
        this.handle(536872696, NoArgument, NoArgument, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void patternCannotBeSubtypeOfExpression(LocalVariableBinding local, ASTNode location) {
        this.handle(536872694, NoArgument, NoArgument, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void duplicateMethodInType(AbstractMethodDeclaration methodDecl, boolean equalParameters, int severity) {
        MethodBinding method = methodDecl.binding;
        if (equalParameters) {
            this.handle(67109219, new String[]{new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
        } else {
            this.handle(16777743, new String[]{new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
    }

    public void duplicateModifierForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33554773, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void duplicateModifierForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
        this.handle(67109221, new String[]{new String(type.sourceName()), new String(methodDecl.selector)}, new String[]{new String(type.shortReadableName()), new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void duplicateModifierForType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777517, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void duplicateModifierForVariable(LocalDeclaration localDecl, boolean complainForArgument) {
        String[] arguments = new String[]{new String(localDecl.name)};
        this.handle(complainForArgument ? 67109232 : 67109259, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }

    public void duplicateNestedType(TypeDeclaration typeDecl) {
        String[] arguments = new String[]{new String(typeDecl.name)};
        this.handle(16777535, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void duplicateSuperinterface(SourceTypeBinding type, TypeReference reference, ReferenceBinding superType) {
        this.handle(16777530, new String[]{new String(superType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(type.sourceName())}, reference.sourceStart, reference.sourceEnd);
    }

    public void duplicateTargetInTargetAnnotation(TypeBinding annotationType, NameReference reference) {
        FieldBinding field = reference.fieldBinding();
        String name = new String(field.name);
        this.handle(536871533, new String[]{name, new String(annotationType.readableName())}, new String[]{name, new String(annotationType.shortReadableName())}, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }

    public void duplicateTypeParameterInType(TypeParameter typeParameter) {
        this.handle(0x20000208, new String[]{new String(typeParameter.name)}, new String[]{new String(typeParameter.name)}, typeParameter.sourceStart, typeParameter.sourceEnd);
    }

    public void duplicateTypes(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
        String[] arguments = new String[]{new String(compUnitDecl.getFileName()), new String(typeDecl.name)};
        this.referenceContext = typeDecl;
        int end = typeDecl.sourceEnd;
        if (end <= 0) {
            end = -1;
        }
        this.handle(16777539, arguments, arguments, typeDecl.sourceStart, end, compUnitDecl.compilationResult);
    }

    public void emptyControlFlowStatement(int sourceStart, int sourceEnd) {
        this.handle(553648316, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void enumAbstractMethodMustBeImplemented(AbstractMethodDeclaration method) {
        MethodBinding abstractMethod = method.binding;
        this.handle(67109622, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName())}, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName())}, method.sourceStart(), method.sourceEnd());
    }

    public void enumConstantMustImplementAbstractMethod(AbstractMethodDeclaration method, FieldDeclaration field) {
        MethodBinding abstractMethod = method.binding;
        this.handle(67109627, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(field.name)}, new String[]{new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(field.name)}, field.sourceStart(), field.sourceEnd());
    }

    public void enumConstantsCannotBeSurroundedByParenthesis(Expression expression) {
        this.handle(1610613178, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void enumStaticFieldUsedDuringInitialization(FieldBinding field, ASTNode location) {
        this.handle(33555194, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void enumSwitchCannotTargetField(Reference reference, FieldBinding field) {
        this.handle(33555191, new String[]{String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name)}, new String[]{String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name)}, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }

    public void errorNoMethodFor(MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        int i = 0;
        int length = params.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
            ++i;
        }
        int id = recType.isArrayType() ? 0x4000074 : 67108978;
        this.handle(id, new String[]{new String(recType.readableName()), new String(messageSend.selector), buffer.toString()}, new String[]{new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString()}, messageSend.sourceStart, messageSend.sourceEnd);
    }

    public void errorNoMethodFor(Expression expression, TypeBinding recType, char[] selector, TypeBinding[] params) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        int i = 0;
        int length = params.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
            ++i;
        }
        int id = recType.isArrayType() ? 0x4000074 : 67108978;
        this.handle(id, new String[]{new String(recType.readableName()), new String(selector), buffer.toString()}, new String[]{new String(recType.shortReadableName()), new String(selector), shortBuffer.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void errorThisSuperInStatic(ASTNode reference) {
        String[] arguments = new String[]{reference.isSuper() ? "super" : "this"};
        this.handle(536871112, arguments, arguments, reference.sourceStart, reference.sourceEnd);
    }

    public void errorNoSuperInInterface(ASTNode reference) {
        this.handle(1610612962, NoArgument, NoArgument, reference.sourceStart, reference.sourceEnd);
    }

    public void expressionShouldBeAVariable(Expression expression) {
        this.handle(1610612959, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void fakeReachable(ASTNode location) {
        int sourceStart = location.sourceStart;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LocalDeclaration) {
            LocalDeclaration declaration = (LocalDeclaration)location;
            sourceStart = declaration.declarationSourceStart;
            sourceEnd = declaration.declarationSourceEnd;
        }
        this.handle(536871061, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void fieldHiding(FieldDeclaration fieldDecl, Binding hiddenVariable) {
        ReferenceBinding referenceBinding;
        FieldBinding field = fieldDecl.binding;
        if (CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name) && field.isStatic() && field.isPrivate() && field.isFinal() && TypeBinding.equalsEquals(TypeBinding.LONG, field.type) && (referenceBinding = field.declaringClass) != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
            return;
        }
        if (CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name) && field.isStatic() && field.isPrivate() && field.isFinal() && field.type.dimensions() == 1 && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName()) && (referenceBinding = field.declaringClass) != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
            return;
        }
        boolean isLocal = hiddenVariable instanceof LocalVariableBinding;
        int severity = this.computeSeverity(isLocal ? 570425436 : 570425437);
        if (severity == 256) {
            return;
        }
        if (isLocal) {
            this.handle(570425436, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(hiddenVariable, fieldDecl), this.nodeSourceEnd(hiddenVariable, fieldDecl));
        } else if (hiddenVariable instanceof FieldBinding) {
            FieldBinding hiddenField = (FieldBinding)hiddenVariable;
            this.handle(570425437, new String[]{new String(field.declaringClass.readableName()), new String(field.name), new String(hiddenField.declaringClass.readableName())}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name), new String(hiddenField.declaringClass.shortReadableName())}, severity, this.nodeSourceStart(hiddenField, fieldDecl), this.nodeSourceEnd(hiddenField, fieldDecl));
        }
    }

    public void fieldsOrThisBeforeConstructorInvocation(ASTNode reference) {
        this.handle(0x800008A, NoArgument, NoArgument, reference.sourceStart, reference instanceof LambdaExpression ? ((LambdaExpression)reference).diagnosticsSourceEnd() : reference.sourceEnd);
    }

    public void finallyMustCompleteNormally(Block finallyBlock) {
        this.handle(536871096, NoArgument, NoArgument, finallyBlock.sourceStart, finallyBlock.sourceEnd);
    }

    public void finalMethodCannotBeOverridden(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        this.handle(67109265, new String[]{new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(inheritedMethod.declaringClass.shortReadableName())}, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }

    public void finalVariableBound(TypeVariableBinding typeVariable, TypeReference typeRef) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(16777753);
        if (severity == 256) {
            return;
        }
        this.handle(16777753, new String[]{new String(typeVariable.sourceName()), new String(typeRef.resolvedType.readableName())}, new String[]{new String(typeVariable.sourceName()), new String(typeRef.resolvedType.shortReadableName())}, severity, typeRef.sourceStart, typeRef.sourceEnd);
    }

    public void forbiddenReference(FieldBinding field, ASTNode location, byte classpathEntryType, String classpathEntryName, int problemId) {
        int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        this.handle(problemId, new String[]{new String(field.readableName())}, ProblemReporter.getElaborationId(0x1000133, (byte)(4 | classpathEntryType)), new String[]{classpathEntryName, new String(field.shortReadableName()), new String(field.declaringClass.shortReadableName())}, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void forbiddenReference(MethodBinding method, InvocationSite location, byte classpathEntryType, String classpathEntryName, int problemId) {
        int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        if (method.isConstructor()) {
            this.handle(problemId, new String[]{new String(method.readableName())}, ProblemReporter.getElaborationId(0x1000133, (byte)(8 | classpathEntryType)), new String[]{classpathEntryName, new String(method.shortReadableName())}, severity, location.nameSourceStart(), location.nameSourceEnd());
        } else {
            this.handle(problemId, new String[]{new String(method.readableName())}, ProblemReporter.getElaborationId(0x1000133, (byte)(0xC | classpathEntryType)), new String[]{classpathEntryName, new String(method.shortReadableName()), new String(method.declaringClass.shortReadableName())}, severity, location.nameSourceStart(), location.nameSourceEnd());
        }
    }

    public void forbiddenReference(TypeBinding type, ASTNode location, byte classpathEntryType, String classpathEntryName, int problemId) {
        if (location == null) {
            return;
        }
        int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        this.handle(problemId, new String[]{new String(type.readableName())}, ProblemReporter.getElaborationId(0x1000133, classpathEntryType), new String[]{classpathEntryName, new String(type.shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
    }

    public void forwardReference(Reference reference, int indexInQualification, FieldBinding field) {
        this.handle(570425419, NoArgument, NoArgument, this.nodeSourceStart(field, reference, indexInQualification), this.nodeSourceEnd(field, reference, indexInQualification));
    }

    public void forwardTypeVariableReference(ASTNode location, TypeVariableBinding type) {
        this.handle(0x1000210, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void genericTypeCannotExtendThrowable(TypeDeclaration typeDecl) {
        TypeReference location = typeDecl.binding.isAnonymousType() ? typeDecl.allocation.type : typeDecl.superclass;
        this.handle(16777773, new String[]{new String(typeDecl.binding.readableName())}, new String[]{new String(typeDecl.binding.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    private void handle(int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition) {
        this.handle(problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, this.referenceContext, this.referenceContext == null ? null : this.referenceContext.compilationResult());
        this.referenceContext = null;
    }

    private void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition) {
        this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition, this.referenceContext, this.referenceContext == null ? null : this.referenceContext.compilationResult());
        this.referenceContext = null;
    }

    private void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition, CompilationResult unitResult) {
        this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition, this.referenceContext, unitResult);
        this.referenceContext = null;
    }

    private void handle(int problemId, String[] problemArguments, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition) {
        this.handle(problemId, problemArguments, 0, messageArguments, severity, problemStartPosition, problemEndPosition);
    }

    protected void handleUntagged(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition) {
        boolean oldSuppressing = this.suppressTagging;
        this.suppressTagging = true;
        try {
            this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition);
        }
        finally {
            this.suppressTagging = oldSuppressing;
        }
    }

    public void hiddenCatchBlock(ReferenceBinding exceptionType, ASTNode location) {
        this.handle(16777381, new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void hierarchyCircularity(SourceTypeBinding sourceType, ReferenceBinding superType, TypeReference reference) {
        int start = 0;
        int end = 0;
        if (reference == null) {
            start = sourceType.sourceStart();
            end = sourceType.sourceEnd();
        } else {
            start = reference.sourceStart;
            end = reference.sourceEnd;
        }
        if (TypeBinding.equalsEquals(sourceType, superType)) {
            this.handle(16777532, new String[]{new String(sourceType.readableName())}, new String[]{new String(sourceType.shortReadableName())}, start, end);
        } else {
            this.handle(16777533, new String[]{new String(sourceType.readableName()), new String(superType.readableName())}, new String[]{new String(sourceType.shortReadableName()), new String(superType.shortReadableName())}, start, end);
        }
    }

    public void hierarchyCircularity(TypeVariableBinding type, ReferenceBinding superType, TypeReference reference) {
        int start = 0;
        int end = 0;
        start = reference.sourceStart;
        end = reference.sourceEnd;
        if (TypeBinding.equalsEquals(type, superType)) {
            this.handle(16777532, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, start, end);
        } else {
            this.handle(16777533, new String[]{new String(type.readableName()), new String(superType.readableName())}, new String[]{new String(type.shortReadableName()), new String(superType.shortReadableName())}, start, end);
        }
    }

    public void hierarchyHasProblems(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777543, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalAbstractModifierCombinationForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(type.sourceName()), new String(methodDecl.selector)};
        this.handle(67109226, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalAbstractModifierCombinationForMethod(AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(methodDecl.selector)};
        this.handle(67109921, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalAccessFromTypeVariable(TypeVariableBinding variable, ASTNode location) {
        if ((location.bits & 0x8000) != 0) {
            this.javadocInvalidReference(location.sourceStart, location.sourceEnd);
        } else {
            String[] arguments = new String[]{new String(variable.sourceName)};
            this.handle(16777791, arguments, arguments, location.sourceStart, location.sourceEnd);
        }
    }

    public void illegalClassLiteralForTypeVariable(TypeVariableBinding variable, ASTNode location) {
        String[] arguments = new String[]{new String(variable.sourceName)};
        this.handle(16777774, arguments, arguments, location.sourceStart, location.sourceEnd);
    }

    public void illegalExtendedDimensions(AnnotationMethodDeclaration annotationTypeMemberDeclaration) {
        this.handle(67109465, NoArgument, NoArgument, annotationTypeMemberDeclaration.sourceStart, annotationTypeMemberDeclaration.sourceEnd);
    }

    public void illegalExtendedDimensions(AbstractVariableDeclaration aVarDecl) {
        this.handle(1610613536, NoArgument, NoArgument, aVarDecl.sourceStart, aVarDecl.sourceEnd);
    }

    public void illegalGenericArray(TypeBinding leafComponentType, ASTNode location) {
        this.handle(16777751, new String[]{new String(leafComponentType.readableName())}, new String[]{new String(leafComponentType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void illegalInstanceOfGenericType(TypeBinding checkedType, ASTNode location) {
        int i;
        TypeBinding erasedType = checkedType.leafComponentType().erasure();
        StringBuffer recommendedFormBuffer = new StringBuffer(10);
        if (erasedType instanceof ReferenceBinding) {
            ReferenceBinding referenceBinding = (ReferenceBinding)erasedType;
            recommendedFormBuffer.append(referenceBinding.qualifiedSourceName());
        } else {
            recommendedFormBuffer.append(erasedType.sourceName());
        }
        int count = erasedType.typeVariables().length;
        if (count > 0) {
            recommendedFormBuffer.append('<');
            i = 0;
            while (i < count) {
                if (i > 0) {
                    recommendedFormBuffer.append(',');
                }
                recommendedFormBuffer.append('?');
                ++i;
            }
            recommendedFormBuffer.append('>');
        }
        i = 0;
        int dim = checkedType.dimensions();
        while (i < dim) {
            recommendedFormBuffer.append("[]");
            ++i;
        }
        String recommendedForm = recommendedFormBuffer.toString();
        if (checkedType.leafComponentType().isTypeVariable()) {
            this.handle(0x20000223, new String[]{new String(checkedType.readableName()), recommendedForm}, new String[]{new String(checkedType.shortReadableName()), recommendedForm}, location.sourceStart, location.sourceEnd);
            return;
        }
        this.handle(0x20000222, new String[]{new String(checkedType.readableName()), recommendedForm}, new String[]{new String(checkedType.shortReadableName()), recommendedForm}, location.sourceStart, location.sourceEnd);
    }

    public void illegalLocalTypeDeclaration(TypeDeclaration typeDeclaration) {
        if (this.isRecoveredName(typeDeclaration.name)) {
            return;
        }
        int problemID = 0;
        if ((typeDeclaration.modifiers & 0x4000) != 0) {
            problemID = 536870943;
        } else if ((typeDeclaration.modifiers & 0x2000) != 0) {
            problemID = 536870942;
        } else if ((typeDeclaration.modifiers & 0x200) != 0) {
            problemID = 536870938;
        } else if (typeDeclaration.isRecord()) {
            problemID = 16778972;
        }
        if (problemID != 0) {
            String[] arguments = new String[]{new String(typeDeclaration.name)};
            this.handle(problemID, arguments, arguments, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
        }
    }

    public void illegalModifierCombinationFinalAbstractForClass(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777524, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierCombinationFinalVolatileForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33554777, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalModifierCombinationForInterfaceMethod(AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(methodDecl.selector)};
        this.handle(0x4000420, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalModifierCombinationForPrivateInterfaceMethod(AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(methodDecl.selector)};
        this.handle(67109934, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalModifierForAnnotationField(FieldDeclaration fieldDecl) {
        String name = new String(fieldDecl.name);
        this.handle(536871527, new String[]{new String(fieldDecl.binding.declaringClass.readableName()), name}, new String[]{new String(fieldDecl.binding.declaringClass.shortReadableName()), name}, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalModifierForAnnotationMember(AbstractMethodDeclaration methodDecl) {
        this.handle(67109464, new String[]{new String(methodDecl.binding.declaringClass.readableName()), new String(methodDecl.selector)}, new String[]{new String(methodDecl.binding.declaringClass.shortReadableName()), new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalModifierForAnnotationMemberType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777820, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForAnnotationType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777819, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForClass(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777518, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForModule(ModuleDeclaration module) {
        String[] arguments = new String[]{new String(module.moduleName)};
        this.handle(8389926, arguments, arguments, module.sourceStart(), module.sourceEnd());
    }

    public void illegalModifierForEnum(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777966, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForEnumConstant(ReferenceBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33555183, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalModifierForEnumConstructor(AbstractMethodDeclaration constructor) {
        this.handle(67109624, NoArgument, NoArgument, constructor.sourceStart, constructor.sourceEnd);
    }

    public void illegalModifierForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33554774, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalModifierForInterface(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777519, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForInterfaceField(FieldDeclaration fieldDecl) {
        String name = new String(fieldDecl.name);
        this.handle(33554775, new String[]{new String(fieldDecl.binding.declaringClass.readableName()), name}, new String[]{new String(fieldDecl.binding.declaringClass.shortReadableName()), name}, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalModifierForInterfaceMethod(AbstractMethodDeclaration methodDecl, long level) {
        int problem = level < 0x340000L ? 67109223 : (level < 0x350000L ? 67109914 : 67109935);
        this.handle(problem, new String[]{new String(methodDecl.selector)}, new String[]{new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalModifierForLocalClass(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777522, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForMemberClass(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(0x1000130, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForMemberEnum(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777969, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForMemberInterface(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(0x1000131, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForMethod(AbstractMethodDeclaration methodDecl) {
        this.handle(methodDecl.isConstructor() ? 67109233 : 67109222, new String[]{new String(methodDecl.selector)}, new String[]{new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalModifierForVariable(LocalDeclaration localDecl, boolean complainAsArgument) {
        String[] arguments = new String[]{new String(localDecl.name)};
        int problemId = (localDecl.modifiers & 0x10000000) != 0 ? 536872695 : (complainAsArgument ? 67109220 : 67109260);
        this.handle(problemId, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }

    public void illegalPrimitiveOrArrayTypeForEnclosingInstance(TypeBinding enclosingType, ASTNode location) {
        this.handle(0x100001B, new String[]{new String(enclosingType.readableName())}, new String[]{new String(enclosingType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void illegalQualifiedParameterizedTypeAllocation(TypeReference qualifiedTypeReference, TypeBinding allocatedType) {
        this.handle(16777782, new String[]{new String(allocatedType.readableName()), new String(allocatedType.enclosingType().readableName())}, new String[]{new String(allocatedType.shortReadableName()), new String(allocatedType.enclosingType().shortReadableName())}, qualifiedTypeReference.sourceStart, qualifiedTypeReference.sourceEnd);
    }

    public void illegalStaticModifierForMemberType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777527, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalUsageOfQualifiedTypeReference(QualifiedTypeReference qualifiedTypeReference) {
        StringBuffer buffer = new StringBuffer();
        char[][] tokens = qualifiedTypeReference.tokens;
        int i = 0;
        while (i < tokens.length) {
            if (i > 0) {
                buffer.append('.');
            }
            buffer.append(tokens[i]);
            ++i;
        }
        String[] arguments = new String[]{String.valueOf(buffer)};
        this.handle(0x600000C6, arguments, arguments, qualifiedTypeReference.sourceStart, qualifiedTypeReference.sourceEnd);
    }

    public void illegalUsageOfWildcard(TypeReference wildcard) {
        this.handle(1610613314, NoArgument, NoArgument, wildcard.sourceStart, wildcard.sourceEnd);
    }

    public void illegalVararg(Argument argType, AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{CharOperation.toString(argType.type.getTypeName()), new String(methodDecl.selector)};
        this.handle(67109279, arguments, arguments, argType.sourceStart, argType.sourceEnd);
    }

    public void illegalVarargInLambda(Argument argType) {
        String[] arguments = new String[]{CharOperation.toString(argType.type.getTypeName())};
        this.handle(553648782, arguments, arguments, argType.sourceStart, argType.sourceEnd);
    }

    public void illegalThisDeclaration(Argument argument) {
        String[] arguments = NoArgument;
        this.handle(1610613378, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }

    public void illegalSourceLevelForThis(Argument argument) {
        String[] arguments = NoArgument;
        this.handle(1610613379, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }

    public void disallowedThisParameter(Receiver receiver) {
        String[] arguments = NoArgument;
        this.handle(1610613374, arguments, arguments, receiver.sourceStart, receiver.sourceEnd);
    }

    public void illegalQualifierForExplicitThis(Receiver receiver, TypeBinding expectedType) {
        String[] problemArguments = new String[]{new String(expectedType.sourceName())};
        this.handle(1610613387, problemArguments, problemArguments, receiver.qualifyingName == null ? receiver.sourceStart : receiver.qualifyingName.sourceStart, receiver.sourceEnd);
    }

    public void illegalQualifierForExplicitThis2(Receiver receiver) {
        this.handle(1610613388, NoArgument, NoArgument, receiver.qualifyingName.sourceStart, receiver.sourceEnd);
    }

    public void illegalTypeForExplicitThis(Receiver receiver, TypeBinding expectedType) {
        this.handle(1610613386, new String[]{new String(expectedType.readableName())}, new String[]{new String(expectedType.shortReadableName())}, receiver.type.sourceStart, receiver.type.sourceEnd);
    }

    public void illegalThis(Argument argument) {
        String[] arguments = NoArgument;
        this.handle(1610613384, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }

    public void defaultMethodsNotBelow18(MethodDeclaration md) {
        this.handle(1610613380, NoArgument, NoArgument, md.sourceStart, md.sourceEnd);
    }

    public void interfaceSuperInvocationNotBelow18(QualifiedSuperReference qualifiedSuperReference) {
        this.handle(1610613403, NoArgument, NoArgument, qualifiedSuperReference.sourceStart, qualifiedSuperReference.sourceEnd);
    }

    public void staticInterfaceMethodsNotBelow18(MethodDeclaration md) {
        this.handle(1610613632, NoArgument, NoArgument, md.sourceStart, md.sourceEnd);
    }

    public void referenceExpressionsNotBelow18(ReferenceExpression rexp) {
        this.handle(rexp.isMethodReference() ? 1610613382 : 1610613383, NoArgument, NoArgument, rexp.sourceStart, rexp.sourceEnd);
    }

    public void lambdaExpressionsNotBelow18(LambdaExpression lexp) {
        this.handle(1610613381, NoArgument, NoArgument, lexp.sourceStart, lexp.diagnosticsSourceEnd());
    }

    public void illegalVisibilityModifierCombinationForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33554776, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void illegalVisibilityModifierCombinationForMemberType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777526, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalVisibilityModifierCombinationForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(type.sourceName()), new String(methodDecl.selector)};
        this.handle(67109224, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void illegalVisibilityModifierForInterfaceMemberType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16777525, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalVoidExpression(ASTNode location) {
        this.handle(536871076, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void importProblem(ImportReference importRef, Binding expectedImport) {
        if (expectedImport instanceof FieldBinding) {
            int id = 33554502;
            FieldBinding field = (FieldBinding)expectedImport;
            String[] readableArguments = null;
            String[] shortArguments = null;
            switch (expectedImport.problemId()) {
                case 2: 
                case 30: {
                    id = expectedImport.problemId() == 2 ? 33554503 : 33555883;
                    readableArguments = new String[]{CharOperation.toString(importRef.tokens), new String(field.declaringClass.readableName())};
                    shortArguments = new String[]{CharOperation.toString(importRef.tokens), new String(field.declaringClass.shortReadableName())};
                    break;
                }
                case 3: {
                    id = 33554504;
                    readableArguments = new String[]{new String(field.readableName())};
                    shortArguments = new String[]{new String(field.readableName())};
                    break;
                }
                case 8: {
                    id = 0x1000003;
                    readableArguments = new String[]{new String(field.declaringClass.leafComponentType().readableName())};
                    shortArguments = new String[]{new String(field.declaringClass.leafComponentType().shortReadableName())};
                }
            }
            this.handleUntagged(id, readableArguments, shortArguments, this.nodeSourceStart(field, importRef), this.nodeSourceEnd(field, importRef));
            return;
        }
        if (expectedImport instanceof PackageBinding && expectedImport.problemId() == 30) {
            char[][] compoundName = ((PackageBinding)expectedImport).compoundName;
            String[] arguments = new String[]{CharOperation.toString(compoundName)};
            this.handleUntagged(268436910, arguments, arguments, importRef.sourceStart, (int)importRef.sourcePositions[compoundName.length - 1]);
            return;
        }
        if (expectedImport.problemId() == 1) {
            char[][] tokens = expectedImport instanceof ProblemReferenceBinding ? ((ProblemReferenceBinding)expectedImport).compoundName : importRef.tokens;
            String[] arguments = new String[]{CharOperation.toString(tokens)};
            this.handleUntagged(268435846, arguments, arguments, importRef.sourceStart, (int)importRef.sourcePositions[tokens.length - 1]);
            return;
        }
        if (expectedImport.problemId() == 14) {
            char[][] tokens = importRef.tokens;
            String[] arguments = new String[]{CharOperation.toString(tokens)};
            this.handleUntagged(268435847, arguments, arguments, importRef.sourceStart, (int)importRef.sourcePositions[tokens.length - 1]);
            return;
        }
        this.invalidType(importRef, (TypeBinding)expectedImport);
    }

    public void conflictingPackagesFromModules(SplitPackageBinding splitPackage, ModuleBinding focusModule, int sourceStart, int sourceEnd) {
        String modules = splitPackage.incarnations.stream().filter(focusModule::canAccess).map(p -> String.valueOf(p.enclosingModule.readableName())).sorted().collect(Collectors.joining(", "));
        String[] arguments = new String[]{CharOperation.toString(splitPackage.compoundName), modules};
        this.handle(8390063, arguments, arguments, sourceStart, sourceEnd);
    }

    public void conflictingPackagesFromModules(PackageBinding pack, Set<ModuleBinding> modules, int sourceStart, int sourceEnd) {
        String moduleNames = modules.stream().map(p -> String.valueOf(p.name())).sorted().collect(Collectors.joining(", "));
        String[] arguments = new String[]{CharOperation.toString(pack.compoundName), moduleNames};
        this.handle(8390063, arguments, arguments, sourceStart, sourceEnd);
    }

    public void conflictingPackagesFromOtherModules(ImportReference currentPackage, Set<ModuleBinding> declaringModules) {
        String moduleNames = declaringModules.stream().map(p -> String.valueOf(p.name())).sorted().collect(Collectors.joining(", "));
        String[] arguments = new String[]{CharOperation.toString(currentPackage.tokens), moduleNames};
        this.handle(8390064, arguments, arguments, currentPackage.sourceStart, currentPackage.sourceEnd);
    }

    public void incompatibleExceptionInThrowsClause(SourceTypeBinding type, MethodBinding currentMethod, MethodBinding inheritedMethod, ReferenceBinding exceptionType) {
        if (TypeBinding.equalsEquals(type, currentMethod.declaringClass)) {
            int id = currentMethod.declaringClass.isInterface() && !inheritedMethod.isPublic() ? 67109278 : 67109266;
            this.handle(id, new String[]{new String(exceptionType.sourceName()), new String(CharOperation.concat(inheritedMethod.declaringClass.readableName(), inheritedMethod.readableName(), '.'))}, new String[]{new String(exceptionType.sourceName()), new String(CharOperation.concat(inheritedMethod.declaringClass.shortReadableName(), inheritedMethod.shortReadableName(), '.'))}, currentMethod.sourceStart(), currentMethod.sourceEnd());
        } else {
            this.handle(67109267, new String[]{new String(exceptionType.sourceName()), new String(CharOperation.concat(currentMethod.declaringClass.sourceName(), currentMethod.readableName(), '.')), new String(CharOperation.concat(inheritedMethod.declaringClass.readableName(), inheritedMethod.readableName(), '.'))}, new String[]{new String(exceptionType.sourceName()), new String(CharOperation.concat(currentMethod.declaringClass.sourceName(), currentMethod.shortReadableName(), '.')), new String(CharOperation.concat(inheritedMethod.declaringClass.shortReadableName(), inheritedMethod.shortReadableName(), '.'))}, type.sourceStart(), type.sourceEnd());
        }
    }

    public void incompatibleReturnType(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(inheritedMethod.declaringClass.readableName()).append('.').append(inheritedMethod.readableName());
        StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(inheritedMethod.declaringClass.shortReadableName()).append('.').append(inheritedMethod.shortReadableName());
        ReferenceBinding declaringClass = currentMethod.declaringClass;
        int id = declaringClass.isInterface() && !inheritedMethod.isPublic() ? 67109277 : 67109268;
        AbstractMethodDeclaration method = currentMethod.sourceMethod();
        int sourceStart = 0;
        int sourceEnd = 0;
        if (method == null) {
            if (declaringClass instanceof SourceTypeBinding) {
                SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringClass;
                sourceStart = sourceTypeBinding.sourceStart();
                sourceEnd = sourceTypeBinding.sourceEnd();
            }
        } else if (method.isConstructor()) {
            sourceStart = method.sourceStart;
            sourceEnd = method.sourceEnd;
        } else {
            TypeReference returnType = ((MethodDeclaration)method).returnType;
            sourceStart = returnType.sourceStart;
            if (returnType instanceof ParameterizedSingleTypeReference) {
                ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference)returnType;
                TypeReference[] typeArguments = typeReference.typeArguments;
                sourceEnd = typeArguments[typeArguments.length - 1].sourceEnd > typeReference.sourceEnd ? this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd) : returnType.sourceEnd;
            } else if (returnType instanceof ParameterizedQualifiedTypeReference) {
                ParameterizedQualifiedTypeReference typeReference = (ParameterizedQualifiedTypeReference)returnType;
                sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
            } else {
                sourceEnd = returnType.sourceEnd;
            }
        }
        this.handle(id, new String[]{methodSignature.toString()}, new String[]{shortSignature.toString()}, sourceStart, sourceEnd);
    }

    public void incorrectArityForParameterizedType(ASTNode location, TypeBinding type, TypeBinding[] argumentTypes) {
        this.incorrectArityForParameterizedType(location, type, argumentTypes, Integer.MAX_VALUE);
    }

    public void incorrectArityForParameterizedType(ASTNode location, TypeBinding type, TypeBinding[] argumentTypes, int index) {
        if (location == null) {
            this.handle(16777741, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true)}, 131, 0, 0);
            return;
        }
        this.handle(16777741, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true)}, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }

    public void diamondNotBelow17(ASTNode location) {
        this.diamondNotBelow17(location, Integer.MAX_VALUE);
    }

    public void diamondNotBelow17(ASTNode location, int index) {
        if (location == null) {
            this.handle(16778099, NoArgument, NoArgument, 131, 0, 0);
            return;
        }
        this.handle(16778099, NoArgument, NoArgument, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }

    public void incorrectLocationForNonEmptyDimension(ArrayAllocationExpression expression, int index) {
        this.handle(536871114, NoArgument, NoArgument, expression.dimensions[index].sourceStart, expression.dimensions[index].sourceEnd);
    }

    public void incorrectSwitchType(Expression expression, TypeBinding testType) {
        if (this.options.sourceLevel < 0x330000L) {
            if (testType.id == 11) {
                this.handle(16778097, new String[]{new String(testType.readableName())}, new String[]{new String(testType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
            } else if (this.options.sourceLevel < 0x310000L && testType.isEnum()) {
                this.handle(16778106, new String[]{new String(testType.readableName())}, new String[]{new String(testType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
            } else {
                this.handle(16777385, new String[]{new String(testType.readableName())}, new String[]{new String(testType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
            }
        } else {
            this.handle(16778093, new String[]{new String(testType.readableName())}, new String[]{new String(testType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
        }
    }

    public void indirectAccessToStaticField(ASTNode location, FieldBinding field) {
        int severity = this.computeSeverity(570425422);
        if (severity == 256) {
            return;
        }
        this.handle(570425422, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void indirectAccessToStaticMethod(ASTNode location, MethodBinding method) {
        int severity = this.computeSeverity(603979895);
        if (severity == 256) {
            return;
        }
        this.handle(603979895, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, location.sourceStart, location.sourceEnd);
    }

    public void inheritedDefaultMethodConflictsWithOtherInherited(SourceTypeBinding type, MethodBinding defaultMethod, MethodBinding otherMethod) {
        TypeDeclaration typeDecl = type.scope.referenceContext;
        String[] problemArguments = new String[]{String.valueOf(defaultMethod.readableName()), String.valueOf(defaultMethod.declaringClass.readableName()), String.valueOf(otherMethod.declaringClass.readableName())};
        String[] messageArguments = new String[]{String.valueOf(defaultMethod.shortReadableName()), String.valueOf(defaultMethod.declaringClass.shortReadableName()), String.valueOf(otherMethod.declaringClass.shortReadableName())};
        this.handle(67109916, problemArguments, messageArguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    private void inheritedMethodReducesVisibility(int sourceStart, int sourceEnd, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        StringBuffer concreteSignature = new StringBuffer();
        concreteSignature.append(concreteMethod.declaringClass.readableName()).append('.').append(concreteMethod.readableName());
        StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(concreteMethod.declaringClass.shortReadableName()).append('.').append(concreteMethod.shortReadableName());
        this.handle(67109269, new String[]{concreteSignature.toString(), new String(abstractMethods[0].declaringClass.readableName())}, new String[]{shortSignature.toString(), new String(abstractMethods[0].declaringClass.shortReadableName())}, sourceStart, sourceEnd);
    }

    public void inheritedMethodReducesVisibility(SourceTypeBinding type, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        this.inheritedMethodReducesVisibility(type.sourceStart(), type.sourceEnd(), concreteMethod, abstractMethods);
    }

    public void inheritedMethodReducesVisibility(TypeParameter typeParameter, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        this.inheritedMethodReducesVisibility(typeParameter.sourceStart(), typeParameter.sourceEnd(), concreteMethod, abstractMethods);
    }

    public void inheritedMethodsHaveIncompatibleReturnTypes(ASTNode location, MethodBinding[] inheritedMethods, int length) {
        StringBuffer methodSignatures = new StringBuffer();
        StringBuffer shortSignatures = new StringBuffer();
        int i = length;
        while (--i >= 0) {
            methodSignatures.append(inheritedMethods[i].declaringClass.readableName()).append('.').append(inheritedMethods[i].readableName());
            shortSignatures.append(inheritedMethods[i].declaringClass.shortReadableName()).append('.').append(inheritedMethods[i].shortReadableName());
            if (i == 0) continue;
            methodSignatures.append(", ");
            shortSignatures.append(", ");
        }
        this.handle(67109283, new String[]{methodSignatures.toString()}, new String[]{shortSignatures.toString()}, location.sourceStart, location.sourceEnd);
    }

    public void inheritedMethodsHaveIncompatibleReturnTypes(SourceTypeBinding type, MethodBinding[] inheritedMethods, int length, boolean[] isOverridden) {
        StringBuffer methodSignatures = new StringBuffer();
        StringBuffer shortSignatures = new StringBuffer();
        int i = length;
        while (--i >= 0) {
            if (isOverridden[i]) continue;
            methodSignatures.append(inheritedMethods[i].declaringClass.readableName()).append('.').append(inheritedMethods[i].readableName());
            shortSignatures.append(inheritedMethods[i].declaringClass.shortReadableName()).append('.').append(inheritedMethods[i].shortReadableName());
            if (i == 0) continue;
            methodSignatures.append(", ");
            shortSignatures.append(", ");
        }
        this.handle(67109283, new String[]{methodSignatures.toString()}, new String[]{shortSignatures.toString()}, type.sourceStart(), type.sourceEnd());
    }

    public void inheritedMethodsHaveNameClash(SourceTypeBinding type, MethodBinding oneMethod, MethodBinding twoMethod) {
        this.handle(67109424, new String[]{new String(oneMethod.selector), this.typesAsString(oneMethod.original(), false), new String(oneMethod.declaringClass.readableName()), this.typesAsString(twoMethod.original(), false), new String(twoMethod.declaringClass.readableName())}, new String[]{new String(oneMethod.selector), this.typesAsString(oneMethod.original(), true), new String(oneMethod.declaringClass.shortReadableName()), this.typesAsString(twoMethod.original(), true), new String(twoMethod.declaringClass.shortReadableName())}, type.sourceStart(), type.sourceEnd());
    }

    public void initializerMustCompleteNormally(FieldDeclaration fieldDecl) {
        this.handle(536871075, NoArgument, NoArgument, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void innerTypesCannotDeclareStaticInitializers(ReferenceBinding innerType, Initializer initializer) {
        this.handle(536870936, new String[]{new String(innerType.readableName())}, new String[]{new String(innerType.shortReadableName())}, initializer.sourceStart, initializer.sourceStart);
    }

    public void interfaceCannotHaveConstructors(ConstructorDeclaration constructor) {
        this.handle(1610612943, NoArgument, NoArgument, constructor.sourceStart, constructor.sourceEnd, constructor, constructor.compilationResult());
    }

    public void interfaceCannotHaveInitializers(char[] sourceName, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(sourceName)};
        this.handle(16777516, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void invalidAnnotationMemberType(MethodDeclaration methodDecl) {
        this.handle(16777821, new String[]{new String(methodDecl.binding.returnType.readableName()), new String(methodDecl.selector), new String(methodDecl.binding.declaringClass.readableName())}, new String[]{new String(methodDecl.binding.returnType.shortReadableName()), new String(methodDecl.selector), new String(methodDecl.binding.declaringClass.shortReadableName())}, methodDecl.returnType.sourceStart, methodDecl.returnType.sourceEnd);
    }

    public void invalidBreak(ASTNode location) {
        this.handle(536871084, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void invalidConstructor(Statement statement, MethodBinding targetConstructor) {
        boolean insideDefaultConstructor = this.referenceContext instanceof ConstructorDeclaration && ((ConstructorDeclaration)this.referenceContext).isDefaultConstructor();
        boolean insideImplicitConstructorCall = statement instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)statement).accessMode == 1;
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof AllocationExpression) {
            AllocationExpression allocation = (AllocationExpression)statement;
            if (allocation.enumConstant != null) {
                sourceStart = allocation.enumConstant.sourceStart;
                sourceEnd = allocation.enumConstant.sourceEnd;
            }
        }
        int id = 0x8000082;
        MethodBinding shownConstructor = targetConstructor;
        switch (targetConstructor.problemId()) {
            case 1: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                if (problemConstructor.closestMatch != null && (problemConstructor.closestMatch.tagBits & 0x80L) != 0L) {
                    this.missingTypeInConstructor(statement, problemConstructor.closestMatch);
                    return;
                }
                if (insideDefaultConstructor) {
                    id = 0x800008C;
                    break;
                }
                if (insideImplicitConstructorCall) {
                    id = 0x800008F;
                    break;
                }
                id = 0x8000082;
                break;
            }
            case 2: {
                id = insideDefaultConstructor ? 0x800008D : (insideImplicitConstructorCall ? 0x8000090 : 0x8000083);
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                if (problemConstructor.closestMatch == null) break;
                shownConstructor = problemConstructor.closestMatch.original();
                break;
            }
            case 30: {
                id = 67110317;
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                if (problemConstructor.closestMatch == null) break;
                shownConstructor = problemConstructor.closestMatch.original();
                break;
            }
            case 3: {
                if (insideDefaultConstructor) {
                    id = 0x800008E;
                    break;
                }
                if (insideImplicitConstructorCall) {
                    id = 134217873;
                    break;
                }
                id = 0x8000084;
                break;
            }
            case 10: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
                shownConstructor = substitutedConstructor.original();
                int augmentedLength = problemConstructor.parameters.length;
                TypeBinding inferredTypeArgument = problemConstructor.parameters[augmentedLength - 2];
                TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[augmentedLength - 1];
                TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(0x1000220, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, sourceStart, sourceEnd);
                return;
            }
            case 11: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                if (shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES) {
                    this.handle(16777767, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true)}, sourceStart, sourceEnd);
                } else {
                    this.handle(16777768, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor.typeVariables, false), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor.typeVariables, true), this.typesAsString(targetConstructor, true)}, sourceStart, sourceEnd);
                }
                return;
            }
            case 12: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(16777769, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), this.typesAsString(targetConstructor, true)}, sourceStart, sourceEnd);
                return;
            }
            case 13: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(16777771, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true)}, sourceStart, sourceEnd);
                return;
            }
            case 16: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                TypeBinding varargsElementType = shownConstructor.parameters[shownConstructor.parameters.length - 1].leafComponentType();
                this.handle(134218536, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), new String(varargsElementType.readableName())}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), new String(varargsElementType.shortReadableName())}, sourceStart, sourceEnd);
                return;
            }
            case 23: 
            case 27: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(0x1000011, new String[]{String.valueOf(shownConstructor.returnType.readableName()), problemConstructor.returnType != null ? String.valueOf(problemConstructor.returnType.readableName()) : "<unknown>"}, new String[]{String.valueOf(shownConstructor.returnType.shortReadableName()), problemConstructor.returnType != null ? String.valueOf(problemConstructor.returnType.shortReadableName()) : "<unknown>"}, statement.sourceStart, statement.sourceEnd);
                return;
            }
            case 25: {
                ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                this.contradictoryNullAnnotationsInferred(problemConstructor.closestMatch, statement);
                return;
            }
            default: {
                this.needImplementation(statement);
            }
        }
        this.handle(id, new String[]{new String(targetConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor, false)}, new String[]{new String(targetConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor, true)}, sourceStart, sourceEnd);
    }

    public void invalidContinue(ASTNode location) {
        this.handle(536871085, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void invalidEnclosingType(Expression expression, TypeBinding type, ReferenceBinding enclosingType) {
        if (enclosingType.isAnonymousType()) {
            enclosingType = enclosingType.superclass();
        }
        if (enclosingType.sourceName != null && enclosingType.sourceName.length == 0) {
            return;
        }
        int flag = 0x1000002;
        switch (type.problemId()) {
            case 1: {
                flag = 0x1000002;
                break;
            }
            case 2: {
                flag = 0x1000003;
                break;
            }
            case 3: {
                flag = 0x1000004;
                break;
            }
            case 4: {
                flag = 0x1000006;
                break;
            }
            default: {
                this.needImplementation(expression);
            }
        }
        this.handle(flag, new String[]{String.valueOf(new String(enclosingType.readableName())) + "." + new String(type.readableName())}, new String[]{String.valueOf(new String(enclosingType.shortReadableName())) + "." + new String(type.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidExplicitConstructorCall(ASTNode location) {
        this.handle(1207959691, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void invalidExpressionAsStatement(Expression expression) {
        this.handle(1610612958, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidField(FieldReference fieldRef, TypeBinding searchedType) {
        if (this.isRecoveredName(fieldRef.token)) {
            return;
        }
        int id = 33554502;
        FieldBinding field = fieldRef.binding;
        switch (field.problemId()) {
            case 1: {
                if ((searchedType.tagBits & 0x80L) != 0L) {
                    this.handle(0x1000002, new String[]{new String(searchedType.leafComponentType().readableName())}, new String[]{new String(searchedType.leafComponentType().shortReadableName())}, fieldRef.receiver.sourceStart, fieldRef.receiver.sourceEnd);
                    return;
                }
                id = 33554502;
                break;
            }
            case 2: 
            case 30: {
                this.handle(field.problemId() == 2 ? 33554503 : 33555883, new String[]{new String(fieldRef.token), new String(field.declaringClass.readableName())}, new String[]{new String(fieldRef.token), new String(field.declaringClass.shortReadableName())}, this.nodeSourceStart(field, fieldRef), this.nodeSourceEnd(field, fieldRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 28: {
                this.noSuchEnclosingInstance(fieldRef.actualReceiverType, fieldRef.receiver, false);
                return;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 0x8000087;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(0x1000003, new String[]{new String(searchedType.leafComponentType().readableName())}, new String[]{new String(searchedType.leafComponentType().shortReadableName())}, fieldRef.receiver.sourceStart, fieldRef.receiver.sourceEnd);
                return;
            }
            default: {
                this.needImplementation(fieldRef);
            }
        }
        String[] arguments = new String[]{new String(field.readableName())};
        this.handle(id, arguments, arguments, this.nodeSourceStart(field, fieldRef), this.nodeSourceEnd(field, fieldRef));
    }

    public void invalidField(NameReference nameRef, FieldBinding field) {
        NameReference ref;
        if (nameRef instanceof QualifiedNameReference) {
            ref = (QualifiedNameReference)nameRef;
            if (this.isRecoveredName(ref.tokens)) {
                return;
            }
        } else {
            ref = (SingleNameReference)nameRef;
            if (this.isRecoveredName(((SingleNameReference)ref).token)) {
                return;
            }
        }
        int id = 33554502;
        switch (field.problemId()) {
            case 1: {
                ReferenceBinding declaringClass = field.declaringClass;
                if (declaringClass != null && (declaringClass.tagBits & 0x80L) != 0L) {
                    this.handle(0x1000002, new String[]{new String(field.declaringClass.readableName())}, new String[]{new String(field.declaringClass.shortReadableName())}, nameRef.sourceStart, nameRef.sourceEnd);
                    return;
                }
                String[] arguments = new String[]{new String(field.readableName())};
                this.handle(id, arguments, arguments, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 2: 
            case 30: {
                char[] name = field.readableName();
                name = CharOperation.lastSegment(name, '.');
                this.handle(field.problemId() == 2 ? 33554503 : 33555883, new String[]{new String(name), new String(field.declaringClass.readableName())}, new String[]{new String(name), new String(field.declaringClass.shortReadableName())}, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 0x8000087;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(0x1000003, new String[]{new String(field.declaringClass.readableName())}, new String[]{new String(field.declaringClass.shortReadableName())}, nameRef.sourceStart, nameRef.sourceEnd);
                return;
            }
            default: {
                this.needImplementation(nameRef);
            }
        }
        String[] arguments = new String[]{new String(field.readableName())};
        this.handle(id, arguments, arguments, nameRef.sourceStart, nameRef.sourceEnd);
    }

    public void invalidField(QualifiedNameReference nameRef, FieldBinding field, int index, TypeBinding searchedType) {
        if (this.isRecoveredName(nameRef.tokens)) {
            return;
        }
        if (searchedType.isBaseType()) {
            this.handle(0x20000DD, new String[]{new String(searchedType.readableName()), CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), new String(nameRef.tokens[index])}, new String[]{new String(searchedType.sourceName()), CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), new String(nameRef.tokens[index])}, nameRef.sourceStart, (int)nameRef.sourcePositions[index]);
            return;
        }
        int id = 33554502;
        switch (field.problemId()) {
            case 1: {
                if ((searchedType.tagBits & 0x80L) != 0L) {
                    this.handle(0x1000002, new String[]{new String(searchedType.leafComponentType().readableName())}, new String[]{new String(searchedType.leafComponentType().shortReadableName())}, nameRef.sourceStart, (int)nameRef.sourcePositions[index - 1]);
                    return;
                }
                String fieldName = new String(nameRef.tokens[index]);
                String[] arguments = new String[]{fieldName};
                this.handle(id, arguments, arguments, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 2: 
            case 30: {
                String fieldName = new String(nameRef.tokens[index]);
                this.handle(field.problemId() == 2 ? 33554503 : 33555883, new String[]{fieldName, new String(field.declaringClass.readableName())}, new String[]{fieldName, new String(field.declaringClass.shortReadableName())}, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 0x8000087;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(0x1000003, new String[]{new String(searchedType.leafComponentType().readableName())}, new String[]{new String(searchedType.leafComponentType().shortReadableName())}, nameRef.sourceStart, (int)nameRef.sourcePositions[index - 1]);
                return;
            }
            default: {
                this.needImplementation(nameRef);
            }
        }
        String[] arguments = new String[]{CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index + 1))};
        this.handle(id, arguments, arguments, nameRef.sourceStart, (int)nameRef.sourcePositions[index]);
    }

    public void invalidFileNameForPackageAnnotations(Annotation annotation) {
        this.handle(1610613338, NoArgument, NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }

    public void nonStaticOrAlienTypeReceiver(MessageSend messageSend, MethodBinding method) {
        this.handle(0x4000054, new String[]{new String(method.declaringClass.readableName()), new String(method.selector)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
    }

    public void invalidMethod(MessageSend messageSend, MethodBinding method, Scope scope) {
        if (this.isRecoveredName(messageSend.selector)) {
            return;
        }
        int id = 0x4000064;
        MethodBinding shownMethod = method;
        switch (method.problemId()) {
            case 31: {
                return;
            }
            case 26: {
                return;
            }
            case 1: {
                if ((method.declaringClass.tagBits & 0x80L) != 0L) {
                    this.handle(0x1000002, new String[]{new String(method.declaringClass.readableName())}, new String[]{new String(method.declaringClass.shortReadableName())}, messageSend.receiver.sourceStart, messageSend.receiver.sourceEnd);
                    return;
                }
                id = 0x4000064;
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch == null) break;
                shownMethod = problemMethod.closestMatch;
                if ((shownMethod.tagBits & 0x80L) != 0L) {
                    this.missingTypeInMethod(messageSend, shownMethod);
                    return;
                }
                String closestParameterTypeNames = this.typesAsString(shownMethod, false);
                String parameterTypeNames = this.typesAsString(problemMethod.parameters, false);
                String closestParameterTypeShortNames = this.typesAsString(shownMethod, true);
                String parameterTypeShortNames = this.typesAsString(problemMethod.parameters, true);
                if (closestParameterTypeNames.equals(parameterTypeNames)) {
                    closestParameterTypeNames = this.typesAsString(shownMethod, false, true);
                    parameterTypeNames = this.typesAsString(problemMethod.parameters, false, true);
                    closestParameterTypeShortNames = this.typesAsString(shownMethod, true, true);
                    parameterTypeShortNames = this.typesAsString(problemMethod.parameters, true, true);
                }
                if (closestParameterTypeShortNames.equals(parameterTypeShortNames)) {
                    closestParameterTypeShortNames = closestParameterTypeNames;
                    parameterTypeShortNames = parameterTypeNames;
                }
                this.handle(67108979, new String[]{new String(shownMethod.declaringClass.readableName()), new String(shownMethod.selector), closestParameterTypeNames, parameterTypeNames}, new String[]{new String(shownMethod.declaringClass.shortReadableName()), new String(shownMethod.selector), closestParameterTypeShortNames, parameterTypeShortNames}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 2: 
            case 30: {
                id = method.problemId() == 2 ? 67108965 : 67110316;
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch == null) break;
                shownMethod = problemMethod.closestMatch.original();
                break;
            }
            case 3: {
                id = 0x4000066;
                break;
            }
            case 5: {
                id = 67109059;
                break;
            }
            case 6: {
                id = 0x8000088;
                break;
            }
            case 7: {
                id = 603979977;
                break;
            }
            case 20: {
                this.nonStaticOrAlienTypeReceiver(messageSend, method);
                return;
            }
            case 29: {
                this.handle(1610613404, new String[]{new String(method.declaringClass.readableName()), new String(method.selector)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 8: {
                this.handle(0x1000003, new String[]{new String(method.declaringClass.readableName())}, new String[]{new String(method.declaringClass.shortReadableName())}, messageSend.receiver.sourceStart, messageSend.receiver.sourceEnd);
                return;
            }
            case 10: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
                shownMethod = substitutedMethod.original();
                int augmentedLength = problemMethod.parameters.length;
                TypeBinding inferredTypeArgument = problemMethod.parameters[augmentedLength - 2];
                TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[augmentedLength - 1];
                TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(16777759, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 11: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                if (shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES) {
                    this.handle(16777764, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                } else {
                    this.handle(16777765, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(shownMethod.typeVariables, false), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(shownMethod.typeVariables, true), this.typesAsString(method, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                return;
            }
            case 12: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(16777766, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), this.typesAsString(method, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 13: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(16777770, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 23: 
            case 27: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                if (problemMethod.returnType == shownMethod.returnType) {
                    if (messageSend.expressionContext == ExpressionContext.VANILLA_CONTEXT) {
                        TypeBinding[] typeVariables = method.shallowOriginal().typeVariables;
                        String typeArguments = this.typesAsString(typeVariables, false);
                        this.handle(16778275, new String[]{typeArguments, String.valueOf(shownMethod.original().readableName())}, new String[]{typeArguments, String.valueOf(shownMethod.original().shortReadableName())}, messageSend.sourceStart, messageSend.sourceEnd);
                    } else {
                        this.handle(1100, new String[]{"Unknown error at invocation of " + String.valueOf(shownMethod.readableName())}, new String[]{"Unknown error at invocation of " + String.valueOf(shownMethod.shortReadableName())}, messageSend.sourceStart, messageSend.sourceEnd);
                    }
                    return;
                }
                TypeBinding shownMethodReturnType = shownMethod.returnType.capture(scope, messageSend.sourceStart, messageSend.sourceEnd);
                this.handle(0x1000011, new String[]{String.valueOf(shownMethodReturnType.readableName()), problemMethod.returnType != null ? String.valueOf(problemMethod.returnType.readableName()) : "<unknown>"}, new String[]{String.valueOf(shownMethodReturnType.shortReadableName()), problemMethod.returnType != null ? String.valueOf(problemMethod.returnType.shortReadableName()) : "<unknown>"}, messageSend.sourceStart, messageSend.sourceEnd);
                return;
            }
            case 16: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch != null) {
                    shownMethod = problemMethod.closestMatch.original();
                }
                TypeBinding varargsElementType = shownMethod.parameters[shownMethod.parameters.length - 1].leafComponentType();
                this.handle(67109671, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), new String(varargsElementType.readableName())}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), new String(varargsElementType.shortReadableName())}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 24: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch != null) {
                    shownMethod = problemMethod.closestMatch.original();
                }
                this.handle(67109673, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName())}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName())}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 25: {
                ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                this.contradictoryNullAnnotationsInferred(problemMethod.closestMatch, messageSend);
                return;
            }
            default: {
                this.needImplementation(messageSend);
            }
        }
        this.handle(id, new String[]{new String(method.declaringClass.readableName()), new String(shownMethod.selector), this.typesAsString(shownMethod, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(shownMethod.selector), this.typesAsString(shownMethod, true)}, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
    }

    public void invalidNullToSynchronize(Expression expression) {
        this.handle(0x200000B0, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidOperator(BinaryExpression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(0x200000A0, new String[]{expression.operatorToString(), String.valueOf(leftName) + ", " + rightName}, new String[]{expression.operatorToString(), String.valueOf(leftShortName) + ", " + rightShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidOperator(CompoundAssignment assign, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(0x200000A0, new String[]{assign.operatorToString(), String.valueOf(leftName) + ", " + rightName}, new String[]{assign.operatorToString(), String.valueOf(leftShortName) + ", " + rightShortName}, assign.sourceStart, assign.sourceEnd);
    }

    public void invalidOperator(UnaryExpression expression, TypeBinding type) {
        this.handle(0x200000A0, new String[]{expression.operatorToString(), new String(type.readableName())}, new String[]{expression.operatorToString(), new String(type.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidParameterizedExceptionType(TypeBinding exceptionType, ASTNode location) {
        this.handle(16777750, new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void invalidParenthesizedExpression(ASTNode reference) {
        this.handle(1610612961, NoArgument, NoArgument, reference.sourceStart, reference.sourceEnd);
    }

    public void invalidType(ASTNode location, TypeBinding type) {
        TypeReference ref;
        char[][] name;
        ASTNode ref2;
        List<TypeBinding> missingTypes;
        TypeBinding leafType;
        if (type instanceof ReferenceBinding ? this.isRecoveredName(((ReferenceBinding)type).compoundName) : type instanceof ArrayBinding && (leafType = ((ArrayBinding)type).leafComponentType) instanceof ReferenceBinding && this.isRecoveredName(((ReferenceBinding)leafType).compoundName)) {
            return;
        }
        if (type.isParameterizedType() && (missingTypes = type.collectMissingTypes(null)) != null) {
            ReferenceContext savedContext = this.referenceContext;
            Iterator<TypeBinding> iterator = missingTypes.iterator();
            while (iterator.hasNext()) {
                try {
                    this.invalidType(location, iterator.next());
                }
                finally {
                    this.referenceContext = savedContext;
                }
            }
            return;
        }
        int id = 0x1000002;
        switch (type.problemId()) {
            case 1: {
                id = 0x1000002;
                break;
            }
            case 2: {
                id = 0x1000003;
                break;
            }
            case 30: {
                id = 16778666;
                break;
            }
            case 3: {
                id = 0x1000004;
                break;
            }
            case 4: {
                id = 0x1000006;
                break;
            }
            case 5: {
                id = 16777413;
                break;
            }
            case 7: {
                id = 0x2000020A;
                break;
            }
            case 9: {
                id = 0x20000209;
                break;
            }
            default: {
                this.needImplementation(location);
            }
        }
        int end = location.sourceEnd;
        if (location instanceof QualifiedNameReference) {
            ref2 = (QualifiedNameReference)location;
            if (this.isRecoveredName(ref2.tokens)) {
                return;
            }
            if (ref2.indexOfFirstFieldBinding >= 1) {
                end = (int)ref2.sourcePositions[ref2.indexOfFirstFieldBinding - 1];
            }
        } else if (location instanceof ParameterizedQualifiedTypeReference) {
            ref2 = (ParameterizedQualifiedTypeReference)location;
            if (this.isRecoveredName(((ParameterizedQualifiedTypeReference)ref2).tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding) {
                name = ((ReferenceBinding)type).compoundName;
                end = (int)((ParameterizedQualifiedTypeReference)ref2).sourcePositions[name.length - 1];
            }
        } else if (location instanceof ArrayQualifiedTypeReference) {
            ArrayQualifiedTypeReference arrayQualifiedTypeReference = (ArrayQualifiedTypeReference)location;
            if (this.isRecoveredName(arrayQualifiedTypeReference.tokens)) {
                return;
            }
            TypeBinding leafType2 = type.leafComponentType();
            if (leafType2 instanceof ReferenceBinding) {
                char[][] name2 = ((ReferenceBinding)leafType2).compoundName;
                end = (int)arrayQualifiedTypeReference.sourcePositions[name2.length - 1];
            } else {
                long[] positions = arrayQualifiedTypeReference.sourcePositions;
                end = (int)positions[positions.length - 1];
            }
        } else if (location instanceof QualifiedTypeReference) {
            ref2 = (QualifiedTypeReference)location;
            if (this.isRecoveredName(((QualifiedTypeReference)ref2).tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding && (name = ((ReferenceBinding)type).compoundName).length <= ((QualifiedTypeReference)ref2).sourcePositions.length) {
                end = (int)((QualifiedTypeReference)ref2).sourcePositions[name.length - 1];
            }
        } else if (location instanceof ImportReference) {
            ref2 = (ImportReference)location;
            if (this.isRecoveredName(((ImportReference)ref2).tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding) {
                name = ((ReferenceBinding)type).compoundName;
                end = (int)((ImportReference)ref2).sourcePositions[name.length - 1];
            }
        } else if (location instanceof ArrayTypeReference) {
            ArrayTypeReference arrayTypeReference = (ArrayTypeReference)location;
            if (this.isRecoveredName(arrayTypeReference.token)) {
                return;
            }
            end = arrayTypeReference.originalSourceEnd;
        }
        int start = location.sourceStart;
        if (location instanceof SingleTypeReference) {
            ref = (SingleTypeReference)location;
            if (ref.annotations != null) {
                start = end - ref.token.length + 1;
            }
        } else if (location instanceof QualifiedTypeReference) {
            ref = (QualifiedTypeReference)location;
            if (((QualifiedTypeReference)ref).annotations != null) {
                start = (int)(((QualifiedTypeReference)ref).sourcePositions[0] & 0xFFFFFFFFL) - ((QualifiedTypeReference)ref).tokens[0].length + 1;
            }
        }
        this.handle(id, new String[]{new String(type.leafComponentType().readableName())}, new String[]{new String(type.leafComponentType().shortReadableName())}, start, end);
    }

    public void invalidTypeForCollection(Expression expression) {
        this.handle(536871493, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidTypeForCollectionTarget14(Expression expression) {
        this.handle(536871494, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidTypeToSynchronize(Expression expression, TypeBinding type) {
        this.handle(536871087, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidTypeVariableAsException(TypeBinding exceptionType, ASTNode location) {
        this.handle(16777749, new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void invalidUnaryExpression(Expression expression) {
        this.handle(1610612942, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidUsageOfAnnotation(Annotation annotation) {
        this.handle(1610613332, NoArgument, NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }

    public void invalidUsageOfAnnotationDeclarations(TypeDeclaration annotationTypeDeclaration) {
        this.handle(1610613333, NoArgument, NoArgument, annotationTypeDeclaration.sourceStart, annotationTypeDeclaration.sourceEnd);
    }

    public void invalidUsageOfEnumDeclarations(TypeDeclaration enumDeclaration) {
        this.handle(1610613330, NoArgument, NoArgument, enumDeclaration.sourceStart, enumDeclaration.sourceEnd);
    }

    public void invalidUsageOfForeachStatements(LocalDeclaration elementVariable, Expression collection) {
        this.handle(1610613328, NoArgument, NoArgument, elementVariable.declarationSourceStart, collection.sourceEnd);
    }

    public void invalidUsageOfStaticImports(ImportReference staticImport) {
        this.handle(1610613327, NoArgument, NoArgument, staticImport.declarationSourceStart, staticImport.declarationSourceEnd);
    }

    public void invalidUsageOfTypeArguments(TypeReference firstTypeReference, TypeReference lastTypeReference) {
        this.handle(1610613329, NoArgument, NoArgument, firstTypeReference.sourceStart, lastTypeReference.sourceEnd);
    }

    public void invalidUsageOfTypeParameters(TypeParameter firstTypeParameter, TypeParameter lastTypeParameter) {
        this.handle(1610613326, NoArgument, NoArgument, firstTypeParameter.declarationSourceStart, lastTypeParameter.declarationSourceEnd);
    }

    public void invalidUsageOfTypeParametersForAnnotationDeclaration(TypeDeclaration annotationTypeDeclaration) {
        TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
        int length = parameters.length;
        this.handle(1610613334, NoArgument, NoArgument, parameters[0].declarationSourceStart, parameters[length - 1].declarationSourceEnd);
    }

    public void invalidUsageOfTypeParametersForEnumDeclaration(TypeDeclaration annotationTypeDeclaration) {
        TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
        int length = parameters.length;
        this.handle(1610613335, NoArgument, NoArgument, parameters[0].declarationSourceStart, parameters[length - 1].declarationSourceEnd);
    }

    public void invalidUsageOfVarargs(AbstractVariableDeclaration aVarDecl) {
        this.handle(1610613331, NoArgument, NoArgument, aVarDecl.type.sourceStart, aVarDecl.sourceEnd);
    }

    public void invalidUsageOfTypeAnnotations(Annotation annotation) {
        this.handle(1610613373, NoArgument, NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }

    public void misplacedTypeAnnotations(Annotation first, Annotation last) {
        this.handle(1610613375, NoArgument, NoArgument, first.sourceStart, last.sourceEnd);
    }

    public void illegalUsageOfTypeAnnotations(Annotation annotation) {
        this.handle(1610613377, NoArgument, NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }

    public void illegalTypeAnnotationsInStaticMemberAccess(Annotation first, Annotation last) {
        this.handle(1610613376, NoArgument, NoArgument, first.sourceStart, last.sourceEnd);
    }

    public void discouragedValueBasedTypeToSynchronize(Expression expression, TypeBinding type) {
        if (type.isParameterizedType()) {
            type = ((ParameterizedTypeBinding)type).actualType();
        }
        this.handle(536872732, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void isClassPathCorrect(char[][] wellKnownTypeName, CompilationUnitDeclaration compUnitDecl, Object location, boolean implicitAnnotationUse) {
        ReferenceContext savedContext = this.referenceContext;
        this.referenceContext = compUnitDecl;
        String[] arguments = new String[]{CharOperation.toString(wellKnownTypeName)};
        int start = 0;
        int end = 0;
        if (location != null) {
            if (location instanceof InvocationSite) {
                InvocationSite site = (InvocationSite)location;
                start = site.sourceStart();
                end = site.sourceEnd();
            } else if (location instanceof ASTNode) {
                ASTNode node = (ASTNode)location;
                start = node.sourceStart();
                end = node.sourceEnd();
            }
        }
        try {
            this.handle(implicitAnnotationUse ? 536871894 : 0x1000144, arguments, arguments, start, end);
        }
        finally {
            this.referenceContext = savedContext;
        }
    }

    private boolean isIdentifier(int token) {
        return token == 22;
    }

    private boolean isRestrictedIdentifier(int token) {
        switch (token) {
            case 41: 
            case 75: 
            case 80: 
            case 127: {
                return true;
            }
        }
        return false;
    }

    private boolean isKeyword(int token) {
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

    private boolean isLiteral(int token) {
        return Scanner.isLiteral(token);
    }

    private boolean isRecoveredName(char[] simpleName) {
        return simpleName == RecoveryScanner.FAKE_IDENTIFIER;
    }

    private boolean isRecoveredName(char[][] qualifiedName) {
        if (qualifiedName == null) {
            return false;
        }
        int i = 0;
        while (i < qualifiedName.length) {
            if (qualifiedName[i] == RecoveryScanner.FAKE_IDENTIFIER) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public void javadocAmbiguousMethodReference(int sourceStart, int sourceEnd, Binding fieldBinding, int modifiers) {
        int severity = this.computeSeverity(-1610612225);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{new String(fieldBinding.readableName())};
            this.handle(-1610612225, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocDeprecatedField(FieldBinding field, ASTNode location, int modifiers) {
        int severity = this.computeSeverity(-1610612245);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612245, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
        }
    }

    public void javadocDeprecatedMethod(MethodBinding method, ASTNode location, int modifiers) {
        boolean isConstructor = method.isConstructor();
        int severity = this.computeSeverity(isConstructor ? -1610612241 : -1610612237);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            if (isConstructor) {
                this.handle(-1610612241, new String[]{new String(method.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity, location.sourceStart, location.sourceEnd);
            } else {
                this.handle(-1610612237, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, location.sourceStart, location.sourceEnd);
            }
        }
    }

    public void javadocDeprecatedType(TypeBinding type, ASTNode location, int modifiers) {
        this.javadocDeprecatedType(type, location, modifiers, Integer.MAX_VALUE);
    }

    public void javadocDeprecatedType(TypeBinding type, ASTNode location, int modifiers, int index) {
        if (location == null) {
            return;
        }
        int severity = this.computeSeverity(-1610612230);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            if (type.isMemberType() && type instanceof ReferenceBinding && !this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, ((ReferenceBinding)type).modifiers)) {
                this.handle(-1610612271, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
            } else {
                this.handle(-1610612230, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, severity, location.sourceStart, this.nodeSourceEnd(null, location, index));
            }
        }
    }

    public void javadocDuplicatedParamTag(char[] token, int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612263);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(token)};
            this.handle(-1610612263, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocDuplicatedProvidesTag(int sourceStart, int sourceEnd) {
        this.handle(-1610610930, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocDuplicatedReturnTag(int sourceStart, int sourceEnd) {
        this.handle(-1610612260, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocDuplicatedTag(char[] tagName, int sourceStart, int sourceEnd) {
        String[] arguments = new String[]{new String(tagName)};
        this.handle(-1610612272, arguments, arguments, sourceStart, sourceEnd);
    }

    public void javadocDuplicatedThrowsClassName(TypeReference typeReference, int modifiers) {
        int severity = this.computeSeverity(-1610612256);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(typeReference.resolvedType.sourceName())};
            this.handle(-1610612256, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }

    public void javadocDuplicatedUsesTag(int sourceStart, int sourceEnd) {
        this.handle(-1610610935, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocEmptyReturnTag(int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612220);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{new String(JavadocTagConstants.TAG_RETURN)};
            this.handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
        }
    }

    public void javadocErrorNoMethodFor(MessageSend messageSend, TypeBinding recType, TypeBinding[] params, int modifiers) {
        int id = recType.isArrayType() ? -1610612234 : -1610612236;
        int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        int i = 0;
        int length = params.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
            ++i;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(id, new String[]{new String(recType.readableName()), new String(messageSend.selector), buffer.toString()}, new String[]{new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString()}, severity, messageSend.sourceStart, messageSend.sourceEnd);
        }
    }

    public void javadocHiddenReference(int sourceStart, int sourceEnd, Scope scope, int modifiers) {
        Scope currentScope = scope;
        while (currentScope.parent.kind != 4) {
            if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, currentScope.getDeclarationModifiers())) {
                return;
            }
            currentScope = currentScope.parent;
        }
        String[] arguments = new String[]{this.options.getVisibilityString(this.options.reportInvalidJavadocTagsVisibility), this.options.getVisibilityString(modifiers)};
        this.handle(-1610612271, arguments, arguments, sourceStart, sourceEnd);
    }

    public void javadocInvalidConstructor(Statement statement, MethodBinding targetConstructor, int modifiers) {
        if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            return;
        }
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof AllocationExpression) {
            AllocationExpression allocation = (AllocationExpression)statement;
            if (allocation.enumConstant != null) {
                sourceStart = allocation.enumConstant.sourceStart;
                sourceEnd = allocation.enumConstant.sourceEnd;
            }
        }
        int id = -1610612244;
        ProblemMethodBinding problemConstructor = null;
        MethodBinding shownConstructor = null;
        switch (targetConstructor.problemId()) {
            case 1: {
                id = -1610612244;
                break;
            }
            case 2: {
                id = -1610612243;
                break;
            }
            case 3: {
                id = -1610612242;
                break;
            }
            case 10: {
                int severity = this.computeSeverity(-1610611881);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
                shownConstructor = substitutedConstructor.original();
                int augmentedLength = problemConstructor.parameters.length;
                TypeBinding inferredTypeArgument = problemConstructor.parameters[augmentedLength - 2];
                TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[augmentedLength - 1];
                TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(-1610611881, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, severity, sourceStart, sourceEnd);
                return;
            }
            case 11: {
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                boolean noTypeVariables = shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES;
                int severity = this.computeSeverity(noTypeVariables ? -1610611880 : -1610611879);
                if (severity == 256) {
                    return;
                }
                if (noTypeVariables) {
                    this.handle(-1610611880, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true)}, severity, sourceStart, sourceEnd);
                } else {
                    this.handle(-1610611879, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor.typeVariables, false), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor.typeVariables, true), this.typesAsString(targetConstructor, true)}, severity, sourceStart, sourceEnd);
                }
                return;
            }
            case 12: {
                int severity = this.computeSeverity(-1610611878);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(-1610611878, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), this.typesAsString(targetConstructor, true)}, severity, sourceStart, sourceEnd);
                return;
            }
            case 13: {
                int severity = this.computeSeverity(-1610611877);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(-1610611877, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false)}, new String[]{new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true)}, severity, sourceStart, sourceEnd);
                return;
            }
            default: {
                this.needImplementation(statement);
            }
        }
        int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        this.handle(id, new String[]{new String(targetConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false)}, new String[]{new String(targetConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true)}, severity, statement.sourceStart, statement.sourceEnd);
    }

    public void javadocInvalidField(FieldReference fieldRef, Binding fieldBinding, TypeBinding searchedType, int modifiers) {
        int id = -1610612248;
        switch (fieldBinding.problemId()) {
            case 1: {
                id = -1610612248;
                break;
            }
            case 2: {
                id = -1610612247;
                break;
            }
            case 3: {
                id = -1610612246;
                break;
            }
            default: {
                this.needImplementation(fieldRef);
            }
        }
        int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{new String(fieldBinding.readableName())};
            this.handle(id, arguments, arguments, severity, fieldRef.sourceStart, fieldRef.sourceEnd);
        }
    }

    public void javadocInvalidMemberTypeQualification(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612270, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocInvalidModuleQualification(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610610926, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocInvalidMethod(MessageSend messageSend, MethodBinding method, int modifiers) {
        int severity;
        if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            return;
        }
        ProblemMethodBinding problemMethod = null;
        MethodBinding shownMethod = null;
        int id = -1610612240;
        switch (method.problemId()) {
            case 1: {
                String parameterTypeShortNames;
                id = -1610612240;
                problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch == null) break;
                int severity2 = this.computeSeverity(-1610612235);
                if (severity2 == 256) {
                    return;
                }
                String closestParameterTypeNames = this.typesAsString(problemMethod.closestMatch, false);
                String parameterTypeNames = this.typesAsString(method, false);
                String closestParameterTypeShortNames = this.typesAsString(problemMethod.closestMatch, true);
                if (closestParameterTypeShortNames.equals(parameterTypeShortNames = this.typesAsString(method, true))) {
                    closestParameterTypeShortNames = closestParameterTypeNames;
                    parameterTypeShortNames = parameterTypeNames;
                }
                this.handle(-1610612235, new String[]{new String(problemMethod.closestMatch.declaringClass.readableName()), new String(problemMethod.closestMatch.selector), closestParameterTypeNames, parameterTypeNames}, new String[]{new String(problemMethod.closestMatch.declaringClass.shortReadableName()), new String(problemMethod.closestMatch.selector), closestParameterTypeShortNames, parameterTypeShortNames}, severity2, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 2: {
                id = -1610612239;
                break;
            }
            case 3: {
                id = -1610612238;
                break;
            }
            case 10: {
                int severity3 = this.computeSeverity(-1610611886);
                if (severity3 == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
                shownMethod = substitutedMethod.original();
                int augmentedLength = problemMethod.parameters.length;
                TypeBinding inferredTypeArgument = problemMethod.parameters[augmentedLength - 2];
                TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[augmentedLength - 1];
                TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(-1610611886, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, severity3, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 11: {
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                boolean noTypeVariables = shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES;
                int severity4 = this.computeSeverity(noTypeVariables ? -1610611885 : -1610611884);
                if (severity4 == 256) {
                    return;
                }
                if (noTypeVariables) {
                    this.handle(-1610611885, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity4, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                } else {
                    this.handle(-1610611884, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(shownMethod.typeVariables, false), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(shownMethod.typeVariables, true), this.typesAsString(method, true)}, severity4, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                return;
            }
            case 12: {
                int severity5 = this.computeSeverity(-1610611883);
                if (severity5 == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(-1610611883, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), this.typesAsString(method, true)}, severity5, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 13: {
                int severity6 = this.computeSeverity(-1610611882);
                if (severity6 == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(-1610611882, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity6, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            default: {
                this.needImplementation(messageSend);
            }
        }
        if ((severity = this.computeSeverity(id)) == 256) {
            return;
        }
        this.handle(id, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
    }

    public void javadocInvalidParamTagName(int sourceStart, int sourceEnd) {
        this.handle(-1610612217, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidParamTypeParameter(int sourceStart, int sourceEnd) {
        this.handle(-1610612267, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidProvidesClass(int sourceStart, int sourceEnd) {
        this.handle(-1610610927, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidProvidesClassName(TypeReference typeReference, int modifiers) {
        int severity = this.computeSeverity(-1610610928);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(typeReference.resolvedType.sourceName())};
            this.handle(-1610610928, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }

    public void javadocInvalidReference(int sourceStart, int sourceEnd) {
        this.handle(-1610612253, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidSeeHref(int sourceStart, int sourceEnd) {
        this.handle(-1610612252, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidSeeReferenceArgs(int sourceStart, int sourceEnd) {
        this.handle(-1610612251, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidSeeUrlReference(int sourceStart, int sourceEnd) {
        this.handle(-1610612274, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidTag(int sourceStart, int sourceEnd) {
        this.handle(-1610612249, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidThrowsClass(int sourceStart, int sourceEnd) {
        this.handle(-1610612257, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidThrowsClassName(TypeReference typeReference, int modifiers) {
        int severity = this.computeSeverity(-1610612255);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(typeReference.resolvedType.sourceName())};
            this.handle(-1610612255, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }

    public void javadocInvalidType(ASTNode location, TypeBinding type, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            int id = -1610612233;
            switch (type.problemId()) {
                case 1: {
                    id = -1610612233;
                    break;
                }
                case 2: {
                    id = -1610612232;
                    break;
                }
                case 30: {
                    id = -2130704982;
                    break;
                }
                case 3: {
                    id = -1610612231;
                    break;
                }
                case 4: {
                    id = -1610612229;
                    break;
                }
                case 5: {
                    id = -1610612226;
                    break;
                }
                case 7: {
                    id = -1610612268;
                    break;
                }
                default: {
                    this.needImplementation(location);
                }
            }
            int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            this.handle(id, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
        }
    }

    public void javadocInvalidUsesClass(int sourceStart, int sourceEnd) {
        this.handle(-1610610932, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocInvalidUsesClassName(TypeReference typeReference, int modifiers) {
        int severity = this.computeSeverity(-1610610933);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(typeReference.resolvedType.sourceName())};
            this.handle(-1610610933, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }

    public void javadocInvalidValueReference(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612219, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMalformedSeeReference(int sourceStart, int sourceEnd) {
        this.handle(-1610612223, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocMissing(int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612250);
        this.javadocMissing(sourceStart, sourceEnd, severity, modifiers);
    }

    public void javadocMissing(int sourceStart, int sourceEnd, int severity, int modifiers) {
        String arg;
        boolean report;
        if (severity == 256) {
            return;
        }
        boolean overriding = (modifiers & 0x30000000) != 0;
        boolean bl = report = this.options.getSeverity(0x100000) != 256 && (!overriding || this.options.reportMissingJavadocCommentsOverriding);
        if (report && (arg = this.javadocVisibilityArgument(this.options.reportMissingJavadocCommentsVisibility, modifiers)) != null) {
            String[] arguments = new String[]{arg};
            this.handle(-1610612250, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocModuleMissing(int sourceStart, int sourceEnd, int severity) {
        boolean report;
        if (severity == 256) {
            return;
        }
        boolean bl = report = this.options.getSeverity(0x100000) != 256;
        if (report) {
            String[] arguments = new String[]{"module"};
            this.handle(-1610612250, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingHashCharacter(int sourceStart, int sourceEnd, String ref) {
        int severity = this.computeSeverity(-1610612221);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{ref};
        this.handle(-1610612221, arguments, arguments, severity, sourceStart, sourceEnd);
    }

    public void javadocMissingIdentifier(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612269, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingParamName(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612264, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingParamTag(char[] name, int sourceStart, int sourceEnd, int modifiers) {
        boolean report;
        int severity = this.computeSeverity(-1610612265);
        if (severity == 256) {
            return;
        }
        boolean overriding = (modifiers & 0x30000000) != 0;
        boolean bl = report = this.options.getSeverity(0x200000) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(name)};
            this.handle(-1610612265, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingProvidesClassName(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610610929, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingProvidesTag(TypeReference typeRef, int sourceStart, int sourceEnd, int modifiers) {
        boolean report;
        int severity = this.computeSeverity(-1610610931);
        if (severity == 256) {
            return;
        }
        boolean bl = report = this.options.getSeverity(0x200000) != 256;
        if (report) {
            String[] arguments = new String[]{String.valueOf(typeRef.resolvedType.sourceName())};
            this.handle(-1610610931, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingReference(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612254, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingReturnTag(int sourceStart, int sourceEnd, int modifiers) {
        boolean report;
        boolean overriding = (modifiers & 0x30000000) != 0;
        boolean bl = report = this.options.getSeverity(0x200000) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612261, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingTagDescription(char[] tokenName, int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612273);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{new String(tokenName)};
            this.handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingTagDescriptionAfterReference(int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612273);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612273, NoArgument, NoArgument, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingThrowsClassName(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612258, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingThrowsTag(TypeReference typeRef, int modifiers) {
        boolean report;
        int severity = this.computeSeverity(-1610612259);
        if (severity == 256) {
            return;
        }
        boolean overriding = (modifiers & 0x30000000) != 0;
        boolean bl = report = this.options.getSeverity(0x200000) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(typeRef.resolvedType.sourceName())};
            this.handle(-1610612259, arguments, arguments, severity, typeRef.sourceStart, typeRef.sourceEnd);
        }
    }

    public void javadocMissingUsesClassName(int sourceStart, int sourceEnd, int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610610934, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
    }

    public void javadocMissingUsesTag(TypeReference typeRef, int sourceStart, int sourceEnd, int modifiers) {
        boolean report;
        int severity = this.computeSeverity(-1610610936);
        if (severity == 256) {
            return;
        }
        boolean bl = report = this.options.getSeverity(0x200000) != 256;
        if (report) {
            String[] arguments = new String[]{String.valueOf(typeRef.resolvedType.sourceName())};
            this.handle(-1610610936, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocUndeclaredParamTagName(char[] token, int sourceStart, int sourceEnd, int modifiers) {
        int severity = this.computeSeverity(-1610612262);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            String[] arguments = new String[]{String.valueOf(token)};
            this.handle(-1610612262, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }

    public void javadocUnexpectedTag(int sourceStart, int sourceEnd) {
        this.handle(-1610612266, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocUnexpectedText(int sourceStart, int sourceEnd) {
        this.handle(-1610612218, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void javadocUnterminatedInlineTag(int sourceStart, int sourceEnd) {
        this.handle(-1610612224, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    private boolean javadocVisibility(int visibility, int modifiers) {
        if (modifiers < 0) {
            return true;
        }
        switch (modifiers & 7) {
            case 1: {
                return true;
            }
            case 4: {
                return visibility != 1;
            }
            case 0: {
                return visibility == 0 || visibility == 2;
            }
            case 2: {
                return visibility == 2;
            }
        }
        return true;
    }

    private String javadocVisibilityArgument(int visibility, int modifiers) {
        String argument = null;
        switch (modifiers & 7) {
            case 1: {
                argument = "public";
                break;
            }
            case 4: {
                if (visibility == 1) break;
                argument = "protected";
                break;
            }
            case 0: {
                if (visibility != 0 && visibility != 2) break;
                argument = "default";
                break;
            }
            case 2: {
                if (visibility != 2) break;
                argument = "private";
            }
        }
        return argument;
    }

    public void localVariableHiding(LocalDeclaration local, Binding hiddenVariable, boolean isSpecialArgHidingField) {
        if (hiddenVariable instanceof LocalVariableBinding) {
            int id = local instanceof Argument ? 536871006 : 536871002;
            int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            String[] arguments = new String[]{new String(local.name)};
            this.handle(id, arguments, arguments, severity, this.nodeSourceStart(hiddenVariable, local), this.nodeSourceEnd(hiddenVariable, local));
        } else if (hiddenVariable instanceof FieldBinding) {
            if (isSpecialArgHidingField && (!this.options.reportSpecialParameterHidingField || ((FieldBinding)hiddenVariable).isRecordComponent())) {
                return;
            }
            int id = local instanceof Argument ? 536871007 : 570425435;
            int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            FieldBinding field = (FieldBinding)hiddenVariable;
            this.handle(id, new String[]{new String(local.name), new String(field.declaringClass.readableName())}, new String[]{new String(local.name), new String(field.declaringClass.shortReadableName())}, severity, local.sourceStart, local.sourceEnd);
        }
    }

    public void localVariableNonNullComparedToNull(LocalVariableBinding local, ASTNode location) {
        int problemId;
        String[] arguments;
        int severity = this.computeSeverity(536871370);
        if (severity == 256) {
            return;
        }
        if (local.isNonNull()) {
            char[][] annotationName = this.options.nonNullAnnotationName;
            arguments = new String[]{new String(local.name), new String(annotationName[annotationName.length - 1])};
            problemId = 536871844;
        } else {
            arguments = new String[]{new String(local.name)};
            problemId = 536871370;
        }
        this.handle(problemId, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void localVariableNullComparedToNonNull(LocalVariableBinding local, ASTNode location) {
        int severity = this.computeSeverity(536871366);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871366, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public boolean expressionNonNullComparison(Expression expr, boolean checkForNull) {
        long tagBits;
        int problemId = 0;
        Binding binding = null;
        String[] arguments = null;
        int start = 0;
        int end = 0;
        Expression location = expr;
        if (expr.resolvedType != null && (tagBits = expr.resolvedType.tagBits & 0x180000000000000L) == 0x100000000000000L) {
            problemId = 536871873;
            arguments = new String[]{String.valueOf(expr.resolvedType.nullAnnotatedReadableName(this.options, true))};
            start = this.nodeSourceStart(location);
            end = this.nodeSourceEnd(location);
            this.handle(problemId, arguments, arguments, start, end);
            return true;
        }
        while (true) {
            if (expr instanceof Assignment) {
                return false;
            }
            if (!(expr instanceof CastExpression)) break;
            expr = ((CastExpression)expr).expression;
        }
        if (expr instanceof MessageSend) {
            MethodBinding method;
            problemId = checkForNull ? 536871848 : 536871832;
            binding = method = ((MessageSend)expr).binding;
            arguments = new String[]{new String(method.shortReadableName())};
            start = location.sourceStart;
            end = location.sourceEnd;
        } else if (expr instanceof Reference && !(expr instanceof ThisReference) && !(expr instanceof ArrayReference)) {
            FieldBinding field = ((Reference)expr).lastFieldBinding();
            if (field == null) {
                return false;
            }
            if (field.isNonNull()) {
                problemId = checkForNull ? 536871850 : 536871849;
                char[][] nonNullName = this.options.nonNullAnnotationName;
                arguments = new String[]{new String(field.name), new String(nonNullName[nonNullName.length - 1])};
            } else if (field.constant() != Constant.NotAConstant) {
                problemId = checkForNull ? 536871857 : 536871856;
                char[][] nonNullName = this.options.nonNullAnnotationName;
                arguments = new String[]{new String(field.name), new String(nonNullName[nonNullName.length - 1])};
            } else {
                problemId = checkForNull ? 536871854 : 536871853;
                arguments = new String[]{String.valueOf(field.name)};
            }
            binding = field;
            start = this.nodeSourceStart(binding, location);
            end = this.nodeSourceEnd(binding, location);
        } else if (!(expr instanceof AllocationExpression || expr instanceof ArrayAllocationExpression || expr instanceof ArrayInitializer || expr instanceof ClassLiteralAccess || expr instanceof ThisReference)) {
            if (expr instanceof Literal || expr instanceof ConditionalExpression || expr instanceof SwitchExpression) {
                if (expr instanceof NullLiteral) {
                    this.needImplementation(location);
                    return false;
                }
                if (expr.resolvedType != null && expr.resolvedType.isBaseType()) {
                    return false;
                }
            } else if (expr instanceof BinaryExpression) {
                if ((expr.bits & 0xF) != 11) {
                    return false;
                }
            } else {
                this.needImplementation(expr);
                return false;
            }
        }
        if (problemId == 0) {
            problemId = checkForNull ? 536871582 : 536871583;
            start = location.sourceStart;
            end = location.sourceEnd;
            arguments = NoArgument;
        }
        this.handle(problemId, arguments, arguments, start, end);
        return true;
    }

    public void nullAnnotationUnsupportedLocation(Annotation annotation) {
        String[] arguments = new String[]{String.valueOf(annotation.resolvedType.readableName())};
        String[] shortArguments = new String[]{String.valueOf(annotation.resolvedType.shortReadableName())};
        int severity = 129;
        if (annotation.recipient instanceof ReferenceBinding && ((ReferenceBinding)annotation.recipient).isAnnotationType()) {
            severity = 0;
        }
        this.handle(536871874, arguments, shortArguments, severity, annotation.sourceStart, annotation.sourceEnd);
    }

    public void nullAnnotationAtQualifyingType(Annotation annotation) {
        String[] arguments = new String[]{String.valueOf(annotation.resolvedType.readableName())};
        String[] shortArguments = new String[]{String.valueOf(annotation.resolvedType.shortReadableName())};
        int severity = 129;
        this.handle(1610613797, arguments, shortArguments, severity, annotation.sourceStart, annotation.sourceEnd);
    }

    public void nullAnnotationUnsupportedLocation(TypeReference type) {
        int sourceEnd = type.sourceEnd;
        if (type instanceof ParameterizedSingleTypeReference) {
            ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference)type;
            TypeReference[] typeArguments = typeReference.typeArguments;
            sourceEnd = typeArguments[typeArguments.length - 1].sourceEnd > typeReference.sourceEnd ? this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd) : type.sourceEnd;
        } else if (type instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference typeReference = (ParameterizedQualifiedTypeReference)type;
            sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
        } else {
            sourceEnd = type.sourceEnd;
        }
        this.handle(536871875, NoArgument, NoArgument, type.sourceStart, sourceEnd);
    }

    public void localVariableNullInstanceof(LocalVariableBinding local, ASTNode location) {
        int severity = this.computeSeverity(536871368);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871368, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void localVariableNullReference(LocalVariableBinding local, ASTNode location) {
        if (location instanceof Expression && ((Expression)location).isTrulyExpression() && (((Expression)location).implicitConversion & 0x400) != 0) {
            this.nullUnboxing(location, local.type);
            return;
        }
        int severity = this.computeSeverity(536871363);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871363, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void fieldFreeTypeVariableReference(FieldBinding variable, long position) {
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(variable.type.readableName()), new String(nullableName[nullableName.length - 1])};
        this.handle(976, arguments, arguments, (int)(position >>> 32), (int)position);
    }

    public void localVariableFreeTypeVariableReference(LocalVariableBinding local, ASTNode location) {
        int severity = this.computeSeverity(976);
        if (severity == 256) {
            return;
        }
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(local.type.readableName()), new String(nullableName[nullableName.length - 1])};
        this.handle(976, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void methodReturnTypeFreeTypeVariableReference(MethodBinding method, ASTNode location) {
        int severity = this.computeSeverity(976);
        if (severity == 256) {
            return;
        }
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(method.returnType.readableName()), new String(nullableName[nullableName.length - 1])};
        this.handle(976, arguments, arguments, location.sourceStart, location.sourceEnd);
    }

    public void localVariablePotentialNullReference(LocalVariableBinding local, ASTNode location) {
        if (local.type.isFreeTypeVariable()) {
            this.localVariableFreeTypeVariableReference(local, location);
            return;
        }
        if (location instanceof Expression && ((Expression)location).isTrulyExpression() && (((Expression)location).implicitConversion & 0x400) != 0) {
            this.potentialNullUnboxing(location, local.type);
            return;
        }
        if ((local.type.tagBits & 0x80000000000000L) != 0L && location instanceof Expression && ((Expression)location).isTrulyExpression()) {
            this.dereferencingNullableExpression((Expression)location);
            return;
        }
        int severity = this.computeSeverity(536871364);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871364, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void potentialNullUnboxing(ASTNode expression, TypeBinding boxType) {
        String[] arguments = new String[]{String.valueOf(boxType.readableName())};
        String[] argumentsShort = new String[]{String.valueOf(boxType.shortReadableName())};
        this.handle(536871371, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    public void nullUnboxing(ASTNode expression, TypeBinding boxType) {
        String[] arguments = new String[]{String.valueOf(boxType.readableName())};
        String[] argumentsShort = new String[]{String.valueOf(boxType.shortReadableName())};
        this.handle(536871373, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    public void nullableFieldDereference(FieldBinding variable, long position) {
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(variable.name), new String(nullableName[nullableName.length - 1])};
        this.handle(33555356, arguments, arguments, (int)(position >>> 32), (int)position);
    }

    public void localVariableRedundantCheckOnNonNull(LocalVariableBinding local, ASTNode location) {
        int problemId;
        String[] arguments;
        int severity = this.computeSeverity(536871369);
        if (severity == 256) {
            return;
        }
        if (local.isNonNull()) {
            char[][] annotationName = this.options.nonNullAnnotationName;
            arguments = new String[]{new String(local.name), new String(annotationName[annotationName.length - 1])};
            problemId = 536871843;
        } else {
            arguments = new String[]{new String(local.name)};
            problemId = 536871369;
        }
        this.handle(problemId, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void localVariableRedundantCheckOnNull(LocalVariableBinding local, ASTNode location) {
        int severity = this.computeSeverity(536871365);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871365, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void localVariableRedundantNullAssignment(LocalVariableBinding local, ASTNode location) {
        if ((location.bits & 8) != 0) {
            return;
        }
        int severity = this.computeSeverity(536871367);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.name)};
        this.handle(536871367, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void methodMustOverride(AbstractMethodDeclaration method, long complianceLevel) {
        MethodBinding binding = method.binding;
        this.handle(complianceLevel == 0x310000L ? 67109487 : 67109498, new String[]{new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName())}, new String[]{new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName())}, method.sourceStart, method.sourceEnd);
    }

    public void methodNameClash(MethodBinding currentMethod, MethodBinding inheritedMethod, int severity) {
        this.handle(67109424, new String[]{new String(currentMethod.selector), this.typesAsString(currentMethod, false), new String(currentMethod.declaringClass.readableName()), this.typesAsString(inheritedMethod, false), new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(currentMethod.selector), this.typesAsString(currentMethod, true), new String(currentMethod.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod, true), new String(inheritedMethod.declaringClass.shortReadableName())}, severity, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }

    public void methodNameClashHidden(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        this.handle(67109448, new String[]{new String(currentMethod.selector), this.typesAsString(currentMethod, currentMethod.parameters, false), new String(currentMethod.declaringClass.readableName()), this.typesAsString(inheritedMethod, inheritedMethod.parameters, false), new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(currentMethod.selector), this.typesAsString(currentMethod, currentMethod.parameters, true), new String(currentMethod.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod, inheritedMethod.parameters, true), new String(inheritedMethod.declaringClass.shortReadableName())}, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }

    public void methodNeedBody(AbstractMethodDeclaration methodDecl) {
        this.handle(603979883, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void methodNeedingNoBody(MethodDeclaration methodDecl) {
        this.handle((methodDecl.modifiers & 0x100) != 0 ? 603979888 : 603979889, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void methodWithConstructorName(MethodDeclaration methodDecl) {
        this.handle(67108974, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void methodCanBeDeclaredStatic(MethodDeclaration methodDecl) {
        int severity = this.computeSeverity(603979897);
        if (severity == 256) {
            return;
        }
        MethodBinding method = methodDecl.binding;
        this.handle(603979897, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void methodCanBePotentiallyDeclaredStatic(MethodDeclaration methodDecl) {
        int severity = this.computeSeverity(603979898);
        if (severity == 256) {
            return;
        }
        MethodBinding method = methodDecl.binding;
        this.handle(603979898, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void missingDeprecatedAnnotationForField(FieldDeclaration field) {
        int severity = this.computeSeverity(536871540);
        if (severity == 256) {
            return;
        }
        FieldBinding binding = field.binding;
        this.handle(536871540, new String[]{new String(binding.declaringClass.readableName()), new String(binding.name)}, new String[]{new String(binding.declaringClass.shortReadableName()), new String(binding.name)}, severity, this.nodeSourceStart(binding, field), this.nodeSourceEnd(binding, field));
    }

    public void missingDeprecatedAnnotationForMethod(AbstractMethodDeclaration method) {
        int severity = this.computeSeverity(536871541);
        if (severity == 256) {
            return;
        }
        MethodBinding binding = method.binding;
        this.handle(536871541, new String[]{new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName())}, new String[]{new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName())}, severity, method.sourceStart, method.sourceEnd);
    }

    public void missingDeprecatedAnnotationForType(TypeDeclaration type) {
        int severity = this.computeSeverity(536871542);
        if (severity == 256) {
            return;
        }
        SourceTypeBinding binding = type.binding;
        this.handle(536871542, new String[]{new String(((Binding)binding).readableName())}, new String[]{new String(((Binding)binding).shortReadableName())}, severity, type.sourceStart, type.sourceEnd);
    }

    public void notAFunctionalInterface(TypeDeclaration type) {
        SourceTypeBinding binding = type.binding;
        this.handle(553648792, new String[]{new String(((Binding)binding).readableName())}, new String[]{new String(((Binding)binding).shortReadableName())}, type.sourceStart, type.sourceEnd);
    }

    public void missingEnumConstantCase(SwitchStatement switchStatement, FieldBinding enumConstant) {
        this.missingEnumConstantCase(switchStatement.defaultCase, enumConstant, switchStatement.expression);
    }

    public void missingEnumConstantCase(SwitchExpression switchExpression, FieldBinding enumConstant) {
        this.missingSwitchExpressionEnumConstantCase(switchExpression.defaultCase, enumConstant, switchExpression.expression);
    }

    private void missingSwitchExpressionEnumConstantCase(CaseStatement defaultCase, FieldBinding enumConstant, ASTNode expression) {
        this.handle(1073743533, new String[]{new String(enumConstant.declaringClass.readableName()), new String(enumConstant.name)}, new String[]{new String(enumConstant.declaringClass.shortReadableName()), new String(enumConstant.name)}, expression.sourceStart, expression.sourceEnd);
    }

    private void missingEnumConstantCase(CaseStatement defaultCase, FieldBinding enumConstant, ASTNode expression) {
        this.handle(defaultCase == null ? 33555193 : 0x2000300, new String[]{new String(enumConstant.declaringClass.readableName()), new String(enumConstant.name)}, new String[]{new String(enumConstant.declaringClass.shortReadableName()), new String(enumConstant.name)}, expression.sourceStart, expression.sourceEnd);
    }

    public void missingDefaultCase(SwitchStatement switchStatement, boolean isEnumSwitch, TypeBinding expressionType) {
        if (isEnumSwitch) {
            this.handle(536871678, new String[]{new String(expressionType.readableName())}, new String[]{new String(expressionType.shortReadableName())}, switchStatement.expression.sourceStart, switchStatement.expression.sourceEnd);
        } else {
            this.handle(switchStatement instanceof SwitchExpression ? 1073743531 : 0x200002FF, NoArgument, NoArgument, switchStatement.expression.sourceStart, switchStatement.expression.sourceEnd);
        }
    }

    public void missingOverrideAnnotation(AbstractMethodDeclaration method) {
        int severity = this.computeSeverity(67109491);
        if (severity == 256) {
            return;
        }
        MethodBinding binding = method.binding;
        this.handle(67109491, new String[]{new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName())}, new String[]{new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName())}, severity, method.sourceStart, method.sourceEnd);
    }

    public void missingOverrideAnnotationForInterfaceMethodImplementation(AbstractMethodDeclaration method) {
        int severity = this.computeSeverity(67109500);
        if (severity == 256) {
            return;
        }
        MethodBinding binding = method.binding;
        this.handle(67109500, new String[]{new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName())}, new String[]{new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName())}, severity, method.sourceStart, method.sourceEnd);
    }

    public void missingReturnType(AbstractMethodDeclaration methodDecl) {
        this.handle(16777327, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void missingSemiColon(Expression expression) {
        this.handle(0x600000E0, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void missingSerialVersion(TypeDeclaration typeDecl) {
        String[] arguments = new String[]{new String(typeDecl.name)};
        this.handle(0x20000060, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void missingSynchronizedOnInheritedMethod(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        this.handle(67109281, new String[]{new String(currentMethod.declaringClass.readableName()), new String(currentMethod.selector), this.typesAsString(currentMethod, false)}, new String[]{new String(currentMethod.declaringClass.shortReadableName()), new String(currentMethod.selector), this.typesAsString(currentMethod, true)}, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }

    public void missingTypeInConstructor(ASTNode location, MethodBinding constructor) {
        List<TypeBinding> missingTypes = constructor.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The constructor " + constructor + " is wrongly tagged as containing missing types");
            return;
        }
        TypeBinding missingType = missingTypes.get(0);
        int start = location.sourceStart;
        int end = location.sourceEnd;
        if (location instanceof QualifiedAllocationExpression) {
            QualifiedAllocationExpression qualifiedAllocation = (QualifiedAllocationExpression)location;
            if (qualifiedAllocation.anonymousType != null) {
                start = qualifiedAllocation.anonymousType.sourceStart;
                end = qualifiedAllocation.anonymousType.sourceEnd;
            }
        }
        this.handle(0x8000081, new String[]{new String(constructor.declaringClass.readableName()), this.typesAsString(constructor, false), new String(missingType.readableName())}, new String[]{new String(constructor.declaringClass.shortReadableName()), this.typesAsString(constructor, true), new String(missingType.shortReadableName())}, start, end);
    }

    public void missingTypeInLambda(LambdaExpression lambda, MethodBinding method) {
        int nameSourceStart = lambda.sourceStart();
        int nameSourceEnd = lambda.diagnosticsSourceEnd();
        List<TypeBinding> missingTypes = method.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The lambda expression " + method + " is wrongly tagged as containing missing types");
            return;
        }
        TypeBinding missingType = missingTypes.get(0);
        this.handle(67109135, new String[]{new String(missingType.readableName())}, new String[]{new String(missingType.shortReadableName())}, nameSourceStart, nameSourceEnd);
    }

    public void missingTypeInMethod(ASTNode astNode, MethodBinding method) {
        int nameSourceEnd;
        int nameSourceStart;
        if (astNode instanceof MessageSend) {
            MessageSend messageSend = astNode instanceof MessageSend ? (MessageSend)astNode : null;
            nameSourceStart = (int)(messageSend.nameSourcePosition >>> 32);
            nameSourceEnd = (int)messageSend.nameSourcePosition;
        } else {
            nameSourceStart = astNode.sourceStart;
            nameSourceEnd = astNode.sourceEnd;
        }
        List<TypeBinding> missingTypes = method.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The method " + method + " is wrongly tagged as containing missing types");
            return;
        }
        TypeBinding missingType = missingTypes.get(0);
        this.handle(67108984, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false), new String(missingType.readableName())}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true), new String(missingType.shortReadableName())}, nameSourceStart, nameSourceEnd);
    }

    public void missingValueForAnnotationMember(Annotation annotation, char[] memberName) {
        String memberString = new String(memberName);
        this.handle(16777825, new String[]{new String(annotation.resolvedType.readableName()), memberString}, new String[]{new String(annotation.resolvedType.shortReadableName()), memberString}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void mustDefineDimensionsOrInitializer(ArrayAllocationExpression expression) {
        this.handle(536871071, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void mustUseAStaticMethod(MessageSend messageSend, MethodBinding method) {
        this.handle(603979977, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, messageSend.sourceStart, messageSend.sourceEnd);
    }

    public void nativeMethodsCannotBeStrictfp(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(type.sourceName()), new String(methodDecl.selector)};
        this.handle(67109231, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void needImplementation(ASTNode location) {
        this.abortDueToInternalError(Messages.abort_missingCode, location);
    }

    public void needToEmulateFieldAccess(FieldBinding field, ASTNode location, boolean isReadAccess) {
        int id = isReadAccess ? 33554622 : 33554623;
        int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        this.handle(id, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void needToEmulateMethodAccess(MethodBinding method, ASTNode location) {
        if (method.isConstructor()) {
            int severity = this.computeSeverity(67109057);
            if (severity == 256) {
                return;
            }
            if (method.declaringClass.isEnum()) {
                return;
            }
            this.handle(67109057, new String[]{new String(method.declaringClass.readableName()), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true)}, severity, location.sourceStart, location.sourceEnd);
            return;
        }
        int severity = this.computeSeverity(0x40000C0);
        if (severity == 256) {
            return;
        }
        this.handle(0x40000C0, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, location.sourceStart, location.sourceEnd);
    }

    public void noAdditionalBoundAfterTypeVariable(TypeReference boundReference) {
        this.handle(16777789, new String[]{new String(boundReference.resolvedType.readableName())}, new String[]{new String(boundReference.resolvedType.shortReadableName())}, boundReference.sourceStart, boundReference.sourceEnd);
    }

    private int nodeSourceEnd(ASTNode node) {
        FieldBinding field;
        if (node instanceof Reference && (field = ((Reference)node).lastFieldBinding()) != null) {
            return this.nodeSourceEnd(field, node);
        }
        return node.sourceEnd;
    }

    private int nodeSourceEnd(Binding field, ASTNode node) {
        return this.nodeSourceEnd(field, node, 0);
    }

    private int nodeSourceEnd(Binding field, ASTNode node, int index) {
        if (node instanceof ArrayTypeReference) {
            return ((ArrayTypeReference)node).originalSourceEnd;
        }
        if (node instanceof QualifiedNameReference) {
            QualifiedNameReference ref = (QualifiedNameReference)node;
            if (ref.binding == field) {
                if (index == 0) {
                    return (int)ref.sourcePositions[ref.indexOfFirstFieldBinding - 1];
                }
                int length = ref.sourcePositions.length;
                if (index < length) {
                    return (int)ref.sourcePositions[index];
                }
                return (int)ref.sourcePositions[0];
            }
            FieldBinding[] otherFields = ref.otherBindings;
            if (otherFields != null) {
                int offset = ref.indexOfFirstFieldBinding;
                if (index != 0) {
                    int i = 0;
                    int length = otherFields.length;
                    while (i < length) {
                        if (otherFields[i] == field && i + offset == index) {
                            return (int)ref.sourcePositions[i + offset];
                        }
                        ++i;
                    }
                } else {
                    int i = 0;
                    int length = otherFields.length;
                    while (i < length) {
                        if (otherFields[i] == field) {
                            return (int)ref.sourcePositions[i + offset];
                        }
                        ++i;
                    }
                }
            }
        } else if (node instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
            if (index < reference.sourcePositions.length) {
                return (int)reference.sourcePositions[index];
            }
        } else {
            if (node instanceof ArrayQualifiedTypeReference) {
                ArrayQualifiedTypeReference reference = (ArrayQualifiedTypeReference)node;
                int length = reference.sourcePositions.length;
                if (index < length) {
                    return (int)reference.sourcePositions[index];
                }
                return (int)reference.sourcePositions[length - 1];
            }
            if (node instanceof QualifiedTypeReference) {
                QualifiedTypeReference reference = (QualifiedTypeReference)node;
                int length = reference.sourcePositions.length;
                if (index < length) {
                    return (int)reference.sourcePositions[index];
                }
            }
        }
        return node.sourceEnd;
    }

    private int nodeSourceStart(ASTNode node) {
        FieldBinding field;
        if (node instanceof Reference && (field = ((Reference)node).lastFieldBinding()) != null) {
            return this.nodeSourceStart(field, node);
        }
        return node.sourceStart;
    }

    private int nodeSourceStart(Binding field, ASTNode node) {
        return this.nodeSourceStart(field, node, 0);
    }

    private int nodeSourceStart(Binding field, ASTNode node, int index) {
        if (node instanceof FieldReference) {
            FieldReference fieldReference = (FieldReference)node;
            return (int)(fieldReference.nameSourcePosition >> 32);
        }
        if (node instanceof QualifiedNameReference) {
            QualifiedNameReference ref = (QualifiedNameReference)node;
            if (ref.binding == field) {
                if (index == 0) {
                    return (int)(ref.sourcePositions[ref.indexOfFirstFieldBinding - 1] >> 32);
                }
                return (int)(ref.sourcePositions[index] >> 32);
            }
            FieldBinding[] otherFields = ref.otherBindings;
            if (otherFields != null) {
                int offset = ref.indexOfFirstFieldBinding;
                if (index != 0) {
                    int i = 0;
                    int length = otherFields.length;
                    while (i < length) {
                        if (otherFields[i] == field && i + offset == index) {
                            return (int)(ref.sourcePositions[i + offset] >> 32);
                        }
                        ++i;
                    }
                } else {
                    int i = 0;
                    int length = otherFields.length;
                    while (i < length) {
                        if (otherFields[i] == field) {
                            return (int)(ref.sourcePositions[i + offset] >> 32);
                        }
                        ++i;
                    }
                }
            }
        } else if (node instanceof ParameterizedQualifiedTypeReference) {
            ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
            return (int)(reference.sourcePositions[0] >>> 32);
        }
        return node.sourceStart;
    }

    public void noMoreAvailableSpaceForArgument(LocalVariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.name)};
        this.handle(local instanceof SyntheticArgumentBinding ? 536870979 : 536870977, arguments, arguments, 159, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void noMoreAvailableSpaceForConstant(TypeDeclaration typeDeclaration) {
        this.handle(536871343, new String[]{new String(typeDeclaration.binding.readableName())}, new String[]{new String(typeDeclaration.binding.shortReadableName())}, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void noMoreAvailableSpaceForLocal(LocalVariableBinding local, ASTNode location) {
        String[] arguments = new String[]{new String(local.name)};
        this.handle(0x20000042, arguments, arguments, 159, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    public void noMoreAvailableSpaceInConstantPool(TypeDeclaration typeDeclaration) {
        this.handle(536871342, new String[]{new String(typeDeclaration.binding.readableName())}, new String[]{new String(typeDeclaration.binding.shortReadableName())}, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void nonExternalizedStringLiteral(ASTNode location) {
        this.handle(536871173, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void nonGenericTypeCannotBeParameterized(int index, ASTNode location, TypeBinding type, TypeBinding[] argumentTypes) {
        if (location == null) {
            this.handle(16777740, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true)}, 131, 0, 0);
            return;
        }
        this.handle(16777740, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true)}, this.nodeSourceStart(null, location), this.nodeSourceEnd(null, location, index));
    }

    public void nonStaticAccessToStaticField(ASTNode location, FieldBinding field) {
        this.nonStaticAccessToStaticField(location, field, -1);
    }

    public void nonStaticAccessToStaticField(ASTNode location, FieldBinding field, int index) {
        int severity = this.computeSeverity(570425420);
        if (severity == 256) {
            return;
        }
        this.handle(570425420, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(field, location, index), this.nodeSourceEnd(field, location, index));
    }

    public void nonStaticAccessToStaticMethod(ASTNode location, MethodBinding method) {
        this.handle(603979893, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, location.sourceStart, location.sourceEnd);
    }

    public void nonStaticContextForEnumMemberType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(0x20000020, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void noSuchEnclosingInstance(TypeBinding targetType, ASTNode location, boolean isConstructorCall) {
        int id = isConstructorCall ? 536870940 : (location instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)location).accessMode == 1 ? 0x1000014 : (location instanceof AllocationExpression && (((AllocationExpression)location).binding.declaringClass.isMemberType() || ((AllocationExpression)location).binding.declaringClass.isAnonymousType() && ((AllocationExpression)location).binding.declaringClass.superclass().isMemberType()) ? 0x1000015 : 0x1000016));
        this.handle(id, new String[]{new String(targetType.readableName())}, new String[]{new String(targetType.shortReadableName())}, location.sourceStart, location instanceof LambdaExpression ? ((LambdaExpression)location).diagnosticsSourceEnd() : location.sourceEnd);
    }

    public void notCompatibleTypesError(EqualExpression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(0x100000F, new String[]{leftName, rightName}, new String[]{leftShortName, rightShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void notCompatibleTypesError(InstanceOfExpression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(0x1000010, new String[]{leftName, rightName}, new String[]{leftShortName, rightShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void notCompatibleTypesErrorInForeach(Expression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777796, new String[]{leftName, rightName}, new String[]{leftShortName, rightShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void objectCannotBeGeneric(TypeDeclaration typeDecl) {
        this.handle(0x2000020B, NoArgument, NoArgument, typeDecl.typeParameters[0].sourceStart, typeDecl.typeParameters[typeDecl.typeParameters.length - 1].sourceEnd);
    }

    public void objectCannotHaveSuperTypes(SourceTypeBinding type) {
        this.handle(536871241, NoArgument, NoArgument, type.sourceStart(), type.sourceEnd());
    }

    public void objectMustBeClass(SourceTypeBinding type) {
        this.handle(536871242, NoArgument, NoArgument, type.sourceStart(), type.sourceEnd());
    }

    public void operatorOnlyValidOnNumericType(CompoundAssignment assignment, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(0x1000011, new String[]{leftName, rightName}, new String[]{leftShortName, rightShortName}, assignment.sourceStart, assignment.sourceEnd);
    }

    public void overridesDeprecatedMethod(MethodBinding localMethod, MethodBinding inheritedMethod) {
        String localMethodName = new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.'));
        String localMethodShortName = new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.'));
        String sinceValue = this.deprecatedSinceValue(() -> inheritedMethod.getAnnotations());
        if (sinceValue != null) {
            this.handle((inheritedMethod.tagBits & 0x4000000000000000L) != 0L ? 67110278 : 67110273, new String[]{localMethodName, new String(inheritedMethod.declaringClass.readableName()), sinceValue}, new String[]{localMethodShortName, new String(inheritedMethod.declaringClass.shortReadableName()), sinceValue}, localMethod.sourceStart(), localMethod.sourceEnd());
        } else {
            this.handle((inheritedMethod.tagBits & 0x4000000000000000L) != 0L ? 67110268 : 67109276, new String[]{localMethodName, new String(inheritedMethod.declaringClass.readableName())}, new String[]{localMethodShortName, new String(inheritedMethod.declaringClass.shortReadableName())}, localMethod.sourceStart(), localMethod.sourceEnd());
        }
    }

    public void overridesMethodWithoutSuperInvocation(MethodBinding localMethod) {
        this.handle(67109280, new String[]{new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.'))}, new String[]{new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.'))}, localMethod.sourceStart(), localMethod.sourceEnd());
    }

    public void overridesPackageDefaultMethod(MethodBinding localMethod, MethodBinding inheritedMethod) {
        this.handle(67109274, new String[]{new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.')), new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.')), new String(inheritedMethod.declaringClass.shortReadableName())}, localMethod.sourceStart(), localMethod.sourceEnd());
    }

    public void packageCollidesWithType(CompilationUnitDeclaration compUnitDecl) {
        String[] arguments = new String[]{CharOperation.toString(compUnitDecl.currentPackage.tokens)};
        this.handle(0x1000141, arguments, arguments, compUnitDecl.currentPackage.sourceStart, compUnitDecl.currentPackage.sourceEnd);
    }

    public void packageIsNotExpectedPackage(CompilationUnitDeclaration compUnitDecl) {
        boolean hasPackageDeclaration = compUnitDecl.currentPackage == null;
        String[] arguments = new String[]{CharOperation.toString(compUnitDecl.compilationResult.compilationUnit.getPackageName()), hasPackageDeclaration ? "" : CharOperation.toString(compUnitDecl.currentPackage.tokens)};
        int end = compUnitDecl.sourceEnd <= 0 ? -1 : (hasPackageDeclaration ? 0 : compUnitDecl.currentPackage.sourceEnd);
        this.handle(536871240, arguments, arguments, hasPackageDeclaration ? 0 : compUnitDecl.currentPackage.sourceStart, end);
    }

    public void parameterAssignment(LocalVariableBinding local, ASTNode location) {
        int severity = this.computeSeverity(536870971);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(536870971, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }

    private String parameterBoundAsString(TypeVariableBinding typeVariable, boolean makeShort) {
        int length;
        StringBuffer nameBuffer = new StringBuffer(10);
        if (TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass)) {
            nameBuffer.append(makeShort ? typeVariable.superclass.shortReadableName() : typeVariable.superclass.readableName());
        }
        if ((length = typeVariable.superInterfaces.length) > 0) {
            int i = 0;
            while (i < length) {
                if (i > 0 || TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass)) {
                    nameBuffer.append(" & ");
                }
                nameBuffer.append(makeShort ? typeVariable.superInterfaces[i].shortReadableName() : typeVariable.superInterfaces[i].readableName());
                ++i;
            }
        }
        return nameBuffer.toString();
    }

    public void parameterizedMemberTypeMissingArguments(ASTNode location, TypeBinding type, int index) {
        if (location == null) {
            this.handle(16777778, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, 131, 0, 0);
            return;
        }
        this.handle(16777778, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }

    public void parseError(int startPosition, int endPosition, int currentToken, char[] currentTokenSource, String errorTokenName, String[] possibleTokens) {
        if (possibleTokens.length == 0) {
            if (this.isKeyword(currentToken)) {
                String[] arguments = new String[]{new String(currentTokenSource)};
                this.handle(1610612946, arguments, arguments, startPosition, endPosition);
                return;
            }
            String[] arguments = new String[]{errorTokenName};
            this.handle(1610612941, arguments, arguments, startPosition, endPosition);
            return;
        }
        StringBuffer list = new StringBuffer(20);
        int i = 0;
        int max = possibleTokens.length;
        while (i < max) {
            if (i > 0) {
                list.append(", ");
            }
            list.append('\"');
            list.append(possibleTokens[i]);
            list.append('\"');
            ++i;
        }
        if (this.isKeyword(currentToken)) {
            String[] arguments = new String[]{new String(currentTokenSource), list.toString()};
            this.handle(1610612945, arguments, arguments, startPosition, endPosition);
            return;
        }
        if (this.isLiteral(currentToken) || this.isIdentifier(currentToken)) {
            errorTokenName = new String(currentTokenSource);
        }
        String[] arguments = new String[]{errorTokenName, list.toString()};
        this.handle(0x600000CC, arguments, arguments, startPosition, endPosition);
    }

    public void parseErrorDeleteToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName) {
        this.syntaxError(1610612968, start, end, currentKind, errorTokenSource, errorTokenName, null);
    }

    public void parseErrorDeleteTokens(int start, int end) {
        this.handle(1610612969, NoArgument, NoArgument, start, end);
    }

    public void parseErrorInsertAfterToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        this.syntaxError(1610612967, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }

    public void parseErrorInsertBeforeToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        this.syntaxError(0x600000E6, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }

    public void parseErrorInsertToComplete(int start, int end, String inserted, String completed) {
        String[] arguments = new String[]{inserted, completed};
        this.handle(0x600000F0, arguments, arguments, start, end);
    }

    public void parseErrorInsertToCompletePhrase(int start, int end, String inserted) {
        String[] arguments = new String[]{inserted};
        this.handle(1610612978, arguments, arguments, start, end);
    }

    public void parseErrorInsertToCompleteScope(int start, int end, String inserted) {
        String[] arguments = new String[]{inserted};
        this.handle(1610612977, arguments, arguments, start, end);
    }

    public void parseErrorInvalidToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        this.syntaxError(1610612971, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }

    public void parseErrorMergeTokens(int start, int end, String expectedToken) {
        String[] arguments = new String[]{expectedToken};
        this.handle(1610612970, arguments, arguments, start, end);
    }

    public void parseErrorMisplacedConstruct(int start, int end) {
        this.handle(1610612972, NoArgument, NoArgument, start, end);
    }

    public void parseErrorNoSuggestion(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName) {
        this.syntaxError(1610612941, start, end, currentKind, errorTokenSource, errorTokenName, null);
    }

    public void parseErrorNoSuggestionForTokens(int start, int end) {
        this.handle(0x600000EE, NoArgument, NoArgument, start, end);
    }

    public void parseErrorReplaceToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        this.handleSyntaxError(0x600000CC, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }

    public void parseErrorReplaceTokens(int start, int end, String expectedToken) {
        String[] arguments = new String[]{expectedToken};
        this.handle(1610612973, arguments, arguments, start, end);
    }

    public void parseErrorUnexpectedEnd(int start, int end) {
        String[] arguments = this.referenceContext instanceof ConstructorDeclaration ? new String[]{Messages.parser_endOfConstructor} : (this.referenceContext instanceof MethodDeclaration ? new String[]{Messages.parser_endOfMethod} : (this.referenceContext instanceof TypeDeclaration ? new String[]{Messages.parser_endOfInitializer} : new String[]{Messages.parser_endOfFile}));
        this.handle(1610612975, arguments, arguments, start, end);
    }

    public void possibleAccidentalBooleanAssignment(Assignment assignment) {
        this.handle(536871091, NoArgument, NoArgument, assignment.sourceStart, assignment.sourceEnd);
    }

    public void possibleFallThroughCase(CaseStatement caseStatement) {
        this.handle(0x200000C2, NoArgument, NoArgument, caseStatement.sourceStart, caseStatement.sourceEnd);
    }

    public void publicClassMustMatchFileName(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
        this.referenceContext = typeDecl;
        String[] arguments = new String[]{new String(compUnitDecl.getFileName()), new String(typeDecl.name)};
        this.handle(16777541, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd, compUnitDecl.compilationResult);
    }

    public void rawMemberTypeCannotBeParameterized(ASTNode location, ReferenceBinding type, TypeBinding[] argumentTypes) {
        if (location == null) {
            this.handle(16777777, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false), new String(type.enclosingType().readableName())}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true), new String(type.enclosingType().shortReadableName())}, 131, 0, 0);
            return;
        }
        this.handle(16777777, new String[]{new String(type.readableName()), this.typesAsString(argumentTypes, false), new String(type.enclosingType().readableName())}, new String[]{new String(type.shortReadableName()), this.typesAsString(argumentTypes, true), new String(type.enclosingType().shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void rawTypeReference(ASTNode location, TypeBinding type) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        type = type.leafComponentType();
        this.handle(16777788, new String[]{new String(type.readableName()), new String(type.erasure().readableName())}, new String[]{new String(type.shortReadableName()), new String(type.erasure().shortReadableName())}, location.sourceStart, this.nodeSourceEnd(null, location, Integer.MAX_VALUE));
    }

    public void recursiveConstructorInvocation(ExplicitConstructorCall constructorCall) {
        this.handle(0x8000089, new String[]{new String(constructorCall.binding.declaringClass.readableName()), this.typesAsString(constructorCall.binding, false)}, new String[]{new String(constructorCall.binding.declaringClass.shortReadableName()), this.typesAsString(constructorCall.binding, true)}, constructorCall.sourceStart, constructorCall.sourceEnd);
    }

    public void redefineArgument(Argument arg) {
        String[] arguments = new String[]{new String(arg.name)};
        this.handle(536870968, arguments, arguments, arg.sourceStart, arg.sourceEnd);
    }

    public void redefineLocal(LocalDeclaration localDecl) {
        String[] arguments = new String[]{new String(localDecl.name)};
        this.handle(536870967, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }

    public void redundantSuperInterface(SourceTypeBinding type, TypeReference reference, ReferenceBinding superinterface, ReferenceBinding declaringType) {
        int severity = this.computeSeverity(16777547);
        if (severity != 256) {
            this.handle(16777547, new String[]{new String(superinterface.readableName()), new String(type.readableName()), new String(declaringType.readableName())}, new String[]{new String(superinterface.shortReadableName()), new String(type.shortReadableName()), new String(declaringType.shortReadableName())}, severity, reference.sourceStart, reference.sourceEnd);
        }
    }

    public void referenceMustBeArrayTypeAt(TypeBinding arrayType, ArrayReference arrayRef) {
        this.handle(536871062, new String[]{new String(arrayType.readableName())}, new String[]{new String(arrayType.shortReadableName())}, arrayRef.sourceStart, arrayRef.sourceEnd);
    }

    public void repeatedAnnotationWithContainer(Annotation annotation, Annotation container) {
        this.handle(16778115, new String[]{new String(annotation.resolvedType.readableName()), new String(container.resolvedType.readableName())}, new String[]{new String(annotation.resolvedType.shortReadableName()), new String(container.resolvedType.shortReadableName())}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void containerAnnotationTypeMustHaveValue(ASTNode markerNode, ReferenceBinding containerAnnotationType) {
        this.handle(16778119, new String[]{new String(containerAnnotationType.readableName())}, new String[]{new String(containerAnnotationType.shortReadableName())}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void containerAnnotationTypeHasWrongValueType(ASTNode markerNode, ReferenceBinding containerAnnotationType, ReferenceBinding annotationType, TypeBinding returnType) {
        this.handle(16778118, new String[]{new String(containerAnnotationType.readableName()), new String(annotationType.readableName()), new String(returnType.readableName())}, new String[]{new String(containerAnnotationType.shortReadableName()), new String(annotationType.shortReadableName()), new String(returnType.shortReadableName())}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void containerAnnotationTypeHasNonDefaultMembers(ASTNode markerNode, ReferenceBinding containerAnnotationType, char[] selector) {
        this.handle(16778120, new String[]{new String(containerAnnotationType.readableName()), new String(selector)}, new String[]{new String(containerAnnotationType.shortReadableName()), new String(selector)}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void containerAnnotationTypeHasShorterRetention(ASTNode markerNode, ReferenceBinding annotationType, String annotationRetention, ReferenceBinding containerAnnotationType, String containerRetention) {
        this.handle(16778121, new String[]{new String(annotationType.readableName()), annotationRetention, new String(containerAnnotationType.readableName()), containerRetention}, new String[]{new String(annotationType.shortReadableName()), annotationRetention, new String(containerAnnotationType.shortReadableName()), containerRetention}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void repeatableAnnotationTypeTargetMismatch(ASTNode markerNode, ReferenceBinding annotationType, ReferenceBinding containerAnnotationType, String unmetTargets) {
        this.handle(16778122, new String[]{new String(annotationType.readableName()), new String(containerAnnotationType.readableName()), unmetTargets}, new String[]{new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName()), unmetTargets}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void repeatableAnnotationTypeIsDocumented(ASTNode markerNode, ReferenceBinding annotationType, ReferenceBinding containerAnnotationType) {
        this.handle(16778123, new String[]{new String(annotationType.readableName()), new String(containerAnnotationType.readableName())}, new String[]{new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName())}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void repeatableAnnotationTypeIsInherited(ASTNode markerNode, ReferenceBinding annotationType, ReferenceBinding containerAnnotationType) {
        this.handle(16778124, new String[]{new String(annotationType.readableName()), new String(containerAnnotationType.readableName())}, new String[]{new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName())}, markerNode.sourceStart, markerNode.sourceEnd);
    }

    public void repeatableAnnotationWithRepeatingContainer(Annotation annotation, ReferenceBinding containerType) {
        this.handle(16778125, new String[]{new String(annotation.resolvedType.readableName()), new String(containerType.readableName())}, new String[]{new String(annotation.resolvedType.shortReadableName()), new String(containerType.shortReadableName())}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void reset() {
        this.positionScanner = null;
    }

    public void resourceHasToImplementAutoCloseable(TypeBinding binding, ASTNode reference) {
        if (this.options.sourceLevel < 0x330000L) {
            return;
        }
        this.handle(16778087, new String[]{new String(binding.readableName())}, new String[]{new String(binding.shortReadableName())}, reference.sourceStart, reference.sourceEnd);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private int retrieveClosingAngleBracketPosition(int start) {
        if (this.referenceContext == null) {
            return start;
        }
        CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return start;
        }
        ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return start;
        }
        char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return start;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false, this.options.enablePreviewFeatures);
            this.positionScanner.returnOnlyGreater = true;
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(start, contents.length);
        int end = start;
        int count = 0;
        try {
            int token;
            while ((token = this.positionScanner.getNextToken()) != 64) {
                switch (token) {
                    case 11: {
                        ++count;
                        break;
                    }
                    case 15: {
                        if (--count != 0) break;
                        return this.positionScanner.currentPosition - 1;
                    }
                    case 38: {
                        return end;
                    }
                }
            }
            return end;
        }
        catch (InvalidInputException invalidInputException) {}
        return end;
    }

    private int retrieveEndingPositionAfterOpeningParenthesis(int sourceStart, int sourceEnd, int numberOfParen) {
        if (this.referenceContext == null) {
            return sourceEnd;
        }
        CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return sourceEnd;
        }
        ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return sourceEnd;
        }
        char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return sourceEnd;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false, this.options.enablePreviewFeatures);
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(sourceStart, sourceEnd);
        try {
            int token;
            int previousSourceEnd = sourceEnd;
            while ((token = this.positionScanner.getNextToken()) != 64) {
                switch (token) {
                    case 26: {
                        return previousSourceEnd;
                    }
                }
                previousSourceEnd = this.positionScanner.currentPosition - 1;
            }
        }
        catch (InvalidInputException invalidInputException) {}
        return sourceEnd;
    }

    private int retrieveStartingPositionAfterOpeningParenthesis(int sourceStart, int sourceEnd, int numberOfParen) {
        if (this.referenceContext == null) {
            return sourceStart;
        }
        CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return sourceStart;
        }
        ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return sourceStart;
        }
        char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return sourceStart;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false, this.options.enablePreviewFeatures);
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(sourceStart, sourceEnd);
        int count = 0;
        try {
            int token;
            while ((token = this.positionScanner.getNextToken()) != 64) {
                switch (token) {
                    case 23: {
                        if (++count != numberOfParen) break;
                        this.positionScanner.getNextToken();
                        return this.positionScanner.startPosition;
                    }
                }
            }
        }
        catch (InvalidInputException invalidInputException) {}
        return sourceStart;
    }

    public void scannerError(Parser parser, String errorTokenName) {
        String[] stringArray;
        char[] source;
        Scanner scanner = parser.scanner;
        int flag = 1610612941;
        int startPos = scanner.startPosition;
        int endPos = scanner.currentPosition - 1;
        if (errorTokenName.equals("End_Of_Source")) {
            flag = 1610612986;
        } else if (errorTokenName.equals("Invalid_Hexa_Literal")) {
            flag = 1610612987;
        } else if (errorTokenName.equals("Illegal_Hexa_Literal")) {
            flag = 1610613006;
        } else if (errorTokenName.equals("Invalid_Octal_Literal")) {
            flag = 1610612988;
        } else if (errorTokenName.equals("Invalid_Character_Constant")) {
            flag = 1610612989;
        } else if (errorTokenName.equals("Invalid_Escape")) {
            flag = 1610612990;
        } else if (errorTokenName.equals("Invalid_Unicode_Escape")) {
            flag = 0x60000100;
            int checkPos = scanner.currentPosition - 1;
            source = scanner.source;
            if (checkPos >= source.length) {
                checkPos = source.length - 1;
            }
            while (checkPos >= startPos) {
                if (source[checkPos] == '\\') break;
                --checkPos;
            }
            startPos = checkPos;
        } else if (errorTokenName.equals("Invalid_Low_Surrogate")) {
            flag = 1610612999;
        } else if (errorTokenName.equals("Invalid_High_Surrogate")) {
            flag = 1610613000;
            source = scanner.source;
            int checkPos = scanner.startPosition + 1;
            while (checkPos <= endPos) {
                if (source[checkPos] == '\\') break;
                ++checkPos;
            }
            endPos = checkPos - 1;
        } else if (errorTokenName.equals("Invalid_Float_Literal")) {
            flag = 0x60000101;
        } else if (errorTokenName.equals("Unterminated_String")) {
            flag = 1610612995;
        } else if (errorTokenName.equals("Unterminated_Text_Block")) {
            flag = 0x200110;
        } else if (errorTokenName.equals("Unterminated_Comment")) {
            flag = 1610612996;
        } else if (errorTokenName.equals("Invalid_Char_In_String")) {
            flag = 1610612995;
        } else if (errorTokenName.equals("Invalid_Digit")) {
            flag = 0x60000106;
        } else if (errorTokenName.equals("Invalid_Binary_Literal")) {
            flag = 1610613002;
        } else if (errorTokenName.equals("Binary_Literal_Not_Below_17")) {
            flag = 1610613003;
        } else if (errorTokenName.equals("Invalid_Underscore")) {
            flag = 1610613004;
        } else if (errorTokenName.equals("Underscores_In_Literals_Not_Below_17")) {
            flag = 1610613005;
        }
        if (flag == 1610612941) {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = errorTokenName;
        } else {
            stringArray = NoArgument;
        }
        String[] arguments = stringArray;
        this.handle(flag, arguments, arguments, startPos, endPos, parser.compilationUnit.compilationResult);
    }

    public void shouldImplementHashcode(SourceTypeBinding type) {
        this.handle(16777548, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, type.sourceStart(), type.sourceEnd());
    }

    public void shouldReturn(TypeBinding returnType, ASTNode location) {
        int sourceStart = location.sourceStart;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LambdaExpression) {
            LambdaExpression exp = (LambdaExpression)location;
            sourceStart = exp.sourceStart;
            sourceEnd = exp.diagnosticsSourceEnd();
        }
        this.handle(this.methodHasMissingSwitchDefault() ? 0x4000303 : 603979884, new String[]{new String(returnType.readableName())}, new String[]{new String(returnType.shortReadableName())}, sourceStart, sourceEnd);
    }

    public void signalNoImplicitStringConversionForCharArrayExpression(Expression expression) {
        this.handle(536871063, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void staticAndInstanceConflict(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        if (currentMethod.isStatic()) {
            this.handle(67109271, new String[]{new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(inheritedMethod.declaringClass.shortReadableName())}, currentMethod.sourceStart(), currentMethod.sourceEnd());
        } else {
            this.handle(67109270, new String[]{new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(inheritedMethod.declaringClass.shortReadableName())}, currentMethod.sourceStart(), currentMethod.sourceEnd());
        }
    }

    public void staticFieldAccessToNonStaticVariable(ASTNode location, FieldBinding field) {
        String[] arguments = new String[]{new String(field.readableName())};
        this.handle(33554506, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void staticInheritedMethodConflicts(SourceTypeBinding type, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
        this.handle(67109272, new String[]{new String(concreteMethod.readableName()), new String(abstractMethods[0].declaringClass.readableName())}, new String[]{new String(concreteMethod.readableName()), new String(abstractMethods[0].declaringClass.shortReadableName())}, type.sourceStart(), type.sourceEnd());
    }

    public void staticMemberOfParameterizedType(ASTNode location, ReferenceBinding type, ReferenceBinding qualifyingType, int index) {
        if (location == null) {
            this.handle(16777779, new String[]{new String(type.readableName()), new String(type.enclosingType().readableName())}, new String[]{new String(type.shortReadableName()), new String(type.enclosingType().shortReadableName())}, 131, 0, 0);
            return;
        }
        this.handle(16777779, new String[]{new String(type.readableName()), new String(qualifyingType.readableName())}, new String[]{new String(type.shortReadableName()), new String(qualifyingType.shortReadableName())}, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }

    public void stringConstantIsExceedingUtf8Limit(ASTNode location) {
        this.handle(536871064, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void superclassMustBeAClass(SourceTypeBinding type, TypeReference superclassRef, ReferenceBinding superType) {
        this.handle(16777528, new String[]{new String(superType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(type.sourceName())}, superclassRef.sourceStart, superclassRef.sourceEnd);
    }

    public void superfluousSemicolon(int sourceStart, int sourceEnd) {
        this.handle(536871092, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void superinterfaceMustBeAnInterface(SourceTypeBinding type, TypeReference superInterfaceRef, ReferenceBinding superType) {
        this.handle(16777531, new String[]{new String(superType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(type.sourceName())}, superInterfaceRef.sourceStart, superInterfaceRef.sourceEnd);
    }

    public void superinterfacesCollide(TypeBinding type, ASTNode decl, TypeBinding superType, TypeBinding inheritedSuperType) {
        this.handle(16777755, new String[]{new String(superType.readableName()), new String(inheritedSuperType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(inheritedSuperType.shortReadableName()), new String(type.sourceName())}, decl.sourceStart, decl.sourceEnd);
    }

    public void superTypeCannotUseWildcard(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777772, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, superclass.sourceStart, superclass.sourceEnd);
    }

    private boolean handleSyntaxErrorOnNewTokens(int id, int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        if (this.isIdentifier(currentKind)) {
            return this.validateRestrictedKeywords(errorTokenSource, expectedToken, start, end, true);
        }
        return false;
    }

    private void handleSyntaxError(int id, int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken) {
        if (!this.handleSyntaxErrorOnNewTokens(0x600000CC, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken)) {
            this.syntaxError(0x600000CC, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
        }
    }

    private void syntaxError(int id, int startPosition, int endPosition, int currentKind, char[] currentTokenSource, String errorTokenName, String expectedToken) {
        String[] arguments;
        if (currentKind == 36 && expectedToken != null && expectedToken.equals("@")) {
            return;
        }
        String eTokenName = this.isKeyword(currentKind) || this.isLiteral(currentKind) || this.isIdentifier(currentKind) ? new String(currentTokenSource) : errorTokenName;
        if (this.isRestrictedIdentifier(currentKind)) {
            eTokenName = this.replaceIfSynthetic(eTokenName);
        }
        if (expectedToken != null) {
            expectedToken = this.replaceIfSynthetic(expectedToken);
            arguments = new String[]{eTokenName, expectedToken};
        } else {
            arguments = new String[]{eTokenName};
        }
        this.handle(id, arguments, arguments, startPosition, endPosition);
    }

    private String replaceIfSynthetic(String token) {
        if (token.equals("BeginTypeArguments")) {
            return ".";
        }
        if (token.equals("BeginLambda")) {
            return "(";
        }
        if (token.equals("RestrictedIdentifierYield")) {
            return "yield";
        }
        if (token.equals(RESTRICTED_IDENTIFIER_RECORD)) {
            return RECORD;
        }
        if (token.equals(RESTRICTED_IDENTIFIER_SEALED)) {
            return SEALED;
        }
        if (token.equals(RESTRICTED_IDENTIFIER_PERMITS)) {
            return PERMITS;
        }
        return token;
    }

    public void task(String tag, String message, String priority, int start, int end) {
        this.handle(536871362, new String[]{tag, message, priority}, new String[]{tag, message, priority}, start, end);
    }

    public void tooManyDimensions(ASTNode expression) {
        this.handle(0x20000044, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void tooManyFields(TypeDeclaration typeDeclaration) {
        this.handle(536871344, new String[]{new String(typeDeclaration.binding.readableName())}, new String[]{new String(typeDeclaration.binding.shortReadableName())}, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void tooManyMethods(TypeDeclaration typeDeclaration) {
        this.handle(536871345, new String[]{new String(typeDeclaration.binding.readableName())}, new String[]{new String(typeDeclaration.binding.shortReadableName())}, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }

    public void tooManyParametersForSyntheticMethod(AbstractMethodDeclaration method) {
        MethodBinding binding = method.binding;
        String selector = null;
        selector = binding.isConstructor() ? new String(binding.declaringClass.sourceName()) : new String(method.selector);
        this.handle(536871346, new String[]{selector, this.typesAsString(binding, false), new String(binding.declaringClass.readableName())}, new String[]{selector, this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName())}, 145, method.sourceStart, method.sourceEnd);
    }

    public void typeCastError(CastExpression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777372, new String[]{rightName, leftName}, new String[]{rightShortName, leftShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void unsafeCastInInstanceof(Expression expression, TypeBinding leftType, TypeBinding rightType) {
        String rightShortName;
        String leftName = new String(leftType.readableName());
        String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        if (leftShortName.equals(rightShortName = new String(rightType.shortReadableName()))) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777428, new String[]{rightName, leftName}, new String[]{rightShortName, leftShortName}, expression.sourceStart, expression.sourceEnd);
    }

    public void typeCollidesWithEnclosingType(TypeDeclaration typeDecl) {
        String[] arguments = new String[]{new String(typeDecl.name)};
        this.handle(16777534, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void typeCollidesWithPackage(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
        this.referenceContext = typeDecl;
        String[] arguments = new String[]{new String(compUnitDecl.getFileName()), new String(typeDecl.name)};
        this.handle(16777538, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd, compUnitDecl.compilationResult);
    }

    public void typeHiding(TypeDeclaration typeDecl, TypeBinding hiddenType) {
        int severity = this.computeSeverity(0x1000021);
        if (severity == 256) {
            return;
        }
        this.handle(0x1000021, new String[]{new String(typeDecl.name), new String(hiddenType.shortReadableName())}, new String[]{new String(typeDecl.name), new String(hiddenType.readableName())}, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void typeHiding(TypeDeclaration typeDecl, TypeVariableBinding hiddenTypeParameter) {
        int severity = this.computeSeverity(16777792);
        if (severity == 256) {
            return;
        }
        if (hiddenTypeParameter.declaringElement instanceof TypeBinding) {
            TypeBinding declaringType = (TypeBinding)hiddenTypeParameter.declaringElement;
            this.handle(16777792, new String[]{new String(typeDecl.name), new String(hiddenTypeParameter.readableName()), new String(declaringType.readableName())}, new String[]{new String(typeDecl.name), new String(hiddenTypeParameter.shortReadableName()), new String(declaringType.shortReadableName())}, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
        } else {
            MethodBinding declaringMethod = (MethodBinding)hiddenTypeParameter.declaringElement;
            this.handle(16777793, new String[]{new String(typeDecl.name), new String(hiddenTypeParameter.readableName()), new String(declaringMethod.selector), this.typesAsString(declaringMethod, false), new String(declaringMethod.declaringClass.readableName())}, new String[]{new String(typeDecl.name), new String(hiddenTypeParameter.shortReadableName()), new String(declaringMethod.selector), this.typesAsString(declaringMethod, true), new String(declaringMethod.declaringClass.shortReadableName())}, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
        }
    }

    public void typeHiding(TypeParameter typeParam, Binding hidden) {
        int severity = this.computeSeverity(16777787);
        if (severity == 256) {
            return;
        }
        TypeBinding hiddenType = (TypeBinding)hidden;
        this.handle(16777787, new String[]{new String(typeParam.name), new String(hiddenType.readableName())}, new String[]{new String(typeParam.name), new String(hiddenType.shortReadableName())}, severity, typeParam.sourceStart, typeParam.sourceEnd);
    }

    public void notAnnotationType(TypeBinding actualType, ASTNode location) {
        this.handle(0x1000022, new String[]{new String(actualType.leafComponentType().readableName())}, new String[]{new String(actualType.leafComponentType().shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void typeMismatchError(TypeBinding actualType, TypeBinding expectedType, ASTNode location, ASTNode expectingLocation) {
        if (this.options.sourceLevel < 0x310000L) {
            if (actualType instanceof TypeVariableBinding) {
                actualType = actualType.erasure();
            }
            if (expectedType instanceof TypeVariableBinding) {
                expectedType = expectedType.erasure();
            }
        }
        if (actualType != null && (actualType.tagBits & 0x80L) != 0L) {
            if (location instanceof Annotation) {
                return;
            }
            this.handle(0x1000002, new String[]{new String(actualType.leafComponentType().readableName())}, new String[]{new String(actualType.leafComponentType().shortReadableName())}, location.sourceStart, location.sourceEnd);
            return;
        }
        if (expectingLocation != null && (expectedType.tagBits & 0x80L) != 0L) {
            this.handle(0x1000002, new String[]{new String(expectedType.leafComponentType().readableName())}, new String[]{new String(expectedType.leafComponentType().shortReadableName())}, expectingLocation.sourceStart, expectingLocation.sourceEnd);
            return;
        }
        char[] actualShortReadableName = actualType.shortReadableName();
        char[] expectedShortReadableName = expectedType.shortReadableName();
        char[] actualReadableName = actualType.readableName();
        char[] expectedReadableName = expectedType.readableName();
        if (CharOperation.equals(actualShortReadableName, expectedShortReadableName)) {
            if (CharOperation.equals(actualReadableName, expectedReadableName)) {
                actualReadableName = actualType.nullAnnotatedReadableName(this.options, false);
                expectedReadableName = expectedType.nullAnnotatedReadableName(this.options, false);
                actualShortReadableName = actualType.nullAnnotatedReadableName(this.options, true);
                expectedShortReadableName = expectedType.nullAnnotatedReadableName(this.options, true);
            } else {
                actualShortReadableName = actualReadableName;
                expectedShortReadableName = expectedReadableName;
            }
        }
        this.handle(expectingLocation instanceof ReturnStatement ? 0x1000013 : 0x1000011, new String[]{new String(actualReadableName), new String(expectedReadableName)}, new String[]{new String(actualShortReadableName), new String(expectedShortReadableName)}, location.sourceStart, location.sourceEnd);
    }

    public void typeMismatchError(TypeBinding typeArgument, TypeVariableBinding typeParameter, ReferenceBinding genericType, ASTNode location) {
        if (location == null) {
            this.handle(16777742, new String[]{new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, 131, 0, 0);
            return;
        }
        this.handle(16777742, new String[]{new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false)}, new String[]{new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true)}, location.sourceStart, location.sourceEnd);
    }

    private String typesAsString(MethodBinding methodBinding, boolean makeShort) {
        return this.typesAsString(methodBinding, methodBinding.parameters, makeShort);
    }

    private String typesAsString(MethodBinding methodBinding, TypeBinding[] parameters, boolean makeShort) {
        return this.typesAsString(methodBinding, parameters, makeShort, false);
    }

    private String typesAsString(MethodBinding methodBinding, boolean makeShort, boolean showNullAnnotations) {
        return this.typesAsString(methodBinding, methodBinding.parameters, makeShort, showNullAnnotations);
    }

    private String typesAsString(MethodBinding methodBinding, TypeBinding[] parameters, boolean makeShort, boolean showNullAnnotations) {
        if (methodBinding.isPolymorphic()) {
            TypeBinding[] types = methodBinding.original().parameters;
            StringBuffer buffer = new StringBuffer(10);
            int i = 0;
            int length = types.length;
            while (i < length) {
                boolean isVarargType;
                if (i != 0) {
                    buffer.append(", ");
                }
                TypeBinding type = types[i];
                boolean bl = isVarargType = i == length - 1;
                if (isVarargType) {
                    type = ((ArrayBinding)type).elementsType();
                }
                if (showNullAnnotations) {
                    buffer.append(new String(type.nullAnnotatedReadableName(this.options, makeShort)));
                } else {
                    buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
                }
                if (isVarargType) {
                    buffer.append("...");
                }
                ++i;
            }
            return buffer.toString();
        }
        StringBuffer buffer = new StringBuffer(10);
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            boolean isVarargType;
            if (i != 0) {
                buffer.append(", ");
            }
            TypeBinding type = parameters[i];
            boolean bl = isVarargType = methodBinding.isVarargs() && i == length - 1;
            if (isVarargType) {
                type = ((ArrayBinding)type).elementsType();
            }
            if (showNullAnnotations) {
                buffer.append(new String(type.nullAnnotatedReadableName(this.options, makeShort)));
            } else {
                buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
            }
            if (isVarargType) {
                buffer.append("...");
            }
            ++i;
        }
        return buffer.toString();
    }

    private String typesAsString(TypeBinding[] types, boolean makeShort) {
        return this.typesAsString(types, makeShort, false);
    }

    private String typesAsString(TypeBinding[] types, boolean makeShort, boolean showNullAnnotations) {
        StringBuffer buffer = new StringBuffer(10);
        int i = 0;
        int length = types.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
            }
            TypeBinding type = types[i];
            if (showNullAnnotations) {
                buffer.append(new String(type.nullAnnotatedReadableName(this.options, makeShort)));
            } else {
                buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
            }
            ++i;
        }
        return buffer.toString();
    }

    public void undefinedAnnotationValue(TypeBinding annotationType, MemberValuePair memberValuePair) {
        if (this.isRecoveredName(memberValuePair.name)) {
            return;
        }
        String name = new String(memberValuePair.name);
        this.handle(67109475, new String[]{name, new String(annotationType.readableName())}, new String[]{name, new String(annotationType.shortReadableName())}, memberValuePair.sourceStart, memberValuePair.sourceEnd);
    }

    public void undefinedLabel(BranchStatement statement) {
        if (this.isRecoveredName(statement.label)) {
            return;
        }
        String[] arguments = new String[]{new String(statement.label)};
        this.handle(536871086, arguments, arguments, statement.sourceStart, statement.sourceEnd);
    }

    public void undefinedTypeVariableSignature(char[] variableName, ReferenceBinding binaryType) {
        this.handle(536871450, new String[]{new String(variableName), new String(binaryType.readableName())}, new String[]{new String(variableName), new String(binaryType.shortReadableName())}, 131, 0, 0);
    }

    public void undocumentedEmptyBlock(int blockStart, int blockEnd) {
        this.handle(536871372, NoArgument, NoArgument, blockStart, blockEnd);
    }

    public void unexpectedStaticModifierForField(SourceTypeBinding type, FieldDeclaration fieldDecl) {
        String[] arguments = new String[]{new String(fieldDecl.name)};
        this.handle(33554778, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }

    public void unexpectedStaticModifierForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
        String[] arguments = new String[]{new String(type.sourceName()), new String(methodDecl.selector)};
        this.handle(67109225, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void unhandledException(TypeBinding exceptionType, ASTNode location) {
        boolean insideDefaultConstructor = this.referenceContext instanceof ConstructorDeclaration && ((ConstructorDeclaration)this.referenceContext).isDefaultConstructor();
        boolean insideImplicitConstructorCall = location instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)location).accessMode == 1;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LocalDeclaration) {
            sourceEnd = ((LocalDeclaration)location).declarationEnd;
        }
        this.handle(insideDefaultConstructor ? 16777362 : (insideImplicitConstructorCall ? 0x800008F : 16777384), new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, sourceEnd);
    }

    public void unhandledExceptionFromAutoClose(TypeBinding exceptionType, ASTNode location) {
        Binding binding = null;
        if (location instanceof LocalDeclaration) {
            binding = ((LocalDeclaration)location).binding;
        } else if (location instanceof NameReference) {
            binding = ((NameReference)location).binding;
        } else if (location instanceof FieldReference) {
            binding = ((FieldReference)location).binding;
        }
        if (binding != null) {
            this.handle(16778098, new String[]{new String(exceptionType.readableName()), new String(binding.readableName())}, new String[]{new String(exceptionType.shortReadableName()), new String(binding.shortReadableName())}, location.sourceStart, location.sourceEnd);
        }
    }

    public void unhandledWarningToken(Expression token) {
        String[] arguments = new String[]{token.constant.stringValue()};
        this.handle(0x20000277, arguments, arguments, token.sourceStart, token.sourceEnd);
    }

    public void uninitializedBlankFinalField(FieldBinding field, ASTNode location) {
        String[] arguments = new String[]{new String(field.readableName())};
        this.handle(this.methodHasMissingSwitchDefault() ? 0x2000302 : 33554513, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void uninitializedNonNullField(FieldBinding field, ASTNode location) {
        char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        if (!field.isNonNull()) {
            String[] arguments = new String[]{new String(field.readableName()), new String(field.type.readableName()), new String(nonNullAnnotationName[nonNullAnnotationName.length - 1])};
            this.handle(this.methodHasMissingSwitchDefault() ? 978 : 977, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
            return;
        }
        String[] arguments = new String[]{new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(field.readableName())};
        this.handle(this.methodHasMissingSwitchDefault() ? 33555367 : 33555366, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void uninitializedLocalVariable(LocalVariableBinding binding, ASTNode location, Scope scope) {
        binding.markAsUninitializedIn(scope);
        String[] arguments = new String[]{new String(binding.readableName())};
        this.handle(this.methodHasMissingSwitchDefault() ? 536871681 : 0x20000033, arguments, arguments, this.nodeSourceStart(binding, location), this.nodeSourceEnd(binding, location));
    }

    private boolean methodHasMissingSwitchDefault() {
        MethodScope methodScope = null;
        if (this.referenceContext instanceof Block) {
            methodScope = ((Block)((Object)this.referenceContext)).scope.methodScope();
        } else if (this.referenceContext instanceof AbstractMethodDeclaration) {
            methodScope = ((AbstractMethodDeclaration)this.referenceContext).scope;
        }
        return methodScope != null && methodScope.hasMissingSwitchDefault;
    }

    public void unmatchedBracket(int position, ReferenceContext context, CompilationResult compilationResult) {
        this.handle(1610612956, NoArgument, NoArgument, position, position, context, compilationResult);
    }

    public void unnecessaryCast(CastExpression castExpression) {
        if (castExpression.expression instanceof FunctionalExpression) {
            return;
        }
        int severity = this.computeSeverity(553648309);
        if (severity == 256) {
            return;
        }
        TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        this.handle(553648309, new String[]{new String(castedExpressionType.readableName()), new String(castExpression.type.resolvedType.readableName())}, new String[]{new String(castedExpressionType.shortReadableName()), new String(castExpression.type.resolvedType.shortReadableName())}, severity, castExpression.sourceStart, castExpression.sourceEnd);
    }

    public void unnecessaryElse(ASTNode location) {
        this.handle(536871101, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void unnecessaryEnclosingInstanceSpecification(Expression expression, ReferenceBinding targetType) {
        this.handle(0x1000017, new String[]{new String(targetType.readableName())}, new String[]{new String(targetType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void unnecessaryInstanceof(InstanceOfExpression instanceofExpression, TypeBinding checkType) {
        int severity = this.computeSeverity(553648311);
        if (severity == 256) {
            return;
        }
        TypeBinding expressionType = instanceofExpression.expression.resolvedType;
        this.handle(553648311, new String[]{new String(expressionType.readableName()), new String(checkType.readableName())}, new String[]{new String(expressionType.shortReadableName()), new String(checkType.shortReadableName())}, severity, instanceofExpression.sourceStart, instanceofExpression.sourceEnd);
    }

    public void unnecessaryNLSTags(int sourceStart, int sourceEnd) {
        this.handle(536871177, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void unnecessaryTypeArgumentsForMethodInvocation(MethodBinding method, TypeBinding[] genericTypeArguments, TypeReference[] typeArguments) {
        String methodName = method.isConstructor() ? new String(method.declaringClass.shortReadableName()) : new String(method.selector);
        this.handle(method.isConstructor() ? 67109524 : 67109443, new String[]{methodName, this.typesAsString(method, false), new String(method.declaringClass.readableName()), this.typesAsString(genericTypeArguments, false)}, new String[]{methodName, this.typesAsString(method, true), new String(method.declaringClass.shortReadableName()), this.typesAsString(genericTypeArguments, true)}, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }

    public void unqualifiedFieldAccess(NameReference reference, FieldBinding field) {
        int sourceStart = reference.sourceStart;
        int sourceEnd = reference.sourceEnd;
        if (reference instanceof SingleNameReference) {
            int numberOfParens = (reference.bits & 0x1FE00000) >> 21;
            if (numberOfParens != 0) {
                sourceStart = this.retrieveStartingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
                sourceEnd = this.retrieveEndingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
            } else {
                sourceStart = this.nodeSourceStart(field, reference);
                sourceEnd = this.nodeSourceEnd(field, reference);
            }
        } else {
            sourceStart = this.nodeSourceStart(field, reference);
            sourceEnd = this.nodeSourceEnd(field, reference);
        }
        this.handle(570425423, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, sourceStart, sourceEnd);
    }

    public void unreachableCatchBlock(ReferenceBinding exceptionType, ASTNode location) {
        this.handle(83886247, new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void unreachableCode(Statement statement) {
        int statemendEnd;
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof LocalDeclaration) {
            LocalDeclaration declaration = (LocalDeclaration)statement;
            sourceStart = declaration.declarationSourceStart;
            sourceEnd = declaration.declarationSourceEnd;
        } else if (statement instanceof Expression && ((Expression)statement).isTrulyExpression() && (statemendEnd = ((Expression)statement).statementEnd) != -1) {
            sourceEnd = statemendEnd;
        }
        this.handle(536871073, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void unresolvableReference(NameReference nameRef, Binding binding) {
        NameReference ref;
        String[] arguments = new String[]{new String(binding.readableName())};
        int end = nameRef.sourceEnd;
        int sourceStart = nameRef.sourceStart;
        if (nameRef instanceof QualifiedNameReference) {
            ref = (QualifiedNameReference)nameRef;
            if (this.isRecoveredName(ref.tokens)) {
                return;
            }
            if (ref.indexOfFirstFieldBinding >= 1) {
                end = (int)ref.sourcePositions[ref.indexOfFirstFieldBinding - 1];
            }
        } else {
            ref = (SingleNameReference)nameRef;
            if (this.isRecoveredName(((SingleNameReference)ref).token)) {
                return;
            }
            int numberOfParens = (((SingleNameReference)ref).bits & 0x1FE00000) >> 21;
            if (numberOfParens != 0) {
                sourceStart = this.retrieveStartingPositionAfterOpeningParenthesis(sourceStart, end, numberOfParens);
                end = this.retrieveEndingPositionAfterOpeningParenthesis(sourceStart, end, numberOfParens);
            }
        }
        int problemId = (nameRef.bits & 3) != 0 && (nameRef.bits & 4) == 0 ? 33554515 : 0x22000032;
        this.handle(problemId, arguments, arguments, sourceStart, end);
    }

    public void unsafeCast(CastExpression castExpression, Scope scope) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(0x1000221);
        if (severity == 256) {
            return;
        }
        TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        TypeBinding castExpressionResolvedType = castExpression.resolvedType;
        this.handle(0x1000221, new String[]{new String(castedExpressionType.readableName()), new String(castExpressionResolvedType.readableName())}, new String[]{new String(castedExpressionType.shortReadableName()), new String(castExpressionResolvedType.shortReadableName())}, severity, castExpression.sourceStart, castExpression.sourceEnd);
    }

    public void unsafeNullnessCast(CastExpression castExpression, Scope scope) {
        TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        TypeBinding castExpressionResolvedType = castExpression.resolvedType;
        this.handle(536871879, new String[]{new String(castedExpressionType.nullAnnotatedReadableName(this.options, false)), new String(castExpressionResolvedType.nullAnnotatedReadableName(this.options, false))}, new String[]{new String(castedExpressionType.nullAnnotatedReadableName(this.options, true)), new String(castExpressionResolvedType.nullAnnotatedReadableName(this.options, true))}, castExpression.sourceStart, castExpression.sourceEnd);
    }

    public void unsafeGenericArrayForVarargs(TypeBinding leafComponentType, ASTNode location) {
        int severity = this.computeSeverity(67109438);
        if (severity == 256) {
            return;
        }
        this.handle(67109438, new String[]{new String(leafComponentType.readableName())}, new String[]{new String(leafComponentType.shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
    }

    public void unsafeRawFieldAssignment(FieldBinding field, TypeBinding expressionType, ASTNode location) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(16777752);
        if (severity == 256) {
            return;
        }
        this.handle(16777752, new String[]{new String(expressionType.readableName()), new String(field.name), new String(field.declaringClass.readableName()), new String(field.declaringClass.erasure().readableName())}, new String[]{new String(expressionType.shortReadableName()), new String(field.name), new String(field.declaringClass.shortReadableName()), new String(field.declaringClass.erasure().shortReadableName())}, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }

    public void unsafeRawGenericMethodInvocation(ASTNode location, MethodBinding rawMethod, TypeBinding[] argumentTypes) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        boolean isConstructor = rawMethod.isConstructor();
        int severity = this.computeSeverity(isConstructor ? 16777785 : 16777786);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(16777785, new String[]{new String(rawMethod.declaringClass.sourceName()), this.typesAsString(rawMethod.original(), false), new String(rawMethod.declaringClass.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(rawMethod.declaringClass.sourceName()), this.typesAsString(rawMethod.original(), true), new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(argumentTypes, true)}, severity, location.sourceStart, location.sourceEnd);
        } else {
            this.handle(16777786, new String[]{new String(rawMethod.selector), this.typesAsString(rawMethod.original(), false), new String(rawMethod.declaringClass.readableName()), this.typesAsString(argumentTypes, false)}, new String[]{new String(rawMethod.selector), this.typesAsString(rawMethod.original(), true), new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(argumentTypes, true)}, severity, location.sourceStart, location.sourceEnd);
        }
    }

    public void unsafeRawInvocation(ASTNode location, MethodBinding rawMethod) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        boolean isConstructor = rawMethod.isConstructor();
        int severity = this.computeSeverity(isConstructor ? 0x1000212 : 16777747);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(0x1000212, new String[]{new String(rawMethod.declaringClass.readableName()), this.typesAsString(rawMethod.original(), rawMethod.parameters, false), new String(rawMethod.declaringClass.erasure().readableName())}, new String[]{new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(rawMethod.original(), rawMethod.parameters, true), new String(rawMethod.declaringClass.erasure().shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
        } else {
            this.handle(16777747, new String[]{new String(rawMethod.selector), this.typesAsString(rawMethod.original(), rawMethod.parameters, false), new String(rawMethod.declaringClass.readableName()), new String(rawMethod.declaringClass.erasure().readableName())}, new String[]{new String(rawMethod.selector), this.typesAsString(rawMethod.original(), rawMethod.parameters, true), new String(rawMethod.declaringClass.shortReadableName()), new String(rawMethod.declaringClass.erasure().shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
        }
    }

    public void unsafeReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod, SourceTypeBinding type) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(67109423);
        if (severity == 256) {
            return;
        }
        int start = type.sourceStart();
        int end = type.sourceEnd();
        if (TypeBinding.equalsEquals(currentMethod.declaringClass, type)) {
            TypeReference location = ((MethodDeclaration)currentMethod.sourceMethod()).returnType;
            start = location.sourceStart();
            end = location.sourceEnd();
        }
        this.handle(67109423, new String[]{new String(currentMethod.returnType.readableName()), new String(currentMethod.selector), this.typesAsString(currentMethod.original(), false), new String(currentMethod.declaringClass.readableName()), new String(inheritedMethod.returnType.readableName()), new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(currentMethod.returnType.shortReadableName()), new String(currentMethod.selector), this.typesAsString(currentMethod.original(), true), new String(currentMethod.declaringClass.shortReadableName()), new String(inheritedMethod.returnType.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName())}, severity, start, end);
    }

    public void unsafeTypeConversion(Expression expression, TypeBinding expressionType, TypeBinding expectedType) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(16777748);
        if (severity == 256) {
            return;
        }
        if (!this.options.reportUnavoidableGenericTypeProblems && expression.forcedToBeRaw(this.referenceContext)) {
            return;
        }
        this.handle(16777748, new String[]{new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName())}, new String[]{new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName())}, severity, expression.sourceStart, expression.sourceEnd);
    }

    public void unsafeElementTypeConversion(Expression expression, TypeBinding expressionType, TypeBinding expectedType) {
        if (this.options.sourceLevel < 0x310000L) {
            return;
        }
        int severity = this.computeSeverity(16777801);
        if (severity == 256) {
            return;
        }
        if (!this.options.reportUnavoidableGenericTypeProblems && expression.forcedToBeRaw(this.referenceContext)) {
            return;
        }
        this.handle(16777801, new String[]{new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName())}, new String[]{new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName())}, severity, expression.sourceStart, expression.sourceEnd);
    }

    public void unusedArgument(LocalDeclaration localDecl) {
        int severity = this.computeSeverity(536870974);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(localDecl.name)};
        this.handle(536870974, arguments, arguments, severity, localDecl.sourceStart, localDecl.sourceEnd);
    }

    public void unusedExceptionParameter(LocalDeclaration exceptionParameter) {
        int severity = this.computeSeverity(0x20000055);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(exceptionParameter.name)};
        this.handle(0x20000055, arguments, arguments, severity, exceptionParameter.sourceStart, exceptionParameter.sourceEnd);
    }

    public void unusedDeclaredThrownException(ReferenceBinding exceptionType, AbstractMethodDeclaration method, ASTNode location) {
        boolean isConstructor = method.isConstructor();
        int severity = this.computeSeverity(isConstructor ? 536871098 : 536871097);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(536871098, new String[]{new String(method.binding.declaringClass.readableName()), this.typesAsString(method.binding, false), new String(exceptionType.readableName())}, new String[]{new String(method.binding.declaringClass.shortReadableName()), this.typesAsString(method.binding, true), new String(exceptionType.shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
        } else {
            this.handle(536871097, new String[]{new String(method.binding.declaringClass.readableName()), new String(method.selector), this.typesAsString(method.binding, false), new String(exceptionType.readableName())}, new String[]{new String(method.binding.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method.binding, true), new String(exceptionType.shortReadableName())}, severity, location.sourceStart, location.sourceEnd);
        }
    }

    public void unusedImport(ImportReference importRef) {
        int severity = this.computeSeverity(268435844);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{CharOperation.toString(importRef.tokens)};
        this.handle(268435844, arguments, arguments, severity, importRef.sourceStart, importRef.sourceEnd);
    }

    public void unusedLabel(LabeledStatement statement) {
        int severity = this.computeSeverity(536871111);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(statement.label)};
        this.handle(536871111, arguments, arguments, severity, statement.sourceStart, statement.labelEnd);
    }

    public void unusedLocalVariable(LocalDeclaration localDecl) {
        int severity = this.computeSeverity(536870973);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(localDecl.name)};
        this.handle(536870973, arguments, arguments, severity, localDecl.sourceStart, localDecl.sourceEnd);
    }

    public void unusedObjectAllocation(AllocationExpression allocationExpression) {
        this.handle(536871060, NoArgument, NoArgument, allocationExpression.sourceStart, allocationExpression.sourceEnd);
    }

    public void unusedPrivateConstructor(ConstructorDeclaration constructorDecl) {
        int severity = this.computeSeverity(603979910);
        if (severity == 256) {
            return;
        }
        if (this.excludeDueToAnnotation(constructorDecl.annotations, 603979910)) {
            return;
        }
        MethodBinding constructor = constructorDecl.binding;
        this.handle(603979910, new String[]{new String(constructor.declaringClass.readableName()), this.typesAsString(constructor, false)}, new String[]{new String(constructor.declaringClass.shortReadableName()), this.typesAsString(constructor, true)}, severity, constructorDecl.sourceStart, constructorDecl.sourceEnd);
    }

    public void unusedPrivateField(FieldDeclaration fieldDecl) {
        ReferenceBinding referenceBinding;
        int severity = this.computeSeverity(570425421);
        if (severity == 256) {
            return;
        }
        FieldBinding field = fieldDecl.binding;
        if (CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name) && field.isStatic() && field.isFinal() && TypeBinding.equalsEquals(TypeBinding.LONG, field.type) && (referenceBinding = field.declaringClass) != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
            return;
        }
        if (CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name) && field.isStatic() && field.isFinal() && field.type.dimensions() == 1 && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName()) && (referenceBinding = field.declaringClass) != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
            return;
        }
        if (this.excludeDueToAnnotation(fieldDecl.annotations, 570425421)) {
            return;
        }
        this.handle(570425421, new String[]{new String(field.declaringClass.readableName()), new String(field.name)}, new String[]{new String(field.declaringClass.shortReadableName()), new String(field.name)}, severity, this.nodeSourceStart(field, fieldDecl), this.nodeSourceEnd(field, fieldDecl));
    }

    public void unusedPrivateMethod(AbstractMethodDeclaration methodDecl) {
        int severity = this.computeSeverity(603979894);
        if (severity == 256) {
            return;
        }
        MethodBinding method = methodDecl.binding;
        if (!method.isStatic() && TypeBinding.VOID == method.returnType && method.parameters.length == 1 && method.parameters[0].dimensions() == 0 && CharOperation.equals(method.selector, TypeConstants.READOBJECT) && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTINPUTSTREAM, method.parameters[0].readableName())) {
            return;
        }
        if (!method.isStatic() && TypeBinding.VOID == method.returnType && method.parameters.length == 1 && method.parameters[0].dimensions() == 0 && CharOperation.equals(method.selector, TypeConstants.WRITEOBJECT) && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTOUTPUTSTREAM, method.parameters[0].readableName())) {
            return;
        }
        if (!method.isStatic() && 1 == method.returnType.id && method.parameters.length == 0 && CharOperation.equals(method.selector, TypeConstants.READRESOLVE)) {
            return;
        }
        if (!method.isStatic() && 1 == method.returnType.id && method.parameters.length == 0 && CharOperation.equals(method.selector, TypeConstants.WRITEREPLACE)) {
            return;
        }
        if (this.excludeDueToAnnotation(methodDecl.annotations, 603979894)) {
            return;
        }
        this.handle(603979894, new String[]{new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false)}, new String[]{new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true)}, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    private boolean excludeDueToAnnotation(Annotation[] annotations, int problemId) {
        int annotationsLen = 0;
        if (annotations == null) {
            return false;
        }
        annotationsLen = annotations.length;
        if (annotationsLen == 0) {
            return false;
        }
        int i = 0;
        while (i < annotationsLen) {
            TypeBinding resolvedType = annotations[i].resolvedType;
            if (resolvedType != null) {
                switch (resolvedType.id) {
                    case 44: 
                    case 49: 
                    case 60: {
                        break;
                    }
                    case 80: 
                    case 81: 
                    case 82: {
                        if (problemId == 570425421) break;
                        return true;
                    }
                    default: {
                        if (resolvedType instanceof ReferenceBinding && ((ReferenceBinding)resolvedType).hasNullBit(224)) break;
                        return true;
                    }
                }
            }
            ++i;
        }
        return false;
    }

    public void unusedPrivateType(TypeDeclaration typeDecl) {
        int severity = this.computeSeverity(553648135);
        if (severity == 256) {
            return;
        }
        if (this.excludeDueToAnnotation(typeDecl.annotations, 553648135)) {
            return;
        }
        SourceTypeBinding type = typeDecl.binding;
        this.handle(553648135, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void unusedTypeParameter(TypeParameter typeParameter) {
        int severity = this.computeSeverity(16777877);
        if (severity == 256) {
            return;
        }
        String[] arguments = new String[]{new String(typeParameter.name)};
        this.handle(16777877, arguments, arguments, typeParameter.sourceStart, typeParameter.sourceEnd);
    }

    public void unusedWarningToken(Expression token) {
        String[] arguments = new String[]{token.constant.stringValue()};
        this.handle(536871547, arguments, arguments, token.sourceStart, token.sourceEnd);
    }

    public void problemNotAnalysed(Expression token, String optionKey) {
        String[] stringArray;
        if (optionKey != null) {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = optionKey;
        } else {
            stringArray = new String[]{};
        }
        this.handle(1102, stringArray, new String[]{token.constant.stringValue()}, token.sourceStart, token.sourceEnd);
    }

    public void previewFeatureUsed(int sourceStart, int sourceEnd) {
        this.handle(0x400450, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void previewAPIUsed(int sourceStart, int sourceEnd, boolean isFatal) {
        if (this.options.enablePreviewFeatures) {
            return;
        }
        this.handle(0x400454, NoArgument, NoArgument, isFatal ? 129 : 0, sourceStart, sourceEnd);
    }

    public boolean validateRestrictedKeywords(char[] name, int start, int end, boolean reportSyntaxError) {
        return this.validateRestrictedKeywords(name, null, start, end, reportSyntaxError);
    }

    private boolean validateRestrictedKeywords(char[] name, String expectedToken, int start, int end, boolean reportSyntaxError) {
        String tokenName;
        String restrictedIdentifier;
        boolean isPreviewEnabled = this.options.enablePreviewFeatures;
        if (!(expectedToken == null || (restrictedIdentifier = permittedRestrictedKeyWordMap.get(tokenName = new String(name))) != null && restrictedIdentifier.equals(expectedToken))) {
            return false;
        }
        JavaFeature[] javaFeatureArray = JavaFeature.values();
        int n = javaFeatureArray.length;
        int n2 = 0;
        while (n2 < n) {
            char[][] restrictedKeywords;
            JavaFeature feature = javaFeatureArray[n2];
            char[][] cArray = restrictedKeywords = feature.getRestrictedKeywords();
            int n3 = restrictedKeywords.length;
            int n4 = 0;
            while (n4 < n3) {
                char[] k = cArray[n4];
                if (CharOperation.equals(name, k)) {
                    long compliance;
                    int severity;
                    if (reportSyntaxError) {
                        return this.validateJavaFeatureSupport(feature, start, end);
                    }
                    if (feature.isPreview()) {
                        int severity2 = isPreviewEnabled ? 129 : 0;
                        this.restrictedTypeName(name, CompilerOptions.versionFromJdkLevel(feature.getCompliance()), start, end, severity2);
                        return isPreviewEnabled;
                    }
                    if (this.options.complianceLevel < feature.getCompliance()) {
                        severity = 0;
                        compliance = this.options.complianceLevel;
                    } else {
                        severity = 129;
                        compliance = feature.getCompliance();
                    }
                    this.restrictedTypeName(name, CompilerOptions.versionFromJdkLevel(compliance), start, end, severity);
                    return true;
                }
                ++n4;
            }
            ++n2;
        }
        return false;
    }

    public boolean validateRestrictedKeywords(char[] name, ASTNode node) {
        return this.validateRestrictedKeywords(name, node.sourceStart, node.sourceEnd, false);
    }

    public boolean validateJavaFeatureSupport(JavaFeature feature, int sourceStart, int sourceEnd) {
        boolean versionInRange = feature.getCompliance() <= this.options.sourceLevel;
        String version = CompilerOptions.versionFromJdkLevel(feature.getCompliance());
        int problemId = -1;
        if (feature.isPreview()) {
            if (!versionInRange) {
                problemId = 4195409;
            } else if (!this.options.enablePreviewFeatures) {
                problemId = 0x40044F;
            } else if (this.options.isAnyEnabled(IrritantSet.PREVIEW)) {
                problemId = 0x400450;
            }
        } else if (!versionInRange) {
            problemId = 4195411;
        }
        if (problemId > -1) {
            String[] args = new String[]{feature.getName(), version};
            this.handle(problemId, args, args, sourceStart, sourceEnd);
            return true;
        }
        return false;
    }

    public void useAssertAsAnIdentifier(int sourceStart, int sourceEnd) {
        this.handle(536871352, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void useEnumAsAnIdentifier(int sourceStart, int sourceEnd) {
        this.handle(536871353, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void illegalUseOfUnderscoreAsAnIdentifier(int sourceStart, int sourceEnd, boolean reportError) {
        this.underScoreIsError = reportError;
        int problemId = reportError ? 1610613181 : 1610613179;
        try {
            this.handle(problemId, NoArgument, NoArgument, sourceStart, sourceEnd);
        }
        finally {
            this.underScoreIsError = false;
        }
    }

    public void varargsArgumentNeedCast(MethodBinding method, TypeBinding argumentType, InvocationSite location) {
        int severity = this.options.getSeverity(0x20000040);
        if (severity == 256) {
            return;
        }
        ArrayBinding varargsType = (ArrayBinding)method.parameters[method.parameters.length - 1];
        if (method.isConstructor()) {
            this.handle(134218530, new String[]{new String(argumentType.readableName()), new String(varargsType.readableName()), new String(method.declaringClass.readableName()), this.typesAsString(method, false), new String(varargsType.elementsType().readableName())}, new String[]{new String(argumentType.shortReadableName()), new String(varargsType.shortReadableName()), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true), new String(varargsType.elementsType().shortReadableName())}, severity, location.sourceStart(), location.sourceEnd());
        } else {
            this.handle(67109665, new String[]{new String(argumentType.readableName()), new String(varargsType.readableName()), new String(method.selector), this.typesAsString(method, false), new String(method.declaringClass.readableName()), new String(varargsType.elementsType().readableName())}, new String[]{new String(argumentType.shortReadableName()), new String(varargsType.shortReadableName()), new String(method.selector), this.typesAsString(method, true), new String(method.declaringClass.shortReadableName()), new String(varargsType.elementsType().shortReadableName())}, severity, location.sourceStart(), location.sourceEnd());
        }
    }

    public void varargsConflict(MethodBinding method1, MethodBinding method2, SourceTypeBinding type) {
        this.handle(67109667, new String[]{new String(method1.selector), this.typesAsString(method1, false), new String(method1.declaringClass.readableName()), this.typesAsString(method2, false), new String(method2.declaringClass.readableName())}, new String[]{new String(method1.selector), this.typesAsString(method1, true), new String(method1.declaringClass.shortReadableName()), this.typesAsString(method2, true), new String(method2.declaringClass.shortReadableName())}, TypeBinding.equalsEquals(method1.declaringClass, type) ? method1.sourceStart() : type.sourceStart(), TypeBinding.equalsEquals(method1.declaringClass, type) ? method1.sourceEnd() : type.sourceEnd());
    }

    public void safeVarargsOnFixedArityMethod(MethodBinding method) {
        String[] arguments = new String[]{new String(method.isConstructor() ? method.declaringClass.shortReadableName() : method.selector)};
        this.handle(67109668, arguments, arguments, method.sourceStart(), method.sourceEnd());
    }

    public void safeVarargsOnNonFinalInstanceMethod(MethodBinding method) {
        String[] arguments = new String[]{new String(method.isConstructor() ? method.declaringClass.shortReadableName() : method.selector)};
        this.handle(67109669, arguments, arguments, method.sourceStart(), method.sourceEnd());
    }

    public void possibleHeapPollutionFromVararg(AbstractVariableDeclaration vararg) {
        String[] arguments = new String[]{new String(vararg.name)};
        this.handle(67109670, arguments, arguments, vararg.sourceStart, vararg.sourceEnd);
    }

    public void safeVarargsOnOnSyntheticRecordAccessor(RecordComponent comp) {
        String[] arguments = new String[]{new String(comp.name)};
        this.handle(16778980, arguments, arguments, comp.sourceStart, comp.sourceEnd);
    }

    public void variableTypeCannotBeVoid(AbstractVariableDeclaration varDecl) {
        String[] arguments = new String[]{new String(varDecl.name)};
        this.handle(536870964, arguments, arguments, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalMultipleDeclarators(AbstractVariableDeclaration varDecl) {
        this.handle(1073743324, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalCannotBeArray(AbstractVariableDeclaration varDecl) {
        this.handle(1073743325, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalReferencesItself(AbstractVariableDeclaration varDecl) {
        this.handle(1073743326, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalWithoutInitizalier(AbstractVariableDeclaration varDecl) {
        this.handle(1073743327, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalInitializedToNull(AbstractVariableDeclaration varDecl) {
        this.handle(16778720, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalInitializedToVoid(AbstractVariableDeclaration varDecl) {
        this.handle(16778721, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalCannotBeArrayInitalizers(AbstractVariableDeclaration varDecl) {
        this.handle(16778722, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalCannotBeLambda(AbstractVariableDeclaration varDecl) {
        this.handle(16778723, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varLocalCannotBeMethodReference(AbstractVariableDeclaration varDecl) {
        this.handle(16778724, NoArgument, NoArgument, varDecl.sourceStart, varDecl.sourceEnd);
    }

    public void varIsReservedTypeName(TypeDeclaration decl) {
        this.handle(1073743333, NoArgument, NoArgument, decl.sourceStart, decl.sourceEnd);
    }

    public void varIsReservedTypeNameInFuture(ASTNode decl) {
        this.handle(1073743334, NoArgument, NoArgument, 0, decl.sourceStart, decl.sourceEnd);
    }

    public void varIsNotAllowedHere(ASTNode astNode) {
        this.handle(1073743335, NoArgument, NoArgument, astNode.sourceStart, astNode.sourceEnd);
    }

    public void varCannotBeMixedWithNonVarParams(ASTNode astNode) {
        this.handle(1073743336, NoArgument, NoArgument, astNode.sourceStart, astNode.sourceEnd);
    }

    public void variableTypeCannotBeVoidArray(AbstractVariableDeclaration varDecl) {
        this.handle(536870966, NoArgument, NoArgument, varDecl.type.sourceStart, varDecl.type.sourceEnd);
    }

    public void visibilityConflict(MethodBinding currentMethod, MethodBinding inheritedMethod) {
        this.handle(67109273, new String[]{new String(inheritedMethod.declaringClass.readableName())}, new String[]{new String(inheritedMethod.declaringClass.shortReadableName())}, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }

    public void wildcardAssignment(TypeBinding variableType, TypeBinding expressionType, ASTNode location) {
        this.handle(16777758, new String[]{new String(expressionType.readableName()), new String(variableType.readableName())}, new String[]{new String(expressionType.shortReadableName()), new String(variableType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void wildcardInvocation(ASTNode location, TypeBinding receiverType, MethodBinding method, TypeBinding[] arguments) {
        TypeBinding offendingArgument = null;
        TypeBinding offendingParameter = null;
        int i = 0;
        int length = method.parameters.length;
        while (i < length) {
            TypeBinding parameter = method.parameters[i];
            if (parameter.isWildcard() && ((WildcardBinding)parameter).boundKind != 2) {
                offendingParameter = parameter;
                offendingArgument = arguments[i];
                break;
            }
            ++i;
        }
        if (method.isConstructor()) {
            this.handle(16777756, new String[]{new String(receiverType.sourceName()), this.typesAsString(method, false), new String(receiverType.readableName()), this.typesAsString(arguments, false), new String(offendingArgument.readableName()), new String(offendingParameter.readableName())}, new String[]{new String(receiverType.sourceName()), this.typesAsString(method, true), new String(receiverType.shortReadableName()), this.typesAsString(arguments, true), new String(offendingArgument.shortReadableName()), new String(offendingParameter.shortReadableName())}, location.sourceStart, location.sourceEnd);
        } else {
            this.handle(16777757, new String[]{new String(method.selector), this.typesAsString(method, false), new String(receiverType.readableName()), this.typesAsString(arguments, false), new String(offendingArgument.readableName()), new String(offendingParameter.readableName())}, new String[]{new String(method.selector), this.typesAsString(method, true), new String(receiverType.shortReadableName()), this.typesAsString(arguments, true), new String(offendingArgument.shortReadableName()), new String(offendingParameter.shortReadableName())}, location.sourceStart, location.sourceEnd);
        }
    }

    public void wrongSequenceOfExceptionTypesError(TypeReference typeRef, TypeBinding exceptionType, TypeBinding hidingExceptionType) {
        this.handle(553648315, new String[]{new String(exceptionType.readableName()), new String(hidingExceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName()), new String(hidingExceptionType.shortReadableName())}, typeRef.sourceStart, typeRef.sourceEnd);
    }

    public void wrongSequenceOfExceptionTypes(TypeReference typeRef, TypeBinding exceptionType, TypeBinding hidingExceptionType) {
        this.handle(553649001, new String[]{new String(exceptionType.readableName()), new String(hidingExceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName()), new String(hidingExceptionType.shortReadableName())}, typeRef.sourceStart, typeRef.sourceEnd);
    }

    public void autoManagedResourcesNotBelow17(Statement[] resources) {
        Statement stmt0 = resources[0];
        Statement stmtn = resources[resources.length - 1];
        int sourceStart = stmt0 instanceof LocalDeclaration ? ((LocalDeclaration)stmt0).declarationSourceStart : stmt0.sourceStart;
        int sourceEnd = stmtn instanceof LocalDeclaration ? ((LocalDeclaration)stmtn).declarationSourceEnd : stmtn.sourceEnd;
        this.handle(1610613610, NoArgument, NoArgument, sourceStart, sourceEnd);
    }

    public void autoManagedVariableResourcesNotBelow9(Expression resource) {
        this.handle(1610614087, NoArgument, NoArgument, resource.sourceStart, resource.sourceEnd);
    }

    public void cannotInferElidedTypes(AllocationExpression allocationExpression) {
        String[] arguments = new String[]{allocationExpression.type.toString()};
        this.handle(16778094, arguments, arguments, allocationExpression.sourceStart, allocationExpression.sourceEnd);
    }

    public void diamondNotWithExplicitTypeArguments(TypeReference[] typeArguments) {
        this.handle(16778095, NoArgument, NoArgument, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }

    public void rawConstructorReferenceNotWithExplicitTypeArguments(TypeReference[] typeArguments) {
        this.handle(16778219, NoArgument, NoArgument, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }

    public void diamondNotWithAnoymousClasses(TypeReference type) {
        this.handle(16778096, NoArgument, NoArgument, type.sourceStart, type.sourceEnd);
    }

    public void anonymousDiamondWithNonDenotableTypeArguments(TypeReference type, TypeBinding tb) {
        this.handle(16778528, new String[]{new String(tb.leafComponentType().shortReadableName()), type.toString()}, new String[]{new String(tb.leafComponentType().shortReadableName()), type.toString()}, type.sourceStart, type.sourceEnd);
    }

    public void redundantSpecificationOfTypeArguments(ASTNode location, TypeBinding[] argumentTypes) {
        int severity = this.computeSeverity(16778100);
        if (severity != 256) {
            int sourceStart = -1;
            if (location instanceof QualifiedTypeReference) {
                QualifiedTypeReference ref = (QualifiedTypeReference)location;
                sourceStart = (int)(ref.sourcePositions[ref.sourcePositions.length - 1] >> 32);
            } else {
                sourceStart = location.sourceStart;
            }
            this.handle(16778100, new String[]{this.typesAsString(argumentTypes, false)}, new String[]{this.typesAsString(argumentTypes, true)}, severity, sourceStart, location.sourceEnd);
        }
    }

    public void potentiallyUnclosedCloseable(FakedTrackingVariable trackVar, ASTNode location) {
        String[] args = new String[]{trackVar.nameForReporting(location, this.referenceContext)};
        if (location == null || trackVar.acquisition != null) {
            this.handle(536871797, args, args, trackVar.sourceStart, trackVar.sourceEnd);
        } else {
            this.handle(536871798, args, args, location.sourceStart, location.sourceEnd);
        }
    }

    public void unclosedCloseable(FakedTrackingVariable trackVar, ASTNode location) {
        String[] args = new String[]{String.valueOf(trackVar.name)};
        if (location == null) {
            this.handle(536871799, args, args, trackVar.sourceStart, trackVar.sourceEnd);
        } else {
            this.handle(536871800, args, args, location.sourceStart, location.sourceEnd);
        }
    }

    public void explicitlyClosedAutoCloseable(FakedTrackingVariable trackVar) {
        String[] args = new String[]{String.valueOf(trackVar.name)};
        this.handle(536871801, args, args, trackVar.sourceStart, trackVar.sourceEnd);
    }

    public void nullityMismatch(Expression expression, TypeBinding providedType, TypeBinding requiredType, int nullStatus, char[][] annotationName) {
        if ((nullStatus & 2) != 0) {
            this.nullityMismatchIsNull(expression, requiredType);
            return;
        }
        if (expression instanceof MessageSend && (((MessageSend)expression).binding.tagBits & 0x80000000000000L) != 0L) {
            this.nullityMismatchSpecdNullable(expression, requiredType, this.options.nonNullAnnotationName);
            return;
        }
        if ((nullStatus & 0x10) != 0) {
            VariableBinding var = expression.localVariableBinding();
            if (var == null && expression instanceof Reference) {
                var = ((Reference)expression).lastFieldBinding();
            }
            if (var != null && var.type.isFreeTypeVariable()) {
                this.nullityMismatchVariableIsFreeTypeVariable(var, expression);
                return;
            }
            if (var != null && var.isNullable()) {
                this.nullityMismatchSpecdNullable(expression, requiredType, annotationName);
                return;
            }
            if (expression instanceof ArrayReference && expression.resolvedType.isFreeTypeVariable()) {
                this.nullityMismatchingTypeAnnotation(expression, providedType, requiredType, NullAnnotationMatching.NULL_ANNOTATIONS_MISMATCH);
                return;
            }
            this.nullityMismatchPotentiallyNull(expression, requiredType, annotationName);
            return;
        }
        if (this.options.usesNullTypeAnnotations()) {
            this.nullityMismatchingTypeAnnotation(expression, providedType, requiredType, NullAnnotationMatching.NULL_ANNOTATIONS_UNCHECKED);
        } else {
            this.nullityMismatchIsUnknown(expression, providedType, requiredType, annotationName);
        }
    }

    public void nullityMismatchIsNull(Expression expression, TypeBinding requiredType) {
        String[] argumentsShort;
        String[] arguments;
        int problemId = 16778126;
        boolean useNullTypeAnnotations = this.options.usesNullTypeAnnotations();
        if (useNullTypeAnnotations && requiredType.isTypeVariable() && !requiredType.hasNullTypeAnnotations()) {
            problemId = 969;
        }
        if (requiredType instanceof CaptureBinding) {
            CaptureBinding capture = (CaptureBinding)requiredType;
            if (capture.wildcard != null) {
                requiredType = capture.wildcard;
            }
        }
        if (!useNullTypeAnnotations) {
            arguments = new String[]{this.annotatedTypeName(requiredType, this.options.nonNullAnnotationName)};
            argumentsShort = new String[]{this.shortAnnotatedTypeName(requiredType, this.options.nonNullAnnotationName)};
        } else if (problemId == 969) {
            arguments = new String[]{new String(requiredType.sourceName())};
            argumentsShort = new String[]{new String(requiredType.sourceName())};
        } else {
            arguments = new String[]{new String(requiredType.nullAnnotatedReadableName(this.options, false))};
            argumentsShort = new String[]{new String(requiredType.nullAnnotatedReadableName(this.options, true))};
        }
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    public void nullityMismatchSpecdNullable(Expression expression, TypeBinding requiredType, char[][] annotationName) {
        int problemId = 536871845;
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{this.annotatedTypeName(requiredType, annotationName), String.valueOf(CharOperation.concatWith(nullableName, '.'))};
        String[] argumentsShort = new String[]{this.shortAnnotatedTypeName(requiredType, annotationName), String.valueOf(nullableName[nullableName.length - 1])};
        if (expression.resolvedType != null && expression.resolvedType.hasNullTypeAnnotations()) {
            problemId = 536871865;
            arguments[1] = String.valueOf(expression.resolvedType.nullAnnotatedReadableName(this.options, false));
            argumentsShort[1] = String.valueOf(expression.resolvedType.nullAnnotatedReadableName(this.options, true));
        }
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    public void nullityMismatchPotentiallyNull(Expression expression, TypeBinding requiredType, char[][] annotationName) {
        int problemId = 16778127;
        char[][] nullableName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{this.annotatedTypeName(requiredType, annotationName), String.valueOf(CharOperation.concatWith(nullableName, '.'))};
        String[] argumentsShort = new String[]{this.shortAnnotatedTypeName(requiredType, annotationName), String.valueOf(nullableName[nullableName.length - 1])};
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    public void nullityMismatchIsUnknown(Expression expression, TypeBinding providedType, TypeBinding requiredType, char[][] annotationName) {
        int problemId = 16778128;
        String[] arguments = new String[]{String.valueOf(providedType.readableName()), this.annotatedTypeName(requiredType, annotationName)};
        String[] argumentsShort = new String[]{String.valueOf(providedType.shortReadableName()), this.shortAnnotatedTypeName(requiredType, annotationName)};
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }

    private void nullityMismatchIsFreeTypeVariable(TypeBinding providedType, int sourceStart, int sourceEnd) {
        char[][] nullableName = this.options.nullableAnnotationName;
        char[][] nonNullName = this.options.nonNullAnnotationName;
        String[] arguments = new String[]{new String(nonNullName[nonNullName.length - 1]), new String(providedType.readableName()), new String(nullableName[nullableName.length - 1])};
        this.handle(16778195, arguments, arguments, sourceStart, sourceEnd);
    }

    public void nullityMismatchVariableIsFreeTypeVariable(VariableBinding variable, ASTNode location) {
        int severity = this.computeSeverity(16778195);
        if (severity == 256) {
            return;
        }
        this.nullityMismatchIsFreeTypeVariable(variable.type, this.nodeSourceStart(variable, location), this.nodeSourceEnd(variable, location));
    }

    public void illegalRedefinitionToNonNullParameter(Argument argument, ReferenceBinding declaringClass, char[][] inheritedAnnotationName) {
        int sourceStart = argument.type.sourceStart;
        if (argument.annotations != null) {
            int i = 0;
            while (i < argument.annotations.length) {
                Annotation annotation = argument.annotations[i];
                if (annotation.hasNullBit(96)) {
                    sourceStart = annotation.sourceStart;
                    break;
                }
                ++i;
            }
        }
        if (inheritedAnnotationName == null) {
            this.handle(67109780, new String[]{new String(argument.name), new String(declaringClass.readableName())}, new String[]{new String(argument.name), new String(declaringClass.shortReadableName())}, sourceStart, argument.type.sourceEnd);
        } else {
            this.handle(67109779, new String[]{new String(argument.name), new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName)}, new String[]{new String(argument.name), new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1])}, sourceStart, argument.type.sourceEnd);
        }
    }

    public void parameterLackingNullableAnnotation(Argument argument, ReferenceBinding declaringClass, char[][] inheritedAnnotationName) {
        this.handle(67109782, new String[]{new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName)}, new String[]{new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1])}, argument.type.sourceStart, argument.type.sourceEnd);
    }

    public void parameterLackingNonnullAnnotation(Argument argument, ReferenceBinding declaringClass, char[][] inheritedAnnotationName) {
        this.handle(67109781, new String[]{new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName)}, new String[]{new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1])}, argument.type.sourceStart, argument.type.sourceEnd);
    }

    public void inheritedParameterLackingNonnullAnnotation(MethodBinding currentMethod, int paramRank, ReferenceBinding specificationType, ASTNode location, char[][] annotationName) {
        this.handle(67109810, new String[]{String.valueOf(paramRank), new String(currentMethod.readableName()), new String(specificationType.readableName()), CharOperation.toString(annotationName)}, new String[]{String.valueOf(paramRank), new String(currentMethod.shortReadableName()), new String(specificationType.shortReadableName()), new String(annotationName[annotationName.length - 1])}, location.sourceStart, location.sourceEnd);
    }

    public void illegalParameterRedefinition(Argument argument, ReferenceBinding declaringClass, TypeBinding inheritedParameter) {
        int sourceStart = argument.type.sourceStart;
        if (argument.annotations != null) {
            int i = 0;
            while (i < argument.annotations.length) {
                Annotation annotation = argument.annotations[i];
                if (annotation.hasNullBit(96)) {
                    sourceStart = annotation.sourceStart;
                    break;
                }
                ++i;
            }
        }
        this.handle(67109836, new String[]{new String(argument.name), new String(declaringClass.readableName()), new String(inheritedParameter.nullAnnotatedReadableName(this.options, false))}, new String[]{new String(argument.name), new String(declaringClass.shortReadableName()), new String(inheritedParameter.nullAnnotatedReadableName(this.options, true))}, sourceStart, argument.type.sourceEnd);
    }

    public void illegalReturnRedefinition(AbstractMethodDeclaration abstractMethodDecl, MethodBinding inheritedMethod, char[][] nonNullAnnotationName) {
        MethodDeclaration methodDecl = (MethodDeclaration)abstractMethodDecl;
        StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(inheritedMethod.declaringClass.readableName()).append('.').append(inheritedMethod.readableName());
        StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(inheritedMethod.declaringClass.shortReadableName()).append('.').append(inheritedMethod.shortReadableName());
        int sourceStart = methodDecl.returnType.sourceStart;
        Annotation[] annotations = methodDecl.annotations;
        Annotation annotation = this.findAnnotation(annotations, 64);
        if (annotation != null) {
            sourceStart = annotation.sourceStart;
        }
        TypeBinding inheritedReturnType = inheritedMethod.returnType;
        int problemId = 67109778;
        StringBuilder returnType = new StringBuilder();
        StringBuilder returnTypeShort = new StringBuilder();
        if (this.options.usesNullTypeAnnotations()) {
            if (inheritedReturnType.isTypeVariable() && (inheritedReturnType.tagBits & 0x180000000000000L) == 0L) {
                problemId = 67109838;
                returnType.append(inheritedReturnType.readableName());
                returnTypeShort.append(inheritedReturnType.shortReadableName());
            } else {
                returnType.append(inheritedReturnType.nullAnnotatedReadableName(this.options, false));
                returnTypeShort.append(inheritedReturnType.nullAnnotatedReadableName(this.options, true));
            }
        } else {
            returnType.append('@').append(CharOperation.concatWith(nonNullAnnotationName, '.'));
            returnType.append(' ').append(inheritedReturnType.readableName());
            returnTypeShort.append('@').append(nonNullAnnotationName[nonNullAnnotationName.length - 1]);
            returnTypeShort.append(' ').append(inheritedReturnType.shortReadableName());
        }
        String[] arguments = new String[]{methodSignature.toString(), returnType.toString()};
        String[] argumentsShort = new String[]{shortSignature.toString(), returnTypeShort.toString()};
        this.handle(problemId, arguments, argumentsShort, sourceStart, methodDecl.returnType.sourceEnd);
    }

    public void referenceExpressionArgumentNullityMismatch(ReferenceExpression location, TypeBinding requiredType, TypeBinding providedType, MethodBinding descriptorMethod, int idx, NullAnnotationMatching status) {
        StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(descriptorMethod.declaringClass.readableName()).append('.').append(descriptorMethod.readableName());
        StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(descriptorMethod.declaringClass.shortReadableName()).append('.').append(descriptorMethod.shortReadableName());
        this.handle(status.isUnchecked() ? 67109822 : 67109821, new String[]{idx == -1 ? "'this'" : String.valueOf(idx + 1), String.valueOf(requiredType.nullAnnotatedReadableName(this.options, false)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, false)), methodSignature.toString()}, new String[]{idx == -1 ? "'this'" : String.valueOf(idx + 1), String.valueOf(requiredType.nullAnnotatedReadableName(this.options, true)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, true)), shortSignature.toString()}, location.sourceStart, location.sourceEnd);
    }

    public void illegalReturnRedefinition(ASTNode location, MethodBinding descriptorMethod, boolean isUnchecked, TypeBinding providedType) {
        StringBuffer methodSignature = new StringBuffer().append(descriptorMethod.declaringClass.readableName()).append('.').append(descriptorMethod.readableName());
        StringBuffer shortSignature = new StringBuffer().append(descriptorMethod.declaringClass.shortReadableName()).append('.').append(descriptorMethod.shortReadableName());
        this.handle(isUnchecked ? 67109824 : 67109823, new String[]{methodSignature.toString(), String.valueOf(descriptorMethod.returnType.nullAnnotatedReadableName(this.options, false)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, false))}, new String[]{shortSignature.toString(), String.valueOf(descriptorMethod.returnType.nullAnnotatedReadableName(this.options, true)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, true))}, location.sourceStart, location.sourceEnd);
    }

    public void messageSendPotentialNullReference(MethodBinding method, ASTNode location) {
        String[] arguments = new String[]{new String(method.readableName())};
        this.handle(536871831, arguments, arguments, location.sourceStart, location.sourceEnd);
    }

    public void messageSendRedundantCheckOnNonNull(MethodBinding method, ASTNode location) {
        String[] arguments = new String[]{new String(method.readableName())};
        this.handle(536871832, arguments, arguments, location.sourceStart, location.sourceEnd);
    }

    public void expressionNullReference(ASTNode location) {
        this.handle(0x200002A0, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void expressionPotentialNullReference(ASTNode location) {
        this.handle(536871585, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void cannotImplementIncompatibleNullness(ReferenceContext context, MethodBinding currentMethod, MethodBinding inheritedMethod, boolean showReturn) {
        int sourceStart = 0;
        int sourceEnd = 0;
        if (context instanceof TypeDeclaration) {
            TypeDeclaration type = (TypeDeclaration)context;
            if (type.superclass != null) {
                sourceStart = type.superclass.sourceStart;
                sourceEnd = type.superclass.sourceEnd;
            } else {
                sourceStart = type.sourceStart;
                sourceEnd = type.sourceEnd;
            }
        }
        String[] problemArguments = new String[]{showReturn ? String.valueOf(new String(currentMethod.returnType.nullAnnotatedReadableName(this.options, false))) + ' ' : "", new String(currentMethod.selector), this.typesAsString(currentMethod, false, true), new String(currentMethod.declaringClass.readableName()), new String(inheritedMethod.declaringClass.readableName())};
        String[] messageArguments = new String[]{showReturn ? String.valueOf(new String(currentMethod.returnType.nullAnnotatedReadableName(this.options, true))) + ' ' : "", new String(currentMethod.selector), this.typesAsString(currentMethod, true, true), new String(currentMethod.declaringClass.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName())};
        this.handle(536871833, problemArguments, messageArguments, sourceStart, sourceEnd);
    }

    public void nullAnnotationIsRedundant(AbstractMethodDeclaration sourceMethod, int i) {
        int sourceEnd;
        int sourceStart;
        if (i == -1) {
            MethodDeclaration methodDecl = (MethodDeclaration)sourceMethod;
            Annotation annotation = this.findAnnotation(methodDecl.annotations, 32);
            sourceStart = annotation != null ? annotation.sourceStart : methodDecl.returnType.sourceStart;
            sourceEnd = methodDecl.returnType.sourceEnd;
        } else {
            Argument arg = sourceMethod.arguments[i];
            sourceStart = arg.declarationSourceStart;
            sourceEnd = arg.sourceEnd;
        }
        this.handle(67109786, ProblemHandler.NoArgument, ProblemHandler.NoArgument, sourceStart, sourceEnd);
    }

    public void nullAnnotationIsRedundant(FieldDeclaration sourceField) {
        Annotation annotation = this.findAnnotation(sourceField.annotations, 32);
        int sourceStart = annotation != null ? annotation.sourceStart : sourceField.type.sourceStart;
        int sourceEnd = sourceField.type.sourceEnd;
        this.handle(67109786, ProblemHandler.NoArgument, ProblemHandler.NoArgument, sourceStart, sourceEnd);
    }

    public void nullDefaultAnnotationIsRedundant(ASTNode location, Annotation[] annotations, Binding outer) {
        if (outer == Scope.NOT_REDUNDANT) {
            return;
        }
        Annotation annotation = this.findAnnotation(annotations, 128);
        int start = annotation != null ? annotation.sourceStart : location.sourceStart;
        int end = annotation != null ? annotation.sourceEnd : location.sourceStart;
        String[] args = NoArgument;
        String[] shortArgs = NoArgument;
        if (outer != null) {
            args = new String[]{new String(outer.readableName())};
            shortArgs = new String[]{new String(outer.shortReadableName())};
        }
        int problemId = 536871837;
        if (outer instanceof ModuleBinding) {
            problemId = 536871855;
        } else if (outer instanceof PackageBinding) {
            problemId = 536871838;
        } else if (outer instanceof ReferenceBinding) {
            problemId = 536871839;
        } else if (outer instanceof MethodBinding) {
            problemId = 536871840;
        } else if (outer instanceof LocalVariableBinding) {
            problemId = 536871974;
        } else if (outer instanceof FieldBinding) {
            problemId = 536871975;
        }
        this.handle(problemId, args, shortArgs, start, end);
    }

    public void contradictoryNullAnnotations(Annotation annotation) {
        this.contradictoryNullAnnotations(annotation.sourceStart, annotation.sourceEnd);
    }

    public void contradictoryNullAnnotations(Annotation[] annotations) {
        this.contradictoryNullAnnotations(annotations[0].sourceStart, annotations[annotations.length - 1].sourceEnd);
    }

    public void contradictoryNullAnnotations(int sourceStart, int sourceEnd) {
        char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.'))};
        String[] shortArguments = new String[]{new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1])};
        this.handle(536871841, arguments, shortArguments, sourceStart, sourceEnd);
    }

    public void contradictoryNullAnnotationsInferred(MethodBinding inferredMethod, ASTNode location) {
        this.contradictoryNullAnnotationsInferred(inferredMethod, location.sourceStart, location.sourceEnd, false);
    }

    public void contradictoryNullAnnotationsInferred(MethodBinding inferredMethod, int sourceStart, int sourceEnd, boolean isFunctionalExpression) {
        char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.')), new String(inferredMethod.returnType.nullAnnotatedReadableName(this.options, false)), new String(inferredMethod.selector), this.typesAsString(inferredMethod, false, true)};
        String[] shortArguments = new String[]{new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1]), new String(inferredMethod.returnType.nullAnnotatedReadableName(this.options, true)), new String(inferredMethod.selector), this.typesAsString(inferredMethod, true, true)};
        this.handle(isFunctionalExpression ? 67109837 : 536871878, arguments, shortArguments, sourceStart, sourceEnd);
    }

    public void contradictoryNullAnnotationsOnBounds(Annotation annotation, long previousTagBit) {
        char[][] annotationName = previousTagBit == 0x100000000000000L ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(CharOperation.concatWith(annotationName, '.'))};
        String[] shortArguments = new String[]{new String(annotationName[annotationName.length - 1])};
        this.handle(536871877, arguments, shortArguments, annotation.sourceStart, annotation.sourceEnd);
    }

    public void conflictingNullAnnotations(MethodBinding currentMethod, ASTNode location, MethodBinding inheritedMethod) {
        char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.')), new String(inheritedMethod.declaringClass.readableName())};
        String[] shortArguments = new String[]{new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1]), new String(inheritedMethod.declaringClass.shortReadableName())};
        this.handle(67109803, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }

    public void conflictingInheritedNullAnnotations(ASTNode location, boolean previousIsNonNull, MethodBinding previousInherited, boolean isNonNull, MethodBinding inheritedMethod) {
        char[][] previousAnnotationName = previousIsNonNull ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        char[][] annotationName = isNonNull ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        String[] arguments = new String[]{new String(CharOperation.concatWith(previousAnnotationName, '.')), new String(previousInherited.declaringClass.readableName()), new String(CharOperation.concatWith(annotationName, '.')), new String(inheritedMethod.declaringClass.readableName())};
        String[] shortArguments = new String[]{new String(previousAnnotationName[previousAnnotationName.length - 1]), new String(previousInherited.declaringClass.shortReadableName()), new String(annotationName[annotationName.length - 1]), new String(inheritedMethod.declaringClass.shortReadableName())};
        this.handle(67109804, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }

    public void illegalAnnotationForBaseType(TypeReference type, Annotation[] annotations, long nullAnnotationTagBit) {
        int typeBit = nullAnnotationTagBit == 0x80000000000000L ? 64 : 32;
        char[][] annotationNames = nullAnnotationTagBit == 0x100000000000000L ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        String typeName = new String(type.resolvedType.leafComponentType().readableName());
        String[] args = new String[]{new String(annotationNames[annotationNames.length - 1]), typeName};
        Annotation annotation = this.findAnnotation(annotations, typeBit);
        int start = annotation != null ? annotation.sourceStart : type.sourceStart;
        int end = annotation != null ? annotation.sourceEnd : type.sourceEnd;
        this.handle(16778139, args, args, start, end);
    }

    public void illegalAnnotationForBaseType(Annotation annotation, TypeBinding type) {
        String[] args = new String[]{new String(annotation.resolvedType.shortReadableName()), new String(type.readableName())};
        this.handle(16778139, args, args, annotation.sourceStart, annotation.sourceEnd);
    }

    private String annotatedTypeName(TypeBinding type, char[][] annotationName) {
        if ((type.tagBits & 0x180000000000000L) != 0L) {
            return String.valueOf(type.nullAnnotatedReadableName(this.options, false));
        }
        int dims = 0;
        char[] typeName = type.readableName();
        char[] annotationDisplayName = CharOperation.concatWith(annotationName, '.');
        return this.internalAnnotatedTypeName(annotationDisplayName, typeName, dims);
    }

    private String shortAnnotatedTypeName(TypeBinding type, char[][] annotationName) {
        if ((type.tagBits & 0x180000000000000L) != 0L) {
            return String.valueOf(type.nullAnnotatedReadableName(this.options, true));
        }
        int dims = 0;
        char[] typeName = type.shortReadableName();
        char[] annotationDisplayName = annotationName[annotationName.length - 1];
        return this.internalAnnotatedTypeName(annotationDisplayName, typeName, dims);
    }

    String internalAnnotatedTypeName(char[] annotationName, char[] typeName, int dims) {
        char[] fullName;
        if (dims > 0) {
            int plainLen = annotationName.length + typeName.length + 2;
            fullName = new char[plainLen + 2 * dims];
            System.arraycopy(typeName, 0, fullName, 0, typeName.length);
            fullName[typeName.length] = 32;
            fullName[typeName.length + 1] = 64;
            System.arraycopy(annotationName, 0, fullName, typeName.length + 2, annotationName.length);
            int i = 0;
            while (i < dims) {
                fullName[plainLen + i] = 91;
                fullName[plainLen + i + 1] = 93;
                ++i;
            }
        } else {
            fullName = new char[annotationName.length + typeName.length + 2];
            fullName[0] = 64;
            System.arraycopy(annotationName, 0, fullName, 1, annotationName.length);
            fullName[annotationName.length + 1] = 32;
            System.arraycopy(typeName, 0, fullName, annotationName.length + 2, typeName.length);
        }
        return String.valueOf(fullName);
    }

    private Annotation findAnnotation(Annotation[] annotations, int typeBit) {
        if (annotations != null) {
            int length = annotations.length;
            int j = length - 1;
            while (j >= 0) {
                if (annotations[j].hasNullBit(typeBit)) {
                    return annotations[j];
                }
                --j;
            }
        }
        return null;
    }

    public void missingNonNullByDefaultAnnotation(TypeDeclaration type) {
        CompilationUnitDeclaration compUnitDecl = type.getCompilationUnitDeclaration();
        if (compUnitDecl.currentPackage == null) {
            int severity = this.computeSeverity(536871842);
            if (severity == 256) {
                return;
            }
            SourceTypeBinding binding = type.binding;
            this.handle(536871842, new String[]{new String(((Binding)binding).readableName())}, new String[]{new String(((Binding)binding).shortReadableName())}, severity, type.sourceStart, type.sourceEnd);
        } else {
            int severity = this.computeSeverity(536871825);
            if (severity == 256) {
                return;
            }
            String[] arguments = new String[]{CharOperation.toString(compUnitDecl.currentPackage.tokens)};
            this.handle(536871825, arguments, arguments, severity, compUnitDecl.currentPackage.sourceStart, compUnitDecl.currentPackage.sourceEnd);
        }
    }

    public void illegalModifiersForElidedType(Argument argument) {
        String[] arg = new String[]{new String(argument.name)};
        this.handle(536871913, arg, arg, argument.declarationSourceStart, argument.declarationSourceEnd);
    }

    public void illegalModifiers(int modifierSourceStart, int modifiersSourceEnd) {
        this.handle(536871914, NoArgument, NoArgument, modifierSourceStart, modifiersSourceEnd);
    }

    public void arrayReferencePotentialNullReference(ArrayReference arrayReference) {
        this.handle(536871863, NoArgument, NoArgument, arrayReference.sourceStart, arrayReference.sourceEnd);
    }

    public void nullityMismatchingTypeAnnotation(Expression expression, TypeBinding providedType, TypeBinding requiredType, NullAnnotationMatching status) {
        String requiredNameShort;
        String requiredName;
        String[] shortArguments;
        String[] arguments;
        if (providedType == requiredType) {
            return;
        }
        if (providedType.id == 12 || status.nullStatus == 2) {
            this.nullityMismatchIsNull(expression, requiredType);
            return;
        }
        if ((requiredType.tagBits & 0x100000000000000L) != 0L) {
            if (status.isPotentiallyNullMismatch() && (providedType.tagBits & 0x80000000000000L) == 0L) {
                if (this.options.pessimisticNullAnalysisForFreeTypeVariablesEnabled && providedType.isTypeVariable() && !providedType.hasNullTypeAnnotations()) {
                    this.nullityMismatchIsFreeTypeVariable(providedType, expression.sourceStart, expression.sourceEnd);
                    return;
                }
                this.nullityMismatchPotentiallyNull(expression, requiredType, this.options.nonNullAnnotationName);
                return;
            }
            VariableBinding var = expression.localVariableBinding();
            if (var == null && expression instanceof Reference) {
                var = ((Reference)expression).lastFieldBinding();
            }
            if (var != null && var.type.isFreeTypeVariable()) {
                this.nullityMismatchVariableIsFreeTypeVariable(var, expression);
                return;
            }
        }
        int problemId = 0;
        String superHint = null;
        String superHintShort = null;
        if (status.superTypeHint != null && requiredType.isParameterizedType()) {
            problemId = status.isAnnotatedToUnannotated() ? 536871896 : (status.isUnchecked() ? 536871868 : 536871866);
            superHint = status.superTypeHintName(this.options, false);
            superHintShort = status.superTypeHintName(this.options, true);
        } else {
            int n = status.isAnnotatedToUnannotated() ? 536871895 : (status.isUnchecked() ? 536871867 : (problemId = requiredType.isTypeVariable() && !requiredType.hasNullTypeAnnotations() ? 970 : 536871865));
            if (problemId == 970) {
                String[] stringArray = new String[3];
                stringArray[2] = new String(requiredType.sourceName());
                arguments = stringArray;
                String[] stringArray2 = new String[3];
                stringArray2[2] = new String(requiredType.sourceName());
                shortArguments = stringArray2;
            } else {
                arguments = new String[2];
                shortArguments = new String[2];
            }
        }
        if (problemId == 970) {
            requiredName = new String(requiredType.sourceName());
            requiredNameShort = new String(requiredType.sourceName());
        } else {
            requiredName = new String(requiredType.nullAnnotatedReadableName(this.options, false));
            requiredNameShort = new String(requiredType.nullAnnotatedReadableName(this.options, true));
        }
        String providedName = String.valueOf(providedType.nullAnnotatedReadableName(this.options, false));
        String providedNameShort = String.valueOf(providedType.nullAnnotatedReadableName(this.options, true));
        if (superHint != null) {
            arguments = new String[]{requiredName, providedName, superHint};
            shortArguments = new String[]{requiredNameShort, providedNameShort, superHintShort};
        } else {
            arguments = new String[]{requiredName, providedName};
            shortArguments = new String[]{requiredNameShort, providedNameShort};
        }
        this.handle(problemId, arguments, shortArguments, expression.sourceStart, expression.sourceEnd);
    }

    public void nullityMismatchTypeArgument(TypeBinding typeVariable, TypeBinding typeArgument, ASTNode location) {
        String[] arguments = new String[]{String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, false)), String.valueOf(typeArgument.nullAnnotatedReadableName(this.options, false))};
        String[] shortArguments = new String[]{String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, true)), String.valueOf(typeArgument.nullAnnotatedReadableName(this.options, true))};
        this.handle(536871876, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }

    public void cannotRedefineTypeArgumentNullity(TypeBinding typeVariable, Binding superElement, ASTNode location) {
        String[] arguments = new String[2];
        String[] shortArguments = new String[2];
        arguments[0] = String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, false));
        shortArguments[0] = String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, true));
        if (superElement instanceof MethodBinding) {
            ReferenceBinding declaringClass = ((MethodBinding)superElement).declaringClass;
            arguments[1] = String.valueOf(CharOperation.concat(declaringClass.readableName(), superElement.shortReadableName(), '.'));
            shortArguments[1] = String.valueOf(CharOperation.concat(declaringClass.shortReadableName(), superElement.shortReadableName(), '.'));
        } else {
            arguments[1] = String.valueOf(superElement.readableName());
            shortArguments[1] = String.valueOf(superElement.shortReadableName());
        }
        this.handle(975, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }

    public void implicitObjectBoundNoNullDefault(TypeReference reference) {
        this.handle(971, NoArgument, NoArgument, 0, reference.sourceStart, reference.sourceEnd);
    }

    public void nonNullTypeVariableInUnannotatedBinary(LookupEnvironment environment, MethodBinding method, Expression expression, int providedSeverity) {
        TypeBinding declaredReturnType = method.original().returnType;
        int severity = this.computeSeverity(16778196);
        if ((severity & 0x501) == 0) {
            severity = providedSeverity;
        }
        if (declaredReturnType instanceof TypeVariableBinding) {
            TypeVariableBinding typeVariable = (TypeVariableBinding)declaredReturnType;
            ReferenceBinding declaringClass = method.declaringClass;
            char[][] nonNullName = this.options.nonNullAnnotationName;
            String shortNonNullName = String.valueOf(nonNullName[nonNullName.length - 1]);
            if (typeVariable.declaringElement instanceof ReferenceBinding) {
                String[] arguments = new String[]{shortNonNullName, String.valueOf(((TypeBinding)declaringClass).nullAnnotatedReadableName(this.options, false)), String.valueOf(declaringClass.original().readableName())};
                String[] shortArguments = new String[]{shortNonNullName, String.valueOf(((TypeBinding)declaringClass).nullAnnotatedReadableName(this.options, true)), String.valueOf(declaringClass.original().shortReadableName())};
                this.handle(16778196, arguments, shortArguments, severity, expression.sourceStart, expression.sourceEnd);
            } else if (typeVariable.declaringElement instanceof MethodBinding && method instanceof ParameterizedGenericMethodBinding) {
                TypeBinding substitution = ((ParameterizedGenericMethodBinding)method).typeArguments[typeVariable.rank];
                String[] arguments = new String[]{shortNonNullName, String.valueOf(typeVariable.readableName()), String.valueOf(substitution.nullAnnotatedReadableName(this.options, false)), String.valueOf(declaringClass.original().readableName())};
                String[] shortArguments = new String[]{shortNonNullName, String.valueOf(typeVariable.shortReadableName()), String.valueOf(substitution.nullAnnotatedReadableName(this.options, true)), String.valueOf(declaringClass.original().shortReadableName())};
                this.handle(16778197, arguments, shortArguments, severity, expression.sourceStart, expression.sourceEnd);
            }
        }
    }

    public void dereferencingNullableExpression(Expression expression) {
        if (expression instanceof MessageSend) {
            MessageSend send = (MessageSend)expression;
            this.messageSendPotentialNullReference(send.binding, send);
            return;
        }
        char[][] nullableName = this.options.nullableAnnotationName;
        char[] nullableShort = nullableName[nullableName.length - 1];
        String[] arguments = new String[]{String.valueOf(nullableShort)};
        int start = this.nodeSourceStart(expression);
        int end = this.nodeSourceEnd(expression);
        this.handle(536871864, arguments, arguments, start, end);
    }

    public void dereferencingNullableExpression(long positions, LookupEnvironment env) {
        char[][] nullableName = env.getNullableAnnotationName();
        char[] nullableShort = nullableName[nullableName.length - 1];
        String[] arguments = new String[]{String.valueOf(nullableShort)};
        this.handle(536871864, arguments, arguments, (int)(positions >>> 32), (int)(positions & 0xFFFFL));
    }

    public void onlyReferenceTypesInIntersectionCast(TypeReference typeReference) {
        this.handle(16778108, NoArgument, NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }

    public void illegalArrayTypeInIntersectionCast(TypeReference typeReference) {
        this.handle(16778109, NoArgument, NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }

    public void intersectionCastNotBelow18(TypeReference[] typeReferences) {
        int length = typeReferences.length;
        this.handle(16778107, NoArgument, NoArgument, typeReferences[0].sourceStart, typeReferences[length - 1].sourceEnd);
    }

    public void duplicateBoundInIntersectionCast(TypeReference typeReference) {
        this.handle(16778110, NoArgument, NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }

    public void lambdaRedeclaresArgument(Argument argument) {
        String[] arguments = new String[]{new String(argument.name)};
        this.handle(536871009, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }

    public void lambdaRedeclaresLocal(LocalDeclaration local) {
        String[] arguments = new String[]{new String(local.name)};
        this.handle(0x20000062, arguments, arguments, local.sourceStart, local.sourceEnd);
    }

    public void descriptorHasInvisibleType(FunctionalExpression expression, ReferenceBinding referenceBinding) {
        this.handle(99, new String[]{new String(referenceBinding.readableName())}, new String[]{new String(referenceBinding.shortReadableName())}, expression.sourceStart, expression.diagnosticsSourceEnd());
    }

    public void methodReferenceSwingsBothWays(ReferenceExpression expression, MethodBinding instanceMethod, MethodBinding nonInstanceMethod) {
        char[] selector = instanceMethod.selector;
        ReferenceBinding receiverType = instanceMethod.declaringClass;
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer shortBuffer1 = new StringBuffer();
        TypeBinding[] parameters = instanceMethod.parameters;
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            if (i != 0) {
                buffer1.append(", ");
                shortBuffer1.append(", ");
            }
            buffer1.append(new String(parameters[i].readableName()));
            shortBuffer1.append(new String(parameters[i].shortReadableName()));
            ++i;
        }
        StringBuffer buffer2 = new StringBuffer();
        StringBuffer shortBuffer2 = new StringBuffer();
        parameters = nonInstanceMethod.parameters;
        int i2 = 0;
        int length2 = parameters.length;
        while (i2 < length2) {
            if (i2 != 0) {
                buffer2.append(", ");
                shortBuffer2.append(", ");
            }
            buffer2.append(new String(parameters[i2].readableName()));
            shortBuffer2.append(new String(parameters[i2].shortReadableName()));
            ++i2;
        }
        int id = 603979899;
        this.handle(id, new String[]{new String(((Binding)receiverType).readableName()), new String(selector), buffer1.toString(), new String(selector), buffer2.toString()}, new String[]{new String(((Binding)receiverType).shortReadableName()), new String(selector), shortBuffer1.toString(), new String(selector), shortBuffer2.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void methodMustBeAccessedStatically(ReferenceExpression expression, MethodBinding nonInstanceMethod) {
        ReferenceBinding receiverType = nonInstanceMethod.declaringClass;
        char[] selector = nonInstanceMethod.selector;
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        TypeBinding[] parameters = nonInstanceMethod.parameters;
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
            ++i;
        }
        int id = 603979900;
        this.handle(id, new String[]{new String(((Binding)receiverType).readableName()), new String(selector), buffer.toString()}, new String[]{new String(((Binding)receiverType).shortReadableName()), new String(selector), shortBuffer.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void methodMustBeAccessedWithInstance(ReferenceExpression expression, MethodBinding instanceMethod) {
        ReferenceBinding receiverType = instanceMethod.declaringClass;
        char[] selector = instanceMethod.selector;
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        TypeBinding[] parameters = instanceMethod.parameters;
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
            ++i;
        }
        int id = 603979977;
        this.handle(id, new String[]{new String(((Binding)receiverType).readableName()), new String(selector), buffer.toString()}, new String[]{new String(((Binding)receiverType).shortReadableName()), new String(selector), shortBuffer.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void invalidArrayConstructorReference(ReferenceExpression expression, TypeBinding lhsType, TypeBinding[] parameters) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
            ++i;
        }
        int id = 603979901;
        this.handle(id, new String[]{new String(lhsType.readableName()), buffer.toString()}, new String[]{new String(lhsType.shortReadableName()), shortBuffer.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void constructedArrayIncompatible(ReferenceExpression expression, TypeBinding receiverType, TypeBinding returnType) {
        this.handle(603979902, new String[]{new String(receiverType.readableName()), new String(returnType.readableName())}, new String[]{new String(receiverType.shortReadableName()), new String(returnType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void danglingReference(ReferenceExpression expression, TypeBinding receiverType, char[] selector, TypeBinding[] descriptorParameters) {
        StringBuffer buffer = new StringBuffer();
        StringBuffer shortBuffer = new StringBuffer();
        TypeBinding[] parameters = descriptorParameters;
        int i = 0;
        int length = parameters.length;
        while (i < length) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
            ++i;
        }
        int id = 603979903;
        this.handle(id, new String[]{new String(receiverType.readableName()), new String(selector), buffer.toString()}, new String[]{new String(receiverType.shortReadableName()), new String(selector), shortBuffer.toString()}, expression.sourceStart, expression.sourceEnd);
    }

    public void unhandledException(TypeBinding exceptionType, ReferenceExpression location) {
        this.handle(16777384, new String[]{new String(exceptionType.readableName())}, new String[]{new String(exceptionType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void incompatibleReturnType(ReferenceExpression expression, MethodBinding method, TypeBinding returnType) {
        if (method.isConstructor()) {
            this.handle(553648793, new String[]{new String(method.declaringClass.readableName()), new String(returnType.readableName())}, new String[]{new String(method.declaringClass.shortReadableName()), new String(returnType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
        } else {
            StringBuffer buffer = new StringBuffer();
            StringBuffer shortBuffer = new StringBuffer();
            TypeBinding[] parameters = method.parameters;
            int i = 0;
            int length = parameters.length;
            while (i < length) {
                if (i != 0) {
                    buffer.append(", ");
                    shortBuffer.append(", ");
                }
                buffer.append(new String(parameters[i].readableName()));
                shortBuffer.append(new String(parameters[i].shortReadableName()));
                ++i;
            }
            String selector = new String(method.selector);
            this.handle(603979904, new String[]{selector, buffer.toString(), new String(method.declaringClass.readableName()), new String(method.returnType.readableName()), new String(returnType.readableName())}, new String[]{selector, shortBuffer.toString(), new String(method.declaringClass.shortReadableName()), new String(method.returnType.shortReadableName()), new String(returnType.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
        }
    }

    public void illegalSuperAccess(TypeBinding superType, TypeBinding directSuperType, ASTNode location) {
        if (directSuperType.problemId() == 29) {
            this.interfaceSuperInvocationNotBelow18((QualifiedSuperReference)location);
            return;
        }
        if (directSuperType.problemId() != 21) {
            this.needImplementation(location);
        }
        this.handle(16778270, new String[]{String.valueOf(superType.readableName()), String.valueOf(directSuperType.readableName())}, new String[]{String.valueOf(superType.shortReadableName()), String.valueOf(directSuperType.shortReadableName())}, location.sourceStart, location.sourceEnd);
    }

    public void illegalSuperCallBypassingOverride(InvocationSite location, MethodBinding targetMethod, ReferenceBinding overrider) {
        this.handle(67109919, new String[]{String.valueOf(targetMethod.readableName()), String.valueOf(targetMethod.declaringClass.readableName()), String.valueOf(overrider.readableName())}, new String[]{String.valueOf(targetMethod.shortReadableName()), String.valueOf(targetMethod.declaringClass.shortReadableName()), String.valueOf(overrider.shortReadableName())}, location.sourceStart(), location.sourceEnd());
    }

    public void disallowedTargetForContainerAnnotation(Annotation annotation, TypeBinding containerAnnotationType) {
        this.handle(16778114, new String[]{new String(annotation.resolvedType.readableName()), new String(containerAnnotationType.readableName())}, new String[]{new String(annotation.resolvedType.shortReadableName()), new String(containerAnnotationType.shortReadableName())}, annotation.sourceStart, annotation.sourceEnd);
    }

    public void typeAnnotationAtQualifiedName(Annotation annotation) {
        this.handle(1610613796, NoArgument, NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }

    public void genericInferenceError(String message, InvocationSite invocationSite) {
        this.genericInferenceProblem(message, invocationSite, 1);
    }

    public void genericInferenceProblem(String message, InvocationSite invocationSite, int severity) {
        String[] args = new String[]{message};
        int start = 0;
        int end = 0;
        if (invocationSite != null) {
            start = invocationSite.sourceStart();
            end = invocationSite.sourceEnd();
        }
        this.handle(1100, args, args, severity | 0x200, start, end);
    }

    public void uninternedIdentityComparison(EqualExpression expr, TypeBinding lhs, TypeBinding rhs, CompilationUnitDeclaration unit) {
        char[] lhsName = lhs.sourceName();
        char[] rhsName = rhs.sourceName();
        if (CharOperation.equals(lhsName, "VoidTypeBinding".toCharArray()) || CharOperation.equals(lhsName, "NullTypeBinding".toCharArray()) || CharOperation.equals(lhsName, "ProblemReferenceBinding".toCharArray())) {
            return;
        }
        if (CharOperation.equals(rhsName, "VoidTypeBinding".toCharArray()) || CharOperation.equals(rhsName, "NullTypeBinding".toCharArray()) || CharOperation.equals(rhsName, "ProblemReferenceBinding".toCharArray())) {
            return;
        }
        boolean[] validIdentityComparisonLines = unit.validIdentityComparisonLines;
        if (validIdentityComparisonLines != null) {
            int lineNumber;
            int n;
            int problemStartPosition = expr.left.sourceStart;
            if (problemStartPosition >= 0) {
                int[] lineEnds = unit.compilationResult().getLineSeparatorPositions();
                n = Util.getLineNumber(problemStartPosition, lineEnds, 0, lineEnds.length - 1);
            } else {
                n = lineNumber = 0;
            }
            if (lineNumber <= validIdentityComparisonLines.length && validIdentityComparisonLines[lineNumber - 1]) {
                return;
            }
        }
        this.handle(1610613180, new String[]{new String(lhs.readableName()), new String(rhs.readableName())}, new String[]{new String(lhs.shortReadableName()), new String(rhs.shortReadableName())}, expr.sourceStart, expr.sourceEnd);
    }

    public void invalidTypeArguments(TypeReference[] typeReference) {
        this.handle(83886666, NoArgument, NoArgument, typeReference[0].sourceStart, typeReference[typeReference.length - 1].sourceEnd);
    }

    public void invalidModule(ModuleReference ref) {
        this.handle(8389908, NoArgument, new String[]{CharOperation.charToString(ref.moduleName)}, ref.sourceStart, ref.sourceEnd);
    }

    public void missingModuleAddReads(char[] requiredModuleName) {
        String[] args = new String[]{new String(requiredModuleName)};
        this.handle(8389927, args, args, 0, 0);
    }

    public void invalidOpensStatement(OpensStatement statement, ModuleDeclaration module) {
        this.handle(8389923, NoArgument, new String[]{CharOperation.charToString(module.moduleName)}, statement.declarationSourceStart, statement.declarationSourceEnd);
    }

    public void invalidPackageReference(int problem, PackageVisibilityStatement ref) {
        this.handle(problem, NoArgument, new String[]{CharOperation.charToString(ref.pkgName)}, ref.computeSeverity(problem), ref.pkgRef.sourceStart, ref.pkgRef.sourceEnd);
    }

    public void exportingForeignPackage(PackageVisibilityStatement ref, ModuleBinding enclosingModule) {
        String[] arguments = new String[]{CharOperation.charToString(ref.pkgName), CharOperation.charToString(enclosingModule.moduleName)};
        this.handle(8389928, arguments, arguments, ref.pkgRef.sourceStart, ref.pkgRef.sourceEnd);
    }

    public void duplicateModuleReference(int problem, ModuleReference ref) {
        this.handle(problem, NoArgument, new String[]{CharOperation.charToString(ref.moduleName)}, ref.sourceStart, ref.sourceEnd);
    }

    public void duplicateTypeReference(int problem, TypeReference ref) {
        this.handle(problem, NoArgument, new String[]{ref.toString()}, ref.sourceStart, ref.sourceEnd);
    }

    public void duplicateTypeReference(int problem, TypeReference ref1, TypeReference ref2) {
        this.handle(problem, NoArgument, new String[]{ref1.toString(), ref2.toString()}, ref1.sourceStart, ref2.sourceEnd);
    }

    public void duplicateResourceReference(Reference ref) {
        this.handle(536872163, NoArgument, new String[]{ref.toString()}, 0, ref.sourceStart, ref.sourceEnd);
    }

    public void cyclicModuleDependency(ModuleBinding binding, ModuleReference ref) {
        this.handle(8389913, NoArgument, new String[]{CharOperation.charToString(binding.moduleName), CharOperation.charToString(ref.moduleName)}, ref.sourceStart, ref.sourceEnd);
    }

    public void invalidServiceRef(int problem, TypeReference type) {
        this.handle(problem, NoArgument, new String[]{CharOperation.charToString(type.resolvedType.readableName())}, type.sourceStart, type.sourceEnd);
    }

    public void unlikelyArgumentType(Expression argument, MethodBinding method, TypeBinding argumentType, TypeBinding receiverType, TypeConstants.DangerousMethod dangerousMethod) {
        this.handle(dangerousMethod == TypeConstants.DangerousMethod.Equals ? 1201 : 1200, new String[]{new String(argumentType.readableName()), new String(method.readableName()), new String(receiverType.readableName())}, new String[]{new String(argumentType.shortReadableName()), new String(method.shortReadableName()), new String(receiverType.shortReadableName())}, argument.sourceStart, argument.sourceEnd);
    }

    public void nonPublicTypeInAPI(TypeBinding type, int sourceStart, int sourceEnd) {
        this.handle(8390065, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, sourceStart, sourceEnd);
    }

    public void notExportedTypeInAPI(TypeBinding type, int sourceStart, int sourceEnd) {
        this.handle(8390066, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, sourceStart, sourceEnd);
    }

    public void missingRequiresTransitiveForTypeInAPI(ReferenceBinding referenceBinding, int sourceStart, int sourceEnd) {
        String moduleName = new String(referenceBinding.fPackage.enclosingModule.readableName());
        this.handle(8390067, new String[]{new String(referenceBinding.readableName()), moduleName}, new String[]{new String(referenceBinding.shortReadableName()), moduleName}, sourceStart, sourceEnd);
    }

    public void unnamedPackageInNamedModule(ModuleBinding module) {
        String[] args = new String[]{new String(module.readableName())};
        this.handle(8390068, args, args, 0, 0);
    }

    public void autoModuleWithUnstableName(ModuleReference moduleReference) {
        String[] args = new String[]{new String(moduleReference.moduleName)};
        this.handle(8390069, args, args, moduleReference.sourceStart, moduleReference.sourceEnd);
    }

    public void conflictingPackageInModules(char[][] wellKnownTypeName, CompilationUnitDeclaration compUnitDecl, Object location, char[] packageName, char[] expectedModuleName, char[] conflictingModuleName) {
        ReferenceContext savedContext = this.referenceContext;
        this.referenceContext = compUnitDecl;
        String[] arguments = new String[]{CharOperation.toString(wellKnownTypeName), new String(packageName), new String(expectedModuleName), new String(conflictingModuleName)};
        int start = 0;
        int end = 0;
        if (location != null) {
            if (location instanceof InvocationSite) {
                InvocationSite site = (InvocationSite)location;
                start = site.sourceStart();
                end = site.sourceEnd();
            } else if (location instanceof ASTNode) {
                ASTNode node = (ASTNode)location;
                start = node.sourceStart();
                end = node.sourceEnd();
            }
        }
        try {
            this.handle(8390070, arguments, arguments, start, end);
        }
        finally {
            this.referenceContext = savedContext;
        }
    }

    public void switchExpressionIncompatibleResultExpressions(SwitchExpression expression) {
        TypeBinding type = expression.resultExpressions.get((int)0).resolvedType;
        this.handle(16778916, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, expression.sourceStart, expression.sourceEnd);
    }

    public void switchExpressionEmptySwitchBlock(SwitchExpression expression) {
        this.handle(1073743525, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void switchExpressionNoResultExpressions(SwitchExpression expression) {
        this.handle(536872614, NoArgument, NoArgument, expression.sourceStart, expression.sourceEnd);
    }

    public void switchExpressionSwitchLabeledBlockCompletesNormally(Block block) {
        this.handle(536872615, NoArgument, NoArgument, block.sourceEnd - 1, block.sourceEnd);
    }

    public void switchExpressionLastStatementCompletesNormally(Statement stmt) {
        this.handle(536872615, NoArgument, NoArgument, stmt.sourceEnd - 1, stmt.sourceEnd);
    }

    public void switchExpressionIllegalLastStatement(Statement stmt) {
        this.handle(536872622, NoArgument, NoArgument, stmt.sourceStart, stmt.sourceEnd);
    }

    public void switchExpressionTrailingSwitchLabels(Statement stmt) {
        this.handle(536872617, NoArgument, NoArgument, stmt.sourceStart, stmt.sourceEnd);
    }

    public void switchExpressionMixedCase(ASTNode statement) {
        this.handle(1073743530, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionBreakNotAllowed(ASTNode statement) {
        this.handle(1073743535, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldUnqualifiedMethodWarning(ASTNode statement) {
        this.handle(1073743536, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldUnqualifiedMethodError(ASTNode statement) {
        this.handle(1073743537, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldOutsideSwitchExpression(ASTNode statement) {
        this.handle(1073743538, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldRestrictedGeneralWarning(ASTNode statement) {
        this.handle(536872627, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldIllegalStatement(ASTNode statement) {
        this.handle(536872628, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldTypeDeclarationWarning(ASTNode statement) {
        this.handle(536872629, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsYieldTypeDeclarationError(ASTNode statement) {
        this.handle(536872630, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void multiConstantCaseLabelsNotSupported(ASTNode statement) {
        this.handle(1073743543, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void arrowInCaseStatementsNotSupported(ASTNode statement) {
        this.handle(1073743544, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsNotSupported(ASTNode statement) {
        this.handle(1073743545, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsBreakOutOfSwitchExpression(ASTNode statement) {
        this.handle(1073743546, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsContinueOutOfSwitchExpression(ASTNode statement) {
        this.handle(1073743547, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void switchExpressionsReturnWithinSwitchExpression(ASTNode statement) {
        this.handle(1073743548, NoArgument, NoArgument, statement.sourceStart, statement.sourceEnd);
    }

    public void illegalModifierForLocalRecord(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16778978, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForInnerRecord(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16778946, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForRecord(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16778947, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void recordNonStaticFieldDeclarationInRecord(FieldDeclaration field) {
        this.handle(16778949, new String[]{new String(field.name)}, new String[]{new String(field.name)}, field.sourceStart, field.sourceEnd);
    }

    public void recordAccessorMethodHasThrowsClause(ASTNode methodDeclaration) {
        this.handle(16778950, NoArgument, NoArgument, methodDeclaration.sourceStart, methodDeclaration.sourceEnd);
    }

    public void recordCanonicalConstructorVisibilityReduced(AbstractMethodDeclaration methodDecl) {
        this.handle(16778952, new String[]{new String(methodDecl.selector)}, new String[]{new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCompactConstructorHasReturnStatement(ReturnStatement stmt) {
        this.handle(16778954, NoArgument, NoArgument, stmt.sourceStart, stmt.sourceEnd);
    }

    public void recordIllegalComponentNameInRecord(RecordComponent recComp, TypeDeclaration typeDecl) {
        this.handle(16778948, new String[]{new String(recComp.name), new String(typeDecl.name)}, new String[]{new String(recComp.name), new String(typeDecl.name)}, recComp.sourceStart, recComp.sourceEnd);
    }

    public void recordDuplicateComponent(RecordComponent recordComponent) {
        this.handle(16778955, new String[]{new String(recordComponent.name)}, new String[]{new String(recordComponent.name)}, recordComponent.sourceStart, recordComponent.sourceEnd);
    }

    public void recordIllegalNativeModifierInRecord(AbstractMethodDeclaration method) {
        this.handle(16778956, new String[]{new String(method.selector)}, new String[]{new String(method.selector)}, method.sourceStart, method.sourceEnd);
    }

    public void recordInstanceInitializerBlockInRecord(Initializer initializer) {
        this.handle(16778957, NoArgument, NoArgument, initializer.sourceStart, initializer.sourceEnd);
    }

    public void restrictedTypeName(char[] name, String compliance, int start, int end, int severity) {
        this.handle(16778958, new String[]{new String(name), compliance}, new String[]{new String(name), compliance}, severity, start, end);
    }

    public void recordIllegalAccessorReturnType(ASTNode returnType, TypeBinding type) {
        this.handle(16778959, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, returnType.sourceStart, returnType.sourceEnd);
    }

    public void recordAccessorMethodShouldNotBeGeneric(ASTNode methodDecl) {
        this.handle(16778960, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordAccessorMethodShouldBePublic(ASTNode methodDecl) {
        this.handle(16778961, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCanonicalConstructorShouldNotBeGeneric(AbstractMethodDeclaration methodDecl) {
        this.handle(16778962, new String[]{new String(methodDecl.selector)}, new String[]{new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCanonicalConstructorHasThrowsClause(AbstractMethodDeclaration methodDecl) {
        this.handle(16778951, new String[]{new String(methodDecl.selector)}, new String[]{new String(methodDecl.selector)}, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCanonicalConstructorHasReturnStatement(ASTNode methodDecl) {
        this.handle(16778963, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCanonicalConstructorHasExplicitConstructorCall(ASTNode methodDecl) {
        this.handle(16778964, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCompactConstructorHasExplicitConstructorCall(ASTNode methodDecl) {
        this.handle(16778965, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordNestedRecordInherentlyStatic(SourceTypeBinding type) {
        this.handle(16778966, NoArgument, NoArgument, type.sourceStart(), type.sourceEnd());
    }

    public void recordAccessorMethodShouldNotBeStatic(ASTNode methodDecl) {
        this.handle(16778967, NoArgument, NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }

    public void recordCannotExtendRecord(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16778968, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, superclass.sourceStart, superclass.sourceEnd);
    }

    public void recordComponentCannotBeVoid(RecordComponent arg) {
        String[] arguments = new String[]{new String(arg.name)};
        this.handle(16778969, arguments, arguments, arg.sourceStart, arg.sourceEnd);
    }

    public void recordIllegalVararg(RecordComponent argType, TypeDeclaration typeDecl) {
        String[] arguments = new String[]{CharOperation.toString(argType.type.getTypeName()), new String(typeDecl.name)};
        this.handle(16778970, arguments, arguments, argType.sourceStart, argType.sourceEnd);
    }

    public void recordStaticReferenceToOuterLocalVariable(LocalVariableBinding local, ASTNode node) {
        String[] arguments = new String[]{new String(local.readableName())};
        this.handle(16778971, arguments, arguments, node.sourceStart, node.sourceEnd);
    }

    public void recordComponentsCannotHaveModifiers(RecordComponent comp) {
        String[] arguments = new String[]{new String(comp.name)};
        this.handle(16778973, arguments, arguments, comp.sourceStart, comp.sourceEnd);
    }

    public void recordIllegalParameterNameInCanonicalConstructor(RecordComponentBinding comp, Argument arg) {
        this.handle(16778974, new String[]{new String(arg.name), new String(comp.name)}, new String[]{new String(arg.name), new String(comp.name)}, arg.sourceStart, arg.sourceEnd);
    }

    public void recordIllegalExplicitFinalFieldAssignInCompactConstructor(FieldBinding field, FieldReference fieldRef) {
        String[] arguments = new String[]{new String(field.name)};
        this.handle(16778975, arguments, arguments, fieldRef.sourceStart, fieldRef.sourceEnd);
    }

    public void recordMissingExplicitConstructorCallInNonCanonicalConstructor(ASTNode location) {
        this.handle(16778976, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
    }

    public void recordIllegalStaticModifierForLocalClassOrInterface(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(16778977, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void recordIllegalExtendedDimensionsForRecordComponent(AbstractVariableDeclaration aVarDecl) {
        this.handle(1610614499, NoArgument, NoArgument, aVarDecl.sourceStart, aVarDecl.sourceEnd);
    }

    public void localStaticsIllegalVisibilityModifierForInterfaceLocalType(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(2098917, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    public void illegalModifierForLocalEnumDeclaration(SourceTypeBinding type) {
        String[] arguments = new String[]{new String(type.sourceName())};
        this.handle(2098918, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }

    private void sealedMissingModifier(int problem, SourceTypeBinding type, TypeDeclaration typeDecl, TypeBinding superTypeBinding) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(problem, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void sealedMissingClassModifier(SourceTypeBinding type, TypeDeclaration typeDecl, TypeBinding superTypeBinding) {
        this.sealedMissingModifier(2099002, type, typeDecl, superTypeBinding);
    }

    public void sealedMissingInterfaceModifier(SourceTypeBinding type, TypeDeclaration typeDecl, TypeBinding superTypeBinding) {
        this.sealedMissingModifier(2099007, type, typeDecl, superTypeBinding);
    }

    public void sealedDisAllowedNonSealedModifierInClass(SourceTypeBinding type, TypeDeclaration typeDecl) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099003, new String[]{name}, new String[]{name}, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    private void sealedSuperTypeDoesNotPermit(int problem, SourceTypeBinding type, TypeReference superType, TypeBinding superTypeBinding) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(problem, new String[]{name, superTypeFullName}, new String[]{name, superTypeShortName}, superType.sourceStart, superType.sourceEnd);
    }

    public void sealedSuperClassDoesNotPermit(SourceTypeBinding type, TypeReference superType, TypeBinding superTypeBinding) {
        this.sealedSuperTypeDoesNotPermit(2099004, type, superType, superTypeBinding);
    }

    public void sealedSuperInterfaceDoesNotPermit(SourceTypeBinding type, TypeReference superType, TypeBinding superTypeBinding) {
        String keyword;
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        String string = keyword = type.isClass() ? new String("implements") : new String("extends");
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(2099005, new String[]{name, superTypeFullName, keyword}, new String[]{name, superTypeShortName, keyword}, superType.sourceStart, superType.sourceEnd);
    }

    public void sealedMissingSealedModifier(SourceTypeBinding type, ASTNode node) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099006, new String[]{name}, new String[]{name}, node.sourceStart, node.sourceEnd);
    }

    public void sealedDuplicateTypeInPermits(SourceTypeBinding type, TypeReference reference, ReferenceBinding superType) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        this.handle(2099008, new String[]{new String(superType.readableName()), new String(type.sourceName())}, new String[]{new String(superType.shortReadableName()), new String(type.sourceName())}, reference.sourceStart, reference.sourceEnd);
    }

    public void sealedNotDirectSuperClass(ReferenceBinding type, TypeReference reference, SourceTypeBinding superType) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        this.handle(2099009, new String[]{new String(type.sourceName()), new String(superType.readableName())}, new String[]{new String(type.sourceName()), new String(superType.readableName())}, reference.sourceStart, reference.sourceEnd);
    }

    public void sealedPermittedTypeOutsideOfModule(ReferenceBinding permType, SourceTypeBinding type, ASTNode node, ModuleBinding moduleBinding) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String permTypeName = new String(permType.sourceName);
        String name = new String(type.sourceName());
        String moduleName = new String(moduleBinding.name());
        String[] arguments = new String[]{permTypeName, moduleName, name};
        this.handle(2099010, arguments, arguments, node.sourceStart, node.sourceEnd);
    }

    public void sealedPermittedTypeOutsideOfModule(SourceTypeBinding type, ASTNode node) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099010, new String[]{name}, new String[]{name}, node.sourceStart, node.sourceEnd);
    }

    public void sealedPermittedTypeOutsideOfPackage(ReferenceBinding permType, SourceTypeBinding type, ASTNode node, PackageBinding packageBinding) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String permTypeName = new String(permType.sourceName);
        String name = new String(type.sourceName());
        String packageName = packageBinding.compoundName == CharOperation.NO_CHAR_CHAR ? "default" : CharOperation.toString(packageBinding.compoundName);
        String[] arguments = new String[]{permTypeName, packageName, name};
        this.handle(2099011, arguments, arguments, node.sourceStart, node.sourceEnd);
    }

    public void sealedSealedTypeMissingPermits(SourceTypeBinding type, ASTNode node) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099012, new String[]{name}, new String[]{name}, node.sourceStart, node.sourceEnd);
    }

    public void sealedInterfaceIsSealedAndNonSealed(SourceTypeBinding type, ASTNode node) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099013, new String[]{name}, new String[]{name}, node.sourceStart, node.sourceEnd);
    }

    public void sealedDisAllowedNonSealedModifierInInterface(SourceTypeBinding type, TypeDeclaration typeDecl) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        this.handle(2099014, new String[]{name}, new String[]{name}, typeDecl.sourceStart, typeDecl.sourceEnd);
    }

    public void sealedNotDirectSuperInterface(ReferenceBinding type, TypeReference reference, SourceTypeBinding superType) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        this.handle(2099015, new String[]{new String(type.sourceName()), new String(superType.readableName())}, new String[]{new String(type.sourceName()), new String(superType.readableName())}, reference.sourceStart, reference.sourceEnd);
    }

    public void sealedLocalDirectSuperTypeSealed(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        String name = new String(type.sourceName());
        String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(2099016, new String[]{superTypeFullName, name}, new String[]{superTypeShortName, name}, superclass.sourceStart, superclass.sourceEnd);
    }

    public void sealedAnonymousClassCannotExtendSealedType(TypeReference reference, TypeBinding type) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        this.handle(2099017, new String[]{new String(type.readableName())}, new String[]{new String(type.shortReadableName())}, reference.sourceStart, reference.sourceEnd);
    }
}

