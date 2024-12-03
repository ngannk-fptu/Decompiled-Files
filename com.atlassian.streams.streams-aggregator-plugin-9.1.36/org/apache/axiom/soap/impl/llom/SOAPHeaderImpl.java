/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.soap.impl.llom;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.Checker;
import org.apache.axiom.soap.impl.llom.HeaderIterator;
import org.apache.axiom.soap.impl.llom.MURoleChecker;
import org.apache.axiom.soap.impl.llom.RoleChecker;
import org.apache.axiom.soap.impl.llom.RolePlayerChecker;
import org.apache.axiom.soap.impl.llom.SOAPElement;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SOAPHeaderImpl
extends SOAPElement
implements SOAPHeader {
    private static final Log log = LogFactory.getLog(SOAPHeaderImpl.class);

    protected SOAPHeaderImpl(OMNamespace ns, SOAPFactory factory) {
        super("Header", ns, factory);
    }

    public SOAPHeaderImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, "Header", true, factory);
    }

    public SOAPHeaderImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)envelope, "Header", builder, factory);
    }

    public SOAPHeaderBlock addHeaderBlock(String localName, OMNamespace ns) throws OMException {
        SOAPHeaderBlock soapHeaderBlock;
        if (ns == null || ns.getNamespaceURI().length() == 0) {
            throw new OMException("All the SOAP Header blocks should be namespace qualified");
        }
        OMNamespace namespace = this.findNamespace(ns.getNamespaceURI(), ns.getPrefix());
        if (namespace != null) {
            ns = namespace;
        }
        try {
            soapHeaderBlock = ((SOAPFactory)this.factory).createSOAPHeaderBlock(localName, ns, this);
        }
        catch (SOAPProcessingException e) {
            throw new OMException(e);
        }
        ((OMNodeEx)((Object)soapHeaderBlock)).setComplete(true);
        return soapHeaderBlock;
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer));
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer, namespace));
    }

    public Iterator examineHeaderBlocks(String role) {
        return new HeaderIterator(this, new RoleChecker(role));
    }

    public abstract Iterator extractHeaderBlocks(String var1);

    public Iterator examineMustUnderstandHeaderBlocks(String actor) {
        return new HeaderIterator(this, new MURoleChecker(actor));
    }

    public Iterator examineAllHeaderBlocks() {
        class DefaultChecker
        implements Checker {
            DefaultChecker() {
            }

            public boolean checkHeader(SOAPHeaderBlock header) {
                return true;
            }
        }
        return new HeaderIterator(this, new DefaultChecker());
    }

    public Iterator extractAllHeaderBlocks() {
        ArrayList<OMElement> result = new ArrayList<OMElement>();
        Iterator iter = this.getChildElements();
        while (iter.hasNext()) {
            OMElement headerBlock = (OMElement)iter.next();
            iter.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }

    public ArrayList getHeaderBlocksWithNSURI(String nsURI) {
        ArrayList<OMElement> headers = null;
        OMElement header = this.getFirstElement();
        if (header != null) {
            headers = new ArrayList<OMElement>();
        }
        for (OMNode node = header; node != null; node = node.getNextOMSibling()) {
            if (node.getType() != 1) continue;
            header = node;
            OMNamespace namespace = header.getNamespace();
            if (nsURI == null) {
                if (namespace != null) continue;
                headers.add(header);
                continue;
            }
            if (namespace == null || !nsURI.equals(namespace.getNamespaceURI())) continue;
            headers.add(header);
        }
        return headers;
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException("Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }

    public void addChild(OMNode child, boolean fromBuilder) {
        if (log.isDebugEnabled() && child instanceof OMElement && !(child instanceof SOAPHeaderBlock)) {
            SOAPProcessingException e = new SOAPProcessingException("An attempt was made to add a normal OMElement as a child of a SOAPHeader.  This is not supported.  The child should be a SOAPHeaderBlock.");
            log.debug((Object)SOAPHeaderImpl.exceptionToString(e));
        }
        super.addChild(child, fromBuilder);
    }

    public static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);
        PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        return text;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPHeader((SOAPEnvelope)targetParent);
    }
}

