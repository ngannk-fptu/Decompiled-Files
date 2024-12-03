/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

public interface Notes {
    public int getNoteAnchorPosition(int var1);

    public int getNotesCount();

    public int getNoteIndexByAnchorPosition(int var1);

    public int getNoteTextEndOffset(int var1);

    public int getNoteTextStartOffset(int var1);
}

