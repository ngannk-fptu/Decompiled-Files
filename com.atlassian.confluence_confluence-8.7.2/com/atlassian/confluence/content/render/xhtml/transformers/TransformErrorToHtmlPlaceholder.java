/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageEmbeddedImageUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroUtil;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.util.HtmlUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.xml.stream.XMLEventReader;
import org.apache.commons.lang3.StringUtils;

public class TransformErrorToHtmlPlaceholder
implements FragmentTransformationErrorHandler {
    private final PlaceholderUrlFactory placeholderUrlFactory;
    private static final String LINK_ELEMENT = "<" + StorageLinkConstants.LINK_ELEMENT.getPrefix() + ":" + StorageLinkConstants.LINK_ELEMENT.getLocalPart();
    private static final String IMAGE_ELEMENT = "<" + StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getPrefix() + ":" + StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getLocalPart();

    public TransformErrorToHtmlPlaceholder(PlaceholderUrlFactory placeholderUrlFactory) {
        this.placeholderUrlFactory = placeholderUrlFactory;
    }

    @Override
    public String handle(XMLEventReader erroneousFragmentReader, Exception transformationException) {
        String encodedXml;
        String erroneousXml = StaxUtils.toString(erroneousFragmentReader);
        try {
            encodedXml = URLEncoder.encode(erroneousXml, "UTF-8");
        }
        catch (UnsupportedEncodingException encodingException) {
            throw new RuntimeException(encodingException);
        }
        String errorTextI18nKey = this.getErrorTextI18nKey(erroneousXml);
        return String.format("<img src=\"" + this.placeholderUrlFactory.getUrlForErrorPlaceholder(errorTextI18nKey) + "\" title=\"%s\" class=\"%s\" data-encoded-xml=\"%s\" />", HtmlUtil.htmlEncode(StringUtils.defaultString((String)transformationException.getMessage())), "transform-error", encodedXml);
    }

    String getErrorTextI18nKey(String erroneousXml) {
        if (erroneousXml.startsWith(LINK_ELEMENT)) {
            return "editor.placeholder.broken.link";
        }
        if (erroneousXml.startsWith(IMAGE_ELEMENT)) {
            return "editor.placeholder.broken.image";
        }
        if (StorageMacroUtil.isMacroElement(erroneousXml)) {
            return "editor.placeholder.broken.macro";
        }
        return "editor.placeholder.error";
    }
}

