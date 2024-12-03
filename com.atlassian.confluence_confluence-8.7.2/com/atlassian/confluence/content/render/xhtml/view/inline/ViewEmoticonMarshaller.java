/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.inline;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewEmoticonMarshaller
implements Marshaller<Emoticon> {
    private final XMLOutputFactory xmlOutputFactory;
    private final EmoticonDisplayMapper emoticonDisplayMapper;
    private final UserI18NBeanFactory userI18NBeanFactory;
    private final HrefEvaluator hrefEvaluator;

    public ViewEmoticonMarshaller(XMLOutputFactory xmlOutputFactory, EmoticonDisplayMapper emoticonDisplayMapper, UserI18NBeanFactory userI18NBeanFactory, HrefEvaluator hrefEvaluator) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.emoticonDisplayMapper = emoticonDisplayMapper;
        this.userI18NBeanFactory = userI18NBeanFactory;
        this.hrefEvaluator = hrefEvaluator;
    }

    @Override
    public Streamable marshal(Emoticon emoticon, ConversionContext conversionContext) throws XhtmlException {
        String hrefSrc = this.hrefEvaluator.createHref(conversionContext, new WebLink(this.emoticonDisplayMapper.getRelativeImageUrl(emoticon)), null);
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeEmptyElement("img");
                writer.writeAttribute("class", "emoticon emoticon-" + emoticon.getType());
                writer.writeAttribute("src", hrefSrc);
                writer.writeAttribute("data-emoticon-name", emoticon.getType());
                String text = this.userI18NBeanFactory.getI18NBean().getText("content.emoticon." + emoticon.getType());
                writer.writeAttribute("alt", text);
                writer.writeAttribute("data-emoji-short-name", ":" + emoticon.getType() + ":");
                writer.close();
            }
            catch (XMLStreamException ex) {
                throw new IOException("Exception while writing the emoticon " + emoticon.getType() + " for display", ex);
            }
        };
    }
}

