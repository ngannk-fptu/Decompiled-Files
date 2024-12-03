/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.message;

import com.sun.xml.ws.api.message.Attachment;
import com.sun.xml.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

public final class AttachmentSetImpl
implements AttachmentSet {
    private final ArrayList<Attachment> attList = new ArrayList();

    public AttachmentSetImpl() {
    }

    public AttachmentSetImpl(Iterable<Attachment> base) {
        for (Attachment a : base) {
            this.add(a);
        }
    }

    @Override
    public Attachment get(String contentId) {
        for (int i = this.attList.size() - 1; i >= 0; --i) {
            Attachment a = this.attList.get(i);
            if (!a.getContentId().equals(contentId)) continue;
            return a;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return this.attList.isEmpty();
    }

    @Override
    public void add(Attachment att) {
        this.attList.add(att);
    }

    @Override
    public Iterator<Attachment> iterator() {
        return this.attList.iterator();
    }
}

