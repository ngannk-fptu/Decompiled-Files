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
import software.amazon.awssdk.services.s3.model.InventoryOptionalField;

final class InventoryOptionalFieldsCopier {
    InventoryOptionalFieldsCopier() {
    }

    static List<String> copy(Collection<String> inventoryOptionalFieldsParam) {
        Object list;
        if (inventoryOptionalFieldsParam == null || inventoryOptionalFieldsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryOptionalFieldsParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<String> copyEnumToString(Collection<InventoryOptionalField> inventoryOptionalFieldsParam) {
        Object list;
        if (inventoryOptionalFieldsParam == null || inventoryOptionalFieldsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryOptionalFieldsParam.forEach(entry -> {
                String result = entry.toString();
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<InventoryOptionalField> copyStringToEnum(Collection<String> inventoryOptionalFieldsParam) {
        Object list;
        if (inventoryOptionalFieldsParam == null || inventoryOptionalFieldsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            inventoryOptionalFieldsParam.forEach(entry -> {
                InventoryOptionalField result = InventoryOptionalField.fromValue(entry);
                modifiableList.add(result);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

