/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.parser.FastPage;
import com.opensymphony.module.sitemesh.util.CharArrayReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class FastPageParser
implements PageParser {
    private static final int TOKEN_NONE = 0;
    private static final int TOKEN_EOF = -1;
    private static final int TOKEN_TEXT = -2;
    private static final int TOKEN_TAG = -3;
    private static final int TOKEN_COMMENT = -4;
    private static final int TOKEN_CDATA = -5;
    private static final int TOKEN_SCRIPT = -6;
    private static final int TOKEN_DOCTYPE = -7;
    private static final int TOKEN_EMPTYTAG = -8;
    private static final int STATE_EOF = -1;
    private static final int STATE_TEXT = -2;
    private static final int STATE_TAG = -3;
    private static final int STATE_COMMENT = -4;
    private static final int STATE_TAG_QUOTE = -5;
    private static final int STATE_CDATA = -6;
    private static final int STATE_SCRIPT = -7;
    private static final int STATE_DOCTYPE = -8;
    private static final int TAG_STATE_NONE = 0;
    private static final int TAG_STATE_HTML = -1;
    private static final int TAG_STATE_HEAD = -2;
    private static final int TAG_STATE_TITLE = -3;
    private static final int TAG_STATE_BODY = -4;
    private static final int TAG_STATE_XML = -6;
    private static final int TAG_STATE_XMP = -7;
    private static final int SLASH_XML_HASH = 1518984;
    private static final int XML_HASH = 118807;
    private static final int SLASH_XMP_HASH = 1518988;
    private static final int XMP_HASH = 118811;
    private static final int HTML_HASH = 3213227;
    private static final int SLASH_HTML_HASH = 46618714;
    private static final int HEAD_HASH = 3198432;
    private static final int TITLE_HASH = 110371416;
    private static final int SLASH_TITLE_HASH = 1455941513;
    private static final int PARAMETER_HASH = 1954460585;
    private static final int META_HASH = 3347973;
    private static final int SLASH_HEAD_HASH = 46603919;
    private static final int FRAMESET_HASH = -1644953643;
    private static final int FRAME_HASH = 97692013;
    private static final int BODY_HASH = 3029410;
    private static final int SLASH_BODY_HASH = 46434897;
    private static final int CONTENT_HASH = 951530617;

    public Page parse(char[] buffer) throws IOException {
        return this.parse(new DefaultSitemeshBuffer(buffer));
    }

    public Page parse(SitemeshBuffer buffer) throws IOException {
        CharArrayReader reader = new CharArrayReader(buffer.getCharArray(), 0, buffer.getBufferLength());
        CharArray _buffer = new CharArray(4096);
        CharArray _body = new CharArray(4096);
        CharArray _head = new CharArray(512);
        CharArray _title = new CharArray(128);
        Map _htmlProperties = null;
        HashMap<String, String> _metaProperties = new HashMap<String, String>(6);
        HashMap<String, String> _sitemeshProperties = new HashMap<String, String>(6);
        Map _bodyProperties = null;
        CharArray _currentTaggedContent = new CharArray(1024);
        String _contentTagId = null;
        boolean tagged = false;
        boolean _frameSet = false;
        int _state = -2;
        int _tokenType = 0;
        int _pushBack = 0;
        int _comment = 0;
        int _quote = 0;
        boolean hide = false;
        int state = 0;
        int laststate = 0;
        boolean doneTitle = false;
        Tag tagObject = new Tag();
        block29: while (_tokenType != -1) {
            if (tagged) {
                if (_tokenType == -3 || _tokenType == -8) {
                    if (_buffer == null || _buffer.length() == 0) {
                        _tokenType = 0;
                        continue;
                    }
                    if (this.parseTag(tagObject, _buffer) == null) continue;
                    if (_buffer.compareLowerSubstr("/content")) {
                        tagged = false;
                        if (_contentTagId != null) {
                            state = 0;
                            _sitemeshProperties.put(_contentTagId, _currentTaggedContent.toString());
                            _currentTaggedContent.setLength(0);
                            _contentTagId = null;
                        }
                    } else {
                        _currentTaggedContent.append('<').append(_buffer).append('>');
                    }
                } else if (_buffer.length() > 0) {
                    _currentTaggedContent.append(_buffer);
                }
            } else if (_tokenType == -3 || _tokenType == -8) {
                if (_buffer == null || _buffer.length() == 0) {
                    _tokenType = 0;
                    continue;
                }
                if (this.parseTag(tagObject, _buffer) == null) {
                    _tokenType = -2;
                    continue;
                }
                int tagHash = _buffer.substrHashCode();
                if (state == -6 || state == -7) {
                    FastPageParser.writeTag(state, laststate, hide, _head, _buffer, _body);
                    if (state == -6 && tagHash == 1518984 || state == -7 && tagHash == 1518988) {
                        state = laststate;
                    }
                } else {
                    boolean doDefault = false;
                    switch (tagHash) {
                        case 3213227: {
                            if (!_buffer.compareLowerSubstr("html")) {
                                doDefault = true;
                                break;
                            }
                            state = -1;
                            _htmlProperties = FastPageParser.parseProperties((Tag)tagObject, (CharArray)_buffer).properties;
                            break;
                        }
                        case 3198432: {
                            if (!_buffer.compareLowerSubstr("head")) {
                                doDefault = true;
                                break;
                            }
                            state = -2;
                            break;
                        }
                        case 118807: {
                            if (!_buffer.compareLowerSubstr("xml")) {
                                doDefault = true;
                                break;
                            }
                            laststate = state;
                            FastPageParser.writeTag(state, laststate, hide, _head, _buffer, _body);
                            state = -6;
                            break;
                        }
                        case 118811: {
                            if (!_buffer.compareLowerSubstr("xmp")) {
                                doDefault = true;
                                break;
                            }
                            laststate = state;
                            FastPageParser.writeTag(state, laststate, hide, _head, _buffer, _body);
                            state = -7;
                            break;
                        }
                        case 110371416: {
                            if (!_buffer.compareLowerSubstr("title")) {
                                doDefault = true;
                                break;
                            }
                            if (doneTitle) {
                                hide = true;
                                break;
                            }
                            laststate = state;
                            state = -3;
                            break;
                        }
                        case 1455941513: {
                            if (!_buffer.compareLowerSubstr("/title")) {
                                doDefault = true;
                                break;
                            }
                            if (doneTitle) {
                                hide = false;
                                break;
                            }
                            doneTitle = true;
                            state = laststate;
                            break;
                        }
                        case 1954460585: {
                            if (!_buffer.compareLowerSubstr("parameter")) {
                                doDefault = true;
                                break;
                            }
                            FastPageParser.parseProperties(tagObject, _buffer);
                            String name = (String)tagObject.properties.get("name");
                            String value = (String)tagObject.properties.get("value");
                            if (name == null || value == null) break;
                            _sitemeshProperties.put(name, value);
                            break;
                        }
                        case 3347973: {
                            String httpEquiv;
                            if (!_buffer.compareLowerSubstr("meta")) {
                                doDefault = true;
                                break;
                            }
                            CharArray metaDestination = state == -2 ? _head : _body;
                            metaDestination.append('<');
                            metaDestination.append(_buffer);
                            metaDestination.append('>');
                            FastPageParser.parseProperties(tagObject, _buffer);
                            String name = (String)tagObject.properties.get("name");
                            String value = (String)tagObject.properties.get("content");
                            if (name == null && (httpEquiv = (String)tagObject.properties.get("http-equiv")) != null) {
                                name = "http-equiv." + httpEquiv;
                            }
                            if (name == null || value == null) break;
                            _metaProperties.put(name, value);
                            break;
                        }
                        case 46603919: {
                            if (!_buffer.compareLowerSubstr("/head")) {
                                doDefault = true;
                                break;
                            }
                            state = -1;
                            break;
                        }
                        case 97692013: {
                            if (!_buffer.compareLowerSubstr("frame")) {
                                doDefault = true;
                                break;
                            }
                            _frameSet = true;
                            break;
                        }
                        case -1644953643: {
                            if (!_buffer.compareLowerSubstr("frameset")) {
                                doDefault = true;
                                break;
                            }
                            _frameSet = true;
                            break;
                        }
                        case 3029410: {
                            if (!_buffer.compareLowerSubstr("body")) {
                                doDefault = true;
                                break;
                            }
                            if (_tokenType == -8) {
                                state = -4;
                            }
                            _bodyProperties = FastPageParser.parseProperties((Tag)tagObject, (CharArray)_buffer).properties;
                            break;
                        }
                        case 951530617: {
                            if (!_buffer.compareLowerSubstr("content")) {
                                doDefault = true;
                                break;
                            }
                            state = 0;
                            Map props = FastPageParser.parseProperties((Tag)tagObject, (CharArray)_buffer).properties;
                            if (props == null) break;
                            tagged = true;
                            _contentTagId = (String)props.get("tag");
                            break;
                        }
                        case 1518988: {
                            if (!_buffer.compareLowerSubstr("/xmp")) {
                                doDefault = true;
                                break;
                            }
                            hide = false;
                            break;
                        }
                        case 46434897: {
                            if (!_buffer.compareLowerSubstr("/body")) {
                                doDefault = true;
                                break;
                            }
                            state = 0;
                            hide = true;
                            break;
                        }
                        case 46618714: {
                            if (!_buffer.compareLowerSubstr("/html")) {
                                doDefault = true;
                                break;
                            }
                            state = 0;
                            hide = true;
                            break;
                        }
                        default: {
                            doDefault = true;
                        }
                    }
                    if (doDefault) {
                        FastPageParser.writeTag(state, laststate, hide, _head, _buffer, _body);
                    }
                }
            } else if (!hide) {
                if (_tokenType == -2) {
                    if (state == -3) {
                        _title.append(_buffer);
                    } else if (FastPageParser.shouldWriteToHead(state, laststate)) {
                        _head.append(_buffer);
                    } else {
                        _body.append(_buffer);
                    }
                } else if (_tokenType == -4) {
                    CharArray commentDestination = FastPageParser.shouldWriteToHead(state, laststate) ? _head : _body;
                    commentDestination.append("<!--");
                    commentDestination.append(_buffer);
                    commentDestination.append("-->");
                } else if (_tokenType == -5) {
                    CharArray commentDestination = state == -2 ? _head : _body;
                    commentDestination.append("<![CDATA[");
                    commentDestination.append(_buffer);
                    commentDestination.append("]]>");
                } else if (_tokenType == -6) {
                    CharArray commentDestination = state == -2 ? _head : _body;
                    commentDestination.append('<');
                    commentDestination.append(_buffer);
                }
            }
            _buffer.setLength(0);
            block30: while (true) {
                int c;
                if (_pushBack != 0) {
                    c = _pushBack;
                    _pushBack = 0;
                } else {
                    try {
                        c = reader.read();
                    }
                    catch (IOException e) {
                        _tokenType = -1;
                        continue block29;
                    }
                }
                if (c < 0) {
                    int tmpstate = _state;
                    _state = -1;
                    if (_buffer.length() > 0 && tmpstate == -2) {
                        _tokenType = -2;
                        continue block29;
                    }
                    _tokenType = -1;
                    continue block29;
                }
                switch (_state) {
                    case -3: {
                        int buflen = _buffer.length();
                        if (c == 62) {
                            _tokenType = _buffer.length() > 1 && _buffer.charAt(_buffer.length() - 1) == '/' ? -8 : -3;
                            _state = -2;
                            continue block29;
                        }
                        if (c == 47) {
                            _buffer.append('/');
                            break;
                        }
                        if (c == 60 && buflen == 0) {
                            _buffer.append("<<");
                            _state = -2;
                            break;
                        }
                        if (c == 45 && buflen == 2 && _buffer.charAt(1) == '-' && _buffer.charAt(0) == '!') {
                            _buffer.setLength(0);
                            _state = -4;
                            break;
                        }
                        if (c == 91 && buflen == 7 && _buffer.charAt(0) == '!' && _buffer.charAt(1) == '[' && _buffer.compareLower("cdata", 2)) {
                            _buffer.setLength(0);
                            _state = -6;
                            break;
                        }
                        if ((c == 101 || c == 69) && buflen == 7 && _buffer.charAt(0) == '!' && _buffer.compareLower("doctyp", 1)) {
                            _buffer.append((char)c);
                            _state = -8;
                            break;
                        }
                        if ((c == 84 || c == 116) && buflen == 5 && _buffer.compareLower("scrip", 0)) {
                            _buffer.append((char)c);
                            _state = -7;
                            break;
                        }
                        if (c == 34 || c == 39) {
                            _quote = c;
                            _buffer.append((char)c);
                            _state = -5;
                            break;
                        }
                        _buffer.append((char)c);
                        break;
                    }
                    case -2: {
                        if (c == 60) {
                            _state = -3;
                            if (_buffer.length() <= 0) break;
                            _tokenType = -2;
                            continue block29;
                        }
                        _buffer.append((char)c);
                        break;
                    }
                    case -5: {
                        if (c == 62) {
                            _pushBack = c;
                            _state = -3;
                            break;
                        }
                        _buffer.append((char)c);
                        if (c != _quote) break;
                        _state = -3;
                        break;
                    }
                    case -4: {
                        if (c == 62 && _comment >= 2) {
                            _buffer.setLength(_buffer.length() - 2);
                            _comment = 0;
                            _state = -2;
                            _tokenType = -4;
                            continue block29;
                        }
                        _comment = c == 45 ? ++_comment : 0;
                        _buffer.append((char)c);
                        break;
                    }
                    case -6: {
                        if (c == 62 && _comment >= 2) {
                            _buffer.setLength(_buffer.length() - 2);
                            _comment = 0;
                            _state = -2;
                            _tokenType = -5;
                            continue block29;
                        }
                        _comment = c == 93 ? ++_comment : 0;
                        _buffer.append((char)c);
                        break;
                    }
                    case -7: {
                        _buffer.append((char)c);
                        if (c == 60) {
                            _comment = 0;
                            break;
                        }
                        if (c == 47 && _comment == 0 || (c == 115 || c == 83) && _comment == 1 || (c == 99 || c == 67) && _comment == 2 || (c == 114 || c == 82) && _comment == 3 || (c == 105 || c == 73) && _comment == 4 || (c == 112 || c == 80) && _comment == 5 || (c == 116 || c == 84) && _comment == 6) {
                            ++_comment;
                            break;
                        }
                        if (c != 62 || _comment < 7) continue block30;
                        _comment = 0;
                        _state = -2;
                        _tokenType = -6;
                        continue block29;
                    }
                    case -8: {
                        _buffer.append((char)c);
                        if (c == 62) {
                            _state = -2;
                            _tokenType = -7;
                            continue block29;
                        }
                        _comment = 0;
                    }
                }
            }
        }
        _currentTaggedContent = null;
        _buffer = null;
        return new FastPage(buffer, _sitemeshProperties, _htmlProperties, _metaProperties, _bodyProperties, _title.toString().trim(), _head.toString().trim(), _body.toString().trim(), _frameSet);
    }

    private static void writeTag(int state, int laststate, boolean hide, CharArray _head, CharArray _buffer, CharArray _body) {
        if (!hide) {
            if (FastPageParser.shouldWriteToHead(state, laststate)) {
                _head.append('<').append(_buffer).append('>');
            } else {
                _body.append('<').append(_buffer).append('>');
            }
        }
    }

    private static boolean shouldWriteToHead(int state, int laststate) {
        return state == -2 || laststate == -2 && (state == -6 || state == -7);
    }

    private Tag parseTag(Tag tag, CharArray buf) {
        int idx;
        int len = buf.length();
        for (idx = 0; idx < len && Character.isWhitespace(buf.charAt(idx)); ++idx) {
        }
        if (idx == len) {
            return null;
        }
        int begin = idx;
        while (idx < len && !Character.isWhitespace(buf.charAt(idx))) {
            ++idx;
        }
        buf.setSubstr(begin, buf.charAt(idx - 1) == '/' ? idx - 1 : idx);
        tag.nameEndIdx = idx;
        return tag;
    }

    private static Tag parseProperties(Tag tag, CharArray buffer) {
        int len = buffer.length();
        int idx = tag.nameEndIdx;
        tag.properties = Collections.EMPTY_MAP;
        while (idx < len) {
            int end;
            while (idx < len && Character.isWhitespace(buffer.charAt(idx))) {
                ++idx;
            }
            if (idx == len) continue;
            int begin = idx;
            if (buffer.charAt(idx) == '\"') {
                ++idx;
                while (idx < len && buffer.charAt(idx) != '\"') {
                    ++idx;
                }
                if (idx == len) continue;
                ++idx;
            } else if (buffer.charAt(idx) == '\'') {
                ++idx;
                while (idx < len && buffer.charAt(idx) != '\'') {
                    ++idx;
                }
                if (idx == len) continue;
                ++idx;
            } else {
                while (idx < len && !Character.isWhitespace(buffer.charAt(idx)) && buffer.charAt(idx) != '=') {
                    ++idx;
                }
            }
            buffer.setSubstr(begin, idx);
            if (idx < len && Character.isWhitespace(buffer.charAt(idx))) {
                while (idx < len && Character.isWhitespace(buffer.charAt(idx))) {
                    ++idx;
                }
            }
            if (idx == len || buffer.charAt(idx) != '=' || ++idx == len) continue;
            while (idx < len && (buffer.charAt(idx) == '\n' || buffer.charAt(idx) == '\r')) {
                ++idx;
            }
            if (buffer.charAt(idx) == ' ') {
                while (idx < len && Character.isWhitespace(buffer.charAt(idx))) {
                    ++idx;
                }
                if (idx == len || buffer.charAt(idx) != '\"' && buffer.charAt(idx) != '\"') continue;
            }
            begin = idx;
            if (buffer.charAt(idx) == '\"') {
                begin = ++idx;
                while (idx < len && buffer.charAt(idx) != '\"') {
                    ++idx;
                }
                if (idx == len) continue;
                end = idx++;
            } else if (buffer.charAt(idx) == '\'') {
                begin = ++idx;
                while (idx < len && buffer.charAt(idx) != '\'') {
                    ++idx;
                }
                if (idx == len) continue;
                end = idx++;
            } else {
                while (idx < len && !Character.isWhitespace(buffer.charAt(idx))) {
                    ++idx;
                }
                end = idx;
            }
            String name = buffer.getLowerSubstr();
            String value = buffer.substring(begin, end);
            tag.addProperty(name, value);
        }
        return tag;
    }

    private class Tag {
        public int nameEndIdx = 0;
        public Map properties = Collections.EMPTY_MAP;

        private Tag() {
        }

        public void addProperty(String name, String value) {
            if (this.properties == Collections.EMPTY_MAP) {
                this.properties = new HashMap(8);
            }
            this.properties.put(name, value);
        }
    }
}

