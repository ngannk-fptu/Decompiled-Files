/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.codec.ClientMessageType;
import com.hazelcast.client.impl.protocol.codec.MemberCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public final class ClientAuthenticationCodec {
    public static final ClientMessageType REQUEST_TYPE = ClientMessageType.CLIENT_AUTHENTICATION;
    public static final int RESPONSE_TYPE = 107;

    public static ClientMessage encodeRequest(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion) {
        int requiredDataSize = RequestParameters.calculateDataSize(username, password, uuid, ownerUuid, isOwnerConnection, clientType, serializationVersion);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.authentication");
        clientMessage.set(username);
        clientMessage.set(password);
        if (uuid == null) {
            boolean uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            boolean uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            boolean ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            boolean ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(isOwnerConnection);
        clientMessage.set(clientType);
        clientMessage.set(serializationVersion);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion, String clientHazelcastVersion) {
        int requiredDataSize = RequestParameters.calculateDataSize(username, password, uuid, ownerUuid, isOwnerConnection, clientType, serializationVersion, clientHazelcastVersion);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.authentication");
        clientMessage.set(username);
        clientMessage.set(password);
        if (uuid == null) {
            boolean uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            boolean uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            boolean ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            boolean ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(isOwnerConnection);
        clientMessage.set(clientType);
        clientMessage.set(serializationVersion);
        clientMessage.set(clientHazelcastVersion);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeRequest(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion, String clientHazelcastVersion, String clientName, Collection<String> labels, Integer partitionCount, String clusterId) {
        boolean ownerUuid_isNull;
        boolean uuid_isNull;
        int requiredDataSize = RequestParameters.calculateDataSize(username, password, uuid, ownerUuid, isOwnerConnection, clientType, serializationVersion, clientHazelcastVersion, clientName, labels, partitionCount, clusterId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(REQUEST_TYPE.id());
        clientMessage.setRetryable(true);
        clientMessage.setAcquiresResource(false);
        clientMessage.setOperationName("Client.authentication");
        clientMessage.set(username);
        clientMessage.set(password);
        if (uuid == null) {
            uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(isOwnerConnection);
        clientMessage.set(clientType);
        clientMessage.set(serializationVersion);
        clientMessage.set(clientHazelcastVersion);
        clientMessage.set(clientName);
        clientMessage.set(labels.size());
        for (String labels_item : labels) {
            clientMessage.set(labels_item);
        }
        if (partitionCount == null) {
            boolean partitionCount_isNull = true;
            clientMessage.set(partitionCount_isNull);
        } else {
            boolean partitionCount_isNull = false;
            clientMessage.set(partitionCount_isNull);
            clientMessage.set(partitionCount);
        }
        if (clusterId == null) {
            boolean clusterId_isNull = true;
            clientMessage.set(clusterId_isNull);
        } else {
            boolean clusterId_isNull = false;
            clientMessage.set(clusterId_isNull);
            clientMessage.set(clusterId);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static RequestParameters decodeRequest(ClientMessage clientMessage) {
        RequestParameters parameters = new RequestParameters();
        String username = null;
        parameters.username = username = clientMessage.getStringUtf8();
        String password = null;
        parameters.password = password = clientMessage.getStringUtf8();
        String uuid = null;
        boolean uuid_isNull = clientMessage.getBoolean();
        if (!uuid_isNull) {
            parameters.uuid = uuid = clientMessage.getStringUtf8();
        }
        String ownerUuid = null;
        boolean ownerUuid_isNull = clientMessage.getBoolean();
        if (!ownerUuid_isNull) {
            parameters.ownerUuid = ownerUuid = clientMessage.getStringUtf8();
        }
        boolean isOwnerConnection = false;
        parameters.isOwnerConnection = isOwnerConnection = clientMessage.getBoolean();
        String clientType = null;
        parameters.clientType = clientType = clientMessage.getStringUtf8();
        byte serializationVersion = 0;
        parameters.serializationVersion = serializationVersion = clientMessage.getByte();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        try {
            String clientHazelcastVersion = null;
            parameters.clientHazelcastVersion = clientHazelcastVersion = clientMessage.getStringUtf8();
        }
        catch (IndexOutOfBoundsException e) {
            if ("CSP".equals(parameters.clientType)) {
                return parameters;
            }
            throw e;
        }
        parameters.clientHazelcastVersionExist = true;
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String clientName = null;
        parameters.clientName = clientName = clientMessage.getStringUtf8();
        parameters.clientNameExist = true;
        ArrayList<String> labels = null;
        int labels_size = clientMessage.getInt();
        labels = new ArrayList<String>(labels_size);
        for (int labels_index = 0; labels_index < labels_size; ++labels_index) {
            String labels_item = clientMessage.getStringUtf8();
            labels.add(labels_item);
        }
        parameters.labels = labels;
        parameters.labelsExist = true;
        Integer partitionCount = null;
        boolean partitionCount_isNull = clientMessage.getBoolean();
        if (!partitionCount_isNull) {
            parameters.partitionCount = partitionCount = Integer.valueOf(clientMessage.getInt());
        }
        parameters.partitionCountExist = true;
        String clusterId = null;
        boolean clusterId_isNull = clientMessage.getBoolean();
        if (!clusterId_isNull) {
            parameters.clusterId = clusterId = clientMessage.getStringUtf8();
        }
        parameters.clusterIdExist = true;
        return parameters;
    }

    public static ClientMessage encodeResponse(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion) {
        int requiredDataSize = ResponseParameters.calculateDataSize(status, address, uuid, ownerUuid, serializationVersion);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(107);
        clientMessage.set(status);
        if (address == null) {
            boolean address_isNull = true;
            clientMessage.set(address_isNull);
        } else {
            boolean address_isNull = false;
            clientMessage.set(address_isNull);
            AddressCodec.encode(address, clientMessage);
        }
        if (uuid == null) {
            boolean uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            boolean uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            boolean ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            boolean ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(serializationVersion);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion, String serverHazelcastVersion, Collection<Member> clientUnregisteredMembers) {
        boolean ownerUuid_isNull;
        boolean uuid_isNull;
        boolean address_isNull;
        int requiredDataSize = ResponseParameters.calculateDataSize(status, address, uuid, ownerUuid, serializationVersion, serverHazelcastVersion, clientUnregisteredMembers);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(107);
        clientMessage.set(status);
        if (address == null) {
            address_isNull = true;
            clientMessage.set(address_isNull);
        } else {
            address_isNull = false;
            clientMessage.set(address_isNull);
            AddressCodec.encode(address, clientMessage);
        }
        if (uuid == null) {
            uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(serializationVersion);
        clientMessage.set(serverHazelcastVersion);
        if (clientUnregisteredMembers == null) {
            boolean clientUnregisteredMembers_isNull = true;
            clientMessage.set(clientUnregisteredMembers_isNull);
        } else {
            boolean clientUnregisteredMembers_isNull = false;
            clientMessage.set(clientUnregisteredMembers_isNull);
            clientMessage.set(clientUnregisteredMembers.size());
            for (Member clientUnregisteredMembers_item : clientUnregisteredMembers) {
                MemberCodec.encode(clientUnregisteredMembers_item, clientMessage);
            }
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ClientMessage encodeResponse(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion, String serverHazelcastVersion, Collection<Member> clientUnregisteredMembers, int partitionCount, String clusterId) {
        boolean ownerUuid_isNull;
        boolean uuid_isNull;
        boolean address_isNull;
        int requiredDataSize = ResponseParameters.calculateDataSize(status, address, uuid, ownerUuid, serializationVersion, serverHazelcastVersion, clientUnregisteredMembers, partitionCount, clusterId);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(107);
        clientMessage.set(status);
        if (address == null) {
            address_isNull = true;
            clientMessage.set(address_isNull);
        } else {
            address_isNull = false;
            clientMessage.set(address_isNull);
            AddressCodec.encode(address, clientMessage);
        }
        if (uuid == null) {
            uuid_isNull = true;
            clientMessage.set(uuid_isNull);
        } else {
            uuid_isNull = false;
            clientMessage.set(uuid_isNull);
            clientMessage.set(uuid);
        }
        if (ownerUuid == null) {
            ownerUuid_isNull = true;
            clientMessage.set(ownerUuid_isNull);
        } else {
            ownerUuid_isNull = false;
            clientMessage.set(ownerUuid_isNull);
            clientMessage.set(ownerUuid);
        }
        clientMessage.set(serializationVersion);
        clientMessage.set(serverHazelcastVersion);
        if (clientUnregisteredMembers == null) {
            boolean clientUnregisteredMembers_isNull = true;
            clientMessage.set(clientUnregisteredMembers_isNull);
        } else {
            boolean clientUnregisteredMembers_isNull = false;
            clientMessage.set(clientUnregisteredMembers_isNull);
            clientMessage.set(clientUnregisteredMembers.size());
            for (Member clientUnregisteredMembers_item : clientUnregisteredMembers) {
                MemberCodec.encode(clientUnregisteredMembers_item, clientMessage);
            }
        }
        clientMessage.set(partitionCount);
        clientMessage.set(clusterId);
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ResponseParameters parameters = new ResponseParameters();
        byte status = 0;
        parameters.status = status = clientMessage.getByte();
        Address address = null;
        boolean address_isNull = clientMessage.getBoolean();
        if (!address_isNull) {
            parameters.address = address = AddressCodec.decode(clientMessage);
        }
        String uuid = null;
        boolean uuid_isNull = clientMessage.getBoolean();
        if (!uuid_isNull) {
            parameters.uuid = uuid = clientMessage.getStringUtf8();
        }
        String ownerUuid = null;
        boolean ownerUuid_isNull = clientMessage.getBoolean();
        if (!ownerUuid_isNull) {
            parameters.ownerUuid = ownerUuid = clientMessage.getStringUtf8();
        }
        byte serializationVersion = 0;
        parameters.serializationVersion = serializationVersion = clientMessage.getByte();
        if (clientMessage.isComplete()) {
            return parameters;
        }
        String serverHazelcastVersion = null;
        parameters.serverHazelcastVersion = serverHazelcastVersion = clientMessage.getStringUtf8();
        parameters.serverHazelcastVersionExist = true;
        ArrayList<Member> clientUnregisteredMembers = null;
        boolean clientUnregisteredMembers_isNull = clientMessage.getBoolean();
        if (!clientUnregisteredMembers_isNull) {
            int clientUnregisteredMembers_size = clientMessage.getInt();
            clientUnregisteredMembers = new ArrayList<Member>(clientUnregisteredMembers_size);
            for (int clientUnregisteredMembers_index = 0; clientUnregisteredMembers_index < clientUnregisteredMembers_size; ++clientUnregisteredMembers_index) {
                Member clientUnregisteredMembers_item = MemberCodec.decode(clientMessage);
                clientUnregisteredMembers.add(clientUnregisteredMembers_item);
            }
            parameters.clientUnregisteredMembers = clientUnregisteredMembers;
        }
        parameters.clientUnregisteredMembersExist = true;
        if (clientMessage.isComplete()) {
            return parameters;
        }
        int partitionCount = 0;
        parameters.partitionCount = partitionCount = clientMessage.getInt();
        parameters.partitionCountExist = true;
        String clusterId = null;
        parameters.clusterId = clusterId = clientMessage.getStringUtf8();
        parameters.clusterIdExist = true;
        return parameters;
    }

    public static class ResponseParameters {
        public byte status;
        public Address address;
        public String uuid;
        public String ownerUuid;
        public byte serializationVersion;
        public boolean serverHazelcastVersionExist = false;
        public String serverHazelcastVersion;
        public boolean clientUnregisteredMembersExist = false;
        public List<Member> clientUnregisteredMembers;
        public boolean partitionCountExist = false;
        public int partitionCount;
        public boolean clusterIdExist = false;
        public String clusterId;

        public static int calculateDataSize(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            ++dataSize;
            if (address != null) {
                dataSize += AddressCodec.calculateDataSize(address);
            }
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            return ++dataSize;
        }

        public static int calculateDataSize(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion, String serverHazelcastVersion, Collection<Member> clientUnregisteredMembers) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            ++dataSize;
            if (address != null) {
                dataSize += AddressCodec.calculateDataSize(address);
            }
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(serverHazelcastVersion);
            ++dataSize;
            if (clientUnregisteredMembers != null) {
                dataSize += 4;
                for (Member clientUnregisteredMembers_item : clientUnregisteredMembers) {
                    dataSize += MemberCodec.calculateDataSize(clientUnregisteredMembers_item);
                }
            }
            return dataSize;
        }

        public static int calculateDataSize(byte status, Address address, String uuid, String ownerUuid, byte serializationVersion, String serverHazelcastVersion, Collection<Member> clientUnregisteredMembers, int partitionCount, String clusterId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            ++dataSize;
            ++dataSize;
            if (address != null) {
                dataSize += AddressCodec.calculateDataSize(address);
            }
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(serverHazelcastVersion);
            ++dataSize;
            if (clientUnregisteredMembers != null) {
                dataSize += 4;
                for (Member clientUnregisteredMembers_item : clientUnregisteredMembers) {
                    dataSize += MemberCodec.calculateDataSize(clientUnregisteredMembers_item);
                }
            }
            dataSize += 4;
            return dataSize += ParameterUtil.calculateDataSize(clusterId);
        }
    }

    public static class RequestParameters {
        public static final ClientMessageType TYPE = REQUEST_TYPE;
        public String username;
        public String password;
        public String uuid;
        public String ownerUuid;
        public boolean isOwnerConnection;
        public String clientType;
        public byte serializationVersion;
        public boolean clientHazelcastVersionExist = false;
        public String clientHazelcastVersion;
        public boolean clientNameExist = false;
        public String clientName;
        public boolean labelsExist = false;
        public List<String> labels;
        public boolean partitionCountExist = false;
        public Integer partitionCount;
        public boolean clusterIdExist = false;
        public String clusterId;

        public static int calculateDataSize(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(username);
            dataSize += ParameterUtil.calculateDataSize(password);
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(clientType);
            return ++dataSize;
        }

        public static int calculateDataSize(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion, String clientHazelcastVersion) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(username);
            dataSize += ParameterUtil.calculateDataSize(password);
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(clientType);
            ++dataSize;
            return dataSize += ParameterUtil.calculateDataSize(clientHazelcastVersion);
        }

        public static int calculateDataSize(String username, String password, String uuid, String ownerUuid, boolean isOwnerConnection, String clientType, byte serializationVersion, String clientHazelcastVersion, String clientName, Collection<String> labels, Integer partitionCount, String clusterId) {
            int dataSize = ClientMessage.HEADER_SIZE;
            dataSize += ParameterUtil.calculateDataSize(username);
            dataSize += ParameterUtil.calculateDataSize(password);
            ++dataSize;
            if (uuid != null) {
                dataSize += ParameterUtil.calculateDataSize(uuid);
            }
            ++dataSize;
            if (ownerUuid != null) {
                dataSize += ParameterUtil.calculateDataSize(ownerUuid);
            }
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(clientType);
            ++dataSize;
            dataSize += ParameterUtil.calculateDataSize(clientHazelcastVersion);
            dataSize += ParameterUtil.calculateDataSize(clientName);
            dataSize += 4;
            for (String labels_item : labels) {
                dataSize += ParameterUtil.calculateDataSize(labels_item);
            }
            ++dataSize;
            if (partitionCount != null) {
                dataSize += ParameterUtil.calculateDataSize(partitionCount);
            }
            ++dataSize;
            if (clusterId != null) {
                dataSize += ParameterUtil.calculateDataSize(clusterId);
            }
            return dataSize;
        }
    }
}

