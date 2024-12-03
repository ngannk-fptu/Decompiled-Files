/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.Box;

class HTMLOutline {
    private static final Pattern HEADING = Pattern.compile("h([1-6])", 2);
    private static final Pattern ROOT = Pattern.compile("blockquote|details|fieldset|figure|td", 2);
    private static final Pattern WS = Pattern.compile("\\s+");
    private static final int MAX_NAME_LENGTH = 200;
    private final HTMLOutline parent;
    private final int level;
    private final ITextOutputDevice.Bookmark bookmark;

    private HTMLOutline() {
        this(0, "root", null);
    }

    private HTMLOutline(int level, String name, HTMLOutline parent) {
        this.level = level;
        this.bookmark = new ITextOutputDevice.Bookmark(name, "");
        this.parent = parent;
        if (parent != null) {
            parent.bookmark.addChild(this.bookmark);
        }
    }

    public static List<ITextOutputDevice.Bookmark> generate(Element context, Box box) {
        HTMLOutline root;
        NodeIterator iterator = NestedSectioningFilter.iterator(context);
        if (iterator == null) {
            return Collections.emptyList();
        }
        HTMLOutline current = root = new HTMLOutline();
        IdentityHashMap<Element, ITextOutputDevice.Bookmark> map = new IdentityHashMap<Element, ITextOutputDevice.Bookmark>();
        Element element = (Element)iterator.nextNode();
        while (element != null) {
            block6: {
                int level;
                try {
                    level = Integer.parseInt(HTMLOutline.getOutlineLevel(element));
                    if (level < 1) {
                    }
                }
                catch (NumberFormatException e) {}
                break block6;
                String name = HTMLOutline.getBookmarkName(element);
                while (current.level >= level) {
                    current = current.parent;
                }
                current = new HTMLOutline(level, name, current);
                map.put(element, current.bookmark);
            }
            element = (Element)iterator.nextNode();
        }
        HTMLOutline.initBoxRefs(map, box);
        return root.bookmark.getChildren();
    }

    private static void initBoxRefs(Map<Element, ITextOutputDevice.Bookmark> map, Box box) {
        ITextOutputDevice.Bookmark bookmark = map.get(box.getElement());
        if (bookmark != null) {
            bookmark.setBox(box);
        }
        int len = box.getChildCount();
        for (int i = 0; i < len; ++i) {
            HTMLOutline.initBoxRefs(map, box.getChild(i));
        }
    }

    private static String getBookmarkName(Element element) {
        String name = element.getAttribute("data-pdf-bookmark-name").trim();
        if (name.isEmpty()) {
            name = element.getTextContent();
        }
        if ((name = WS.matcher(name.trim()).replaceAll(" ")).length() > 200) {
            name = name.substring(0, 200);
        }
        return name;
    }

    static String getOutlineLevel(Element element) {
        String bookmark = element.getAttribute("data-pdf-bookmark").trim();
        if (bookmark.isEmpty()) {
            Matcher heading = HEADING.matcher(element.getTagName());
            bookmark = heading.matches() ? heading.group(1) : (ROOT.matcher(element.getTagName()).matches() ? "exclude" : "none");
        }
        return bookmark;
    }

    private static class NestedSectioningFilter
    implements NodeFilter {
        static final NestedSectioningFilter INSTANCE = new NestedSectioningFilter();

        private NestedSectioningFilter() {
        }

        static NodeIterator iterator(Element root) {
            Document ownerDocument = root.getOwnerDocument();
            return ownerDocument instanceof DocumentTraversal ? ((DocumentTraversal)((Object)ownerDocument)).createNodeIterator(root, 1, INSTANCE, true) : null;
        }

        @Override
        public short acceptNode(Node n) {
            String outlineLevel = HTMLOutline.getOutlineLevel((Element)n);
            if (outlineLevel.equalsIgnoreCase("none")) {
                return 3;
            }
            return outlineLevel.equalsIgnoreCase("exclude") ? (short)2 : 1;
        }
    }
}

