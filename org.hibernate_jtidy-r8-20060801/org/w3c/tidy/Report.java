/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.w3c.tidy.AttVal;
import org.w3c.tidy.Lexer;
import org.w3c.tidy.Node;
import org.w3c.tidy.PPrint;
import org.w3c.tidy.ParsePropertyImpl;
import org.w3c.tidy.TagTable;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

public final class Report {
    public static final String ACCESS_URL = "http://www.w3.org/WAI/GL";
    public static final Date RELEASE_DATE = new Date(1096227718000L);
    public static final String RELEASE_DATE_STRING = new SimpleDateFormat("dd MMM yyyy").format(RELEASE_DATE);
    public static final short MISSING_SEMICOLON = 1;
    public static final short MISSING_SEMICOLON_NCR = 2;
    public static final short UNKNOWN_ENTITY = 3;
    public static final short UNESCAPED_AMPERSAND = 4;
    public static final short APOS_UNDEFINED = 5;
    public static final short MISSING_ENDTAG_FOR = 6;
    public static final short MISSING_ENDTAG_BEFORE = 7;
    public static final short DISCARDING_UNEXPECTED = 8;
    public static final short NESTED_EMPHASIS = 9;
    public static final short NON_MATCHING_ENDTAG = 10;
    public static final short TAG_NOT_ALLOWED_IN = 11;
    public static final short MISSING_STARTTAG = 12;
    public static final short UNEXPECTED_ENDTAG = 13;
    public static final short USING_BR_INPLACE_OF = 14;
    public static final short INSERTING_TAG = 15;
    public static final short SUSPECTED_MISSING_QUOTE = 16;
    public static final short MISSING_TITLE_ELEMENT = 17;
    public static final short DUPLICATE_FRAMESET = 18;
    public static final short CANT_BE_NESTED = 19;
    public static final short OBSOLETE_ELEMENT = 20;
    public static final short PROPRIETARY_ELEMENT = 21;
    public static final short UNKNOWN_ELEMENT = 22;
    public static final short TRIM_EMPTY_ELEMENT = 23;
    public static final short COERCE_TO_ENDTAG = 24;
    public static final short ILLEGAL_NESTING = 25;
    public static final short NOFRAMES_CONTENT = 26;
    public static final short CONTENT_AFTER_BODY = 27;
    public static final short INCONSISTENT_VERSION = 28;
    public static final short MALFORMED_COMMENT = 29;
    public static final short BAD_COMMENT_CHARS = 30;
    public static final short BAD_XML_COMMENT = 31;
    public static final short BAD_CDATA_CONTENT = 32;
    public static final short INCONSISTENT_NAMESPACE = 33;
    public static final short DOCTYPE_AFTER_TAGS = 34;
    public static final short MALFORMED_DOCTYPE = 35;
    public static final short UNEXPECTED_END_OF_FILE = 36;
    public static final short DTYPE_NOT_UPPER_CASE = 37;
    public static final short TOO_MANY_ELEMENTS = 38;
    public static final short UNESCAPED_ELEMENT = 39;
    public static final short NESTED_QUOTATION = 40;
    public static final short ELEMENT_NOT_EMPTY = 41;
    public static final short ENCODING_IO_CONFLICT = 42;
    public static final short MIXED_CONTENT_IN_BLOCK = 43;
    public static final short MISSING_DOCTYPE = 44;
    public static final short SPACE_PRECEDING_XMLDECL = 45;
    public static final short TOO_MANY_ELEMENTS_IN = 46;
    public static final short UNEXPECTED_ENDTAG_IN = 47;
    public static final short REPLACING_ELEMENT = 83;
    public static final short REPLACING_UNEX_ELEMENT = 84;
    public static final short COERCE_TO_ENDTAG_WARN = 85;
    public static final short UNKNOWN_ATTRIBUTE = 48;
    public static final short MISSING_ATTRIBUTE = 49;
    public static final short MISSING_ATTR_VALUE = 50;
    public static final short BAD_ATTRIBUTE_VALUE = 51;
    public static final short UNEXPECTED_GT = 52;
    public static final short PROPRIETARY_ATTRIBUTE = 53;
    public static final short PROPRIETARY_ATTR_VALUE = 54;
    public static final short REPEATED_ATTRIBUTE = 55;
    public static final short MISSING_IMAGEMAP = 56;
    public static final short XML_ATTRIBUTE_VALUE = 57;
    public static final short MISSING_QUOTEMARK = 58;
    public static final short UNEXPECTED_QUOTEMARK = 59;
    public static final short ID_NAME_MISMATCH = 60;
    public static final short BACKSLASH_IN_URI = 61;
    public static final short FIXED_BACKSLASH = 62;
    public static final short ILLEGAL_URI_REFERENCE = 63;
    public static final short ESCAPED_ILLEGAL_URI = 64;
    public static final short NEWLINE_IN_URI = 65;
    public static final short ANCHOR_NOT_UNIQUE = 66;
    public static final short ENTITY_IN_ID = 67;
    public static final short JOINING_ATTRIBUTE = 68;
    public static final short UNEXPECTED_EQUALSIGN = 69;
    public static final short ATTR_VALUE_NOT_LCASE = 70;
    public static final short XML_ID_SYNTAX = 71;
    public static final short INVALID_ATTRIBUTE = 72;
    public static final short BAD_ATTRIBUTE_VALUE_REPLACED = 73;
    public static final short INVALID_XML_ID = 74;
    public static final short UNEXPECTED_END_OF_FILE_ATTR = 75;
    public static final short VENDOR_SPECIFIC_CHARS = 76;
    public static final short INVALID_SGML_CHARS = 77;
    public static final short INVALID_UTF8 = 78;
    public static final short INVALID_UTF16 = 79;
    public static final short ENCODING_MISMATCH = 80;
    public static final short INVALID_URI = 81;
    public static final short INVALID_NCR = 82;
    public static final short DOCTYPE_GIVEN_SUMMARY = 110;
    public static final short REPORT_VERSION_SUMMARY = 111;
    public static final short BADACCESS_SUMMARY = 112;
    public static final short BADFORM_SUMMARY = 113;
    public static final short MISSING_IMAGE_ALT = 1;
    public static final short MISSING_LINK_ALT = 2;
    public static final short MISSING_SUMMARY = 4;
    public static final short MISSING_IMAGE_MAP = 8;
    public static final short USING_FRAMES = 16;
    public static final short USING_NOFRAMES = 32;
    public static final short USING_SPACER = 1;
    public static final short USING_LAYER = 2;
    public static final short USING_NOBR = 4;
    public static final short USING_FONT = 8;
    public static final short USING_BODY = 16;
    public static final short WINDOWS_CHARS = 1;
    public static final short NON_ASCII = 2;
    public static final short FOUND_UTF16 = 4;
    public static final short REPLACED_CHAR = 0;
    public static final short DISCARDED_CHAR = 1;
    private static ResourceBundle res;
    private String currentFile;
    private TidyMessageListener listener;

