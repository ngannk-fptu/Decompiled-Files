/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;

final class MetadataCopier {
    MetadataCopier() {
    }

    static Map<String, String> copy(Map<String, String> metadataParam) {
        Object map;
        if (metadataParam == null || metadataParam instanceof SdkAutoConstructMap) {
            map = DefaultSdkAutoConstructMap.getInstance();
        } else {
            LinkedHashMap modifiableMap = new LinkedHashMap();
            metadataParam.forEach((key, value) -> modifiableMap.put(key, value));
            map = Collections.unmodifiableMap(modifiableMap);
        }
        return map;
    }
}

