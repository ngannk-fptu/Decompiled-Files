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

public class SpaceCursor
implements Cursor {
    private final boolean isReverse;
    private final Long spaceId;
    public static final SpaceCursor EMPTY_CURSOR = new SpaceCursor(false, null);

    private SpaceCursor(boolean isReverse, Long spaceId) {
        this.isReverse = isReverse;
        this.spaceId = spaceId;
    }

    public static SpaceCursor createCursor(boolean isReverse, Long spaceId) {
        return new SpaceCursor(isReverse, spaceId);
    }

    @Override
    public CursorType getCursorType() {
        return CursorType.SPACE;
    }

    @Override
    public boolean isReverse() {
        return this.isReverse;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    @Override
    public boolean isEmpty() {
        return this.equals(EMPTY_CURSOR);
    }

    public String toString() {
        return String.join((CharSequence)":", this.getCursorType().getType(), String.valueOf(this.isReverse), String.valueOf(this.spaceId));
    }

    public static SpaceCursor valueOf(@NonNull String cursorToken) {
        try {
            String[] cursorArray = cursorToken.split(":");
            if (cursorArray.length != 3) {
                throw new IllegalArgumentException(String.format("Illegal cursor value %s. Cursor needs to be in the next format: 'cursorType:isReverse:spaceId'", cursorToken));
            }
            if (!CursorType.SPACE.getType().equalsIgnoreCase(cursorArray[0])) {
                throw new IllegalArgumentException(String.format("Illegal cursor type. Expected %s, but received %s", CursorType.SPACE.getType(), cursorArray[0]));
            }
            boolean isReverse = Boolean.parseBoolean(cursorArray[1]);
            Long spaceId = cursorArray[2].equalsIgnoreCase("null") ? null : Long.valueOf(Long.parseLong(cursorArray[2]));
            return new SpaceCursor(isReverse, spaceId);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Illegal cursor value %s. Cursor needs to be in the next format: 'cursorType:isReverse:spaceId'", cursorToken), e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpaceCursor)) {
            return false;
        }
        SpaceCursor that = (SpaceCursor)o;
        return this.isReverse == that.isReverse && Objects.equals(this.spaceId, that.spaceId);
    }

    public int hashCode() {
        return Objects.hash(this.isReverse, this.spaceId);
    }
}

