/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import javax.xml.namespace.QName;
import org.apache.axis.deployment.wsdd.WSDDChain;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.w3c.dom.Element;

public class WSDDRequestFlow
extends WSDDChain {
    public WSDDRequestFlow() {
    }

    public WSDDRequestFlow(Element e) throws WSDDException {
        super(e);
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_REQFLOW;
    }
}

