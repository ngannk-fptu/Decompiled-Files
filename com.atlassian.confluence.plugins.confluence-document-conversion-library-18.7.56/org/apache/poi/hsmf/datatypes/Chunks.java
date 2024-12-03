/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroupWithProperties;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.MessagePropertiesChunk;
import org.apache.poi.hsmf.datatypes.MessageSubmissionChunk;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.hsmf.datatypes.StringChunk;

public final class Chunks
implements ChunkGroupWithProperties {
    private static final Logger LOG = LogManager.getLogger(Chunks.class);
    private final Map<MAPIProperty, List<Chunk>> allChunks = new HashMap<MAPIProperty, List<Chunk>>();
    private final Map<Long, MAPIProperty> unknownProperties = new HashMap<Long, MAPIProperty>();
    private StringChunk messageClass;
    private StringChunk textBodyChunk;
    private StringChunk htmlBodyChunkString;
    private ByteChunk htmlBodyChunkBinary;
    private ByteChunk rtfBodyChunk;
    private StringChunk subjectChunk;
    private StringChunk displayToChunk;
    private StringChunk displayFromChunk;
    private StringChunk displayCCChunk;
    private StringChunk displayBCCChunk;
    private StringChunk conversationTopic;
    private StringChunk sentByServerType;
    private StringChunk messageHeaders;
    private MessageSubmissionChunk submissionChunk;
    private StringChunk emailFromChunk;
    private StringChunk messageId;
    private MessagePropertiesChunk messageProperties;

    @Override
    public Map<MAPIProperty, List<PropertyValue>> getProperties() {
        if (this.messageProperties != null) {
            return this.messageProperties.getProperties();
        }
        return Collections.emptyMap();
    }

    public Map<MAPIProperty, PropertyValue> getRawProperties() {
        if (this.messageProperties != null) {
            return this.messageProperties.getRawProperties();
        }
        return Collections.emptyMap();
    }

    public Map<MAPIProperty, List<Chunk>> getAll() {
        return this.allChunks;
    }

    @Override
    public Chunk[] getChunks() {
        ArrayList<Chunk> chunks = new ArrayList<Chunk>(this.allChunks.size());
        for (List<Chunk> c : this.allChunks.values()) {
            chunks.addAll(c);
        }
        return chunks.toArray(new Chunk[0]);
    }

    public StringChunk getMessageClass() {
        return this.messageClass;
    }

    public StringChunk getTextBodyChunk() {
        return this.textBodyChunk;
    }

    public StringChunk getHtmlBodyChunkString() {
        return this.htmlBodyChunkString;
    }

    public ByteChunk getHtmlBodyChunkBinary() {
        return this.htmlBodyChunkBinary;
    }

    public ByteChunk getRtfBodyChunk() {
        return this.rtfBodyChunk;
    }

    public StringChunk getSubjectChunk() {
        return this.subjectChunk;
    }

    public StringChunk getDisplayToChunk() {
        return this.displayToChunk;
    }

    public StringChunk getDisplayFromChunk() {
        return this.displayFromChunk;
    }

    public StringChunk getDisplayCCChunk() {
        return this.displayCCChunk;
    }

    public StringChunk getDisplayBCCChunk() {
        return this.displayBCCChunk;
    }

    public StringChunk getConversationTopic() {
        return this.conversationTopic;
    }

    public StringChunk getSentByServerType() {
        return this.sentByServerType;
    }

    public StringChunk getMessageHeaders() {
        return this.messageHeaders;
    }

    public MessageSubmissionChunk getSubmissionChunk() {
        return this.submissionChunk;
    }

    public StringChunk getEmailFromChunk() {
        return this.emailFromChunk;
    }

    public StringChunk getMessageId() {
        return this.messageId;
    }

    public MessagePropertiesChunk getMessageProperties() {
        return this.messageProperties;
    }

    @Override
    public void record(Chunk chunk) {
        long id;
        MAPIProperty prop = MAPIProperty.get(chunk.getChunkId());
        if (prop == MAPIProperty.UNKNOWN && (prop = this.unknownProperties.get(id = (long)(chunk.getChunkId() << 16) + (long)chunk.getType().getId())) == null) {
            prop = MAPIProperty.createCustom(chunk.getChunkId(), chunk.getType(), chunk.getEntryName());
            this.unknownProperties.put(id, prop);
        }
        if (prop == MAPIProperty.MESSAGE_CLASS) {
            this.messageClass = (StringChunk)chunk;
        } else if (prop == MAPIProperty.INTERNET_MESSAGE_ID) {
            this.messageId = (StringChunk)chunk;
        } else if (prop == MAPIProperty.MESSAGE_SUBMISSION_ID) {
            this.submissionChunk = (MessageSubmissionChunk)chunk;
        } else if (prop == MAPIProperty.RECEIVED_BY_ADDRTYPE) {
            this.sentByServerType = (StringChunk)chunk;
        } else if (prop == MAPIProperty.TRANSPORT_MESSAGE_HEADERS) {
            this.messageHeaders = (StringChunk)chunk;
        } else if (prop == MAPIProperty.CONVERSATION_TOPIC) {
            this.conversationTopic = (StringChunk)chunk;
        } else if (prop == MAPIProperty.SUBJECT) {
            this.subjectChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.DISPLAY_TO) {
            this.displayToChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.DISPLAY_CC) {
            this.displayCCChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.DISPLAY_BCC) {
            this.displayBCCChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.SENDER_EMAIL_ADDRESS) {
            this.emailFromChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.SENDER_NAME) {
            this.displayFromChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.BODY) {
            this.textBodyChunk = (StringChunk)chunk;
        } else if (prop == MAPIProperty.BODY_HTML) {
            if (chunk instanceof StringChunk) {
                this.htmlBodyChunkString = (StringChunk)chunk;
            }
            if (chunk instanceof ByteChunk) {
                this.htmlBodyChunkBinary = (ByteChunk)chunk;
            }
        } else if (prop == MAPIProperty.RTF_COMPRESSED) {
            this.rtfBodyChunk = (ByteChunk)chunk;
        } else if (chunk instanceof MessagePropertiesChunk) {
            this.messageProperties = (MessagePropertiesChunk)chunk;
        }
        this.allChunks.computeIfAbsent(prop, k -> new ArrayList());
        this.allChunks.get(prop).add(chunk);
    }

    @Override
    public void chunksComplete() {
        if (this.messageProperties != null) {
            this.messageProperties.matchVariableSizedPropertiesToChunks();
        } else {
            LOG.atWarn().log("Message didn't contain a root list of properties!");
        }
    }
}

