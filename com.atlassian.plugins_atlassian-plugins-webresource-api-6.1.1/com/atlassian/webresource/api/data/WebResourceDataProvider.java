/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.google.common.base.Supplier
 */
package com.atlassian.webresource.api.data;

import com.atlassian.json.marshal.Jsonable;
import com.google.common.base.Supplier;

public interface WebResourceDataProvider
extends Supplier<Jsonable> {
}

