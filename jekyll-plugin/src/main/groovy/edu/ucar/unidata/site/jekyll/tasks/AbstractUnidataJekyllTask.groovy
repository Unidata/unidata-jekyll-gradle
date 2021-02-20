package edu.ucar.unidata.site.jekyll.tasks

import com.github.jrubygradle.JRubyExec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.github.jrubygradle.internal.JRubyExecUtils.resolveScript

class AbstractUnidataJekyllTask extends JRubyExec {
  @InputDirectory
  final DirectoryProperty sourceDirectory = project.objects.directoryProperty()

  @OutputDirectory
  final DirectoryProperty  destinationDirectory = project.objects.directoryProperty()

  @TaskAction
  @Override
  void exec() {
    super.exec()
  }

  /**
   * get Jekyll source directory
   *
   * @param scr DirectoryProperty that points to the site source directory.
   */
  DirectoryProperty getSourceDirectory() {
    sourceDirectory
  }

  /**
   * get Jekyll output directory
   *
   * @param scr DirectoryProperty that points to where Jekyll should output the rendered html.
   */
  DirectoryProperty getDestinationDirectory() {
    destinationDirectory
  }

  // overrides some base JRubyExec task methods to add missing (or correct) incremental build property
  // type annotations. Allows ./gradlew validatePlugins to pass.
  @Internal
  @Override
  Provider<File> getGemWorkDir() {
    super.getGemWorkDir()
  }

  @Internal
  @Deprecated
  @Override
  String getJrubyVersion() {
    super.getJrubyVersion()
  }

  private Object script

  @Override
  void setScript(Object scr) {
    this.script = scr
  }

  @Internal
  File getScript() {
    resolveScript(project, this.script)
  }
}
