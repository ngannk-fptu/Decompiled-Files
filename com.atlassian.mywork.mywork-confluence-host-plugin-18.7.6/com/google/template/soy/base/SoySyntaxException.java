/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.base;

import com.google.common.base.Preconditions;
import com.google.template.soy.base.SourceLocation;
import javax.annotation.Nullable;

public class SoySyntaxException
extends RuntimeException {
    private SourceLocation srcLoc = SourceLocation.UNKNOWN;
    private String templateName;

    public static SoySyntaxException createWithoutMetaInfo(String message) {
        return new SoySyntaxException(message);
    }

    public static SoySyntaxException createCausedWithoutMetaInfo(@Nullable String message, Throwable cause) {
        Preconditions.checkNotNull((Object)cause);
        if (message != null) {
            return new SoySyntaxException(message, cause);
        }
        return new SoySyntaxException(cause);
    }

    public static SoySyntaxException createWithMetaInfo(String message, @Nullable SourceLocation srcLoc, @Nullable String filePath, @Nullable String templateName) {
        return SoySyntaxException.createWithoutMetaInfo(message).associateMetaInfo(srcLoc, filePath, templateName);
    }

    public static SoySyntaxException createCausedWithMetaInfo(@Nullable String message, Throwable cause, @Nullable SourceLocation srcLoc, @Nullable String filePath, @Nullable String templateName) {
        Preconditions.checkNotNull((Object)cause);
        return SoySyntaxException.createCausedWithoutMetaInfo(message, cause).associateMetaInfo(srcLoc, filePath, templateName);
    }

    @Deprecated
    public SoySyntaxException(String message) {
        super(message);
    }

    protected SoySyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    protected SoySyntaxException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public SoySyntaxException associateMetaInfo(@Nullable SourceLocation srcLoc, @Nullable String filePath, @Nullable String templateName) {
        if (srcLoc != null) {
            Preconditions.checkArgument((filePath == null ? 1 : 0) != 0);
            if (this.srcLoc == SourceLocation.UNKNOWN) {
                this.srcLoc = srcLoc;
            } else {
                Preconditions.checkState((boolean)this.srcLoc.equals(srcLoc));
            }
        }
        if (filePath != null) {
            if (this.srcLoc == SourceLocation.UNKNOWN) {
                this.srcLoc = new SourceLocation(filePath, 0);
            } else {
                Preconditions.checkState((boolean)this.srcLoc.getFilePath().equals(filePath));
            }
        }
        if (templateName != null) {
            if (this.templateName == null) {
                this.templateName = templateName;
            } else {
                Preconditions.checkState((boolean)this.templateName.equals(templateName));
            }
        }
        return this;
    }

    public SourceLocation getSourceLocation() {
        return this.srcLoc;
    }

    @Nullable
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public String getMessage() {
        boolean locationKnown = this.srcLoc.isKnown();
        boolean templateKnown = this.templateName != null;
        String message = super.getMessage();
        if (locationKnown) {
            if (templateKnown) {
                return "In file " + this.srcLoc + ", template " + this.templateName + ": " + message;
            }
            return "In file " + this.srcLoc + ": " + message;
        }
        if (templateKnown) {
            return "In template " + this.templateName + ": " + message;
        }
        return message;
    }

    public String getOriginalMessage() {
        return super.getMessage();
    }
}

