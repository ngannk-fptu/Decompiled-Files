/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.CursorType;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ContentCursor
implements Cursor {
    private final boolean isReverse;
    private final Long contentId;
    public static final ContentCursor EMPTY_CURSOR = new ContentCursor(false, null);
    public static final ContentCursor EMPTY_CURSOR_PREV = new ContentCursor(true, null);

    private ContentCursor(boolean isReverse, Long contentId) {
        this.isReverse = isReverse;
        this.contentId = contentId;
    }

    public static ContentCursor createCursor(boolean isReverse, long contentId) {
        return new ContentCursor(isReverse, contentId);
    }

    @Override
    public CursorType getCursorType() {
        return CursorType.CONTENT;
    }

    @Override
    public boolean isReverse() {
        return this.isReverse;
    }

    public Long getContentId() {
        return this.contentId;
    }

    @Override
    public boolean isEmpty() {
        return this.equals(EMPTY_CURSOR) || this.equals(EMPTY_CURSOR_PREV);
    }

    public String toString() {
        return String.join((CharSequence)":", this.getCursorType().getType(), String.valueOf(this.isReverse), String.valueOf(this.contentId));
    }

    public static ContentCursor valueOf(@NonNull String cursorToken) {
        try {
            String[] cursorArray = cursorToken.split(":");
            if (cursorArray.length != 3) {
                throw new IllegalArgumentException(String.format("Illegal cursor value %s. Cursor needs to be in the next format: 'cursorType:isReverse:contentId'", cursorToken));
            }
            if (!CursorType.CONTENT.getType().equalsIgnoreCase(cursorArray[0])) {
                throw new IllegalArgumentException(String.format("Illegal cursor type. Expected %s, but received %s", CursorType.CONTENT.getType(), cursorArray[0]));
            }
            boolean isReverse = Boolean.parseBoolean(cursorArray[1]);
            Long contentId = cursorArray[2].equalsIgnoreCase("null") ? null : Long.valueOf(Long.parseLong(cursorArray[2]));
            return new ContentCursor(isReverse, contentId);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Illegal cursor value %s. Cursor needs to be in the next format: 'cursorType:isReverse:contentId'", cursorToken), e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContentCursor)) {
            return false;
        }
        ContentCursor that = (ContentCursor)o;
        return this.isReverse == that.isReverse && Objects.equals(this.contentId, that.contentId);
    }

    public int hashCode() {
        return Objects.hash(this.isReverse, this.contentId);
    }
}

