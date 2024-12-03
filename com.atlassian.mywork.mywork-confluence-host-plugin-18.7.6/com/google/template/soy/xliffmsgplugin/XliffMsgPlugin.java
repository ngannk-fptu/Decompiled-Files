/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.xliffmsgplugin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.SoyMsgException;
import com.google.template.soy.msgs.SoyMsgPlugin;
import com.google.template.soy.xliffmsgplugin.XliffGenerator;
import com.google.template.soy.xliffmsgplugin.XliffParser;
import org.xml.sax.SAXException;

@Singleton
public class XliffMsgPlugin
implements SoyMsgPlugin {
    @Inject
    public XliffMsgPlugin() {
    }

    @Override
    public CharSequence generateExtractedMsgsFile(SoyMsgBundle msgBundle, SoyMsgBundleHandler.OutputFileOptions options) throws SoyMsgException {
        return XliffGenerator.generateXliff(msgBundle, options.getSourceLocaleString(), options.getTargetLocaleString());
    }

    @Override
    public SoyMsgBundle parseTranslatedMsgsFile(String translatedMsgsFileContent) throws SoyMsgException {
        try {
            return XliffParser.parseXliffTargetMsgs(translatedMsgsFileContent);
        }
        catch (SAXException e) {
            throw new SoyMsgException(e);
        }
    }
}

