/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.google.common.base.Preconditions
 *  com.querydsl.core.Tuple
 *  com.querydsl.core.types.EntityPath
 *  com.querydsl.core.types.Expression
 *  com.querydsl.core.types.Operator
 *  com.querydsl.core.types.OrderSpecifier
 *  com.querydsl.core.types.Path
 *  com.querydsl.core.types.Predicate
 *  com.querydsl.core.types.Projections
 *  com.querydsl.core.types.SubQueryExpression
 *  com.querydsl.core.types.dsl.BooleanExpression
 *  com.querydsl.core.types.dsl.CaseBuilder
 *  com.querydsl.core.types.dsl.ComparableExpression
 *  com.querydsl.core.types.dsl.ComparableExpressionBase
 *  com.querydsl.core.types.dsl.DateTimeExpression
 *  com.querydsl.core.types.dsl.DateTimeOperation
 *  com.querydsl.core.types.dsl.Expressions
 *  com.querydsl.core.types.dsl.NumberExpression
 *  com.querydsl.core.types.dsl.PathBuilder
 *  com.querydsl.core.types.dsl.SimpleExpression
 *  com.querydsl.core.types.dsl.SimpleTemplate
 *  com.querydsl.core.types.dsl.StringExpression
 *  com.querydsl.core.types.dsl.StringPath
 *  com.querydsl.core.types.dsl.Wildcard
 *  com.querydsl.sql.DatePart
 *  com.querydsl.sql.RelationalPath
 *  com.querydsl.sql.SQLExpressions
 *  com.querydsl.sql.SQLQuery
 *  com.querydsl.sql.SQLQueryFactory
 *  com.querydsl.sql.dml.SQLDeleteClause
 *  com.querydsl.sql.dml.SQLInsertClause
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.Grouping
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.store.server;

