/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 */
package com.atlassian.confluence.api.model.accessmode;

import com.atlassian.annotations.ExperimentalApi;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@ExperimentalApi
@JsonAutoDetect
public enum AccessMode {
    READ_WRITE,
    READ_ONLY;

}

