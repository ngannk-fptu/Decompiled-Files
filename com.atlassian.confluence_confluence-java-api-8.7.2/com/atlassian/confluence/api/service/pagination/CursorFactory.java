/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.pagination;

import com.atlassian.confluence.api.model.pagination.ContentCursor;
import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.CursorType;
import com.atlassian.confluence.api.model.pagination.SpaceCursor;

public class CursorFactory {
    private CursorFactory() {
    }

    public static Cursor buildFrom(String cursorString) {
        if (cursorString == null || cursorString.isEmpty()) {
            return null;
        }
        int firstColonIndex = cursorString.indexOf(":");
        if (firstColonIndex < 0) {
            throw new IllegalArgumentException(String.format("Format of cursor %s is not supported. At least one colon ':' is expected", cursorString));
        }
        String cursorTypeString = cursorString.substring(0, firstColonIndex);
        if (CursorType.SPACE.getType().equalsIgnoreCase(cursorTypeString)) {
            return SpaceCursor.valueOf(cursorString);
        }
        if (CursorType.CONTENT.getType().equalsIgnoreCase(cursorTypeString)) {
            return ContentCursor.valueOf(cursorString);
        }
        throw new IllegalArgumentException(String.format("Cursor %s is not supported", cursorString));
    }
}

