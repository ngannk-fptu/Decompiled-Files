/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.LocalContext;
import freemarker.core.LocalContextStack;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core.TemplateObject;
import freemarker.core._CoreStringUtils;
import freemarker.core._DelayedJQuote;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.Constants;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public final class Macro
extends TemplateElement
implements TemplateModel {
    static final Macro DO_NOTHING_MACRO = new Macro(".pass", Collections.EMPTY_MAP, null, false, false, TemplateElements.EMPTY);
    static final int TYPE_MACRO = 0;
    static final int TYPE_FUNCTION = 1;
    private final String name;
    private final String[] paramNames;
    private final Map<String, Expression> paramNamesWithDefault;
    private final WithArgs withArgs;
    private boolean requireArgsSpecialVariable;
    private final String catchAllParamName;
    private final boolean function;
    private final Object namespaceLookupKey;

    Macro(String name, Map<String, Expression> paramNamesWithDefault, String catchAllParamName, boolean function, boolean requireArgsSpecialVariable, TemplateElements children) {
        this.name = name;
        this.paramNamesWithDefault = paramNamesWithDefault;
        this.paramNames = paramNamesWithDefault.keySet().toArray(new String[0]);
        this.catchAllParamName = catchAllParamName;
        this.withArgs = null;
        this.requireArgsSpecialVariable = requireArgsSpecialVariable;
        this.function = function;
        this.setChildren(children);
        this.namespaceLookupKey = this;
    }

    Macro(Macro that, WithArgs withArgs) {
        this.name = that.name;
        this.paramNamesWithDefault = that.paramNamesWithDefault;
        this.paramNames = that.paramNames;
        this.catchAllParamName = that.catchAllParamName;
        this.withArgs = withArgs;
        this.requireArgsSpecialVariable = that.requireArgsSpecialVariable;
        this.function = that.function;
        this.namespaceLookupKey = that.namespaceLookupKey;
        super.copyFieldsFrom(that);
    }

    boolean getRequireArgsSpecialVariable() {
        return this.requireArgsSpecialVariable;
    }

    public String getCatchAll() {
        return this.catchAllParamName;
    }

    public String[] getArgumentNames() {
        return (String[])this.paramNames.clone();
    }

    String[] getArgumentNamesNoCopy() {
        return this.paramNames;
    }

    public boolean hasArgNamed(String name) {
        return this.paramNamesWithDefault.containsKey(name);
    }

    public String getName() {
        return this.name;
    }

    public WithArgs getWithArgs() {
        return this.withArgs;
    }

    public Object getNamespaceLookupKey() {
        return this.namespaceLookupKey;
    }

    @Override
    TemplateElement[] accept(Environment env) {
        env.visitMacroDef(this);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        if (this.withArgs != null) {
            sb.append('?').append(this.getTemplate().getActualNamingConvention() == 12 ? "withArgs" : "with_args").append("(...)");
        }
        sb.append(' ');
        sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.name));
        if (this.function) {
            sb.append('(');
        }
        int argCnt = this.paramNames.length;
        for (int i = 0; i < argCnt; ++i) {
            if (this.function) {
                if (i != 0) {
                    sb.append(", ");
                }
            } else {
                sb.append(' ');
            }
            String paramName = this.paramNames[i];
            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(paramName));
            Expression paramDefaultExp = this.paramNamesWithDefault.get(paramName);
            if (paramDefaultExp == null) continue;
            sb.append('=');
            if (this.function) {
                sb.append(paramDefaultExp.getCanonicalForm());
                continue;
            }
            _MessageUtil.appendExpressionAsUntearable(sb, paramDefaultExp);
        }
        if (this.catchAllParamName != null) {
            if (this.function) {
                if (argCnt != 0) {
                    sb.append(", ");
                }
            } else {
                sb.append(' ');
            }
            sb.append(this.catchAllParamName);
            sb.append("...");
        }
        if (this.function) {
            sb.append(')');
        }
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</").append(this.getNodeTypeSymbol()).append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.function ? "#function" : "#macro";
    }

    public boolean isFunction() {
        return this.function;
    }

    @Override
    int getParameterCount() {
        return 1 + this.paramNames.length * 2 + 1 + 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx == 0) {
            return this.name;
        }
        int argDescsEnd = this.paramNames.length * 2 + 1;
        if (idx < argDescsEnd) {
            String paramName = this.paramNames[(idx - 1) / 2];
            if (idx % 2 != 0) {
                return paramName;
            }
            return this.paramNamesWithDefault.get(paramName);
        }
        if (idx == argDescsEnd) {
            return this.catchAllParamName;
        }
        if (idx == argDescsEnd + 1) {
            return this.function ? 1 : 0;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx == 0) {
            return ParameterRole.ASSIGNMENT_TARGET;
        }
        int argDescsEnd = this.paramNames.length * 2 + 1;
        if (idx < argDescsEnd) {
            if (idx % 2 != 0) {
                return ParameterRole.PARAMETER_NAME;
            }
            return ParameterRole.PARAMETER_DEFAULT;
        }
        if (idx == argDescsEnd) {
            return ParameterRole.CATCH_ALL_PARAMETER_NAME;
        }
        if (idx == argDescsEnd + 1) {
            return ParameterRole.AST_NODE_SUBTYPE;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return true;
    }

    static final class WithArgs {
        private final TemplateHashModelEx byName;
        private final TemplateSequenceModel byPosition;
        private final boolean orderLast;

        WithArgs(TemplateHashModelEx byName, boolean orderLast) {
            this.byName = byName;
            this.byPosition = null;
            this.orderLast = orderLast;
        }

        WithArgs(TemplateSequenceModel byPosition, boolean orderLast) {
            this.byName = null;
            this.byPosition = byPosition;
            this.orderLast = orderLast;
        }

        public TemplateHashModelEx getByName() {
            return this.byName;
        }

        public TemplateSequenceModel getByPosition() {
            return this.byPosition;
        }

        public boolean isOrderLast() {
            return this.orderLast;
        }
    }

    class Context
    implements LocalContext {
        final Environment.Namespace localVars;
        final TemplateObject callPlace;
        final Environment.Namespace nestedContentNamespace;
        final List<String> nestedContentParameterNames;
        final LocalContextStack prevLocalContextStack;
        final Context prevMacroContext;
        TemplateModel argsSpecialVariableValue;

        Context(Environment env, TemplateObject callPlace, List<String> nestedContentParameterNames) {
            this.localVars = env.new Environment.Namespace();
            this.callPlace = callPlace;
            this.nestedContentNamespace = env.getCurrentNamespace();
            this.nestedContentParameterNames = nestedContentParameterNames;
            this.prevLocalContextStack = env.getLocalContextStack();
            this.prevMacroContext = env.getCurrentMacroContext();
        }

        Macro getMacro() {
            return Macro.this;
        }

        void checkParamsSetAndApplyDefaults(Environment env) throws TemplateException {
            InvalidReferenceException firstInvalidReferenceExceptionForDefaultValue;
            Expression firstUnresolvedDefaultValueExpression;
            boolean resolvedADefaultValue;
            boolean hasUnresolvedDefaultValue;
            TemplateModel[] argsSpecVarDraft = Macro.this.requireArgsSpecialVariable ? new TemplateModel[Macro.this.paramNames.length] : null;
            do {
                firstUnresolvedDefaultValueExpression = null;
                firstInvalidReferenceExceptionForDefaultValue = null;
                hasUnresolvedDefaultValue = false;
                resolvedADefaultValue = false;
                for (int paramIndex = 0; paramIndex < Macro.this.paramNames.length; ++paramIndex) {
                    String argName = Macro.this.paramNames[paramIndex];
                    TemplateModel argValue = this.localVars.get(argName);
                    if (argValue == null) {
                        Object[] objectArray;
                        Expression defaultValueExp = (Expression)Macro.this.paramNamesWithDefault.get(argName);
                        if (defaultValueExp != null) {
                            try {
                                TemplateModel defaultValue = defaultValueExp.eval(env);
                                if (defaultValue == null) {
                                    if (hasUnresolvedDefaultValue) continue;
                                    firstUnresolvedDefaultValueExpression = defaultValueExp;
                                    hasUnresolvedDefaultValue = true;
                                    continue;
                                }
                                this.localVars.put(argName, defaultValue);
                                resolvedADefaultValue = true;
                                if (argsSpecVarDraft == null) continue;
                                argsSpecVarDraft[paramIndex] = defaultValue;
                            }
                            catch (InvalidReferenceException e) {
                                if (hasUnresolvedDefaultValue) continue;
                                hasUnresolvedDefaultValue = true;
                                firstInvalidReferenceExceptionForDefaultValue = e;
                            }
                            continue;
                        }
                        if (env.isClassicCompatible()) continue;
                        boolean argWasSpecified = this.localVars.containsKey(argName);
                        _ErrorDescriptionBuilder _ErrorDescriptionBuilder2 = new _ErrorDescriptionBuilder("When calling ", Macro.this.isFunction() ? "function" : "macro", " ", new _DelayedJQuote(Macro.this.name), ", required parameter ", new _DelayedJQuote(argName), " (parameter #", paramIndex + 1, ") was ", argWasSpecified ? "specified, but had null/missing value." : "not specified.");
                        if (argWasSpecified) {
                            Object[] objectArray2 = new Object[1];
                            objectArray = objectArray2;
                            objectArray2[0] = "If the parameter value expression on the caller side is known to be legally null/missing, you may want to specify a default value for it with the \"!\" operator, like paramValue!defaultValue.";
                        } else {
                            Object[] objectArray3 = new Object[3];
                            objectArray3[0] = "If the omission was deliberate, you may consider making the parameter optional in the macro by specifying a default value for it, like ";
                            objectArray3[1] = "<#macro macroName paramName=defaultExpr>";
                            objectArray = objectArray3;
                            objectArray3[2] = ")";
                        }
                        throw new _MiscTemplateException(env, _ErrorDescriptionBuilder2.tip(objectArray));
                    }
                    if (argsSpecVarDraft == null) continue;
                    argsSpecVarDraft[paramIndex] = argValue;
                }
            } while (hasUnresolvedDefaultValue && resolvedADefaultValue);
            if (hasUnresolvedDefaultValue) {
                if (firstInvalidReferenceExceptionForDefaultValue != null) {
                    throw firstInvalidReferenceExceptionForDefaultValue;
                }
                if (!env.isClassicCompatible()) {
                    throw InvalidReferenceException.getInstance(firstUnresolvedDefaultValueExpression, env);
                }
            }
            if (argsSpecVarDraft != null) {
                TemplateModel catchAllArgValue;
                String catchAllParamName = this.getMacro().catchAllParamName;
                TemplateModel templateModel = catchAllArgValue = catchAllParamName != null ? this.localVars.get(catchAllParamName) : null;
                if (this.getMacro().isFunction()) {
                    int lengthWithCatchAlls = argsSpecVarDraft.length;
                    if (catchAllArgValue != null) {
                        lengthWithCatchAlls += ((TemplateSequenceModel)catchAllArgValue).size();
                    }
                    SimpleSequence argsSpecVarValue = new SimpleSequence(lengthWithCatchAlls, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                    for (int paramIndex = 0; paramIndex < argsSpecVarDraft.length; ++paramIndex) {
                        argsSpecVarValue.add(argsSpecVarDraft[paramIndex]);
                    }
                    if (catchAllParamName != null) {
                        TemplateSequenceModel catchAllSeq = (TemplateSequenceModel)catchAllArgValue;
                        int catchAllSize = catchAllSeq.size();
                        for (int j = 0; j < catchAllSize; ++j) {
                            argsSpecVarValue.add(catchAllSeq.get(j));
                        }
                    }
                    assert (argsSpecVarValue.size() == lengthWithCatchAlls);
                    this.argsSpecialVariableValue = argsSpecVarValue;
                } else {
                    TemplateHashModelEx2 catchAllHash;
                    int lengthWithCatchAlls = argsSpecVarDraft.length;
                    if (catchAllParamName != null) {
                        if (catchAllArgValue instanceof TemplateSequenceModel) {
                            if (((TemplateSequenceModel)catchAllArgValue).size() != 0) {
                                throw new _MiscTemplateException("The macro can only by called with named arguments, because it uses both .", "args", " and a non-empty catch-all parameter.");
                            }
                            catchAllHash = Constants.EMPTY_HASH_EX2;
                        } else {
                            catchAllHash = (TemplateHashModelEx2)catchAllArgValue;
                        }
                        lengthWithCatchAlls += catchAllHash.size();
                    } else {
                        catchAllHash = null;
                    }
                    SimpleHash argsSpecVarValue = new SimpleHash(new LinkedHashMap<String, Object>(lengthWithCatchAlls * 4 / 3, 1.0f), _ObjectWrappers.SAFE_OBJECT_WRAPPER, 0);
                    for (int paramIndex = 0; paramIndex < argsSpecVarDraft.length; ++paramIndex) {
                        argsSpecVarValue.put(Macro.this.paramNames[paramIndex], argsSpecVarDraft[paramIndex]);
                    }
                    if (catchAllArgValue != null) {
                        TemplateHashModelEx2.KeyValuePairIterator iter = catchAllHash.keyValuePairIterator();
                        while (iter.hasNext()) {
                            TemplateHashModelEx2.KeyValuePair kvp = iter.next();
                            argsSpecVarValue.put(((TemplateScalarModel)kvp.getKey()).getAsString(), kvp.getValue());
                        }
                    }
                    assert (argsSpecVarValue.size() == lengthWithCatchAlls);
                    this.argsSpecialVariableValue = argsSpecVarValue;
                }
            }
        }

        @Override
        public TemplateModel getLocalVariable(String name) throws TemplateModelException {
            return this.localVars.get(name);
        }

        Environment.Namespace getLocals() {
            return this.localVars;
        }

        void setLocalVar(String name, TemplateModel var) {
            this.localVars.put(name, var);
        }

        @Override
        public Collection getLocalVariableNames() throws TemplateModelException {
            HashSet<String> result = new HashSet<String>();
            TemplateModelIterator it = this.localVars.keys().iterator();
            while (it.hasNext()) {
                result.add(((TemplateScalarModel)it.next()).getAsString());
            }
            return result;
        }

        TemplateModel getArgsSpecialVariableValue() {
            return this.argsSpecialVariableValue;
        }

        void setArgsSpecialVariableValue(TemplateModel argsSpecialVariableValue) {
            this.argsSpecialVariableValue = argsSpecialVariableValue;
        }
    }
}

