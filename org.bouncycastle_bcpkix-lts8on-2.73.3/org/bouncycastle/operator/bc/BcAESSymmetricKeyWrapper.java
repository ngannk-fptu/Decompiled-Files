/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.crypto.Wrapper
 *  org.bouncycastle.crypto.engines.AESWrapEngine
 *  org.bouncycastle.crypto.params.KeyParameter
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.bc.AESUtil;
import org.bouncycastle.operator.bc.BcSymmetricKeyWrapper;

public class BcAESSymmetricKeyWrapper
extends BcSymmetricKeyWrapper {
    public BcAESSymmetricKeyWrapper(KeyParameter wrappingKey) {
        super(AESUtil.determineKeyEncAlg(wrappingKey), (Wrapper)new AESWrapEngine(), wrappingKey);
    }
}

