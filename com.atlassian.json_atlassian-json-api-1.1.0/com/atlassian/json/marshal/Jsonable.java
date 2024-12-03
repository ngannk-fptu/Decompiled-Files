/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.marshal;

import com.atlassian.annotations.PublicApi;
import java.io.IOException;
import java.io.Writer;

@PublicApi
public interface Jsonable {
    public void write(Writer var1) throws IOException;

    public static class JsonMappingException
    extends RuntimeException {
        public JsonMappingException(Throwable cause) {
            super(cause);
        }
    }
}

