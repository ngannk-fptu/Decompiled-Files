/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model.transform;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.MarshallLocation;
import com.amazonaws.protocol.MarshallingInfo;
import com.amazonaws.protocol.MarshallingType;
import com.amazonaws.protocol.ProtocolMarshaller;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;

@SdkInternalApi
public class ScheduleKeyDeletionRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<Integer> PENDINGWINDOWINDAYS_BINDING = MarshallingInfo.builder(MarshallingType.INTEGER).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("PendingWindowInDays").build();
    private static final ScheduleKeyDeletionRequestMarshaller instance = new ScheduleKeyDeletionRequestMarshaller();

    public static ScheduleKeyDeletionRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(ScheduleKeyDeletionRequest scheduleKeyDeletionRequest, ProtocolMarshaller protocolMarshaller) {
        if (scheduleKeyDeletionRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(scheduleKeyDeletionRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(scheduleKeyDeletionRequest.getPendingWindowInDays(), PENDINGWINDOWINDAYS_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

