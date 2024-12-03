/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Block;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public class Define
extends Block {
    public String getName() {
        return "define";
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        if (node.jjtGetNumChildren() != 2) {
            throw new VelocityException("parameter missing: block name at " + Log.formatFileString(this));
        }
        this.key = node.jjtGetChild((int)0).getFirstToken().image.substring(1);
        this.maxDepth = rs.getInt("directive.define.max.depth", 2);
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        context.put(this.key, new Block.Reference(context, this));
        return true;
    }
}

