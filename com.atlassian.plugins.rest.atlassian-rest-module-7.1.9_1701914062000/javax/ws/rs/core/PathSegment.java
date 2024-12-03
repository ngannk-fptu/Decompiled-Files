/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import javax.ws.rs.core.MultivaluedMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PathSegment {
    public String getPath();

    public MultivaluedMap<String, String> getMatrixParameters();
}

