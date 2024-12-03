/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNativeQueryPropertyReturnType;

public interface NativeQueryNonScalarRootReturn {
    public String getAlias();

    public LockMode getLockMode();

    public List<JaxbHbmNativeQueryPropertyReturnType> getReturnProperty();
}

