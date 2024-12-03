/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node;

import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface NodeParser {
    public String getAttribute(String var1) throws IllegalStateException;

    public String getRequiredAttribute(String var1) throws IllegalStateException;

    public String getName();

    public boolean isClosed();

    public NodeParser getNextNode();

    public String getContentAsString() throws IllegalStateException;

    public Boolean getContentAsBoolean() throws IllegalStateException;

    public Date getContentAsDate() throws IllegalStateException;

    public BigInteger getContentAsBigInteger() throws IllegalStateException;

    public BigDecimal getContentAsBigDecimal() throws IllegalStateException;

    public void getContent(Writer var1) throws IllegalStateException;
}

