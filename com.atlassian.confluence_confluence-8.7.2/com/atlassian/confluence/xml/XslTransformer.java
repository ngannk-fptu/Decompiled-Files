/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xml;

import java.io.Reader;
import javax.xml.transform.Result;

public interface XslTransformer {
    public Result transform(Reader var1, Reader var2, Result var3);
}

