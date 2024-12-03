/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.amazonaws.services.s3.event;

import com.amazonaws.internal.DateTimeJsonSerializer;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.joda.time.DateTime;

public class S3EventNotification {
    private final List<S3EventNotificationRecord> records;

    @JsonCreator
    public S3EventNotification(@JsonProperty(value="Records") List<S3EventNotificationRecord> records) {
        this.records = records;
    }

    public static S3EventNotification parseJson(String json) {
        return Jackson.fromJsonString(json, S3EventNotification.class);
    }

    @JsonProperty(value="Records")
    public List<S3EventNotificationRecord> getRecords() {
        return this.records;
    }

    public String toJson() {
        return Jackson.toJsonString(this);
    }

    public static class S3EventNotificationRecord {
        private final String awsRegion;
        private final String eventName;
        private final String eventSource;
        private DateTime eventTime;
        private final String eventVersion;
        private final RequestParametersEntity requestParameters;
        private final ResponseElementsEntity responseElements;
        private final S3Entity s3;
        private final UserIdentityEntity userIdentity;
        private final GlacierEventDataEntity glacierEventData;
        private final LifecycleEventDataEntity lifecycleEventData;
        private final IntelligentTieringEventDataEntity intelligentTieringEventData;
        private final ReplicationEventDataEntity replicationEventDataEntity;

        @Deprecated
        public S3EventNotificationRecord(String awsRegion, String eventName, String eventSource, String eventTime, String eventVersion, RequestParametersEntity requestParameters, ResponseElementsEntity responseElements, S3Entity s3, UserIdentityEntity userIdentity) {
            this(awsRegion, eventName, eventSource, eventTime, eventVersion, requestParameters, responseElements, s3, userIdentity, null, null, null, null);
        }

        @Deprecated
        public S3EventNotificationRecord(String awsRegion, String eventName, String eventSource, String eventTime, String eventVersion, RequestParametersEntity requestParameters, ResponseElementsEntity responseElements, S3Entity s3, UserIdentityEntity userIdentity, GlacierEventDataEntity glacierEventData) {
            this(awsRegion, eventName, eventSource, eventTime, eventVersion, requestParameters, responseElements, s3, userIdentity, glacierEventData, null, null, null);
        }

        @JsonCreator
        public S3EventNotificationRecord(@JsonProperty(value="awsRegion") String awsRegion, @JsonProperty(value="eventName") String eventName, @JsonProperty(value="eventSource") String eventSource, @JsonProperty(value="eventTime") String eventTime, @JsonProperty(value="eventVersion") String eventVersion, @JsonProperty(value="requestParameters") RequestParametersEntity requestParameters, @JsonProperty(value="responseElements") ResponseElementsEntity responseElements, @JsonProperty(value="s3") S3Entity s3, @JsonProperty(value="userIdentity") UserIdentityEntity userIdentity, @JsonProperty(value="glacierEventData") GlacierEventDataEntity glacierEventData, @JsonProperty(value="lifecycleEventData") LifecycleEventDataEntity lifecycleEventData, @JsonProperty(value="intelligentTieringEventData") IntelligentTieringEventDataEntity intelligentTieringEventData, @JsonProperty(value="replicationEventData") ReplicationEventDataEntity replicationEventData) {
            this.awsRegion = awsRegion;
            this.eventName = eventName;
            this.eventSource = eventSource;
            if (eventTime != null) {
                this.eventTime = DateTime.parse((String)eventTime);
            }
            this.eventVersion = eventVersion;
            this.requestParameters = requestParameters;
            this.responseElements = responseElements;
            this.s3 = s3;
            this.userIdentity = userIdentity;
            this.glacierEventData = glacierEventData;
            this.lifecycleEventData = lifecycleEventData;
            this.intelligentTieringEventData = intelligentTieringEventData;
            this.replicationEventDataEntity = replicationEventData;
        }

        public String getAwsRegion() {
            return this.awsRegion;
        }

        public String getEventName() {
            return this.eventName;
        }

        @JsonIgnore
        public S3Event getEventNameAsEnum() {
            return S3Event.fromValue(this.eventName);
        }

        public String getEventSource() {
            return this.eventSource;
        }

        @JsonSerialize(using=DateTimeJsonSerializer.class)
        public DateTime getEventTime() {
            return this.eventTime;
        }

        public String getEventVersion() {
            return this.eventVersion;
        }

        public RequestParametersEntity getRequestParameters() {
            return this.requestParameters;
        }

        public ResponseElementsEntity getResponseElements() {
            return this.responseElements;
        }

        public S3Entity getS3() {
            return this.s3;
        }

        public UserIdentityEntity getUserIdentity() {
            return this.userIdentity;
        }

