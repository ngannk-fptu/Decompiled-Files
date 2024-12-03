/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.s3.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum Event {
    S3_REDUCED_REDUNDANCY_LOST_OBJECT("s3:ReducedRedundancyLostObject"),
    S3_OBJECT_CREATED("s3:ObjectCreated:*"),
    S3_OBJECT_CREATED_PUT("s3:ObjectCreated:Put"),
    S3_OBJECT_CREATED_POST("s3:ObjectCreated:Post"),
    S3_OBJECT_CREATED_COPY("s3:ObjectCreated:Copy"),
    S3_OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD("s3:ObjectCreated:CompleteMultipartUpload"),
    S3_OBJECT_REMOVED("s3:ObjectRemoved:*"),
    S3_OBJECT_REMOVED_DELETE("s3:ObjectRemoved:Delete"),
    S3_OBJECT_REMOVED_DELETE_MARKER_CREATED("s3:ObjectRemoved:DeleteMarkerCreated"),
    S3_OBJECT_RESTORE("s3:ObjectRestore:*"),
    S3_OBJECT_RESTORE_POST("s3:ObjectRestore:Post"),
    S3_OBJECT_RESTORE_COMPLETED("s3:ObjectRestore:Completed"),
    S3_REPLICATION("s3:Replication:*"),
    S3_REPLICATION_OPERATION_FAILED_REPLICATION("s3:Replication:OperationFailedReplication"),
    S3_REPLICATION_OPERATION_NOT_TRACKED("s3:Replication:OperationNotTracked"),
    S3_REPLICATION_OPERATION_MISSED_THRESHOLD("s3:Replication:OperationMissedThreshold"),
    S3_REPLICATION_OPERATION_REPLICATED_AFTER_THRESHOLD("s3:Replication:OperationReplicatedAfterThreshold"),
    S3_OBJECT_RESTORE_DELETE("s3:ObjectRestore:Delete"),
    S3_LIFECYCLE_TRANSITION("s3:LifecycleTransition"),
    S3_INTELLIGENT_TIERING("s3:IntelligentTiering"),
    S3_OBJECT_ACL_PUT("s3:ObjectAcl:Put"),
    S3_LIFECYCLE_EXPIRATION("s3:LifecycleExpiration:*"),
    S3_LIFECYCLE_EXPIRATION_DELETE("s3:LifecycleExpiration:Delete"),
    S3_LIFECYCLE_EXPIRATION_DELETE_MARKER_CREATED("s3:LifecycleExpiration:DeleteMarkerCreated"),
    S3_OBJECT_TAGGING("s3:ObjectTagging:*"),
    S3_OBJECT_TAGGING_PUT("s3:ObjectTagging:Put"),
    S3_OBJECT_TAGGING_DELETE("s3:ObjectTagging:Delete"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, Event> VALUE_MAP;
    private final String value;

    private Event(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static Event fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<Event> knownValues() {
        EnumSet<Event> knownValues = EnumSet.allOf(Event.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(Event.class, Event::toString);
    }
}

