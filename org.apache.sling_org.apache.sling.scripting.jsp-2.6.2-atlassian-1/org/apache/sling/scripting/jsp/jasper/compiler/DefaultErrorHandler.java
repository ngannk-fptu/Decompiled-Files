/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorHandler;
import org.apache.sling.scripting.jsp.jasper.compiler.JavacErrorDetail;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;

class DefaultErrorHandler
implements ErrorHandler {
    DefaultErrorHandler() {
    }

    @Override
    public void jspError(String fname, int line, int column, String errMsg, Exception ex) throws JasperException {
        throw new JasperException(fname + "(" + line + "," + column + ") " + errMsg, ex);
    }

    @Override
    public void jspError(String errMsg, Exception ex) throws JasperException {
        throw new JasperException(errMsg, ex);
    }

    @Override
    public void javacError(JavacErrorDetail[] details) throws JasperException {
        if (details == null) {
            return;
        }
        Object[] args = null;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < details.length; ++i) {
            if (details[i].getJspBeginLineNumber() >= 0) {
                args = new Object[]{new Integer(details[i].getJspBeginLineNumber()), details[i].getJspFileName()};
                buf.append("\n\n");
                buf.append(Localizer.getMessage("jsp.error.single.line.number", args));
                buf.append("\n");
                buf.append(details[i].getErrorMessage());
                buf.append("\n");
                buf.append(details[i].getJspExtract());
                continue;
            }
            args = new Object[]{new Integer(details[i].getJavaLineNumber())};
            buf.append("\n\n");
            buf.append(Localizer.getMessage("jsp.error.java.line.number", args));
            buf.append("\n");
            buf.append(details[i].getErrorMessage());
        }
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile") + ": " + buf);
    }

    @Override
    public void javacError(String errorReport, Exception exception) throws JasperException {
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"), exception);
    }
}

