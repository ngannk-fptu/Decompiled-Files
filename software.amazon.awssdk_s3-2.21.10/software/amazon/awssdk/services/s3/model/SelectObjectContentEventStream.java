/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.services.s3.model.ContinuationEvent;
import software.amazon.awssdk.services.s3.model.EndEvent;
import software.amazon.awssdk.services.s3.model.ProgressEvent;
import software.amazon.awssdk.services.s3.model.RecordsEvent;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.StatsEvent;
import software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream.DefaultCont;
import software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream.DefaultEnd;
import software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream.DefaultProgress;
import software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream.DefaultRecords;
import software.amazon.awssdk.services.s3.model.selectobjectcontenteventstream.DefaultStats;
import software.amazon.awssdk.utils.internal.EnumUtils;

@SdkPublicApi
public interface SelectObjectContentEventStream
extends SdkPojo {
    public static final SelectObjectContentEventStream UNKNOWN = new SelectObjectContentEventStream(){

        public List<SdkField<?>> sdkFields() {
            return Collections.emptyList();
        }

        @Override
        public void accept(SelectObjectContentResponseHandler.Visitor visitor) {
            visitor.visitDefault(this);
        }
    };

    public static RecordsEvent.Builder recordsBuilder() {
        return DefaultRecords.builder();
    }

    public static StatsEvent.Builder statsBuilder() {
        return DefaultStats.builder();
    }

    public static ProgressEvent.Builder progressBuilder() {
        return DefaultProgress.builder();
    }

    public static ContinuationEvent.Builder contBuilder() {
        return DefaultCont.builder();
    }

    public static EndEvent.Builder endBuilder() {
        return DefaultEnd.builder();
    }

    default public EventType sdkEventType() {
        return EventType.UNKNOWN_TO_SDK_VERSION;
    }

    public void accept(SelectObjectContentResponseHandler.Visitor var1);

    public static enum EventType {
        RECORDS("Records"),
        STATS("Stats"),
        PROGRESS("Progress"),
        CONT("Cont"),
        END("End"),
        UNKNOWN_TO_SDK_VERSION(null);

        private static final Map<String, EventType> VALUE_MAP;
        private final String value;

        private EventType(String value) {
            this.value = value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        public static EventType fromValue(String value) {
            if (value == null) {
                return null;
            }
            return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
        }

        public static Set<EventType> knownValues() {
            EnumSet<EventType> knownValues = EnumSet.allOf(EventType.class);
            knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
            return knownValues;
        }

        static {
            VALUE_MAP = EnumUtils.uniqueIndex(EventType.class, EventType::toString);
        }
    }
}

