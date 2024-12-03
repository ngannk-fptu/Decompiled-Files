/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.engine.BaseRenderEngine;
import org.radeox.engine.context.BaseRenderContext;

public class InteractiveExample {
    private static DateFormat perfFormat = new SimpleDateFormat("m'm's's'S'ms'");

    public static void main(String[] args) {
        System.err.println("Radeox 0.8");
        System.err.println("Copyright (c) 2003 Stephan J. Schmidt, Matthias L. Jugel. \nAll Rights Reserved.");
        System.err.println("See License Agreement for terms and conditions of use.");
        BaseRenderEngine engine = new BaseRenderEngine();
        BaseRenderContext context = new BaseRenderContext();
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            if (inputFile.exists()) {
                InteractiveExample.batch(engine, context, inputFile);
            } else {
                System.err.println("The file '" + args[0] + "' does not exist.");
            }
        } else {
            InteractiveExample.interactive(engine, context);
        }
    }

    private static void batch(RenderEngine engine, RenderContext context, File inputFile) {
        StringBuffer input = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            char[] buffer = new char[4096];
            int length = 0;
            while ((length = reader.read(buffer)) != -1) {
                input.append(buffer, 0, length);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        InteractiveExample.render(engine, context, input.toString());
    }

    private static void interactive(RenderEngine engine, RenderContext context) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line;
            System.out.print("> ");
            System.out.flush();
            while ((line = reader.readLine()) != null) {
                InteractiveExample.render(engine, context, line);
                System.out.print("> ");
                System.out.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void render(RenderEngine engine, RenderContext context, String input) {
        long start = System.currentTimeMillis();
        String result = engine.render(input, context);
        long length = System.currentTimeMillis() - start;
        System.out.println(result);
        System.out.println("rendered in " + perfFormat.format(new Date(length)));
    }
}

