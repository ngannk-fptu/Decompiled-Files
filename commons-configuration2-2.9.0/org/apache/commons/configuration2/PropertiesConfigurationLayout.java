/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.lang3.StringUtils;

public class PropertiesConfigurationLayout
implements EventListener<ConfigurationEvent> {
    private static final String CR = "\n";
    private static final String COMMENT_PREFIX = "# ";
    private final Map<String, PropertyLayoutData> layoutData;
    private String headerComment;
    private String footerComment;
    private String globalSeparator;
    private String lineSeparator;
    private final AtomicInteger loadCounter;
    private boolean forceSingleLine;
    private final ArrayDeque<URL> seenStack = new ArrayDeque();

    public PropertiesConfigurationLayout() {
        this(null);
    }

    public PropertiesConfigurationLayout(PropertiesConfigurationLayout c) {
        this.loadCounter = new AtomicInteger();
        this.layoutData = new LinkedHashMap<String, PropertyLayoutData>();
        if (c != null) {
            this.copyFrom(c);
        }
    }

    public String getCanonicalComment(String key, boolean commentChar) {
        return PropertiesConfigurationLayout.constructCanonicalComment(this.getComment(key), commentChar);
    }

    public String getComment(String key) {
        return this.fetchLayoutData(key).getComment();
    }

    public void setComment(String key, String comment) {
        this.fetchLayoutData(key).setComment(comment);
    }

    @Deprecated
    public int getBlancLinesBefore(String key) {
        return this.getBlankLinesBefore(key);
    }

    public int getBlankLinesBefore(String key) {
        return this.fetchLayoutData(key).getBlankLines();
    }

    @Deprecated
    public void setBlancLinesBefore(String key, int number) {
        this.setBlankLinesBefore(key, number);
    }

    public void setBlankLinesBefore(String key, int number) {
        this.fetchLayoutData(key).setBlankLines(number);
    }

    public String getCanonicalHeaderComment(boolean commentChar) {
        return PropertiesConfigurationLayout.constructCanonicalComment(this.getHeaderComment(), commentChar);
    }

    public String getHeaderComment() {
        return this.headerComment;
    }

    public void setHeaderComment(String comment) {
        this.headerComment = comment;
    }

    public String getCanonicalFooterCooment(boolean commentChar) {
        return PropertiesConfigurationLayout.constructCanonicalComment(this.getFooterComment(), commentChar);
    }

    public String getFooterComment() {
        return this.footerComment;
    }

    public void setFooterComment(String footerComment) {
        this.footerComment = footerComment;
    }

    public boolean isSingleLine(String key) {
        return this.fetchLayoutData(key).isSingleLine();
    }

    public void setSingleLine(String key, boolean f) {
        this.fetchLayoutData(key).setSingleLine(f);
    }

    public boolean isForceSingleLine() {
        return this.forceSingleLine;
    }

    public void setForceSingleLine(boolean f) {
        this.forceSingleLine = f;
    }

    public String getSeparator(String key) {
        return this.fetchLayoutData(key).getSeparator();
    }

    public void setSeparator(String key, String sep) {
        this.fetchLayoutData(key).setSeparator(sep);
    }

    public String getGlobalSeparator() {
        return this.globalSeparator;
    }

    public void setGlobalSeparator(String globalSeparator) {
        this.globalSeparator = globalSeparator;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public Set<String> getKeys() {
        return this.layoutData.keySet();
    }

    public void load(PropertiesConfiguration config, Reader reader) throws ConfigurationException {
        this.loadCounter.incrementAndGet();
        PropertiesConfiguration.PropertiesReader pReader = config.getIOFactory().createPropertiesReader(reader);
        try {
            while (pReader.nextProperty()) {
                if (!config.propertyLoaded(pReader.getPropertyName(), pReader.getPropertyValue(), this.seenStack)) continue;
                boolean contained = this.layoutData.containsKey(pReader.getPropertyName());
                int blankLines = 0;
                int idx = this.checkHeaderComment(pReader.getCommentLines());
                while (idx < pReader.getCommentLines().size() && StringUtils.isEmpty((CharSequence)pReader.getCommentLines().get(idx))) {
                    ++idx;
                    ++blankLines;
                }
                String comment = this.extractComment(pReader.getCommentLines(), idx, pReader.getCommentLines().size() - 1);
                PropertyLayoutData data = this.fetchLayoutData(pReader.getPropertyName());
                if (contained) {
                    data.addComment(comment);
                    data.setSingleLine(false);
                    continue;
                }
                data.setComment(comment);
                data.setBlankLines(blankLines);
                data.setSeparator(pReader.getPropertySeparator());
            }
            this.setFooterComment(this.extractComment(pReader.getCommentLines(), 0, pReader.getCommentLines().size() - 1));
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
        finally {
            this.loadCounter.decrementAndGet();
        }
    }

    public void save(PropertiesConfiguration config, Writer writer) throws ConfigurationException {
        try {
            PropertiesConfiguration.PropertiesWriter pWriter = config.getIOFactory().createPropertiesWriter(writer, config.getListDelimiterHandler());
            pWriter.setGlobalSeparator(this.getGlobalSeparator());
            if (this.getLineSeparator() != null) {
                pWriter.setLineSeparator(this.getLineSeparator());
            }
            if (this.headerComment != null) {
                PropertiesConfigurationLayout.writeComment(pWriter, this.getCanonicalHeaderComment(true));
            }
            boolean firstKey = true;
            for (String key : this.getKeys()) {
                if (config.containsKeyInternal(key)) {
                    if (firstKey && this.headerComment != null && this.getBlankLinesBefore(key) == 0) {
                        pWriter.writeln(null);
                    }
                    for (int i = 0; i < this.getBlankLinesBefore(key); ++i) {
                        pWriter.writeln(null);
                    }
                    PropertiesConfigurationLayout.writeComment(pWriter, this.getCanonicalComment(key, true));
                    boolean singleLine = this.isForceSingleLine() || this.isSingleLine(key);
                    pWriter.setCurrentSeparator(this.getSeparator(key));
                    pWriter.writeProperty(key, config.getPropertyInternal(key), singleLine);
                }
                firstKey = false;
            }
            PropertiesConfigurationLayout.writeComment(pWriter, this.getCanonicalFooterCooment(true));
            pWriter.flush();
        }
        catch (IOException ioex) {
            throw new ConfigurationException(ioex);
        }
    }

    @Override
    public void onEvent(ConfigurationEvent event) {
        if (!event.isBeforeUpdate() && this.loadCounter.get() == 0) {
            if (ConfigurationEvent.ADD_PROPERTY.equals(event.getEventType())) {
                boolean contained = this.layoutData.containsKey(event.getPropertyName());
                PropertyLayoutData data = this.fetchLayoutData(event.getPropertyName());
                data.setSingleLine(!contained);
            } else if (ConfigurationEvent.CLEAR_PROPERTY.equals(event.getEventType())) {
                this.layoutData.remove(event.getPropertyName());
            } else if (ConfigurationEvent.CLEAR.equals(event.getEventType())) {
                this.clear();
            } else if (ConfigurationEvent.SET_PROPERTY.equals(event.getEventType())) {
                this.fetchLayoutData(event.getPropertyName());
            }
        }
    }

    private PropertyLayoutData fetchLayoutData(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Property key must not be null!");
        }
        PropertyLayoutData data = this.layoutData.get(key);
        if (data == null) {
            data = new PropertyLayoutData();
            data.setSingleLine(true);
            this.layoutData.put(key, data);
        }
        return data;
    }

    private void clear() {
        this.seenStack.clear();
        this.layoutData.clear();
        this.setHeaderComment(null);
        this.setFooterComment(null);
    }

    static boolean isCommentLine(String line) {
        return PropertiesConfiguration.isCommentLine(line);
    }

    static String trimComment(String s, boolean comment) {
        int pos;
        StringBuilder buf = new StringBuilder(s.length());
        int lastPos = 0;
        do {
            if ((pos = s.indexOf(CR, lastPos)) < 0) continue;
            String line = s.substring(lastPos, pos);
            buf.append(PropertiesConfigurationLayout.stripCommentChar(line, comment)).append(CR);
            lastPos = pos + CR.length();
        } while (pos >= 0);
        if (lastPos < s.length()) {
            buf.append(PropertiesConfigurationLayout.stripCommentChar(s.substring(lastPos), comment));
        }
        return buf.toString();
    }

    static String stripCommentChar(String s, boolean comment) {
        if (StringUtils.isBlank((CharSequence)s) || PropertiesConfigurationLayout.isCommentLine(s) == comment) {
            return s;
        }
        if (!comment) {
            int pos = 0;
            while ("#!".indexOf(s.charAt(pos)) < 0) {
                ++pos;
            }
            ++pos;
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                ++pos;
            }
            return pos < s.length() ? s.substring(pos) : "";
        }
        return COMMENT_PREFIX + s;
    }

    private String extractComment(List<String> commentLines, int from, int to) {
        if (to < from) {
            return null;
        }
        StringBuilder buf = new StringBuilder(commentLines.get(from));
        for (int i = from + 1; i <= to; ++i) {
            buf.append(CR);
            buf.append(commentLines.get(i));
        }
        return buf.toString();
    }

    private int checkHeaderComment(List<String> commentLines) {
        if (this.loadCounter.get() == 1 && this.layoutData.isEmpty()) {
            int index;
            for (index = commentLines.size() - 1; index >= 0 && StringUtils.isNotEmpty((CharSequence)commentLines.get(index)); --index) {
            }
            while (index >= 0 && StringUtils.isEmpty((CharSequence)commentLines.get(index))) {
                --index;
            }
            if (this.getHeaderComment() == null) {
                this.setHeaderComment(this.extractComment(commentLines, 0, index));
            }
            return index + 1;
        }
        return 0;
    }

    private void copyFrom(PropertiesConfigurationLayout c) {
        c.getKeys().forEach(key -> this.layoutData.put((String)key, c.layoutData.get(key).clone()));
        this.setHeaderComment(c.getHeaderComment());
        this.setFooterComment(c.getFooterComment());
    }

    private static void writeComment(PropertiesConfiguration.PropertiesWriter writer, String comment) throws IOException {
        if (comment != null) {
            writer.writeln(StringUtils.replace((String)comment, (String)CR, (String)writer.getLineSeparator()));
        }
    }

    private static String constructCanonicalComment(String comment, boolean commentChar) {
        return comment == null ? null : PropertiesConfigurationLayout.trimComment(comment, commentChar);
    }

    static class PropertyLayoutData
    implements Cloneable {
        private StringBuffer comment;
        private String separator = " = ";
        private int blankLines;
        private boolean singleLine = true;

        @Deprecated
        public int getBlancLines() {
            return this.getBlankLines();
        }

        public int getBlankLines() {
            return this.blankLines;
        }

        @Deprecated
        public void setBlancLines(int blankLines) {
            this.setBlankLines(blankLines);
        }

        public void setBlankLines(int blankLines) {
            this.blankLines = blankLines;
        }

        public boolean isSingleLine() {
            return this.singleLine;
        }

        public void setSingleLine(boolean singleLine) {
            this.singleLine = singleLine;
        }

        public void addComment(String s) {
            if (s != null) {
                if (this.comment == null) {
                    this.comment = new StringBuffer(s);
                } else {
                    this.comment.append(PropertiesConfigurationLayout.CR).append(s);
                }
            }
        }

        public void setComment(String s) {
            this.comment = s == null ? null : new StringBuffer(s);
        }

        public String getComment() {
            return Objects.toString(this.comment, null);
        }

        public String getSeparator() {
            return this.separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public PropertyLayoutData clone() {
            try {
                PropertyLayoutData copy = (PropertyLayoutData)super.clone();
                if (this.comment != null) {
                    copy.comment = new StringBuffer(this.getComment());
                }
                return copy;
            }
            catch (CloneNotSupportedException cnex) {
                throw new ConfigurationRuntimeException(cnex);
            }
        }
    }
}

