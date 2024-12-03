/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MemberAndArguments;
import freemarker.ext.beans.OverloadedMethods;
import freemarker.ext.beans._MethodUtil;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.util.Collections;
import java.util.List;

public class OverloadedMethodsModel
implements TemplateMethodModelEx,
TemplateSequenceModel {
    private final Object object;
    private final OverloadedMethods overloadedMethods;
    private final BeansWrapper wrapper;

    OverloadedMethodsModel(Object object, OverloadedMethods overloadedMethods, BeansWrapper wrapper) {
        this.object = object;
        this.overloadedMethods = overloadedMethods;
        this.wrapper = wrapper;
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        MemberAndArguments maa = this.overloadedMethods.getMemberAndArguments(arguments, this.wrapper);
        try {
            return maa.invokeMethod(this.wrapper, this.object);
        }
        catch (Exception e) {
            if (e instanceof TemplateModelException) {
                throw (TemplateModelException)e;
            }
            throw _MethodUtil.newInvocationTemplateModelException(this.object, maa.getCallableMemberDescriptor(), (Throwable)e);
        }
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return (TemplateModel)this.exec(Collections.singletonList(new SimpleNumber((Number)index)));
    }

    @Override
    public int size() throws TemplateModelException {
        throw new TemplateModelException("?size is unsupported for " + this.getClass().getName());
    }
}

