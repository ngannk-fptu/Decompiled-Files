/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;

public class ARIAWrapEngine
extends RFC3394WrapEngine {
    public ARIAWrapEngine() {
        super(new ARIAEngine());
    }

    public ARIAWrapEngine(boolean bl) {
        super(new ARIAEngine(), bl);
    }
}

