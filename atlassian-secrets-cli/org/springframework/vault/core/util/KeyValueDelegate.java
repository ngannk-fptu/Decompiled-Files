/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

public class KeyValueDelegate {
    private final Map<String, MountInfo> mountInfo;
    private final VaultOperations operations;

    public KeyValueDelegate(VaultOperations operations) {
        this(operations, ConcurrentReferenceHashMap::new);
    }

    public KeyValueDelegate(VaultOperations operations, Supplier<Map<String, ?>> cacheSupplier) {
        this.operations = operations;
        this.mountInfo = cacheSupplier.get();
    }

    public boolean isVersioned(String path) {
        return this.getMountInfo(path).isKeyValue(VaultKeyValueOperationsSupport.KeyValueBackend.versioned());
    }

    @Nullable
    public VaultResponse getSecret(String path) {
        MountInfo mountInfo = this.mountInfo.get(path);
        if (!mountInfo.isKeyValue(VaultKeyValueOperationsSupport.KeyValueBackend.versioned())) {
            return this.operations.read(path);
        }
        VaultResponse response = this.operations.read(KeyValueDelegate.getKeyValue2Path(mountInfo.getPath(), path));
        KeyValueDelegate.unwrapDataResponse(response);
        return response;
    }

    static String getKeyValue2Path(String mountPath, String requestedSecret) {
        if (!requestedSecret.startsWith(mountPath)) {
            return requestedSecret;
        }
        String keyPath = requestedSecret.substring(mountPath.length());
        return String.format("%sdata/%s", mountPath, keyPath);
    }

    private static void unwrapDataResponse(@Nullable VaultResponse response) {
        if (response == null || response.getData() == null || !((Map)response.getData()).containsKey("data")) {
            return;
        }
        LinkedHashMap nested = new LinkedHashMap((Map)((Map)response.getRequiredData()).get("data"));
        response.setData(nested);
    }

    private MountInfo doGetMountInfo(String path) {
        VaultResponse response = this.operations.read(String.format("sys/internal/ui/mounts/%s", path));
        if (response == null || response.getData() == null) {
            return MountInfo.unavailable();
        }
        Map data = (Map)response.getData();
        return MountInfo.from((String)data.get("path"), (Map)data.get("options"));
    }

    private MountInfo getMountInfo(String path) {
        MountInfo mountInfo = this.mountInfo.get(path);
        if (mountInfo == null) {
            try {
                mountInfo = this.doGetMountInfo(path);
            }
            catch (RuntimeException e) {
                mountInfo = MountInfo.unavailable();
            }
            this.mountInfo.put(path, mountInfo);
        }
        return mountInfo;
    }

    static class MountInfo {
        static final MountInfo UNAVAILABLE = new MountInfo("", Collections.emptyMap(), false);
        final String path;
        @Nullable
        final Map<String, Object> options;
        final boolean available;

        private MountInfo(String path, @Nullable Map<String, Object> options, boolean available) {
            this.path = path;
            this.options = options;
            this.available = available;
        }

        static MountInfo unavailable() {
            return UNAVAILABLE;
        }

        static MountInfo from(String path, @Nullable Map<String, Object> options) {
            return new MountInfo(path, options, true);
        }

        boolean isKeyValue(VaultKeyValueOperationsSupport.KeyValueBackend versioned) {
            if (!this.isAvailable() || !StringUtils.hasText(this.path) || this.options == null) {
                return false;
            }
            Object version = this.options.get("version");
            if (version != null) {
                if (version.toString().equals("1") && versioned == VaultKeyValueOperationsSupport.KeyValueBackend.KV_1) {
                    return true;
                }
                if (version.toString().equals("2") && versioned == VaultKeyValueOperationsSupport.KeyValueBackend.KV_2) {
                    return true;
                }
            }
            return false;
        }

        public String getPath() {
            return this.path;
        }

        @Nullable
        public Map<String, Object> getOptions() {
            return this.options;
        }

        public boolean isAvailable() {
            return this.available;
        }
    }
}

