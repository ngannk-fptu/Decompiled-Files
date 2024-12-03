/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.accept.AbstractMappingContentNegotiationStrategy;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.accept.ServletPathExtensionContentNegotiationStrategy;
import org.springframework.web.context.ServletContextAware;

public class ContentNegotiationManagerFactoryBean
implements FactoryBean<ContentNegotiationManager>,
ServletContextAware,
InitializingBean {
    @Nullable
    private List<ContentNegotiationStrategy> strategies;
    private boolean favorPathExtension = true;
    private boolean favorParameter = false;
    private boolean ignoreAcceptHeader = false;
    private Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
    private boolean ignoreUnknownPathExtensions = true;
    @Nullable
    private Boolean useRegisteredExtensionsOnly;
    private String parameterName = "format";
    @Nullable
    private ContentNegotiationStrategy defaultNegotiationStrategy;
    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    @Nullable
    private ServletContext servletContext;

    public void setStrategies(@Nullable List<ContentNegotiationStrategy> strategies) {
        this.strategies = strategies != null ? new ArrayList<ContentNegotiationStrategy>(strategies) : null;
    }

    public void setFavorPathExtension(boolean favorPathExtension) {
        this.favorPathExtension = favorPathExtension;
    }

    public void setMediaTypes(Properties mediaTypes) {
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            mediaTypes.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(key, value) -> {
                String extension = ((String)key).toLowerCase(Locale.ENGLISH);
                MediaType mediaType = MediaType.valueOf((String)value);
                this.mediaTypes.put(extension, mediaType);
            }));
        }
    }

    public void addMediaType(String fileExtension, MediaType mediaType) {
        this.mediaTypes.put(fileExtension, mediaType);
    }

    public void addMediaTypes(@Nullable Map<String, MediaType> mediaTypes) {
        if (mediaTypes != null) {
            this.mediaTypes.putAll(mediaTypes);
        }
    }

    public void setIgnoreUnknownPathExtensions(boolean ignore) {
        this.ignoreUnknownPathExtensions = ignore;
    }

    @Deprecated
    public void setUseJaf(boolean useJaf) {
        this.setUseRegisteredExtensionsOnly(!useJaf);
    }

    public void setUseRegisteredExtensionsOnly(boolean useRegisteredExtensionsOnly) {
        this.useRegisteredExtensionsOnly = useRegisteredExtensionsOnly;
    }

    private boolean useRegisteredExtensionsOnly() {
        return this.useRegisteredExtensionsOnly != null && this.useRegisteredExtensionsOnly != false;
    }

    public void setFavorParameter(boolean favorParameter) {
        this.favorParameter = favorParameter;
    }

    public void setParameterName(String parameterName) {
        Assert.notNull((Object)parameterName, "parameterName is required");
        this.parameterName = parameterName;
    }

    public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
        this.ignoreAcceptHeader = ignoreAcceptHeader;
    }

    public void setDefaultContentType(MediaType contentType) {
        this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentType);
    }

    public void setDefaultContentTypes(List<MediaType> contentTypes) {
        this.defaultNegotiationStrategy = new FixedContentNegotiationStrategy(contentTypes);
    }

    public void setDefaultContentTypeStrategy(ContentNegotiationStrategy strategy) {
        this.defaultNegotiationStrategy = strategy;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.build();
    }

    public ContentNegotiationManager build() {
        ArrayList<ContentNegotiationStrategy> strategies = new ArrayList<ContentNegotiationStrategy>();
        if (this.strategies != null) {
            strategies.addAll(this.strategies);
        } else {
            AbstractMappingContentNegotiationStrategy strategy;
            if (this.favorPathExtension) {
                strategy = this.servletContext != null && !this.useRegisteredExtensionsOnly() ? new ServletPathExtensionContentNegotiationStrategy(this.servletContext, this.mediaTypes) : new PathExtensionContentNegotiationStrategy(this.mediaTypes);
                strategy.setIgnoreUnknownExtensions(this.ignoreUnknownPathExtensions);
                if (this.useRegisteredExtensionsOnly != null) {
                    strategy.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly);
                }
                strategies.add(strategy);
            }
            if (this.favorParameter) {
                strategy = new ParameterContentNegotiationStrategy(this.mediaTypes);
                ((ParameterContentNegotiationStrategy)strategy).setParameterName(this.parameterName);
                if (this.useRegisteredExtensionsOnly != null) {
                    strategy.setUseRegisteredExtensionsOnly(this.useRegisteredExtensionsOnly);
                } else {
                    strategy.setUseRegisteredExtensionsOnly(true);
                }
                strategies.add(strategy);
            }
            if (!this.ignoreAcceptHeader) {
                strategies.add(new HeaderContentNegotiationStrategy());
            }
            if (this.defaultNegotiationStrategy != null) {
                strategies.add(this.defaultNegotiationStrategy);
            }
        }
        this.contentNegotiationManager = new ContentNegotiationManager(strategies);
        return this.contentNegotiationManager;
    }

    @Override
    @Nullable
    public ContentNegotiationManager getObject() {
        return this.contentNegotiationManager;
    }

    @Override
    public Class<?> getObjectType() {
        return ContentNegotiationManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

