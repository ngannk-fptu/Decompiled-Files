/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sisyphus;

import com.atlassian.sisyphus.DefaultMatchResultVisitor;
import com.atlassian.sisyphus.DefaultSisyphusDateMatcher;
import com.atlassian.sisyphus.DefaultSisyphusPatternMatcher;
import com.atlassian.sisyphus.LogLine;
import com.atlassian.sisyphus.PatternMatchSet;
import com.atlassian.sisyphus.RemoteXmlPatternSource;
import com.atlassian.sisyphus.SisyphusPattern;
import com.atlassian.sisyphus.SisyphusPatternSource;
import com.atlassian.sisyphus.SisyphusPatternSourceDecorator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public class SisyphusCLIScanner {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Required arguments:\n\tProduct names {confluence, jira, bamboo, crowd, fisheye, crucible etc.} - comma separated, no spaces\n\tLog file path (absolute path or relative path to current dir)");
            System.exit(-1);
        }
        SisyphusPatternSource patternSource = SisyphusCLIScanner.loadPatterns(args[0]);
        DefaultSisyphusPatternMatcher pm = new DefaultSisyphusPatternMatcher(patternSource, new DefaultSisyphusDateMatcher());
        String logFileLocation = args[1];
        System.out.println("Log file: " + logFileLocation);
        PrintingResultVisitor visitor = new PrintingResultVisitor();
        pm.match(new BufferedReader(new FileReader(new File(logFileLocation))), visitor, null);
        visitor.printResults();
    }

    private static SisyphusPatternSource loadPatterns(String products) throws IOException, ClassNotFoundException, MalformedURLException {
        Properties prop = SisyphusCLIScanner.getRunProperties();
        System.out.println("Products: " + products);
        StringTokenizer butcher = new StringTokenizer(products, ",");
        SisyphusPatternSourceDecorator source = new SisyphusPatternSourceDecorator();
        while (butcher.hasMoreTokens()) {
            String product = butcher.nextToken();
            String regexLocation = prop.getProperty(product + "_regex");
            if (StringUtils.isEmpty((CharSequence)regexLocation)) {
                regexLocation = "http://confluence.atlassian.com/download/attachments/179443532/" + product + "_regex.xml";
            }
            RemoteXmlPatternSource src = new RemoteXmlPatternSource(new URL(regexLocation));
            System.out.println("Product: '" + product + "' Regex: " + src.size() + " from: " + regexLocation);
            source.add(src);
        }
        if (source.size() == 0) {
            System.out.println("ERROR: no patterns were loaded for specified products.");
            System.out.println(-1);
        }
        return source;
    }

    public static Properties getRunProperties() throws IOException {
        String fileName = System.getProperty("scanner.properties", "scanner.properties");
        Properties prop = new Properties();
        InputStream propIn = null;
        if (!new File(fileName).exists()) {
            System.out.println("WARNING: Custom file not found: " + fileName);
            propIn = SisyphusCLIScanner.class.getClassLoader().getResourceAsStream("scanner.properties");
            if (propIn == null) {
                System.out.println("ERROR: Failed to find scanner.properties");
                System.exit(-1);
            }
        } else {
            propIn = new FileInputStream(fileName);
        }
        prop.load(propIn);
        prop.putAll((Map<?, ?>)System.getProperties());
        return prop;
    }

    private static final class PrintingResultVisitor
    extends DefaultMatchResultVisitor {
        private PrintingResultVisitor() {
        }

        @Override
        public void patternMatched(String line, LogLine datedLine, SisyphusPattern pattern) {
            System.out.println("Line " + datedLine.getLineNo() + ", date " + datedLine.getDate() + ":\n\t" + pattern.getPageName() + "\n\t" + pattern.getURL());
            super.patternMatched(line, datedLine, pattern);
        }

        @Override
        public boolean isCancelled() {
            return super.isCancelled();
        }

        public void printResults() {
            TreeSet<PatternMatchSet> set = new TreeSet<PatternMatchSet>(new Comparator<PatternMatchSet>(){

                @Override
                public int compare(PatternMatchSet o1, PatternMatchSet o2) {
                    return o2.getLastMatchedLine() - o1.getLastMatchedLine();
                }
            });
            set.addAll(this.getResults().values());
            System.out.println("\n\n#######################################################################\n\t\t\t\tReport\n#######################################################################");
            for (PatternMatchSet paternMatchSet : set) {
                SisyphusPattern pattern = paternMatchSet.getPattern();
                System.out.println("* " + pattern.getPageName() + "\n\t- " + pattern.getURL() + "\n\t- Last matched line: " + paternMatchSet.getLastMatchedLine() + "\n\t- Last matched date: " + paternMatchSet.getLastMatchedDate() + "\n");
            }
        }
    }
}

