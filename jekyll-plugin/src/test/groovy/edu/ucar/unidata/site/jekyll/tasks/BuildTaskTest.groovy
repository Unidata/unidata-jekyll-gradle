package edu.ucar.unidata.site.jekyll.tasks

import com.github.jrubygradle.JRubyExec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BuildTaskTest extends Specification {

  def canAddTaskToProject() {
    when: "a BuildTask is added to a project"
    Project project = ProjectBuilder.builder().build()
    def task = project.task('myBuildTask', type: BuildTask)
    then: "make sure it gets added to the project and is an instance of JRubyExec"
    task instanceof BuildTask
    task instanceof JRubyExec
    and: "has one task action"
    task.properties.actions.size == 1
    and: "the task action method is called exec because we are overriding the JRubyExec exec method"
    task.properties.actions.get(0).method.name == 'exec'
  }
}