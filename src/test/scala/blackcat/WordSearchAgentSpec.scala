package blackcat

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.matching.Regex
import scala.collection.immutable.StringOps

@RunWith(classOf[JUnitRunner])
class WordSearchAgentSpec extends FunSpec {

  describe( "A random word generator") {
    
    it("should return words with the passed length")  {
      val expectedLength = 10
      assert(RandomWord.generate(expectedLength).length() == expectedLength)
    }
    
    it("shoud return an empty string if the passed length is zero") {
      assert(RandomWord.generate(0) == "")
    }
    
    it("should throw a NullPointerException if the passed alphabet is null") {
      intercept[NullPointerException] {
        RandomWord.generate(length = 10, alphabet = null)
      }
    }
    
    it("should throw an IllegalArgumentException if a negativ length is passed") {
      intercept[IllegalArgumentException] {
        RandomWord.generate(-10)
      }
    }
    
    it ("should only contain symbols of a given alphabet") {
      val alphabet = "ENISRATDHULCGMOBWFKZPVßJYXQ";
      
      assert(RandomWord.generate(length = 100, alphabet = alphabet) matches "(E|N|I|S|R|A|T|D|H|U|L|C|G|M|O|B|W|F|K|Z|P|V|ß|J|Y|X|Q)*")
    }
  }
}