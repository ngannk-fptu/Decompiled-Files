/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;

public class DigestGenerator {
    public static final String md5DigestAlgorithm = "MD5";
    public static final String shaDigestAlgorithm = "SHA";
    public static final String sha1DigestAlgorithm = "SHA1";

    public byte[] getDigest(OMDocument document, String digestAlgorithm) throws OMException {
        byte[] digest = new byte[]{};
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(9);
            Collection childNodes = this.getValidElements(document);
            dos.writeInt(childNodes.size());
            for (OMNode node : childNodes) {
                if (node.getType() == 3) {
                    dos.write(this.getDigest((OMProcessingInstruction)node, digestAlgorithm));
                    continue;
                }
                if (node.getType() != 1) continue;
                dos.write(this.getDigest((OMElement)node, digestAlgorithm));
            }
            dos.close();
            md.update(baos.toByteArray());
            digest = md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (IOException e) {
            throw new OMException(e);
        }
        return digest;
    }

    public byte[] getDigest(OMNode node, String digestAlgorithm) {
        if (node.getType() == 1) {
            return this.getDigest((OMElement)node, digestAlgorithm);
        }
        if (node.getType() == 4) {
            return this.getDigest((OMText)node, digestAlgorithm);
        }
        if (node.getType() == 3) {
            return this.getDigest((OMProcessingInstruction)node, digestAlgorithm);
        }
        return new byte[0];
    }

    public byte[] getDigest(OMElement element, String digestAlgorithm) throws OMException {
        byte[] digest = new byte[]{};
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(1);
            dos.write(this.getExpandedName(element).getBytes("UnicodeBigUnmarked"));
            dos.write(0);
            dos.write(0);
            Collection attrs = this.getAttributesWithoutNS(element);
            dos.writeInt(attrs.size());
            Iterator itr = attrs.iterator();
            while (itr.hasNext()) {
                dos.write(this.getDigest((OMAttribute)itr.next(), digestAlgorithm));
            }
            OMNode node = element.getFirstOMChild();
            int length = 0;
            itr = element.getChildren();
            while (itr.hasNext()) {
                OMNode child = (OMNode)itr.next();
                if (!(child instanceof OMElement) && !(child instanceof OMText) && !(child instanceof OMProcessingInstruction)) continue;
                ++length;
            }
            dos.writeInt(length);
            while (node != null) {
                dos.write(this.getDigest(node, digestAlgorithm));
                node = node.getNextOMSibling();
            }
            dos.close();
            md.update(baos.toByteArray());
            digest = md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (IOException e) {
            throw new OMException(e);
        }
        return digest;
    }

    public byte[] getDigest(OMProcessingInstruction pi, String digestAlgorithm) throws OMException {
        byte[] digest = new byte[]{};
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)7);
            md.update(pi.getTarget().getBytes("UnicodeBigUnmarked"));
            md.update((byte)0);
            md.update((byte)0);
            md.update(pi.getValue().getBytes("UnicodeBigUnmarked"));
            digest = md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new OMException(e);
        }
        return digest;
    }

    public byte[] getDigest(OMAttribute attribute, String digestAlgorithm) throws OMException {
        byte[] digest = new byte[]{};
        if (!attribute.getLocalName().equals("xmlns") && !attribute.getLocalName().startsWith("xmlns:")) {
            try {
                MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
                md.update((byte)0);
                md.update((byte)0);
                md.update((byte)0);
                md.update((byte)2);
                md.update(this.getExpandedName(attribute).getBytes("UnicodeBigUnmarked"));
                md.update((byte)0);
                md.update((byte)0);
                md.update(attribute.getAttributeValue().getBytes("UnicodeBigUnmarked"));
                digest = md.digest();
            }
            catch (NoSuchAlgorithmException e) {
                throw new OMException(e);
            }
            catch (UnsupportedEncodingException e) {
                throw new OMException(e);
            }
        }
        return digest;
    }

    public byte[] getDigest(OMText text, String digestAlgorithm) throws OMException {
        byte[] digest = new byte[]{};
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)0);
            md.update((byte)3);
            md.update(text.getText().getBytes("UnicodeBigUnmarked"));
            digest = md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new OMException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new OMException(e);
        }
        return digest;
    }

    public String getExpandedName(OMElement element) {
        return this.internalGetExpandedName(element);
    }

    public String getExpandedName(OMAttribute attribute) {
        return this.internalGetExpandedName(attribute);
    }

    private String internalGetExpandedName(OMNamedInformationItem informationItem) {
        String uri = informationItem.getNamespaceURI();
        return uri == null ? informationItem.getLocalName() : uri + ":" + informationItem.getLocalName();
    }

    public Collection getAttributesWithoutNS(OMElement element) {
        TreeMap<String, OMAttribute> map = new TreeMap<String, OMAttribute>();
        Iterator itr = element.getAllAttributes();
        while (itr.hasNext()) {
            OMAttribute attribute = (OMAttribute)itr.next();
            if (attribute.getLocalName().equals("xmlns") || attribute.getLocalName().startsWith("xmlns:")) continue;
            map.put(this.getExpandedName(attribute), attribute);
        }
        return map.values();
    }

    public Collection getValidElements(OMDocument document) {
        ArrayList<OMNode> list = new ArrayList<OMNode>();
        Iterator itr = document.getChildren();
        while (itr.hasNext()) {
            OMNode node = (OMNode)itr.next();
            if (node.getType() != 1 && node.getType() != 3) continue;
            list.add(node);
        }
        return list;
    }

    public String getStringRepresentation(byte[] array) {
        String str = "";
        for (int i = 0; i < array.length; ++i) {
            str = str + array[i];
        }
        return str;
    }

    public boolean compareOMNode(OMNode node, OMNode comparingNode, String digestAlgorithm) {
        return Arrays.equals(this.getDigest(node, digestAlgorithm), this.getDigest(comparingNode, digestAlgorithm));
    }

    public boolean compareOMDocument(OMDocument document, OMDocument comparingDocument, String digestAlgorithm) {
        return Arrays.equals(this.getDigest(document, digestAlgorithm), this.getDigest(comparingDocument, digestAlgorithm));
    }

    public boolean compareOMAttribute(OMAttribute attribute, OMAttribute comparingAttribute, String digestAlgorithm) {
        return Arrays.equals(this.getDigest(attribute, digestAlgorithm), this.getDigest(comparingAttribute, digestAlgorithm));
    }
}

