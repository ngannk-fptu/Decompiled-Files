/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.propertyset.BucketPropertySetItem
 *  com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType
 *  com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle
 *  com.atlassian.confluence.impl.hibernate.extras.ExportProgress
 *  com.atlassian.confluence.impl.hibernate.extras.HibernateTranslator
 *  com.atlassian.confluence.impl.hibernate.extras.XMLDatabinder
 *  com.atlassian.hibernate.BucketClobStringType
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.io.ByteStreams
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.type.StringType
 *  org.hibernate.type.Type
 */
package com.atlassian.confluence.importexport.impl;

import bucket.user.propertyset.BucketPropertySetItem;
import com.atlassian.confluence.core.persistence.hibernate.CustomClobType;
import com.atlassian.confluence.core.persistence.hibernate.InstantType;
import com.atlassian.confluence.impl.hibernate.SpoolingBlobInputStreamType;
import com.atlassian.confluence.impl.hibernate.extras.ExportHibernateHandle;
import com.atlassian.confluence.impl.hibernate.extras.ExportProgress;
import com.atlassian.confluence.impl.hibernate.extras.HibernateTranslator;
import com.atlassian.confluence.impl.hibernate.extras.XMLDatabinder;
import com.atlassian.confluence.importexport.impl.HibernateObjectHandleTranslator;
import com.atlassian.confluence.labels.persistence.dao.hibernate.NamespaceUserType;
import com.atlassian.confluence.security.persistence.dao.hibernate.CryptographicKeyType;
import com.atlassian.confluence.security.persistence.dao.hibernate.KeyTransferBean;
import com.atlassian.confluence.user.persistence.dao.hibernate.UserKeyUserType;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.hibernate.BucketClobStringType;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class AtlassianXMLDatabinder
extends XMLDatabinder {
    public static final String NAME_BUCKET_CLOB_STRING_TYPE = BucketClobStringType.class.getName();
    public static final String NAME_CUSTOM_CLOB_TYPE = CustomClobType.class.getName();
    public static final String NAME_NAMESPACE_USER_TYPE = NamespaceUserType.class.getName();
    public static final String NAME_CRYPTOGRAPHIC_KEY_TYPE = CryptographicKeyType.class.getName();
    public static final String NAME_SPOOLING_BLOB_INPUT_STREAM_TYPE = SpoolingBlobInputStreamType.class.getName();
    public static final String NAME_USER_KEY_TYPE = UserKeyUserType.class.getName();
    public static final String NAME_INSTANT_TYPE = InstantType.class.getName();

    public AtlassianXMLDatabinder(SessionFactoryImplementor factory, String encoding, HibernateObjectHandleTranslator translator) {
        super(factory, encoding, (HibernateTranslator)translator);
    }

    public void toGenericXML(Writer writer, ExportProgress progressMeter) throws IOException, HibernateException {
        this.moveBucketItemsFromHandlesToBucketHandles();
        super.toGenericXML(writer, progressMeter);
    }

    private void moveBucketItemsFromHandlesToBucketHandles() {
        Iterator it = this.handles.iterator();
        while (it.hasNext()) {
            ExportHibernateHandle handle = (ExportHibernateHandle)it.next();
            Class clazz = handle.getClazz();
            if (clazz != BucketPropertySetItem.class) continue;
            it.remove();
            this.bucketHandles.add(handle);
        }
    }

    public boolean parseCustomType(Writer writer, Type type, Object value, String xmlValue) throws IOException {
        boolean parsed = false;
        String typeName = type.getName();
        if (type instanceof StringType || typeName.equals(NAME_BUCKET_CLOB_STRING_TYPE) || typeName.equals(NAME_CUSTOM_CLOB_TYPE) || typeName.equals(NAME_NAMESPACE_USER_TYPE)) {
            writer.write("<![CDATA[" + GeneralUtil.escapeCDATA(xmlValue) + "]]>");
            parsed = true;
        } else if (typeName.equals(NAME_CRYPTOGRAPHIC_KEY_TYPE)) {
            writer.write(new KeyTransferBean((Key)value).asCDataEncodedString());
            parsed = true;
        } else if (typeName.equals(NAME_SPOOLING_BLOB_INPUT_STREAM_TYPE)) {
            InputStream is = (InputStream)value;
            if (is.markSupported()) {
                is.mark(Integer.MAX_VALUE);
            }
            byte[] bytes = ByteStreams.toByteArray((InputStream)is);
            if (is.markSupported()) {
                is.reset();
            }
            String base64 = Base64.getEncoder().encodeToString(bytes);
            writer.write("<![CDATA[" + base64 + "]]>");
            parsed = true;
        } else if (typeName.equals(NAME_USER_KEY_TYPE)) {
            writer.write("<![CDATA[" + GeneralUtil.escapeCDATA(((UserKey)value).getStringValue()) + "]]>");
            parsed = true;
        } else if (typeName.equals(NAME_INSTANT_TYPE)) {
            writer.write("<![CDATA[" + GeneralUtil.escapeCDATA(Long.toString(((Instant)value).toEpochMilli())) + "]]>");
            parsed = true;
        }
        return parsed;
    }

    protected Object maybeInitializeIfProxy(Object object) {
        return Hibernate.unproxy((Object)object);
    }
}

