package edu.ucar.unidata.site.jekyll

import com.github.jrubygradle.JRubyPlugin
import edu.ucar.unidata.site.jekyll.extensions.UnidataJekyllExtension
import edu.ucar.unidata.site.jekyll.tasks.BuildTask
import edu.ucar.unidata.site.jekyll.tasks.ServeTask
import java.util.stream.Collectors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy

class UnidataJekyllPlugin implements Plugin<Project> {

  private def basePluginId = 'base'
  private def jrubyPluginId = 'com.github.jruby-gradle.base'
  private def gemJarConfigName = 'gemjar'

  private String readPluginVersionFromProps() {
    Properties props = new Properties()
    InputStream stream = getClass().getResourceAsStream("/unidata-jekyll-plugin-info/gradle.properties")
    props.load(stream)
    stream.close()
    return props.getProperty('unidataPluginVersion')
  }

  private static void addJcenterRepo(Project project) {
    project.repositories.jcenter()
  }

  private static void addUnidataRepo(Project project) {
    project.repositories.maven({
      it.name = 'Unidata artifacts (snapshot and release - added by edu.ucar.unidata.site.jekyll plugin)'
      it.url = 'https://artifacts.unidata.ucar.edu/repository/unidata-all/'
    })
  }

  private static void ensureBaseRepos(Project project) {
    // make sure we have the necessary repositories enabled
    if (project.repositories.size() == 0) {
      addJcenterRepo(project)
      addUnidataRepo(project)
    } else {
      if (!project.repositories.stream().any {
        it.getProperties().get('url').toString().contains('jcenter.bintray.com')
      }) {
        addJcenterRepo(project)
      }
      // only add the unidata repo if no other unidata repo has been added
      if (!project.repositories.stream().any {
        it.getProperties().get('url').toString().contains('artifacts.unidata.ucar.edu/repository/')
      }) {
        addUnidataRepo(project)
      }
    }
  }

  private void applyPlugins(Project project) {
    // only apply a plugin if it has not been applied yet
    if (!project.getPluginManager().hasPlugin(basePluginId)) {
      project.getPluginManager().apply(BasePlugin.class)
    }

    if (!project.getPluginManager().hasPlugin(jrubyPluginId)) {
      project.getPluginManager().apply(JRubyPlugin.class)
    }
  }

  private void ensureUnidataGemJarDependency(Project project) {
    def requiredGroup = 'edu.ucar.unidata.site'
    def requiredId1 = 'jekyll-gems'
    def requiredId2 = 'jekyll-gems-minimum'
    def requiredModule1 = "${requiredGroup}:${requiredId1}"
    def requiredModule2 = "${requiredGroup}:${requiredId2}"
    // get a list of all dependency modules names attached to the gemJarConfigName configuration
    def gemJarConfig = project.configurations.getByName(gemJarConfigName)
    List<String> currentGemJarDeps = gemJarConfig.dependencies.stream().
        map({dep -> dep.properties.get('module')
        }).
        collect(Collectors.toList())

    // if both of the the required module are not there, then we must add one as a dependency
    if (!(currentGemJarDeps.dependencies.contains(requiredModule1) ||
        currentGemJarDeps.dependencies.contains(requiredModule2))) {
      def version = readPluginVersionFromProps()
      //"group:name:version:classifier@extension"
      gemJarConfig.dependencies.add(project.dependencies.create("${requiredModule}:${version}"))
    }
  }

  private static void applyExtensions(Project project) {
    project.extensions.create('unidataJekyll', UnidataJekyllExtension)
  }

  private void addConfiguration(Project project) {
    project.configurations.create(gemJarConfigName)
  }

  private void createTasks(Project project) {

    project.tasks.create('buildJekyllSite', BuildTask) {
      group = 'documentation'
      script = 'jekyll'
      sourceDirectory = project.unidataJekyll.sourceDirectory.
          convention(project.layout.projectDirectory.dir('src/site'))
      destinationDirectory = project.unidataJekyll.destinationDirectory.
          convention(project.layout.buildDirectory.dir('site'))
    }

    project.tasks.create('serveJekyllSite', ServeTask) {
      group = 'documentation'
      script = 'jekyll'
      sourceDirectory = project.unidataJekyll.sourceDirectory.
          convention(project.layout.projectDirectory.dir('src/site'))
      destinationDirectory = project.unidataJekyll.destinationDirectory.
          convention(project.layout.buildDirectory.dir('site'))
    }

    project.tasks.register('unpackGemJar', Copy) {
      from project.zipTree(project.configurations.getByName(gemJarConfigName).singleFile)
      into project.tasks.jrubyPrepare.outputDir
    }

    project.tasks.getByName('buildJekyllSite').dependsOn('unpackGemJar')
    project.tasks.getByName('serveJekyllSite').dependsOn('unpackGemJar')
  }

  void apply(Project project) {
    ensureBaseRepos(project)
    applyPlugins(project)
    addConfiguration(project)

    // If we are running the tests, we need to load the gem jar from disk.
    // due to the way we are testing by programmatically creating test projects.
    // So, only have the plugin ensure that we have the gem jar dependency added
    // on apply when we are NOT testing.
    if (!Boolean.getBoolean("UnidataJekyllPluginTesting")) {
      ensureUnidataGemJarDependency(project)
    }

    applyExtensions(project)
    createTasks(project)
  }
}