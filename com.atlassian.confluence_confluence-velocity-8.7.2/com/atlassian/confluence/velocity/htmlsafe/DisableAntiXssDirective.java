/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.directive.Directive
 *  org.apache.velocity.runtime.parser.node.Node
 */
package com.atlassian.confluence.velocity.htmlsafe;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

public final class DisableAntiXssDirective
extends Directive {
    public static final String NAME = "disableAntiXss";

    public String getName() {
        return NAME;
    }

    public int getType() {
        return 2;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        return true;
    }
}

