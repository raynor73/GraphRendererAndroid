package ilapin.graphrenderer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ilapin.graphrenderer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var nodesCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.graph.listener = object : GraphView.Listener {

            override fun onNodeClicked(node: RenderableNode) {
                val newNode = RenderableNode(nodesCount++)
                newNode.position = node.position
                binding.graph.addNode(newNode)
                binding.graph.makeConnection(node.value, newNode.value)
            }
        }

        binding.graph.addNode(RenderableNode(0))
        binding.graph.addNode(RenderableNode(1))
        nodesCount = 2
        /*binding.graph.addNode(RenderableNode(2))
        binding.graph.addNode(RenderableNode(3))
        binding.graph.addNode(RenderableNode(4))
        binding.graph.addNode(RenderableNode(5))
        binding.graph.addNode(RenderableNode(6))
        binding.graph.addNode(RenderableNode(7))
        binding.graph.addNode(RenderableNode(8))*/

        /*binding.graph.addNode(RenderableNode(9))
        binding.graph.addNode(RenderableNode(10))
        binding.graph.addNode(RenderableNode(11))
        binding.graph.addNode(RenderableNode(12))
        binding.graph.addNode(RenderableNode(13))
        binding.graph.addNode(RenderableNode(14))
        binding.graph.addNode(RenderableNode(15))
        binding.graph.addNode(RenderableNode(16))
        binding.graph.addNode(RenderableNode(17))
        binding.graph.addNode(RenderableNode(18))*/

        binding.graph.makeConnection(0, 1)
        /*binding.graph.makeConnection(0, 2)
        binding.graph.makeConnection(0, 3)
        binding.graph.makeConnection(0, 4)
        binding.graph.makeConnection(4, 5)
        binding.graph.makeConnection(4, 6)
        binding.graph.makeConnection(4, 7)
        binding.graph.makeConnection(4, 8)*/

        /*binding.graph.makeConnection(1, 9)
        binding.graph.makeConnection(1, 10)
        binding.graph.makeConnection(1, 11)
        binding.graph.makeConnection(1, 12)
        binding.graph.makeConnection(1, 13)
        binding.graph.makeConnection(1, 14)
        binding.graph.makeConnection(1, 15)
        binding.graph.makeConnection(1, 16)
        binding.graph.makeConnection(1, 17)
        binding.graph.makeConnection(1, 18)*/
    }
}