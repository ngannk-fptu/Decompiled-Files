/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import java.util.ArrayList;

public class Meta
implements Element {
    private int type;
    private StringBuffer content;

    Meta(int type, String content) {
        this.type = type;
        this.content = new StringBuffer(content);
    }

    public Meta(String tag, String content) {
        this.type = Meta.getType(tag);
        this.content = new StringBuffer(content);
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return this.type;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return new ArrayList<Element>();
    }

    @Override
    public boolean isContent() {
        return false;
    }

    @Override
    public boolean isNestable() {
        return false;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public String getContent() {
        return this.content.toString();
    }

    public String getName() {
        switch (this.type) {
            case 2: {
                return "subject";
            }
            case 3: {
                return "keywords";
            }
            case 4: {
                return "author";
            }
            case 1: {
                return "title";
            }
            case 5: {
                return "producer";
            }
            case 6: {
                return "creationdate";
            }
        }
        return "unknown";
    }

    public static int getType(String tag) {
        if ("subject".equals(tag)) {
            return 2;
        }
        if ("keywords".equals(tag)) {
            return 3;
        }
        if ("author".equals(tag)) {
            return 4;
        }
        if ("title".equals(tag)) {
            return 1;
        }
        if ("producer".equals(tag)) {
            return 5;
        }
        if ("creationdate".equals(tag)) {
            return 6;
        }
        return 0;
    }
}

