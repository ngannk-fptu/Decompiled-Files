/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.core.BugException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CallableMemberDescriptor;
import freemarker.ext.beans.EmptyCallableMemberDescriptor;
import freemarker.ext.beans.EmptyMemberAndArguments;
import freemarker.ext.beans.MaybeEmptyCallableMemberDescriptor;
import freemarker.ext.beans.MaybeEmptyMemberAndArguments;
import freemarker.ext.beans.MemberAndArguments;
import freemarker.ext.beans.OverloadedMethodsSubset;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class OverloadedVarArgsMethods
extends OverloadedMethodsSubset {
    OverloadedVarArgsMethods(boolean bugfixed) {
        super(bugfixed);
    }

    @Override
    Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
        int ln;
        Class[] preprocessedParamTypes = (Class[])memberDesc.getParamTypes().clone();
        Class<?> varArgsCompType = preprocessedParamTypes[(ln = preprocessedParamTypes.length) - 1].getComponentType();
        if (varArgsCompType == null) {
            throw new BugException("Only varargs methods should be handled here");
        }
        preprocessedParamTypes[ln - 1] = varArgsCompType;
        return preprocessedParamTypes;
    }

    @Override
    void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
        Class[] oneLongerHints;
        int i;
        int paramCount = paramTypes.length;
        Class[][] unwrappingHintsByParamCount = this.getUnwrappingHintsByParamCount();
        for (i = paramCount - 1; i >= 0; --i) {
            Class[] previousHints = unwrappingHintsByParamCount[i];
            if (previousHints == null) continue;
            this.widenHintsToCommonSupertypes(paramCount, previousHints, this.getTypeFlags(i));
            break;
        }
        if (paramCount + 1 < unwrappingHintsByParamCount.length && (oneLongerHints = unwrappingHintsByParamCount[paramCount + 1]) != null) {
            this.widenHintsToCommonSupertypes(paramCount, oneLongerHints, this.getTypeFlags(paramCount + 1));
        }
        for (i = paramCount + 1; i < unwrappingHintsByParamCount.length; ++i) {
            this.widenHintsToCommonSupertypes(i, paramTypes, paramNumericalTypes);
        }
        if (paramCount > 0) {
            this.widenHintsToCommonSupertypes(paramCount - 1, paramTypes, paramNumericalTypes);
        }
    }

    private void widenHintsToCommonSupertypes(int paramCountOfWidened, Class[] wideningTypes, int[] wideningTypeFlags) {
        Class[] typesToWiden = this.getUnwrappingHintsByParamCount()[paramCountOfWidened];
        if (typesToWiden == null) {
            return;
        }
        int typesToWidenLen = typesToWiden.length;
        int wideningTypesLen = wideningTypes.length;
        int min = Math.min(wideningTypesLen, typesToWidenLen);
        for (int i = 0; i < min; ++i) {
            typesToWiden[i] = this.getCommonSupertypeForUnwrappingHint(typesToWiden[i], wideningTypes[i]);
        }
        if (typesToWidenLen > wideningTypesLen) {
            Class varargsComponentType = wideningTypes[wideningTypesLen - 1];
            for (int i = wideningTypesLen; i < typesToWidenLen; ++i) {
                typesToWiden[i] = this.getCommonSupertypeForUnwrappingHint(typesToWiden[i], varargsComponentType);
            }
        }
        if (this.bugfixed) {
            this.mergeInTypesFlags(paramCountOfWidened, wideningTypeFlags);
        }
    }

    @Override
    MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
        MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc;
        if (tmArgs == null) {
            tmArgs = Collections.EMPTY_LIST;
        }
        int argsLen = tmArgs.size();
        Class[][] unwrappingHintsByParamCount = this.getUnwrappingHintsByParamCount();
        Object[] pojoArgs = new Object[argsLen];
        int[] typesFlags = null;
        block0: for (int paramCount = Math.min(argsLen + 1, unwrappingHintsByParamCount.length - 1); paramCount >= 0; --paramCount) {
            Class[] unwarappingHints = unwrappingHintsByParamCount[paramCount];
            if (unwarappingHints == null) {
                if (paramCount != 0) continue;
                return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
            }
            typesFlags = this.getTypeFlags(paramCount);
            if (typesFlags == ALL_ZEROS_ARRAY) {
                typesFlags = null;
            }
            Iterator it = tmArgs.iterator();
            for (int i = 0; i < argsLen; ++i) {
                int paramIdx = i < paramCount ? i : paramCount - 1;
                Object pojo = unwrapper.tryUnwrapTo((TemplateModel)it.next(), unwarappingHints[paramIdx], typesFlags != null ? typesFlags[paramIdx] : 0);
                if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) continue block0;
                pojoArgs[i] = pojo;
            }
        }
        if ((maybeEmtpyMemberDesc = this.getMemberDescriptorForArgs(pojoArgs, true)) instanceof CallableMemberDescriptor) {
            CallableMemberDescriptor memberDesc = (CallableMemberDescriptor)maybeEmtpyMemberDesc;
            Object argsOrErrorIdx = this.replaceVarargsSectionWithArray(pojoArgs, tmArgs, memberDesc, unwrapper);
            if (!(argsOrErrorIdx instanceof Object[])) {
                return EmptyMemberAndArguments.noCompatibleOverload((Integer)argsOrErrorIdx);
            }
            Object[] pojoArgsWithArray = (Object[])argsOrErrorIdx;
            if (this.bugfixed) {
                if (typesFlags != null) {
                    this.forceNumberArgumentsToParameterTypes(pojoArgsWithArray, memberDesc.getParamTypes(), typesFlags);
                }
            } else {
                BeansWrapper.coerceBigDecimals(memberDesc.getParamTypes(), pojoArgsWithArray);
            }
            return new MemberAndArguments(memberDesc, pojoArgsWithArray);
        }
        return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor)maybeEmtpyMemberDesc, pojoArgs);
    }

    private Object replaceVarargsSectionWithArray(Object[] args, List modelArgs, CallableMemberDescriptor memberDesc, BeansWrapper unwrapper) throws TemplateModelException {
        Class[] paramTypes = memberDesc.getParamTypes();
        int paramCount = paramTypes.length;
        Class<?> varArgsCompType = paramTypes[paramCount - 1].getComponentType();
        int totalArgCount = args.length;
        int fixArgCount = paramCount - 1;
        if (args.length != paramCount) {
            Object[] packedArgs = new Object[paramCount];
            System.arraycopy(args, 0, packedArgs, 0, fixArgCount);
            Object varargs = Array.newInstance(varArgsCompType, totalArgCount - fixArgCount);
            for (int i = fixArgCount; i < totalArgCount; ++i) {
                Object val = unwrapper.tryUnwrapTo((TemplateModel)modelArgs.get(i), varArgsCompType);
                if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                    return i + 1;
                }
                Array.set(varargs, i - fixArgCount, val);
            }
            packedArgs[fixArgCount] = varargs;
            return packedArgs;
        }
        Object val = unwrapper.tryUnwrapTo((TemplateModel)modelArgs.get(fixArgCount), varArgsCompType);
        if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
            return fixArgCount + 1;
        }
        Object array = Array.newInstance(varArgsCompType, 1);
        Array.set(array, 0, val);
        args[fixArgCount] = array;
        return args;
    }
}

