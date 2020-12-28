package edu.ucar.unidata.site.jekyll

import edu.ucar.unidata.site.jekyll.extensions.BuildExtension
import edu.ucar.unidata.site.jekyll.tasks.BuildTask
import com.github.jrubygradle.JRubyPlugin
import com.github.jrubygradle.JRubyExec
import com.github.jrubygradle.api.core.RepositoryHandlerExtension
import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin

import java.util.stream.Collectors

class UnidataJekyllPlugin implements Plugin<Project> {

  private def basePluginId = 'base'
  private def jrubyPluginId = 'com.github.jruby-gradle.base'

  private void ensureBaseRepos(Project project) {
    // make sure we have the necessary repositories enabled
    if (!project.repositories.contains(project.repositories.jcenter())) {
      project.repositories.jcenter({
        it.name = 'jcenter repository (added by edu.ucar.unidata.site.jekyll plugin)'
      })
    }
    // only add the unidata repo if no other unidata repo has been added
    if (project.repositories.stream().any{
      it.getProperties().get('url').toString().contains('artifacts.unidata.ucar.edu/repository/')
    }) {
      println 'has a unidata repo'
    } else {
      println 'does not have unidata repo'
      project.repositories.maven({
        it.name = 'Unidata artifacts (snapshot and release - added by edu.ucar.unidata.site.jekyll plugin)'
        it.url = 'https://artifacts.unidata.ucar.edu/repository/unidata-all/'
      })
    }
  }

  private void applyPlugins(Project project) {
    // only apply a plugin if it has not been applied yet
    if (!project.getPluginManager().hasPlugin(basePluginId)) {
      project.getPluginManager().apply(BasePlugin.class);
    }

    if (!project.getPluginManager().hasPlugin(jrubyPluginId)) {
      project.getPluginManager().apply(JRubyPlugin.class)
    }
  }

  private void setupJrubyRepo(Project project) {
    if (!project.getPluginManager().hasPlugin(jrubyPluginId)) {
      throw new RuntimeException("Cannot setup JRuby repository unless the JRubyPlugin has been applied.")
    }

    RepositoryHandlerExtension rhe = new RepositoryHandlerExtension(project)
    // only add the repo if it has not been added yet
    if (!project.repositories.contains(rhe.gems())) {
      project.repositories.addLast(rhe.gems())
    }
    project.repositories.contains(rhe.gems())
  }

  private void addGemDependencies(Project project) {

    List<String> deps = Arrays.asList(
        'rubygems:jekyll:3.8.7',
        'rubygems:minima:2.5.1',
        'rubygems:jekyll-feed:0.15.1',
        'rubygems:jekyll-seo-tag:2.7.1',
        'rubygems:unidata-jekyll-theme:0.0.1-SNAPSHOT',
        'rubygems:unidata-jekyll-plugins:0.0.1-SNAPSHOT'
    )


    def gemConfig = project.configurations.getByName("gems")
    List<String> currentGemDeps = gemConfig.dependencies.stream().
        map({dep -> dep.properties.get('module')}).
        collect(Collectors.toList())
    // Add any missing required dependencies to the gem configuration
    deps.stream().forEach({
      String moduleName = it.substring(0, it.findLastIndexOf({':'}))
      if (!currentGemDeps.contains(moduleName)) {
        gemConfig.dependencies.add(project.dependencies.create(it))
      }
    })
  }

  private void applyExtensions(Project project) {
    project.extensions.create('buildJekyllSite', BuildExtension)
  }

  private void createTasks(Project project) {
    project.tasks.register('baseJrubyJekyllTask', JRubyExec) {
      script = 'jekyll'
    }

    project.tasks.register('buildJekyllSite', BuildTask) {
      sourceDirectory = project.buildJekyllSite.sourceDirectory
      destinationDirectory = project.buildJekyllSite.destinationDirectory
    }
    project.tasks.getByName('buildJekyllSite').dependsOn('jrubyPrepare')
    project.tasks.getByName('buildJekyllSite').dependsOn('jrubyPrepare')
  }

  void apply(Project project) {
    ensureBaseRepos(project)
    applyPlugins(project)
    setupJrubyRepo(project)
    addGemDependencies(project)

    applyExtensions(project)
    createTasks(project)
  }
}