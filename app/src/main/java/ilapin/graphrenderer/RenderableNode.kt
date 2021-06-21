package ilapin.graphrenderer

import org.joml.Vector2f
import org.joml.Vector2fc

/**
 * @author igorlapin on 20/06/2021.
 */
class RenderableNode(val value: Int, position: Vector2fc = Vector2f()) {

    private val _position = Vector2f(position)

    var isSelected = false

    var position: Vector2fc
        get() = _position
        set(value) {
            _position.set(value)
        }
}