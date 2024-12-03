/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.RenderingException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.Validateable;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SupportToolsAction
extends Validateable {
    @Nonnull
    public String getName();

    @Nullable
    public String getCategory();

    @Nonnull
    public String getSuccessTemplatePath();

    @Nonnull
    public String getErrorTemplatePath();

    @Nonnull
    public String getStartTemplatePath();

    @Nullable
    public String getTitle();

    @Nonnull
    public SupportToolsAction newInstance();

    public void prepare(Map<String, Object> var1, SafeHttpServletRequest var2, ValidationLog var3);

    public void execute(Map<String, Object> var1, SafeHttpServletRequest var2, ValidationLog var3) throws RenderingException, IOException, Exception;

    default public boolean requiresWebSudo() {
        return true;
    }
}

