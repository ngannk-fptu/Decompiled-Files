/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.cache.TemplateNameFormat;
import freemarker.cache._CacheAPI;
import freemarker.core.AttemptBlock;
import freemarker.core.BackwardCompatibleTemplateNumberFormat;
import freemarker.core.BodyInstruction;
import freemarker.core.CFormat;
import freemarker.core.Configurable;
import freemarker.core.DirectiveCallPlace;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.FlowControlException;
import freemarker.core.ISOTemplateDateFormatFactory;
import freemarker.core.Identifier;
import freemarker.core.IteratorBlock;
import freemarker.core.JavaTemplateDateFormatFactory;
import freemarker.core.JavaTemplateNumberFormatFactory;
import freemarker.core.LegacyCFormat;
import freemarker.core.LocalContext;
import freemarker.core.LocalContextStack;
import freemarker.core.Macro;
import freemarker.core.RecoveryBlock;
import freemarker.core.ReturnInstruction;
import freemarker.core.StopException;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateNullModel;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.core.TemplateObject;
import freemarker.core.TemplateValueFormatException;
import freemarker.core.UndefinedCustomFormatException;
import freemarker.core.UnformattableValueException;
import freemarker.core.UnifiedCall;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.core.XSTemplateDateFormatFactory;
import freemarker.core._DelayedAOrAn;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._DelayedJQuote;
import freemarker.core._DelayedJoinWithComma;
import freemarker.core._DelayedToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.TransformControl;
import freemarker.template._ObjectWrappers;
import freemarker.template._VersionInts;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.NullWriter;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.TemplateModelUtils;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public final class Environment
extends Configurable {
    private static final ThreadLocal threadEnv = new ThreadLocal();
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static final Logger ATTEMPT_LOGGER = Logger.getLogger("freemarker.runtime.attempt");
    private final Configuration configuration;
    private final boolean incompatibleImprovementsGE2328;
    private final TemplateHashModel rootDataModel;
    private TemplateElement[] instructionStack = new TemplateElement[16];
    private int instructionStackSize = 0;
    private final ArrayList recoveredErrorStack = new ArrayList();
    private TemplateNumberFormat cachedTemplateNumberFormat;
    private Map<String, TemplateNumberFormat> cachedTemplateNumberFormats;
    private TemplateDateFormat[] cachedTempDateFormatArray;
    private HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray;
    private static final int CACHED_TDFS_ZONELESS_INPUT_OFFS = 4;
    private static final int CACHED_TDFS_SQL_D_T_TZ_OFFS = 8;
    private static final int CACHED_TDFS_LENGTH = 16;
    private Boolean cachedSQLDateAndTimeTimeZoneSameAsNormal;
    @Deprecated
    private NumberFormat cNumberFormat;
    private TemplateNumberFormat cTemplateNumberFormat;
    private TemplateNumberFormat cTemplateNumberFormatWithPre2331IcIBug;
    private Configurable trueAndFalseStringsCachedForParent;
    private String cachedTrueString;
    private String cachedFalseString;
    private DateUtil.DateToISO8601CalendarFactory isoBuiltInCalendarFactory;
    private Collator cachedCollator;
    private Writer out;
    private Macro.Context currentMacroContext;
    private LocalContextStack localContextStack;
    private final Namespace mainNamespace;
    private Namespace currentNamespace;
    private Namespace globalNamespace;
    private HashMap<String, Namespace> loadedLibs;
    private Configurable legacyParent;
    private boolean inAttemptBlock;
    private Throwable lastThrowable;
    private TemplateModel lastReturnValue;
    private Map<Object, Namespace> macroToNamespaceLookup = new IdentityHashMap<Object, Namespace>();
    private TemplateNodeModel currentVisitorNode;
    private TemplateSequenceModel nodeNamespaces;
    private int nodeNamespaceIndex;
    private String currentNodeName;
    private String currentNodeNS;
    private String cachedURLEscapingCharset;
    private boolean cachedURLEscapingCharsetSet;
    private boolean fastInvalidReferenceExceptions;
    private static final TemplateModel[] NO_OUT_ARGS = new TemplateModel[0];
    static final String COMPUTER_FORMAT_STRING = "computer";
    private static final int TERSE_MODE_INSTRUCTION_STACK_TRACE_LIMIT = 10;
    private IdentityHashMap<Object, Object> customStateVariables;
    private static final Writer EMPTY_BODY_WRITER = new Writer(){

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (len > 0) {
                throw new IOException("This transform does not allow nested content.");
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    public static Environment getCurrentEnvironment() {
        return (Environment)threadEnv.get();
    }

    static void setCurrentEnvironment(Environment env) {
        threadEnv.set(env);
    }

    public Environment(Template template, TemplateHashModel rootDataModel, Writer out) {
        super(template);
        this.configuration = template.getConfiguration();
        this.incompatibleImprovementsGE2328 = this.configuration.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_28;
        this.globalNamespace = new Namespace(null);
        this.currentNamespace = this.mainNamespace = new Namespace(template);
        this.out = out;
        this.rootDataModel = rootDataModel;
        this.importMacros(template);
    }

    @Deprecated
    public Template getTemplate() {
        return (Template)this.getParent();
    }

    Template getTemplate230() {
        Template legacyParent = (Template)this.legacyParent;
        return legacyParent != null ? legacyParent : this.getTemplate();
    }

    public Template getMainTemplate() {
        return this.mainNamespace.getTemplate();
    }

    public Template getCurrentTemplate() {
        int ln = this.instructionStackSize;
        return ln == 0 ? this.getMainTemplate() : this.instructionStack[ln - 1].getTemplate();
    }

    public DirectiveCallPlace getCurrentDirectiveCallPlace() {
        int ln = this.instructionStackSize;
        if (ln == 0) {
            return null;
        }
        TemplateElement te = this.instructionStack[ln - 1];
        if (te instanceof UnifiedCall) {
            return (UnifiedCall)te;
        }
        if (te instanceof Macro && ln > 1 && this.instructionStack[ln - 2] instanceof UnifiedCall) {
            return (UnifiedCall)this.instructionStack[ln - 2];
        }
        return null;
    }

    private void clearCachedValues() {
        this.cachedTemplateNumberFormats = null;
        this.cachedTemplateNumberFormat = null;
        this.cachedTempDateFormatArray = null;
        this.cachedTempDateFormatsByFmtStrArray = null;
        this.cachedCollator = null;
        this.cachedURLEscapingCharset = null;
        this.cachedURLEscapingCharsetSet = false;
    }

    public void process() throws TemplateException, IOException {
        Object savedEnv = threadEnv.get();
        threadEnv.set(this);
        try {
            this.clearCachedValues();
            try {
                this.doAutoImportsAndIncludes(this);
                this.visit(this.getTemplate().getRootTreeNode());
                if (this.getAutoFlush()) {
                    this.out.flush();
                }
            }
            finally {
                this.clearCachedValues();
            }
        }
        finally {
            threadEnv.set(savedEnv);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void visit(TemplateElement element) throws IOException, TemplateException {
        block7: {
            this.pushElement(element);
            try {
                TemplateElement[] templateElementsToVisit = element.accept(this);
                if (templateElementsToVisit == null) break block7;
                for (TemplateElement el : templateElementsToVisit) {
                    if (el == null) {
                        break;
                    }
                    this.visit(el);
                }
            }
            catch (TemplateException te) {
                this.handleTemplateException(te);
            }
            finally {
                this.popElement();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void visit(TemplateElement[] elementBuffer) throws IOException, TemplateException {
        if (elementBuffer == null) {
            return;
        }
        block5: for (TemplateElement element : elementBuffer) {
            if (element == null) break;
            this.pushElement(element);
            try {
                TemplateElement[] templateElementsToVisit = element.accept(this);
                if (templateElementsToVisit == null) continue;
                for (TemplateElement el : templateElementsToVisit) {
                    if (el == null) {
                        continue block5;
                    }
                    this.visit(el);
                }
            }
            catch (TemplateException te) {
                this.handleTemplateException(te);
            }
            finally {
                this.popElement();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final void visit(TemplateElement[] elementBuffer, Writer out) throws IOException, TemplateException {
        Writer prevOut = this.out;
        this.out = out;
        try {
            this.visit(elementBuffer);
        }
        finally {
            this.out = prevOut;
        }
    }

    private TemplateElement replaceTopElement(TemplateElement element) {
        TemplateElement templateElement = element;
        this.instructionStack[this.instructionStackSize - 1] = templateElement;
        return templateElement;
    }

    @Deprecated
    public void visit(TemplateElement element, TemplateDirectiveModel directiveModel, Map args, List bodyParameterNames) throws TemplateException, IOException {
        this.visit(new TemplateElement[]{element}, directiveModel, args, bodyParameterNames);
    }

    void visit(TemplateElement[] childBuffer, TemplateDirectiveModel directiveModel, Map args, final List bodyParameterNames) throws TemplateException, IOException {
        NestedElementTemplateDirectiveBody nested = childBuffer == null ? null : new NestedElementTemplateDirectiveBody(childBuffer);
        final TemplateModel[] outArgs = bodyParameterNames == null || bodyParameterNames.isEmpty() ? NO_OUT_ARGS : new TemplateModel[bodyParameterNames.size()];
        if (outArgs.length > 0) {
            this.pushLocalContext(new LocalContext(){

                @Override
                public TemplateModel getLocalVariable(String name) {
                    int index = bodyParameterNames.indexOf(name);
                    return index != -1 ? outArgs[index] : null;
                }

                @Override
                public Collection getLocalVariableNames() {
                    return bodyParameterNames;
                }
            });
        }
        try {
            directiveModel.execute(this, args, outArgs, nested);
        }
        catch (FlowControlException e) {
            throw e;
        }
        catch (TemplateException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            if (EvalUtil.shouldWrapUncheckedException(e, this)) {
                throw new _MiscTemplateException((Throwable)e, this, "Directive has thrown an unchecked exception; see the cause exception.");
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new UndeclaredThrowableException(e);
        }
        finally {
            if (outArgs.length > 0) {
                this.localContextStack.pop();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void visitAndTransform(TemplateElement[] elementBuffer, TemplateTransformModel transform, Map args) throws TemplateException, IOException {
        block17: {
            try {
                Writer tw = transform.getWriter(this.out, args);
                if (tw == null) {
                    tw = EMPTY_BODY_WRITER;
                }
                TransformControl tc = tw instanceof TransformControl ? (TransformControl)((Object)tw) : null;
                Writer prevOut = this.out;
                this.out = tw;
                try {
                    if (tc == null || tc.onStart() != 0) {
                        do {
                            this.visit(elementBuffer);
                        } while (tc != null && tc.afterBody() == 0);
                    }
                }
                catch (Throwable t) {
                    try {
                        if (!(tc == null || t instanceof FlowControlException && this.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_27)) {
                            tc.onError(t);
                            break block17;
                        }
                        throw t;
                    }
                    catch (TemplateException | IOException | Error e) {
                        throw e;
                    }
                    catch (Throwable e) {
                        if (EvalUtil.shouldWrapUncheckedException(e, this)) {
                            throw new _MiscTemplateException(e, this, "Transform has thrown an unchecked exception; see the cause exception.");
                        }
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException)e;
                        }
                        throw new UndeclaredThrowableException(e);
                    }
                }
                finally {
                    this.out = prevOut;
                    if (prevOut != tw) {
                        tw.close();
                    }
                }
            }
            catch (TemplateException te) {
                this.handleTemplateException(te);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void visitAttemptRecover(AttemptBlock attemptBlock, TemplateElement attemptedSection, RecoveryBlock recoverySection) throws TemplateException, IOException {
        Writer prevOut = this.out;
        StringWriter sw = new StringWriter();
        this.out = sw;
        TemplateException thrownException = null;
        boolean lastFIRE = this.setFastInvalidReferenceExceptions(false);
        boolean lastInAttemptBlock = this.inAttemptBlock;
        try {
            this.inAttemptBlock = true;
            this.visit(attemptedSection);
        }
        catch (TemplateException te) {
            thrownException = te;
        }
        finally {
            this.inAttemptBlock = lastInAttemptBlock;
            this.setFastInvalidReferenceExceptions(lastFIRE);
            this.out = prevOut;
        }
        if (thrownException != null) {
            if (ATTEMPT_LOGGER.isDebugEnabled()) {
                ATTEMPT_LOGGER.debug("Error in attempt block " + attemptBlock.getStartLocationQuoted(), thrownException);
            }
            try {
                this.recoveredErrorStack.add(thrownException);
                this.visit(recoverySection);
            }
            finally {
                this.recoveredErrorStack.remove(this.recoveredErrorStack.size() - 1);
            }
        } else {
            this.out.write(sw.toString());
        }
    }

    String getCurrentRecoveredErrorMessage() throws TemplateException {
        if (this.recoveredErrorStack.isEmpty()) {
            throw new _MiscTemplateException(this, ".error is not available outside of a #recover block");
        }
        return ((Throwable)this.recoveredErrorStack.get(this.recoveredErrorStack.size() - 1)).getMessage();
    }

    public boolean isInAttemptBlock() {
        return this.inAttemptBlock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void invokeNestedContent(BodyInstruction.Context bodyCtx) throws TemplateException, IOException {
        TemplateElement[] nestedContentBuffer;
        Macro.Context invokingMacroContext = this.getCurrentMacroContext();
        LocalContextStack prevLocalContextStack = this.localContextStack;
        TemplateObject callPlace = invokingMacroContext.callPlace;
        TemplateElement[] templateElementArray = nestedContentBuffer = callPlace instanceof TemplateElement ? ((TemplateElement)callPlace).getChildBuffer() : null;
        if (nestedContentBuffer != null) {
            this.currentMacroContext = invokingMacroContext.prevMacroContext;
            this.currentNamespace = invokingMacroContext.nestedContentNamespace;
            boolean parentReplacementOn = this.isBeforeIcI2322();
            Configurable prevParent = this.getParent();
            if (parentReplacementOn) {
                this.setParent(this.currentNamespace.getTemplate());
            } else {
                this.legacyParent = this.currentNamespace.getTemplate();
            }
            this.localContextStack = invokingMacroContext.prevLocalContextStack;
            if (invokingMacroContext.nestedContentParameterNames != null) {
                this.pushLocalContext(bodyCtx);
            }
            try {
                this.visit(nestedContentBuffer);
            }
            finally {
                if (invokingMacroContext.nestedContentParameterNames != null) {
                    this.localContextStack.pop();
                }
                this.currentMacroContext = invokingMacroContext;
                this.currentNamespace = this.getMacroNamespace(invokingMacroContext.getMacro());
                if (parentReplacementOn) {
                    this.setParent(prevParent);
                } else {
                    this.legacyParent = prevParent;
                }
                this.localContextStack = prevLocalContextStack;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean visitIteratorBlock(IteratorBlock.IterationContext ictxt) throws TemplateException, IOException {
        this.pushLocalContext(ictxt);
        try {
            boolean bl = ictxt.accept(this);
            return bl;
        }
        catch (TemplateException te) {
            this.handleTemplateException(te);
            boolean bl = true;
            return bl;
        }
        finally {
            this.localContextStack.pop();
        }
    }

    IteratorBlock.IterationContext findEnclosingIterationContextWithVisibleVariable(String loopVarName) {
        return this.findEnclosingIterationContext(loopVarName);
    }

    IteratorBlock.IterationContext findClosestEnclosingIterationContext() {
        return this.findEnclosingIterationContext(null);
    }

    private IteratorBlock.IterationContext findEnclosingIterationContext(String visibleLoopVarName) {
        LocalContextStack ctxStack = this.getLocalContextStack();
        if (ctxStack != null) {
            for (int i = ctxStack.size() - 1; i >= 0; --i) {
                LocalContext ctx = ctxStack.get(i);
                if (!(ctx instanceof IteratorBlock.IterationContext) || visibleLoopVarName != null && !((IteratorBlock.IterationContext)ctx).hasVisibleLoopVar(visibleLoopVarName)) continue;
                return (IteratorBlock.IterationContext)ctx;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    TemplateModel evaluateWithNewLocal(Expression exp, String lambdaArgName, TemplateModel lamdaArgValue) throws TemplateException {
        this.pushLocalContext(new LocalContextWithNewLocal(lambdaArgName, lamdaArgValue));
        try {
            TemplateModel templateModel = exp.eval(this);
            return templateModel;
        }
        finally {
            this.localContextStack.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void invokeNodeHandlerFor(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
        block13: {
            if (this.nodeNamespaces == null) {
                SimpleSequence ss = new SimpleSequence(1, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                ss.add(this.currentNamespace);
                this.nodeNamespaces = ss;
            }
            int prevNodeNamespaceIndex = this.nodeNamespaceIndex;
            String prevNodeName = this.currentNodeName;
            String prevNodeNS = this.currentNodeNS;
            TemplateSequenceModel prevNodeNamespaces = this.nodeNamespaces;
            TemplateNodeModel prevVisitorNode = this.currentVisitorNode;
            this.currentVisitorNode = node;
            if (namespaces != null) {
                this.nodeNamespaces = namespaces;
            }
            try {
                TemplateModel macroOrTransform = this.getNodeProcessor(node);
                if (macroOrTransform instanceof Macro) {
                    this.invokeMacro((Macro)macroOrTransform, null, null, null, null);
                    break block13;
                }
                if (macroOrTransform instanceof TemplateTransformModel) {
                    this.visitAndTransform(null, (TemplateTransformModel)macroOrTransform, null);
                    break block13;
                }
                String nodeType = node.getNodeType();
                if (nodeType != null) {
                    if (nodeType.equals("text") && node instanceof TemplateScalarModel) {
                        this.out.write(((TemplateScalarModel)((Object)node)).getAsString());
                    } else if (nodeType.equals("document")) {
                        this.recurse(node, namespaces);
                    } else if (!(nodeType.equals("pi") || nodeType.equals("comment") || nodeType.equals("document_type"))) {
                        throw new _MiscTemplateException(this, this.noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), nodeType));
                    }
                    break block13;
                }
                throw new _MiscTemplateException(this, this.noNodeHandlerDefinedDescription(node, node.getNodeNamespace(), "default"));
            }
            finally {
                this.currentVisitorNode = prevVisitorNode;
                this.nodeNamespaceIndex = prevNodeNamespaceIndex;
                this.currentNodeName = prevNodeName;
                this.currentNodeNS = prevNodeNS;
                this.nodeNamespaces = prevNodeNamespaces;
            }
        }
    }

    private Object[] noNodeHandlerDefinedDescription(TemplateNodeModel node, String ns, String nodeType) throws TemplateModelException {
        String nsPrefix;
        if (ns != null) {
            nsPrefix = ns.length() > 0 ? " and namespace " : " and no namespace";
        } else {
            nsPrefix = "";
            ns = "";
        }
        return new Object[]{"No macro or directive is defined for node named ", new _DelayedJQuote(node.getNodeName()), nsPrefix, ns, ", and there is no fallback handler called @", nodeType, " either."};
    }

    void fallback() throws TemplateException, IOException {
        TemplateModel macroOrTransform = this.getNodeProcessor(this.currentNodeName, this.currentNodeNS, this.nodeNamespaceIndex);
        if (macroOrTransform instanceof Macro) {
            this.invokeMacro((Macro)macroOrTransform, null, null, null, null);
        } else if (macroOrTransform instanceof TemplateTransformModel) {
            this.visitAndTransform(null, (TemplateTransformModel)macroOrTransform, null);
        }
    }

    void invokeMacro(Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
        this.invokeMacroOrFunctionCommonPart(macro, namedArgs, positionalArgs, bodyParameterNames, callPlace);
    }

    TemplateModel invokeFunction(Environment env, Macro func, List<? extends Expression> argumentExps, TemplateObject callPlace) throws TemplateException {
        env.setLastReturnValue(null);
        if (!func.isFunction()) {
            throw new _MiscTemplateException(env, "A macro cannot be called in an expression. (Functions can be.)");
        }
        Writer prevOut = env.getOut();
        try {
            env.setOut(NullWriter.INSTANCE);
            env.invokeMacro(func, null, argumentExps, null, callPlace);
        }
        catch (IOException e) {
            throw new TemplateException("Unexpected exception during function execution", e, env);
        }
        finally {
            env.setOut(prevOut);
        }
        return env.getLastReturnValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void invokeMacroOrFunctionCommonPart(Macro macroOrFunction, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs, List<String> bodyParameterNames, TemplateObject callPlace) throws TemplateException, IOException {
        block15: {
            boolean elementPushed;
            if (macroOrFunction == Macro.DO_NOTHING_MACRO) {
                return;
            }
            if (!this.incompatibleImprovementsGE2328) {
                this.pushElement(macroOrFunction);
                elementPushed = true;
            } else {
                elementPushed = false;
            }
            try {
                Macro macro = macroOrFunction;
                macro.getClass();
                Macro.Context macroCtx = new Macro.Context(macro, this, callPlace, bodyParameterNames);
                this.setMacroContextLocalsFromArguments(macroCtx, macroOrFunction, namedArgs, positionalArgs);
                if (!elementPushed) {
                    this.pushElement(macroOrFunction);
                    elementPushed = true;
                }
                Macro.Context prevMacroCtx = this.currentMacroContext;
                this.currentMacroContext = macroCtx;
                LocalContextStack prevLocalContextStack = this.localContextStack;
                this.localContextStack = null;
                Namespace prevNamespace = this.currentNamespace;
                this.currentNamespace = this.getMacroNamespace(macroOrFunction);
                try {
                    macroCtx.checkParamsSetAndApplyDefaults(this);
                    this.visit(macroOrFunction.getChildBuffer());
                }
                catch (ReturnInstruction.Return return_) {
                    this.currentMacroContext = prevMacroCtx;
                    this.localContextStack = prevLocalContextStack;
                    this.currentNamespace = prevNamespace;
                }
                catch (TemplateException te) {
                    this.handleTemplateException(te);
                    break block15;
                    {
                        catch (Throwable throwable) {
                            throw throwable;
                        }
                    }
                }
                finally {
                    this.currentMacroContext = prevMacroCtx;
                    this.localContextStack = prevLocalContextStack;
                    this.currentNamespace = prevNamespace;
                }
            }
            finally {
                if (elementPushed) {
                    this.popElement();
                }
            }
        }
    }

    private void setMacroContextLocalsFromArguments(Macro.Context macroCtx, Macro macro, Map<String, ? extends Expression> namedArgs, List<? extends Expression> positionalArgs) throws TemplateException {
        block43: {
            WithArgsState withArgsState;
            int nextPositionalArgToAssignIdx;
            SimpleSequence positionalCatchAllParamValue;
            block44: {
                TemplateModel argValue;
                String catchAllParamName = macro.getCatchAll();
                SimpleHash namedCatchAllParamValue = null;
                positionalCatchAllParamValue = null;
                nextPositionalArgToAssignIdx = 0;
                withArgsState = Environment.getWithArgState(macro);
                if (withArgsState != null) {
                    TemplateHashModelEx byNameWithArgs = withArgsState.byName;
                    TemplateSequenceModel templateSequenceModel = withArgsState.byPosition;
                    if (byNameWithArgs != null) {
                        TemplateHashModelEx2.KeyValuePairIterator withArgsKVPIter = TemplateModelUtils.getKeyValuePairIterator(byNameWithArgs);
                        while (withArgsKVPIter.hasNext()) {
                            TemplateHashModelEx2.KeyValuePair withArgKVP = withArgsKVPIter.next();
                            TemplateModel argNameTM = withArgKVP.getKey();
                            if (!(argNameTM instanceof TemplateScalarModel)) {
                                throw new _TemplateModelException("Expected string keys in the \"with args\" hash, but one of the keys was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(argNameTM)), ".");
                            }
                            String argName = EvalUtil.modelToString((TemplateScalarModel)argNameTM, null, null);
                            argValue = withArgKVP.getValue();
                            boolean isArgNameDeclared = macro.hasArgNamed(argName);
                            if (isArgNameDeclared) {
                                macroCtx.setLocalVar(argName, argValue);
                                continue;
                            }
                            if (catchAllParamName != null) {
                                if (namedCatchAllParamValue == null) {
                                    namedCatchAllParamValue = Environment.initNamedCatchAllParameter(macroCtx, catchAllParamName);
                                }
                                if (!withArgsState.orderLast) {
                                    namedCatchAllParamValue.put(argName, argValue);
                                    continue;
                                }
                                ArrayList<NameValuePair> orderLastByNameCatchAll = withArgsState.orderLastByNameCatchAll;
                                if (orderLastByNameCatchAll == null) {
                                    orderLastByNameCatchAll = new ArrayList<NameValuePair>();
                                    withArgsState.orderLastByNameCatchAll = orderLastByNameCatchAll;
                                }
                                orderLastByNameCatchAll.add(new NameValuePair(argName, argValue));
                                continue;
                            }
                            throw this.newUndeclaredParamNameException(macro, argName);
                        }
                    } else if (templateSequenceModel != null) {
                        if (!withArgsState.orderLast) {
                            int argsCnt;
                            String[] argNames = macro.getArgumentNamesNoCopy();
                            if (argNames.length < (argsCnt = templateSequenceModel.size()) && catchAllParamName == null) {
                                throw this.newTooManyArgumentsException(macro, argNames, argsCnt);
                            }
                            for (int argIdx = 0; argIdx < argsCnt; ++argIdx) {
                                argValue = templateSequenceModel.get(argIdx);
                                try {
                                    if (nextPositionalArgToAssignIdx < argNames.length) {
                                        String argName = argNames[nextPositionalArgToAssignIdx++];
                                        macroCtx.setLocalVar(argName, argValue);
                                        continue;
                                    }
                                    if (positionalCatchAllParamValue == null) {
                                        positionalCatchAllParamValue = Environment.initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                                    }
                                    positionalCatchAllParamValue.add(argValue);
                                    continue;
                                }
                                catch (RuntimeException re) {
                                    throw new _MiscTemplateException((Throwable)re, this);
                                }
                            }
                        } else {
                            int totalPositionalArgCnt;
                            if (namedArgs != null && !namedArgs.isEmpty() && templateSequenceModel.size() != 0) {
                                throw new _MiscTemplateException("Call can't pass parameters by name, as there's \"with args last\" in effect that specifies parameters by position.");
                            }
                            if (catchAllParamName == null && (totalPositionalArgCnt = (positionalArgs != null ? positionalArgs.size() : 0) + templateSequenceModel.size()) > macro.getArgumentNamesNoCopy().length) {
                                throw this.newTooManyArgumentsException(macro, macro.getArgumentNamesNoCopy(), totalPositionalArgCnt);
                            }
                        }
                    }
                }
                if (namedArgs != null) {
                    if (catchAllParamName != null && namedCatchAllParamValue == null && positionalCatchAllParamValue == null) {
                        if (namedArgs.isEmpty() && withArgsState != null && withArgsState.byPosition != null) {
                            positionalCatchAllParamValue = Environment.initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                        } else {
                            namedCatchAllParamValue = Environment.initNamedCatchAllParameter(macroCtx, catchAllParamName);
                        }
                    }
                    for (Map.Entry entry : namedArgs.entrySet()) {
                        String argName = (String)entry.getKey();
                        boolean isArgNameDeclared = macro.hasArgNamed(argName);
                        if (isArgNameDeclared || namedCatchAllParamValue != null) {
                            Expression argValueExp = (Expression)entry.getValue();
                            argValue = argValueExp.eval(this);
                            if (isArgNameDeclared) {
                                macroCtx.setLocalVar(argName, argValue);
                                continue;
                            }
                            namedCatchAllParamValue.put(argName, argValue);
                            continue;
                        }
                        if (positionalCatchAllParamValue != null) {
                            throw this.newBothNamedAndPositionalCatchAllParamsException(macro);
                        }
                        throw this.newUndeclaredParamNameException(macro, argName);
                    }
                } else if (positionalArgs != null) {
                    int n;
                    int argsWithWithArgsCnt;
                    Object argNames;
                    if (catchAllParamName != null && positionalCatchAllParamValue == null && namedCatchAllParamValue == null) {
                        if (positionalArgs.isEmpty() && withArgsState != null && withArgsState.byName != null) {
                            namedCatchAllParamValue = Environment.initNamedCatchAllParameter(macroCtx, catchAllParamName);
                        } else {
                            positionalCatchAllParamValue = Environment.initPositionalCatchAllParameter(macroCtx, catchAllParamName);
                        }
                    }
                    if (((String[])(argNames = macro.getArgumentNamesNoCopy())).length < (argsWithWithArgsCnt = (n = positionalArgs.size()) + nextPositionalArgToAssignIdx) && positionalCatchAllParamValue == null) {
                        if (namedCatchAllParamValue != null) {
                            throw this.newBothNamedAndPositionalCatchAllParamsException(macro);
                        }
                        throw this.newTooManyArgumentsException(macro, (String[])argNames, argsWithWithArgsCnt);
                    }
                    for (int srcPosArgIdx = 0; srcPosArgIdx < n; ++srcPosArgIdx) {
                        Expression argValueExp = positionalArgs.get(srcPosArgIdx);
                        try {
                            argValue = argValueExp.eval(this);
                        }
                        catch (RuntimeException e) {
                            throw new _MiscTemplateException((Throwable)e, this);
                        }
                        if (nextPositionalArgToAssignIdx < ((Object)argNames).length) {
                            Object argName = argNames[nextPositionalArgToAssignIdx++];
                            macroCtx.setLocalVar((String)argName, argValue);
                            continue;
                        }
                        positionalCatchAllParamValue.add(argValue);
                    }
                }
                if (withArgsState == null || !withArgsState.orderLast) break block43;
                if (withArgsState.orderLastByNameCatchAll == null) break block44;
                for (NameValuePair nameValuePair : withArgsState.orderLastByNameCatchAll) {
                    if (namedCatchAllParamValue.containsKey(nameValuePair.name)) continue;
                    namedCatchAllParamValue.put(nameValuePair.name, nameValuePair.value);
                }
                break block43;
            }
            if (withArgsState.byPosition == null) break block43;
            TemplateSequenceModel byPosition = withArgsState.byPosition;
            int n = byPosition.size();
            String[] argNames = macro.getArgumentNamesNoCopy();
            for (int withArgIdx = 0; withArgIdx < n; ++withArgIdx) {
                TemplateModel withArgValue = byPosition.get(withArgIdx);
                if (nextPositionalArgToAssignIdx < argNames.length) {
                    String argName = argNames[nextPositionalArgToAssignIdx++];
                    macroCtx.setLocalVar(argName, withArgValue);
                    continue;
                }
                positionalCatchAllParamValue.add(withArgValue);
            }
        }
    }

    private static WithArgsState getWithArgState(Macro macro) {
        Macro.WithArgs withArgs = macro.getWithArgs();
        return withArgs == null ? null : new WithArgsState(withArgs.getByName(), withArgs.getByPosition(), withArgs.isOrderLast());
    }

    private _MiscTemplateException newTooManyArgumentsException(Macro macro, String[] argNames, int argsCnt) {
        return new _MiscTemplateException(this, macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " only accepts ", new _DelayedToString(argNames.length), " parameters, but got ", new _DelayedToString(argsCnt), ".");
    }

    private static SimpleSequence initPositionalCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
        SimpleSequence positionalCatchAllParamValue = new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
        macroCtx.setLocalVar(catchAllParamName, positionalCatchAllParamValue);
        return positionalCatchAllParamValue;
    }

    private static SimpleHash initNamedCatchAllParameter(Macro.Context macroCtx, String catchAllParamName) {
        SimpleHash namedCatchAllParamValue = new SimpleHash(new LinkedHashMap<String, Object>(), _ObjectWrappers.SAFE_OBJECT_WRAPPER, 0);
        macroCtx.setLocalVar(catchAllParamName, namedCatchAllParamValue);
        return namedCatchAllParamValue;
    }

    private _MiscTemplateException newUndeclaredParamNameException(Macro macro, String argName) {
        return new _MiscTemplateException(this, macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " has no parameter with name ", new _DelayedJQuote(argName), ". Valid parameter names are: ", new _DelayedJoinWithComma(macro.getArgumentNamesNoCopy()));
    }

    private _MiscTemplateException newBothNamedAndPositionalCatchAllParamsException(Macro macro) {
        return new _MiscTemplateException(this, macro.isFunction() ? "Function " : "Macro ", new _DelayedJQuote(macro.getName()), " call can't have both named and positional arguments that has to go into catch-all parameter.");
    }

    void visitMacroDef(Macro macro) {
        this.macroToNamespaceLookup.put(macro.getNamespaceLookupKey(), this.currentNamespace);
        this.currentNamespace.put(macro.getName(), macro);
    }

    Namespace getMacroNamespace(Macro macro) {
        return this.macroToNamespaceLookup.get(macro.getNamespaceLookupKey());
    }

    void recurse(TemplateNodeModel node, TemplateSequenceModel namespaces) throws TemplateException, IOException {
        if (node == null && (node = this.getCurrentVisitorNode()) == null) {
            throw new _TemplateModelException("The target node of recursion is missing or null.");
        }
        TemplateSequenceModel children = node.getChildNodes();
        if (children == null) {
            return;
        }
        int size = children.size();
        for (int i = 0; i < size; ++i) {
            TemplateNodeModel child = (TemplateNodeModel)children.get(i);
            if (child == null) continue;
            this.invokeNodeHandlerFor(child, namespaces);
        }
    }

    Macro.Context getCurrentMacroContext() {
        return this.currentMacroContext;
    }

    private void handleTemplateException(TemplateException templateException) throws TemplateException {
        if (templateException instanceof TemplateModelException && ((TemplateModelException)templateException).getReplaceWithCause() && templateException.getCause() instanceof TemplateException) {
            templateException = (TemplateException)templateException.getCause();
        }
        if (this.lastThrowable == templateException) {
            throw templateException;
        }
        this.lastThrowable = templateException;
        if (this.getLogTemplateExceptions() && LOG.isErrorEnabled() && !this.isInAttemptBlock()) {
            LOG.error("Error executing FreeMarker template", templateException);
        }
        try {
            if (templateException instanceof StopException) {
                throw templateException;
            }
            this.getTemplateExceptionHandler().handleTemplateException(templateException, this, this.out);
        }
        catch (TemplateException e) {
            if (this.isInAttemptBlock()) {
                this.getAttemptExceptionReporter().report(templateException, this);
            }
            throw e;
        }
    }

    @Override
    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        super.setTemplateExceptionHandler(templateExceptionHandler);
        this.lastThrowable = null;
    }

    @Override
    public void setLocale(Locale locale) {
        Locale prevLocale = this.getLocale();
        super.setLocale(locale);
        if (!locale.equals(prevLocale)) {
            this.cachedTemplateNumberFormats = null;
            if (this.cachedTemplateNumberFormat != null && this.cachedTemplateNumberFormat.isLocaleBound()) {
                this.cachedTemplateNumberFormat = null;
            }
            if (this.cachedTempDateFormatArray != null) {
                for (int i = 0; i < 16; ++i) {
                    TemplateDateFormat f = this.cachedTempDateFormatArray[i];
                    if (f == null || !f.isLocaleBound()) continue;
                    this.cachedTempDateFormatArray[i] = null;
                }
            }
            this.cachedTempDateFormatsByFmtStrArray = null;
            this.cachedCollator = null;
        }
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        TimeZone prevTimeZone = this.getTimeZone();
        super.setTimeZone(timeZone);
        if (!timeZone.equals(prevTimeZone)) {
            int i;
            if (this.cachedTempDateFormatArray != null) {
                for (i = 0; i < 8; ++i) {
                    TemplateDateFormat f = this.cachedTempDateFormatArray[i];
                    if (f == null || !f.isTimeZoneBound()) continue;
                    this.cachedTempDateFormatArray[i] = null;
                }
            }
            if (this.cachedTempDateFormatsByFmtStrArray != null) {
                for (i = 0; i < 8; ++i) {
                    this.cachedTempDateFormatsByFmtStrArray[i] = null;
                }
            }
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
        }
    }

    @Override
    public void setSQLDateAndTimeTimeZone(TimeZone timeZone) {
        TimeZone prevTimeZone = this.getSQLDateAndTimeTimeZone();
        super.setSQLDateAndTimeTimeZone(timeZone);
        if (!Environment.nullSafeEquals(timeZone, prevTimeZone)) {
            int i;
            if (this.cachedTempDateFormatArray != null) {
                for (i = 8; i < 16; ++i) {
                    TemplateDateFormat format = this.cachedTempDateFormatArray[i];
                    if (format == null || !format.isTimeZoneBound()) continue;
                    this.cachedTempDateFormatArray[i] = null;
                }
            }
            if (this.cachedTempDateFormatsByFmtStrArray != null) {
                for (i = 8; i < 16; ++i) {
                    this.cachedTempDateFormatsByFmtStrArray[i] = null;
                }
            }
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = null;
        }
    }

    private static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    boolean isSQLDateAndTimeTimeZoneSameAsNormal() {
        if (this.cachedSQLDateAndTimeTimeZoneSameAsNormal == null) {
            this.cachedSQLDateAndTimeTimeZoneSameAsNormal = this.getSQLDateAndTimeTimeZone() == null || this.getSQLDateAndTimeTimeZone().equals(this.getTimeZone());
        }
        return this.cachedSQLDateAndTimeTimeZoneSameAsNormal;
    }

    @Override
    public void setURLEscapingCharset(String urlEscapingCharset) {
        this.cachedURLEscapingCharsetSet = false;
        super.setURLEscapingCharset(urlEscapingCharset);
    }

    @Override
    public void setOutputEncoding(String outputEncoding) {
        this.cachedURLEscapingCharsetSet = false;
        super.setOutputEncoding(outputEncoding);
    }

    String getEffectiveURLEscapingCharset() {
        if (!this.cachedURLEscapingCharsetSet) {
            this.cachedURLEscapingCharset = this.getURLEscapingCharset();
            if (this.cachedURLEscapingCharset == null) {
                this.cachedURLEscapingCharset = this.getOutputEncoding();
            }
            this.cachedURLEscapingCharsetSet = true;
        }
        return this.cachedURLEscapingCharset;
    }

    Collator getCollator() {
        if (this.cachedCollator == null) {
            this.cachedCollator = Collator.getInstance(this.getLocale());
        }
        return this.cachedCollator;
    }

    public boolean applyEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 1, rightValue, this);
    }

    public boolean applyEqualsOperatorLenient(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compareLenient(leftValue, 1, rightValue, this);
    }

    public boolean applyLessThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 3, rightValue, this);
    }

    public boolean applyLessThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 5, rightValue, this);
    }

    public boolean applyGreaterThanOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 4, rightValue, this);
    }

    public boolean applyWithGreaterThanOrEqualsOperator(TemplateModel leftValue, TemplateModel rightValue) throws TemplateException {
        return EvalUtil.compare(leftValue, 6, rightValue, this);
    }

    public void setOut(Writer out) {
        this.out = out;
    }

    public Writer getOut() {
        return this.out;
    }

    @Override
    public void setNumberFormat(String formatName) {
        super.setNumberFormat(formatName);
        this.cachedTemplateNumberFormat = null;
    }

    String formatNumberToPlainText(TemplateNumberModel number, Expression exp, boolean useTempModelExc) throws TemplateException {
        return this.formatNumberToPlainText(number, this.getTemplateNumberFormat(exp, useTempModelExc), exp, useTempModelExc);
    }

    String formatNumberToPlainText(TemplateNumberModel number, TemplateNumberFormat format, Expression exp, boolean useTempModelExc) throws TemplateException {
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(number));
        }
        catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatNumberException(format, exp, e, useTempModelExc);
        }
    }

    String formatNumberToPlainText(Number number, BackwardCompatibleTemplateNumberFormat format, Expression exp) throws TemplateModelException, _MiscTemplateException {
        try {
            return format.format(number);
        }
        catch (UnformattableValueException e) {
            throw new _MiscTemplateException(exp, (Throwable)e, this, "Failed to format number with ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage());
        }
    }

    public TemplateNumberFormat getTemplateNumberFormat() throws TemplateValueFormatException {
        TemplateNumberFormat format = this.cachedTemplateNumberFormat;
        if (format == null) {
            this.cachedTemplateNumberFormat = format = this.getTemplateNumberFormat(this.getNumberFormat(), false);
        }
        return format;
    }

    public TemplateNumberFormat getTemplateNumberFormat(String formatString) throws TemplateValueFormatException {
        return this.getTemplateNumberFormat(formatString, true);
    }

    public TemplateNumberFormat getTemplateNumberFormat(String formatString, Locale locale) throws TemplateValueFormatException {
        if (locale.equals(this.getLocale())) {
            this.getTemplateNumberFormat(formatString);
        }
        return this.getTemplateNumberFormatWithoutCache(formatString, locale);
    }

    TemplateNumberFormat getTemplateNumberFormat(Expression exp, boolean useTempModelExc) throws TemplateException {
        TemplateNumberFormat format;
        try {
            format = this.getTemplateNumberFormat();
        }
        catch (TemplateValueFormatException e) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to get number format object for the current number format string, ", new _DelayedJQuote(this.getNumberFormat()), ": ", e.getMessage()).blame(exp);
            throw useTempModelExc ? new _TemplateModelException((Throwable)e, this, desc) : new _MiscTemplateException((Throwable)e, this, desc);
        }
        return format;
    }

    TemplateNumberFormat getTemplateNumberFormat(String formatString, Expression exp, boolean useTempModelExc) throws TemplateException {
        TemplateNumberFormat format;
        try {
            format = this.getTemplateNumberFormat(formatString);
        }
        catch (TemplateValueFormatException e) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Failed to get number format object for the ", new _DelayedJQuote(formatString), " number format string: ", e.getMessage()).blame(exp);
            throw useTempModelExc ? new _TemplateModelException((Throwable)e, this, desc) : new _MiscTemplateException((Throwable)e, this, desc);
        }
        return format;
    }

    private TemplateNumberFormat getTemplateNumberFormat(String formatString, boolean cacheResult) throws TemplateValueFormatException {
        TemplateNumberFormat format;
        if (this.cachedTemplateNumberFormats == null) {
            if (cacheResult) {
                this.cachedTemplateNumberFormats = new HashMap<String, TemplateNumberFormat>();
            }
        } else {
            format = this.cachedTemplateNumberFormats.get(formatString);
            if (format != null) {
                return format;
            }
        }
        format = this.getTemplateNumberFormatWithoutCache(formatString, this.getLocale());
        if (cacheResult) {
            this.cachedTemplateNumberFormats.put(formatString, format);
        }
        return format;
    }

    private TemplateNumberFormat getTemplateNumberFormatWithoutCache(String formatString, Locale locale) throws TemplateValueFormatException {
        int formatStringLen = formatString.length();
        if (formatStringLen > 1 && formatString.charAt(0) == '@' && (this.isIcI2324OrLater() || this.hasCustomFormats()) && Character.isLetter(formatString.charAt(1))) {
            char c;
            int endIdx;
            for (endIdx = 1; endIdx < formatStringLen && (c = formatString.charAt(endIdx)) != ' ' && c != '_'; ++endIdx) {
            }
            String name = formatString.substring(1, endIdx);
            String params = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
            TemplateNumberFormatFactory formatFactory = this.getCustomNumberFormat(name);
            if (formatFactory == null) {
                throw new UndefinedCustomFormatException("No custom number format was defined with name " + StringUtil.jQuote(name));
            }
            return formatFactory.get(params, locale, this);
        }
        if (formatStringLen >= 1 && formatString.charAt(0) == 'c' && (formatStringLen == 1 || formatString.equals(COMPUTER_FORMAT_STRING))) {
            return this.getCTemplateNumberFormatWithPre2331IcIBug();
        }
        return JavaTemplateNumberFormatFactory.INSTANCE.get(formatString, locale, this);
    }

    @Deprecated
    public NumberFormat getCNumberFormat() {
        if (this.cNumberFormat == null) {
            CFormat cFormat = this.getCFormat();
            this.cNumberFormat = cFormat == LegacyCFormat.INSTANCE && this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_31 ? ((LegacyCFormat)cFormat).getLegacyNumberFormat(_VersionInts.V_2_3_20) : cFormat.getLegacyNumberFormat(this);
        }
        return this.cNumberFormat;
    }

    public TemplateNumberFormat getCTemplateNumberFormat() {
        if (this.cTemplateNumberFormat == null) {
            this.cTemplateNumberFormat = this.getCFormat().getTemplateNumberFormat(this);
        }
        return this.cTemplateNumberFormat;
    }

    private TemplateNumberFormat getCTemplateNumberFormatWithPre2331IcIBug() {
        if (this.cTemplateNumberFormatWithPre2331IcIBug == null) {
            CFormat cFormat = this.getCFormat();
            this.cTemplateNumberFormatWithPre2331IcIBug = cFormat == LegacyCFormat.INSTANCE && this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_31 ? ((LegacyCFormat)cFormat).getTemplateNumberFormat(_VersionInts.V_2_3_20) : cFormat.getTemplateNumberFormat(this);
        }
        return this.cTemplateNumberFormatWithPre2331IcIBug;
    }

    @Override
    public void setCFormat(CFormat cFormat) {
        CFormat prevCFormat = this.getCFormat();
        super.setCFormat(cFormat);
        if (prevCFormat != cFormat) {
            this.cTemplateNumberFormat = null;
            this.cTemplateNumberFormatWithPre2331IcIBug = null;
            this.cNumberFormat = null;
            if (this.cachedTemplateNumberFormats != null) {
                this.cachedTemplateNumberFormats.remove("c");
                this.cachedTemplateNumberFormats.remove(COMPUTER_FORMAT_STRING);
            }
            this.clearCachedTrueAndFalseString();
        }
    }

    @Override
    public void setTimeFormat(String timeFormat) {
        String prevTimeFormat = this.getTimeFormat();
        super.setTimeFormat(timeFormat);
        if (!timeFormat.equals(prevTimeFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 1] = null;
            }
        }
    }

    @Override
    public void setDateFormat(String dateFormat) {
        String prevDateFormat = this.getDateFormat();
        super.setDateFormat(dateFormat);
        if (!dateFormat.equals(prevDateFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 2] = null;
            }
        }
    }

    @Override
    public void setDateTimeFormat(String dateTimeFormat) {
        String prevDateTimeFormat = this.getDateTimeFormat();
        super.setDateTimeFormat(dateTimeFormat);
        if (!dateTimeFormat.equals(prevDateTimeFormat) && this.cachedTempDateFormatArray != null) {
            for (int i = 0; i < 16; i += 4) {
                this.cachedTempDateFormatArray[i + 3] = null;
            }
        }
    }

    @Override
    public void setBooleanFormat(String booleanFormat) {
        super.setBooleanFormat(booleanFormat);
        this.clearCachedTrueAndFalseString();
    }

    String formatBoolean(boolean value, boolean fallbackToTrueFalse) throws TemplateException {
        if (value) {
            String s = this.getTrueStringValue();
            if (s == null) {
                if (fallbackToTrueFalse) {
                    return "true";
                }
                throw new _MiscTemplateException(this.getNullBooleanFormatErrorDescription());
            }
            return s;
        }
        String s = this.getFalseStringValue();
        if (s == null) {
            if (fallbackToTrueFalse) {
                return "false";
            }
            throw new _MiscTemplateException(this.getNullBooleanFormatErrorDescription());
        }
        return s;
    }

    private _ErrorDescriptionBuilder getNullBooleanFormatErrorDescription() {
        return new _ErrorDescriptionBuilder("Can't convert boolean to string automatically, because the \"", "boolean_format", "\" setting was ", new _DelayedJQuote(this.getBooleanFormat()), this.getBooleanFormat().equals("true,false") ? ", which is the legacy deprecated default, and we treat it as if no format was set. This is the default configuration; you should provide the format explicitly for each place where you print a boolean." : ".").tips("Write something like myBool?string('yes', 'no') to specify boolean formatting in place.", new Object[]{"If you want \"true\"/\"false\" result as you are generating computer-language output (not for direct human consumption), then use \"?c\", like ${myBool?c}. (If you always generate computer-language output, then it's might be reasonable to set the \"", "boolean_format", "\" setting to \"c\" instead.)"}, new Object[]{"If you need the same two values on most places, the programmers can set the \"", "boolean_format", "\" setting to something like \"yes,no\". However, then it will be easy to unwillingly format booleans like that."});
    }

    String getTrueStringValue() {
        if (this.trueAndFalseStringsCachedForParent == this.getParent()) {
            return this.cachedTrueString;
        }
        this.cacheTrueAndFalseStrings();
        return this.cachedTrueString;
    }

    String getFalseStringValue() {
        if (this.trueAndFalseStringsCachedForParent == this.getParent()) {
            return this.cachedFalseString;
        }
        this.cacheTrueAndFalseStrings();
        return this.cachedFalseString;
    }

    private void clearCachedTrueAndFalseString() {
        this.trueAndFalseStringsCachedForParent = null;
        this.cachedTrueString = null;
        this.cachedFalseString = null;
    }

    private void cacheTrueAndFalseStrings() {
        String[] parsedBooleanFormat = Environment.parseBooleanFormat(this.getBooleanFormat());
        if (parsedBooleanFormat != null) {
            if (parsedBooleanFormat.length == 0) {
                CFormat cFormat = this.getCFormat();
                this.cachedTrueString = cFormat.getTrueString();
                this.cachedFalseString = cFormat.getFalseString();
            } else {
                this.cachedTrueString = parsedBooleanFormat[0];
                this.cachedFalseString = parsedBooleanFormat[1];
            }
        } else {
            this.cachedTrueString = null;
            this.cachedFalseString = null;
        }
        this.trueAndFalseStringsCachedForParent = this.getParent();
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    TemplateModel getLastReturnValue() {
        return this.lastReturnValue;
    }

    void setLastReturnValue(TemplateModel lastReturnValue) {
        this.lastReturnValue = lastReturnValue;
    }

    void clearLastReturnValue() {
        this.lastReturnValue = null;
    }

    String formatDateToPlainText(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateException {
        TemplateDateFormat format = this.getTemplateDateFormat(tdm, tdmSourceExpr, useTempModelExc);
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
        }
        catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatDateException(format, tdmSourceExpr, e, useTempModelExc);
        }
    }

    String formatDateToPlainText(TemplateDateModel tdm, String formatString, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
        Date date = EvalUtil.modelToDate(tdm, blamedDateSourceExp);
        TemplateDateFormat format = this.getTemplateDateFormat(formatString, tdm.getDateType(), date.getClass(), blamedDateSourceExp, blamedFormatterExp, useTempModelExc);
        try {
            return EvalUtil.assertFormatResultNotNull(format.formatToPlainText(tdm));
        }
        catch (TemplateValueFormatException e) {
            throw _MessageUtil.newCantFormatDateException(format, blamedDateSourceExp, e, useTempModelExc);
        }
    }

    public TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = Environment.isSQLDateOrTimeClass(dateClass);
        return this.getTemplateDateFormat(dateType, this.shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = Environment.isSQLDateOrTimeClass(dateClass);
        return this.getTemplateDateFormat(formatString, dateType, this.shouldUseSQLDTTimeZone(isSQLDateOrTime), isSQLDateOrTime, true);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = Environment.isSQLDateOrTimeClass(dateClass);
        boolean useSQLDTTZ = this.shouldUseSQLDTTimeZone(isSQLDateOrTime);
        return this.getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? this.getSQLDateAndTimeTimeZone() : this.getTimeZone(), isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Locale locale, TimeZone timeZone, TimeZone sqlDateAndTimeTimeZone) throws TemplateValueFormatException {
        boolean isSQLDateOrTime = Environment.isSQLDateOrTimeClass(dateClass);
        boolean useSQLDTTZ = this.shouldUseSQLDTTimeZone(isSQLDateOrTime);
        return this.getTemplateDateFormat(formatString, dateType, locale, useSQLDTTZ ? sqlDateAndTimeTimeZone : timeZone, isSQLDateOrTime);
    }

    public TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
        TimeZone currentSQLDTTimeZone;
        TimeZone currentTimeZone;
        int equalCurrentTZ;
        Locale currentLocale = this.getLocale();
        if (locale.equals(currentLocale) && (equalCurrentTZ = timeZone.equals(currentTimeZone = this.getTimeZone()) ? 1 : (timeZone.equals(currentSQLDTTimeZone = this.getSQLDateAndTimeTimeZone()) ? 2 : 0)) != 0) {
            return this.getTemplateDateFormat(formatString, dateType, equalCurrentTZ == 2, zonelessInput, true);
        }
        return this.getTemplateDateFormatWithoutCache(formatString, dateType, locale, timeZone, zonelessInput);
    }

    TemplateDateFormat getTemplateDateFormat(TemplateDateModel tdm, Expression tdmSourceExpr, boolean useTempModelExc) throws TemplateModelException, TemplateException {
        Date date = EvalUtil.modelToDate(tdm, tdmSourceExpr);
        TemplateDateFormat format = this.getTemplateDateFormat(tdm.getDateType(), date.getClass(), tdmSourceExpr, useTempModelExc);
        return format;
    }

    TemplateDateFormat getTemplateDateFormat(int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, boolean useTempModelExc) throws TemplateException {
        try {
            return this.getTemplateDateFormat(dateType, dateClass);
        }
        catch (UnknownDateTypeFormattingUnsupportedException e) {
            throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
        }
        catch (TemplateValueFormatException e) {
            String settingValue;
            String settingName;
            switch (dateType) {
                case 1: {
                    settingName = "time_format";
                    settingValue = this.getTimeFormat();
                    break;
                }
                case 2: {
                    settingName = "date_format";
                    settingValue = this.getDateFormat();
                    break;
                }
                case 3: {
                    settingName = "datetime_format";
                    settingValue = this.getDateTimeFormat();
                    break;
                }
                default: {
                    settingName = "???";
                    settingValue = "???";
                }
            }
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("The value of the \"", settingName, "\" FreeMarker configuration setting is a malformed date/time/datetime format string: ", new _DelayedJQuote(settingValue), ". Reason given: ", e.getMessage());
            throw useTempModelExc ? new _TemplateModelException((Throwable)e, desc) : new _MiscTemplateException((Throwable)e, desc);
        }
    }

    TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, Class<? extends Date> dateClass, Expression blamedDateSourceExp, Expression blamedFormatterExp, boolean useTempModelExc) throws TemplateException {
        try {
            return this.getTemplateDateFormat(formatString, dateType, dateClass);
        }
        catch (UnknownDateTypeFormattingUnsupportedException e) {
            throw _MessageUtil.newCantFormatUnknownTypeDateException(blamedDateSourceExp, e);
        }
        catch (TemplateValueFormatException e) {
            _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder("Can't create date/time/datetime format based on format string ", new _DelayedJQuote(formatString), ". Reason given: ", e.getMessage()).blame(blamedFormatterExp);
            throw useTempModelExc ? new _TemplateModelException((Throwable)e, desc) : new _MiscTemplateException((Throwable)e, desc);
        }
    }

    private TemplateDateFormat getTemplateDateFormat(int dateType, boolean useSQLDTTZ, boolean zonelessInput) throws TemplateValueFormatException {
        TemplateDateFormat format;
        if (dateType == 0) {
            throw new UnknownDateTypeFormattingUnsupportedException();
        }
        int cacheIdx = this.getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTZ);
        TemplateDateFormat[] cachedTemplateDateFormats = this.cachedTempDateFormatArray;
        if (cachedTemplateDateFormats == null) {
            this.cachedTempDateFormatArray = cachedTemplateDateFormats = new TemplateDateFormat[16];
        }
        if ((format = cachedTemplateDateFormats[cacheIdx]) == null) {
            String formatString;
            switch (dateType) {
                case 1: {
                    formatString = this.getTimeFormat();
                    break;
                }
                case 2: {
                    formatString = this.getDateFormat();
                    break;
                }
                case 3: {
                    formatString = this.getDateTimeFormat();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid date type enum: " + Integer.valueOf(dateType));
                }
            }
            cachedTemplateDateFormats[cacheIdx] = format = this.getTemplateDateFormat(formatString, dateType, useSQLDTTZ, zonelessInput, false);
        }
        return format;
    }

    private TemplateDateFormat getTemplateDateFormat(String formatString, int dateType, boolean useSQLDTTimeZone, boolean zonelessInput, boolean cacheResult) throws TemplateValueFormatException {
        HashMap<String, TemplateDateFormat> cachedFormatsByFormatString;
        block4: {
            TemplateDateFormat format;
            block6: {
                block5: {
                    int cacheArrIdx;
                    HashMap<String, TemplateDateFormat>[] cachedTempDateFormatsByFmtStrArray;
                    block2: {
                        block3: {
                            cachedTempDateFormatsByFmtStrArray = this.cachedTempDateFormatsByFmtStrArray;
                            if (cachedTempDateFormatsByFmtStrArray != null) break block2;
                            if (cacheResult) break block3;
                            cachedFormatsByFormatString = null;
                            break block4;
                        }
                        this.cachedTempDateFormatsByFmtStrArray = cachedTempDateFormatsByFmtStrArray = new HashMap[16];
                    }
                    if ((cachedFormatsByFormatString = cachedTempDateFormatsByFmtStrArray[cacheArrIdx = this.getTemplateDateFormatCacheArrayIndex(dateType, zonelessInput, useSQLDTTimeZone)]) != null) break block5;
                    if (!cacheResult) break block4;
                    cachedFormatsByFormatString = new HashMap(4);
                    cachedTempDateFormatsByFmtStrArray[cacheArrIdx] = cachedFormatsByFormatString;
                    format = null;
                    break block6;
                }
                format = cachedFormatsByFormatString.get(formatString);
            }
            if (format != null) {
                return format;
            }
        }
        TemplateDateFormat format = this.getTemplateDateFormatWithoutCache(formatString, dateType, this.getLocale(), useSQLDTTimeZone ? this.getSQLDateAndTimeTimeZone() : this.getTimeZone(), zonelessInput);
        if (cacheResult) {
            cachedFormatsByFormatString.put(formatString, format);
        }
        return format;
    }

    private TemplateDateFormat getTemplateDateFormatWithoutCache(String formatString, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput) throws TemplateValueFormatException {
        String formatParams;
        TemplateDateFormatFactory formatFactory;
        char firstChar;
        int formatStringLen = formatString.length();
        char c = firstChar = formatStringLen != 0 ? formatString.charAt(0) : (char)'\u0000';
        if (firstChar == 'x' && formatStringLen > 1 && formatString.charAt(1) == 's') {
            formatFactory = XSTemplateDateFormatFactory.INSTANCE;
            formatParams = formatString;
        } else if (firstChar == 'i' && formatStringLen > 2 && formatString.charAt(1) == 's' && formatString.charAt(2) == 'o') {
            formatFactory = ISOTemplateDateFormatFactory.INSTANCE;
            formatParams = formatString;
        } else if (firstChar == '@' && formatStringLen > 1 && (this.isIcI2324OrLater() || this.hasCustomFormats()) && Character.isLetter(formatString.charAt(1))) {
            char c2;
            int endIdx;
            for (endIdx = 1; endIdx < formatStringLen && (c2 = formatString.charAt(endIdx)) != ' ' && c2 != '_'; ++endIdx) {
            }
            String name = formatString.substring(1, endIdx);
            formatParams = endIdx < formatStringLen ? formatString.substring(endIdx + 1) : "";
            formatFactory = this.getCustomDateFormat(name);
            if (formatFactory == null) {
                throw new UndefinedCustomFormatException("No custom date format was defined with name " + StringUtil.jQuote(name));
            }
        } else {
            formatParams = formatString;
            formatFactory = JavaTemplateDateFormatFactory.INSTANCE;
        }
        return ((TemplateDateFormatFactory)formatFactory).get(formatParams, dateType, locale, timeZone, zonelessInput, this);
    }

    boolean shouldUseSQLDTTZ(Class dateClass) {
        return dateClass != Date.class && !this.isSQLDateAndTimeTimeZoneSameAsNormal() && Environment.isSQLDateOrTimeClass(dateClass);
    }

    private boolean shouldUseSQLDTTimeZone(boolean sqlDateOrTime) {
        return sqlDateOrTime && !this.isSQLDateAndTimeTimeZoneSameAsNormal();
    }

    private static boolean isSQLDateOrTimeClass(Class dateClass) {
        return dateClass != Date.class && (dateClass == java.sql.Date.class || dateClass == Time.class || dateClass != Timestamp.class && (java.sql.Date.class.isAssignableFrom(dateClass) || Time.class.isAssignableFrom(dateClass)));
    }

    private int getTemplateDateFormatCacheArrayIndex(int dateType, boolean zonelessInput, boolean sqlDTTZ) {
        return dateType + (zonelessInput ? 4 : 0) + (sqlDTTZ ? 8 : 0);
    }

    DateUtil.DateToISO8601CalendarFactory getISOBuiltInCalendarFactory() {
        if (this.isoBuiltInCalendarFactory == null) {
            this.isoBuiltInCalendarFactory = new DateUtil.TrivialDateToISO8601CalendarFactory();
        }
        return this.isoBuiltInCalendarFactory;
    }

    TemplateTransformModel getTransform(Expression exp) throws TemplateException {
        TemplateTransformModel ttm = null;
        TemplateModel tm = exp.eval(this);
        if (tm instanceof TemplateTransformModel) {
            ttm = (TemplateTransformModel)tm;
        } else if (exp instanceof Identifier && (tm = this.configuration.getSharedVariable(exp.toString())) instanceof TemplateTransformModel) {
            ttm = (TemplateTransformModel)tm;
        }
        return ttm;
    }

    public TemplateModel getLocalVariable(String name) throws TemplateModelException {
        TemplateModel val = this.getNullableLocalVariable(name);
        return val != TemplateNullModel.INSTANCE ? val : null;
    }

    private final TemplateModel getNullableLocalVariable(String name) throws TemplateModelException {
        if (this.localContextStack != null) {
            for (int i = this.localContextStack.size() - 1; i >= 0; --i) {
                LocalContext lc = this.localContextStack.get(i);
                TemplateModel tm = lc.getLocalVariable(name);
                if (tm == null) continue;
                return tm;
            }
        }
        return this.currentMacroContext == null ? null : this.currentMacroContext.getLocalVariable(name);
    }

    public TemplateModel getVariable(String name) throws TemplateModelException {
        TemplateModel result = this.getNullableLocalVariable(name);
        if (result != null) {
            return result != TemplateNullModel.INSTANCE ? result : null;
        }
        result = this.currentNamespace.get(name);
        if (result != null) {
            return result;
        }
        return this.getGlobalVariable(name);
    }

    public TemplateModel getGlobalVariable(String name) throws TemplateModelException {
        TemplateModel result = this.globalNamespace.get(name);
        if (result != null) {
            return result;
        }
        return this.getDataModelOrSharedVariable(name);
    }

    public TemplateModel getDataModelOrSharedVariable(String name) throws TemplateModelException {
        TemplateModel dataModelVal = this.rootDataModel.get(name);
        if (dataModelVal != null) {
            return dataModelVal;
        }
        return this.configuration.getSharedVariable(name);
    }

    public void setGlobalVariable(String name, TemplateModel value) {
        this.globalNamespace.put(name, value);
    }

    public void setVariable(String name, TemplateModel value) {
        this.currentNamespace.put(name, value);
    }

    public void setLocalVariable(String name, TemplateModel value) {
        if (this.currentMacroContext == null) {
            throw new IllegalStateException("Not executing macro body");
        }
        this.currentMacroContext.setLocalVar(name, value);
    }

    public Set getKnownVariableNames() throws TemplateModelException {
        Set set = this.configuration.getSharedVariableNames();
        if (this.rootDataModel instanceof TemplateHashModelEx) {
            TemplateModelIterator rootNames = ((TemplateHashModelEx)this.rootDataModel).keys().iterator();
            while (rootNames.hasNext()) {
                set.add(((TemplateScalarModel)rootNames.next()).getAsString());
            }
        }
        TemplateModelIterator tmi = this.globalNamespace.keys().iterator();
        while (tmi.hasNext()) {
            set.add(((TemplateScalarModel)tmi.next()).getAsString());
        }
        tmi = this.currentNamespace.keys().iterator();
        while (tmi.hasNext()) {
            set.add(((TemplateScalarModel)tmi.next()).getAsString());
        }
        if (this.currentMacroContext != null) {
            set.addAll(this.currentMacroContext.getLocalVariableNames());
        }
        if (this.localContextStack != null) {
            for (int i = this.localContextStack.size() - 1; i >= 0; --i) {
                LocalContext lc = this.localContextStack.get(i);
                set.addAll(lc.getLocalVariableNames());
            }
        }
        return set;
    }

    public void outputInstructionStack(PrintWriter pw) {
        Environment.outputInstructionStack(this.getInstructionStackSnapshot(), false, pw);
        pw.flush();
    }

    static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer w) {
        PrintWriter pw = (PrintWriter)(w instanceof PrintWriter ? w : null);
        try {
            if (instructionStackSnapshot != null) {
                int totalFrames = instructionStackSnapshot.length;
                int framesToPrint = terseMode ? (totalFrames <= 10 ? totalFrames : 9) : totalFrames;
                boolean hideNestringRelatedFrames = terseMode && framesToPrint < totalFrames;
                int nestingRelatedFramesHidden = 0;
                int trailingFramesHidden = 0;
                int framesPrinted = 0;
                for (int frameIdx = 0; frameIdx < totalFrames; ++frameIdx) {
                    boolean nestingRelatedElement;
                    TemplateElement stackEl = instructionStackSnapshot[frameIdx];
                    boolean bl = nestingRelatedElement = frameIdx > 0 && stackEl instanceof BodyInstruction || frameIdx > 1 && instructionStackSnapshot[frameIdx - 1] instanceof BodyInstruction;
                    if (framesPrinted < framesToPrint) {
                        if (!nestingRelatedElement || !hideNestringRelatedFrames) {
                            w.write(frameIdx == 0 ? "\t- Failed at: " : (nestingRelatedElement ? "\t~ Reached through: " : "\t- Reached through: "));
                            w.write(Environment.instructionStackItemToString(stackEl));
                            if (pw != null) {
                                pw.println();
                            } else {
                                w.write(10);
                            }
                            ++framesPrinted;
                            continue;
                        }
                        ++nestingRelatedFramesHidden;
                        continue;
                    }
                    ++trailingFramesHidden;
                }
                boolean hadClosingNotes = false;
                if (trailingFramesHidden > 0) {
                    w.write("\t... (Had ");
                    w.write(String.valueOf(trailingFramesHidden + nestingRelatedFramesHidden));
                    w.write(" more, hidden for tersenes)");
                    hadClosingNotes = true;
                }
                if (nestingRelatedFramesHidden > 0) {
                    if (hadClosingNotes) {
                        w.write(32);
                    } else {
                        w.write(9);
                    }
                    w.write("(Hidden " + nestingRelatedFramesHidden + " \"~\" lines for terseness)");
                    if (pw != null) {
                        pw.println();
                    } else {
                        w.write(10);
                    }
                    hadClosingNotes = true;
                }
                if (hadClosingNotes) {
                    if (pw != null) {
                        pw.println();
                    } else {
                        w.write(10);
                    }
                }
            } else {
                w.write("(The stack was empty)");
                if (pw != null) {
                    pw.println();
                } else {
                    w.write(10);
                }
            }
        }
        catch (IOException e) {
            LOG.error("Failed to print FTL stack trace", e);
        }
    }

    TemplateElement[] getInstructionStackSnapshot() {
        int requiredLength = 0;
        int ln = this.instructionStackSize;
        for (int i = 0; i < ln; ++i) {
            TemplateElement stackEl = this.instructionStack[i];
            if (i != ln - 1 && !stackEl.isShownInStackTrace()) continue;
            ++requiredLength;
        }
        if (requiredLength == 0) {
            return null;
        }
        TemplateElement[] result = new TemplateElement[requiredLength];
        int dstIdx = requiredLength - 1;
        for (int i = 0; i < ln; ++i) {
            TemplateElement stackEl = this.instructionStack[i];
            if (i != ln - 1 && !stackEl.isShownInStackTrace()) continue;
            result[dstIdx--] = stackEl;
        }
        return result;
    }

    static String instructionStackItemToString(TemplateElement stackEl) {
        StringBuilder sb = new StringBuilder();
        Environment.appendInstructionStackItem(stackEl, sb);
        return sb.toString();
    }

    static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
        sb.append(_MessageUtil.shorten(stackEl.getDescription(), 40));
        sb.append("  [");
        Macro enclosingMacro = Environment.getEnclosingMacro(stackEl);
        if (enclosingMacro != null) {
            sb.append(_MessageUtil.formatLocationForEvaluationError(enclosingMacro, stackEl.beginLine, stackEl.beginColumn));
        } else {
            sb.append(_MessageUtil.formatLocationForEvaluationError(stackEl.getTemplate(), stackEl.beginLine, stackEl.beginColumn));
        }
        sb.append("]");
    }

    private static Macro getEnclosingMacro(TemplateElement stackEl) {
        while (stackEl != null) {
            if (stackEl instanceof Macro) {
                return (Macro)stackEl;
            }
            stackEl = stackEl.getParentElement();
        }
        return null;
    }

    private void pushLocalContext(LocalContext localContext) {
        if (this.localContextStack == null) {
            this.localContextStack = new LocalContextStack();
        }
        this.localContextStack.push(localContext);
    }

    LocalContextStack getLocalContextStack() {
        return this.localContextStack;
    }

    public Namespace getNamespace(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (this.loadedLibs != null) {
            return this.loadedLibs.get(name);
        }
        return null;
    }

    public Namespace getMainNamespace() {
        return this.mainNamespace;
    }

    public Namespace getCurrentNamespace() {
        return this.currentNamespace;
    }

    public Namespace getGlobalNamespace() {
        return this.globalNamespace;
    }

    public TemplateHashModel getDataModel() {
        return this.rootDataModel instanceof TemplateHashModelEx ? new TemplateHashModelEx(){

            @Override
            public boolean isEmpty() throws TemplateModelException {
                return false;
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                return Environment.this.getDataModelOrSharedVariable(key);
            }

            @Override
            public TemplateCollectionModel values() throws TemplateModelException {
                return ((TemplateHashModelEx)Environment.this.rootDataModel).values();
            }

            @Override
            public TemplateCollectionModel keys() throws TemplateModelException {
                return ((TemplateHashModelEx)Environment.this.rootDataModel).keys();
            }

            @Override
            public int size() throws TemplateModelException {
                return ((TemplateHashModelEx)Environment.this.rootDataModel).size();
            }
        } : new TemplateHashModel(){

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                TemplateModel value = Environment.this.rootDataModel.get(key);
                return value != null ? value : Environment.this.configuration.getSharedVariable(key);
            }
        };
    }

    public TemplateHashModel getGlobalVariables() {
        return new TemplateHashModel(){

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public TemplateModel get(String key) throws TemplateModelException {
                TemplateModel result = Environment.this.globalNamespace.get(key);
                if (result == null) {
                    result = Environment.this.rootDataModel.get(key);
                }
                if (result == null) {
                    result = Environment.this.configuration.getSharedVariable(key);
                }
                return result;
            }
        };
    }

    private void pushElement(TemplateElement element) {
        int newSize;
        TemplateElement[] instructionStack = this.instructionStack;
        if ((newSize = ++this.instructionStackSize) > instructionStack.length) {
            TemplateElement[] newInstructionStack = new TemplateElement[newSize * 2];
            for (int i = 0; i < instructionStack.length; ++i) {
                newInstructionStack[i] = instructionStack[i];
            }
            this.instructionStack = instructionStack = newInstructionStack;
        }
        instructionStack[newSize - 1] = element;
    }

    private void popElement() {
        --this.instructionStackSize;
    }

    void replaceElementStackTop(TemplateElement instr) {
        this.instructionStack[this.instructionStackSize - 1] = instr;
    }

    public TemplateNodeModel getCurrentVisitorNode() {
        return this.currentVisitorNode;
    }

    public void setCurrentVisitorNode(TemplateNodeModel node) {
        this.currentVisitorNode = node;
    }

    TemplateModel getNodeProcessor(TemplateNodeModel node) throws TemplateException {
        String nodeName = node.getNodeName();
        if (nodeName == null) {
            throw new _MiscTemplateException(this, "Node name is null.");
        }
        TemplateModel result = this.getNodeProcessor(nodeName, node.getNodeNamespace(), 0);
        if (result == null) {
            String type = node.getNodeType();
            if (type == null) {
                type = "default";
            }
            result = this.getNodeProcessor("@" + type, null, 0);
        }
        return result;
    }

    private TemplateModel getNodeProcessor(String nodeName, String nsURI, int startIndex) throws TemplateException {
        int i;
        TemplateModel result = null;
        int size = this.nodeNamespaces.size();
        for (i = startIndex; i < size; ++i) {
            Namespace ns = null;
            try {
                ns = (Namespace)this.nodeNamespaces.get(i);
            }
            catch (ClassCastException cce) {
                throw new _MiscTemplateException(this, "A \"using\" clause should contain a sequence of namespaces or strings that indicate the location of importable macro libraries.");
            }
            result = this.getNodeProcessor(ns, nodeName, nsURI);
            if (result != null) break;
        }
        if (result != null) {
            this.nodeNamespaceIndex = i + 1;
            this.currentNodeName = nodeName;
            this.currentNodeNS = nsURI;
        }
        return result;
    }

    private TemplateModel getNodeProcessor(Namespace ns, String localName, String nsURI) throws TemplateException {
        TemplateModel result = null;
        if (nsURI == null) {
            result = ns.get(localName);
            if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                result = null;
            }
        } else {
            Template template = ns.getTemplate();
            String prefix = template.getPrefixForNamespace(nsURI);
            if (prefix == null) {
                return null;
            }
            if (prefix.length() > 0) {
                result = ns.get(prefix + ":" + localName);
                if (!(result instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                    result = null;
                }
            } else {
                if (nsURI.length() == 0 && !((result = ns.get("N:" + localName)) instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                    result = null;
                }
                if (nsURI.equals(template.getDefaultNS()) && !((result = ns.get("D:" + localName)) instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                    result = null;
                }
                if (result == null && !((result = ns.get(localName)) instanceof Macro) && !(result instanceof TemplateTransformModel)) {
                    result = null;
                }
            }
        }
        return result;
    }

    public void include(String name, String encoding, boolean parse) throws IOException, TemplateException {
        this.include(this.getTemplateForInclusion(name, encoding, parse));
    }

    public Template getTemplateForInclusion(String name, String encoding, boolean parse) throws IOException {
        return this.getTemplateForInclusion(name, encoding, parse, false);
    }

    public Template getTemplateForInclusion(String name, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
        return this.configuration.getTemplate(name, this.getLocale(), this.getIncludedTemplateCustomLookupCondition(), encoding != null ? encoding : this.getIncludedTemplateEncoding(), parseAsFTL, ignoreMissing);
    }

    private Object getIncludedTemplateCustomLookupCondition() {
        return this.getTemplate().getCustomLookupCondition();
    }

    private String getIncludedTemplateEncoding() {
        String encoding = this.getTemplate().getEncoding();
        if (encoding == null) {
            encoding = this.configuration.getEncoding(this.getLocale());
        }
        return encoding;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void include(Template includedTemplate) throws TemplateException, IOException {
        boolean parentReplacementOn = this.isBeforeIcI2322();
        Template prevTemplate = this.getTemplate();
        if (parentReplacementOn) {
            this.setParent(includedTemplate);
        } else {
            this.legacyParent = includedTemplate;
        }
        this.importMacros(includedTemplate);
        try {
            this.visit(includedTemplate.getRootTreeNode());
        }
        finally {
            if (parentReplacementOn) {
                this.setParent(prevTemplate);
            } else {
                this.legacyParent = prevTemplate;
            }
        }
    }

    public Namespace importLib(String templateName, String targetNsVarName) throws IOException, TemplateException {
        return this.importLib(templateName, targetNsVarName, this.getLazyImports());
    }

    public Namespace importLib(Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
        return this.importLib(null, loadedTemplate, targetNsVarName);
    }

    public Namespace importLib(String templateName, String targetNsVarName, boolean lazy) throws IOException, TemplateException {
        return lazy ? this.importLib(templateName, null, targetNsVarName) : this.importLib(null, this.getTemplateForImporting(templateName), targetNsVarName);
    }

    public Template getTemplateForImporting(String name) throws IOException {
        return this.getTemplateForInclusion(name, null, true);
    }

    private Namespace importLib(String templateName, Template loadedTemplate, String targetNsVarName) throws IOException, TemplateException {
        Namespace existingNamespace;
        boolean lazyImport;
        if (loadedTemplate != null) {
            lazyImport = false;
            templateName = loadedTemplate.getName();
        } else {
            lazyImport = true;
            TemplateNameFormat tnf = this.getConfiguration().getTemplateNameFormat();
            templateName = _CacheAPI.normalizeRootBasedName(tnf, templateName);
        }
        if (this.loadedLibs == null) {
            this.loadedLibs = new HashMap();
        }
        if ((existingNamespace = this.loadedLibs.get(templateName)) != null) {
            if (targetNsVarName != null) {
                this.setVariable(targetNsVarName, existingNamespace);
                if (this.isIcI2324OrLater() && this.currentNamespace == this.mainNamespace) {
                    this.globalNamespace.put(targetNsVarName, existingNamespace);
                }
            }
            if (!lazyImport && existingNamespace instanceof LazilyInitializedNamespace) {
                ((LazilyInitializedNamespace)existingNamespace).ensureInitializedTME();
            }
        } else {
            Namespace newNamespace = lazyImport ? new LazilyInitializedNamespace(templateName) : new Namespace(loadedTemplate);
            this.loadedLibs.put(templateName, newNamespace);
            if (targetNsVarName != null) {
                this.setVariable(targetNsVarName, newNamespace);
                if (this.currentNamespace == this.mainNamespace) {
                    this.globalNamespace.put(targetNsVarName, newNamespace);
                }
            }
            if (!lazyImport) {
                this.initializeImportLibNamespace(newNamespace, loadedTemplate);
            }
        }
        return this.loadedLibs.get(templateName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initializeImportLibNamespace(Namespace newNamespace, Template loadedTemplate) throws TemplateException, IOException {
        Namespace prevNamespace = this.currentNamespace;
        this.currentNamespace = newNamespace;
        Writer prevOut = this.out;
        this.out = NullWriter.INSTANCE;
        try {
            this.include(loadedTemplate);
        }
        finally {
            this.out = prevOut;
            this.currentNamespace = prevNamespace;
        }
    }

    public String toFullTemplateName(String baseName, String targetName) throws MalformedTemplateNameException {
        if (this.isClassicCompatible() || baseName == null) {
            return targetName;
        }
        return _CacheAPI.toRootBasedName(this.configuration.getTemplateNameFormat(), baseName, targetName);
    }

    public String rootBasedToAbsoluteTemplateName(String rootBasedName) throws MalformedTemplateNameException {
        return _CacheAPI.rootBasedNameToAbsoluteName(this.configuration.getTemplateNameFormat(), rootBasedName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String renderElementToString(TemplateElement te) throws IOException, TemplateException {
        Writer prevOut = this.out;
        try {
            StringWriter sw = new StringWriter();
            this.out = sw;
            this.visit(te);
            String string = sw.toString();
            return string;
        }
        finally {
            this.out = prevOut;
        }
    }

    void importMacros(Template template) {
        Iterator it = template.getMacros().values().iterator();
        while (it.hasNext()) {
            this.visitMacroDef((Macro)it.next());
        }
    }

    public String getNamespaceForPrefix(String prefix) {
        return this.currentNamespace.getTemplate().getNamespaceForPrefix(prefix);
    }

    public String getPrefixForNamespace(String nsURI) {
        return this.currentNamespace.getTemplate().getPrefixForNamespace(nsURI);
    }

    public String getDefaultNS() {
        return this.currentNamespace.getTemplate().getDefaultNS();
    }

    public Object __getitem__(String key) throws TemplateModelException {
        return BeansWrapper.getDefaultInstance().unwrap(this.getVariable(key));
    }

    public void __setitem__(String key, Object o) throws TemplateException {
        this.setGlobalVariable(key, this.getObjectWrapper().wrap(o));
    }

    public Object getCustomState(Object identityKey) {
        if (this.customStateVariables == null) {
            return null;
        }
        return this.customStateVariables.get(identityKey);
    }

    public Object setCustomState(Object identityKey, Object value) {
        IdentityHashMap<Object, Object> customStateVariables = this.customStateVariables;
        if (customStateVariables == null) {
            this.customStateVariables = customStateVariables = new IdentityHashMap();
        }
        return customStateVariables.put(identityKey, value);
    }

    private boolean isBeforeIcI2322() {
        return this.configuration.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_22;
    }

    boolean isIcI2324OrLater() {
        return this.configuration.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_24;
    }

    boolean getFastInvalidReferenceExceptions() {
        return this.fastInvalidReferenceExceptions;
    }

    boolean setFastInvalidReferenceExceptions(boolean b) {
        boolean res = this.fastInvalidReferenceExceptions;
        this.fastInvalidReferenceExceptions = b;
        return res;
    }

    class LazilyInitializedNamespace
    extends Namespace {
        private final String templateName;
        private final Locale locale;
        private final String encoding;
        private final Object customLookupCondition;
        private InitializationStatus status;

        private LazilyInitializedNamespace(String templateName) {
            super(null);
            this.status = InitializationStatus.UNINITIALIZED;
            this.templateName = templateName;
            this.locale = Environment.this.getLocale();
            this.encoding = Environment.this.getIncludedTemplateEncoding();
            this.customLookupCondition = Environment.this.getIncludedTemplateCustomLookupCondition();
        }

        private void ensureInitializedTME() throws TemplateModelException {
            if (this.status != InitializationStatus.INITIALIZED && this.status != InitializationStatus.INITIALIZING) {
                if (this.status == InitializationStatus.FAILED) {
                    throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has already failed earlier; won't retry it.");
                }
                try {
                    this.status = InitializationStatus.INITIALIZING;
                    this.initialize();
                    this.status = InitializationStatus.INITIALIZED;
                }
                catch (Exception e) {
                    throw new TemplateModelException("Lazy initialization of the imported namespace for " + StringUtil.jQuote(this.templateName) + " has failed; see cause exception", e);
                }
                finally {
                    if (this.status != InitializationStatus.INITIALIZED) {
                        this.status = InitializationStatus.FAILED;
                    }
                }
            }
        }

        private void ensureInitializedRTE() {
            try {
                this.ensureInitializedTME();
            }
            catch (TemplateModelException e) {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }

        private void initialize() throws IOException, TemplateException {
            this.setTemplate(Environment.this.configuration.getTemplate(this.templateName, this.locale, this.customLookupCondition, this.encoding, true, false));
            Locale lastLocale = Environment.this.getLocale();
            try {
                Environment.this.setLocale(this.locale);
                Environment.this.initializeImportLibNamespace(this, this.getTemplate());
            }
            finally {
                Environment.this.setLocale(lastLocale);
            }
        }

        @Override
        protected Map copyMap(Map map) {
            this.ensureInitializedRTE();
            return super.copyMap(map);
        }

        @Override
        public Template getTemplate() {
            this.ensureInitializedRTE();
            return super.getTemplate();
        }

        @Override
        public void put(String key, Object value) {
            this.ensureInitializedRTE();
            super.put(key, value);
        }

        @Override
        public void put(String key, boolean b) {
            this.ensureInitializedRTE();
            super.put(key, b);
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            this.ensureInitializedTME();
            return super.get(key);
        }

        @Override
        public boolean containsKey(String key) {
            this.ensureInitializedRTE();
            return super.containsKey(key);
        }

        @Override
        public void remove(String key) {
            this.ensureInitializedRTE();
            super.remove(key);
        }

        @Override
        public void putAll(Map m) {
            this.ensureInitializedRTE();
            super.putAll(m);
        }

        @Override
        public Map toMap() throws TemplateModelException {
            this.ensureInitializedTME();
            return super.toMap();
        }

        @Override
        public String toString() {
            this.ensureInitializedRTE();
            return super.toString();
        }

        @Override
        public int size() {
            this.ensureInitializedRTE();
            return super.size();
        }

        @Override
        public boolean isEmpty() {
            this.ensureInitializedRTE();
            return super.isEmpty();
        }

        @Override
        public TemplateCollectionModel keys() {
            this.ensureInitializedRTE();
            return super.keys();
        }

        @Override
        public TemplateCollectionModel values() {
            this.ensureInitializedRTE();
            return super.values();
        }

        @Override
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() {
            this.ensureInitializedRTE();
            return super.keyValuePairIterator();
        }
    }

    private static enum InitializationStatus {
        UNINITIALIZED,
        INITIALIZING,
        INITIALIZED,
        FAILED;

    }

    public class Namespace
    extends SimpleHash {
        private Template template;

        Namespace() {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.template = Environment.this.getTemplate();
        }

        Namespace(Template template) {
            super(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
            this.template = template;
        }

        public Template getTemplate() {
            return this.template == null ? Environment.this.getTemplate() : this.template;
        }

        void setTemplate(Template template) {
            this.template = template;
        }
    }

    final class NestedElementTemplateDirectiveBody
    implements TemplateDirectiveBody {
        private final TemplateElement[] childBuffer;

        private NestedElementTemplateDirectiveBody(TemplateElement[] childBuffer) {
            this.childBuffer = childBuffer;
        }

        @Override
        public void render(Writer newOut) throws TemplateException, IOException {
            Writer prevOut = Environment.this.out;
            Environment.this.out = newOut;
            try {
                Environment.this.visit(this.childBuffer);
            }
            finally {
                Environment.this.out = prevOut;
            }
        }

        TemplateElement[] getChildrenBuffer() {
            return this.childBuffer;
        }
    }

    private static final class NameValuePair {
        private final String name;
        private final TemplateModel value;

        public NameValuePair(String name, TemplateModel value) {
            this.name = name;
            this.value = value;
        }
    }

    private static final class WithArgsState {
        private final TemplateHashModelEx byName;
        private final TemplateSequenceModel byPosition;
        private final boolean orderLast;
        private List<NameValuePair> orderLastByNameCatchAll;

        public WithArgsState(TemplateHashModelEx byName, TemplateSequenceModel byPosition, boolean orderLast) {
            this.byName = byName;
            this.byPosition = byPosition;
            this.orderLast = orderLast;
        }
    }

    private static class LocalContextWithNewLocal
    implements LocalContext {
        private final String lambdaArgName;
        private final TemplateModel lambdaArgValue;

        public LocalContextWithNewLocal(String lambdaArgName, TemplateModel lambdaArgValue) {
            this.lambdaArgName = lambdaArgName;
            this.lambdaArgValue = lambdaArgValue;
        }

        @Override
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            return name.equals(this.lambdaArgName) ? this.lambdaArgValue : null;
        }

        @Override
        public Collection getLocalVariableNames() throws TemplateModelException {
            return Collections.singleton(this.lambdaArgName);
        }
    }
}

