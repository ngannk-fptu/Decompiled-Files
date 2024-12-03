/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.DefaultErrorHandler;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorHandler;
import org.apache.sling.scripting.jsp.jasper.compiler.JavacErrorDetail;
import org.apache.sling.scripting.jsp.jasper.compiler.Localizer;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.xml.sax.SAXException;

public class ErrorDispatcher {
    private ErrorHandler errHandler = new DefaultErrorHandler();
    private boolean jspcMode = false;

    public ErrorDispatcher(boolean jspcMode) {
        this.jspcMode = jspcMode;
    }

    public void jspError(String errCode) throws JasperException {
        this.dispatch(null, errCode, null, null);
    }

    public void jspError(Mark where, String errCode) throws JasperException {
        this.dispatch(where, errCode, null, null);
    }

    public void jspError(Node n, String errCode) throws JasperException {
        this.dispatch(n.getStart(), errCode, null, null);
    }

    public void jspError(String errCode, String arg) throws JasperException {
        this.dispatch(null, errCode, new Object[]{arg}, null);
    }

    public void jspError(Mark where, String errCode, String arg) throws JasperException {
        this.dispatch(where, errCode, new Object[]{arg}, null);
    }

    public void jspError(Node n, String errCode, String arg) throws JasperException {
        this.dispatch(n.getStart(), errCode, new Object[]{arg}, null);
    }

    public void jspError(String errCode, String arg1, String arg2) throws JasperException {
        this.dispatch(null, errCode, new Object[]{arg1, arg2}, null);
    }

    public void jspError(String errCode, String arg1, String arg2, String arg3) throws JasperException {
        this.dispatch(null, errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void jspError(Mark where, String errCode, String arg1, String arg2) throws JasperException {
        this.dispatch(where, errCode, new Object[]{arg1, arg2}, null);
    }

    public void jspError(Mark where, String errCode, String arg1, String arg2, String arg3) throws JasperException {
        this.dispatch(where, errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void jspError(Node n, String errCode, String arg1, String arg2) throws JasperException {
        this.dispatch(n.getStart(), errCode, new Object[]{arg1, arg2}, null);
    }

    public void jspError(Node n, String errCode, String arg1, String arg2, String arg3) throws JasperException {
        this.dispatch(n.getStart(), errCode, new Object[]{arg1, arg2, arg3}, null);
    }

    public void jspError(Exception e) throws JasperException {
        this.dispatch(null, null, null, e);
    }

    public void jspError(String errCode, String arg, Exception e) throws JasperException {
        this.dispatch(null, errCode, new Object[]{arg}, e);
    }

    public void jspError(Node n, String errCode, String arg, Exception e) throws JasperException {
        this.dispatch(n.getStart(), errCode, new Object[]{arg}, e);
    }

    public static JavacErrorDetail[] parseJavacErrors(String errMsg, String fname, Node.Nodes page) throws JasperException, IOException {
        return ErrorDispatcher.parseJavacMessage(errMsg, fname, page);
    }

    public void javacError(JavacErrorDetail[] javacErrors) throws JasperException {
        this.errHandler.javacError(javacErrors);
    }

    public void javacError(String errorReport, Exception e) throws JasperException {
        this.errHandler.javacError(errorReport, e);
    }

    private void dispatch(Mark where, String errCode, Object[] args, Exception e) throws JasperException {
        String file = null;
        String errMsg = null;
        int line = -1;
        int column = -1;
        boolean hasLocation = false;
        if (errCode != null) {
            errMsg = Localizer.getMessage(errCode, args);
        } else if (e != null) {
            errMsg = e.getMessage();
        }
        if (where != null) {
            if (this.jspcMode) {
                try {
                    file = where.getURL().toString();
                }
                catch (MalformedURLException me) {
                    file = where.getFile();
                }
            } else {
                file = where.getFile();
            }
            line = where.getLineNumber();
            column = where.getColumnNumber();
            hasLocation = true;
        }
        Exception nestedEx = e;
        if (e instanceof SAXException && ((SAXException)e).getException() != null) {
            nestedEx = ((SAXException)e).getException();
        }
        if (hasLocation) {
            this.errHandler.jspError(file, line, column, errMsg, nestedEx);
        } else {
            this.errHandler.jspError(errMsg, nestedEx);
        }
    }

    private static JavacErrorDetail[] parseJavacMessage(String errMsg, String fname, Node.Nodes page) throws IOException, JasperException {
        ArrayList<JavacErrorDetail> errors = new ArrayList<JavacErrorDetail>();
        StringBuffer errMsgBuf = null;
        int lineNum = -1;
        JavacErrorDetail javacError = null;
        BufferedReader reader = new BufferedReader(new StringReader(errMsg));
        String line = null;
        while ((line = reader.readLine()) != null) {
            int beginColon = line.indexOf(58, 2);
            int endColon = line.indexOf(58, beginColon + 1);
            if (beginColon >= 0 && endColon >= 0) {
                if (javacError != null) {
                    errors.add(javacError);
                }
                String lineNumStr = line.substring(beginColon + 1, endColon);
                try {
                    lineNum = Integer.parseInt(lineNumStr);
                }
                catch (NumberFormatException e) {
                    lineNum = -1;
                }
                errMsgBuf = new StringBuffer();
                javacError = ErrorDispatcher.createJavacError(fname, page, errMsgBuf, lineNum);
            }
            if (errMsgBuf == null) continue;
            errMsgBuf.append(line);
            errMsgBuf.append("\n");
        }
        if (javacError != null) {
            errors.add(javacError);
        }
        reader.close();
        JavacErrorDetail[] errDetails = null;
        if (errors.size() > 0) {
            errDetails = new JavacErrorDetail[errors.size()];
            errors.toArray(errDetails);
        }
        return errDetails;
    }

    public static JavacErrorDetail createJavacError(String fname, Node.Nodes page, StringBuffer errMsgBuf, int lineNum) throws JasperException {
        return ErrorDispatcher.createJavacError(fname, page, errMsgBuf, lineNum, null);
    }

    public static JavacErrorDetail createJavacError(String fname, Node.Nodes page, StringBuffer errMsgBuf, int lineNum, JspCompilationContext ctxt) throws JasperException {
        JavacErrorDetail javacError = null;
        if (page != null) {
            ErrorVisitor errVisitor = new ErrorVisitor(lineNum);
            page.visit(errVisitor);
            Node errNode = errVisitor.getJspSourceNode();
            if (errNode != null && errNode.getStart() != null) {
                javacError = new JavacErrorDetail(fname, lineNum, errNode.getStart().getFile(), errNode.getStart().getLineNumber(), errMsgBuf, ctxt);
            }
        }
        if (javacError == null) {
            javacError = new JavacErrorDetail(fname, lineNum, errMsgBuf);
        }
        return javacError;
    }

    static class ErrorVisitor
    extends Node.Visitor {
        private int lineNum;
        Node found;

        public ErrorVisitor(int lineNum) {
            this.lineNum = lineNum;
        }

        @Override
        public void doVisit(Node n) throws JasperException {
            if (this.lineNum >= n.getBeginJavaLine() && this.lineNum < n.getEndJavaLine()) {
                this.found = n;
            }
        }

        public Node getJspSourceNode() {
            return this.found;
        }
    }
}

