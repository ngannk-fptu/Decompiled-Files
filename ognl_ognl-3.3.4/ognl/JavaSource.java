/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.OgnlContext;

public interface JavaSource {
    public String toGetSourceString(OgnlContext var1, Object var2);

    public String toSetSourceString(OgnlContext var1, Object var2);
}

