/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTTextblock
extends SimpleNode {
    public static final String START = "#[[";
    public static final String END = "]]#";
    private char[] ctext;

    public ASTTextblock(int id) {
        super(id);
    }

    public ASTTextblock(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        Token t = this.getFirstToken();
        String text = t.image;
        text = text.substring(START.length(), text.length() - END.length());
        this.ctext = text.toCharArray();
        return data;
    }

    public boolean render(InternalContextAdapter context, Writer writer) throws IOException {
        writer.write(this.ctext);
        return true;
    }
}

