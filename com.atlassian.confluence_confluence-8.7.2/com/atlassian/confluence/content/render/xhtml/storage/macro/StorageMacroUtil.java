/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

public class StorageMacroUtil {
    private static final String MACRO_ELEMENT_XML = "<" + StorageMacroConstants.MACRO_ELEMENT.getPrefix() + ":" + StorageMacroConstants.MACRO_ELEMENT.getLocalPart();
    private static final String MACRO_V2_ELEMENT_XML = "<" + StorageMacroConstants.MACRO_V2_ELEMENT.getPrefix() + ":" + StorageMacroConstants.MACRO_V2_ELEMENT.getLocalPart();

    public static boolean isMacroElement(StartElement startElement) {
        QName startElementName = startElement.getName();
        return StorageMacroConstants.MACRO_ELEMENT.equals(startElementName) || StorageMacroConstants.MACRO_V2_ELEMENT.equals(startElementName);
    }

    public static boolean isMacroElement(String xmlFragment) {
        return xmlFragment.startsWith(MACRO_ELEMENT_XML) || xmlFragment.startsWith(MACRO_V2_ELEMENT_XML);
    }
}

