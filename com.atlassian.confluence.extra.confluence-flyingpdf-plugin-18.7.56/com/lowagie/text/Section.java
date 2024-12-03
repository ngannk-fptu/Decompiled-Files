/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.LargeElement;
import com.lowagie.text.MarkedObject;
import com.lowagie.text.MarkedSection;
import com.lowagie.text.Paragraph;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Section
extends ArrayList<Element>
implements TextElementArray,
LargeElement {
    public static final int NUMBERSTYLE_DOTTED = 0;
    public static final int NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT = 1;
    private static final long serialVersionUID = 3324172577544748043L;
    protected Paragraph title;
    protected String bookmarkTitle;
    protected int numberDepth;
    protected int numberStyle = 0;
    protected float indentationLeft;
    protected float indentationRight;
    protected float indentation;
    protected boolean bookmarkOpen = true;
    protected boolean triggerNewPage = false;
    protected int subsections = 0;
    protected List<Integer> numbers = null;
    protected boolean complete = true;
    protected boolean addedCompletely = false;
    protected boolean notAddedYet = true;

    protected Section() {
        this.title = new Paragraph();
        this.numberDepth = 1;
    }

    protected Section(Paragraph title, int numberDepth) {
        this.numberDepth = numberDepth;
        this.title = title;
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            for (Object o : this) {
                Element element = (Element)o;
                listener.add(element);
            }
            return true;
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 13;
    }

    public boolean isChapter() {
        return this.type() == 16;
    }

    public boolean isSection() {
        return this.type() == 13;
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> tmp = new ArrayList<Element>();
        for (Element o : this) {
            tmp.addAll(o.getChunks());
        }
        return tmp;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return false;
    }

    @Override
    public void add(int index, Element o) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        try {
            Element element = o;
            if (!element.isNestable()) {
                throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.t.add.a.1.to.a.section", element.getClass().getName()));
            }
            super.add(index, element);
        }
        catch (ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", cce.getMessage()));
        }
    }

    @Override
    public boolean add(Element o) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        try {
            Element element = o;
            if (element.type() == 13) {
                Section section = (Section)o;
                section.setNumbers(++this.subsections, this.numbers);
                return super.add(section);
            }
            if (o instanceof MarkedSection && ((MarkedObject)o).element.type() == 13) {
                MarkedSection mo = (MarkedSection)o;
                Section section = (Section)mo.element;
                section.setNumbers(++this.subsections, this.numbers);
                return super.add(mo);
            }
            if (element.isNestable()) {
                return super.add(o);
            }
            throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.t.add.a.1.to.a.section", element.getClass().getName()));
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

    public Section addSection(float indentation, Paragraph title, int numberDepth) {
        if (this.isAddedCompletely()) {
            throw new IllegalStateException(MessageLocalization.getComposedMessage("this.largeelement.has.already.been.added.to.the.document"));
        }
        Section section = new Section(title, numberDepth);
        section.setIndentation(indentation);
        this.add(section);
        return section;
    }

    public Section addSection(float indentation, Paragraph title) {
        return this.addSection(indentation, title, this.numberDepth + 1);
    }

    public Section addSection(Paragraph title, int numberDepth) {
        return this.addSection(0.0f, title, numberDepth);
    }

    public MarkedSection addMarkedSection() {
        MarkedSection section = new MarkedSection(new Section(null, this.numberDepth + 1));
        this.add(section);
        return section;
    }

    public Section addSection(Paragraph title) {
        return this.addSection(0.0f, title, this.numberDepth + 1);
    }

    public Section addSection(float indentation, String title, int numberDepth) {
        return this.addSection(indentation, new Paragraph(title), numberDepth);
    }

    public Section addSection(String title, int numberDepth) {
        return this.addSection(new Paragraph(title), numberDepth);
    }

    public Section addSection(float indentation, String title) {
        return this.addSection(indentation, new Paragraph(title));
    }

    public Section addSection(String title) {
        return this.addSection(new Paragraph(title));
    }

    public void setTitle(Paragraph title) {
        this.title = title;
    }

    public Paragraph getTitle() {
        return Section.constructTitle(this.title, this.numbers, this.numberDepth, this.numberStyle);
    }

    public static Paragraph constructTitle(Paragraph title, List<Integer> numbers, int numberDepth, int numberStyle) {
        if (title == null) {
            return null;
        }
        int depth = Math.min(numbers.size(), numberDepth);
        if (depth < 1) {
            return title;
        }
        StringBuilder buf = new StringBuilder(" ");
        for (int i = 0; i < depth; ++i) {
            buf.insert(0, ".");
            buf.insert(0, numbers.get(i));
        }
        if (numberStyle == 1) {
            buf.deleteCharAt(buf.length() - 2);
        }
        Paragraph result = new Paragraph(title);
        result.add(0, new Chunk(buf.toString(), title.getFont()));
        return result;
    }

    public void setNumberDepth(int numberDepth) {
        this.numberDepth = numberDepth;
    }

    public int getNumberDepth() {
        return this.numberDepth;
    }

    public void setNumberStyle(int numberStyle) {
        this.numberStyle = numberStyle;
    }

    public int getNumberStyle() {
        return this.numberStyle;
    }

    public void setIndentationLeft(float indentation) {
        this.indentationLeft = indentation;
    }

    public float getIndentationLeft() {
        return this.indentationLeft;
    }

    public void setIndentationRight(float indentation) {
        this.indentationRight = indentation;
    }

    public float getIndentationRight() {
        return this.indentationRight;
    }

    public void setIndentation(float indentation) {
        this.indentation = indentation;
    }

    public float getIndentation() {
        return this.indentation;
    }

    public void setBookmarkOpen(boolean bookmarkOpen) {
        this.bookmarkOpen = bookmarkOpen;
    }

    public boolean isBookmarkOpen() {
        return this.bookmarkOpen;
    }

    public void setTriggerNewPage(boolean triggerNewPage) {
        this.triggerNewPage = triggerNewPage;
    }

    public boolean isTriggerNewPage() {
        return this.triggerNewPage && this.notAddedYet;
    }

    public void setBookmarkTitle(String bookmarkTitle) {
        this.bookmarkTitle = bookmarkTitle;
    }

    public Paragraph getBookmarkTitle() {
        if (this.bookmarkTitle == null) {
            return this.getTitle();
        }
        return new Paragraph(this.bookmarkTitle);
    }

    public void setChapterNumber(int number) {
        this.numbers.set(this.numbers.size() - 1, number);
        for (Object o : this) {
            Object s = o;
            if (!(s instanceof Section)) continue;
            ((Section)s).setChapterNumber(number);
        }
    }

    public int getDepth() {
        return this.numbers.size();
    }

    private void setNumbers(int number, List<Integer> numbers) {
        this.numbers = new ArrayList<Integer>();
        this.numbers.add(number);
        this.numbers.addAll(numbers);
    }

    public boolean isNotAddedYet() {
        return this.notAddedYet;
    }

    public void setNotAddedYet(boolean notAddedYet) {
        this.notAddedYet = notAddedYet;
    }

    protected boolean isAddedCompletely() {
        return this.addedCompletely;
    }

    protected void setAddedCompletely(boolean addedCompletely) {
        this.addedCompletely = addedCompletely;
    }

    @Override
    public void flushContent() {
        this.setNotAddedYet(false);
        this.title = null;
        Iterator i = this.iterator();
        while (i.hasNext()) {
            Element element = (Element)i.next();
            if (element instanceof Section) {
                Section s = (Section)element;
                if (!s.isComplete() && this.size() == 1) {
                    s.flushContent();
                    return;
                }
                s.setAddedCompletely(true);
            }
            i.remove();
        }
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void newPage() {
        this.add(Chunk.NEXTPAGE);
    }
}

