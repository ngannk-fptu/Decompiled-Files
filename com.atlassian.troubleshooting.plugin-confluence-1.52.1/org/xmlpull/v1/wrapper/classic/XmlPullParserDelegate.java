/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlPullParserDelegate
implements XmlPullParser {
    protected XmlPullParser pp;

    public XmlPullParserDelegate(XmlPullParser pp) {
        this.pp = pp;
    }

    public String getText() {
        return this.pp.getText();
    }

    public void setFeature(String name, boolean state) throws XmlPullParserException {
        this.pp.setFeature(name, state);
    }

    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return this.pp.getTextCharacters(holderForStartAndLength);
    }

    public int getColumnNumber() {
        return this.pp.getColumnNumber();
    }

    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return this.pp.getNamespaceCount(depth);
    }

    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return this.pp.getNamespacePrefix(pos);
    }

    public String getAttributeName(int index) {
        return this.pp.getAttributeName(index);
    }

    public String getName() {
        return this.pp.getName();
    }

    public boolean getFeature(String name) {
        return this.pp.getFeature(name);
    }

    public String getInputEncoding() {
        return this.pp.getInputEncoding();
    }

    public String getAttributeValue(int index) {
        return this.pp.getAttributeValue(index);
    }

    public String getNamespace(String prefix) {
        return this.pp.getNamespace(prefix);
    }

    public void setInput(Reader in) throws XmlPullParserException {
        this.pp.setInput(in);
    }

    public int getLineNumber() {
        return this.pp.getLineNumber();
    }

    public Object getProperty(String name) {
        return this.pp.getProperty(name);
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {
        return this.pp.isEmptyElementTag();
    }

    public boolean isAttributeDefault(int index) {
        return this.pp.isAttributeDefault(index);
    }

    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return this.pp.getNamespaceUri(pos);
    }

    public int next() throws XmlPullParserException, IOException {
        return this.pp.next();
    }

    public int nextToken() throws XmlPullParserException, IOException {
        return this.pp.nextToken();
    }

    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        this.pp.defineEntityReplacementText(entityName, replacementText);
    }

    public int getAttributeCount() {
        return this.pp.getAttributeCount();
    }

    public boolean isWhitespace() throws XmlPullParserException {
        return this.pp.isWhitespace();
    }

    public String getPrefix() {
        return this.pp.getPrefix();
    }

    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        this.pp.require(type, namespace, name);
    }

    public String nextText() throws XmlPullParserException, IOException {
        return this.pp.nextText();
    }

    public String getAttributeType(int index) {
        return this.pp.getAttributeType(index);
    }

    public int getDepth() {
        return this.pp.getDepth();
    }

    public int nextTag() throws XmlPullParserException, IOException {
        return this.pp.nextTag();
    }

    public int getEventType() throws XmlPullParserException {
        return this.pp.getEventType();
    }

    public String getAttributePrefix(int index) {
        return this.pp.getAttributePrefix(index);
    }

    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        this.pp.setInput(inputStream, inputEncoding);
    }

    public String getAttributeValue(String namespace, String name) {
        return this.pp.getAttributeValue(namespace, name);
    }

    public void setProperty(String name, Object value) throws XmlPullParserException {
        this.pp.setProperty(name, value);
    }

    public String getPositionDescription() {
        return this.pp.getPositionDescription();
    }

    public String getNamespace() {
        return this.pp.getNamespace();
    }

    public String getAttributeNamespace(int index) {
        return this.pp.getAttributeNamespace(index);
    }
}