    protected Report() {
    }

    protected String getMessage(int errorCode, Lexer lexer, String message, Object[] params, TidyMessage.Level level) throws MissingResourceException {
        String resource = res.getString(message);
        String position = lexer != null && level != TidyMessage.Level.SUMMARY ? this.getPosition(lexer) : "";
        String prefix = level == TidyMessage.Level.ERROR ? res.getString("error") : (level == TidyMessage.Level.WARNING ? res.getString("warning") : "");
        String messageString = params != null ? MessageFormat.format(resource, params) : resource;
        if (this.listener != null) {
            TidyMessage msg = new TidyMessage(errorCode, lexer != null ? lexer.lines : 0, lexer != null ? lexer.columns : 0, level, messageString);
            this.listener.messageReceived(msg);
        }
        return position + prefix + messageString;
    }

    private void printMessage(int errorCode, Lexer lexer, String message, Object[] params, TidyMessage.Level level) {
        String resource;
        try {
            resource = this.getMessage(errorCode, lexer, message, params, level);
        }
        catch (MissingResourceException e) {
            lexer.errout.println(e.toString());
            return;
        }
        lexer.errout.println(resource);
    }

    private void printMessage(PrintWriter errout, String message, Object[] params, TidyMessage.Level level) {
        String resource;
        try {
            resource = this.getMessage(-1, null, message, params, level);
        }
        catch (MissingResourceException e) {
            errout.println(e.toString());
            return;
        }
        errout.println(resource);
    }

