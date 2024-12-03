/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.AddConcatExpression;
import freemarker.core.AndExpression;
import freemarker.core.ArithmeticExpression;
import freemarker.core.Assignment;
import freemarker.core.AssignmentInstruction;
import freemarker.core.AttemptBlock;
import freemarker.core.AutoEscBlock;
import freemarker.core.BlockAssignment;
import freemarker.core.BodyInstruction;
import freemarker.core.BooleanLiteral;
import freemarker.core.BreakInstruction;
import freemarker.core.BugException;
import freemarker.core.BuiltIn;
import freemarker.core.BuiltInBannedWhenAutoEscaping;
import freemarker.core.BuiltInForLoopVariable;
import freemarker.core.BuiltInWithDirectCallOptimization;
import freemarker.core.BuiltInWithParseTimeParameters;
import freemarker.core.BuiltInsForCallables;
import freemarker.core.BuiltinVariable;
import freemarker.core.Case;
import freemarker.core.CombinedMarkupOutputFormat;
import freemarker.core.Comment;
import freemarker.core.ComparisonExpression;
import freemarker.core.CompressedBlock;
import freemarker.core.ConditionalBlock;
import freemarker.core.ContinueInstruction;
import freemarker.core.DefaultToExpression;
import freemarker.core.DollarVariable;
import freemarker.core.Dot;
import freemarker.core.DynamicKeyName;
import freemarker.core.ElseOfList;
import freemarker.core.EscapeBlock;
import freemarker.core.ExistsExpression;
import freemarker.core.Expression;
import freemarker.core.FMParserConstants;
import freemarker.core.FMParserTokenManager;
import freemarker.core.FallbackInstruction;
import freemarker.core.FlushInstruction;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.HashLiteral;
import freemarker.core.Identifier;
import freemarker.core.IfBlock;
import freemarker.core.Include;
import freemarker.core.Interpolation;
import freemarker.core.Items;
import freemarker.core.IteratorBlock;
import freemarker.core.LegacyConstructorParserConfiguration;
import freemarker.core.LibraryLoad;
import freemarker.core.ListElseContainer;
import freemarker.core.ListLiteral;
import freemarker.core.LocalLambdaExpression;
import freemarker.core.Macro;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.MarkupOutputFormatBoundBuiltIn;
import freemarker.core.MethodCall;
import freemarker.core.MixedContent;
import freemarker.core.NoAutoEscBlock;
import freemarker.core.NoEscapeBlock;
import freemarker.core.NotExpression;
import freemarker.core.NumberLiteral;
import freemarker.core.NumericalOutput;
import freemarker.core.OrExpression;
import freemarker.core.OutputFormat;
import freemarker.core.OutputFormatBlock;
import freemarker.core.OutputFormatBoundBuiltIn;
import freemarker.core.ParentheticalExpression;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.PropertySetting;
import freemarker.core.Range;
import freemarker.core.RecoveryBlock;
import freemarker.core.RecurseNode;
import freemarker.core.ReturnInstruction;
import freemarker.core.Sep;
import freemarker.core.SimpleCharStream;
import freemarker.core.SpecialBuiltIn;
import freemarker.core.StopInstruction;
import freemarker.core.StringLiteral;
import freemarker.core.SwitchBlock;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core.TemplateObject;
import freemarker.core.TextBlock;
import freemarker.core.Token;
import freemarker.core.TransformBlock;
import freemarker.core.TrimInstruction;
import freemarker.core.UnaryPlusMinusExpression;
import freemarker.core.UnifiedCall;
import freemarker.core.UnregisteredOutputFormatException;
import freemarker.core.VisitNode;
import freemarker.core.XMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.DeepUnwrap;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class FMParser
implements FMParserConstants {
    private static final int ITERATOR_BLOCK_KIND_LIST = 0;
    private static final int ITERATOR_BLOCK_KIND_FOREACH = 1;
    private static final int ITERATOR_BLOCK_KIND_ITEMS = 2;
    private static final int ITERATOR_BLOCK_KIND_USER_DIRECTIVE = 3;
    private Template template;
    private boolean stripWhitespace;
    private boolean stripText;
    private boolean preventStrippings;
    private int incompatibleImprovements;
    private OutputFormat outputFormat;
    private int autoEscapingPolicy;
    private boolean autoEscaping;
    private ParserConfiguration pCfg;
    private List<ParserIteratorBlockContext> iteratorBlockContexts;
    private int breakableDirectiveNesting;
    private int continuableDirectiveNesting;
    private boolean inMacro;
    private boolean inFunction;
    private boolean requireArgsSpecialVariable;
    private LinkedList escapes = new LinkedList();
    private int mixedContentNesting;
    public FMParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[116];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private static int[] jj_la1_3;
    private static int[] jj_la1_4;
    private final JJCalls[] jj_2_rtns = new JJCalls[17];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static FMParser createExpressionParser(String s) {
        SimpleCharStream scs = new SimpleCharStream(new StringReader(s), 1, 1, s.length());
        FMParserTokenManager token_source = new FMParserTokenManager(scs);
        token_source.SwitchTo(2);
        FMParser parser = new FMParser(token_source);
        token_source.setParser(parser);
        return parser;
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace) {
        this(template, reader, strictSyntaxMode, stripWhitespace, 0);
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax) {
        this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, Configuration.PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS);
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax, int incompatibleImprovements) {
        this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, 10, incompatibleImprovements);
    }

    public FMParser(String template) {
        this(FMParser.dummyTemplate(), new StringReader(template), true, true);
    }

    private static Template dummyTemplate() {
        try {
            return new Template(null, new StringReader(""), Configuration.getDefaultConfiguration());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create dummy template", e);
        }
    }

    public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int namingConvention, int incompatibleImprovements) {
        this(template, reader, new LegacyConstructorParserConfiguration(strictSyntaxMode, whitespaceStripping, tagSyntax, 20, namingConvention, template != null ? template.getParserConfiguration().getAutoEscapingPolicy() : 21, template != null ? template.getParserConfiguration().getOutputFormat() : null, template != null ? Boolean.valueOf(template.getParserConfiguration().getRecognizeStandardFileExtensions()) : null, template != null ? Integer.valueOf(template.getParserConfiguration().getTabSize()) : null, new Version(incompatibleImprovements), template != null ? template.getArithmeticEngine() : null));
    }

    public FMParser(Template template, Reader reader, ParserConfiguration pCfg) {
        this(template, true, FMParser.readerToTokenManager(reader, pCfg), pCfg);
    }

    private static FMParserTokenManager readerToTokenManager(Reader reader, ParserConfiguration pCfg) {
        SimpleCharStream simpleCharStream = new SimpleCharStream(reader, 1, 1);
        simpleCharStream.setTabSize(pCfg.getTabSize());
        return new FMParserTokenManager(simpleCharStream);
    }

    public FMParser(Template template, boolean newTemplate, FMParserTokenManager tkMan, ParserConfiguration pCfg) {
        this(tkMan);
        OutputFormat outputFormatFromExt;
        int incompatibleImprovements;
        NullArgumentException.check(pCfg);
        this.pCfg = pCfg;
        NullArgumentException.check(template);
        this.template = template;
        if (pCfg instanceof LegacyConstructorParserConfiguration) {
            LegacyConstructorParserConfiguration lpCfg = (LegacyConstructorParserConfiguration)pCfg;
            lpCfg.setArithmeticEngineIfNotSet(template.getArithmeticEngine());
            lpCfg.setAutoEscapingPolicyIfNotSet(template.getConfiguration().getAutoEscapingPolicy());
            lpCfg.setOutputFormatIfNotSet(template.getOutputFormat());
            lpCfg.setRecognizeStandardFileExtensionsIfNotSet(template.getParserConfiguration().getRecognizeStandardFileExtensions());
            lpCfg.setTabSizeIfNotSet(template.getParserConfiguration().getTabSize());
        }
        this.token_source.incompatibleImprovements = incompatibleImprovements = pCfg.getIncompatibleImprovements().intValue();
        this.incompatibleImprovements = incompatibleImprovements;
        if (!pCfg.getRecognizeStandardFileExtensions() || (outputFormatFromExt = this.getFormatFromStdFileExt()) == null) {
            this.autoEscapingPolicy = pCfg.getAutoEscapingPolicy();
            this.outputFormat = pCfg.getOutputFormat();
        } else {
            this.autoEscapingPolicy = 21;
            this.outputFormat = outputFormatFromExt;
        }
        this.recalculateAutoEscapingField();
        this.token_source.setParser(this);
        this.token_source.strictSyntaxMode = pCfg.getStrictSyntaxMode();
        int tagSyntax = pCfg.getTagSyntax();
        switch (tagSyntax) {
            case 0: {
                this.token_source.autodetectTagSyntax = true;
                break;
            }
            case 1: {
                this.token_source.squBracTagSyntax = false;
                break;
            }
            case 2: {
                this.token_source.squBracTagSyntax = true;
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal argument for tagSyntax: " + tagSyntax);
            }
        }
        this.token_source.interpolationSyntax = pCfg.getInterpolationSyntax();
        int namingConvention = pCfg.getNamingConvention();
        switch (namingConvention) {
            case 10: 
            case 11: 
            case 12: {
                this.token_source.initialNamingConvention = namingConvention;
                this.token_source.namingConvention = namingConvention;
                break;
            }
            default: {
                throw new IllegalArgumentException("Illegal argument for namingConvention: " + namingConvention);
            }
        }
        this.stripWhitespace = pCfg.getWhitespaceStripping();
        if (newTemplate) {
            _TemplateAPI.setAutoEscaping(template, this.autoEscaping);
            _TemplateAPI.setOutputFormat(template, this.outputFormat);
        }
    }

    void setupStringLiteralMode(FMParser parentParser, OutputFormat outputFormat) {
        FMParserTokenManager parentTokenSource = parentParser.token_source;
        this.token_source.initialNamingConvention = parentTokenSource.initialNamingConvention;
        this.token_source.namingConvention = parentTokenSource.namingConvention;
        this.token_source.namingConventionEstabilisher = parentTokenSource.namingConventionEstabilisher;
        this.token_source.SwitchTo(1);
        this.outputFormat = outputFormat;
        this.recalculateAutoEscapingField();
        if (this.incompatibleImprovements < _VersionInts.V_2_3_24) {
            this.incompatibleImprovements = _VersionInts.V_2_3_0;
        }
        this.iteratorBlockContexts = parentParser.iteratorBlockContexts;
    }

    void tearDownStringLiteralMode(FMParser parentParser) {
        FMParserTokenManager parentTokenSource = parentParser.token_source;
        parentTokenSource.namingConvention = this.token_source.namingConvention;
        parentTokenSource.namingConventionEstabilisher = this.token_source.namingConventionEstabilisher;
    }

    void setPreventStrippings(boolean preventStrippings) {
        this.preventStrippings = preventStrippings;
    }

    private OutputFormat getFormatFromStdFileExt() {
        String sourceName = this.template.getSourceName();
        if (sourceName == null) {
            return null;
        }
        int ln = sourceName.length();
        if (ln < 5) {
            return null;
        }
        char c = sourceName.charAt(ln - 5);
        if (c != '.') {
            return null;
        }
        c = sourceName.charAt(ln - 4);
        if (c != 'f' && c != 'F') {
            return null;
        }
        c = sourceName.charAt(ln - 3);
        if (c != 't' && c != 'T') {
            return null;
        }
        c = sourceName.charAt(ln - 2);
        if (c != 'l' && c != 'L') {
            return null;
        }
        c = sourceName.charAt(ln - 1);
        try {
            if (c == 'h' || c == 'H') {
                return this.template.getConfiguration().getOutputFormat(HTMLOutputFormat.INSTANCE.getName());
            }
            if (c == 'x' || c == 'X') {
                return this.template.getConfiguration().getOutputFormat(XMLOutputFormat.INSTANCE.getName());
            }
        }
        catch (UnregisteredOutputFormatException e) {
            throw new BugException("Unregistered std format", e);
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void recalculateAutoEscapingField() {
        if (this.outputFormat instanceof MarkupOutputFormat) {
            if (this.autoEscapingPolicy == 21) {
                this.autoEscaping = ((MarkupOutputFormat)this.outputFormat).isAutoEscapedByDefault();
                return;
            } else if (this.autoEscapingPolicy == 22) {
                this.autoEscaping = true;
                return;
            } else {
                if (this.autoEscapingPolicy != 20) throw new IllegalStateException("Unhandled autoEscaping ENUM: " + this.autoEscapingPolicy);
                this.autoEscaping = false;
            }
            return;
        } else {
            this.autoEscaping = false;
        }
    }

    MarkupOutputFormat getMarkupOutputFormat() {
        return this.outputFormat instanceof MarkupOutputFormat ? (MarkupOutputFormat)this.outputFormat : null;
    }

    public int _getLastTagSyntax() {
        return this.token_source.squBracTagSyntax ? 2 : 1;
    }

    public int _getLastNamingConvention() {
        return this.token_source.namingConvention;
    }

    private void notStringLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof StringLiteral) {
            throw new ParseException("Found string literal: " + exp + ". Expecting: " + expected, exp);
        }
    }

    private void notNumberLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof NumberLiteral) {
            throw new ParseException("Found number literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void notBooleanLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof BooleanLiteral) {
            throw new ParseException("Found: " + exp.getCanonicalForm() + " literal. Expecting " + expected, exp);
        }
    }

    private void notHashLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof HashLiteral) {
            throw new ParseException("Found hash literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void notListLiteral(Expression exp, String expected) throws ParseException {
        if (exp instanceof ListLiteral) {
            throw new ParseException("Found list literal: " + exp.getCanonicalForm() + ". Expecting " + expected, exp);
        }
    }

    private void numberLiteralOnly(Expression exp) throws ParseException {
        this.notStringLiteral(exp, "number");
        this.notListLiteral(exp, "number");
        this.notHashLiteral(exp, "number");
        this.notBooleanLiteral(exp, "number");
    }

    private void stringLiteralOnly(Expression exp) throws ParseException {
        this.notNumberLiteral(exp, "string");
        this.notListLiteral(exp, "string");
        this.notHashLiteral(exp, "string");
        this.notBooleanLiteral(exp, "string");
    }

    private void booleanLiteralOnly(Expression exp) throws ParseException {
        this.notStringLiteral(exp, "boolean (true/false)");
        this.notListLiteral(exp, "boolean (true/false)");
        this.notHashLiteral(exp, "boolean (true/false)");
        this.notNumberLiteral(exp, "boolean (true/false)");
    }

    private Expression escapedExpression(Expression exp) throws ParseException {
        if (!this.escapes.isEmpty()) {
            return ((EscapeBlock)this.escapes.getFirst()).doEscape(exp);
        }
        return exp;
    }

    private boolean getBoolean(Expression exp, boolean legacyCompat) throws ParseException {
        TemplateModel tm = null;
        try {
            tm = exp.eval(null);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage() + "\nCould not evaluate expression: " + exp.getCanonicalForm(), exp, e);
        }
        if (tm instanceof TemplateBooleanModel) {
            try {
                return ((TemplateBooleanModel)tm).getAsBoolean();
            }
            catch (TemplateModelException e) {
                // empty catch block
            }
        }
        if (legacyCompat && tm instanceof TemplateScalarModel) {
            try {
                return StringUtil.getYesNo(((TemplateScalarModel)tm).getAsString());
            }
            catch (Exception e) {
                throw new ParseException(e.getMessage() + "\nExpecting boolean (true/false), found: " + exp.getCanonicalForm(), exp);
            }
        }
        throw new ParseException("Expecting boolean (true/false) parameter", exp);
    }

    void checkCurrentOutputFormatCanEscape(Token start) throws ParseException {
        if (!(this.outputFormat instanceof MarkupOutputFormat)) {
            throw new ParseException("The current output format can't do escaping: " + this.outputFormat, this.template, start);
        }
    }

    private ParserIteratorBlockContext pushIteratorBlockContext() {
        if (this.iteratorBlockContexts == null) {
            this.iteratorBlockContexts = new ArrayList<ParserIteratorBlockContext>(4);
        }
        ParserIteratorBlockContext newCtx = new ParserIteratorBlockContext();
        this.iteratorBlockContexts.add(newCtx);
        return newCtx;
    }

    private void popIteratorBlockContext() {
        this.iteratorBlockContexts.remove(this.iteratorBlockContexts.size() - 1);
    }

    private ParserIteratorBlockContext peekIteratorBlockContext() {
        int size = this.iteratorBlockContexts != null ? this.iteratorBlockContexts.size() : 0;
        return size != 0 ? this.iteratorBlockContexts.get(size - 1) : null;
    }

    private void checkLoopVariableBuiltInLHO(String loopVarName, Expression lhoExp, Token biName) throws ParseException {
        int size = this.iteratorBlockContexts != null ? this.iteratorBlockContexts.size() : 0;
        for (int i = size - 1; i >= 0; --i) {
            ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(i);
            if (!loopVarName.equals(ctx.loopVarName) && !loopVarName.equals(ctx.loopVar2Name)) continue;
            if (ctx.kind == 3) {
                throw new ParseException("The left hand operand of ?" + biName.image + " can't be the loop variable of an user defined directive: " + loopVarName, lhoExp);
            }
            return;
        }
        throw new ParseException("The left hand operand of ?" + biName.image + " must be a loop variable, but there's no loop variable in scope with this name: " + loopVarName, lhoExp);
    }

    private String forEachDirectiveSymbol() {
        return this.token_source.namingConvention == 12 ? "#forEach" : "#foreach";
    }

    public final Expression Expression() throws ParseException {
        Expression exp = this.OrExpression();
        if ("" != null) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression PrimaryExpression() throws ParseException {
        Expression exp;
        block13: {
            exp = this.AtomicExpression();
            block11: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 99: 
                    case 103: 
                    case 104: 
                    case 129: 
                    case 133: 
                    case 135: 
                    case 153: {
                        break;
                    }
                    default: {
                        this.jj_la1[0] = this.jj_gen;
                        break block13;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 99: {
                        exp = this.DotVariable(exp);
                        continue block11;
                    }
                    case 133: {
                        exp = this.DynamicKey(exp);
                        continue block11;
                    }
                    case 135: {
                        exp = this.MethodArgs(exp);
                        continue block11;
                    }
                    case 103: {
                        exp = this.BuiltIn(exp);
                        continue block11;
                    }
                    case 129: 
                    case 153: {
                        exp = this.DefaultTo(exp);
                        continue block11;
                    }
                    case 104: {
                        exp = this.Exists(exp);
                        continue block11;
                    }
                }
                break;
            }
            this.jj_la1[1] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        if ("" != null) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression AtomicExpression() throws ParseException {
        Expression exp;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 97: 
            case 98: {
                exp = this.NumberLiteral();
                break;
            }
            case 137: {
                exp = this.HashLiteral();
                break;
            }
            case 93: 
            case 94: {
                exp = this.StringLiteral(true);
                break;
            }
            case 95: 
            case 96: {
                exp = this.BooleanLiteral();
                break;
            }
            case 133: {
                exp = this.ListLiteral();
                break;
            }
            case 142: {
                exp = this.Identifier();
                break;
            }
            case 135: {
                exp = this.Parenthesis();
                break;
            }
            case 99: {
                exp = this.BuiltinVariable();
                break;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression Parenthesis() throws ParseException {
        Token start = this.jj_consume_token(135);
        Expression exp = this.Expression();
        Token end = this.jj_consume_token(136);
        ParentheticalExpression result = new ParentheticalExpression(exp);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression UnaryExpression() throws ParseException {
        Expression result;
        boolean haveNot = false;
        Object t = null;
        Object start = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 120: 
            case 121: {
                result = this.UnaryPlusMinusExpression();
                break;
            }
            case 129: {
                result = this.NotExpression();
                break;
            }
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 133: 
            case 135: 
            case 137: 
            case 142: {
                result = this.PrimaryExpression();
                break;
            }
            default: {
                this.jj_la1[3] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression NotExpression() throws ParseException {
        NotExpression result = null;
        ArrayList<Token> nots = new ArrayList<Token>();
        block3: while (true) {
            Token t = this.jj_consume_token(129);
            nots.add(t);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 129: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[4] = this.jj_gen;
        Expression exp = this.PrimaryExpression();
        for (int i = 0; i < nots.size(); ++i) {
            result = new NotExpression(exp);
            Token tok = (Token)nots.get(nots.size() - i - 1);
            result.setLocation(this.template, tok, (TemplateObject)exp);
            exp = result;
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression UnaryPlusMinusExpression() throws ParseException {
        Token t;
        boolean isMinus = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 120: {
                t = this.jj_consume_token(120);
                break;
            }
            case 121: {
                t = this.jj_consume_token(121);
                isMinus = true;
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        Expression exp = this.PrimaryExpression();
        UnaryPlusMinusExpression result = new UnaryPlusMinusExpression(exp, isMinus);
        result.setLocation(this.template, t, (TemplateObject)exp);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression AdditiveExpression() throws ParseException {
        Expression lhs;
        Expression result = lhs = this.MultiplicativeExpression();
        while (this.jj_2_1(Integer.MAX_VALUE)) {
            boolean plus;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 120: {
                    this.jj_consume_token(120);
                    plus = true;
                    break;
                }
                case 121: {
                    this.jj_consume_token(121);
                    plus = false;
                    break;
                }
                default: {
                    this.jj_la1[6] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            Expression rhs = this.MultiplicativeExpression();
            if (plus) {
                result = new AddConcatExpression(lhs, rhs);
            } else {
                this.numberLiteralOnly(lhs);
                this.numberLiteralOnly(rhs);
                result = new ArithmeticExpression(lhs, rhs, 0);
            }
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
            lhs = result;
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression MultiplicativeExpression() throws ParseException {
        Expression lhs;
        int operation = 1;
        Expression result = lhs = this.UnaryExpression();
        while (this.jj_2_2(Integer.MAX_VALUE)) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 122: {
                    this.jj_consume_token(122);
                    operation = 1;
                    break;
                }
                case 125: {
                    this.jj_consume_token(125);
                    operation = 2;
                    break;
                }
                case 126: {
                    this.jj_consume_token(126);
                    operation = 3;
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            Expression rhs = this.UnaryExpression();
            this.numberLiteralOnly(lhs);
            this.numberLiteralOnly(rhs);
            result = new ArithmeticExpression(lhs, rhs, operation);
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
            lhs = result;
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression EqualityExpression() throws ParseException {
        Expression lhs;
        Expression result = lhs = this.RelationalExpression();
        if (this.jj_2_3(Integer.MAX_VALUE)) {
            Token t;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 107: {
                    t = this.jj_consume_token(107);
                    break;
                }
                case 105: {
                    t = this.jj_consume_token(105);
                    break;
                }
                case 106: {
                    t = this.jj_consume_token(106);
                    break;
                }
                default: {
                    this.jj_la1[8] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            Expression rhs = this.RelationalExpression();
            this.notHashLiteral(lhs, "different type for equality check");
            this.notHashLiteral(rhs, "different type for equality check");
            this.notListLiteral(lhs, "different type for equality check");
            this.notListLiteral(rhs, "different type for equality check");
            result = new ComparisonExpression(lhs, rhs, t.image);
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression RelationalExpression() throws ParseException {
        Expression lhs;
        Expression result = lhs = this.RangeExpression();
        if (this.jj_2_4(Integer.MAX_VALUE)) {
            Token t;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 151: {
                    t = this.jj_consume_token(151);
                    break;
                }
                case 118: {
                    t = this.jj_consume_token(118);
                    break;
                }
                case 150: {
                    t = this.jj_consume_token(150);
                    break;
                }
                case 117: {
                    t = this.jj_consume_token(117);
                    break;
                }
                case 116: {
                    t = this.jj_consume_token(116);
                    break;
                }
                case 115: {
                    t = this.jj_consume_token(115);
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            Expression rhs = this.RangeExpression();
            this.numberLiteralOnly(lhs);
            this.numberLiteralOnly(rhs);
            result = new ComparisonExpression(lhs, rhs, t.image);
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression RangeExpression() throws ParseException {
        Expression lhs;
        Expression rhs = null;
        Token dotDot = null;
        Expression result = lhs = this.AdditiveExpression();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 100: 
            case 101: 
            case 102: {
                int endType;
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 101: 
                    case 102: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 101: {
                                this.jj_consume_token(101);
                                endType = 1;
                                break;
                            }
                            case 102: {
                                this.jj_consume_token(102);
                                endType = 3;
                                break;
                            }
                            default: {
                                this.jj_la1[10] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        rhs = this.AdditiveExpression();
                        break;
                    }
                    case 100: {
                        dotDot = this.jj_consume_token(100);
                        endType = 2;
                        if (!this.jj_2_5(Integer.MAX_VALUE)) break;
                        rhs = this.AdditiveExpression();
                        endType = 0;
                        break;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.numberLiteralOnly(lhs);
                if (rhs != null) {
                    this.numberLiteralOnly(rhs);
                }
                Range range = new Range(lhs, rhs, endType);
                if (rhs != null) {
                    range.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
                } else {
                    range.setLocation(this.template, (TemplateObject)lhs, dotDot);
                }
                result = range;
                break;
            }
            default: {
                this.jj_la1[12] = this.jj_gen;
            }
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression AndExpression() throws ParseException {
        Expression lhs;
        Expression result = lhs = this.EqualityExpression();
        while (this.jj_2_6(Integer.MAX_VALUE)) {
            this.jj_consume_token(127);
            Expression rhs = this.EqualityExpression();
            this.booleanLiteralOnly(lhs);
            this.booleanLiteralOnly(rhs);
            result = new AndExpression(lhs, rhs);
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
            lhs = result;
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression OrExpression() throws ParseException {
        Expression lhs;
        Expression result = lhs = this.AndExpression();
        while (this.jj_2_7(Integer.MAX_VALUE)) {
            this.jj_consume_token(128);
            Expression rhs = this.AndExpression();
            this.booleanLiteralOnly(lhs);
            this.booleanLiteralOnly(rhs);
            result = new OrExpression(lhs, rhs);
            result.setLocation(this.template, (TemplateObject)lhs, (TemplateObject)rhs);
            lhs = result;
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ListLiteral ListLiteral() throws ParseException {
        ArrayList values = new ArrayList();
        Token begin = this.jj_consume_token(133);
        values = this.PositionalArgs();
        Token end = this.jj_consume_token(134);
        ListLiteral result = new ListLiteral(values);
        result.setLocation(this.template, begin, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression NumberLiteral() throws ParseException {
        Token t;
        Token op = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 97: {
                t = this.jj_consume_token(97);
                break;
            }
            case 98: {
                t = this.jj_consume_token(98);
                break;
            }
            default: {
                this.jj_la1[13] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        String s = t.image;
        NumberLiteral result = new NumberLiteral(this.pCfg.getArithmeticEngine().toNumber(s));
        Token startToken = op != null ? op : t;
        result.setLocation(this.template, startToken, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Identifier Identifier() throws ParseException {
        Token t = this.jj_consume_token(142);
        Identifier id = new Identifier(t.image);
        id.setLocation(this.template, t, t);
        if ("" != null) {
            return id;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression IdentifierOrStringLiteral() throws ParseException {
        Expression exp;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 142: {
                exp = this.Identifier();
                break;
            }
            case 93: 
            case 94: {
                exp = this.StringLiteral(false);
                break;
            }
            default: {
                this.jj_la1[14] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return exp;
        }
        throw new Error("Missing return statement in function");
    }

    public final BuiltinVariable BuiltinVariable() throws ParseException {
        TemplateModel parseTimeValue;
        Token dot = this.jj_consume_token(99);
        Token name = this.jj_consume_token(142);
        BuiltinVariable result = null;
        this.token_source.checkNamingConvention(name);
        String nameStr = name.image;
        if (nameStr.equals("output_format") || nameStr.equals("outputFormat")) {
            parseTimeValue = new SimpleScalar(this.outputFormat.getName());
        } else if (nameStr.equals("auto_esc") || nameStr.equals("autoEsc")) {
            parseTimeValue = this.autoEscaping ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        } else if (nameStr.equals("args")) {
            if (!this.inMacro && !this.inFunction) {
                throw new ParseException("The \"args\" special variable must be inside a macro or function in the template source code.", this.template, name);
            }
            this.requireArgsSpecialVariable = true;
            parseTimeValue = null;
        } else {
            parseTimeValue = null;
        }
        result = new BuiltinVariable(name, this.token_source, parseTimeValue);
        result.setLocation(this.template, dot, name);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DefaultTo(Expression exp) throws ParseException {
        Token t;
        Expression rhs = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 153: {
                t = this.jj_consume_token(153);
                break;
            }
            case 129: {
                t = this.jj_consume_token(129);
                if (!this.jj_2_8(Integer.MAX_VALUE)) break;
                rhs = this.Expression();
                break;
            }
            default: {
                this.jj_la1[15] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        DefaultToExpression result = new DefaultToExpression(exp, rhs);
        if (rhs == null) {
            result.setLocation(this.template, exp.beginColumn, exp.beginLine, t.beginColumn, t.beginLine);
        } else {
            result.setLocation(this.template, (TemplateObject)exp, (TemplateObject)rhs);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression Exists(Expression exp) throws ParseException {
        Token t = this.jj_consume_token(104);
        ExistsExpression result = new ExistsExpression(exp);
        result.setLocation(this.template, (TemplateObject)exp, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression BuiltIn(Expression lhoExp) throws ParseException {
        Token closeParen;
        Token openParen;
        Token t = null;
        ArrayList args = null;
        this.jj_consume_token(103);
        t = this.jj_consume_token(142);
        this.token_source.checkNamingConvention(t);
        BuiltIn result = BuiltIn.newBuiltIn(this.incompatibleImprovements, lhoExp, t, this.token_source);
        result.setLocation(this.template, (TemplateObject)lhoExp, t);
        if (!(result instanceof SpecialBuiltIn) && "" != null) {
            return result;
        }
        if (result instanceof BuiltInForLoopVariable) {
            if (!(lhoExp instanceof Identifier)) {
                throw new ParseException("Expression used as the left hand operand of ?" + t.image + " must be a simple loop variable name.", lhoExp);
            }
            String loopVarName = ((Identifier)lhoExp).getName();
            this.checkLoopVariableBuiltInLHO(loopVarName, lhoExp, t);
            ((BuiltInForLoopVariable)result).bindToLoopVariable(loopVarName);
            if ("" != null) {
                return result;
            }
        }
        if (result instanceof BuiltInBannedWhenAutoEscaping) {
            if (this.outputFormat instanceof MarkupOutputFormat && this.autoEscaping) {
                throw new ParseException("Using ?" + t.image + " (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat.getName() + "), to avoid double-escaping mistakes.", this.template, t);
            }
            if ("" != null) {
                return result;
            }
        }
        if (result instanceof MarkupOutputFormatBoundBuiltIn) {
            if (!(this.outputFormat instanceof MarkupOutputFormat)) {
                throw new ParseException("?" + t.image + " can't be used here, as the current output format isn't a markup (escaping) format: " + this.outputFormat, this.template, t);
            }
            ((MarkupOutputFormatBoundBuiltIn)result).bindToMarkupOutputFormat((MarkupOutputFormat)this.outputFormat);
            if ("" != null) {
                return result;
            }
        }
        if (result instanceof OutputFormatBoundBuiltIn) {
            ((OutputFormatBoundBuiltIn)result).bindToOutputFormat(this.outputFormat, this.autoEscapingPolicy);
            if ("" != null) {
                return result;
            }
        }
        if (result instanceof BuiltInWithParseTimeParameters && !((BuiltInWithParseTimeParameters)result).isLocalLambdaParameterSupported()) {
            openParen = this.jj_consume_token(135);
            args = this.PositionalArgs();
            closeParen = this.jj_consume_token(136);
            result.setLocation(this.template, (TemplateObject)lhoExp, closeParen);
            ((BuiltInWithParseTimeParameters)result).bindToParameters(args, openParen, closeParen);
            if ("" != null) {
                return result;
            }
        }
        if (result instanceof BuiltInWithParseTimeParameters && ((BuiltInWithParseTimeParameters)result).isLocalLambdaParameterSupported()) {
            openParen = this.jj_consume_token(135);
            args = this.PositionalMaybeLambdaArgs();
            closeParen = this.jj_consume_token(136);
            result.setLocation(this.template, (TemplateObject)lhoExp, closeParen);
            ((BuiltInWithParseTimeParameters)result).bindToParameters(args, openParen, closeParen);
            if ("" != null) {
                return result;
            }
        }
        if (this.jj_2_9(Integer.MAX_VALUE) && result instanceof BuiltInWithDirectCallOptimization) {
            MethodCall methodCall = this.MethodArgs(result);
            ((BuiltInWithDirectCallOptimization)result).setDirectlyCalled();
            if ("" != null) {
                return methodCall;
            }
        }
        if (result instanceof BuiltInWithDirectCallOptimization && "" != null) {
            return result;
        }
        throw new AssertionError((Object)("Unhandled " + SpecialBuiltIn.class.getName() + " subclass: " + result.getClass()));
    }

    public final Expression LocalLambdaExpression() throws ParseException {
        Expression result;
        if (this.jj_2_10(Integer.MAX_VALUE)) {
            LocalLambdaExpression.LambdaParameterList lhs = this.LambdaExpressionParameterList();
            this.jj_consume_token(119);
            Expression rhs = this.OrExpression();
            result = new LocalLambdaExpression(lhs, rhs);
            if (lhs.getOpeningParenthesis() != null) {
                result.setLocation(this.template, lhs.getOpeningParenthesis(), (TemplateObject)rhs);
            } else {
                result.setLocation(this.template, (TemplateObject)lhs.getParameters().get(0), (TemplateObject)rhs);
            }
        } else {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 120: 
                case 121: 
                case 129: 
                case 133: 
                case 135: 
                case 137: 
                case 142: {
                    result = this.OrExpression();
                    break;
                }
                default: {
                    this.jj_la1[16] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final LocalLambdaExpression.LambdaParameterList LambdaExpressionParameterList() throws ParseException {
        Token openParen = null;
        Token closeParen = null;
        List<Identifier> params = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 135: {
                openParen = this.jj_consume_token(135);
                block4 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 142: {
                        Identifier param = this.Identifier();
                        params = new ArrayList<Identifier>(4);
                        params.add(param);
                        while (true) {
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 130: {
                                    break;
                                }
                                default: {
                                    this.jj_la1[17] = this.jj_gen;
                                    break block4;
                                }
                            }
                            this.jj_consume_token(130);
                            param = this.Identifier();
                            params.add(param);
                        }
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                    }
                }
                closeParen = this.jj_consume_token(136);
                break;
            }
            case 142: {
                Identifier param = this.Identifier();
                params = Collections.singletonList(param);
                break;
            }
            default: {
                this.jj_la1[19] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return new LocalLambdaExpression.LambdaParameterList(openParen, params != null ? params : Collections.emptyList(), closeParen);
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DotVariable(Expression exp) throws ParseException {
        Token t;
        this.jj_consume_token(99);
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 142: {
                t = this.jj_consume_token(142);
                break;
            }
            case 122: {
                t = this.jj_consume_token(122);
                break;
            }
            case 123: {
                t = this.jj_consume_token(123);
                break;
            }
            case 95: 
            case 96: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 139: 
            case 140: 
            case 141: {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 115: {
                        t = this.jj_consume_token(115);
                        break;
                    }
                    case 116: {
                        t = this.jj_consume_token(116);
                        break;
                    }
                    case 117: {
                        t = this.jj_consume_token(117);
                        break;
                    }
                    case 118: {
                        t = this.jj_consume_token(118);
                        break;
                    }
                    case 95: {
                        t = this.jj_consume_token(95);
                        break;
                    }
                    case 96: {
                        t = this.jj_consume_token(96);
                        break;
                    }
                    case 139: {
                        t = this.jj_consume_token(139);
                        break;
                    }
                    case 140: {
                        t = this.jj_consume_token(140);
                        break;
                    }
                    case 141: {
                        t = this.jj_consume_token(141);
                        break;
                    }
                    default: {
                        this.jj_la1[20] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                if (Character.isLetter(t.image.charAt(0))) break;
                throw new ParseException(t.image + " is not a valid identifier.", this.template, t);
            }
            default: {
                this.jj_la1[21] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.notListLiteral(exp, "hash");
        this.notStringLiteral(exp, "hash");
        this.notBooleanLiteral(exp, "hash");
        Dot dot = new Dot(exp, t.image);
        dot.setLocation(this.template, (TemplateObject)exp, t);
        if ("" != null) {
            return dot;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression DynamicKey(Expression exp) throws ParseException {
        this.jj_consume_token(133);
        Expression arg = this.Expression();
        Token t = this.jj_consume_token(134);
        this.notBooleanLiteral(exp, "list or hash");
        this.notNumberLiteral(exp, "list or hash");
        DynamicKeyName dkn = new DynamicKeyName(exp, arg);
        dkn.setLocation(this.template, (TemplateObject)exp, t);
        if ("" != null) {
            return dkn;
        }
        throw new Error("Missing return statement in function");
    }

    public final MethodCall MethodArgs(Expression exp) throws ParseException {
        ArrayList args = new ArrayList();
        this.jj_consume_token(135);
        args = this.PositionalArgs();
        Token end = this.jj_consume_token(136);
        args.trimToSize();
        MethodCall result = new MethodCall(exp, args);
        result.setLocation(this.template, (TemplateObject)exp, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final StringLiteral StringLiteral(boolean interpolate) throws ParseException {
        int interpolationSyntax;
        String s;
        Token t;
        boolean raw = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 93: {
                t = this.jj_consume_token(93);
                break;
            }
            case 94: {
                t = this.jj_consume_token(94);
                raw = true;
                break;
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (raw) {
            s = t.image.substring(2, t.image.length() - 1);
        } else {
            try {
                s = StringUtil.FTLStringLiteralDec(t.image.substring(1, t.image.length() - 1));
            }
            catch (ParseException pe) {
                pe.lineNumber = t.beginLine;
                pe.columnNumber = t.beginColumn;
                pe.endLineNumber = t.endLine;
                pe.endColumnNumber = t.endColumn;
                throw pe;
            }
        }
        StringLiteral result = new StringLiteral(s);
        result.setLocation(this.template, t, t);
        if (interpolate && !raw && (((interpolationSyntax = this.pCfg.getInterpolationSyntax()) == 20 || interpolationSyntax == 21) && t.image.indexOf("${") != -1 || interpolationSyntax == 20 && t.image.indexOf("#{") != -1 || interpolationSyntax == 22 && t.image.indexOf("[=") != -1)) {
            result.parseValue(this, this.outputFormat);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Expression BooleanLiteral() throws ParseException {
        BooleanLiteral result;
        Token t;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 95: {
                t = this.jj_consume_token(95);
                result = new BooleanLiteral(false);
                break;
            }
            case 96: {
                t = this.jj_consume_token(96);
                result = new BooleanLiteral(true);
                break;
            }
            default: {
                this.jj_la1[23] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        result.setLocation(this.template, t, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final HashLiteral HashLiteral() throws ParseException {
        ArrayList<Expression> keys = new ArrayList<Expression>();
        ArrayList<Expression> values = new ArrayList<Expression>();
        Token begin = this.jj_consume_token(137);
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 120: 
            case 121: 
            case 129: 
            case 133: 
            case 135: 
            case 137: 
            case 142: {
                Expression key = this.Expression();
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 130: {
                        this.jj_consume_token(130);
                        break;
                    }
                    case 132: {
                        this.jj_consume_token(132);
                        break;
                    }
                    default: {
                        this.jj_la1[24] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                Expression value = this.Expression();
                this.stringLiteralOnly(key);
                keys.add(key);
                values.add(value);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 130: {
                            break;
                        }
                        default: {
                            this.jj_la1[25] = this.jj_gen;
                            break block0;
                        }
                    }
                    this.jj_consume_token(130);
                    key = this.Expression();
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 130: {
                            this.jj_consume_token(130);
                            break;
                        }
                        case 132: {
                            this.jj_consume_token(132);
                            break;
                        }
                        default: {
                            this.jj_la1[26] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    value = this.Expression();
                    this.stringLiteralOnly(key);
                    keys.add(key);
                    values.add(value);
                }
            }
            default: {
                this.jj_la1[27] = this.jj_gen;
            }
        }
        Token end = this.jj_consume_token(138);
        keys.trimToSize();
        values.trimToSize();
        HashLiteral result = new HashLiteral(keys, values);
        result.setLocation(this.template, begin, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final DollarVariable StringOutput() throws ParseException {
        Token end;
        Expression exp;
        Token begin;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 82: {
                begin = this.jj_consume_token(82);
                exp = this.Expression();
                end = this.jj_consume_token(138);
                break;
            }
            case 84: {
                begin = this.jj_consume_token(84);
                exp = this.Expression();
                end = this.jj_consume_token(134);
                break;
            }
            default: {
                this.jj_la1[28] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.notHashLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
        this.notListLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
        DollarVariable result = new DollarVariable(exp, this.escapedExpression(exp), this.outputFormat, this.autoEscaping);
        result.setLocation(this.template, begin, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NumericalOutput NumericalOutput() throws ParseException {
        NumericalOutput result;
        MarkupOutputFormat autoEscOF;
        Token fmt = null;
        Token begin = this.jj_consume_token(83);
        Expression exp = this.Expression();
        this.numberLiteralOnly(exp);
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 131: {
                this.jj_consume_token(131);
                fmt = this.jj_consume_token(142);
                break;
            }
            default: {
                this.jj_la1[29] = this.jj_gen;
            }
        }
        Token end = this.jj_consume_token(138);
        MarkupOutputFormat markupOutputFormat = autoEscOF = this.autoEscaping && this.outputFormat instanceof MarkupOutputFormat ? (MarkupOutputFormat)this.outputFormat : null;
        if (fmt != null) {
            int minFrac = -1;
            int maxFrac = -1;
            StringTokenizer st = new StringTokenizer(fmt.image, "mM", true);
            int type = 45;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                try {
                    if (type != 45) {
                        switch (type) {
                            case 109: {
                                if (minFrac != -1) {
                                    throw new ParseException("Invalid formatting string", this.template, fmt);
                                }
                                minFrac = Integer.parseInt(token);
                                break;
                            }
                            case 77: {
                                if (maxFrac != -1) {
                                    throw new ParseException("Invalid formatting string", this.template, fmt);
                                }
                                maxFrac = Integer.parseInt(token);
                                break;
                            }
                            default: {
                                throw new ParseException("Invalid formatting string", this.template, fmt);
                            }
                        }
                        type = 45;
                        continue;
                    }
                    if (token.equals("m")) {
                        type = 109;
                        continue;
                    }
                    if (token.equals("M")) {
                        type = 77;
                        continue;
                    }
                    throw new ParseException();
                }
                catch (ParseException e) {
                    throw new ParseException("Invalid format specifier " + fmt.image, this.template, fmt);
                }
                catch (NumberFormatException e) {
                    throw new ParseException("Invalid number in the format specifier " + fmt.image, this.template, fmt);
                }
            }
            if (maxFrac == -1) {
                if (minFrac == -1) {
                    throw new ParseException("Invalid format specification, at least one of m and M must be specified!", this.template, fmt);
                }
                maxFrac = minFrac;
            } else if (minFrac == -1) {
                minFrac = 0;
            }
            if (minFrac > maxFrac) {
                throw new ParseException("Invalid format specification, min cannot be greater than max!", this.template, fmt);
            }
            if (minFrac > 50 || maxFrac > 50) {
                throw new ParseException("Cannot specify more than 50 fraction digits", this.template, fmt);
            }
            result = new NumericalOutput(exp, minFrac, maxFrac, autoEscOF);
        } else {
            result = new NumericalOutput(exp, autoEscOF);
        }
        result.setLocation(this.template, begin, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement If() throws ParseException {
        Token t;
        Token start = this.jj_consume_token(8);
        Expression condition = this.Expression();
        Token end = this.jj_consume_token(148);
        TemplateElements children = this.MixedContentElements();
        ConditionalBlock cblock = new ConditionalBlock(condition, children, 0);
        cblock.setLocation(this.template, start, end, children);
        IfBlock ifBlock = new IfBlock(cblock);
        block6: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 9: {
                    break;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                    break block6;
                }
            }
            t = this.jj_consume_token(9);
            condition = this.Expression();
            end = this.LooseDirectiveEnd();
            children = this.MixedContentElements();
            cblock = new ConditionalBlock(condition, children, 2);
            cblock.setLocation(this.template, t, end, children);
            ifBlock.addBlock(cblock);
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 54: {
                t = this.jj_consume_token(54);
                children = this.MixedContentElements();
                cblock = new ConditionalBlock(null, children, 1);
                cblock.setLocation(this.template, t, t, children);
                ifBlock.addBlock(cblock);
                break;
            }
            default: {
                this.jj_la1[31] = this.jj_gen;
            }
        }
        end = this.jj_consume_token(36);
        ifBlock.setLocation(this.template, start, end);
        if ("" != null) {
            return ifBlock;
        }
        throw new Error("Missing return statement in function");
    }

    public final AttemptBlock Attempt() throws ParseException {
        Token end;
        Token start = this.jj_consume_token(6);
        TemplateElements children = this.MixedContentElements();
        RecoveryBlock recoveryBlock = this.Recover();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 40: {
                end = this.jj_consume_token(40);
                break;
            }
            case 41: {
                end = this.jj_consume_token(41);
                break;
            }
            default: {
                this.jj_la1[32] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        AttemptBlock result = new AttemptBlock(children, recoveryBlock);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final RecoveryBlock Recover() throws ParseException {
        Token start = this.jj_consume_token(7);
        TemplateElements children = this.MixedContentElements();
        RecoveryBlock result = new RecoveryBlock(children);
        result.setLocation(this.template, start, start, children);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement List() throws ParseException {
        TemplateElement result;
        Token loopVar = null;
        Token loopVar2 = null;
        ElseOfList elseOfList = null;
        Token start = this.jj_consume_token(10);
        Expression exp = this.Expression();
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 140: {
                this.jj_consume_token(140);
                loopVar = this.jj_consume_token(142);
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 130: {
                        this.jj_consume_token(130);
                        loopVar2 = this.jj_consume_token(142);
                        break block0;
                    }
                }
                this.jj_la1[33] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[34] = this.jj_gen;
            }
        }
        this.jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = this.pushIteratorBlockContext();
        if (loopVar != null) {
            iterCtx.loopVarName = loopVar.image;
            ++this.breakableDirectiveNesting;
            ++this.continuableDirectiveNesting;
            if (loopVar2 != null) {
                iterCtx.loopVar2Name = loopVar2.image;
                iterCtx.hashListing = true;
                if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
                    throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx.loopVarName, this.template, start);
                }
            }
        }
        TemplateElements childrendBeforeElse = this.MixedContentElements();
        if (loopVar != null) {
            --this.breakableDirectiveNesting;
            --this.continuableDirectiveNesting;
        } else if (iterCtx.kind != 2) {
            throw new ParseException("#list must have either \"as loopVar\" parameter or nested #items that belongs to it.", this.template, start);
        }
        this.popIteratorBlockContext();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 54: {
                elseOfList = this.ElseOfList();
                break;
            }
            default: {
                this.jj_la1[35] = this.jj_gen;
            }
        }
        Token end = this.jj_consume_token(37);
        IteratorBlock list = new IteratorBlock(exp, loopVar != null ? loopVar.image : null, loopVar2 != null ? loopVar2.image : null, childrendBeforeElse, iterCtx.hashListing, false);
        list.setLocation(this.template, start, end);
        if (elseOfList == null) {
            result = list;
        } else {
            result = new ListElseContainer(list, elseOfList);
            result.setLocation(this.template, start, end);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ElseOfList ElseOfList() throws ParseException {
        Token start = this.jj_consume_token(54);
        TemplateElements children = this.MixedContentElements();
        ElseOfList result = new ElseOfList(children);
        result.setLocation(this.template, start, start, children);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final IteratorBlock ForEach() throws ParseException {
        Token start = this.jj_consume_token(13);
        Token loopVar = this.jj_consume_token(142);
        this.jj_consume_token(139);
        Expression exp = this.Expression();
        this.jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = this.pushIteratorBlockContext();
        iterCtx.loopVarName = loopVar.image;
        iterCtx.kind = 1;
        ++this.breakableDirectiveNesting;
        ++this.continuableDirectiveNesting;
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(42);
        --this.breakableDirectiveNesting;
        --this.continuableDirectiveNesting;
        this.popIteratorBlockContext();
        IteratorBlock result = new IteratorBlock(exp, loopVar.image, null, children, false, true);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Items Items() throws ParseException {
        Token loopVar2 = null;
        Token start = this.jj_consume_token(11);
        Token loopVar = this.jj_consume_token(142);
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 130: {
                this.jj_consume_token(130);
                loopVar2 = this.jj_consume_token(142);
                break;
            }
            default: {
                this.jj_la1[36] = this.jj_gen;
            }
        }
        this.jj_consume_token(148);
        ParserIteratorBlockContext iterCtx = this.peekIteratorBlockContext();
        if (iterCtx == null) {
            throw new ParseException("#items must be inside a #list block.", this.template, start);
        }
        if (iterCtx.loopVarName != null) {
            String msg = iterCtx.kind == 1 ? this.forEachDirectiveSymbol() + " doesn't support nested #items." : (iterCtx.kind == 2 ? "Can't nest #items into each other when they belong to the same #list." : "The parent #list of the #items must not have \"as loopVar\" parameter.");
            throw new ParseException(msg, this.template, start);
        }
        iterCtx.kind = 2;
        iterCtx.loopVarName = loopVar.image;
        if (loopVar2 != null) {
            iterCtx.loopVar2Name = loopVar2.image;
            iterCtx.hashListing = true;
            if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
                throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx.loopVarName, this.template, start);
            }
        }
        ++this.breakableDirectiveNesting;
        ++this.continuableDirectiveNesting;
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(38);
        --this.breakableDirectiveNesting;
        --this.continuableDirectiveNesting;
        iterCtx.loopVarName = null;
        iterCtx.loopVar2Name = null;
        Items result = new Items(loopVar.image, loopVar2 != null ? loopVar2.image : null, children);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Sep Sep() throws ParseException {
        Token end = null;
        Token start = this.jj_consume_token(12);
        if (this.peekIteratorBlockContext() == null) {
            throw new ParseException("#sep must be inside a #list (or " + this.forEachDirectiveSymbol() + ") block.", this.template, start);
        }
        TemplateElements children = this.MixedContentElements();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 39: {
                end = this.jj_consume_token(39);
                break;
            }
            default: {
                this.jj_la1[37] = this.jj_gen;
            }
        }
        Sep result = new Sep(children);
        if (end != null) {
            result.setLocation(this.template, start, end);
        } else {
            result.setLocation(this.template, start, start, children);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final VisitNode Visit() throws ParseException {
        Expression namespaces = null;
        Token start = this.jj_consume_token(24);
        Expression targetNode = this.Expression();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 141: {
                this.jj_consume_token(141);
                namespaces = this.Expression();
                break;
            }
            default: {
                this.jj_la1[38] = this.jj_gen;
            }
        }
        Token end = this.LooseDirectiveEnd();
        VisitNode result = new VisitNode(targetNode, namespaces);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final RecurseNode Recurse() throws ParseException {
        Token start;
        Token end = null;
        Expression node = null;
        Expression namespaces = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 67: {
                start = this.jj_consume_token(67);
                break;
            }
            case 68: {
                start = this.jj_consume_token(68);
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 97: 
                    case 98: 
                    case 99: 
                    case 120: 
                    case 121: 
                    case 129: 
                    case 133: 
                    case 135: 
                    case 137: 
                    case 142: {
                        node = this.Expression();
                        break;
                    }
                    default: {
                        this.jj_la1[39] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 141: {
                        this.jj_consume_token(141);
                        namespaces = this.Expression();
                        break;
                    }
                    default: {
                        this.jj_la1[40] = this.jj_gen;
                    }
                }
                end = this.LooseDirectiveEnd();
                break;
            }
            default: {
                this.jj_la1[41] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (end == null) {
            end = start;
        }
        RecurseNode result = new RecurseNode(node, namespaces);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final FallbackInstruction FallBack() throws ParseException {
        Token tok = this.jj_consume_token(69);
        if (!this.inMacro) {
            throw new ParseException("Cannot fall back outside a macro.", this.template, tok);
        }
        FallbackInstruction result = new FallbackInstruction();
        result.setLocation(this.template, tok, tok);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final BreakInstruction Break() throws ParseException {
        Token start = this.jj_consume_token(55);
        if (this.breakableDirectiveNesting < 1) {
            throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items, #switch (or the deprecated " + this.forEachDirectiveSymbol() + ")", this.template, start);
        }
        BreakInstruction result = new BreakInstruction();
        result.setLocation(this.template, start, start);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ContinueInstruction Continue() throws ParseException {
        Token start = this.jj_consume_token(56);
        if (this.continuableDirectiveNesting < 1) {
            throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items (or the deprecated " + this.forEachDirectiveSymbol() + ")", this.template, start);
        }
        ContinueInstruction result = new ContinueInstruction();
        result.setLocation(this.template, start, start);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ReturnInstruction Return() throws ParseException {
        Token start;
        Token end = null;
        Expression exp = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 57: {
                end = start = this.jj_consume_token(57);
                break;
            }
            case 26: {
                start = this.jj_consume_token(26);
                exp = this.Expression();
                end = this.LooseDirectiveEnd();
                break;
            }
            default: {
                this.jj_la1[42] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (this.inMacro) {
            if (exp != null) {
                throw new ParseException("A macro cannot return a value", this.template, start);
            }
        } else if (this.inFunction) {
            if (exp == null) {
                throw new ParseException("A function must return a value", this.template, start);
            }
        } else if (exp == null) {
            throw new ParseException("A return instruction can only occur inside a macro or function", this.template, start);
        }
        ReturnInstruction result = new ReturnInstruction(exp);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final StopInstruction Stop() throws ParseException {
        Token start = null;
        Expression exp = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 58: {
                start = this.jj_consume_token(58);
                break;
            }
            case 25: {
                start = this.jj_consume_token(25);
                exp = this.Expression();
                this.LooseDirectiveEnd();
                break;
            }
            default: {
                this.jj_la1[43] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        StopInstruction result = new StopInstruction(exp);
        result.setLocation(this.template, start, start);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Nested() throws ParseException {
        Token t;
        BodyInstruction result = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 65: {
                t = this.jj_consume_token(65);
                result = new BodyInstruction(null);
                result.setLocation(this.template, t, t);
                break;
            }
            case 66: {
                t = this.jj_consume_token(66);
                ArrayList bodyParameters = this.PositionalArgs();
                Token end = this.LooseDirectiveEnd();
                result = new BodyInstruction(bodyParameters);
                result.setLocation(this.template, t, end);
                break;
            }
            default: {
                this.jj_la1[44] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (!this.inMacro) {
            throw new ParseException("Cannot use a " + t.image + " instruction outside a macro.", this.template, t);
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Flush() throws ParseException {
        Token t = this.jj_consume_token(59);
        FlushInstruction result = new FlushInstruction();
        result.setLocation(this.template, t, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Trim() throws ParseException {
        Token t;
        TrimInstruction result = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 60: {
                t = this.jj_consume_token(60);
                result = new TrimInstruction(true, true);
                break;
            }
            case 61: {
                t = this.jj_consume_token(61);
                result = new TrimInstruction(true, false);
                break;
            }
            case 62: {
                t = this.jj_consume_token(62);
                result = new TrimInstruction(false, true);
                break;
            }
            case 63: {
                t = this.jj_consume_token(63);
                result = new TrimInstruction(false, false);
                break;
            }
            default: {
                this.jj_la1[45] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        result.setLocation(this.template, t, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Assign() throws ParseException {
        int scope;
        Token start;
        Token id = null;
        Expression nsExp = null;
        ArrayList<Assignment> assignments = new ArrayList<Assignment>();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 16: {
                start = this.jj_consume_token(16);
                scope = 1;
                break;
            }
            case 17: {
                start = this.jj_consume_token(17);
                scope = 3;
                break;
            }
            case 18: {
                start = this.jj_consume_token(18);
                scope = 2;
                scope = 2;
                if (this.inMacro || this.inFunction) break;
                throw new ParseException("Local variable assigned outside a macro.", this.template, start);
            }
            default: {
                this.jj_la1[46] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        Expression nameExp = this.IdentifierOrStringLiteral();
        String varName = nameExp instanceof StringLiteral ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 105: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 113: 
            case 114: {
                Expression exp;
                Token equalsOp;
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 105: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 105: {
                                this.jj_consume_token(105);
                                break;
                            }
                            case 108: {
                                this.jj_consume_token(108);
                                break;
                            }
                            case 109: {
                                this.jj_consume_token(109);
                                break;
                            }
                            case 110: {
                                this.jj_consume_token(110);
                                break;
                            }
                            case 111: {
                                this.jj_consume_token(111);
                                break;
                            }
                            case 112: {
                                this.jj_consume_token(112);
                                break;
                            }
                            default: {
                                this.jj_la1[47] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        equalsOp = this.token;
                        exp = this.Expression();
                        break;
                    }
                    case 113: 
                    case 114: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 113: {
                                this.jj_consume_token(113);
                                break;
                            }
                            case 114: {
                                this.jj_consume_token(114);
                                break;
                            }
                            default: {
                                this.jj_la1[48] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        equalsOp = this.token;
                        exp = null;
                        break;
                    }
                    default: {
                        this.jj_la1[49] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                Assignment ass = new Assignment(varName, equalsOp.kind, exp, scope);
                if (exp != null) {
                    ass.setLocation(this.template, (TemplateObject)nameExp, (TemplateObject)exp);
                } else {
                    ass.setLocation(this.template, (TemplateObject)nameExp, equalsOp);
                }
                assignments.add(ass);
                while (this.jj_2_11(Integer.MAX_VALUE)) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 130: {
                            this.jj_consume_token(130);
                            break;
                        }
                        default: {
                            this.jj_la1[50] = this.jj_gen;
                        }
                    }
                    nameExp = this.IdentifierOrStringLiteral();
                    varName = nameExp instanceof StringLiteral ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 105: 
                        case 108: 
                        case 109: 
                        case 110: 
                        case 111: 
                        case 112: {
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 105: {
                                    this.jj_consume_token(105);
                                    break;
                                }
                                case 108: {
                                    this.jj_consume_token(108);
                                    break;
                                }
                                case 109: {
                                    this.jj_consume_token(109);
                                    break;
                                }
                                case 110: {
                                    this.jj_consume_token(110);
                                    break;
                                }
                                case 111: {
                                    this.jj_consume_token(111);
                                    break;
                                }
                                case 112: {
                                    this.jj_consume_token(112);
                                    break;
                                }
                                default: {
                                    this.jj_la1[51] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            equalsOp = this.token;
                            exp = this.Expression();
                            break;
                        }
                        case 113: 
                        case 114: {
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 113: {
                                    this.jj_consume_token(113);
                                    break;
                                }
                                case 114: {
                                    this.jj_consume_token(114);
                                    break;
                                }
                                default: {
                                    this.jj_la1[52] = this.jj_gen;
                                    this.jj_consume_token(-1);
                                    throw new ParseException();
                                }
                            }
                            equalsOp = this.token;
                            exp = null;
                            break;
                        }
                        default: {
                            this.jj_la1[53] = this.jj_gen;
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                    ass = new Assignment(varName, equalsOp.kind, exp, scope);
                    if (exp != null) {
                        ass.setLocation(this.template, (TemplateObject)nameExp, (TemplateObject)exp);
                    } else {
                        ass.setLocation(this.template, (TemplateObject)nameExp, equalsOp);
                    }
                    assignments.add(ass);
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 139: {
                        id = this.jj_consume_token(139);
                        nsExp = this.Expression();
                        if (scope == 1) break;
                        throw new ParseException("Cannot assign to namespace here.", this.template, id);
                    }
                    default: {
                        this.jj_la1[54] = this.jj_gen;
                    }
                }
                Token end = this.LooseDirectiveEnd();
                if (assignments.size() == 1) {
                    Assignment a = (Assignment)assignments.get(0);
                    a.setNamespaceExp(nsExp);
                    a.setLocation(this.template, start, end);
                    if ("" == null) break;
                    return a;
                }
                AssignmentInstruction ai = new AssignmentInstruction(scope);
                for (int i = 0; i < assignments.size(); ++i) {
                    ai.addAssignment((Assignment)assignments.get(i));
                }
                ai.setNamespaceExp(nsExp);
                ai.setLocation(this.template, start, end);
                if ("" == null) break;
                return ai;
            }
            case 139: 
            case 148: {
                Token end;
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 139: {
                        id = this.jj_consume_token(139);
                        nsExp = this.Expression();
                        if (scope == 1) break;
                        throw new ParseException("Cannot assign to namespace here.", this.template, id);
                    }
                    default: {
                        this.jj_la1[55] = this.jj_gen;
                    }
                }
                this.jj_consume_token(148);
                TemplateElements children = this.MixedContentElements();
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 43: {
                        end = this.jj_consume_token(43);
                        if (scope == 2) break;
                        throw new ParseException("Mismatched assignment tags.", this.template, end);
                    }
                    case 45: {
                        end = this.jj_consume_token(45);
                        if (scope == 1) break;
                        throw new ParseException("Mismatched assignment tags.", this.template, end);
                    }
                    case 44: {
                        end = this.jj_consume_token(44);
                        if (scope == 3) break;
                        throw new ParseException("Mismatched assignment tags", this.template, end);
                    }
                    default: {
                        this.jj_la1[56] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                BlockAssignment ba = new BlockAssignment(children, varName, scope, nsExp, this.getMarkupOutputFormat());
                ba.setLocation(this.template, start, end);
                if ("" == null) break;
                return ba;
            }
            default: {
                this.jj_la1[57] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        throw new Error("Missing return statement in function");
    }

    public final Include Include() throws ParseException {
        Expression nameExp;
        Token start;
        Expression ignoreMissingExp;
        Expression encodingExp;
        Expression parseExp;
        block10: {
            String attString;
            Token att;
            parseExp = null;
            encodingExp = null;
            ignoreMissingExp = null;
            start = this.jj_consume_token(19);
            nameExp = this.Expression();
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 131: {
                    this.jj_consume_token(131);
                    break;
                }
                default: {
                    this.jj_la1[58] = this.jj_gen;
                }
            }
            while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 142: {
                        break;
                    }
                    default: {
                        this.jj_la1[59] = this.jj_gen;
                        break block10;
                    }
                }
                att = this.jj_consume_token(142);
                this.jj_consume_token(105);
                Expression exp = this.Expression();
                attString = att.image;
                if (attString.equalsIgnoreCase("parse")) {
                    parseExp = exp;
                    continue;
                }
                if (attString.equalsIgnoreCase("encoding")) {
                    encodingExp = exp;
                    continue;
                }
                if (!attString.equalsIgnoreCase("ignore_missing") && !attString.equals("ignoreMissing")) break;
                this.token_source.checkNamingConvention(att);
                ignoreMissingExp = exp;
            }
            String correctedName = attString.equals("ignoreMissing") ? "ignore_missing" : null;
            throw new ParseException("Unsupported named #include parameter: \"" + attString + "\". Supported parameters are: \"parse\", \"encoding\", \"ignore_missing\"." + (correctedName == null ? "" : " Supporting camelCase parameter names is planned for FreeMarker 2.4.0; check if an update is available, and if it indeed supports camel case."), this.template, att);
        }
        Token end = this.LooseDirectiveEnd();
        Include result = new Include(this.template, nameExp, encodingExp, parseExp, ignoreMissingExp);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final LibraryLoad Import() throws ParseException {
        Token start = this.jj_consume_token(20);
        Expression nameExp = this.Expression();
        this.jj_consume_token(140);
        Token ns = this.jj_consume_token(142);
        Token end = this.LooseDirectiveEnd();
        LibraryLoad result = new LibraryLoad(this.template, nameExp, ns.image);
        result.setLocation(this.template, start, end);
        this.template.addImport(result);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Macro Macro() throws ParseException {
        Token end;
        int lastContinuableDirectiveNesting;
        int lastBreakableDirectiveNesting;
        Token start;
        LinkedHashMap<String, Expression> paramNamesWithDefault = new LinkedHashMap<String, Expression>();
        Expression defValue = null;
        String catchAllParamName = null;
        boolean isCatchAll = false;
        boolean isFunction = false;
        boolean hasDefaults = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 22: {
                start = this.jj_consume_token(22);
                break;
            }
            case 21: {
                start = this.jj_consume_token(21);
                isFunction = true;
                break;
            }
            default: {
                this.jj_la1[60] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (this.inMacro || this.inFunction) {
            throw new ParseException("Macro or function definitions can't be nested into each other.", this.template, start);
        }
        if (isFunction) {
            this.inFunction = true;
        } else {
            this.inMacro = true;
        }
        this.requireArgsSpecialVariable = false;
        Expression nameExp = this.IdentifierOrStringLiteral();
        String name = nameExp instanceof StringLiteral ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 135: {
                this.jj_consume_token(135);
                break;
            }
            default: {
                this.jj_la1[61] = this.jj_gen;
            }
        }
        block26: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 142: {
                    break;
                }
                default: {
                    this.jj_la1[62] = this.jj_gen;
                    break block26;
                }
            }
            Token arg = this.jj_consume_token(142);
            defValue = null;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 124: {
                    this.jj_consume_token(124);
                    isCatchAll = true;
                    break;
                }
                default: {
                    this.jj_la1[63] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 105: {
                    this.jj_consume_token(105);
                    defValue = this.Expression();
                    hasDefaults = true;
                    break;
                }
                default: {
                    this.jj_la1[64] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 130: {
                    this.jj_consume_token(130);
                    break;
                }
                default: {
                    this.jj_la1[65] = this.jj_gen;
                }
            }
            if (catchAllParamName != null) {
                throw new ParseException("There may only be one \"catch-all\" parameter in a macro declaration, and it must be the last parameter.", this.template, arg);
            }
            if (isCatchAll) {
                if (defValue != null) {
                    throw new ParseException("\"Catch-all\" macro parameter may not have a default value.", this.template, arg);
                }
                catchAllParamName = arg.image;
                continue;
            }
            if (hasDefaults && defValue == null) {
                throw new ParseException("In a macro declaration, parameters without a default value must all occur before the parameters with default values.", this.template, arg);
            }
            paramNamesWithDefault.put(arg.image, defValue);
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 136: {
                this.jj_consume_token(136);
                break;
            }
            default: {
                this.jj_la1[66] = this.jj_gen;
            }
        }
        this.jj_consume_token(148);
        List<ParserIteratorBlockContext> lastIteratorBlockContexts = this.iteratorBlockContexts;
        this.iteratorBlockContexts = null;
        if (this.incompatibleImprovements >= _VersionInts.V_2_3_23) {
            lastBreakableDirectiveNesting = this.breakableDirectiveNesting;
            lastContinuableDirectiveNesting = this.continuableDirectiveNesting;
            this.breakableDirectiveNesting = 0;
            this.continuableDirectiveNesting = 0;
        } else {
            lastBreakableDirectiveNesting = 0;
            lastContinuableDirectiveNesting = 0;
        }
        TemplateElements children = this.MixedContentElements();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 47: {
                end = this.jj_consume_token(47);
                if (!isFunction) break;
                throw new ParseException("Expected function end tag here.", this.template, end);
            }
            case 46: {
                end = this.jj_consume_token(46);
                if (isFunction) break;
                throw new ParseException("Expected macro end tag here.", this.template, end);
            }
            default: {
                this.jj_la1[67] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.iteratorBlockContexts = lastIteratorBlockContexts;
        if (this.incompatibleImprovements >= _VersionInts.V_2_3_23) {
            this.breakableDirectiveNesting = lastBreakableDirectiveNesting;
            this.continuableDirectiveNesting = lastContinuableDirectiveNesting;
        }
        this.inFunction = false;
        this.inMacro = false;
        Macro result = new Macro(name, paramNamesWithDefault, catchAllParamName, isFunction, this.requireArgsSpecialVariable, children);
        result.setLocation(this.template, start, end);
        this.template.addMacro(result);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final CompressedBlock Compress() throws ParseException {
        Token start = this.jj_consume_token(32);
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(51);
        CompressedBlock cb = new CompressedBlock(children);
        cb.setLocation(this.template, start, end);
        if ("" != null) {
            return cb;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement UnifiedMacroTransform() throws ParseException {
        TemplateElements children;
        Token end;
        Expression methodCallTarget;
        Token start = null;
        HashMap namedArgs = null;
        ArrayList positionalArgs = null;
        ArrayList<String> bodyParameters = null;
        int pushedCtxCount = 0;
        start = this.jj_consume_token(74);
        Expression exp = this.Expression();
        Expression cleanedExp = exp;
        if (cleanedExp instanceof MethodCall && (methodCallTarget = ((MethodCall)cleanedExp).getTarget()) instanceof BuiltInsForCallables.with_argsBI) {
            cleanedExp = ((BuiltInsForCallables.with_argsBI)methodCallTarget).target;
        }
        Expression startTagNameExp = cleanedExp instanceof Identifier || cleanedExp instanceof Dot && ((Dot)cleanedExp).onlyHasIdentifiers() ? cleanedExp : null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 152: {
                this.jj_consume_token(152);
                break;
            }
            default: {
                this.jj_la1[68] = this.jj_gen;
            }
        }
        if (this.jj_2_12(Integer.MAX_VALUE)) {
            namedArgs = this.NamedArgs();
        } else {
            positionalArgs = this.PositionalArgs();
        }
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 131: {
                this.jj_consume_token(131);
                bodyParameters = new ArrayList<String>(4);
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 142: 
                    case 152: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 152: {
                                this.jj_consume_token(152);
                                break;
                            }
                            default: {
                                this.jj_la1[69] = this.jj_gen;
                            }
                        }
                        Token t = this.jj_consume_token(142);
                        bodyParameters.add(t.image);
                        while (true) {
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 130: 
                                case 152: {
                                    break;
                                }
                                default: {
                                    this.jj_la1[70] = this.jj_gen;
                                    break block3;
                                }
                            }
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 152: {
                                    this.jj_consume_token(152);
                                    break;
                                }
                                default: {
                                    this.jj_la1[71] = this.jj_gen;
                                }
                            }
                            this.jj_consume_token(130);
                            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                                case 152: {
                                    this.jj_consume_token(152);
                                    break;
                                }
                                default: {
                                    this.jj_la1[72] = this.jj_gen;
                                }
                            }
                            t = this.jj_consume_token(142);
                            bodyParameters.add(t.image);
                        }
                    }
                }
                this.jj_la1[73] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[74] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 149: {
                end = this.jj_consume_token(149);
                children = TemplateElements.EMPTY;
                break;
            }
            case 148: {
                this.jj_consume_token(148);
                if (bodyParameters != null && this.iteratorBlockContexts != null && !this.iteratorBlockContexts.isEmpty()) {
                    int ctxsLen = this.iteratorBlockContexts.size();
                    int bodyParsLen = bodyParameters.size();
                    block26: for (int bodyParIdx = 0; bodyParIdx < bodyParsLen; ++bodyParIdx) {
                        String bodyParName = bodyParameters.get(bodyParIdx);
                        for (int ctxIdx = ctxsLen - 1; ctxIdx >= 0; --ctxIdx) {
                            ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(ctxIdx);
                            if (ctx.loopVarName == null || !ctx.loopVarName.equals(bodyParName)) continue;
                            if (ctx.kind == 3) continue block26;
                            ParserIteratorBlockContext shadowingCtx = this.pushIteratorBlockContext();
                            shadowingCtx.loopVarName = bodyParName;
                            shadowingCtx.kind = 3;
                            ++pushedCtxCount;
                            continue block26;
                        }
                    }
                }
                children = this.MixedContentElements();
                end = this.jj_consume_token(75);
                for (int i = 0; i < pushedCtxCount; ++i) {
                    this.popIteratorBlockContext();
                }
                String endTagName = end.image.substring(3, end.image.length() - 1).trim();
                if (endTagName.length() <= 0) break;
                if (startTagNameExp == null) {
                    throw new ParseException("Expecting </@>", this.template, end);
                }
                String startTagName = startTagNameExp.getCanonicalForm();
                if (endTagName.equals(startTagName)) break;
                throw new ParseException("Expecting </@> or </@" + startTagName + ">", this.template, end);
            }
            default: {
                this.jj_la1[75] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        UnifiedCall result = positionalArgs != null ? new UnifiedCall(exp, positionalArgs, children, bodyParameters) : new UnifiedCall(exp, namedArgs, children, bodyParameters);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Call() throws ParseException {
        HashMap namedArgs = null;
        ArrayList positionalArgs = null;
        Identifier macroName = null;
        Token start = this.jj_consume_token(27);
        Token id = this.jj_consume_token(142);
        macroName = new Identifier(id.image);
        macroName.setLocation(this.template, id, id);
        if (this.jj_2_14(Integer.MAX_VALUE)) {
            namedArgs = this.NamedArgs();
        } else {
            if (this.jj_2_13(Integer.MAX_VALUE)) {
                this.jj_consume_token(135);
            }
            positionalArgs = this.PositionalArgs();
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 136: {
                    this.jj_consume_token(136);
                    break;
                }
                default: {
                    this.jj_la1[76] = this.jj_gen;
                }
            }
        }
        Token end = this.LooseDirectiveEnd();
        UnifiedCall result = null;
        result = positionalArgs != null ? new UnifiedCall((Expression)macroName, positionalArgs, TemplateElements.EMPTY, null) : new UnifiedCall((Expression)macroName, namedArgs, TemplateElements.EMPTY, null);
        result.legacySyntax = true;
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final HashMap NamedArgs() throws ParseException {
        HashMap<String, Expression> result = new HashMap<String, Expression>();
        block3: while (true) {
            Token t = this.jj_consume_token(142);
            this.jj_consume_token(105);
            this.token_source.SwitchTo(4);
            this.token_source.inInvocation = true;
            Expression exp = this.Expression();
            result.put(t.image, exp);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 142: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[77] = this.jj_gen;
        this.token_source.inInvocation = false;
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ArrayList PositionalArgs() throws ParseException {
        ArrayList<Expression> result = new ArrayList<Expression>();
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 120: 
            case 121: 
            case 129: 
            case 133: 
            case 135: 
            case 137: 
            case 142: {
                Expression arg = this.Expression();
                result.add(arg);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 93: 
                        case 94: 
                        case 95: 
                        case 96: 
                        case 97: 
                        case 98: 
                        case 99: 
                        case 120: 
                        case 121: 
                        case 129: 
                        case 130: 
                        case 133: 
                        case 135: 
                        case 137: 
                        case 142: {
                            break;
                        }
                        default: {
                            this.jj_la1[78] = this.jj_gen;
                            break block0;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 130: {
                            this.jj_consume_token(130);
                            break;
                        }
                        default: {
                            this.jj_la1[79] = this.jj_gen;
                        }
                    }
                    arg = this.Expression();
                    result.add(arg);
                }
            }
            default: {
                this.jj_la1[80] = this.jj_gen;
            }
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final ArrayList PositionalMaybeLambdaArgs() throws ParseException {
        ArrayList<Expression> result = new ArrayList<Expression>();
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 97: 
            case 98: 
            case 99: 
            case 120: 
            case 121: 
            case 129: 
            case 133: 
            case 135: 
            case 137: 
            case 142: {
                Expression arg = this.LocalLambdaExpression();
                result.add(arg);
                while (true) {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 93: 
                        case 94: 
                        case 95: 
                        case 96: 
                        case 97: 
                        case 98: 
                        case 99: 
                        case 120: 
                        case 121: 
                        case 129: 
                        case 130: 
                        case 133: 
                        case 135: 
                        case 137: 
                        case 142: {
                            break;
                        }
                        default: {
                            this.jj_la1[81] = this.jj_gen;
                            break block0;
                        }
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 130: {
                            this.jj_consume_token(130);
                            break;
                        }
                        default: {
                            this.jj_la1[82] = this.jj_gen;
                        }
                    }
                    arg = this.LocalLambdaExpression();
                    result.add(arg);
                }
            }
            default: {
                this.jj_la1[83] = this.jj_gen;
            }
        }
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Comment Comment() throws ParseException {
        Token start;
        StringBuilder buf = new StringBuilder();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 33: {
                start = this.jj_consume_token(33);
                break;
            }
            case 34: {
                start = this.jj_consume_token(34);
                break;
            }
            default: {
                this.jj_la1[84] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        Token end = this.UnparsedContent(start, buf);
        Comment result = new Comment(buf.toString());
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TextBlock NoParse() throws ParseException {
        StringBuilder buf = new StringBuilder();
        Token start = this.jj_consume_token(35);
        Token end = this.UnparsedContent(start, buf);
        TextBlock result = new TextBlock(buf.toString(), true);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TransformBlock Transform() throws ParseException {
        Token end;
        TemplateElements children = null;
        HashMap<String, Expression> args = null;
        Token start = this.jj_consume_token(23);
        Expression exp = this.Expression();
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 131: {
                this.jj_consume_token(131);
                break;
            }
            default: {
                this.jj_la1[85] = this.jj_gen;
            }
        }
        block10: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 142: {
                    break;
                }
                default: {
                    this.jj_la1[86] = this.jj_gen;
                    break block10;
                }
            }
            Token argName = this.jj_consume_token(142);
            this.jj_consume_token(105);
            Expression argExp = this.Expression();
            if (args == null) {
                args = new HashMap<String, Expression>();
            }
            args.put(argName.image, argExp);
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 149: {
                end = this.jj_consume_token(149);
                break;
            }
            case 148: {
                this.jj_consume_token(148);
                children = this.MixedContentElements();
                end = this.jj_consume_token(52);
                break;
            }
            default: {
                this.jj_la1[87] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        TransformBlock result = new TransformBlock(exp, args, children);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final SwitchBlock Switch() throws ParseException {
        MixedContent ignoredSectionBeforeFirstCase = null;
        boolean defaultFound = false;
        Token start = this.jj_consume_token(14);
        Expression switchExp = this.Expression();
        this.jj_consume_token(148);
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 33: 
            case 34: 
            case 79: {
                ignoredSectionBeforeFirstCase = this.WhitespaceAndComments();
                break;
            }
            default: {
                this.jj_la1[88] = this.jj_gen;
            }
        }
        ++this.breakableDirectiveNesting;
        SwitchBlock switchBlock = new SwitchBlock(switchExp, ignoredSectionBeforeFirstCase);
        block3 : switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 15: 
            case 64: {
                block12: while (true) {
                    Case caseIns = this.Case();
                    if (caseIns.condition == null) {
                        if (defaultFound) {
                            throw new ParseException("You can only have one default case in a switch statement", this.template, start);
                        }
                        defaultFound = true;
                    }
                    switchBlock.addCase(caseIns);
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 15: 
                        case 64: {
                            continue block12;
                        }
                    }
                    break;
                }
                this.jj_la1[89] = this.jj_gen;
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 79: {
                        this.jj_consume_token(79);
                        break block3;
                    }
                }
                this.jj_la1[90] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[91] = this.jj_gen;
            }
        }
        Token end = this.jj_consume_token(53);
        --this.breakableDirectiveNesting;
        switchBlock.setLocation(this.template, start, end);
        if ("" != null) {
            return switchBlock;
        }
        throw new Error("Missing return statement in function");
    }

    public final Case Case() throws ParseException {
        Expression exp;
        Token start;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 15: {
                start = this.jj_consume_token(15);
                exp = this.Expression();
                this.jj_consume_token(148);
                break;
            }
            case 64: {
                start = this.jj_consume_token(64);
                exp = null;
                break;
            }
            default: {
                this.jj_la1[92] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        TemplateElements children = this.MixedContentElements();
        Case result = new Case(exp, children);
        result.setLocation(this.template, start, start, children);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final EscapeBlock Escape() throws ParseException {
        Token start = this.jj_consume_token(70);
        if (this.outputFormat instanceof MarkupOutputFormat && this.autoEscaping) {
            throw new ParseException("Using the \"escape\" directive (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat.getName() + "), to avoid confusion and double-escaping mistakes.", this.template, start);
        }
        Token variable = this.jj_consume_token(142);
        this.jj_consume_token(140);
        Expression escapeExpr = this.Expression();
        this.jj_consume_token(148);
        EscapeBlock result = new EscapeBlock(variable.image, escapeExpr, this.escapedExpression(escapeExpr));
        this.escapes.addFirst(result);
        TemplateElements children = this.MixedContentElements();
        result.setContent(children);
        this.escapes.removeFirst();
        Token end = this.jj_consume_token(71);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NoEscapeBlock NoEscape() throws ParseException {
        Token start = this.jj_consume_token(72);
        if (this.escapes.isEmpty()) {
            throw new ParseException("#noescape with no matching #escape encountered.", this.template, start);
        }
        Object escape = this.escapes.removeFirst();
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(73);
        this.escapes.addFirst(escape);
        NoEscapeBlock result = new NoEscapeBlock(children);
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final OutputFormatBlock OutputFormat() throws ParseException {
        String paramStr;
        TemplateModel paramTM;
        Token start = this.jj_consume_token(29);
        Expression paramExp = this.Expression();
        this.jj_consume_token(148);
        if (!paramExp.isLiteral()) {
            throw new ParseException("Parameter expression must be parse-time evaluable (constant): " + paramExp.getCanonicalForm(), paramExp);
        }
        try {
            paramTM = paramExp.eval(null);
        }
        catch (Exception e) {
            throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp.getCanonicalForm() + "\nUnderlying cause: " + e, paramExp, e);
        }
        if (paramTM instanceof TemplateScalarModel) {
            try {
                paramStr = ((TemplateScalarModel)paramTM).getAsString();
            }
            catch (TemplateModelException e) {
                throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp.getCanonicalForm() + "\nUnderlying cause: " + e, paramExp, e);
            }
        } else {
            throw new ParseException("Parameter must be a string, but was: " + ClassUtil.getFTLTypeDescription(paramTM), paramExp);
        }
        OutputFormat lastOutputFormat = this.outputFormat;
        try {
            if (paramStr.startsWith("{")) {
                if (!paramStr.endsWith("}")) {
                    throw new ParseException("Output format name that starts with '{' must end with '}': " + paramStr, this.template, start);
                }
                OutputFormat innerOutputFormat = this.template.getConfiguration().getOutputFormat(paramStr.substring(1, paramStr.length() - 1));
                if (!(innerOutputFormat instanceof MarkupOutputFormat)) {
                    throw new ParseException("The output format inside the {...} must be a markup format, but was: " + innerOutputFormat, this.template, start);
                }
                if (!(this.outputFormat instanceof MarkupOutputFormat)) {
                    throw new ParseException("The current output format must be a markup format when using {...}, but was: " + this.outputFormat, this.template, start);
                }
                this.outputFormat = new CombinedMarkupOutputFormat((MarkupOutputFormat)this.outputFormat, (MarkupOutputFormat)innerOutputFormat);
            } else {
                this.outputFormat = this.template.getConfiguration().getOutputFormat(paramStr);
            }
            this.recalculateAutoEscapingField();
        }
        catch (IllegalArgumentException e) {
            throw new ParseException("Invalid format name: " + e.getMessage(), this.template, start, e.getCause());
        }
        catch (UnregisteredOutputFormatException e) {
            throw new ParseException(e.getMessage(), this.template, start, e.getCause());
        }
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(48);
        OutputFormatBlock result = new OutputFormatBlock(children, paramExp);
        result.setLocation(this.template, start, end);
        this.outputFormat = lastOutputFormat;
        this.recalculateAutoEscapingField();
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final AutoEscBlock AutoEsc() throws ParseException {
        Token start = this.jj_consume_token(30);
        this.checkCurrentOutputFormatCanEscape(start);
        int lastAutoEscapingPolicy = this.autoEscapingPolicy;
        this.autoEscapingPolicy = 22;
        this.recalculateAutoEscapingField();
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(49);
        AutoEscBlock result = new AutoEscBlock(children);
        result.setLocation(this.template, start, end);
        this.autoEscapingPolicy = lastAutoEscapingPolicy;
        this.recalculateAutoEscapingField();
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final NoAutoEscBlock NoAutoEsc() throws ParseException {
        Token start = this.jj_consume_token(31);
        int lastAutoEscapingPolicy = this.autoEscapingPolicy;
        this.autoEscapingPolicy = 20;
        this.recalculateAutoEscapingField();
        TemplateElements children = this.MixedContentElements();
        Token end = this.jj_consume_token(50);
        NoAutoEscBlock result = new NoAutoEscBlock(children);
        result.setLocation(this.template, start, end);
        this.autoEscapingPolicy = lastAutoEscapingPolicy;
        this.recalculateAutoEscapingField();
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Token LooseDirectiveEnd() throws ParseException {
        Token t;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 148: {
                t = this.jj_consume_token(148);
                break;
            }
            case 149: {
                t = this.jj_consume_token(149);
                break;
            }
            default: {
                this.jj_la1[93] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return t;
        }
        throw new Error("Missing return statement in function");
    }

    public final PropertySetting Setting() throws ParseException {
        Token start = this.jj_consume_token(28);
        Token key = this.jj_consume_token(142);
        this.jj_consume_token(105);
        Expression value = this.Expression();
        Token end = this.LooseDirectiveEnd();
        this.token_source.checkNamingConvention(key);
        PropertySetting result = new PropertySetting(key, this.token_source, value, this.template.getConfiguration());
        result.setLocation(this.template, start, end);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement FreemarkerDirective() throws ParseException {
        TemplateElement tp;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 8: {
                tp = this.If();
                break;
            }
            case 10: {
                tp = this.List();
                break;
            }
            case 13: {
                tp = this.ForEach();
                break;
            }
            case 16: 
            case 17: 
            case 18: {
                tp = this.Assign();
                break;
            }
            case 19: {
                tp = this.Include();
                break;
            }
            case 20: {
                tp = this.Import();
                break;
            }
            case 21: 
            case 22: {
                tp = this.Macro();
                break;
            }
            case 32: {
                tp = this.Compress();
                break;
            }
            case 74: {
                tp = this.UnifiedMacroTransform();
                break;
            }
            case 11: {
                tp = this.Items();
                break;
            }
            case 12: {
                tp = this.Sep();
                break;
            }
            case 27: {
                tp = this.Call();
                break;
            }
            case 33: 
            case 34: {
                tp = this.Comment();
                break;
            }
            case 35: {
                tp = this.NoParse();
                break;
            }
            case 23: {
                tp = this.Transform();
                break;
            }
            case 14: {
                tp = this.Switch();
                break;
            }
            case 28: {
                tp = this.Setting();
                break;
            }
            case 55: {
                tp = this.Break();
                break;
            }
            case 56: {
                tp = this.Continue();
                break;
            }
            case 26: 
            case 57: {
                tp = this.Return();
                break;
            }
            case 25: 
            case 58: {
                tp = this.Stop();
                break;
            }
            case 59: {
                tp = this.Flush();
                break;
            }
            case 60: 
            case 61: 
            case 62: 
            case 63: {
                tp = this.Trim();
                break;
            }
            case 65: 
            case 66: {
                tp = this.Nested();
                break;
            }
            case 70: {
                tp = this.Escape();
                break;
            }
            case 72: {
                tp = this.NoEscape();
                break;
            }
            case 24: {
                tp = this.Visit();
                break;
            }
            case 67: 
            case 68: {
                tp = this.Recurse();
                break;
            }
            case 69: {
                tp = this.FallBack();
                break;
            }
            case 6: {
                tp = this.Attempt();
                break;
            }
            case 29: {
                tp = this.OutputFormat();
                break;
            }
            case 30: {
                tp = this.AutoEsc();
                break;
            }
            case 31: {
                tp = this.NoAutoEsc();
                break;
            }
            default: {
                this.jj_la1[94] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if ("" != null) {
            return tp;
        }
        throw new Error("Missing return statement in function");
    }

    public final TextBlock PCData() throws ParseException {
        StringBuilder buf = new StringBuilder();
        Token t = null;
        Token start = null;
        Token prevToken = null;
        block8: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: {
                    t = this.jj_consume_token(79);
                    break;
                }
                case 80: {
                    t = this.jj_consume_token(80);
                    break;
                }
                case 81: {
                    t = this.jj_consume_token(81);
                    break;
                }
                default: {
                    this.jj_la1[95] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(t.image);
            if (start == null) {
                start = t;
            }
            if (prevToken != null) {
                prevToken.next = null;
            }
            prevToken = t;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: 
                case 80: 
                case 81: {
                    continue block8;
                }
            }
            break;
        }
        this.jj_la1[96] = this.jj_gen;
        if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != null) {
            return null;
        }
        TextBlock result = new TextBlock(buf.toString(), false);
        result.setLocation(this.template, start, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final TextBlock WhitespaceText() throws ParseException {
        Token t = null;
        Object start = null;
        t = this.jj_consume_token(79);
        if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != null) {
            return null;
        }
        TextBlock result = new TextBlock(t.image, false);
        result.setLocation(this.template, t, t);
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final Token UnparsedContent(Token start, StringBuilder buf) throws ParseException {
        Token t;
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 156: {
                    t = this.jj_consume_token(156);
                    break;
                }
                case 155: {
                    t = this.jj_consume_token(155);
                    break;
                }
                case 154: {
                    t = this.jj_consume_token(154);
                    break;
                }
                case 157: {
                    t = this.jj_consume_token(157);
                    break;
                }
                default: {
                    this.jj_la1[97] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            buf.append(t.image);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 154: 
                case 155: 
                case 156: 
                case 157: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[98] = this.jj_gen;
        buf.setLength(buf.length() - t.image.length());
        if (!t.image.endsWith(";") && _TemplateAPI.getTemplateLanguageVersionAsInt(this.template) >= _VersionInts.V_2_3_21) {
            throw new ParseException("Unclosed \"" + start.image + "\"", this.template, start);
        }
        if ("" != null) {
            return t;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElements MixedContentElements() throws ParseException {
        TemplateElement[] childBuffer = null;
        int childCount = 0;
        ++this.mixedContentNesting;
        block9: while (true) {
            TemplateElement elem;
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 6: 
                case 8: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 72: 
                case 74: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: {
                    break;
                }
                default: {
                    this.jj_la1[99] = this.jj_gen;
                    break block9;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: 
                case 80: 
                case 81: {
                    elem = this.PCData();
                    break;
                }
                case 82: 
                case 84: {
                    elem = this.StringOutput();
                    break;
                }
                case 83: {
                    elem = this.NumericalOutput();
                    break;
                }
                case 6: 
                case 8: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 72: 
                case 74: {
                    elem = this.FreemarkerDirective();
                    break;
                }
                default: {
                    this.jj_la1[100] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (elem == null) continue;
            ++childCount;
            if (childBuffer == null) {
                childBuffer = new TemplateElement[16];
            } else if (childBuffer.length < childCount) {
                TemplateElement[] newChildBuffer = new TemplateElement[childCount * 2];
                for (int i = 0; i < childBuffer.length; ++i) {
                    newChildBuffer[i] = childBuffer[i];
                }
                childBuffer = newChildBuffer;
            }
            childBuffer[childCount - 1] = elem;
        }
        --this.mixedContentNesting;
        if ("" != null) {
            return childBuffer != null ? new TemplateElements(childBuffer, childCount) : TemplateElements.EMPTY;
        }
        throw new Error("Missing return statement in function");
    }

    public final MixedContent MixedContent() throws ParseException {
        TemplateElement elem;
        MixedContent mixedContent = new MixedContent();
        TemplateElement begin = null;
        ++this.mixedContentNesting;
        block9: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: 
                case 80: 
                case 81: {
                    elem = this.PCData();
                    break;
                }
                case 82: 
                case 84: {
                    elem = this.StringOutput();
                    break;
                }
                case 83: {
                    elem = this.NumericalOutput();
                    break;
                }
                case 6: 
                case 8: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 72: 
                case 74: {
                    elem = this.FreemarkerDirective();
                    break;
                }
                default: {
                    this.jj_la1[101] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (begin == null) {
                begin = elem;
            }
            mixedContent.addElement(elem);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 6: 
                case 8: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 55: 
                case 56: 
                case 57: 
                case 58: 
                case 59: 
                case 60: 
                case 61: 
                case 62: 
                case 63: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 72: 
                case 74: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: {
                    continue block9;
                }
            }
            break;
        }
        this.jj_la1[102] = this.jj_gen;
        --this.mixedContentNesting;
        mixedContent.setLocation(this.template, (TemplateObject)begin, (TemplateObject)elem);
        if ("" != null) {
            return mixedContent;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement OptionalBlock() throws ParseException {
        MixedContent tp = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 6: 
            case 8: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: 
            case 31: 
            case 32: 
            case 33: 
            case 34: 
            case 35: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 59: 
            case 60: 
            case 61: 
            case 62: 
            case 63: 
            case 65: 
            case 66: 
            case 67: 
            case 68: 
            case 69: 
            case 70: 
            case 72: 
            case 74: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: {
                tp = this.MixedContent();
                break;
            }
            default: {
                this.jj_la1[103] = this.jj_gen;
            }
        }
        if ("" != null) {
            return tp != null ? tp : new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false);
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement FreeMarkerText() throws ParseException {
        TemplateElement elem;
        MixedContent nodes = new MixedContent();
        TemplateElement begin = null;
        block8: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: 
                case 80: 
                case 81: {
                    elem = this.PCData();
                    break;
                }
                case 82: 
                case 84: {
                    elem = this.StringOutput();
                    break;
                }
                case 83: {
                    elem = this.NumericalOutput();
                    break;
                }
                default: {
                    this.jj_la1[104] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (begin == null) {
                begin = elem;
            }
            nodes.addChild(elem);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: {
                    continue block8;
                }
            }
            break;
        }
        this.jj_la1[105] = this.jj_gen;
        nodes.setLocation(this.template, (TemplateObject)begin, (TemplateObject)elem);
        if ("" != null) {
            return nodes;
        }
        throw new Error("Missing return statement in function");
    }

    public final MixedContent WhitespaceAndComments() throws ParseException {
        TemplateElement elem;
        MixedContent nodes = new MixedContent();
        TemplateElement begin = null;
        block7: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 79: {
                    elem = this.WhitespaceText();
                    break;
                }
                case 33: 
                case 34: {
                    elem = this.Comment();
                    break;
                }
                default: {
                    this.jj_la1[106] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            if (elem != null) {
                if (begin == null) {
                    begin = elem;
                }
                nodes.addChild(elem);
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 33: 
                case 34: 
                case 79: {
                    continue block7;
                }
            }
            break;
        }
        this.jj_la1[107] = this.jj_gen;
        if ((begin == null || this.stripWhitespace && !this.preventStrippings && nodes.getChildCount() == 1 && nodes.getChild(0) instanceof TextBlock) && "" != null) {
            return null;
        }
        nodes.setLocation(this.template, (TemplateObject)begin, (TemplateObject)elem);
        if ("" != null) {
            return nodes;
        }
        throw new Error("Missing return statement in function");
    }

    /*
     * Unable to fully structure code
     */
    public final void HeaderElement() throws ParseException {
        exp = null;
        autoEscRequester = null;
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 79: {
                this.jj_consume_token(79);
                break;
            }
            default: {
                this.jj_la1[108] = this.jj_gen;
            }
        }
        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
            case 77: {
                this.jj_consume_token(77);
                break;
            }
            case 76: {
                this.jj_consume_token(76);
                block23: while (true) lbl-1000:
                // 12 sources

                {
                    switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                        case 142: {
                            break;
                        }
                        default: {
                            this.jj_la1[109] = this.jj_gen;
                            ** GOTO lbl122
                        }
                    }
                    key = this.jj_consume_token(142);
                    this.jj_consume_token(105);
                    exp = this.Expression();
                    this.token_source.checkNamingConvention(key);
                    ks = key.image;
                    value = null;
                    try {
                        value = exp.eval(null);
                    }
                    catch (Exception e) {
                        throw new ParseException("Could not evaluate expression (on parse-time): " + exp.getCanonicalForm() + " \nUnderlying cause: " + e, exp, e);
                    }
                    vs = null;
                    if (value instanceof TemplateScalarModel) {
                        try {
                            vs = ((TemplateScalarModel)exp).getAsString();
                        }
                        catch (TemplateModelException var7_9) {
                            // empty catch block
                        }
                    }
                    if (this.template == null) continue;
                    if (ks.equalsIgnoreCase("encoding")) {
                        if (vs == null) {
                            throw new ParseException("Expected a string constant for \"" + ks + "\".", exp);
                        }
                        encoding = this.template.getEncoding();
                        if (encoding == null || encoding.equalsIgnoreCase(vs)) continue;
                        throw new Template.WrongEncodingException(vs, encoding);
                    }
                    if (ks.equalsIgnoreCase("STRIP_WHITESPACE") || ks.equals("stripWhitespace")) {
                        this.stripWhitespace = this.getBoolean(exp, true);
                        continue;
                    }
                    if (ks.equalsIgnoreCase("STRIP_TEXT") || ks.equals("stripText")) {
                        this.stripText = this.getBoolean(exp, true);
                        continue;
                    }
                    if (ks.equalsIgnoreCase("STRICT_SYNTAX") || ks.equals("strictSyntax")) {
                        this.token_source.strictSyntaxMode = this.getBoolean(exp, true);
                        continue;
                    }
                    if (ks.equalsIgnoreCase("auto_esc") || ks.equals("autoEsc")) {
                        if (this.getBoolean(exp, false)) {
                            autoEscRequester = key;
                            this.autoEscapingPolicy = 22;
                        } else {
                            this.autoEscapingPolicy = 20;
                        }
                        this.recalculateAutoEscapingField();
                        _TemplateAPI.setAutoEscaping(this.template, this.autoEscaping);
                        continue;
                    }
                    if (ks.equalsIgnoreCase("output_format") || ks.equals("outputFormat")) {
                        if (vs == null) {
                            throw new ParseException("Expected a string constant for \"" + ks + "\".", exp);
                        }
                        try {
                            this.outputFormat = this.template.getConfiguration().getOutputFormat(vs);
                        }
                        catch (IllegalArgumentException e) {
                            throw new ParseException("Invalid format name: " + e.getMessage(), exp, e.getCause());
                        }
                        catch (UnregisteredOutputFormatException e) {
                            throw new ParseException(e.getMessage(), exp, e.getCause());
                        }
                        this.recalculateAutoEscapingField();
                        _TemplateAPI.setOutputFormat(this.template, this.outputFormat);
                        _TemplateAPI.setAutoEscaping(this.template, this.autoEscaping);
                        continue;
                    }
                    if (ks.equalsIgnoreCase("ns_prefixes") || ks.equals("nsPrefixes")) {
                        if (!(value instanceof TemplateHashModelEx)) {
                            throw new ParseException("Expecting a hash of prefixes to namespace URI's.", exp);
                        }
                        prefixMap = (TemplateHashModelEx)value;
                        try {
                            keys = prefixMap.keys();
                            it = keys.iterator();
                            while (true) lbl-1000:
                            // 2 sources

                            {
                                if (!it.hasNext()) ** GOTO lbl-1000
                                prefix = ((TemplateScalarModel)it.next()).getAsString();
                                valueModel = prefixMap.get(prefix);
                                if (!(valueModel instanceof TemplateScalarModel)) {
                                    throw new ParseException("Non-string value in prefix to namespace hash.", exp);
                                }
                                nsURI = ((TemplateScalarModel)valueModel).getAsString();
                                try {
                                    this.template.addPrefixNSMapping(prefix, nsURI);
                                    continue;
                                }
                                catch (IllegalArgumentException iae) {
                                    throw new ParseException(iae.getMessage(), exp);
                                }
                                break;
                            }
                        }
                        catch (TemplateModelException keys) {}
                        {
                            ** while (true)
                        }
                        continue;
                    }
                    if (!ks.equalsIgnoreCase("attributes")) break;
                    if (!(value instanceof TemplateHashModelEx)) {
                        throw new ParseException("Expecting a hash of attribute names to values.", exp);
                    }
                    attributeMap = (TemplateHashModelEx)value;
                    try {
                        keys = attributeMap.keys();
                        it = keys.iterator();
                        while (true) {
                            if (!it.hasNext()) continue block23;
                            attName = ((TemplateScalarModel)it.next()).getAsString();
                            attValue = DeepUnwrap.unwrap(attributeMap.get(attName));
                            this.template.setCustomAttribute(attName, attValue);
                        }
                    }
                    catch (TemplateModelException var8_14) {
                        continue;
                    }
                    break;
                }
                correctName = ks.equals("charset") != false ? "encoding" : (ks.equals("xmlns") != false ? (this.token_source.namingConvention == 12 ? "nsPrefixes" : "ns_prefixes") : (ks.equals("auto_escape") != false || ks.equals("auto_escaping") != false || ks.equals("autoesc") != false ? "auto_esc" : (ks.equals("autoEscape") != false || ks.equals("autoEscaping") != false ? "autoEsc" : null)));
                throw new ParseException("Unknown FTL header parameter: " + key.image + (correctName == null ? "" : ". You may meant: " + correctName), this.template, key);
lbl122:
                // 1 sources

                if (autoEscRequester != null) {
                    this.checkCurrentOutputFormatCanEscape(autoEscRequester);
                }
                this.LooseDirectiveEnd();
                break;
            }
            default: {
                this.jj_la1[110] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }

    public final Map ParamList() throws ParseException {
        HashMap<String, Expression> result = new HashMap<String, Expression>();
        block6: while (true) {
            Identifier id = this.Identifier();
            this.jj_consume_token(105);
            Expression exp = this.Expression();
            result.put(id.toString(), exp);
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 130: {
                    this.jj_consume_token(130);
                    break;
                }
                default: {
                    this.jj_la1[111] = this.jj_gen;
                }
            }
            switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                case 142: {
                    continue block6;
                }
            }
            break;
        }
        this.jj_la1[112] = this.jj_gen;
        if ("" != null) {
            return result;
        }
        throw new Error("Missing return statement in function");
    }

    public final List<Object> StaticTextAndInterpolations() throws ParseException {
        ArrayList<Object> parts;
        StringBuilder staticTextCollector;
        block21: {
            staticTextCollector = null;
            parts = new ArrayList<Object>();
            block12: while (true) {
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: {
                        break;
                    }
                    default: {
                        this.jj_la1[113] = this.jj_gen;
                        break block21;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                    case 79: 
                    case 80: 
                    case 81: {
                        String s;
                        Token t;
                        switch (this.jj_ntk == -1 ? this.jj_ntk_f() : this.jj_ntk) {
                            case 79: {
                                t = this.jj_consume_token(79);
                                break;
                            }
                            case 80: {
                                t = this.jj_consume_token(80);
                                break;
                            }
                            case 81: {
                                t = this.jj_consume_token(81);
                                break;
                            }
                            default: {
                                this.jj_la1[114] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        if ((s = t.image).length() == 0) continue block12;
                        if (staticTextCollector == null) {
                            staticTextCollector = new StringBuilder(t.image);
                            continue block12;
                        }
                        staticTextCollector.append(t.image);
                        continue block12;
                    }
                    case 82: 
                    case 83: 
                    case 84: {
                        Interpolation interpolation;
                        if (this.jj_2_15(Integer.MAX_VALUE)) {
                            interpolation = this.StringOutput();
                        } else if (this.jj_2_16(Integer.MAX_VALUE)) {
                            interpolation = this.NumericalOutput();
                        } else {
                            this.jj_consume_token(-1);
                            throw new ParseException();
                        }
                        if (staticTextCollector != null) {
                            parts.add(staticTextCollector.toString());
                            staticTextCollector.setLength(0);
                        }
                        parts.add(interpolation);
                        continue block12;
                    }
                }
                break;
            }
            this.jj_la1[115] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        if (staticTextCollector != null && staticTextCollector.length() != 0) {
            parts.add(staticTextCollector.toString());
        }
        parts.trimToSize();
        if ("" != null) {
            return parts;
        }
        throw new Error("Missing return statement in function");
    }

    public final TemplateElement Root() throws ParseException {
        if (this.jj_2_17(Integer.MAX_VALUE)) {
            this.HeaderElement();
        }
        TemplateElements children = this.MixedContentElements();
        this.jj_consume_token(0);
        TemplateElement root = children.asSingleElement();
        root.setFieldsForRootElement();
        if (!this.preventStrippings) {
            root = root.postParseCleanup(this.stripWhitespace);
        }
        root.setFieldsForRootElement();
        if ("" != null) {
            return root;
        }
        throw new Error("Missing return statement in function");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_1();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(0, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_2();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(1, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_3(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_3();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(2, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_4(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_4();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(3, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_5(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_5();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(4, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_6(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_6();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(5, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_7(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_7();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(6, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_8(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_8();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(7, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_9(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_9();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(8, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_10(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_10();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(9, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_11(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_11();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(10, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_12(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_12();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(11, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_13(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_13();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(12, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_14(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_14();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(13, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_15(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_15();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(14, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_16(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_16();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(15, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_17(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_17();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(16, xla);
        }
    }

    private boolean jj_3R_66() {
        return this.jj_3R_81();
    }

    private boolean jj_3R_65() {
        return this.jj_3R_80();
    }

    private boolean jj_3_3() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(107)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(105)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(106)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3_12() {
        if (this.jj_scan_token(142)) {
            return true;
        }
        return this.jj_scan_token(105);
    }

    private boolean jj_3R_64() {
        return this.jj_3R_79();
    }

    private boolean jj_3R_102() {
        return this.jj_3R_88();
    }

    private boolean jj_3R_95() {
        return this.jj_scan_token(94);
    }

    private boolean jj_3R_60() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_64()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_65()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_66()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_67()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_68()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_69()) {
                                this.jj_scanpos = xsp;
                                if (this.jj_3R_70()) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_3R_71()) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_57() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(107)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(105)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(106)) {
                    return true;
                }
            }
        }
        return this.jj_3R_56();
    }

    private boolean jj_3R_43() {
        return this.jj_scan_token(142);
    }

    private boolean jj_3R_81() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(93)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_95()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_50() {
        if (this.jj_3R_56()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_57()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_101() {
        if (this.jj_scan_token(135)) {
            return true;
        }
        if (this.jj_3R_109()) {
            return true;
        }
        return this.jj_scan_token(136);
    }

    private boolean jj_3R_77() {
        return this.jj_3R_91();
    }

    private boolean jj_3R_76() {
        return this.jj_3R_90();
    }

    private boolean jj_3R_88() {
        if (this.jj_scan_token(135)) {
            return true;
        }
        if (this.jj_3R_98()) {
            return true;
        }
        return this.jj_scan_token(136);
    }

    private boolean jj_3R_75() {
        return this.jj_3R_89();
    }

    private boolean jj_3R_74() {
        return this.jj_3R_88();
    }

    private boolean jj_3R_114() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_3R_113();
    }

    private boolean jj_3R_73() {
        return this.jj_3R_87();
    }

    private boolean jj_3R_49() {
        return this.jj_scan_token(126);
    }

    private boolean jj_3R_79() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(97)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(98)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_61() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_72()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_73()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_74()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_75()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_76()) {
                            this.jj_scanpos = xsp;
                            if (this.jj_3R_77()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_72() {
        return this.jj_3R_86();
    }

    private boolean jj_3R_48() {
        return this.jj_scan_token(125);
    }

    private boolean jj_3R_47() {
        return this.jj_scan_token(122);
    }

    private boolean jj_3R_112() {
        Token xsp;
        if (this.jj_3R_113()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_114());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_100() {
        if (this.jj_scan_token(135)) {
            return true;
        }
        if (this.jj_3R_98()) {
            return true;
        }
        return this.jj_scan_token(136);
    }

    private boolean jj_3_2() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(122)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(125)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(126)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3_17() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(79)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(77)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(76)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_55() {
        Token xsp;
        if (this.jj_3R_60()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_61());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_109() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_112()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_37() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_47()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_48()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_49()) {
                    return true;
                }
            }
        }
        return this.jj_3R_36();
    }

    private boolean jj_3R_83() {
        if (this.jj_scan_token(133)) {
            return true;
        }
        if (this.jj_3R_98()) {
            return true;
        }
        return this.jj_scan_token(134);
    }

    private boolean jj_3R_87() {
        if (this.jj_scan_token(133)) {
            return true;
        }
        if (this.jj_3R_29()) {
            return true;
        }
        return this.jj_scan_token(134);
    }

    private boolean jj_3R_111() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        return this.jj_3R_29();
    }

    private boolean jj_3R_31() {
        Token xsp;
        if (this.jj_3R_36()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_37());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_29() {
        return this.jj_3R_33();
    }

    private boolean jj_3_7() {
        return this.jj_scan_token(128);
    }

    private boolean jj_3R_108() {
        Token xsp;
        if (this.jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_111());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_98() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_108()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_41() {
        if (this.jj_scan_token(128)) {
            return true;
        }
        return this.jj_3R_40();
    }

    private boolean jj_3_16() {
        return this.jj_scan_token(83);
    }

    private boolean jj_3_15() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(82)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(84)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_33() {
        Token xsp;
        if (this.jj_3R_40()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_41());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_39() {
        return this.jj_scan_token(121);
    }

    private boolean jj_3R_38() {
        return this.jj_scan_token(120);
    }

    private boolean jj_3_1() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(120)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(121)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_6() {
        return this.jj_scan_token(127);
    }

    private boolean jj_3R_32() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_38()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_39()) {
                return true;
            }
        }
        return this.jj_3R_31();
    }

    private boolean jj_3R_99() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(115)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(116)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(117)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(118)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(95)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(96)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(139)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(140)) {
                                        this.jj_scanpos = xsp;
                                        if (this.jj_scan_token(141)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_51() {
        if (this.jj_scan_token(127)) {
            return true;
        }
        return this.jj_3R_50();
    }

    private boolean jj_3R_28() {
        Token xsp;
        if (this.jj_3R_31()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_32());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_89() {
        if (this.jj_scan_token(103)) {
            return true;
        }
        if (this.jj_scan_token(142)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_100()) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_101()) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_3R_102()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_40() {
        Token xsp;
        if (this.jj_3R_50()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_51());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_86() {
        if (this.jj_scan_token(99)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(142)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(122)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(123)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_99()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3_13() {
        return this.jj_scan_token(135);
    }

    private boolean jj_3_5() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_58() {
        return this.jj_scan_token(121);
    }

    private boolean jj_3_14() {
        if (this.jj_scan_token(142)) {
            return true;
        }
        return this.jj_scan_token(105);
    }

    private boolean jj_3R_106() {
        return this.jj_3R_28();
    }

    private boolean jj_3R_53() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(120)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_58()) {
                return true;
            }
        }
        return this.jj_3R_55();
    }

    private boolean jj_3R_91() {
        return this.jj_scan_token(104);
    }

    private boolean jj_3_8() {
        return this.jj_3R_29();
    }

    private boolean jj_3R_52() {
        if (this.jj_scan_token(130)) {
            return true;
        }
        return this.jj_3R_43();
    }

    private boolean jj_3R_35() {
        return this.jj_3R_43();
    }

    private boolean jj_3R_107() {
        if (this.jj_scan_token(130)) {
            return true;
        }
        if (this.jj_3R_29()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(130)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(132)) {
                return true;
            }
        }
        return this.jj_3R_29();
    }

    private boolean jj_3R_105() {
        return this.jj_scan_token(102);
    }

    private boolean jj_3R_104() {
        return this.jj_scan_token(101);
    }

    private boolean jj_3R_110() {
        return this.jj_3R_29();
    }

    private boolean jj_3R_93() {
        if (this.jj_scan_token(100)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_106()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_42() {
        Token xsp;
        if (this.jj_3R_43()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_52());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_59() {
        return this.jj_scan_token(129);
    }

    private boolean jj_3R_92() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_104()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_105()) {
                return true;
            }
        }
        return this.jj_3R_28();
    }

    private boolean jj_3R_94() {
        if (this.jj_3R_29()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(130)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(132)) {
                return true;
            }
        }
        if (this.jj_3R_29()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_107());
        this.jj_scanpos = xsp;
        return false;
    }

    private boolean jj_3R_103() {
        if (this.jj_scan_token(129)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_110()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_54() {
        Token xsp;
        if (this.jj_3R_59()) {
            return true;
        }
        do {
            xsp = this.jj_scanpos;
        } while (!this.jj_3R_59());
        this.jj_scanpos = xsp;
        return this.jj_3R_55();
    }

    private boolean jj_3R_34() {
        if (this.jj_scan_token(135)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_42()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(136);
    }

    private boolean jj_3R_78() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_92()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_93()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_80() {
        if (this.jj_scan_token(137)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_94()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(138);
    }

    private boolean jj_3R_90() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(153)) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_103()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_30() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_34()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_35()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_46() {
        return this.jj_3R_55();
    }

    private boolean jj_3R_62() {
        if (this.jj_3R_28()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_78()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_45() {
        return this.jj_3R_54();
    }

    private boolean jj_3_11() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(130)) {
            this.jj_scanpos = xsp;
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(142)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(93)) {
                return true;
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(105)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(108)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(109)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(110)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(111)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(112)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(113)) {
                                    this.jj_scanpos = xsp;
                                    if (this.jj_scan_token(114)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_44() {
        return this.jj_3R_53();
    }

    private boolean jj_3R_97() {
        return this.jj_scan_token(96);
    }

    private boolean jj_3R_116() {
        return this.jj_3R_33();
    }

    private boolean jj_3R_36() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_44()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_45()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_46()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3R_96() {
        return this.jj_scan_token(95);
    }

    private boolean jj_3R_82() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_96()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_97()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3_10() {
        if (this.jj_3R_30()) {
            return true;
        }
        return this.jj_scan_token(119);
    }

    private boolean jj_3_4() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(151)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(118)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(150)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(117)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(116)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(116)) {
                                this.jj_scanpos = xsp;
                                if (this.jj_scan_token(115)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean jj_3R_115() {
        if (this.jj_3R_30()) {
            return true;
        }
        if (this.jj_scan_token(119)) {
            return true;
        }
        return this.jj_3R_33();
    }

    private boolean jj_3R_84() {
        if (this.jj_scan_token(135)) {
            return true;
        }
        if (this.jj_3R_29()) {
            return true;
        }
        return this.jj_scan_token(136);
    }

    private boolean jj_3R_63() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(151)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(118)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(150)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(117)) {
                        this.jj_scanpos = xsp;
                        if (this.jj_scan_token(116)) {
                            this.jj_scanpos = xsp;
                            if (this.jj_scan_token(115)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return this.jj_3R_62();
    }

    private boolean jj_3R_113() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_115()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_116()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_85() {
        if (this.jj_scan_token(99)) {
            return true;
        }
        return this.jj_scan_token(142);
    }

    private boolean jj_3R_56() {
        if (this.jj_3R_62()) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_63()) {
            this.jj_scanpos = xsp;
        }
        return false;
    }

    private boolean jj_3R_71() {
        return this.jj_3R_85();
    }

    private boolean jj_3R_70() {
        return this.jj_3R_84();
    }

    private boolean jj_3R_69() {
        return this.jj_3R_43();
    }

    private boolean jj_3_9() {
        return this.jj_scan_token(135);
    }

    private boolean jj_3R_68() {
        return this.jj_3R_83();
    }

    private boolean jj_3R_67() {
        return this.jj_3R_82();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x4000000, 0x2000000, 0, 0, 458752, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x600000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32768, 0, 32768, 32768, 0, -33472, 0, 0, 0, 0, -33472, -33472, -33472, -33472, -33472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x400000, 768, 0, 0, 0x400000, 0, 128, 0, 0, 0, 0, 0x2000000, 0x4000000, 0, -268435456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14336, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 6, 0, 0, 0, 0, 0, -8388593, 0, 0, 0, 0, -8388593, -8388593, -8388593, -8388593, -8388593, 0, 0, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_2() {
        jj_la1_2 = new int[]{0, 0, -536870912, -536870912, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x60000000, 0, -536870912, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 0x60000000, Integer.MIN_VALUE, 0, 0, 0, -536870912, 0x140000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, 24, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, -536870912, -536870912, 0, -536870912, 0, 0, 0, 0, 32768, 1, 32768, 1, 1, 0, 1406, 229376, 229376, 0, 0, 2065790, 2065790, 2065790, 2065790, 2065790, 2064384, 2064384, 32768, 32768, 32768, 0, 12288, 0, 0, 2064384, 229376, 2064384};
    }

    private static void jj_la1_init_3() {
        jj_la1_3 = new int[]{392, 392, 15, 0x300000F, 0, 0x3000000, 0x3000000, 0x64000000, 3584, 0x780000, 96, 112, 112, 6, 0, 0, 0x300000F, 0, 0, 0, 7864321, 209190913, 0, 1, 0, 0, 0, 0x300000F, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x300000F, 0, 0, 0, 0, 0, 0, 0, 127488, 393216, 520704, 0, 127488, 393216, 520704, 0, 0, 0, 520704, 0, 0, 0, 0, 0, 0x10000000, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0x300000F, 0, 0x300000F, 0x300000F, 0, 0x300000F, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private static void jj_la1_init_4() {
        jj_la1_4 = new int[]{0x20000A2, 0x20000A2, 17056, 17058, 2, 0, 0, 0, 0, 0xC00000, 0, 0, 0, 0, 16384, 0x2000002, 17058, 4, 16384, 16512, 14336, 30720, 0, 0, 20, 4, 20, 17058, 0, 8, 0, 0, 0, 4, 4096, 0, 4, 0, 8192, 17058, 8192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2048, 2048, 0, 0x100800, 8, 16384, 0, 128, 16384, 0, 0, 4, 256, 0, 0x1000000, 0x1000000, 0x1000004, 0x1000000, 0x1000000, 0x1004000, 8, 0x300000, 256, 16384, 17062, 4, 17058, 17062, 4, 17058, 0, 8, 16384, 0x300000, 0, 0, 0, 0, 0, 0x300000, 0, 0, 0, 0x3C000000, 0x3C000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16384, 0, 4, 16384, 0, 0, 0};
    }

    public FMParser(InputStream stream) {
        this(stream, null);
    }

    public FMParser(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new FMParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(InputStream stream) {
        this.ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        int i;
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public FMParser(Reader stream) {
        int i;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new FMParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(Reader stream) {
        int i;
        if (this.jj_input_stream == null) {
            this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        } else {
            this.jj_input_stream.ReInit(stream, 1, 1);
        }
        if (this.token_source == null) {
            this.token_source = new FMParserTokenManager(this.jj_input_stream);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public FMParser(FMParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(FMParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 116; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    JJCalls c = this.jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                        c = c.next;
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
                this.jj_lastpos = this.jj_scanpos.next;
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;
            while (tok != null && tok != this.jj_scanpos) {
                ++i;
                tok = tok.next;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk_f() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                boolean isMatched = true;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] == this.jj_expentry[i]) continue;
                    isMatched = false;
                    break;
                }
                if (!isMatched) continue;
                this.jj_expentries.add(this.jj_expentry);
                break;
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[158];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 116; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) != 0) {
                    la1tokens[32 + j] = true;
                }
                if ((jj_la1_2[i] & 1 << j) != 0) {
                    la1tokens[64 + j] = true;
                }
                if ((jj_la1_3[i] & 1 << j) != 0) {
                    la1tokens[96 + j] = true;
                }
                if ((jj_la1_4[i] & 1 << j) == 0) continue;
                la1tokens[128 + j] = true;
            }
        }
        for (i = 0; i < 158; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 17; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
                            break;
                        }
                        case 2: {
                            this.jj_3_3();
                            break;
                        }
                        case 3: {
                            this.jj_3_4();
                            break;
                        }
                        case 4: {
                            this.jj_3_5();
                            break;
                        }
                        case 5: {
                            this.jj_3_6();
                            break;
                        }
                        case 6: {
                            this.jj_3_7();
                            break;
                        }
                        case 7: {
                            this.jj_3_8();
                            break;
                        }
                        case 8: {
                            this.jj_3_9();
                            break;
                        }
                        case 9: {
                            this.jj_3_10();
                            break;
                        }
                        case 10: {
                            this.jj_3_11();
                            break;
                        }
                        case 11: {
                            this.jj_3_12();
                            break;
                        }
                        case 12: {
                            this.jj_3_13();
                            break;
                        }
                        case 13: {
                            this.jj_3_14();
                            break;
                        }
                        case 14: {
                            this.jj_3_15();
                            break;
                        }
                        case 15: {
                            this.jj_3_16();
                            break;
                        }
                        case 16: {
                            this.jj_3_17();
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (LookaheadSuccess lookaheadSuccess) {
                // empty catch block
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];
        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        FMParser.jj_la1_init_0();
        FMParser.jj_la1_init_1();
        FMParser.jj_la1_init_2();
        FMParser.jj_la1_init_3();
        FMParser.jj_la1_init_4();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }
    }

    private static class ParserIteratorBlockContext {
        private String loopVarName;
        private String loopVar2Name;
        private int kind;
        private boolean hashListing;

        private ParserIteratorBlockContext() {
        }
    }
}

