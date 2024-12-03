/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.Messages;
import org.apache.batik.util.ParsedURL;

public class DefaultExternalResourceSecurity
implements ExternalResourceSecurity {
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL = "DefaultExternalResourceSecurity.error.cannot.access.document.url";
    public static final String ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL = "DefaultExternalResourceSecurity.error.external.resource.from.different.url";
    protected SecurityException se;

    @Override
    public void checkLoadExternalResource() {
        if (this.se != null) {
            this.se.fillInStackTrace();
            throw this.se;
        }
    }

    public DefaultExternalResourceSecurity(ParsedURL externalResourceURL, ParsedURL docURL) {
        if (DATA_PROTOCOL.equals(externalResourceURL.getProtocol())) {
            return;
        }
        if (docURL == null) {
            this.se = new SecurityException(Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL, new Object[]{externalResourceURL}));
        } else {
            String docHost = docURL.getHost();
            String externalResourceHost = externalResourceURL.getHost();
            if (externalResourceHost == null && !DATA_PROTOCOL.equals(externalResourceURL.getProtocol())) {
                try {
                    externalResourceHost = new URI(externalResourceURL.getPath()).getHost();
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!(docHost == externalResourceHost || docHost != null && docHost.equals(externalResourceHost) || externalResourceURL != null && DATA_PROTOCOL.equals(externalResourceURL.getProtocol()))) {
                this.se = new SecurityException(Messages.formatMessage(ERROR_EXTERNAL_RESOURCE_FROM_DIFFERENT_URL, new Object[]{externalResourceURL}));
            }
        }
    }
}

