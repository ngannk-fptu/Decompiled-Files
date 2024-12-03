/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.PublishedApi
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.reflect.KDeclarationContainer
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.web.servlet.function;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctionDsl;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=1, d1={"\u0000\u009a\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B \b\u0000\u0012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005\u00a2\u0006\u0002\u0010\u0006J\u001a\u0010\r\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\r\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\r\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\r\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\r\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0015\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0016\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0017\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0018\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u0019\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u0019\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u0019\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010\u001a\u001a\u00020\u00042\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J*\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\"\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001f\u0010\u001b\u001a\u00020\u00112\u0012\u0010\u001c\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001e0\u001d\"\u00020\u001e\u00a2\u0006\u0002\u0010\u001fJ\"\u0010\u001b\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001e2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u0006\u0010 \u001a\u00020!J\u0014\u0010\"\u001a\u00020\u00042\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00100$J \u0010%\u001a\u00020\u00042\u0018\u0010&\u001a\u0014\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00100'J\u0006\u0010(\u001a\u00020!J\u001a\u0010)\u001a\u00020\u00042\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000f0\u0003J\u0013\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00100$H\u0000\u00a2\u0006\u0002\b,J\u001f\u0010-\u001a\u00020\u00112\u0012\u0010.\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001e0\u001d\"\u00020\u001e\u00a2\u0006\u0002\u0010\u001fJ\"\u0010-\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u001e2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010/\u001a\u00020!2\u0006\u00100\u001a\u000201J,\u00102\u001a\u00020\u00042$\u00103\u001a \u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003\u0012\u0004\u0012\u00020\u00100'J\u000e\u00104\u001a\u00020!2\u0006\u00105\u001a\u00020\u0010J\u001a\u00106\u001a\u00020\u00112\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u0002090\u0003J.\u00106\u001a\u00020\u00042\u0012\u00107\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u00020<J\"\u0010:\u001a\u00020\u00042\u0006\u0010;\u001a\u00020<2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\n\u0010=\u001a\u0006\u0012\u0002\b\u00030>J\n\u0010?\u001a\u0006\u0012\u0002\b\u00030>J\u0006\u0010@\u001a\u00020!J4\u0010A\u001a\u00020\u00042\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u0002090\u00032\u0018\u0010C\u001a\u0014\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100'J1\u0010A\u001a\u00020\u0004\"\n\b\u0000\u0010D\u0018\u0001*\u00020B2\u001a\b\b\u0010C\u001a\u0014\u0012\u0004\u0012\u00020B\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100'H\u0086\bJ\"\u0010E\u001a\u00020\u00112\u0006\u0010F\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u0003J6\u0010E\u001a\u00020\u00042\u0006\u0010F\u001a\u00020\u00132\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010G\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\"\u0010G\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u001a\u0010H\u001a\u00020\u00112\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u0003J.\u0010H\u001a\u00020\u00042\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u0002090\u00032\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010H\u001a\u00020\u00112\u0006\u0010I\u001a\u00020\u0013J\"\u0010H\u001a\u00020\u00042\u0006\u0010I\u001a\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003J\u000e\u0010J\u001a\u00020!2\u0006\u00100\u001a\u000201J\u001c\u0010K\u001a\u00020\u00042\u0014\u0010L\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0006\u0012\u0004\u0018\u00010M0\u0003J\u0016\u0010K\u001a\u00020\u00042\u0006\u0010G\u001a\u00020\u00132\u0006\u00100\u001a\u00020MJ\u000e\u0010N\u001a\u00020!2\u0006\u00100\u001a\u000201J\u000e\u0010O\u001a\u00020!2\u0006\u0010O\u001a\u00020PJ\u000e\u0010O\u001a\u00020!2\u0006\u0010O\u001a\u00020QJ\u000e\u0010R\u001a\u00020!2\u0006\u00100\u001a\u000201J\u0006\u0010S\u001a\u00020!J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00132\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0013H\u0086\u0004J\u0015\u0010T\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J!\u0010U\u001a\u00020\u0004*\u00020\u00132\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003H\u0086\u0002J!\u0010U\u001a\u00020\u0004*\u00020\u00112\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00100\u0003H\u0086\u0002J#\u0010V\u001a\u00020\u0004*\u00020\u00132\u0017\u0010W\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005J#\u0010V\u001a\u00020\u0004*\u00020\u00112\u0017\u0010W\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005J\r\u0010X\u001a\u00020\u0011*\u00020\u0011H\u0086\u0002J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00132\u0006\u00105\u001a\u00020\u0011H\u0086\u0004J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0013H\u0086\u0004J\u0015\u0010Y\u001a\u00020\u0011*\u00020\u00112\u0006\u00105\u001a\u00020\u0011H\u0086\u0004R\u001c\u0010\u0007\u001a\u00020\b8\u0000X\u0081\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\t\u0010\n\u001a\u0004\b\u000b\u0010\fR\u001f\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006Z"}, d2={"Lorg/springframework/web/servlet/function/RouterFunctionDsl;", "", "init", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "(Lkotlin/jvm/functions/Function1;)V", "builder", "Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "builder$annotations", "()V", "getBuilder", "()Lorg/springframework/web/servlet/function/RouterFunctions$Builder;", "DELETE", "f", "Lorg/springframework/web/servlet/function/ServerRequest;", "Lorg/springframework/web/servlet/function/ServerResponse;", "Lorg/springframework/web/servlet/function/RequestPredicate;", "pattern", "", "predicate", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT", "accept", "mediaType", "", "Lorg/springframework/http/MediaType;", "([Lorg/springframework/http/MediaType;)Lorg/springframework/web/servlet/function/RequestPredicate;", "accepted", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", "add", "routerFunction", "Lorg/springframework/web/servlet/function/RouterFunction;", "after", "responseProcessor", "Lkotlin/Function2;", "badRequest", "before", "requestProcessor", "build", "build$spring_webmvc", "contentType", "mediaTypes", "created", "location", "Ljava/net/URI;", "filter", "filterFunction", "from", "other", "headers", "headersPredicate", "Lorg/springframework/web/servlet/function/ServerRequest$Headers;", "", "method", "httpMethod", "Lorg/springframework/http/HttpMethod;", "noContent", "Lorg/springframework/web/servlet/function/ServerResponse$HeadersBuilder;", "notFound", "ok", "onError", "", "responseProvider", "E", "param", "name", "path", "pathExtension", "extension", "permanentRedirect", "resources", "lookupFunction", "Lorg/springframework/core/io/Resource;", "seeOther", "status", "", "Lorg/springframework/http/HttpStatus;", "temporaryRedirect", "unprocessableEntity", "and", "invoke", "nest", "r", "not", "or", "spring-webmvc"})
public final class RouterFunctionDsl {
    @NotNull
    private final RouterFunctions.Builder builder;
    private final Function1<RouterFunctionDsl, Unit> init;

    @PublishedApi
    public static /* synthetic */ void builder$annotations() {
    }

    @NotNull
    public final RouterFunctions.Builder getBuilder() {
        return this.builder;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$and, (String)"$this$and");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = $this$and.and(this.path(other));
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"this.and(path(other))");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull String other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$or, (String)"$this$or");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = $this$or.or(this.path(other));
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"this.or(path(other))");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate and(@NotNull String $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$and, (String)"$this$and");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = this.path($this$and).and(other);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"path(this).and(other)");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate or(@NotNull String $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$or, (String)"$this$or");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = this.path($this$or).or(other);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"path(this).or(other)");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate and(@NotNull RequestPredicate $this$and, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$and, (String)"$this$and");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = $this$and.and(other);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"this.and(other)");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate or(@NotNull RequestPredicate $this$or, @NotNull RequestPredicate other) {
        Intrinsics.checkParameterIsNotNull((Object)$this$or, (String)"$this$or");
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        RequestPredicate requestPredicate = $this$or.or(other);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"this.or(other)");
        return requestPredicate;
    }

    @NotNull
    public final RequestPredicate not(@NotNull RequestPredicate $this$not) {
        Intrinsics.checkParameterIsNotNull((Object)$this$not, (String)"$this$not");
        RequestPredicate requestPredicate = $this$not.negate();
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"this.negate()");
        return requestPredicate;
    }

    public final void nest(@NotNull RequestPredicate $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> r) {
        Intrinsics.checkParameterIsNotNull((Object)$this$nest, (String)"$this$nest");
        Intrinsics.checkParameterIsNotNull(r, (String)"r");
        Function0 function0 = (Function0)new Function0<RouterFunction<ServerResponse>>(new RouterFunctionDsl(r)){

            @NotNull
            public final RouterFunction<ServerResponse> invoke() {
                return ((RouterFunctionDsl)this.receiver).build$spring_webmvc();
            }

            public final KDeclarationContainer getOwner() {
                return Reflection.getOrCreateKotlinClass(RouterFunctionDsl.class);
            }

            public final String getName() {
                return "build";
            }

            public final String getSignature() {
                return "build$spring_webmvc()Lorg/springframework/web/servlet/function/RouterFunction;";
            }
        };
        this.builder.nest($this$nest, new Supplier(function0){
            private final /* synthetic */ Function0 function;
            {
                this.function = function0;
            }

            public final /* synthetic */ Object get() {
                return this.function.invoke();
            }
        });
    }

    public final void nest(@NotNull String $this$nest, @NotNull Function1<? super RouterFunctionDsl, Unit> r) {
        Intrinsics.checkParameterIsNotNull((Object)$this$nest, (String)"$this$nest");
        Intrinsics.checkParameterIsNotNull(r, (String)"r");
        this.nest(this.path($this$nest), r);
    }

    public final void GET(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.GET(new HandlerFunction(function1){
            private final /* synthetic */ Function1 function;
            {
                this.function = function1;
            }

            public final /* synthetic */ ServerResponse handle(ServerRequest request) {
                Intrinsics.checkParameterIsNotNull((Object)request, (String)"request");
                return (ServerResponse)this.function.invoke((Object)request);
            }
        });
    }

    public final void GET(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.GET(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void GET(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.GET(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void GET(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.GET(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate GET(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.GET(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.GET(pattern)");
        return requestPredicate;
    }

    public final void HEAD(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.HEAD(new /* invalid duplicate definition of identical inner class */);
    }

    public final void HEAD(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.HEAD(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void HEAD(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.HEAD(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void HEAD(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.HEAD(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate HEAD(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.HEAD(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.HEAD(pattern)");
        return requestPredicate;
    }

    public final void POST(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.POST(new /* invalid duplicate definition of identical inner class */);
    }

    public final void POST(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.POST(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void POST(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.POST(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void POST(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.POST(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate POST(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.POST(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.POST(pattern)");
        return requestPredicate;
    }

    public final void PUT(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PUT(new /* invalid duplicate definition of identical inner class */);
    }

    public final void PUT(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PUT(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void PUT(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PUT(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void PUT(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PUT(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate PUT(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.PUT(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.PUT(pattern)");
        return requestPredicate;
    }

    public final void PATCH(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PATCH(new /* invalid duplicate definition of identical inner class */);
    }

    public final void PATCH(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PATCH(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void PATCH(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PATCH(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void PATCH(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.PATCH(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate PATCH(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.PATCH(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.PATCH(pattern)");
        return requestPredicate;
    }

    public final void DELETE(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.DELETE(new /* invalid duplicate definition of identical inner class */);
    }

    public final void DELETE(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.DELETE(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void DELETE(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.DELETE(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void DELETE(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.DELETE(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate DELETE(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.DELETE(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.DELETE(pattern)");
        return requestPredicate;
    }

    public final void OPTIONS(@NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.OPTIONS(new /* invalid duplicate definition of identical inner class */);
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.OPTIONS(pattern, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void OPTIONS(@NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.OPTIONS(predicate, (HandlerFunction<ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final void OPTIONS(@NotNull String pattern, @NotNull RequestPredicate predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull((Object)predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.OPTIONS(pattern, predicate, new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final RequestPredicate OPTIONS(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.OPTIONS(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.OPTIONS(pattern)");
        return requestPredicate;
    }

    public final void accept(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)mediaType, (String)"mediaType");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.accept(mediaType), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate accept(MediaType ... mediaType) {
        Intrinsics.checkParameterIsNotNull((Object)mediaType, (String)"mediaType");
        RequestPredicate requestPredicate = RequestPredicates.accept(Arrays.copyOf(mediaType, mediaType.length));
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.accept(*mediaType)");
        return requestPredicate;
    }

    public final void contentType(@NotNull MediaType mediaType, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)mediaType, (String)"mediaType");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.contentType(mediaType), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate contentType(MediaType ... mediaTypes) {
        Intrinsics.checkParameterIsNotNull((Object)mediaTypes, (String)"mediaTypes");
        RequestPredicate requestPredicate = RequestPredicates.contentType(Arrays.copyOf(mediaTypes, mediaTypes.length));
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.contentType(*mediaTypes)");
        return requestPredicate;
    }

    public final void headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> headersPredicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(headersPredicate, (String)"headersPredicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Object object = headersPredicate;
        Predicate predicate = new Predicate((Function1)object){
            private final /* synthetic */ Function1 function;
            {
                this.function = function1;
            }

            public final /* synthetic */ boolean test(Object p0) {
                Object object = this.function.invoke(p0);
                Intrinsics.checkExpressionValueIsNotNull((Object)object, (String)"invoke(...)");
                return (Boolean)object;
            }
        };
        object = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.headers(predicate), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate headers(@NotNull Function1<? super ServerRequest.Headers, Boolean> headersPredicate) {
        Intrinsics.checkParameterIsNotNull(headersPredicate, (String)"headersPredicate");
        Function1<? super ServerRequest.Headers, Boolean> function1 = headersPredicate;
        RequestPredicate requestPredicate = RequestPredicates.headers(new /* invalid duplicate definition of identical inner class */);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.headers(headersPredicate)");
        return requestPredicate;
    }

    public final void method(@NotNull HttpMethod httpMethod, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)((Object)httpMethod), (String)"httpMethod");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.method(httpMethod), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate method(@NotNull HttpMethod httpMethod) {
        Intrinsics.checkParameterIsNotNull((Object)((Object)httpMethod), (String)"httpMethod");
        RequestPredicate requestPredicate = RequestPredicates.method(httpMethod);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.method(httpMethod)");
        return requestPredicate;
    }

    public final void path(@NotNull String pattern, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.path(pattern), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate path(@NotNull String pattern) {
        Intrinsics.checkParameterIsNotNull((Object)pattern, (String)"pattern");
        RequestPredicate requestPredicate = RequestPredicates.path(pattern);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.path(pattern)");
        return requestPredicate;
    }

    public final void pathExtension(@NotNull String extension, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)extension, (String)"extension");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension(extension), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull String extension) {
        Intrinsics.checkParameterIsNotNull((Object)extension, (String)"extension");
        RequestPredicate requestPredicate = RequestPredicates.pathExtension(extension);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.pathExtension(extension)");
        return requestPredicate;
    }

    public final void pathExtension(@NotNull Function1<? super String, Boolean> predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull(predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Object object = predicate;
        Predicate predicate2 = new /* invalid duplicate definition of identical inner class */;
        object = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.pathExtension(predicate2), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate pathExtension(@NotNull Function1<? super String, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull(predicate, (String)"predicate");
        Function1<? super String, Boolean> function1 = predicate;
        RequestPredicate requestPredicate = RequestPredicates.pathExtension(new /* invalid duplicate definition of identical inner class */);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.pathExtension(predicate)");
        return requestPredicate;
    }

    public final void param(@NotNull String name, @NotNull Function1<? super String, Boolean> predicate, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        Intrinsics.checkParameterIsNotNull(predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Object object = predicate;
        Predicate predicate2 = new /* invalid duplicate definition of identical inner class */;
        object = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.param(name, predicate2), new /* invalid duplicate definition of identical inner class */));
    }

    @NotNull
    public final RequestPredicate param(@NotNull String name, @NotNull Function1<? super String, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        Intrinsics.checkParameterIsNotNull(predicate, (String)"predicate");
        Function1<? super String, Boolean> function1 = predicate;
        RequestPredicate requestPredicate = RequestPredicates.param(name, new /* invalid duplicate definition of identical inner class */);
        Intrinsics.checkExpressionValueIsNotNull((Object)requestPredicate, (String)"RequestPredicates.param(name, predicate)");
        return requestPredicate;
    }

    public final void invoke(@NotNull RequestPredicate $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)$this$invoke, (String)"$this$invoke");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route($this$invoke, new /* invalid duplicate definition of identical inner class */));
    }

    public final void invoke(@NotNull String $this$invoke, @NotNull Function1<? super ServerRequest, ? extends ServerResponse> f) {
        Intrinsics.checkParameterIsNotNull((Object)$this$invoke, (String)"$this$invoke");
        Intrinsics.checkParameterIsNotNull(f, (String)"f");
        Function1<? super ServerRequest, ? extends ServerResponse> function1 = f;
        this.builder.add(RouterFunctions.route(RequestPredicates.path($this$invoke), new /* invalid duplicate definition of identical inner class */));
    }

    public final void resources(@NotNull String path, @NotNull Resource location) {
        Intrinsics.checkParameterIsNotNull((Object)path, (String)"path");
        Intrinsics.checkParameterIsNotNull((Object)location, (String)"location");
        this.builder.resources(path, location);
    }

    public final void resources(@NotNull Function1<? super ServerRequest, ? extends Resource> lookupFunction) {
        Intrinsics.checkParameterIsNotNull(lookupFunction, (String)"lookupFunction");
        this.builder.resources(new Function(lookupFunction){
            final /* synthetic */ Function1 $lookupFunction;

            @NotNull
            public final Optional<Resource> apply(ServerRequest it) {
                ServerRequest serverRequest = it;
                Intrinsics.checkExpressionValueIsNotNull((Object)serverRequest, (String)"it");
                return Optional.ofNullable(this.$lookupFunction.invoke((Object)serverRequest));
            }
            {
                this.$lookupFunction = function1;
            }
        });
    }

    public final void add(@NotNull RouterFunction<ServerResponse> routerFunction) {
        Intrinsics.checkParameterIsNotNull(routerFunction, (String)"routerFunction");
        this.builder.add(routerFunction);
    }

    public final void filter(@NotNull Function2<? super ServerRequest, ? super Function1<? super ServerRequest, ? extends ServerResponse>, ? extends ServerResponse> filterFunction) {
        Intrinsics.checkParameterIsNotNull(filterFunction, (String)"filterFunction");
        this.builder.filter(new HandlerFilterFunction(filterFunction){
            final /* synthetic */ Function2 $filterFunction;

            @NotNull
            public final ServerResponse filter(@NotNull ServerRequest request, @NotNull HandlerFunction<ServerResponse> next) {
                Intrinsics.checkParameterIsNotNull((Object)request, (String)"request");
                Intrinsics.checkParameterIsNotNull(next, (String)"next");
                return (ServerResponse)this.$filterFunction.invoke((Object)request, (Object)new Function1<ServerRequest, ServerResponse>(next){
                    final /* synthetic */ HandlerFunction $next;

                    @NotNull
                    public final ServerResponse invoke(@NotNull ServerRequest handlerRequest) {
                        Intrinsics.checkParameterIsNotNull((Object)handlerRequest, (String)"handlerRequest");
                        T t = this.$next.handle(handlerRequest);
                        Intrinsics.checkExpressionValueIsNotNull(t, (String)"next.handle(handlerRequest)");
                        return t;
                    }
                    {
                        this.$next = handlerFunction;
                        super(1);
                    }
                });
            }
            {
                this.$filterFunction = function2;
            }
        });
    }

    public final void before(@NotNull Function1<? super ServerRequest, ? extends ServerRequest> requestProcessor) {
        Intrinsics.checkParameterIsNotNull(requestProcessor, (String)"requestProcessor");
        Function1<? super ServerRequest, ? extends ServerRequest> function1 = requestProcessor;
        this.builder.before(new Function(function1){
            private final /* synthetic */ Function1 function;
            {
                this.function = function1;
            }

            public final /* synthetic */ Object apply(Object p0) {
                return this.function.invoke(p0);
            }
        });
    }

    public final void after(@NotNull Function2<? super ServerRequest, ? super ServerResponse, ? extends ServerResponse> responseProcessor) {
        Intrinsics.checkParameterIsNotNull(responseProcessor, (String)"responseProcessor");
        Function2<? super ServerRequest, ? super ServerResponse, ? extends ServerResponse> function2 = responseProcessor;
        this.builder.after(new BiFunction(function2){
            private final /* synthetic */ Function2 function;
            {
                this.function = function2;
            }

            public final /* synthetic */ Object apply(Object p0, Object p1) {
                return this.function.invoke(p0, p1);
            }
        });
    }

    public final void onError(@NotNull Function1<? super Throwable, Boolean> predicate, @NotNull Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> responseProvider) {
        Intrinsics.checkParameterIsNotNull(predicate, (String)"predicate");
        Intrinsics.checkParameterIsNotNull(responseProvider, (String)"responseProvider");
        Function1<? super Throwable, Boolean> function1 = predicate;
        Predicate predicate2 = new /* invalid duplicate definition of identical inner class */;
        function1 = responseProvider;
        this.builder.onError(predicate2, (BiFunction<Throwable, ServerRequest, ServerResponse>)new /* invalid duplicate definition of identical inner class */);
    }

    public final /* synthetic */ <E extends Throwable> void onError(Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> responseProvider) {
        int $i$f$onError = 0;
        Intrinsics.checkParameterIsNotNull(responseProvider, (String)"responseProvider");
        RouterFunctions.Builder builder = this.getBuilder();
        Intrinsics.needClassReification();
        Function2<? super Throwable, ? super ServerRequest, ? extends ServerResponse> function2 = responseProvider;
        builder.onError(onError.1.INSTANCE, (BiFunction<Throwable, ServerRequest, ServerResponse>)new BiFunction(function2){
            private final /* synthetic */ Function2 function;
            {
                this.function = function2;
            }

            public final /* synthetic */ Object apply(Object p0, Object p1) {
                return this.function.invoke(p0, p1);
            }
        });
    }

    @NotNull
    public final RouterFunction<ServerResponse> build$spring_webmvc() {
        this.init.invoke((Object)this);
        RouterFunction<ServerResponse> routerFunction = this.builder.build();
        Intrinsics.checkExpressionValueIsNotNull(routerFunction, (String)"builder.build()");
        return routerFunction;
    }

    @NotNull
    public final ServerResponse.BodyBuilder from(@NotNull ServerResponse other) {
        Intrinsics.checkParameterIsNotNull((Object)other, (String)"other");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.from(other);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.from(other)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder created(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull((Object)location, (String)"location");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.created(location);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.created(location)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder ok() {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.ok();
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.ok()");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.HeadersBuilder<?> noContent() {
        ServerResponse.HeadersBuilder<?> headersBuilder = ServerResponse.noContent();
        Intrinsics.checkExpressionValueIsNotNull(headersBuilder, (String)"ServerResponse.noContent()");
        return headersBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder accepted() {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.accepted();
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.accepted()");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder permanentRedirect(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull((Object)location, (String)"location");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.permanentRedirect(location);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.permanentRedirect(location)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder temporaryRedirect(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull((Object)location, (String)"location");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.temporaryRedirect(location);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.temporaryRedirect(location)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder seeOther(@NotNull URI location) {
        Intrinsics.checkParameterIsNotNull((Object)location, (String)"location");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.seeOther(location);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.seeOther(location)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder badRequest() {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.badRequest();
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.badRequest()");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.HeadersBuilder<?> notFound() {
        ServerResponse.HeadersBuilder<?> headersBuilder = ServerResponse.notFound();
        Intrinsics.checkExpressionValueIsNotNull(headersBuilder, (String)"ServerResponse.notFound()");
        return headersBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder unprocessableEntity() {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.unprocessableEntity();
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.unprocessableEntity()");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder status(@NotNull HttpStatus status) {
        Intrinsics.checkParameterIsNotNull((Object)((Object)status), (String)"status");
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.status(status);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.status(status)");
        return bodyBuilder;
    }

    @NotNull
    public final ServerResponse.BodyBuilder status(int status) {
        ServerResponse.BodyBuilder bodyBuilder = ServerResponse.status(status);
        Intrinsics.checkExpressionValueIsNotNull((Object)bodyBuilder, (String)"ServerResponse.status(status)");
        return bodyBuilder;
    }

    public RouterFunctionDsl(@NotNull Function1<? super RouterFunctionDsl, Unit> init) {
        Intrinsics.checkParameterIsNotNull(init, (String)"init");
        this.init = init;
        RouterFunctions.Builder builder = RouterFunctions.route();
        Intrinsics.checkExpressionValueIsNotNull((Object)builder, (String)"RouterFunctions.route()");
        this.builder = builder;
    }
}

