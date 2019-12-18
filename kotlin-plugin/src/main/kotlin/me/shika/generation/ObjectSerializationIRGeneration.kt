package me.shika.generation

import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectSerializationIrGeneration : IrGenerationExtension {
    override fun generate(file: IrFile, backendContext: BackendContext, bindingContext: BindingContext) {
        file.acceptVoid(
            recursiveIrClassVisitor {
                if (it.descriptor.needSerializableFix()) {
                    fixSerializable(it, backendContext)
                }
            }
        )
    }

    private fun fixSerializable(cls: IrClass, context: BackendContext) {
        cls.addFunction(SERIALIZABLE_READ, context.irBuiltIns.anyNType).also { function ->
            function.body = context.createIrBuilder(function.symbol).irBlockBody {
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
