/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cloud.atlassian.logmon.laas.api.PrivacySafeException
 *  com.atlassian.migration.app.dto.AppContainerDetails
 *  com.atlassian.migration.app.dto.ConnectAppCustomField
 *  com.atlassian.migration.app.dto.ConnectWorkflowRule
 *  com.atlassian.migration.app.dto.InitializeUploadResponse
 *  com.atlassian.migration.app.dto.RegisterForgeTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferAnalytics
 *  com.atlassian.migration.app.dto.RegisterTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferRerunRequest
 *  com.atlassian.migration.app.dto.RerunEnablementDto
 *  com.atlassian.migration.app.dto.RerunTransferResponse
 *  com.atlassian.migration.app.dto.ServerAddonCustomField
 *  com.atlassian.migration.app.dto.TransferErrorRequest
 *  com.atlassian.migration.app.forge.ForgeEnvironmentType
 *  com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1
 *  com.atlassian.migration.app.upload.ChunkUploadData
 *  com.atlassian.migration.app.upload.factories.MultipartUploadConsumerFactory
 *  com.atlassian.migration.app.upload.factories.MultipartUploadStrategyFactory
 *  com.atlassian.migration.app.upload.sinks.AnalyticEventHandler
 *  com.atlassian.migration.app.upload.sinks.MultipartUploadFinalizer
 *  com.atlassian.migration.app.upload.strategies.MultipartUploadStrategy
 *  com.atlassian.migration.app.upload.strategies.UploadStrategy
 *  com.atlassian.migration.app.upload.streams.FixedSizeOutputStream
 *  com.atlassian.migration.app.upload.streams.MultipartUploadStream
 *  com.atlassian.migration.app.util.FixedSizeBlockingChannel
 *  com.atlassian.migration.app.util.Sink
 *  com.google.common.cache.CacheBuilder
 *  kotlin.ExceptionsKt
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.migration.app;

