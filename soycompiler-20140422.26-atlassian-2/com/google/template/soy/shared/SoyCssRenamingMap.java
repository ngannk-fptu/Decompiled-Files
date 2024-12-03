/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.shared;

import com.google.template.soy.shared.SoyIdRenamingMap;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SoyCssRenamingMap
extends SoyIdRenamingMap {
    public static final SoyCssRenamingMap IDENTITY = new SoyCssRenamingMap(){

        @Override
        public String get(String key) {
            return key;
        }
    };
}

