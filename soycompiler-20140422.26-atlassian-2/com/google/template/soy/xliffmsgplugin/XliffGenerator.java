/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.google.template.soy.xliffmsgplugin;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.base.internal.IndentedLinesBuilder;
import com.google.template.soy.internal.base.CharEscaper;
import com.google.template.soy.internal.base.CharEscapers;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import java.util.Map;
import javax.annotation.Nullable;

class XliffGenerator {
    private static final Map<String, String> CONTENT_TYPE_TO_XLIFF_DATATYPE_MAP = ImmutableMap.builder().put((Object)"text/plain", (Object)"plaintext").put((Object)"text/html", (Object)"html").put((Object)"application/xhtml+xml", (Object)"xhtml").put((Object)"application/javascript", (Object)"javascript").put((Object)"text/css", (Object)"css").put((Object)"text/xml", (Object)"xml").build();

    private XliffGenerator() {
    }

    static CharSequence generateXliff(SoyMsgBundle msgBundle, String sourceLocaleString, @Nullable String targetLocaleString) {
        CharEscaper attributeEscaper = CharEscapers.xmlEscaper();
        CharEscaper contentEscaper = CharEscapers.xmlContentEscaper();
        boolean hasTarget = targetLocaleString != null && targetLocaleString.length() > 0;
        IndentedLinesBuilder ilb = new IndentedLinesBuilder(2);
        ilb.appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ilb.appendLine("<xliff version=\"1.2\" xmlns=\"urn:oasis:names:tc:xliff:document:1.2\">");
        ilb.increaseIndent();
        ilb.appendLineStart("<file original=\"SoyMsgBundle\" datatype=\"x-soy-msg-bundle\"", " xml:space=\"preserve\"", " source-language=\"", attributeEscaper.escape(sourceLocaleString), "\"");
        if (hasTarget) {
            ilb.appendParts(" target-language=\"", attributeEscaper.escape(targetLocaleString), "\"");
        }
        ilb.appendLineEnd(">");
        ilb.increaseIndent();
        ilb.appendLine("<body>");
        ilb.increaseIndent();
        for (SoyMsg msg : msgBundle) {
            String meaning;
            String desc;
            ilb.appendLineStart("<trans-unit id=\"", Long.toString(msg.getId()), "\"");
            String contentType = msg.getContentType();
            if (contentType != null && contentType.length() > 0) {
                String xliffDatatype = CONTENT_TYPE_TO_XLIFF_DATATYPE_MAP.get(contentType);
                if (xliffDatatype == null) {
                    xliffDatatype = contentType;
                }
                ilb.appendParts(" datatype=\"", attributeEscaper.escape(xliffDatatype), "\"");
            }
            ilb.appendLineEnd(">");
            ilb.increaseIndent();
            ilb.appendLineStart("<source>");
            for (SoyMsgPart msgPart : msg.getParts()) {
                if (msgPart instanceof SoyMsgRawTextPart) {
                    String rawText = ((SoyMsgRawTextPart)msgPart).getRawText();
                    ilb.append(contentEscaper.escape(rawText));
                    continue;
                }
                String placeholderName = ((SoyMsgPlaceholderPart)msgPart).getPlaceholderName();
                ilb.appendParts("<x id=\"", attributeEscaper.escape(placeholderName), "\"/>");
            }
            ilb.appendLineEnd("</source>");
            if (hasTarget) {
                ilb.appendLine("<target/>");
            }
            if ((desc = msg.getDesc()) != null && desc.length() > 0) {
                ilb.appendLine("<note priority=\"1\" from=\"description\">", contentEscaper.escape(desc), "</note>");
            }
            if ((meaning = msg.getMeaning()) != null && meaning.length() > 0) {
                ilb.appendLine("<note priority=\"1\" from=\"meaning\">", contentEscaper.escape(meaning), "</note>");
            }
            ilb.decreaseIndent();
            ilb.appendLine("</trans-unit>");
        }
        ilb.decreaseIndent();
        ilb.appendLine("</body>");
        ilb.decreaseIndent();
        ilb.appendLine("</file>");
        ilb.decreaseIndent();
        ilb.appendLine("</xliff>");
        return ilb;
    }
}

