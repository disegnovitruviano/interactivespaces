/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.workbench.confederate;

import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.workbench.JdomReader;
import interactivespaces.workbench.project.JdomProjectReader;
import interactivespaces.workbench.project.Project;
import org.apache.commons.logging.Log;
import org.jdom.Element;

import java.io.File;
import java.util.List;

/**
 * @author Trevor Pering
 */
public class JdomConfederacyReader extends JdomReader {

  public static final String CONFEDERACY_ELEMENT_NAME = "confederacy";

  private final PrototypeManager prototypeManager = new PrototypeManager();

  /**
   * Construct a project reader.
   *
   * @param log
   *          the logger to use
   */
  public JdomConfederacyReader(Log log) {
    super(log);
  }

  public void processSpecification(Confederacy confederacy, Element rootElement) {
    if (!CONFEDERACY_ELEMENT_NAME.equals(rootElement.getName())) {
      throw new SimpleInteractiveSpacesException("Illegal root element name " + rootElement.getName());
    }

    List<Element> children = getChildren(rootElement);
    for (Element child : children) {
      addElementToSpec(confederacy, child);
    }
  }

  private void addElementToSpec(Confederacy spec, Element child) {
    String name = child.getName();

    try {
      if (JdomProjectReader.GROUP_ELEMENT_NAME.equals(name)) {
        addProjects(spec, child);
      } else if (PrototypeManager.GROUP_ELEMENT_NAME.equals(name)) {
        addPrototypes(spec, child);
      } else {
        throw new SimpleInteractiveSpacesException("Unrecognized element");
      }
    } catch (Exception e) {
      throw new SimpleInteractiveSpacesException("While processing confederacy element: " + name, e);
    }
  }

  private void addPrototypes(Confederacy spec, Element group) {
    List<Element> children = getChildren(group);
    for (Element entry : children) {
      prototypeManager.addPrototypeElement(entry);
    }
  }

  private void addProjects(Confederacy spec, Element group) {
    List<Element> children = getChildren(group);
    for (Element entry : children) {
      Project project = new JdomProjectReader(log, prototypeManager).processSpecification(entry);
      project.setSpecificationSource(spec.getSpecificationSource());
      project.setBaseDirectory(new File("."));
      spec.addProject(project);
    }
  }

}
