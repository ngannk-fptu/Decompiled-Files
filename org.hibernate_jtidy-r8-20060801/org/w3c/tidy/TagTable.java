/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.tidy.Anchor;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Dict;
import org.w3c.tidy.Node;
import org.w3c.tidy.Parser;
import org.w3c.tidy.ParserImpl;
import org.w3c.tidy.TagCheckImpl;

public final class TagTable {
    public static final Dict XML_TAGS = new Dict(null, 3103, 8, null, null);
    private static final Dict[] TAGS = new Dict[]{new Dict("html", 3103, 0x208002, ParserImpl.HTML, TagCheckImpl.HTML), new Dict("head", 3103, 0x208002, ParserImpl.HEAD, null), new Dict("title", 3103, 4, ParserImpl.TITLE, null), new Dict("base", 3103, 5, ParserImpl.EMPTY, null), new Dict("link", 3103, 5, ParserImpl.EMPTY, TagCheckImpl.LINK), new Dict("meta", 3103, 5, ParserImpl.EMPTY, TagCheckImpl.META), new Dict("style", 28, 4, ParserImpl.SCRIPT, TagCheckImpl.STYLE), new Dict("script", 28, 131100, ParserImpl.SCRIPT, TagCheckImpl.SCRIPT), new Dict("server", 64, 131100, ParserImpl.SCRIPT, null), new Dict("body", 3103, 0x208002, ParserImpl.BODY, null), new Dict("frameset", 16, 8194, ParserImpl.FRAMESET, null), new Dict("p", 3103, 32776, ParserImpl.INLINE, null), new Dict("h1", 3103, 16392, ParserImpl.INLINE, null), new Dict("h2", 3103, 16392, ParserImpl.INLINE, null), new Dict("h3", 3103, 16392, ParserImpl.INLINE, null), new Dict("h4", 3103, 16392, ParserImpl.INLINE, null), new Dict("h5", 3103, 16392, ParserImpl.INLINE, null), new Dict("h6", 3103, 16392, ParserImpl.INLINE, null), new Dict("ul", 3103, 8, ParserImpl.LIST, null), new Dict("ol", 3103, 8, ParserImpl.LIST, null), new Dict("dl", 3103, 8, ParserImpl.DEFLIST, null), new Dict("dir", 26, 524296, ParserImpl.LIST, null), new Dict("menu", 26, 524296, ParserImpl.LIST, null), new Dict("pre", 3103, 8, ParserImpl.PRE, null), new Dict("listing", 3103, 524296, ParserImpl.PRE, null), new Dict("xmp", 3103, 524296, ParserImpl.PRE, null), new Dict("plaintext", 3103, 524296, ParserImpl.PRE, null), new Dict("address", 3103, 8, ParserImpl.BLOCK, null), new Dict("blockquote", 3103, 8, ParserImpl.BLOCK, null), new Dict("form", 3103, 8, ParserImpl.BLOCK, TagCheckImpl.FORM), new Dict("isindex", 26, 9, ParserImpl.EMPTY, null), new Dict("fieldset", 28, 8, ParserImpl.BLOCK, null), new Dict("table", 30, 8, ParserImpl.TABLETAG, TagCheckImpl.TABLE), new Dict("hr", 1055, 9, ParserImpl.EMPTY, TagCheckImpl.HR), new Dict("div", 30, 8, ParserImpl.BLOCK, null), new Dict("multicol", 64, 8, ParserImpl.BLOCK, null), new Dict("nosave", 64, 8, ParserImpl.BLOCK, null), new Dict("layer", 64, 8, ParserImpl.BLOCK, null), new Dict("ilayer", 64, 16, ParserImpl.INLINE, null), new Dict("nolayer", 64, 131096, ParserImpl.BLOCK, null), new Dict("align", 64, 8, ParserImpl.BLOCK, null), new Dict("center", 26, 8, ParserImpl.BLOCK, null), new Dict("ins", 28, 131096, ParserImpl.INLINE, null), new Dict("del", 28, 131096, ParserImpl.INLINE, null), new Dict("li", 3103, 294944, ParserImpl.BLOCK, null), new Dict("dt", 3103, 294976, ParserImpl.INLINE, null), new Dict("dd", 3103, 294976, ParserImpl.BLOCK, null), new Dict("caption", 30, 128, ParserImpl.INLINE, TagCheckImpl.CAPTION), new Dict("colgroup", 28, 32896, ParserImpl.COLGROUP, null), new Dict("col", 28, 129, ParserImpl.EMPTY, null), new Dict("thead", 28, 33152, ParserImpl.ROWGROUP, null), new Dict("tfoot", 28, 33152, ParserImpl.ROWGROUP, null), new Dict("tbody", 28, 33152, ParserImpl.ROWGROUP, null), new Dict("tr", 30, 32896, ParserImpl.ROW, null), new Dict("td", 30, 295424, ParserImpl.BLOCK, TagCheckImpl.TABLECELL), new Dict("th", 30, 295424, ParserImpl.BLOCK, TagCheckImpl.TABLECELL), new Dict("q", 28, 16, ParserImpl.INLINE, null), new Dict("a", 3103, 16, ParserImpl.INLINE, TagCheckImpl.ANCHOR), new Dict("br", 3103, 17, ParserImpl.EMPTY, null), new Dict("img", 3103, 65553, ParserImpl.EMPTY, TagCheckImpl.IMG), new Dict("object", 28, 71700, ParserImpl.BLOCK, null), new Dict("applet", 26, 71696, ParserImpl.BLOCK, null), new Dict("servlet", 256, 71696, ParserImpl.BLOCK, null), new Dict("param", 30, 17, ParserImpl.EMPTY, null), new Dict("embed", 64, 65553, ParserImpl.EMPTY, null), new Dict("noembed", 64, 16, ParserImpl.INLINE, null), new Dict("iframe", 8, 16, ParserImpl.BLOCK, null), new Dict("frame", 16, 8193, ParserImpl.EMPTY, null), new Dict("noframes", 24, 8200, ParserImpl.NOFRAMES, null), new Dict("noscript", 28, 131096, ParserImpl.BLOCK, null), new Dict("b", 1055, 16, ParserImpl.INLINE, null), new Dict("i", 1055, 16, ParserImpl.INLINE, null), new Dict("u", 26, 16, ParserImpl.INLINE, null), new Dict("tt", 1055, 16, ParserImpl.INLINE, null), new Dict("s", 26, 16, ParserImpl.INLINE, null), new Dict("strike", 26, 16, ParserImpl.INLINE, null), new Dict("big", 28, 16, ParserImpl.INLINE, null), new Dict("small", 28, 16, ParserImpl.INLINE, null), new Dict("sub", 28, 16, ParserImpl.INLINE, null), new Dict("sup", 28, 16, ParserImpl.INLINE, null), new Dict("em", 3103, 16, ParserImpl.INLINE, null), new Dict("strong", 3103, 16, ParserImpl.INLINE, null), new Dict("dfn", 3103, 16, ParserImpl.INLINE, null), new Dict("code", 3103, 16, ParserImpl.INLINE, null), new Dict("samp", 3103, 16, ParserImpl.INLINE, null), new Dict("kbd", 3103, 16, ParserImpl.INLINE, null), new Dict("var", 3103, 16, ParserImpl.INLINE, null), new Dict("cite", 3103, 16, ParserImpl.INLINE, null), new Dict("abbr", 28, 16, ParserImpl.INLINE, null), new Dict("acronym", 28, 16, ParserImpl.INLINE, null), new Dict("span", 30, 16, ParserImpl.INLINE, null), new Dict("blink", 448, 16, ParserImpl.INLINE, null), new Dict("nobr", 448, 16, ParserImpl.INLINE, null), new Dict("wbr", 448, 17, ParserImpl.EMPTY, null), new Dict("marquee", 128, 32784, ParserImpl.INLINE, null), new Dict("bgsound", 128, 5, ParserImpl.EMPTY, null), new Dict("comment", 128, 16, ParserImpl.INLINE, null), new Dict("spacer", 64, 17, ParserImpl.EMPTY, null), new Dict("keygen", 64, 17, ParserImpl.EMPTY, null), new Dict("nolayer", 64, 131096, ParserImpl.BLOCK, null), new Dict("ilayer", 64, 16, ParserImpl.INLINE, null), new Dict("map", 28, 16, ParserImpl.BLOCK, TagCheckImpl.MAP), new Dict("area", 1055, 9, ParserImpl.EMPTY, TagCheckImpl.AREA), new Dict("input", 3103, 65553, ParserImpl.EMPTY, null), new Dict("select", 3103, 1040, ParserImpl.SELECT, null), new Dict("option", 3103, 33792, ParserImpl.TEXT, null), new Dict("optgroup", 28, 33792, ParserImpl.OPTGROUP, null), new Dict("textarea", 3103, 1040, ParserImpl.TEXT, null), new Dict("label", 28, 16, ParserImpl.INLINE, null), new Dict("legend", 28, 16, ParserImpl.INLINE, null), new Dict("button", 28, 16, ParserImpl.INLINE, null), new Dict("basefont", 26, 17, ParserImpl.EMPTY, null), new Dict("font", 26, 16, ParserImpl.INLINE, null), new Dict("bdo", 28, 16, ParserImpl.INLINE, null), new Dict("ruby", 1024, 16, ParserImpl.INLINE, null), new Dict("rbc", 1024, 16, ParserImpl.INLINE, null), new Dict("rtc", 1024, 16, ParserImpl.INLINE, null), new Dict("rb", 1024, 16, ParserImpl.INLINE, null), new Dict("rt", 1024, 16, ParserImpl.INLINE, null), new Dict("", 1024, 16, ParserImpl.INLINE, null), new Dict("rp", 1024, 16, ParserImpl.INLINE, null)};
    protected Dict tagHtml;
    protected Dict tagHead;
    protected Dict tagBody;
    protected Dict tagFrameset;
    protected Dict tagFrame;
    protected Dict tagIframe;
    protected Dict tagNoframes;
    protected Dict tagMeta;
    protected Dict tagTitle;
    protected Dict tagBase;
    protected Dict tagHr;
    protected Dict tagPre;
    protected Dict tagListing;
    protected Dict tagH1;
    protected Dict tagH2;
    protected Dict tagP;
    protected Dict tagUl;
    protected Dict tagOl;
    protected Dict tagDir;
    protected Dict tagLi;
    protected Dict tagDt;
    protected Dict tagDd;
    protected Dict tagDl;
    protected Dict tagTd;
    protected Dict tagTh;
    protected Dict tagTr;
    protected Dict tagCol;
    protected Dict tagColgroup;
    protected Dict tagBr;
    protected Dict tagA;
    protected Dict tagLink;
    protected Dict tagB;
    protected Dict tagI;
    protected Dict tagStrong;
    protected Dict tagEm;
    protected Dict tagBig;
    protected Dict tagSmall;
    protected Dict tagParam;
    protected Dict tagOption;
    protected Dict tagOptgroup;
    protected Dict tagImg;
    protected Dict tagMap;
    protected Dict tagArea;
    protected Dict tagNobr;
    protected Dict tagWbr;
    protected Dict tagFont;
    protected Dict tagSpacer;
    protected Dict tagLayer;
    protected Dict tagCenter;
    protected Dict tagStyle;
    protected Dict tagScript;
    protected Dict tagNoscript;
    protected Dict tagTable;
    protected Dict tagCaption;
    protected Dict tagForm;
    protected Dict tagTextarea;
    protected Dict tagBlockquote;
    protected Dict tagApplet;
    protected Dict tagObject;
    protected Dict tagDiv;
    protected Dict tagSpan;
    protected Dict tagInput;
    protected Dict tagQ;
    protected Dict tagBlink;
    protected Anchor anchorList;
    private Configuration configuration;
    private Map tagHashtable = new Hashtable();

