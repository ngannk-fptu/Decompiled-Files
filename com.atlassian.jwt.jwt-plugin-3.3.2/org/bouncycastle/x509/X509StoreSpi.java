/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.x509;

import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.x509.X509StoreParameters;

public abstract class X509StoreSpi {
    public abstract void engineInit(X509StoreParameters var1);

    public abstract Collection engineGetMatches(Selector var1);
}

