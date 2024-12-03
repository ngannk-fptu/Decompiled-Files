/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.FileUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.misc;

import com.opensymphony.util.FileUtils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import org.apache.commons.lang3.StringUtils;

@Deprecated(forRemoval=true)
public class SampleWikiConverter {
    private static final String RESULT_DIR = "c:\\temp\\devwiki\\result\\";

    public static void main(String[] args) {
        try {
            System.out.println("Loading HSQL class");
            Class.forName("org.hsqldb.jdbcDriver");
            System.out.println("Getting connection");
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:c:\\temp\\devwiki\\SimpleWeb", "sa", "");
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * from PAGE");
            File resultDir = new File(RESULT_DIR);
            resultDir.mkdirs();
            System.out.println("rs = " + rs);
            while (rs.next()) {
                System.out.println("rs.getString(\"title\") = " + rs.getString("title"));
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp modified = rs.getTimestamp("modified");
                if (!StringUtils.isNotEmpty((CharSequence)title.trim())) continue;
                File f = new File(RESULT_DIR + SampleWikiConverter.encodeFilename(title) + ".txt");
                f.createNewFile();
                FileUtils.write((File)f, (String)SampleWikiConverter.encodeContent(content));
                f.setLastModified(modified.getTime());
            }
            rs.close();
            stat.close();
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encodeContent(String content) {
        content = content.replaceAll("&lt;br&gt;", "\\\\\\\\");
        content = content.replaceAll("&lt;li&gt;", "* ");
        content = content.replaceAll("&lt;ul&gt;", "\r\n&lt;ul&gt;");
        content = content.replaceAll("&lt;/ul&gt;", "&lt;/ul&gt;\r\n");
        content = content.replaceAll("&lt;hr&gt;", "----");
        content = content.replaceAll("&lt;h(\\d)&gt;(.*?)&lt;/h\\d&gt;", "h$1. $2");
        content = content.replaceAll("&lt;b&gt;(.*?)&lt;/b&gt;", "*$1*");
        content = content.replaceAll("&lt;i&gt;(.*?)&lt;/i&gt;", "_$1_");
        content = content.replaceAll("&lt;s&gt;(.*?)&lt;/s&gt;", "-$1-");
        content = content.replaceAll("&lt;a&gt;(.*?)&lt;/a&gt;", "[$1]");
        content = content.replaceAll("&lt;a href=&quot;(.*?)&quot;&gt;(.*?)&lt;/a&gt;", "$2 ($1)");
        content = content.replaceAll("&lt;p&gt;", "\r\n\r\n");
        return content;
    }

    private static String encodeFilename(String title) {
        title = StringUtils.replace((String)title, (String)" / ", (String)" and ");
        return StringUtils.replace((String)title, (String)"/", (String)" and ");
    }
}

