/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.FixedSecureRandom;

public class TestRandomData
extends FixedSecureRandom {
    public TestRandomData(String string) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(Hex.decode(string))});
    }

    public TestRandomData(byte[] byArray) {
        super(new FixedSecureRandom.Source[]{new FixedSecureRandom.Data(byArray)});
    }
}

