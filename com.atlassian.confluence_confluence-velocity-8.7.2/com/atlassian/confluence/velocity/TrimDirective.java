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
package com.atlassian.confluence.velocity;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

public final class TrimDirective
extends Directive {
    public static final String NAME = "trim";

    public String getName() {
        return NAME;
    }

    public int getType() {
        return 1;
    }

    public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        StringWriter strWriter = new StringWriter();
        node.jjtGetChild(0).render(internalContextAdapter, (Writer)strWriter);
        writer.write(this.toTrimmedString(strWriter.getBuffer()));
        return true;
    }

    private String toTrimmedString(StringBuffer buffer) {
        int st;
        int len = buffer.length();
        for (st = 0; st < len && buffer.charAt(st) <= ' '; ++st) {
        }
        while (st < len && buffer.charAt(len - 1) <= ' ') {
            --len;
        }
        return buffer.substring(st, len);
    }
}

