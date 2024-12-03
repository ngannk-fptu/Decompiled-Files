/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.directive.Directive
 *  org.apache.velocity.runtime.parser.node.Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderVelocityTemplateDirective
extends Directive {
    private static final Logger log = LoggerFactory.getLogger(RenderVelocityTemplateDirective.class);

    public String getName() {
        return "renderVelocityTemplate";
    }

    public int getType() {
        return 2;
    }

    public void init(RuntimeServices services, InternalContextAdapter adapter, Node node) {
        super.init(services, adapter, node);
        int numArgs = node.jjtGetNumChildren();
        if (numArgs != 1) {
            services.getLog().error((Object)"#renderVelocityTemplate error: You must pass the template contents.");
        }
    }

    public boolean render(InternalContextAdapter adapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String templateContent = (String)node.jjtGetChild(0).value(adapter);
        try {
            this.rsvc.evaluate(adapter.getInternalUserContext(), writer, "renderVelocityTemplate", templateContent);
        }
        catch (Exception e) {
            log.error("Unable to render template content", (Throwable)e);
            return false;
        }
        return true;
    }
}

