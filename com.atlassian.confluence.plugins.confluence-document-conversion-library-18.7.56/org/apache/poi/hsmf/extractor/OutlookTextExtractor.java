/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.datatypes.StringChunk;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;

public class OutlookTextExtractor
implements POIOLE2TextExtractor {
    private final MAPIMessage msg;
    private boolean doCloseFilesystem = true;

    public OutlookTextExtractor(MAPIMessage msg) {
        this.msg = msg;
    }

    public OutlookTextExtractor(DirectoryNode poifsDir) throws IOException {
        this(new MAPIMessage(poifsDir));
    }

    public OutlookTextExtractor(POIFSFileSystem fs) throws IOException {
        this(new MAPIMessage(fs));
    }

    public OutlookTextExtractor(InputStream inp) throws IOException {
        this(new MAPIMessage(inp));
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: OutlookTextExtractor <file> [<file> ...]");
            System.exit(1);
        }
        for (String filename : args) {
            try (POIFSFileSystem poifs = new POIFSFileSystem(new File(filename));
                 OutlookTextExtractor extractor = new OutlookTextExtractor(poifs);){
                System.out.println(extractor.getText());
            }
        }
    }

    public MAPIMessage getMAPIMessage() {
        return this.msg;
    }

    @Override
    public String getText() {
        Iterator<String> emails;
        StringBuilder s = new StringBuilder();
        this.msg.guess7BitEncoding();
        try {
            emails = Arrays.asList(this.msg.getRecipientEmailAddressList()).iterator();
        }
        catch (ChunkNotFoundException e) {
            emails = Collections.emptyIterator();
        }
        try {
            s.append("From: ").append(this.msg.getDisplayFrom()).append("\n");
        }
        catch (ChunkNotFoundException e) {
            // empty catch block
        }
        try {
            this.handleEmails(s, "To", this.msg.getDisplayTo(), emails);
        }
        catch (ChunkNotFoundException e) {
            // empty catch block
        }
        try {
            this.handleEmails(s, "CC", this.msg.getDisplayCC(), emails);
        }
        catch (ChunkNotFoundException e) {
            // empty catch block
        }
        try {
            this.handleEmails(s, "BCC", this.msg.getDisplayBCC(), emails);
        }
        catch (ChunkNotFoundException e) {
            // empty catch block
        }
        try {
            SimpleDateFormat f = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.ROOT);
            f.setTimeZone(LocaleUtil.getUserTimeZone());
            s.append("Date: ").append(f.format(this.msg.getMessageDate().getTime())).append("\n");
        }
        catch (ChunkNotFoundException e) {
            try {
                String[] headers;
                for (String header : headers = this.msg.getHeaders()) {
                    if (!StringUtil.startsWithIgnoreCase(header, "date:")) continue;
                    s.append("Date:").append(header, header.indexOf(58) + 1, header.length()).append("\n");
                }
            }
            catch (ChunkNotFoundException chunkNotFoundException) {
                // empty catch block
            }
        }
        try {
            s.append("Subject: ").append(this.msg.getSubject()).append("\n");
        }
        catch (ChunkNotFoundException chunkNotFoundException) {
            // empty catch block
        }
        for (AttachmentChunks att : this.msg.getAttachmentFiles()) {
            String attName;
            StringChunk name = att.getAttachLongFileName();
            if (name == null) {
                name = att.getAttachFileName();
            }
            String string = attName = name == null ? null : name.getValue();
            if (att.getAttachMimeTag() != null && att.getAttachMimeTag().getValue() != null) {
                attName = att.getAttachMimeTag().getValue() + " = " + attName;
            }
            s.append("Attachment: ").append(attName).append("\n");
        }
        try {
            s.append("\n").append(this.msg.getTextBody()).append("\n");
        }
        catch (ChunkNotFoundException chunkNotFoundException) {
            // empty catch block
        }
        return s.toString();
    }

    protected void handleEmails(StringBuilder s, String type, String displayText, Iterator<String> emails) {
        if (displayText == null || displayText.length() == 0) {
            return;
        }
        String[] names = displayText.split(";\\s*");
        boolean first = true;
        s.append(type).append(": ");
        for (String name : names) {
            String email;
            if (first) {
                first = false;
            } else {
                s.append("; ");
            }
            s.append(name);
            if (!emails.hasNext() || (email = emails.next()).equals(name)) continue;
            s.append(" <").append(email).append(">");
        }
        s.append("\n");
    }

    @Override
    public MAPIMessage getDocument() {
        return this.msg;
    }

    @Override
    public void setCloseFilesystem(boolean doCloseFilesystem) {
        this.doCloseFilesystem = doCloseFilesystem;
    }

    @Override
    public boolean isCloseFilesystem() {
        return this.doCloseFilesystem;
    }

    @Override
    public MAPIMessage getFilesystem() {
        return this.msg;
    }
}

