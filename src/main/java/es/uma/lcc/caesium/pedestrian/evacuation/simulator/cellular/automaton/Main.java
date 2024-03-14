package es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton;

import com.github.cliftonlabs.json_simple.Jsoner;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomaton;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.CellularAutomatonParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.Statistics;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.neighbourhood.MooreNeighbourhood;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.pedestrian.PedestrianParameters;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.examples.RandomScenario;
import es.uma.lcc.caesium.pedestrian.evacuation.simulator.cellular.automaton.automata.scenario.examples.Supermarket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import static es.uma.lcc.caesium.statistics.Random.random;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  public static void main(String[] args) {
    random.setSeed();

    var scenario = random.bernoulli(0.75) ? RandomScenario.randomScenario() : Supermarket.supermarket();

    var cellularAutomatonParameters =
        new CellularAutomatonParameters.Builder()
            .scenario(scenario) // use this scenario
            .timeLimit(10 * 60) // 10 minutes is time limit for simulation
            .neighbourhood(MooreNeighbourhood::of) // use Moore's Neighbourhood for automaton
            .pedestrianReferenceVelocity(1.3) // fastest pedestrians walk at 1.3 m/s
            .GUITimeFactor(8) // perform GUI animation x8 times faster than real time
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    Supplier<PedestrianParameters> pedestrianParametersSupplier = () ->
        new PedestrianParameters.Builder()
            .fieldAttractionBias(random.nextDouble(1.0, 10.0 ))
            .crowdRepulsion(random.nextDouble(0.1, 0.5))
            .velocityPercent(random.nextDouble(0.3, 1.0))
            .build();

    var numberOfPedestrians = random.nextInt(150, 600);
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParametersSupplier);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);

    // write trace to json file
    var trace = automaton.getTrace();
    String fileName = "data/traces/trace.json";
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(Jsoner.prettyPrint(trace.toJson().toJson()));
      fileWriter.flush();
      System.out.printf("Trace written to file %s successfully.%n", fileName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
