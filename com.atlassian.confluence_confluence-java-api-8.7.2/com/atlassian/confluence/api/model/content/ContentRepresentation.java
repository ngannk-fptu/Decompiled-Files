/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import java.util.Arrays;
import java.util.Collections;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public final class ContentRepresentation
extends BaseApiEnum {
    public static final ContentRepresentation RAW = new ContentRepresentation("raw", false, false);
    public static final ContentRepresentation STORAGE = new ContentRepresentation("storage", true, true);
    public static final ContentRepresentation EDITOR = new ContentRepresentation("editor", true, true);
    public static final ContentRepresentation VIEW = new ContentRepresentation("view", false, true);
    public static final ContentRepresentation EXPORT_VIEW = new ContentRepresentation("export_view", false, true);
    public static final ContentRepresentation ANONYMOUS_EXPORT_VIEW = new ContentRepresentation("anonymous_export_view", false, true);
    public static final ContentRepresentation WIKI = new ContentRepresentation("wiki", true, false);
    public static final ContentRepresentation PLAIN = new ContentRepresentation("plain", false, false);
    public static final ContentRepresentation STYLED_VIEW = new ContentRepresentation("styled_view", false, true);
    public static final Iterable<ContentRepresentation> INPUT_CONVERSION_TO_STORAGE_ORDER = Collections.unmodifiableList(Arrays.asList(STORAGE, WIKI, EDITOR));
    private static final ContentRepresentation[] BUILT_IN = new ContentRepresentation[]{RAW, STORAGE, EDITOR, VIEW, EXPORT_VIEW, WIKI, PLAIN, STYLED_VIEW};
    private final boolean convertsToStorage;
    private final boolean convertsFromStorage;

    @JsonCreator
    public static ContentRepresentation valueOf(String representation) {
        for (ContentRepresentation contentRepresentation : BUILT_IN) {
            if (!representation.equals(contentRepresentation.getRepresentation())) continue;
            return contentRepresentation;
        }
        throw new IllegalArgumentException("Unknown representation '" + representation + "'");
    }

    @JsonIgnore
    private ContentRepresentation(String representation, boolean convertsToStorage, boolean convertsFromStorage) {
        super(representation);
        this.convertsToStorage = convertsToStorage;
        this.convertsFromStorage = convertsFromStorage;
    }

    public boolean convertsFromStorage() {
        return this.convertsFromStorage;
    }

    public boolean convertsToStorage() {
        return this.convertsToStorage;
    }

    public String getRepresentation() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.serialise();
    }
}

