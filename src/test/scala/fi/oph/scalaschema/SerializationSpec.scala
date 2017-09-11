package fi.oph.scalaschema

import java.time.LocalDate

import org.json4s.JsonAST.{JObject, JString}
import org.scalatest.{FreeSpec, Matchers}

import scala.reflect.ClassTag

class SerializationSpec extends FreeSpec with Matchers {
  "strings" in {
    testSerialization(Strings("a"), """{"s":"a"}""")
  }
  "numbers" in {
    testSerialization(Numbers(1, 1l, 0.4f, 1.1), """{"a":1,"b":1,"c":0.4000000059604645,"d":1.1}""")
  }

  "traits" in {
    testSerialization(ThingContainingTrait(Impl1("hello")), """{"x":{"x":"hello"}}""")
  }

  "dates" in {
    testSerialization(Dates(LocalDate.parse("2015-12-30")), """{"d":"2015-12-30"}""")
  }

  "booleans" in {
    testSerialization(Booleans(true), """{"field":true}""")
  }

  "lists" in {
    testSerialization(Lists(List(1)), """{"things":[1]}""")
  }

  "options" in {
    testSerialization(OptionalFields(None), """{}""")
    testSerialization(OptionalFields(Some(true)), """{"field":true}""")
  }

  "synthetic properties" in {
    testSerialization(WithSyntheticProperties(), """{"field":true}""")
  }

  "overridden synthetic properties" in {
    testSerialization(WithOverriddenSyntheticProperties(false), """{"field":false}""")
  }

  "empty optional" in {
    val json = Serializer.serialize(WithOptionalDiscriminator("name", None))(serializationContext(classOf[TestTrait]))
    json should equal(JObject("name" -> JString("name")))
  }

  def testSerialization[T](x: T, expected: String)(implicit tag: ClassTag[T]) = {
    val jValue = Serializer.serialize(x)(serializationContext(tag.runtimeClass))
    org.json4s.jackson.JsonMethods.compact(jValue) should equal(expected)
  }

  private def serializationContext[T](clazz: Class[_]) =
    SerializationContext(SchemaFactory.default.createSchema(clazz))
}

case class ThingContainingTrait(x: TraitsWithFields)
trait TraitsWithFields
case class Impl1(x: String) extends TraitsWithFields
case class Impl2(x: Int) extends TraitsWithFields