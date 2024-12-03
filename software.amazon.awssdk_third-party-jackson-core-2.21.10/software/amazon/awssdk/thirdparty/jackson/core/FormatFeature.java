/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core;

import software.amazon.awssdk.thirdparty.jackson.core.util.JacksonFeature;

public interface FormatFeature
extends JacksonFeature {
    @Override
    public boolean enabledByDefault();

    @Override
    public int getMask();

    @Override
    public boolean enabledIn(int var1);
}

