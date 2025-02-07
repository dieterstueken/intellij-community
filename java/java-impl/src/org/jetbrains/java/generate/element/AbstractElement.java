/*
 * Copyright 2001-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.java.generate.element;

/**
 * Base class to extends for Elements.
 * <p/>
 * Currently there are two kind of elements: Field and Method.
 */
public abstract class AbstractElement implements Element {

    protected String name;
    protected boolean isArray;
    protected boolean isPrimitiveArray;
    protected boolean isObjectArray;
    protected boolean isStringArray;
    protected boolean isCollection;
    protected boolean isMap;
    protected boolean isSet;
    protected boolean isList;
    protected boolean isPrimitive;
    protected boolean isString;
    protected boolean isNumeric;
    protected boolean isObject;
    protected boolean isDate;
    protected boolean isCalendar;
    protected boolean isBoolean;
    protected boolean isLong;
    protected boolean isFloat;
    protected boolean isDouble;
    protected boolean isVoid;
    protected boolean isChar;
    protected boolean isByte;
    protected boolean isShort;
    protected boolean isInt;
    protected String typeName;
    protected String type;
    protected String typeQualifiedName;
    protected boolean isModifierStatic;
    protected boolean isModifierPublic;
    protected boolean isModifierProtected;
    protected boolean isModifierPackageLocal;
    protected boolean isModifierPrivate;
    protected boolean isModifierFinal;
    private boolean isNotNull;
    private boolean isNestedArray;

  @Override
  public String getName() {
        return name;
    }

    @Override
    public boolean isArray() {
        return isArray;
    }

    @Override
    public boolean isNestedArray() {
      return isNestedArray;
    }

    public void setNestedArray(boolean isNestedArray) {
      this.isNestedArray = isNestedArray;
    }

    @Override
    public boolean isCollection() {
        return isCollection;
    }

    @Override
    public boolean isMap() {
        return isMap;
    }

    @Override
    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public boolean isString() {
        return isString;
    }

    @Override
    public boolean isPrimitiveArray() {
        return isPrimitiveArray;
    }

    @Override
    public boolean isObjectArray() {
        return isObjectArray;
    }

    @Override
    public boolean isNumeric() {
        return isNumeric;
    }

    @Override
    public boolean isObject() {
        return isObject;
    }

    @Override
    public boolean isDate() {
        return isDate;
    }

    @Override
    public boolean isSet() {
        return isSet;
    }

    @Override
    public boolean isList() {
        return isList;
    }

    @Override
    public boolean isStringArray() {
        return isStringArray;
    }

    @Override
    public boolean isCalendar() {
        return isCalendar;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getTypeQualifiedName() {
        return typeQualifiedName;
    }

    @Override
    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    @Override
    public boolean isBoolean() {
        return isBoolean;
    }

    @Override
    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }

    @Override
    public boolean isFloat() {
        return isFloat;
    }

    public void setFloat(boolean isFloat) {
        this.isFloat = isFloat;
    }

    @Override
    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean isDouble) {
        this.isDouble = isDouble;
    }

    @Override
    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public boolean isNotNull() {
      return isNotNull;
    }

  public void setNotNull(boolean isNotNull) {
    this.isNotNull = isNotNull;
  }

  public void setVoid(boolean isVoid) {
        this.isVoid = isVoid;
    }

    @Override
    public boolean isChar() {
        return isChar;
    }

    public void setChar(boolean isChar) {
        this.isChar = isChar;
    }

    @Override
    public boolean isByte() {
        return isByte;
    }

    public void setByte(boolean isByte) {
        this.isByte = isByte;
    }

    @Override
    public boolean isShort() {
        return isShort;
    }

    public void setShort(boolean isShort) {
        this.isShort = isShort;
    }

  public boolean isInt() {
    return isInt;
  }

  public void setInt(boolean anInt) {
    isInt = anInt;
  }

  public void setBoolean(boolean aBoolean) {
        isBoolean = aBoolean;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setNumeric(boolean numeric) {
        isNumeric = numeric;
    }

    public void setObject(boolean object) {
        isObject = object;
    }

    void setDate(boolean date) {
        isDate = date;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    void setCollection(boolean collection) {
        isCollection = collection;
    }

    void setMap(boolean map) {
        isMap = map;
    }

     public void setPrimitive(boolean primitive) {
        isPrimitive = primitive;
    }

    public void setString(boolean string) {
        isString = string;
    }

    public void setPrimitiveArray(boolean primitiveArray) {
        isPrimitiveArray = primitiveArray;
    }

    void setObjectArray(boolean objectArray) {
        isObjectArray = objectArray;
    }

    void setSet(boolean set) {
        isSet = set;
    }

    void setList(boolean list) {
        isList = list;
    }

    void setStringArray(boolean stringArray) {
        isStringArray = stringArray;
    }

    void setCalendar(boolean calendar) {
        isCalendar = calendar;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setTypeQualifiedName(String typeQualifiedName) {
        this.typeQualifiedName = typeQualifiedName;
    }

    public boolean isModifierStatic() {
        return isModifierStatic;
    }

    public boolean isModifierPublic() {
        return isModifierPublic;
    }

    void setModifierPublic(boolean modifierPublic) {
        isModifierPublic = modifierPublic;
    }

    public boolean isModifierProtected() {
        return isModifierProtected;
    }

    void setModifierProtected(boolean modifierProtected) {
        isModifierProtected = modifierProtected;
    }

    public boolean isModifierPackageLocal() {
        return isModifierPackageLocal;
    }

    void setModifierPackageLocal(boolean modifierPackageLocal) {
        isModifierPackageLocal = modifierPackageLocal;
    }

    public boolean isModifierPrivate() {
        return isModifierPrivate;
    }

    void setModifierPrivate(boolean modifierPrivate) {
        isModifierPrivate = modifierPrivate;
    }

    public boolean isModifierFinal() {
        return isModifierFinal;
    }

    void setModifierFinal(boolean modifierFinal) {
        isModifierFinal = modifierFinal;
    }

    void setModifierStatic(boolean modifierStatic) {
        isModifierStatic = modifierStatic;
    }

    @Override
    public String toString() {
        return "AbstractElement{" +
                "name='" + name + "'" +
                ", isArray=" + isArray +
                ", isPrimitiveArray=" + isPrimitiveArray +
                ", isObjectArray=" + isObjectArray +
                ", isStringArray=" + isStringArray +
                ", isCollection=" + isCollection +
                ", isMap=" + isMap +
                ", isSet=" + isSet +
                ", isList=" + isList +
                ", isPrimitive=" + isPrimitive +
                ", isString=" + isString +
                ", isNumeric=" + isNumeric +
                ", isObject=" + isObject +
                ", isDate=" + isDate +
                ", isCalendar=" + isCalendar +
                ", isBoolean=" + isBoolean +
                ", isLong=" + isLong +
                ", isFloat=" + isFloat +
                ", isDouble=" + isDouble +
                ", isVoid=" + isVoid +
                ", isChar=" + isChar +
                ", isByte=" + isByte +
                ", isShort=" + isShort +
                ", typeName='" + typeName + "'" +
                ", type='" + type + "'" +
                ", typeQualifiedName='" + typeQualifiedName + "'" +
                ", isModifierStatic=" + isModifierStatic +
                ", isModifierPublic=" + isModifierPublic +
                ", isModifierProtected=" + isModifierProtected +
                ", isModifierPackageLocal=" + isModifierPackageLocal +
                ", isModifierPrivate=" + isModifierPrivate +
                ", isModifierFinal=" + isModifierFinal +
                "}";
    }


}
