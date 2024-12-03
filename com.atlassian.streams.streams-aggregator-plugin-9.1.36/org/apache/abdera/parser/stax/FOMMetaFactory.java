/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;

public class FOMMetaFactory
extends OMLinkedListMetaFactory {
    private final OMFactory omFactory = new FOMFactory();

    public OMFactory getOMFactory() {
        return this.omFactory;
    }
}

