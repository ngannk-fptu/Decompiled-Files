/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;

public interface Node {
    public void jjtOpen();

    public void jjtClose();

    public void jjtSetParent(Node var1);

    public Node jjtGetParent();

    public void jjtAddChild(Node var1, int var2);

    public Node jjtGetChild(int var1);

    public int jjtGetNumChildren();

    public Object jjtAccept(JCRSQLParserVisitor var1, Object var2);
}

