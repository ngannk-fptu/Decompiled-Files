/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.inline;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class EditorEmoticonMarshaller
implements Marshaller<Emoticon> {
    public static final String EMOTICON_KEY_PREFIX = "content.emoticon.";
    static final String ELEMENT_NAME = "img";
    static final String CSS_CLASS = "emoticon";
    static final String FULL_CSS_CLASS_VALUE = "emoticon emoticon-";
    private final XMLOutputFactory xmlOutputFactory;
    private final EmoticonDisplayMapper emoticonDisplayMapper;
    private final UserI18NBeanFactory userI18NBeanFactory;

    public EditorEmoticonMarshaller(XMLOutputFactory xmlOutputFactory, EmoticonDisplayMapper emoticonDisplayMapper, UserI18NBeanFactory userI18NBeanFactory, MarshallingRegistry registry) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.emoticonDisplayMapper = emoticonDisplayMapper;
        this.userI18NBeanFactory = userI18NBeanFactory;
        registry.register(this, Emoticon.class, MarshallingType.EDITOR);
    }

    @Override
    public Streamable marshal(Emoticon emoticon, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeEmptyElement(ELEMENT_NAME);
                writer.writeAttribute("class", FULL_CSS_CLASS_VALUE + emoticon.getType());
                writer.writeAttribute("data-emoticon-name", emoticon.getType());
                writer.writeAttribute("border", "0");
                writer.writeAttribute("src", this.emoticonDisplayMapper.getRelativeImageUrl(emoticon));
                String text = this.userI18NBeanFactory.getI18NBean().getText(EMOTICON_KEY_PREFIX + emoticon.getType());
                writer.writeAttribute("alt", text);
                writer.writeAttribute("title", text);
                writer.writeAttribute("data-emoji-short-name", ":" + emoticon.getType() + ":");
                writer.close();
            }
            catch (XMLStreamException ex) {
                throw new IOException("Exception while writing the emoticon " + emoticon.getType() + " for the editor", ex);
            }
        };
    }
}

