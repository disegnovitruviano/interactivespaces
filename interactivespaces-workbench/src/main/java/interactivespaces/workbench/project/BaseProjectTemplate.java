/*
 * Copyright (C) 2014 Google Inc.
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

package interactivespaces.workbench.project;

import com.google.common.io.Closeables;
import interactivespaces.SimpleInteractiveSpacesException;
import interactivespaces.workbench.FreemarkerTemplater;
import interactivespaces.workbench.project.constituent.ProjectConstituent;
import interactivespaces.workbench.project.creator.ProjectCreationContext;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Base template for any kind of project.
 *
 * @author Keith M. Hughes
 */
public class BaseProjectTemplate implements ProjectTemplate {

  /**
   * Output file for temporarily writing variables.
   */
  public static final File TEMPLATE_VARIABLES_TMP = new File("template_variables.tmp");

  /**
   * Template variable name to use for holding the base directory.
   */
  public static final String BASE_DIRECTORY_VARIABLE = "baseDirectory";

  /**
   * Templater to use for constructing the template.
   */
  private FreemarkerTemplater templater;

  /**
   * Process the given creation specification.
   *
   * @param spec
   *          specification to process.
   */
  public void process(ProjectCreationContext spec) {
    try {
      templateSetup(spec);
      onTemplateSetup(spec);
      onTemplateWrite(spec);
      processTemplateConstituents(spec);
    } catch (Exception e) {
      dumpVariables(TEMPLATE_VARIABLES_TMP, spec.getTemplateData());
      throw new SimpleInteractiveSpacesException(
          "Template variables can be found in " + TEMPLATE_VARIABLES_TMP.getAbsolutePath(), e);
    }
  }

  /**
   * Setup the template as necessary for basic operation.
   *
   * @param spec
   *          spec for the project
   *
   */
  private void templateSetup(ProjectCreationContext spec) {
    Project project = spec.getProject();

    spec.addTemplateDataEntry(BASE_DIRECTORY_VARIABLE, spec.getBaseDirectory().getAbsolutePath());
    spec.addTemplateDataEntry("internalTemplates", FreemarkerTemplater.TEMPLATE_LOCATION.getAbsoluteFile());
    spec.addTemplateDataEntry("spec", spec);
    spec.addTemplateDataEntry("project", project);
  }

  /**
   * Template is being set up. Can be overridden in a project-type specific project template.
   *
   * @param spec
   *          spec for the project
   */
  protected void onTemplateSetup(ProjectCreationContext spec) {
    // Default is to do nothing.
  }

  /**
   * Process the defined template constituents.
   *
   * @param spec
   *          spec for the project
   *
   */
  private void processTemplateConstituents(ProjectCreationContext spec) {
    Project project = spec.getProject();
    List<ProjectConstituent> projectConstituents = project.getExtraConstituents();
    for (ProjectConstituent constituent : projectConstituents) {
      constituent.processConstituent(project, null, spec);
    }
  }

  /**
   * Function called on template write. Can be overridden to provide different functionality for other project
   * types.
   *
   * @param spec
   *          specification that is being written
   */
  public void onTemplateWrite(ProjectCreationContext spec) {
    // Default is to do nothing.
  }

  /**
   * Dump the given variables to an output file.
   *
   * @param outputFile
   *          variable dump output file
   * @param variables
   *          variables to dump
   */
  private void dumpVariables(File outputFile, Map<String, Object> variables) {
    PrintWriter variableWriter = null;
    try {
      variableWriter = new PrintWriter(outputFile);
      for (Map.Entry<String, Object> entry : variables.entrySet()) {
        variableWriter.println(String.format("%s=%s", entry.getKey(), entry.getValue()));
      }
    } catch (Exception e) {
      throw new SimpleInteractiveSpacesException(
          "Error writing variable dump file " + outputFile.getAbsolutePath(), e);
    } finally {
      Closeables.closeQuietly(variableWriter);
    }
  }

  /**
   * Get the templater used by this template.
   *
   * @return templater in use
   */
  public FreemarkerTemplater getTemplater() {
    return templater;
  }

  /**
   * Set the templater to use for this project.
   *
   * @param templater
   *          templater to use
   */
  public void setTemplater(FreemarkerTemplater templater) {
    this.templater = templater;
  }
}
