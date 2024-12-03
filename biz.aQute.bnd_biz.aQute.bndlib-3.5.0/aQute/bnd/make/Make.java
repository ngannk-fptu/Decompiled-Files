/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.make;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.MakePlugin;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Make {
    private static final Logger logger = LoggerFactory.getLogger(Make.class);
    Builder builder;
    Map<Instruction, Map<String, String>> make;

    public Make(Builder builder) {
        this.builder = builder;
    }

    public Resource process(String source) {
        Map<Instruction, Map<String, String>> make = this.getMakeHeader();
        logger.debug("make {}", (Object)source);
        for (Map.Entry<Instruction, Map<String, String>> entry : make.entrySet()) {
            Instruction instr = entry.getKey();
            Matcher m = instr.getMatcher(source);
            if (!m.matches() && !instr.isNegated()) continue;
            Map<String, String> arguments = this.replace(m, entry.getValue());
            List<MakePlugin> plugins = this.builder.getPlugins(MakePlugin.class);
            for (MakePlugin plugin : plugins) {
                try {
                    Resource resource = plugin.make(this.builder, source, arguments);
                    if (resource == null) continue;
                    logger.debug("Made {} from args {} with {}", new Object[]{source, arguments, plugin});
                    return resource;
                }
                catch (Exception e) {
                    this.builder.exception(e, "Plugin %s generates error when use in making %s with args %s", plugin, source, arguments);
                }
            }
        }
        return null;
    }

    private Map<String, String> replace(Matcher m, Map<String, String> value) {
        Map<String, String> newArgs = Processor.newMap();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            String s = entry.getValue();
            s = this.replace(m, s);
            newArgs.put(entry.getKey(), s);
        }
        return newArgs;
    }

    String replace(Matcher m, CharSequence s) {
        StringBuilder sb = new StringBuilder();
        int max = 48 + m.groupCount() + 1;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '$' && i < s.length() - 1) {
                if ((c = s.charAt(++i)) >= '0' && c <= max) {
                    int index = c - 48;
                    String replacement = m.group(index);
                    if (replacement == null) continue;
                    sb.append(replacement);
                    continue;
                }
                if (c == '$') {
                    ++i;
                }
                sb.append(c);
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    Map<Instruction, Map<String, String>> getMakeHeader() {
        if (this.make != null) {
            return this.make;
        }
        this.make = Processor.newMap();
        Parameters make = this.builder.getMergedParameters("-make");
        for (Map.Entry<String, Attrs> entry : make.entrySet()) {
            String pattern = Processor.removeDuplicateMarker(entry.getKey());
            Instruction instr = new Instruction(pattern);
            this.make.put(instr, entry.getValue());
        }
        return this.make;
    }
}

