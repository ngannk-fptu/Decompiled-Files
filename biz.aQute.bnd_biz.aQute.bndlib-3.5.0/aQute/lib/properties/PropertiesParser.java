/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.properties;

import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;

@Deprecated
public class PropertiesParser {
    public static final String $$$ERRORS = "$$$ERRORS";

    public static Properties parse(URI input) throws Exception {
        BufferedReader reader = IO.reader(input.toURL().openStream());
        return PropertiesParser.parse(reader, input);
    }

    public static Properties parse(Reader reader, URI input) throws Exception {
        int c;
        String file = input.getPath();
        Properties properties = new Properties();
        int line = 0;
        String section = null;
        String errors = "";
        block25: while ((c = reader.read()) != -1) {
            boolean start = false;
            if ((c = PropertiesParser.ws(c, reader)) == -1) break;
            if (c == 91) {
                c = reader.read();
                if ((c = PropertiesParser.ws(c, reader)) == -1) break;
                StringBuilder sb = new StringBuilder();
                while (Character.isJavaIdentifierPart(c)) {
                    sb.append((char)c);
                    c = reader.read();
                }
                if ((c = PropertiesParser.ws(c, reader)) == 93) {
                    c = reader.read();
                    if (sb.length() == 0) {
                        section = null;
                        continue;
                    }
                    section = sb.toString();
                    continue;
                }
                errors = errors + file + "#" + line + ": section " + sb + " not properly finished, ignored\n";
                continue;
            }
            if (c == 35 || c == 47) {
                if (c != 47) continue;
                c = reader.read();
                if (c == 42) {
                    while ((c = reader.read()) != -1) {
                        while (c == 42) {
                            c = reader.read();
                            if (c != 47 && c != -1) continue;
                            continue block25;
                        }
                    }
                    continue;
                }
                errors = errors + file + "#" + line + ": false comment";
                continue;
            }
            StringBuilder name = new StringBuilder();
            if (section != null) {
                name.append(section).append(".");
            }
            while (Character.isJavaIdentifierPart(c) || c == 45) {
                name.append((char)c);
                c = reader.read();
            }
            c = PropertiesParser.ws(c, reader);
            StringBuilder value = new StringBuilder();
            if (c != -1) {
                boolean multiline;
                if (c == 58 || c == 61) {
                    c = reader.read();
                    c = PropertiesParser.ws(c, reader);
                }
                boolean bl = multiline = c == 123;
                if (multiline) {
                    c = reader.read();
                    c = PropertiesParser.ws(c, reader);
                    while (c == 10 || c == 13) {
                        if (c == 10) {
                            ++line;
                        }
                        c = reader.read();
                    }
                }
                block31: while (true) {
                    switch (c) {
                        case -1: {
                            break block31;
                        }
                        case 10: {
                            if (!multiline) break block31;
                            ++line;
                            value.append('\n');
                            break;
                        }
                        case 125: {
                            if (multiline) {
                                c = reader.read();
                                break block31;
                            }
                            value.append((char)c);
                            break;
                        }
                        case 13: {
                            break;
                        }
                        case 92: {
                            c = reader.read();
                            switch (c) {
                                case -1: {
                                    errors = file + "#" + line + ": escaped eof";
                                    break;
                                }
                                case 117: {
                                    try {
                                        int code = Hex.nibble(reader.read()) * 4096;
                                        code += Hex.nibble(reader.read()) * 256;
                                        code += Hex.nibble(reader.read()) * 16;
                                        if ((code += Hex.nibble(reader.read()) * '\u0001') < 0 || code > 65535) break;
                                        value.append((char)code);
                                    }
                                    catch (Exception e) {
                                        errors = errors + file + "#" + line + ": " + e + "\n";
                                    }
                                    break;
                                }
                                case 10: {
                                    ++line;
                                }
                                default: {
                                    value.append((char)c);
                                }
                            }
                        }
                        default: {
                            value.append((char)c);
                        }
                    }
                    c = reader.read();
                }
            }
            while (Character.isWhitespace(value.charAt(value.length() - 1))) {
                value.deleteCharAt(value.length() - 1);
            }
            if (name.toString().equals("-include")) {
                for (String uri : name.toString().split("\\s*,\\s*")) {
                    boolean mandatory = true;
                    while (uri.startsWith("-")) {
                        mandatory = false;
                        uri = uri.substring(1);
                    }
                    URI u = input.resolve(uri);
                    try (BufferedReader inc = IO.reader(u.toURL().openStream());){
                        Properties p = PropertiesParser.parse(u);
                        Enumeration<?> e = p.propertyNames();
                        while (e.hasMoreElements()) {
                            String k = (String)e.nextElement();
                            String v = p.getProperty(k);
                            if (k.equals($$$ERRORS)) {
                                errors = errors + v;
                                continue;
                            }
                            properties.setProperty(k, v);
                        }
                    }
                    catch (Exception e) {
                        if (!mandatory) continue;
                        errors = errors + file + "#" + line + ": include not found " + uri + "\n";
                    }
                }
            } else {
                properties.setProperty(name.toString(), value.toString());
                properties.setProperty("$$$." + name.toString(), file + "#" + line);
            }
            if (c != -1 && c != 10) {
                c = PropertiesParser.ws(c, reader);
            }
            if (c != 10 && c != -1) {
                errors = errors + file + "#" + line + ": found unexpected characters at end of line\n";
            }
            c = PropertiesParser.eol(c, reader);
            ++line;
        }
        if (errors.length() != 0) {
            properties.put($$$ERRORS, errors);
        }
        return properties;
    }

    private static int eol(int c, Reader reader) throws IOException {
        while (c != 10 && c != -1) {
            c = reader.read();
        }
        return c;
    }

    private static int ws(int c, Reader reader) throws IOException {
        while (Character.isWhitespace(c) || c == 13) {
            c = reader.read();
        }
        return c;
    }
}