        public GlacierEventDataEntity getGlacierEventData() {
            return this.glacierEventData;
        }

        public LifecycleEventDataEntity getLifecycleEventData() {
            return this.lifecycleEventData;
        }

        public IntelligentTieringEventDataEntity getIntelligentTieringEventData() {
            return this.intelligentTieringEventData;
        }

        public ReplicationEventDataEntity getReplicationEventDataEntity() {
            return this.replicationEventDataEntity;
        }
    }

    public static class RestoreEventDataEntity {
        private DateTime lifecycleRestorationExpiryTime;
        private final String lifecycleRestoreStorageClass;

        @JsonCreator
        public RestoreEventDataEntity(@JsonProperty(value="lifecycleRestorationExpiryTime") String lifecycleRestorationExpiryTime, @JsonProperty(value="lifecycleRestoreStorageClass") String lifecycleRestoreStorageClass) {
            if (lifecycleRestorationExpiryTime != null) {
                this.lifecycleRestorationExpiryTime = DateTime.parse((String)lifecycleRestorationExpiryTime);
            }
            this.lifecycleRestoreStorageClass = lifecycleRestoreStorageClass;
        }

        @JsonSerialize(using=DateTimeJsonSerializer.class)
        public DateTime getLifecycleRestorationExpiryTime() {
            return this.lifecycleRestorationExpiryTime;
        }

        public String getLifecycleRestoreStorageClass() {
            return this.lifecycleRestoreStorageClass;
        }
    }

    public static class TransitionEventDataEntity {
        private final String destinationStorageClass;

        @JsonCreator
        public TransitionEventDataEntity(@JsonProperty(value="destinationStorageClass") String destinationStorageClass) {
            this.destinationStorageClass = destinationStorageClass;
        }

        public String getDestinationStorageClass() {
            return this.destinationStorageClass;
        }
    }

    public static class ReplicationEventDataEntity {
        private final String replicationRuleId;
        private final String destinationBucket;
        private final String s3Operation;
        private final String requestTime;
        private final String failureReason;
        private final String threshold;
        private final String replicationTime;

        @JsonCreator
        public ReplicationEventDataEntity(@JsonProperty(value="replicationRuleId") String replicationRuleId, @JsonProperty(value="destinationBucket") String destinationBucket, @JsonProperty(value="s3Operation") String s3Operation, @JsonProperty(value="requestTime") String requestTime, @JsonProperty(value="failureReason") String failureReason, @JsonProperty(value="threshold") String threshold, @JsonProperty(value="replicationTime") String replicationTime) {
            this.replicationRuleId = replicationRuleId;
            this.destinationBucket = destinationBucket;
            this.s3Operation = s3Operation;
            this.requestTime = requestTime;
            this.failureReason = failureReason;
            this.threshold = threshold;
            this.replicationTime = replicationTime;
        }

        @JsonProperty(value="replicationRuleId")
        public String getReplicationRuleId() {
            return this.replicationRuleId;
        }

        @JsonProperty(value="destinationBucket")
        public String getDestinationBucket() {
            return this.destinationBucket;
        }

        @JsonProperty(value="s3Operation")
        public String getS3Operation() {
            return this.s3Operation;
        }

        @JsonProperty(value="requestTime")
        public String getRequestTime() {
            return this.requestTime;
        }

        @JsonProperty(value="failureReason")
        public String getFailureReason() {
            return this.failureReason;
        }

        @JsonProperty(value="threshold")
        public String getThreshold() {
            return this.threshold;
        }

        @JsonProperty(value="replicationTime")
        public String getReplicationTime() {
            return this.replicationTime;
        }
    }

    public static class IntelligentTieringEventDataEntity {
        private final String destinationAccessTier;

        @JsonCreator
        public IntelligentTieringEventDataEntity(@JsonProperty(value="destinationAccessTier") String destinationAccessTier) {
            this.destinationAccessTier = destinationAccessTier;
        }

        @JsonProperty(value="destinationAccessTier")
        public String getDestinationAccessTier() {
            return this.destinationAccessTier;
        }
    }

    public static class LifecycleEventDataEntity {
        private final TransitionEventDataEntity transitionEventData;

        @JsonCreator
        public LifecycleEventDataEntity(@JsonProperty(value="transitionEventData") TransitionEventDataEntity transitionEventData) {
            this.transitionEventData = transitionEventData;
        }

        public TransitionEventDataEntity getTransitionEventData() {
            return this.transitionEventData;
        }
    }

    public static class GlacierEventDataEntity {
        private final RestoreEventDataEntity restoreEventData;

        @JsonCreator
        public GlacierEventDataEntity(@JsonProperty(value="restoreEventData") RestoreEventDataEntity restoreEventData) {
            this.restoreEventData = restoreEventData;
        }