import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventCursor;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.Page;
import com.addonengine.addons.analytics.service.PageRequest;
import com.addonengine.addons.analytics.service.model.AnalyticsEvent;
import com.addonengine.addons.analytics.service.model.ContentRef;
import com.addonengine.addons.analytics.service.model.ContentSortField;
import com.addonengine.addons.analytics.service.model.ContentType;
import com.addonengine.addons.analytics.service.model.CountType;
import com.addonengine.addons.analytics.service.model.DatePeriodOptions;
import com.addonengine.addons.analytics.service.model.DatePeriodOptionsKt;
import com.addonengine.addons.analytics.service.model.GlobalUserSortField;
import com.addonengine.addons.analytics.service.model.SortOrder;
import com.addonengine.addons.analytics.service.model.SpaceLevelUserSortField;
import com.addonengine.addons.analytics.service.model.SpaceSortField;
import com.addonengine.addons.analytics.service.model.SpaceType;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.model.ContentViewsByUserData;
import com.addonengine.addons.analytics.store.model.DataRetentionEventData;
import com.addonengine.addons.analytics.store.model.EventData;
import com.addonengine.addons.analytics.store.model.EventsByChildContentData;
import com.addonengine.addons.analytics.store.model.EventsByPeriodData;
import com.addonengine.addons.analytics.store.model.EventsByTimestampData;
import com.addonengine.addons.analytics.store.model.FullContentStatistics;
import com.addonengine.addons.analytics.store.model.FullGlobalUserStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceStatistics;
import com.addonengine.addons.analytics.store.model.FullSpaceUserStatistics;
import com.addonengine.addons.analytics.store.server.BucketPeriodActivity;
import com.addonengine.addons.analytics.store.server.ChildContentActivityData;
import com.addonengine.addons.analytics.store.server.EventRepositoryServerImpl;
import com.addonengine.addons.analytics.store.server.HourTotalBucketPeriodActivity;
import com.addonengine.addons.analytics.store.server.TimedEvent;
import com.addonengine.addons.analytics.store.server.UserViewsData;
import com.addonengine.addons.analytics.store.server.querydsl.QContentStatistics;
import com.addonengine.addons.analytics.store.server.querydsl.QSpaceStatistics;
import com.addonengine.addons.analytics.store.server.querydsl.QTotalsByTimeBucket;
import com.addonengine.addons.analytics.store.server.querydsl.QUsersByTimeBucket;
import com.addonengine.addons.analytics.store.server.querydsl.QueryDslDbConnectionManager;
import com.addonengine.addons.analytics.store.server.querydsl.Tables;
import com.addonengine.addons.analytics.store.server.querydsl.operator.FastStringHash;
import com.addonengine.addons.analytics.store.server.querydsl.operator.UnixSecondToUtcDatetime;
import com.addonengine.addons.analytics.store.server.querydsl.operator.UtcDatetimeToUnixSecond;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.google.common.base.Preconditions;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.DateTimeOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.Grouping;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u00c0\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\b\u001a\u00020\tH\u0016J\u001c\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002J\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\u000f2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\t0\u000fH\u0002J\u0018\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\tH\u0016J\u0010\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\tH\u0016J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0012H\u0002J\u001c\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u001c\u0010\u001f\u001a\u0006\u0012\u0002\b\u00030 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002JZ\u0010%\u001a\u0006\u0012\u0002\b\u00030 2\u0006\u0010!\u001a\u00020&2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002JZ\u0010,\u001a\u0006\u0012\u0002\b\u00030 2\u0006\u0010!\u001a\u00020-2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u001c\u0010.\u001a\u0006\u0012\u0002\b\u00030 2\u0006\u0010!\u001a\u00020/2\u0006\u0010#\u001a\u000200H\u0002J\u0016\u00101\u001a\b\u0012\u0004\u0012\u0002020\u00112\u0006\u00103\u001a\u000204H\u0016J\n\u00105\u001a\u0004\u0018\u000106H\u0016J\b\u00107\u001a\u00020\tH\u0016J0\u00108\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020:0\u0011\u0012\u0004\u0012\u00020;092\u0006\u0010<\u001a\u00020=2\f\u0010>\u001a\b\u0012\u0004\u0012\u00020;0?H\u0016J\u0016\u0010@\u001a\b\u0012\u0004\u0012\u00020A0\u00112\u0006\u0010B\u001a\u00020\tH\u0016J>\u0010C\u001a\b\u0012\u0004\u0012\u00020D0\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010E\u001a\u00020F2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010G\u001a\u00020H2\b\b\u0002\u0010I\u001a\u00020JH\u0002J4\u0010K\u001a\b\u0012\u0004\u0012\u00020D0\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010L\u001a\u00020\u001c2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010G\u001a\u00020HH\u0016J4\u0010M\u001a\b\u0012\u0004\u0012\u00020D0\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010N\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010G\u001a\u00020HH\u0016J:\u0010O\u001a\b\u0012\u0004\u0012\u00020D0\u00112\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\f\u001a\u00020\r2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020R0Q2\u0006\u0010G\u001a\u00020HH\u0016Jx\u0010S\u001a\b\u0012\u0004\u0012\u00020T0\u00112\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\f\u001a\u00020\r2\f\u0010U\u001a\b\u0012\u0004\u0012\u00020V0Q2\u0006\u0010L\u001a\u00020\u001c2\u0006\u0010W\u001a\u00020\t2\u0006\u0010!\u001a\u00020\"2\u0006\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020\t2\u0006\u0010[\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u0006H\u0016J\u0084\u0001\u0010]\u001a\b\u0012\u0004\u0012\u00020^0\u00112\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\f\u001a\u00020\r2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020R0Q2\u0006\u0010!\u001a\u00020/2\u0006\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020\t2\u0006\u0010[\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u00062\f\u0010G\u001a\b\u0012\u0004\u0012\u00020H0QH\u0016J\u0092\u0001\u0010_\u001a\b\u0012\u0004\u0012\u00020`0\u00112\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\f\u001a\u00020\r2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020R0Q2\u0006\u0010!\u001a\u00020&2\u0006\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020\t2\u0006\u0010[\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u0006H\u0016J\u008c\u0001\u0010a\u001a\b\u0012\u0004\u0012\u00020b0\u00112\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\f\u001a\u00020\r2\u0006\u0010L\u001a\u00020\u001c2\u0006\u0010!\u001a\u00020-2\u0006\u0010X\u001a\u00020Y2\u0006\u0010Z\u001a\u00020\t2\u0006\u0010[\u001a\u00020\u00062\u0006\u0010\\\u001a\u00020\u0006H\u0016J\n\u0010c\u001a\u0004\u0018\u00010\u0017H\u0016J\n\u0010d\u001a\u0004\u0018\u000106H\u0016J\u000f\u0010e\u001a\u0004\u0018\u00010\tH\u0016\u00a2\u0006\u0002\u0010fJ\u000f\u0010g\u001a\u0004\u0018\u00010\tH\u0002\u00a2\u0006\u0002\u0010fJ4\u0010h\u001a\u000e\u0012\u0004\u0012\u0002Hj\u0012\u0004\u0012\u00020\u00170i\"\b\b\u0000\u0010j*\u00020k2\f\u0010l\u001a\b\u0012\u0004\u0012\u0002Hj0m2\u0006\u0010\f\u001a\u00020\rH\u0002J\u001e\u0010n\u001a\u00020\t2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020o0\u00112\u0006\u0010p\u001a\u00020JH\u0016J,\u0010q\u001a&\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t s*\u0012\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t\u0018\u00010r0rH\u0002J$\u0010t\u001a\u0006\u0012\u0002\b\u00030u2\u0006\u0010!\u001a\u00020\"2\u0006\u0010X\u001a\u00020Y2\u0006\u0010#\u001a\u00020$H\u0002Jb\u0010t\u001a\u0006\u0012\u0002\b\u00030u2\u0006\u0010!\u001a\u00020&2\u0006\u0010X\u001a\u00020Y2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002Jb\u0010t\u001a\u0006\u0012\u0002\b\u00030u2\u0006\u0010!\u001a\u00020-2\u0006\u0010X\u001a\u00020Y2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J$\u0010t\u001a\u0006\u0012\u0002\b\u00030u2\u0006\u0010!\u001a\u00020/2\u0006\u0010X\u001a\u00020Y2\u0006\u0010#\u001a\u000200H\u0002J\u001e\u0010v\u001a\n s*\u0004\u0018\u00010w0w2\f\u0010P\u001a\b\u0012\u0004\u0012\u00020R0QH\u0002J\u001c\u0010x\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00112\f\u0010P\u001a\b\u0012\u0004\u0012\u00020R0QH\u0002J(\u0010y\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020:0\u0011\u0012\u0004\u0012\u00020;092\f\u0010z\u001a\b\u0012\u0004\u0012\u00020:0\u0011H\u0002J:\u0010{\u001a&\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t s*\u0012\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t\u0018\u00010\u000f0\u000f2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J6\u0010|\u001a&\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t s*\u0012\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t\u0018\u00010}0}*\b\u0012\u0004\u0012\u00020\t0\u000fH\u0002J\u001d\u0010~\u001a\u0006\u0012\u0002\b\u00030\u007f*\u0006\u0012\u0002\b\u00030\u007f2\u0007\u0010\u0080\u0001\u001a\u00020JH\u0002J\u001e\u0010\u0081\u0001\u001a\u0006\u0012\u0002\b\u00030\u000f*\u0006\u0012\u0002\b\u00030\u000f2\u0007\u0010\u0080\u0001\u001a\u00020JH\u0002J9\u0010\u0082\u0001\u001a(\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t s*\u0013\u0012\f\u0012\n s*\u0004\u0018\u00010\t0\t\u0018\u00010\u0083\u00010\u0083\u0001*\b\u0012\u0004\u0012\u00020\t0\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0084\u0001"}, d2={"Lcom/addonengine/addons/analytics/store/server/EventRepositoryServerImpl;", "Lcom/addonengine/addons/analytics/store/EventRepository;", "db", "Lcom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager;", "(Lcom/addonengine/addons/analytics/store/server/querydsl/QueryDslDbConnectionManager;)V", "hour", "", "maxParamCount", "clearSampleEvents", "", "convertQueryDatesToEpochMilliseconds", "Lkotlin/Pair;", "datePeriodOptions", "Lcom/addonengine/addons/analytics/service/model/DatePeriodOptions;", "countOfEventTypes", "Lcom/querydsl/core/types/dsl/NumberExpression;", "types", "", "Lcom/addonengine/addons/analytics/service/model/AnalyticsEvent;", "countOfPositiveValues", "value", "deleteEventsBeforeDate", "date", "Ljava/time/Instant;", "numToDelete", "deleteOldestEvents", "batchSize", "eventNamesToQueryArg", "", "event", "events", "extractContentSortField", "Lcom/querydsl/core/types/dsl/ComparableExpressionBase;", "sortField", "Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "subquery", "Lcom/addonengine/addons/analytics/store/server/querydsl/QContentStatistics;", "extractGlobalUsersSortField", "Lcom/addonengine/addons/analytics/service/model/GlobalUserSortField;", "viewEvents", "createEvents", "updateEvents", "commentEvents", "contributorEvents", "extractSpaceLevelUsersSortField", "Lcom/addonengine/addons/analytics/service/model/SpaceLevelUserSortField;", "extractSpaceSortField", "Lcom/addonengine/addons/analytics/service/model/SpaceSortField;", "Lcom/addonengine/addons/analytics/store/server/querydsl/QSpaceStatistics;", "getContentViewsByUser", "Lcom/addonengine/addons/analytics/store/model/ContentViewsByUserData;", "content", "Lcom/addonengine/addons/analytics/service/model/ContentRef;", "getEarliestEvent", "Lcom/addonengine/addons/analytics/store/server/TimedEvent;", "getEstimatedCount", "getEvents", "Lcom/addonengine/addons/analytics/service/Page;", "Lcom/addonengine/addons/analytics/service/Event;", "Lcom/addonengine/addons/analytics/service/EventCursor;", "query", "Lcom/addonengine/addons/analytics/service/EventQuery;", "pageRequest", "Lcom/addonengine/addons/analytics/service/PageRequest;", "getEventsByChildContent", "Lcom/addonengine/addons/analytics/store/model/EventsByChildContentData;", "containerId", "getEventsByPeriod", "Lcom/addonengine/addons/analytics/store/model/EventsByPeriodData;", "predicate", "Lcom/querydsl/core/types/Predicate;", "countType", "Lcom/addonengine/addons/analytics/service/model/CountType;", "needsSpaceJoin", "", "getEventsByPeriodForContentInSpace", "spaceKey", "getEventsByPeriodForSingleContent", "contentId", "getEventsByPeriodForSpaces", "spaceTypes", "", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "getEventsForAllSpaceContent", "Lcom/addonengine/addons/analytics/store/model/FullContentStatistics;", "contentTypes", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "spaceId", "sortOrder", "Lcom/addonengine/addons/analytics/service/model/SortOrder;", "maxEventId", "offset", "limit", "getEventsForAllSpaces", "Lcom/addonengine/addons/analytics/store/model/FullSpaceStatistics;", "getEventsForGlobalUsers", "Lcom/addonengine/addons/analytics/store/model/FullGlobalUserStatistics;", "getEventsForSpaceUsers", "Lcom/addonengine/addons/analytics/store/model/FullSpaceUserStatistics;", "getFirstEventDate", "getLatestEvent", "getMaximumEventId", "()Ljava/lang/Long;", "getMinimumEventId", "groupBucketsByPeriod", "Lkotlin/collections/Grouping;", "T", "Lcom/addonengine/addons/analytics/store/server/BucketPeriodActivity;", "data", "", "insertEvents", "Lcom/addonengine/addons/analytics/store/model/EventData;", "useSampleStore", "nullExpression", "Lcom/querydsl/core/types/dsl/SimpleTemplate;", "kotlin.jvm.PlatformType", "sortFieldToQueryArg", "Lcom/querydsl/core/types/OrderSpecifier;", "spaceTypeAndStatusPredicate", "Lcom/querydsl/core/types/dsl/BooleanExpression;", "spaceTypesToQueryArg", "toEventsPage", "eventList", "uniqUsersOfEventTypes", "nullToZero", "Lcom/querydsl/core/types/dsl/ComparableExpression;", "orNullIf", "Lcom/querydsl/core/types/Expression;", "condition", "orZeroIf", "zeroToNull", "Lcom/querydsl/core/types/dsl/SimpleExpression;", "analytics"})
@SourceDebugExtension(value={"SMAP\nEventRepositoryServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/EventRepositoryServerImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n+ 4 Grouping.kt\nkotlin/collections/GroupingKt__GroupingKt\n+ 5 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 6 KotlinQueryDslExtensions.kt\ncom/addonengine/addons/analytics/store/server/querydsl/KotlinQueryDslExtensionsKt\n*L\n1#1,895:1\n1549#2:896\n1620#2,3:897\n1549#2:918\n1620#2,3:919\n1549#2:924\n1620#2,3:925\n1536#2:928\n1549#2:929\n1620#2,3:930\n1284#3,3:900\n1284#3,3:903\n164#4:906\n53#4:907\n80#4,6:908\n125#5:914\n152#5,3:915\n8#6:922\n8#6:923\n*S KotlinDebug\n*F\n+ 1 EventRepositoryServerImpl.kt\ncom/addonengine/addons/analytics/store/server/EventRepositoryServerImpl\n*L\n96#1:896\n96#1:897,3\n629#1:918\n629#1:919,3\n749#1:924\n749#1:925,3\n813#1:928\n819#1:929\n819#1:930,3\n134#1:900,3\n167#1:903,3\n241#1:906\n241#1:907\n241#1:908,6\n243#1:914\n243#1:915,3\n690#1:922\n704#1:923\n*E\n"})
public final class EventRepositoryServerImpl
implements EventRepository {
    @NotNull
    private final QueryDslDbConnectionManager db;
    private final int hour;
    private final int maxParamCount;

    @Autowired
    public EventRepositoryServerImpl(@NotNull QueryDslDbConnectionManager db) {
        Intrinsics.checkNotNullParameter((Object)db, (String)"db");
        this.db = db;
        this.hour = 3600000;
        this.maxParamCount = 2000;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<ContentViewsByUserData> getContentViewsByUser(@NotNull ContentRef content) {
        void $this$mapTo$iv$iv;
        AnalyticsEvent analyticsEvent;
        Intrinsics.checkNotNullParameter((Object)content, (String)"content");
        switch (WhenMappings.$EnumSwitchMapping$0[content.getType().ordinal()]) {
            case 1: {
                analyticsEvent = AnalyticsEvent.PAGE_VIEWED;
                break;
            }
            case 2: {
                analyticsEvent = AnalyticsEvent.BLOG_VIEWED;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        AnalyticsEvent event = analyticsEvent;
        List viewsByUser2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<UserViewsData>>(this, event, content){
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ AnalyticsEvent $event;
            final /* synthetic */ ContentRef $content;
            {
                this.this$0 = $receiver;
                this.$event = $event;
                this.$content = $content;
                super(1);
            }

            public final List<UserViewsData> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[4];
                expressionArray[0] = Tables.INSTANCE.getEvent().getUserKey();
                Intrinsics.checkNotNullExpressionValue((Object)Tables.INSTANCE.getEvent().getVersionModificationDate().max(), (String)"max(...)");
                Intrinsics.checkNotNullExpressionValue((Object)Tables.INSTANCE.getEvent().getEventAt().max(), (String)"max(...)");
                Intrinsics.checkNotNullExpressionValue((Object)Wildcard.count, (String)"count");
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection = false;
                expressionArray = new Predicate[]{Tables.INSTANCE.getEvent().getName().eq((Object)EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, this.$event)), Tables.INSTANCE.getEvent().getContainerId().eq((Object)this.$content.getId())};
                return ((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(UserViewsData.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)Tables.INSTANCE.getEvent())).where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getUserKey())).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)viewsByUser2);
        Iterable $this$map$iv = viewsByUser2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserViewsData userViewsData = (UserViewsData)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String string = Intrinsics.areEqual((Object)it.getUserKey(), (Object)"[anonymous]") ? null : it.getUserKey();
            Instant instant = Instant.ofEpochMilli(it.getLastVersionViewedModificationDate());
            Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochMilli(...)");
            Instant instant2 = Instant.ofEpochMilli(it.getLastViewedAt());
            Intrinsics.checkNotNullExpressionValue((Object)instant2, (String)"ofEpochMilli(...)");
            collection.add(new ContentViewsByUserData(string, instant, instant2, it.getViews()));
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public long deleteEventsBeforeDate(@NotNull Instant date, long numToDelete) {
        void $this$fold$iv;
        Intrinsics.checkNotNullParameter((Object)date, (String)"date");
        List events2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<DataRetentionEventData>>(numToDelete){
            final /* synthetic */ long $numToDelete;
            {
                this.$numToDelete = $numToDelete;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<DataRetentionEventData> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void exprs$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getId(), Tables.INSTANCE.getEvent().getEventAt()};
                boolean $i$f$projection = false;
                return ((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(DataRetentionEventData.class, (Expression[])((Expression[])Arrays.copyOf(exprs$iv, ((void)exprs$iv).length)))).from((Expression)Tables.INSTANCE.getEvent())).orderBy(Tables.INSTANCE.getEvent().getId().asc())).limit(this.$numToDelete)).fetch();
            }
        }, 1, null);
        long dateMilli = date.toEpochMilli();
        Intrinsics.checkNotNull((Object)events2);
        Sequence sequence = SequencesKt.chunked((Sequence)SequencesKt.map((Sequence)SequencesKt.filter((Sequence)CollectionsKt.asSequence((Iterable)events2), (Function1)((Function1)new Function1<DataRetentionEventData, Boolean>(dateMilli){
            final /* synthetic */ long $dateMilli;
            {
                this.$dateMilli = $dateMilli;
                super(1);
            }

            @NotNull
            public final Boolean invoke(DataRetentionEventData it) {
                return it.getEventAt() <= this.$dateMilli;
            }
        })), (Function1)deleteEventsBeforeDate.2.INSTANCE), (int)this.maxParamCount);
        long initial$iv = 0L;
        boolean $i$f$fold = false;
        long accumulator$iv = initial$iv;
        for (Object element$iv : $this$fold$iv) {
            void element;
            List list = (List)element$iv;
            long sum = accumulator$iv;
            boolean bl = false;
            accumulator$iv = sum + ((Number)this.db.execute(false, (Function1)new Function1<SQLQueryFactory, Long>((List<Long>)element){
                final /* synthetic */ List<Long> $element;
                {
                    this.$element = $element;
                    super(1);
                }

                @NotNull
                public final Long invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                    Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                    return ((SQLDeleteClause)sqlQueryFactory.delete((RelationalPath)Tables.INSTANCE.getEvent()).where((Predicate)Tables.INSTANCE.getEvent().getId().in((Collection)this.$element))).execute();
                }
            })).longValue();
        }
        return accumulator$iv;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public long deleteOldestEvents(long batchSize) {
        void $this$fold$iv;
        List events2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<Long>>(batchSize){
            final /* synthetic */ long $batchSize;
            {
                this.$batchSize = $batchSize;
                super(1);
            }

            public final List<Long> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                return ((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Tables.INSTANCE.getEvent().getId()).from((Expression)Tables.INSTANCE.getEvent())).orderBy(Tables.INSTANCE.getEvent().getId().asc())).limit(this.$batchSize)).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)events2);
        Sequence sequence = SequencesKt.chunked((Sequence)SequencesKt.map((Sequence)CollectionsKt.asSequence((Iterable)events2), (Function1)deleteOldestEvents.1.INSTANCE), (int)this.maxParamCount);
        long initial$iv = 0L;
        boolean $i$f$fold = false;
        long accumulator$iv = initial$iv;
        for (Object element$iv : $this$fold$iv) {
            void element;
            List list = (List)element$iv;
            long sum = accumulator$iv;
            boolean bl = false;
            accumulator$iv = sum + ((Number)this.db.execute(false, (Function1)new Function1<SQLQueryFactory, Long>((List<Long>)element){
                final /* synthetic */ List<Long> $element;
                {
                    this.$element = $element;
                    super(1);
                }

                @NotNull
                public final Long invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                    Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                    return ((SQLDeleteClause)sqlQueryFactory.delete((RelationalPath)Tables.INSTANCE.getEvent()).where((Predicate)Tables.INSTANCE.getEvent().getId().in((Collection)this.$element))).execute();
                }
            })).longValue();
        }
        return accumulator$iv;
    }

    @Override
    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForSpaces(@NotNull List<? extends AnalyticsEvent> events2, @NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull CountType countType) {
        Intrinsics.checkNotNullParameter(events2, (String)"events");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)countType), (String)"countType");
        BooleanExpression booleanExpression = this.spaceTypeAndStatusPredicate(spaceTypes);
        Intrinsics.checkNotNullExpressionValue((Object)booleanExpression, (String)"spaceTypeAndStatusPredicate(...)");
        return this.getEventsByPeriod(events2, (Predicate)booleanExpression, datePeriodOptions, countType, true);
    }

    @Override
    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForContentInSpace(@NotNull List<? extends AnalyticsEvent> events2, @NotNull String spaceKey, @NotNull DatePeriodOptions datePeriodOptions, @NotNull CountType countType) {
        Intrinsics.checkNotNullParameter(events2, (String)"events");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)((Object)countType), (String)"countType");
        BooleanExpression booleanExpression = Tables.INSTANCE.getEvent().getSpaceKey().eq((Object)spaceKey);
        Intrinsics.checkNotNullExpressionValue((Object)booleanExpression, (String)"eq(...)");
        return EventRepositoryServerImpl.getEventsByPeriod$default(this, events2, (Predicate)booleanExpression, datePeriodOptions, countType, false, 16, null);
    }

    @Override
    @NotNull
    public List<EventsByPeriodData> getEventsByPeriodForSingleContent(@NotNull List<? extends AnalyticsEvent> events2, long contentId, @NotNull DatePeriodOptions datePeriodOptions, @NotNull CountType countType) {
        Intrinsics.checkNotNullParameter(events2, (String)"events");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)((Object)countType), (String)"countType");
        BooleanExpression booleanExpression = Tables.INSTANCE.getEvent().getContainerId().eq((Object)contentId);
        Intrinsics.checkNotNullExpressionValue((Object)booleanExpression, (String)"eq(...)");
        return EventRepositoryServerImpl.getEventsByPeriod$default(this, events2, (Predicate)booleanExpression, datePeriodOptions, countType, false, 16, null);
    }

    private final BooleanExpression spaceTypeAndStatusPredicate(Set<? extends SpaceType> spaceTypes) {
        return Tables.INSTANCE.getSpaces().getSpaceStatus().eq((Object)SpaceStatus.CURRENT.toString()).and((Predicate)Tables.INSTANCE.getSpaces().getSpaceType().in((Collection)this.spaceTypesToQueryArg(spaceTypes)));
    }

    /*
     * WARNING - void declaration
     */
    private final List<EventsByPeriodData> getEventsByPeriod(List<? extends AnalyticsEvent> events2, Predicate predicate, DatePeriodOptions datePeriodOptions, CountType countType, boolean needsSpaceJoin) {
        List list;
        Pair<Long, Long> pair = this.convertQueryDatesToEpochMilliseconds(datePeriodOptions);
        long from = ((Number)pair.component1()).longValue();
        long to = ((Number)pair.component2()).longValue();
        Expression clientTimezoneOffset = Expressions.constant((Object)datePeriodOptions.getTimezone().getRules().getOffset(Instant.now()).getTotalSeconds());
        Expression[] expressionArray = new Expression[2];
        Expression[] expressionArray2 = new Expression[]{Tables.INSTANCE.getEvent().getEventAt().divide((Number)1000), clientTimezoneOffset};
        expressionArray[0] = SQLExpressions.datetrunc((DatePart)datePeriodOptions.getPeriod().getDatePart(), (DateTimeExpression)((DateTimeExpression)Expressions.dateTimeOperation(Comparable.class, (Operator)UnixSecondToUtcDatetime.INSTANCE, (Expression[])expressionArray2)));
        expressionArray[1] = clientTimezoneOffset;
        DateTimeOperation timestampBucketExpression = Expressions.dateTimeOperation(Comparable.class, (Operator)UtcDatetimeToUnixSecond.INSTANCE, (Expression[])expressionArray);
        switch (WhenMappings.$EnumSwitchMapping$1[countType.ordinal()]) {
            case 1: {
                void $this$mapTo$iv$iv;
                void result;
                void destination$iv$iv$iv;
                void $this$aggregateTo$iv$iv$iv;
                void $this$fold$iv;
                List totalPeriodActivity2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<HourTotalBucketPeriodActivity>>(this, events2, from, to, predicate, needsSpaceJoin){
                    final /* synthetic */ EventRepositoryServerImpl this$0;
                    final /* synthetic */ List<AnalyticsEvent> $events;
                    final /* synthetic */ long $from;
                    final /* synthetic */ long $to;
                    final /* synthetic */ Predicate $predicate;
                    final /* synthetic */ boolean $needsSpaceJoin;
                    {
                        this.this$0 = $receiver;
                        this.$events = $events;
                        this.$from = $from;
                        this.$to = $to;
                        this.$predicate = $predicate;
                        this.$needsSpaceJoin = $needsSpaceJoin;
                        super(1);
                    }

                    /*
                     * WARNING - void declaration
                     */
                    public final List<HourTotalBucketPeriodActivity> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                        void exprs$iv;
                        Expression[] $this$isIn$iv;
                        Predicate[] $this$_as$iv;
                        Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                        NumberExpression numberExpression = Tables.INSTANCE.getEvent().getEventAt().divide((Number)EventRepositoryServerImpl.access$getHour$p(this.this$0)).floor();
                        Intrinsics.checkNotNullExpressionValue((Object)numberExpression, (String)"floor(...)");
                        SimpleExpression simpleExpression = (SimpleExpression)numberExpression;
                        String alias$iv322 = "BUCKET";
                        boolean $i$f$_as = false;
                        $this$_as$iv = SQLExpressions.select((Expression)((Expression)$this$_as$iv.as(alias$iv322))).from((Expression)Tables.INSTANCE.getEvent());
                        boolean alias$iv322 = this.$needsSpaceJoin;
                        SQLQuery it = (SQLQuery)$this$_as$iv;
                        boolean bl = false;
                        $this$_as$iv = new Predicate[4];
                        SimpleExpression alias$iv322 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                        Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, this.$events);
                        boolean $i$f$isIn = false;
                        $this$_as$iv[0] = $this$isIn$iv.in(right$iv);
                        $this$_as$iv[1] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from);
                        $this$_as$iv[2] = Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to);
                        $this$_as$iv[3] = this.$predicate;
                        SQLQuery eventBucketsQuery = (SQLQuery)(alias$iv322 ? (SQLQuery)((SQLQuery)it.innerJoin((EntityPath)Tables.INSTANCE.getSpaces())).on((Predicate)Tables.INSTANCE.getEvent().getSpaceKey().eq((Expression)Tables.INSTANCE.getSpaces().getSpaceKey())) : it).where($this$_as$iv);
                        PathBuilder ebq = new PathBuilder(Tuple.class, "E");
                        $this$isIn$iv = new Expression[2];
                        Intrinsics.checkNotNullExpressionValue((Object)ebq.getNumber("BUCKET", Double.TYPE), (String)"getNumber(...)");
                        Intrinsics.checkNotNullExpressionValue((Object)Wildcard.count, (String)"count");
                        boolean $i$f$projection = false;
                        return ((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(HourTotalBucketPeriodActivity.class, (Expression[])((Expression[])Arrays.copyOf(exprs$iv, ((void)exprs$iv).length)))).from((SubQueryExpression)eventBucketsQuery, (Path)ebq)).groupBy((Expression)ebq.get("BUCKET"))).fetch();
                    }
                }, 1, null);
                Intrinsics.checkNotNull((Object)totalPeriodActivity2);
                Grouping grouping = this.groupBucketsByPeriod(totalPeriodActivity2, datePeriodOptions);
                Long initialValue$iv = 0L;
                boolean $i$f$fold22 = false;
                void $this$aggregate$iv$iv = $this$fold$iv;
                boolean $i$f$aggregate = false;
                Iterator iterator = $this$aggregate$iv$iv;
                Map map = new LinkedHashMap();
                boolean $i$f$aggregateTo = false;
                Iterator iterator2 = $this$aggregateTo$iv$iv$iv.sourceIterator();
                while (iterator2.hasNext()) {
                    void periodActivity;
                    void acc$iv;
                    void first$iv;
                    void e$iv;
                    Object e$iv$iv$iv = iterator2.next();
                    Object key$iv$iv$iv = $this$aggregateTo$iv$iv$iv.keyOf(e$iv$iv$iv);
                    Object accumulator$iv$iv$iv = destination$iv$iv$iv.get(key$iv$iv$iv);
                    boolean bl = accumulator$iv$iv$iv == null && !destination$iv$iv$iv.containsKey(key$iv$iv$iv);
                    Object e = e$iv$iv$iv;
                    Object v = accumulator$iv$iv$iv;
                    Object object = key$iv$iv$iv;
                    void var33_36 = destination$iv$iv$iv;
                    boolean bl2 = false;
                    HourTotalBucketPeriodActivity hourTotalBucketPeriodActivity = (HourTotalBucketPeriodActivity)e$iv;
                    long count = ((Number)(first$iv != false ? initialValue$iv : acc$iv)).longValue();
                    boolean bl3 = false;
                    Long l = count + periodActivity.getTotal();
                    var33_36.put(object, l);
                }
                void $this$map$iv = result = destination$iv$iv$iv;
                boolean $i$f$map = false;
                void $i$f$fold22 = $this$map$iv;
                Collection destination$iv$iv = new ArrayList($this$map$iv.size());
                boolean $i$f$mapTo = false;
                iterator = $this$mapTo$iv$iv.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry item$iv$iv;
                    Map.Entry entry = item$iv$iv = iterator.next();
                    Collection collection = destination$iv$iv;
                    boolean bl = false;
                    Instant date = (Instant)entry.getKey();
                    long total = ((Number)entry.getValue()).longValue();
                    collection.add(new EventsByPeriodData(date, total));
                }
                list = (List)destination$iv$iv;
                break;
            }
            case 2: {
                QUsersByTimeBucket internalSubquery = new QUsersByTimeBucket("stat_internal");
                QTotalsByTimeBucket externalSubquery = new QTotalsByTimeBucket("stat_external");
                list = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<? extends EventsByPeriodData>>(externalSubquery, internalSubquery, timestampBucketExpression, this, events2, from, to, predicate, needsSpaceJoin){
                    final /* synthetic */ QTotalsByTimeBucket $externalSubquery;
                    final /* synthetic */ QUsersByTimeBucket $internalSubquery;
                    final /* synthetic */ DateTimeOperation<Comparable<?>> $timestampBucketExpression;
                    final /* synthetic */ EventRepositoryServerImpl this$0;
                    final /* synthetic */ List<AnalyticsEvent> $events;
                    final /* synthetic */ long $from;
                    final /* synthetic */ long $to;
                    final /* synthetic */ Predicate $predicate;
                    final /* synthetic */ boolean $needsSpaceJoin;
                    {
                        this.$externalSubquery = $externalSubquery;
                        this.$internalSubquery = $internalSubquery;
                        this.$timestampBucketExpression = $timestampBucketExpression;
                        this.this$0 = $receiver;
                        this.$events = $events;
                        this.$from = $from;
                        this.$to = $to;
                        this.$predicate = $predicate;
                        this.$needsSpaceJoin = $needsSpaceJoin;
                        super(1);
                    }

                    /*
                     * WARNING - void declaration
                     */
                    @NotNull
                    public final List<EventsByPeriodData> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                        void $this$mapTo$iv$iv;
                        Iterable $this$isIn$iv;
                        void it;
                        Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                        Predicate[] predicateArray = new Expression[2];
                        predicateArray[0] = this.$externalSubquery.getBucketStartTimestamp();
                        Intrinsics.checkNotNullExpressionValue((Object)Wildcard.count, (String)"count");
                        Predicate[] exprs$iv = predicateArray;
                        boolean $i$f$projection4222 = false;
                        SQLQuery sQLQuery = sqlQueryFactory.select((Expression)Projections.constructor(EventsByTimestampData.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length)));
                        exprs$iv = new Expression[2];
                        exprs$iv[0] = this.$timestampBucketExpression.as(this.$internalSubquery.getBucketStartTimestamp().getMetadata().getName());
                        Expression[] $i$f$projection4222 = new Expression[]{Tables.INSTANCE.getEvent().getUserKey()};
                        exprs$iv[1] = Expressions.operation(Comparable.class, (Operator)FastStringHash.INSTANCE, (Expression[])$i$f$projection4222).as(this.$internalSubquery.getUserKeyHash().getMetadata().getName());
                        exprs$iv = sqlQueryFactory.select((Expression[])exprs$iv).from((Expression)Tables.INSTANCE.getEvent());
                        boolean $i$f$projection4222 = this.$needsSpaceJoin;
                        SQLQuery sQLQuery2 = (SQLQuery)exprs$iv;
                        SQLQuery sQLQuery3 = sqlQueryFactory.select((Expression)this.$internalSubquery.getBucketStartTimestamp().as(this.$externalSubquery.getBucketStartTimestamp().getMetadata().getName()));
                        Object object = sQLQuery;
                        boolean bl = false;
                        void var13_12 = $i$f$projection4222 ? (SQLQuery)((SQLQuery)it.innerJoin((EntityPath)Tables.INSTANCE.getSpaces())).on((Predicate)Tables.INSTANCE.getEvent().getSpaceKey().eq((Expression)Tables.INSTANCE.getSpaces().getSpaceKey())) : it;
                        exprs$iv = new Predicate[4];
                        SimpleExpression $i$f$projection4222 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                        Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, this.$events);
                        boolean $i$f$isIn = false;
                        exprs$iv[0] = $this$isIn$iv.in(right$iv);
                        exprs$iv[1] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from);
                        exprs$iv[2] = Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to);
                        exprs$iv[3] = this.$predicate;
                        SQLQuery sQLQuery4 = (SQLQuery)sQLQuery3.from((Expression)((SQLQuery)var13_12.where(exprs$iv)).as((Path)this.$internalSubquery));
                        exprs$iv = new Expression[]{this.$internalSubquery.getBucketStartTimestamp(), this.$internalSubquery.getUserKeyHash()};
                        List list = ((SQLQuery)((SQLQuery)object.from((Expression)((SQLQuery)sQLQuery4.groupBy((Expression[])exprs$iv)).as((Path)this.$externalSubquery))).groupBy((Expression)this.$externalSubquery.getBucketStartTimestamp())).fetch();
                        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"fetch(...)");
                        Iterable $this$map$iv = list;
                        boolean $i$f$map = false;
                        $this$isIn$iv = $this$map$iv;
                        Collection destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                        boolean $i$f$mapTo = false;
                        for (T item$iv$iv : $this$mapTo$iv$iv) {
                            void it2;
                            EventsByTimestampData eventsByTimestampData = (EventsByTimestampData)item$iv$iv;
                            object = destination$iv$iv;
                            boolean bl2 = false;
                            Instant instant = Instant.ofEpochSecond(it2.getTimestamp());
                            Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochSecond(...)");
                            object.add(new EventsByPeriodData(instant, it2.getTotal()));
                        }
                        return (List)destination$iv$iv;
                    }
                }, 1, null);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return list;
    }

    static /* synthetic */ List getEventsByPeriod$default(EventRepositoryServerImpl eventRepositoryServerImpl, List list, Predicate predicate, DatePeriodOptions datePeriodOptions, CountType countType, boolean bl, int n, Object object) {
        if ((n & 0x10) != 0) {
            bl = false;
        }
        return eventRepositoryServerImpl.getEventsByPeriod(list, predicate, datePeriodOptions, countType, bl);
    }

    @Override
    @NotNull
    public List<FullSpaceStatistics> getEventsForAllSpaces(@NotNull List<? extends AnalyticsEvent> viewEvents, @NotNull List<? extends AnalyticsEvent> createEvents, @NotNull List<? extends AnalyticsEvent> updateEvents, @NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull SpaceSortField sortField, @NotNull SortOrder sortOrder, long maxEventId, int offset, int limit, @NotNull Set<? extends CountType> countType) {
        Intrinsics.checkNotNullParameter(viewEvents, (String)"viewEvents");
        Intrinsics.checkNotNullParameter(createEvents, (String)"createEvents");
        Intrinsics.checkNotNullParameter(updateEvents, (String)"updateEvents");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Intrinsics.checkNotNullParameter(countType, (String)"countType");
        Preconditions.checkArgument((boolean)EnumSet.of((Enum)CountType.TOTAL, (Enum)CountType.UNIQUE).containsAll((Collection)countType));
        Preconditions.checkArgument((!countType.isEmpty() ? 1 : 0) != 0);
        if (sortField == SpaceSortField.VIEWED_COUNT && !countType.contains((Object)CountType.TOTAL) || sortField == SpaceSortField.VIEWED_USERS && !countType.contains((Object)CountType.UNIQUE)) {
            throw new IllegalArgumentException("Requested sort field " + (Object)((Object)sortField) + " is not compatible with count type " + countType);
        }
        Pair<Long, Long> pair = this.convertQueryDatesToEpochMilliseconds(datePeriodOptions);
        long from = ((Number)pair.component1()).longValue();
        long to = ((Number)pair.component2()).longValue();
        if (countType.contains((Object)CountType.UNIQUE)) {
            Object object = QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<FullSpaceStatistics>>(countType, this, viewEvents, createEvents, updateEvents, from, to, maxEventId, spaceTypes, sortField, sortOrder, offset, limit){
                final /* synthetic */ Set<CountType> $countType;
                final /* synthetic */ EventRepositoryServerImpl this$0;
                final /* synthetic */ List<AnalyticsEvent> $viewEvents;
                final /* synthetic */ List<AnalyticsEvent> $createEvents;
                final /* synthetic */ List<AnalyticsEvent> $updateEvents;
                final /* synthetic */ long $from;
                final /* synthetic */ long $to;
                final /* synthetic */ long $maxEventId;
                final /* synthetic */ Set<SpaceType> $spaceTypes;
                final /* synthetic */ SpaceSortField $sortField;
                final /* synthetic */ SortOrder $sortOrder;
                final /* synthetic */ int $offset;
                final /* synthetic */ int $limit;
                {
                    this.$countType = $countType;
                    this.this$0 = $receiver;
                    this.$viewEvents = $viewEvents;
                    this.$createEvents = $createEvents;
                    this.$updateEvents = $updateEvents;
                    this.$from = $from;
                    this.$to = $to;
                    this.$maxEventId = $maxEventId;
                    this.$spaceTypes = $spaceTypes;
                    this.$sortField = $sortField;
                    this.$sortOrder = $sortOrder;
                    this.$offset = $offset;
                    this.$limit = $limit;
                    super(1);
                }

                /*
                 * WARNING - void declaration
                 */
                public final List<FullSpaceStatistics> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                    void $this$isIn$iv;
                    void exprs$iv;
                    Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                    QSpaceStatistics internalSubquery = new QSpaceStatistics("statInternal");
                    QSpaceStatistics externalSubquery = new QSpaceStatistics("statExternal");
                    boolean calculateOnlyUniques = !this.$countType.contains((Object)((Object)CountType.TOTAL));
                    Expression[] expressionArray = new Expression[7];
                    expressionArray[0] = Tables.INSTANCE.getSpaces().getSpaceKey();
                    expressionArray[1] = Tables.INSTANCE.getSpaces().getSpaceName();
                    Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$zeroToNull(this.this$0, (NumberExpression)externalSubquery.getMaxEventAt()), (String)"access$zeroToNull(...)");
                    ComparableExpression comparableExpression = EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)externalSubquery.getViewedCount());
                    Intrinsics.checkNotNullExpressionValue((Object)comparableExpression, (String)"access$nullToZero(...)");
                    expressionArray[3] = EventRepositoryServerImpl.access$orNullIf(this.this$0, (Expression)comparableExpression, calculateOnlyUniques);
                    Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)externalSubquery.getCreatedCount()), (String)"access$nullToZero(...)");
                    Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)externalSubquery.getUpdatedCount()), (String)"access$nullToZero(...)");
                    Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)externalSubquery.getViewedUsers()), (String)"access$nullToZero(...)");
                    boolean $i$f$projection22 = false;
                    SQLQuery sQLQuery = sqlQueryFactory.select((Expression)Projections.constructor(FullSpaceStatistics.class, (Expression[])((Expression[])Arrays.copyOf(exprs$iv, ((void)exprs$iv).length))));
                    expressionArray = new Expression[6];
                    expressionArray[0] = internalSubquery.getSpaceKey().as(externalSubquery.getSpaceKey().getMetadata().getName());
                    expressionArray[1] = internalSubquery.getMaxEventAt().max().as(externalSubquery.getMaxEventAt().getMetadata().getName());
                    NumberExpression numberExpression = internalSubquery.getViewedCount().sum();
                    Intrinsics.checkNotNullExpressionValue((Object)numberExpression, (String)"sum(...)");
                    expressionArray[2] = EventRepositoryServerImpl.access$orZeroIf(this.this$0, numberExpression, calculateOnlyUniques).as(externalSubquery.getViewedCount().getMetadata().getName());
                    expressionArray[3] = internalSubquery.getCreatedCount().sum().as(externalSubquery.getCreatedCount().getMetadata().getName());
                    expressionArray[4] = internalSubquery.getUpdatedCount().sum().as(externalSubquery.getUpdatedCount().getMetadata().getName());
                    expressionArray[5] = EventRepositoryServerImpl.access$countOfPositiveValues(this.this$0, (NumberExpression)internalSubquery.getViewedCount()).as(externalSubquery.getViewedUsers().getMetadata().getName());
                    SQLQuery sQLQuery2 = sqlQueryFactory.select(expressionArray);
                    expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getSpaceKey().as(internalSubquery.getSpaceKey().getMetadata().getName()), Tables.INSTANCE.getEvent().getEventAt().max().as(internalSubquery.getMaxEventAt().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$viewEvents).as(internalSubquery.getViewedCount().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$createEvents).as(internalSubquery.getCreatedCount().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$updateEvents).as(internalSubquery.getUpdatedCount().getMetadata().getName())};
                    SQLQuery sQLQuery3 = (SQLQuery)sqlQueryFactory.select(expressionArray).from((Expression)Tables.INSTANCE.getEvent());
                    expressionArray = new Predicate[3];
                    SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                    Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)this.$viewEvents, (Iterable)this.$createEvents), (Iterable)this.$updateEvents)));
                    boolean $i$f$isIn = false;
                    expressionArray[0] = $this$isIn$iv.in(right$iv);
                    expressionArray[1] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from).and((Predicate)Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to));
                    expressionArray[2] = Tables.INSTANCE.getEvent().getId().loe((Number)this.$maxEventId);
                    SQLQuery sQLQuery4 = (SQLQuery)sQLQuery3.where((Predicate[])expressionArray);
                    expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getSpaceKey(), Tables.INSTANCE.getEvent().getUserKey()};
                    expressionArray = ((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)sQLQuery.from((Expression)((SQLQuery)((SQLQuery)sQLQuery2.from((Expression)((SQLQuery)sQLQuery4.groupBy(expressionArray)).as((Path)internalSubquery))).groupBy((Expression)internalSubquery.getSpaceKey())).as((Path)externalSubquery))).rightJoin((EntityPath)Tables.INSTANCE.getSpaces())).on((Predicate)externalSubquery.getSpaceKey().eq((Expression)Tables.INSTANCE.getSpaces().getSpaceKey()))).where((Predicate)EventRepositoryServerImpl.access$spaceTypeAndStatusPredicate(this.this$0, this.$spaceTypes))).orderBy(EventRepositoryServerImpl.access$sortFieldToQueryArg(this.this$0, this.$sortField, this.$sortOrder, externalSubquery));
                    SpaceSortField spaceSortField = this.$sortField;
                    SQLQuery it = (SQLQuery)expressionArray;
                    boolean bl = false;
                    return ((SQLQuery)((SQLQuery)((SQLQuery)(spaceSortField == SpaceSortField.SPACE_NAME ? it : (SQLQuery)it.orderBy(Tables.INSTANCE.getSpaces().getSpaceName().asc())).orderBy(Tables.INSTANCE.getSpaces().getSpaceKey().asc())).offset((long)this.$offset)).limit((long)this.$limit)).fetch();
                }
            }, 1, null);
            Intrinsics.checkNotNull((Object)object);
            return (List)object;
        }
        Object object = QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<FullSpaceStatistics>>(this, viewEvents, createEvents, updateEvents, from, to, maxEventId, spaceTypes, sortField, sortOrder, offset, limit){
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $createEvents;
            final /* synthetic */ List<AnalyticsEvent> $updateEvents;
            final /* synthetic */ long $from;
            final /* synthetic */ long $to;
            final /* synthetic */ long $maxEventId;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ SpaceSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ int $offset;
            final /* synthetic */ int $limit;
            {
                this.this$0 = $receiver;
                this.$viewEvents = $viewEvents;
                this.$createEvents = $createEvents;
                this.$updateEvents = $updateEvents;
                this.$from = $from;
                this.$to = $to;
                this.$maxEventId = $maxEventId;
                this.$spaceTypes = $spaceTypes;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$offset = $offset;
                this.$limit = $limit;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<FullSpaceStatistics> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void $this$isIn$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                QSpaceStatistics subquery = new QSpaceStatistics("stat");
                Expression[] expressionArray = new Expression[7];
                expressionArray[0] = Tables.INSTANCE.getSpaces().getSpaceKey();
                expressionArray[1] = Tables.INSTANCE.getSpaces().getSpaceName();
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$zeroToNull(this.this$0, (NumberExpression)subquery.getMaxEventAt()), (String)"access$zeroToNull(...)");
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)subquery.getViewedCount()), (String)"access$nullToZero(...)");
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)subquery.getCreatedCount()), (String)"access$nullToZero(...)");
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullToZero(this.this$0, (NumberExpression)subquery.getUpdatedCount()), (String)"access$nullToZero(...)");
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$nullExpression(this.this$0), (String)"access$nullExpression(...)");
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection22 = false;
                expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getSpaceKey().as(subquery.getSpaceKey().getMetadata().getName()), Tables.INSTANCE.getEvent().getEventAt().max().as(subquery.getMaxEventAt().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$viewEvents).as(subquery.getViewedCount().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$createEvents).as(subquery.getCreatedCount().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$updateEvents).as(subquery.getUpdatedCount().getMetadata().getName())};
                SQLQuery sQLQuery = (SQLQuery)sqlQueryFactory.select(expressionArray).from((Expression)Tables.INSTANCE.getEvent());
                expressionArray = new Predicate[3];
                SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)this.$viewEvents, (Iterable)this.$createEvents), (Iterable)this.$updateEvents)));
                boolean $i$f$isIn = false;
                expressionArray[0] = $this$isIn$iv.in(right$iv);
                expressionArray[1] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from).and((Predicate)Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to));
                expressionArray[2] = Tables.INSTANCE.getEvent().getId().loe((Number)this.$maxEventId);
                expressionArray = ((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(FullSpaceStatistics.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)((SQLQuery)((SQLQuery)sQLQuery.where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getSpaceKey())).as((Path)subquery))).rightJoin((EntityPath)Tables.INSTANCE.getSpaces())).on((Predicate)subquery.getSpaceKey().eq((Expression)Tables.INSTANCE.getSpaces().getSpaceKey()))).where((Predicate)EventRepositoryServerImpl.access$spaceTypeAndStatusPredicate(this.this$0, this.$spaceTypes))).orderBy(EventRepositoryServerImpl.access$sortFieldToQueryArg(this.this$0, this.$sortField, this.$sortOrder, subquery));
                SpaceSortField spaceSortField = this.$sortField;
                SQLQuery it = (SQLQuery)expressionArray;
                boolean bl = false;
                return ((SQLQuery)((SQLQuery)((SQLQuery)(spaceSortField == SpaceSortField.SPACE_NAME ? it : (SQLQuery)it.orderBy(Tables.INSTANCE.getSpaces().getSpaceName().asc())).orderBy(Tables.INSTANCE.getSpaces().getSpaceKey().asc())).offset((long)this.$offset)).limit((long)this.$limit)).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)object);
        return (List)object;
    }

    private final SimpleTemplate<Long> nullExpression() {
        return Expressions.template(Long.TYPE, (String)"null", (Object[])new Object[0]);
    }

    private final Expression<?> orNullIf(Expression<?> $this$orNullIf, boolean condition) {
        if (condition) {
            SimpleTemplate<Long> simpleTemplate = this.nullExpression();
            Intrinsics.checkNotNullExpressionValue(simpleTemplate, (String)"nullExpression(...)");
            return (Expression)simpleTemplate;
        }
        return $this$orNullIf;
    }

    private final NumberExpression<?> orZeroIf(NumberExpression<?> $this$orZeroIf, boolean condition) {
        if (condition) {
            NumberExpression numberExpression = Expressions.ZERO;
            Intrinsics.checkNotNullExpressionValue((Object)numberExpression, (String)"ZERO");
            return numberExpression;
        }
        return $this$orZeroIf;
    }

    @Override
    @NotNull
    public List<FullGlobalUserStatistics> getEventsForGlobalUsers(@NotNull List<? extends AnalyticsEvent> viewEvents, @NotNull List<? extends AnalyticsEvent> createEvents, @NotNull List<? extends AnalyticsEvent> updateEvents, @NotNull List<? extends AnalyticsEvent> commentEvents, @NotNull List<? extends AnalyticsEvent> contributorEvents, @NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends SpaceType> spaceTypes, @NotNull GlobalUserSortField sortField, @NotNull SortOrder sortOrder, long maxEventId, int offset, int limit) {
        Intrinsics.checkNotNullParameter(viewEvents, (String)"viewEvents");
        Intrinsics.checkNotNullParameter(createEvents, (String)"createEvents");
        Intrinsics.checkNotNullParameter(updateEvents, (String)"updateEvents");
        Intrinsics.checkNotNullParameter(commentEvents, (String)"commentEvents");
        Intrinsics.checkNotNullParameter(contributorEvents, (String)"contributorEvents");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(spaceTypes, (String)"spaceTypes");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Pair<Long, Long> pair = this.convertQueryDatesToEpochMilliseconds(datePeriodOptions);
        long from = ((Number)pair.component1()).longValue();
        long to = ((Number)pair.component2()).longValue();
        Object object = QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<FullGlobalUserStatistics>>(this, viewEvents, createEvents, updateEvents, commentEvents, contributorEvents, from, to, maxEventId, spaceTypes, sortField, sortOrder, offset, limit){
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $createEvents;
            final /* synthetic */ List<AnalyticsEvent> $updateEvents;
            final /* synthetic */ List<AnalyticsEvent> $commentEvents;
            final /* synthetic */ List<AnalyticsEvent> $contributorEvents;
            final /* synthetic */ long $from;
            final /* synthetic */ long $to;
            final /* synthetic */ long $maxEventId;
            final /* synthetic */ Set<SpaceType> $spaceTypes;
            final /* synthetic */ GlobalUserSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ int $offset;
            final /* synthetic */ int $limit;
            {
                this.this$0 = $receiver;
                this.$viewEvents = $viewEvents;
                this.$createEvents = $createEvents;
                this.$updateEvents = $updateEvents;
                this.$commentEvents = $commentEvents;
                this.$contributorEvents = $contributorEvents;
                this.$from = $from;
                this.$to = $to;
                this.$maxEventId = $maxEventId;
                this.$spaceTypes = $spaceTypes;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$offset = $offset;
                this.$limit = $limit;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<FullGlobalUserStatistics> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void $this$isIn$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getUserKey(), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$viewEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$createEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$updateEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$commentEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$contributorEvents)};
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection22 = false;
                expressionArray = new Predicate[4];
                SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)this.$viewEvents, (Iterable)this.$createEvents), (Iterable)this.$updateEvents), (Iterable)this.$commentEvents), (Iterable)this.$contributorEvents)));
                boolean $i$f$isIn = false;
                expressionArray[0] = $this$isIn$iv.in(right$iv);
                expressionArray[1] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from).and((Predicate)Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to));
                expressionArray[2] = Tables.INSTANCE.getEvent().getId().loe((Number)this.$maxEventId);
                expressionArray[3] = EventRepositoryServerImpl.access$spaceTypeAndStatusPredicate(this.this$0, this.$spaceTypes);
                SQLQuery sQLQuery = (SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(FullGlobalUserStatistics.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)Tables.INSTANCE.getEvent())).innerJoin((EntityPath)Tables.INSTANCE.getSpaces())).on((Predicate)Tables.INSTANCE.getEvent().getSpaceKey().eq((Expression)Tables.INSTANCE.getSpaces().getSpaceKey()))).where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getUserKey());
                expressionArray = new OrderSpecifier[]{EventRepositoryServerImpl.access$sortFieldToQueryArg(this.this$0, this.$sortField, this.$sortOrder, this.$viewEvents, this.$createEvents, this.$updateEvents, this.$commentEvents, this.$contributorEvents), Tables.INSTANCE.getEvent().getUserKey().asc()};
                return ((SQLQuery)((SQLQuery)((SQLQuery)sQLQuery.orderBy((OrderSpecifier[])expressionArray)).offset((long)this.$offset)).limit((long)this.$limit)).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)object);
        return (List)object;
    }

    @Override
    @NotNull
    public List<FullContentStatistics> getEventsForAllSpaceContent(@NotNull List<? extends AnalyticsEvent> viewEvents, @NotNull List<? extends AnalyticsEvent> commentEvents, @NotNull DatePeriodOptions datePeriodOptions, @NotNull Set<? extends ContentType> contentTypes, @NotNull String spaceKey, long spaceId, @NotNull ContentSortField sortField, @NotNull SortOrder sortOrder, long maxEventId, int offset, int limit) {
        Intrinsics.checkNotNullParameter(viewEvents, (String)"viewEvents");
        Intrinsics.checkNotNullParameter(commentEvents, (String)"commentEvents");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter(contentTypes, (String)"contentTypes");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Pair<Long, Long> pair = this.convertQueryDatesToEpochMilliseconds(datePeriodOptions);
        long from = ((Number)pair.component1()).longValue();
        long to = ((Number)pair.component2()).longValue();
        Object object = QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<FullContentStatistics>>(this, viewEvents, commentEvents, spaceKey, from, to, maxEventId, spaceId, contentTypes, sortField, sortOrder, offset, limit){
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $commentEvents;
            final /* synthetic */ String $spaceKey;
            final /* synthetic */ long $from;
            final /* synthetic */ long $to;
            final /* synthetic */ long $maxEventId;
            final /* synthetic */ long $spaceId;
            final /* synthetic */ Set<ContentType> $contentTypes;
            final /* synthetic */ ContentSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ int $offset;
            final /* synthetic */ int $limit;
            {
                this.this$0 = $receiver;
                this.$viewEvents = $viewEvents;
                this.$commentEvents = $commentEvents;
                this.$spaceKey = $spaceKey;
                this.$from = $from;
                this.$to = $to;
                this.$maxEventId = $maxEventId;
                this.$spaceId = $spaceId;
                this.$contentTypes = $contentTypes;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$offset = $offset;
                this.$limit = $limit;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<FullContentStatistics> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Collection<String> collection;
                void $this$mapTo$iv$iv;
                void $this$map$iv;
                Object $this$isIn$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                QContentStatistics subquery = new QContentStatistics("stat");
                Expression[] expressionArray = new Expression[8];
                expressionArray[0] = Tables.INSTANCE.getContent().getContentId();
                expressionArray[1] = Tables.INSTANCE.getContent().getContentName();
                expressionArray[2] = Tables.INSTANCE.getContent().getCreationDate();
                expressionArray[3] = Tables.INSTANCE.getContent().getLastModificationDate();
                Intrinsics.checkNotNullExpressionValue((Object)EventRepositoryServerImpl.access$zeroToNull(this.this$0, (NumberExpression)subquery.getMaxEventAt()), (String)"access$zeroToNull(...)");
                expressionArray[5] = subquery.getCommentsCount();
                expressionArray[6] = subquery.getViewedCount();
                expressionArray[7] = subquery.getViewedUsers();
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection22 = false;
                expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getContainerId().as(subquery.getContentId().getMetadata().getName()), Tables.INSTANCE.getEvent().getEventAt().max().as(subquery.getMaxEventAt().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$viewEvents).as(subquery.getViewedCount().getMetadata().getName()), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$commentEvents).as(subquery.getCommentsCount().getMetadata().getName()), EventRepositoryServerImpl.access$uniqUsersOfEventTypes(this.this$0, this.$viewEvents).as(subquery.getViewedUsers().getMetadata().getName())};
                SQLQuery sQLQuery = (SQLQuery)sqlQueryFactory.select(expressionArray).from((Expression)Tables.INSTANCE.getEvent());
                expressionArray = new Predicate[4];
                expressionArray[0] = Tables.INSTANCE.getEvent().getSpaceKey().eq((Object)this.$spaceKey);
                SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)this.$viewEvents, (Iterable)this.$commentEvents)));
                boolean $i$f$isIn22 = false;
                expressionArray[1] = $this$isIn$iv.in(right$iv);
                expressionArray[2] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from).and((Predicate)Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to));
                expressionArray[3] = Tables.INSTANCE.getEvent().getId().loe((Number)this.$maxEventId);
                SQLQuery sQLQuery2 = (SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(FullContentStatistics.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)((SQLQuery)((SQLQuery)sQLQuery.where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getContainerId())).as((Path)subquery))).rightJoin((EntityPath)Tables.INSTANCE.getContent())).on((Predicate)subquery.getContentId().eq((Expression)Tables.INSTANCE.getContent().getContentId()));
                expressionArray = new Predicate[4];
                expressionArray[0] = Tables.INSTANCE.getContent().getSpaceId().eq((Object)this.$spaceId);
                $this$isIn$iv = new String[]{ContentStatus.CURRENT.getValue()};
                expressionArray[1] = Tables.INSTANCE.getContent().getContentStatus().in($this$isIn$iv);
                expressionArray[2] = Tables.INSTANCE.getContent().getOriginalVersion().isNull();
                $this$isIn$iv = this.$contentTypes;
                StringPath stringPath = Tables.INSTANCE.getContent().getContentType();
                int n = 3;
                Expression[] expressionArray2 = expressionArray;
                SQLQuery sQLQuery3 = sQLQuery2;
                boolean $i$f$map = false;
                void $i$f$isIn22 = $this$map$iv;
                Collection destination$iv$iv = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
                boolean $i$f$mapTo = false;
                for (T item$iv$iv : $this$mapTo$iv$iv) {
                    void it;
                    ContentType contentType = (ContentType)((Object)item$iv$iv);
                    collection = destination$iv$iv;
                    boolean bl = false;
                    collection.add(it.getContentTableValue());
                }
                collection = (List)destination$iv$iv;
                expressionArray2[n] = stringPath.in((Collection)collection);
                expressionArray = ((SQLQuery)sQLQuery3.where((Predicate[])expressionArray)).orderBy(EventRepositoryServerImpl.access$sortFieldToQueryArg(this.this$0, this.$sortField, this.$sortOrder, subquery));
                ContentSortField contentSortField = this.$sortField;
                SQLQuery it = (SQLQuery)expressionArray;
                boolean bl = false;
                return ((SQLQuery)((SQLQuery)((SQLQuery)(contentSortField == ContentSortField.CONTENT_NAME ? it : (SQLQuery)it.orderBy(Tables.INSTANCE.getContent().getContentName().asc())).orderBy(Tables.INSTANCE.getContent().getContentId().asc())).offset((long)this.$offset)).limit((long)this.$limit)).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)object);
        return (List)object;
    }

    private final OrderSpecifier<?> sortFieldToQueryArg(ContentSortField sortField, SortOrder sortOrder, QContentStatistics subquery) {
        return new OrderSpecifier(sortOrder.getDslOrder(), (Expression)this.extractContentSortField(sortField, subquery));
    }

    private final ComparableExpressionBase<?> extractContentSortField(ContentSortField sortField, QContentStatistics subquery) {
        ComparableExpressionBase comparableExpressionBase;
        switch (WhenMappings.$EnumSwitchMapping$2[sortField.ordinal()]) {
            case 1: {
                comparableExpressionBase = (ComparableExpressionBase)Tables.INSTANCE.getContent().getContentName();
                break;
            }
            case 2: {
                comparableExpressionBase = (ComparableExpressionBase)Tables.INSTANCE.getContent().getCreationDate();
                break;
            }
            case 3: {
                comparableExpressionBase = (ComparableExpressionBase)Tables.INSTANCE.getContent().getLastModificationDate();
                break;
            }
            case 4: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getMaxEventAt()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 5: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getCommentsCount()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 6: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getViewedUsers()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 7: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getViewedCount()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return comparableExpressionBase;
    }

    @Override
    @NotNull
    public List<FullSpaceUserStatistics> getEventsForSpaceUsers(@NotNull List<? extends AnalyticsEvent> viewEvents, @NotNull List<? extends AnalyticsEvent> createEvents, @NotNull List<? extends AnalyticsEvent> updateEvents, @NotNull List<? extends AnalyticsEvent> commentEvents, @NotNull List<? extends AnalyticsEvent> contributorEvents, @NotNull DatePeriodOptions datePeriodOptions, @NotNull String spaceKey, @NotNull SpaceLevelUserSortField sortField, @NotNull SortOrder sortOrder, long maxEventId, int offset, int limit) {
        Intrinsics.checkNotNullParameter(viewEvents, (String)"viewEvents");
        Intrinsics.checkNotNullParameter(createEvents, (String)"createEvents");
        Intrinsics.checkNotNullParameter(updateEvents, (String)"updateEvents");
        Intrinsics.checkNotNullParameter(commentEvents, (String)"commentEvents");
        Intrinsics.checkNotNullParameter(contributorEvents, (String)"contributorEvents");
        Intrinsics.checkNotNullParameter((Object)datePeriodOptions, (String)"datePeriodOptions");
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter((Object)((Object)sortField), (String)"sortField");
        Intrinsics.checkNotNullParameter((Object)((Object)sortOrder), (String)"sortOrder");
        Pair<Long, Long> pair = this.convertQueryDatesToEpochMilliseconds(datePeriodOptions);
        long from = ((Number)pair.component1()).longValue();
        long to = ((Number)pair.component2()).longValue();
        Object object = QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<FullSpaceUserStatistics>>(this, viewEvents, createEvents, updateEvents, commentEvents, contributorEvents, spaceKey, from, to, maxEventId, sortField, sortOrder, offset, limit){
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $viewEvents;
            final /* synthetic */ List<AnalyticsEvent> $createEvents;
            final /* synthetic */ List<AnalyticsEvent> $updateEvents;
            final /* synthetic */ List<AnalyticsEvent> $commentEvents;
            final /* synthetic */ List<AnalyticsEvent> $contributorEvents;
            final /* synthetic */ String $spaceKey;
            final /* synthetic */ long $from;
            final /* synthetic */ long $to;
            final /* synthetic */ long $maxEventId;
            final /* synthetic */ SpaceLevelUserSortField $sortField;
            final /* synthetic */ SortOrder $sortOrder;
            final /* synthetic */ int $offset;
            final /* synthetic */ int $limit;
            {
                this.this$0 = $receiver;
                this.$viewEvents = $viewEvents;
                this.$createEvents = $createEvents;
                this.$updateEvents = $updateEvents;
                this.$commentEvents = $commentEvents;
                this.$contributorEvents = $contributorEvents;
                this.$spaceKey = $spaceKey;
                this.$from = $from;
                this.$to = $to;
                this.$maxEventId = $maxEventId;
                this.$sortField = $sortField;
                this.$sortOrder = $sortOrder;
                this.$offset = $offset;
                this.$limit = $limit;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<FullSpaceUserStatistics> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void $this$isIn$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getUserKey(), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$viewEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$createEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$updateEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$commentEvents), EventRepositoryServerImpl.access$countOfEventTypes(this.this$0, this.$contributorEvents)};
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection22 = false;
                expressionArray = new Predicate[4];
                expressionArray[0] = Tables.INSTANCE.getEvent().getSpaceKey().eq((Object)this.$spaceKey);
                SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, CollectionsKt.toList((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)CollectionsKt.union((Iterable)this.$viewEvents, (Iterable)this.$createEvents), (Iterable)this.$updateEvents), (Iterable)this.$commentEvents), (Iterable)this.$contributorEvents)));
                boolean $i$f$isIn = false;
                expressionArray[1] = $this$isIn$iv.in(right$iv);
                expressionArray[2] = Tables.INSTANCE.getEvent().getEventAt().goe((Number)this.$from).and((Predicate)Tables.INSTANCE.getEvent().getEventAt().lt((Number)this.$to));
                expressionArray[3] = Tables.INSTANCE.getEvent().getId().loe((Number)this.$maxEventId);
                SQLQuery sQLQuery = (SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(FullSpaceUserStatistics.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)Tables.INSTANCE.getEvent())).where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getUserKey());
                expressionArray = new OrderSpecifier[]{EventRepositoryServerImpl.access$sortFieldToQueryArg(this.this$0, this.$sortField, this.$sortOrder, this.$viewEvents, this.$createEvents, this.$updateEvents, this.$commentEvents, this.$contributorEvents), Tables.INSTANCE.getEvent().getUserKey().asc()};
                return ((SQLQuery)((SQLQuery)((SQLQuery)sQLQuery.orderBy((OrderSpecifier[])expressionArray)).offset((long)this.$offset)).limit((long)this.$limit)).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)object);
        return (List)object;
    }

    private final OrderSpecifier<?> sortFieldToQueryArg(SpaceLevelUserSortField sortField, SortOrder sortOrder, List<? extends AnalyticsEvent> viewEvents, List<? extends AnalyticsEvent> createEvents, List<? extends AnalyticsEvent> updateEvents, List<? extends AnalyticsEvent> commentEvents, List<? extends AnalyticsEvent> contributorEvents) {
        return new OrderSpecifier(sortOrder.getDslOrder(), (Expression)this.extractSpaceLevelUsersSortField(sortField, viewEvents, createEvents, updateEvents, commentEvents, contributorEvents));
    }

    @Override
    @Nullable
    public Long getMaximumEventId() {
        TimedEvent timedEvent = this.getLatestEvent();
        return timedEvent != null ? Long.valueOf(timedEvent.getId()) : null;
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> spaceTypesToQueryArg(Set<? extends SpaceType> spaceTypes) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = spaceTypes;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            SpaceType spaceType = (SpaceType)((Object)item$iv$iv);
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getConfluenceType().toString());
        }
        return (List)destination$iv$iv;
    }

    private final OrderSpecifier<?> sortFieldToQueryArg(SpaceSortField sortField, SortOrder sortOrder, QSpaceStatistics subquery) {
        return new OrderSpecifier(sortOrder.getDslOrder(), (Expression)this.extractSpaceSortField(sortField, subquery));
    }

    private final ComparableExpressionBase<?> extractSpaceSortField(SpaceSortField sortField, QSpaceStatistics subquery) {
        ComparableExpressionBase comparableExpressionBase;
        switch (WhenMappings.$EnumSwitchMapping$3[sortField.ordinal()]) {
            case 1: {
                comparableExpressionBase = (ComparableExpressionBase)Tables.INSTANCE.getSpaces().getSpaceName();
                break;
            }
            case 2: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getViewedCount()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 3: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getCreatedCount()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 4: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getUpdatedCount()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 5: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getViewedUsers()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            case 6: {
                ComparableExpression<Long> comparableExpression = this.nullToZero((NumberExpression<Long>)((NumberExpression)subquery.getMaxEventAt()));
                Intrinsics.checkNotNullExpressionValue(comparableExpression, (String)"nullToZero(...)");
                comparableExpressionBase = (ComparableExpressionBase)comparableExpression;
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return comparableExpressionBase;
    }

    private final OrderSpecifier<?> sortFieldToQueryArg(GlobalUserSortField sortField, SortOrder sortOrder, List<? extends AnalyticsEvent> viewEvents, List<? extends AnalyticsEvent> createEvents, List<? extends AnalyticsEvent> updateEvents, List<? extends AnalyticsEvent> commentEvents, List<? extends AnalyticsEvent> contributorEvents) {
        return new OrderSpecifier(sortOrder.getDslOrder(), (Expression)this.extractGlobalUsersSortField(sortField, viewEvents, createEvents, updateEvents, commentEvents, contributorEvents));
    }

    private final ComparableExpressionBase<?> extractGlobalUsersSortField(GlobalUserSortField sortField, List<? extends AnalyticsEvent> viewEvents, List<? extends AnalyticsEvent> createEvents, List<? extends AnalyticsEvent> updateEvents, List<? extends AnalyticsEvent> commentEvents, List<? extends AnalyticsEvent> contributorEvents) {
        ComparableExpressionBase comparableExpressionBase;
        switch (WhenMappings.$EnumSwitchMapping$4[sortField.ordinal()]) {
            case 1: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(viewEvents);
                break;
            }
            case 2: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(createEvents);
                break;
            }
            case 3: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(updateEvents);
                break;
            }
            case 4: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(commentEvents);
                break;
            }
            case 5: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(contributorEvents);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return comparableExpressionBase;
    }

    private final ComparableExpressionBase<?> extractSpaceLevelUsersSortField(SpaceLevelUserSortField sortField, List<? extends AnalyticsEvent> viewEvents, List<? extends AnalyticsEvent> createEvents, List<? extends AnalyticsEvent> updateEvents, List<? extends AnalyticsEvent> commentEvents, List<? extends AnalyticsEvent> contributorEvents) {
        ComparableExpressionBase comparableExpressionBase;
        switch (WhenMappings.$EnumSwitchMapping$5[sortField.ordinal()]) {
            case 1: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(viewEvents);
                break;
            }
            case 2: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(createEvents);
                break;
            }
            case 3: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(updateEvents);
                break;
            }
            case 4: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(commentEvents);
                break;
            }
            case 5: {
                comparableExpressionBase = (ComparableExpressionBase)this.countOfEventTypes(contributorEvents);
                break;
            }
            default: {
                throw new NoWhenBranchMatchedException();
            }
        }
        return comparableExpressionBase;
    }

    /*
     * WARNING - void declaration
     */
    private final NumberExpression<Long> uniqUsersOfEventTypes(List<? extends AnalyticsEvent> viewEvents) {
        void $this$isIn$iv;
        SimpleExpression simpleExpression = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
        Collection right$iv = this.eventNamesToQueryArg(viewEvents);
        boolean $i$f$isIn = false;
        return ((StringExpression)new CaseBuilder().when((Predicate)$this$isIn$iv.in(right$iv)).then((StringExpression)Tables.INSTANCE.getEvent().getUserKey()).otherwise((Expression)Expressions.nullExpression())).countDistinct();
    }

    private final ComparableExpression<Long> nullToZero(NumberExpression<Long> $this$nullToZero) {
        Expression[] expressionArray = new Expression[]{Expressions.constant((Object)0L)};
        return $this$nullToZero.coalesce(expressionArray).getValue();
    }

    private final SimpleExpression<Long> zeroToNull(NumberExpression<Long> $this$zeroToNull) {
        return $this$zeroToNull.nullif((Object)0L);
    }

    /*
     * WARNING - void declaration
     */
    private final NumberExpression<Long> countOfEventTypes(List<? extends AnalyticsEvent> types) {
        void $this$isIn$iv;
        SimpleExpression simpleExpression = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
        Collection right$iv = this.eventNamesToQueryArg(types);
        boolean $i$f$isIn = false;
        NumberExpression numberExpression = ((NumberExpression)new CaseBuilder().when((Predicate)$this$isIn$iv.in(right$iv)).then((Number)1L).otherwise((Expression)Expressions.nullExpression())).count();
        Intrinsics.checkNotNullExpressionValue((Object)numberExpression, (String)"count(...)");
        return numberExpression;
    }

    private final NumberExpression<Long> countOfPositiveValues(NumberExpression<Long> value) {
        NumberExpression numberExpression = ((NumberExpression)new CaseBuilder().when((Predicate)value.gt((Number)0)).then((Number)1L).otherwise((Expression)Expressions.nullExpression())).count();
        Intrinsics.checkNotNullExpressionValue((Object)numberExpression, (String)"count(...)");
        return numberExpression;
    }

    @Override
    @Nullable
    public Instant getFirstEventDate() {
        Long firstEventAt2;
        Long l = firstEventAt2 = (Long)QueryDslDbConnectionManager.execute$default(this.db, false, getFirstEventDate.firstEventAt.1.INSTANCE, 1, null);
        return l == null ? null : Instant.ofEpochMilli(l);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public List<EventsByChildContentData> getEventsByChildContent(long containerId) {
        void $this$mapTo$iv$iv;
        List events2 = CollectionsKt.listOf((Object)((Object)AnalyticsEvent.ATTACHMENT_VIEWED));
        List viewsByAttachment2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<ChildContentActivityData>>(containerId, this, (List<? extends AnalyticsEvent>)events2){
            final /* synthetic */ long $containerId;
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ List<AnalyticsEvent> $events;
            {
                this.$containerId = $containerId;
                this.this$0 = $receiver;
                this.$events = $events;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            public final List<ChildContentActivityData> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void $this$isIn$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[3];
                expressionArray[0] = Tables.INSTANCE.getEvent().getContentId();
                Intrinsics.checkNotNullExpressionValue((Object)Tables.INSTANCE.getEvent().getEventAt().max(), (String)"max(...)");
                Intrinsics.checkNotNullExpressionValue((Object)Wildcard.count, (String)"count");
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection22 = false;
                expressionArray = new Predicate[2];
                expressionArray[0] = Tables.INSTANCE.getEvent().getContainerId().eq((Object)this.$containerId);
                SimpleExpression $i$f$projection22 = (SimpleExpression)Tables.INSTANCE.getEvent().getName();
                Collection right$iv = EventRepositoryServerImpl.access$eventNamesToQueryArg(this.this$0, this.$events);
                boolean $i$f$isIn = false;
                expressionArray[1] = $this$isIn$iv.in(right$iv);
                return ((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(ChildContentActivityData.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)Tables.INSTANCE.getEvent())).where((Predicate[])expressionArray)).groupBy((Expression)Tables.INSTANCE.getEvent().getContentId())).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)viewsByAttachment2);
        Iterable $this$map$iv = viewsByAttachment2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            ChildContentActivityData childContentActivityData = (ChildContentActivityData)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            long l = it.getContentId();
            Instant instant = Instant.ofEpochMilli(it.getLastViewedAt());
            Intrinsics.checkNotNullExpressionValue((Object)instant, (String)"ofEpochMilli(...)");
            collection.add(new EventsByChildContentData(l, instant, it.getViews()));
        }
        return (List)destination$iv$iv;
    }

    @Override
    public long getEstimatedCount() {
        Long l = this.getMaximumEventId();
        if (l == null) {
            return 0L;
        }
        long maxId = l;
        Long l2 = this.getMinimumEventId();
        return maxId - (l2 != null ? l2 : 0L);
    }

    @Override
    public long insertEvents(@NotNull List<EventData> events2, boolean useSampleStore) {
        Intrinsics.checkNotNullParameter(events2, (String)"events");
        return ((Number)this.db.execute(false, (Function1)new Function1<SQLQueryFactory, Long>(events2, this){
            final /* synthetic */ List<EventData> $events;
            final /* synthetic */ EventRepositoryServerImpl this$0;
            {
                this.$events = $events;
                this.this$0 = $receiver;
                super(1);
            }

            /*
             * WARNING - void declaration
             */
            @NotNull
            public final Long invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                void $this$forEach$iv;
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                SQLInsertClause insert = sqlQueryFactory.insert((RelationalPath)Tables.INSTANCE.getSampleEvent());
                Iterable iterable = this.$events;
                EventRepositoryServerImpl eventRepositoryServerImpl = this.this$0;
                boolean $i$f$forEach = false;
                for (T element$iv : $this$forEach$iv) {
                    EventData it = (EventData)element$iv;
                    boolean bl = false;
                    Instant instant = it.getVersionModificationDate();
                    ((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)((SQLInsertClause)insert.set((Path)Tables.INSTANCE.getSampleEvent().getName(), (Object)EventRepositoryServerImpl.access$eventNamesToQueryArg(eventRepositoryServerImpl, it.getName()))).set((Path)Tables.INSTANCE.getSampleEvent().getEventAt(), (Object)it.getEventAt().toEpochMilli())).set((Path)Tables.INSTANCE.getSampleEvent().getContainerId(), (Object)it.getContainerId())).set((Path)Tables.INSTANCE.getSampleEvent().getSpaceKey(), (Object)it.getSpaceKey())).set((Path)Tables.INSTANCE.getSampleEvent().getUserKey(), (Object)it.getUserKey())).set((Path)Tables.INSTANCE.getSampleEvent().getVersionModificationDate(), (Object)(instant != null ? Long.valueOf(instant.toEpochMilli()) : null))).addBatch();
                }
                return insert.execute();
            }
        })).longValue();
    }

    @Override
    public long clearSampleEvents() {
        return ((Number)this.db.execute(false, clearSampleEvents.1.INSTANCE)).longValue();
    }

    private final Pair<Long, Long> convertQueryDatesToEpochMilliseconds(DatePeriodOptions datePeriodOptions) {
        return new Pair((Object)datePeriodOptions.getQueryFrom().toInstant().toEpochMilli(), (Object)datePeriodOptions.getQueryTo().toInstant().toEpochMilli());
    }

    private final <T extends BucketPeriodActivity> Grouping<T, Instant> groupBucketsByPeriod(List<T> data, DatePeriodOptions datePeriodOptions) {
        Iterable $this$groupingBy$iv = data;
        boolean $i$f$groupingBy = false;
        return new Grouping<T, Instant>($this$groupingBy$iv, this, datePeriodOptions){
            final /* synthetic */ Iterable $this_groupingBy;
            final /* synthetic */ EventRepositoryServerImpl this$0;
            final /* synthetic */ DatePeriodOptions $datePeriodOptions$inlined;
            {
                this.$this_groupingBy = $receiver;
                this.this$0 = eventRepositoryServerImpl;
                this.$datePeriodOptions$inlined = datePeriodOptions;
            }

            @NotNull
            public Iterator<T> sourceIterator() {
                return this.$this_groupingBy.iterator();
            }

            public Instant keyOf(T element) {
                BucketPeriodActivity it = (BucketPeriodActivity)element;
                boolean bl = false;
                ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long)it.getBucket() * (long)EventRepositoryServerImpl.access$getHour$p(this.this$0)), this.$datePeriodOptions$inlined.getTimezone());
                Intrinsics.checkNotNull((Object)date);
                return DatePeriodOptionsKt.startOf(date, this.$datePeriodOptions$inlined.getPeriod()).toInstant();
            }
        };
    }

    /*
     * WARNING - void declaration
     */
    private final List<String> eventNamesToQueryArg(List<? extends AnalyticsEvent> events2) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = events2;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            AnalyticsEvent analyticsEvent = (AnalyticsEvent)((Object)item$iv$iv);
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(this.eventNamesToQueryArg((AnalyticsEvent)it));
        }
        return (List)destination$iv$iv;
    }

    private final String eventNamesToQueryArg(AnalyticsEvent event) {
        String string = event.toString();
        Locale locale = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"getDefault(...)");
        String string2 = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"this as java.lang.String).toLowerCase(locale)");
        return string2;
    }

    private final Long getMinimumEventId() {
        TimedEvent timedEvent = this.getEarliestEvent();
        return timedEvent != null ? Long.valueOf(timedEvent.getId()) : null;
    }

    @Override
    @Nullable
    public TimedEvent getEarliestEvent() {
        return (TimedEvent)QueryDslDbConnectionManager.execute$default(this.db, false, getEarliestEvent.1.INSTANCE, 1, null);
    }

    @Override
    @Nullable
    public TimedEvent getLatestEvent() {
        return (TimedEvent)QueryDslDbConnectionManager.execute$default(this.db, false, getLatestEvent.1.INSTANCE, 1, null);
    }

    @Override
    @NotNull
    public Page<List<Event>, EventCursor> getEvents(@NotNull EventQuery query, @NotNull PageRequest<EventCursor> pageRequest) {
        Intrinsics.checkNotNullParameter((Object)query, (String)"query");
        Intrinsics.checkNotNullParameter(pageRequest, (String)"pageRequest");
        BooleanExpression between = Tables.INSTANCE.getEvent().getEventAt().between((Number)query.getFrom().toEpochMilli(), (Number)query.getTo().toEpochMilli());
        List eventList2 = (List)QueryDslDbConnectionManager.execute$default(this.db, false, (Function1)new Function1<SQLQueryFactory, List<Event>>(pageRequest, between){
            final /* synthetic */ PageRequest<EventCursor> $pageRequest;
            final /* synthetic */ BooleanExpression $between;
            {
                this.$pageRequest = $pageRequest;
                this.$between = $between;
                super(1);
            }

            public final List<Event> invoke(@NotNull SQLQueryFactory sqlQueryFactory) {
                Intrinsics.checkNotNullParameter((Object)sqlQueryFactory, (String)"sqlQueryFactory");
                Expression[] expressionArray = new Expression[]{Tables.INSTANCE.getEvent().getId(), Tables.INSTANCE.getEvent().getName(), Tables.INSTANCE.getEvent().getEventAt(), Tables.INSTANCE.getEvent().getContainerId(), Tables.INSTANCE.getEvent().getSpaceKey(), Tables.INSTANCE.getEvent().getUserKey(), Tables.INSTANCE.getEvent().getContentId(), Tables.INSTANCE.getEvent().getVersionModificationDate()};
                Expression[] exprs$iv = expressionArray;
                boolean $i$f$projection = false;
                expressionArray = new OrderSpecifier[]{Tables.INSTANCE.getEvent().getEventAt().asc(), Tables.INSTANCE.getEvent().getId().asc()};
                return ((SQLQuery)((SQLQuery)((SQLQuery)((SQLQuery)sqlQueryFactory.select((Expression)Projections.constructor(Event.class, (Expression[])Arrays.copyOf(exprs$iv, exprs$iv.length))).from((Expression)Tables.INSTANCE.getEvent())).where((Predicate)(this.$pageRequest.getCursor() != null ? this.$between.and((Predicate)Tables.INSTANCE.getEvent().getEventAt().gt((Number)this.$pageRequest.getCursor().getEventAt()).or((Predicate)Tables.INSTANCE.getEvent().getEventAt().eq((Object)this.$pageRequest.getCursor().getEventAt()).and((Predicate)Tables.INSTANCE.getEvent().getId().gt((Number)this.$pageRequest.getCursor().getEventId())))) : this.$between))).orderBy((OrderSpecifier[])expressionArray)).limit((long)this.$pageRequest.getLimit())).fetch();
            }
        }, 1, null);
        Intrinsics.checkNotNull((Object)eventList2);
        return this.toEventsPage(eventList2);
    }

    private final Page<List<Event>, EventCursor> toEventsPage(List<Event> eventList2) {
        EventCursor eventCursor;
        if (eventList2.size() > 0) {
            long l = ((Event)CollectionsKt.last(eventList2)).getEventAt();
            long l2 = ((Event)CollectionsKt.last(eventList2)).getId();
            eventCursor = new EventCursor(l2, l);
        } else {
            eventCursor = null;
        }
        return new Page<List<Event>, Object>(eventList2, eventCursor);
    }

    public static final /* synthetic */ String access$eventNamesToQueryArg(EventRepositoryServerImpl $this, AnalyticsEvent event) {
        return $this.eventNamesToQueryArg(event);
    }

    public static final /* synthetic */ int access$getHour$p(EventRepositoryServerImpl $this) {
        return $this.hour;
    }

    public static final /* synthetic */ List access$eventNamesToQueryArg(EventRepositoryServerImpl $this, List events2) {
        return $this.eventNamesToQueryArg(events2);
    }

    public static final /* synthetic */ SimpleExpression access$zeroToNull(EventRepositoryServerImpl $this, NumberExpression $receiver) {
        return $this.zeroToNull((NumberExpression<Long>)$receiver);
    }

    public static final /* synthetic */ Expression access$orNullIf(EventRepositoryServerImpl $this, Expression $receiver, boolean condition) {
        return $this.orNullIf($receiver, condition);
    }

    public static final /* synthetic */ ComparableExpression access$nullToZero(EventRepositoryServerImpl $this, NumberExpression $receiver) {
        return $this.nullToZero((NumberExpression<Long>)$receiver);
    }

    public static final /* synthetic */ NumberExpression access$orZeroIf(EventRepositoryServerImpl $this, NumberExpression $receiver, boolean condition) {
        return $this.orZeroIf($receiver, condition);
    }

    public static final /* synthetic */ NumberExpression access$countOfPositiveValues(EventRepositoryServerImpl $this, NumberExpression value) {
        return $this.countOfPositiveValues((NumberExpression<Long>)value);
    }

    public static final /* synthetic */ NumberExpression access$countOfEventTypes(EventRepositoryServerImpl $this, List types) {
        return $this.countOfEventTypes(types);
    }

    public static final /* synthetic */ BooleanExpression access$spaceTypeAndStatusPredicate(EventRepositoryServerImpl $this, Set spaceTypes) {
        return $this.spaceTypeAndStatusPredicate(spaceTypes);
    }

    public static final /* synthetic */ OrderSpecifier access$sortFieldToQueryArg(EventRepositoryServerImpl $this, SpaceSortField sortField, SortOrder sortOrder, QSpaceStatistics subquery) {
        return $this.sortFieldToQueryArg(sortField, sortOrder, subquery);
    }

    public static final /* synthetic */ SimpleTemplate access$nullExpression(EventRepositoryServerImpl $this) {
        return $this.nullExpression();
    }

    public static final /* synthetic */ OrderSpecifier access$sortFieldToQueryArg(EventRepositoryServerImpl $this, GlobalUserSortField sortField, SortOrder sortOrder, List viewEvents, List createEvents, List updateEvents, List commentEvents, List contributorEvents) {
        return $this.sortFieldToQueryArg(sortField, sortOrder, (List<? extends AnalyticsEvent>)viewEvents, (List<? extends AnalyticsEvent>)createEvents, (List<? extends AnalyticsEvent>)updateEvents, (List<? extends AnalyticsEvent>)commentEvents, (List<? extends AnalyticsEvent>)contributorEvents);
    }

    public static final /* synthetic */ NumberExpression access$uniqUsersOfEventTypes(EventRepositoryServerImpl $this, List viewEvents) {
        return $this.uniqUsersOfEventTypes(viewEvents);
    }

    public static final /* synthetic */ OrderSpecifier access$sortFieldToQueryArg(EventRepositoryServerImpl $this, ContentSortField sortField, SortOrder sortOrder, QContentStatistics subquery) {
        return $this.sortFieldToQueryArg(sortField, sortOrder, subquery);
    }

    public static final /* synthetic */ OrderSpecifier access$sortFieldToQueryArg(EventRepositoryServerImpl $this, SpaceLevelUserSortField sortField, SortOrder sortOrder, List viewEvents, List createEvents, List updateEvents, List commentEvents, List contributorEvents) {
        return $this.sortFieldToQueryArg(sortField, sortOrder, (List<? extends AnalyticsEvent>)viewEvents, (List<? extends AnalyticsEvent>)createEvents, (List<? extends AnalyticsEvent>)updateEvents, (List<? extends AnalyticsEvent>)commentEvents, (List<? extends AnalyticsEvent>)contributorEvents);
    }

    @Metadata(mv={1, 9, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;
        public static final /* synthetic */ int[] $EnumSwitchMapping$2;
        public static final /* synthetic */ int[] $EnumSwitchMapping$3;
        public static final /* synthetic */ int[] $EnumSwitchMapping$4;
        public static final /* synthetic */ int[] $EnumSwitchMapping$5;

        static {
            int[] nArray = new int[ContentType.values().length];
            try {
                nArray[ContentType.PAGE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentType.BLOG.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[CountType.values().length];
            try {
                nArray[CountType.TOTAL.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[CountType.UNIQUE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
            nArray = new int[ContentSortField.values().length];
            try {
                nArray[ContentSortField.CONTENT_NAME.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.CREATED_DATE.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.MODIFIED_LAST_DATE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.VIEWED_LAST_DATE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.COMMENTS_COUNT.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.VIEWED_USERS.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[ContentSortField.VIEWED_COUNT.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$2 = nArray;
            nArray = new int[SpaceSortField.values().length];
            try {
                nArray[SpaceSortField.SPACE_NAME.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceSortField.VIEWED_COUNT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceSortField.CREATED_COUNT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceSortField.UPDATED_COUNT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceSortField.VIEWED_USERS.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceSortField.VIEWED_LAST_DATE.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$3 = nArray;
            nArray = new int[GlobalUserSortField.values().length];
            try {
                nArray[GlobalUserSortField.VIEWED_COUNT.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GlobalUserSortField.CREATED_COUNT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GlobalUserSortField.UPDATED_COUNT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GlobalUserSortField.COMMENTS_COUNT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[GlobalUserSortField.CONTRIBUTOR_SCORE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$4 = nArray;
            nArray = new int[SpaceLevelUserSortField.values().length];
            try {
                nArray[SpaceLevelUserSortField.VIEWED_COUNT.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceLevelUserSortField.CREATED_COUNT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceLevelUserSortField.UPDATED_COUNT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceLevelUserSortField.COMMENTS_COUNT.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[SpaceLevelUserSortField.CONTRIBUTOR_SCORE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$5 = nArray;
        }
    }
}