    protected TagTable() {
        for (int i = 0; i < TAGS.length; ++i) {
            this.install(TAGS[i]);
        }
        this.tagHtml = this.lookup("html");
        this.tagHead = this.lookup("head");
        this.tagBody = this.lookup("body");
        this.tagFrameset = this.lookup("frameset");
        this.tagFrame = this.lookup("frame");
        this.tagIframe = this.lookup("iframe");
        this.tagNoframes = this.lookup("noframes");
        this.tagMeta = this.lookup("meta");
        this.tagTitle = this.lookup("title");
        this.tagBase = this.lookup("base");
        this.tagHr = this.lookup("hr");
        this.tagPre = this.lookup("pre");
        this.tagListing = this.lookup("listing");
        this.tagH1 = this.lookup("h1");
        this.tagH2 = this.lookup("h2");
        this.tagP = this.lookup("p");
        this.tagUl = this.lookup("ul");
        this.tagOl = this.lookup("ol");
        this.tagDir = this.lookup("dir");
        this.tagLi = this.lookup("li");
        this.tagDt = this.lookup("dt");
        this.tagDd = this.lookup("dd");
        this.tagDl = this.lookup("dl");
        this.tagTd = this.lookup("td");
        this.tagTh = this.lookup("th");
        this.tagTr = this.lookup("tr");
        this.tagCol = this.lookup("col");
        this.tagColgroup = this.lookup("colgroup");
        this.tagBr = this.lookup("br");
        this.tagA = this.lookup("a");
        this.tagLink = this.lookup("link");
        this.tagB = this.lookup("b");
        this.tagI = this.lookup("i");
        this.tagStrong = this.lookup("strong");
        this.tagEm = this.lookup("em");
        this.tagBig = this.lookup("big");
        this.tagSmall = this.lookup("small");
        this.tagParam = this.lookup("param");
        this.tagOption = this.lookup("option");
        this.tagOptgroup = this.lookup("optgroup");
        this.tagImg = this.lookup("img");
        this.tagMap = this.lookup("map");
        this.tagArea = this.lookup("area");
        this.tagNobr = this.lookup("nobr");
        this.tagWbr = this.lookup("wbr");
        this.tagFont = this.lookup("font");
        this.tagSpacer = this.lookup("spacer");
        this.tagLayer = this.lookup("layer");
        this.tagCenter = this.lookup("center");
        this.tagStyle = this.lookup("style");
        this.tagScript = this.lookup("script");
        this.tagNoscript = this.lookup("noscript");
        this.tagTable = this.lookup("table");
        this.tagCaption = this.lookup("caption");
        this.tagForm = this.lookup("form");
        this.tagTextarea = this.lookup("textarea");
        this.tagBlockquote = this.lookup("blockquote");
        this.tagApplet = this.lookup("applet");
        this.tagObject = this.lookup("object");
        this.tagDiv = this.lookup("div");
        this.tagSpan = this.lookup("span");
        this.tagInput = this.lookup("input");
        this.tagQ = this.lookup("q");
        this.tagBlink = this.lookup("blink");
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Dict lookup(String name) {
        return (Dict)this.tagHashtable.get(name);
    }

    public Dict install(Dict dict) {
        Dict d = (Dict)this.tagHashtable.get(dict.name);
        if (d != null) {
            d.versions = dict.versions;
            d.model |= dict.model;
            d.setParser(dict.getParser());
            d.setChkattrs(dict.getChkattrs());
            return d;
        }
        this.tagHashtable.put(dict.name, dict);
        return dict;
    }

    public boolean findTag(Node node) {
        Dict np;
        if (this.configuration != null && this.configuration.xmlTags) {
            node.tag = XML_TAGS;
            return true;
        }
        if (node.element != null && (np = this.lookup(node.element)) != null) {
            node.tag = np;
            return true;
        }
        return false;
    }

    public Parser findParser(Node node) {
        Dict np;
        if (node.element != null && (np = this.lookup(node.element)) != null) {
            return np.getParser();
        }
        return null;
    }

    boolean isAnchorElement(Node node) {
        return node.tag == this.tagA || node.tag == this.tagApplet || node.tag == this.tagForm || node.tag == this.tagFrame || node.tag == this.tagIframe || node.tag == this.tagImg || node.tag == this.tagMap;
    }

    public void defineTag(short tagType, String name) {
        Parser tagParser;
        int model;
        switch (tagType) {
            case 4: {
                model = 8;
                tagParser = ParserImpl.BLOCK;
                break;
            }
            case 1: {
                model = 1;
                tagParser = ParserImpl.BLOCK;
                break;
            }
            case 8: {
                model = 8;
                tagParser = ParserImpl.PRE;
                break;
            }
            default: {
                model = 16;
                tagParser = ParserImpl.INLINE;
            }
        }
        this.install(new Dict(name, 448, model, tagParser, null));
    }

    List findAllDefinedTag(short tagType) {
        ArrayList<String> tagNames = new ArrayList<String>();
        Iterator iterator = this.tagHashtable.values().iterator();
        while (iterator.hasNext()) {
            Dict curDictEntry = (Dict)iterator.next();
            if (curDictEntry == null) continue;
            switch (tagType) {
                case 1: {
                    if (curDictEntry.versions != 448 || (curDictEntry.model & 1) != 1 || curDictEntry == this.tagWbr) break;
                    tagNames.add(curDictEntry.name);
                    break;
                }
                case 2: {
                    if (curDictEntry.versions != 448 || (curDictEntry.model & 0x10) != 16 || curDictEntry == this.tagBlink || curDictEntry == this.tagNobr || curDictEntry == this.tagWbr) break;
                    tagNames.add(curDictEntry.name);
                    break;
                }
                case 4: {
                    if (curDictEntry.versions != 448 || (curDictEntry.model & 8) != 8 || curDictEntry.getParser() != ParserImpl.BLOCK) break;
                    tagNames.add(curDictEntry.name);
                    break;
                }
                case 8: {
                    if (curDictEntry.versions != 448 || (curDictEntry.model & 8) != 8 || curDictEntry.getParser() != ParserImpl.PRE) break;
                    tagNames.add(curDictEntry.name);
                }
            }
        }
        return tagNames;
    }

    public void freeAttrs(Node node) {
        while (node.attributes != null) {
            AttVal av = node.attributes;
            if ("id".equalsIgnoreCase(av.attribute) || "name".equalsIgnoreCase(av.attribute) && this.isAnchorElement(node)) {
                this.removeAnchorByNode(node);
            }
            node.attributes = av.next;
        }
    }

    void removeAnchorByNode(Node node) {
        Anchor delme = null;
        Anchor found = null;
        Anchor prev = null;
        Anchor next = null;
        found = this.anchorList;
        while (found != null) {
            next = found.next;
            if (found.node == node) {
                if (prev != null) {
                    prev.next = next;
                } else {
                    this.anchorList = next;
                }
                delme = found;
            } else {
                prev = found;
            }
            found = found.next;
        }
        if (delme != null) {
            delme = null;
        }
    }

    Anchor newAnchor() {
        Anchor a = new Anchor();
        return a;
    }

    Anchor addAnchor(String name, Node node) {
        Anchor a = this.newAnchor();
        a.name = name;
        a.node = node;
        if (this.anchorList == null) {
            this.anchorList = a;
        } else {
            Anchor here = this.anchorList;
            while (here.next != null) {
                here = here.next;
            }
            here.next = a;
        }
        return this.anchorList;
    }

    Node getNodeByAnchor(String name) {
        Anchor found = this.anchorList;
        while (found != null && !name.equalsIgnoreCase(found.name)) {
            found = found.next;
        }
        if (found != null) {
            return found.node;
        }
        return null;
    }

    void freeAnchors() {
        this.anchorList = null;
    }
}

