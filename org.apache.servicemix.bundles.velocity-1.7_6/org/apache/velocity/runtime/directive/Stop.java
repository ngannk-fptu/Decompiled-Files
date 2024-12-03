/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public class Stop
extends Directive {
    private static final StopCommand STOP_ALL = new StopCommand("StopCommand to exit merging");
    private boolean hasMessage = false;

    public String getName() {
        return "stop";
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
            throw new VelocityException("The #stop directive only accepts a single message parameter at " + Log.formatFileString(this));
        }
        this.hasMessage = kids == 1;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        if (!this.hasMessage) {
            throw STOP_ALL;
        }
        Object argument = node.jjtGetChild(0).value(context);
        throw new StopCommand(String.valueOf(argument));
    }
}

