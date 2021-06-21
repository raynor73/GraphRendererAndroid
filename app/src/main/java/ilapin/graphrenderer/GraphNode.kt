package ilapin.graphrenderer

/**
 * @author igorlapin on 20/06/2021.
 */
class GraphNode<T>(var value: T) {
    val children: MutableList<GraphNode<T>> = ArrayList()
}
