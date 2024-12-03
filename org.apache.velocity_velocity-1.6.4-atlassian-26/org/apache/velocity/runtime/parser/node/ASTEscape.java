/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTEscape
extends SimpleNode {
    public String val;
    private char[] ctext;

    public ASTEscape(int id) {
        super(id);
    }

    public ASTEscape(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) {
        this.ctext = this.val.toCharArray();
        return data;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer) throws IOException {
        if (context.getAllowRendering()) {
            writer.write(this.ctext);
        }
        return true;
    }
}

