package edu.ucar.unidata.site.jekyll.tasks

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

class BuildTaskTest {

  @Test
  void canAddTaskToProject() {
    Project project = ProjectBuilder.builder().build()
    def task = project.task('buildTask', type: BuildTask)
    assertTrue(task instanceof BuildTask)
  }
}