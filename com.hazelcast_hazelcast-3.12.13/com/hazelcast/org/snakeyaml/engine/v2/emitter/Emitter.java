/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.emitter;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.StreamDataWriter;
import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.common.ArrayStack;
import com.hazelcast.org.snakeyaml.engine.v2.common.CharConstants;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.Emitable;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.EmitterState;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.ScalarAnalysis;
import com.hazelcast.org.snakeyaml.engine.v2.events.AliasEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.CollectionStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.NodeEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.ScalarEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.EmitterException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;

public final class Emitter
implements Emitable {
    private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
    public static final int MIN_INDENT = 1;
    public static final int MAX_INDENT = 10;
    private static final String SPACE = " ";
    private static final Map<String, String> DEFAULT_TAG_PREFIXES;
    private final StreamDataWriter stream;
    private final ArrayStack<EmitterState> states;
    private EmitterState state;
    private final Queue<Event> events;
    private Event event;
    private final ArrayStack<Integer> indents;
    private Integer indent;
    private int flowLevel;
    private boolean rootContext;
    private boolean mappingContext;
    private boolean simpleKeyContext;
    private int column;
    private boolean whitespace;
    private boolean indention;
    private boolean openEnded;
    private Boolean canonical;
    private Boolean multiLineFlow;
    private boolean allowUnicode;
    private int bestIndent;
    private int indicatorIndent;
    private int bestWidth;
    private String bestLineBreak;
    private boolean splitLines;
    private int maxSimpleKeyLength;
    private Map<String, String> tagPrefixes;
    private Optional<Anchor> preparedAnchor;
    private String preparedTag;
    private ScalarAnalysis analysis;
    private Optional<ScalarStyle> scalarStyle;
    private static final Pattern HANDLE_FORMAT;

    public Emitter(DumpSettings opts, StreamDataWriter stream) {
        this.stream = stream;
        this.states = new ArrayStack(100);
        this.state = new ExpectStreamStart();
        this.events = new ArrayBlockingQueue<Event>(100);
        this.event = null;
        this.indents = new ArrayStack(10);
        this.indent = null;
        this.flowLevel = 0;
        this.mappingContext = false;
        this.simpleKeyContext = false;
        this.column = 0;
        this.whitespace = true;
        this.indention = true;
        this.openEnded = false;
        this.canonical = opts.isCanonical();
        this.multiLineFlow = opts.isMultiLineFlow();
        this.allowUnicode = opts.isUseUnicodeEncoding();
        this.bestIndent = 2;
        if (opts.getIndent() > 1 && opts.getIndent() < 10) {
            this.bestIndent = opts.getIndent();
        }
        this.indicatorIndent = opts.getIndicatorIndent();
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = opts.getBestLineBreak();
        this.splitLines = opts.isSplitLines();
        this.maxSimpleKeyLength = opts.getMaxSimpleKeyLength();
        this.tagPrefixes = new LinkedHashMap<String, String>();
        this.preparedAnchor = Optional.empty();
        this.preparedTag = null;
        this.analysis = null;
        this.scalarStyle = Optional.empty();
    }

    @Override
    public void emit(Event event) {
        this.events.add(event);
        while (!this.needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }

    private boolean needMoreEvents() {
        if (this.events.isEmpty()) {
            return true;
        }
        Event nextEvent = this.events.peek();
        if (nextEvent.getEventId() == Event.ID.DocumentStart) {
            return this.needEvents(1);
        }
        if (nextEvent.getEventId() == Event.ID.SequenceStart) {
            return this.needEvents(2);
        }
        if (nextEvent.getEventId() == Event.ID.MappingStart) {
            return this.needEvents(3);
        }
        return false;
    }

    private boolean needEvents(int count) {
        int level = 0;
        Iterator iter = this.events.iterator();
        iter.next();
        while (iter.hasNext()) {
            Event nextEvent = (Event)iter.next();
            if (nextEvent.getEventId() == Event.ID.DocumentStart || nextEvent instanceof CollectionStartEvent) {
                ++level;
            } else if (nextEvent.getEventId() == Event.ID.DocumentEnd || nextEvent instanceof CollectionEndEvent) {
                --level;
            } else if (nextEvent.getEventId() == Event.ID.StreamEnd) {
                level = -1;
            }
            if (level >= 0) continue;
            return false;
        }
        return this.events.size() < count + 1;
    }

    private void increaseIndent(boolean isFlow, boolean indentless) {
        this.indents.push(this.indent);
        if (this.indent == null) {
            this.indent = isFlow ? Integer.valueOf(this.bestIndent) : Integer.valueOf(0);
        } else if (!indentless) {
            Emitter emitter = this;
            emitter.indent = emitter.indent + this.bestIndent;
        }
    }

    private void expectNode(boolean root, boolean mapping, boolean simpleKey) {
        this.rootContext = root;
        this.mappingContext = mapping;
        this.simpleKeyContext = simpleKey;
        if (this.event.getEventId() == Event.ID.Alias) {
            this.expectAlias();
        } else if (this.event.getEventId() == Event.ID.Scalar || this.event.getEventId() == Event.ID.SequenceStart || this.event.getEventId() == Event.ID.MappingStart) {
            this.processAnchor("&");
            this.processTag();
            this.handleNodeEvent(this.event.getEventId());
        } else {
            throw new EmitterException("expected NodeEvent, but got " + this.event);
        }
    }

    private void handleNodeEvent(Event.ID id) {
        switch (id) {
            case Scalar: {
                this.expectScalar();
                break;
            }
            case SequenceStart: {
                if (this.flowLevel != 0 || this.canonical.booleanValue() || ((SequenceStartEvent)this.event).isFlow() || this.checkEmptySequence()) {
                    this.expectFlowSequence();
                    break;
                }
                this.expectBlockSequence();
                break;
            }
            case MappingStart: {
                if (this.flowLevel != 0 || this.canonical.booleanValue() || ((MappingStartEvent)this.event).isFlow() || this.checkEmptyMapping()) {
                    this.expectFlowMapping();
                    break;
                }
                this.expectBlockMapping();
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void expectAlias() {
        if (!(this.event instanceof AliasEvent)) {
            throw new EmitterException("Expecting Alias.");
        }
        this.processAnchor("*");
        this.state = this.states.pop();
    }

    private void expectScalar() {
        this.increaseIndent(true, false);
        this.processScalar();
        this.indent = this.indents.pop();
        this.state = this.states.pop();
    }

    private void expectFlowSequence() {
        this.writeIndicator("[", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.multiLineFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowSequenceItem();
    }

    private void expectFlowMapping() {
        this.writeIndicator("{", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.multiLineFlow.booleanValue()) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowMappingKey();
    }

    private void expectBlockSequence() {
        boolean indentless = this.mappingContext && !this.indention;
        this.increaseIndent(false, indentless);
        this.state = new ExpectFirstBlockSequenceItem();
    }

    private void expectBlockMapping() {
        this.increaseIndent(false, false);
        this.state = new ExpectFirstBlockMappingKey();
    }

    private boolean checkEmptySequence() {
        return this.event.getEventId() == Event.ID.SequenceStart && !this.events.isEmpty() && this.events.peek().getEventId() == Event.ID.SequenceEnd;
    }

    private boolean checkEmptyMapping() {
        return this.event.getEventId() == Event.ID.MappingStart && !this.events.isEmpty() && this.events.peek().getEventId() == Event.ID.MappingEnd;
    }

    private boolean checkSimpleKey() {
        Optional<Anchor> anchorOpt;
        int length = 0;
        if (this.event instanceof NodeEvent && (anchorOpt = ((NodeEvent)this.event).getAnchor()).isPresent()) {
            if (!this.preparedAnchor.isPresent()) {
                this.preparedAnchor = anchorOpt;
            }
            length += anchorOpt.get().getValue().length();
        }
        Optional<Object> tag = Optional.empty();
        if (this.event.getEventId() == Event.ID.Scalar) {
            tag = ((ScalarEvent)this.event).getTag();
        } else if (this.event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent)this.event).getTag();
        }
        if (tag.isPresent()) {
            if (this.preparedTag == null) {
                this.preparedTag = this.prepareTag((String)tag.get());
            }
            length += this.preparedTag.length();
        }
        if (this.event.getEventId() == Event.ID.Scalar) {
            if (this.analysis == null) {
                this.analysis = this.analyzeScalar(((ScalarEvent)this.event).getValue());
            }
            length += this.analysis.getScalar().length();
        }
        return length < this.maxSimpleKeyLength && (this.event.getEventId() == Event.ID.Alias || this.event.getEventId() == Event.ID.Scalar && !this.analysis.isEmpty() && !this.analysis.isMultiline() || this.checkEmptySequence() || this.checkEmptyMapping());
    }

    private void processAnchor(String indicator) {
        NodeEvent ev = (NodeEvent)this.event;
        Optional<Anchor> anchorOption = ev.getAnchor();
        if (anchorOption.isPresent()) {
            Anchor anchor = anchorOption.get();
            if (!this.preparedAnchor.isPresent()) {
                this.preparedAnchor = anchorOption;
            }
            this.writeIndicator(indicator + anchor, true, false, false);
        }
        this.preparedAnchor = Optional.empty();
    }

    private void processTag() {
        Optional<String> tag;
        if (this.event.getEventId() == Event.ID.Scalar) {
            ScalarEvent ev = (ScalarEvent)this.event;
            tag = ev.getTag();
            if (!this.scalarStyle.isPresent()) {
                this.scalarStyle = this.chooseScalarStyle(ev);
            }
            if ((!this.canonical.booleanValue() || !tag.isPresent()) && (!this.scalarStyle.isPresent() && ev.getImplicit().canOmitTagInPlainScalar() || this.scalarStyle.isPresent() && ev.getImplicit().canOmitTagInNonPlainScalar())) {
                this.preparedTag = null;
                return;
            }
            if (ev.getImplicit().canOmitTagInPlainScalar() && !tag.isPresent()) {
                tag = Optional.of("!");
                this.preparedTag = null;
            }
        } else {
            CollectionStartEvent ev = (CollectionStartEvent)this.event;
            tag = ev.getTag();
            if (!(this.canonical.booleanValue() && tag.isPresent() || !ev.isImplicit())) {
                this.preparedTag = null;
                return;
            }
        }
        if (!tag.isPresent()) {
            throw new EmitterException("tag is not specified");
        }
        if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag(tag.get());
        }
        this.writeIndicator(this.preparedTag, true, false, false);
        this.preparedTag = null;
    }

    private Optional<ScalarStyle> chooseScalarStyle(ScalarEvent ev) {
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        if (!ev.isPlain() && ev.getScalarStyle() == ScalarStyle.DOUBLE_QUOTED || this.canonical.booleanValue()) {
            return Optional.of(ScalarStyle.DOUBLE_QUOTED);
        }
        if (ev.isPlain() && ev.getImplicit().canOmitTagInPlainScalar() && (!this.simpleKeyContext || !this.analysis.isEmpty() && !this.analysis.isMultiline()) && (this.flowLevel != 0 && this.analysis.isAllowFlowPlain() || this.flowLevel == 0 && this.analysis.isAllowBlockPlain())) {
            return Optional.empty();
        }
        if (!(ev.isPlain() || ev.getScalarStyle() != ScalarStyle.LITERAL && ev.getScalarStyle() != ScalarStyle.FOLDED || this.flowLevel != 0 || this.simpleKeyContext || !this.analysis.isAllowBlock())) {
            return Optional.of(ev.getScalarStyle());
        }
        if (!(!ev.isPlain() && ev.getScalarStyle() != ScalarStyle.SINGLE_QUOTED || !this.analysis.isAllowSingleQuoted() || this.simpleKeyContext && this.analysis.isMultiline())) {
            return Optional.of(ScalarStyle.SINGLE_QUOTED);
        }
        return Optional.of(ScalarStyle.DOUBLE_QUOTED);
    }

    private void processScalar() {
        boolean split;
        ScalarEvent ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        if (!this.scalarStyle.isPresent()) {
            this.scalarStyle = this.chooseScalarStyle(ev);
        }
        boolean bl = split = !this.simpleKeyContext && this.splitLines;
        if (!this.scalarStyle.isPresent()) {
            this.writePlain(this.analysis.getScalar(), split);
        } else {
            switch (this.scalarStyle.get()) {
                case DOUBLE_QUOTED: {
                    this.writeDoubleQuoted(this.analysis.getScalar(), split);
                    break;
                }
                case SINGLE_QUOTED: {
                    this.writeSingleQuoted(this.analysis.getScalar(), split);
                    break;
                }
                case FOLDED: {
                    this.writeFolded(this.analysis.getScalar(), split);
                    break;
                }
                case LITERAL: {
                    this.writeLiteral(this.analysis.getScalar());
                    break;
                }
                default: {
                    throw new YamlEngineException("Unexpected scalarStyle: " + this.scalarStyle);
                }
            }
        }
        this.analysis = null;
        this.scalarStyle = Optional.empty();
    }

    private String prepareVersion(SpecVersion version) {
        if (version.getMajor() != 1) {
            throw new EmitterException("unsupported YAML version: " + version);
        }
        return version.getRepresentation();
    }

    private String prepareTagHandle(String handle) {
        if (handle.length() == 0) {
            throw new EmitterException("tag handle must not be empty");
        }
        if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!') {
            throw new EmitterException("tag handle must start and end with '!': " + handle);
        }
        if (!"!".equals(handle) && !HANDLE_FORMAT.matcher(handle).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + handle);
        }
        return handle;
    }

    private String prepareTagPrefix(String prefix) {
        if (prefix.length() == 0) {
            throw new EmitterException("tag prefix must not be empty");
        }
        StringBuilder chunks = new StringBuilder();
        int start = 0;
        int end = 0;
        if (prefix.charAt(0) == '!') {
            end = 1;
        }
        while (end < prefix.length()) {
            ++end;
        }
        if (start < end) {
            chunks.append(prefix.substring(start, end));
        }
        return chunks.toString();
    }

    private String prepareTag(String tag) {
        int end;
        String suffixText;
        if (tag.length() == 0) {
            throw new EmitterException("tag must not be empty");
        }
        if ("!".equals(tag)) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (String prefix : this.tagPrefixes.keySet()) {
            if (!tag.startsWith(prefix) || !"!".equals(prefix) && prefix.length() >= tag.length()) continue;
            handle = prefix;
        }
        if (handle != null) {
            suffix = tag.substring(handle.length());
            handle = this.tagPrefixes.get(handle);
        }
        String string = suffixText = (end = suffix.length()) > 0 ? suffix.substring(0, end) : "";
        if (handle != null) {
            return handle + suffixText;
        }
        return "!<" + suffixText + ">";
    }

    private ScalarAnalysis analyzeScalar(String scalar) {
        if (scalar.length() == 0) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, false);
        }
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;
        boolean leadingSpace = false;
        boolean leadingBreak = false;
        boolean trailingSpace = false;
        boolean trailingBreak = false;
        boolean breakSpace = false;
        boolean spaceBreak = false;
        if (scalar.startsWith("---") || scalar.startsWith("...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        boolean preceededByWhitespace = true;
        boolean followedByWhitespace = scalar.length() == 1 || CharConstants.NULL_BL_T_LINEBR.has(scalar.codePointAt(1));
        boolean previousSpace = false;
        boolean previousBreak = false;
        int index = 0;
        while (index < scalar.length()) {
            int nextIndex;
            boolean isLineBreak;
            int c = scalar.codePointAt(index);
            if (index == 0) {
                if ("#,[]{}&*!|>'\"%@`".indexOf(c) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (c == 63 || c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 45 && followedByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            } else {
                if (",?[]{}".indexOf(c) != -1) {
                    flowIndicators = true;
                }
                if (c == 58) {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (c == 35 && preceededByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            if (isLineBreak = CharConstants.LINEBR.has(c)) {
                lineBreaks = true;
            }
            if (c != 10 && (32 > c || c > 126)) {
                if (c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF) {
                    if (!this.allowUnicode) {
                        specialCharacters = true;
                    }
                } else {
                    specialCharacters = true;
                }
            }
            if (c == 32) {
                if (index == 0) {
                    leadingSpace = true;
                }
                if (index == scalar.length() - 1) {
                    trailingSpace = true;
                }
                if (previousBreak) {
                    breakSpace = true;
                }
                previousSpace = true;
                previousBreak = false;
            } else if (isLineBreak) {
                if (index == 0) {
                    leadingBreak = true;
                }
                if (index == scalar.length() - 1) {
                    trailingBreak = true;
                }
                if (previousSpace) {
                    spaceBreak = true;
                }
                previousSpace = false;
                previousBreak = true;
            } else {
                previousSpace = false;
                previousBreak = false;
            }
            preceededByWhitespace = CharConstants.NULL_BL_T.has(c) || isLineBreak;
            followedByWhitespace = true;
            if ((index += Character.charCount(c)) + 1 >= scalar.length() || (nextIndex = index + Character.charCount(scalar.codePointAt(index))) >= scalar.length()) continue;
            followedByWhitespace = CharConstants.NULL_BL_T.has(scalar.codePointAt(nextIndex)) || isLineBreak;
        }
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        boolean allowBlock = true;
        if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (trailingSpace) {
            allowBlock = false;
        }
        if (breakSpace) {
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (spaceBreak || specialCharacters) {
            allowBlock = false;
            allowSingleQuoted = false;
            allowBlockPlain = false;
            allowFlowPlain = false;
        }
        if (lineBreaks) {
            allowFlowPlain = false;
        }
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        if (blockIndicators) {
            allowBlockPlain = false;
        }
        return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowBlock);
    }

    void flushStream() {
        this.stream.flush();
    }

    void writeStreamStart() {
    }

    void writeStreamEnd() {
        this.flushStream();
    }

    void writeIndicator(String indicator, boolean needWhitespace, boolean whitespace, boolean indentation) {
        if (!this.whitespace && needWhitespace) {
            ++this.column;
            this.stream.write(SPACE);
        }
        this.whitespace = whitespace;
        this.indention = this.indention && indentation;
        this.column += indicator.length();
        this.openEnded = false;
        this.stream.write(indicator);
    }

    void writeIndent() {
        int indentToWrite = this.indent != null ? this.indent : 0;
        if (!this.indention || this.column > indentToWrite || this.column == indentToWrite && !this.whitespace) {
            this.writeLineBreak(null);
        }
        this.writeWhitespace(indentToWrite - this.column);
    }

    private void writeWhitespace(int length) {
        if (length <= 0) {
            return;
        }
        this.whitespace = true;
        for (int i = 0; i < length; ++i) {
            this.stream.write(SPACE);
        }
        this.column += length;
    }

    private void writeLineBreak(String data) {
        this.whitespace = true;
        this.indention = true;
        this.column = 0;
        if (data == null) {
            this.stream.write(this.bestLineBreak);
        } else {
            this.stream.write(data);
        }
    }

    void writeVersionDirective(String versionText) {
        this.stream.write("%YAML ");
        this.stream.write(versionText);
        this.writeLineBreak(null);
    }

    void writeTagDirective(String handleText, String prefixText) {
        this.stream.write("%TAG ");
        this.stream.write(handleText);
        this.stream.write(SPACE);
        this.stream.write(prefixText);
        this.writeLineBreak(null);
    }

    private void writeSingleQuoted(String text, boolean split) {
        this.writeIndicator("'", true, false, false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            int len;
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch == '\u0000' || ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
                        this.writeIndent();
                    } else {
                        len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    this.writeIndent();
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 '") && start < end) {
                len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch == '\'') {
                this.column += 2;
                this.stream.write("''");
                start = end + 1;
            }
            if (ch == '\u0000') continue;
            spaces = ch == ' ';
            breaks = CharConstants.LINEBR.has(ch);
        }
        this.writeIndicator("'", false, false, false);
    }

    private void writeDoubleQuoted(String text, boolean split) {
        this.writeIndicator("\"", true, false, false);
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            Character ch = null;
            if (end < text.length()) {
                ch = Character.valueOf(text.charAt(end));
            }
            if (ch == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(ch.charValue()) != -1 || ' ' > ch.charValue() || ch.charValue() > '~') {
                if (start < end) {
                    int len = end - start;
                    this.column += len;
                    this.stream.write(text, start, len);
                    start = end;
                }
                if (ch != null) {
                    String data;
                    if (ESCAPE_REPLACEMENTS.containsKey(ch)) {
                        data = "\\" + ESCAPE_REPLACEMENTS.get(ch);
                    } else if (!this.allowUnicode || !StreamReader.isPrintable(ch.charValue())) {
                        String s;
                        if (ch.charValue() <= '\u00ff') {
                            s = "0" + Integer.toString(ch.charValue(), 16);
                            data = "\\x" + s.substring(s.length() - 2);
                        } else if (ch.charValue() >= '\ud800' && ch.charValue() <= '\udbff') {
                            if (end + 1 < text.length()) {
                                Character ch2 = Character.valueOf(text.charAt(++end));
                                String s2 = "000" + Long.toHexString(Character.toCodePoint(ch.charValue(), ch2.charValue()));
                                data = "\\U" + s2.substring(s2.length() - 8);
                            } else {
                                s = "000" + Integer.toString(ch.charValue(), 16);
                                data = "\\u" + s.substring(s.length() - 4);
                            }
                        } else {
                            s = "000" + Integer.toString(ch.charValue(), 16);
                            data = "\\u" + s.substring(s.length() - 4);
                        }
                    } else {
                        data = String.valueOf(ch);
                    }
                    this.column += data.length();
                    this.stream.write(data);
                    start = end + 1;
                }
            }
            if (0 >= end || end >= text.length() - 1 || ch.charValue() != ' ' && start < end || this.column + (end - start) <= this.bestWidth || !split) continue;
            String data = start >= end ? "\\" : text.substring(start, end) + "\\";
            if (start < end) {
                start = end;
            }
            this.column += data.length();
            this.stream.write(data);
            this.writeIndent();
            this.whitespace = false;
            this.indention = false;
            if (text.charAt(start) != ' ') continue;
            data = "\\";
            this.column += data.length();
            this.stream.write(data);
        }
        this.writeIndicator("\"", false, false, false);
    }

    private String determineBlockHints(String text) {
        char ch1;
        StringBuilder hints = new StringBuilder();
        if (CharConstants.LINEBR.has(text.charAt(0), SPACE)) {
            hints.append(this.bestIndent);
        }
        if (CharConstants.LINEBR.hasNo(ch1 = text.charAt(text.length() - 1))) {
            hints.append("-");
        } else if (text.length() == 1 || CharConstants.LINEBR.has(text.charAt(text.length() - 2))) {
            hints.append("+");
        }
        return hints.toString();
    }

    void writeFolded(String text, boolean split) {
        String hints = this.determineBlockHints(text);
        this.writeIndicator(">" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean leadingSpace = true;
        boolean spaces = false;
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    if (!leadingSpace && ch != '\u0000' && ch != ' ' && text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    leadingSpace = ch == ' ';
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    if (ch != '\u0000') {
                        this.writeIndent();
                    }
                    start = end;
                }
            } else if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        this.writeIndent();
                    } else {
                        int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 ")) {
                int len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                if (ch == '\u0000') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch == '\u0000') continue;
            breaks = CharConstants.LINEBR.has(ch);
            spaces = ch == ' ';
        }
    }

    void writeLiteral(String text) {
        String hints = this.determineBlockHints(text);
        this.writeIndicator("|" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\u0000' || CharConstants.LINEBR.hasNo(ch)) {
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    if (ch != '\u0000') {
                        this.writeIndent();
                    }
                    start = end;
                }
            } else if (ch == '\u0000' || CharConstants.LINEBR.has(ch)) {
                this.stream.write(text, start, end - start);
                if (ch == '\u0000') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch == '\u0000') continue;
            breaks = CharConstants.LINEBR.has(ch);
        }
    }

    void writePlain(String text, boolean split) {
        if (this.rootContext) {
            this.openEnded = true;
        }
        if (text.length() == 0) {
            return;
        }
        if (!this.whitespace) {
            ++this.column;
            this.stream.write(SPACE);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            int len;
            char ch = '\u0000';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        this.writeIndent();
                        this.whitespace = false;
                        this.indention = false;
                    } else {
                        len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            } else if (breaks) {
                if (CharConstants.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    String data = text.substring(start, end);
                    for (char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                            continue;
                        }
                        this.writeLineBreak(String.valueOf(br));
                    }
                    this.writeIndent();
                    this.whitespace = false;
                    this.indention = false;
                    start = end;
                }
            } else if (CharConstants.LINEBR.has(ch, "\u0000 ")) {
                len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch == '\u0000') continue;
            spaces = ch == ' ';
            breaks = CharConstants.LINEBR.has(ch);
        }
    }

    static {
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0000'), "0");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0007'), "a");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\b'), "b");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\t'), "t");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\n'), "n");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u000b'), "v");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\f'), "f");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\r'), "r");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u001b'), "e");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\"'), "\"");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\\'), "\\");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u0085'), "N");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u00a0'), "_");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u2028'), "L");
        ESCAPE_REPLACEMENTS.put(Character.valueOf('\u2029'), "P");
        DEFAULT_TAG_PREFIXES = new LinkedHashMap<String, String>();
        DEFAULT_TAG_PREFIXES.put("!", "!");
        DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
        HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
    }

    private class ExpectBlockMappingValue
    implements EmitterState {
        private ExpectBlockMappingValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndent();
            Emitter.this.writeIndicator(":", true, false, true);
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
        }
    }

    private class ExpectBlockMappingSimpleValue
    implements EmitterState {
        private ExpectBlockMappingSimpleValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, true, false);
        }
    }

    private class ExpectBlockMappingKey
    implements EmitterState {
        private boolean first;

        public ExpectBlockMappingKey(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            if (!this.first && Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndent();
                if (Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectBlockMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, true);
                    Emitter.this.states.push(new ExpectBlockMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectFirstBlockMappingKey
    implements EmitterState {
        private ExpectFirstBlockMappingKey() {
        }

        @Override
        public void expect() {
            new ExpectBlockMappingKey(true).expect();
        }
    }

    private class ExpectBlockSequenceItem
    implements EmitterState {
        private boolean first;

        public ExpectBlockSequenceItem(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            if (!this.first && Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndent();
                Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
                Emitter.this.writeIndicator("-", true, false, true);
                Emitter.this.states.push(new ExpectBlockSequenceItem(false));
                Emitter.this.expectNode(false, false, false);
            }
        }
    }

    private class ExpectFirstBlockSequenceItem
    implements EmitterState {
        private ExpectFirstBlockSequenceItem() {
        }

        @Override
        public void expect() {
            new ExpectBlockSequenceItem(true).expect();
        }
    }

    private class ExpectFlowMappingValue
    implements EmitterState {
        private ExpectFlowMappingValue() {
        }

        @Override
        public void expect() {
            if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.multiLineFlow.booleanValue()) {
                Emitter.this.writeIndent();
            }
            Emitter.this.writeIndicator(":", true, false, false);
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
        }
    }

    private class ExpectFlowMappingSimpleValue
    implements EmitterState {
        private ExpectFlowMappingSimpleValue() {
        }

        @Override
        public void expect() {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, true, false);
        }
    }

    private class ExpectFlowMappingKey
    implements EmitterState {
        private ExpectFlowMappingKey() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                if (Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndicator(",", false, false, false);
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectFirstFlowMappingKey
    implements EmitterState {
        private ExpectFirstFlowMappingKey() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.MappingEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical.booleanValue() && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, true, true);
                } else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, true, false);
                }
            }
        }
    }

    private class ExpectFlowSequenceItem
    implements EmitterState {
        private ExpectFlowSequenceItem() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("]", false, false, false);
                if (Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                Emitter.this.writeIndicator(",", false, false, false);
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
            }
        }
    }

    private class ExpectFirstFlowSequenceItem
    implements EmitterState {
        private ExpectFirstFlowSequenceItem() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.SequenceEnd) {
                Emitter.this.indent = (Integer)Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("]", false, false, false);
                Emitter.this.state = (EmitterState)Emitter.this.states.pop();
            } else {
                if (Emitter.this.canonical.booleanValue() || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.multiLineFlow.booleanValue()) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, false, false);
            }
        }
    }

    private class ExpectDocumentRoot
    implements EmitterState {
        private ExpectDocumentRoot() {
        }

        @Override
        public void expect() {
            Emitter.this.states.push(new ExpectDocumentEnd());
            Emitter.this.expectNode(true, false, false);
        }
    }

    private class ExpectDocumentEnd
    implements EmitterState {
        private ExpectDocumentEnd() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.DocumentEnd) {
                Emitter.this.writeIndent();
                if (((DocumentEndEvent)Emitter.this.event).isExplicit()) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
            } else {
                throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
            }
            Emitter.this.flushStream();
            Emitter.this.state = new ExpectDocumentStart(false);
        }
    }

    private class ExpectDocumentStart
    implements EmitterState {
        private boolean first;

        public ExpectDocumentStart(boolean first) {
            this.first = first;
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() == Event.ID.DocumentStart) {
                DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
                this.handleDocumentStartEvent(ev);
                Emitter.this.state = new ExpectDocumentRoot();
            } else if (Emitter.this.event.getEventId() == Event.ID.StreamEnd) {
                Emitter.this.writeStreamEnd();
                Emitter.this.state = new ExpectNothing();
            } else {
                throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
            }
        }

        private void handleDocumentStartEvent(DocumentStartEvent ev) {
            boolean implicit;
            if ((ev.getSpecVersion().isPresent() || !ev.getTags().isEmpty()) && Emitter.this.openEnded) {
                Emitter.this.writeIndicator("...", true, false, false);
                Emitter.this.writeIndent();
            }
            ev.getSpecVersion().ifPresent(version -> Emitter.this.writeVersionDirective(Emitter.this.prepareVersion(version)));
            Emitter.this.tagPrefixes = new LinkedHashMap(DEFAULT_TAG_PREFIXES);
            if (!ev.getTags().isEmpty()) {
                this.handleTagDirectives(ev.getTags());
            }
            boolean bl = implicit = this.first && !ev.isExplicit() && Emitter.this.canonical == false && !ev.getSpecVersion().isPresent() && ev.getTags().isEmpty() && !this.checkEmptyDocument();
            if (!implicit) {
                Emitter.this.writeIndent();
                Emitter.this.writeIndicator("---", true, false, false);
                if (Emitter.this.canonical.booleanValue()) {
                    Emitter.this.writeIndent();
                }
            }
        }

        private void handleTagDirectives(Map<String, String> tags) {
            TreeSet<String> handles = new TreeSet<String>(tags.keySet());
            for (String handle : handles) {
                String prefix = tags.get(handle);
                Emitter.this.tagPrefixes.put(prefix, handle);
                String handleText = Emitter.this.prepareTagHandle(handle);
                String prefixText = Emitter.this.prepareTagPrefix(prefix);
                Emitter.this.writeTagDirective(handleText, prefixText);
            }
        }

        private boolean checkEmptyDocument() {
            if (Emitter.this.event.getEventId() != Event.ID.DocumentStart || Emitter.this.events.isEmpty()) {
                return false;
            }
            Event nextEvent = (Event)Emitter.this.events.peek();
            if (nextEvent.getEventId() == Event.ID.Scalar) {
                ScalarEvent e = (ScalarEvent)nextEvent;
                return !e.getAnchor().isPresent() && !e.getTag().isPresent() && e.getImplicit() != null && e.getValue().length() == 0;
            }
            return false;
        }
    }

    private class ExpectFirstDocumentStart
    implements EmitterState {
        private ExpectFirstDocumentStart() {
        }

        @Override
        public void expect() {
            new ExpectDocumentStart(true).expect();
        }
    }

    private class ExpectNothing
    implements EmitterState {
        private ExpectNothing() {
        }

        @Override
        public void expect() {
            throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
        }
    }

    private class ExpectStreamStart
    implements EmitterState {
        private ExpectStreamStart() {
        }

        @Override
        public void expect() {
            if (Emitter.this.event.getEventId() != Event.ID.StreamStart) {
                throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
            }
            Emitter.this.writeStreamStart();
            Emitter.this.state = new ExpectFirstDocumentStart();
        }
    }
}

