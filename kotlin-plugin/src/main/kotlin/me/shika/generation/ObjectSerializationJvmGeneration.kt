package me.shika.generation

import org.jetbrains.kotlin.codegen.FunctionCodegen
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.util.ArrayUtil.EMPTY_STRING_ARRAY
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin.Companion.NO_ORIGIN
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_SYNTHETIC
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ObjectSerializationJvmGeneration : ExpressionCodegenExtension {
    override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
        if (codegen.descriptor.needSerializableFix()) {
            val selfType = codegen.typeMapper.mapType(codegen.descriptor)

            val visitor = codegen.v.newMethod(
                NO_ORIGIN,
                ACC_PUBLIC or ACC_SYNTHETIC,
                SERIALIZABLE_READ,
                "()Ljava/lang/Object;",
                null,
                EMPTY_STRING_ARRAY
            )

            visitor.visitCode()
            val iv = InstructionAdapter(visitor)
            iv.getstatic(codegen.className, "INSTANCE", selfType.descriptor)
            iv.areturn(selfType)
            FunctionCodegen.endVisit(iv, "JVM serialization bindings")
        }

    }
}
