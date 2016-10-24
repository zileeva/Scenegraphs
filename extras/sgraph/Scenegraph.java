package sgraph;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import util.IVertexData;
import util.PolygonMesh;

import java.util.*;

/**
 * A specific implementation of this scene graph. This implementation is still independent
 * of the rendering technology (i.e. OpenGL)
 * @author Amit Shesh
 */
public class Scenegraph<VertexType extends IVertexData> implements IScenegraph<VertexType>
{
    /**
     * The root of the scene graph tree
     */
    protected INode root;

    /**
     * A map to store the (name,mesh) pairs. A map is chosen for efficient search
     */
    protected Map<String,util.PolygonMesh<VertexType>> meshes;

    /**
     * A map to store the (name,node) pairs. A map is chosen for efficient search
     */
    protected Map<String,INode> nodes;

    protected Map<String,String> textures;

    /**
     * The associated renderer for this scene graph. This must be set before attempting to
     * render the scene graph
     */
    protected IScenegraphRenderer renderer;


    public Scenegraph()
    {
        root = null;
        meshes = new HashMap<String,util.PolygonMesh<VertexType>>();
        nodes = new HashMap<String,INode>();
        textures = new HashMap<String,String>();
    }

    public void dispose()
    {
        renderer.dispose();
    }

    /**
     * Sets the renderer, and then adds all the meshes to the renderer.
     * This function must be called when the scene graph is complete, otherwise not all of its
     * meshes will be known to the renderer
     * @param renderer The {@link IScenegraphRenderer} object that will act as its renderer
     * @throws Exception
     */
    @Override
    public void setRenderer(IScenegraphRenderer renderer) throws Exception {
        this.renderer = renderer;

        //now add all the meshes
        for (String meshName:meshes.keySet())
        {
            this.renderer.addMesh(meshName,meshes.get(meshName));
        }

    }


    /**
     * Set the root of the scenegraph, and then pass a reference to this scene graph object
     * to all its node. This will enable any node to call functions of its associated scene graph
     * @param root
     */

    @Override
    public void makeScenegraph(INode root)
    {
        this.root = root;
        this.root.setScenegraph(this);

    }

    /**
     * Draw this scene graph. It delegates this operation to the renderer
     * @param modelView
     */
    @Override
    public void draw(Stack<Matrix4f> modelView) {
        if ((root!=null) && (renderer!=null))
        {
            renderer.draw(root,modelView);
        }
    }


    @Override
    public void addPolygonMesh(String name, util.PolygonMesh<VertexType> mesh)
    {
        meshes.put(name,mesh);
    }




    @Override
    public void animate(float time) {

        INode righthand = nodes.get("righthand");
        INode lefthand = nodes.get("lefthand");
        INode humanoid = nodes.get("humanoid");
        INode rightelbow = nodes.get("rightforearm");
        INode leftelbow = nodes.get("leftforearm");

        Float wingsTime = time % 360;
        Float angle = (float) Math.toRadians(wingsTime);
        Float elbowangle = (float) Math.toRadians(wingsTime);

        if (angle > (float) Math.toRadians(180) && angle <= (float) Math.toRadians(360)) {
            angle = (float) Math.toRadians(-1 * wingsTime);
            elbowangle = (float) Math.toRadians(0.2 * wingsTime);
        } else if (angle < (float) Math.toRadians(180)) {
            angle = (float) Math.toRadians(wingsTime);
            elbowangle = (float) Math.toRadians(-0.2 * wingsTime);
        }

        lefthand.setAnimationTransform(new Matrix4f().rotate(angle, 0, 0, 1));
        leftelbow.setAnimationTransform(new Matrix4f().rotate(elbowangle, 0, 0, 1));

        if (angle <= (float) Math.toRadians(-180)){
            angle = (float) Math.toRadians(wingsTime);
            elbowangle = (float) Math.toRadians(-0.2 * wingsTime);
        } else if (angle > (float) Math.toRadians(0)) {
            angle = (float) Math.toRadians(-1 * wingsTime);
            elbowangle = (float) Math.toRadians(0.2 * wingsTime);
        }

        righthand.setAnimationTransform(new Matrix4f().rotate(angle, 0, 0, 1));
        rightelbow.setAnimationTransform(new Matrix4f().rotate(elbowangle, 0, 0, 1));

        humanoid.setAnimationTransform(
                new Matrix4f()
                        .rotate((float) Math.toRadians(20), 0, 0, 1)
                        .rotate(0.002f * time, 0, 1, 0)
                        .translate(30, 0, 30)
        );


    }

    @Override
    public void addNode(String name, INode node) {
        nodes.put(name,node);
    }


    @Override
    public INode getRoot() {
        return root;
    }

    @Override
    public Map<String, PolygonMesh<VertexType>> getPolygonMeshes() {
       Map<String,util.PolygonMesh<VertexType>> meshes = new HashMap<String,PolygonMesh<VertexType>>(this.meshes);
        return meshes;
    }

    @Override
    public Map<String, INode> getNodes() {
        Map<String,INode> nodes = new TreeMap<String,INode>();
        nodes.putAll(this.nodes);
        return nodes;
    }

    @Override
    public void addTexture(String name, String path) {
        textures.put(name,path);
    }



}
