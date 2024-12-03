/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapterParent
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.ContentEntityAdapterParent;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.mail.archive.content.ContentBackedMail;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.fugue.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailContentEntityAdapter
extends ContentEntityAdapterParent {
    private static final Logger log = LoggerFactory.getLogger(MailContentEntityAdapter.class);
    public static final String PLUGIN_CONTENT_KEY = "com.atlassian.confluence.plugins.confluence-mail-archiving:mail";

    public Option<String> getUrlPath(CustomContentEntityObject pluginContentEntityObject) {
        if (pluginContentEntityObject.getId() != 0L && pluginContentEntityObject.getSpace() != null) {
            return Option.some((Object)("/display/" + pluginContentEntityObject.getSpace().getKey() + "/mail/" + pluginContentEntityObject.getId()));
        }
        return Option.none();
    }

    public Option<String> getDisplayTitle(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    public Option<String> getNameForComparison(CustomContentEntityObject pluginContentEntityObject) {
        return Option.none();
    }

    public Option<String> getAttachmentsUrlPath(CustomContentEntityObject pluginContentEntityObject) {
        return Option.some((Object)((String)this.getUrlPath(pluginContentEntityObject).get() + "#attachments"));
    }

    public Option<String> getAttachmentUrlPath(CustomContentEntityObject pluginContentEntityObject, Attachment attachment) {
        return Option.some((Object)(GeneralUtil.appendAmpersandOrQuestionMark((String)((String)this.getUrlPath(pluginContentEntityObject).get())) + "highlight=" + GeneralUtil.urlEncode((String)attachment.getFileName()) + "#attachments"));
    }

    public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
        return BodyType.RAW;
    }

    public Option<String> getExcerpt(CustomContentEntityObject pluginContentEntityObject) {
        return Option.some((Object)GeneralUtil.makeFlatSummary((String)this.getUnquotedTextForSummary(pluginContentEntityObject)));
    }

    public String getUnquotedTextForSummary(CustomContentEntityObject pluginContentEntityObject) {
        ContentBackedMail mail = ContentBackedMail.newInstance(pluginContentEntityObject);
        StringBuilder buf = new StringBuilder();
        String lastLine = "";
        String lastTry = "";
        try {
            String line;
            String body = mail.getMessageBody();
            BufferedReader r = new BufferedReader(new StringReader(body));
            State state = State.START;
            boolean reading = true;
            while (reading && (line = r.readLine()) != null && !"--".equals(line = line.trim())) {
                switch (state) {
                    case START: {
                        if (line.length() == 0 || !Character.isLetterOrDigit(line.charAt(0))) break;
                        lastLine = line;
                        state = State.POSSIBLE;
                        break;
                    }
                    case POSSIBLE: {
                        if (line.length() == 0) break;
                        if (!Character.isLetterOrDigit(line.charAt(0))) {
                            state = State.NEXT;
                            break;
                        }
                        buf.append(lastLine).append(" ");
                        buf.append(line).append(" ");
                        state = State.UNTIL;
                        break;
                    }
                    case NEXT: {
                        if (line.length() == 0 || !Character.isLetterOrDigit(line.charAt(0))) break;
                        buf.append(line).append(" ");
                        state = State.UNTIL;
                        break;
                    }
                    case UNTIL: {
                        if (line.length() > 0 && !Character.isLetterOrDigit(line.charAt(0)) || line.length() == 0 && buf.length() > 80) {
                            if (buf.length() > 80) {
                                reading = false;
                            }
                            if (buf.length() > lastTry.length()) {
                                lastTry = buf.toString();
                            }
                            state = State.NEXT;
                            buf.setLength(0);
                            break;
                        }
                        buf.append(line).append(" ");
                    }
                }
            }
        }
        catch (IOException e) {
            log.info("Error getting unquoted mail text: " + e.getMessage());
        }
        if (lastTry.length() == 0 && lastLine.length() != 0) {
            lastTry = lastLine;
        }
        if (lastTry.length() < buf.length()) {
            lastTry = buf.toString();
        }
        if (lastTry.length() == 0) {
            return mail.getMessageBody();
        }
        return lastTry;
    }

    public static enum State {
        START,
        POSSIBLE,
        NEXT,
        UNTIL;

    }
}

