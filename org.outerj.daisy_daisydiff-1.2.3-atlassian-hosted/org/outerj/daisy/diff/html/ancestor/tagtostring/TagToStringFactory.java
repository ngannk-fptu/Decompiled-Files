/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor.tagtostring;

import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import org.outerj.daisy.diff.html.ancestor.TagChangeSematic;
import org.outerj.daisy.diff.html.ancestor.tagtostring.AnchorToString;
import org.outerj.daisy.diff.html.ancestor.tagtostring.NoContentTagToString;
import org.outerj.daisy.diff.html.ancestor.tagtostring.TagToString;
import org.outerj.daisy.diff.html.dom.TagNode;

public class TagToStringFactory {
    private static final Set<String> containerTags = new HashSet<String>();
    private static final Set<String> styleTags = new HashSet<String>();
    private static final String BUNDLE_NAME = "l10n/messages";

    public TagToString create(TagNode node, Locale locale) {
        TagChangeSematic sem = this.getChangeSemantic(node.getQName());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        if (node.getQName().equalsIgnoreCase("a")) {
            return new AnchorToString(node, sem, bundle);
        }
        if (node.getQName().equalsIgnoreCase("img")) {
            return new NoContentTagToString(node, sem, bundle);
        }
        return new TagToString(node, sem, bundle);
    }

    protected TagChangeSematic getChangeSemantic(String string) {
        if (containerTags.contains(string.toLowerCase())) {
            return TagChangeSematic.MOVED;
        }
        if (styleTags.contains(string.toLowerCase())) {
            return TagChangeSematic.STYLE;
        }
        return TagChangeSematic.UNKNOWN;
    }

    static {
        containerTags.add("html");
        containerTags.add("body");
        containerTags.add("p");
        containerTags.add("blockquote");
        containerTags.add("h1");
        containerTags.add("h2");
        containerTags.add("h3");
        containerTags.add("h4");
        containerTags.add("h5");
        containerTags.add("pre");
        containerTags.add("div");
        containerTags.add("ul");
        containerTags.add("ol");
        containerTags.add("li");
        containerTags.add("table");
        containerTags.add("tbody");
        containerTags.add("tr");
        containerTags.add("td");
        containerTags.add("th");
        containerTags.add("br");
        containerTags.add("hr");
        containerTags.add("code");
        containerTags.add("dl");
        containerTags.add("dt");
        containerTags.add("dd");
        containerTags.add("input");
        containerTags.add("form");
        containerTags.add("img");
        containerTags.add("span");
        containerTags.add("a");
        styleTags.add("i");
        styleTags.add("b");
        styleTags.add("strong");
        styleTags.add("em");
        styleTags.add("font");
        styleTags.add("big");
        styleTags.add("del");
        styleTags.add("tt");
        styleTags.add("sub");
        styleTags.add("sup");
        styleTags.add("strike");
    }
}

