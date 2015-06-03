package model.SentimentAnalysis

import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.util.CoreMap
import model._
import play.api.libs.json.{Json, JsError}
import scala.util.control.Breaks._
import scala.collection.JavaConversions._
/**
  * Created by abdelrazektarek on 5/2/15.
  */
object SentimentCalculator {

   var wrapper = new nlpWrapper("tokenize, ssplit, pos, lemma, parse, sentiment")

    def compareProducts(categoryID : String, ontologyTree : DataCollectionModel.OntologyTree, username : String = "") =
    {

        APP_DBManager.modifyOntology(categoryID, ontologyTree, username)
        val productCodes = DataCollection_DBManager.retrieveProductsWithReviewSentences(categoryID)
        val nodes = ontologyTree.getBFSNodes()
        productCodes.map(
        code => {
          nodes.foreach(n => n.sentiment = 0.0)
          val results = calcSentiment(code, ontologyTree)
          (results.root.sentiment,Json.toJson(APPModel.ProductRanking(results, APP_DBManager.retrieveProduct(code).info)))
        }).sortBy(_._1).reverse.map(_._2)

    }
        //.sortBy(_._2.root.sentiment)
   def calcSentiment(codes : Map[String, String], ontologyTree : DataCollectionModel.OntologyTree) : DataCollectionModel.OntologyTree =  {

     val product = APP_DBManager.retrieveProduct(codes)

     val reviewSentencesAndGraphs = DataCollection_DBManager.retrieveReveiwsSentencescodes(codes)

     var reverseBFSNodes = ontologyTree.getBFSNodes().reverse

     for( rev <- reviewSentencesAndGraphs) {
       for (senAndgraph <- rev) {
         var tokens = wrapper.getTokens(senAndgraph._1)
         var graph = senAndgraph._2
         var features = getFeatures(senAndgraph._1, reverseBFSNodes)
         calculateSentiment(graph, features, tokens)

       }//Sentences


     }//Reviews

     ontologyTree.aggregateSentiment()
     ontologyTree

   }

   def getSentences(review : APPModel.CustomerReview): List[CoreMap] =
   {

      wrapper.getSentences(review.text)
   }
   def getGraph(Sentence: CoreMap, Tokens:List[CoreLabel]): WeightedGraph = {

     val numTokens = Tokens.length
     val g = new WeightedGraph(1)
     for (i <- 1 to numTokens) g.addNode


     var dependency = wrapper.getDependencies(Sentence)


     for (d <- dependency) {
       if (!d.reln().toString.equals("root")) {
         //        println("Relation: "+d.reln().toString)
         val Gov = g.nodes.get(d.gov().index() - 1)
         val dep = g.nodes.get(d.dep().index() - 1)
         //        println("Gov: "+d.gov().toString +" index: "+d.gov().index())
         Gov.connectWith(dep)
       }
     }

     dependency = null
     g
   }

   def getGraphs(Sentences: List[CoreMap]): List[WeightedGraph] = {
     Sentences.map(s => getGraph(s, wrapper.getTokens(s)))
   }



   def calculateSentiment(graph:WeightedGraph, features:Map[List[CoreLabel],DataCollectionModel.OntologyNode],
                          Tokens:List[CoreLabel]): Unit ={

     if(features.isEmpty)
       return

     var featuresIndexs = List[Int]()

     for( f <- features)
       for(term <- f._1) featuresIndexs :+= term.index()-1


     val dijkstra = new Dijkstra[graph.type](graph)

     graph.nodes.zipWithIndex.foreach{
       case (word,index) =>
         var nearestFeature = features.head._1
         var nearestFeatureDistance = Int.MaxValue
         if(!featuresIndexs.contains(index)){
           features.foreach{
             case(feature, node) =>
               var nearestTerm = feature(0)
               var nearestTermDistance = Int.MaxValue
               feature.foreach{
                 term =>
                   val termNode = graph.nodes.get(term.index()-1)
                   val wordNode = graph.nodes.get(index)
                   val (start, target) = (termNode, wordNode)
                   dijkstra.stopCondition = (S, D, P) => !S.contains(target)
                   val (distance, path) = dijkstra.compute(start, target)
                   if(distance.contains(target) && distance(target) < nearestTermDistance) {
                       nearestTermDistance = distance(target)
                       nearestTerm = term
                     }
 //                  println("Shortest-path cost: " + distance(target))
               }
               if (nearestTermDistance < nearestFeatureDistance){
                 nearestFeatureDistance = nearestTermDistance
                 nearestFeature = feature
               }
           }
           val sent:Int = wrapper.getSentiment(Tokens(index)).toLowerCase() match{
             case "negative"|"very negative" => -1
             case "neutral" => 0
             case "positive"|"very positive" => 1
           }

           var fet = features(nearestFeature)
           fet.sentiment += sent
         }
     }

   }

   def getFeatures(Sentence: CoreMap, TreeBFSR: List[DataCollectionModel.OntologyNode]): Map[List[CoreLabel],DataCollectionModel.OntologyNode] = {
     val candidateFeatures = wrapper.groupConsecuetiveNouns(Sentence)
     var features:Map[List[CoreLabel],DataCollectionModel.OntologyNode] = Map()

     for(feature <- candidateFeatures){
       breakable {
         for (node <- TreeBFSR) {
           for (term <- feature)
             if (node.features.contains(wrapper.getTokenText(term))) {
               features += (feature -> node)
               break
             }
         }
       }
     }
     features
   }


 }
