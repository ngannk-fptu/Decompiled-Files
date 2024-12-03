/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 */
package com.atlassian.confluence.plugins.rest.entities.documentation;

import com.atlassian.plugins.rest.common.Link;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkExamples {
    public static final Link SELF;
    public static final Link ALTERNATE;
    public static final Link ALTERNATE_PDF;

    static {
        URI uri = null;
        try {
            uri = new URI("http://localhost");
        }
        catch (URISyntaxException uRISyntaxException) {
        }
        finally {
            SELF = Link.link((URI)uri, (String)"self");
            ALTERNATE = Link.link((URI)uri, (String)"alternate");
            ALTERNATE_PDF = Link.link((URI)uri, (String)"alternate", (String)"application/pdf");
        }
    }
}

