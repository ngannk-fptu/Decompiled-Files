/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.msgs.restricted;

import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralPart;
import com.google.template.soy.msgs.restricted.SoyMsgSelectPart;
import java.util.List;

public class MsgPartUtils {
    private MsgPartUtils() {
    }

    public static boolean hasPlrselPart(List<SoyMsgPart> msgParts) {
        for (SoyMsgPart origMsgPart : msgParts) {
            if (!(origMsgPart instanceof SoyMsgPluralPart) && !(origMsgPart instanceof SoyMsgSelectPart)) continue;
            return true;
        }
        return false;
    }
}

