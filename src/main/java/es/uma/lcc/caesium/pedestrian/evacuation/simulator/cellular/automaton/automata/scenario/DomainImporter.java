package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario;

import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.floorField.FloorField;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.geometry._2d.Rectangle;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Domain;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Shape;

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
   * Checks whether a rectangle intersects with any shape provided in list.
   * @param shapes list of shapes.
   * @param bottom vertical bottom coordinate of rectangle.
   * @param left horizontal left coordinate of rectangle.
   * @param height height of rectangle.
   * @param width width of rectangle.
   * @return {@code true} if rectangle intersects with any shape.
   */
  static protected boolean intersectsAny(List<? extends Shape> shapes, double bottom, double left,
                                         double height, double width) {
    var intersects = false;
    var it = shapes.iterator();
    while(!intersects && it.hasNext()) {
      var shape = it.next();
      intersects = shape.intersects(left, bottom, width, height);
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

    var rows = (int) Math.ceil(height / cellDimension);
    var columns = (int) Math.ceil(width / cellDimension);
    scenario = new Scenario(rows, columns, cellDimension, buildStaticFloorField);

    var accesses = new ArrayList<Shape>();
    for (var access : domain.getAccesses()) {
      accesses.add(access.getShape());
    }

    var obstacles = new ArrayList<Shape>();
    for (var obstacle : domain.getObstacles()) {
      obstacles.add(obstacle.getShape());
    }

    for (var i = 0; i < rows; i++) {
      for (var j = 0; j < columns; j++) {
        var bottom = i * cellDimension;
        var left = j * cellDimension;

        var cell = new Rectangle(i, j, 1, 1);

        if(intersectsAny(accesses, bottom, left, cellDimension, cellDimension)) {
          scenario.setExit(cell);
        }
        if(intersectsAny(obstacles, bottom, left, cellDimension, cellDimension)) {
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
