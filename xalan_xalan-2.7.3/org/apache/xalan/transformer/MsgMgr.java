/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.transformer;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.transformer.TransformerImpl;
import org.w3c.dom.Node;

public class MsgMgr {
    private TransformerImpl m_transformer;

    public MsgMgr(TransformerImpl transformer) {
        this.m_transformer = transformer;
    }

    public void message(SourceLocator srcLctr, String msg, boolean terminate) throws TransformerException {
        ErrorListener errHandler = this.m_transformer.getErrorListener();
        if (null != errHandler) {
            errHandler.warning(new TransformerException(msg, srcLctr));
        } else {
            if (terminate) {
                throw new TransformerException(msg, srcLctr);
            }
            System.out.println(msg);
        }
    }

    public void warn(SourceLocator srcLctr, String msg) throws TransformerException {
        this.warn(srcLctr, null, null, msg, null);
    }

    public void warn(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException {
        this.warn(srcLctr, null, null, msg, args);
    }

    public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg) throws TransformerException {
        this.warn(srcLctr, styleNode, sourceNode, msg, null);
    }

    public void warn(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object[] args) throws TransformerException {
        String formattedMsg = XSLMessages.createWarning(msg, args);
        ErrorListener errHandler = this.m_transformer.getErrorListener();
        if (null != errHandler) {
            errHandler.warning(new TransformerException(formattedMsg, srcLctr));
        } else {
            System.out.println(formattedMsg);
        }
    }

    public void error(SourceLocator srcLctr, String msg) throws TransformerException {
        this.error(srcLctr, null, null, msg, null);
    }

    public void error(SourceLocator srcLctr, String msg, Object[] args) throws TransformerException {
        this.error(srcLctr, null, null, msg, args);
    }

    public void error(SourceLocator srcLctr, String msg, Exception e) throws TransformerException {
        this.error(srcLctr, msg, null, e);
    }

    public void error(SourceLocator srcLctr, String msg, Object[] args, Exception e) throws TransformerException {
        String formattedMsg = XSLMessages.createMessage(msg, args);
        ErrorListener errHandler = this.m_transformer.getErrorListener();
        if (null == errHandler) {
            throw new TransformerException(formattedMsg, srcLctr);
        }
        errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    }

    public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg) throws TransformerException {
        this.error(srcLctr, styleNode, sourceNode, msg, null);
    }

    public void error(SourceLocator srcLctr, Node styleNode, Node sourceNode, String msg, Object[] args) throws TransformerException {
        String formattedMsg = XSLMessages.createMessage(msg, args);
        ErrorListener errHandler = this.m_transformer.getErrorListener();
        if (null == errHandler) {
            throw new TransformerException(formattedMsg, srcLctr);
        }
        errHandler.fatalError(new TransformerException(formattedMsg, srcLctr));
    }
}