        public RestoreEventDataEntity getRestoreEventData() {
            return this.restoreEventData;
        }
    }

    public static class ResponseElementsEntity {
        private final String xAmzId2;
        private final String xAmzRequestId;

        @JsonCreator
        public ResponseElementsEntity(@JsonProperty(value="x-amz-id-2") String xAmzId2, @JsonProperty(value="x-amz-request-id") String xAmzRequestId) {
            this.xAmzId2 = xAmzId2;
            this.xAmzRequestId = xAmzRequestId;
        }

        @JsonProperty(value="x-amz-id-2")
        public String getxAmzId2() {
            return this.xAmzId2;
        }

        @JsonProperty(value="x-amz-request-id")
        public String getxAmzRequestId() {
            return this.xAmzRequestId;
        }
    }

    public static class RequestParametersEntity {
        private final String sourceIPAddress;

        @JsonCreator
        public RequestParametersEntity(@JsonProperty(value="sourceIPAddress") String sourceIPAddress) {
            this.sourceIPAddress = sourceIPAddress;
        }

        public String getSourceIPAddress() {
            return this.sourceIPAddress;
        }
    }

    public static class S3Entity {
        private final String configurationId;
        private final S3BucketEntity bucket;
        private final S3ObjectEntity object;
        private final String s3SchemaVersion;

        @JsonCreator
        public S3Entity(@JsonProperty(value="configurationId") String configurationId, @JsonProperty(value="bucket") S3BucketEntity bucket, @JsonProperty(value="object") S3ObjectEntity object, @JsonProperty(value="s3SchemaVersion") String s3SchemaVersion) {
            this.configurationId = configurationId;
            this.bucket = bucket;
            this.object = object;
            this.s3SchemaVersion = s3SchemaVersion;
        }

        public String getConfigurationId() {
            return this.configurationId;
        }

        public S3BucketEntity getBucket() {
            return this.bucket;
        }

        public S3ObjectEntity getObject() {
            return this.object;
        }

        public String getS3SchemaVersion() {
            return this.s3SchemaVersion;
        }
    }

    public static class S3ObjectEntity {
        private final String key;
        private final Long size;
        private final String eTag;
        private final String versionId;
        private final String sequencer;

        @Deprecated
        public S3ObjectEntity(String key, Integer size, String eTag, String versionId) {
            this.key = key;
            this.size = size == null ? null : Long.valueOf(size.longValue());
            this.eTag = eTag;
            this.versionId = versionId;
            this.sequencer = null;
        }

        @Deprecated
        public S3ObjectEntity(String key, Long size, String eTag, String versionId) {
            this(key, size, eTag, versionId, null);
        }

        @JsonCreator
        public S3ObjectEntity(@JsonProperty(value="key") String key, @JsonProperty(value="size") Long size, @JsonProperty(value="eTag") String eTag, @JsonProperty(value="versionId") String versionId, @JsonProperty(value="sequencer") String sequencer) {
            this.key = key;
            this.size = size;
            this.eTag = eTag;
            this.versionId = versionId;
            this.sequencer = sequencer;
        }

        public String getKey() {
            return this.key;
        }

        public String getUrlDecodedKey() {
            return SdkHttpUtils.urlDecode(this.getKey());
        }

        @Deprecated
        @JsonIgnore
        public Integer getSize() {
            return this.size == null ? null : Integer.valueOf(this.size.intValue());
        }

        @JsonProperty(value="size")
        public Long getSizeAsLong() {
            return this.size;
        }

        public String geteTag() {
            return this.eTag;
        }

        public String getVersionId() {
            return this.versionId;
        }

        public String getSequencer() {
            return this.sequencer;
        }
    }

    public static class S3BucketEntity {
        private final String name;
        private final UserIdentityEntity ownerIdentity;
        private final String arn;

        @JsonCreator
        public S3BucketEntity(@JsonProperty(value="name") String name, @JsonProperty(value="ownerIdentity") UserIdentityEntity ownerIdentity, @JsonProperty(value="arn") String arn) {
            this.name = name;
            this.ownerIdentity = ownerIdentity;
            this.arn = arn;
        }

        public String getName() {
            return this.name;
        }

        public UserIdentityEntity getOwnerIdentity() {
            return this.ownerIdentity;
        }

        public String getArn() {
            return this.arn;
        }
    }

    public static class UserIdentityEntity {
        private final String principalId;

        @JsonCreator
        public UserIdentityEntity(@JsonProperty(value="principalId") String principalId) {
            this.principalId = principalId;
        }

        public String getPrincipalId() {
            return this.principalId;
        }
    }
}

