/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.Messages;
import org.apache.batik.util.ParsedURL;

public class EmbededExternalResourceSecurity
implements ExternalResourceSecurity {
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_EXTERNAL_RESOURCE_NOT_EMBEDED = "EmbededExternalResourceSecurity.error.external.resource.not.embeded";
    protected SecurityException se;

    @Override
    public void checkLoadExternalResource() {
        if (this.se != null) {
            throw this.se;
        }
    }

    public EmbededExternalResourceSecurity(ParsedURL externalResourceURL) {
        if (externalResourceURL == null || !DATA_PROTOCOL.equals(externalResourceURL.getProtocol())) {
            this.se = new SecurityException(Messages.formatMessage(ERROR_EXTERNAL_RESOURCE_NOT_EMBEDED, new Object[]{externalResourceURL}));
        }
    }
}

