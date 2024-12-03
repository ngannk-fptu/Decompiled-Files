/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment$Type
 */
package com.atlassian.confluence.plugins.viewfile.macro;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.viewfile.macro.FilePlaceholderSize;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ViewFileMacroUtils {
    private static final int ICON_AND_FILENAME_SPACE = 30;
    private static final Font font = new Font("Arial", 0, 14);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final String IMAGE_RESOURCE_PATH = "com/atlassian/confluence/plugins/viewfile/resources/images/";

    public static String getIconFileName(String fileType, FilePlaceholderSize filePlaceholderSize) {
        String iconName = FileTypeInfo.getFileTypeInfo(fileType).getIconName();
        filePlaceholderSize = filePlaceholderSize == null ? FilePlaceholderSize.MEDIUM : filePlaceholderSize;
        return "placeholder-" + filePlaceholderSize.name().toLowerCase() + "-" + iconName + ".png";
    }

    public static int getPlaceholderWidth(String fileType, FilePlaceholderSize filePlaceholderSize) {
        filePlaceholderSize = filePlaceholderSize == null ? FilePlaceholderSize.MEDIUM : filePlaceholderSize;
        FileTypeInfo fileTypeInfo = FileTypeInfo.getFileTypeInfo(fileType);
        switch (filePlaceholderSize) {
            case SMALL: {
                return fileTypeInfo.getSmallSize();
            }
            case MEDIUM: {
                return fileTypeInfo.getMediumSize();
            }
            case LARGE: {
                return fileTypeInfo.getLargeSize();
            }
        }
        return fileTypeInfo.getMediumSize();
    }

    public static String encodeToString(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();){
            ImageIO.write((RenderedImage)image, "png", bos);
            byte[] imageBytes = bos.toByteArray();
            String string = Base64.getEncoder().encodeToString(imageBytes);
            return string;
        }
    }

    public static BufferedImage getPlaceholderWithFileName(String fileName, String fileType, String fileHeight) throws IOException {
        InputStream imageStream = ViewFileMacroUtils.class.getClassLoader().getResourceAsStream(ViewFileMacroUtils.getImageResourcePath(fileType, fileHeight));
        BufferedImage bufferedImage = ImageIO.read(imageStream);
        ViewFileMacroUtils.drawFileNameOnImage(bufferedImage, fileName);
        return bufferedImage;
    }

    private static String getImageResourcePath(String fileType, String fileHeight) {
        return IMAGE_RESOURCE_PATH + ViewFileMacroUtils.getIconFileName(fileType, FilePlaceholderSize.from(fileHeight));
    }

    private static void drawFileNameOnImage(BufferedImage bufferedImage, String fileName) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);
        graphics.setColor(TEXT_COLOR);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int txtWidth = fontMetrics.stringWidth((String)fileName);
        int txtHeight = fontMetrics.getHeight();
        int imgWidth = bufferedImage.getWidth();
        int imgHeight = bufferedImage.getHeight();
        int xBuffer = 20;
        int eWidth = fontMetrics.stringWidth("...");
        if (txtWidth + xBuffer > imgWidth) {
            int length = ((String)fileName).length();
            int pos = (imgWidth - xBuffer - eWidth) / fontMetrics.getMaxAdvance() / 2;
            Object leading = ((String)fileName).substring(0, pos);
            Object trailing = ((String)fileName).substring(length - pos, length);
            while (fontMetrics.stringWidth((String)leading) + fontMetrics.stringWidth((String)trailing) + eWidth + xBuffer <= imgWidth) {
                leading = (String)leading + ((String)fileName).charAt(pos);
                trailing = ((String)fileName).charAt(length - pos - 1) + (String)trailing;
                ++pos;
            }
            fileName = (String)leading + "..." + (String)trailing;
        }
        int yPos = imgHeight / 2 + 30 + txtHeight - fontMetrics.getDescent();
        int xPos = imgWidth / 2 - fontMetrics.stringWidth((String)fileName) / 2;
        graphics.drawString((String)fileName, xPos, yPos);
    }

    private static enum FileTypeInfo {
        PDF(Attachment.Type.PDF.getDescription(), "pdf", 200, 333, 533),
        MULTIMEDIA(Attachment.Type.MULTIMEDIA.getDescription(), "multimedia", 267, 444, 711),
        XML(Attachment.Type.XML.getDescription(), "code", 150, 250, 400),
        HTML(Attachment.Type.HTML.getDescription(), "code", 150, 250, 400),
        TEXT(Attachment.Type.TEXT.getDescription(), "text", 107, 177, 284),
        WORD(Attachment.Type.WORD.getDescription(), "doc", 107, 177, 284),
        EXCEL(Attachment.Type.EXCEL.getDescription(), "spreadsheet", 150, 250, 400),
        POWERPOINT(Attachment.Type.POWERPOINT.getDescription(), "presentation", 200, 333, 533),
        JAVA_SOURCE(Attachment.Type.JAVA_SOURCE.getDescription(), "code", 150, 250, 400),
        JAVA_ARCHIVE(Attachment.Type.JAVA_ARCHIVE.getDescription(), "zip", 150, 250, 400),
        ZIP(Attachment.Type.ZIP.getDescription(), "zip", 150, 250, 400),
        UNKNOWN("unknown", "file", 150, 250, 400);

        private String description;
        private String iconName;
        private int smallSize;
        private int mediumSize;
        private int largeSize;

        private FileTypeInfo(String description, String iconName, int smallSize, int mediumSize, int largeSize) {
            this.description = description;
            this.iconName = iconName;
            this.smallSize = smallSize;
            this.mediumSize = mediumSize;
            this.largeSize = largeSize;
        }

        public String getDescription() {
            return this.description;
        }

        public String getIconName() {
            return this.iconName;
        }

        public int getSmallSize() {
            return this.smallSize;
        }

        public int getMediumSize() {
            return this.mediumSize;
        }

        public int getLargeSize() {
            return this.largeSize;
        }

        public static FileTypeInfo getFileTypeInfo(String description) {
            for (FileTypeInfo info : FileTypeInfo.values()) {
                if (!info.getDescription().equals(description)) continue;
                return info;
            }
            return UNKNOWN;
        }
    }
}

