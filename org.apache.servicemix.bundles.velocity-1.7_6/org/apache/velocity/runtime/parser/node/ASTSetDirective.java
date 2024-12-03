/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;

public class ASTSetDirective
extends SimpleNode {
    private String leftReference = "";
    private Node right = null;
    private ASTReference left = null;
    boolean logOnNull = false;
    private boolean allowNull = false;
    private boolean isInitialized;
    protected Info uberInfo;
    protected boolean strictRef = false;

    public ASTSetDirective(int id) {
        super(id);
    }

    public ASTSetDirective(Parser p, int id) {
        super(p, id);
    }

    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public synchronized Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        if (!this.isInitialized) {
            super.init(context, data);
            this.uberInfo = new Info(this.getTemplateName(), this.getLine(), this.getColumn());
            this.right = this.getRightHandSide();
            this.left = this.getLeftHandSide();
            this.logOnNull = this.rsvc.getBoolean("runtime.log.invalid.references", true);
            this.allowNull = this.rsvc.getBoolean("directive.set.null.allowed", false);
            this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
            if (this.strictRef) {
                this.allowNull = true;
            }
            this.leftReference = this.left.getFirstToken().image.substring(1);
            this.isInitialized = true;
        }
        return data;
    }

    public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException {
        Object value = this.right.value(context);
        if (!this.allowNull && value == null) {
            boolean doit;
            if (this.logOnNull && (doit = EventHandlerUtil.shouldLogOnNullSet(this.rsvc, context, this.left.literal(), this.right.literal())) && this.rsvc.getLog().isDebugEnabled()) {
                this.rsvc.getLog().debug("RHS of #set statement is null. Context will not be modified. " + Log.formatFileString(this));
            }
            String rightReference = null;
            if (this.right instanceof ASTExpression) {
                rightReference = ((ASTExpression)this.right).getLastToken().image;
            }
            EventHandlerUtil.invalidSetMethod(this.rsvc, context, this.leftReference, rightReference, this.uberInfo);
            return false;
        }
        if (value == null && !this.strictRef) {
            String rightReference = null;
            if (this.right instanceof ASTExpression) {
                rightReference = ((ASTExpression)this.right).getLastToken().image;
            }
            EventHandlerUtil.invalidSetMethod(this.rsvc, context, this.leftReference, rightReference, this.uberInfo);
            if (this.left.jjtGetNumChildren() == 0) {
                context.remove(this.leftReference);
            } else {
                this.left.setValue(context, null);
            }
            return false;
        }
        if (this.left.jjtGetNumChildren() == 0) {
            context.put(this.leftReference, value);
        } else {
            this.left.setValue(context, value);
        }
        return true;
    }

    private ASTReference getLeftHandSide() {
        return (ASTReference)this.jjtGetChild(0);
    }

    private Node getRightHandSide() {
        return this.jjtGetChild(1);
    }
}

