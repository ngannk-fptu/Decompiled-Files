/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedConversionToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.core._TemplateModelException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CallableMemberDescriptor;
import freemarker.ext.beans.EmptyMemberAndArguments;
import freemarker.ext.beans.MaybeEmptyMemberAndArguments;
import freemarker.ext.beans.MemberAndArguments;
import freemarker.ext.beans.OverloadedFixArgsMethods;
import freemarker.ext.beans.OverloadedMethodsSubset;
import freemarker.ext.beans.OverloadedVarArgsMethods;
import freemarker.ext.beans.ReflectionCallableMemberDescriptor;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

final class OverloadedMethods {
    private final OverloadedMethodsSubset fixArgMethods;
    private OverloadedMethodsSubset varargMethods;
    private final boolean bugfixed;

    OverloadedMethods(boolean bugfixed) {
        this.bugfixed = bugfixed;
        this.fixArgMethods = new OverloadedFixArgsMethods(bugfixed);
    }

    void addMethod(Method method) {
        Class[] paramTypes = method.getParameterTypes();
        this.addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(method, paramTypes));
    }

    void addConstructor(Constructor constr) {
        Class[] paramTypes = constr.getParameterTypes();
        this.addCallableMemberDescriptor(new ReflectionCallableMemberDescriptor(constr, paramTypes));
    }

    private void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
        this.fixArgMethods.addCallableMemberDescriptor(memberDesc);
        if (memberDesc.isVarargs()) {
            if (this.varargMethods == null) {
                this.varargMethods = new OverloadedVarArgsMethods(this.bugfixed);
            }
            this.varargMethods.addCallableMemberDescriptor(memberDesc);
        }
    }

    MemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
        MaybeEmptyMemberAndArguments varargsRes;
        MaybeEmptyMemberAndArguments fixArgsRes = this.fixArgMethods.getMemberAndArguments(tmArgs, unwrapper);
        if (fixArgsRes instanceof MemberAndArguments) {
            return (MemberAndArguments)fixArgsRes;
        }
        if (this.varargMethods != null) {
            varargsRes = this.varargMethods.getMemberAndArguments(tmArgs, unwrapper);
            if (varargsRes instanceof MemberAndArguments) {
                return (MemberAndArguments)varargsRes;
            }
        } else {
            varargsRes = null;
        }
        _ErrorDescriptionBuilder edb = new _ErrorDescriptionBuilder(this.toCompositeErrorMessage((EmptyMemberAndArguments)fixArgsRes, (EmptyMemberAndArguments)varargsRes, tmArgs), "\nThe matching overload was searched among these members:\n", this.memberListToString());
        if (!this.bugfixed) {
            edb.tip("You seem to use BeansWrapper with incompatibleImprovements set below 2.3.21. If you think this error is unfounded, enabling 2.3.21 fixes may helps. See version history for more.");
        }
        this.addMarkupBITipAfterNoNoMarchIfApplicable(edb, tmArgs);
        throw new _TemplateModelException(edb);
    }

    private Object[] toCompositeErrorMessage(EmptyMemberAndArguments fixArgsEmptyRes, EmptyMemberAndArguments varargsEmptyRes, List tmArgs) {
        Object[] argsErrorMsg = varargsEmptyRes != null ? (fixArgsEmptyRes == null || fixArgsEmptyRes.isNumberOfArgumentsWrong() ? this.toErrorMessage(varargsEmptyRes, tmArgs) : new Object[]{"When trying to call the non-varargs overloads:\n", this.toErrorMessage(fixArgsEmptyRes, tmArgs), "\nWhen trying to call the varargs overloads:\n", this.toErrorMessage(varargsEmptyRes, null)}) : this.toErrorMessage(fixArgsEmptyRes, tmArgs);
        return argsErrorMsg;
    }

    private Object[] toErrorMessage(EmptyMemberAndArguments res, List tmArgs) {
        Object[] objectArray;
        Object object;
        Object[] unwrappedArgs = res.getUnwrappedArguments();
        Object[] objectArray2 = new Object[3];
        objectArray2[0] = res.getErrorDescription();
        if (tmArgs != null) {
            Object[] objectArray3 = new Object[3];
            objectArray3[0] = "\nThe FTL type of the argument values were: ";
            objectArray3[1] = this.getTMActualParameterTypes(tmArgs);
            object = objectArray3;
            objectArray3[2] = ".";
        } else {
            object = objectArray2[1] = "";
        }
        if (unwrappedArgs != null) {
            Object[] objectArray4 = new Object[2];
            objectArray4[0] = "\nThe Java type of the argument values were: ";
            objectArray = objectArray4;
            objectArray4[1] = this.getUnwrappedActualParameterTypes(unwrappedArgs) + ".";
        } else {
            objectArray = "";
        }
        objectArray2[2] = objectArray;
        return objectArray2;
    }

    private _DelayedConversionToString memberListToString() {
        return new _DelayedConversionToString(null){

            @Override
            protected String doConversion(Object obj) {
                boolean hasMethods;
                Iterator fixArgMethodsIter = OverloadedMethods.this.fixArgMethods.getMemberDescriptors();
                Iterator varargMethodsIter = OverloadedMethods.this.varargMethods != null ? OverloadedMethods.this.varargMethods.getMemberDescriptors() : null;
                boolean bl = hasMethods = fixArgMethodsIter.hasNext() || varargMethodsIter != null && varargMethodsIter.hasNext();
                if (hasMethods) {
                    CallableMemberDescriptor callableMemberDesc;
                    StringBuilder sb = new StringBuilder();
                    HashSet<CallableMemberDescriptor> fixArgMethods = new HashSet<CallableMemberDescriptor>();
                    while (fixArgMethodsIter.hasNext()) {
                        if (sb.length() != 0) {
                            sb.append(",\n");
                        }
                        sb.append("    ");
                        callableMemberDesc = (CallableMemberDescriptor)fixArgMethodsIter.next();
                        fixArgMethods.add(callableMemberDesc);
                        sb.append(callableMemberDesc.getDeclaration());
                    }
                    if (varargMethodsIter != null) {
                        while (varargMethodsIter.hasNext()) {
                            callableMemberDesc = (CallableMemberDescriptor)varargMethodsIter.next();
                            if (fixArgMethods.contains(callableMemberDesc)) continue;
                            if (sb.length() != 0) {
                                sb.append(",\n");
                            }
                            sb.append("    ");
                            sb.append(callableMemberDesc.getDeclaration());
                        }
                    }
                    return sb.toString();
                }
                return "No members";
            }
        };
    }

    private void addMarkupBITipAfterNoNoMarchIfApplicable(_ErrorDescriptionBuilder edb, List tmArgs) {
        for (int argIdx = 0; argIdx < tmArgs.size(); ++argIdx) {
            Object tmArg = tmArgs.get(argIdx);
            if (!(tmArg instanceof TemplateMarkupOutputModel)) continue;
            Iterator membDescs = this.fixArgMethods.getMemberDescriptors();
            while (membDescs.hasNext()) {
                CallableMemberDescriptor membDesc = (CallableMemberDescriptor)membDescs.next();
                Class[] paramTypes = membDesc.getParamTypes();
                Class<String> paramType = null;
                if (membDesc.isVarargs() && argIdx >= paramTypes.length - 1 && (paramType = paramTypes[paramTypes.length - 1]).isArray()) {
                    paramType = paramType.getComponentType();
                }
                if (paramType == null && argIdx < paramTypes.length) {
                    paramType = paramTypes[argIdx];
                }
                if (paramType == null || !paramType.isAssignableFrom(String.class) || paramType.isAssignableFrom(tmArg.getClass())) continue;
                edb.tip("A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.");
                return;
            }
        }
    }

    private _DelayedConversionToString getTMActualParameterTypes(List arguments) {
        Object[] argumentTypeDescs = new String[arguments.size()];
        for (int i = 0; i < arguments.size(); ++i) {
            argumentTypeDescs[i] = ClassUtil.getFTLTypeDescription((TemplateModel)arguments.get(i));
        }
        return new DelayedCallSignatureToString(argumentTypeDescs){

            @Override
            String argumentToString(Object argType) {
                return (String)argType;
            }
        };
    }

    private Object getUnwrappedActualParameterTypes(Object[] unwrappedArgs) {
        Object[] argumentTypes = new Class[unwrappedArgs.length];
        for (int i = 0; i < unwrappedArgs.length; ++i) {
            Object unwrappedArg = unwrappedArgs[i];
            argumentTypes[i] = unwrappedArg != null ? unwrappedArg.getClass() : null;
        }
        return new DelayedCallSignatureToString(argumentTypes){

            @Override
            String argumentToString(Object argType) {
                return argType != null ? ClassUtil.getShortClassName((Class)argType) : ClassUtil.getShortClassNameOfObject(null);
            }
        };
    }

    private abstract class DelayedCallSignatureToString
    extends _DelayedConversionToString {
        public DelayedCallSignatureToString(Object[] argTypeArray) {
            super(argTypeArray);
        }

        @Override
        protected String doConversion(Object obj) {
            Object[] argTypes = (Object[])obj;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < argTypes.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(this.argumentToString(argTypes[i]));
            }
            return sb.toString();
        }

        abstract String argumentToString(Object var1);
    }
}

