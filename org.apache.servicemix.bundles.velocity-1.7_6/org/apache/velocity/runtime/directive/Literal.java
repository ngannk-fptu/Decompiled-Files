/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

public class Literal
extends Directive {
    String literalText;

    public String getName() {
        return "literal";
    }

    public int getType() {
        return 1;
    }

    public boolean isScopeProvided() {
        return false;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.literalText = node.jjtGetChild(0).literal();
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException {
        writer.write(this.literalText);
        return true;
    }
}

