package blackcat

import java.io.File
import scala.io.Source
import scala.util.Random
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.routing.RoundRobinRouter
import akka.actor.ActorSystem

object BlackCat extends App {

  findWords

  def findWords(): Unit = {

    val system = ActorSystem("BlackCatSlotMachine")

    val master = system.actorOf(Props[WordSearchMaster], name = "master")

    master ! SearchMasterJob(RandomWord.generate(3), "D:\\java.source\\dojo-20131001\\word-db")
  }
}

case class SearchMasterJob(searchTerm: String, wordDbDir: String)

case class SearchAgentJob(searchTerm: String, wordFilePath: String)

case class SearchResult(found: Boolean)

object RandomWord {


  /**
   *
   * @param length
   * @param alphabet
   * @return
   */
  def generate(length: Int, alphabet: String = "ENISRATDHULCGMOBWFKZPVßJYXQ"): String = {

    if (length < 0) {
      throw new IllegalArgumentException
    }

    if (alphabet == null) {
      throw new NullPointerException()
    }

    Stream.continually(Random.nextInt(alphabet.length)).map(alphabet).take(length).mkString
  }

}

/**
 *
 */
class WordSearchAgent extends Actor {

  override def preStart = {
    println("Starting actor " + self)
  }

  def receive = {

    case job: SearchAgentJob =>

      sender ! SearchResult(Source.fromFile(job.wordFilePath).getLines().exists(w => w.equalsIgnoreCase(job.searchTerm)))

    case _ =>

      unhandled()

  }
}


class WordSearchMaster extends Actor {

  var jobCount = 0

  def receive = {

    case job: SearchMasterJob =>

      println("got job " + job.searchTerm + " from " + sender)

      val agentRouter = context.actorOf(Props[WordSearchAgent].withRouter(RoundRobinRouter(nrOfInstances = 5)))

      val files = new File(job.wordDbDir).listFiles

      jobCount = files.length

      files.foreach { f =>
        agentRouter ! SearchAgentJob(job.searchTerm, f.getAbsolutePath)
      }

    case result: SearchResult =>

      println("got result " + result.found)

      jobCount -= 1

      if (jobCount == 0) {

        context.stop(self)

      }

    case _ =>

      unhandled()
  }
}

