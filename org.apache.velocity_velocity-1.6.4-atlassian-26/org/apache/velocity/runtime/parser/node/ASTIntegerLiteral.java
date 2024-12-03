/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.math.BigInteger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTIntegerLiteral
extends SimpleNode {
    private Number value = null;

    public ASTIntegerLiteral(int id) {
        super(id);
    }

    public ASTIntegerLiteral(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        String str = this.getFirstToken().image;
        try {
            this.value = new Integer(str);
        }
        catch (NumberFormatException E1) {
            try {
                this.value = new Long(str);
            }
            catch (NumberFormatException E2) {
                this.value = new BigInteger(str);
            }
        }
        return data;
    }

    @Override
    public Object value(InternalContextAdapter context) {
        return this.value;
    }
}

