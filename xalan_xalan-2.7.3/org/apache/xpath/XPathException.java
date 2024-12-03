/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.w3c.dom.Node;

public class XPathException
extends TransformerException {
    static final long serialVersionUID = 4263549717619045963L;
    Object m_styleNode = null;
    protected Exception m_exception;

    public Object getStylesheetNode() {
        return this.m_styleNode;
    }

    public void setStylesheetNode(Object styleNode) {
        this.m_styleNode = styleNode;
    }

    public XPathException(String message, ExpressionNode ex) {
        super(message);
        this.setLocator(ex);
        this.setStylesheetNode(this.getStylesheetNode(ex));
    }

    public XPathException(String message) {
        super(message);
    }

    public Node getStylesheetNode(ExpressionNode ex) {
        ExpressionNode owner = this.getExpressionOwner(ex);
        if (null != owner && owner instanceof Node) {
            return (Node)((Object)owner);
        }
        return null;
    }

    protected ExpressionNode getExpressionOwner(ExpressionNode ex) {
        ExpressionNode parent;
        for (parent = ex.exprGetParent(); null != parent && parent instanceof Expression; parent = parent.exprGetParent()) {
        }
        return parent;
    }

    public XPathException(String message, Object styleNode) {
        super(message);
        this.m_styleNode = styleNode;
    }

    public XPathException(String message, Node styleNode, Exception e) {
        super(message);
        this.m_styleNode = styleNode;
        this.m_exception = e;
    }

    public XPathException(String message, Exception e) {
        super(message);
        this.m_exception = e;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (s == null) {
            s = System.err;
        }
        try {
            super.printStackTrace(s);
        }
        catch (Exception exception) {
            // empty catch block
        }
        Throwable exception = this.m_exception;
        for (int i = 0; i < 10 && null != exception; ++i) {
            s.println("---------");
            exception.printStackTrace(s);
            if (exception instanceof TransformerException) {
                Exception prev = exception;
                TransformerException se = (TransformerException)exception;
                if (prev != (exception = se.getException())) continue;
                break;
            }
            exception = null;
        }
    }

    @Override
    public String getMessage() {
        String lastMessage = super.getMessage();
        Throwable exception = this.m_exception;
        while (null != exception) {
            String nextMessage = exception.getMessage();
            if (null != nextMessage) {
                lastMessage = nextMessage;
            }
            if (exception instanceof TransformerException) {
                Exception prev = exception;
                TransformerException se = (TransformerException)exception;
                if (prev != (exception = se.getException())) continue;
                break;
            }
            exception = null;
        }
        return null != lastMessage ? lastMessage : "";
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (s == null) {
            s = new PrintWriter(System.err);
        }
        try {
            super.printStackTrace(s);
        }
        catch (Exception exception) {
            // empty catch block
        }
        boolean isJdk14OrHigher = false;
        try {
            Throwable.class.getMethod("getCause", null);
            isJdk14OrHigher = true;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (!isJdk14OrHigher) {
            Throwable exception = this.m_exception;
            for (int i = 0; i < 10 && null != exception; ++i) {
                s.println("---------");
                try {
                    exception.printStackTrace(s);
                }
                catch (Exception e) {
                    s.println("Could not print stack trace...");
                }
                if (exception instanceof TransformerException) {
                    Exception prev = exception;
                    TransformerException se = (TransformerException)exception;
                    if (prev != (exception = se.getException())) continue;
                    exception = null;
                    break;
                }
                exception = null;
            }
        }
    }

    @Override
    public Throwable getException() {
        return this.m_exception;
    }
}

