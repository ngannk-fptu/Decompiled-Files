/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.directive.Directive
 *  org.apache.velocity.runtime.parser.node.Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkipLinkDirective
extends Directive {
    private static final Logger log = LoggerFactory.getLogger(SkipLinkDirective.class);

    public String getName() {
        return "skiplink";
    }

    public int getType() {
        return 1;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String sectionName = this.getSectionName(context, node);
        String startLabel = this.getStartLabel(context, node);
        String endLabel = this.getEndLabel(context, node);
        String body = this.getBodyContent(context, node);
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("sectionName", (Object)sectionName);
        velocityContext.put("startLabel", (Object)startLabel);
        velocityContext.put("endLabel", (Object)endLabel);
        velocityContext.put("body", (Object)body);
        try {
            VelocityUtils.renderTemplateWithoutSwallowingErrors("/includes/skiplinked-section.vm", (Context)velocityContext, writer);
        }
        catch (Exception e) {
            log.error("Error rendering skip link section. Ignoring.", (Throwable)e);
            writer.write(body);
        }
        return true;
    }

    private String getSectionName(InternalContextAdapter context, Node node) {
        return String.valueOf(node.jjtGetChild(SkipLinkParameters.SECTION_NAME.getIndex()).value(context));
    }

    private String getStartLabel(InternalContextAdapter context, Node node) {
        return String.valueOf(node.jjtGetChild(SkipLinkParameters.START_LABEL.getIndex()).value(context));
    }

    private String getEndLabel(InternalContextAdapter context, Node node) {
        return String.valueOf(node.jjtGetChild(SkipLinkParameters.END_LABEL.getIndex()).value(context));
    }

    private String getBodyContent(InternalContextAdapter context, Node node) throws IOException {
        StringWriter blockContent = new StringWriter();
        node.jjtGetChild(SkipLinkParameters.BODY.getIndex()).render(context, (Writer)blockContent);
        return blockContent.toString();
    }

    private static enum SkipLinkParameters {
        SECTION_NAME(0),
        START_LABEL(1),
        END_LABEL(2),
        BODY(3);

        private int paramIndex;

        private SkipLinkParameters(int i) {
            this.paramIndex = i;
        }

        public boolean isIndex(int i) {
            return i == this.paramIndex;
        }

        public int getIndex() {
            return this.paramIndex;
        }
    }
}

