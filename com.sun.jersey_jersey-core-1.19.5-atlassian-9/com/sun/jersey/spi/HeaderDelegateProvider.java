/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.ext.RuntimeDelegate$HeaderDelegate
 */
package com.sun.jersey.spi;

import javax.ws.rs.ext.RuntimeDelegate;

public interface HeaderDelegateProvider<T>
extends RuntimeDelegate.HeaderDelegate<T> {
    public boolean supports(Class<?> var1);
}

