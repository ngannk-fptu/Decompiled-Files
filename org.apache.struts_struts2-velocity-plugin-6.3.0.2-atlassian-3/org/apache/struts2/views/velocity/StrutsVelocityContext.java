/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.util.ValueStack
 *  org.apache.struts2.util.ValueStackProvider
 *  org.apache.velocity.VelocityContext
 */
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.apache.struts2.util.ValueStackProvider;
import org.apache.velocity.VelocityContext;

public class StrutsVelocityContext
extends VelocityContext
implements ValueStackProvider {
    private final ValueStack stack;
    private final List<VelocityContext> chainedContexts;

    public StrutsVelocityContext(List<VelocityContext> chainedContexts, ValueStack stack) {
        this.chainedContexts = chainedContexts;
        this.stack = stack;
    }

    @Deprecated
    public StrutsVelocityContext(ValueStack stack) {
        this((List<VelocityContext>)null, stack);
    }

    @Deprecated
    public StrutsVelocityContext(VelocityContext[] chainedContexts, ValueStack stack) {
        this(new ArrayList<VelocityContext>(Arrays.asList(chainedContexts)), stack);
    }

    public boolean internalContainsKey(String key) {
        return this.internalGet(key) != null;
    }

    public Object internalGet(String key) {
        for (Function<String, Object> contextGet : this.contextGetterList()) {
            Object val = contextGet.apply(key);
            if (val == null) continue;
            return val;
        }
        return null;
    }

    protected List<Function<String, Object>> contextGetterList() {
        return Arrays.asList(this::superInternalGet, this::chainedContextGet, this::stackGet);
    }

    protected Object superInternalGet(String key) {
        return super.internalGet(key);
    }

    protected Object stackGet(String key) {
        if (this.stack == null) {
            return null;
        }
        return this.stack.findValue(key);
    }

    protected Object chainedContextGet(String key) {
        if (this.chainedContexts == null) {
            return null;
        }
        for (VelocityContext chainedContext : this.chainedContexts) {
            Object val = chainedContext.get(key);
            if (val == null) continue;
            return val;
        }
        return null;
    }

    public ValueStack getValueStack() {
        return this.stack;
    }
}

