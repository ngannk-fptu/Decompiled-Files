/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.InputBase;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.resource.ContentResource;

public class Include
extends InputBase {
    private String outputMsgStart = "";
    private String outputMsgEnd = "";

    public String getName() {
        return "include";
    }

    public int getType() {
        return 2;
    }

    public boolean isScopeProvided() {
        return false;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.outputMsgStart = this.rsvc.getString("directive.include.output.errormsg.start");
        this.outputMsgStart = this.outputMsgStart + " ";
        this.outputMsgEnd = this.rsvc.getString("directive.include.output.errormsg.end");
        this.outputMsgEnd = " " + this.outputMsgEnd;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, ResourceNotFoundException {
        int argCount = node.jjtGetNumChildren();
        for (int i = 0; i < argCount; ++i) {
            Node n = node.jjtGetChild(i);
            if (n.getType() == 8 || n.getType() == 18) {
                if (this.renderOutput(n, context, writer)) continue;
                this.outputErrorToStream(writer, "error with arg " + i + " please see log.");
                continue;
            }
            String msg = "invalid #include() argument '" + n.toString() + "' at " + Log.formatFileString(this);
            this.rsvc.getLog().error(msg);
            this.outputErrorToStream(writer, "error with arg " + i + " please see log.");
            throw new VelocityException(msg);
        }
        return true;
    }

    private boolean renderOutput(Node node, InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ResourceNotFoundException {
        if (node == null) {
            this.rsvc.getLog().error("#include() null argument");
            return false;
        }
        Object value = node.value(context);
        if (value == null) {
            this.rsvc.getLog().error("#include() null argument");
            return false;
        }
        String sourcearg = value.toString();
        String arg = EventHandlerUtil.includeEvent(this.rsvc, context, sourcearg, context.getCurrentTemplateName(), this.getName());
        boolean blockinput = false;
        if (arg == null) {
            blockinput = true;
        }
        ContentResource resource = null;
        try {
            if (!blockinput) {
                resource = this.rsvc.getContent(arg, this.getInputEncoding(context));
            }
        }
        catch (ResourceNotFoundException rnfe) {
            this.rsvc.getLog().error("#include(): cannot find resource '" + arg + "', called at " + Log.formatFileString(this));
            throw rnfe;
        }
        catch (RuntimeException e) {
            this.rsvc.getLog().error("#include(): arg = '" + arg + "', called at " + Log.formatFileString(this));
            throw e;
        }
        catch (Exception e) {
            String msg = "#include(): arg = '" + arg + "', called at " + Log.formatFileString(this);
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
        if (blockinput) {
            return true;
        }
        if (resource == null) {
            return false;
        }
        writer.write((String)resource.getData());
        return true;
    }

    private void outputErrorToStream(Writer writer, String msg) throws IOException {
        if (this.outputMsgStart != null && this.outputMsgEnd != null) {
            writer.write(this.outputMsgStart);
            writer.write(msg);
            writer.write(this.outputMsgEnd);
        }
    }
}

