/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.ListBackedList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class ListImpl
extends ListBackedList {
    public static ListImpl forProviderList(List<? extends SoyValueProvider> providerList) {
        return new ListImpl(providerList);
    }

    ListImpl(List<? extends SoyValueProvider> providerList) {
        super(providerList);
    }
}

