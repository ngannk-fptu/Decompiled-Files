/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class OverloadedFixArgsMethods
extends OverloadedMethodsSubset {
    OverloadedFixArgsMethods(boolean bugfixed) {
        super(bugfixed);
    }

    @Override
    Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
        return memberDesc.getParamTypes();
    }

    @Override
    void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
    }

    @Override
    MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
        if (tmArgs == null) {
            tmArgs = Collections.EMPTY_LIST;
        }
        int argCount = tmArgs.size();
        Class[][] unwrappingHintsByParamCount = this.getUnwrappingHintsByParamCount();
        if (unwrappingHintsByParamCount.length <= argCount) {
            return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
        }
        Class[] unwarppingHints = unwrappingHintsByParamCount[argCount];
        if (unwarppingHints == null) {
            return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
        }
        Object[] pojoArgs = new Object[argCount];
        int[] typeFlags = this.getTypeFlags(argCount);
        if (typeFlags == ALL_ZEROS_ARRAY) {
            typeFlags = null;
        }
        Iterator it = tmArgs.iterator();
        for (int i = 0; i < argCount; ++i) {
            Object pojo = unwrapper.tryUnwrapTo((TemplateModel)it.next(), unwarppingHints[i], typeFlags != null ? typeFlags[i] : 0);
            if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
                return EmptyMemberAndArguments.noCompatibleOverload(i + 1);
            }
            pojoArgs[i] = pojo;
        }
        MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = this.getMemberDescriptorForArgs(pojoArgs, false);
        if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
            CallableMemberDescriptor memberDesc = (CallableMemberDescriptor)maybeEmtpyMemberDesc;
            if (this.bugfixed) {
                if (typeFlags != null) {
                    this.forceNumberArgumentsToParameterTypes(pojoArgs, memberDesc.getParamTypes(), typeFlags);
                }
            } else {
                BeansWrapper.coerceBigDecimals(memberDesc.getParamTypes(), pojoArgs);
            }
            return new MemberAndArguments(memberDesc, pojoArgs);
        }
        return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor)maybeEmtpyMemberDesc, pojoArgs);
    }
}

