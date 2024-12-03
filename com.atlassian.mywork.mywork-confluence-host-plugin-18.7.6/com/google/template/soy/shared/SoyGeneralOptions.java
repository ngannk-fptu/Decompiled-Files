/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.io.Files
 *  com.google.common.io.Resources
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.google.template.soy.shared;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.template.soy.SoyUtils;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.data.internalutils.InternalValueUtils;
import com.google.template.soy.data.restricted.PrimitiveData;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoyGeneralOptions
implements Cloneable {
    @Nullable
    private SyntaxVersion declaredSyntaxVersion = null;
    private Boolean allowExternalCalls = null;
    private CssHandlingScheme cssHandlingScheme = CssHandlingScheme.LITERAL;
    private ImmutableMap<String, PrimitiveData> compileTimeGlobals = null;
    private boolean supportContentSecurityPolicy = false;

    public void setDeclaredSyntaxVersionName(@Nonnull String versionName) {
        this.declaredSyntaxVersion = SyntaxVersion.forName(versionName);
    }

    public SyntaxVersion getDeclaredSyntaxVersion(SyntaxVersion defaultSyntaxVersion) {
        return this.declaredSyntaxVersion != null ? this.declaredSyntaxVersion : defaultSyntaxVersion;
    }

    public void setAllowExternalCalls(boolean allowExternalCalls) {
        this.allowExternalCalls = allowExternalCalls;
    }

    public Boolean allowExternalCalls() {
        return this.allowExternalCalls;
    }

    public void setCssHandlingScheme(CssHandlingScheme cssHandlingScheme) {
        this.cssHandlingScheme = cssHandlingScheme;
    }

    public CssHandlingScheme getCssHandlingScheme() {
        return this.cssHandlingScheme;
    }

    public void setCompileTimeGlobals(Map<String, ?> compileTimeGlobalsMap) {
        this.setCompileTimeGlobalsInternal(InternalValueUtils.convertCompileTimeGlobalsMap(compileTimeGlobalsMap));
    }

    private void setCompileTimeGlobalsInternal(ImmutableMap<String, PrimitiveData> compileTimeGlobalsMap) {
        Preconditions.checkState((this.compileTimeGlobals == null ? 1 : 0) != 0, (Object)"Compile-time globals already set.");
        this.compileTimeGlobals = compileTimeGlobalsMap;
    }

    public void setCompileTimeGlobals(File compileTimeGlobalsFile) throws IOException {
        this.setCompileTimeGlobalsInternal(SoyUtils.parseCompileTimeGlobals(Files.asCharSource((File)compileTimeGlobalsFile, (Charset)Charsets.UTF_8)));
    }

    public void setCompileTimeGlobals(URL compileTimeGlobalsResource) throws IOException {
        this.setCompileTimeGlobalsInternal(SoyUtils.parseCompileTimeGlobals(Resources.asCharSource((URL)compileTimeGlobalsResource, (Charset)Charsets.UTF_8)));
    }

    public ImmutableMap<String, PrimitiveData> getCompileTimeGlobals() {
        return this.compileTimeGlobals;
    }

    public void setSupportContentSecurityPolicy(boolean supportContentSecurityPolicy) {
        this.supportContentSecurityPolicy = supportContentSecurityPolicy;
    }

    public boolean supportContentSecurityPolicy() {
        return this.supportContentSecurityPolicy;
    }

    public final SoyGeneralOptions clone() {
        try {
            return (SoyGeneralOptions)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("Cloneable interface removed from SoyGeneralOptions.");
        }
    }

    public static enum CssHandlingScheme {
        LITERAL,
        REFERENCE,
        BACKEND_SPECIFIC;

    }
}

