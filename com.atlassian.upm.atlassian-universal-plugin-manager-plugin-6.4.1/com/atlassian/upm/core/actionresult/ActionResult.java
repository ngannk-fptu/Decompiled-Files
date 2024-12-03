/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.actionresult;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActionResult
implements Serializable {
    @JsonProperty
    private final ActionResultType type;
    @JsonProperty
    private final String content;

    public ActionResult(ActionResultType type, String content) {
        this.type = type;
        this.content = content;
    }

    public ActionResultType getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public static enum ActionResultType {
        SUCCESS(1),
        WARNING(2),
        ERROR(3);

        private final int ordering;

        private ActionResultType(int ordering) {
            this.ordering = ordering;
        }

        public int getOrdering() {
            return this.ordering;
        }
    }
}

