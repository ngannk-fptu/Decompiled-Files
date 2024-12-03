/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.IdentityReferenceInsertionHandler
 *  com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.runtime.resource.Resource
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareContext;
import com.atlassian.confluence.velocity.htmlsafe.ConfluenceHtmlAnnotationEscaper;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafeVelocityTemplate;
import com.atlassian.velocity.htmlsafe.IdentityReferenceInsertionHandler;
import com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy;
import java.util.Set;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfluenceHtmlEntityEncodingPolicy
implements ReferenceInsertionPolicy {
    private static final ReferenceInsertionEventHandler IDENTITY_INSERTION_HANDLER = new IdentityReferenceInsertionHandler();
    private static final ReferenceInsertionEventHandler HTML_SAFE_PROCESSING_HANDLER = new ConfluenceHtmlAnnotationEscaper();
    private final EncodingPolicy templateEncodingPolicy = template -> !template.isAutoEncodeDisabled();
    private static final Set<String> HTML_MIME_TYPES = Set.of("text/html", "application/xhtml+xml");

    public boolean shouldAutoEncode(Context context, @Nullable Template template) {
        if (template instanceof HtmlSafeVelocityTemplate) {
            return this.templateEncodingPolicy.shouldAutoEncode((HtmlSafeVelocityTemplate)template);
        }
        return this.getHtmlOutputMode(context) != HtmlOutputMode.NOT_HTML;
    }

    public boolean shouldAutoEncode(Context context) {
        Template template = this.extractTemplateOrNull(context);
        return this.shouldAutoEncode(context, template);
    }

    private @Nullable Template extractTemplateOrNull(Context context) {
        InternalContextAdapter ica;
        Resource resource;
        if (context instanceof InternalContextAdapter && (resource = (ica = (InternalContextAdapter)context).getCurrentResource()) instanceof Template) {
            return (Template)resource;
        }
        return null;
    }

    public ReferenceInsertionEventHandler getReferenceInsertionEventHandler(Context context) {
        return this.shouldAutoEncode(context) ? HTML_SAFE_PROCESSING_HANDLER : IDENTITY_INSERTION_HANDLER;
    }

    private HtmlOutputMode getHtmlOutputMode(Context context) {
        InternalContextAdapter ica;
        if (context instanceof OutputMimeTypeAwareContext) {
            OutputMimeTypeAwareContext outputAwareContext = (OutputMimeTypeAwareContext)context;
            if (HTML_MIME_TYPES.contains(outputAwareContext.getOutputMimeType())) {
                return HtmlOutputMode.IS_HTML;
            }
            return HtmlOutputMode.NOT_HTML;
        }
        if (context instanceof InternalContextAdapter && (ica = (InternalContextAdapter)context).getInternalUserContext() != ica) {
            return this.getHtmlOutputMode(ica.getInternalUserContext());
        }
        return HtmlOutputMode.UNSPECIFIED;
    }

    @FunctionalInterface
    static interface EncodingPolicy {
        public boolean shouldAutoEncode(HtmlSafeVelocityTemplate var1);
    }

    private static enum HtmlOutputMode {
        UNSPECIFIED,
        IS_HTML,
        NOT_HTML;

    }
}

