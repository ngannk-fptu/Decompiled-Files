/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;

public interface PartMarshaller {
    public boolean marshall(PackagePart var1, OutputStream var2) throws OpenXML4JException;
}

