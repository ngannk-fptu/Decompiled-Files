/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.usermodel.Bookmark;

public interface Bookmarks {
    public Bookmark getBookmark(int var1) throws IndexOutOfBoundsException;

    public int getBookmarksCount();

    public Map<Integer, List<Bookmark>> getBookmarksStartedBetween(int var1, int var2);

    public void remove(int var1);
}

