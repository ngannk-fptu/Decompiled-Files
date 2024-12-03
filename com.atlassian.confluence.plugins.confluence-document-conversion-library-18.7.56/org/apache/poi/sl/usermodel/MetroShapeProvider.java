/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.usermodel;

import java.io.IOException;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.Internal;

@Internal
public interface MetroShapeProvider {
    public Shape<?, ?> parseShape(byte[] var1) throws IOException;
}

