package me.shika

import me.shika.ObjectSerializationCommandLineProcessor.Companion.KEY_ENABLED
import me.shika.generation.ObjectSerializationIrGeneration
import me.shika.generation.ObjectSerializationJvmGeneration
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class ObjectSerializationCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        ExpressionCodegenExtension.registerExtension(
            ObjectSerializationJvmGeneration()
        )

        IrGenerationExtension.registerExtension(
            ObjectSerializationIrGeneration()
        )
    }

}
