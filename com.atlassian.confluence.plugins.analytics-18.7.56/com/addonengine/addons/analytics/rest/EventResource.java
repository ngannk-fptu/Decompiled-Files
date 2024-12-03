/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$BooleanRef
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.ErrorDto;
import com.addonengine.addons.analytics.rest.dto.ErrorResponseDto;
import com.addonengine.addons.analytics.rest.filter.UserIsSystemAdminFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.EventService;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.OutputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Path(value="/event")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={ValidAddonLicenseFilter.class, UserIsSystemAdminFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J1\u0010\r\u001a\u00020\u000e2\n\b\u0001\u0010\u000f\u001a\u0004\u0018\u00010\u00102\n\b\u0001\u0010\u0011\u001a\u0004\u0018\u00010\u00102\n\b\u0001\u0010\u0012\u001a\u0004\u0018\u00010\u0006H\u0007\u00a2\u0006\u0002\u0010\u0013R\u0014\u0010\u0005\u001a\u00020\u0006X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\u0006X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/rest/EventResource;", "", "eventService", "Lcom/addonengine/addons/analytics/service/EventService;", "(Lcom/addonengine/addons/analytics/service/EventService;)V", "DEFAULT_EXPORT_FROM_DAYS", "", "getDEFAULT_EXPORT_FROM_DAYS", "()J", "DEFAULT_LIMIT", "getDEFAULT_LIMIT", "mapper", "Lorg/codehaus/jackson/map/ObjectMapper;", "getEvents", "Ljavax/ws/rs/core/Response;", "from", "", "to", "limit", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;)Ljavax/ws/rs/core/Response;", "analytics"})
@SourceDebugExtension(value={"SMAP\nEventResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventResource.kt\ncom/addonengine/addons/analytics/rest/EventResource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,86:1\n1#2:87\n*E\n"})
public final class EventResource {
    @NotNull
    private final EventService eventService;
    private final long DEFAULT_EXPORT_FROM_DAYS;
    private final long DEFAULT_LIMIT;
    @NotNull
    private final ObjectMapper mapper;

    public EventResource(@NotNull EventService eventService) {
        Intrinsics.checkNotNullParameter((Object)eventService, (String)"eventService");
        this.eventService = eventService;
        this.DEFAULT_EXPORT_FROM_DAYS = 365L;
        this.DEFAULT_LIMIT = 10000L;
        this.mapper = new ObjectMapper();
    }

    public final long getDEFAULT_EXPORT_FROM_DAYS() {
        return this.DEFAULT_EXPORT_FROM_DAYS;
    }

    public final long getDEFAULT_LIMIT() {
        return this.DEFAULT_LIMIT;
    }

    @GET
    @NotNull
    public final Response getEvents(@QueryParam(value="from") @Nullable String from, @QueryParam(value="to") @Nullable String to, @QueryParam(value="limit") @Nullable Long limit) {
        Instant actualFrom = null;
        Instant actualTo = null;
        try {
            Instant instant;
            Instant instant2;
            Instant instant3;
            Instant instant4;
            Instant instant5;
            String it;
            String string = from;
            if (string != null) {
                it = string;
                boolean bl = false;
                instant5 = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(from));
            } else {
                instant5 = instant4 = null;
            }
            if (instant4 == null) {
                Instant instant6 = Instant.now().minus(this.DEFAULT_EXPORT_FROM_DAYS, ChronoUnit.DAYS);
                instant3 = instant6;
                Intrinsics.checkNotNullExpressionValue((Object)instant6, (String)"minus(...)");
            } else {
                instant3 = instant4;
            }
            actualFrom = instant3;
            String string2 = to;
            if (string2 != null) {
                it = string2;
                boolean bl = false;
                instant2 = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(to));
            } else {
                instant2 = instant4 = null;
            }
            if (instant4 == null) {
                Instant instant7 = Instant.now();
                instant = instant7;
                Intrinsics.checkNotNullExpressionValue((Object)instant7, (String)"now(...)");
            } else {
                instant = instant4;
            }
            actualTo = instant;
        }
        catch (DateTimeParseException e) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/json").entity((Object)new ErrorResponseDto(new ErrorDto("InvalidDateTimeFormat", "Query string parameter 'from' and 'to' are expected to be ISO date-time format."))).build());
        }
        Long l = limit;
        long actualLimit = l != null ? l : this.DEFAULT_LIMIT;
        Stream<Event> stream = this.eventService.stream(new EventQuery(actualFrom, actualTo));
        Ref.BooleanRef isFirstItem = new Ref.BooleanRef();
        isFirstItem.element = true;
        Response response = Response.ok(arg_0 -> EventResource.getEvents$lambda$3(stream, actualLimit, isFirstItem, this, arg_0)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    private static final void getEvents$lambda$3$lambda$2(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        $tmp0.invoke(p0);
    }

    private static final void getEvents$lambda$3(Stream $stream, long $actualLimit, Ref.BooleanRef $isFirstItem, EventResource this$0, OutputStream output) {
        block1: {
            Intrinsics.checkNotNullParameter((Object)$stream, (String)"$stream");
            Intrinsics.checkNotNullParameter((Object)$isFirstItem, (String)"$isFirstItem");
            Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
            OutputStream outputStream = output;
            if (outputStream != null) {
                outputStream.write(91);
            }
            $stream.limit($actualLimit).forEach(arg_0 -> EventResource.getEvents$lambda$3$lambda$2((Function1)new Function1<Event, Unit>($isFirstItem, output, this$0){
                final /* synthetic */ Ref.BooleanRef $isFirstItem;
                final /* synthetic */ OutputStream $output;
                final /* synthetic */ EventResource this$0;
                {
                    this.$isFirstItem = $isFirstItem;
                    this.$output = $output;
                    this.this$0 = $receiver;
                    super(1);
                }

                public final void invoke(Event it) {
                    if (!this.$isFirstItem.element) {
                        OutputStream outputStream = this.$output;
                        if (outputStream != null) {
                            outputStream.write(44);
                        }
                    }
                    OutputStream outputStream = this.$output;
                    if (outputStream != null) {
                        outputStream.write(EventResource.access$getMapper$p(this.this$0).writeValueAsBytes((Object)it));
                    }
                    this.$isFirstItem.element = false;
                }
            }, arg_0));
            OutputStream outputStream2 = output;
            if (outputStream2 == null) break block1;
            outputStream2.write(93);
        }
    }

    public static final /* synthetic */ ObjectMapper access$getMapper$p(EventResource $this) {
        return $this.mapper;
    }
}

