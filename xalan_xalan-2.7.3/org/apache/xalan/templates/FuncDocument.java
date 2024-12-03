/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.templates;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.SourceTreeManager;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.functions.WrongNumberArgsException;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;

public class FuncDocument
extends Function2Args {
    static final long serialVersionUID = 2483304325971281424L;

    @Override
    public XObject execute(XPathContext xctxt) throws TransformerException {
        int context = xctxt.getCurrentNode();
        DTM dtm = xctxt.getDTM(context);
        int docContext = dtm.getDocumentRoot(context);
        XObject arg = this.getArg0().execute(xctxt);
        String base = "";
        Expression arg1Expr = this.getArg1();
        if (null != arg1Expr) {
            XObject arg2 = arg1Expr.execute(xctxt);
            if (4 == arg2.getType()) {
                int baseNode = arg2.iter().nextNode();
                if (baseNode == -1) {
                    this.warn(xctxt, "WG_EMPTY_SECOND_ARG", null);
                    XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
                    return nodes;
                }
                DTM baseDTM = xctxt.getDTM(baseNode);
                base = baseDTM.getDocumentBaseURI();
            } else {
                arg2.iter();
            }
        } else {
            this.assertion(null != xctxt.getNamespaceContext(), "Namespace context can not be null!");
            base = xctxt.getNamespaceContext().getBaseIdentifier();
        }
        XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
        NodeSetDTM mnl = nodes.mutableNodeset();
        DTMIterator iterator = 4 == arg.getType() ? arg.iter() : null;
        int pos = -1;
        while (null == iterator || -1 != (pos = iterator.nextNode())) {
            int newDoc;
            XMLString ref;
            XMLString xMLString = ref = null != iterator ? xctxt.getDTM(pos).getStringValue(pos) : arg.xstr();
            if (null == arg1Expr && -1 != pos) {
                DTM baseDTM = xctxt.getDTM(pos);
                base = baseDTM.getDocumentBaseURI();
            }
            if (null == ref) continue;
            if (-1 == docContext) {
                this.error(xctxt, "ER_NO_CONTEXT_OWNERDOC", null);
            }
            int indexOfColon = ref.indexOf(58);
            int indexOfSlash = ref.indexOf(47);
            if (indexOfColon != -1 && indexOfSlash != -1 && indexOfColon < indexOfSlash) {
                base = null;
            }
            if (-1 != (newDoc = this.getDoc(xctxt, context, ref.toString(), base)) && !mnl.contains(newDoc)) {
                mnl.addElement(newDoc);
            }
            if (null != iterator && newDoc != -1) continue;
            break;
        }
        return nodes;
    }

    int getDoc(XPathContext xctxt, int context, String uri, String base) throws TransformerException {
        int newDoc;
        Source source;
        SourceTreeManager treeMgr = xctxt.getSourceTreeManager();
        try {
            source = treeMgr.resolveURI(base, uri, xctxt.getSAXLocator());
            newDoc = treeMgr.getNode(source);
        }
        catch (IOException ioe) {
            throw new TransformerException(ioe.getMessage(), xctxt.getSAXLocator(), ioe);
        }
        catch (TransformerException te) {
            throw new TransformerException(te);
        }
        if (-1 != newDoc) {
            return newDoc;
        }
        if (uri.length() == 0) {
            uri = xctxt.getNamespaceContext().getBaseIdentifier();
            try {
                source = treeMgr.resolveURI(base, uri, xctxt.getSAXLocator());
            }
            catch (IOException ioe) {
                throw new TransformerException(ioe.getMessage(), xctxt.getSAXLocator(), ioe);
            }
        }
        String diagnosticsString = null;
        try {
            if (null != uri && uri.length() > 0) {
                newDoc = treeMgr.getSourceTree(source, xctxt.getSAXLocator(), xctxt);
            } else {
                this.warn(xctxt, "WG_CANNOT_MAKE_URL_FROM", new Object[]{(base == null ? "" : base) + uri});
            }
        }
        catch (Throwable throwable2) {
            Exception throwable2;
            newDoc = -1;
            while (throwable2 instanceof WrappedRuntimeException) {
                throwable2 = ((WrappedRuntimeException)throwable2).getException();
            }
            if (throwable2 instanceof NullPointerException || throwable2 instanceof ClassCastException) {
                throw new WrappedRuntimeException(throwable2);
            }
            StringWriter sw = new StringWriter();
            PrintWriter diagnosticsWriter = new PrintWriter(sw);
            if (throwable2 instanceof TransformerException) {
                TransformerException spe;
                Throwable e = spe = (TransformerException)throwable2;
                while (null != e) {
                    if (null != e.getMessage()) {
                        diagnosticsWriter.println(" (" + e.getClass().getName() + "): " + e.getMessage());
                    }
                    if (e instanceof TransformerException) {
                        TransformerException spe2 = e;
                        SourceLocator locator = spe2.getLocator();
                        if (null != locator && null != locator.getSystemId()) {
                            diagnosticsWriter.println("   ID: " + locator.getSystemId() + " Line #" + locator.getLineNumber() + " Column #" + locator.getColumnNumber());
                        }
                        if (!((e = spe2.getException()) instanceof WrappedRuntimeException)) continue;
                        e = ((WrappedRuntimeException)e).getException();
                        continue;
                    }
                    e = null;
                }
            } else {
                diagnosticsWriter.println(" (" + throwable2.getClass().getName() + "): " + throwable2.getMessage());
            }
            diagnosticsString = throwable2.getMessage();
        }
        if (-1 == newDoc) {
            if (null != diagnosticsString) {
                this.warn(xctxt, "WG_CANNOT_LOAD_REQUESTED_DOC", new Object[]{diagnosticsString});
            } else {
                this.warn(xctxt, "WG_CANNOT_LOAD_REQUESTED_DOC", new Object[]{uri == null ? (base == null ? "" : base) + uri : uri.toString()});
            }
        }
        return newDoc;
    }

    @Override
    public void error(XPathContext xctxt, String msg, Object[] args) throws TransformerException {
        String formattedMsg = XSLMessages.createMessage(msg, args);
        ErrorListener errHandler = xctxt.getErrorListener();
        TransformerException spe = new TransformerException(formattedMsg, xctxt.getSAXLocator());
        if (null != errHandler) {
            errHandler.error(spe);
        } else {
            System.out.println(formattedMsg);
        }
    }

    @Override
    public void warn(XPathContext xctxt, String msg, Object[] args) throws TransformerException {
        String formattedMsg = XSLMessages.createWarning(msg, args);
        ErrorListener errHandler = xctxt.getErrorListener();
        TransformerException spe = new TransformerException(formattedMsg, xctxt.getSAXLocator());
        if (null != errHandler) {
            errHandler.warning(spe);
        } else {
            System.out.println(formattedMsg);
        }
    }

    @Override
    public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
        if (argNum < 1 || argNum > 2) {
            this.reportWrongNumberArgs();
        }
    }

    @Override
    protected void reportWrongNumberArgs() throws WrongNumberArgsException {
        throw new WrongNumberArgsException(XSLMessages.createMessage("ER_ONE_OR_TWO", null));
    }

    @Override
    public boolean isNodesetExpr() {
        return true;
    }
}

