import java.io.Serializable

interface TestInterface: Serializable

object DirectlyImplementsTestInterface: TestInterface

abstract class ExtendsTestInterface: TestInterface

object IndirectlyImplementsTestInterface: ExtendsTestInterface()

object NotSerializable