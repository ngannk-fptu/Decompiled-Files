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
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;

final class ChecksumAlgorithmListCopier {
    ChecksumAlgorithmListCopier() {
    }

    static List<String> copy(Collection<String> checksumAlgorithmListParam) {
        Object list;
        if (checksumAlgorithmListParam == null || checksumAlgorithmListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            checksumAlgorithmListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<String> copyEnumToString(Collection<ChecksumAlgorithm> checksumAlgorithmListParam) {
        Object list;
        if (checksumAlgorithmListParam == null || checksumAlgorithmListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            checksumAlgorithmListParam.forEach(entry -> {
                String result = entry.toString();
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ChecksumAlgorithm> copyStringToEnum(Collection<String> checksumAlgorithmListParam) {
        Object list;
        if (checksumAlgorithmListParam == null || checksumAlgorithmListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            checksumAlgorithmListParam.forEach(entry -> {
                ChecksumAlgorithm result = ChecksumAlgorithm.fromValue(entry);
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

