/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class ASTStringLiteral
extends SimpleNode {
    private boolean interpolate = true;
    private SimpleNode nodeTree = null;
    private String image = "";
    private String interpolateimage = "";
    private boolean containsLineComment;

    public ASTStringLiteral(int id) {
        super(id);
    }

    public ASTStringLiteral(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.interpolate = this.rsvc.getBoolean("runtime.interpolate.string.literals", true) && this.getFirstToken().image.startsWith("\"") && (this.getFirstToken().image.indexOf(36) != -1 || this.getFirstToken().image.indexOf(35) != -1);
        this.image = this.getFirstToken().image.substring(1, this.getFirstToken().image.length() - 1);
        if (this.getFirstToken().image.startsWith("\"")) {
            this.image = ASTStringLiteral.unescape(this.image);
        }
        this.containsLineComment = this.image.indexOf("##") != -1;
        this.interpolateimage = !this.containsLineComment ? this.image + " " : this.image;
        if (this.interpolate) {
            StringReader br = new StringReader(this.interpolateimage);
            String templateName = context != null ? context.getCurrentTemplateName() : "StringLiteral";
            try {
                this.nodeTree = this.rsvc.parse(br, templateName, false);
            }
            catch (ParseException e) {
                String msg = "Failed to parse String literal at " + Log.formatFileString(templateName, this.getLine(), this.getColumn());
                throw new TemplateInitException(msg, e, templateName, this.getColumn(), this.getLine());
            }
            this.adjTokenLineNums(this.nodeTree);
            this.nodeTree.init(context, this.rsvc);
        }
        return data;
    }

    public void adjTokenLineNums(Node node) {
        Token tok = node.getFirstToken();
        while (tok != null && tok != node.getLastToken()) {
            if (tok.beginLine == 1) {
                tok.beginColumn += this.getColumn();
            }
            if (tok.endLine == 1) {
                tok.endColumn += this.getColumn();
            }
            tok.beginLine += this.getLine() - 1;
            tok.endLine += this.getLine() - 1;
            tok = tok.next;
        }
    }

    public static String unescape(String string) {
        int u = string.indexOf("\\u");
        if (u < 0) {
            return string;
        }
        StrBuilder result = new StrBuilder();
        int lastCopied = 0;
        do {
            result.append(string.substring(lastCopied, u));
            char c = (char)Integer.parseInt(string.substring(u + 2, u + 6), 16);
            result.append(c);
        } while ((u = string.indexOf("\\u", lastCopied = u + 6)) >= 0);
        result.append(string.substring(lastCopied));
        return result.toString();
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isConstant() {
        return !this.interpolate;
    }

    @Override
    public Object value(InternalContextAdapter context) {
        if (this.interpolate) {
            try {
                StringWriter writer = new StringWriter();
                this.nodeTree.render(context, writer);
                String ret = writer.toString();
                if (!this.containsLineComment && ret.length() > 0) {
                    return ret.substring(0, ret.length() - 1);
                }
                return ret;
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (IOException e) {
                String msg = "Error in interpolating string literal";
                this.log.error(msg, e);
                throw new VelocityException(msg, e);
            }
        }
        return this.image;
    }
}

