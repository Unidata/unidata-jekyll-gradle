package edu.ucar.unidata.site.jekyll.tasks

import com.github.jrubygradle.JRubyExec
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class BuildTask extends DefaultTask {

  @InputDirectory
  Property<File> sourceDirectory = project.objects.property(File)

  @OutputDirectory
  Property<File> destinationDirectory = project.objects.property(File)

  @TaskAction
  def buildSite() {
    def task = project.tasks.findByName('baseJrubyJekyllTask')
    List<String> scriptArgs = Arrays.asList('build', '--source=' + getSourceDirectory(),
        '--destination=' + getDestinationDirectory());
    task.setScriptArgs(scriptArgs)
    task.exec()
  }

  /**
   * Set Jekyll output directory
   *
   * @param scr Path to output directory. Can be any object that is convertible to File.
   */
  File getDestinationDirectory() {
    project.file(destinationDirectory)
  }

  /**
   * Set Jekyll source directory
   *
   * @param scr Path to jekyll source directory. Can be any object that is convertible to File.
   */
  File getSourceDirectory() {
    project.file(sourceDirectory)
  }
}
