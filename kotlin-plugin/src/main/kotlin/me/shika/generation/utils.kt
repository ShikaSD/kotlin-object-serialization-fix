package me.shika.generation

import org.jetbrains.kotlin.backend.wasm.ir2wasm.allSuperInterfaces
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getAllSuperclasses
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.has
import org.jetbrains.kotlin.platform.jvm.JvmPlatform
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import java.io.Serializable

fun IrClass.needSerializableFix(): Boolean {
    return isObject && isSerializable() && !hasReadMethod()
}

fun IrClass.isSerializable(): Boolean {
    return allSuperInterfaces()
        .any { it.fqNameWhenAvailable == SERIALIZABLE_FQ_NAME || it !== this && it.isSerializable() }
        || getAllSuperclasses().any { it.isSerializable() }
}

fun IrClass.hasReadMethod(): Boolean {
    return functions.any { it.name == SERIALIZABLE_READ && it.valueParameters.isEmpty() }
}

fun IrModuleFragment.hasJvmPlatform(): Boolean {
    return descriptor
        .platform
        ?.has<JvmPlatform>()
        ?: false
}

val SERIALIZABLE_READ = Name.identifier("readResolve")
val SERIALIZABLE_FQ_NAME = FqName(Serializable::class.java.name)
