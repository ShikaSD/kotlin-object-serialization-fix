package me.shika

import org.gradle.api.Plugin
import org.gradle.api.Project

class ObjectSerializationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            "objectSerialization",
            ObjectSerializationExtension::class.java
        )
    }
}

open class ObjectSerializationExtension {
    var enabled: Boolean = true
}
