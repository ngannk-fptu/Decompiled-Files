/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodAccessor
 *  ognl.NullHandler
 *  ognl.OgnlRuntime
 *  ognl.PropertyAccessor
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlNullHandlerWrapper;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import java.util.Set;
import ognl.MethodAccessor;
import ognl.NullHandler;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import org.apache.commons.lang3.BooleanUtils;

public class OgnlValueStackFactory
implements ValueStackFactory {
    protected XWorkConverter xworkConverter;
    protected RootAccessor compoundRootAccessor;
    protected TextProvider textProvider;
    protected Container container;

    @Inject
    protected void setXWorkConverter(XWorkConverter converter) {
        this.xworkConverter = converter;
    }

    @Inject
    protected void setCompoundRootAccessor(RootAccessor compoundRootAccessor) {
        this.compoundRootAccessor = compoundRootAccessor;
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, (PropertyAccessor)compoundRootAccessor);
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, (MethodAccessor)compoundRootAccessor);
    }

    @Inject
    protected void setMethodAccessor(MethodAccessor methodAccessor) {
        OgnlRuntime.setMethodAccessor(Object.class, (MethodAccessor)methodAccessor);
    }

    @Inject(value="system")
    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    @Override
    public ValueStack createValueStack() {
        return this.createValueStack(null, true);
    }

    @Override
    public ValueStack createValueStack(ValueStack stack) {
        return this.createValueStack(stack, false);
    }

    protected ValueStack createValueStack(ValueStack stack, boolean useTextProvider) {
        OgnlValueStack newStack = new OgnlValueStack(stack, this.xworkConverter, this.compoundRootAccessor, useTextProvider ? this.textProvider : null, this.container.getInstance(SecurityMemberAccess.class));
        this.container.inject(newStack);
        return newStack.getActionContext().withContainer(this.container).withValueStack(newStack).getValueStack();
    }

    @Inject
    protected void setContainer(Container container) throws ClassNotFoundException {
        Class<?> cls;
        Set<String> names = container.getInstanceNames(PropertyAccessor.class);
        for (String name : names) {
            cls = Class.forName(name);
            OgnlRuntime.setPropertyAccessor(cls, (PropertyAccessor)container.getInstance(PropertyAccessor.class, name));
        }
        names = container.getInstanceNames(com.opensymphony.xwork2.conversion.NullHandler.class);
        for (String name : names) {
            cls = Class.forName(name);
            OgnlRuntime.setNullHandler(cls, (NullHandler)new OgnlNullHandlerWrapper(container.getInstance(com.opensymphony.xwork2.conversion.NullHandler.class, name)));
        }
        this.container = container;
    }

    @Deprecated
    protected boolean containerAllowsStaticFieldAccess() {
        return BooleanUtils.toBoolean((String)this.container.getInstance(String.class, "struts.ognl.allowStaticFieldAccess"));
    }
}

