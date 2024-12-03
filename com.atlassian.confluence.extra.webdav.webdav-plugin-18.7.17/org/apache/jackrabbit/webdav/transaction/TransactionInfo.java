/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.transaction;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.transaction.TransactionConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TransactionInfo
implements TransactionConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(TransactionInfo.class);
    private final boolean isCommit;

    public TransactionInfo(boolean isCommit) {
        this.isCommit = isCommit;
    }

    public TransactionInfo(Element transactionInfo) throws DavException {
        if (transactionInfo == null || !"transactioninfo".equals(transactionInfo.getLocalName())) {
            log.warn("'transactionInfo' element expected.");
            throw new DavException(400);
        }
        Element txStatus = DomUtil.getChildElement(transactionInfo, "transactionstatus", NAMESPACE);
        if (txStatus == null) {
            log.warn("transactionInfo must contain a single 'transactionstatus' element.");
            throw new DavException(400);
        }
        this.isCommit = DomUtil.hasChildElement(txStatus, "commit", NAMESPACE);
    }

    public boolean isCommit() {
        return this.isCommit;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = DomUtil.createElement(document, "transactioninfo", NAMESPACE);
        Element st = DomUtil.addChildElement(elem, "transactionstatus", NAMESPACE);
        String lName = this.isCommit ? "commit" : "rollback";
        DomUtil.addChildElement(st, lName, NAMESPACE);
        return elem;
    }
}

