/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.tools.VocabularyGenerator;
import com.sun.xml.fastinfoset.util.CharArrayArray;
import com.sun.xml.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

public class PrintTable {
    public static void printVocabulary(ParserVocabulary vocabulary) {
        PrintTable.printArray("Attribute Name Table", vocabulary.attributeName);
        PrintTable.printArray("Attribute Value Table", vocabulary.attributeValue);
        PrintTable.printArray("Character Content Chunk Table", vocabulary.characterContentChunk);
        PrintTable.printArray("Element Name Table", vocabulary.elementName);
        PrintTable.printArray("Local Name Table", vocabulary.localName);
        PrintTable.printArray("Namespace Name Table", vocabulary.namespaceName);
        PrintTable.printArray("Other NCName Table", vocabulary.otherNCName);
        PrintTable.printArray("Other String Table", vocabulary.otherString);
        PrintTable.printArray("Other URI Table", vocabulary.otherURI);
        PrintTable.printArray("Prefix Table", vocabulary.prefix);
    }

    public static void printArray(String title, StringArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, PrefixArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, CharArrayArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }

    public static void printArray(String title, ContiguousCharArrayArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getString(i));
        }
    }

    public static void printArray(String title, QualifiedNameArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            QualifiedName name = a.getArray()[i];
            System.out.println("" + (name.index + 1) + ": {" + name.namespaceName + "}" + name.prefix + ":" + name.localName);
        }
    }

    public static void main(String[] args) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            ParserVocabulary referencedVocabulary = new ParserVocabulary();
            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(referencedVocabulary);
            File f = new File(args[0]);
            saxParser.parse(f, (DefaultHandler)vocabularyGenerator);
            PrintTable.printVocabulary(referencedVocabulary);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

