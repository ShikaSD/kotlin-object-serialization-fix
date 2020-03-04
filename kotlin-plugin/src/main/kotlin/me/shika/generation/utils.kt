package me.shika.generation

import org.jetbrains.kotlin.descriptors.ClassDescriptor
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

fun ClassDescriptor.needSerializableFix() =
    module.platform.has<JvmPlatform>()
        && DescriptorUtils.isObject(this)
        && isSerializable()
        && !hasReadMethod()

fun ClassDescriptor.hasReadMethod() =
    unsubstitutedMemberScope.getFunctionNames().contains(SERIALIZABLE_READ)

fun ClassDescriptor.isSerializable(): Boolean =
    getSuperInterfaces().any { it.fqNameSafe == SERIALIZABLE_FQ_NAME || it.isSerializable() }
        || getSuperClassNotAny()?.isSerializable() == true

val SERIALIZABLE_READ = Name.identifier("readResolve")
val SERIALIZABLE_FQ_NAME = FqName(Serializable::class.java.name)
