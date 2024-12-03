/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CallPlaceCustomDataInitializationException;
import freemarker.core.DirectiveCallPlace;
import freemarker.core.Dot;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Identifier;
import freemarker.core.InvalidReferenceException;
import freemarker.core.Macro;
import freemarker.core.MiscUtil;
import freemarker.core.NonUserDefinedDirectiveLikeException;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core._CoreStringUtils;
import freemarker.core._DelayedJQuote;
import freemarker.core._MessageUtil;
import freemarker.core._MiscTemplateException;
import freemarker.template.EmptyMap;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template.utility.ObjectFactory;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class UnifiedCall
extends TemplateElement
implements DirectiveCallPlace {
    private Expression nameExp;
    private Map<String, ? extends Expression> namedArgs;
    private List<? extends Expression> positionalArgs;
    private List<String> bodyParameterNames;
    boolean legacySyntax;
    private volatile transient SoftReference sortedNamedArgsCache;
    private CustomDataHolder customDataHolder;

    UnifiedCall(Expression nameExp, Map<String, ? extends Expression> namedArgs, TemplateElements children, List<String> bodyParameterNames) {
        this.nameExp = nameExp;
        this.namedArgs = namedArgs;
        this.setChildren(children);
        this.bodyParameterNames = bodyParameterNames;
    }

    UnifiedCall(Expression nameExp, List<? extends Expression> positionalArgs, TemplateElements children, List<String> bodyParameterNames) {
        this.nameExp = nameExp;
        this.positionalArgs = positionalArgs;
        this.setChildren(children);
        this.bodyParameterNames = bodyParameterNames;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        TemplateModel tm = this.nameExp.eval(env);
        if (tm == Macro.DO_NOTHING_MACRO) {
            return null;
        }
        if (tm instanceof Macro) {
            Macro macro = (Macro)tm;
            if (macro.isFunction() && !this.legacySyntax) {
                throw new _MiscTemplateException(env, "Routine ", new _DelayedJQuote(macro.getName()), " is a function, not a directive. Functions can only be called from expressions, like in ${f()}, ${x + f()} or ", "<@someDirective someParam=f() />", ".");
            }
            env.invokeMacro(macro, this.namedArgs, this.positionalArgs, this.bodyParameterNames, this);
        } else {
            boolean isDirectiveModel = tm instanceof TemplateDirectiveModel;
            if (isDirectiveModel || tm instanceof TemplateTransformModel) {
                Map args;
                if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
                    args = new HashMap();
                    for (Map.Entry<String, ? extends Expression> entry : this.namedArgs.entrySet()) {
                        String key = entry.getKey();
                        Expression valueExp = entry.getValue();
                        TemplateModel value = valueExp.eval(env);
                        args.put(key, value);
                    }
                } else {
                    args = EmptyMap.instance;
                }
                if (isDirectiveModel) {
                    env.visit(this.getChildBuffer(), (TemplateDirectiveModel)tm, args, this.bodyParameterNames);
                } else {
                    env.visitAndTransform(this.getChildBuffer(), (TemplateTransformModel)tm, args);
                }
            } else {
                if (tm == null) {
                    throw InvalidReferenceException.getInstance(this.nameExp, env);
                }
                throw new NonUserDefinedDirectiveLikeException(this.nameExp, tm, env);
            }
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        int i;
        boolean nameIsInParen;
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append('@');
        _MessageUtil.appendExpressionAsUntearable(sb, this.nameExp);
        boolean bl = nameIsInParen = sb.charAt(sb.length() - 1) == ')';
        if (this.positionalArgs != null) {
            for (i = 0; i < this.positionalArgs.size(); ++i) {
                Expression argExp = this.positionalArgs.get(i);
                if (i != 0) {
                    sb.append(',');
                }
                sb.append(' ');
                sb.append(argExp.getCanonicalForm());
            }
        } else {
            List entries = this.getSortedNamedArgs();
            for (int i2 = 0; i2 < entries.size(); ++i2) {
                Map.Entry entry = (Map.Entry)entries.get(i2);
                Expression argExp = (Expression)entry.getValue();
                sb.append(' ');
                sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference((String)entry.getKey()));
                sb.append('=');
                _MessageUtil.appendExpressionAsUntearable(sb, argExp);
            }
        }
        if (this.bodyParameterNames != null && !this.bodyParameterNames.isEmpty()) {
            sb.append("; ");
            for (i = 0; i < this.bodyParameterNames.size(); ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.bodyParameterNames.get(i)));
            }
        }
        if (canonical) {
            if (this.getChildCount() == 0) {
                sb.append("/>");
            } else {
                sb.append('>');
                sb.append(this.getChildrenCanonicalForm());
                sb.append("</@");
                if (!nameIsInParen && (this.nameExp instanceof Identifier || this.nameExp instanceof Dot && ((Dot)this.nameExp).onlyHasIdentifiers())) {
                    sb.append(this.nameExp.getCanonicalForm());
                }
                sb.append('>');
            }
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "@";
    }

    @Override
    int getParameterCount() {
        return 1 + (this.positionalArgs != null ? this.positionalArgs.size() : 0) + (this.namedArgs != null ? this.namedArgs.size() * 2 : 0) + (this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0);
    }

    @Override
    Object getParameterValue(int idx) {
        int bodyParameterNamesSize;
        int namedArgsSize;
        int positionalArgsSize;
        if (idx == 0) {
            return this.nameExp;
        }
        int base = 1;
        int n = positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
        if (idx - base < positionalArgsSize) {
            return this.positionalArgs.get(idx - base);
        }
        int n2 = namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
        if (idx - (base += positionalArgsSize) < namedArgsSize * 2) {
            Map.Entry namedArg = (Map.Entry)this.getSortedNamedArgs().get((idx - base) / 2);
            return (idx - base) % 2 == 0 ? namedArg.getKey() : namedArg.getValue();
        }
        int n3 = bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
        if (idx - (base += namedArgsSize * 2) < bodyParameterNamesSize) {
            return this.bodyParameterNames.get(idx - base);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        int bodyParameterNamesSize;
        int namedArgsSize;
        int positionalArgsSize;
        if (idx == 0) {
            return ParameterRole.CALLEE;
        }
        int base = 1;
        int n = positionalArgsSize = this.positionalArgs != null ? this.positionalArgs.size() : 0;
        if (idx - base < positionalArgsSize) {
            return ParameterRole.ARGUMENT_VALUE;
        }
        int n2 = namedArgsSize = this.namedArgs != null ? this.namedArgs.size() : 0;
        if (idx - (base += positionalArgsSize) < namedArgsSize * 2) {
            return (idx - base) % 2 == 0 ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
        }
        int n3 = bodyParameterNamesSize = this.bodyParameterNames != null ? this.bodyParameterNames.size() : 0;
        if (idx - (base += namedArgsSize * 2) < bodyParameterNamesSize) {
            return ParameterRole.TARGET_LOOP_VARIABLE;
        }
        throw new IndexOutOfBoundsException();
    }

    private List getSortedNamedArgs() {
        List res;
        SoftReference ref = this.sortedNamedArgsCache;
        if (ref != null && (res = (List)((Reference)ref).get()) != null) {
            return res;
        }
        res = MiscUtil.sortMapOfExpressions(this.namedArgs);
        this.sortedNamedArgsCache = new SoftReference<List>(res);
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getOrCreateCustomData(Object providerIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
        UnifiedCall unifiedCall;
        CustomDataHolder customDataHolder = this.customDataHolder;
        if (customDataHolder == null) {
            unifiedCall = this;
            synchronized (unifiedCall) {
                customDataHolder = this.customDataHolder;
                if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
                    this.customDataHolder = customDataHolder = this.createNewCustomData(providerIdentity, objectFactory);
                }
            }
        }
        if (customDataHolder.providerIdentity != providerIdentity) {
            unifiedCall = this;
            synchronized (unifiedCall) {
                customDataHolder = this.customDataHolder;
                if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
                    this.customDataHolder = customDataHolder = this.createNewCustomData(providerIdentity, objectFactory);
                }
            }
        }
        return customDataHolder.customData;
    }

    private CustomDataHolder createNewCustomData(Object provierIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
        Object customData;
        try {
            customData = objectFactory.createObject();
        }
        catch (Exception e) {
            throw new CallPlaceCustomDataInitializationException("Failed to initialize custom data for provider identity " + StringUtil.tryToString(provierIdentity) + " via factory " + StringUtil.tryToString(objectFactory), e);
        }
        if (customData == null) {
            throw new NullPointerException("ObjectFactory.createObject() has returned null");
        }
        CustomDataHolder customDataHolder = new CustomDataHolder(provierIdentity, customData);
        return customDataHolder;
    }

    @Override
    public boolean isNestedOutputCacheable() {
        return this.isChildrenOutputCacheable();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return true;
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }

    private static class CustomDataHolder {
        private final Object providerIdentity;
        private final Object customData;

        public CustomDataHolder(Object providerIdentity, Object customData) {
            this.providerIdentity = providerIdentity;
            this.customData = customData;
        }
    }
}

