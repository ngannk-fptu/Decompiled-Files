/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.radeox.EngineManager;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class RegexpTest {
    public static void main(String[] args) {
        String file = args.length > 0 ? args[0] : "conf/wiki.txt";
        try {
            System.setOut(new PrintStream((OutputStream)System.out, true, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            // empty catch block
        }
        StringBuffer tmp = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(file), "UTF-8"));
            char[] buffer = new char[1024];
            int n = 0;
            while ((n = reader.read(buffer)) != -1) {
                tmp.append(buffer, 0, n);
            }
        }
        catch (Exception e) {
            System.err.println("File not found: " + e.getMessage());
        }
        String content = tmp.toString();
        System.out.println(content);
        BaseRenderContext context = new BaseRenderContext();
        RenderEngine engine = EngineManager.getInstance();
        System.out.println(engine.render(content, (RenderContext)context));
    }
}

