package physics.plasma.particlepush;

/**
 * I tried to avoid building a separate particle class because I'm worried it'll weigh
 * things down. I can get around it though. Coordinates are accessed by different threads
 * for rendering them and for processing where they should be, and there are more than just
 * coordinates to keep track of if we want adaptive mass/charge-like quantities.
 *
 * This will manage the qualities of each particles: things like its position, mass, charge,
 * and it will allow for the creation of new qualities as dictated by the System Manager. *
 */
public class Particle {

    // TODO Qualities as different numbers in a vector? Should they each have their own variable?
    // TODO Historical data: d^n(x) vector that adaptively truncates below a certain threshold.

    // This is the array that holds a reference to all of a particle's associated Qualities.
    public Quality[] qualities;

    /**
     * Particle
     *
     * This is the main constructor for a Particle. By default Particles should have position.
     * They can also have some standard qualities defined for them like mass or charge.
     *
     * The goal of a particle as a class is that it needs to construct its qualities on the fly:
     *
     * If a particle doesn't have a quality that is asked for by the System Manager,
     * the Manager must have a default value stored in itself for the case of a null return.
     *
     * If the default value is changed overall, then it can be updated in the Manager
     * with no need to talk to the particles individually.
     *
     * But if the particle's individual value is set as different than the default,
     * then we must attach the associated quality to the given particle.
     *
     * Hopefully this conserves memory and processing time.
     */
    public Particle(){

        qualities = new Quality[0];

    }

    /**
     * addQuality
     *
     * This takes the array of qualities for the particle and adds a new space,
     * and then fills the space with the given quality (which should have a preset value).
     */
    public void addQuality(Quality quality){

        // This is a standard copy process using a temporary array to store the originals
        // while increasing the main copy's length, and then moving the data back over.
        Quality[] temp = qualities;
        qualities = new Quality[temp.length+1];

        for(int i=0;i<temp.length;i++){
            qualities[i] = temp[i];
        }

        qualities[temp.length] = quality;

    }

    /**
     * addQuality
     *
     * This takes the array of qualities for the particle and adds a new space,
     * and then fills the space by making a new quality with the associated name and value.
     *
     * This method should be avoided since we should normally be passing qualities directly
     * from the System Manager to avoid using additional construction time for new ones.
     */
    public void addQuality(String name,float value){

        // This is a standard copy process using a temporary array to store the originals
        // while increasing the main copy's length, and then moving the data back over.
        Quality[] temp = qualities;
        qualities = new Quality[temp.length+1];

        for(int i=0;i<temp.length;i++){
            qualities[i] = temp[i];
        }

        qualities[temp.length] = new Quality(name,value);

    }
}