package edu.ucar.unidata.site.jekyll

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class UnidataJekyllPluginTest extends Specification {

  def doClean = false

  //@Rule
  //TemporaryFolder testProjectDir = new TemporaryFolder()
  File testProjectDir
  File testProjectGradleSettingsFile
  File testProjectBuildScript
  File jekyllSiteIndexFile

  def setup() {
    testProjectDir = new File("src/test/testSiteProject/")
    jekyllSiteIndexFile  = new File('src/test/testSiteProject/build/site/index.html');

    testProjectBuildScript = new File("src/test/testSiteProject/build.gradle")
    if (testProjectBuildScript.exists()) {
      testProjectBuildScript.delete()
    }
    testProjectBuildScript.createNewFile()
    testProjectBuildScript.write """
      plugins {
        id 'edu.ucar.unidata.site.jekyll'
      }
      """

    testProjectGradleSettingsFile = new File("src/test/testSiteProject/settings.gradle")
    if (testProjectGradleSettingsFile.exists()) {
      testProjectGradleSettingsFile.delete()
    }
    testProjectGradleSettingsFile.createNewFile()
    testProjectGradleSettingsFile.write "rootProject.name = 'test-jekyll-site'"
  }

  def cleanup() {
    if (doClean) {
      GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments('clean')
          .withPluginClasspath()
          .withDebug(true)
          .build()
    }
  }

  def "can build default jekyll site."() {
    given:
    File testSiteDir = new File("src/test/testSiteProject")
    String testSiteDirString = testSiteDir.absolutePath
    String testProjectDirString = testProjectDir.absolutePath

    // fix windows paths
    if (File.separator == '\\') {
      testSiteDirString = testSiteDirString.replaceAll('\\\\', '/')
      testProjectDirString = testProjectDirString.replaceAll('\\\\', '/')
    }

    String testSiteDestinationDir = testProjectDirString + '/build/site'
    File destinationDir = new File(testSiteDestinationDir)
    if (destinationDir.exists()) {
      FileUtils.deleteDirectory(destinationDir)
    }

    testProjectBuildScript.append """
      buildJekyllSite {
        sourceDirectory = file('${testSiteDirString}/src/site')
        destinationDirectory = file('${testSiteDestinationDir}')
      }
      """

    when:
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments('buildJekyllSite')
        .withPluginClasspath()
        .withDebug(true)
        .build()
    then:
    assert jekyllSiteIndexFile.exists();
    result.task(":buildJekyllSite").outcome == SUCCESS
  }
}
