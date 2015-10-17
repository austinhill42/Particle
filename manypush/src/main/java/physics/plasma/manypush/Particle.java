package physics.plasma.manypush;

/**
 * I tried to avoid building a seperate particle class because I'm worried it'll weigh
 * things down. I can get around it though. Coordinates are accessed by different threads
 * for rendering them and for processing where they should be, and there are more than just
 * coordinates to keep track of if we want adaptive mass/charge-like quantities.
 *
 * This will manage the qualities of each particles: things like its position, mass, charge,
 * and it will allow for the creation of new qualities as dictated by the System Manager.
 *
 */
public class Particle {

    // TODO Qualities as different numbers in a vector? Should they each have their own variable?
    // TODO Historical data: d^n(x) vector that adaptively truncates below a certain threshold.

}
