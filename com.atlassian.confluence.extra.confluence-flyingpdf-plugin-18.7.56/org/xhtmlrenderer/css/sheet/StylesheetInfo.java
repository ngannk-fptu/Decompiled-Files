/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import java.util.ArrayList;
import java.util.List;
import org.xhtmlrenderer.css.sheet.Stylesheet;

public class StylesheetInfo {
    private Stylesheet stylesheet = null;
    private String title;
    private String uri;
    private int origin = 0;
    private String type;
    private List mediaTypes = new ArrayList();
    private String content;
    public static final int USER_AGENT = 0;
    public static final int USER = 1;
    public static final int AUTHOR = 2;

    public boolean appliesToMedia(String m) {
        return m.toLowerCase().equals("all") || this.mediaTypes.contains("all") || this.mediaTypes.contains(m.toLowerCase());
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMedia(String media) {
        String[] mediaTypes = media.split(",");
        ArrayList<String> l = new ArrayList<String>(mediaTypes.length);
        for (int i = 0; i < mediaTypes.length; ++i) {
            l.add(mediaTypes[i].trim().toLowerCase());
        }
        this.mediaTypes = l;
    }

    public void setMedia(List mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public void addMedium(String medium) {
        this.mediaTypes.add(medium);
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getUri() {
        return this.uri;
    }

    public List getMedia() {
        return this.mediaTypes;
    }

    public int getOrigin() {
        return this.origin;
    }

    public String getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public Stylesheet getStylesheet() {
        return this.stylesheet;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isInline() {
        return this.content != null;
    }
}

