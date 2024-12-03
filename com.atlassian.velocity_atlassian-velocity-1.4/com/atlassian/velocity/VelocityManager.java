/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.VelocityException
 */
package com.atlassian.velocity;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Map;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;

public interface VelocityManager {
    public String getBody(String var1, String var2, Map<String, Object> var3) throws VelocityException;

    public String getBody(String var1, String var2, String var3, Map<String, Object> var4) throws VelocityException;

    public String getEncodedBody(String var1, String var2, String var3, Map<String, Object> var4) throws VelocityException;

    public String getEncodedBody(String var1, String var2, String var3, String var4, Map<String, Object> var5) throws VelocityException;

    public String getEncodedBodyForContent(String var1, String var2, Map<String, Object> var3) throws VelocityException;

    public DateFormat getDateFormat();

    public String getEncodedBody(String var1, String var2, String var3, String var4, Context var5) throws VelocityException;

    public void writeEncodedBodyForContent(Writer var1, String var2, Context var3) throws VelocityException, IOException;

    public void writeEncodedBody(Writer var1, String var2, String var3, String var4, Context var5) throws VelocityException, IOException;
}

