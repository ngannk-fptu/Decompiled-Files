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
import software.amazon.awssdk.services.s3.model.MultipartUpload;

final class MultipartUploadListCopier {
    MultipartUploadListCopier() {
    }

    static List<MultipartUpload> copy(Collection<? extends MultipartUpload> multipartUploadListParam) {
        Object list;
        if (multipartUploadListParam == null || multipartUploadListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            multipartUploadListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MultipartUpload> copyFromBuilder(Collection<? extends MultipartUpload.Builder> multipartUploadListParam) {
        Object list;
        if (multipartUploadListParam == null || multipartUploadListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            multipartUploadListParam.forEach(entry -> {
                MultipartUpload member = entry == null ? null : (MultipartUpload)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MultipartUpload.Builder> copyToBuilder(Collection<? extends MultipartUpload> multipartUploadListParam) {
        Object list;
        if (multipartUploadListParam == null || multipartUploadListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            multipartUploadListParam.forEach(entry -> {
                MultipartUpload.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

