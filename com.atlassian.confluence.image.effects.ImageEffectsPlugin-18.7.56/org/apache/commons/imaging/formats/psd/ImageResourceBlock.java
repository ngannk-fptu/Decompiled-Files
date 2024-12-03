/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.psd;

import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.internal.Debug;

class ImageResourceBlock {
    final int id;
    final byte[] nameData;
    final byte[] data;

    ImageResourceBlock(int id, byte[] nameData, byte[] data) {
        this.id = id;
        this.nameData = nameData;
        this.data = data;
    }

    String getName() {
        Debug.debug("getName: " + this.nameData.length);
        return new String(this.nameData, StandardCharsets.ISO_8859_1);
    }
}

