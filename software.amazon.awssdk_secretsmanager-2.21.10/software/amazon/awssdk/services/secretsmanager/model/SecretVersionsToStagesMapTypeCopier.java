/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;

final class SecretVersionsToStagesMapTypeCopier {
    SecretVersionsToStagesMapTypeCopier() {
    }

    static Map<String, List<String>> copy(Map<String, ? extends Collection<String>> secretVersionsToStagesMapTypeParam) {
        Object map;
        if (secretVersionsToStagesMapTypeParam == null || secretVersionsToStagesMapTypeParam instanceof SdkAutoConstructMap) {
            map = DefaultSdkAutoConstructMap.getInstance();
        } else {
            LinkedHashMap modifiableMap = new LinkedHashMap();
            secretVersionsToStagesMapTypeParam.forEach((key, value) -> {
                Object list;
                if (value == null || value instanceof SdkAutoConstructList) {
                    list = DefaultSdkAutoConstructList.getInstance();
                } else {
                    ArrayList modifiableList = new ArrayList();
                    value.forEach(entry -> modifiableList.add(entry));
                    list = Collections.unmodifiableList(modifiableList);
                }
                modifiableMap.put(key, list);
            });
            map = Collections.unmodifiableMap(modifiableMap);
        }
        return map;
    }
}

