/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.hooks.weaving;

import java.security.ProtectionDomain;
import java.util.List;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.wiring.BundleWiring;

@ProviderType
public interface WovenClass {
    public static final int TRANSFORMING = 1;
    public static final int TRANSFORMED = 2;
    public static final int DEFINED = 4;
    public static final int TRANSFORMING_FAILED = 8;
    public static final int DEFINE_FAILED = 16;

    public byte[] getBytes();

    public void setBytes(byte[] var1);

    public List<String> getDynamicImports();

    public boolean isWeavingComplete();

    public String getClassName();

    public ProtectionDomain getProtectionDomain();

    public Class<?> getDefinedClass();

    public BundleWiring getBundleWiring();

    public int getState();
}

