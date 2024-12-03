/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.batik.css.parser.ParseException;
import org.apache.batik.css.parser.ScannerUtilities;
import org.apache.batik.util.io.NormalizingReader;
import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.StringNormalizingReader;

public class Scanner {
    protected NormalizingReader reader;
    protected int current;
    protected char[] buffer = new char[128];
    protected int position;
    protected int type;
    protected int start;
    protected int end;
    protected int blankCharacters;

    public Scanner(Reader r) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(r);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public Scanner(InputStream is, String enc) throws ParseException {
        try {
            this.reader = new StreamNormalizingReader(is, enc);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public Scanner(String s) throws ParseException {
        try {
            this.reader = new StringNormalizingReader(s);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public int getLine() {
        return this.reader.getLine();
    }

    public int getColumn() {
        return this.reader.getColumn();
    }

    public char[] getBuffer() {
        return this.buffer;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void clearBuffer() {
        if (this.position <= 0) {
            this.position = 0;
        } else {
            this.buffer[0] = this.buffer[this.position - 1];
            this.position = 1;
        }
    }

    public int getType() {
        return this.type;
    }

    public String getStringValue() {
        return new String(this.buffer, this.start, this.end - this.start);
    }

    public void scanAtRule() throws ParseException {
        try {
            block11: while (true) {
                switch (this.current) {
                    case 123: {
                        int brackets = 1;
                        block12: while (true) {
                            this.nextChar();
                            switch (this.current) {
                                case 125: {
                                    if (--brackets > 0) continue block12;
                                }
                                case -1: {
                                    break block11;
                                }
                                case 123: {
                                    ++brackets;
                                }
                            }
                        }
                    }
                    case -1: 
                    case 59: {
                        break block11;
                    }
                    default: {
                        this.nextChar();
                        continue block11;
                    }
                }
                break;
            }
            this.end = this.position;
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }

    public int next() throws ParseException {
        this.blankCharacters = 0;
        this.start = this.position - 1;
        this.nextToken();
        this.end = this.position - this.endGap();
        return this.type;
    }

    public void close() {
        try {
            this.reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected int endGap() {
        int result = this.current == -1 ? 0 : 1;
        switch (this.type) {
            case 19: 
            case 42: 
            case 43: 
            case 52: {
                ++result;
                break;
            }
            case 18: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 44: 
            case 45: 
            case 46: {
                result += 2;
                break;
            }
            case 47: 
            case 48: 
            case 50: {
                result += 3;
                break;
            }
            case 49: {
                result += 4;
            }
        }
        return result + this.blankCharacters;
    }

    protected void nextToken() throws ParseException {
        try {
            switch (this.current) {
                case -1: {
                    this.type = 0;
                    return;
                }
                case 123: {
                    this.nextChar();
                    this.type = 1;
                    return;
                }
                case 125: {
                    this.nextChar();
                    this.type = 2;
                    return;
                }
                case 61: {
                    this.nextChar();
                    this.type = 3;
                    return;
                }
                case 43: {
                    this.nextChar();
                    this.type = 4;
                    return;
                }
                case 44: {
                    this.nextChar();
                    this.type = 6;
                    return;
                }
                case 59: {
                    this.nextChar();
                    this.type = 8;
                    return;
                }
                case 62: {
                    this.nextChar();
                    this.type = 9;
                    return;
                }
                case 91: {
                    this.nextChar();
                    this.type = 11;
                    return;
                }
                case 93: {
                    this.nextChar();
                    this.type = 12;
                    return;
                }
                case 42: {
                    this.nextChar();
                    this.type = 13;
                    return;
                }
                case 40: {
                    this.nextChar();
                    this.type = 14;
                    return;
                }
                case 41: {
                    this.nextChar();
                    this.type = 15;
                    return;
                }
                case 58: {
                    this.nextChar();
                    this.type = 16;
                    return;
                }
                case 9: 
                case 10: 
                case 12: 
                case 13: 
                case 32: {
                    do {
                        this.nextChar();
                    } while (ScannerUtilities.isCSSSpace((char)this.current));
                    this.type = 17;
                    return;
                }
                case 47: {
                    this.nextChar();
                    if (this.current != 42) {
                        this.type = 10;
                        return;
                    }
                    this.nextChar();
                    this.start = this.position - 1;
                    while (true) {
                        if (this.current != -1 && this.current != 42) {
                            this.nextChar();
                            continue;
                        }
                        do {
                            this.nextChar();
                        } while (this.current != -1 && this.current == 42);
                        if (this.current == -1 || this.current == 47) break;
                    }
                    if (this.current == -1) {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    this.nextChar();
                    this.type = 18;
                    return;
                }
                case 39: {
                    this.type = this.string1();
                    return;
                }
                case 34: {
                    this.type = this.string2();
                    return;
                }
                case 60: {
                    this.nextChar();
                    if (this.current != 33) {
                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    }
                    this.nextChar();
                    if (this.current == 45) {
                        this.nextChar();
                        if (this.current == 45) {
                            this.nextChar();
                            this.type = 21;
                            return;
                        }
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 45: {
                    this.nextChar();
                    if (this.current != 45) {
                        this.type = 5;
                        return;
                    }
                    this.nextChar();
                    if (this.current == 62) {
                        this.nextChar();
                        this.type = 22;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 124: {
                    this.nextChar();
                    if (this.current == 61) {
                        this.nextChar();
                        this.type = 25;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 126: {
                    this.nextChar();
                    if (this.current == 61) {
                        this.nextChar();
                        this.type = 26;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 35: {
                    this.nextChar();
                    if (ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                        this.start = this.position - 1;
                        do {
                            this.nextChar();
                            while (this.current == 92) {
                                this.nextChar();
                                this.escape();
                            }
                        } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                        this.type = 27;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 64: {
                    this.nextChar();
                    switch (this.current) {
                        case 67: 
                        case 99: {
                            this.start = this.position - 1;
                            if (!Scanner.isEqualIgnoreCase(this.nextChar(), 'h') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'a') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'r') || !Scanner.isEqualIgnoreCase(this.nextChar(), 's') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'e') || !Scanner.isEqualIgnoreCase(this.nextChar(), 't')) break;
                            this.nextChar();
                            this.type = 30;
                            return;
                        }
                        case 70: 
                        case 102: {
                            this.start = this.position - 1;
                            if (!Scanner.isEqualIgnoreCase(this.nextChar(), 'o') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'n') || !Scanner.isEqualIgnoreCase(this.nextChar(), 't') || !Scanner.isEqualIgnoreCase(this.nextChar(), '-') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'f') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'a') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'c') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'e')) break;
                            this.nextChar();
                            this.type = 31;
                            return;
                        }
                        case 73: 
                        case 105: {
                            this.start = this.position - 1;
                            if (!Scanner.isEqualIgnoreCase(this.nextChar(), 'm') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'p') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'o') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'r') || !Scanner.isEqualIgnoreCase(this.nextChar(), 't')) break;
                            this.nextChar();
                            this.type = 28;
                            return;
                        }
                        case 77: 
                        case 109: {
                            this.start = this.position - 1;
                            if (!Scanner.isEqualIgnoreCase(this.nextChar(), 'e') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'd') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'i') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'a')) break;
                            this.nextChar();
                            this.type = 32;
                            return;
                        }
                        case 80: 
                        case 112: {
                            this.start = this.position - 1;
                            if (!Scanner.isEqualIgnoreCase(this.nextChar(), 'a') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'g') || !Scanner.isEqualIgnoreCase(this.nextChar(), 'e')) break;
                            this.nextChar();
                            this.type = 33;
                            return;
                        }
                        default: {
                            if (!ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
                                throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
                            }
                            this.start = this.position - 1;
                        }
                    }
                    do {
                        this.nextChar();
                        while (this.current == 92) {
                            this.nextChar();
                            this.escape();
                        }
                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                    this.type = 29;
                    return;
                }
                case 33: {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current));
                    if (Scanner.isEqualIgnoreCase(this.current, 'i') && Scanner.isEqualIgnoreCase(this.nextChar(), 'm') && Scanner.isEqualIgnoreCase(this.nextChar(), 'p') && Scanner.isEqualIgnoreCase(this.nextChar(), 'o') && Scanner.isEqualIgnoreCase(this.nextChar(), 'r') && Scanner.isEqualIgnoreCase(this.nextChar(), 't') && Scanner.isEqualIgnoreCase(this.nextChar(), 'a') && Scanner.isEqualIgnoreCase(this.nextChar(), 'n') && Scanner.isEqualIgnoreCase(this.nextChar(), 't')) {
                        this.nextChar();
                        this.type = 23;
                        return;
                    }
                    if (this.current == -1) {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    this.type = this.number();
                    return;
                }
                case 46: {
                    switch (this.nextChar()) {
                        case 48: 
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: {
                            this.type = this.dotNumber();
                            return;
                        }
                    }
                    this.type = 7;
                    return;
                }
                case 85: 
                case 117: {
                    this.nextChar();
                    switch (this.current) {
                        case 43: {
                            boolean range = false;
                            block68: for (int i = 0; i < 6; ++i) {
                                this.nextChar();
                                switch (this.current) {
                                    case 63: {
                                        range = true;
                                        continue block68;
                                    }
                                    default: {
                                        if (!range || ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) continue block68;
                                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                    }
                                }
                            }
                            this.nextChar();
                            if (range) {
                                this.type = 53;
                                return;
                            }
                            if (this.current == 45) {
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                this.type = 53;
                                return;
                            }
                        }
                        case 82: 
                        case 114: {
                            this.nextChar();
                            switch (this.current) {
                                case 76: 
                                case 108: {
                                    this.nextChar();
                                    switch (this.current) {
                                        case 40: {
                                            do {
                                                this.nextChar();
                                            } while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current));
                                            switch (this.current) {
                                                case 39: {
                                                    this.string1();
                                                    this.blankCharacters += 2;
                                                    while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                        ++this.blankCharacters;
                                                        this.nextChar();
                                                    }
                                                    if (this.current == -1) {
                                                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                                    }
                                                    if (this.current != 41) {
                                                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                    }
                                                    this.nextChar();
                                                    this.type = 51;
                                                    return;
                                                }
                                                case 34: {
                                                    this.string2();
                                                    this.blankCharacters += 2;
                                                    while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                        ++this.blankCharacters;
                                                        this.nextChar();
                                                    }
                                                    if (this.current == -1) {
                                                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                                    }
                                                    if (this.current != 41) {
                                                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                    }
                                                    this.nextChar();
                                                    this.type = 51;
                                                    return;
                                                }
                                                case 41: {
                                                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                }
                                            }
                                            if (!ScannerUtilities.isCSSURICharacter((char)this.current)) {
                                                throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                            }
                                            this.start = this.position - 1;
                                            do {
                                                this.nextChar();
                                            } while (this.current != -1 && ScannerUtilities.isCSSURICharacter((char)this.current));
                                            ++this.blankCharacters;
                                            while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                ++this.blankCharacters;
                                                this.nextChar();
                                            }
                                            if (this.current == -1) {
                                                throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                            }
                                            if (this.current != 41) {
                                                throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                            }
                                            this.nextChar();
                                            this.type = 51;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                        this.nextChar();
                    }
                    if (this.current == 40) {
                        this.nextChar();
                        this.type = 52;
                        return;
                    }
                    this.type = 20;
                    return;
                }
            }
            if (this.current == 92) {
                do {
                    this.nextChar();
                    this.escape();
                } while (this.current == 92);
            } else if (!ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
                this.nextChar();
                throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
            }
            while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                this.nextChar();
                while (this.current == 92) {
                    this.nextChar();
                    this.escape();
                }
            }
            if (this.current == 40) {
                this.nextChar();
                this.type = 52;
                return;
            }
            this.type = 20;
            return;
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected int string1() throws IOException {
        this.start = this.position;
        block9: while (true) {
            switch (this.nextChar()) {
                case -1: {
                    throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                }
                case 39: {
                    break block9;
                }
                case 34: {
                    continue block9;
                }
                case 92: {
                    switch (this.nextChar()) {
                        case 10: 
                        case 12: {
                            continue block9;
                        }
                    }
                    this.escape();
                    continue block9;
                }
                default: {
                    if (!ScannerUtilities.isCSSStringCharacter((char)this.current)) throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    continue block9;
                }
            }
            break;
        }
        this.nextChar();
        return 19;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected int string2() throws IOException {
        this.start = this.position;
        block9: while (true) {
            switch (this.nextChar()) {
                case -1: {
                    throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                }
                case 39: {
                    continue block9;
                }
                case 34: {
                    break block9;
                }
                case 92: {
                    switch (this.nextChar()) {
                        case 10: 
                        case 12: {
                            continue block9;
                        }
                    }
                    this.escape();
                    continue block9;
                }
                default: {
                    if (!ScannerUtilities.isCSSStringCharacter((char)this.current)) throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    continue block9;
                }
            }
            break;
        }
        this.nextChar();
        return 19;
    }

    protected int number() throws IOException {
        block7: while (true) {
            switch (this.nextChar()) {
                case 46: {
                    switch (this.nextChar()) {
                        case 48: 
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: 
                        case 56: 
                        case 57: {
                            return this.dotNumber();
                        }
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                default: {
                    break block7;
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    continue block7;
                }
            }
            break;
        }
        return this.numberUnit(true);
    }

    protected int dotNumber() throws IOException {
        block3: while (true) {
            switch (this.nextChar()) {
                default: {
                    break block3;
                }
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    continue block3;
                }
            }
            break;
        }
        return this.numberUnit(false);
    }

    protected int numberUnit(boolean integer) throws IOException {
        switch (this.current) {
            case 37: {
                this.nextChar();
                return 42;
            }
            case 67: 
            case 99: {
                switch (this.nextChar()) {
                    case 77: 
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 37;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 68: 
            case 100: {
                switch (this.nextChar()) {
                    case 69: 
                    case 101: {
                        switch (this.nextChar()) {
                            case 71: 
                            case 103: {
                                this.nextChar();
                                if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                    do {
                                        this.nextChar();
                                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                    return 34;
                                }
                                return 47;
                            }
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 69: 
            case 101: {
                switch (this.nextChar()) {
                    case 77: 
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 36;
                    }
                    case 88: 
                    case 120: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 35;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 71: 
            case 103: {
                switch (this.nextChar()) {
                    case 82: 
                    case 114: {
                        switch (this.nextChar()) {
                            case 65: 
                            case 97: {
                                switch (this.nextChar()) {
                                    case 68: 
                                    case 100: {
                                        this.nextChar();
                                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                            do {
                                                this.nextChar();
                                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                            return 34;
                                        }
                                        return 49;
                                    }
                                }
                            }
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 72: 
            case 104: {
                this.nextChar();
                switch (this.current) {
                    case 90: 
                    case 122: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 41;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 73: 
            case 105: {
                switch (this.nextChar()) {
                    case 78: 
                    case 110: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 39;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 75: 
            case 107: {
                switch (this.nextChar()) {
                    case 72: 
                    case 104: {
                        switch (this.nextChar()) {
                            case 90: 
                            case 122: {
                                this.nextChar();
                                if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                    do {
                                        this.nextChar();
                                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                    return 34;
                                }
                                return 50;
                            }
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 77: 
            case 109: {
                switch (this.nextChar()) {
                    case 77: 
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 38;
                    }
                    case 83: 
                    case 115: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 40;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 80: 
            case 112: {
                switch (this.nextChar()) {
                    case 67: 
                    case 99: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 44;
                    }
                    case 84: 
                    case 116: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 45;
                    }
                    case 88: 
                    case 120: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 46;
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 82: 
            case 114: {
                switch (this.nextChar()) {
                    case 65: 
                    case 97: {
                        switch (this.nextChar()) {
                            case 68: 
                            case 100: {
                                this.nextChar();
                                if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                    do {
                                        this.nextChar();
                                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                    return 34;
                                }
                                return 48;
                            }
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 83: 
            case 115: {
                this.nextChar();
                return 43;
            }
        }
        if (this.current != -1 && ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
            do {
                this.nextChar();
            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
            return 34;
        }
        return integer ? 24 : 54;
    }

    protected void escape() throws IOException {
        if (ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
        }
        if (this.current >= 32 && this.current <= 126 || this.current >= 128) {
            this.nextChar();
            return;
        }
        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
    }

    protected static boolean isEqualIgnoreCase(int i, char c) {
        return i == -1 ? false : Character.toLowerCase((char)i) == c;
    }

    protected int nextChar() throws IOException {
        this.current = this.reader.read();
        if (this.current == -1) {
            return this.current;
        }
        if (this.position == this.buffer.length) {
            char[] t = new char[1 + this.position + this.position / 2];
            System.arraycopy(this.buffer, 0, t, 0, this.position);
            this.buffer = t;
        }
        char c = (char)this.current;
        this.buffer[this.position++] = c;
        return c;
    }
}

