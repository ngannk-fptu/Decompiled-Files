/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils.resolver.implementations;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ResolverXPointer
extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ResolverXPointer.class);
    private static final String XP = "#xpointer(id(";
    private static final int XP_LENGTH = "#xpointer(id(".length();

    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        Node resultNode = null;
        Document doc = context.attr.getOwnerElement().getOwnerDocument();
        if (ResolverXPointer.isXPointerSlash(context.uriToResolve)) {
            resultNode = doc;
        } else if (ResolverXPointer.isXPointerId(context.uriToResolve)) {
            Element start;
            String id = ResolverXPointer.getXPointerId(context.uriToResolve);
            resultNode = doc.getElementById(id);
            if (context.secureValidation && !XMLUtils.protectAgainstWrappingAttack(start = context.attr.getOwnerDocument().getDocumentElement(), id)) {
                Object[] exArgs = new Object[]{id};
                throw new ResourceResolverException("signature.Verification.MultipleIDs", exArgs, context.uriToResolve, context.baseUri);
            }
            if (resultNode == null) {
                Object[] exArgs = new Object[]{id};
                throw new ResourceResolverException("signature.Verification.MissingID", exArgs, context.uriToResolve, context.baseUri);
            }
        }
        XMLSignatureInput result = new XMLSignatureInput(resultNode);
        result.setSecureValidation(context.secureValidation);
        result.setMIMEType("text/xml");
        if (context.baseUri != null && context.baseUri.length() > 0) {
            result.setSourceURI(context.baseUri.concat(context.uriToResolve));
        } else {
            result.setSourceURI(context.uriToResolve);
        }
        return result;
    }

    @Override
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        return ResolverXPointer.isXPointerSlash(context.uriToResolve) || ResolverXPointer.isXPointerId(context.uriToResolve);
    }

    private static boolean isXPointerSlash(String uri) {
        return "#xpointer(/)".equals(uri);
    }

    private static boolean isXPointerId(String uri) {
        if (uri != null && uri.startsWith(XP) && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH, uri.length() - 2);
            int idLen = idPlusDelim.length() - 1;
            if (idPlusDelim.charAt(0) == '\"' && idPlusDelim.charAt(idLen) == '\"' || idPlusDelim.charAt(0) == '\'' && idPlusDelim.charAt(idLen) == '\'') {
                LOG.debug("Id = {}", (Object)idPlusDelim.substring(1, idLen));
                return true;
            }
        }
        return false;
    }

    private static String getXPointerId(String uri) {
        if (uri.startsWith(XP) && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH, uri.length() - 2);
            int idLen = idPlusDelim.length() - 1;
            if (idPlusDelim.charAt(0) == '\"' && idPlusDelim.charAt(idLen) == '\"' || idPlusDelim.charAt(0) == '\'' && idPlusDelim.charAt(idLen) == '\'') {
                return idPlusDelim.substring(1, idLen);
            }
        }
        return null;
    }
}

