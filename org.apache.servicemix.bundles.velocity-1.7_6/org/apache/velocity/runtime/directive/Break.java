/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.Scope;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public class Break
extends Directive {
    private boolean scoped = false;

    public String getName() {
        return "break";
    }

    public int getType() {
        return 2;
    }

    public boolean isScopeProvided() {
        return false;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) {
        super.init(rs, context, node);
        int kids = node.jjtGetNumChildren();
        if (kids > 1) {
            throw new VelocityException("The #stop directive only accepts a single scope object at " + Log.formatFileString(this));
        }
        this.scoped = kids == 1;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        if (!this.scoped) {
            throw new StopCommand();
        }
        Object argument = node.jjtGetChild(0).value(context);
        if (!(argument instanceof Scope)) {
            throw new VelocityException(node.jjtGetChild(0).literal() + " is not a valid " + Scope.class.getName() + " instance at " + Log.formatFileString(this));
        }
        ((Scope)argument).stop();
        return false;
    }
}

