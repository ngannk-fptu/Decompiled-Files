/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MonitoringConsoleHelperServlet
extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int count;
        InputStream instr = ((Object)((Object)this)).getClass().getClassLoader().getResourceAsStream("swf/dataTables/copy_csv_xls.swf");
        if (null == instr) {
            response.setStatus(404);
            return;
        }
        response.setContentType("application/x-shockwave-flash");
        ServletOutputStream outstr = response.getOutputStream();
        byte[] buf = new byte[1024];
        while ((count = instr.read(buf)) >= 0) {
            outstr.write(buf, 0, count);
        }
        instr.close();
        outstr.close();
    }
}

