/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.upm.transformers.template;

import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TransformerUrlBuilder;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.transformer.WebResourceTransformerFactory;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.upm.transformers.template.UnderscoreTemplateRenderer;
import com.atlassian.upm.transformers.webresource.UrlReadingWebResourceUrlBuilder;
import java.util.Objects;

public class UrlReadingUnderscoreTemplateWebResourceTransformer
implements WebResourceTransformerFactory {
    private final UnderscoreTemplateRenderer underscoreTemplateRenderer;
    private final UrlReadingWebResourceUrlBuilder urlReadingWebResourceUrlBuilder;

    public UrlReadingUnderscoreTemplateWebResourceTransformer(UnderscoreTemplateRenderer underscoreTemplateRenderer, UrlReadingWebResourceUrlBuilder urlReadingWebResourceUrlBuilder) {
        this.underscoreTemplateRenderer = Objects.requireNonNull(underscoreTemplateRenderer);
        this.urlReadingWebResourceUrlBuilder = Objects.requireNonNull(urlReadingWebResourceUrlBuilder);
    }

    public TransformerUrlBuilder makeUrlBuilder(TransformerParameters parameters) {
        return new UnderscoreTemplateUrlBuilder();
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters parameters) {
        return new UnderscoreTemplateResourceTransformer();
    }

    private final class UnderscoreTemplateResourceTransformer
    implements UrlReadingWebResourceTransformer {
        private UnderscoreTemplateResourceTransformer() {
        }

        public DownloadableResource transform(final TransformableResource transformableResource, QueryParams params) {
            return new CharSequenceDownloadableResource(transformableResource.nextResource()){

                protected CharSequence transform(CharSequence templateContent) {
                    return UrlReadingUnderscoreTemplateWebResourceTransformer.this.underscoreTemplateRenderer.renderUnderscoreTemplate(transformableResource.location().getLocation(), templateContent);
                }
            };
        }
    }

    private final class UnderscoreTemplateUrlBuilder
    implements TransformerUrlBuilder {
        private UnderscoreTemplateUrlBuilder() {
        }

        public void addToUrl(UrlBuilder urlBuilder) {
            UrlReadingUnderscoreTemplateWebResourceTransformer.this.urlReadingWebResourceUrlBuilder.build(urlBuilder);
        }
    }
}

