/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.modules;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.spi.ToolProvider;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.util.CompositeMapper;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.ResourceUtils;

public class Link
extends Task {
    private static final String INVALID_LAUNCHER_STRING = "Launcher command must take the form name=module or name=module/mainclass";
    private Path modulePath;
    private final List<ModuleSpec> modules = new ArrayList<ModuleSpec>();
    private final List<ModuleSpec> observableModules = new ArrayList<ModuleSpec>();
    private final List<Launcher> launchers = new ArrayList<Launcher>();
    private final List<LocaleSpec> locales = new ArrayList<LocaleSpec>();
    private final List<PatternListEntry> ordering = new ArrayList<PatternListEntry>();
    private final List<PatternListEntry> excludedFiles = new ArrayList<PatternListEntry>();
    private final List<PatternListEntry> excludedResources = new ArrayList<PatternListEntry>();
    private boolean bindServices;
    private boolean ignoreSigning;
    private boolean includeHeaders = true;
    private boolean includeManPages = true;
    private boolean includeNativeCommands = true;
    private boolean debug = true;
    private LogLevel verboseLevel;
    private File outputDir;
    private Endianness endianness;
    private CompressionLevel compressionLevel;
    private Compression compression;
    private boolean checkDuplicateLegal;
    private VMType vmType;
    private final List<ReleaseInfo> releaseInfo = new ArrayList<ReleaseInfo>();

    public Path createModulePath() {
        if (this.modulePath == null) {
            this.modulePath = new Path(this.getProject());
        }
        return this.modulePath.createPath();
    }

    public Path getModulePath() {
        return this.modulePath;
    }

    public void setModulePath(Path path) {
        if (this.modulePath == null) {
            this.modulePath = path;
        } else {
            this.modulePath.append(path);
        }
    }

    public void setModulePathRef(Reference ref) {
        this.createModulePath().setRefid(ref);
    }

    public ModuleSpec createModule() {
        ModuleSpec module = new ModuleSpec();
        this.modules.add(module);
        return module;
    }

    public void setModules(String moduleList) {
        for (String moduleName : moduleList.split(",")) {
            this.modules.add(new ModuleSpec(moduleName));
        }
    }

    public ModuleSpec createObservableModule() {
        ModuleSpec module = new ModuleSpec();
        this.observableModules.add(module);
        return module;
    }

    public void setObservableModules(String moduleList) {
        for (String moduleName : moduleList.split(",")) {
            this.observableModules.add(new ModuleSpec(moduleName));
        }
    }

    public Launcher createLauncher() {
        Launcher command = new Launcher();
        this.launchers.add(command);
        return command;
    }

    public void setLaunchers(String launcherList) {
        for (String launcherSpec : launcherList.split(",")) {
            this.launchers.add(new Launcher(launcherSpec));
        }
    }

    public LocaleSpec createLocale() {
        LocaleSpec locale = new LocaleSpec();
        this.locales.add(locale);
        return locale;
    }

    public void setLocales(String localeList) {
        for (String localeName : localeList.split(",")) {
            this.locales.add(new LocaleSpec(localeName));
        }
    }

    public PatternListEntry createExcludeFiles() {
        PatternListEntry entry = new PatternListEntry();
        this.excludedFiles.add(entry);
        return entry;
    }

    public void setExcludeFiles(String patternList) {
        for (String pattern : patternList.split(",")) {
            this.excludedFiles.add(new PatternListEntry(pattern));
        }
    }

    public PatternListEntry createExcludeResources() {
        PatternListEntry entry = new PatternListEntry();
        this.excludedResources.add(entry);
        return entry;
    }

    public void setExcludeResources(String patternList) {
        for (String pattern : patternList.split(",")) {
            this.excludedResources.add(new PatternListEntry(pattern));
        }
    }

    public PatternListEntry createResourceOrder() {
        PatternListEntry order = new PatternListEntry();
        this.ordering.add(order);
        return order;
    }

    public void setResourceOrder(String patternList) {
        ArrayList<PatternListEntry> orderList = new ArrayList<PatternListEntry>();
        for (String pattern : patternList.split(",")) {
            orderList.add(new PatternListEntry(pattern));
        }
        this.ordering.addAll(0, orderList);
    }

    public boolean getBindServices() {
        return this.bindServices;
    }

    public void setBindServices(boolean bind) {
        this.bindServices = bind;
    }

    public boolean getIgnoreSigning() {
        return this.ignoreSigning;
    }

    public void setIgnoreSigning(boolean ignore) {
        this.ignoreSigning = ignore;
    }

    public boolean getIncludeHeaders() {
        return this.includeHeaders;
    }

    public void setIncludeHeaders(boolean include) {
        this.includeHeaders = include;
    }

    public boolean getIncludeManPages() {
        return this.includeManPages;
    }

    public void setIncludeManPages(boolean include) {
        this.includeManPages = include;
    }

    public boolean getIncludeNativeCommands() {
        return this.includeNativeCommands;
    }

    public void setIncludeNativeCommands(boolean include) {
        this.includeNativeCommands = include;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public LogLevel getVerboseLevel() {
        return this.verboseLevel;
    }

    public void setVerboseLevel(LogLevel level) {
        this.verboseLevel = level;
    }

    public File getDestDir() {
        return this.outputDir;
    }

    public void setDestDir(File dir) {
        this.outputDir = dir;
    }

    public CompressionLevel getCompress() {
        return this.compressionLevel;
    }

    public void setCompress(CompressionLevel level) {
        this.compressionLevel = level;
    }

    public Compression createCompress() {
        if (this.compression != null) {
            throw new BuildException("Only one nested compression element is permitted.", this.getLocation());
        }
        this.compression = new Compression();
        return this.compression;
    }

    public Endianness getEndianness() {
        return this.endianness;
    }

    public void setEndianness(Endianness endianness) {
        this.endianness = endianness;
    }

    public boolean getCheckDuplicateLegal() {
        return this.checkDuplicateLegal;
    }

    public void setCheckDuplicateLegal(boolean check) {
        this.checkDuplicateLegal = check;
    }

    public VMType getVmType() {
        return this.vmType;
    }

    public void setVmType(VMType type) {
        this.vmType = type;
    }

    public ReleaseInfo createReleaseInfo() {
        ReleaseInfo info = new ReleaseInfo();
        this.releaseInfo.add(info);
        return info;
    }

    @Override
    public void execute() throws BuildException {
        int exitCode;
        if (this.outputDir == null) {
            throw new BuildException("Destination directory is required.", this.getLocation());
        }
        if (this.modulePath == null || this.modulePath.isEmpty()) {
            throw new BuildException("Module path is required.", this.getLocation());
        }
        if (this.modules.isEmpty()) {
            throw new BuildException("At least one module must be specified.", this.getLocation());
        }
        if (this.outputDir.exists()) {
            CompositeMapper imageMapper = new CompositeMapper();
            try (Stream<java.nio.file.Path> imageTree = Files.walk(this.outputDir.toPath(), new FileVisitOption[0]);){
                imageTree.forEach(p -> imageMapper.add(new MergingMapper(p.toString())));
                ResourceCollection outOfDate = ResourceUtils.selectOutOfDateSources((ProjectComponent)this, this.modulePath, (FileNameMapper)imageMapper, (ResourceFactory)this.getProject(), FileUtils.getFileUtils().getFileTimestampGranularity());
                if (outOfDate.isEmpty()) {
                    this.log("Skipping image creation, since \"" + this.outputDir + "\" is already newer than all constituent modules.", 3);
                    return;
                }
            }
            catch (IOException e) {
                throw new BuildException("Could not scan \"" + this.outputDir + "\" for being up-to-date: " + e, e, this.getLocation());
            }
        }
        this.modules.forEach(ModuleSpec::validate);
        this.observableModules.forEach(ModuleSpec::validate);
        this.launchers.forEach(Launcher::validate);
        this.locales.forEach(LocaleSpec::validate);
        this.ordering.forEach(PatternListEntry::validate);
        this.excludedFiles.forEach(PatternListEntry::validate);
        this.excludedResources.forEach(PatternListEntry::validate);
        Collection<String> args = this.buildJlinkArgs();
        ToolProvider jlink2 = ToolProvider.findFirst("jlink").orElseThrow(() -> new BuildException("jlink tool not found in JDK.", this.getLocation()));
        if (this.outputDir.exists()) {
            this.log("Deleting existing " + this.outputDir, 3);
            this.deleteTree(this.outputDir.toPath());
        }
        this.log("Executing: jlink " + String.join((CharSequence)" ", args), 3);
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(stdout);
             PrintStream err = new PrintStream(stderr);){
            exitCode = jlink2.run(out, err, args.toArray(new String[0]));
        }
        if (exitCode != 0) {
            StringBuilder message = new StringBuilder();
            message.append("jlink failed (exit code ").append(exitCode).append(")");
            if (stdout.size() > 0) {
                message.append(", output is: ").append(stdout);
            }
            if (stderr.size() > 0) {
                message.append(", error output is: ").append(stderr);
            }
            throw new BuildException(message.toString(), this.getLocation());
        }
        if (this.verboseLevel != null) {
            int level = this.verboseLevel.getLevel();
            if (stdout.size() > 0) {
                this.log(stdout.toString(), level);
            }
            if (stderr.size() > 0) {
                this.log(stderr.toString(), level);
            }
        }
        this.log("Created " + this.outputDir.getAbsolutePath(), 2);
    }

    private void deleteTree(java.nio.file.Path dir) {
        try {
            Files.walkFileTree(dir, (FileVisitor<? super java.nio.file.Path>)new SimpleFileVisitor<java.nio.file.Path>(){

                @Override
                public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attr) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException e) throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                    }
                    return super.postVisitDirectory(dir, e);
                }
            });
        }
        catch (IOException e) {
            throw new BuildException("Could not delete \"" + dir + "\": " + e, e, this.getLocation());
        }
    }

    private Collection<String> buildJlinkArgs() {
        ArrayList<String> args = new ArrayList<String>();
        args.add("--output");
        args.add(this.outputDir.toString());
        args.add("--module-path");
        args.add(this.modulePath.toString());
        args.add("--add-modules");
        args.add(this.modules.stream().map(ModuleSpec::getName).collect(Collectors.joining(",")));
        if (!this.observableModules.isEmpty()) {
            args.add("--limit-modules");
            args.add(this.observableModules.stream().map(ModuleSpec::getName).collect(Collectors.joining(",")));
        }
        if (!this.locales.isEmpty()) {
            args.add("--include-locales=" + this.locales.stream().map(LocaleSpec::getName).collect(Collectors.joining(",")));
        }
        for (Launcher launcher : this.launchers) {
            args.add("--launcher");
            args.add(launcher.toString());
        }
        if (!this.ordering.isEmpty()) {
            args.add("--order-resources=" + this.ordering.stream().map(PatternListEntry::toOptionValue).collect(Collectors.joining(",")));
        }
        if (!this.excludedFiles.isEmpty()) {
            args.add("--exclude-files=" + this.excludedFiles.stream().map(PatternListEntry::toOptionValue).collect(Collectors.joining(",")));
        }
        if (!this.excludedResources.isEmpty()) {
            args.add("--exclude-resources=" + this.excludedResources.stream().map(PatternListEntry::toOptionValue).collect(Collectors.joining(",")));
        }
        if (this.bindServices) {
            args.add("--bind-services");
        }
        if (this.ignoreSigning) {
            args.add("--ignore-signing-information");
        }
        if (!this.includeHeaders) {
            args.add("--no-header-files");
        }
        if (!this.includeManPages) {
            args.add("--no-man-pages");
        }
        if (!this.includeNativeCommands) {
            args.add("--strip-native-commands");
        }
        if (!this.debug) {
            args.add("--strip-debug");
        }
        if (this.verboseLevel != null) {
            args.add("--verbose");
        }
        if (this.endianness != null) {
            args.add("--endian");
            args.add(this.endianness.getValue());
        }
        if (this.compressionLevel != null) {
            if (this.compression != null) {
                throw new BuildException("compressionLevel attribute and <compression> child element cannot both be present.", this.getLocation());
            }
            args.add("--compress=" + this.compressionLevel.toCommandLineOption());
        }
        if (this.compression != null) {
            this.compression.validate();
            args.add("--compress=" + this.compression.toCommandLineOption());
        }
        if (this.vmType != null) {
            args.add("--vm=" + this.vmType.getValue());
        }
        if (this.checkDuplicateLegal) {
            args.add("--dedup-legal-notices=error-if-not-same-content");
        }
        for (ReleaseInfo info : this.releaseInfo) {
            info.validate();
            args.addAll(info.toCommandLineOptions());
        }
        return args;
    }

    public class ModuleSpec {
        private String name;

        public ModuleSpec() {
        }

        public ModuleSpec(String name) {
            this.setName(name);
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void validate() {
            if (this.name == null) {
                throw new BuildException("name is required for module.", Link.this.getLocation());
            }
        }
    }

    public class Launcher {
        private String name;
        private String module;
        private String mainClass;

        public Launcher() {
        }

        public Launcher(String textSpec) {
            Objects.requireNonNull(textSpec, "Text cannot be null");
            int equals = textSpec.lastIndexOf(61);
            if (equals < 1) {
                throw new BuildException(Link.INVALID_LAUNCHER_STRING);
            }
            this.setName(textSpec.substring(0, equals));
            int slash = textSpec.indexOf(47, equals);
            if (slash < 0) {
                this.setModule(textSpec.substring(equals + 1));
            } else if (slash > equals + 1 && slash < textSpec.length() - 1) {
                this.setModule(textSpec.substring(equals + 1, slash));
                this.setMainClass(textSpec.substring(slash + 1));
            } else {
                throw new BuildException(Link.INVALID_LAUNCHER_STRING);
            }
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModule() {
            return this.module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getMainClass() {
            return this.mainClass;
        }

        public void setMainClass(String className) {
            this.mainClass = className;
        }

        public void validate() {
            if (this.name == null || this.name.isEmpty()) {
                throw new BuildException("Launcher must have a name", Link.this.getLocation());
            }
            if (this.module == null || this.module.isEmpty()) {
                throw new BuildException("Launcher must have specify a module", Link.this.getLocation());
            }
        }

        public String toString() {
            if (this.mainClass != null) {
                return this.name + "=" + this.module + "/" + this.mainClass;
            }
            return this.name + "=" + this.module;
        }
    }

    public class LocaleSpec {
        private String name;

        public LocaleSpec() {
        }

        public LocaleSpec(String name) {
            this.setName(name);
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void validate() {
            if (this.name == null) {
                throw new BuildException("name is required for locale.", Link.this.getLocation());
            }
        }
    }

    public class PatternListEntry {
        private String pattern;
        private File file;

        public PatternListEntry() {
        }

        public PatternListEntry(String pattern) {
            if (pattern.startsWith("@")) {
                this.setListFile(new File(pattern.substring(1)));
            } else {
                this.setPattern(pattern);
            }
        }

        public String getPattern() {
            return this.pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public File getListFile() {
            return this.file;
        }

        public void setListFile(File file) {
            this.file = file;
        }

        public void validate() {
            if (this.pattern == null && this.file == null || this.pattern != null && this.file != null) {
                throw new BuildException("Each entry in a pattern list must specify exactly one of pattern or file.", Link.this.getLocation());
            }
        }

        public String toOptionValue() {
            return this.pattern != null ? this.pattern : "@" + this.file;
        }
    }

    public static class CompressionLevel
    extends EnumeratedAttribute {
        private static final Map<String, String> KEYWORDS;

        @Override
        public String[] getValues() {
            return KEYWORDS.keySet().toArray(new String[0]);
        }

        String toCommandLineOption() {
            return KEYWORDS.get(this.getValue());
        }

        static {
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("0", "0");
            map.put("1", "1");
            map.put("2", "2");
            map.put("none", "0");
            map.put("strings", "1");
            map.put("zip", "2");
            KEYWORDS = Collections.unmodifiableMap(map);
        }
    }

    public class Compression {
        private CompressionLevel level;
        private final List<PatternListEntry> patterns = new ArrayList<PatternListEntry>();

        public CompressionLevel getLevel() {
            return this.level;
        }

        public void setLevel(CompressionLevel level) {
            this.level = level;
        }

        public PatternListEntry createFiles() {
            PatternListEntry pattern = new PatternListEntry();
            this.patterns.add(pattern);
            return pattern;
        }

        public void setFiles(String patternList) {
            this.patterns.clear();
            for (String pattern : patternList.split(",")) {
                this.patterns.add(new PatternListEntry(pattern));
            }
        }

        public void validate() {
            if (this.level == null) {
                throw new BuildException("Compression level must be specified.", Link.this.getLocation());
            }
            this.patterns.forEach(PatternListEntry::validate);
        }

        public String toCommandLineOption() {
            StringBuilder option = new StringBuilder(this.level.toCommandLineOption());
            if (!this.patterns.isEmpty()) {
                String separator = ":filter=";
                for (PatternListEntry entry : this.patterns) {
                    option.append(separator).append(entry.toOptionValue());
                    separator = ",";
                }
            }
            return option.toString();
        }
    }

    public static class Endianness
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"little", "big"};
        }
    }

    public static class VMType
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"client", "server", "minimal", "all"};
        }
    }

    public class ReleaseInfo {
        private File file;
        private final List<ReleaseInfoEntry> propertiesToAdd = new ArrayList<ReleaseInfoEntry>();
        private final List<ReleaseInfoKey> propertiesToDelete = new ArrayList<ReleaseInfoKey>();

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public ReleaseInfoEntry createAdd() {
            ReleaseInfoEntry property = new ReleaseInfoEntry();
            this.propertiesToAdd.add(property);
            return property;
        }

        public ReleaseInfoKey createDelete() {
            ReleaseInfoKey key = new ReleaseInfoKey();
            this.propertiesToDelete.add(key);
            return key;
        }

        public void setDelete(String keyList) {
            for (String key : keyList.split(",")) {
                this.propertiesToDelete.add(new ReleaseInfoKey(key));
            }
        }

        public void validate() {
            this.propertiesToAdd.forEach(ReleaseInfoEntry::validate);
            this.propertiesToDelete.forEach(ReleaseInfoKey::validate);
        }

        public Collection<String> toCommandLineOptions() {
            StringBuilder option;
            ArrayList<String> options = new ArrayList<String>();
            if (this.file != null) {
                options.add("--release-info=" + this.file);
            }
            if (!this.propertiesToAdd.isEmpty()) {
                option = new StringBuilder("--release-info=add");
                for (ReleaseInfoEntry entry : this.propertiesToAdd) {
                    Properties props = entry.toProperties();
                    for (String key : props.stringPropertyNames()) {
                        option.append(":").append(key).append("=");
                        option.append(props.getProperty(key));
                    }
                }
                options.add(option.toString());
            }
            if (!this.propertiesToDelete.isEmpty()) {
                option = new StringBuilder("--release-info=del:keys=");
                String separator = "";
                for (ReleaseInfoKey key : this.propertiesToDelete) {
                    option.append(separator).append(key.getKey());
                    separator = ",";
                }
                options.add(option.toString());
            }
            return options;
        }
    }

    public class ReleaseInfoEntry {
        private String key;
        private String value;
        private File file;
        private String charset = StandardCharsets.ISO_8859_1.name();

        public ReleaseInfoEntry() {
        }

        public ReleaseInfoEntry(String key, String value) {
            this.setKey(key);
            this.setValue(value);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getCharset() {
            return this.charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public void validate() {
            if (this.file == null && (this.key == null || this.value == null)) {
                throw new BuildException("Release info must define 'key' and 'value' attributes, or a 'file' attribute.", Link.this.getLocation());
            }
            if (this.file != null && (this.key != null || this.value != null)) {
                throw new BuildException("Release info cannot define both a file attribute and key/value attributes.", Link.this.getLocation());
            }
            if (this.charset == null) {
                throw new BuildException("Charset cannot be null.", Link.this.getLocation());
            }
            try {
                Charset.forName(this.charset);
            }
            catch (IllegalArgumentException e) {
                throw new BuildException(e, Link.this.getLocation());
            }
        }

        public Properties toProperties() {
            Properties props;
            block9: {
                props = new Properties();
                if (this.file != null) {
                    try (BufferedReader reader = Files.newBufferedReader(this.file.toPath(), Charset.forName(this.charset));){
                        props.load(reader);
                        break block9;
                    }
                    catch (IOException e) {
                        throw new BuildException("Cannot read release info file \"" + this.file + "\": " + e, e, Link.this.getLocation());
                    }
                }
                props.setProperty(this.key, this.value);
            }
            return props;
        }
    }

    public class ReleaseInfoKey {
        private String key;

        public ReleaseInfoKey() {
        }

        public ReleaseInfoKey(String key) {
            this.setKey(key);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void validate() {
            if (this.key == null) {
                throw new BuildException("Release info key must define a 'key' attribute.", Link.this.getLocation());
            }
        }
    }
}

