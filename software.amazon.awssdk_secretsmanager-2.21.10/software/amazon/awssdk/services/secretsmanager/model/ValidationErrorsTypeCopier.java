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
import software.amazon.awssdk.services.secretsmanager.model.ValidationErrorsEntry;

final class ValidationErrorsTypeCopier {
    ValidationErrorsTypeCopier() {
    }

    static List<ValidationErrorsEntry> copy(Collection<? extends ValidationErrorsEntry> validationErrorsTypeParam) {
        Object list;
        if (validationErrorsTypeParam == null || validationErrorsTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            validationErrorsTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ValidationErrorsEntry> copyFromBuilder(Collection<? extends ValidationErrorsEntry.Builder> validationErrorsTypeParam) {
        Object list;
        if (validationErrorsTypeParam == null || validationErrorsTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            validationErrorsTypeParam.forEach(entry -> {
                ValidationErrorsEntry member = entry == null ? null : (ValidationErrorsEntry)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ValidationErrorsEntry.Builder> copyToBuilder(Collection<? extends ValidationErrorsEntry> validationErrorsTypeParam) {
        Object list;
        if (validationErrorsTypeParam == null || validationErrorsTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            validationErrorsTypeParam.forEach(entry -> {
                ValidationErrorsEntry.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

