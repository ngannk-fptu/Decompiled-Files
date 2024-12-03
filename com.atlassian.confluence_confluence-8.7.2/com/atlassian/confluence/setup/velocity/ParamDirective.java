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

import com.atlassian.confluence.setup.velocity.ApplyDecoratorDirective;
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

public final class ParamDirective
extends Directive {
    private static final Logger log = LoggerFactory.getLogger(ParamDirective.class);

    public String getName() {
        return "decoratorParam";
    }

    public int getType() {
        return 2;
    }

    public void init(RuntimeServices services, InternalContextAdapter adapter, Node node) {
        super.init(services, adapter, node);
        int numArgs = node.jjtGetNumChildren();
        if (numArgs != 2) {
            services.getLog().error((Object)"#decoratorParam error: You must specify a param name and value.");
        }
    }

    public boolean render(InternalContextAdapter adapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        ApplyDecoratorDirective.DirectiveStack stack = (ApplyDecoratorDirective.DirectiveStack)adapter.get("DirectiveStack");
        if (stack == null) {
            throw new ParseErrorException("#decoratorParam error: You must nest this directive within a #applyDecorator directive");
        }
        ApplyDecoratorDirective parent = stack.peek();
        if (parent == null) {
            log.error("#decoratorParam error: You must nest this directive within a #applyDecorator directive");
            return false;
        }
        String paramName = (String)node.jjtGetChild(0).value(adapter);
        Object paramValue = node.jjtGetChild(1).value(adapter);
        parent.addParameter(paramName, paramValue);
        return true;
    }
}

