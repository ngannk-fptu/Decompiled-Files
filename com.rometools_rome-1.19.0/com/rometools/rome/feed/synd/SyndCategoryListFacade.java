/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

class SyndCategoryListFacade
extends AbstractList<SyndCategory> {
    private final List<DCSubject> subjects;

    public SyndCategoryListFacade() {
        this(new ArrayList<DCSubject>());
    }

    public SyndCategoryListFacade(List<DCSubject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public SyndCategory get(int index) {
        return new SyndCategoryImpl(this.subjects.get(index));
    }

    @Override
    public int size() {
        return this.subjects.size();
    }

    @Override
    public SyndCategory set(int index, SyndCategory obj) {
        SyndCategoryImpl sCat = (SyndCategoryImpl)obj;
        DCSubject subject = sCat != null ? sCat.getSubject() : null;
        if ((subject = this.subjects.set(index, subject)) != null) {
            return new SyndCategoryImpl(subject);
        }
        return null;
    }

    @Override
    public void add(int index, SyndCategory obj) {
        SyndCategoryImpl sCat = (SyndCategoryImpl)obj;
        DCSubject subject = sCat != null ? sCat.getSubject() : null;
        this.subjects.add(index, subject);
    }

    @Override
    public SyndCategory remove(int index) {
        DCSubject subject = this.subjects.remove(index);
        if (subject != null) {
            return new SyndCategoryImpl(subject);
        }
        return null;
    }

    public static List<DCSubject> convertElementsSyndCategoryToSubject(List<SyndCategory> cList) {
        ArrayList<DCSubject> sList = null;
        if (cList != null) {
            sList = new ArrayList<DCSubject>();
            for (int i = 0; i < cList.size(); ++i) {
                SyndCategoryImpl sCat = (SyndCategoryImpl)cList.get(i);
                DCSubject subject = null;
                if (sCat != null) {
                    subject = sCat.getSubject();
                }
                sList.add(subject);
            }
        }
        return sList;
    }
}

