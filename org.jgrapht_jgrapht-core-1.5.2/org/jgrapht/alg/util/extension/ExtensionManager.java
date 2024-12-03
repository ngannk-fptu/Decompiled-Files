/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util.extension;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.alg.util.extension.Extension;
import org.jgrapht.alg.util.extension.ExtensionFactory;

public class ExtensionManager<T, B extends Extension> {
    private ExtensionFactory<B> extensionFactory;
    private Map<T, B> originalToExtensionMap = new HashMap<T, B>();

    public ExtensionManager(ExtensionFactory<B> factory) {
        this.extensionFactory = factory;
    }

    public B createExtension() {
        return this.extensionFactory.create();
    }

    public B getExtension(T t) {
        if (this.originalToExtensionMap.containsKey(t)) {
            return (B)((Extension)this.originalToExtensionMap.get(t));
        }
        B extension = this.createExtension();
        this.originalToExtensionMap.put(t, extension);
        return extension;
    }
}

