/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

public interface Property
extends Item {
    public static final String JCR_PRIMARY_TYPE = "{http://www.jcp.org/jcr/1.0}primaryType";
    public static final String JCR_MIXIN_TYPES = "{http://www.jcp.org/jcr/1.0}mixinTypes";
    public static final String JCR_CONTENT = "{http://www.jcp.org/jcr/1.0}content";
    public static final String JCR_DATA = "{http://www.jcp.org/jcr/1.0}data";
    public static final String JCR_PROTOCOL = "{http://www.jcp.org/jcr/1.0}protocol";
    public static final String JCR_HOST = "{http://www.jcp.org/jcr/1.0}host";
    public static final String JCR_PORT = "{http://www.jcp.org/jcr/1.0}port";
    public static final String JCR_REPOSITORY = "{http://www.jcp.org/jcr/1.0}repository";
    public static final String JCR_WORKSPACE = "{http://www.jcp.org/jcr/1.0}workspace";
    public static final String JCR_PATH = "{http://www.jcp.org/jcr/1.0}path";
    public static final String JCR_ID = "{http://www.jcp.org/jcr/1.0}id";
    public static final String JCR_UUID = "{http://www.jcp.org/jcr/1.0}uuid";
    public static final String JCR_TITLE = "{http://www.jcp.org/jcr/1.0}title";
    public static final String JCR_DESCRIPTION = "{http://www.jcp.org/jcr/1.0}description";
    public static final String JCR_CREATED = "{http://www.jcp.org/jcr/1.0}created";
    public static final String JCR_CREATED_BY = "{http://www.jcp.org/jcr/1.0}createdBy";
    public static final String JCR_LAST_MODIFIED = "{http://www.jcp.org/jcr/1.0}lastModified";
    public static final String JCR_LAST_MODIFIED_BY = "{http://www.jcp.org/jcr/1.0}lastModifiedBy";
    public static final String JCR_LANGUAGE = "{http://www.jcp.org/jcr/1.0}language";
    public static final String JCR_MIMETYPE = "{http://www.jcp.org/jcr/1.0}mimeType";
    public static final String JCR_ENCODING = "{http://www.jcp.org/jcr/1.0}encoding";
    public static final String JCR_NODE_TYPE_NAME = "{http://www.jcp.org/jcr/1.0}nodeTypeName";
    public static final String JCR_SUPERTYPES = "{http://www.jcp.org/jcr/1.0}supertypes";
    public static final String JCR_IS_ABSTRACT = "{http://www.jcp.org/jcr/1.0}isAbstract";
    public static final String JCR_IS_MIXIN = "{http://www.jcp.org/jcr/1.0}isMixin";
    public static final String JCR_HAS_ORDERABLE_CHILD_NODES = "{http://www.jcp.org/jcr/1.0}hasOrderableChildNodes";
    public static final String JCR_PRIMARY_ITEM_NAME = "{http://www.jcp.org/jcr/1.0}primaryItemName";
    public static final String JCR_NAME = "{http://www.jcp.org/jcr/1.0}name";
    public static final String JCR_AUTOCREATED = "{http://www.jcp.org/jcr/1.0}autoCreated";
    public static final String JCR_MANDATORY = "{http://www.jcp.org/jcr/1.0}mandatory";
    public static final String JCR_PROTECTED = "{http://www.jcp.org/jcr/1.0}protected";
    public static final String JCR_ON_PARENT_VERSION = "{http://www.jcp.org/jcr/1.0}onParentVersion";
    public static final String JCR_REQUIRED_TYPE = "{http://www.jcp.org/jcr/1.0}requiredType";
    public static final String JCR_VALUE_CONSTRAINTS = "{http://www.jcp.org/jcr/1.0}valueConstraints";
    public static final String JCR_DEFAULT_VALUES = "{http://www.jcp.org/jcr/1.0}defaultValues";
    public static final String JCR_MULTIPLE = "{http://www.jcp.org/jcr/1.0}multiple";
    public static final String JCR_REQUIRED_PRIMARY_TYPES = "{http://www.jcp.org/jcr/1.0}requiredPrimaryTypes";
    public static final String JCR_DEFAULT_PRIMARY_TYPE = "{http://www.jcp.org/jcr/1.0}defaultPrimaryType";
    public static final String JCR_SAME_NAME_SIBLINGS = "{http://www.jcp.org/jcr/1.0}sameNameSiblings";
    public static final String JCR_LOCK_OWNER = "{http://www.jcp.org/jcr/1.0}lockOwner";
    public static final String JCR_LOCK_IS_DEEP = "{http://www.jcp.org/jcr/1.0}lockIsDeep";
    public static final String JCR_LIFECYCLE_POLICY = "{http://www.jcp.org/jcr/1.0}lifecyclePolicy";
    public static final String JCR_CURRENT_LIFECYCLE_STATE = "{http://www.jcp.org/jcr/1.0}currentLifecycleState";
    public static final String JCR_IS_CHECKED_OUT = "{http://www.jcp.org/jcr/1.0}isCheckedOut";
    public static final String JCR_FROZEN_PRIMARY_TYPE = "{http://www.jcp.org/jcr/1.0}frozenPrimaryType";
    public static final String JCR_FROZEN_MIXIN_TYPES = "{http://www.jcp.org/jcr/1.0}frozenMixinTypes";
    public static final String JCR_FROZEN_UUID = "{http://www.jcp.org/jcr/1.0}frozenUuid";
    public static final String JCR_VERSION_HISTORY = "{http://www.jcp.org/jcr/1.0}versionHistory";
    public static final String JCR_BASE_VERSION = "{http://www.jcp.org/jcr/1.0}baseVersion";
    public static final String JCR_PREDECESSORS = "{http://www.jcp.org/jcr/1.0}predecessors";
    public static final String JCR_MERGE_FAILED = "{http://www.jcp.org/jcr/1.0}mergeFailed";
    public static final String JCR_ACTIVITY = "{http://www.jcp.org/jcr/1.0}activity";
    public static final String JCR_CONFIGURATION = "{http://www.jcp.org/jcr/1.0}configuration";
    public static final String JCR_VERSIONABLE_UUID = "{http://www.jcp.org/jcr/1.0}versionableUuid";
    public static final String JCR_COPIED_FROM = "{http://www.jcp.org/jcr/1.0}copiedFrom";
    public static final String JCR_SUCCESSORS = "{http://www.jcp.org/jcr/1.0}successors";
    public static final String JCR_CHILD_VERSION_HISTORY = "{http://www.jcp.org/jcr/1.0}childVersionHistory";
    public static final String JCR_ROOT = "{http://www.jcp.org/jcr/1.0}root";
    public static final String JCR_STATEMENT = "{http://www.jcp.org/jcr/1.0}statement";

    public void setValue(Value var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(Value[] var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(String var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(String[] var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(InputStream var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(Binary var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(long var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(double var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(BigDecimal var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(Calendar var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(boolean var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public void setValue(Node var1) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException;

    public Value getValue() throws ValueFormatException, RepositoryException;

    public Value[] getValues() throws ValueFormatException, RepositoryException;

    public String getString() throws ValueFormatException, RepositoryException;

    public InputStream getStream() throws ValueFormatException, RepositoryException;

    public Binary getBinary() throws ValueFormatException, RepositoryException;

    public long getLong() throws ValueFormatException, RepositoryException;

    public double getDouble() throws ValueFormatException, RepositoryException;

    public BigDecimal getDecimal() throws ValueFormatException, RepositoryException;

    public Calendar getDate() throws ValueFormatException, RepositoryException;

    public boolean getBoolean() throws ValueFormatException, RepositoryException;

    public Node getNode() throws ItemNotFoundException, ValueFormatException, RepositoryException;

    public Property getProperty() throws ItemNotFoundException, ValueFormatException, RepositoryException;

    public long getLength() throws ValueFormatException, RepositoryException;

    public long[] getLengths() throws ValueFormatException, RepositoryException;

    public PropertyDefinition getDefinition() throws RepositoryException;

    public int getType() throws RepositoryException;

    public boolean isMultiple() throws RepositoryException;
}

