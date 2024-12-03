/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.util.ParsedURL;

public class RelaxedExternalResourceSecurity
implements ExternalResourceSecurity {
    @Override
    public void checkLoadExternalResource() {
    }

    public RelaxedExternalResourceSecurity(ParsedURL externalResourceURL, ParsedURL docURL) {
    }
}

