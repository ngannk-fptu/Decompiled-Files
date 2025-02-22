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
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.services.kms.model.CreateGrantRequest;
import java.util.List;

@SdkInternalApi
public class CreateGrantRequestMarshaller {
    private static final MarshallingInfo<String> KEYID_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("KeyId").build();
    private static final MarshallingInfo<String> GRANTEEPRINCIPAL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GranteePrincipal").build();
    private static final MarshallingInfo<String> RETIRINGPRINCIPAL_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("RetiringPrincipal").build();
    private static final MarshallingInfo<List> OPERATIONS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Operations").build();
    private static final MarshallingInfo<StructuredPojo> CONSTRAINTS_BINDING = MarshallingInfo.builder(MarshallingType.STRUCTURED).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Constraints").build();
    private static final MarshallingInfo<List> GRANTTOKENS_BINDING = MarshallingInfo.builder(MarshallingType.LIST).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("GrantTokens").build();
    private static final MarshallingInfo<String> NAME_BINDING = MarshallingInfo.builder(MarshallingType.STRING).marshallLocation(MarshallLocation.PAYLOAD).marshallLocationName("Name").build();
    private static final CreateGrantRequestMarshaller instance = new CreateGrantRequestMarshaller();

    public static CreateGrantRequestMarshaller getInstance() {
        return instance;
    }

    public void marshall(CreateGrantRequest createGrantRequest, ProtocolMarshaller protocolMarshaller) {
        if (createGrantRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }
        try {
            protocolMarshaller.marshall(createGrantRequest.getKeyId(), KEYID_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getGranteePrincipal(), GRANTEEPRINCIPAL_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getRetiringPrincipal(), RETIRINGPRINCIPAL_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getOperations(), OPERATIONS_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getConstraints(), CONSTRAINTS_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getGrantTokens(), GRANTTOKENS_BINDING);
            protocolMarshaller.marshall(createGrantRequest.getName(), NAME_BINDING);
        }
        catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }
}

