
import scala.collection.mutable

class LRUCache[T](val maxSize: Int = 10) {

  case class LinkedListNode[T](key: String, 
                               value: T, 
                               var prev: Option[LinkedListNode[T]], 
                               var next: Option[LinkedListNode[T]])

  var head: Option[LinkedListNode[T]] = None
  var lastNode: Option[LinkedListNode[T]] = None
  var size = 0
  val keysIndex = mutable.Map[String, LinkedListNode[T]]()

  def get(k: String): Option[T] = {
    keysIndex.get(k) match {
      case Some(node) => {
        makeNodeHead(node)
        Some(head.get.value)
      } 
      case None => None
    }
  }

  def put(k: String, v: T) = {
    keysIndex.get(k) match {
      case Some(node) => {
        deleteNode(node)
        makeNodeHead(LinkedListNode(k, v, None, None))
        keysIndex.put(k, head.get)
      }
      case _ => {
        makeNodeHead(LinkedListNode(k, v, None, None))
        keysIndex.put(k, head.get)
        size += 1
        if (size > maxSize) {
          deleteNode(lastNode.get)
          size -= 1
        }
      }
    }
  }

  private def makeNodeHead(node: LinkedListNode[T]) = {
    if (!isHeadNode(node)) {

      if (isLastNode(node)) { lastNode = node.prev }

      node.prev.map { p => p.next = node.next }
      node.next.map { n => n.prev = node.prev }
      head.map { h => h.prev = Some(node) }
      node.next = head
      node.prev = None
      head = Some(node)

      if (lastNode.isEmpty) {
        lastNode = head
      }
    }
  }

  private def deleteNode(node: LinkedListNode[T]) = {
    if (isLastNode(node)) { lastNode = node.prev }
    node.prev.map { p => p.next = node.next }
    node.next.map { n => n.prev = node.prev }
    keysIndex.remove(node.key)
  }

  private def isHeadNode(node: LinkedListNode[T]): Boolean = {
    head match {
      case Some(n) => n.key == node.key
      case _ => false
    }
  }

  private def isLastNode(node: LinkedListNode[T]): Boolean = {
    lastNode match {
      case Some(n) => n.key == node.key
      case _ => false
    }
  }
}

val l = new LRUCache[String](5)
println(l.get("shouldn't exist"), "should be None")
l.put("key1","value1")
println(l.get("key1"), "should be Some(value1)")
println(l.lastNode.get.key, "should be", l.head.get.key)
l.put("key2","value2")
println(l.head.get.key, "should be key2")
println(l.lastNode.get.key, "should be key1")
l.put("key3","value3")
println(l.head.get.key, "should be key3")
println(l.lastNode.get.key, "should be key1")
l.put("key4","value4")
println(l.head.get.key, "should be key4")
println(l.lastNode.get.key, "should be key1")
println(l.get("key1"), "should be Some(value1)")
println(l.head.get.key, "should be key1")
println(l.lastNode.get.key, "should be key2")
l.put("key5","value5")
println(l.head.get.key, "should be key5")
println(l.lastNode.get.key, "should be key2")
l.put("key6","value6")
println(l.head.get.key, "should be key6")
println(l.lastNode.get.key, "should be key3")
l.put("key7","value7")
println(l.head.get.key, "should be key7")
println(l.lastNode.get.key, "should be key4")
l.put("key4","value7")
println(l.head.get.key, "should be key4")
println(l.lastNode.get.key, "should be key1")
println(l.get("key4"), "should be Some(value7)")
println(l.head.get.key, "should be key4")
println(l.lastNode.get.key, "should be key1")
println(l.size, "should be 5")
