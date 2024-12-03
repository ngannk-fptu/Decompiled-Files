/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;

final class FilterValuesStringListCopier {
    FilterValuesStringListCopier() {
    }

    static List<String> copy(Collection<String> filterValuesStringListParam) {
        Object list;
        if (filterValuesStringListParam == null || filterValuesStringListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filterValuesStringListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

