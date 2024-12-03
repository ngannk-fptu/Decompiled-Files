/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LocaleUtil;

public class MessageSubmissionChunk
extends Chunk {
    private static final Logger LOG = LogManager.getLogger(MessageSubmissionChunk.class);
    private String rawId;
    private Calendar date;
    private static final Pattern datePatern = Pattern.compile("(\\d\\d)(\\d\\d)(\\d\\d)(\\d\\d)(\\d\\d)(\\d\\d)Z?");

    public MessageSubmissionChunk(String namePrefix, int chunkId, Types.MAPIType type) {
        super(namePrefix, chunkId, type);
    }

    public MessageSubmissionChunk(int chunkId, Types.MAPIType type) {
        super(chunkId, type);
    }

    @Override
    public void readValue(InputStream value) throws IOException {
        String[] parts;
        byte[] data = IOUtils.toByteArray(value);
        this.rawId = new String(data, StandardCharsets.US_ASCII);
        for (String part : parts = this.rawId.split(";")) {
            int datePartBegin;
            if (!part.startsWith("l=")) continue;
            String dateS = null;
            int numberPartBegin = part.lastIndexOf(45);
            if (numberPartBegin != -1 && (datePartBegin = part.lastIndexOf(45, numberPartBegin - 1)) != -1 && numberPartBegin > datePartBegin) {
                dateS = part.substring(datePartBegin + 1, numberPartBegin);
            }
            if (dateS == null) continue;
            Matcher m = datePatern.matcher(dateS);
            if (m.matches()) {
                int year;
                this.date = LocaleUtil.getLocaleCalendar();
                this.date.set(1, year + ((year = Integer.parseInt(m.group(1))) > 80 ? 1900 : 2000));
                this.date.set(2, Integer.parseInt(m.group(2)) - 1);
                this.date.set(5, Integer.parseInt(m.group(3)));
                this.date.set(11, Integer.parseInt(m.group(4)));
                this.date.set(12, Integer.parseInt(m.group(5)));
                this.date.set(13, Integer.parseInt(m.group(6)));
                this.date.clear(14);
                continue;
            }
            LOG.atWarn().log("Warning - unable to make sense of date {}", (Object)dateS);
        }
    }

    @Override
    public void writeValue(OutputStream out) throws IOException {
        byte[] data = this.rawId.getBytes(StandardCharsets.US_ASCII);
        out.write(data);
    }

    public Calendar getAcceptedAtTime() {
        return this.date;
    }

    public String getSubmissionId() {
        return this.rawId;
    }
}

