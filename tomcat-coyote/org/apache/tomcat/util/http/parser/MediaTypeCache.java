/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.collections.ConcurrentCache
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import org.apache.tomcat.util.collections.ConcurrentCache;
import org.apache.tomcat.util.http.parser.MediaType;

public class MediaTypeCache {
    private final ConcurrentCache<String, String[]> cache;

    public MediaTypeCache(int size) {
        this.cache = new ConcurrentCache(size);
    }

    public String[] parse(String input) {
        String[] result = (String[])this.cache.get((Object)input);
        if (result != null) {
            return result;
        }
        MediaType m = null;
        try {
            m = MediaType.parseMediaType(new StringReader(input));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (m != null) {
            result = new String[]{m.toStringNoCharset(), m.getCharset()};
            this.cache.put((Object)input, (Object)result);
        }
        return result;
    }
}

