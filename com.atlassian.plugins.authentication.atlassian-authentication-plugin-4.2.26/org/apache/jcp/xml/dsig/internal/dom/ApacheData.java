/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.Data;
import org.apache.xml.security.signature.XMLSignatureInput;

public interface ApacheData
extends Data {
    public XMLSignatureInput getXMLSignatureInput();
}

