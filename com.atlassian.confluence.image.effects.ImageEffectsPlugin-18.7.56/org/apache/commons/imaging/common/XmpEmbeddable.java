/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSource;

public interface XmpEmbeddable {
    public String getXmpXml(ByteSource var1, Map<String, Object> var2) throws ImageReadException, IOException;
}

