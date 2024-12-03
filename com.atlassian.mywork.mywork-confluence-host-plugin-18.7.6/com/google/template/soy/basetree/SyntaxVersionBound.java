/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.basetree;

import com.google.template.soy.basetree.SyntaxVersion;
import javax.annotation.Nullable;

public final class SyntaxVersionBound {
    public final SyntaxVersion syntaxVersion;
    public final String reasonStr;

    public static SyntaxVersionBound selectLower(@Nullable SyntaxVersionBound origBound, SyntaxVersionBound newBound) {
        if (origBound != null && origBound.syntaxVersion.num <= newBound.syntaxVersion.num) {
            return origBound;
        }
        return newBound;
    }

    public SyntaxVersionBound(SyntaxVersion syntaxVersion, String reasonStr) {
        this.syntaxVersion = syntaxVersion;
        this.reasonStr = reasonStr;
    }
}

