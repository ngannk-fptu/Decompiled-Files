/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.reflect.MemberSignature;

public interface CodeSignature
extends MemberSignature {
    public Class[] getParameterTypes();

    public String[] getParameterNames();

    public Class[] getExceptionTypes();
}

