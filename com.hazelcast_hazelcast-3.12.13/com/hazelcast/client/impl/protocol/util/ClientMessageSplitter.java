/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.util;

import com.hazelcast.client.impl.protocol.ClientMessage;
import java.util.ArrayList;
import java.util.List;

public final class ClientMessageSplitter {
    private ClientMessageSplitter() {
    }

    public static List<ClientMessage> getSubFrames(int maxFrameSize, ClientMessage clientMessage) {
        int numberOFSubFrames = ClientMessageSplitter.getNumberOfSubFrames(maxFrameSize, clientMessage);
        ArrayList<ClientMessage> messages = new ArrayList<ClientMessage>(numberOFSubFrames);
        for (int i = 0; i < numberOFSubFrames; ++i) {
            messages.add(ClientMessageSplitter.getSubFrame(maxFrameSize, i, numberOFSubFrames, clientMessage));
        }
        return messages;
    }

    static int getNumberOfSubFrames(int frameSize, ClientMessage originalClientMessage) {
        assert (ClientMessage.HEADER_SIZE < frameSize);
        int frameLength = originalClientMessage.getFrameLength();
        int sizeWithoutHeader = frameSize - ClientMessage.HEADER_SIZE;
        return (int)Math.ceil((float)(frameLength - ClientMessage.HEADER_SIZE) / (float)sizeWithoutHeader);
    }

    static ClientMessage getSubFrame(int frameSize, int frameIndex, int numberOfSubFrames, ClientMessage originalClientMessage) {
        assert (frameIndex > -1);
        assert (frameIndex < numberOfSubFrames);
        assert (ClientMessage.HEADER_SIZE < frameSize);
        int frameLength = originalClientMessage.getFrameLength();
        if (frameSize > frameLength) {
            assert (frameIndex == 0);
            return originalClientMessage;
        }
        int subFrameMaxDataLength = frameSize - ClientMessage.HEADER_SIZE;
        int startOffset = ClientMessage.HEADER_SIZE + frameIndex * subFrameMaxDataLength;
        int subFrameDataLength = numberOfSubFrames != frameIndex + 1 ? subFrameMaxDataLength : frameLength - startOffset;
        ClientMessage subFrame = ClientMessage.createForEncode(ClientMessage.HEADER_SIZE + subFrameDataLength);
        System.arraycopy(originalClientMessage.buffer.byteArray(), startOffset, subFrame.buffer.byteArray(), subFrame.getDataOffset(), subFrameDataLength);
        if (frameIndex == 0) {
            subFrame.addFlag((short)128);
        } else if (numberOfSubFrames == frameIndex + 1) {
            subFrame.addFlag((short)64);
        }
        subFrame.setPartitionId(originalClientMessage.getPartitionId());
        subFrame.setFrameLength(ClientMessage.HEADER_SIZE + subFrameDataLength);
        subFrame.setMessageType(originalClientMessage.getMessageType());
        subFrame.setRetryable(originalClientMessage.isRetryable());
        subFrame.setCorrelationId(originalClientMessage.getCorrelationId());
        return subFrame;
    }
}

