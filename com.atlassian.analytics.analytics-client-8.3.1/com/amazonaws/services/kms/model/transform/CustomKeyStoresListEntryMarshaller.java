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
import com.amazonaws.services.kms.model.CustomKeyStoresListEntry;
import java.util.Date;

@SdkInternalApi
public class CustomKeyStoresListEntryMarshaller {
    private static final MarshallingInfo<String> CUSTOMKEYSTOREID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreId").build();
    private static final MarshallingInfo<String> CUSTOMKEYSTORENAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CustomKeyStoreName").build();
    private static final MarshallingInfo<String> CLOUDHSMCLUSTERID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CloudHsmClusterId").build();
    private static final MarshallingInfo<String> TRUSTANCHORCERTIFICATE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TrustAnchorCertificate").build();
    private static final MarshallingInfo<String> CONNECTIONSTATE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ConnectionState").build();
    private static final MarshallingInfo<String> CONNECTIONERRORCODE_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("ConnectionErrorCode").build();
    private static final MarshallingInfo<Date> CREATIONDATE_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CreationDate").timestampFormat("unixTimestamp").build();
    private static final CustomKeyStoresListEntryMarshaller instance = new CustomKeyStoresListEntryMarshaller();

    public static CustomKeyStoresListEntryMarshaller getInstance() {
        return instance;
    }

    public void marshall(CustomKeyStoresListEntry customKeyStoresListEntry, ProtocolMarshaller protocolMarshaller) {
        if (customKeyStoresListEntry == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(customKeyStoresListEntry.getCustomKeyStoreId(), CUSTOMKEYSTOREID_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getCustomKeyStoreName(), CUSTOMKEYSTORENAME_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getCloudHsmClusterId(), CLOUDHSMCLUSTERID_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getTrustAnchorCertificate(), TRUSTANCHORCERTIFICATE_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getConnectionState(), CONNECTIONSTATE_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getConnectionErrorCode(), CONNECTIONERRORCODE_BINDING);
            protocolMarshaller.marshall(customKeyStoresListEntry.getCreationDate(), CREATIONDATE_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