    public void showVersion(PrintWriter p) {
        this.printMessage(p, "version_summary", new Object[]{RELEASE_DATE}, TidyMessage.Level.SUMMARY);
    }

    private String getTagName(Node tag) {
        if (tag != null) {
            if (tag.type == 5) {
                return "<" + tag.element + ">";
            }
            if (tag.type == 6) {
                return "</" + tag.element + ">";
            }
            if (tag.type == 1) {
                return "<!DOCTYPE>";
            }
            if (tag.type == 4) {
                return "plain text";
            }
            return tag.element;
        }
        return "";
    }

    public void unknownOption(String option) {
        try {
            System.err.println(MessageFormat.format(res.getString("unknown_option"), option));
        }
        catch (MissingResourceException e) {
            System.err.println(e.toString());
        }
    }

    public void badArgument(String key, String value) {
        try {
            System.err.println(MessageFormat.format(res.getString("bad_argument"), value, key));
        }
        catch (MissingResourceException e) {
            System.err.println(e.toString());
        }
    }

    private String getPosition(Lexer lexer) {
        try {
            if (lexer.configuration.emacs) {
                return MessageFormat.format(res.getString("emacs_format"), this.currentFile, new Integer(lexer.lines), new Integer(lexer.columns)) + " ";
            }
            return MessageFormat.format(res.getString("line_column"), new Integer(lexer.lines), new Integer(lexer.columns));
        }
        catch (MissingResourceException e) {
            lexer.errout.println(e.toString());
            return "";
        }
    }

