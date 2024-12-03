/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.viewfile.macro;

import com.atlassian.confluence.plugins.viewfile.macro.ViewFileMacroUtils;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class FilePlaceholderGeneratorServlet
extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileType = URLDecoder.decode((String)StringUtils.defaultIfBlank((CharSequence)req.getParameter("type"), (CharSequence)""), "UTF-8");
        String fileName = URLDecoder.decode((String)StringUtils.defaultIfBlank((CharSequence)req.getParameter("name"), (CharSequence)""), "UTF-8");
        String fileHeight = (String)StringUtils.defaultIfBlank((CharSequence)req.getParameter("height"), (CharSequence)"");
        try (ServletOutputStream outputStream = resp.getOutputStream();){
            BufferedImage bufferedImage = ViewFileMacroUtils.getPlaceholderWithFileName(fileName, fileType, fileHeight);
            resp.setContentType("image/png");
            ImageIO.write((RenderedImage)bufferedImage, "png", (OutputStream)outputStream);
        }
    }
}

