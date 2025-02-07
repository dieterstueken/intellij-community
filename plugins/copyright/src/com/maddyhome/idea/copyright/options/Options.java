// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.maddyhome.idea.copyright.options;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.maddyhome.idea.copyright.CopyrightUpdaters;
import com.maddyhome.idea.copyright.psi.UpdateCopyrightsProvider;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Options implements Cloneable {
  public LanguageOptions getOptions(String fileTypeName) {
    LanguageOptions res = options.get(fileTypeName);
    if (res != null) return res;

    final FileType fileType = FileTypeManager.getInstance().findFileTypeByName(fileTypeName);
    if (fileType == null) return new LanguageOptions();
    
    FileType acceptableFileType = CopyrightUpdaters.INSTANCE.getRegisteredFileTypeFromLanguageHierarchy(fileType);
    if (acceptableFileType != null) {
      res = this.options.get(acceptableFileType.getName());
      if (res != null) return res;
    }

    final UpdateCopyrightsProvider provider = CopyrightUpdaters.INSTANCE.forFileType(fileType);
    if (provider != null) return provider.getDefaultOptions();
    
    return new LanguageOptions();
  }

  public LanguageOptions getTemplateOptions() {
    return getOptions(LANG_TEMPLATE);
  }

  public void setOptions(String name, LanguageOptions options) {
    this.options.put(name, options);
  }

  public void setTemplateOptions(LanguageOptions options) {
    setOptions(LANG_TEMPLATE, options);
  }

  public @Nullable LanguageOptions getMergedOptions(String name) {
    try {
      LanguageOptions lang = getOptions(name).clone();
      LanguageOptions temp = getTemplateOptions().clone();
      switch (lang.getFileTypeOverride()) {
        case LanguageOptions.USE_TEMPLATE -> {
          temp.setFileLocation(lang.getFileLocation());
          temp.setFileTypeOverride(lang.getFileTypeOverride());
          lang = temp;
        }
        case LanguageOptions.USE_TEXT -> { }
      }

      return lang;
    }
    catch (CloneNotSupportedException e) {
      // This shouldn't happen
    }

    return null;
  }


  public void readExternal(Element element) throws InvalidDataException {
    logger.debug("readExternal()");
    List<Element> languageOptions = element.getChildren("LanguageOptions");
    if (!languageOptions.isEmpty()) {
      //noinspection ForLoopReplaceableByForEach
      for (int i = 0; i < languageOptions.size(); i++) {
        Element languageOption = languageOptions.get(i);
        // NOTE: If any change is made here you need to update ConfigTabFactory and UpdateCopyrightFactory too.
        LanguageOptions opts = new LanguageOptions();
        opts.readExternal(languageOption);
        setOptions(languageOption.getAttributeValue("name"), opts);
      }
    }
    else {
      Element root = null;
      Element jOpts = element.getChild("JavaOptions");
      if (jOpts != null) // version 2.1.x
      {
        root = jOpts;
      }
      else // versions 0.0.1 - 2.0.x
      {
        Element child = element.getChild("option");
        if (child != null && child.getAttribute("name") != null) {
          root = element;
        }
      }
      if (root != null) {
        String languageName = StdFileTypes.JAVA.getName();
        // NOTE: If any change is made here you need to update ConfigTabFactory and UpdateCopyrightFactory too.
        LanguageOptions opts = new LanguageOptions();
        opts.setFileTypeOverride(LanguageOptions.USE_TEMPLATE);
        for (Element option : root.getChildren("option")) {
          String name = option.getAttributeValue("name");
          String val = option.getAttributeValue("value");
          if ("location".equals(name)) {
            if(val != null){
              opts.setFileLocation(Integer.parseInt(val));
            }

          }
        }

        setOptions(languageName, opts);
      }
    }

    logger.debug("options=" + this);
  }

  public void writeExternal(Element element) throws WriteExternalException {
    logger.debug("writeExternal()");

    for (String lang : options.keySet()) {
      Element elem = new Element("LanguageOptions");
      elem.setAttribute("name", lang);
      element.addContent(elem);
      options.get(lang).writeExternal(elem);
    }

    logger.debug("options=" + this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Options options1 = (Options)o;

    return options.equals(options1.options);
  }

  @Override
  public int hashCode() {
    int result;
    result = options.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Options" + "{options=" + options + '}';
  }

  @Override
  public Options clone() throws CloneNotSupportedException {
    Options res = (Options)super.clone();
    res.options = new TreeMap<>();
    for (String lang : options.keySet()) {
      LanguageOptions opts = options.get(lang);
      res.options.put(lang, opts.clone());
    }

    return res;
  }

  private Map<String, LanguageOptions> options = new TreeMap<>();
  private static final String LANG_TEMPLATE = "__TEMPLATE__";

  private static final Logger logger = Logger.getInstance(Options.class.getName());
}