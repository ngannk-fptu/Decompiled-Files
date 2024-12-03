/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.internal.StandardArchiveDescriptorFactory;
import org.hibernate.boot.archive.scan.spi.AbstractScannerImpl;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;

public class StandardScanner
extends AbstractScannerImpl {
    public StandardScanner() {
        this(StandardArchiveDescriptorFactory.INSTANCE);
    }

    public StandardScanner(ArchiveDescriptorFactory value) {
        super(value);
    }
}

