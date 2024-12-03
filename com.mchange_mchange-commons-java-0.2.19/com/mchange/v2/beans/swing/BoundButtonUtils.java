/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import com.mchange.v2.beans.swing.PropertyBoundButtonGroup;
import com.mchange.v2.beans.swing.SetPropertyElementBoundButtonModel;
import java.beans.IntrospectionException;
import javax.swing.AbstractButton;

public final class BoundButtonUtils {
    public static void bindToSetProperty(AbstractButton[] abstractButtonArray, Object[] objectArray, Object object, String string) throws IntrospectionException {
        SetPropertyElementBoundButtonModel.bind(abstractButtonArray, objectArray, object, string);
    }

    public static void bindAsRadioButtonsToProperty(AbstractButton[] abstractButtonArray, Object[] objectArray, Object object, String string) throws IntrospectionException {
        PropertyBoundButtonGroup propertyBoundButtonGroup = new PropertyBoundButtonGroup(object, string);
        for (int i = 0; i < abstractButtonArray.length; ++i) {
            propertyBoundButtonGroup.add(abstractButtonArray[i], objectArray[i]);
        }
    }
}

