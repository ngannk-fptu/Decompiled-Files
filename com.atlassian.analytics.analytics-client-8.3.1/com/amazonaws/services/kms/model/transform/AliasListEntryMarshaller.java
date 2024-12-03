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
import com.amazonaws.services.kms.model.AliasListEntry;
import java.util.Date;

@SdkInternalApi
public class AliasListEntryMarshaller {
    private static final MarshallingInfo<String> ALIASNAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AliasName").build();
    private static final MarshallingInfo<String> ALIASARN_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("AliasArn").build();
    private static final MarshallingInfo<String> TARGETKEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("TargetKeyId").build();
    private static final MarshallingInfo<Date> CREATIONDATE_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("CreationDate").timestampFormat("unixTimestamp").build();
    private static final MarshallingInfo<Date> LASTUPDATEDDATE_BINDING = MarshallingInfo.builder(MarshallingType.DATE).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("LastUpdatedDate").timestampFormat("unixTimestamp").build();
    private static final AliasListEntryMarshaller instance = new AliasListEntryMarshaller();

    public static AliasListEntryMarshaller getInstance() {
        return instance;
    }

    public void marshall(AliasListEntry aliasListEntry, ProtocolMarshaller protocolMarshaller) {
        if (aliasListEntry == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(aliasListEntry.getAliasName(), ALIASNAME_BINDING);
            protocolMarshaller.marshall(aliasListEntry.getAliasArn(), ALIASARN_BINDING);
            protocolMarshaller.marshall(aliasListEntry.getTargetKeyId(), TARGETKEYID_BINDING);
            protocolMarshaller.marshall(aliasListEntry.getCreationDate(), CREATIONDATE_BINDING);
            protocolMarshaller.marshall(aliasListEntry.getLastUpdatedDate(), LASTUPDATEDDATE_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

