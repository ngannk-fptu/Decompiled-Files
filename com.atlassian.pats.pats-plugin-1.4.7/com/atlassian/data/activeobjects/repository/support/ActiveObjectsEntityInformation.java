/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.support.ActiveObjectsEntityMetadata;
import org.springframework.data.repository.core.EntityInformation;

public interface ActiveObjectsEntityInformation<T, ID>
extends EntityInformation<T, ID>,
ActiveObjectsEntityMetadata<T> {
}

