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
import software.amazon.awssdk.services.s3.model.LambdaFunctionConfiguration;

final class LambdaFunctionConfigurationListCopier {
    LambdaFunctionConfigurationListCopier() {
    }

    static List<LambdaFunctionConfiguration> copy(Collection<? extends LambdaFunctionConfiguration> lambdaFunctionConfigurationListParam) {
        Object list;
        if (lambdaFunctionConfigurationListParam == null || lambdaFunctionConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            lambdaFunctionConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<LambdaFunctionConfiguration> copyFromBuilder(Collection<? extends LambdaFunctionConfiguration.Builder> lambdaFunctionConfigurationListParam) {
        Object list;
        if (lambdaFunctionConfigurationListParam == null || lambdaFunctionConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            lambdaFunctionConfigurationListParam.forEach(entry -> {
                LambdaFunctionConfiguration member = entry == null ? null : (LambdaFunctionConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<LambdaFunctionConfiguration.Builder> copyToBuilder(Collection<? extends LambdaFunctionConfiguration> lambdaFunctionConfigurationListParam) {
        Object list;
        if (lambdaFunctionConfigurationListParam == null || lambdaFunctionConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            lambdaFunctionConfigurationListParam.forEach(entry -> {
                LambdaFunctionConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

