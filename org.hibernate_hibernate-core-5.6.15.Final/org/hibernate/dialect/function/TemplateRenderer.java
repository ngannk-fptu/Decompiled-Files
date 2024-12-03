/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect.function;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class TemplateRenderer {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TemplateRenderer.class.getName());
    private final String template;
    private final String[] chunks;
    private final int[] paramIndexes;

    public TemplateRenderer(String template) {
        int i;
        this.template = template;
        ArrayList<String> chunkList = new ArrayList<String>();
        ArrayList<Integer> paramList = new ArrayList<Integer>();
        StringBuilder chunk = new StringBuilder(10);
        StringBuilder index = new StringBuilder(2);
        int len = template.length();
        for (i = 0; i < len; ++i) {
            char c = template.charAt(i);
            if (c == '?') {
                chunkList.add(chunk.toString());
                chunk.delete(0, chunk.length());
                while (++i < template.length()) {
                    c = template.charAt(i);
                    if (Character.isDigit(c)) {
                        index.append(c);
                        continue;
                    }
                    chunk.append(c);
                    break;
                }
                paramList.add(Integer.valueOf(index.toString()));
                index.delete(0, index.length());
                continue;
            }
            chunk.append(c);
        }
        if (chunk.length() > 0) {
            chunkList.add(chunk.toString());
        }
        this.chunks = chunkList.toArray(new String[chunkList.size()]);
        this.paramIndexes = new int[paramList.size()];
        for (i = 0; i < this.paramIndexes.length; ++i) {
            this.paramIndexes[i] = (Integer)paramList.get(i);
        }
    }

    public String getTemplate() {
        return this.template;
    }

    public int getAnticipatedNumberOfArguments() {
        return this.paramIndexes.length;
    }

    public String render(List args, SessionFactoryImplementor factory) {
        int numberOfArguments = args.size();
        if (this.getAnticipatedNumberOfArguments() > 0 && numberOfArguments != this.getAnticipatedNumberOfArguments()) {
            LOG.missingArguments(this.getAnticipatedNumberOfArguments(), numberOfArguments);
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.chunks.length; ++i) {
            if (i < this.paramIndexes.length) {
                Object arg;
                int index = this.paramIndexes[i] - 1;
                Object v0 = arg = index < numberOfArguments ? args.get(index) : null;
                if (arg == null) continue;
                buf.append(this.chunks[i]).append((Object)arg);
                continue;
            }
            buf.append(this.chunks[i]);
        }
        return buf.toString();
    }
}

