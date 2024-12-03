/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.runtime.resource.Resource
 *  org.apache.velocity.util.ContextAware
 */
package com.atlassian.velocity.htmlsafe.event.referenceinsertion;

import com.atlassian.velocity.htmlsafe.HtmlAnnotationEscaper;
import com.atlassian.velocity.htmlsafe.directive.DefaultDirectiveChecker;
import com.atlassian.velocity.htmlsafe.directive.DirectiveChecker;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.util.ContextAware;

public class EnableHtmlEscapingDirectiveHandler
implements ReferenceInsertionEventHandler,
ContextAware {
    private Context context;
    private DirectiveChecker directiveChecker = new DefaultDirectiveChecker();
    private final ReferenceInsertionEventHandler htmlEscapingHandler;

    public EnableHtmlEscapingDirectiveHandler() {
        this(new HtmlAnnotationEscaper());
    }

    public EnableHtmlEscapingDirectiveHandler(ReferenceInsertionEventHandler htmlEscapingHandler) {
        this.htmlEscapingHandler = htmlEscapingHandler;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setDirectiveChecker(DirectiveChecker directiveChecker) {
        this.directiveChecker = directiveChecker;
    }

    public Object referenceInsert(String reference, Object value) {
        if (this.isEscapingEnabled()) {
            return this.htmlEscapingHandler.referenceInsert(reference, value);
        }
        return value;
    }

    private boolean isEscapingEnabled() {
        InternalContextAdapter ica;
        Resource resource;
        if (this.context instanceof InternalContextAdapter && (resource = (ica = (InternalContextAdapter)this.context).getCurrentResource()) instanceof Template) {
            return this.directiveChecker.isPresent("enable_html_escaping", (Template)resource);
        }
        return false;
    }
}

