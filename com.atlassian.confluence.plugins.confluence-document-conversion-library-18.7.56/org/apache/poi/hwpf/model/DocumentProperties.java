/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.types.DOPAbstractType;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;

@Internal
public final class DocumentProperties
extends DOPAbstractType {
    private byte[] _preserved;

    public DocumentProperties(byte[] tableStream, int offset) {
        this(tableStream, offset, DOPAbstractType.getSize());
    }

    public DocumentProperties(byte[] tableStream, int offset, int length) {
        super.fillFields(tableStream, offset);
        int supportedSize = DOPAbstractType.getSize();
        this._preserved = length != supportedSize ? IOUtils.safelyClone(tableStream, offset + supportedSize, length - supportedSize, HWPFDocument.getMaxRecordLength()) : new byte[0];
    }

    @Override
    public void serialize(byte[] data, int offset) {
        super.serialize(data, offset);
    }

    public void writeTo(ByteArrayOutputStream tableStream) throws IOException {
        byte[] supported = new byte[DocumentProperties.getSize()];
        this.serialize(supported, 0);
        tableStream.write(supported);
        tableStream.write(this._preserved);
    }
}

