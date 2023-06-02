package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.pedestrian;

/**
 * Class representing parameters describing a pedestrian.
 *
 * @param fieldAttractionBias how is the pedestrian attracted to exits.
 * @param crowdRepulsion      pedestrian's repulsion to get stuck in a position too crowded.
 * @param velocityPercent     pedestrian's velocity as percent of maximum velocity achieved by fastest pedestrian (1.0 =
 *                            100%). Maximum velocity is defined as
 * {@link es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomatonParameters.BuilderWithScenarioWithTimeLimit#timePerTick(double)}
 *   /
 *  {@link es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario#getCellDimension()}.
 *
 * @author Pepe Gallardo
 */
public record PedestrianParameters(double fieldAttractionBias, double crowdRepulsion, double velocityPercent) {
  /**
   * Class for building a pedestrian parameters by providing each one.
   */
  public static final class Builder {
    private double fieldAttractionBias = 1.0;
    private double crowdRepulsion = 1.10;
    private double velocityPercent = 1.0;

    public Builder() {
    }

    /**
     * @param fieldAttractionBias how is the pedestrian attracted to exits.
     */
    public Builder fieldAttractionBias(double fieldAttractionBias) {
      this.fieldAttractionBias = fieldAttractionBias;
      return this;
    }

    /**
     * @param crowdRepulsion pedestrian's repulsion to get stuck in a position too crowded.
     */
    public Builder crowdRepulsion(double crowdRepulsion) {
      this.crowdRepulsion = crowdRepulsion;
      return this;
    }

    /**
     * @param velocityPercent pedestrian's velocity as percent of maximum velocity achieved by fastest pedestrian (1.0 =
     *                        100%). Maximum velocity is defined as
     *  {@link es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomatonParameters.BuilderWithScenarioWithTimeLimit#timePerTick(double)}
     *   /
     *  {@link es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario#getCellDimension()}.
     */
    public Builder velocityPercent(double velocityPercent) {
      assert velocityPercent > 0 && velocityPercent <= 1.0 : "PedestrianParameters.velocityPercent: velocity percent " +
          "must be in (0, 1.0)";
      this.velocityPercent = velocityPercent;
      return this;
    }

    public PedestrianParameters build() {
      return new PedestrianParameters(fieldAttractionBias, crowdRepulsion, velocityPercent);
    }
  }
}
