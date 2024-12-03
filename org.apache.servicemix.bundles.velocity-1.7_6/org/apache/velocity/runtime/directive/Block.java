/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.text.StrBuilder
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public abstract class Block
extends Directive {
    protected Node block;
    protected Log log;
    protected int maxDepth;
    protected String key;

    public int getType() {
        return 1;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.log = rs.getLog();
        this.block = node.jjtGetChild(node.jjtGetNumChildren() - 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean render(InternalContextAdapter context, Writer writer) {
        this.preRender(context);
        try {
            boolean bl = this.block.render(context, writer);
            return bl;
        }
        catch (IOException e) {
            String msg = "Failed to render " + this.id(context) + " to writer " + " at " + Log.formatFileString(this);
            this.log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        catch (StopCommand stop) {
            if (!stop.isFor(this)) {
                throw stop;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            this.postRender(context);
        }
    }

    protected String id(InternalContextAdapter context) {
        StrBuilder str = new StrBuilder(100).append("block $").append(this.key);
        if (!context.getCurrentTemplateName().equals(this.getTemplateName())) {
            str.append(" used in ").append(context.getCurrentTemplateName());
        }
        return str.toString();
    }

    public static class Reference
    implements Renderable {
        private InternalContextAdapter context;
        private Block parent;
        private int depth;

        public Reference(InternalContextAdapter context, Block parent) {
            this.context = context;
            this.parent = parent;
        }

        public boolean render(InternalContextAdapter context, Writer writer) {
            ++this.depth;
            if (this.depth > this.parent.maxDepth) {
                this.parent.log.debug("Max recursion depth reached for " + this.parent.id(context) + " at " + Log.formatFileString(this.parent));
                --this.depth;
                return false;
            }
            this.parent.render(context, writer);
            --this.depth;
            return true;
        }

        public String toString() {
            StringWriter writer = new StringWriter();
            if (this.render(this.context, writer)) {
                return ((Object)writer).toString();
            }
            return null;
        }
    }
}

