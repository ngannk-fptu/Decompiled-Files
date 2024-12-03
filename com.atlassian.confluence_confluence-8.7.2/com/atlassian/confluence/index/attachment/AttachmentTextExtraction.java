/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.index.attachment;

import com.atlassian.annotations.Internal;
import java.io.Serializable;
import java.util.Optional;

@Internal
public interface AttachmentTextExtraction
extends Serializable {
    public Optional<String> getText();
}

