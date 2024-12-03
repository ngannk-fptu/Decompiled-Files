/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class Anchor
extends Phrase {
    private static final long serialVersionUID = -852278536049236911L;
    protected String name = null;
    protected String reference = null;

    public Anchor() {
        super(16.0f);
    }

    public Anchor(float leading) {
        super(leading);
    }

    public Anchor(Chunk chunk) {
        super(chunk);
    }

    public Anchor(String string) {
        super(string);
    }

    public Anchor(String string, Font font) {
        super(string, font);
    }

    public Anchor(float leading, Chunk chunk) {
        super(leading, chunk);
    }

    public Anchor(float leading, String string) {
        super(leading, string);
    }

    public Anchor(float leading, String string, Font font) {
        super(leading, string, font);
    }

    public Anchor(Phrase phrase) {
        super(phrase);
        if (phrase instanceof Anchor) {
            Anchor a = (Anchor)phrase;
            this.setName(a.name);
            this.setReference(a.reference);
        }
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            Iterator<Element> i = this.getChunks().iterator();
            boolean localDestination = this.reference != null && this.reference.startsWith("#");
            boolean notGotoOK = true;
            while (i.hasNext()) {
                Chunk chunk = (Chunk)i.next();
                if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                    chunk.setLocalDestination(this.name);
                    notGotoOK = false;
                }
                if (localDestination) {
                    chunk.setLocalGoto(this.reference.substring(1));
                }
                listener.add(chunk);
            }
            return true;
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> tmp = new ArrayList<Element>();
        Iterator i = this.iterator();
        boolean localDestination = this.reference != null && this.reference.startsWith("#");
        boolean notGotoOK = true;
        while (i.hasNext()) {
            Chunk chunk = (Chunk)i.next();
            if (this.name != null && notGotoOK && !chunk.isEmpty()) {
                chunk.setLocalDestination(this.name);
                notGotoOK = false;
            }
            if (localDestination) {
                chunk.setLocalGoto(this.reference.substring(1));
            } else if (this.reference != null) {
                chunk.setAnchor(this.reference);
            }
            tmp.add(chunk);
        }
        return tmp;
    }

    @Override
    public int type() {
        return 17;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName() {
        return this.name;
    }

    public String getReference() {
        return this.reference;
    }

    public URL getUrl() {
        try {
            return new URL(this.reference);
        }
        catch (MalformedURLException mue) {
            return null;
        }
    }
}

