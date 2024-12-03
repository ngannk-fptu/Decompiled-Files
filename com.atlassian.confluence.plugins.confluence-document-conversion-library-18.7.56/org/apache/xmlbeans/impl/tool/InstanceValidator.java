/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.CommandLine;

public class InstanceValidator {
    public static void printUsage() {
        System.out.println("Validates the specified instance against the specified schema.");
        System.out.println("Contrast with the svalidate tool, which validates using a stream.");
        System.out.println("Usage: validate [-dl] [-nopvr] [-noupa] [-license] schema.xsd instance.xml");
        System.out.println("Options:");
        System.out.println("    -dl - permit network downloads for imports and includes (default is off)");
        System.out.println("    -noupa - do not enforce the unique particle attribution rule");
        System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
        System.out.println("    -strict - performs strict(er) validation");
        System.out.println("    -partial - allow partial schema type system");
        System.out.println("    -license - prints license information");
    }

    public static void main(String[] args) {
        System.exit(InstanceValidator.extraMain(args));
    }

    public static int extraMain(String[] args) {
        HashSet<String> flags = new HashSet<String>();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("strict");
        flags.add("partial");
        CommandLine cl = new CommandLine(args, flags, Collections.EMPTY_SET);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null || args.length < 1) {
            InstanceValidator.printUsage();
            return 0;
        }
        String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (int i = 0; i < badopts.length; ++i) {
                System.out.println("Unrecognized option: " + badopts[i]);
            }
            InstanceValidator.printUsage();
            return 0;
        }
        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            return 0;
        }
        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            return 0;
        }
        if (cl.args().length == 0) {
            return 0;
        }
        boolean dl = cl.getOpt("dl") != null;
        boolean nopvr = cl.getOpt("nopvr") != null;
        boolean noupa = cl.getOpt("noupa") != null;
        boolean strict = cl.getOpt("strict") != null;
        boolean partial = cl.getOpt("partial") != null;
        File[] schemaFiles = cl.filesEndingWith(".xsd");
        File[] instanceFiles = cl.filesEndingWith(".xml");
        File[] jarFiles = cl.filesEndingWith(".jar");
        ArrayList<XmlObject> sdocs = new ArrayList<XmlObject>();
        for (File schemaFile : schemaFiles) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFile, new XmlOptions().setLoadLineNumbers().setLoadMessageDigest()));
            }
            catch (Exception e) {
                System.err.println(schemaFile + " not loadable: " + e);
            }
        }
        XmlObject[] schemas = sdocs.toArray(new XmlObject[0]);
        SchemaTypeLoader sLoader = null;
        ArrayList<XmlError> compErrors = new ArrayList<XmlError>();
        XmlOptions schemaOptions = new XmlOptions();
        schemaOptions.setErrorListener(compErrors);
        if (dl) {
            schemaOptions.setCompileDownloadUrls();
        }
        if (nopvr) {
            schemaOptions.setCompileNoPvrRule();
        }
        if (noupa) {
            schemaOptions.setCompileNoUpaRule();
        }
        if (partial) {
            schemaOptions.setCompilePartialTypesystem();
        }
        if (jarFiles != null && jarFiles.length > 0) {
            sLoader = XmlBeans.typeLoaderForResource(XmlBeans.resourceLoaderForPath(jarFiles));
        }
        int returnCode = 0;
        try {
            if (schemas != null && schemas.length > 0) {
                sLoader = XmlBeans.compileXsd(schemas, sLoader, schemaOptions);
            }
        }
        catch (Exception e) {
            if (compErrors.isEmpty() || !(e instanceof XmlException)) {
                e.printStackTrace(System.err);
            }
            System.out.println("Schema invalid:" + (partial ? " couldn't recover from errors" : ""));
            for (XmlError compError : compErrors) {
                System.out.println(compError);
            }
            returnCode = 10;
            return returnCode;
        }
        if (partial && !compErrors.isEmpty()) {
            returnCode = 11;
            System.out.println("Schema invalid: partial schema type system recovered");
            for (XmlError compError : compErrors) {
                System.out.println(compError);
            }
        }
        if (sLoader == null) {
            sLoader = XmlBeans.getContextTypeLoader();
        }
        for (File instanceFile : instanceFiles) {
            XmlObject xobj;
            try {
                XmlOptions xo = new XmlOptions();
                xo.setLoadLineNumbersEndElement();
                xobj = sLoader.parse(instanceFile, null, xo);
            }
            catch (Exception e) {
                System.err.println(instanceFile + " not loadable: " + e);
                e.printStackTrace(System.err);
                continue;
            }
            ArrayList<XmlError> errors = new ArrayList<XmlError>();
            if (xobj.schemaType() == XmlObject.type) {
                System.out.println(instanceFile + " NOT valid.  ");
                System.out.println("  Document type not found.");
                continue;
            }
            if (xobj.validate(strict ? new XmlOptions().setErrorListener(errors).setValidateStrict() : new XmlOptions().setErrorListener(errors))) {
                System.out.println(instanceFile + " valid.");
                continue;
            }
            returnCode = 1;
            System.out.println(instanceFile + " NOT valid.");
            for (XmlError error : errors) {
                System.out.println(error);
            }
        }
        return returnCode;
    }
}

