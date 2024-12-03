/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.POIReadOnlyDocument;
import org.apache.poi.hmef.attribute.MAPIRtfAttribute;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.datatypes.ByteChunk;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.Chunks;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.NameIdChunks;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.hsmf.datatypes.RecipientChunks;
import org.apache.poi.hsmf.datatypes.StringChunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.hsmf.parsers.POIFSChunkParser;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;

public class MAPIMessage
extends POIReadOnlyDocument {
    private static final Logger LOG = LogManager.getLogger(MAPIMessage.class);
    private static final Pattern GUESS_7_BIT_ENCODING_PATTERN = Pattern.compile("content-type:.*?charset=[\"']?([^;'\"]+)[\"']?", 2);
    private Chunks mainChunks;
    private NameIdChunks nameIdChunks;
    private RecipientChunks[] recipientChunks;
    private AttachmentChunks[] attachmentChunks;
    private boolean returnNullOnMissingChunk;

    public MAPIMessage() {
        super(new POIFSFileSystem());
    }

    public MAPIMessage(String filename) throws IOException {
        this(new File(filename));
    }

    public MAPIMessage(File file) throws IOException {
        this(new POIFSFileSystem(file));
    }

    public MAPIMessage(InputStream in) throws IOException {
        this(new POIFSFileSystem(in));
    }

    public MAPIMessage(POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }

    public MAPIMessage(DirectoryNode poifsDir) throws IOException {
        super(poifsDir);
        ChunkGroup[] chunkGroups = POIFSChunkParser.parse(poifsDir);
        ArrayList<AttachmentChunks> attachments = new ArrayList<AttachmentChunks>();
        ArrayList<RecipientChunks> recipients = new ArrayList<RecipientChunks>();
        for (ChunkGroup group : chunkGroups) {
            if (group instanceof Chunks) {
                this.mainChunks = (Chunks)group;
            } else if (group instanceof NameIdChunks) {
                this.nameIdChunks = (NameIdChunks)group;
            } else if (group instanceof RecipientChunks) {
                recipients.add((RecipientChunks)group);
            }
            if (!(group instanceof AttachmentChunks)) continue;
            attachments.add((AttachmentChunks)group);
        }
        this.attachmentChunks = attachments.toArray(new AttachmentChunks[0]);
        this.recipientChunks = recipients.toArray(new RecipientChunks[0]);
        Arrays.sort(this.attachmentChunks, new AttachmentChunks.AttachmentChunksSorter());
        Arrays.sort(this.recipientChunks, new RecipientChunks.RecipientChunksSorter());
    }

    public String getStringFromChunk(StringChunk chunk) throws ChunkNotFoundException {
        if (chunk == null) {
            if (this.returnNullOnMissingChunk) {
                return null;
            }
            throw new ChunkNotFoundException();
        }
        return chunk.getValue();
    }

    public String getTextBody() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getTextBodyChunk());
    }

    public String getHtmlBody() throws ChunkNotFoundException {
        ByteChunk htmlBodyBinaryChunk = this.mainChunks.getHtmlBodyChunkBinary();
        if (htmlBodyBinaryChunk != null) {
            List<PropertyValue> cpid = this.mainChunks.getProperties().get(MAPIProperty.INTERNET_CPID);
            if (cpid != null && !cpid.isEmpty()) {
                int codepage = ((PropertyValue.LongPropertyValue)cpid.get(0)).getValue();
                try {
                    String encoding = CodePageUtil.codepageToEncoding(codepage, true);
                    byte[] htmlBodyBinary = htmlBodyBinaryChunk.getValue();
                    return new String(htmlBodyBinary, encoding);
                }
                catch (UnsupportedEncodingException e) {
                    LOG.atWarn().log("HTML body binary: Invalid codepage ID {} set for the message via {}, ignoring", (Object)Unbox.box(codepage), (Object)MAPIProperty.INTERNET_CPID);
                }
            }
            return htmlBodyBinaryChunk.getAs7bitString();
        }
        return this.getStringFromChunk(this.mainChunks.getHtmlBodyChunkString());
    }

    public String getRtfBody() throws ChunkNotFoundException {
        ByteChunk chunk = this.mainChunks.getRtfBodyChunk();
        if (chunk == null) {
            if (this.returnNullOnMissingChunk) {
                return null;
            }
            throw new ChunkNotFoundException();
        }
        try {
            MAPIRtfAttribute rtf = new MAPIRtfAttribute(MAPIProperty.RTF_COMPRESSED, Types.BINARY.getId(), chunk.getValue());
            return rtf.getDataString();
        }
        catch (IOException e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    public String getSubject() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getSubjectChunk());
    }

    public String getDisplayFrom() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getDisplayFromChunk());
    }

    public String getDisplayTo() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getDisplayToChunk());
    }

    public String getDisplayCC() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getDisplayCCChunk());
    }

    public String getDisplayBCC() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getDisplayBCCChunk());
    }

    public String getRecipientEmailAddress() throws ChunkNotFoundException {
        return this.toSemicolonList(this.getRecipientEmailAddressList());
    }

    public String[] getRecipientEmailAddressList() throws ChunkNotFoundException {
        if (this.recipientChunks == null || this.recipientChunks.length == 0) {
            throw new ChunkNotFoundException("No recipients section present");
        }
        String[] emails = new String[this.recipientChunks.length];
        for (int i = 0; i < emails.length; ++i) {
            RecipientChunks rc = this.recipientChunks[i];
            String email = rc.getRecipientEmailAddress();
            if (email != null) {
                emails[i] = email;
                continue;
            }
            if (this.returnNullOnMissingChunk) {
                emails[i] = null;
                continue;
            }
            throw new ChunkNotFoundException("No email address holding chunks found for the " + (i + 1) + "th recipient");
        }
        return emails;
    }

    public String getRecipientNames() throws ChunkNotFoundException {
        return this.toSemicolonList(this.getRecipientNamesList());
    }

    public String[] getRecipientNamesList() throws ChunkNotFoundException {
        if (this.recipientChunks == null || this.recipientChunks.length == 0) {
            throw new ChunkNotFoundException("No recipients section present");
        }
        String[] names = new String[this.recipientChunks.length];
        for (int i = 0; i < names.length; ++i) {
            RecipientChunks rc = this.recipientChunks[i];
            String name = rc.getRecipientName();
            if (name == null) {
                throw new ChunkNotFoundException("No display name holding chunks found for the " + (i + 1) + "th recipient");
            }
            names[i] = name;
        }
        return names;
    }

    public void guess7BitEncoding() {
        String generalcodepage = null;
        String htmlbodycodepage = null;
        String bodycodepage = null;
        List<PropertyValue> val = this.mainChunks.getProperties().get(MAPIProperty.MESSAGE_CODEPAGE);
        if (val != null && !val.isEmpty()) {
            int codepage = ((PropertyValue.LongPropertyValue)val.get(0)).getValue();
            try {
                generalcodepage = CodePageUtil.codepageToEncoding(codepage, true);
            }
            catch (UnsupportedEncodingException e) {
                LOG.atWarn().log("Invalid codepage ID {} set for the message via {}, ignoring", (Object)Unbox.box(codepage), (Object)MAPIProperty.MESSAGE_CODEPAGE);
            }
        }
        if (generalcodepage == null && (val = this.mainChunks.getProperties().get(MAPIProperty.MESSAGE_LOCALE_ID)) != null && !val.isEmpty()) {
            int lcid = ((PropertyValue.LongPropertyValue)val.get(0)).getValue();
            int codepage = LocaleUtil.getDefaultCodePageFromLCID(lcid);
            try {
                if (codepage != 0) {
                    generalcodepage = CodePageUtil.codepageToEncoding(codepage, true);
                }
            }
            catch (UnsupportedEncodingException e) {
                LOG.atWarn().log("Invalid codepage ID {}from locale ID{} set for the message via {}, ignoring", (Object)Unbox.box(codepage), (Object)Unbox.box(lcid), (Object)MAPIProperty.MESSAGE_LOCALE_ID);
            }
        }
        if (generalcodepage == null) {
            try {
                String[] headers = this.getHeaders();
                if (headers != null && headers.length > 0) {
                    for (String header : headers) {
                        Matcher m;
                        if (!header.toLowerCase(LocaleUtil.getUserLocale()).startsWith("content-type") || !(m = GUESS_7_BIT_ENCODING_PATTERN.matcher(header)).matches()) continue;
                        generalcodepage = m.group(1);
                    }
                }
            }
            catch (ChunkNotFoundException headers) {
                // empty catch block
            }
        }
        if ((val = this.mainChunks.getProperties().get(MAPIProperty.INTERNET_CPID)) != null && !val.isEmpty()) {
            int codepage = ((PropertyValue.LongPropertyValue)val.get(0)).getValue();
            try {
                String encoding;
                htmlbodycodepage = encoding = CodePageUtil.codepageToEncoding(codepage, true);
                if (!encoding.equalsIgnoreCase("utf-8")) {
                    bodycodepage = encoding;
                }
            }
            catch (UnsupportedEncodingException e) {
                LOG.atWarn().log("Invalid codepage ID {} set for the message via {}, ignoring", (Object)Unbox.box(codepage), (Object)MAPIProperty.INTERNET_CPID);
            }
        }
        this.set7BitEncoding(generalcodepage, htmlbodycodepage, bodycodepage);
    }

    public void set7BitEncoding(String charset) {
        this.set7BitEncoding(charset, charset, charset);
    }

    public void set7BitEncoding(String generalcharset, String htmlbodycharset, String bodycharset) {
        for (Chunk chunk : this.mainChunks.getChunks()) {
            if (!(chunk instanceof StringChunk)) continue;
            if (chunk.getChunkId() == MAPIProperty.BODY_HTML.id) {
                if (htmlbodycharset == null) continue;
                ((StringChunk)chunk).set7BitEncoding(htmlbodycharset);
                continue;
            }
            if (chunk.getChunkId() == MAPIProperty.BODY.id) {
                if (bodycharset == null) continue;
                ((StringChunk)chunk).set7BitEncoding(bodycharset);
                continue;
            }
            if (generalcharset == null) continue;
            ((StringChunk)chunk).set7BitEncoding(generalcharset);
        }
        if (generalcharset != null) {
            if (this.nameIdChunks != null) {
                for (Chunk chunk : this.nameIdChunks.getChunks()) {
                    if (!(chunk instanceof StringChunk)) continue;
                    ((StringChunk)chunk).set7BitEncoding(generalcharset);
                }
            }
            for (RecipientChunks recipientChunks : this.recipientChunks) {
                for (Chunk c : recipientChunks.getAll()) {
                    if (!(c instanceof StringChunk)) continue;
                    ((StringChunk)c).set7BitEncoding(generalcharset);
                }
            }
        }
    }

    public boolean has7BitEncodingStrings() {
        for (Chunk chunk : this.mainChunks.getChunks()) {
            if (!(chunk instanceof StringChunk) || chunk.getType() != Types.ASCII_STRING) continue;
            return true;
        }
        if (this.nameIdChunks != null) {
            for (Chunk chunk : this.nameIdChunks.getChunks()) {
                if (!(chunk instanceof StringChunk) || chunk.getType() != Types.ASCII_STRING) continue;
                return true;
            }
        }
        for (RecipientChunks recipientChunks : this.recipientChunks) {
            for (Chunk c : recipientChunks.getAll()) {
                if (!(c instanceof StringChunk) || c.getType() != Types.ASCII_STRING) continue;
                return true;
            }
        }
        return false;
    }

    public String[] getHeaders() throws ChunkNotFoundException {
        String headers = this.getStringFromChunk(this.mainChunks.getMessageHeaders());
        if (headers == null) {
            return null;
        }
        return headers.split("\\r?\\n");
    }

    public String getConversationTopic() throws ChunkNotFoundException {
        return this.getStringFromChunk(this.mainChunks.getConversationTopic());
    }

    public MESSAGE_CLASS getMessageClassEnum() throws ChunkNotFoundException {
        String mc = this.getStringFromChunk(this.mainChunks.getMessageClass());
        if (StringUtil.isBlank(mc)) {
            return MESSAGE_CLASS.UNSPECIFIED;
        }
        if (mc.equalsIgnoreCase("IPM.Note")) {
            return MESSAGE_CLASS.NOTE;
        }
        if (mc.equalsIgnoreCase("IPM.Contact")) {
            return MESSAGE_CLASS.CONTACT;
        }
        if (mc.equalsIgnoreCase("IPM.Appointment")) {
            return MESSAGE_CLASS.APPOINTMENT;
        }
        if (mc.equalsIgnoreCase("IPM.StickyNote")) {
            return MESSAGE_CLASS.STICKY_NOTE;
        }
        if (mc.equalsIgnoreCase("IPM.Task")) {
            return MESSAGE_CLASS.TASK;
        }
        if (mc.equalsIgnoreCase("IPM.Post")) {
            return MESSAGE_CLASS.POST;
        }
        LOG.atWarn().log("I don't recognize message class '{}'. Please open an issue on POI's bugzilla", (Object)mc);
        return MESSAGE_CLASS.UNKNOWN;
    }

    public Calendar getMessageDate() throws ChunkNotFoundException {
        if (this.mainChunks.getSubmissionChunk() != null) {
            return this.mainChunks.getSubmissionChunk().getAcceptedAtTime();
        }
        for (MAPIProperty prop : new MAPIProperty[]{MAPIProperty.CLIENT_SUBMIT_TIME, MAPIProperty.LAST_MODIFICATION_TIME, MAPIProperty.CREATION_TIME}) {
            List<PropertyValue> val = this.mainChunks.getProperties().get(prop);
            if (val == null || val.isEmpty()) continue;
            return ((PropertyValue.TimePropertyValue)val.get(0)).getValue();
        }
        if (this.returnNullOnMissingChunk) {
            return null;
        }
        throw new ChunkNotFoundException();
    }

    public Chunks getMainChunks() {
        return this.mainChunks;
    }

    public RecipientChunks[] getRecipientDetailsChunks() {
        return this.recipientChunks;
    }

    public NameIdChunks getNameIdChunks() {
        return this.nameIdChunks;
    }

    public AttachmentChunks[] getAttachmentFiles() {
        return this.attachmentChunks;
    }

    public boolean isReturnNullOnMissingChunk() {
        return this.returnNullOnMissingChunk;
    }

    public void setReturnNullOnMissingChunk(boolean returnNullOnMissingChunk) {
        this.returnNullOnMissingChunk = returnNullOnMissingChunk;
    }

    private String toSemicolonList(String[] l) {
        StringBuilder list = new StringBuilder();
        boolean first = true;
        for (String s : l) {
            if (s == null) continue;
            if (first) {
                first = false;
            } else {
                list.append("; ");
            }
            list.append(s);
        }
        return list.toString();
    }

    public static enum MESSAGE_CLASS {
        APPOINTMENT,
        CONTACT,
        NOTE,
        POST,
        STICKY_NOTE,
        TASK,
        UNKNOWN,
        UNSPECIFIED;

    }
}

