/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.databinding;

import com.oracle.webservices.api.databinding.WSDLResolver;
import java.io.File;

public interface WSDLGenerator {
    public WSDLGenerator inlineSchema(boolean var1);

    public WSDLGenerator property(String var1, Object var2);

    public void generate(WSDLResolver var1);

    public void generate(File var1, String var2);
}

