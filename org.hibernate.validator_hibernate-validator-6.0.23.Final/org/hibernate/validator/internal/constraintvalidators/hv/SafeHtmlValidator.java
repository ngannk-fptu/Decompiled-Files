/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Node
 *  org.jsoup.parser.Parser
 *  org.jsoup.safety.Cleaner
 *  org.jsoup.safety.Whitelist
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.SafeHtml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

@Deprecated
public class SafeHtmlValidator
implements ConstraintValidator<SafeHtml, CharSequence> {
    private Whitelist whitelist;
    private String baseURI;

    public void initialize(SafeHtml safeHtmlAnnotation) {
        switch (safeHtmlAnnotation.whitelistType()) {
            case BASIC: {
                this.whitelist = Whitelist.basic();
                break;
            }
            case BASIC_WITH_IMAGES: {
                this.whitelist = Whitelist.basicWithImages();
                break;
            }
            case NONE: {
                this.whitelist = Whitelist.none();
                break;
            }
            case RELAXED: {
                this.whitelist = Whitelist.relaxed();
                break;
            }
            case SIMPLE_TEXT: {
                this.whitelist = Whitelist.simpleText();
            }
        }
        this.baseURI = safeHtmlAnnotation.baseURI();
        this.whitelist.addTags(safeHtmlAnnotation.additionalTags());
        for (SafeHtml.Tag tag : safeHtmlAnnotation.additionalTagsWithAttributes()) {
            this.whitelist.addTags(new String[]{tag.name()});
            if (tag.attributes().length > 0) {
                this.whitelist.addAttributes(tag.name(), tag.attributes());
            }
            for (SafeHtml.Attribute attribute : tag.attributesWithProtocols()) {
                this.whitelist.addAttributes(tag.name(), new String[]{attribute.name()});
                if (attribute.protocols().length <= 0) continue;
                this.whitelist.addProtocols(tag.name(), attribute.name(), attribute.protocols());
            }
        }
    }

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return new Cleaner(this.whitelist).isValid(this.getFragmentAsDocument(value));
    }

    private Document getFragmentAsDocument(CharSequence value) {
        Document fragment = Jsoup.parse((String)value.toString(), (String)this.baseURI, (Parser)Parser.xmlParser());
        Document document = Document.createShell((String)this.baseURI);
        List childNodes = fragment.childNodes();
        for (Node node : childNodes) {
            document.body().appendChild(node.clone());
        }
        return document;
    }
}

