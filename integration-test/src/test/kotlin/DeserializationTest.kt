import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class ObjectSerializationIntegrationTest {
    private object TestObject : Serializable

    @Test
    fun `object instance is the same after deserialization`() {
        assertEquals(TestObject, serializeDeserialize(TestObject))
    }

    private fun serializeDeserialize(instance: Serializable): Serializable {
        val outputStream = ByteArrayOutputStream()
        ObjectOutputStream(outputStream).use {
            it.writeObject(instance)
        }
        return ObjectInputStream(ByteArrayInputStream(outputStream.toByteArray())).use {
            it.readObject() as TestObject
        }
    }
}
