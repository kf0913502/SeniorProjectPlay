package model.SentimentAnalysis

import java.io.{ByteArrayOutputStream, ObjectOutputStream}

/**
  * Created by karAdmin on 5/31/2015.
  */

case class WeightedGraph(defaultWeight: Int) extends UndirectedGraph{
   type Node = NodeImpl
   type Edge = EdgeImpl with Weight


   trait Weight{
     var weight = defaultWeight

     def getWeight = weight

     def setWeight(weight: Int): Unit = {
       this.weight = weight
     }
   }
   def getSerialized(): Array[Byte] =
   {
     val baos = new ByteArrayOutputStream()
     val objOstream = new ObjectOutputStream(baos)
     objOstream.writeObject(this)
     objOstream.flush()
     objOstream.close()
     baos.toByteArray

   }

  def assignGraph(newNodes : List[WeightedGraph#Node], newEdges : List[WeightedGraph#Edge]): WeightedGraph =
  {
    this.edges = newEdges.asInstanceOf[List[Edge]]
    this.nodes = newNodes.asInstanceOf[List[Node]]
    return this
  }
   override protected def newNode: Node = new NodeImpl
   override protected def newEdge(one: Node, other: Node): Edge with Weight =
     new EdgeImpl(one, other) with Weight
 }
