/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.lib.hex.Hex
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.base.MoreObjects
 *  com.hazelcast.nio.ObjectDataInput
 *  com.hazelcast.nio.ObjectDataOutput
 *  javax.annotation.Nonnull
 *  org.bouncycastle.crypto.generators.SCrypt
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator;

import aQute.lib.hex.Hex;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterAuthenticationResult;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterAuthenticator;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinMode;
import com.atlassian.confluence.impl.cluster.hazelcast.interceptor.authenticator.ClusterJoinRequest;
import com.atlassian.security.utils.ConstantTimeComparison;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.base.MoreObjects;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import javax.annotation.Nonnull;
import org.bouncycastle.crypto.generators.SCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedSecretClusterAuthenticator
implements ClusterAuthenticator {
    private static final int ITERATIONS = 32768;
    private static final int MAX_NETWORK_BYTEARRAY_SIZE = 1024;
    private static final int NONCE_BYTES = 16;
    private static final Random RND = new SecureRandom();
    private static final String VERIFICATION_FAILED_MESSAGE = "Cluster authentication failed. Please make sure all members share the same value for 'confluence.cluster.name' and 'confluence.cluster.authentication.secret' in confluence.cfg.xml.";
    private static final int VERSION = 2;
    private static final Logger log = LoggerFactory.getLogger(SharedSecretClusterAuthenticator.class);
    private final String groupName;
    private final String sharedSecret;

    public SharedSecretClusterAuthenticator(String groupName, String sharedSecret) {
        this.groupName = groupName;
        this.sharedSecret = sharedSecret;
    }

    @Override
    public ClusterAuthenticationResult authenticate(@Nonnull ClusterJoinRequest request) throws IOException {
        try (Ticker ignored = Timers.start((String)("Cluster member authentication mode - " + request.getJoinMode()));){
            ClusterAuthenticationResult clusterAuthenticationResult = this.runMutualChallengeResponse(request);
            return clusterAuthenticationResult;
        }
    }

    private static byte[] readByteArray(ObjectDataInput in) throws IOException {
        int length = in.readInt();
        if (length > 1024 || length <= 0) {
            throw new IOException("Unable to read array: invalid length: " + length);
        }
        byte[] array = new byte[length];
        in.readFully(array);
        return array;
    }

    private static void writeByteArray(ObjectDataOutput out, byte[] array) throws IOException {
        out.writeInt(array.length);
        out.write(array);
    }

    private byte[] generateSalt(byte[] firstNonce, byte[] secondNonce, String address, int port, boolean isConnect) throws IOException {
        try (ByteArrayOutputStream salt = new ByteArrayOutputStream();){
            byte[] byArray;
            try (DataOutputStream data = new DataOutputStream(salt);){
                data.write(firstNonce);
                data.write(secondNonce);
                data.writeBoolean(isConnect);
                data.writeInt(port);
                data.write(address.getBytes(StandardCharsets.UTF_8));
                byArray = salt.toByteArray();
            }
            return byArray;
        }
    }

    private byte[] generateKey(byte[] salt) throws UnsupportedEncodingException {
        try (Ticker ignored = Timers.start((String)"Generate key");){
            byte[] byArray = SCrypt.generate((byte[])this.sharedSecret.getBytes(StandardCharsets.UTF_8.name()), (byte[])salt, (int)32768, (int)8, (int)1, (int)32);
            return byArray;
        }
    }

    private ClusterAuthenticationResult runMutualChallengeResponse(ClusterJoinRequest request) {
        try {
            boolean isConnecting;
            log.debug("Inside runMutualChallengeResponse ... ClusterJoinRequest : {}", (Object)request);
            ObjectDataInput in = request.in();
            ObjectDataOutput out = request.out();
            boolean bl = isConnecting = request.getJoinMode() == ClusterJoinMode.CONNECT;
            if (!this.verifyGroupName(in, out, isConnecting)) {
                return new ClusterAuthenticationResult(false, VERIFICATION_FAILED_MESSAGE);
            }
            out.writeInt(2);
            int version = in.readInt();
            if (version == -100) {
                return new ClusterAuthenticationResult(false, "Application version is incompatible. Zero Downtime Upgrades between this application version and older versions are not supported.");
            }
            if (version != 2) {
                return new ClusterAuthenticationResult(false, "Cannot form a cluster with nodes using different Confluence versions");
            }
            Nonce localNonce = this.generateNewNonce();
            log.trace("Generated: {}", (Object)localNonce);
            Nonce remoteNonce = new Nonce();
            Response response = new Response();
            if (isConnecting) {
                remoteNonce.readData(in);
                localNonce.writeData(out);
                response.readData(in);
                this.createResponse(localNonce, remoteNonce, request.getLocalAddress(), request.getLocalPort(), isConnecting).writeData(out);
            } else {
                localNonce.writeData(out);
                remoteNonce.readData(in);
                this.createResponse(localNonce, remoteNonce, request.getLocalAddress(), request.getLocalPort(), isConnecting).writeData(out);
                response.readData(in);
            }
            return this.verifyResponse(response, localNonce, remoteNonce, isConnecting, request.getRemoteAddress(), request.getRemotePort());
        }
        catch (IOException e) {
            return new ClusterAuthenticationResult(false, "Unexpected bytes from remote node, closing socket");
        }
    }

    private Response createResponse(Nonce localNonce, Nonce remoteNonce, String localAddress, int localPort, boolean isConnect) throws IOException {
        byte[] salt = this.generateSalt(localNonce.nonceBytes, remoteNonce.nonceBytes, localAddress, localPort, isConnect);
        byte[] key = this.generateKey(salt);
        Response response = new Response(key);
        log.debug("Created: {}", (Object)response);
        return response;
    }

    private Nonce generateNewNonce() {
        byte[] localNonce = new byte[16];
        RND.nextBytes(localNonce);
        return new Nonce(localNonce);
    }

    private ClusterAuthenticationResult verifyResponse(Response message, Nonce localNonce, Nonce remoteNonce, boolean isConnect, String address, int port) throws IOException {
        byte[] proof = message.proof;
        byte[] salt = this.generateSalt(remoteNonce.nonceBytes, localNonce.nonceBytes, address, port, !isConnect);
        byte[] key = this.generateKey(salt);
        if (log.isTraceEnabled()) {
            log.trace("Verification: remote proof: {}", (Object)Hex.toHexString((byte[])proof));
            log.trace("Verification: local proof:  {}", (Object)Hex.toHexString((byte[])key));
        }
        return new ClusterAuthenticationResult(ConstantTimeComparison.isEqual((byte[])key, (byte[])proof), VERIFICATION_FAILED_MESSAGE);
    }

    private boolean verifyGroupName(ObjectDataInput in, ObjectDataOutput out, boolean isConnecting) throws IOException {
        if (isConnecting) {
            out.writeUTF(this.groupName);
            String remoteGroup = in.readUTF();
            return this.groupName.equals(remoteGroup);
        }
        String remoteGroup = in.readUTF();
        boolean result = this.groupName.equals(remoteGroup);
        if (result) {
            out.writeUTF(this.groupName);
        } else {
            out.writeUTF("");
        }
        return result;
    }

    private static final class Response {
        private byte[] proof;

        public Response() {
        }

        Response(byte[] proof) {
            this.proof = proof;
        }

        public void readData(ObjectDataInput in) throws IOException {
            this.proof = SharedSecretClusterAuthenticator.readByteArray(in);
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("proof", (Object)Hex.toHexString((byte[])this.proof)).toString();
        }

        public void writeData(ObjectDataOutput out) throws IOException {
            SharedSecretClusterAuthenticator.writeByteArray(out, this.proof);
        }
    }

    private static final class Nonce {
        private byte[] nonceBytes;

        public Nonce() {
        }

        Nonce(byte[] nonceBytes) {
            this.nonceBytes = nonceBytes;
        }

        public void readData(ObjectDataInput in) throws IOException {
            this.nonceBytes = SharedSecretClusterAuthenticator.readByteArray(in);
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("nonce", (Object)Hex.toHexString((byte[])this.nonceBytes)).toString();
        }

        public void writeData(ObjectDataOutput out) throws IOException {
            SharedSecretClusterAuthenticator.writeByteArray(out, this.nonceBytes);
        }
    }
}

