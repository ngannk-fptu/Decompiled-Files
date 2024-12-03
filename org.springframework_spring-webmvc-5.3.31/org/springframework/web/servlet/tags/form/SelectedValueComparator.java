/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.tags.form.ValueFormatter;

abstract class SelectedValueComparator {
    SelectedValueComparator() {
    }

    public static boolean isSelected(BindStatus bindStatus, @Nullable Object candidateValue) {
        Object boundValue = bindStatus.getValue();
        if (ObjectUtils.nullSafeEquals((Object)boundValue, (Object)candidateValue)) {
            return true;
        }
        Object actualValue = bindStatus.getActualValue();
        if (actualValue != null && actualValue != boundValue && ObjectUtils.nullSafeEquals((Object)actualValue, (Object)candidateValue)) {
            return true;
        }
        if (actualValue != null) {
            boundValue = actualValue;
        } else if (boundValue == null) {
            return false;
        }
        boolean selected = false;
        if (candidateValue != null) {
            if (boundValue.getClass().isArray()) {
                selected = SelectedValueComparator.collectionCompare(CollectionUtils.arrayToList((Object)boundValue), candidateValue, bindStatus);
            } else if (boundValue instanceof Collection) {
                selected = SelectedValueComparator.collectionCompare((Collection)boundValue, candidateValue, bindStatus);
            } else if (boundValue instanceof Map) {
                selected = SelectedValueComparator.mapCompare((Map)boundValue, candidateValue, bindStatus);
            }
        }
        if (!selected) {
            selected = SelectedValueComparator.exhaustiveCompare(boundValue, candidateValue, bindStatus.getEditor(), null);
        }
        return selected;
    }

    private static boolean collectionCompare(Collection<?> boundCollection, Object candidateValue, BindStatus bindStatus) {
        try {
            if (boundCollection.contains(candidateValue)) {
                return true;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return SelectedValueComparator.exhaustiveCollectionCompare(boundCollection, candidateValue, bindStatus);
    }

    private static boolean mapCompare(Map<?, ?> boundMap, Object candidateValue, BindStatus bindStatus) {
        try {
            if (boundMap.containsKey(candidateValue)) {
                return true;
            }
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        return SelectedValueComparator.exhaustiveCollectionCompare(boundMap.keySet(), candidateValue, bindStatus);
    }

    private static boolean exhaustiveCollectionCompare(Collection<?> collection, Object candidateValue, BindStatus bindStatus) {
        HashMap<PropertyEditor, Object> convertedValueCache = new HashMap<PropertyEditor, Object>();
        PropertyEditor editor = null;
        boolean candidateIsString = candidateValue instanceof String;
        if (!candidateIsString) {
            editor = bindStatus.findEditor(candidateValue.getClass());
        }
        for (Object element : collection) {
            if (editor == null && element != null && candidateIsString) {
                editor = bindStatus.findEditor(element.getClass());
            }
            if (!SelectedValueComparator.exhaustiveCompare(element, candidateValue, editor, convertedValueCache)) continue;
            return true;
        }
        return false;
    }

    private static boolean exhaustiveCompare(@Nullable Object boundValue, @Nullable Object candidate, @Nullable PropertyEditor editor, @Nullable Map<PropertyEditor, Object> convertedValueCache) {
        String candidateDisplayString = ValueFormatter.getDisplayString(candidate, editor, false);
        if (boundValue != null && boundValue.getClass().isEnum()) {
            Enum boundEnum = (Enum)boundValue;
            String enumCodeAsString = ObjectUtils.getDisplayString((Object)boundEnum.name());
            if (enumCodeAsString.equals(candidateDisplayString)) {
                return true;
            }
            String enumLabelAsString = ObjectUtils.getDisplayString((Object)boundEnum.toString());
            if (enumLabelAsString.equals(candidateDisplayString)) {
                return true;
            }
        } else if (ObjectUtils.getDisplayString((Object)boundValue).equals(candidateDisplayString)) {
            return true;
        }
        if (editor != null && candidate instanceof String) {
            Object candidateAsValue;
            String candidateAsString = (String)candidate;
            if (convertedValueCache != null && convertedValueCache.containsKey(editor)) {
                candidateAsValue = convertedValueCache.get(editor);
            } else {
                editor.setAsText(candidateAsString);
                candidateAsValue = editor.getValue();
                if (convertedValueCache != null) {
                    convertedValueCache.put(editor, candidateAsValue);
                }
            }
            if (ObjectUtils.nullSafeEquals((Object)boundValue, (Object)candidateAsValue)) {
                return true;
            }
        }
        return false;
    }
}

