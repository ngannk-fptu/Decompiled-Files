/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.ParserVisitor;

public interface Node {
    public void jjtOpen();

    public void jjtClose();

    public void jjtSetParent(Node var1);

    public Node jjtGetParent();

    public void jjtAddChild(Node var1, int var2);

    public Node jjtGetChild(int var1);

    public int jjtGetNumChildren();

    public Object jjtAccept(ParserVisitor var1, Object var2);

    public Object childrenAccept(ParserVisitor var1, Object var2);

    public Token getFirstToken();

    public Token getLastToken();

    public int getType();

    public Object init(InternalContextAdapter var1, Object var2) throws TemplateInitException;

    public boolean evaluate(InternalContextAdapter var1) throws MethodInvocationException;

    public Object value(InternalContextAdapter var1) throws MethodInvocationException;

    public boolean render(InternalContextAdapter var1, Writer var2) throws IOException, MethodInvocationException, ParseErrorException, ResourceNotFoundException;

    public Object execute(Object var1, InternalContextAdapter var2) throws MethodInvocationException;

    public void setInfo(int var1);

    public int getInfo();

    public String literal();

    public void setInvalid();

    public boolean isInvalid();

    public int getLine();

    public int getColumn();

    public String getTemplateName();
}

