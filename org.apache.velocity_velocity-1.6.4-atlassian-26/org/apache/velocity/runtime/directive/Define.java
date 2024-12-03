/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;

public class Define
extends Directive {
    private String key;
    private Node block;
    private Log log;
    private int maxDepth;
    private String definingTemplate;

    @Override
    public String getName() {
        return "define";
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.log = rs.getLog();
        this.maxDepth = rs.getInt("directive.define.max.depth", 2);
        this.key = node.jjtGetChild((int)0).getFirstToken().image.substring(1);
        this.block = node.jjtGetChild(1);
        this.definingTemplate = context.getCurrentTemplateName();
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) {
        context.put(this.key, new Block(context, this));
        return true;
    }

    protected String id(InternalContextAdapter context) {
        StrBuilder str = new StrBuilder(100).append("block $").append(this.key).append(" (defined in ").append(this.definingTemplate).append(" [line ").append(this.getLine()).append(", column ").append(this.getColumn()).append("])");
        if (!context.getCurrentTemplateName().equals(this.definingTemplate)) {
            str.append(" used in ").append(context.getCurrentTemplateName());
        }
        return str.toString();
    }

    public static class Block
    implements Renderable {
        private InternalContextAdapter context;
        private Define parent;
        private int depth;

        public Block(InternalContextAdapter context, Define parent) {
            this.context = context;
            this.parent = parent;
        }

        @Override
        public boolean render(InternalContextAdapter context, Writer writer) {
            try {
                ++this.depth;
                if (this.depth > this.parent.maxDepth) {
                    this.parent.log.debug("Max recursion depth reached for " + this.parent.id(context));
                    --this.depth;
                    return false;
                }
                this.parent.block.render(context, writer);
                --this.depth;
                return true;
            }
            catch (IOException e) {
                String msg = "Failed to render " + this.parent.id(context) + " to writer";
                this.parent.log.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            catch (VelocityException ve) {
                String msg = "Failed to render " + this.parent.id(context) + " due to " + ve;
                this.parent.log.error(msg, ve);
                throw ve;
            }
        }

        public String toString() {
            StringWriter stringwriter = new StringWriter();
            if (this.render(this.context, stringwriter)) {
                return ((Object)stringwriter).toString();
            }
            return null;
        }
    }
}

