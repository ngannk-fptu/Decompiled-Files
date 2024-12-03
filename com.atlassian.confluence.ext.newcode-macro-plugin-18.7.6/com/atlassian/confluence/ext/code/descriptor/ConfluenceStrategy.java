/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.descriptor;

import com.atlassian.confluence.ext.code.descriptor.BrushDefinition;
import com.atlassian.confluence.ext.code.descriptor.ThemeDefinition;
import java.util.List;

public interface ConfluenceStrategy {
    public BrushDefinition[] listBuiltinBrushes();

    public ThemeDefinition[] listBuiltinThemes();

    public List<String> listLocalization();
}

