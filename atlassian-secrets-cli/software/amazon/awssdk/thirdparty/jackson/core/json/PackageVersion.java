/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.json;

import software.amazon.awssdk.thirdparty.jackson.core.Version;
import software.amazon.awssdk.thirdparty.jackson.core.Versioned;
import software.amazon.awssdk.thirdparty.jackson.core.util.VersionUtil;

public final class PackageVersion
implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion("2.15.2", "software.amazon.awssdk.thirdparty.jackson.core", "jackson-core");

    @Override
    public Version version() {
        return VERSION;
    }
}

