/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.error;

import org.apache.abdera.protocol.error.Error;
import org.apache.abdera.util.AbstractExtensionFactory;

public class ErrorExtensionFactory
extends AbstractExtensionFactory {
    public ErrorExtensionFactory() {
        super("http://abdera.apache.org");
        this.addImpl(Error.ERROR, Error.class);
    }
}

