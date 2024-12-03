/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.MessageTaskFactory;
import com.hazelcast.client.impl.protocol.MessageTaskFactoryProvider;
import com.hazelcast.client.impl.protocol.codec.AtomicLongAddAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongAlterAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongAlterCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongApplyCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongDecrementAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndAddCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndAlterCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndIncrementCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongIncrementAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicLongSetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceAlterAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceAlterCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceApplyCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceClearCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceContainsCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceGetAndAlterCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceGetAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceIsNullCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceSetAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.AtomicReferenceSetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongAddAndGetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongAlterCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongApplyCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetAndAddCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicLongGetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefApplyCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefCompareAndSetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefContainsCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefGetCodec;
import com.hazelcast.client.impl.protocol.codec.CPAtomicRefSetCodec;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchAwaitCodec;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchCountDownCodec;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchGetCountCodec;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchGetRoundCodec;
import com.hazelcast.client.impl.protocol.codec.CPCountDownLatchTrySetCountCodec;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockGetLockOwnershipCodec;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockLockCodec;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockTryLockCodec;
import com.hazelcast.client.impl.protocol.codec.CPFencedLockUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.CPGroupCreateCPGroupCodec;
import com.hazelcast.client.impl.protocol.codec.CPGroupDestroyCPObjectCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreAcquireCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreAvailablePermitsCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreChangeCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreDrainCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreGetSemaphoreTypeCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreInitCodec;
import com.hazelcast.client.impl.protocol.codec.CPSemaphoreReleaseCodec;
import com.hazelcast.client.impl.protocol.codec.CPSessionCloseSessionCodec;
import com.hazelcast.client.impl.protocol.codec.CPSessionCreateSessionCodec;
import com.hazelcast.client.impl.protocol.codec.CPSessionGenerateThreadIdCodec;
import com.hazelcast.client.impl.protocol.codec.CPSessionHeartbeatSessionCodec;
import com.hazelcast.client.impl.protocol.codec.CacheAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheAddInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheAddNearCacheInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheAssignAndGetUuidsCodec;
import com.hazelcast.client.impl.protocol.codec.CacheClearCodec;
import com.hazelcast.client.impl.protocol.codec.CacheContainsKeyCodec;
import com.hazelcast.client.impl.protocol.codec.CacheCreateConfigCodec;
import com.hazelcast.client.impl.protocol.codec.CacheDestroyCodec;
import com.hazelcast.client.impl.protocol.codec.CacheEntryProcessorCodec;
import com.hazelcast.client.impl.protocol.codec.CacheEventJournalReadCodec;
import com.hazelcast.client.impl.protocol.codec.CacheEventJournalSubscribeCodec;
import com.hazelcast.client.impl.protocol.codec.CacheFetchNearCacheInvalidationMetadataCodec;
import com.hazelcast.client.impl.protocol.codec.CacheGetAllCodec;
import com.hazelcast.client.impl.protocol.codec.CacheGetAndRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.CacheGetAndReplaceCodec;
import com.hazelcast.client.impl.protocol.codec.CacheGetCodec;
import com.hazelcast.client.impl.protocol.codec.CacheGetConfigCodec;
import com.hazelcast.client.impl.protocol.codec.CacheIterateCodec;
import com.hazelcast.client.impl.protocol.codec.CacheIterateEntriesCodec;
import com.hazelcast.client.impl.protocol.codec.CacheListenerRegistrationCodec;
import com.hazelcast.client.impl.protocol.codec.CacheLoadAllCodec;
import com.hazelcast.client.impl.protocol.codec.CacheManagementConfigCodec;
import com.hazelcast.client.impl.protocol.codec.CachePutAllCodec;
import com.hazelcast.client.impl.protocol.codec.CachePutCodec;
import com.hazelcast.client.impl.protocol.codec.CachePutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveAllCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveAllKeysCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemoveInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.CacheReplaceCodec;
import com.hazelcast.client.impl.protocol.codec.CacheSetExpiryPolicyCodec;
import com.hazelcast.client.impl.protocol.codec.CacheSizeCodec;
import com.hazelcast.client.impl.protocol.codec.CardinalityEstimatorAddCodec;
import com.hazelcast.client.impl.protocol.codec.CardinalityEstimatorEstimateCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAddDistributedObjectListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAddMembershipListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAddPartitionListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAuthenticationCodec;
import com.hazelcast.client.impl.protocol.codec.ClientAuthenticationCustomCodec;
import com.hazelcast.client.impl.protocol.codec.ClientCreateProxiesCodec;
import com.hazelcast.client.impl.protocol.codec.ClientCreateProxyCodec;
import com.hazelcast.client.impl.protocol.codec.ClientDeployClassesCodec;
import com.hazelcast.client.impl.protocol.codec.ClientDestroyProxyCodec;
import com.hazelcast.client.impl.protocol.codec.ClientGetDistributedObjectsCodec;
import com.hazelcast.client.impl.protocol.codec.ClientGetPartitionsCodec;
import com.hazelcast.client.impl.protocol.codec.ClientIsFailoverSupportedCodec;
import com.hazelcast.client.impl.protocol.codec.ClientPingCodec;
import com.hazelcast.client.impl.protocol.codec.ClientRemoveAllListenersCodec;
import com.hazelcast.client.impl.protocol.codec.ClientRemoveDistributedObjectListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ClientStatisticsCodec;
import com.hazelcast.client.impl.protocol.codec.ConditionAwaitCodec;
import com.hazelcast.client.impl.protocol.codec.ConditionBeforeAwaitCodec;
import com.hazelcast.client.impl.protocol.codec.ConditionSignalAllCodec;
import com.hazelcast.client.impl.protocol.codec.ConditionSignalCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryAddListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryDestroyCacheCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryMadePublishableCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryPublisherCreateCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQueryPublisherCreateWithValueCodec;
import com.hazelcast.client.impl.protocol.codec.ContinuousQuerySetReadCursorCodec;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchAwaitCodec;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchCountDownCodec;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchGetCountCodec;
import com.hazelcast.client.impl.protocol.codec.CountDownLatchTrySetCountCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorDisposeResultCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorIsShutdownCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorRetrieveAndDisposeResultCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorRetrieveResultCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorShutdownCodec;
import com.hazelcast.client.impl.protocol.codec.DurableExecutorSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddCacheConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddCardinalityEstimatorConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddDurableExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddEventJournalConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddFlakeIdGeneratorConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddListConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddLockConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMapConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMerkleTreeConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddMultiMapConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddPNCounterConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddQueueConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddReliableTopicConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddReplicatedMapConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddRingbufferConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddScheduledExecutorConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddSemaphoreConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddSetConfigCodec;
import com.hazelcast.client.impl.protocol.codec.DynamicConfigAddTopicConfigCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceCancelOnAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceCancelOnPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceIsShutdownCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceShutdownCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceSubmitToAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ExecutorServiceSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.FlakeIdGeneratorNewIdBatchCodec;
import com.hazelcast.client.impl.protocol.codec.ListAddAllCodec;
import com.hazelcast.client.impl.protocol.codec.ListAddAllWithIndexCodec;
import com.hazelcast.client.impl.protocol.codec.ListAddCodec;
import com.hazelcast.client.impl.protocol.codec.ListAddListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ListAddWithIndexCodec;
import com.hazelcast.client.impl.protocol.codec.ListClearCodec;
import com.hazelcast.client.impl.protocol.codec.ListCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.codec.ListCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.codec.ListContainsAllCodec;
import com.hazelcast.client.impl.protocol.codec.ListContainsCodec;
import com.hazelcast.client.impl.protocol.codec.ListGetAllCodec;
import com.hazelcast.client.impl.protocol.codec.ListGetCodec;
import com.hazelcast.client.impl.protocol.codec.ListIndexOfCodec;
import com.hazelcast.client.impl.protocol.codec.ListIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.ListIteratorCodec;
import com.hazelcast.client.impl.protocol.codec.ListLastIndexOfCodec;
import com.hazelcast.client.impl.protocol.codec.ListListIteratorCodec;
import com.hazelcast.client.impl.protocol.codec.ListRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.ListRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ListRemoveWithIndexCodec;
import com.hazelcast.client.impl.protocol.codec.ListSetCodec;
import com.hazelcast.client.impl.protocol.codec.ListSizeCodec;
import com.hazelcast.client.impl.protocol.codec.ListSubCodec;
import com.hazelcast.client.impl.protocol.codec.LockForceUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.LockGetLockCountCodec;
import com.hazelcast.client.impl.protocol.codec.LockGetRemainingLeaseTimeCodec;
import com.hazelcast.client.impl.protocol.codec.LockIsLockedByCurrentThreadCodec;
import com.hazelcast.client.impl.protocol.codec.LockIsLockedCodec;
import com.hazelcast.client.impl.protocol.codec.LockLockCodec;
import com.hazelcast.client.impl.protocol.codec.LockTryLockCodec;
import com.hazelcast.client.impl.protocol.codec.LockUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerToKeyWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddEntryListenerWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddIndexCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddInterceptorCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddNearCacheEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddNearCacheInvalidationListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapAddPartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapAggregateCodec;
import com.hazelcast.client.impl.protocol.codec.MapAggregateWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapAssignAndGetUuidsCodec;
import com.hazelcast.client.impl.protocol.codec.MapClearCodec;
import com.hazelcast.client.impl.protocol.codec.MapClearNearCacheCodec;
import com.hazelcast.client.impl.protocol.codec.MapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MapContainsValueCodec;
import com.hazelcast.client.impl.protocol.codec.MapDeleteCodec;
import com.hazelcast.client.impl.protocol.codec.MapEntriesWithPagingPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapEntriesWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapEntrySetCodec;
import com.hazelcast.client.impl.protocol.codec.MapEventJournalReadCodec;
import com.hazelcast.client.impl.protocol.codec.MapEventJournalSubscribeCodec;
import com.hazelcast.client.impl.protocol.codec.MapEvictAllCodec;
import com.hazelcast.client.impl.protocol.codec.MapEvictCodec;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnAllKeysCodec;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MapExecuteOnKeysCodec;
import com.hazelcast.client.impl.protocol.codec.MapExecuteWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapFetchEntriesCodec;
import com.hazelcast.client.impl.protocol.codec.MapFetchKeysCodec;
import com.hazelcast.client.impl.protocol.codec.MapFetchNearCacheInvalidationMetadataCodec;
import com.hazelcast.client.impl.protocol.codec.MapFetchWithQueryCodec;
import com.hazelcast.client.impl.protocol.codec.MapFlushCodec;
import com.hazelcast.client.impl.protocol.codec.MapForceUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.MapGetAllCodec;
import com.hazelcast.client.impl.protocol.codec.MapGetCodec;
import com.hazelcast.client.impl.protocol.codec.MapGetEntryViewCodec;
import com.hazelcast.client.impl.protocol.codec.MapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.MapIsLockedCodec;
import com.hazelcast.client.impl.protocol.codec.MapKeySetCodec;
import com.hazelcast.client.impl.protocol.codec.MapKeySetWithPagingPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapKeySetWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapLoadAllCodec;
import com.hazelcast.client.impl.protocol.codec.MapLoadGivenKeysCodec;
import com.hazelcast.client.impl.protocol.codec.MapLockCodec;
import com.hazelcast.client.impl.protocol.codec.MapProjectCodec;
import com.hazelcast.client.impl.protocol.codec.MapProjectWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutAllCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutIfAbsentWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutTransientCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutTransientWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.codec.MapPutWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceCancelCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceForCustomCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceForListCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceForMapCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceForMultiMapCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceForSetCodec;
import com.hazelcast.client.impl.protocol.codec.MapReduceJobProcessInformationCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemoveAllCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemoveIfSameCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemoveInterceptorCodec;
import com.hazelcast.client.impl.protocol.codec.MapRemovePartitionLostListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MapReplaceCodec;
import com.hazelcast.client.impl.protocol.codec.MapReplaceIfSameCodec;
import com.hazelcast.client.impl.protocol.codec.MapSetCodec;
import com.hazelcast.client.impl.protocol.codec.MapSetTtlCodec;
import com.hazelcast.client.impl.protocol.codec.MapSetWithMaxIdleCodec;
import com.hazelcast.client.impl.protocol.codec.MapSizeCodec;
import com.hazelcast.client.impl.protocol.codec.MapSubmitToKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MapTryLockCodec;
import com.hazelcast.client.impl.protocol.codec.MapTryPutCodec;
import com.hazelcast.client.impl.protocol.codec.MapTryRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.MapUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.MapValuesCodec;
import com.hazelcast.client.impl.protocol.codec.MapValuesWithPagingPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MapValuesWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapClearCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsEntryCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapContainsValueCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapDeleteCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapEntrySetCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapForceUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapGetCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapIsLockedCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapKeySetCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapLockCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapPutCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveEntryCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapSizeCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapTryLockCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapUnlockCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapValueCountCodec;
import com.hazelcast.client.impl.protocol.codec.MultiMapValuesCodec;
import com.hazelcast.client.impl.protocol.codec.PNCounterAddCodec;
import com.hazelcast.client.impl.protocol.codec.PNCounterGetCodec;
import com.hazelcast.client.impl.protocol.codec.PNCounterGetConfiguredReplicaCountCodec;
import com.hazelcast.client.impl.protocol.codec.QueueAddAllCodec;
import com.hazelcast.client.impl.protocol.codec.QueueAddListenerCodec;
import com.hazelcast.client.impl.protocol.codec.QueueClearCodec;
import com.hazelcast.client.impl.protocol.codec.QueueCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.codec.QueueCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.codec.QueueContainsAllCodec;
import com.hazelcast.client.impl.protocol.codec.QueueContainsCodec;
import com.hazelcast.client.impl.protocol.codec.QueueDrainToCodec;
import com.hazelcast.client.impl.protocol.codec.QueueDrainToMaxSizeCodec;
import com.hazelcast.client.impl.protocol.codec.QueueIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.QueueIteratorCodec;
import com.hazelcast.client.impl.protocol.codec.QueueOfferCodec;
import com.hazelcast.client.impl.protocol.codec.QueuePeekCodec;
import com.hazelcast.client.impl.protocol.codec.QueuePollCodec;
import com.hazelcast.client.impl.protocol.codec.QueuePutCodec;
import com.hazelcast.client.impl.protocol.codec.QueueRemainingCapacityCodec;
import com.hazelcast.client.impl.protocol.codec.QueueRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.QueueRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.codec.QueueSizeCodec;
import com.hazelcast.client.impl.protocol.codec.QueueTakeCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerToKeyCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerToKeyWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddEntryListenerWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapAddNearCacheEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapClearCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapContainsValueCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapEntrySetCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapGetCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapKeySetCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapPutAllCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapPutCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapRemoveEntryListenerCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapSizeCodec;
import com.hazelcast.client.impl.protocol.codec.ReplicatedMapValuesCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferAddAllCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferAddCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferCapacityCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferHeadSequenceCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferReadManyCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferReadOneCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferRemainingCapacityCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferSizeCodec;
import com.hazelcast.client.impl.protocol.codec.RingbufferTailSequenceCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorCancelFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorCancelFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorDisposeFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorDisposeFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetAllScheduledFuturesCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetDelayFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetDelayFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetResultFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetResultFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetStatsFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorGetStatsFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsCancelledFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsCancelledFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsDoneFromAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorIsDoneFromPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorShutdownCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorSubmitToAddressCodec;
import com.hazelcast.client.impl.protocol.codec.ScheduledExecutorSubmitToPartitionCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreAcquireCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreAvailablePermitsCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreDrainPermitsCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreIncreasePermitsCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreInitCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreReducePermitsCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreReleaseCodec;
import com.hazelcast.client.impl.protocol.codec.SemaphoreTryAcquireCodec;
import com.hazelcast.client.impl.protocol.codec.SetAddAllCodec;
import com.hazelcast.client.impl.protocol.codec.SetAddCodec;
import com.hazelcast.client.impl.protocol.codec.SetAddListenerCodec;
import com.hazelcast.client.impl.protocol.codec.SetClearCodec;
import com.hazelcast.client.impl.protocol.codec.SetCompareAndRemoveAllCodec;
import com.hazelcast.client.impl.protocol.codec.SetCompareAndRetainAllCodec;
import com.hazelcast.client.impl.protocol.codec.SetContainsAllCodec;
import com.hazelcast.client.impl.protocol.codec.SetContainsCodec;
import com.hazelcast.client.impl.protocol.codec.SetGetAllCodec;
import com.hazelcast.client.impl.protocol.codec.SetIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.SetRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.SetRemoveListenerCodec;
import com.hazelcast.client.impl.protocol.codec.SetSizeCodec;
import com.hazelcast.client.impl.protocol.codec.TopicAddMessageListenerCodec;
import com.hazelcast.client.impl.protocol.codec.TopicPublishCodec;
import com.hazelcast.client.impl.protocol.codec.TopicRemoveMessageListenerCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionCommitCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionCreateCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionRollbackCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalListAddCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalListRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalListSizeCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapContainsKeyCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapDeleteCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapGetCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapGetForUpdateCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapIsEmptyCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapKeySetCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapKeySetWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapPutCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapPutIfAbsentCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapRemoveIfSameCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapReplaceCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapReplaceIfSameCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapSetCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapSizeCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapValuesCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMapValuesWithPredicateCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapGetCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapPutCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapRemoveEntryCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapSizeCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalMultiMapValueCountCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueOfferCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueuePeekCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueuePollCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueSizeCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalQueueTakeCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetAddCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetRemoveCodec;
import com.hazelcast.client.impl.protocol.codec.TransactionalSetSizeCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionClearRemoteCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionCollectTransactionsCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionCommitCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionCreateCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionFinalizeCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionPrepareCodec;
import com.hazelcast.client.impl.protocol.codec.XATransactionRollbackCodec;
import com.hazelcast.client.impl.protocol.task.AddDistributedObjectListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.AddMembershipListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.AddPartitionListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.AddPartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.AuthenticationCustomCredentialsMessageTask;
import com.hazelcast.client.impl.protocol.task.AuthenticationMessageTask;
import com.hazelcast.client.impl.protocol.task.ClientStatisticsMessageTask;
import com.hazelcast.client.impl.protocol.task.CreateProxiesMessageTask;
import com.hazelcast.client.impl.protocol.task.CreateProxyMessageTask;
import com.hazelcast.client.impl.protocol.task.DeployClassesMessageTask;
import com.hazelcast.client.impl.protocol.task.DestroyProxyMessageTask;
import com.hazelcast.client.impl.protocol.task.GetDistributedObjectsMessageTask;
import com.hazelcast.client.impl.protocol.task.GetPartitionsMessageTask;
import com.hazelcast.client.impl.protocol.task.IsFailoverSupportedMessageTask;
import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.client.impl.protocol.task.PingMessageTask;
import com.hazelcast.client.impl.protocol.task.RemoveAllListenersMessageTask;
import com.hazelcast.client.impl.protocol.task.RemoveDistributedObjectListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.RemovePartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongAddAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongAlterAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongAlterMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongApplyMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongCompareAndSetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongDecrementAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongGetAndAddMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongGetAndAlterMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongGetAndIncrementMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongGetAndSetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongIncrementAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomiclong.AtomicLongSetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceAlterAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceAlterMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceApplyMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceClearMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceCompareAndSetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceContainsMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceGetAndAlterMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceGetAndSetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceIsNullMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceSetAndGetMessageTask;
import com.hazelcast.client.impl.protocol.task.atomicreference.AtomicReferenceSetMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheAddEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheAddNearCacheInvalidationListenerTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheAddPartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheAssignAndGetUuidsMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheClearMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheContainsKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheCreateConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheDestroyMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheEntryProcessorMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheEventJournalReadTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheEventJournalSubscribeTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheFetchNearCacheInvalidationMetadataTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheGetAllMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheGetAndRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheGetAndReplaceMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheGetConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheGetMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheIterateEntriesMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheIterateMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheListenerRegistrationMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheLoadAllMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheManagementConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CachePutAllMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CachePutIfAbsentMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CachePutMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemoveAllKeysMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemoveAllMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemoveEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemoveInvalidationListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheRemovePartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheReplaceMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheSetExpiryPolicyMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.CacheSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.cache.Pre38CacheAddInvalidationListenerTask;
import com.hazelcast.client.impl.protocol.task.cardinality.CardinalityEstimatorAddMessageTask;
import com.hazelcast.client.impl.protocol.task.cardinality.CardinalityEstimatorEstimateMessageTask;
import com.hazelcast.client.impl.protocol.task.condition.ConditionAwaitMessageTask;
import com.hazelcast.client.impl.protocol.task.condition.ConditionBeforeAwaitMessageTask;
import com.hazelcast.client.impl.protocol.task.condition.ConditionSignalAllMessageTask;
import com.hazelcast.client.impl.protocol.task.condition.ConditionSignalMessageTask;
import com.hazelcast.client.impl.protocol.task.countdownlatch.CountDownLatchAwaitMessageTask;
import com.hazelcast.client.impl.protocol.task.countdownlatch.CountDownLatchCountDownMessageTask;
import com.hazelcast.client.impl.protocol.task.countdownlatch.CountDownLatchGetCountMessageTask;
import com.hazelcast.client.impl.protocol.task.countdownlatch.CountDownLatchTrySetCountMessageTask;
import com.hazelcast.client.impl.protocol.task.crdt.pncounter.PNCounterAddMessageTask;
import com.hazelcast.client.impl.protocol.task.crdt.pncounter.PNCounterGetConfiguredReplicaCountMessageTask;
import com.hazelcast.client.impl.protocol.task.crdt.pncounter.PNCounterGetMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddCacheConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddCardinalityEstimatorConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddDurableExecutorConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddEventJournalConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddExecutorConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddFlakeIdGeneratorConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddListConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddLockConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddMapConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddMerkleTreeConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddMultiMapConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddPNCounterConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddQueueConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddReliableTopicConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddReplicatedMapConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddRingbufferConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddScheduledExecutorConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddSemaphoreConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddSetConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.AddTopicConfigMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceCancelOnAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceCancelOnPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceIsShutdownMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceShutdownMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceSubmitToAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.ExecutorServiceSubmitToPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorDisposeResultMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorIsShutdownMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorRetrieveAndDisposeResultMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorRetrieveResultMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorShutdownMessageTask;
import com.hazelcast.client.impl.protocol.task.executorservice.durable.DurableExecutorSubmitToPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListAddAllMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListAddAllWithIndexMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListAddListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListAddMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListAddWithIndexMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListClearMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListCompareAndRemoveAllMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListCompareAndRetainAllMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListContainsAllMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListContainsMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListGetAllMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListGetMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListIndexOfMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListIteratorMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListLastIndexOfMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListListIteratorMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListRemoveListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListRemoveWithIndexMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListSetMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.list.ListSubMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockForceUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockGetLockCountMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockGetRemainingLeaseTimeMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockIsLockedByCurrentThreadMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockIsLockedMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockLockMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockTryLockMessageTask;
import com.hazelcast.client.impl.protocol.task.lock.LockUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddEntryListenerToKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddEntryListenerToKeyWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddEntryListenerWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddIndexMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddInterceptorMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddNearCacheInvalidationListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAddPartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAggregateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAggregateWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapAssignAndGetUuidsMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapClearMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapClearNearCacheMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapContainsKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapContainsValueMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapDeleteMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapDestroyCacheMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapEntriesWithPagingPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapEntriesWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapEntrySetMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapEventJournalReadTask;
import com.hazelcast.client.impl.protocol.task.map.MapEventJournalSubscribeTask;
import com.hazelcast.client.impl.protocol.task.map.MapEvictAllMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapEvictMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapExecuteOnAllKeysMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapExecuteOnKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapExecuteOnKeysMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapExecuteWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapFetchEntriesMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapFetchKeysMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapFetchNearCacheInvalidationMetadataTask;
import com.hazelcast.client.impl.protocol.task.map.MapFetchWithQueryMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapFlushMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapForceUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapGetAllMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapGetEntryViewMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapGetMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapIsLockedMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapKeySetMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapKeySetWithPagingPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapKeySetWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapLoadAllMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapLoadGivenKeysMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapLockMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapMadePublishableMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapProjectionMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapProjectionWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPublisherCreateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPublisherCreateWithValueMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutAllMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutIfAbsentMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutIfAbsentWithMaxIdleMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutTransientMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutTransientWithMaxIdleMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapPutWithMaxIdleMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemoveAllMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemoveEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemoveIfSameMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemoveInterceptorMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapRemovePartitionLostListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapReplaceIfSameMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapReplaceMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSetMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSetReadCursorMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSetTtlMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSetWithMaxIdleMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapSubmitToKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapTryLockMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapTryPutMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapTryRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapValuesMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapValuesWithPagingPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.MapValuesWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.map.Pre38MapAddNearCacheEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceCancelMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceForCustomMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceForListMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceForMapMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceForMultiMapMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceForSetMessageTask;
import com.hazelcast.client.impl.protocol.task.mapreduce.MapReduceJobProcessInformationMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapAddEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapAddEntryListenerToKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapClearMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapContainsEntryMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapContainsKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapContainsValueMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapDeleteMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapEntrySetMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapForceUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapGetMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapIsLockedMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapKeySetMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapLockMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapPutMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapRemoveEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapRemoveEntryMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapTryLockMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapUnlockMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapValueCountMessageTask;
import com.hazelcast.client.impl.protocol.task.multimap.MultiMapValuesMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueAddAllMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueAddListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueClearMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueCompareAndRemoveAllMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueCompareAndRetainAllMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueContainsAllMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueContainsMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueDrainMaxSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueDrainMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueIteratorMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueOfferMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueuePeekMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueuePollMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueuePutMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueRemainingCapacityMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueRemoveListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.queue.QueueTakeMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapAddEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapAddEntryListenerToKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapAddEntryListenerToKeyWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapAddEntryListenerWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapAddNearCacheListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapClearMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapContainsKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapContainsValueMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapEntrySetMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapGetMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapKeySetMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapPutAllMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapPutMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapRemoveEntryListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.replicatedmap.ReplicatedMapValuesMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferAddAllMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferAddMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferCapacityMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferHeadSequenceMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferReadManyMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferReadOneMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferRemainingCapacityMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.ringbuffer.RingbufferTailSequenceMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorGetAllScheduledMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorShutdownMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorSubmitToAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorSubmitToPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskCancelFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskCancelFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskDisposeFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskDisposeFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetDelayFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetDelayFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetResultFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetResultFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetStatisticsFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskGetStatisticsFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskIsCancelledFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskIsCancelledFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskIsDoneFromAddressMessageTask;
import com.hazelcast.client.impl.protocol.task.scheduledexecutor.ScheduledExecutorTaskIsDoneFromPartitionMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreAcquireMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreAvailablePermitsMessageTasks;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreDrainPermitsMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreIncreasePermitsMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreInitMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreReducePermitsMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreReleaseMessageTask;
import com.hazelcast.client.impl.protocol.task.semaphore.SemaphoreTryAcquireMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetAddAllMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetAddListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetAddMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetClearMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetCompareAndRemoveAllMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetCompareAndRetainAllMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetContainsAllMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetContainsMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetGetAllMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetRemoveListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.set.SetSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.topic.TopicAddMessageListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.topic.TopicPublishMessageTask;
import com.hazelcast.client.impl.protocol.task.topic.TopicRemoveMessageListenerMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.TransactionCommitMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.TransactionCreateMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.TransactionRollbackMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XAClearRemoteTransactionMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XACollectTransactionsMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XAFinalizeTransactionMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XATransactionCommitMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XATransactionCreateMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XATransactionPrepareMessageTask;
import com.hazelcast.client.impl.protocol.task.transaction.XATransactionRollbackMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionallist.TransactionalListAddMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionallist.TransactionalListRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionallist.TransactionalListSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapContainsKeyMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapDeleteMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapGetForUpdateMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapGetMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapIsEmptyMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapKeySetMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapKeySetWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapPutIfAbsentMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapPutMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapRemoveIfSameMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapReplaceIfSameMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapReplaceMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapSetMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapValuesMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmap.TransactionalMapValuesWithPredicateMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapGetMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapPutMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapRemoveEntryMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalmultimap.TransactionalMultiMapValueCountMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalqueue.TransactionalQueueOfferMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalqueue.TransactionalQueuePeekMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalqueue.TransactionalQueuePollMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalqueue.TransactionalQueueSizeMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalqueue.TransactionalQueueTakeMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalset.TransactionalSetAddMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalset.TransactionalSetRemoveMessageTask;
import com.hazelcast.client.impl.protocol.task.transactionalset.TransactionalSetSizeMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.client.AddAndGetMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.client.AlterMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.client.CompareAndSetMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.client.GetAndAddMessageTask;
import com.hazelcast.cp.internal.datastructures.atomiclong.client.GetAndSetMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.client.ApplyMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.client.ContainsMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.client.GetMessageTask;
import com.hazelcast.cp.internal.datastructures.atomicref.client.SetMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.client.AwaitMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.client.CountDownMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.client.GetCountMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.client.GetRoundMessageTask;
import com.hazelcast.cp.internal.datastructures.countdownlatch.client.TrySetCountMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.client.GetLockOwnershipStateMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.client.LockMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.client.TryLockMessageTask;
import com.hazelcast.cp.internal.datastructures.lock.client.UnlockMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.AcquirePermitsMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.AvailablePermitsMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.ChangePermitsMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.DrainPermitsMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.GetSemaphoreTypeMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.InitSemaphoreMessageTask;
import com.hazelcast.cp.internal.datastructures.semaphore.client.ReleasePermitsMessageTask;
import com.hazelcast.cp.internal.datastructures.spi.client.CreateRaftGroupMessageTask;
import com.hazelcast.cp.internal.datastructures.spi.client.DestroyRaftObjectMessageTask;
import com.hazelcast.cp.internal.session.client.CloseSessionMessageTask;
import com.hazelcast.cp.internal.session.client.CreateSessionMessageTask;
import com.hazelcast.cp.internal.session.client.GenerateThreadIdMessageTask;
import com.hazelcast.cp.internal.session.client.HeartbeatSessionMessageTask;
import com.hazelcast.flakeidgen.impl.client.NewIdBatchMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DefaultMessageTaskFactoryProvider
implements MessageTaskFactoryProvider {
    private final MessageTaskFactory[] factories = new MessageTaskFactory[Short.MAX_VALUE];
    private final Node node;

    public DefaultMessageTaskFactoryProvider(NodeEngine nodeEngine) {
        this.node = ((NodeEngineImpl)nodeEngine).getNode();
        this.initFactories();
    }

    public void initFactories() {
        this.factories[SetRemoveListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetRemoveListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetCompareAndRemoveAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetCompareAndRemoveAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetContainsAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetContainsAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetAddAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetAddAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetCompareAndRetainAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetCompareAndRetainAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetGetAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetGetAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetAddListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetAddListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetContainsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetContainsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SetSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferReadOneCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferReadOneMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferAddAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferAddAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferCapacityCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferCapacityMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferTailSequenceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferTailSequenceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferRemainingCapacityCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferRemainingCapacityMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferReadManyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferReadManyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferHeadSequenceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferHeadSequenceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[RingbufferSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RingbufferSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockIsLockedCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockIsLockedMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockForceUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockForceUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockGetRemainingLeaseTimeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockGetRemainingLeaseTimeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockIsLockedByCurrentThreadCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockIsLockedByCurrentThreadMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockTryLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockTryLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[LockGetLockCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockGetLockCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheAssignAndGetUuidsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheAssignAndGetUuidsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheFetchNearCacheInvalidationMetadataCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheFetchNearCacheInvalidationMetadataTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheReplaceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheReplaceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheContainsKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheContainsKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheCreateConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheCreateConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheGetAndReplaceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheGetAndReplaceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheGetAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheGetAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CachePutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CachePutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheAddInvalidationListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new Pre38CacheAddInvalidationListenerTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheAddNearCacheInvalidationListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheAddNearCacheInvalidationListenerTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CachePutAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CachePutAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheSetExpiryPolicyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheSetExpiryPolicyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheLoadAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheLoadAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheListenerRegistrationCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheListenerRegistrationMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheAddEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheAddEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemoveEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemoveEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemoveInvalidationListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemoveInvalidationListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheDestroyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheDestroyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheEntryProcessorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheEntryProcessorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheGetAndRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheGetAndRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheManagementConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheManagementConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CachePutIfAbsentCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CachePutIfAbsentMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemoveAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemoveAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemoveAllKeysCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemoveAllKeysMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheIterateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheIterateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheAddPartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheAddPartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheGetConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheGetConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheRemovePartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheRemovePartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheIterateEntriesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheIterateEntriesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheEventJournalSubscribeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheEventJournalSubscribeTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CacheEventJournalReadCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CacheEventJournalReadTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceJobProcessInformationCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceJobProcessInformationMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceCancelCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceCancelMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceForCustomCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceForCustomMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceForMapCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceForMapMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceForListCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceForListMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceForSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceForSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReduceForMultiMapCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReduceForMultiMapMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapRemoveEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapRemoveEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapAddEntryListenerToKeyWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapAddEntryListenerToKeyWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapPutAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapPutAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapContainsKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapContainsKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapContainsValueCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapContainsValueMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapAddNearCacheEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapAddNearCacheListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapAddEntryListenerWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapAddEntryListenerWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapAddEntryListenerToKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapAddEntryListenerToKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapValuesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapValuesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapEntrySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapEntrySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapAddEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapAddEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ReplicatedMapKeySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReplicatedMapKeySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongApplyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongApplyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongDecrementAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongDecrementAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongGetAndAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongGetAndAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongAlterAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongAlterAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongAddAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongAddAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongCompareAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongCompareAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongAlterCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongAlterMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongIncrementAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongIncrementAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongGetAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongGetAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongGetAndAlterCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongGetAndAlterMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicLongGetAndIncrementCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicLongGetAndIncrementMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreDrainPermitsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreDrainPermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreAvailablePermitsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreAvailablePermitsMessageTasks(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreInitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreInitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreAcquireCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreAcquireMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreReducePermitsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreReducePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreIncreasePermitsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreIncreasePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreTryAcquireCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreTryAcquireMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[SemaphoreReleaseCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SemaphoreReleaseMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalListSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalListSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalListRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalListRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalListAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalListAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapRemoveEntryCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapRemoveEntryMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMultiMapValueCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMultiMapValueCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ConditionSignalCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ConditionSignalMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ConditionBeforeAwaitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ConditionBeforeAwaitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ConditionAwaitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ConditionAwaitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ConditionSignalAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ConditionSignalAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListGetAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListGetAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListListIteratorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListListIteratorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListAddAllWithIndexCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListAddAllWithIndexMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListCompareAndRemoveAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListCompareAndRemoveAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListRemoveListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListRemoveListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListRemoveWithIndexCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListRemoveWithIndexMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListAddListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListAddListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListIteratorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListIteratorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListAddAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListAddAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListAddWithIndexCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListAddWithIndexMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListLastIndexOfCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListLastIndexOfMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListSubCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListSubMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListContainsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListContainsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListIndexOfCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListIndexOfMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListContainsAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListContainsAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ListCompareAndRetainAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ListCompareAndRetainAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CountDownLatchAwaitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CountDownLatchAwaitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CountDownLatchCountDownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CountDownLatchCountDownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CountDownLatchGetCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CountDownLatchGetCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CountDownLatchTrySetCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CountDownLatchTrySetCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalQueueSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalQueueSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalQueueOfferCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalQueueOfferMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalQueuePeekCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalQueuePeekMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalQueuePollCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalQueuePollMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalQueueTakeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalQueueTakeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapRemoveEntryCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapRemoveEntryMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapContainsKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapContainsKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapAddEntryListenerToKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapAddEntryListenerToKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapAddEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapAddEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapTryLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapTryLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapIsLockedCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapIsLockedMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapContainsValueCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapContainsValueMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapKeySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapKeySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapEntrySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapEntrySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapValueCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapValueCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapRemoveEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapRemoveEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapContainsEntryCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapContainsEntryMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapForceUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapForceUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapValuesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapValuesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MultiMapDeleteCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MultiMapDeleteMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceCompareAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceCompareAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceGetAndAlterCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceGetAndAlterMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceGetAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceGetAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceApplyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceApplyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceIsNullCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceIsNullMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceAlterAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceAlterAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceSetAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceSetAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceAlterCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceAlterMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[AtomicReferenceContainsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AtomicReferenceContainsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TopicPublishCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TopicPublishMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TopicAddMessageListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TopicAddMessageListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TopicRemoveMessageListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TopicRemoveMessageListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapValuesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapValuesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapPutIfAbsentCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapPutIfAbsentMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapGetForUpdateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapGetForUpdateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapKeySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapKeySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapKeySetWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapKeySetWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapReplaceIfSameCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapReplaceIfSameMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapContainsKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapContainsKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapRemoveIfSameCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapRemoveIfSameMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapReplaceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapReplaceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapDeleteCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapDeleteMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalMapValuesWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalMapValuesWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceCancelOnPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceCancelOnPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceSubmitToPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceSubmitToPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceCancelOnAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceCancelOnAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceIsShutdownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceIsShutdownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceShutdownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceShutdownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ExecutorServiceSubmitToAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ExecutorServiceSubmitToAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorSubmitToPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorSubmitToPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorIsShutdownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorIsShutdownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorShutdownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorShutdownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorRetrieveResultCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorRetrieveResultMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorDisposeResultCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorDisposeResultMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DurableExecutorRetrieveAndDisposeResultCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DurableExecutorRetrieveAndDisposeResultMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionCreateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionCreateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionClearRemoteCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XAClearRemoteTransactionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionFinalizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XAFinalizeTransactionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionCommitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionCommitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionCollectTransactionsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XACollectTransactionsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionPrepareCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XATransactionPrepareMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionCreateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XATransactionCreateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionRollbackCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionRollbackMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionCommitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XATransactionCommitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[XATransactionRollbackCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new XATransactionRollbackMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalSetSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalSetSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalSetAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalSetAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[TransactionalSetRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TransactionalSetRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEntriesWithPagingPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEntriesWithPagingPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapClearNearCacheCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapClearNearCacheMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAssignAndGetUuidsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAssignAndGetUuidsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapFetchNearCacheInvalidationMetadataCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapFetchNearCacheInvalidationMetadataTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemoveIfSameCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemoveIfSameMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddInterceptorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddInterceptorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEntriesWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEntriesWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutTransientCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutTransientMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapContainsValueCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapContainsValueMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReplaceCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReplaceMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemoveInterceptorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemoveInterceptorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddNearCacheEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new Pre38MapAddNearCacheEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddNearCacheInvalidationListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddNearCacheInvalidationListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapExecuteOnAllKeysCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapExecuteOnAllKeysMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapFlushCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapFlushMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapTryLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapTryLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddEntryListenerToKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddEntryListenerToKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEntrySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEntrySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapGetEntryViewCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapGetEntryViewMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemovePartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemovePartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapLoadGivenKeysCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapLoadGivenKeysMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapExecuteWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapExecuteWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemoveAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemoveAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutIfAbsentCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutIfAbsentMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapTryRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapTryRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapValuesWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapValuesWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddEntryListenerToKeyWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddEntryListenerToKeyWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEvictCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEvictMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapGetAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapGetAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapForceUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapForceUnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapLoadAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapLoadAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddIndexCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddIndexMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapExecuteOnKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapExecuteOnKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapKeySetWithPagingPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapKeySetWithPagingPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemoveEntryListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemoveEntryListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapIsLockedCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapIsLockedMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEvictAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEvictAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapSubmitToKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSubmitToKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapValuesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapValuesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddEntryListenerWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddEntryListenerWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapDeleteCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapDeleteMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAddPartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddPartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapKeySetWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapKeySetWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapExecuteOnKeysCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapExecuteOnKeysMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapReplaceIfSameCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapReplaceIfSameMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapContainsKeyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapContainsKeyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapTryPutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapTryPutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapValuesWithPagingPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapValuesWithPagingPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapKeySetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapKeySetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapFetchKeysCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapFetchKeysMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapFetchEntriesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapFetchEntriesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAggregateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAggregateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapAggregateWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAggregateWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapProjectCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapProjectionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapProjectWithPredicateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapProjectionWithPredicateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapFetchWithQueryCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapFetchWithQueryMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEventJournalSubscribeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEventJournalSubscribeTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapEventJournalReadCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapEventJournalReadTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapSetTtlCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSetTtlMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapSetWithMaxIdleCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSetWithMaxIdleMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutWithMaxIdleCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutWithMaxIdleMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutIfAbsentWithMaxIdleCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutIfAbsentWithMaxIdleMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[MapPutTransientWithMaxIdleCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPutTransientWithMaxIdleMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAddPartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddPartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientRemovePartitionLostListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RemovePartitionLostListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientCreateProxyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CreateProxyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientGetDistributedObjectsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetDistributedObjectsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAddDistributedObjectListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddDistributedObjectListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientDestroyProxyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DestroyProxyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientPingCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new PingMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAddMembershipListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddMembershipListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAuthenticationCustomCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AuthenticationCustomCredentialsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientRemoveAllListenersCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RemoveAllListenersMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientRemoveDistributedObjectListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new RemoveDistributedObjectListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientGetPartitionsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetPartitionsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAuthenticationCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AuthenticationMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientStatisticsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ClientStatisticsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientDeployClassesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DeployClassesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientAddPartitionListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddPartitionListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientCreateProxiesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CreateProxiesMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ClientIsFailoverSupportedCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new IsFailoverSupportedMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueCompareAndRemoveAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueCompareAndRemoveAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueContainsAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueContainsAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueAddAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueAddAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueTakeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueTakeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueAddListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueAddListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueCompareAndRetainAllCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueCompareAndRetainAllMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueOfferCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueOfferMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueuePeekCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueuePeekMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueRemoveCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueRemoveMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueIsEmptyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueIsEmptyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueIteratorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueIteratorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueuePutCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueuePutMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueContainsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueContainsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueuePollCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueuePollMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueDrainToCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueDrainMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueRemoveListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueRemoveListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueRemainingCapacityCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueRemainingCapacityMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueClearCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueClearMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[QueueDrainToMaxSizeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new QueueDrainMaxSizeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CardinalityEstimatorAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CardinalityEstimatorAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CardinalityEstimatorEstimateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CardinalityEstimatorEstimateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorSubmitToPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorSubmitToPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorSubmitToAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorSubmitToAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorShutdownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorShutdownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorDisposeFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskDisposeFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorDisposeFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskDisposeFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorCancelFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskCancelFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorCancelFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskCancelFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorIsDoneFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskIsDoneFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorIsDoneFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskIsDoneFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetDelayFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetDelayFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetDelayFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetDelayFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetStatsFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetStatisticsFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetStatsFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetStatisticsFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetResultFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetResultFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetResultFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskGetResultFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorGetAllScheduledFuturesCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorGetAllScheduledMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorIsCancelledFromPartitionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskIsCancelledFromPartitionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ScheduledExecutorIsCancelledFromAddressCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ScheduledExecutorTaskIsCancelledFromAddressMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQueryDestroyCacheCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapDestroyCacheMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQueryPublisherCreateCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPublisherCreateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQuerySetReadCursorCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapSetReadCursorMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQueryAddListenerCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapAddListenerMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQueryMadePublishableCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapMadePublishableMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[ContinuousQueryPublisherCreateWithValueCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new MapPublisherCreateWithValueMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddMultiMapConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddMultiMapConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddCardinalityEstimatorConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddCardinalityEstimatorConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddExecutorConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddExecutorConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddDurableExecutorConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddDurableExecutorConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddScheduledExecutorConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddScheduledExecutorConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddRingbufferConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddRingbufferConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddLockConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddLockConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddListConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddListConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddSetConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddSetConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddSemaphoreConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddSemaphoreConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddTopicConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddTopicConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddReplicatedMapConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddReplicatedMapConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddQueueConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddQueueConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddMapConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddMapConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddReliableTopicConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddReliableTopicConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddCacheConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddCacheConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddEventJournalConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddEventJournalConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddFlakeIdGeneratorConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddFlakeIdGeneratorConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddPNCounterConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddPNCounterConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[FlakeIdGeneratorNewIdBatchCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new NewIdBatchMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[PNCounterGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new PNCounterGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[PNCounterAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new PNCounterAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[PNCounterGetConfiguredReplicaCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new PNCounterGetConfiguredReplicaCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[DynamicConfigAddMerkleTreeConfigCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddMerkleTreeConfigMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPGroupCreateCPGroupCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CreateRaftGroupMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPGroupDestroyCPObjectCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DestroyRaftObjectMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSessionCreateSessionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CreateSessionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSessionHeartbeatSessionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new HeartbeatSessionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSessionCloseSessionCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CloseSessionMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSessionGenerateThreadIdCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GenerateThreadIdMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongAddAndGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AddAndGetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongCompareAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CompareAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongGetAndAddCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetAndAddMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new com.hazelcast.cp.internal.datastructures.atomiclong.client.GetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongGetAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongApplyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new com.hazelcast.cp.internal.datastructures.atomiclong.client.ApplyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicLongAlterCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AlterMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicRefApplyCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ApplyMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicRefSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new SetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicRefContainsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ContainsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicRefGetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPAtomicRefCompareAndSetCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new com.hazelcast.cp.internal.datastructures.atomicref.client.CompareAndSetMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPCountDownLatchAwaitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AwaitMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPCountDownLatchCountDownCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new CountDownMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPCountDownLatchGetCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPCountDownLatchGetRoundCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetRoundMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPCountDownLatchTrySetCountCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TrySetCountMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPFencedLockLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new LockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPFencedLockTryLockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new TryLockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPFencedLockUnlockCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new UnlockMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPFencedLockGetLockOwnershipCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetLockOwnershipStateMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreAcquireCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AcquirePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreAvailablePermitsCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new AvailablePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreChangeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ChangePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreDrainCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new DrainPermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreGetSemaphoreTypeCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new GetSemaphoreTypeMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreInitCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new InitSemaphoreMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
        this.factories[CPSemaphoreReleaseCodec.RequestParameters.TYPE.id()] = new MessageTaskFactory(){

            @Override
            public MessageTask create(ClientMessage clientMessage, Connection connection) {
                return new ReleasePermitsMessageTask(clientMessage, DefaultMessageTaskFactoryProvider.this.node, connection);
            }
        };
    }

    @Override
    @SuppressFBWarnings(value={"MS_EXPOSE_REP", "EI_EXPOSE_REP"})
    public MessageTaskFactory[] getFactories() {
        return this.factories;
    }
}

