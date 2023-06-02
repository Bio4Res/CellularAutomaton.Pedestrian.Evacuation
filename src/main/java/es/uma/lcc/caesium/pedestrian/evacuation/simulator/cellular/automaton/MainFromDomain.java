package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomaton;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomatonParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.Statistics;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.floorField.DijkstraStaticFloorFieldWithMooreNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.MooreNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.pedestrian.PedestrianParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.Scenario;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Domain;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.environment.Environment;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import static es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.statistics.Random.random;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
public class MainFromDomain {
  public static void main(String[] args) throws FileNotFoundException, JsonException {
    String filename = (args.length == 0) ? "data/environments/environment-example-supermarket.json" : args[0];
    Environment environment = Environment.fromFile(filename);
    Domain domain = environment.getDomain(1);

    Scenario scenario = new Scenario.FromDomainBuilder(domain)
        .cellDimension(domain.getWidth() / 110)
        .floorField(DijkstraStaticFloorFieldWithMooreNeighbourhood::of)
        .build();

    var cellularAutomatonParameters =
        new CellularAutomatonParameters.Builder()
            .scenario(scenario) // use this scenario
            .timeLimit(10 * 60) // 10 minutes is time limit for simulation
            .neighbourhood(MooreNeighbourhood::of) // use Moore's Neighbourhood for automaton
            .pedestrianVelocity(1.3) // fastest pedestrians walk at 1.3 m/s
            .GUITimeFactor(8) // perform GUI animation x8 times faster than real time
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    Supplier<PedestrianParameters> pedestrianParametersSupplier = () ->
        new PedestrianParameters.Builder()
            .fieldAttractionBias(random.nextDouble(0.65, 2.0))
            .crowdRepulsion(random.nextDouble(1.00, 1.50))
            .velocityPercent(random.nextDouble(0.3, 1.0))
            .build();

    var numberOfPedestrians = random.nextInt(150, 600);
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParametersSupplier);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);

    // write trace to json file
    var jsonTrace = automaton.jsonTrace();
    String fileName = "data/traces/trace.json";
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(Jsoner.prettyPrint(jsonTrace.toJson()));
      fileWriter.flush();
      System.out.println(String.format("Trace written to file %s successfully.", fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
