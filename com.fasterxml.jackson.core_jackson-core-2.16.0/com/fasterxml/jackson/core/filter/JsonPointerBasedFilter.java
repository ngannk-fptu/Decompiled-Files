/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.filter.TokenFilter;

public class JsonPointerBasedFilter
extends TokenFilter {
    protected final JsonPointer _pathToMatch;
    protected final boolean _includeAllElements;

    public JsonPointerBasedFilter(String ptrExpr) {
        this(JsonPointer.compile(ptrExpr), false);
    }

    public JsonPointerBasedFilter(JsonPointer pathToMatch) {
        this(pathToMatch, false);
    }

    public JsonPointerBasedFilter(JsonPointer pathToMatch, boolean includeAllElements) {
        this._pathToMatch = pathToMatch;
        this._includeAllElements = includeAllElements;
    }

    protected JsonPointerBasedFilter construct(JsonPointer pathToMatch, boolean includeAllElements) {
        return new JsonPointerBasedFilter(pathToMatch, includeAllElements);
    }

    @Override
    public TokenFilter includeElement(int index) {
        JsonPointer next = this._includeAllElements && !this._pathToMatch.mayMatchElement() ? this._pathToMatch.tail() : this._pathToMatch.matchElement(index);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return TokenFilter.INCLUDE_ALL;
        }
        return this.construct(next, this._includeAllElements);
    }

    @Override
    public TokenFilter includeProperty(String name) {
        JsonPointer next = this._pathToMatch.matchProperty(name);
        if (next == null) {
            return null;
        }
        if (next.matches()) {
            return TokenFilter.INCLUDE_ALL;
        }
        return this.construct(next, this._includeAllElements);
    }

    @Override
    public TokenFilter filterStartArray() {
        return this;
    }

    @Override
    public TokenFilter filterStartObject() {
        return this;
    }

    @Override
    protected boolean _includeScalar() {
        return this._pathToMatch.matches();
    }

    @Override
    public String toString() {
        return "[JsonPointerFilter at: " + this._pathToMatch + "]";
    }
}

