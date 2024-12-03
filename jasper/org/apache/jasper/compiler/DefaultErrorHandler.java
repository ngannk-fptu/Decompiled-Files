/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ErrorHandler;
import org.apache.jasper.compiler.JavacErrorDetail;
import org.apache.jasper.compiler.Localizer;

class DefaultErrorHandler
implements ErrorHandler {
    DefaultErrorHandler() {
    }

    @Override
    public void jspError(String fname, int line, int column, String errMsg, Exception ex) throws JasperException {
        throw new JasperException(fname + " (" + Localizer.getMessage("jsp.error.location", Integer.toString(line), Integer.toString(column)) + ") " + errMsg, ex);
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
        StringBuilder buf = new StringBuilder();
        for (JavacErrorDetail detail : details) {
            if (detail.getJspBeginLineNumber() >= 0) {
                args = new Object[]{detail.getJspBeginLineNumber(), detail.getJspFileName()};
                buf.append(System.lineSeparator());
                buf.append(System.lineSeparator());
                buf.append(Localizer.getMessage("jsp.error.single.line.number", args));
                buf.append(System.lineSeparator());
                buf.append(detail.getErrorMessage());
                buf.append(System.lineSeparator());
                buf.append(detail.getJspExtract());
                continue;
            }
            args = new Object[]{detail.getJavaLineNumber(), detail.getJavaFileName()};
            buf.append(System.lineSeparator());
            buf.append(System.lineSeparator());
            buf.append(Localizer.getMessage("jsp.error.java.line.number", args));
            buf.append(System.lineSeparator());
            buf.append(detail.getErrorMessage());
        }
        buf.append(System.lineSeparator());
        buf.append(System.lineSeparator());
        buf.append("Stacktrace:");
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile") + ": " + buf);
    }

    @Override
    public void javacError(String errorReport, Exception exception) throws JasperException {
        throw new JasperException(Localizer.getMessage("jsp.error.unable.compile"), exception);
    }
}

