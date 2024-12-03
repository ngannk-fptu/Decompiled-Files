/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.color.ICCProfileSanitizer;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_Profile;

final class LCMSSanitizerStrategy
implements ICCProfileSanitizer {
    LCMSSanitizerStrategy() {
    }

    @Override
    public void fixProfile(ICC_Profile iCC_Profile) {
        Validate.notNull((Object)iCC_Profile, (String)"profile");
    }

    @Override
    public boolean validationAltersProfileHeader() {
        return true;
    }
}

