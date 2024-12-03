/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractSupportToolsAction
implements SupportToolsAction {
    private final String category;
    private final String name;
    private final String templateError;
    private final String templateStart;
    private final String templateSuccess;
    private final String title;

    protected AbstractSupportToolsAction(String name, @Nullable String category, @Nullable String title, @Nullable String templatePath) {
        this.category = category;
        this.name = Objects.requireNonNull(name);
        this.templateError = this.getDefaultTemplatePath(templatePath, "start");
        this.templateStart = this.getDefaultTemplatePath(templatePath, "start");
        this.templateSuccess = this.getDefaultTemplatePath(templatePath, "execute");
        this.title = title;
    }

    protected static boolean canModifyState(HttpServletRequest req) {
        return !"GET".equals(req.getMethod());
    }

    @Override
    @Nullable
    public String getCategory() {
        return this.category;
    }

    @Override
    @Nonnull
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getTitle() {
        return this.title;
    }

    @Override
    @Nonnull
    public String getSuccessTemplatePath() {
        return this.templateSuccess;
    }

    @Override
    @Nonnull
    public String getErrorTemplatePath() {
        return this.templateError;
    }

    @Override
    @Nonnull
    public String getStartTemplatePath() {
        return this.templateStart;
    }

    @Override
    public void prepare(Map<String, Object> context, SafeHttpServletRequest request, ValidationLog validationLog) {
    }

    @Override
    public void execute(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
    }

    @Override
    public void validate(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
    }

    @Nonnull
    protected String getDefaultTemplatePath(String path, String suffix) {
        if (path != null) {
            return path;
        }
        return "templates/html/" + this.name + "-" + suffix + ".vm";
    }
}

