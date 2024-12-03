/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.primitives.Primitives
 *  io.atlassian.util.concurrent.Timeout
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsConsumer;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import io.atlassian.util.concurrent.Timeout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultConversionContext
implements ConversionContext {
    private final RenderContext renderContext;
    private final Map<String, Object> properties = new HashMap<String, Object>();
    private ContentTree contentTree;
    private final Timeout timeout;
    private final String outputDeviceType;
    private boolean asyncRenderSafe = true;
    private final Set<MarshallerMetricsConsumer> marshallerMetricsConsumers = new HashSet<MarshallerMetricsConsumer>();

    public DefaultConversionContext(RenderContext renderContext) {
        this(renderContext, renderContext instanceof PageContext ? ((PageContext)renderContext).getOutputDeviceType() : "desktop");
    }

    public DefaultConversionContext(RenderContext renderContext, String outputDeviceType) {
        if (StringUtils.isBlank((CharSequence)outputDeviceType)) {
            throw new IllegalArgumentException("A device output type is manadatory. Try desktop");
        }
        this.renderContext = renderContext;
        this.timeout = DefaultConversionContext.getOrCreateTimeout(renderContext);
        this.outputDeviceType = outputDeviceType;
        this.properties.put("output-device-type", outputDeviceType);
        if (renderContext instanceof PageContext) {
            ((PageContext)renderContext).setOutputDeviceType(outputDeviceType);
        }
    }

    @Deprecated
    public ImmutableMap<String, Object> immutableProperties() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof String) && !Primitives.isWrapperType(value.getClass())) continue;
            builder.put(entry);
        }
        return builder.build();
    }

    public Map<String, Object> getImmutableProperties() {
        return ImmutableMap.copyOf(this.immutableProperties());
    }

    private static Timeout getOrCreateTimeout(RenderContext renderContext) {
        Timeout renderContextTimeout;
        if (renderContext instanceof PageContext && (renderContextTimeout = ((PageContext)renderContext).timeout()) != null) {
            return renderContextTimeout;
        }
        return PageContext.minimumTimeout();
    }

    @Override
    public PageContext getPageContext() {
        if (this.renderContext instanceof PageContext) {
            return (PageContext)this.renderContext;
        }
        return null;
    }

    @Override
    public RenderContext getRenderContext() {
        return this.renderContext;
    }

    @Override
    public Object removeProperty(String name) {
        return this.properties.remove(name);
    }

    @Override
    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        Object value = this.properties.get(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public String getPropertyAsString(String name) {
        Object obj = this.properties.get(name);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    @Override
    public boolean hasProperty(String name) {
        return this.properties.containsKey(name);
    }

    @Override
    public ContentTree getContentTree() {
        if (this.contentTree == null) {
            ContentEntityObject ceo;
            this.contentTree = new ContentTree();
            if (this.renderContext instanceof PageContext && (ceo = ((PageContext)this.renderContext).getEntity()) instanceof Page) {
                ContentNode node = new ContentNode((Page)ceo);
                this.contentTree.addRootNode(node);
            }
        }
        return this.contentTree;
    }

    public void setContentTree(ContentTree contentTree) {
        this.contentTree = contentTree;
    }

    @Override
    public String getOutputDeviceType() {
        return this.outputDeviceType;
    }

    @Override
    public @NonNull String getOutputType() {
        if (this.renderContext != null) {
            return this.renderContext.getOutputType();
        }
        return ConversionContextOutputType.DISPLAY.value();
    }

    @Override
    public boolean isAsyncRenderSafe() {
        return this.asyncRenderSafe;
    }

    @Override
    public void disableAsyncRenderSafe() {
        this.asyncRenderSafe = false;
    }

    @Override
    public Timeout timeout() {
        return this.timeout;
    }

    @Override
    public void checkTimeout() throws XhtmlTimeoutException {
        if (this.timeout.isExpired()) {
            throw XhtmlTimeoutException.createForTimeout(this.timeout);
        }
    }

    @Override
    public boolean isDiffOrEmail() {
        String outputType = this.getOutputType();
        return outputType.equals(ConversionContextOutputType.DIFF.value()) || outputType.equals(ConversionContextOutputType.EMAIL.value());
    }

    @Override
    public void addMarshallerMetricsConsumer(MarshallerMetricsConsumer consumer) {
        this.marshallerMetricsConsumers.add(consumer);
    }

    @Override
    public boolean removeMarshallerMetricsConsumer(MarshallerMetricsConsumer consumer) {
        return this.marshallerMetricsConsumers.remove(consumer);
    }

    @Override
    public Set<MarshallerMetricsConsumer> getMarshallerMetricsConsumers() {
        return Set.copyOf(this.marshallerMetricsConsumers);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultConversionContext that = (DefaultConversionContext)o;
        if (this.properties != null ? !this.properties.equals(that.properties) : that.properties != null) {
            return false;
        }
        if (this.renderContext != null ? !this.renderContext.equals((Object)that.renderContext) : that.renderContext != null) {
            return false;
        }
        return this.contentTree != null ? this.contentTree.equals(that.contentTree) : that.contentTree == null;
    }

    public int hashCode() {
        int result = this.renderContext != null ? this.renderContext.hashCode() : 0;
        result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
        result = 31 * result + (this.contentTree != null ? this.contentTree.hashCode() : 0);
        return result;
    }

    @Override
    public ContentEntityObject getEntity() {
        if (this.renderContext instanceof PageTemplateContext) {
            PageTemplate template = ((PageTemplateContext)this.renderContext).getTemplate();
            SpaceContentEntityObject contentEntitySimulacrum = new SpaceContentEntityObject(){

                @Override
                public String getType() {
                    return "page";
                }

                @Override
                public String getUrlPath() {
                    return "";
                }
            };
            contentEntitySimulacrum.setBodyAsString(template.getContent());
            contentEntitySimulacrum.setTitle("Template Preview");
            contentEntitySimulacrum.setSpace(template.getSpace());
            return contentEntitySimulacrum;
        }
        if (this.renderContext instanceof PageContext) {
            return ((PageContext)this.renderContext).getEntity();
        }
        return null;
    }

    @Override
    public PageTemplate getTemplate() {
        if (this.renderContext instanceof PageTemplateContext) {
            return ((PageTemplateContext)this.renderContext).getTemplate();
        }
        return null;
    }

    @Override
    public String getSpaceKey() {
        if (this.renderContext instanceof PageContext) {
            return ((PageContext)this.renderContext).getSpaceKey();
        }
        return null;
    }
}

