package me.shika

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class ObjectSerializationPlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create(
            "objectSerialization",
            ObjectSerializationExtension::class.java
        )
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(ObjectSerializationExtension::class.java) ?: ObjectSerializationExtension()

        return project.objects.listProperty(SubpluginOption::class.java).apply {
            add(
                SubpluginOption(
                    key = "enabled",
                    value = extension.enabled.toString()
                )
            )
        }
    }

    override fun getCompilerPluginId(): String = "object-serialization-fix"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "me.shika",
            artifactId = "kotlin-object-java-serialization",
            version = "1.5.0"
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType in setOf(
            KotlinPlatformType.jvm,
            KotlinPlatformType.androidJvm,
            KotlinPlatformType.common
        )
}

open class ObjectSerializationExtension {
    var enabled: Boolean = true
}
