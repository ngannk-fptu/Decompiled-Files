/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;

final class Transforms {
    static final Function<SoyValueProvider, SoyValue> RESOLVE_FUNCTION = new Function<SoyValueProvider, SoyValue>(){

        public SoyValue apply(SoyValueProvider provider) {
            Preconditions.checkNotNull((Object)provider);
            return provider.resolve();
        }
    };

    Transforms() {
    }
}

