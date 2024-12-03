/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils.resolver.implementations;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolverLocalFilesystem
extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ResolverLocalFilesystem.class);

    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        try {
            URI uriNew = ResolverLocalFilesystem.getNewURI(context.uriToResolve, context.baseUri);
            InputStream inputStream = Files.newInputStream(Paths.get(uriNew), new OpenOption[0]);
            XMLSignatureInput result = new XMLSignatureInput(inputStream);
            result.setSecureValidation(context.secureValidation);
            result.setSourceURI(uriNew.toString());
            return result;
        }
        catch (Exception e) {
            throw new ResourceResolverException(e, context.uriToResolve, context.baseUri, "generic.EmptyMessage");
        }
    }

    @Override
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null) {
            return false;
        }
        if (context.uriToResolve.isEmpty() || context.uriToResolve.charAt(0) == '#' || context.uriToResolve.startsWith("http:")) {
            return false;
        }
        try {
            LOG.debug("I was asked whether I can resolve {}", (Object)context.uriToResolve);
            if (context.uriToResolve.startsWith("file:") || context.baseUri.startsWith("file:")) {
                LOG.debug("I state that I can resolve {}", (Object)context.uriToResolve);
                return true;
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage(), (Throwable)e);
        }
        LOG.debug("But I can't");
        return false;
    }

    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri = null;
        newUri = baseURI == null || baseURI.length() == 0 ? new URI(uri) : new URI(baseURI).resolve(uri);
        if (newUri.getFragment() != null) {
            return new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
        }
        return newUri;
    }
}