    public void encodingError(Lexer lexer, int code, int c) {
        lexer.warnings = (short)(lexer.warnings + 1);
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            String buf = Integer.toHexString(c);
            if ((code & 0xFFFFFFFE) == 80) {
                lexer.badChars = (short)(lexer.badChars | 0x50);
                this.printMessage(code, lexer, "encoding_mismatch", new Object[]{lexer.configuration.getInCharEncodingName(), ParsePropertyImpl.CHAR_ENCODING.getFriendlyName(null, new Integer(c), lexer.configuration)}, TidyMessage.Level.WARNING);
            } else if ((code & 0xFFFFFFFE) == 76) {
                lexer.badChars = (short)(lexer.badChars | 0x4C);
                this.printMessage(code, lexer, "invalid_char", new Object[]{new Integer(code & 1), buf}, TidyMessage.Level.WARNING);
            } else if ((code & 0xFFFFFFFE) == 77) {
                lexer.badChars = (short)(lexer.badChars | 0x4D);
                this.printMessage(code, lexer, "invalid_char", new Object[]{new Integer(code & 1), buf}, TidyMessage.Level.WARNING);
            } else if ((code & 0xFFFFFFFE) == 78) {
                lexer.badChars = (short)(lexer.badChars | 0x4E);
                this.printMessage(code, lexer, "invalid_utf8", new Object[]{new Integer(code & 1), buf}, TidyMessage.Level.WARNING);
            } else if ((code & 0xFFFFFFFE) == 79) {
                lexer.badChars = (short)(lexer.badChars | 0x4F);
                this.printMessage(code, lexer, "invalid_utf16", new Object[]{new Integer(code & 1), buf}, TidyMessage.Level.WARNING);
            } else if ((code & 0xFFFFFFFE) == 82) {
                lexer.badChars = (short)(lexer.badChars | 0x52);
                this.printMessage(code, lexer, "invalid_ncr", new Object[]{new Integer(code & 1), buf}, TidyMessage.Level.WARNING);
            }
        }
    }

    public void entityError(Lexer lexer, short code, String entity, int c) {
        lexer.warnings = (short)(lexer.warnings + 1);
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            switch (code) {
                case 1: {
                    this.printMessage(code, lexer, "missing_semicolon", new Object[]{entity}, TidyMessage.Level.WARNING);
                    break;
                }
                case 2: {
                    this.printMessage(code, lexer, "missing_semicolon_ncr", new Object[]{entity}, TidyMessage.Level.WARNING);
                    break;
                }
                case 3: {
                    this.printMessage(code, lexer, "unknown_entity", new Object[]{entity}, TidyMessage.Level.WARNING);
                    break;
                }
                case 4: {
                    this.printMessage(code, lexer, "unescaped_ampersand", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 5: {
                    this.printMessage(code, lexer, "apos_undefined", null, TidyMessage.Level.WARNING);
                    break;
                }
            }
        }
    }

    public void attrError(Lexer lexer, Node node, AttVal attribute, short code) {
        if (code == 52) {
            lexer.errors = (short)(lexer.errors + 1);
        } else {
            lexer.warnings = (short)(lexer.warnings + 1);
        }
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (code == 52) {
            this.printMessage(code, lexer, "unexpected_gt", new Object[]{this.getTagName(node)}, TidyMessage.Level.ERROR);
        }
        if (!lexer.configuration.showWarnings) {
            return;
        }
        switch (code) {
            case 48: {
                this.printMessage(code, lexer, "unknown_attribute", new Object[]{attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 49: {
                this.printMessage(code, lexer, "missing_attribute", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 50: {
                this.printMessage(code, lexer, "missing_attr_value", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 56: {
                this.printMessage(code, lexer, "missing_imagemap", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                lexer.badAccess = (short)(lexer.badAccess | 8);
                break;
            }
            case 51: {
                this.printMessage(code, lexer, "bad_attribute_value", new Object[]{this.getTagName(node), attribute.attribute, attribute.value}, TidyMessage.Level.WARNING);
                break;
            }
            case 71: {
                this.printMessage(code, lexer, "xml_id_sintax", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 57: {
                this.printMessage(code, lexer, "xml_attribute_value", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 59: {
                this.printMessage(code, lexer, "unexpected_quotemark", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 58: {
                this.printMessage(code, lexer, "missing_quotemark", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 55: {
                this.printMessage(code, lexer, "repeated_attribute", new Object[]{this.getTagName(node), attribute.value, attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 54: {
                this.printMessage(code, lexer, "proprietary_attr_value", new Object[]{this.getTagName(node), attribute.value}, TidyMessage.Level.WARNING);
                break;
            }
            case 53: {
                this.printMessage(code, lexer, "proprietary_attribute", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 36: {
                lexer.lines = lexer.in.getCurline();
                lexer.columns = lexer.in.getCurcol();
                this.printMessage(code, lexer, "unexpected_end_of_file", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 60: {
                this.printMessage(code, lexer, "id_name_mismatch", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 61: {
                this.printMessage(code, lexer, "backslash_in_uri", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 62: {
                this.printMessage(code, lexer, "fixed_backslash", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 63: {
                this.printMessage(code, lexer, "illegal_uri_reference", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 64: {
                this.printMessage(code, lexer, "escaped_illegal_uri", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 65: {
                this.printMessage(code, lexer, "newline_in_uri", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 66: {
                this.printMessage(code, lexer, "anchor_not_unique", new Object[]{this.getTagName(node), attribute.value}, TidyMessage.Level.WARNING);
                break;
            }
            case 67: {
                this.printMessage(code, lexer, "entity_in_id", null, TidyMessage.Level.WARNING);
                break;
            }
            case 68: {
                this.printMessage(code, lexer, "joining_attribute", new Object[]{this.getTagName(node), attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
            case 69: {
                this.printMessage(code, lexer, "expected_equalsign", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                break;
            }
            case 70: {
                this.printMessage(code, lexer, "attr_value_not_lcase", new Object[]{this.getTagName(node), attribute.value, attribute.attribute}, TidyMessage.Level.WARNING);
                break;
            }
        }
    }

    public void warning(Lexer lexer, Node element, Node node, short code) {
        TagTable tt = lexer.configuration.tt;
        if (code != 8 || lexer.badForm == 0) {
            lexer.warnings = (short)(lexer.warnings + 1);
        }
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            switch (code) {
                case 6: {
                    this.printMessage(code, lexer, "missing_endtag_for", new Object[]{element.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 7: {
                    this.printMessage(code, lexer, "missing_endtag_before", new Object[]{element.element, this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 8: {
                    if (lexer.badForm != 0) break;
                    this.printMessage(code, lexer, "discarding_unexpected", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 9: {
                    this.printMessage(code, lexer, "nested_emphasis", new Object[]{this.getTagName(node)}, TidyMessage.Level.INFO);
                    break;
                }
                case 24: {
                    this.printMessage(code, lexer, "coerce_to_endtag", new Object[]{element.element}, TidyMessage.Level.INFO);
                    break;
                }
                case 10: {
                    this.printMessage(code, lexer, "non_matching_endtag", new Object[]{this.getTagName(node), element.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 11: {
                    this.printMessage(code, lexer, "tag_not_allowed_in", new Object[]{this.getTagName(node), element.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 34: {
                    this.printMessage(code, lexer, "doctype_after_tags", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 12: {
                    this.printMessage(code, lexer, "missing_starttag", new Object[]{node.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 13: {
                    if (element != null) {
                        this.printMessage(code, lexer, "unexpected_endtag_in", new Object[]{node.element, element.element}, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(code, lexer, "unexpected_endtag", new Object[]{node.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 38: {
                    if (element != null) {
                        this.printMessage(code, lexer, "too_many_elements_in", new Object[]{node.element, element.element}, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(code, lexer, "too_many_elements", new Object[]{node.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 14: {
                    this.printMessage(code, lexer, "using_br_inplace_of", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 15: {
                    this.printMessage(code, lexer, "inserting_tag", new Object[]{node.element}, TidyMessage.Level.WARNING);
                    break;
                }
                case 19: {
                    this.printMessage(code, lexer, "cant_be_nested", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 21: {
                    this.printMessage(code, lexer, "proprietary_element", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                    if (node.tag == tt.tagLayer) {
                        lexer.badLayout = (short)(lexer.badLayout | 2);
                        break;
                    }
                    if (node.tag == tt.tagSpacer) {
                        lexer.badLayout = (short)(lexer.badLayout | 1);
                        break;
                    }
                    if (node.tag != tt.tagNobr) break;
                    lexer.badLayout = (short)(lexer.badLayout | 4);
                    break;
                }
                case 20: {
                    if (element.tag != null && (element.tag.model & 0x80000) != 0) {
                        this.printMessage(code, lexer, "obsolete_element", new Object[]{this.getTagName(element), this.getTagName(node)}, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(code, lexer, "replacing_element", new Object[]{this.getTagName(element), this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 39: {
                    this.printMessage(code, lexer, "unescaped_element", new Object[]{this.getTagName(element)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 23: {
                    this.printMessage(code, lexer, "trim_empty_element", new Object[]{this.getTagName(element)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 17: {
                    this.printMessage(code, lexer, "missing_title_element", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 25: {
                    this.printMessage(code, lexer, "illegal_nesting", new Object[]{this.getTagName(element)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 26: {
                    this.printMessage(code, lexer, "noframes_content", new Object[]{this.getTagName(node)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 28: {
                    this.printMessage(code, lexer, "inconsistent_version", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 35: {
                    this.printMessage(code, lexer, "malformed_doctype", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 27: {
                    this.printMessage(code, lexer, "content_after_body", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 29: {
                    this.printMessage(code, lexer, "malformed_comment", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 30: {
                    this.printMessage(code, lexer, "bad_comment_chars", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 31: {
                    this.printMessage(code, lexer, "bad_xml_comment", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 32: {
                    this.printMessage(code, lexer, "bad_cdata_content", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 33: {
                    this.printMessage(code, lexer, "inconsistent_namespace", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 37: {
                    this.printMessage(code, lexer, "dtype_not_upper_case", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 36: {
                    lexer.lines = lexer.in.getCurline();
                    lexer.columns = lexer.in.getCurcol();
                    this.printMessage(code, lexer, "unexpected_end_of_file", new Object[]{this.getTagName(element)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 40: {
                    this.printMessage(code, lexer, "nested_quotation", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 41: {
                    this.printMessage(code, lexer, "element_not_empty", new Object[]{this.getTagName(element)}, TidyMessage.Level.WARNING);
                    break;
                }
                case 44: {
                    this.printMessage(code, lexer, "missing_doctype", null, TidyMessage.Level.WARNING);
                    break;
                }
            }
        }
        if (code == 8 && lexer.badForm != 0) {
            this.printMessage(code, lexer, "discarding_unexpected", new Object[]{this.getTagName(node)}, TidyMessage.Level.ERROR);
        }
    }

    public void error(Lexer lexer, Node element, Node node, short code) {
        lexer.errors = (short)(lexer.errors + 1);
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (code == 16) {
            this.printMessage(code, lexer, "suspected_missing_quote", null, TidyMessage.Level.ERROR);
        } else if (code == 18) {
            this.printMessage(code, lexer, "duplicate_frameset", null, TidyMessage.Level.ERROR);
        } else if (code == 22) {
            this.printMessage(code, lexer, "unknown_element", new Object[]{this.getTagName(node)}, TidyMessage.Level.ERROR);
        } else if (code == 13) {
            if (element != null) {
                this.printMessage(code, lexer, "unexpected_endtag_in", new Object[]{node.element, element.element}, TidyMessage.Level.ERROR);
            } else {
                this.printMessage(code, lexer, "unexpected_endtag", new Object[]{node.element}, TidyMessage.Level.ERROR);
            }
        }
    }

    public void errorSummary(Lexer lexer) {
        if ((lexer.badAccess & 0x30) != 0 && ((lexer.badAccess & 0x10) == 0 || (lexer.badAccess & 0x20) != 0)) {
            lexer.badAccess = (short)(lexer.badAccess & 0xFFFFFFCF);
        }
        if (lexer.badChars != 0) {
            int encodingChoiche;
            if ((lexer.badChars & 0x4C) != 0) {
                encodingChoiche = 0;
                if ("Cp1252".equals(lexer.configuration.getInCharEncodingName())) {
                    encodingChoiche = 1;
                } else if ("MacRoman".equals(lexer.configuration.getInCharEncodingName())) {
                    encodingChoiche = 2;
                }
                this.printMessage(76, lexer, "vendor_specific_chars_summary", new Object[]{new Integer(encodingChoiche)}, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4D) != 0 || (lexer.badChars & 0x52) != 0) {
                encodingChoiche = 0;
                if ("Cp1252".equals(lexer.configuration.getInCharEncodingName())) {
                    encodingChoiche = 1;
                } else if ("MacRoman".equals(lexer.configuration.getInCharEncodingName())) {
                    encodingChoiche = 2;
                }
                this.printMessage(77, lexer, "invalid_sgml_chars_summary", new Object[]{new Integer(encodingChoiche)}, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4E) != 0) {
                this.printMessage(78, lexer, "invalid_utf8_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4F) != 0) {
                this.printMessage(79, lexer, "invalid_utf16_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x51) != 0) {
                this.printMessage(81, lexer, "invaliduri_summary", null, TidyMessage.Level.SUMMARY);
            }
        }
        if (lexer.badForm != 0) {
            this.printMessage(113, lexer, "badform_summary", null, TidyMessage.Level.SUMMARY);
        }
        if (lexer.badAccess != 0) {
            if ((lexer.badAccess & 4) != 0) {
                this.printMessage(4, lexer, "badaccess_missing_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 1) != 0) {
                this.printMessage(1, lexer, "badaccess_missing_image_alt", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 8) != 0) {
                this.printMessage(8, lexer, "badaccess_missing_image_map", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 2) != 0) {
                this.printMessage(2, lexer, "badaccess_missing_link_alt", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 0x10) != 0 && (lexer.badAccess & 0x20) == 0) {
                this.printMessage(16, lexer, "badaccess_frames", null, TidyMessage.Level.SUMMARY);
            }
            this.printMessage(112, lexer, "badaccess_summary", new Object[]{ACCESS_URL}, TidyMessage.Level.SUMMARY);
        }
        if (lexer.badLayout != 0) {
            if ((lexer.badLayout & 2) != 0) {
                this.printMessage(2, lexer, "badlayout_using_layer", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 1) != 0) {
                this.printMessage(1, lexer, "badlayout_using_spacer", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 8) != 0) {
                this.printMessage(8, lexer, "badlayout_using_font", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 4) != 0) {
                this.printMessage(4, lexer, "badlayout_using_nobr", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 0x10) != 0) {
                this.printMessage(16, lexer, "badlayout_using_body", null, TidyMessage.Level.SUMMARY);
            }
        }
    }

    public void unknownOption(PrintWriter errout, char c) {
        this.printMessage(errout, "unrecognized_option", new Object[]{new String(new char[]{c})}, TidyMessage.Level.ERROR);
    }

    public void unknownFile(PrintWriter errout, String file) {
        this.printMessage(errout, "unknown_file", new Object[]{"Tidy", file}, TidyMessage.Level.ERROR);
    }

    public void needsAuthorIntervention(PrintWriter errout) {
        this.printMessage(errout, "needs_author_intervention", null, TidyMessage.Level.SUMMARY);
    }

    public void missingBody(PrintWriter errout) {
        this.printMessage(errout, "missing_body", null, TidyMessage.Level.ERROR);
    }

    public void reportNumberOfSlides(PrintWriter errout, int count) {
        this.printMessage(errout, "slides_found", new Object[]{new Integer(count)}, TidyMessage.Level.SUMMARY);
    }

    public void generalInfo(PrintWriter errout) {
        this.printMessage(errout, "general_info", null, TidyMessage.Level.SUMMARY);
    }

    public void helloMessage(PrintWriter errout) {
        this.printMessage(errout, "hello_message", new Object[]{RELEASE_DATE, this.currentFile}, TidyMessage.Level.SUMMARY);
    }

    public void setFilename(String filename) {
        this.currentFile = filename;
    }

    public void reportVersion(PrintWriter errout, Lexer lexer, String filename, Node doctype) {
        int state = 0;
        String vers = lexer.htmlVersionName();
        int[] cc = new int[1];
        lexer.lines = 1;
        lexer.columns = 1;
        if (doctype != null) {
            StringBuffer doctypeBuffer = new StringBuffer();
            for (int i = doctype.start; i < doctype.end; ++i) {
                int c = doctype.textarray[i];
                if (c < 0) {
                    i += PPrint.getUTF8(doctype.textarray, i, cc);
                    c = cc[0];
                }
                if (c == 34) {
                    ++state;
                    continue;
                }
                if (state != 1) continue;
                doctypeBuffer.append((char)c);
            }
            this.printMessage(110, lexer, "doctype_given", new Object[]{filename, doctypeBuffer}, TidyMessage.Level.SUMMARY);
        }
        this.printMessage(111, lexer, "report_version", new Object[]{filename, vers != null ? vers : "HTML proprietary"}, TidyMessage.Level.SUMMARY);
    }

    public void reportNumWarnings(PrintWriter errout, Lexer lexer) {
        if (lexer.warnings > 0 || lexer.errors > 0) {
            this.printMessage(errout, "num_warnings", new Object[]{new Integer(lexer.warnings), new Integer(lexer.errors)}, TidyMessage.Level.SUMMARY);
        } else {
            this.printMessage(errout, "no_warnings", null, TidyMessage.Level.SUMMARY);
        }
    }

    public void helpText(PrintWriter out) {
        this.printMessage(out, "help_text", new Object[]{"Tidy", RELEASE_DATE}, TidyMessage.Level.SUMMARY);
    }

    public void badTree(PrintWriter errout) {
        this.printMessage(errout, "bad_tree", null, TidyMessage.Level.ERROR);
    }

    public void addMessageListener(TidyMessageListener listener) {
        this.listener = listener;
    }

    static {
        try {
            res = ResourceBundle.getBundle("org/w3c/tidy/TidyMessages");
        }
        catch (MissingResourceException e) {
            throw new Error(e.toString());
        }
    }
}

