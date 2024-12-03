/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.model.ExtensibleElement;

public interface Control
extends ExtensibleElement {
    public boolean isDraft();

    public Control setDraft(boolean var1);

    public Control unsetDraft();
}

