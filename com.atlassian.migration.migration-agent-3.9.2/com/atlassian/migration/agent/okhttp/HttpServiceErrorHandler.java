/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Response
 */
package com.atlassian.migration.agent.okhttp;

import java.util.function.Consumer;
import okhttp3.Response;

@FunctionalInterface
interface HttpServiceErrorHandler
extends Consumer<Response> {
}

