/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.jssrc;

import com.google.common.base.Preconditions;

public class SoyJsSrcOptions
implements Cloneable {
    private boolean shouldAllowDeprecatedSyntax = false;
    private boolean isUsingIjData = false;
    private CodeStyle codeStyle = CodeStyle.CONCAT;
    private boolean shouldGenerateJsdoc = false;
    private boolean shouldProvideRequireSoyNamespaces = false;
    private boolean shouldProvideRequireJsFunctions = false;
    private boolean shouldProvideBothSoyNamespacesAndJsFunctions = false;
    private boolean shouldDeclareTopLevelNamespaces = true;
    private boolean shouldGenerateGoogMsgDefs = false;
    private boolean googMsgsAreExternal = false;
    private int bidiGlobalDir = 0;
    private boolean useGoogIsRtlForBidiGlobalDir = false;

    public void setShouldAllowDeprecatedSyntax(boolean shouldAllowDeprecatedSyntax) {
        this.shouldAllowDeprecatedSyntax = shouldAllowDeprecatedSyntax;
    }

    @Deprecated
    public boolean shouldAllowDeprecatedSyntax() {
        return this.shouldAllowDeprecatedSyntax;
    }

    public void setIsUsingIjData(boolean isUsingIjData) {
        this.isUsingIjData = isUsingIjData;
    }

    public boolean isUsingIjData() {
        return this.isUsingIjData;
    }

    public void setCodeStyle(CodeStyle codeStyle) {
        this.codeStyle = codeStyle;
    }

    public CodeStyle getCodeStyle() {
        return this.codeStyle;
    }

    public void setShouldGenerateJsdoc(boolean shouldGenerateJsdoc) {
        this.shouldGenerateJsdoc = shouldGenerateJsdoc;
    }

    public boolean shouldGenerateJsdoc() {
        return this.shouldGenerateJsdoc;
    }

    public void setShouldProvideRequireSoyNamespaces(boolean shouldProvideRequireSoyNamespaces) {
        this.shouldProvideRequireSoyNamespaces = shouldProvideRequireSoyNamespaces;
        Preconditions.checkState((!this.shouldProvideRequireSoyNamespaces || !this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Must not enable both shouldProvideRequireSoyNamespaces and shouldProvideRequireJsFunctions.");
        Preconditions.checkState((this.shouldDeclareTopLevelNamespaces || !this.shouldProvideRequireSoyNamespaces ? 1 : 0) != 0, (Object)"Turning off shouldDeclareTopLevelNamespaces has no meaning when shouldProvideRequireSoyNamespaces is enabled.");
    }

    public boolean shouldProvideRequireSoyNamespaces() {
        return this.shouldProvideRequireSoyNamespaces;
    }

    public void setShouldProvideRequireJsFunctions(boolean shouldProvideRequireJsFunctions) {
        this.shouldProvideRequireJsFunctions = shouldProvideRequireJsFunctions;
        Preconditions.checkState((!this.shouldProvideRequireSoyNamespaces || !this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Must not enable both shouldProvideRequireSoyNamespaces and shouldProvideRequireJsFunctions.");
        Preconditions.checkState((this.shouldDeclareTopLevelNamespaces || !this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Turning off shouldDeclareTopLevelNamespaces has no meaning when shouldProvideRequireJsFunctions is enabled.");
    }

    public boolean shouldProvideRequireJsFunctions() {
        return this.shouldProvideRequireJsFunctions;
    }

    public void setShouldProvideBothSoyNamespacesAndJsFunctions(boolean shouldProvideBothSoyNamespacesAndJsFunctions) {
        this.shouldProvideBothSoyNamespacesAndJsFunctions = shouldProvideBothSoyNamespacesAndJsFunctions;
        if (shouldProvideBothSoyNamespacesAndJsFunctions) {
            Preconditions.checkState((this.shouldProvideRequireSoyNamespaces || this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Must only enable shouldProvideBothSoyNamespacesAndJsFunctions after enabling either shouldProvideRequireSoyNamespaces or shouldProvideRequireJsFunctions.");
        }
    }

    public boolean shouldProvideBothSoyNamespacesAndJsFunctions() {
        return this.shouldProvideBothSoyNamespacesAndJsFunctions;
    }

    public void setShouldDeclareTopLevelNamespaces(boolean shouldDeclareTopLevelNamespaces) {
        this.shouldDeclareTopLevelNamespaces = shouldDeclareTopLevelNamespaces;
        Preconditions.checkState((this.shouldDeclareTopLevelNamespaces || !this.shouldProvideRequireSoyNamespaces ? 1 : 0) != 0, (Object)"Turning off shouldDeclareTopLevelNamespaces has no meaning when shouldProvideRequireSoyNamespaces is enabled.");
        Preconditions.checkState((this.shouldDeclareTopLevelNamespaces || !this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Turning off shouldDeclareTopLevelNamespaces has no meaning when shouldProvideRequireJsFunctions is enabled.");
    }

    public boolean shouldDeclareTopLevelNamespaces() {
        return this.shouldDeclareTopLevelNamespaces;
    }

    public void setShouldGenerateGoogMsgDefs(boolean shouldGenerateGoogMsgDefs) {
        this.shouldGenerateGoogMsgDefs = shouldGenerateGoogMsgDefs;
    }

    public boolean shouldGenerateGoogMsgDefs() {
        return this.shouldGenerateGoogMsgDefs;
    }

    public void setGoogMsgsAreExternal(boolean googMsgsAreExternal) {
        this.googMsgsAreExternal = googMsgsAreExternal;
    }

    public boolean googMsgsAreExternal() {
        return this.googMsgsAreExternal;
    }

    public void setBidiGlobalDir(int bidiGlobalDir) {
        Preconditions.checkArgument((bidiGlobalDir >= -1 && bidiGlobalDir <= 1 ? 1 : 0) != 0, (Object)"bidiGlobalDir must be 1 for LTR, or -1 for RTL (or 0 to leave unspecified).");
        Preconditions.checkState((!this.useGoogIsRtlForBidiGlobalDir || bidiGlobalDir == 0 ? 1 : 0) != 0, (Object)"Must not specify both bidiGlobalDir and useGoogIsRtlForBidiGlobalDir.");
        this.bidiGlobalDir = bidiGlobalDir;
    }

    public int getBidiGlobalDir() {
        return this.bidiGlobalDir;
    }

    public void setUseGoogIsRtlForBidiGlobalDir(boolean useGoogIsRtlForBidiGlobalDir) {
        Preconditions.checkState((!useGoogIsRtlForBidiGlobalDir || this.shouldGenerateGoogMsgDefs ? 1 : 0) != 0, (Object)"Do not specify useGoogIsRtlForBidiGlobalDir without shouldGenerateGoogMsgDefs.");
        Preconditions.checkState((!useGoogIsRtlForBidiGlobalDir || this.shouldProvideRequireSoyNamespaces || this.shouldProvideRequireJsFunctions ? 1 : 0) != 0, (Object)"Do not specify useGoogIsRtlForBidiGlobalDir without either shouldProvideRequireSoyNamespaces or shouldProvideRequireJsFunctions.");
        Preconditions.checkState((!useGoogIsRtlForBidiGlobalDir || this.bidiGlobalDir == 0 ? 1 : 0) != 0, (Object)"Must not specify both bidiGlobalDir and useGoogIsRtlForBidiGlobalDir.");
        this.useGoogIsRtlForBidiGlobalDir = useGoogIsRtlForBidiGlobalDir;
    }

    public boolean getUseGoogIsRtlForBidiGlobalDir() {
        return this.useGoogIsRtlForBidiGlobalDir;
    }

    public final SoyJsSrcOptions clone() {
        try {
            return (SoyJsSrcOptions)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("Cloneable interface removed from SoyJsSrcOptions.");
        }
    }

    public static enum CodeStyle {
        STRINGBUILDER,
        CONCAT;

    }
}

