/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.directive.Parse
 *  org.apache.velocity.runtime.parser.node.Node
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Parse;
import org.apache.velocity.runtime.parser.node.Node;

public class ProfilingParseDirective
extends Parse {
    public boolean render(InternalContextAdapter adapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        String templateName = this.getTemplateName(adapter, node);
        try (Ticker ignored = Timers.start((String)("Parse: " + templateName));){
            boolean bl = super.render(adapter, writer, node);
            return bl;
        }
    }

    private String getTemplateName(InternalContextAdapter adapter, Node node) {
        if (node.jjtGetChild(0) == null) {
            return null;
        }
        Object value = node.jjtGetChild(0).value(adapter);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}

