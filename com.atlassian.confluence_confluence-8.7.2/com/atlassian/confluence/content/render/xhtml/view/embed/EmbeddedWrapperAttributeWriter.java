/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class EmbeddedWrapperAttributeWriter {
    private static final String EMBEDDED_WRAPPER_CLASS_SUFFIX = "-wrapper";
    private static final String EMBEDDED_WRAPPER_CLASS = "confluence-embedded-file-wrapper";
    private static final String EMBEDDED_IMAGE_ALIGN_CLASS_PREFIX = "image-";
    private static final String EMBEDDED_MANUAL_SIZE_CLASS = "confluence-embedded-manual-size";
    private XMLStreamWriter writer;

    public EmbeddedWrapperAttributeWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void writeAttributes(EmbeddedImage embeddedImage) throws XMLStreamException {
        StringBuilder cssClass = new StringBuilder(EMBEDDED_WRAPPER_CLASS);
        String alignment = embeddedImage.getAlignment();
        if (StringUtils.isNotBlank((CharSequence)alignment) && !"none".equals(alignment)) {
            cssClass.append(" ").append(EMBEDDED_IMAGE_ALIGN_CLASS_PREFIX).append(alignment).append(EMBEDDED_WRAPPER_CLASS_SUFFIX);
        }
        if (StringUtils.isNotBlank((CharSequence)embeddedImage.getHeight()) || StringUtils.isNotBlank((CharSequence)embeddedImage.getWidth())) {
            cssClass.append(" ").append(EMBEDDED_MANUAL_SIZE_CLASS);
        }
        this.writeAttribute("class", cssClass.toString());
    }

    private void writeAttribute(String name, String value) throws XMLStreamException {
        if (StringUtils.isNotBlank((CharSequence)value)) {
            this.writer.writeAttribute(name, value);
        }
    }
}

