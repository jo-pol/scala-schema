package fi.oph.scalaschema

import java.time.LocalDate

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

  def testSerialization[T](x: T, expected: String)(implicit tag: ClassTag[T]) = {
    val schema = SchemaFactory.default.createSchema(tag.runtimeClass)
    val jValue = Serializer.serialize(x)(SerializationContext(schema))

    org.json4s.jackson.JsonMethods.compact(jValue) should equal(expected)
  }
}

case class ThingContainingTrait(x: TraitsWithFields)
trait TraitsWithFields
case class Impl1(x: String) extends TraitsWithFields
case class Impl2(x: Int) extends TraitsWithFields