import cloud.atlassian.logmon.laas.api.PrivacySafeException;
import com.atlassian.migration.app.AbstractCloudMigrationRegistrarKt;
import com.atlassian.migration.app.AccessScope;
import com.atlassian.migration.app.AppAnalyticsEventService;
import com.atlassian.migration.app.AppCloudMigrationGateway;
import com.atlassian.migration.app.AppCloudMigrationListenerV1;
import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.AppMigrationExecutor;
import com.atlassian.migration.app.AppMigrationServiceClient;
import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.DefaultPaginatedContainers;
import com.atlassian.migration.app.DefaultPaginatedMapping;
import com.atlassian.migration.app.DiscoverableListenerProxy;
import com.atlassian.migration.app.JiraAppCloudMigrationListenerV1;
import com.atlassian.migration.app.MigrationDetailsV1;
import com.atlassian.migration.app.OsgiBundleHelper;
import com.atlassian.migration.app.PaginatedContainers;
import com.atlassian.migration.app.PaginatedMapping;
import com.atlassian.migration.app.ServerAppCustomField;
import com.atlassian.migration.app.dto.AppContainerDetails;
import com.atlassian.migration.app.dto.ConnectAppCustomField;
import com.atlassian.migration.app.dto.ConnectWorkflowRule;
import com.atlassian.migration.app.dto.InitializeUploadResponse;
import com.atlassian.migration.app.dto.RegisterForgeTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferAnalytics;
import com.atlassian.migration.app.dto.RegisterTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferRerunRequest;
import com.atlassian.migration.app.dto.RerunEnablementDto;
import com.atlassian.migration.app.dto.RerunTransferResponse;
import com.atlassian.migration.app.dto.ServerAddonCustomField;
import com.atlassian.migration.app.dto.TransferErrorRequest;
import com.atlassian.migration.app.forge.ForgeEnvironmentType;
import com.atlassian.migration.app.upload.ChunkUploadData;
import com.atlassian.migration.app.upload.factories.MultipartUploadConsumerFactory;
import com.atlassian.migration.app.upload.factories.MultipartUploadStrategyFactory;
import com.atlassian.migration.app.upload.sinks.AnalyticEventHandler;
import com.atlassian.migration.app.upload.sinks.MultipartUploadFinalizer;
import com.atlassian.migration.app.upload.strategies.MultipartUploadStrategy;
import com.atlassian.migration.app.upload.strategies.UploadStrategy;
import com.atlassian.migration.app.upload.streams.FixedSizeOutputStream;
import com.atlassian.migration.app.upload.streams.MultipartUploadStream;
import com.atlassian.migration.app.util.FixedSizeBlockingChannel;
import com.atlassian.migration.app.util.Sink;
import com.google.common.cache.CacheBuilder;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u009a\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001f\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b&\u0018\u0000 \u0080\u00012\u00020\u00012\u00020\u0002:\u0006\u0080\u0001\u0081\u0001\u0082\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\u0010\u00101\u001a\u0002022\u0006\u00103\u001a\u00020\u0013H\u0016J\u001e\u00101\u001a\u0002022\u0006\u00103\u001a\u00020\u00132\f\u00104\u001a\b\u0012\u0004\u0012\u00020\u00130\u0015H\u0002J\u001a\u00101\u001a\u0002022\u0006\u00103\u001a\u00020\u00132\b\u00104\u001a\u0004\u0018\u00010\u0013H\u0016J\u0010\u00105\u001a\u0002062\u0006\u00107\u001a\u00020$H\u0016J\u0018\u00108\u001a\u0002062\u0006\u0010-\u001a\u0002092\u0006\u00103\u001a\u00020\u0013H\u0002J\u001a\u0010:\u001a\u0002062\b\u0010-\u001a\u0004\u0018\u00010/2\u0006\u00103\u001a\u00020\u0013H\u0002J\u0010\u0010:\u001a\u0002062\u0006\u00103\u001a\u00020\u0013H\u0016J\u0016\u0010;\u001a\b\u0012\u0004\u0012\u00020=0<2\u0006\u00107\u001a\u00020>H\u0002J\u001e\u0010;\u001a\b\u0012\u0004\u0012\u00020=0<2\u0006\u00107\u001a\u00020?2\u0006\u0010@\u001a\u00020AH\u0002J\u0014\u0010B\u001a\b\u0012\u0004\u0012\u00020C0*2\u0006\u0010D\u001a\u00020\u0013J&\u0010E\u001a\u0014\u0012\u0004\u0012\u00020\u0013\u0012\n\u0012\b\u0012\u0004\u0012\u00020C0*0\u00162\f\u0010F\u001a\b\u0012\u0004\u0012\u00020\u00130*J\u0010\u0010G\u001a\u00020H2\u0006\u00107\u001a\u00020$H\u0002J*\u0010I\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u00020K0.2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020$0<2\u0006\u0010M\u001a\u00020AH\u0002J \u0010N\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u00020K0\u00162\f\u0010O\u001a\b\u0012\u0004\u0012\u00020A0*J,\u0010P\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u00020K0\u00162\u0006\u00107\u001a\u00020?2\u0006\u0010@\u001a\u00020A2\u0006\u0010Q\u001a\u00020RH\u0002J\u001c\u0010S\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u00020K0\u00162\u0006\u00107\u001a\u00020>H\u0002J.\u0010S\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u00020K0\u00162\u0006\u00107\u001a\u00020?2\u0006\u0010@\u001a\u00020A2\b\b\u0002\u0010T\u001a\u00020\u0013H\u0002J\u001c\u0010U\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u00162\u0006\u00103\u001a\u00020\u0013H\u0016J\"\u0010V\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u00160\u00152\u0006\u00103\u001a\u00020\u0013H\u0016J\u0010\u0010W\u001a\u00020\u00132\u0006\u00103\u001a\u00020\u0013H\u0002J\u001a\u0010X\u001a\b\u0012\u0004\u0012\u00020=0<2\f\u0010O\u001a\b\u0012\u0004\u0012\u00020A0*J\"\u0010Y\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u00160\u00152\u0006\u00103\u001a\u00020\u0013H\u0002J\u0016\u0010Z\u001a\b\u0012\u0004\u0012\u00020$0<2\u0006\u0010M\u001a\u00020AH\u0002J4\u0010[\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00130\u00162\u0006\u00103\u001a\u00020\u00132\u0006\u0010\\\u001a\u00020\u00132\u000e\u0010]\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010*H\u0016J \u0010^\u001a\u00020_2\u0006\u00103\u001a\u00020\u00132\u0006\u0010`\u001a\u00020a2\u0006\u0010b\u001a\u00020cH\u0016J \u0010d\u001a\u00020e2\u0006\u00103\u001a\u00020\u00132\u0006\u0010\\\u001a\u00020\u00132\u0006\u0010b\u001a\u00020cH\u0016J\u000e\u0010f\u001a\b\u0012\u0004\u0012\u00020$0<H\u0002J\u0014\u0010g\u001a\b\u0012\u0004\u0012\u00020\u00130*2\u0006\u0010D\u001a\u00020\u0013J\u001e\u0010h\u001a\u00020i2\u0006\u0010j\u001a\u00020\u00132\u0006\u0010k\u001a\u00020\u00132\u0006\u0010D\u001a\u00020\u0013J\u001e\u0010l\u001a\b\u0012\u0004\u0012\u00020C0*2\u000e\u0010m\u001a\n\u0012\u0004\u0012\u00020C\u0018\u00010*H\u0002J\u0018\u0010n\u001a\u0002062\u0006\u00103\u001a\u00020\u00132\u0006\u0010o\u001a\u00020/H\u0002J\u0010\u0010p\u001a\u0002062\u0006\u00103\u001a\u00020\u0013H$J(\u0010q\u001a\u0002062\u0006\u0010M\u001a\u00020A2\u0006\u0010r\u001a\u00020\u00132\u0006\u0010j\u001a\u00020\u00132\u0006\u0010s\u001a\u00020tH\u0002J\u0010\u0010u\u001a\u0002062\u0006\u00107\u001a\u00020$H\u0016J(\u0010v\u001a\u00020\u00132\u0006\u00107\u001a\u00020$2\u0006\u0010M\u001a\u00020A2\u0006\u0010r\u001a\u00020\u00132\u0006\u0010j\u001a\u00020\u0013H\u0002J&\u0010w\u001a\u0002062\u0006\u0010j\u001a\u00020\u00132\u0006\u0010r\u001a\u00020\u00132\u0006\u0010k\u001a\u00020\u00132\u0006\u0010D\u001a\u00020\u0013J\u0010\u0010x\u001a\u00020y2\u0006\u00103\u001a\u00020\u0013H\u0002J$\u0010z\u001a\u0002062\u0006\u0010{\u001a\u00020\u00132\u0006\u00103\u001a\u00020\u00132\n\u0010|\u001a\u00060}j\u0002`~H\u0002J&\u0010\u007f\u001a\u0002062\u0006\u0010j\u001a\u00020\u00132\u0006\u0010r\u001a\u00020\u00132\f\u0010M\u001a\b\u0012\u0004\u0012\u00020A0*H\u0016R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u009d\u0001\u0010\u0011\u001a\u008a\u0001\u0012\f\u0012\n \u0014*\u0004\u0018\u00010\u00130\u0013\u00120\u0012.\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u0016 \u0014*\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u0016\u0018\u00010\u00150\u0015 \u0014*D\u0012\f\u0012\n \u0014*\u0004\u0018\u00010\u00130\u0013\u00120\u0012.\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u0016 \u0014*\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00170\u0016\u0018\u00010\u00150\u0015\u0018\u00010\u00120\u0012X\u0082\u0004\u00a2\u0006\b\n\u0000\u0012\u0004\b\u0018\u0010\u0019R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\"\u001a\b\u0012\u0004\u0012\u00020$0#X\u0084\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\u0017\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00130*8F\u00a2\u0006\u0006\u001a\u0004\b+\u0010,R\u001a\u0010-\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020/0.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u00100\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00130.X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0083\u0001"}, d2={"Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar;", "Lcom/atlassian/migration/app/AppCloudMigrationGateway;", "Lcom/atlassian/migration/app/AppMigrationExecutor;", "bundleContext", "Lorg/osgi/framework/BundleContext;", "appMigrationDarkFeatures", "Lcom/atlassian/migration/app/AppMigrationDarkFeatures;", "appMigrationServiceClient", "Lcom/atlassian/migration/app/AppMigrationServiceClient;", "appAnalyticsEventService", "Lcom/atlassian/migration/app/AppAnalyticsEventService;", "(Lorg/osgi/framework/BundleContext;Lcom/atlassian/migration/app/AppMigrationDarkFeatures;Lcom/atlassian/migration/app/AppMigrationServiceClient;Lcom/atlassian/migration/app/AppAnalyticsEventService;)V", "analyticEventHandler", "Lcom/atlassian/migration/app/upload/sinks/AnalyticEventHandler;", "appUploadFixedSizeBlockingChannel", "Lcom/atlassian/migration/app/util/FixedSizeBlockingChannel;", "Lcom/atlassian/migration/app/upload/ChunkUploadData;", "feedbackCache", "Ljava/util/concurrent/ConcurrentMap;", "", "kotlin.jvm.PlatformType", "Ljava/util/Optional;", "", "", "getFeedbackCache$annotations", "()V", "multipartUploadConsumerFactory", "Lcom/atlassian/migration/app/upload/factories/MultipartUploadConsumerFactory;", "multipartUploadFinalizer", "Lcom/atlassian/migration/app/upload/sinks/MultipartUploadFinalizer;", "multipartUploadStrategyFactory", "Lcom/atlassian/migration/app/upload/factories/MultipartUploadStrategyFactory;", "osgiBundleHelper", "Lcom/atlassian/migration/app/OsgiBundleHelper;", "registeredListeners", "", "Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "getRegisteredListeners", "()Ljava/util/Collection;", "setRegisteredListeners", "(Ljava/util/Collection;)V", "registeredServerKeys", "", "getRegisteredServerKeys", "()Ljava/util/Set;", "transferContext", "", "Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$TransferContext;", "transferIdToCloudId", "createAppData", "Ljava/io/OutputStream;", "transferId", "label", "deregisterListener", "", "listener", "executeRerun", "Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$RerunTransferContext;", "executeTransfer", "extractWorkFlowRuleMappings", "", "Lcom/atlassian/migration/app/dto/ConnectWorkflowRule;", "Lcom/atlassian/migration/app/JiraAppCloudMigrationListenerV1;", "Lcom/atlassian/migration/app/jira/JiraAppCloudMigrationListenerV1;", "container", "Lcom/atlassian/migration/app/dto/AppContainerDetails;", "getAccessScopesByApp", "Lcom/atlassian/migration/app/AccessScope;", "serverAppKey", "getAccessScopesByServerAppKeys", "serverAppKeys", "getAnalytics", "Lcom/atlassian/migration/app/dto/RegisterTransferAnalytics;", "getAppCustomFieldForDiscoverableProxyListeners", "Lcom/atlassian/migration/app/dto/ServerAddonCustomField;", "Lcom/atlassian/migration/app/dto/ConnectAppCustomField;", "baseListeners", "appContainerDetails", "getAppCustomFieldMappings", "containers", "getAppCustomFieldMappingsForForgeListener", "environmentType", "Lcom/atlassian/migration/app/forge/ForgeEnvironmentType;", "getAppCustomFieldMappingsForListener", "prefixModuleKey", "getCloudFeedback", "getCloudFeedbackIfPresent", "getCloudIdForTransfer", "getConnectWorkFlowRules", "getFeedbackFromAms", "getListenersForContainer", "getMappingById", "namespace", "ids", "getPaginatedContainers", "Lcom/atlassian/migration/app/PaginatedContainers;", "containerType", "Lcom/atlassian/migration/app/ContainerType;", "pageSize", "", "getPaginatedMapping", "Lcom/atlassian/migration/app/PaginatedMapping;", "getRegisteredAndDiscoveredListeners", "getRegisteredCloudKeys", "isRerunEnabled", "Lcom/atlassian/migration/app/dto/RerunEnablementDto;", "cloudId", "containerId", "orEmptySet", "accessScopes", "putContextInQueue", "context", "queueExecution", "registerAndQueue", "migrationId", "migrationDetails", "Lcom/atlassian/migration/app/MigrationDetailsV1;", "registerListener", "registerServerListeners", "rerunMigration", "sendListenerTriggeredEvent", "", "sendTransferException", "cloudIdForTransfer", "e", "Ljava/lang/Exception;", "Lkotlin/Exception;", "startMigration", "Companion", "RerunTransferContext", "TransferContext", "app-migration-assistant"})
public abstract class AbstractCloudMigrationRegistrar
implements AppCloudMigrationGateway,
AppMigrationExecutor {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final AppMigrationDarkFeatures appMigrationDarkFeatures;
    @NotNull
    private final AppMigrationServiceClient appMigrationServiceClient;
    @NotNull
    private final AppAnalyticsEventService appAnalyticsEventService;
    @NotNull
    private final Map<String, String> transferIdToCloudId;
    @NotNull
    private final Map<String, TransferContext> transferContext;
    @NotNull
    private final OsgiBundleHelper osgiBundleHelper;
    @NotNull
    private final MultipartUploadFinalizer multipartUploadFinalizer;
    @NotNull
    private final FixedSizeBlockingChannel<ChunkUploadData> appUploadFixedSizeBlockingChannel;
    @NotNull
    private final AnalyticEventHandler analyticEventHandler;
    @NotNull
    private final MultipartUploadStrategyFactory multipartUploadStrategyFactory;
    @NotNull
    private final MultipartUploadConsumerFactory multipartUploadConsumerFactory;
    private final ConcurrentMap<String, Optional<Map<String, Object>>> feedbackCache;
    @NotNull
    private Collection<BaseAppCloudMigrationListener> registeredListeners;
    @NotNull
    private static final String EMPTY_STRING = "";
    @NotNull
    private static final String DOUBLE_UNDERSCORE_STRING = "__";

    public AbstractCloudMigrationRegistrar(@NotNull BundleContext bundleContext, @NotNull AppMigrationDarkFeatures appMigrationDarkFeatures, @NotNull AppMigrationServiceClient appMigrationServiceClient, @NotNull AppAnalyticsEventService appAnalyticsEventService) {
        Intrinsics.checkNotNullParameter((Object)bundleContext, (String)"bundleContext");
        Intrinsics.checkNotNullParameter((Object)appMigrationDarkFeatures, (String)"appMigrationDarkFeatures");
        Intrinsics.checkNotNullParameter((Object)appMigrationServiceClient, (String)"appMigrationServiceClient");
        Intrinsics.checkNotNullParameter((Object)appAnalyticsEventService, (String)"appAnalyticsEventService");
        this.appMigrationDarkFeatures = appMigrationDarkFeatures;
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.appAnalyticsEventService = appAnalyticsEventService;
        this.transferIdToCloudId = new HashMap();
        this.transferContext = new HashMap();
        this.osgiBundleHelper = new OsgiBundleHelper(bundleContext, this.appMigrationDarkFeatures);
        this.multipartUploadFinalizer = new MultipartUploadFinalizer(this.appMigrationServiceClient);
        this.appUploadFixedSizeBlockingChannel = new FixedSizeBlockingChannel(10);
        this.analyticEventHandler = new AnalyticEventHandler(this.appAnalyticsEventService);
        this.multipartUploadStrategyFactory = new MultipartUploadStrategyFactory(this.appMigrationServiceClient, this.multipartUploadFinalizer, this.appUploadFixedSizeBlockingChannel, this.analyticEventHandler);
        this.multipartUploadConsumerFactory = new MultipartUploadConsumerFactory(this.appMigrationServiceClient, this.multipartUploadFinalizer, this.appUploadFixedSizeBlockingChannel, (Sink)this.analyticEventHandler, this.appMigrationDarkFeatures, 0, null, 96, null);
        this.feedbackCache = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.SECONDS).build().asMap();
        this.registeredListeners = new ArrayList();
    }

    private static /* synthetic */ void getFeedbackCache$annotations() {
    }

    @NotNull
    protected final Collection<BaseAppCloudMigrationListener> getRegisteredListeners() {
        return this.registeredListeners;
    }

    protected final void setRegisteredListeners(@NotNull Collection<BaseAppCloudMigrationListener> collection) {
        Intrinsics.checkNotNullParameter(collection, (String)"<set-?>");
        this.registeredListeners = collection;
    }

    protected abstract void queueExecution(@NotNull String var1);

    /*
     * WARNING - void declaration
     */
    @Override
    public void registerListener(@NotNull BaseAppCloudMigrationListener listener) {
        Intrinsics.checkNotNullParameter((Object)listener, (String)"listener");
        if (listener instanceof AppCloudMigrationListenerV1) {
            void $this$mapTo$iv$iv;
            AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Registering migration listener for server appKey={}, cloud appKey={}", (Object)((AppCloudMigrationListenerV1)listener).getServerAppKey(), (Object)((AppCloudMigrationListenerV1)listener).getCloudAppKey());
            Iterable $this$map$iv = this.getRegisteredAndDiscoveredListeners();
            boolean $i$f$map = false;
            Iterable iterable = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            for (Object item$iv$iv : $this$mapTo$iv$iv) {
                void it;
                BaseAppCloudMigrationListener baseAppCloudMigrationListener = (BaseAppCloudMigrationListener)item$iv$iv;
                Collection collection = destination$iv$iv;
                boolean bl = false;
                collection.add(it.getServerAppKey());
            }
            if (((List)destination$iv$iv).contains(((AppCloudMigrationListenerV1)listener).getServerAppKey())) {
                throw new IllegalArgumentException("There's already a migration listener registered for the server app " + ((AppCloudMigrationListenerV1)listener).getServerAppKey());
            }
        } else {
            throw new IllegalArgumentException("Unsupported listener type.");
        }
        this.registeredListeners.add(listener);
    }

    @Override
    public void deregisterListener(@NotNull BaseAppCloudMigrationListener listener) {
        Intrinsics.checkNotNullParameter((Object)listener, (String)"listener");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("De-registering migration listener for server appKey={}, cloud appKey={}", (Object)listener.getServerAppKey(), (Object)listener.getCloudAppKey());
        this.registeredListeners.remove(listener);
    }

    public void startMigration(@NotNull String cloudId, @NotNull String migrationId, @NotNull Set<AppContainerDetails> appContainerDetails) {
        Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
        Intrinsics.checkNotNullParameter((Object)migrationId, (String)"migrationId");
        Intrinsics.checkNotNullParameter(appContainerDetails, (String)"appContainerDetails");
        Object[] objectArray = new Object[]{cloudId, migrationId, appContainerDetails.size()};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Start queuing server listeners for app-migration for cloudId {}, migrationId {}, appContainerDetails.size {}", objectArray);
        MigrationDetailsV1 migrationDetails = this.appMigrationServiceClient.getMigrationDetailsV1(cloudId, migrationId);
        Iterable $this$forEach$iv = appContainerDetails;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            AppContainerDetails it = (AppContainerDetails)element$iv;
            boolean bl = false;
            this.registerAndQueue(it, migrationId, cloudId, migrationDetails);
        }
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Finished queuing server listeners for app-migration for cloudId {}, migrationId {}", (Object)cloudId, (Object)migrationId);
    }

    @NotNull
    public final RerunEnablementDto isRerunEnabled(@NotNull String cloudId, @NotNull String containerId, @NotNull String serverAppKey) {
        Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
        Intrinsics.checkNotNullParameter((Object)containerId, (String)"containerId");
        Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
        if (this.osgiBundleHelper.getDiscoveredListener(serverAppKey) == null) {
            RerunEnablementDto rerunEnablementDto;
            RerunEnablementDto it = rerunEnablementDto = new RerunEnablementDto(false, CollectionsKt.listOf((Object)"Not available for this app"));
            boolean bl = false;
            AbstractCloudMigrationRegistrarKt.access$getLog$p().info("No DiscoverableListener found for server app key " + serverAppKey);
            return rerunEnablementDto;
        }
        return this.appMigrationServiceClient.isRerunEnabled(cloudId, containerId);
    }

    public final void rerunMigration(@NotNull String cloudId, @NotNull String migrationId, @NotNull String containerId, @NotNull String serverAppKey) {
        Intrinsics.checkNotNullParameter((Object)cloudId, (String)"cloudId");
        Intrinsics.checkNotNullParameter((Object)migrationId, (String)"migrationId");
        Intrinsics.checkNotNullParameter((Object)containerId, (String)"containerId");
        Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Queuing server listener for app-migration rerun for migrationId {} and serverAppKey {}", (Object)migrationId, (Object)serverAppKey);
        MigrationDetailsV1 migrationDetails = this.appMigrationServiceClient.getMigrationDetailsV1(cloudId, migrationId);
        BaseAppCloudMigrationListener baseAppCloudMigrationListener = this.osgiBundleHelper.getDiscoveredListener(serverAppKey);
        if (baseAppCloudMigrationListener == null) {
            throw new IllegalArgumentException("No listener found for server app key " + serverAppKey);
        }
        BaseAppCloudMigrationListener listener = baseAppCloudMigrationListener;
        Set<AccessScope> set = listener.getDataAccessScopes();
        Intrinsics.checkNotNullExpressionValue(set, (String)"listener.dataAccessScopes");
        RerunTransferResponse response = this.appMigrationServiceClient.registerRerunTransfer(cloudId, containerId, new RegisterTransferRerunRequest(set, this.getAnalytics(listener)));
        this.transferIdToCloudId.put(response.getTransferId(), cloudId);
        this.putContextInQueue(response.getTransferId(), new RerunTransferContext(response.getOriginalTransferId(), migrationDetails, listener));
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Finished queuing server listener for app-migration rerun for migrationId {} and serverAppKey {}", (Object)migrationDetails.getMigrationId(), (Object)serverAppKey);
    }

    @Override
    public void executeTransfer(@NotNull String transferId) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Executing transfer for transferId={}", (Object)StringUtils.abbreviate((String)transferId, (int)21));
        TransferContext transferContext = this.transferContext.get(transferId);
        try {
            if (transferContext instanceof RerunTransferContext) {
                this.executeRerun((RerunTransferContext)transferContext, transferId);
            } else {
                this.executeTransfer(transferContext, transferId);
            }
        }
        catch (Exception e) {
            TransferContext transferContext2 = transferContext;
            BaseAppCloudMigrationListener listener = transferContext2 != null ? transferContext2.getListener() : null;
            Object[] objectArray = new Object[4];
            objectArray[0] = StringUtils.abbreviate((String)transferId, (int)21);
            BaseAppCloudMigrationListener baseAppCloudMigrationListener = listener;
            objectArray[1] = baseAppCloudMigrationListener != null ? baseAppCloudMigrationListener.getServerAppKey() : null;
            BaseAppCloudMigrationListener baseAppCloudMigrationListener2 = listener;
            objectArray[2] = baseAppCloudMigrationListener2 != null ? baseAppCloudMigrationListener2.getCloudAppKey() : null;
            objectArray[3] = e;
            AbstractCloudMigrationRegistrarKt.access$getLog$p().error("Failed to execute server listener for app-migration for transferId {}, server app key {}, cloud app key {} with exception: ", objectArray);
            this.sendTransferException(this.getCloudIdForTransfer(transferId), transferId, e);
        }
    }

    private final void executeTransfer(TransferContext transferContext, String transferId) {
        TransferContext transferContext2 = transferContext;
        Intrinsics.checkNotNull((Object)transferContext2);
        BaseAppCloudMigrationListener listener = transferContext2.getListener();
        boolean listenerTriggered = false;
        Object[] objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), listener.getServerAppKey(), listener.getCloudAppKey()};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Notifying server listener for app-migration for transferId {}, server app key {}, cloud app key {}", objectArray);
        if (listener instanceof DiscoverableListenerProxy) {
            listenerTriggered = this.sendListenerTriggeredEvent(transferId);
            ((DiscoverableListenerProxy)listener).onStartAppMigration(this, transferId, transferContext.getMigrationDetailsV1());
        }
        if (listener instanceof AppCloudMigrationListenerV1) {
            AbstractCloudMigrationRegistrarKt.access$getLog$p().warn("The app {} is using a deprecated library and might stop working on future releases: https://developer.atlassian.com/platform/app-migration/release-notes/osgi-tracker-deprecation-notice/", (Object)((AppCloudMigrationListenerV1)listener).getServerAppKey());
            listenerTriggered = this.sendListenerTriggeredEvent(transferId);
            ((AppCloudMigrationListenerV1)listener).onStartAppMigration(transferId, transferContext.getMigrationDetailsV1());
        }
        if (!listenerTriggered) {
            objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), listener.getServerAppKey(), listener.getCloudAppKey()};
            AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Skip notifying unsupported server listener for app-migration for transferId {}, server app key {}, cloud app key {}", objectArray);
        }
    }

    private final void executeRerun(RerunTransferContext transferContext, String transferId) {
        BaseAppCloudMigrationListener listener = transferContext.getListener();
        if (listener instanceof DiscoverableListenerProxy) {
            this.sendListenerTriggeredEvent(transferId);
            ((DiscoverableListenerProxy)listener).onRerunAppMigration(this, transferContext.getOriginalTransferId(), transferId, transferContext.getMigrationDetailsV1());
        } else {
            AbstractCloudMigrationRegistrarKt.access$getLog$p().warn("Migration listener for {} is not compatible with re-runs", (Object)listener.getServerAppKey());
        }
    }

    @Override
    @NotNull
    public Map<String, Object> getCloudFeedback(@NotNull String transferId) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting cloud feedback for transferId={}", (Object)StringUtils.abbreviate((String)transferId, (int)21));
        ConcurrentMap<String, Optional<Map<String, Object>>> concurrentMap = this.feedbackCache;
        Intrinsics.checkNotNullExpressionValue(concurrentMap, (String)"feedbackCache");
        ConcurrentMap<String, Optional<Map<String, Object>>> $this$getOrPut$iv = concurrentMap;
        boolean $i$f$getOrPut = false;
        Object object = $this$getOrPut$iv.get(transferId);
        if (object == null) {
            boolean bl = false;
            Optional<Map<String, Object>> default$iv = this.getFeedbackFromAms(transferId);
            boolean bl2 = false;
            object = $this$getOrPut$iv.putIfAbsent(transferId, default$iv);
            if (object == null) {
                object = default$iv;
            }
        }
        Map map = ((Optional)object).orElseGet(AbstractCloudMigrationRegistrar::getCloudFeedback$lambda$4);
        Intrinsics.checkNotNullExpressionValue((Object)map, (String)"feedbackCache.getOrPut(t\u2026n(\"Feedback not found\") }");
        return map;
    }

    @Override
    @NotNull
    public Optional<Map<String, Object>> getCloudFeedbackIfPresent(@NotNull String transferId) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting cloud feedback if present for transferId={}", (Object)StringUtils.abbreviate((String)transferId, (int)21));
        ConcurrentMap<String, Optional<Map<String, Object>>> concurrentMap = this.feedbackCache;
        Intrinsics.checkNotNullExpressionValue(concurrentMap, (String)"feedbackCache");
        ConcurrentMap<String, Optional<Map<String, Object>>> $this$getOrPut$iv = concurrentMap;
        boolean $i$f$getOrPut = false;
        Object object = $this$getOrPut$iv.get(transferId);
        if (object == null) {
            boolean bl = false;
            Optional<Map<String, Object>> default$iv = this.getFeedbackFromAms(transferId);
            boolean bl2 = false;
            object = $this$getOrPut$iv.putIfAbsent(transferId, default$iv);
            if (object == null) {
                object = default$iv;
            }
        }
        Intrinsics.checkNotNullExpressionValue(object, (String)"feedbackCache.getOrPut(t\u2026backFromAms(transferId) }");
        return (Optional)object;
    }

    @Override
    @NotNull
    public OutputStream createAppData(@NotNull String transferId) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Optional<String> optional = Optional.empty();
        Intrinsics.checkNotNullExpressionValue(optional, (String)"empty()");
        return this.createAppData(transferId, optional);
    }

    @Override
    @NotNull
    public OutputStream createAppData(@NotNull String transferId, @Nullable String label) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Creating app data for transferId={}, label={}", (Object)StringUtils.abbreviate((String)transferId, (int)21), (Object)label);
        Optional<String> optional = Optional.ofNullable(label);
        Intrinsics.checkNotNullExpressionValue(optional, (String)"ofNullable(label)");
        return this.createAppData(transferId, optional);
    }

    @Override
    @NotNull
    public PaginatedMapping getPaginatedMapping(@NotNull String transferId, @NotNull String namespace, int pageSize) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Intrinsics.checkNotNullParameter((Object)namespace, (String)"namespace");
        String cloudId = this.getCloudIdForTransfer(transferId);
        Object[] objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), cloudId, namespace, pageSize};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting paginated mapping for transferId={}, cloudId={}, namespace={}, pageSize={}", objectArray);
        return new DefaultPaginatedMapping(this.appMigrationServiceClient, cloudId, transferId, namespace, pageSize);
    }

    @Override
    @NotNull
    public Map<String, String> getMappingById(@NotNull String transferId, @NotNull String namespace, @Nullable Set<String> ids) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Intrinsics.checkNotNullParameter((Object)namespace, (String)"namespace");
        Collection collection = ids;
        if (collection == null || collection.isEmpty()) {
            return MapsKt.emptyMap();
        }
        if (!(ids.size() <= 100)) {
            boolean bl = false;
            String string = "You can only query up to 100 ids at a time";
            throw new IllegalArgumentException(string.toString());
        }
        String cloudId = this.getCloudIdForTransfer(transferId);
        Object[] objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), cloudId, namespace, ids};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting mapping by id for transferId={}, cloudId={}, namespace={}, ids={}", objectArray);
        return this.appMigrationServiceClient.getMappingById(cloudId, transferId, namespace, ids);
    }

    @Override
    @NotNull
    public PaginatedContainers getPaginatedContainers(@NotNull String transferId, @NotNull ContainerType containerType, int pageSize) {
        Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
        Intrinsics.checkNotNullParameter((Object)((Object)containerType), (String)"containerType");
        String cloudId = this.getCloudIdForTransfer(transferId);
        Object[] objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), cloudId, containerType, pageSize};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting paginated containers for transferId={}, cloudId={}, containerType={}, pageSize={}", objectArray);
        return new DefaultPaginatedContainers(this.appMigrationServiceClient, cloudId, transferId, containerType, pageSize);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Map<String, Set<AccessScope>> getAccessScopesByServerAppKeys(@NotNull Set<String> serverAppKeys) {
        void $this$associateTo$iv$iv;
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter(serverAppKeys, (String)"serverAppKeys");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting access scopes by server app keys for serverAppKeys={}", serverAppKeys);
        Iterable $this$filter$iv = this.getRegisteredAndDiscoveredListeners();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Iterable destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            BaseAppCloudMigrationListener listener = (BaseAppCloudMigrationListener)element$iv$iv;
            boolean bl = false;
            if (!serverAppKeys.contains(listener.getServerAppKey())) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        Iterable $this$associate$iv = (List)destination$iv$iv;
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        destination$iv$iv = $this$associate$iv;
        Map destination$iv$iv2 = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv2;
            BaseAppCloudMigrationListener it = (BaseAppCloudMigrationListener)element$iv$iv;
            boolean bl = false;
            Pair pair = TuplesKt.to((Object)it.getServerAppKey(), it.getDataAccessScopes());
            map.put(pair.getFirst(), pair.getSecond());
        }
        return destination$iv$iv2;
    }

    @NotNull
    public final Set<AccessScope> getAccessScopesByApp(@NotNull String serverAppKey) {
        Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting access scopes by app for serverAppKey={}", (Object)serverAppKey);
        Set set = this.getAccessScopesByServerAppKeys(SetsKt.setOf((Object)serverAppKey)).get(serverAppKey);
        if (set == null) {
            set = SetsKt.emptySet();
        }
        return set;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<String> getRegisteredServerKeys() {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = this.getRegisteredAndDiscoveredListeners();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            BaseAppCloudMigrationListener baseAppCloudMigrationListener = (BaseAppCloudMigrationListener)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getServerAppKey());
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Set<String> getRegisteredCloudKeys(@NotNull String serverAppKey) {
        void $this$mapTo$iv$iv;
        BaseAppCloudMigrationListener it;
        Iterable $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)serverAppKey, (String)"serverAppKey");
        Iterable $this$filter$iv = this.getRegisteredAndDiscoveredListeners();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (BaseAppCloudMigrationListener)element$iv$iv;
            boolean bl = false;
            if (!Intrinsics.areEqual((Object)it.getServerAppKey(), (Object)serverAppKey)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        Iterable $this$map$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            it = (BaseAppCloudMigrationListener)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.getCloudAppKey());
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final Map<ServerAddonCustomField, ConnectAppCustomField> getAppCustomFieldMappings(@NotNull Set<AppContainerDetails> containers) {
        Object element$iv;
        Intrinsics.checkNotNullParameter(containers, (String)"containers");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting app custom field mappings for containers.size={}", (Object)containers.size());
        Map customFieldMappings = new HashMap();
        Object $this$forEach$iv = containers;
        boolean $i$f$forEach = false;
        Iterator<Object> iterator = $this$forEach$iv.iterator();
        while (iterator.hasNext()) {
            void $this$forEach$iv2;
            void $this$filterIsInstanceTo$iv$iv;
            Iterable $this$filterIsInstance$iv;
            element$iv = iterator.next();
            AppContainerDetails appContainerDetails = (AppContainerDetails)element$iv;
            boolean bl = false;
            List<BaseAppCloudMigrationListener> baseListeners = this.getListenersForContainer(appContainerDetails);
            Iterable iterable = baseListeners;
            boolean $i$f$filterIsInstance = false;
            Iterator iterator2 = $this$filterIsInstance$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterIsInstanceTo = false;
            for (Object element$iv$iv : $this$filterIsInstanceTo$iv$iv) {
                if (!(element$iv$iv instanceof JiraAppCloudMigrationListenerV1)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filterIsInstance$iv = (List)destination$iv$iv;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                JiraAppCloudMigrationListenerV1 it = (JiraAppCloudMigrationListenerV1)element$iv2;
                boolean bl2 = false;
                customFieldMappings.putAll(this.getAppCustomFieldMappingsForListener(it));
            }
            Map<ServerAddonCustomField, ConnectAppCustomField> appDiscoverableListenerCFMap = this.getAppCustomFieldForDiscoverableProxyListeners(baseListeners, appContainerDetails);
            customFieldMappings.putAll(appDiscoverableListenerCFMap);
        }
        if (AbstractCloudMigrationRegistrarKt.access$getLog$p().isDebugEnabled()) {
            $this$forEach$iv = customFieldMappings;
            $i$f$forEach = false;
            iterator = $this$forEach$iv.entrySet().iterator();
            while (iterator.hasNext()) {
                Object object = element$iv = (Map.Entry)iterator.next();
                boolean bl = false;
                ServerAddonCustomField key = (ServerAddonCustomField)object.getKey();
                ConnectAppCustomField value = (ConnectAppCustomField)object.getValue();
                AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("customFieldMappings={}->{}", (Object)key, (Object)value);
            }
        }
        return customFieldMappings;
    }

    /*
     * WARNING - void declaration
     */
    private final Map<ServerAddonCustomField, ConnectAppCustomField> getAppCustomFieldForDiscoverableProxyListeners(List<? extends BaseAppCloudMigrationListener> baseListeners, AppContainerDetails appContainerDetails) {
        Iterable $this$forEach$iv;
        Iterable $this$mapNotNullTo$iv$iv;
        Iterable $this$mapNotNull$iv;
        Object it;
        void $this$filterTo$iv$iv;
        Iterable $this$filter$iv;
        void $this$filterIsInstanceTo$iv$iv;
        Iterable $this$filterIsInstance$iv;
        Map dlCustomFieldMappings = new HashMap();
        Iterable iterable = baseListeners;
        boolean $i$f$filterIsInstance = false;
        Iterator iterator = $this$filterIsInstance$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterIsInstanceTo = false;
        for (Object element$iv$iv : $this$filterIsInstanceTo$iv$iv) {
            if (!(element$iv$iv instanceof DiscoverableListenerProxy)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        List listenerProxies = (List)destination$iv$iv;
        $this$filterIsInstance$iv = listenerProxies;
        boolean $i$f$filter = false;
        $this$filterIsInstanceTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (DiscoverableListenerProxy)element$iv$iv;
            boolean bl = false;
            if (!(!((DiscoverableListenerProxy)it).isForge())) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$mapNotNull = false;
        $this$filterTo$iv$iv = $this$mapNotNull$iv;
        destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        it = $this$forEach$iv$iv$iv.iterator();
        while (it.hasNext()) {
            com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 it$iv$iv;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = it.next();
            boolean bl = false;
            DiscoverableListenerProxy it2 = (DiscoverableListenerProxy)element$iv$iv;
            boolean bl2 = false;
            if (it2.getJiraListener() == null) continue;
            boolean bl3 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        $this$mapNotNull$iv = (List)destination$iv$iv;
        boolean $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv) {
            com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 it3 = (com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1)element$iv;
            boolean bl = false;
            dlCustomFieldMappings.putAll(AbstractCloudMigrationRegistrar.getAppCustomFieldMappingsForListener$default(this, it3, appContainerDetails, null, 4, null));
        }
        $this$forEach$iv = listenerProxies;
        $i$f$filter = false;
        $this$mapNotNullTo$iv$iv = $this$filter$iv;
        destination$iv$iv = new ArrayList();
        $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            it = (DiscoverableListenerProxy)element$iv$iv;
            boolean bl = false;
            if (!(((DiscoverableListenerProxy)it).getJiraListener() != null && ((DiscoverableListenerProxy)it).isForge())) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        $i$f$forEach2 = false;
        for (Object element$iv : $this$forEach$iv) {
            DiscoverableListenerProxy it4 = (DiscoverableListenerProxy)element$iv;
            boolean bl = false;
            com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 jiraAppCloudMigrationListenerV1 = it4.getJiraListener();
            Intrinsics.checkNotNull((Object)jiraAppCloudMigrationListenerV1);
            dlCustomFieldMappings.putAll(this.getAppCustomFieldMappingsForForgeListener(jiraAppCloudMigrationListenerV1, appContainerDetails, it4.getForgeEnvironmentType()));
        }
        return dlCustomFieldMappings;
    }

    private final void sendTransferException(String cloudIdForTransfer, String transferId, Exception e) {
        String string = e.getClass().getCanonicalName();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"e.javaClass.canonicalName");
        PrivacySafeException privacySafeException = PrivacySafeException.of((Throwable)e);
        Intrinsics.checkNotNullExpressionValue((Object)privacySafeException, (String)"of(e)");
        TransferErrorRequest transferErrorRequest = new TransferErrorRequest(string, ExceptionsKt.stackTraceToString((Throwable)((Throwable)privacySafeException)));
        try {
            this.appMigrationServiceClient.recordTransferError(cloudIdForTransfer, transferId, transferErrorRequest);
        }
        catch (Exception ex) {
            AbstractCloudMigrationRegistrarKt.access$getLog$p().warn("Unable to send error details to the cloud. Exception: " + ex.getClass().getCanonicalName());
        }
    }

    private final void registerAndQueue(AppContainerDetails appContainerDetails, String migrationId, String cloudId, MigrationDetailsV1 migrationDetails) {
        try {
            Iterable $this$forEach$iv = this.getListenersForContainer(appContainerDetails);
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                BaseAppCloudMigrationListener listener = (BaseAppCloudMigrationListener)element$iv;
                boolean bl = false;
                String transferId = this.registerServerListeners(listener, appContainerDetails, migrationId, cloudId);
                this.transferIdToCloudId.put(transferId, cloudId);
                TransferContext context = new TransferContext(migrationDetails, listener);
                this.putContextInQueue(transferId, context);
            }
        }
        catch (Exception e) {
            AbstractCloudMigrationRegistrarKt.access$getLog$p().warn("Error registerAndQueue for app-migration: ", (Throwable)e);
        }
    }

    private final String registerServerListeners(BaseAppCloudMigrationListener listener, AppContainerDetails appContainerDetails, String migrationId, String cloudId) {
        String string;
        RegisterTransferAnalytics analytics = this.getAnalytics(listener);
        Object[] objectArray = new Object[]{migrationId, appContainerDetails.getServerAppKey(), appContainerDetails.getCloudAppKey()};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Registering server listener for app-migration for migrationId {}, server app key {}, cloud app key {}", objectArray);
        if (listener instanceof DiscoverableListenerProxy && ((DiscoverableListenerProxy)listener).isForge()) {
            string = this.appMigrationServiceClient.registerForgeTransfer(cloudId, appContainerDetails.getContainerId(), new RegisterForgeTransferRequest(migrationId, ((DiscoverableListenerProxy)listener).getForgeAppId(), ((DiscoverableListenerProxy)listener).getForgeEnvironmentType(), this.orEmptySet(((DiscoverableListenerProxy)listener).getDataAccessScopes()), analytics)).toString();
        } else {
            String string2 = appContainerDetails.getContainerId();
            String string3 = listener.getCloudAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"listener.cloudAppKey");
            string = this.appMigrationServiceClient.registerTransfer(cloudId, string2, new RegisterTransferRequest(migrationId, string3, this.orEmptySet(listener.getDataAccessScopes()), analytics, false, 16, null)).toString();
        }
        String string4 = string;
        Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"if (listener is Discover\u2026   ).toString()\n        }");
        String transferId = string4;
        return transferId;
    }

    private final void putContextInQueue(String transferId, TransferContext context) {
        this.transferContext.put(transferId, context);
        Object[] objectArray = new Object[]{StringUtils.abbreviate((String)transferId, (int)21), context.getMigrationDetailsV1().getMigrationId(), context.getListener().getServerAppKey(), context.getListener().getCloudAppKey()};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Queueing migration for transferId {}, migrationId {}, server app key {}, cloud app key {}", objectArray);
        this.queueExecution(transferId);
    }

    private final RegisterTransferAnalytics getAnalytics(BaseAppCloudMigrationListener listener) {
        return new RegisterTransferAnalytics(listener instanceof DiscoverableListenerProxy ? "disco" : (listener.getClass().getName().equals("com.atlassian.migration.app.tracker.BoundListenerImplV1") ? "tracker" : "osgi"));
    }

    private final List<BaseAppCloudMigrationListener> getRegisteredAndDiscoveredListeners() {
        return CollectionsKt.plus(this.registeredListeners, (Iterable)this.osgiBundleHelper.getDiscoveredListeners());
    }

    private final Optional<Map<String, Object>> getFeedbackFromAms(String transferId) {
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Fetching feedback from AMS for transferId={}", (Object)StringUtils.abbreviate((String)transferId, (int)21));
        try {
            String cloudId = this.getCloudIdForTransfer(transferId);
            Optional<Map<String, Object>> optional = Optional.of(this.appMigrationServiceClient.getCloudFeedback(cloudId, transferId).getDetails());
            Intrinsics.checkNotNullExpressionValue(optional, (String)"of(appMigrationServiceCl\u2026dId, transferId).details)");
            return optional;
        }
        catch (Exception e) {
            String string = e.getMessage();
            boolean bl = string != null ? StringsKt.contains$default((CharSequence)string, (CharSequence)"404", (boolean)false, (int)2, null) : false;
            if (bl) {
                Optional<Map<String, Object>> optional = Optional.empty();
                Intrinsics.checkNotNullExpressionValue(optional, (String)"empty()");
                return optional;
            }
            throw e;
        }
    }

    private final boolean sendListenerTriggeredEvent(String transferId) {
        this.appMigrationServiceClient.notifyListenerTriggered(this.getCloudIdForTransfer(transferId), transferId);
        return true;
    }

    /*
     * WARNING - void declaration
     */
    private final Map<ServerAddonCustomField, ConnectAppCustomField> getAppCustomFieldMappingsForListener(JiraAppCloudMigrationListenerV1 listener) {
        void $this$associateTo$iv$iv;
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting app custom field mappings for listener for serverAppKey={}, cloudAppKey={}", (Object)listener.getServerAppKey(), (Object)listener.getCloudAppKey());
        Map<ServerAppCustomField, String> supportedCustomFieldMappings = listener.getSupportedCustomFieldMappings();
        Iterable $this$associate$iv = supportedCustomFieldMappings.entrySet();
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        Iterable iterable = $this$associate$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv;
            Map.Entry it = (Map.Entry)element$iv$iv;
            boolean bl = false;
            String string = listener.getServerAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"listener.serverAppKey");
            String string2 = ((ServerAppCustomField)it.getKey()).getFieldName();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"it.key.fieldName");
            String string3 = ((ServerAppCustomField)it.getKey()).getFieldTypeKey();
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"it.key.fieldTypeKey");
            ServerAddonCustomField serverAddonCustomField = new ServerAddonCustomField(string, string2, string3);
            String string4 = listener.getCloudAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"listener.cloudAppKey");
            Object v = it.getValue();
            Intrinsics.checkNotNullExpressionValue(v, (String)"it.value");
            Pair pair = TuplesKt.to((Object)serverAddonCustomField, (Object)new ConnectAppCustomField(string4, (String)v));
            map.put(pair.getFirst(), pair.getSecond());
        }
        return destination$iv$iv;
    }

    private final Map<ServerAddonCustomField, ConnectAppCustomField> getAppCustomFieldMappingsForForgeListener(com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 listener, AppContainerDetails container, ForgeEnvironmentType environmentType) {
        return this.getAppCustomFieldMappingsForListener(listener, container, environmentType + DOUBLE_UNDERSCORE_STRING);
    }

    /*
     * WARNING - void declaration
     */
    private final Map<ServerAddonCustomField, ConnectAppCustomField> getAppCustomFieldMappingsForListener(com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 listener, AppContainerDetails container, String prefixModuleKey) {
        void $this$associateTo$iv$iv;
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting app custom field mappings for discoverable listener for serverAppKey={}, cloudAppKey={}", (Object)container.getServerAppKey(), (Object)container.getCloudAppKey());
        Map supportedCustomFieldMappings = listener.getSupportedCustomFieldMappings();
        Iterable $this$associate$iv = supportedCustomFieldMappings.entrySet();
        boolean $i$f$associate = false;
        int capacity$iv = RangesKt.coerceAtLeast((int)MapsKt.mapCapacity((int)CollectionsKt.collectionSizeOrDefault((Iterable)$this$associate$iv, (int)10)), (int)16);
        Iterable iterable = $this$associate$iv;
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        boolean $i$f$associateTo = false;
        for (Object element$iv$iv : $this$associateTo$iv$iv) {
            Map map = destination$iv$iv;
            Map.Entry it = (Map.Entry)element$iv$iv;
            boolean bl = false;
            String string = container.getServerAppKey();
            String string2 = ((ServerAppCustomField)it.getKey()).getFieldName();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"it.key.fieldName");
            String string3 = ((ServerAppCustomField)it.getKey()).getFieldTypeKey();
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"it.key.fieldTypeKey");
            Pair pair = TuplesKt.to((Object)new ServerAddonCustomField(string, string2, string3), (Object)new ConnectAppCustomField(container.getCloudAppKey(), prefixModuleKey + (String)it.getValue()));
            map.put(pair.getFirst(), pair.getSecond());
        }
        return destination$iv$iv;
    }

    static /* synthetic */ Map getAppCustomFieldMappingsForListener$default(AbstractCloudMigrationRegistrar abstractCloudMigrationRegistrar, com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 jiraAppCloudMigrationListenerV1, AppContainerDetails appContainerDetails, String string, int n, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getAppCustomFieldMappingsForListener");
        }
        if ((n & 4) != 0) {
            string = EMPTY_STRING;
        }
        return abstractCloudMigrationRegistrar.getAppCustomFieldMappingsForListener(jiraAppCloudMigrationListenerV1, appContainerDetails, string);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public final List<ConnectWorkflowRule> getConnectWorkFlowRules(@NotNull Set<AppContainerDetails> containers) {
        Intrinsics.checkNotNullParameter(containers, (String)"containers");
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting connect workflow rules for container.size={}", (Object)containers.size());
        List connectWorkflowRules = new ArrayList();
        Iterable $this$forEach$iv = containers;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            void $this$mapNotNullTo$iv$iv;
            Iterable $this$mapNotNull$iv;
            Iterable $this$forEach$iv2;
            Object element$iv$iv;
            Object $this$filterIsInstanceTo$iv$iv;
            Iterable $this$filterIsInstance$iv;
            AppContainerDetails appContainerDetails = (AppContainerDetails)element$iv;
            boolean bl = false;
            List<BaseAppCloudMigrationListener> baseListeners = this.getListenersForContainer(appContainerDetails);
            Iterable iterable = baseListeners;
            boolean $i$f$filterIsInstance = false;
            Iterator iterator = $this$filterIsInstance$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterIsInstanceTo = false;
            Iterator iterator2 = $this$filterIsInstanceTo$iv$iv.iterator();
            while (iterator2.hasNext()) {
                element$iv$iv = iterator2.next();
                if (!(element$iv$iv instanceof JiraAppCloudMigrationListenerV1)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filterIsInstance$iv = (List)destination$iv$iv;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                JiraAppCloudMigrationListenerV1 it = (JiraAppCloudMigrationListenerV1)element$iv2;
                boolean bl2 = false;
                connectWorkflowRules.addAll((Collection)this.extractWorkFlowRuleMappings(it));
            }
            $this$forEach$iv2 = baseListeners;
            $i$f$filterIsInstance = false;
            $this$filterIsInstanceTo$iv$iv = $this$filterIsInstance$iv;
            destination$iv$iv = new ArrayList();
            $i$f$filterIsInstanceTo = false;
            Iterator bl2 = $this$filterIsInstanceTo$iv$iv.iterator();
            while (bl2.hasNext()) {
                element$iv$iv = bl2.next();
                if (!(element$iv$iv instanceof DiscoverableListenerProxy)) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filterIsInstance$iv = (List)destination$iv$iv;
            boolean $i$f$mapNotNull = false;
            $this$filterIsInstanceTo$iv$iv = $this$mapNotNull$iv;
            destination$iv$iv = new ArrayList();
            boolean $i$f$mapNotNullTo = false;
            void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
            boolean $i$f$forEach3 = false;
            Iterator iterator3 = $this$forEach$iv$iv$iv.iterator();
            while (iterator3.hasNext()) {
                com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 it$iv$iv;
                Object element$iv$iv$iv;
                Object element$iv$iv2 = element$iv$iv$iv = iterator3.next();
                boolean bl3 = false;
                DiscoverableListenerProxy it = (DiscoverableListenerProxy)element$iv$iv2;
                boolean bl4 = false;
                if (it.getJiraListener() == null) continue;
                boolean bl5 = false;
                destination$iv$iv.add(it$iv$iv);
            }
            $this$mapNotNull$iv = (List)destination$iv$iv;
            $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 it = (com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1)element$iv2;
                boolean bl6 = false;
                connectWorkflowRules.addAll((Collection)this.extractWorkFlowRuleMappings(it, appContainerDetails));
            }
        }
        if (AbstractCloudMigrationRegistrarKt.access$getLog$p().isDebugEnabled()) {
            $this$forEach$iv = connectWorkflowRules;
            $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                ConnectWorkflowRule it = (ConnectWorkflowRule)element$iv;
                boolean bl = false;
                AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("connectWorkflowRules={}", (Object)it);
            }
        }
        return connectWorkflowRules;
    }

    /*
     * WARNING - void declaration
     */
    private final List<ConnectWorkflowRule> extractWorkFlowRuleMappings(JiraAppCloudMigrationListenerV1 listener) {
        void $this$mapTo$iv$iv;
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Extracting work flow rule mappings for serverAppKey={}, cloudAppKey={}", (Object)listener.getServerAppKey(), (Object)listener.getCloudAppKey());
        Map<String, String> map = listener.getSupportedWorkflowRuleMappings();
        if (map == null) {
            return CollectionsKt.emptyList();
        }
        Map<String, String> supportedWorkflowRuleMappings = map;
        Iterable $this$map$iv = supportedWorkflowRuleMappings.entrySet();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            Map.Entry entry = (Map.Entry)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            String string = listener.getServerAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"listener.serverAppKey");
            Intrinsics.checkNotNullExpressionValue((Object)key, (String)"key");
            String string2 = listener.getCloudAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"listener.cloudAppKey");
            Intrinsics.checkNotNullExpressionValue((Object)value, (String)"value");
            collection.add(new ConnectWorkflowRule(string, key, string2, value));
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    private final List<ConnectWorkflowRule> extractWorkFlowRuleMappings(com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1 listener, AppContainerDetails container) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Extracting work flow rule mappings from discoverable listener for serverAppKey={}, cloudAppKey={}", (Object)container.getServerAppKey(), (Object)container.getCloudAppKey());
        Map map = listener.getSupportedWorkflowRuleMappings();
        if (map == null) {
            return CollectionsKt.emptyList();
        }
        Map supportedWorkflowRuleMappings = map;
        Iterable iterable = supportedWorkflowRuleMappings.entrySet();
        boolean $i$f$map = false;
        void var6_6 = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            Map.Entry entry = (Map.Entry)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            String string = container.getServerAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)key, (String)"key");
            String string2 = container.getCloudAppKey();
            Intrinsics.checkNotNullExpressionValue((Object)value, (String)"value");
            collection.add(new ConnectWorkflowRule(string, key, string2, value));
        }
        return (List)destination$iv$iv;
    }

    /*
     * WARNING - void declaration
     */
    private final List<BaseAppCloudMigrationListener> getListenersForContainer(AppContainerDetails appContainerDetails) {
        void $this$filterTo$iv$iv;
        Object[] objectArray = new Object[]{appContainerDetails.getServerAppKey(), appContainerDetails.getCloudAppKey(), appContainerDetails.getContainerId()};
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Getting listeners for serverAppKey={}, cloudAppKey={}, containerId={}", objectArray);
        Iterable $this$filter$iv = this.getRegisteredAndDiscoveredListeners();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            BaseAppCloudMigrationListener it = (BaseAppCloudMigrationListener)element$iv$iv;
            boolean bl = false;
            if (!(Intrinsics.areEqual((Object)it.getServerAppKey(), (Object)appContainerDetails.getServerAppKey()) && Intrinsics.areEqual((Object)it.getCloudAppKey(), (Object)appContainerDetails.getCloudAppKey()))) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private final OutputStream createAppData(String transferId, Optional<String> label) {
        AbstractCloudMigrationRegistrarKt.access$getLog$p().debug("Creating app data for transferId={}, label=\"{}\"", (Object)StringUtils.abbreviate((String)transferId, (int)21), (Object)label.orElse(""));
        String cloudId = this.getCloudIdForTransfer(transferId);
        InitializeUploadResponse initializeUploadResponse = this.appMigrationServiceClient.initializeUpload(cloudId, transferId, label);
        AbstractCloudMigrationRegistrarKt.access$getLog$p().info("Initialized multipart upload for app-migration for transferId {} and label \"{}\"", (Object)StringUtils.abbreviate((String)transferId, (int)21), (Object)label.orElse(""));
        TransferContext transferContext = this.transferContext.get(transferId);
        Intrinsics.checkNotNull((Object)transferContext);
        TransferContext transferContext2 = transferContext;
        String appKey = transferContext2.getListener().getCloudAppKey();
        String migrationId = transferContext2.getMigrationDetailsV1().getMigrationId();
        int chunkSizeBytes = (int)(this.appMigrationDarkFeatures.getAppMigrationUploadChunkSize() * (double)1024 * (double)1024);
        MultipartUploadStrategy multipartUploadStrategy = this.multipartUploadStrategyFactory.create(this.appMigrationDarkFeatures.multiPartUploadParallelModeEnabled() ? UploadStrategy.PARALLEL : UploadStrategy.SERIAL);
        this.multipartUploadConsumerFactory.spawn();
        Intrinsics.checkNotNullExpressionValue((Object)migrationId, (String)"migrationId");
        Intrinsics.checkNotNullExpressionValue((Object)appKey, (String)"appKey");
        return (OutputStream)new FixedSizeOutputStream((OutputStream)new MultipartUploadStream(this.appMigrationServiceClient, initializeUploadResponse, cloudId, migrationId, transferId, appKey, chunkSizeBytes, label, multipartUploadStrategy, this.multipartUploadFinalizer, this.appMigrationDarkFeatures.isFileCachingEnabled()), chunkSizeBytes);
    }

    private final String getCloudIdForTransfer(String transferId) {
        String string = this.transferIdToCloudId.get(transferId);
        if (string == null) {
            throw new IllegalStateException("Failed to retrieve cloudId for transfer " + transferId);
        }
        return string;
    }

    private final Set<AccessScope> orEmptySet(Set<? extends AccessScope> accessScopes) {
        Set set = accessScopes;
        if (set == null) {
            set = SetsKt.emptySet();
        }
        return set;
    }

    private static final Map getCloudFeedback$lambda$4() {
        throw new RuntimeException("Feedback not found");
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0010\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$TransferContext;", "", "migrationDetailsV1", "Lcom/atlassian/migration/app/MigrationDetailsV1;", "listener", "Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "(Lcom/atlassian/migration/app/MigrationDetailsV1;Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;)V", "getListener", "()Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "getMigrationDetailsV1", "()Lcom/atlassian/migration/app/MigrationDetailsV1;", "app-migration-assistant"})
    public static class TransferContext {
        @NotNull
        private final MigrationDetailsV1 migrationDetailsV1;
        @NotNull
        private final BaseAppCloudMigrationListener listener;

        public TransferContext(@NotNull MigrationDetailsV1 migrationDetailsV1, @NotNull BaseAppCloudMigrationListener listener) {
            Intrinsics.checkNotNullParameter((Object)migrationDetailsV1, (String)"migrationDetailsV1");
            Intrinsics.checkNotNullParameter((Object)listener, (String)"listener");
            this.migrationDetailsV1 = migrationDetailsV1;
            this.listener = listener;
        }

        @NotNull
        public final MigrationDetailsV1 getMigrationDetailsV1() {
            return this.migrationDetailsV1;
        }

        @NotNull
        public final BaseAppCloudMigrationListener getListener() {
            return this.listener;
        }
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2={"Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$RerunTransferContext;", "Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$TransferContext;", "originalTransferId", "", "migrationDetailsV1", "Lcom/atlassian/migration/app/MigrationDetailsV1;", "listener", "Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "(Ljava/lang/String;Lcom/atlassian/migration/app/MigrationDetailsV1;Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;)V", "getOriginalTransferId", "()Ljava/lang/String;", "app-migration-assistant"})
    public static final class RerunTransferContext
    extends TransferContext {
        @NotNull
        private final String originalTransferId;

        public RerunTransferContext(@NotNull String originalTransferId, @NotNull MigrationDetailsV1 migrationDetailsV1, @NotNull BaseAppCloudMigrationListener listener) {
            Intrinsics.checkNotNullParameter((Object)originalTransferId, (String)"originalTransferId");
            Intrinsics.checkNotNullParameter((Object)migrationDetailsV1, (String)"migrationDetailsV1");
            Intrinsics.checkNotNullParameter((Object)listener, (String)"listener");
            super(migrationDetailsV1, listener);
            this.originalTransferId = originalTransferId;
        }

        @NotNull
        public final String getOriginalTransferId() {
            return this.originalTransferId;
        }
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006\u00a8\u0006\t"}, d2={"Lcom/atlassian/migration/app/AbstractCloudMigrationRegistrar$Companion;", "", "()V", "DOUBLE_UNDERSCORE_STRING", "", "getDOUBLE_UNDERSCORE_STRING", "()Ljava/lang/String;", "EMPTY_STRING", "getEMPTY_STRING", "app-migration-assistant"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final String getEMPTY_STRING() {
            return EMPTY_STRING;
        }

        @NotNull
        public final String getDOUBLE_UNDERSCORE_STRING() {
            return DOUBLE_UNDERSCORE_STRING;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

