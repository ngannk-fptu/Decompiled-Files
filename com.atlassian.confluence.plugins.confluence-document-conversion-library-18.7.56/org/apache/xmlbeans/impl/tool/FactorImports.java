/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.tool.CodeGenUtil;
import org.apache.xmlbeans.impl.tool.CommandLine;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;

public class FactorImports {
    public static void printUsage() {
        System.out.println("Refactors a directory of XSD files to remove name conflicts.");
        System.out.println("Usage: sfactor [-import common.xsd] [-out outputdir] inputdir");
        System.out.println("    -import common.xsd - The XSD file to contain redundant ");
        System.out.println("                         definitions for importing.");
        System.out.println("    -out outputdir - The directory into which to place XSD ");
        System.out.println("                     files resulting from refactoring, ");
        System.out.println("                     plus a commonly imported common.xsd.");
        System.out.println("    inputdir - The directory containing the XSD files with");
        System.out.println("               redundant definitions.");
        System.out.println("    -license - Print license information.");
        System.out.println();
    }

    /*
     * WARNING - void declaration
     */
    public static void main(String[] args) throws Exception {
        String targetNamespace;
        String out;
        HashSet<String> flags = new HashSet<String>();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        CommandLine cl = new CommandLine(args, flags, Arrays.asList("import", "out"));
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null || args.length < 1) {
            FactorImports.printUsage();
            System.exit(0);
            return;
        }
        String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (String badopt : badopts) {
                System.out.println("Unrecognized option: " + badopt);
            }
            FactorImports.printUsage();
            System.exit(0);
            return;
        }
        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            System.exit(0);
            return;
        }
        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            System.exit(0);
            return;
        }
        args = cl.args();
        if (args.length != 1) {
            System.exit(0);
            return;
        }
        String commonName = cl.getOpt("import");
        if (commonName == null) {
            commonName = "common.xsd";
        }
        if ((out = cl.getOpt("out")) == null) {
            System.out.println("Using output directory 'out'");
            out = "out";
        }
        File outdir = new File(out);
        File basedir = new File(args[0]);
        File[] files = cl.getFiles();
        HashMap<SchemaDocument, File> schemaDocs = new HashMap<SchemaDocument, File>();
        HashSet<QName> elementNames = new HashSet<QName>();
        HashSet<QName> attributeNames = new HashSet<QName>();
        HashSet<QName> typeNames = new HashSet<QName>();
        HashSet<QName> modelGroupNames = new HashSet<QName>();
        HashSet<QName> attrGroupNames = new HashSet<QName>();
        HashSet<QName> dupeElementNames = new HashSet<QName>();
        HashSet<QName> dupeAttributeNames = new HashSet<QName>();
        HashSet<QName> dupeTypeNames = new HashSet<QName>();
        HashSet<QName> dupeModelGroupNames = new HashSet<QName>();
        HashSet<QName> dupeAttrGroupNames = new HashSet<QName>();
        HashSet<String> dupeNamespaces = new HashSet<String>();
        for (File file : files) {
            try {
                NamedAttributeGroup[] namedAttributeGroupArray;
                NamedGroup[] namedGroupArray;
                void var33_69;
                TopLevelAttribute[] at;
                void var32_58;
                TopLevelElement[] el;
                void var31_47;
                TopLevelSimpleType[] st;
                TopLevelComplexType[] ct;
                SchemaDocument doc = (SchemaDocument)SchemaDocument.Factory.parse(file);
                schemaDocs.put(doc, file);
                if (doc.getSchema().sizeOfImportArray() > 0 || doc.getSchema().sizeOfIncludeArray() > 0) {
                    System.out.println("warning: " + file + " contains imports or includes that are being ignored.");
                }
                if ((targetNamespace = doc.getSchema().getTargetNamespace()) == null) {
                    targetNamespace = "";
                }
                for (TopLevelComplexType topLevelComplexType : ct = doc.getSchema().getComplexTypeArray()) {
                    FactorImports.noteName(topLevelComplexType.getName(), targetNamespace, typeNames, dupeTypeNames, dupeNamespaces);
                }
                TopLevelSimpleType[] topLevelSimpleTypeArray = st = doc.getSchema().getSimpleTypeArray();
                int n = topLevelSimpleTypeArray.length;
                boolean bl = false;
                while (var31_47 < n) {
                    TopLevelSimpleType topLevelSimpleType = topLevelSimpleTypeArray[var31_47];
                    FactorImports.noteName(topLevelSimpleType.getName(), targetNamespace, typeNames, dupeTypeNames, dupeNamespaces);
                    ++var31_47;
                }
                TopLevelElement[] topLevelElementArray = el = doc.getSchema().getElementArray();
                int n2 = topLevelElementArray.length;
                boolean bl2 = false;
                while (var32_58 < n2) {
                    TopLevelElement topLevelElement = topLevelElementArray[var32_58];
                    FactorImports.noteName(topLevelElement.getName(), targetNamespace, elementNames, dupeElementNames, dupeNamespaces);
                    ++var32_58;
                }
                TopLevelAttribute[] topLevelAttributeArray = at = doc.getSchema().getAttributeArray();
                int n3 = topLevelAttributeArray.length;
                boolean bl3 = false;
                while (var33_69 < n3) {
                    TopLevelAttribute topLevelAttribute = topLevelAttributeArray[var33_69];
                    FactorImports.noteName(topLevelAttribute.getName(), targetNamespace, attributeNames, dupeAttributeNames, dupeNamespaces);
                    ++var33_69;
                }
                for (NamedGroup namedGroup : namedGroupArray = doc.getSchema().getGroupArray()) {
                    FactorImports.noteName(namedGroup.getName(), targetNamespace, modelGroupNames, dupeModelGroupNames, dupeNamespaces);
                }
                for (NamedAttributeGroup namedAttributeGroup : namedAttributeGroupArray = doc.getSchema().getAttributeGroupArray()) {
                    FactorImports.noteName(namedAttributeGroup.getName(), targetNamespace, attrGroupNames, dupeAttrGroupNames, dupeNamespaces);
                }
            }
            catch (XmlException e) {
                System.out.println("warning: " + file + " is not a schema file - " + e.getError().toString());
            }
            catch (IOException e) {
                System.err.println("Unable to load " + file + " - " + e.getMessage());
                System.exit(1);
                return;
            }
        }
        if (schemaDocs.size() == 0) {
            System.out.println("No schema files found.");
            System.exit(0);
            return;
        }
        if (dupeTypeNames.size() + dupeElementNames.size() + dupeAttributeNames.size() + dupeModelGroupNames.size() + dupeAttrGroupNames.size() == 0) {
            System.out.println("No duplicate names found.");
            System.exit(0);
            return;
        }
        HashMap<String, SchemaDocument> commonDocs = new HashMap<String, SchemaDocument>();
        HashMap<SchemaDocument, File> commonFiles = new HashMap<SchemaDocument, File>();
        int count = dupeNamespaces.size() == 1 ? 0 : 1;
        for (String namespace : dupeNamespaces) {
            SchemaDocument commonDoc = (SchemaDocument)SchemaDocument.Factory.parse("<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'/>");
            if (namespace.length() > 0) {
                commonDoc.getSchema().setTargetNamespace(namespace);
            }
            commonDoc.getSchema().setElementFormDefault(FormChoice.QUALIFIED);
            commonDocs.put(namespace, commonDoc);
            commonFiles.put(commonDoc, FactorImports.commonFileFor(commonName, namespace, count++, outdir));
        }
        for (SchemaDocument doc : schemaDocs.keySet()) {
            void var33_73;
            void var32_63;
            void var31_52;
            targetNamespace = doc.getSchema().getTargetNamespace();
            if (targetNamespace == null) {
                targetNamespace = "";
            }
            SchemaDocument commonDoc = (SchemaDocument)commonDocs.get(targetNamespace);
            boolean needImport = false;
            TopLevelComplexType[] ct = doc.getSchema().getComplexTypeArray();
            for (int j = ct.length - 1; j >= 0; --j) {
                if (!FactorImports.isDuplicate(ct[j].getName(), targetNamespace, dupeTypeNames)) continue;
                if (FactorImports.isFirstDuplicate(ct[j].getName(), targetNamespace, typeNames, dupeTypeNames)) {
                    commonDoc.getSchema().addNewComplexType().set(ct[j]);
                }
                needImport = true;
                doc.getSchema().removeComplexType(j);
            }
            TopLevelSimpleType[] st = doc.getSchema().getSimpleTypeArray();
            boolean bl = false;
            while (var31_52 < st.length) {
                if (FactorImports.isDuplicate(st[var31_52].getName(), targetNamespace, dupeTypeNames)) {
                    if (FactorImports.isFirstDuplicate(st[var31_52].getName(), targetNamespace, typeNames, dupeTypeNames)) {
                        commonDoc.getSchema().addNewSimpleType().set(st[var31_52]);
                    }
                    needImport = true;
                    doc.getSchema().removeSimpleType((int)var31_52);
                }
                ++var31_52;
            }
            TopLevelElement[] topLevelElementArray = doc.getSchema().getElementArray();
            boolean bl4 = false;
            while (var32_63 < topLevelElementArray.length) {
                if (FactorImports.isDuplicate(topLevelElementArray[var32_63].getName(), targetNamespace, dupeElementNames)) {
                    if (FactorImports.isFirstDuplicate(topLevelElementArray[var32_63].getName(), targetNamespace, elementNames, dupeElementNames)) {
                        commonDoc.getSchema().addNewElement().set(topLevelElementArray[var32_63]);
                    }
                    needImport = true;
                    doc.getSchema().removeElement((int)var32_63);
                }
                ++var32_63;
            }
            TopLevelAttribute[] topLevelAttributeArray = doc.getSchema().getAttributeArray();
            boolean bl5 = false;
            while (var33_73 < topLevelAttributeArray.length) {
                if (FactorImports.isDuplicate(topLevelAttributeArray[var33_73].getName(), targetNamespace, dupeAttributeNames)) {
                    if (FactorImports.isFirstDuplicate(topLevelAttributeArray[var33_73].getName(), targetNamespace, attributeNames, dupeAttributeNames)) {
                        commonDoc.getSchema().addNewElement().set(topLevelAttributeArray[var33_73]);
                    }
                    needImport = true;
                    doc.getSchema().removeElement((int)var33_73);
                }
                ++var33_73;
            }
            NamedGroup[] namedGroupArray = doc.getSchema().getGroupArray();
            for (int j = 0; j < namedGroupArray.length; ++j) {
                if (!FactorImports.isDuplicate(namedGroupArray[j].getName(), targetNamespace, dupeModelGroupNames)) continue;
                if (FactorImports.isFirstDuplicate(namedGroupArray[j].getName(), targetNamespace, modelGroupNames, dupeModelGroupNames)) {
                    commonDoc.getSchema().addNewElement().set(namedGroupArray[j]);
                }
                needImport = true;
                doc.getSchema().removeElement(j);
            }
            NamedAttributeGroup[] ag = doc.getSchema().getAttributeGroupArray();
            for (int j = 0; j < ag.length; ++j) {
                if (!FactorImports.isDuplicate(ag[j].getName(), targetNamespace, dupeAttrGroupNames)) continue;
                if (FactorImports.isFirstDuplicate(ag[j].getName(), targetNamespace, attrGroupNames, dupeAttrGroupNames)) {
                    commonDoc.getSchema().addNewElement().set(ag[j]);
                }
                needImport = true;
                doc.getSchema().removeElement(j);
            }
            if (!needImport) continue;
            IncludeDocument.Include newInclude = doc.getSchema().addNewInclude();
            File inputFile = (File)schemaDocs.get(doc);
            File outputFile = FactorImports.outputFileFor(inputFile, basedir, outdir);
            File commonFile = (File)commonFiles.get(commonDoc);
            if (targetNamespace == null) continue;
            newInclude.setSchemaLocation(FactorImports.relativeURIFor(outputFile, commonFile));
        }
        if (!outdir.isDirectory() && !outdir.mkdirs()) {
            System.err.println("Unable to makedir " + outdir);
            System.exit(1);
            return;
        }
        for (SchemaDocument doc : schemaDocs.keySet()) {
            File inputFile = (File)schemaDocs.get(doc);
            File outputFile = FactorImports.outputFileFor(inputFile, basedir, outdir);
            if (outputFile == null) {
                System.out.println("Cannot copy " + inputFile);
                continue;
            }
            doc.save(outputFile, new XmlOptions().setSavePrettyPrint().setSaveAggressiveNamespaces());
        }
        for (SchemaDocument doc : commonFiles.keySet()) {
            File outputFile = (File)commonFiles.get(doc);
            doc.save(outputFile, new XmlOptions().setSavePrettyPrint().setSaveAggressiveNamespaces());
        }
    }

    private static File outputFileFor(File file, File baseDir, File outdir) {
        URI abs;
        URI base = baseDir.getAbsoluteFile().toURI();
        URI rel = base.relativize(abs = file.getAbsoluteFile().toURI());
        if (rel.isAbsolute()) {
            System.out.println("Cannot relativize " + file);
            return null;
        }
        URI outbase = outdir.toURI();
        URI out = CodeGenUtil.resolve(outbase, rel);
        return new File(out);
    }

    private static URI commonAncestor(URI first, URI second) {
        int i;
        String firstStr = first.toString();
        String secondStr = second.toString();
        int len = firstStr.length();
        if (secondStr.length() < len) {
            len = secondStr.length();
        }
        for (i = 0; i < len && firstStr.charAt(i) == secondStr.charAt(i); ++i) {
        }
        if (--i >= 0) {
            i = firstStr.lastIndexOf(47, i);
        }
        if (i < 0) {
            return null;
        }
        try {
            return new URI(firstStr.substring(0, i));
        }
        catch (URISyntaxException e) {
            return null;
        }
    }

    private static String relativeURIFor(File source, File target) {
        URI abs;
        URI base = source.getAbsoluteFile().toURI();
        URI commonBase = FactorImports.commonAncestor(base, abs = target.getAbsoluteFile().toURI());
        if (commonBase == null) {
            return abs.toString();
        }
        URI baserel = commonBase.relativize(base);
        URI targetrel = commonBase.relativize(abs);
        if (baserel.isAbsolute() || targetrel.isAbsolute()) {
            return abs.toString();
        }
        String prefix = "";
        String sourceRel = baserel.toString();
        for (int i = 0; i < sourceRel.length() && (i = sourceRel.indexOf(47, i)) >= 0; ++i) {
            prefix = prefix + "../";
        }
        return prefix + targetrel.toString();
    }

    private static File commonFileFor(String commonName, String namespace, int i, File outdir) {
        String name = commonName;
        if (i > 0) {
            int index = commonName.lastIndexOf(46);
            if (index < 0) {
                index = commonName.length();
            }
            name = commonName.substring(0, index) + i + commonName.substring(index);
        }
        return new File(outdir, name);
    }

    private static void noteName(String name, String targetNamespace, Set<QName> seen, Set<QName> dupes, Set<String> dupeNamespaces) {
        if (name == null) {
            return;
        }
        QName qName = new QName(targetNamespace, name);
        if (seen.contains(qName)) {
            dupes.add(qName);
            dupeNamespaces.add(targetNamespace);
        } else {
            seen.add(qName);
        }
    }

    private static boolean isFirstDuplicate(String name, String targetNamespace, Set<QName> notseen, Set<QName> dupes) {
        if (name == null) {
            return false;
        }
        QName qName = new QName(targetNamespace, name);
        if (dupes.contains(qName) && notseen.contains(qName)) {
            notseen.remove(qName);
            return true;
        }
        return false;
    }

    private static boolean isDuplicate(String name, String targetNamespace, Set<QName> dupes) {
        if (name == null) {
            return false;
        }
        QName qName = new QName(targetNamespace, name);
        return dupes.contains(qName);
    }
}

