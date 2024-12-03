/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Block;
import org.apache.velocity.runtime.directive.RuntimeMacro;
import org.apache.velocity.runtime.parser.node.Node;

public class BlockMacro
extends Block {
    private String name;
    private RuntimeMacro macro;

    public BlockMacro(String name) {
        this.name = name;
    }

    public String getName() {
        return this.key;
    }

    public String getScopeName() {
        return this.name;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.key = this.rsvc.getString("velocimacro.body.reference", "bodyContent");
        this.maxDepth = rs.getInt("velocimacro.max.depth");
        this.macro = new RuntimeMacro(this.name);
        this.macro.setLocation(this.getLine(), this.getColumn(), this.getTemplateName());
        this.macro.init(rs, context, node);
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        return this.macro.render(context, writer, node, new Block.Reference(context, this));
    }
}

