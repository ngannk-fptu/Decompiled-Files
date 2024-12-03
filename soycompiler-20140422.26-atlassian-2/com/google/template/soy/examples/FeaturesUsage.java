/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.io.Resources
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  org.kohsuke.args4j.CmdLineException
 *  org.kohsuke.args4j.CmdLineParser
 *  org.kohsuke.args4j.Option
 */
package com.google.template.soy.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.examples.FeaturesSoyInfo;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.xliffmsgplugin.XliffMsgPluginModule;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class FeaturesUsage {
    private static final String USAGE_PREFIX = "Usage:\njava com.google.template.soy.examples.FeaturesUsage [-locale <locale>]\n";
    private static final String XLIFF_RESOURCE_PREFIX = "examples_translated_";
    @Option(name="-locale", usage="The locale to render templates in. The corresponding XLIFF resource examples_translated_<locale>.xlf must exist. If not provided, the messages from the Soy source will be used.")
    private String locale = "";
    private int numExamples = 0;

    private FeaturesUsage() {
    }

    public static void main(String[] args) throws IOException {
        new FeaturesUsage().execMain(args);
    }

    private void execMain(String[] args) throws IOException {
        SoyMsgBundle msgBundle;
        CmdLineParser cmdLineParser = new CmdLineParser((Object)this);
        cmdLineParser.setUsageWidth(100);
        try {
            cmdLineParser.parseArgument(args);
        }
        catch (CmdLineException cle) {
            System.err.println("\nError: " + cle.getMessage() + "\n\n");
            System.err.println(USAGE_PREFIX);
            cmdLineParser.printUsage((OutputStream)System.err);
            System.exit(1);
        }
        Injector injector = Guice.createInjector((Module[])new Module[]{new SoyModule(), new XliffMsgPluginModule()});
        SoyFileSet.Builder sfsBuilder = (SoyFileSet.Builder)injector.getInstance(SoyFileSet.Builder.class);
        SoyFileSet sfs = sfsBuilder.add(Resources.getResource((String)"simple.soy")).add(Resources.getResource((String)"features.soy")).setCompileTimeGlobals(Resources.getResource((String)"FeaturesUsage_globals.txt")).build();
        SoyTofu tofu = sfs.compileToTofu().forNamespace("soy.examples.features");
        if (this.locale.length() > 0) {
            URL xliffResource;
            SoyMsgBundleHandler msgBundleHandler = (SoyMsgBundleHandler)injector.getInstance(SoyMsgBundleHandler.class);
            msgBundle = msgBundleHandler.createFromResource(xliffResource = Resources.getResource((String)(XLIFF_RESOURCE_PREFIX + this.locale + ".xlf")));
            if (msgBundle.getLocaleString() == null) {
                throw new IOException("Error reading message resource \"examples_translated_" + this.locale + ".xlf\".");
            }
        } else {
            msgBundle = null;
        }
        this.writeExampleHeader("demoComments");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_COMMENTS).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoLineJoining");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_LINE_JOINING).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoRawTextCommands");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_RAW_TEXT_COMMANDS).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoPrint");
        Object[] objectArray = new Object[4];
        objectArray[0] = "boo";
        objectArray[1] = "Boo!";
        objectArray[2] = "two";
        objectArray[3] = 2;
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_PRINT).setData(new SoyMapData(objectArray)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoPrintDirectives");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_PRINT_DIRECTIVES).setData((Map<String, ?>)ImmutableMap.of((Object)"longVarName", (Object)"thisIsSomeRidiculouslyLongVariableName", (Object)"elementId", (Object)"my_element_id", (Object)"cssClass", (Object)"my_css_class")).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoAutoescapeTrue");
        Object[] objectArray2 = new Object[2];
        objectArray2[0] = "italicHtml";
        objectArray2[1] = "<i>italic</i>";
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_AUTOESCAPE_TRUE).setData(new SoyMapData(objectArray2)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoAutoescapeFalse");
        Object[] objectArray3 = new Object[2];
        objectArray3[0] = "italicHtml";
        objectArray3[1] = "<i>italic</i>";
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_AUTOESCAPE_FALSE).setData(new SoyMapData(objectArray3)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoMsg");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_MSG).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Ed", (Object)"labsUrl", (Object)"http://labs.google.com")).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoIf");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_IF).setData(new SoyMapData("pi", 3.14159)).setMsgBundle(msgBundle).render());
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_IF).setData(new SoyMapData("pi", 2.71828)).setMsgBundle(msgBundle).render());
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_IF).setData(new SoyMapData("pi", 1.61803)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoSwitch");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_SWITCH).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Fay")).setMsgBundle(msgBundle).render());
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_SWITCH).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Go")).setMsgBundle(msgBundle).render());
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_SWITCH).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Hal")).setMsgBundle(msgBundle).render());
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_SWITCH).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Ivy")).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoForeach");
        SoyListData persons = new SoyListData();
        persons.add(new SoyMapData("name", "Jen", "numWaffles", 1));
        persons.add(new SoyMapData("name", "Kai", "numWaffles", 3));
        persons.add(new SoyMapData("name", "Lex", "numWaffles", 1));
        persons.add(new SoyMapData("name", "Mel", "numWaffles", 2));
        Object[] objectArray4 = new Object[2];
        objectArray4[0] = "persons";
        objectArray4[1] = persons;
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_FOREACH).setData(new SoyMapData(objectArray4)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoFor");
        Object[] objectArray5 = new Object[2];
        objectArray5[0] = "numLines";
        objectArray5[1] = 3;
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_FOR).setData(new SoyMapData(objectArray5)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoCallWithoutParam");
        Object[] objectArray6 = new Object[4];
        objectArray6[0] = "name";
        objectArray6[1] = "Neo";
        objectArray6[2] = "tripInfo";
        objectArray6[3] = new SoyMapData("name", "Neo", "destination", "The Matrix");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_CALL_WITHOUT_PARAM).setData(new SoyMapData(objectArray6)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoCallWithParam");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_CALL_WITH_PARAM).setData((Map<String, ?>)ImmutableMap.of((Object)"name", (Object)"Oz", (Object)"companionName", (Object)"Pip", (Object)"destinations", (Object)ImmutableList.of((Object)"Gillikin Country", (Object)"Munchkin Country", (Object)"Quadling Country", (Object)"Winkie Country"))).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoCallWithParamBlock");
        Object[] objectArray7 = new Object[2];
        objectArray7[0] = "name";
        objectArray7[1] = "Quo";
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_CALL_WITH_PARAM_BLOCK).setData(new SoyMapData(objectArray7)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoExpressions");
        SoyListData students = new SoyListData();
        students.add(new SoyMapData("name", "Rob", "major", "Physics", "year", 1999));
        students.add(new SoyMapData("name", "Sha", "major", "Finance", "year", 1980));
        students.add(new SoyMapData("name", "Tim", "major", "Engineering", "year", 2005));
        students.add(new SoyMapData("name", "Uma", "major", "Biology", "year", 1972));
        Object[] objectArray8 = new Object[4];
        objectArray8[0] = "students";
        objectArray8[1] = students;
        objectArray8[2] = "currentYear";
        objectArray8[3] = 2008;
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_EXPRESSIONS).setData(new SoyMapData(objectArray8)).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoDoubleBraces");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_DOUBLE_BRACES).setData((Map<String, ?>)ImmutableMap.of((Object)"setName", (Object)"prime numbers", (Object)"setMembers", (Object)ImmutableList.of((Object)2, (Object)3, (Object)5, (Object)7, (Object)11, (Object)13))).setMsgBundle(msgBundle).render());
        this.writeExampleHeader("demoBidiSupport");
        System.out.println(tofu.newRenderer(FeaturesSoyInfo.DEMO_BIDI_SUPPORT).setData((Map<String, ?>)ImmutableMap.of((Object)"title", (Object)"2008: A BiDi Odyssey", (Object)"author", (Object)"John Doe, Esq.", (Object)"year", (Object)"1973", (Object)"keywords", (Object)ImmutableList.of((Object)"Bi(Di)", (Object)"2008 (\u05e9\u05e0\u05d4)", (Object)"2008 (year)"))).setMsgBundle(msgBundle).render());
    }

    private void writeExampleHeader(String exampleName) {
        ++this.numExamples;
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("[%d. %s]\n", this.numExamples, exampleName);
    }
}

