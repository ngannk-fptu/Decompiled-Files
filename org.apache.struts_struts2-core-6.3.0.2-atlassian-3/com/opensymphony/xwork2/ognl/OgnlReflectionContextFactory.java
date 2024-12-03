/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.Ognl
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import java.util.Map;
import ognl.Ognl;

public class OgnlReflectionContextFactory
implements ReflectionContextFactory {
    @Override
    public Map createDefaultContext(Object root) {
        return Ognl.createDefaultContext((Object)root);
    }
}

