package model

import scala.collection.mutable

/**
 * Created by karAdmin on 5/25/2015.
 */
case class MatcherVSM() {

  var index = collection.mutable.Map[String, List[Int]]()
  //var weights = collection.mutable.Map[String, Int]()

  var docs = List[String]()
  def buildIndex(docs : List[String]): Unit =
  {
    this.docs = docs
    docs.zipWithIndex.foreach{case (d, id) =>
      d.split(" ").foreach(t =>
        index.put(t, index.getOrElse(t, List[Int]()) :+ id)
      )}

  }

  def calcIDF(t : String): Double =
  {
     scala.math.log10(docs.size.asInstanceOf[Double]/index(t).size)
  }

  def calcLength(docID : Int): Double =
  {
    scala.math.sqrt(docs(docID).split(" ").foldLeft(0.0){case (a,b) => a + scala.math.pow(calcIDF(b), 2.0)})
  }
  def matchQuery(q : String): (String, Double) =
  {
    var scores = mutable.MutableList.fill(docs.size)(0.0)
    q.split(" ").foreach(x =>{
      index.getOrElse(x, List[Int]()).foreach(y =>
          scores(y) = scores(y) + calcIDF(x))
    })

    val sortedScores = scores.zipWithIndex.map{case (value, id) => (value/calcLength(id), id)}.sortBy{case(value, id) => value}.reverse
    (docs(sortedScores(0)._2),sortedScores(0)._1)

  }
}
