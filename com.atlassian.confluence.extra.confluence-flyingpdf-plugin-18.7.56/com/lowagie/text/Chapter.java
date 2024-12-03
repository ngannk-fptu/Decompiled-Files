/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import java.util.ArrayList;

public class Chapter
extends Section {
    private static final long serialVersionUID = 1791000695779357361L;

    public Chapter(int number) {
        super(null, 1);
        this.numbers = new ArrayList();
        this.numbers.add(number);
        this.triggerNewPage = true;
    }

    public Chapter(Paragraph title, int number) {
        super(title, 1);
        this.numbers = new ArrayList();
        this.numbers.add(number);
        this.triggerNewPage = true;
    }

    public Chapter(String title, int number) {
        this(new Paragraph(title), number);
    }

    @Override
    public int type() {
        return 16;
    }

    @Override
    public boolean isNestable() {
        return false;
    }
}

