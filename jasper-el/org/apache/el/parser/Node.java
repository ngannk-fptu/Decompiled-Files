/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 *  javax.el.MethodInfo
 *  javax.el.ValueReference
 */
package org.apache.el.parser;

import javax.el.ELException;
import javax.el.MethodInfo;
import javax.el.ValueReference;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.NodeVisitor;

public interface Node {
    public void jjtOpen();

    public void jjtClose();

    public void jjtSetParent(Node var1);

    public Node jjtGetParent();

    public void jjtAddChild(Node var1, int var2);

    public Node jjtGetChild(int var1);

    public int jjtGetNumChildren();

    public String getImage();

    public Object getValue(EvaluationContext var1) throws ELException;

    public void setValue(EvaluationContext var1, Object var2) throws ELException;

    public Class<?> getType(EvaluationContext var1) throws ELException;

    public boolean isReadOnly(EvaluationContext var1) throws ELException;

    public void accept(NodeVisitor var1) throws Exception;

    public MethodInfo getMethodInfo(EvaluationContext var1, Class<?>[] var2) throws ELException;

    public Object invoke(EvaluationContext var1, Class<?>[] var2, Object[] var3) throws ELException;

    public ValueReference getValueReference(EvaluationContext var1);

    public boolean isParametersProvided();
}

