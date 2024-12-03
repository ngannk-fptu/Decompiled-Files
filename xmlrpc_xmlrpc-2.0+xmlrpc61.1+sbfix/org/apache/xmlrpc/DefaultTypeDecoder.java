/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import org.apache.xmlrpc.TypeDecoder;

public class DefaultTypeDecoder
implements TypeDecoder {
    public boolean isXmlRpcI4(Object o) {
        return o instanceof Integer;
    }

    public boolean isXmlRpcDouble(Object o) {
        return o instanceof Float || o instanceof Double;
    }
}

