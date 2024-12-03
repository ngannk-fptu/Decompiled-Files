/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import java.util.Map;
import org.apache.axiom.locator.loader.OMMetaFactoryLoader;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;

public class OMLinkedListMetaFactoryLoader
implements OMMetaFactoryLoader {
    public OMMetaFactory load(Map properties) {
        return new OMLinkedListMetaFactory();
    }
}

