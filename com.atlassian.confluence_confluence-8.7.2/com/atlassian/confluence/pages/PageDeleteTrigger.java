/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.OperationTrigger;

public enum PageDeleteTrigger implements OperationTrigger
{
    DELETE_SINGLE,
    BULK_OPERATION,
    UNKNOWN;

}

