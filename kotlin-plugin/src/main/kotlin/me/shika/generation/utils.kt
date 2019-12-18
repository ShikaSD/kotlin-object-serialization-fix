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

fun ClassDescriptor.needSerializableFix() =
    module.platform.has<JvmPlatform>()
        && DescriptorUtils.isObject(this)
        && isSerializable()
        && !hasReadMethod()

fun ClassDescriptor.hasReadMethod() =
    unsubstitutedMemberScope.getFunctionNames().contains(Name.identifier(SERIALIZABLE_READ))

fun ClassDescriptor.isSerializable(): Boolean =
    getSuperInterfaces().any { it.fqNameSafe == SERIALIZABLE_FQ_NAME }
        || getSuperClassNotAny()?.isSerializable() == true

const val SERIALIZABLE_READ = "readResolve"
val SERIALIZABLE_FQ_NAME = FqName("java.io.Serializable")

