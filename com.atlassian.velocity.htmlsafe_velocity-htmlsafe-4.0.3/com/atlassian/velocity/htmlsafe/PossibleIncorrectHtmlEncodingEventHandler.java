/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalWrapperContext
 *  org.apache.velocity.util.ContextAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.velocity.htmlsafe;

import com.atlassian.velocity.htmlsafe.HtmlRegExps;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.velocity.htmlsafe.HtmlSafeAnnotationUtils;
import com.atlassian.velocity.htmlsafe.introspection.AnnotatedReferenceHandler;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalWrapperContext;
import org.apache.velocity.util.ContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PossibleIncorrectHtmlEncodingEventHandler
extends AnnotatedReferenceHandler
implements ContextAware {
    private static final Logger log = LoggerFactory.getLogger(PossibleIncorrectHtmlEncodingEventHandler.class);
    private Context context;

    public static boolean isLoggingEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    protected Object annotatedValueInsert(String referenceName, Object value, Collection<Annotation> annotations) {
        if (value == null) {
            return value;
        }
        boolean isHtmlSafeValue = HtmlSafeAnnotationUtils.containsAnnotationOfType(annotations, HtmlSafe.class) || HtmlSafeAnnotationUtils.hasHtmlSafeToStringMethod(value);
        String stringValue = value.toString();
        if (!isHtmlSafeValue && (this.hasHtml(stringValue) || this.hasEncodedHtml(stringValue))) {
            log.info(referenceName + " in " + this.getCurrentTemplateName());
        }
        return value;
    }

    private boolean hasHtml(String string) {
        return HtmlRegExps.HTML_TAG_PATTERN.matcher(string).find();
    }

    private boolean hasEncodedHtml(String string) {
        return HtmlRegExps.HTML_ENTITY_PATTERN.matcher(string).find();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private String getCurrentTemplateName() {
        String templateName;
        if (this.context instanceof InternalWrapperContext) {
            InternalWrapperContext wrapper = (InternalWrapperContext)this.context;
            templateName = wrapper.getBaseContext().getCurrentTemplateName();
        } else {
            templateName = "unknown";
        }
        return templateName;
    }

    static {
        log.info("This log records Velocity template references that may have been incorrectly handled by the automatic HTML encoding system");
    }
}

