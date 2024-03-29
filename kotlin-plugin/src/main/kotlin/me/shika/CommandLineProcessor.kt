package me.shika

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

//@AutoService(CommandLineProcessor::class)
@OptIn(ExperimentalCompilerApi::class)
class ObjectSerializationCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "object-serialization-fix"
    override val pluginOptions: Collection<AbstractCliOption> =
        listOf(
            CliOption(
                "enabled",
                "<true|false>",
                "Whether plugin is enabled",
                required = false
            )
        )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
        }
    }

    companion object {
        val KEY_ENABLED = CompilerConfigurationKey<Boolean>("di.plugin.enabled")
    }
}
