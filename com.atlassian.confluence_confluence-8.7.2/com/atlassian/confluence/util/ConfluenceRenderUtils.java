/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ColorSchemeBean;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.spring.container.ContainerManager;
import java.util.Objects;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfluenceRenderUtils {
    public static @Nullable String getAttachmentsRemotePath(@Nullable PageContext context) {
        if (context == null || context.getEntity() == null) {
            return null;
        }
        return ConfluenceRenderUtils.getContextPath() + ConfluenceRenderUtils.getAttachmentsPathForContent(context.getEntity());
    }

    public static @Nullable String getAttachmentRemotePath(@Nullable Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        ContentEntityObject container = attachment.getContainer();
        if (container == null) {
            return null;
        }
        return ConfluenceRenderUtils.getContextPath() + ConfluenceRenderUtils.getAttachmentsPathForContent(container);
    }

    public static @Nullable String getAbsoluteAttachmentRemotePath(@Nullable Attachment attachment) {
        if (attachment == null || attachment.getContainer() == null) {
            return null;
        }
        return Objects.requireNonNull(GeneralUtil.getGlobalSettings()).getBaseUrl() + ConfluenceRenderUtils.getAttachmentsPathForContent(attachment.getContainer());
    }

    public static @Nullable String getAttachmentRemotePath(@Nullable ContentEntityObject content) {
        if (content == null) {
            return null;
        }
        return ConfluenceRenderUtils.getContextPath() + ConfluenceRenderUtils.getAttachmentsPathForContent(content);
    }

    private static String getContextPath() {
        return ((BootstrapManager)BootstrapUtils.getBootstrapManager()).getWebAppContextPath();
    }

    public static String getAttachmentsPathForContent(@NonNull ContentEntityObject content) {
        return "/download/attachments/" + content.getId();
    }

    public static String renderDefaultStylesheet() {
        return ConfluenceRenderUtils.renderSpaceStylesheet(null);
    }

    public static String renderSpaceStylesheet(Space space) {
        ColourSchemeManager colourSchemeManager = (ColourSchemeManager)ContainerManager.getComponent((String)"colourSchemeManager");
        ColourScheme colourScheme = colourSchemeManager.getGlobalColourScheme();
        if (space != null) {
            colourScheme = colourSchemeManager.getSpaceColourScheme(space);
        }
        VelocityContext context = new VelocityContext();
        context.put("webResourceManager", ContainerManager.getComponent((String)"webResourceManager"));
        context.put("colorScheme", (Object)new ColorSchemeBean(colourScheme));
        return VelocityUtils.getRenderedTemplate("/styles/default-inline-css.vm", (Context)context);
    }
}

