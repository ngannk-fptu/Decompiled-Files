/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.util.Enumeration;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.Chain;
import org.apache.axis.Handler;
import org.apache.axis.HandlerIterationStrategy;
import org.apache.axis.InternalException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.strategies.InvocationStrategy;
import org.apache.axis.strategies.WSDLGenStrategy;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleChain
extends BasicHandler
implements Chain {
    private static Log log = LogFactory.getLog((class$org$apache$axis$SimpleChain == null ? (class$org$apache$axis$SimpleChain = SimpleChain.class$("org.apache.axis.SimpleChain")) : class$org$apache$axis$SimpleChain).getName());
    protected Vector handlers = new Vector();
    protected boolean invoked = false;
    private String CAUGHTFAULT_PROPERTY = "org.apache.axis.SimpleChain.caughtFaultInResponse";
    private static final HandlerIterationStrategy iVisitor = new InvocationStrategy();
    private static final HandlerIterationStrategy wsdlVisitor = new WSDLGenStrategy();
    static /* synthetic */ Class class$org$apache$axis$SimpleChain;

    public void init() {
        for (int i = 0; i < this.handlers.size(); ++i) {
            ((Handler)this.handlers.elementAt(i)).init();
        }
    }

    public void cleanup() {
        for (int i = 0; i < this.handlers.size(); ++i) {
            ((Handler)this.handlers.elementAt(i)).cleanup();
        }
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleChain::invoke");
        }
        this.invoked = true;
        this.doVisiting(msgContext, iVisitor);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SimpleChain::invoke");
        }
    }

    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleChain::generateWSDL");
        }
        this.invoked = true;
        this.doVisiting(msgContext, wsdlVisitor);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SimpleChain::generateWSDL");
        }
    }

    private void doVisiting(MessageContext msgContext, HandlerIterationStrategy visitor) throws AxisFault {
        int i = 0;
        try {
            Enumeration enumeration = this.handlers.elements();
            while (enumeration.hasMoreElements()) {
                Handler h = (Handler)enumeration.nextElement();
                visitor.visit(h, msgContext);
                ++i;
            }
        }
        catch (AxisFault f) {
            if (!msgContext.isPropertyTrue(this.CAUGHTFAULT_PROPERTY)) {
                Message respMsg = new Message(f);
                msgContext.setResponseMessage(respMsg);
                msgContext.setProperty(this.CAUGHTFAULT_PROPERTY, Boolean.TRUE);
            }
            while (--i >= 0) {
                ((Handler)this.handlers.elementAt(i)).onFault(msgContext);
            }
            throw f;
        }
    }

    public void onFault(MessageContext msgContext) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: SimpleChain::onFault");
        }
        for (int i = this.handlers.size() - 1; i >= 0; --i) {
            ((Handler)this.handlers.elementAt(i)).onFault(msgContext);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SimpleChain::onFault");
        }
    }

    public boolean canHandleBlock(QName qname) {
        for (int i = 0; i < this.handlers.size(); ++i) {
            if (!((Handler)this.handlers.elementAt(i)).canHandleBlock(qname)) continue;
            return true;
        }
        return false;
    }

    public void addHandler(Handler handler) {
        if (handler == null) {
            throw new InternalException(Messages.getMessage("nullHandler00", "SimpleChain::addHandler"));
        }
        if (this.invoked) {
            throw new InternalException(Messages.getMessage("addAfterInvoke00", "SimpleChain::addHandler"));
        }
        this.handlers.add(handler);
    }

    public boolean contains(Handler handler) {
        return this.handlers.contains(handler);
    }

    public Handler[] getHandlers() {
        if (this.handlers.size() == 0) {
            return null;
        }
        Handler[] ret = new Handler[this.handlers.size()];
        return this.handlers.toArray(ret);
    }

    public Element getDeploymentData(Document doc) {
        int i;
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("enter00", "SimpleChain::getDeploymentData"));
        }
        Element root = doc.createElementNS("", "chain");
        StringBuffer str = new StringBuffer();
        for (i = 0; i < this.handlers.size(); ++i) {
            if (i != 0) {
                str.append(",");
            }
            Handler h = (Handler)this.handlers.elementAt(i);
            str.append(h.getName());
        }
        if (i > 0) {
            root.setAttribute("flow", str.toString());
        }
        if (this.options != null) {
            Enumeration e = this.options.keys();
            while (e.hasMoreElements()) {
                String k = (String)e.nextElement();
                Object v = this.options.get(k);
                Element e1 = doc.createElementNS("", "option");
                e1.setAttribute("name", k);
                e1.setAttribute("value", v.toString());
                root.appendChild(e1);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: SimpleChain::getDeploymentData");
        }
        return root;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

