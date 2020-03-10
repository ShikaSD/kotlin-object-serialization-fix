package me.shika

import me.shika.ObjectSerializationCommandLineProcessor.Companion.KEY_ENABLED
import me.shika.generation.ObjectSerializationIrGeneration
import me.shika.generation.ObjectSerializationJvmGeneration
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class ObjectSerializationComponentRegistrar(): ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        ExpressionCodegenExtension.registerExtension(
            project,
            ObjectSerializationJvmGeneration()
        )

        IrGenerationExtension.registerExtension(
            project,
            ObjectSerializationIrGeneration()
        )
    }

}
