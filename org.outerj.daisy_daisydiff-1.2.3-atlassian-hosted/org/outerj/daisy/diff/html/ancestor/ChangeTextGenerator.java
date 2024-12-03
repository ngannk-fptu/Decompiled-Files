/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.html.ancestor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.outerj.daisy.diff.html.ancestor.AncestorComparator;
import org.outerj.daisy.diff.html.ancestor.ChangeText;
import org.outerj.daisy.diff.html.ancestor.tagtostring.TagToString;
import org.outerj.daisy.diff.html.ancestor.tagtostring.TagToStringFactory;
import org.outerj.daisy.diff.html.dom.TagNode;
import org.outerj.daisy.diff.html.modification.HtmlLayoutChange;

public class ChangeTextGenerator {
    private List<HtmlLayoutChange> htmlLayoutChanges = null;
    private AncestorComparator ancestorComparator;
    private AncestorComparator other;
    private TagToStringFactory factory;
    private Locale locale;
    private static final int MAX_OUTPUT_LINE_LENGTH = 55;

    public ChangeTextGenerator(AncestorComparator ancestorComparator, AncestorComparator other, Locale locale) {
        this.ancestorComparator = ancestorComparator;
        this.other = other;
        this.factory = new TagToStringFactory();
        this.locale = locale;
        this.htmlLayoutChanges = new ArrayList<HtmlLayoutChange>();
    }

    public ChangeText getChanged(RangeDifference ... differences) {
        ChangeText txt = new ChangeText(55);
        boolean rootlistopened = false;
        if (differences.length > 1) {
            txt.addHtml("<ul class='changelist'>");
            rootlistopened = true;
        }
        for (int j = 0; j < differences.length; ++j) {
            int i;
            RangeDifference d = differences[j];
            boolean lvl1listopened = false;
            if (rootlistopened) {
                txt.addHtml("<li>");
            }
            if (d.leftLength() + d.rightLength() > 1) {
                txt.addHtml("<ul class='changelist'>");
                lvl1listopened = true;
            }
            for (i = d.leftStart(); i < d.leftEnd(); ++i) {
                if (lvl1listopened) {
                    txt.addHtml("<li>");
                }
                this.addTagOld(txt, this.other.getAncestor(i));
                if (!lvl1listopened) continue;
                txt.addHtml("</li>");
            }
            for (i = d.rightStart(); i < d.rightEnd(); ++i) {
                if (lvl1listopened) {
                    txt.addHtml("<li>");
                }
                this.addTagNew(txt, this.getAncestor(i));
                if (!lvl1listopened) continue;
                txt.addHtml("</li>");
            }
            if (lvl1listopened) {
                txt.addHtml("</ul>");
            }
            if (!rootlistopened) continue;
            txt.addHtml("</li>");
        }
        if (rootlistopened) {
            txt.addHtml("</ul>");
        }
        return txt;
    }

    private void addTagOld(ChangeText txt, TagNode ancestor) {
        TagToString tagToString = this.factory.create(ancestor, this.locale);
        tagToString.getRemovedDescription(txt);
        this.htmlLayoutChanges.add(tagToString.getHtmlLayoutChange());
    }

    private void addTagNew(ChangeText txt, TagNode ancestor) {
        TagToString tagToString = this.factory.create(ancestor, this.locale);
        tagToString.getAddedDescription(txt);
        this.htmlLayoutChanges.add(tagToString.getHtmlLayoutChange());
    }

    private TagNode getAncestor(int i) {
        return this.ancestorComparator.getAncestor(i);
    }

    public List<HtmlLayoutChange> getHtmlLayoutChanges() {
        return this.htmlLayoutChanges;
    }
}

