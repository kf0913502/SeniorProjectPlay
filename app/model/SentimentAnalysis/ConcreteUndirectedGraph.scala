package model.SentimentAnalysis

/**
 * Created by karAdmin on 5/31/2015.
 */
class ConcreteUndirectedGraph extends UndirectedGraph {
   type Node = NodeImpl
   type Edge = EdgeImpl
   protected def newNode: Node = new Node
   protected def newEdge(one: Node, other: Node): Edge =
     new Edge(one, other)
 }
