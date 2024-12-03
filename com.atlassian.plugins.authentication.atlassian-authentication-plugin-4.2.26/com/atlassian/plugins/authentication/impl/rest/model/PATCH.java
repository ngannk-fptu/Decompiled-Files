/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.HttpMethod
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.ws.rs.HttpMethod;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@HttpMethod(value="PATCH")
public @interface PATCH {
}

