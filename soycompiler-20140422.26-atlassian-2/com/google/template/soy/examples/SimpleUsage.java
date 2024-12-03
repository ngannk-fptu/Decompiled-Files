/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Resources
 */
package com.google.template.soy.examples;

import com.google.common.io.Resources;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public class SimpleUsage {
    private static int numExamples = 0;

    private SimpleUsage() {
    }

    public static void main(String[] args) {
        SoyFileSet sfs = SoyFileSet.builder().add(Resources.getResource((String)"simple.soy")).build();
        SoyTofu tofu = sfs.compileToTofu();
        SimpleUsage.writeExampleHeader();
        System.out.println(tofu.newRenderer("soy.examples.simple.helloWorld").render());
        SoyTofu simpleTofu = tofu.forNamespace("soy.examples.simple");
        SimpleUsage.writeExampleHeader();
        System.out.println(simpleTofu.newRenderer(".helloName").setData(new SoyMapData("name", "Ana")).render());
        SimpleUsage.writeExampleHeader();
        System.out.println(simpleTofu.newRenderer(".helloNames").setData(new SoyMapData("names", new SoyListData("Bob", "Cid", "Dee"))).render());
    }

    private static void writeExampleHeader() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("[" + ++numExamples + "]");
    }
}

