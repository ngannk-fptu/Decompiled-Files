/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface NodeCreator {
    public NodeCreator addNode(String var1);

    public NodeCreator closeEntity();

    public NodeCreator setContentAsDate(Date var1);

    public NodeCreator setContentAsBigInteger(BigInteger var1);

    public NodeCreator setContentAsBigDecimal(BigDecimal var1);

    public NodeCreator setContentAsString(String var1);

    public NodeCreator setContentAsBoolean(Boolean var1);

    public NodeCreator setContentAsBinary(byte[] var1);

    public NodeCreator setContent(Reader var1) throws IOException;

    public NodeCreator addAttribute(String var1, String var2);
}

