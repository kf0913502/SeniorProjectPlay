package model.SentimentAnalysis

/**
  * Created by abdelrazektarek on 5/2/15.
  */
abstract class Graph {
   type Edge <: IEdge
   type Node <: INode
   abstract case class INode() {
     def connectWith(node: Node): Edge
   }
   abstract case class IEdge() {
     def a: Node
     def b: Node
     def opposite(n: Node): Option[Node]
   }
   def nodes: List[Node]
   def edges: List[Edge]
   def addNode: Node
 }
