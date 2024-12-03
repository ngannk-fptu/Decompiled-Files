/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroupWithProperties;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.PropertiesChunk;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.hsmf.datatypes.StringChunk;

public final class RecipientChunks
implements ChunkGroupWithProperties {
    private static final Logger LOG = LogManager.getLogger(RecipientChunks.class);
    public static final String PREFIX = "__recip_version1.0_#";
    public static final MAPIProperty RECIPIENT_NAME = MAPIProperty.DISPLAY_NAME;
    public static final MAPIProperty DELIVERY_TYPE = MAPIProperty.ADDRTYPE;
    public static final MAPIProperty RECIPIENT_EMAIL_ADDRESS = MAPIProperty.EMAIL_ADDRESS;
    public static final MAPIProperty RECIPIENT_SEARCH = MAPIProperty.SEARCH_KEY;
    public static final MAPIProperty RECIPIENT_SMTP_ADDRESS = MAPIProperty.SMTP_ADDRESS;
    public static final MAPIProperty RECIPIENT_DISPLAY_NAME = MAPIProperty.RECIPIENT_DISPLAY_NAME;
    private int recipientNumber = -1;
    private ByteChunk recipientSearchChunk;
    private StringChunk recipientNameChunk;
    private StringChunk recipientEmailChunk;
    private StringChunk recipientSMTPChunk;
    private StringChunk deliveryTypeChunk;
    private StringChunk recipientDisplayNameChunk;
    private PropertiesChunk recipientProperties;
    private List<Chunk> allChunks = new ArrayList<Chunk>();

    public RecipientChunks(String name) {
        int splitAt = name.lastIndexOf(35);
        if (splitAt > -1) {
            String number = name.substring(splitAt + 1);
            try {
                this.recipientNumber = Integer.parseInt(number, 16);
            }
            catch (NumberFormatException e) {
                LOG.atError().log("Invalid recipient number in name {}", (Object)name);
            }
        }
    }

    public int getRecipientNumber() {
        return this.recipientNumber;
    }

    public ByteChunk getRecipientSearchChunk() {
        return this.recipientSearchChunk;
    }

    public StringChunk getRecipientNameChunk() {
        return this.recipientNameChunk;
    }

    public StringChunk getRecipientEmailChunk() {
        return this.recipientEmailChunk;
    }

    public StringChunk getRecipientSMTPChunk() {
        return this.recipientSMTPChunk;
    }

    public StringChunk getDeliveryTypeChunk() {
        return this.deliveryTypeChunk;
    }

    public StringChunk getRecipientDisplayNameChunk() {
        return this.recipientDisplayNameChunk;
    }

    public String getRecipientName() {
        if (this.recipientNameChunk != null) {
            return this.recipientNameChunk.getValue();
        }
        if (this.recipientDisplayNameChunk != null) {
            return this.recipientDisplayNameChunk.getValue();
        }
        return null;
    }

    public String getRecipientEmailAddress() {
        String search;
        int idx;
        String name;
        if (this.recipientSMTPChunk != null) {
            return this.recipientSMTPChunk.getValue();
        }
        if (this.recipientEmailChunk != null) {
            String email = this.recipientEmailChunk.getValue();
            int cne = email.indexOf("/CN=");
            if (cne < 0) {
                return email;
            }
            return email.substring(cne + 4);
        }
        if (this.recipientNameChunk != null && (name = this.recipientNameChunk.getValue()).contains("@")) {
            if (name.startsWith("'") && name.endsWith("'")) {
                return name.substring(1, name.length() - 1);
            }
            return name;
        }
        if (this.recipientSearchChunk != null && (idx = (search = this.recipientSearchChunk.getAs7bitString()).indexOf("SMTP:")) >= 0) {
            return search.substring(idx + 5);
        }
        return null;
    }

    @Override
    public Map<MAPIProperty, List<PropertyValue>> getProperties() {
        if (this.recipientProperties != null) {
            return this.recipientProperties.getProperties();
        }
        return Collections.emptyMap();
    }

    public Chunk[] getAll() {
        return this.allChunks.toArray(new Chunk[0]);
    }

    @Override
    public Chunk[] getChunks() {
        return this.getAll();
    }

    @Override
    public void record(Chunk chunk) {
        if (chunk.getChunkId() == RecipientChunks.RECIPIENT_SEARCH.id) {
            this.recipientSearchChunk = (ByteChunk)chunk;
        } else if (chunk.getChunkId() == RecipientChunks.RECIPIENT_NAME.id) {
            this.recipientDisplayNameChunk = (StringChunk)chunk;
        } else if (chunk.getChunkId() == RecipientChunks.RECIPIENT_DISPLAY_NAME.id) {
            this.recipientNameChunk = (StringChunk)chunk;
        } else if (chunk.getChunkId() == RecipientChunks.RECIPIENT_EMAIL_ADDRESS.id) {
            this.recipientEmailChunk = (StringChunk)chunk;
        } else if (chunk.getChunkId() == RecipientChunks.RECIPIENT_SMTP_ADDRESS.id) {
            this.recipientSMTPChunk = (StringChunk)chunk;
        } else if (chunk.getChunkId() == RecipientChunks.DELIVERY_TYPE.id) {
            this.deliveryTypeChunk = (StringChunk)chunk;
        } else if (chunk instanceof PropertiesChunk) {
            this.recipientProperties = (PropertiesChunk)chunk;
        }
        this.allChunks.add(chunk);
    }

    @Override
    public void chunksComplete() {
        if (this.recipientProperties != null) {
            this.recipientProperties.matchVariableSizedPropertiesToChunks();
        } else {
            LOG.atWarn().log("Recipients Chunk didn't contain a list of properties!");
        }
    }

    public static class RecipientChunksSorter
    implements Comparator<RecipientChunks>,
    Serializable {
        @Override
        public int compare(RecipientChunks a, RecipientChunks b) {
            return Integer.compare(a.recipientNumber, b.recipientNumber);
        }
    }
}

