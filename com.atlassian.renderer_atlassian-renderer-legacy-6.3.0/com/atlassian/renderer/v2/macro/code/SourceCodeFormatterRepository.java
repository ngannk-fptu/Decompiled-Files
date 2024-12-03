/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code;

import com.atlassian.renderer.v2.macro.code.SourceCodeFormatter;
import java.util.Collection;

public interface SourceCodeFormatterRepository {
    public SourceCodeFormatter getSourceCodeFormatter(String var1);

    public Collection getAvailableLanguages();
}

