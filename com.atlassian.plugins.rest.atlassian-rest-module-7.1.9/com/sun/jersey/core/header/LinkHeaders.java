/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.LinkHeader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;

public class LinkHeaders {
    private final Map<String, LinkHeader> map;

    public LinkHeaders(MultivaluedMap<String, String> headers) throws IllegalArgumentException {
        List ls = (List)headers.get("Link");
        if (ls != null) {
            this.map = new HashMap<String, LinkHeader>();
            for (String l : ls) {
                LinkHeader lh = LinkHeader.valueOf(l);
                for (String rel : lh.getRel()) {
                    this.map.put(rel, lh);
                }
            }
        } else {
            this.map = Collections.emptyMap();
        }
    }

    public LinkHeader getLink(String rel) throws IllegalArgumentException {
        return this.map.get(rel);
    }
}

