/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.reflect;

import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.PointcutParameter;

public class JoinPointMatchImpl
implements JoinPointMatch {
    public static final JoinPointMatch NO_MATCH = new JoinPointMatchImpl();
    private static final PointcutParameter[] NO_BINDINGS = new PointcutParameter[0];
    private boolean match;
    private PointcutParameter[] bindings;

    public JoinPointMatchImpl(PointcutParameter[] bindings) {
        this.match = true;
        this.bindings = bindings;
    }

    private JoinPointMatchImpl() {
        this.match = false;
        this.bindings = NO_BINDINGS;
    }

    @Override
    public boolean matches() {
        return this.match;
    }

    @Override
    public PointcutParameter[] getParameterBindings() {
        return this.bindings;
    }
}

