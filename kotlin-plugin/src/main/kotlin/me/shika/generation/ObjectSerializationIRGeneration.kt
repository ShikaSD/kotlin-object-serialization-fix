package me.shika.generation

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOriginImpl
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

private object ObjectSerializationOrigin : IrDeclarationOriginImpl("object-serialization-fix", isSynthetic = true)

class ObjectSerializationIrGeneration : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (moduleFragment.hasJvmPlatform()) {
            moduleFragment.acceptVoid(
                recursiveIrClassVisitor {
                    if (it.needSerializableFix()) {
                        fixSerializable(it, pluginContext)
                    }
                }
            )
        }
    }

    private fun fixSerializable(cls: IrClass, context: IrPluginContext) {
        cls.addFunction {
            name = SERIALIZABLE_READ
            returnType = context.irBuiltIns.anyNType
            visibility = DescriptorVisibilities.PUBLIC
            origin = ObjectSerializationOrigin
        }.also { function ->
            function.body = DeclarationIrBuilder(context, function.symbol, function.startOffset, function.endOffset)
                .irBlockBody {
                    +irReturn(irGetObject(cls.symbol))
                }
        }
    }

    private fun recursiveIrClassVisitor(block: (IrClass) -> Unit) =
        object : IrElementVisitorVoid {
            override fun visitElement(element: IrElement) {
                element.acceptChildrenVoid(this)
            }

            override fun visitClass(declaration: IrClass) {
                declaration.acceptChildrenVoid(this)
                block(declaration)
            }
        }
}
