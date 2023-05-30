package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.floorField.FloorField;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.geometry._2d.Rectangle;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Domain;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Obstacle;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.ObstaclePolygon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Class for importing a domain as an scenario.
 *
 * @author Pepe Gallardo
 */
public class DomainImporter {

  /**
   * Resulting scenario after importation.
   */
  protected Scenario scenario;

  /**
   * Checks whether a point intersects with any obstacle in provided list.
   * @param obstacles list of obstacles.
   * @param x X coordinate of point.
   * @param y Y coordinate of point.
   * @return {@code true} if point intersects with any obstacle.
   */
  static protected boolean intersects(List<? extends Obstacle> obstacles, double x, double y) {
    var intersects = false;
    var it = obstacles.iterator();
    while(!intersects && it.hasNext()) {
      intersects = it.next().contains(x, y);
    }
    return intersects;
  }

  /**
   * Constructor for domain importer.
   *
   * @param domain the domain to import.
   * @param cellDimension dimension (in meters) of side of a grid cell in resulting scenario.
   * @param buildStaticFloorField a function taking this scenario and returning its corresponding static floor field.
   */
  public DomainImporter(Domain domain, double cellDimension,
                        Function<Scenario, FloorField> buildStaticFloorField) {
    var width = domain.getWidth();
    var height = domain.getHeight();

    var rows = (int) (height / cellDimension);
    var columns = (int) (width / cellDimension);
    scenario = new Scenario(rows, columns, cellDimension, buildStaticFloorField);

    var accesses = new ArrayList<ObstaclePolygon>();
    for (var access : domain.getAccesses()) {
      var polygon = new ObstaclePolygon(access.shape());
      accesses.add(polygon);
    }

    var obstacles = domain.getObstacles();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        var centerRow = (i + 0.5) * cellDimension;
        var centerColumn = (j + 0.5) * cellDimension;

        var cell = new Rectangle(i, j, 1, 1);
        if(DomainImporter.intersects(accesses, centerColumn, centerRow)) {
          scenario.setExit(cell);
        }
        if(DomainImporter.intersects(obstacles, centerColumn, centerRow)) {
          scenario.setBlock(cell);
        }
      }
    }
  }

  /**
   * Returns imported scenario.
   * @return imported scenario.
   */
  public Scenario getScenario() {
    return scenario;
  }
}
