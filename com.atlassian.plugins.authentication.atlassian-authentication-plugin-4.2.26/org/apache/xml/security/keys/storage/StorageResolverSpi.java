/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.storage;

import java.security.cert.Certificate;
import java.util.Iterator;

public abstract class StorageResolverSpi {
    public abstract Iterator<Certificate> getIterator();
}

