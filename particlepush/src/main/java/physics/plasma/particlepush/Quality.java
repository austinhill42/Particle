package physics.plasma.particlepush;

/**
 * Quality
 *
 * We define the nature of particles by attaching qualities to them. To show up in space
 * the coordinate manager must give them a position, which is an array of Qualities:
 * an xCoordinate, a yCoordinate, and so on depending on the dimension.
 * Or, for example, if you must compute gravitational forces the Manager
 * must assign each particle a mass. Etc...     *
 */
public class Quality {

    //This is the Quality's name, like "mass" or "velocity" or w/e
    public String name;

    // This is the value of the Quality
    // "volatile" means it can be accessed in other threads
    // For example: In the rendering thread to change graphics as the value changes.
    public volatile float value;

    /**
     * Quality
     *
     * This is the main constructor for a Quality that you would attach to a particle.
     * Qualities themselves should always be created through the System Manager, and then
     * passed along to particles as needed to save processing time and memory.         *
     */
    public Quality(String qname){

        name = qname;

    }

    /**
     * Quality
     *
     * This is the filled version of the constructor for a Quality attached to a particle.
     * Qualities themselves should always be created through the System Manager, and then
     * passed along to particles as needed to save processing time and memory.
     *
     * This version of the constructor sets the name and the value on creation.
     */
    public Quality(String qname,float qvalue){

        name = qname;
        value = qvalue;

    }
}
