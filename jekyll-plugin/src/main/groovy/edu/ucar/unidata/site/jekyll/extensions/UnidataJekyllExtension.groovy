package edu.ucar.unidata.site.jekyll.extensions

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class UnidataJekyllExtension {

  final DirectoryProperty sourceDirectory
  final DirectoryProperty destinationDirectory

  @Inject
  UnidataJekyllExtension(ObjectFactory objects) {
    sourceDirectory = objects.directoryProperty()
    destinationDirectory = objects.directoryProperty()
  }
}
