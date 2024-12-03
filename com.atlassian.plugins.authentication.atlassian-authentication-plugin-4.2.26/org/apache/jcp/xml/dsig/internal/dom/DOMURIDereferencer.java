/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.Data;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.Reference;
import org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData;
import org.apache.jcp.xml.dsig.internal.dom.ApacheOctetStreamData;
import org.apache.jcp.xml.dsig.internal.dom.Utils;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public final class DOMURIDereferencer
implements URIDereferencer {
    static final URIDereferencer INSTANCE = new DOMURIDereferencer();

    private DOMURIDereferencer() {
        Init.init();
    }

    @Override
    public Data dereference(URIReference uriRef, XMLCryptoContext context) throws URIReferenceException {
        if (uriRef == null) {
            throw new NullPointerException("uriRef cannot be null");
        }
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        DOMURIReference domRef = (DOMURIReference)uriRef;
        Attr uriAttr = (Attr)domRef.getHere();
        String uri = uriRef.getURI();
        DOMCryptoContext dcc = (DOMCryptoContext)context;
        String baseURI = context.getBaseURI();
        boolean secVal = Utils.secureValidation(context);
        if (uri != null && uri.length() != 0 && uri.charAt(0) == '#') {
            Element referencedElem;
            String id = uri.substring(1);
            if (id.startsWith("xpointer(id(")) {
                int i1 = id.indexOf(39);
                int i2 = id.indexOf(39, i1 + 1);
                if (i1 >= 0 && i2 >= 0) {
                    id = id.substring(i1 + 1, i2);
                }
            }
            if ((referencedElem = dcc.getElementById(id)) != null) {
                Element start;
                if (secVal && !XMLUtils.protectAgainstWrappingAttack(start = referencedElem.getOwnerDocument().getDocumentElement(), referencedElem, id)) {
                    String error = "Multiple Elements with the same ID " + id + " were detected";
                    throw new URIReferenceException(error);
                }
                XMLSignatureInput result = new XMLSignatureInput(referencedElem);
                result.setSecureValidation(secVal);
                if (!uri.substring(1).startsWith("xpointer(id(")) {
                    result.setExcludeComments(true);
                }
                result.setMIMEType("text/xml");
                if (baseURI != null && baseURI.length() > 0) {
                    result.setSourceURI(baseURI.concat(uriAttr.getNodeValue()));
                } else {
                    result.setSourceURI(uriAttr.getNodeValue());
                }
                return new ApacheNodeSetData(result);
            }
        }
        ResourceResolverContext resContext = new ResourceResolverContext(uriAttr, baseURI, secVal);
        if (uriRef instanceof Reference || resContext.isURISafeToResolve()) {
            try {
                XMLSignatureInput in = ResourceResolver.resolve(resContext);
                if (in.isOctetStream()) {
                    return new ApacheOctetStreamData(in);
                }
                return new ApacheNodeSetData(in);
            }
            catch (Exception e) {
                throw new URIReferenceException(e);
            }
        }
        throw new URIReferenceException("URI " + uri + " is forbidden");
    }
}

