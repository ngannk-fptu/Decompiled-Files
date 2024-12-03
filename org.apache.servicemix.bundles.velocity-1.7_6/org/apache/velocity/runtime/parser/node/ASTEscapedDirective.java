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

public class ASTEscapedDirective
extends SimpleNode {
    public ASTEscapedDirective(int id) {
        super(id);
    }

    public ASTEscapedDirective(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean render(InternalContextAdapter context, Writer writer) throws IOException {
        writer.write(this.getFirstToken().image);
        return true;
    }
}

