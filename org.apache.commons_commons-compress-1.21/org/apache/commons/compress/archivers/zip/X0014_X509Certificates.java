/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.archivers.zip.PKWareExtraHeader;
import org.apache.commons.compress.archivers.zip.ZipShort;

public class X0014_X509Certificates
extends PKWareExtraHeader {
    public X0014_X509Certificates() {
        super(new ZipShort(20));
    }
}

