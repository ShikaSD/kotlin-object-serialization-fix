package me.shika

import org.gradle.api.internal.provider.DefaultListProperty
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class ObjectSerializationSubplugin: KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(ObjectSerializationExtension::class.java) ?: ObjectSerializationExtension()

        return DefaultListProperty(SubpluginOption::class.java).apply {
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
            version = "1.1.0"
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.target.project.plugins.hasPlugin(ObjectSerializationPlugin::class.java)
}
