/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hwpf.model.NotesTables;
import org.apache.poi.hwpf.usermodel.Notes;

public class NotesImpl
implements Notes {
    private Map<Integer, Integer> anchorToIndexMap;
    private final NotesTables notesTables;

    public NotesImpl(NotesTables notesTables) {
        this.notesTables = notesTables;
    }

    @Override
    public int getNoteAnchorPosition(int index) {
        return this.notesTables.getDescriptor(index).getStart();
    }

    @Override
    public int getNoteIndexByAnchorPosition(int anchorPosition) {
        this.updateAnchorToIndexMap();
        Integer index = this.anchorToIndexMap.get(anchorPosition);
        if (index == null) {
            return -1;
        }
        return index;
    }

    @Override
    public int getNotesCount() {
        return this.notesTables.getDescriptorsCount();
    }

    @Override
    public int getNoteTextEndOffset(int index) {
        return this.notesTables.getTextPosition(index).getEnd();
    }

    @Override
    public int getNoteTextStartOffset(int index) {
        return this.notesTables.getTextPosition(index).getStart();
    }

    private void updateAnchorToIndexMap() {
        if (this.anchorToIndexMap != null) {
            return;
        }
        HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
        for (int n = 0; n < this.notesTables.getDescriptorsCount(); ++n) {
            int anchorPosition = this.notesTables.getDescriptor(n).getStart();
            result.put(anchorPosition, n);
        }
        this.anchorToIndexMap = result;
    }
}

