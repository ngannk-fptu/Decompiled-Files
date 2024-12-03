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
import software.amazon.awssdk.services.s3.model.InventoryConfiguration;

final class InventoryConfigurationListCopier {
    InventoryConfigurationListCopier() {
    }

    static List<InventoryConfiguration> copy(Collection<? extends InventoryConfiguration> inventoryConfigurationListParam) {
        Object list;
        if (inventoryConfigurationListParam == null || inventoryConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<InventoryConfiguration> copyFromBuilder(Collection<? extends InventoryConfiguration.Builder> inventoryConfigurationListParam) {
        Object list;
        if (inventoryConfigurationListParam == null || inventoryConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryConfigurationListParam.forEach(entry -> {
                InventoryConfiguration member = entry == null ? null : (InventoryConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<InventoryConfiguration.Builder> copyToBuilder(Collection<? extends InventoryConfiguration> inventoryConfigurationListParam) {
        Object list;
        if (inventoryConfigurationListParam == null || inventoryConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryConfigurationListParam.forEach(entry -> {
                InventoryConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

