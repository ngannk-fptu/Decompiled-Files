/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Processor;
import aQute.lib.fileset.FileSet;
import aQute.lib.io.IO;
import aQute.lib.strings.Strings;
import aQute.libg.command.Command;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Makefile
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(Makefile.class);
    private Parameters parameters;
    private List<Cmd> commands = new ArrayList<Cmd>();
    private String path;

    Makefile(Processor project) {
        super(project);
        this.setBase(project.getBase());
        this.getSettings(project);
        this.parameters = new Parameters(this.mergeProperties("-prepare"), this);
        for (Map.Entry<String, Attrs> e : this.parameters.entrySet()) {
            try {
                String[] parts;
                Cmd cmd = new Cmd();
                cmd.make = Makefile.removeDuplicateMarker(e.getKey());
                Attrs attrs = e.getValue();
                cmd.name = cmd.make;
                if (attrs.containsKey("name:")) {
                    cmd.name = attrs.get("name:");
                }
                if ((parts = cmd.make.trim().split("\\s*<=\\s*")).length > 2) {
                    this.error("Command with dep spec %s has too many <= separated parts", cmd.name);
                    continue;
                }
                cmd.target = IO.getFile(this.getBase(), parts[0]);
                FileSet fileSet = cmd.source = parts.length > 1 ? new FileSet(this.getBase(), parts[1]) : null;
                if (attrs.containsKey("report:")) {
                    cmd.report = Pattern.compile(attrs.get("report:"));
                }
                if (!attrs.containsKey("command:")) {
                    this.error("-prepare: No command specified (command:) for %s", cmd.name);
                    continue;
                }
                cmd.command = attrs.remove("command:");
                this.commands.add(cmd);
            }
            catch (Exception ee) {
                this.exception(ee, "-prepare: Could not parse command %s : %s", e.getKey(), e.getValue());
            }
        }
        if (this.parameters.size() > 0) {
            this.path = this.getProperty("-PATH");
            if (this.path != null) {
                String oldpath = System.getenv("PATH");
                this.path = this.path.replaceAll("\\s*,\\s*", File.pathSeparator);
                if (oldpath != null && !oldpath.isEmpty()) {
                    this.path = this.path.contains("${@}") ? this.path.replaceAll("\\$\\{@\\}", oldpath) : this.path + File.pathSeparator + oldpath;
                }
                logger.debug("PATH: {}", (Object)this.path);
            }
        }
    }

    void make() {
        for (Cmd cmd : this.commands) {
            cmd.execute();
        }
        this.getParent().getInfo(this);
    }

    class Cmd {
        Pattern report;
        File target;
        FileSet source;
        String command;
        String name;
        String make;
        Map<String, String> env = new HashMap<String, String>();

        Cmd() {
        }

        void execute() {
            if (!this.isStale()) {
                return;
            }
            StringBuilder errors = new StringBuilder();
            StringBuilder stdout = new StringBuilder();
            logger.debug("executing command {}", (Object)this.name);
            Command cmd = new Command("sh");
            cmd.setTimeout(1L, TimeUnit.MINUTES);
            cmd.setCwd(Makefile.this.getBase());
            cmd.inherit();
            if (Makefile.this.path != null) {
                cmd.var("PATH", Makefile.this.path);
            }
            for (Map.Entry<String, String> e : this.env.entrySet()) {
                String key = e.getKey();
                if (key.endsWith(":")) continue;
                cmd.var(key, e.getValue());
            }
            try {
                String command = this.command;
                command = command.replaceAll("\\$@", this.target.toString());
                if (this.source != null) {
                    command = command.replaceAll("\\$\\^", Strings.join(" ", this.source.getFiles()));
                }
                logger.debug("cmd {}", (Object)command);
                int result = cmd.execute(command, (Appendable)stdout, (Appendable)errors);
                if (stdout.length() > 0) {
                    logger.debug("{} stdout: {}", (Object)this.name, (Object)stdout);
                }
                if (errors.length() > 0) {
                    logger.debug("{} stderr: {}", (Object)this.name, (Object)errors);
                }
                if (result != 0) {
                    IO.delete(this.target);
                    boolean found = false;
                    if (this.report != null) {
                        found |= this.parseErrors(this.report, errors);
                        found |= this.parseErrors(this.report, stdout);
                    }
                    if (!found) {
                        Reporter.SetLocation location = Makefile.this.error("%s: -prepare exit status = %s: %s", this.name, result, stdout + "\n" + errors);
                        Processor.FileLine fl = Makefile.this.getParent().getHeader("-prepare", this.make);
                        if (fl != null) {
                            fl.set(location);
                        }
                    }
                }
            }
            catch (Exception e) {
                Makefile.this.exception(e, "%s: -prepare", this.name);
                IO.delete(this.target);
            }
        }

        boolean isStale() {
            if (this.source != null) {
                if (!this.target.isFile()) {
                    return true;
                }
                long lastModified = this.target.lastModified();
                for (File s : this.source.getFiles()) {
                    if (lastModified >= s.lastModified()) continue;
                    return true;
                }
                return false;
            }
            return true;
        }

        private boolean parseErrors(Pattern report, StringBuilder errors) {
            Matcher m = report.matcher(errors);
            boolean found = false;
            while (m.find()) {
                String ls;
                Reporter.SetLocation location;
                found = true;
                logger.debug("found errors {}", (Object)m.group());
                String type = this.getGroup(m, "type");
                Reporter.SetLocation setLocation = location = "warning".equals(type) ? Makefile.this.warning("%s: %s", this.name, m.group("message")) : Makefile.this.error("%s: %s", this.name, m.group("message"));
                String fileName = this.getGroup(m, "file");
                if (fileName == null) continue;
                File file = IO.getFile(Makefile.this.getBase(), fileName);
                if (file.isFile()) {
                    location.file(file.getAbsolutePath());
                }
                if ((ls = this.getGroup(m, "line")) != null && ls.matches("\\d+")) {
                    location.line(Integer.parseInt(ls));
                }
                logger.debug("file {}#{}", (Object)file, (Object)ls);
            }
            return found;
        }

        private String getGroup(Matcher m, String group) {
            try {
                return m.group(group);
            }
            catch (Exception exception) {
                return null;
            }
        }
    }
}

