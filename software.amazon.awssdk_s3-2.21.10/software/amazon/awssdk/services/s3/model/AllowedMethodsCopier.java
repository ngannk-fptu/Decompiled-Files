/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.s3.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;

final class AllowedMethodsCopier {
    AllowedMethodsCopier() {
    }

    static List<String> copy(Collection<String> allowedMethodsParam) {
        Object list;
        if (allowedMethodsParam == null || allowedMethodsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            allowedMethodsParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

