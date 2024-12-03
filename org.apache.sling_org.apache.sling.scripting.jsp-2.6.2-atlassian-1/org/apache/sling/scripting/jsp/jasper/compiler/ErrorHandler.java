/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.JavacErrorDetail;

public interface ErrorHandler {
    public void jspError(String var1, int var2, int var3, String var4, Exception var5) throws JasperException;

    public void jspError(String var1, Exception var2) throws JasperException;

    public void javacError(JavacErrorDetail[] var1) throws JasperException;

    public void javacError(String var1, Exception var2) throws JasperException;
}

