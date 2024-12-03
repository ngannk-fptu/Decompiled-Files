/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.Version
 */
package org.apache.felix.bundlerepository;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.felix.bundlerepository.FileUtil;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

public class ObrCommandImpl
implements Command {
    private static final String HELP_CMD = "help";
    private static final String ADDURL_CMD = "add-url";
    private static final String REMOVEURL_CMD = "remove-url";
    private static final String LISTURL_CMD = "list-url";
    private static final String REFRESHURL_CMD = "refresh-url";
    private static final String LIST_CMD = "list";
    private static final String INFO_CMD = "info";
    private static final String DEPLOY_CMD = "deploy";
    private static final String START_CMD = "start";
    private static final String SOURCE_CMD = "source";
    private static final String JAVADOC_CMD = "javadoc";
    private static final String EXTRACT_SWITCH = "-x";
    private static final String VERBOSE_SWITCH = "-v";
    private BundleContext m_context = null;
    private RepositoryAdmin m_repoAdmin = null;

    public ObrCommandImpl(BundleContext context, RepositoryAdmin repoAdmin) {
        this.m_context = context;
        this.m_repoAdmin = repoAdmin;
    }

    public String getName() {
        return "obr";
    }

    public String getUsage() {
        return "obr help";
    }

    public String getShortDescription() {
        return "OSGi bundle repository.";
    }

    public synchronized void execute(String commandLine, PrintStream out, PrintStream err) {
        try {
            StringTokenizer st = new StringTokenizer(commandLine);
            st.nextToken();
            String command = HELP_CMD;
            try {
                command = st.nextToken();
            }
            catch (Exception ex) {
                // empty catch block
            }
            if (command == null || command.equals(HELP_CMD)) {
                this.help(out, st);
            } else if (command.equals(ADDURL_CMD) || command.equals(REFRESHURL_CMD) || command.equals(REMOVEURL_CMD) || command.equals(LISTURL_CMD)) {
                this.urls(commandLine, command, out, err);
            } else if (command.equals(LIST_CMD)) {
                this.list(commandLine, command, out, err);
            } else if (command.equals(INFO_CMD)) {
                this.info(commandLine, command, out, err);
            } else if (command.equals(DEPLOY_CMD) || command.equals(START_CMD)) {
                this.deploy(commandLine, command, out, err);
            } else if (command.equals(SOURCE_CMD)) {
                this.source(commandLine, command, out, err);
            } else if (command.equals(JAVADOC_CMD)) {
                this.javadoc(commandLine, command, out, err);
            } else {
                err.println("Unknown command: " + command);
            }
        }
        catch (InvalidSyntaxException ex) {
            err.println("Syntax error: " + ex.getMessage());
        }
        catch (IOException ex) {
            err.println("Error: " + ex);
        }
    }

    private void urls(String commandLine, String command, PrintStream out, PrintStream err) throws IOException {
        StringTokenizer st = new StringTokenizer(commandLine);
        st.nextToken();
        st.nextToken();
        int count = st.countTokens();
        if (count > 0) {
            while (st.hasMoreTokens()) {
                if (command.equals(ADDURL_CMD)) {
                    try {
                        this.m_repoAdmin.addRepository(new URL(st.nextToken()));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(err);
                    }
                    continue;
                }
                if (command.equals(REFRESHURL_CMD)) {
                    try {
                        URL url = new URL(st.nextToken());
                        this.m_repoAdmin.removeRepository(url);
                        this.m_repoAdmin.addRepository(url);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(err);
                    }
                    continue;
                }
                this.m_repoAdmin.removeRepository(new URL(st.nextToken()));
            }
        } else {
            Repository[] repos = this.m_repoAdmin.listRepositories();
            if (repos != null && repos.length > 0) {
                for (int i = 0; i < repos.length; ++i) {
                    out.println(repos[i].getURL());
                }
            } else {
                out.println("No repository URLs are set.");
            }
        }
    }

    private void list(String commandLine, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        ParsedCommand pc = this.parseList(commandLine);
        StringBuffer sb = new StringBuffer();
        if (pc.getTokens() == null || pc.getTokens().length() == 0) {
            sb.append("(|(presentationname=*)(symbolicname=*))");
        } else {
            sb.append("(|(presentationname=*");
            sb.append(pc.getTokens());
            sb.append("*)(symbolicname=*");
            sb.append(pc.getTokens());
            sb.append("*))");
        }
        Resource[] resources = this.m_repoAdmin.discoverResources(sb.toString());
        TreeMap<Resource, Resource[]> revisionMap = new TreeMap<Resource, Resource[]>(new Comparator(){

            public int compare(Object o1, Object o2) {
                Resource r1 = (Resource)o1;
                Resource r2 = (Resource)o2;
                int symCompare = r1.getSymbolicName().compareTo(r2.getSymbolicName());
                if (symCompare == 0) {
                    return 0;
                }
                int compare = r1.getPresentationName().compareToIgnoreCase(r2.getPresentationName());
                if (compare == 0) {
                    return symCompare;
                }
                return compare;
            }
        });
        for (int resIdx = 0; resources != null && resIdx < resources.length; ++resIdx) {
            Resource[] revisions = (Resource[])revisionMap.get(resources[resIdx]);
            revisionMap.put(resources[resIdx], ObrCommandImpl.addResourceByVersion(revisions, resources[resIdx]));
        }
        Iterator i = revisionMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            Resource[] revisions = (Resource[])entry.getValue();
            String name = revisions[0].getPresentationName();
            name = name == null ? revisions[0].getSymbolicName() : name;
            out.print(name);
            if (pc.isVerbose() && revisions[0].getPresentationName() != null) {
                out.print(" [" + revisions[0].getSymbolicName() + "]");
            }
            out.print(" (");
            int revIdx = 0;
            do {
                if (revIdx > 0) {
                    out.print(", ");
                }
                out.print(revisions[revIdx].getVersion());
            } while (pc.isVerbose() && ++revIdx < revisions.length);
            if (!pc.isVerbose() && revisions.length > 1) {
                out.print(", ...");
            }
            out.println(")");
        }
        if (resources == null || resources.length == 0) {
            out.println("No matching bundles.");
        }
    }

    private void info(String commandLine, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        ParsedCommand pc = this.parseInfo(commandLine);
        for (int cmdIdx = 0; pc != null && cmdIdx < pc.getTargetCount(); ++cmdIdx) {
            Resource[] resources = this.searchRepository(pc.getTargetId(cmdIdx), pc.getTargetVersion(cmdIdx));
            if (resources == null) {
                err.println("Unknown bundle and/or version: " + pc.getTargetId(cmdIdx));
                continue;
            }
            for (int resIdx = 0; resIdx < resources.length; ++resIdx) {
                if (resIdx > 0) {
                    out.println("");
                }
                this.printResource(out, resources[resIdx]);
            }
        }
    }

    private void deploy(String commandLine, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        ParsedCommand pc = this.parseInstallStart(commandLine);
        this._deploy(pc, command, out, err);
    }

    private void _deploy(ParsedCommand pc, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        Resolver resolver = this.m_repoAdmin.resolver();
        for (int i = 0; pc != null && i < pc.getTargetCount(); ++i) {
            Resource resource = this.selectNewestVersion(this.searchRepository(pc.getTargetId(i), pc.getTargetVersion(i)));
            if (resource != null) {
                resolver.add(resource);
                continue;
            }
            err.println("Unknown bundle - " + pc.getTargetId(i));
        }
        if (resolver.getAddedResources() != null && resolver.getAddedResources().length > 0) {
            if (resolver.resolve()) {
                int resIdx;
                out.println("Target resource(s):");
                ObrCommandImpl.printUnderline(out, 19);
                Resource[] resources = resolver.getAddedResources();
                for (resIdx = 0; resources != null && resIdx < resources.length; ++resIdx) {
                    out.println("   " + resources[resIdx].getPresentationName() + " (" + resources[resIdx].getVersion() + ")");
                }
                resources = resolver.getRequiredResources();
                if (resources != null && resources.length > 0) {
                    out.println("\nRequired resource(s):");
                    ObrCommandImpl.printUnderline(out, 21);
                    for (resIdx = 0; resIdx < resources.length; ++resIdx) {
                        out.println("   " + resources[resIdx].getPresentationName() + " (" + resources[resIdx].getVersion() + ")");
                    }
                }
                if ((resources = resolver.getOptionalResources()) != null && resources.length > 0) {
                    out.println("\nOptional resource(s):");
                    ObrCommandImpl.printUnderline(out, 21);
                    for (resIdx = 0; resIdx < resources.length; ++resIdx) {
                        out.println("   " + resources[resIdx].getPresentationName() + " (" + resources[resIdx].getVersion() + ")");
                    }
                }
                try {
                    out.print("\nDeploying...");
                    resolver.deploy(command.equals(START_CMD));
                    out.println("done.");
                }
                catch (IllegalStateException ex) {
                    err.println(ex);
                }
            } else {
                Requirement[] reqs = resolver.getUnsatisfiedRequirements();
                if (reqs != null && reqs.length > 0) {
                    out.println("Unsatisfied requirement(s):");
                    ObrCommandImpl.printUnderline(out, 27);
                    for (int reqIdx = 0; reqIdx < reqs.length; ++reqIdx) {
                        out.println("   " + reqs[reqIdx].getFilter());
                        Resource[] resources = resolver.getResources(reqs[reqIdx]);
                        for (int resIdx = 0; resIdx < resources.length; ++resIdx) {
                            out.println("      " + resources[resIdx].getPresentationName());
                        }
                    }
                } else {
                    out.println("Could not resolve targets.");
                }
            }
        }
    }

    private void source(String commandLine, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        ParsedCommand pc = this.parseSource(commandLine);
        for (int i = 0; i < pc.getTargetCount(); ++i) {
            Resource resource = this.selectNewestVersion(this.searchRepository(pc.getTargetId(i), pc.getTargetVersion(i)));
            if (resource == null) {
                err.println("Unknown bundle and/or version: " + pc.getTargetId(i));
                continue;
            }
            URL srcURL = (URL)resource.getProperties().get(SOURCE_CMD);
            if (srcURL != null) {
                FileUtil.downloadSource(out, err, srcURL, pc.getDirectory(), pc.isExtract());
                continue;
            }
            err.println("Missing source URL: " + pc.getTargetId(i));
        }
    }

    private void javadoc(String commandLine, String command, PrintStream out, PrintStream err) throws IOException, InvalidSyntaxException {
        ParsedCommand pc = this.parseSource(commandLine);
        for (int i = 0; i < pc.getTargetCount(); ++i) {
            Resource resource = this.selectNewestVersion(this.searchRepository(pc.getTargetId(i), pc.getTargetVersion(i)));
            if (resource == null) {
                err.println("Unknown bundle and/or version: " + pc.getTargetId(i));
                continue;
            }
            URL docURL = (URL)resource.getProperties().get(JAVADOC_CMD);
            if (docURL != null) {
                FileUtil.downloadSource(out, err, docURL, pc.getDirectory(), pc.isExtract());
                continue;
            }
            err.println("Missing javadoc URL: " + pc.getTargetId(i));
        }
    }

    private Resource[] searchRepository(String targetId, String targetVersion) {
        try {
            Bundle bundle = this.m_context.getBundle(Long.parseLong(targetId));
            targetId = bundle.getSymbolicName();
        }
        catch (NumberFormatException ex) {
            // empty catch block
        }
        StringBuffer sb = new StringBuffer("(|(presentationname=");
        sb.append(targetId);
        sb.append(")(symbolicname=");
        sb.append(targetId);
        sb.append("))");
        if (targetVersion != null) {
            sb.insert(0, "(&");
            sb.append("(version=");
            sb.append(targetVersion);
            sb.append("))");
        }
        return this.m_repoAdmin.discoverResources(sb.toString());
    }

    public Resource selectNewestVersion(Resource[] resources) {
        int idx = -1;
        Version v = null;
        for (int i = 0; resources != null && i < resources.length; ++i) {
            if (i == 0) {
                idx = 0;
                v = resources[i].getVersion();
                continue;
            }
            Version vtmp = resources[i].getVersion();
            if (vtmp.compareTo((Object)v) <= 0) continue;
            idx = i;
            v = vtmp;
        }
        return idx < 0 ? null : resources[idx];
    }

    private void printResource(PrintStream out, Resource resource) {
        Capability[] caps;
        ObrCommandImpl.printUnderline(out, resource.getPresentationName().length());
        out.println(resource.getPresentationName());
        ObrCommandImpl.printUnderline(out, resource.getPresentationName().length());
        Map map = resource.getProperties();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            if (entry.getValue().getClass().isArray()) {
                out.println(entry.getKey() + ":");
                for (int j = 0; j < Array.getLength(entry.getValue()); ++j) {
                    out.println("   " + Array.get(entry.getValue(), j));
                }
                continue;
            }
            out.println(entry.getKey() + ": " + entry.getValue());
        }
        Requirement[] reqs = resource.getRequirements();
        if (reqs != null && reqs.length > 0) {
            out.println("Requires:");
            for (int i = 0; i < reqs.length; ++i) {
                out.println("   " + reqs[i].getFilter());
            }
        }
        if ((caps = resource.getCapabilities()) != null && caps.length > 0) {
            out.println("Capabilities:");
            for (int i = 0; i < caps.length; ++i) {
                out.println("   " + caps[i].getProperties());
            }
        }
    }

    private static void printUnderline(PrintStream out, int length) {
        for (int i = 0; i < length; ++i) {
            out.print('-');
        }
        out.println("");
    }

    private ParsedCommand parseList(String commandLine) throws IOException, InvalidSyntaxException {
        StringReader sr = new StringReader(commandLine);
        StreamTokenizer tokenizer = new StreamTokenizer(sr);
        tokenizer.resetSyntax();
        tokenizer.quoteChar(39);
        tokenizer.quoteChar(34);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(65, 90);
        tokenizer.wordChars(97, 122);
        tokenizer.wordChars(48, 57);
        tokenizer.wordChars(160, 255);
        tokenizer.wordChars(46, 46);
        tokenizer.wordChars(45, 45);
        tokenizer.wordChars(95, 95);
        int type = tokenizer.nextToken();
        type = tokenizer.nextToken();
        int EOF = 1;
        int SWITCH = 2;
        int TOKEN = 4;
        ParsedCommand pc = new ParsedCommand();
        String tokens = null;
        int expecting = SWITCH | TOKEN | EOF;
        while (true) {
            type = tokenizer.nextToken();
            switch (type) {
                case -1: {
                    if ((expecting & EOF) == 0) {
                        throw new InvalidSyntaxException("Expecting more arguments.", null);
                    }
                    if (tokens != null) {
                        pc.setTokens(tokens);
                    }
                    return pc;
                }
                case -3: 
                case 34: 
                case 39: {
                    if ((expecting & SWITCH) > 0 && tokenizer.sval.equals(VERBOSE_SWITCH)) {
                        pc.setVerbose(true);
                        expecting = TOKEN | EOF;
                        break;
                    }
                    if ((expecting & TOKEN) > 0) {
                        tokens = tokens == null ? "" : tokens + " ";
                        tokens = tokens + tokenizer.sval;
                        expecting = EOF | TOKEN;
                        break;
                    }
                    throw new InvalidSyntaxException("Not expecting '" + tokenizer.sval + "'.", null);
                }
            }
        }
    }

    private ParsedCommand parseInfo(String commandLine) throws IOException, InvalidSyntaxException {
        StringReader sr = new StringReader(commandLine);
        StreamTokenizer tokenizer = new StreamTokenizer(sr);
        tokenizer.resetSyntax();
        tokenizer.quoteChar(39);
        tokenizer.quoteChar(34);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(65, 90);
        tokenizer.wordChars(97, 122);
        tokenizer.wordChars(48, 57);
        tokenizer.wordChars(160, 255);
        tokenizer.wordChars(46, 46);
        tokenizer.wordChars(45, 45);
        tokenizer.wordChars(95, 95);
        int type = tokenizer.nextToken();
        type = tokenizer.nextToken();
        int EOF = 1;
        int SWITCH = 2;
        int TARGET = 4;
        int VERSION = 8;
        int VERSION_VALUE = 16;
        ParsedCommand pc = new ParsedCommand();
        String currentTargetName = null;
        int expecting = TARGET;
        while (true) {
            type = tokenizer.nextToken();
            switch (type) {
                case -1: {
                    if ((expecting & EOF) == 0) {
                        throw new InvalidSyntaxException("Expecting more arguments.", null);
                    }
                    if (currentTargetName != null) {
                        pc.addTarget(currentTargetName, null);
                    }
                    return pc;
                }
                case -3: 
                case 34: 
                case 39: {
                    if ((expecting & TARGET) > 0) {
                        if (currentTargetName != null) {
                            pc.addTarget(currentTargetName, null);
                        }
                        currentTargetName = tokenizer.sval;
                        expecting = EOF | TARGET | VERSION;
                        break;
                    }
                    if ((expecting & VERSION_VALUE) > 0) {
                        pc.addTarget(currentTargetName, tokenizer.sval);
                        currentTargetName = null;
                        expecting = EOF | TARGET;
                        break;
                    }
                    throw new InvalidSyntaxException("Not expecting '" + tokenizer.sval + "'.", null);
                }
                case 59: {
                    if ((expecting & VERSION) == 0) {
                        throw new InvalidSyntaxException("Not expecting version.", null);
                    }
                    expecting = VERSION_VALUE;
                }
            }
        }
    }

    private ParsedCommand parseInstallStart(String commandLine) throws IOException, InvalidSyntaxException {
        StringReader sr = new StringReader(commandLine);
        StreamTokenizer tokenizer = new StreamTokenizer(sr);
        tokenizer.resetSyntax();
        tokenizer.quoteChar(39);
        tokenizer.quoteChar(34);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(65, 90);
        tokenizer.wordChars(97, 122);
        tokenizer.wordChars(48, 57);
        tokenizer.wordChars(160, 255);
        tokenizer.wordChars(46, 46);
        tokenizer.wordChars(45, 45);
        tokenizer.wordChars(95, 95);
        int type = tokenizer.nextToken();
        type = tokenizer.nextToken();
        int EOF = 1;
        int SWITCH = 2;
        int TARGET = 4;
        int VERSION = 8;
        int VERSION_VALUE = 16;
        ParsedCommand pc = new ParsedCommand();
        String currentTargetName = null;
        int expecting = SWITCH | TARGET;
        while (true) {
            type = tokenizer.nextToken();
            switch (type) {
                case -1: {
                    if ((expecting & EOF) == 0) {
                        throw new InvalidSyntaxException("Expecting more arguments.", null);
                    }
                    if (currentTargetName != null) {
                        pc.addTarget(currentTargetName, null);
                    }
                    return pc;
                }
                case -3: 
                case 34: 
                case 39: {
                    if ((expecting & TARGET) > 0) {
                        if (currentTargetName != null) {
                            pc.addTarget(currentTargetName, null);
                        }
                        currentTargetName = tokenizer.sval;
                        expecting = EOF | TARGET | VERSION;
                        break;
                    }
                    if ((expecting & VERSION_VALUE) > 0) {
                        pc.addTarget(currentTargetName, tokenizer.sval);
                        currentTargetName = null;
                        expecting = EOF | TARGET;
                        break;
                    }
                    throw new InvalidSyntaxException("Not expecting '" + tokenizer.sval + "'.", null);
                }
                case 59: {
                    if ((expecting & VERSION) == 0) {
                        throw new InvalidSyntaxException("Not expecting version.", null);
                    }
                    expecting = VERSION_VALUE;
                }
            }
        }
    }

    private ParsedCommand parseSource(String commandLine) throws IOException, InvalidSyntaxException {
        StringReader sr = new StringReader(commandLine);
        StreamTokenizer tokenizer = new StreamTokenizer(sr);
        tokenizer.resetSyntax();
        tokenizer.quoteChar(39);
        tokenizer.quoteChar(34);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.wordChars(65, 90);
        tokenizer.wordChars(97, 122);
        tokenizer.wordChars(48, 57);
        tokenizer.wordChars(160, 255);
        tokenizer.wordChars(46, 46);
        tokenizer.wordChars(45, 45);
        tokenizer.wordChars(95, 95);
        tokenizer.wordChars(47, 47);
        tokenizer.wordChars(92, 92);
        tokenizer.wordChars(58, 58);
        int type = tokenizer.nextToken();
        type = tokenizer.nextToken();
        int EOF = 1;
        int SWITCH = 2;
        int DIRECTORY = 4;
        int TARGET = 8;
        int VERSION = 16;
        int VERSION_VALUE = 32;
        ParsedCommand pc = new ParsedCommand();
        String currentTargetName = null;
        int expecting = SWITCH | DIRECTORY;
        while (true) {
            type = tokenizer.nextToken();
            switch (type) {
                case -1: {
                    if ((expecting & EOF) == 0) {
                        throw new InvalidSyntaxException("Expecting more arguments.", null);
                    }
                    if (currentTargetName != null) {
                        pc.addTarget(currentTargetName, null);
                    }
                    return pc;
                }
                case -3: 
                case 34: 
                case 39: {
                    if ((expecting & SWITCH) > 0 && tokenizer.sval.equals(EXTRACT_SWITCH)) {
                        pc.setExtract(true);
                        expecting = DIRECTORY;
                        break;
                    }
                    if ((expecting & DIRECTORY) > 0) {
                        pc.setDirectory(tokenizer.sval);
                        expecting = TARGET;
                        break;
                    }
                    if ((expecting & TARGET) > 0) {
                        if (currentTargetName != null) {
                            pc.addTarget(currentTargetName, null);
                        }
                        currentTargetName = tokenizer.sval;
                        expecting = EOF | TARGET | VERSION;
                        break;
                    }
                    if ((expecting & VERSION_VALUE) > 0) {
                        pc.addTarget(currentTargetName, tokenizer.sval);
                        currentTargetName = null;
                        expecting = EOF | TARGET;
                        break;
                    }
                    throw new InvalidSyntaxException("Not expecting '" + tokenizer.sval + "'.", null);
                }
                case 59: {
                    if ((expecting & VERSION) == 0) {
                        throw new InvalidSyntaxException("Not expecting version.", null);
                    }
                    expecting = VERSION_VALUE;
                }
            }
        }
    }

    private void help(PrintStream out, StringTokenizer st) {
        String command = HELP_CMD;
        if (st.hasMoreTokens()) {
            command = st.nextToken();
        }
        if (command.equals(ADDURL_CMD)) {
            out.println("");
            out.println("obr add-url <repository-url> ...");
            out.println("");
            out.println("This command adds the space-delimited list of repository URLs to\nthe repository service.");
            out.println("");
        } else if (command.equals(REFRESHURL_CMD)) {
            out.println("");
            out.println("obr refresh-url <repository-url> ...");
            out.println("");
            out.println("This command refreshes the space-delimited list of repository URLs\nwithin the repository service.\n(The command internally removes and adds the specified URLs from the\nrepository service.)");
            out.println("");
        } else if (command.equals(REMOVEURL_CMD)) {
            out.println("");
            out.println("obr remove-url <repository-url> ...");
            out.println("");
            out.println("This command removes the space-delimited list of repository URLs\nfrom the repository service.");
            out.println("");
        } else if (command.equals(LISTURL_CMD)) {
            out.println("");
            out.println("obr list-url");
            out.println("");
            out.println("This command displays the repository URLs currently associated\nwith the repository service.");
            out.println("");
        } else if (command.equals(LIST_CMD)) {
            out.println("");
            out.println("obr list [-v] [<string> ...]");
            out.println("");
            out.println("This command lists bundles available in the bundle repository.\nIf no arguments are specified, then all available bundles are\nlisted, otherwise any arguments are concatenated with spaces\nand used as a substring filter on the bundle names. By default,\nonly the most recent version of each artifact is shown. To list\nall available versions use the \"-v\" switch.");
            out.println("");
        } else if (command.equals(INFO_CMD)) {
            out.println("");
            out.println("obr info <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ...");
            out.println("");
            out.println("This command displays the meta-data for the specified bundles.\nIf a bundle's name contains spaces, then it must be surrounded\nby quotes. It is also possible to specify a precise version\nif more than one version exists, such as:\n\n    obr info \"Bundle Repository\";1.0.0\n\nThe above example retrieves the meta-data for version \"1.0.0\"\nof the bundle named \"Bundle Repository\".");
            out.println("");
        } else if (command.equals(DEPLOY_CMD)) {
            out.println("");
            out.println("obr deploy <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ... ");
            out.println("");
            out.println("This command tries to install or update the specified bundles\nand all of their dependencies. You can specify either the bundle\nname or the bundle identifier. If a bundle's name contains spaces,\nthen it must be surrounded by quotes. It is also possible to\nspecify a precise version if more than one version exists, such as:\n\n    obr deploy \"Bundle Repository\";1.0.0\n\nFor the above example, if version \"1.0.0\" of \"Bundle Repository\" is\nalready installed locally, then the command will attempt to update it\nand all of its dependencies; otherwise, the command will install it\nand all of its dependencies.");
            out.println("");
        } else if (command.equals(START_CMD)) {
            out.println("");
            out.println("obr start <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ...");
            out.println("");
            out.println("This command installs and starts the specified bundles and all\nof their dependencies. If a bundle's name contains spaces, then\nit must be surrounded by quotes. If a specified bundle is already\ninstalled, then this command has no effect. It is also possible\nto specify a precise version if more than one version exists,\nsuch as:\n\n    obr start \"Bundle Repository\";1.0.0\n\nThe above example installs and starts version \"1.0.0\" of the\nbundle named \"Bundle Repository\" and its dependencies.");
            out.println("");
        } else if (command.equals(SOURCE_CMD)) {
            out.println("");
            out.println("obr source [-x] <local-dir> <bundle-name>[;<version>] ...");
            out.println("");
            out.println("This command retrieves the source archives of the specified\nbundles and saves them to the specified local directory; use\nthe \"-x\" switch to automatically extract the source archives.\nIf a bundle name contains spaces, then it must be surrounded\nby quotes. It is also possible to specify a precise version if\nmore than one version exists, such as:\n\n    obr source /home/rickhall/tmp \"Bundle Repository\";1.0.0\n\nThe above example retrieves the source archive of version \"1.0.0\"\nof the bundle named \"Bundle Repository\" and saves it to the\nspecified local directory.");
            out.println("");
        } else if (command.equals(JAVADOC_CMD)) {
            out.println("");
            out.println("obr javadoc [-x] <local-dir> <bundle-name>[;<version>] ...");
            out.println("");
            out.println("This command retrieves the javadoc archives of the specified\nbundles and saves them to the specified local directory; use\nthe \"-x\" switch to automatically extract the javadoc archives.\nIf a bundle name contains spaces, then it must be surrounded\nby quotes. It is also possible to specify a precise version if\nmore than one version exists, such as:\n\n    obr javadoc /home/rickhall/tmp \"Bundle Repository\";1.0.0\n\nThe above example retrieves the javadoc archive of version \"1.0.0\"\nof the bundle named \"Bundle Repository\" and saves it to the\nspecified local directory.");
            out.println("");
        } else {
            out.println("obr help [add-url | remove-url | list-url | list | info | deploy | start | source | javadoc]");
            out.println("obr add-url [<repository-file-url> ...]");
            out.println("obr refresh-url [<repository-file-url> ...]");
            out.println("obr remove-url [<repository-file-url> ...]");
            out.println("obr list-url");
            out.println("obr list [-v] [<string> ...]");
            out.println("obr info <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ...");
            out.println("obr deploy <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ...");
            out.println("obr start <bundle-name>|<bundle-symbolic-name>|<bundle-id>[;<version>] ...");
            out.println("obr source [-x] <local-dir> <bundle-name>[;<version>] ...");
            out.println("obr javadoc [-x] <local-dir> <bundle-name>[;<version>] ...");
        }
    }

    private static Resource[] addResourceByVersion(Resource[] revisions, Resource resource) {
        Resource[] sorted = null;
        if (revisions == null) {
            sorted = new Resource[]{resource};
        } else {
            Version version = resource.getVersion();
            Version middleVersion = null;
            int top = 0;
            int bottom = revisions.length - 1;
            int middle = 0;
            while (top <= bottom) {
                middle = (bottom - top) / 2 + top;
                middleVersion = revisions[middle].getVersion();
                int cmp = middleVersion.compareTo((Object)version);
                if (cmp < 0) {
                    bottom = middle - 1;
                    continue;
                }
                top = middle + 1;
            }
            if (top >= revisions.length || revisions[top] != resource) {
                sorted = new Resource[revisions.length + 1];
                System.arraycopy(revisions, 0, sorted, 0, top);
                System.arraycopy(revisions, top, sorted, top + 1, revisions.length - top);
                sorted[top] = resource;
            }
        }
        return sorted;
    }

    private static class ParsedCommand {
        private static final int NAME_IDX = 0;
        private static final int VERSION_IDX = 1;
        private boolean m_isResolve = true;
        private boolean m_isCheck = false;
        private boolean m_isExtract = false;
        private boolean m_isVerbose = false;
        private String m_tokens = null;
        private String m_dir = null;
        private String[][] m_targets = new String[0][];

        private ParsedCommand() {
        }

        public boolean isResolve() {
            return this.m_isResolve;
        }

        public void setResolve(boolean b) {
            this.m_isResolve = b;
        }

        public boolean isCheck() {
            return this.m_isCheck;
        }

        public void setCheck(boolean b) {
            this.m_isCheck = b;
        }

        public boolean isExtract() {
            return this.m_isExtract;
        }

        public void setExtract(boolean b) {
            this.m_isExtract = b;
        }

        public boolean isVerbose() {
            return this.m_isVerbose;
        }

        public void setVerbose(boolean b) {
            this.m_isVerbose = b;
        }

        public String getTokens() {
            return this.m_tokens;
        }

        public void setTokens(String s) {
            this.m_tokens = s;
        }

        public String getDirectory() {
            return this.m_dir;
        }

        public void setDirectory(String s) {
            this.m_dir = s;
        }

        public int getTargetCount() {
            return this.m_targets.length;
        }

        public String getTargetId(int i) {
            if (i < 0 || i >= this.getTargetCount()) {
                return null;
            }
            return this.m_targets[i][0];
        }

        public String getTargetVersion(int i) {
            if (i < 0 || i >= this.getTargetCount()) {
                return null;
            }
            return this.m_targets[i][1];
        }

        public void addTarget(String name, String version) {
            String[][] newTargets = new String[this.m_targets.length + 1][];
            System.arraycopy(this.m_targets, 0, newTargets, 0, this.m_targets.length);
            newTargets[this.m_targets.length] = new String[]{name, version};
            this.m_targets = newTargets;
        }
    }
}

