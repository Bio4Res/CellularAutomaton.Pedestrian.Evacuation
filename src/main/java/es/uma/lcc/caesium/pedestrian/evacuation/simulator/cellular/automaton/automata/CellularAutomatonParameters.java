package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.Neighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.VonNeumannNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario;

import java.util.function.Function;

/**
 * Class representing parameters for a cellular automaton.
 *
 * @param scenario      Static scenario where simulation takes place.
 * @param neighbourhood Neighbourhood relationship used by automaton.
 * @param timeLimit     Time limit of simulation in seconds.
 * @param timePerTick   Seconds of time elapsed for each tick of simulation.
 * @param GUITimeFactor Acceleration for rendering animation wrt real time.
 *
 * @author Pepe Gallardo
 */
public record CellularAutomatonParameters(
    Scenario scenario
    , Neighbourhood neighbourhood
    , double timeLimit
    , double timePerTick
    , int GUITimeFactor
) {

  /**
   * Classes for building cellular automaton parameters by providing each one.
   */
  public static final class Builder {
    /**
     * @param scenario Static scenario where simulation takes place.
     */
    public BuilderWithScenario scenario(Scenario scenario) {
      BuilderWithScenario builder = new BuilderWithScenario();
      builder.scenario = scenario;
      return builder;
    }
  }

  public static final class BuilderWithScenario {
    private Scenario scenario;

    private BuilderWithScenario() {
    }

    /**
     * @param timeLimit Time limit of simulation in seconds.
     */
    public BuilderWithScenarioWithTimeLimit timeLimit(double timeLimit) {
      BuilderWithScenarioWithTimeLimit builder = new BuilderWithScenarioWithTimeLimit(this);
      builder.timeLimit = timeLimit;
      return builder;
    }
  }

  public static final class BuilderWithScenarioWithTimeLimit {
    private final Scenario scenario;
    private double timeLimit;
    private Neighbourhood neighbourhood;
    private double timePerTick;
    private int GUITimeFactor;

    private BuilderWithScenarioWithTimeLimit(BuilderWithScenario builder) {
      this.scenario = builder.scenario;
      this.neighbourhood = VonNeumannNeighbourhood.of(scenario); // default neighbourhood
      this.timePerTick = 0.4; // default is 0.4 secs per tick
      this.GUITimeFactor = 20; // default GUI time is x20 faster
    }

    /**
     * @param buildNeighbourhood a function taking current scenario and returning neighbourhood relationship used by
     *                           automaton.
     */
    public BuilderWithScenarioWithTimeLimit neighbourhood(Function<Scenario, Neighbourhood> buildNeighbourhood) {
      this.neighbourhood = buildNeighbourhood.apply(scenario);
      return this;
    }

    /**
     * @param timePerTick Seconds of time elapsed for each tick of simulation.
     *                       Notice that definition of this parameter also implies
     *                       a redefinition of {@link BuilderWithScenarioWithTimeLimit#pedestrianVelocity(double)}
     *                       in accordance with scenario's cell dimensions.
     */
    public BuilderWithScenarioWithTimeLimit timePerTick(double timePerTick) {
      this.timePerTick = timePerTick;
      return this;
    }

    /**
     * @param pedestrianVelocity Maximum pedestrian velocity in meters per second.
     *                       Notice that definition of this parameter also implies
     *                       a redefinition of {@link BuilderWithScenarioWithTimeLimit#timePerTick(double)}
     *                       in accordance with scenario's cell dimensions.
     */
    public BuilderWithScenarioWithTimeLimit pedestrianVelocity(double pedestrianVelocity) {
      this.timePerTick = pedestrianVelocity * scenario.getCellDimension();
      return this;
    }

    /**
     * @param GUITimeFactor Acceleration for rendering animation wrt real time.
     */
    public BuilderWithScenarioWithTimeLimit GUITimeFactor(int GUITimeFactor) {
      this.GUITimeFactor = GUITimeFactor;
      return this;
    }

    public CellularAutomatonParameters build() {
      return new CellularAutomatonParameters(scenario, neighbourhood, timeLimit, timePerTick, GUITimeFactor);
    }
  }
}
