package edu.ucar.unidata.site.jekyll.extensions

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class BuildExtension {

  final Property<File> sourceDirectory
  final Property<File> destinationDirectory

  @Inject
  BuildExtension(ObjectFactory objects) {
    sourceDirectory = objects.property(File)
    destinationDirectory = objects.property(File)
  }
}
