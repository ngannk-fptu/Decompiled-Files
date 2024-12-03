/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsConsumer;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.util.concurrent.Timeout;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConversionContext {
    public static final String IS_VALIDATING_COMMENT = new String("isValidatingComment");

    public void setProperty(String var1, Object var2);

    @Deprecated
    public PageContext getPageContext();

    default public RenderContext getRenderContext() {
        return null;
    }

    public Object removeProperty(String var1);

    public Object getProperty(String var1);

    public Object getProperty(String var1, Object var2);

    public String getPropertyAsString(String var1);

    public boolean hasProperty(String var1);

    public ContentTree getContentTree();

    public String getOutputDeviceType();

    public @NonNull String getOutputType();

    public boolean isAsyncRenderSafe();

    public void disableAsyncRenderSafe();

    public @Nullable ContentEntityObject getEntity();

    default public @Nullable PageTemplate getTemplate() {
        return null;
    }

    public String getSpaceKey();

    @Deprecated
    default public Timeout getTimeout() {
        return ConcurrentConversionUtil.toComTimeout((io.atlassian.util.concurrent.Timeout)this.timeout());
    }

    public io.atlassian.util.concurrent.Timeout timeout();

    public void checkTimeout() throws XhtmlTimeoutException;

    public boolean isDiffOrEmail();

    public void addMarshallerMetricsConsumer(MarshallerMetricsConsumer var1);

    public boolean removeMarshallerMetricsConsumer(MarshallerMetricsConsumer var1);

    public Set<MarshallerMetricsConsumer> getMarshallerMetricsConsumers();
}

