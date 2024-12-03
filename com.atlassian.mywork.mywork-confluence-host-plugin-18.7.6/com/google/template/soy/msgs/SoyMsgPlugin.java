/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.msgs;

import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.SoyMsgException;

public interface SoyMsgPlugin {
    public CharSequence generateExtractedMsgsFile(SoyMsgBundle var1, SoyMsgBundleHandler.OutputFileOptions var2) throws SoyMsgException;

    public SoyMsgBundle parseTranslatedMsgsFile(String var1) throws SoyMsgException;
}

