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
import software.amazon.awssdk.services.s3.model.OptionalObjectAttributes;

final class OptionalObjectAttributesListCopier {
    OptionalObjectAttributesListCopier() {
    }

    static List<String> copy(Collection<String> optionalObjectAttributesListParam) {
        Object list;
        if (optionalObjectAttributesListParam == null || optionalObjectAttributesListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            optionalObjectAttributesListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<String> copyEnumToString(Collection<OptionalObjectAttributes> optionalObjectAttributesListParam) {
        Object list;
        if (optionalObjectAttributesListParam == null || optionalObjectAttributesListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            optionalObjectAttributesListParam.forEach(entry -> {
                String result = entry.toString();
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<OptionalObjectAttributes> copyStringToEnum(Collection<String> optionalObjectAttributesListParam) {
        Object list;
        if (optionalObjectAttributesListParam == null || optionalObjectAttributesListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            optionalObjectAttributesListParam.forEach(entry -> {
                OptionalObjectAttributes result = OptionalObjectAttributes.fromValue(entry);
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

