/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.usermodel;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public interface HwmfCharsetAware {
    public void setCharsetProvider(Supplier<Charset> var1);
}

