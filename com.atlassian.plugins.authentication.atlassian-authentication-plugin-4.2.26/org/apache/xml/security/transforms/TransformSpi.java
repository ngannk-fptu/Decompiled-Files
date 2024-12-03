/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public abstract class TransformSpi {
    protected abstract XMLSignatureInput enginePerformTransform(XMLSignatureInput var1, OutputStream var2, Element var3, String var4, boolean var5) throws IOException, CanonicalizationException, InvalidCanonicalizerException, TransformationException, ParserConfigurationException, SAXException;

    protected abstract String engineGetURI();
}

