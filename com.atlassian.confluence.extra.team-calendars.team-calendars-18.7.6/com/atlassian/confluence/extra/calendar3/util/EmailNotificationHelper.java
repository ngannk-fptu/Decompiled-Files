/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.util.ByteArrayDataSource
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.mail.MailException;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringUtils;

public class EmailNotificationHelper {
    private static final String MIME_TYPE_IMAGE_PNG = "image/png";

    public static MimeBodyPart addImageBodyPart(String imageUrl, String fileName, String contentId) throws MailException, MessagingException, IOException {
        MimeBodyPart calendarIconBodyPart = new MimeBodyPart();
        calendarIconBodyPart.setDataHandler(new DataHandler(EmailNotificationHelper.createClassPathResourceDataSource(imageUrl, MIME_TYPE_IMAGE_PNG)));
        calendarIconBodyPart.setFileName(fileName);
        calendarIconBodyPart.setHeader("Content-ID", contentId);
        calendarIconBodyPart.setHeader("Content-Disposition", "inline; filename=" + fileName);
        return calendarIconBodyPart;
    }

    public static MimeBodyPart addImageBodyPart(DataHandler dataHandler, String fileName, String contentId) throws MailException, MessagingException, IOException {
        MimeBodyPart userAvatarBodyPart = new MimeBodyPart();
        userAvatarBodyPart.setDataHandler(dataHandler);
        userAvatarBodyPart.setFileName(fileName);
        userAvatarBodyPart.setHeader("Content-ID", contentId);
        userAvatarBodyPart.setHeader("Content-Disposition", "inline; filename=" + fileName);
        return userAvatarBodyPart;
    }

    public static DataSource createClassPathResourceDataSource(String resourcePath, String mimeType) throws IOException {
        try (InputStream classPathResourceInput = EmailNotificationHelper.class.getClassLoader().getResourceAsStream(resourcePath);){
            if (null == classPathResourceInput) {
                throw new IllegalArgumentException(String.format("Invalid class path resource specified: %s", resourcePath));
            }
            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(classPathResourceInput, mimeType);
            return byteArrayDataSource;
        }
    }

    public static String getSubIconUrl(String fullIconUrl) {
        if (StringUtils.isEmpty((CharSequence)fullIconUrl)) {
            return "";
        }
        String[] urlParts = fullIconUrl.split("/");
        if (urlParts == null || urlParts.length < 2) {
            return fullIconUrl;
        }
        return String.format("%s/%s", urlParts[urlParts.length - 2], urlParts[urlParts.length - 1]);
    }
}

