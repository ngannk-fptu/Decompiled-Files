/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.RtfElementInterface;
import com.lowagie.text.SpecialSymbol;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.HyphenationEvent;
import java.util.ArrayList;
import java.util.Collection;

public class Phrase
extends ArrayList<Element>
implements TextElementArray {
    private static final long serialVersionUID = 2643594602455068231L;
    protected float leading = Float.NaN;
    protected Font font;
    protected HyphenationEvent hyphenation = null;

    public Phrase() {
        this(16.0f);
    }

    public Phrase(Phrase phrase) {
        this.addAll(phrase);
        this.leading = phrase.getLeading();
        this.font = phrase.getFont();
        this.setHyphenation(phrase.getHyphenation());
    }

    public Phrase(float leading) {
        this.leading = leading;
        this.font = new Font();
    }

    public Phrase(Chunk chunk) {
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }

    public Phrase(float leading, Chunk chunk) {
        this.leading = leading;
        super.add(chunk);
        this.font = chunk.getFont();
        this.setHyphenation(chunk.getHyphenation());
    }

    public Phrase(String string) {
        this(Float.NaN, string, new Font());
    }

    public Phrase(String string, Font font) {
        this(Float.NaN, string, font);
    }

    public Phrase(float leading, String string) {
        this(leading, string, new Font());
    }

    public Phrase(float leading, String string, Font font) {
        this.leading = leading;
        this.font = font;
        if (string != null && string.length() != 0) {
            super.add(new Chunk(string, font));
        }
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            for (Object o : this) {
                listener.add((Element)o);
            }
            return true;
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 11;
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> tmp = new ArrayList<Element>();
        for (Element element : this) {
            tmp.addAll(element.getChunks());
        }
        return tmp;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    @Override
    public void add(int index, Element element) {
        block7: {
            if (element == null) {
                return;
            }
            try {
                if (element.type() == 10) {
                    Chunk chunk = (Chunk)element;
                    if (!this.font.isStandardFont()) {
                        chunk.setFont(this.font.difference(chunk.getFont()));
                    }
                    if (this.hyphenation != null && chunk.getHyphenation() == null && !chunk.isEmpty()) {
                        chunk.setHyphenation(this.hyphenation);
                    }
                    super.add(index, chunk);
                    break block7;
                }
                if (element.type() == 11 || element.type() == 17 || element.type() == 29 || element.type() == 22 || element.type() == 55 || element.type() == 50) {
                    super.add(index, element);
                    break block7;
                }
                throw new ClassCastException(String.valueOf(element.type()));
            }
            catch (ClassCastException cce) {
                throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
            }
        }
    }

    @Override
    public boolean add(String o) {
        if (o == null) {
            return false;
        }
        return super.add(new Chunk(o, this.font));
    }

    @Override
    public boolean add(Element element) {
        if (element == null) {
            return false;
        }
        if (element instanceof RtfElementInterface) {
            return super.add(element);
        }
        try {
            switch (element.type()) {
                case 10: {
                    return this.addChunk((Chunk)element);
                }
                case 11: 
                case 12: {
                    Phrase phrase = (Phrase)element;
                    boolean success = true;
                    for (Object o1 : phrase) {
                        Element e = (Element)o1;
                        if (e instanceof Chunk) {
                            success &= this.addChunk((Chunk)e);
                            continue;
                        }
                        success &= this.add(e);
                    }
                    return success;
                }
                case 14: 
                case 17: 
                case 22: 
                case 23: 
                case 29: 
                case 50: 
                case 55: 
                case 56: {
                    return super.add(element);
                }
            }
            throw new ClassCastException(String.valueOf(element.type()));
        }
        catch (ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }

    @Override
    public boolean addAll(Collection<? extends Element> collection) {
        for (Element element : collection) {
            this.add(element);
        }
        return true;
    }

    protected boolean addChunk(Chunk chunk) {
        Font f = chunk.getFont();
        String c = chunk.getContent();
        if (this.font != null && !this.font.isStandardFont()) {
            f = this.font.difference(chunk.getFont());
        }
        if (this.size() > 0 && !chunk.hasAttributes()) {
            try {
                Chunk previous = (Chunk)this.get(this.size() - 1);
                if (!(previous.hasAttributes() || f != null && f.compareTo(previous.getFont()) != 0 || "".equals(previous.getContent().trim()) || "".equals(c.trim()))) {
                    previous.append(c);
                    return true;
                }
            }
            catch (ClassCastException previous) {
                // empty catch block
            }
        }
        Chunk newChunk = new Chunk(c, f);
        newChunk.setChunkAttributes(chunk.getChunkAttributes());
        if (this.hyphenation != null && newChunk.getHyphenation() == null && !newChunk.isEmpty()) {
            newChunk.setHyphenation(this.hyphenation);
        }
        return super.add(newChunk);
    }

    protected void addSpecial(Object object) {
        super.add((Element)object);
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading) && this.font != null) {
            return this.font.getCalculatedLeading(1.5f);
        }
        return this.leading;
    }

    public boolean hasLeading() {
        return !Float.isNaN(this.leading);
    }

    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        StringBuilder buf = new StringBuilder();
        for (Element o : this.getChunks()) {
            buf.append(((Object)o).toString());
        }
        return buf.toString();
    }

    @Override
    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                Element element = (Element)this.get(0);
                return element.type() == 10 && ((Chunk)element).isEmpty();
            }
        }
        return false;
    }

    public HyphenationEvent getHyphenation() {
        return this.hyphenation;
    }

    public void setHyphenation(HyphenationEvent hyphenation) {
        this.hyphenation = hyphenation;
    }

    private Phrase(boolean dummy) {
    }

    public static final Phrase getInstance(String string) {
        return Phrase.getInstance(16, string, new Font());
    }

    public static final Phrase getInstance(int leading, String string) {
        return Phrase.getInstance(leading, string, new Font());
    }

    public static final Phrase getInstance(int leading, String string, Font font) {
        Phrase p = new Phrase(true);
        p.setLeading(leading);
        p.font = font;
        if (font.getFamily() != 3 && font.getFamily() != 4 && font.getBaseFont() == null) {
            int index;
            while ((index = SpecialSymbol.index(string)) > -1) {
                if (index > 0) {
                    String firstPart = string.substring(0, index);
                    p.add(new Chunk(firstPart, font));
                    string = string.substring(index);
                }
                Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
                StringBuilder buf = new StringBuilder();
                buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                string = string.substring(1);
                while (SpecialSymbol.index(string) == 0) {
                    buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
                    string = string.substring(1);
                }
                p.add(new Chunk(buf.toString(), symbol));
            }
        }
        if (string != null && string.length() != 0) {
            p.add(new Chunk(string, font));
        }
        return p;
    }
}

