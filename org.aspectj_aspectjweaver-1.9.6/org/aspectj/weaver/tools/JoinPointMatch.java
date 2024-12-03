/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.tools.PointcutParameter;

public interface JoinPointMatch {
    public boolean matches();

    public PointcutParameter[] getParameterBindings();
}

