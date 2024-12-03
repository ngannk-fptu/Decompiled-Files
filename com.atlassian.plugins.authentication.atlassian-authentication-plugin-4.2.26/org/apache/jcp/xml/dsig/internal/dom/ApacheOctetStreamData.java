/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.IOException;
import javax.xml.crypto.OctetStreamData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheData;
import org.apache.xml.security.signature.XMLSignatureInput;

public class ApacheOctetStreamData
extends OctetStreamData
implements ApacheData {
    private XMLSignatureInput xi;

    public ApacheOctetStreamData(XMLSignatureInput xi) throws IOException {
        super(xi.getOctetStream(), xi.getSourceURI(), xi.getMIMEType());
        this.xi = xi;
    }

    @Override
    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }
}

