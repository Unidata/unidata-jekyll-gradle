# Unidata Jekyll Plugin for Gradle

This repository manages a gradle plugin that allows projects at the Unidata Program Center to build Jekyll based sites using the [Unidata Jekyll theme](https://github.com/Unidata/unidata-jekyll-theme).
When applied to a gradle project, the plugin sets up one configuration and creates two tasks.
The plugin also adds any necessary repositories and dependencies.

## Adding the plugin

Adding the plugin to a gradle project is a two-step process.
First, you will need tell gradle where it can find the plugin.
This can be done by adding the following to your projects `settings.gradle` file:

~~~
pluginManagement {
  repositories {
    maven {
      // For Unidata Gradle Plugins
      url 'https://artifacts.unidata.ucar.edu/repository/unidata-all/'
    }
  }
}
~~~

Second, you will need to apply the plugin to your gradle project by adding the following to the project `build.gradle` file:

~~~
plugins {
  id 'edu.ucar.unidata.site.jekyll' version '<plugin-version>'
}
~~~

## Configuration

The plugin has two settings that can be configured: the location of the Jekyll source directory, and the location of the Jekyll output directory.
These locations are configurable using the `unidataJekyll` configuration (default values shown):

~~~
unidataJekyll {
  sourceDirectory = file('src/site')
  destinationDirectory = file('build/site')
}
~~~

## Tasks

When the plugin is applied to a project, two tasks become available - `buildJekyllSite` and `serveJekyllSite`.
The `buildJekyllSite` will read the Jekyll configuration and source markdown files from the `sourceDirectory`, and will generate a static HTML site using the Unidata Jekyll Theme in the `destinationDirectory`.
The `serveJekyllSite` is similar to `buildJekyllSite`, with two additional feature.
First, `serverJekyllSite` will spin up a local webserver that can be used to view the generated HTML.
Second, the tasks will watch the `sourceDirectory` for changes, and will regenerate the HTML when any changes to the source markdown files are made.
Together, this allows you to live edit the content of your Jekyll site.
