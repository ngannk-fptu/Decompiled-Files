/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.msgs;

import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.SoyMsgException;
import com.google.template.soy.msgs.SoyMsgPlugin;

public interface SoyBidirectionalMsgPlugin
extends SoyMsgPlugin {
    public SoyMsgBundle parseExtractedMsgsFile(String var1) throws SoyMsgException;

    public CharSequence generateTranslatedMsgsFile(SoyMsgBundle var1, SoyMsgBundleHandler.OutputFileOptions var2) throws SoyMsgException;
}

