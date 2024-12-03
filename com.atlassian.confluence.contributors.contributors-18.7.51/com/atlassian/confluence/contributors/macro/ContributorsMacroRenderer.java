/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import java.io.IOException;

interface ContributorsMacroRenderer {
    public static final String SOY_TEMPLATES_MODULE = "com.atlassian.confluence.contributors:soy-templates";

    public void render(Appendable var1, MacroParameterModel var2) throws IOException;
}

