/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.observation.ObservationDavServletResponse;

public interface WebdavResponse
extends DavServletResponse,
ObservationDavServletResponse {
    default public void setTrailerFields(Supplier<Map<String, String>> supplier) {
    }

    default public Supplier<Map<String, String>> getTrailerFields() {
        return null;
    }
}

