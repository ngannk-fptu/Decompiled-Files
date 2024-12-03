/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.codec.MemberCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.core.Member;
import com.hazelcast.logging.Logger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientAddMembershipListenerCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_ADDMEMBERSHIPLISTENER;
    public static final int RESPONSE_TYPE = 104;

    public static ClientMessage encodeRequest(boolean localOnly) {
        int requiredDataSize = RequestParameters.calculateDataSize(localOnly);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(false);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.addMembershipListener");
        clientMessage.set(localOnly);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        boolean localOnly = false;
        parameters.localOnly = localOnly = clientMessage.getBoolean();
        return parameters;
    }

    public static ClientMessage encodeResponse(String response) {
        int requiredDataSize = ResponseParameters.calculateDataSize(response);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(104);
        clientMessage.set(response);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        String response = null;
        parameters.response = response = clientMessage.getStringUtf8();
        return parameters;
    }

    public static ClientMessage encodeMemberEvent(Member member, int eventType) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += MemberCodec.calculateDataSize(member);
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize += 4);
        clientMessage.setMessageType(200);
        clientMessage.addFlag((short)1);
        MemberCodec.encode(member, clientMessage);
        clientMessage.set(eventType);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeMemberListEvent(Collection<Member> members) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += 4;
        for (Member members_item : members) {
            dataSize += MemberCodec.calculateDataSize(members_item);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize);
        clientMessage.setMessageType(201);
        clientMessage.addFlag((short)1);
        clientMessage.set(members.size());
        for (Member members_item : members) {
            MemberCodec.encode(members_item, clientMessage);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeMemberAttributeChangeEvent(String uuid, String key, int operationType, String value) {
        int dataSize = ClientMessage.HEADER_SIZE;
        dataSize += ParameterUtil.calculateDataSize(uuid);
        dataSize += ParameterUtil.calculateDataSize(key);
        dataSize += 4;
        ++dataSize;
        if (value != null) {
            dataSize += ParameterUtil.calculateDataSize(value);
        }
        ClientMessage clientMessage = ClientMessage.createForEncode(dataSize);
        clientMessage.setMessageType(202);
        clientMessage.addFlag((short)1);
        clientMessage.set(uuid);
        clientMessage.set(key);
        clientMessage.set(operationType);
        if (value == null) {
            boolean value_isNull = true;
            clientMessage.set(value_isNull);
        } else {
            boolean value_isNull = false;
            clientMessage.set(value_isNull);
            clientMessage.set(value);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static abstract class AbstractEventHandler {
        public void handle(ClientMessage clientMessage) {
            int messageType = clientMessage.getMessageType();
            if (messageType == 200) {
                Member member = null;
                member = MemberCodec.decode(clientMessage);
                int eventType = 0;
                eventType = clientMessage.getInt();
                this.handleMemberEventV10(member, eventType);
                return;
            }
            if (messageType == 201) {
                ArrayList<Member> members = null;
                int members_size = clientMessage.getInt();
                members = new ArrayList<Member>(members_size);
                for (int members_index = 0; members_index < members_size; ++members_index) {
                    Member members_item = MemberCodec.decode(clientMessage);
                    members.add(members_item);
                }
                this.handleMemberListEventV10(members);
                return;
            }
            if (messageType == 202) {
                String uuid = null;
                uuid = clientMessage.getStringUtf8();
                String key = null;
                key = clientMessage.getStringUtf8();
                int operationType = 0;
                operationType = clientMessage.getInt();
                String value = null;
                boolean value_isNull = clientMessage.getBoolean();
                if (!value_isNull) {
                    value = clientMessage.getStringUtf8();
                }
                this.handleMemberAttributeChangeEventV10(uuid, key, operationType, value);
                return;
            }
            Logger.getLogger(super.getClass()).warning("Unknown message type received on event handler :" + messageType);
        }

        public abstract void handleMemberEventV10(Member var1, int var2);

        public abstract void handleMemberListEventV10(Collection<Member> var1);

        public abstract void handleMemberAttributeChangeEventV10(String var1, String var2, int var3, String var4);
    }

    public static class ResponseParameters {
        public String response;

        public static int calculateDataSize(String response) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return dataSize += ParameterUtil.calculateDataSize(response);
        }
    }

    public static class RequestParameters {
        public static final ClientMessageType TYPE = REQUEST_TYPE;
        public boolean localOnly;

        public static int calculateDataSize(boolean localOnly) {
            int dataSize = ClientMessage.HEADER_SIZE;
            return ++dataSize;
        }
    }
}

