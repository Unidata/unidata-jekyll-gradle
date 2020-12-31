package edu.ucar.unidata.site.jekyll.tasks

import org.gradle.api.tasks.TaskAction

class BuildTask extends AbstractUnidataJekyllTask {

  @TaskAction
  @Override
  void exec() {
    List<String> scriptArgs = Arrays.asList('build', '--source=' + getSourceDirectory().get().asFile.absolutePath,
        '--destination=' + getDestinationDirectory().get().asFile.absolutePath);
    setScriptArgs(scriptArgs)
    super.exec()
  }
}
