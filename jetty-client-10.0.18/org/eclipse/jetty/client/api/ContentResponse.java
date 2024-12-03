/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.api;

import org.eclipse.jetty.client.api.Response;

public interface ContentResponse
extends Response {
    public String getMediaType();

    public String getEncoding();

    public byte[] getContent();

    public String getContentAsString();
}

