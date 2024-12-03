/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util.extension;

import org.jgrapht.alg.util.extension.Extension;

public interface ExtensionFactory<B extends Extension> {
    public B create();
}

