/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.parser;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.common.ArrayStack;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.SpecVersion;
import com.hazelcast.org.snakeyaml.engine.v2.events.AliasEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.events.ImplicitTuple;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.NodeEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.ScalarEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.StreamEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.StreamStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ParserException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.parser.Parser;
import com.hazelcast.org.snakeyaml.engine.v2.parser.Production;
import com.hazelcast.org.snakeyaml.engine.v2.parser.VersionTagsTuple;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.Scanner;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.ScannerImpl;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.AliasToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.AnchorToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.BlockEntryToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.DirectiveToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.ScalarToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.StreamEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.StreamStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.TagToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.TagTuple;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ParserImpl
implements Parser {
    private static final Map<String, String> DEFAULT_TAGS = new HashMap<String, String>();
    protected final Scanner scanner;
    private final LoadSettings settings;
    private Optional<Event> currentEvent;
    private final ArrayStack<Production> states;
    private final ArrayStack<Optional<Mark>> marksStack;
    private Optional<Production> state;
    private VersionTagsTuple directives;

    public ParserImpl(StreamReader reader, LoadSettings settings) {
        this(new ScannerImpl(reader), settings);
    }

    public ParserImpl(Scanner scanner, LoadSettings settings) {
        this.scanner = scanner;
        this.settings = settings;
        this.currentEvent = Optional.empty();
        this.directives = new VersionTagsTuple(Optional.empty(), new HashMap<String, String>(DEFAULT_TAGS));
        this.states = new ArrayStack(100);
        this.marksStack = new ArrayStack(10);
        this.state = Optional.of(new ParseStreamStart());
    }

    @Override
    public boolean checkEvent(Event.ID choice) {
        this.peekEvent();
        return this.currentEvent.isPresent() && this.currentEvent.get().getEventId() == choice;
    }

    private void produce() {
        if (!this.currentEvent.isPresent()) {
            this.state.ifPresent(prod -> {
                this.currentEvent = Optional.of(prod.produce());
            });
        }
    }

    @Override
    public Event peekEvent() {
        this.produce();
        return this.currentEvent.orElseThrow(() -> new NoSuchElementException("No more Events found."));
    }

    @Override
    public Event next() {
        this.peekEvent();
        Event value = this.currentEvent.orElseThrow(() -> new NoSuchElementException("No more Events found."));
        this.currentEvent = Optional.empty();
        return value;
    }

    @Override
    public boolean hasNext() {
        this.produce();
        return this.currentEvent.isPresent();
    }

    private VersionTagsTuple processDirectives() {
        Optional<SpecVersion> yamlSpecVersion = Optional.empty();
        HashMap<String, String> tagHandles = new HashMap<String, String>();
        while (this.scanner.checkToken(Token.ID.Directive)) {
            List value;
            DirectiveToken token = (DirectiveToken)this.scanner.next();
            Optional dirOption = token.getValue();
            if (!dirOption.isPresent()) continue;
            List directiveValue = dirOption.get();
            if (token.getName().equals("YAML")) {
                if (yamlSpecVersion.isPresent()) {
                    throw new ParserException("found duplicate YAML directive", token.getStartMark());
                }
                value = directiveValue;
                Integer major = (Integer)value.get(0);
                Integer minor = (Integer)value.get(1);
                yamlSpecVersion = Optional.of(this.settings.getVersionFunction().apply(new SpecVersion(major, minor)));
                continue;
            }
            if (!token.getName().equals("TAG")) continue;
            value = directiveValue;
            String handle = (String)value.get(0);
            String prefix = (String)value.get(1);
            if (tagHandles.containsKey(handle)) {
                throw new ParserException("duplicate tag handle " + handle, token.getStartMark());
            }
            tagHandles.put(handle, prefix);
        }
        if (!yamlSpecVersion.isPresent() || !tagHandles.isEmpty()) {
            for (Map.Entry<String, String> entry : DEFAULT_TAGS.entrySet()) {
                if (tagHandles.containsKey(entry.getKey())) continue;
                tagHandles.put(entry.getKey(), entry.getValue());
            }
            this.directives = new VersionTagsTuple(yamlSpecVersion, tagHandles);
        }
        return this.directives;
    }

    private Event parseFlowNode() {
        return this.parseNode(false, false);
    }

    private Event parseBlockNodeOrIndentlessSequence() {
        return this.parseNode(true, true);
    }

    private Event parseNode(boolean block, boolean indentlessSequence) {
        NodeEvent event;
        Optional<Object> startMark = Optional.empty();
        Optional<Mark> endMark = Optional.empty();
        Optional<Mark> tagMark = Optional.empty();
        if (this.scanner.checkToken(Token.ID.Alias)) {
            AliasToken token = (AliasToken)this.scanner.next();
            event = new AliasEvent(Optional.of(token.getValue()), token.getStartMark(), token.getEndMark());
            this.state = Optional.of(this.states.pop());
        } else {
            boolean implicit;
            Optional<Anchor> anchor = Optional.empty();
            TagTuple tagTupleValue = null;
            if (this.scanner.checkToken(Token.ID.Anchor)) {
                AnchorToken token = (AnchorToken)this.scanner.next();
                startMark = token.getStartMark();
                endMark = token.getEndMark();
                anchor = Optional.of(token.getValue());
                if (this.scanner.checkToken(Token.ID.Tag)) {
                    TagToken tagToken = (TagToken)this.scanner.next();
                    tagMark = tagToken.getStartMark();
                    endMark = tagToken.getEndMark();
                    tagTupleValue = tagToken.getValue();
                }
            } else if (this.scanner.checkToken(Token.ID.Tag)) {
                TagToken tagToken = (TagToken)this.scanner.next();
                startMark = tagToken.getStartMark();
                tagMark = startMark;
                endMark = tagToken.getEndMark();
                tagTupleValue = tagToken.getValue();
                if (this.scanner.checkToken(Token.ID.Anchor)) {
                    AnchorToken token = (AnchorToken)this.scanner.next();
                    endMark = token.getEndMark();
                    anchor = Optional.of(token.getValue());
                }
            }
            Optional<Object> tag = Optional.empty();
            if (tagTupleValue != null) {
                String handle = tagTupleValue.getHandle();
                String suffix = tagTupleValue.getSuffix();
                if (handle != null) {
                    if (!this.directives.getTags().containsKey(handle)) {
                        throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
                    }
                    tag = Optional.of(this.directives.getTags().get(handle) + suffix);
                } else {
                    tag = Optional.of(suffix);
                }
            }
            if (!startMark.isPresent()) {
                startMark = this.scanner.peekToken().getStartMark();
                endMark = startMark;
            }
            boolean bl = implicit = !tag.isPresent();
            if (indentlessSequence && this.scanner.checkToken(Token.ID.BlockEntry)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
                this.state = Optional.of(new ParseIndentlessSequenceEntry());
            } else if (this.scanner.checkToken(Token.ID.Scalar)) {
                ScalarToken token = (ScalarToken)this.scanner.next();
                endMark = token.getEndMark();
                ImplicitTuple implicitValues = token.isPlain() && !tag.isPresent() ? new ImplicitTuple(true, false) : (!tag.isPresent() ? new ImplicitTuple(false, true) : new ImplicitTuple(false, false));
                event = new ScalarEvent(anchor, tag, implicitValues, token.getValue(), token.getStyle(), startMark, endMark);
                this.state = Optional.of(this.states.pop());
            } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, FlowStyle.FLOW, startMark, endMark);
                this.state = Optional.of(new ParseFlowSequenceFirstEntry());
            } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new MappingStartEvent(anchor, tag, implicit, FlowStyle.FLOW, startMark, endMark);
                this.state = Optional.of(new ParseFlowMappingFirstKey());
            } else if (block && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new SequenceStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
                this.state = Optional.of(new ParseBlockSequenceFirstEntry());
            } else if (block && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new MappingStartEvent(anchor, tag, implicit, FlowStyle.BLOCK, startMark, endMark);
                this.state = Optional.of(new ParseBlockMappingFirstKey());
            } else if (anchor.isPresent() || tag.isPresent()) {
                event = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", ScalarStyle.PLAIN, startMark, endMark);
                this.state = Optional.of(this.states.pop());
            } else {
                String node = block ? "block" : "flow";
                Token token = this.scanner.peekToken();
                throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found '" + (Object)((Object)token.getTokenId()) + "'", token.getStartMark());
            }
        }
        return event;
    }

    private Event processEmptyScalar(Optional<Mark> mark) {
        return new ScalarEvent(Optional.empty(), Optional.empty(), new ImplicitTuple(true, false), "", ScalarStyle.PLAIN, mark, mark);
    }

    private Optional<Mark> markPop() {
        return this.marksStack.pop();
    }

    private void markPush(Optional<Mark> mark) {
        this.marksStack.push(mark);
    }

    static {
        DEFAULT_TAGS.put("!", "!");
        DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
    }

    private class ParseFlowMappingEmptyValue
    implements Production {
        private ParseFlowMappingEmptyValue() {
        }

        @Override
        public Event produce() {
            ParserImpl.this.state = Optional.of(new ParseFlowMappingKey(false));
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
        }
    }

    private class ParseFlowMappingValue
    implements Production {
        private ParseFlowMappingValue() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                Token token = ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                    ParserImpl.this.states.push(new ParseFlowMappingKey(false));
                    return ParserImpl.this.parseFlowNode();
                }
                ParserImpl.this.state = Optional.of(new ParseFlowMappingKey(false));
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            ParserImpl.this.state = Optional.of(new ParseFlowMappingKey(false));
            Token token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
        }
    }

    private class ParseFlowMappingKey
    implements Production {
        private final boolean first;

        public ParseFlowMappingKey(boolean first) {
            this.first = first;
        }

        @Override
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                if (!this.first) {
                    if (ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        ParserImpl.this.scanner.next();
                    } else {
                        Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow mapping", ParserImpl.this.markPop(), "expected ',' or '}', but got " + (Object)((Object)token.getTokenId()), token.getStartMark());
                    }
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    Token token = ParserImpl.this.scanner.next();
                    if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                        ParserImpl.this.states.push(new ParseFlowMappingValue());
                        return ParserImpl.this.parseFlowNode();
                    }
                    ParserImpl.this.state = Optional.of(new ParseFlowMappingValue());
                    return ParserImpl.this.processEmptyScalar(token.getEndMark());
                }
                if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                    ParserImpl.this.states.push(new ParseFlowMappingEmptyValue());
                    return ParserImpl.this.parseFlowNode();
                }
            }
            Token token = ParserImpl.this.scanner.next();
            MappingEndEvent event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
            ParserImpl.this.markPop();
            return event;
        }
    }

    private class ParseFlowMappingFirstKey
    implements Production {
        private ParseFlowMappingFirstKey() {
        }

        @Override
        public Event produce() {
            Token token = ParserImpl.this.scanner.next();
            ParserImpl.this.markPush(token.getStartMark());
            return new ParseFlowMappingKey(true).produce();
        }
    }

    private class ParseFlowSequenceEntryMappingEnd
    implements Production {
        private ParseFlowSequenceEntryMappingEnd() {
        }

        @Override
        public Event produce() {
            ParserImpl.this.state = Optional.of(new ParseFlowSequenceEntry(false));
            Token token = ParserImpl.this.scanner.peekToken();
            return new MappingEndEvent(token.getStartMark(), token.getEndMark());
        }
    }

    private class ParseFlowSequenceEntryMappingValue
    implements Production {
        private ParseFlowSequenceEntryMappingValue() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                Token token = ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                    ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingEnd());
                    return ParserImpl.this.parseFlowNode();
                }
                ParserImpl.this.state = Optional.of(new ParseFlowSequenceEntryMappingEnd());
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            ParserImpl.this.state = Optional.of(new ParseFlowSequenceEntryMappingEnd());
            Token token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
        }
    }

    private class ParseFlowSequenceEntryMappingKey
    implements Production {
        private ParseFlowSequenceEntryMappingKey() {
        }

        @Override
        public Event produce() {
            Token token = ParserImpl.this.scanner.next();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingValue());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = Optional.of(new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }

    private class ParseFlowSequenceEntry
    implements Production {
        private final boolean first;

        public ParseFlowSequenceEntry(boolean first) {
            this.first = first;
        }

        @Override
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                if (!this.first) {
                    if (ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        ParserImpl.this.scanner.next();
                    } else {
                        Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow sequence", ParserImpl.this.markPop(), "expected ',' or ']', but got " + (Object)((Object)token.getTokenId()), token.getStartMark());
                    }
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    Token token = ParserImpl.this.scanner.peekToken();
                    MappingStartEvent event = new MappingStartEvent(Optional.empty(), Optional.empty(), true, FlowStyle.FLOW, token.getStartMark(), token.getEndMark());
                    ParserImpl.this.state = Optional.of(new ParseFlowSequenceEntryMappingKey());
                    return event;
                }
                if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                    ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
                    return ParserImpl.this.parseFlowNode();
                }
            }
            Token token = ParserImpl.this.scanner.next();
            SequenceEndEvent event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
            ParserImpl.this.markPop();
            return event;
        }
    }

    private class ParseFlowSequenceFirstEntry
    implements Production {
        private ParseFlowSequenceFirstEntry() {
        }

        @Override
        public Event produce() {
            Token token = ParserImpl.this.scanner.next();
            ParserImpl.this.markPush(token.getStartMark());
            return new ParseFlowSequenceEntry(true).produce();
        }
    }

    private class ParseBlockMappingValue
    implements Production {
        private ParseBlockMappingValue() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                Token token = ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockMappingKey());
                    return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
                }
                ParserImpl.this.state = Optional.of(new ParseBlockMappingKey());
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            ParserImpl.this.state = Optional.of(new ParseBlockMappingKey());
            Token token = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(token.getStartMark());
        }
    }

    private class ParseBlockMappingKey
    implements Production {
        private ParseBlockMappingKey() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                Token token = ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockMappingValue());
                    return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
                }
                ParserImpl.this.state = Optional.of(new ParseBlockMappingValue());
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                Token token = ParserImpl.this.scanner.peekToken();
                throw new ParserException("while parsing a block mapping", ParserImpl.this.markPop(), "expected <block end>, but found '" + (Object)((Object)token.getTokenId()) + "'", token.getStartMark());
            }
            Token token = ParserImpl.this.scanner.next();
            MappingEndEvent event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
            ParserImpl.this.markPop();
            return event;
        }
    }

    private class ParseBlockMappingFirstKey
    implements Production {
        private ParseBlockMappingFirstKey() {
        }

        @Override
        public Event produce() {
            Token token = ParserImpl.this.scanner.next();
            ParserImpl.this.markPush(token.getStartMark());
            return new ParseBlockMappingKey().produce();
        }
    }

    private class ParseIndentlessSequenceEntry
    implements Production {
        private ParseIndentlessSequenceEntry() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                Token token = ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseIndentlessSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = Optional.of(new ParseIndentlessSequenceEntry());
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            Token token = ParserImpl.this.scanner.peekToken();
            SequenceEndEvent event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
            return event;
        }
    }

    private class ParseBlockSequenceEntry
    implements Production {
        private ParseBlockSequenceEntry() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                BlockEntryToken token = (BlockEntryToken)ParserImpl.this.scanner.next();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = Optional.of(new ParseBlockSequenceEntry());
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                Token token = ParserImpl.this.scanner.peekToken();
                throw new ParserException("while parsing a block collection", ParserImpl.this.markPop(), "expected <block end>, but found '" + (Object)((Object)token.getTokenId()) + "'", token.getStartMark());
            }
            Token token = ParserImpl.this.scanner.next();
            SequenceEndEvent event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
            ParserImpl.this.markPop();
            return event;
        }
    }

    private class ParseBlockSequenceFirstEntry
    implements Production {
        private ParseBlockSequenceFirstEntry() {
        }

        @Override
        public Event produce() {
            Token token = ParserImpl.this.scanner.next();
            ParserImpl.this.markPush(token.getStartMark());
            return new ParseBlockSequenceEntry().produce();
        }
    }

    private class ParseBlockNode
    implements Production {
        private ParseBlockNode() {
        }

        @Override
        public Event produce() {
            return ParserImpl.this.parseNode(true, false);
        }
    }

    private class ParseDocumentContent
    implements Production {
        private ParseDocumentContent() {
        }

        @Override
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
                Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
                ParserImpl.this.state = Optional.of(ParserImpl.this.states.pop());
                return event;
            }
            ParseBlockNode p = new ParseBlockNode();
            return p.produce();
        }
    }

    private class ParseDocumentEnd
    implements Production {
        private ParseDocumentEnd() {
        }

        @Override
        public Event produce() {
            Optional<Mark> startMark;
            Token token = ParserImpl.this.scanner.peekToken();
            Optional<Mark> endMark = startMark = token.getStartMark();
            boolean explicit = false;
            if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                token = ParserImpl.this.scanner.next();
                endMark = token.getEndMark();
                explicit = true;
            }
            DocumentEndEvent event = new DocumentEndEvent(explicit, startMark, endMark);
            ParserImpl.this.state = Optional.of(new ParseDocumentStart());
            return event;
        }
    }

    private class ParseDocumentStart
    implements Production {
        private ParseDocumentStart() {
        }

        @Override
        public Event produce() {
            Event event;
            while (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                ParserImpl.this.scanner.next();
            }
            if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                Token token = ParserImpl.this.scanner.peekToken();
                Optional<Mark> startMark = token.getStartMark();
                VersionTagsTuple tuple = ParserImpl.this.processDirectives();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                    throw new ParserException("expected '<document start>', but found '" + (Object)((Object)ParserImpl.this.scanner.peekToken().getTokenId()) + "'", ParserImpl.this.scanner.peekToken().getStartMark());
                }
                token = ParserImpl.this.scanner.next();
                Optional<Mark> endMark = token.getEndMark();
                event = new DocumentStartEvent(true, tuple.getSpecVersion(), tuple.getTags(), startMark, endMark);
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = Optional.of(new ParseDocumentContent());
            } else {
                StreamEndToken token = (StreamEndToken)ParserImpl.this.scanner.next();
                event = new StreamEndEvent(token.getStartMark(), token.getEndMark());
                if (!ParserImpl.this.states.isEmpty()) {
                    throw new YamlEngineException("Unexpected end of stream. States left: " + ParserImpl.this.states);
                }
                if (!this.markEmpty()) {
                    throw new YamlEngineException("Unexpected end of stream. Marks left: " + ParserImpl.this.marksStack);
                }
                ParserImpl.this.state = Optional.empty();
            }
            return event;
        }

        private boolean markEmpty() {
            return ParserImpl.this.marksStack.isEmpty();
        }
    }

    private class ParseImplicitDocumentStart
    implements Production {
        private ParseImplicitDocumentStart() {
        }

        @Override
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
                Optional<Mark> startMark;
                ParserImpl.this.directives = new VersionTagsTuple(Optional.empty(), DEFAULT_TAGS);
                Token token = ParserImpl.this.scanner.peekToken();
                Optional<Mark> endMark = startMark = token.getStartMark();
                DocumentStartEvent event = new DocumentStartEvent(false, Optional.empty(), Collections.emptyMap(), startMark, endMark);
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = Optional.of(new ParseBlockNode());
                return event;
            }
            ParseDocumentStart p = new ParseDocumentStart();
            return p.produce();
        }
    }

    private class ParseStreamStart
    implements Production {
        private ParseStreamStart() {
        }

        @Override
        public Event produce() {
            StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.next();
            StreamStartEvent event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = Optional.of(new ParseImplicitDocumentStart());
            return event;
        }
    }
}

